package com.allen.zookeeper.crud;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author allen
 * @create 2020-06-30 14:46
 */
public class ZKGet {
    String IPPORT = "192.168.0.8:2181";
    ZooKeeper zooKeeper;
    @Before
    public void before() throws IOException, InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        zooKeeper = new ZooKeeper(IPPORT, 5000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    System.out.println("------------连接成功------------");
                    countDownLatch.countDown();
                }
            }
        });
        // 主线程阻塞，等待连接对象的创建成功
        countDownLatch.await();
    }

    @After
    public void after() throws InterruptedException {
        zooKeeper.close();
        System.out.println("--------------关闭连接--------------");
    }

    @Test
    public void exists01() throws KeeperException, InterruptedException {
        Stat stat = zooKeeper.exists("/get", false);
        if (stat != null) {
            System.out.println(stat.getVersion());
        } else {
            System.out.println("节点不存在");
        }
    }

    @Test
    public void exists02() throws InterruptedException {
        zooKeeper.exists("/get", false, new AsyncCallback.StatCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx, Stat stat) {
                if (stat != null) {
                    System.out.println(rc);
                    System.out.println(path);
                    System.out.println(ctx);
                    System.out.println(stat.getVersion());
                } else {
                    System.out.println("节点不存在！");
                }
            }
        }, "I am context");
        Thread.sleep(1000);
        System.out.println("主方法结束");
    }
    @Test
    public void getData01() throws KeeperException, InterruptedException {
        Stat stat = new Stat();
        byte[] bytes = zooKeeper.getData("/get/node01", false, stat);
        System.out.println(new String(bytes));
        System.out.println(stat.getVersion());
    }

    @Test
    public void getData02() {
        zooKeeper.getData("/get/node02", false, new AsyncCallback.DataCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
                System.out.println(rc);
                System.out.println(path);
                System.out.println(ctx);
                System.out.println(new String(data));
                System.out.println(stat.getVersion());
            }
        }, "I am context");
    }

    @Test
    public void getChildren01() throws KeeperException, InterruptedException {
        List<String> childrens = zooKeeper.getChildren("/get", false);

        for (String children : childrens) {
            System.out.println(children);
        }
    }

    @Test
    public void getChildren02() throws InterruptedException {
        zooKeeper.getChildren("/get", false, new AsyncCallback.Children2Callback() {
            @Override
            public void processResult(int rc, String path, Object ctx, List<String> children, Stat stat) {
                System.out.println(rc);
                System.out.println(path);
                System.out.println(ctx);
                for (String child : children) {
                    System.out.println(child);
                }
                System.out.println(stat.getVersion());
            }
        },"I am context");
        Thread.sleep(1000);
        System.out.println("主方法结束");
    }
}
