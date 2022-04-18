package com.allen.zookeeper.case03_distributedLock.two;

import org.apache.zookeeper.KeeperException;

import java.util.concurrent.CountDownLatch;

/**
 * @author allen
 * @create 2020-06-30 23:45
 */
public class TicketSeller {

    private volatile int num = 100;
    public void dec() throws KeeperException, InterruptedException {
//        MyDistributedLock myDistributedLock = new MyDistributedLock();
//        myDistributedLock.acquireLock();
        num--;
        System.out.println("==================================="+num);
//        myDistributedLock.releaseLock();
    }

    public static void main(String[] args) throws InterruptedException {
        TicketSeller ticketSeller = new TicketSeller();
        CountDownLatch countDownLatch = new CountDownLatch(100);
        for (int i = 0; i < 100; i++) {
            new Thread(()->{
                try {
                    ticketSeller.dec();
                    countDownLatch.countDown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
        countDownLatch.await();
        System.out.println("main over");
    }
}
