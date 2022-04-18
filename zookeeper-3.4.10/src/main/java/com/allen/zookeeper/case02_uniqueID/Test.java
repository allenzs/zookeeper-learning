package com.allen.zookeeper.case02_uniqueID;

/**
 * @author allen
 * @create 2020-06-30 22:50
 */
public class Test {
    public static void main(String[] args) throws InterruptedException {
        GlobalUniqueId globalUniqueId = new GlobalUniqueId();
        for (int i = 0; i < 100; i++) {
            String uniqueId = globalUniqueId.getUniqueId();
            System.out.println(uniqueId);
            Thread.sleep(1000);
        }
    }
}
