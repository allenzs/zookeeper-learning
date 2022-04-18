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
 * @create 2020-06-30 18:08
 */
public class ZKWatcherGetData {
    String IPPORT = "192.168.0.8:2181";
    ZooKeeper zooKeeper;

    @Before
    public void before() throws IOException, InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        zooKeeper = new ZooKeeper(IPPORT, 6000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("连接对象的参数");
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    countDownLatch.countDown();
                }
                System.out.println("path="+event.getPath());
                System.out.println("eventType="+event.getType());
            }
        });
        countDownLatch.await();
    }

    @After
    public void after() throws InterruptedException {
        zooKeeper.close();
    }

    @Test
    public void watcherGetData01() throws KeeperException, InterruptedException {
        /**
         * arg1:节点路径
         * arg2:使用连接对象中的watcher
         * 注意：
         *  （1）getData监控的节点必须存在，不然报错
         *  （2）只能捕获1次
         *  （3）捕获事件为：set、delete
         */
        zooKeeper.getData("/watcher02", true, null);
        Thread.sleep(500000);
        System.out.println("主方法结束");
    }

    /*
    * 自定义watcher ：
    * 捕获事件：set/delete
    * */
    @Test
    public void watcherGetData02() throws InterruptedException, KeeperException {
        zooKeeper.getData("/watcher02", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("自定义watcher");
                System.out.println("path=" + event.getPath());
                System.out.println("type=" + event.getType());
            }
        }, null);
        Thread.sleep(50000);
        System.out.println("over");
    }

    /*
    * 自定义 ： watcher，一次性
    * */
    @Test
    public void watcherGetData03() throws KeeperException, InterruptedException {
        Watcher watcher = new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("自定义watcher:匿名内部类创建watcher，一次性");
                System.out.println("path=" + event.getPath());
                System.out.println("type=" + event.getType());
            }
        };
        zooKeeper.getData("/watcher02", watcher, null);
        Thread.sleep(50000);
        System.out.println("over");
    }

    /*
    * 自定义watcher，循环监听
    * */
    @Test
    public void watcherGetData04() throws KeeperException, InterruptedException {
        Watcher watcher = new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("自定义watcher:匿名内部类创建watcher，又一次性");
                System.out.println("path=" + event.getPath());
                System.out.println("type=" + event.getType());
                if (event.getType() == Event.EventType.NodeDataChanged) {
                    try {
                        zooKeeper.getData("/watcher02", this, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        zooKeeper.getData("/watcher02", watcher, null);
        Thread.sleep(50000);
        System.out.println("over");
    }
    /*
    * 一个节点，多个监听器对象
    * */
    @Test
    public void watcherGetData05() throws KeeperException, InterruptedException {
        Watcher watcher01 = new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("自定义watcher:匿名内部类创建watcher，又一次性");
                System.out.println("path=" + event.getPath());
                System.out.println("type=" + event.getType());
                if (event.getType() == Event.EventType.NodeDataChanged) {
                    try {
                        zooKeeper.getData("/watcher02", this, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        zooKeeper.getData("/watcher02", watcher01, null);
        Watcher watcher02 = new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("自定义watcher:匿名内部类创建watcher，又一次性");
                System.out.println("path=" + event.getPath());
                System.out.println("type=" + event.getType());
                if (event.getType() == Event.EventType.NodeDataChanged) {
                    try {
                        zooKeeper.getData("/watcher02", this, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        zooKeeper.getData("/watcher02", watcher02, null);
        Thread.sleep(50000);
        System.out.println("over");
    }


}
