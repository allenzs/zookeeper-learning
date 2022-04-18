package com.allen.zookeeper.case02_uniqueID;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author allen
 * @create 2020-06-30 22:38
 */
public class GlobalUniqueId implements Watcher {

    String IPPORT = "192.168.0.8:2181";
    ZooKeeper zooKeeper;
    String defaultPath = "/uniqueId";
    CountDownLatch countDownLatch = new CountDownLatch(1);

    @Override
    public void process(WatchedEvent event) {
        try {
            if (event.getType() == Event.EventType.None) {
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    System.out.println("连接成功");
                    countDownLatch.countDown();
                } else if (event.getState() == Event.KeeperState.Disconnected) {
                    System.out.println("连接断开");
                } else if (event.getState() == Event.KeeperState.Expired) {
                    System.out.println("连接超时");
                    zooKeeper = new ZooKeeper(IPPORT, 5000, this);
                } else if (event.getState() == Event.KeeperState.AuthFailed) {
                    System.out.println("认证失败");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public GlobalUniqueId() {
        try {
            // 由于zookeeper创建连接时异步的,内部会调用 process方法
            zooKeeper = new ZooKeeper(IPPORT, 5000, this);
            // 所以要阻塞，等待连接完成
            countDownLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUniqueId() {
        String path = "";
        try {
            path = zooKeeper.create(defaultPath, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            zooKeeper.delete(path,-1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // uniqueId0000000001
        return path.substring(9);
    }
}
