package com.allen.zookeeper.case01_config;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author allen
 * @create 2020-06-30 22:04
 */
public class MyConfigCenter implements Watcher {

    String IPPORT = "192.168.0.8:2181";
    CountDownLatch countDownLatch = new CountDownLatch(1);
    static ZooKeeper zooKeeper;

    private String url;
    private String username;
    private String password;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public MyConfigCenter() {
        initValue();
    }

    public void initValue() {
        try {
            // 创建连接对象
            zooKeeper=new ZooKeeper(IPPORT,5000,this);
            // 阻塞线程，等待连接创建成功
            countDownLatch.await();
            this.url =new String(zooKeeper.getData("/config/url", true, null)) ;
            this.username =new String(zooKeeper.getData("/config/username", true, null)) ;
            this.password =new String(zooKeeper.getData("/config/password", true, null)) ;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
                    zooKeeper = new ZooKeeper("192.168.0.8:2181",6000,new MyConfigCenter());
                } else if (event.getState() == Event.KeeperState.AuthFailed) {
                    System.out.println("验证失败");
                }

                // 当节点信息（配置文件）发生变化时
            } else if (event.getType() == Event.EventType.NodeDataChanged) {
                initValue();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
