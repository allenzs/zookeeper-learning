package com.allen.zookeeper.case03_distributedLock.two;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author allen
 * @create 2020-06-30 23:19
 */
public class MyDistributedLock {
    CountDownLatch countDownLatch = new CountDownLatch(1);
    String IPPORT = "192.168.0.8:2181";
    ZooKeeper zooKeeper;
    private static final String LOCK_ROOT_PATH = "/locks";
    private static final String LOCK_NODE_NAME = "lock_";
    private String lockPath;

    /**
     * 监视器对象：
     *      监视上一个节点是否被释放
     */
    Watcher watcher = new Watcher() {
        @Override
        public void process(WatchedEvent event) {
            if (event.getType() == Event.EventType.NodeDeleted) {
                // 锁对象要保持同步
                synchronized (this) {
                    notifyAll();
                }
            }
        }
    };

    public MyDistributedLock() {
        try {
            zooKeeper = new ZooKeeper(IPPORT, 5000, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (event.getType() == Event.EventType.None) {
                        if (event.getState() == Event.KeeperState.SyncConnected) {
                            System.out.println("连接成功");
                            countDownLatch.countDown();
                        }
                    }
                }
            });
            countDownLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void acquireLock() throws KeeperException, InterruptedException {
        // 创建锁节点
        createLock();
        // 尝试获取锁
        attemptLock();
    }
    // 创建锁节点
    private void createLock() throws KeeperException, InterruptedException {
        // 判断是否存在 lock 根结点数据，不存在则创建
        Stat stat = zooKeeper.exists(LOCK_ROOT_PATH, false);
        if (stat == null) {
            zooKeeper.create(LOCK_ROOT_PATH, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
        }
        lockPath = zooKeeper.create(LOCK_ROOT_PATH + "/" + LOCK_NODE_NAME, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println("节点创建成功："+lockPath);

    }

    // 尝试获取锁
    private void attemptLock() throws KeeperException, InterruptedException {
        // 获取locks 节点下的所有子结点
        List<String> list = zooKeeper.getChildren(LOCK_ROOT_PATH, false);
        // 对子结点进行排序
        Collections.sort(list);
        // /lock/locks_0000000001  ---- LOCK_ROOT_PATH/LOCK_NODE_PATH
        int index = list.indexOf(lockPath.substring(LOCK_ROOT_PATH.length() + 1));
        // TODO: 2020/6/30 重点：如果索引为第一个，说明索引最小，为第一个
        if (index == 0) {
            System.out.println("获取锁成功");
            return;
        } else {
            // 上一个节点的路径
            String path = list.get(index - 1);
            Stat stat = zooKeeper.exists(LOCK_ROOT_PATH + "/" + path, watcher);
            if (stat == null) {
                attemptLock();
            } else {
                synchronized (watcher) {
                    //线程阻塞
                    watcher.wait();
                }
                attemptLock();
            }
        }
    }

    // 释放锁
    public void releaseLock() throws KeeperException, InterruptedException {
        // 删除临时有序节点
        zooKeeper.delete(this.lockPath,-1);
        zooKeeper.close();
        System.out.println("锁已经释放");
    }
}
