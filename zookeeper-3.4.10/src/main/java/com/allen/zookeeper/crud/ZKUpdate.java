package com.allen.zookeeper.crud;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author allen
 * @create 2020-06-30 10:42
 */
public class ZKUpdate {
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
    * arg1:节点路径
    * arg2:节点数据
    * arg3:版本号
    *       -1：代表不参与更新(即，更新最新版本）
    *       其他值：要与当前最新版本号匹配才能更新成功
    * */
    @Test
    public void set01() throws KeeperException, InterruptedException {
        Stat stat = zooKeeper.setData("/set/node01", "allen-set".getBytes(), -1);
        System.out.println(stat.getVersion());
        System.out.println(stat.getCzxid());
    }

    /*
    * 异步更新
    * */
    @Test
    public void set02() throws KeeperException, InterruptedException {
        zooKeeper.setData("/set/node01", "james-set".getBytes(), -1, new AsyncCallback.StatCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx, Stat stat) {
                System.out.println(rc);
                System.out.println(ctx);
                // 节点属性描述信息
                System.out.println(stat.getVersion());
            }
        },"I am context");
        Thread.sleep(1000);
        System.out.println("主方法结束");
    }
}
