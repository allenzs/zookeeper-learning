package com.allen.zookeeper.case01_config;

/**
 * @author allen
 * @create 2020-06-30 22:32
 */
public class Test {
    public static void main(String[] args) throws InterruptedException {
        MyConfigCenter myConfigCenter = new MyConfigCenter();

        while (true) {
            String url = myConfigCenter.getUrl();
            String username = myConfigCenter.getUsername();
            String password = myConfigCenter.getPassword();
            System.out.println(url);
            System.out.println(username);
            System.out.println(password);
            Thread.sleep(1000);
            System.out.println("========================");
        }
    }
}
