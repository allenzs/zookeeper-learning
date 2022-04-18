package com.allen.zookeeper.watch;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author allen
 * @create 2020-06-30 20:11
 */
public class ZKWatcherGetChild {
    String IPPORT = "192.168.0.8:2181";
    ZooKeeper zooKeeper;
    @Before
    public void before() throws IOException, InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        zooKeeper = new ZooKeeper(IPPORT, 5000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("---连接对象参数---");
                // 连接成功
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    System.out.println("连接成功");
                    countDownLatch.countDown();
                }
                System.out.println("path="+event.getPath());
                System.out.println("eventType="+event.getType());
            }
        });
        // 主线程阻塞，等待连接对象的创建成功
        countDownLatch.await();
    }

    @After
    public void after() throws InterruptedException {
        zooKeeper.close();
    }

    @Test
    public void watcherGetChild01() throws KeeperException, InterruptedException {
        zooKeeper.getChildren("/watcher03",true);
        Thread.sleep(100000);
        System.out.println("over");
    }

    @Test
    public void watcherGetChild02() throws KeeperException, InterruptedException {
        zooKeeper.getChildren("/watcher03", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("自定义watcher");
                System.out.println("path="+event.getPath());
                System.out.println("type="+event.getType());
            }
        });
        Thread.sleep(10000);
        System.out.println("over");
    }

    /*
    * 自定义watcher，一次性
    * */
    @Test
    public void watcherGetChild03() throws KeeperException, InterruptedException {

        Watcher watcher = new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("自定义watcher");
                System.out.println("path="+event.getPath());
                System.out.println("type="+event.getType());
            }
        };
        zooKeeper.getChildren("/watcher03", watcher);

        Thread.sleep(10000);
        System.out.println("over");
    }
    /*
     * 自定义watcher，循环监听
     * */
    @Test
    public void watcherGetChild04() throws KeeperException, InterruptedException {

        Watcher watcher = new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("自定义watcher");
                System.out.println("path="+event.getPath());
                System.out.println("type="+event.getType());
                if (event.getType()== Event.EventType.NodeChildrenChanged) {
                    try {
                        zooKeeper.getChildren("/watcher03", this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        zooKeeper.getChildren("/watcher03", watcher);
        Thread.sleep(10000);
        System.out.println("over");
    }
    /*
     * 自定义watcher，多个客户端，循环监听
     * */
    @Test
    public void watcherGetChild05() throws KeeperException, InterruptedException {

        Watcher watcher01 = new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("自定义watcher");
                System.out.println("path="+event.getPath());
                System.out.println("type="+event.getType());
                if (event.getType()== Event.EventType.NodeChildrenChanged) {
                    try {
                        zooKeeper.getChildren("/watcher03", this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        zooKeeper.getChildren("/watcher03", watcher01);
        Watcher watcher02 = new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("自定义watcher");
                System.out.println("path="+event.getPath());
                System.out.println("type="+event.getType());
                if (event.getType()== Event.EventType.NodeChildrenChanged) {
                    try {
                        zooKeeper.getChildren("/watcher03", this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        zooKeeper.getChildren("/watcher03", watcher02);
        Thread.sleep(10000);
        System.out.println("over");
    }
}
