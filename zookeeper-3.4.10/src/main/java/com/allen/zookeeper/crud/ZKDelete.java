package com.allen.zookeeper.crud;

import org.apache.zookeeper.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author allen
 * @create 2020-06-30 10:54
 */
public class ZKDelete {
    String IPPORT = "192.168.0.8:2181";
    ZooKeeper zooKeeper;
    @Before
    public void before() throws IOException, InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        zooKeeper = new ZooKeeper(IPPORT, 5000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    System.out.println("连接成功");
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
    }

    /*
    * 同步删除
    * arg1:节点路径
    * arg2：忽略版本信息
    * */
    @Test
    public void delete01() throws KeeperException, InterruptedException {
        zooKeeper.delete("/delete/node01",-1);
    }


    @Test
    public void delete02() throws InterruptedException {
        zooKeeper.delete("/delete/node02", -1, new AsyncCallback.VoidCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx) {
                System.out.println(rc);
                System.out.println(path);
                System.out.println(ctx);
            }
        },"I am context");
        Thread.sleep(1000);
        System.out.println("主方法结束");
    }
}
