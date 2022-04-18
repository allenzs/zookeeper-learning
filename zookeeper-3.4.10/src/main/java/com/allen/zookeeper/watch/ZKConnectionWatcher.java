package com.allen.zookeeper.watch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

/**
 * @author allen
 * @create 2020-06-30 15:33
 */
public class ZKConnectionWatcher implements Watcher {
    static CountDownLatch countDownLatch = new CountDownLatch(1);
    static ZooKeeper zooKeeper;

    @Override
    public void process(WatchedEvent event) {
        try {
            if (event.getType() == Event.EventType.None) {
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    System.out.println("连接创建成功");
                    countDownLatch.countDown();
                } else if (event.getState() == Event.KeeperState.Disconnected) {
                    /*
                    * 网络异常断开，捕获
                    * */
                    System.out.println("断开连接");
                } else if (event.getState() == Event.KeeperState.Expired) {
                    System.out.println("会话超时");
                    zooKeeper = new ZooKeeper("192.168.0.8:2181", 5000, new ZKConnectionWatcher());
                } else if (event.getState() == Event.KeeperState.AuthFailed) {
                    System.out.println("认证失败");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            zooKeeper = new ZooKeeper("192.168.0.8:2181", 5000, new ZKConnectionWatcher());
            // 阻塞线程，等待连接的创建
            countDownLatch.await();

            System.out.println(zooKeeper.getSessionId());
            // 添加授权用户
            zooKeeper.addAuthInfo("digest","allen:1234561".getBytes());
            byte[] bytes = zooKeeper.getData("/hadoop", false, null);
            System.out.println(new String(bytes));

            Thread.sleep(1000);

            zooKeeper.close();
            System.out.println("结束");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
