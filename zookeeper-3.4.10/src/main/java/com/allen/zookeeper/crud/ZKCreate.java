package com.allen.zookeeper.crud;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author allen
 * @create 2020-06-29 20:38
 */
public class ZKCreate {

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

    /**
     * arg1:节点路径(注意：不能创建层级目录，要一级一级的建)
     * arg2:节点数据
     * arg3:权限列表 world:anyone:cdrwa
     * arg4:节点类型
     */
    @Test
    public void create01() throws KeeperException, InterruptedException {

        zooKeeper.create("/create/node01", "node01".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }
    @Test
    public void create02() throws KeeperException, InterruptedException {
        zooKeeper.create("/create/node02", "node02".getBytes(), ZooDefs.Ids.READ_ACL_UNSAFE, CreateMode.PERSISTENT);
    }
    /*
     * world授权模式
     * 权限列表
     * */
    @Test
    public void create03() throws KeeperException, InterruptedException {

        // 授权列表
        List<ACL> acls = new ArrayList<>();

        // 授权模式和授权对象
        Id id = new Id("world", "anyone");

        // 授权设置
        acls.add(new ACL(ZooDefs.Perms.READ, id));
        acls.add(new ACL(ZooDefs.Perms.WRITE, id));

        zooKeeper.create("/create/node03", "node03".getBytes(), acls, CreateMode.PERSISTENT);
    }
    /*
     * ip授权模式
     * 权限列表
     * */
    @Test
    public void create04() throws KeeperException, InterruptedException {

        ArrayList<ACL> acls = new ArrayList<>();
        // 授权模式 和 授权对象
        Id id = new Id("ip", "192.168.0.8");
        // 权限设置
        acls.add(new ACL(ZooDefs.Perms.ALL, id));

        zooKeeper.create("/create/node04", "node04".getBytes(), acls, CreateMode.PERSISTENT);
    }
    /**
     * auth 模式
     * 添加授权用户
     */
    @Test
    public void create05() throws KeeperException, InterruptedException {

        zooKeeper.addAuthInfo("digest", "allen:123456".getBytes());
        zooKeeper.create("/create/node05", "node05".getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL, CreateMode.PERSISTENT);
    }
    /*
     * auth 模式
     * 添加用户
     * */
    @Test
    public void create06() throws KeeperException, InterruptedException {

        zooKeeper.addAuthInfo("digest", "tom:123456".getBytes());
        ArrayList<ACL> acls = new ArrayList<>();
        // 授权模式 和 授权对象
        Id id = new Id("auth", "tom");
        // 权限设置
        acls.add(new ACL(ZooDefs.Perms.READ, id));
        zooKeeper.create("/create/node06", "node06".getBytes(), acls, CreateMode.PERSISTENT);
    }

    /*
     * digest 模式
     * */
    @Test
    public void create07() throws KeeperException, InterruptedException {
        ArrayList<ACL> acls = new ArrayList<>();
        // 授权模式和授权对象
        Id id = new Id("digest","bigdata:JHVv5P7yKf3Kt8Awp28UlQXwjq4=");

        acls.add(new ACL(ZooDefs.Perms.ALL, id));

        zooKeeper.create("/create/node07", "node07".getBytes(), acls, CreateMode.PERSISTENT);
    }
    /*
     * 持久化、顺序-节点
     * */
    @Test
    public void create08() throws KeeperException, InterruptedException {
        String s = zooKeeper.create("/create/node08", "node08".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
        System.out.println(s);
    }
    /*
     * 临时节点
     * 单次会话结束，即可消失
     * */
    @Test
    public void create09() throws KeeperException, InterruptedException {
        String s = zooKeeper.create("/create/node09", "node09".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        System.out.println(s);
    }
    /*
     * 临时有序节点
     * 单次会话有效
     * */
    @Test
    public void create10() throws KeeperException, InterruptedException {
        String s = zooKeeper.create("/create/node10", "node10".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println(s);
    }

    /*
     * 异步方式创建节点
     * */
    @Test
    public void create11() throws InterruptedException {
        zooKeeper.create("/create/node11", "node11".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT, new AsyncCallback.StringCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx, String name) {
                // 0-代表创建成功
                System.out.println(rc);
                System.out.println(path);
                System.out.println(ctx);
                System.out.println(name);
            }
        },"I am context");
        Thread.sleep(3000);
        System.out.println("主方法结束");
    }
}
