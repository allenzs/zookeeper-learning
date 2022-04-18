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
 * @create 2020-06-30 17:04
 */
public class ZKWatcherExists {
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
    public void watcherExists01() throws KeeperException, InterruptedException {
        /*
        * arg1:节点路径
        * arg2:使用连接对象中的watcher
        * */
        zooKeeper.exists("/watcher", true);
        Thread.sleep(100000);
        System.out.println("主方法结束");
    }

    /*
    * 自定义watcher对象
    * */
    @Test
    public void wathcherExists02() throws KeeperException, InterruptedException {
        zooKeeper.exists("/watcher", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("自定义 watcher");
                System.out.println("path:"+event.getPath());
                System.out.println("eventType:"+event.getType());
            }
        });

        Thread.sleep(50000);
        System.out.println("主方法结束");
    }


    /*
    * 自定义 watcher， 一次性，生效1次，监听到，再启动依然失效
    * */
    @Test
    public void watcherExists03() throws KeeperException, InterruptedException {
        Watcher watcher = new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("自定义 watcher");
                System.out.println("path:"+event.getPath());
                System.out.println("eventType:"+event.getType());
            }
        };
        zooKeeper.exists("/watcher", watcher);
    }

    /*
     * 自定义 watcher， 循环监听
     * */
    @Test
    public void watcherExists04() throws KeeperException, InterruptedException {
        Watcher watcher = new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                try {
                    System.out.println("自定义 watcher");
                    System.out.println("path:"+event.getPath());
                    System.out.println("eventType:"+event.getType());
                    // 循环监听，提升一次性性能。
                    zooKeeper.exists("/watcher", this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        zooKeeper.exists("/watcher", watcher);
        Thread.sleep(800000);
        System.out.println("结束");
    }

    /*
    * 注册多个监听器
    * */
    @Test
    public void watcherExists05() throws KeeperException, InterruptedException {

        zooKeeper.exists("/watcher", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("1");
                System.out.println("path="+event.getPath());
                System.out.println("eventType="+event.getType());
            }
        });
        zooKeeper.exists("/watcher", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("2");
                System.out.println("path="+event.getPath());
                System.out.println("eventType="+event.getType());
            }
        });
        Thread.sleep(80000);
        System.out.println("主方法结束");
    }

}
