package com.allen.zookeeper.case03_distributedLock.two;

import java.util.Random;
import java.util.UUID;

/**
 * @author allen
 * @create 2020-07-03 21:12
 */
public class Test {
    public static void main(String[] args) {
        for (int i = 0; i < 40; i++) {
            System.out.println(getPassWord());
        }
    }

    public static String getPassWord() {
        byte[] newByte01 = new byte[4];
        byte[] newByte02 = new byte[4];
        Random random = new Random();

        for (int i = 0; i < 4; i++) {
            byte[] bytes = UUID.randomUUID().toString().replace("-", "").getBytes();
            newByte01[i] = bytes[random.nextInt(31)];
        }
        for (int i = 0; i < 4; i++) {
            byte[] bytes = UUID.randomUUID().toString().replace("-", "").getBytes();
            newByte02[i] = bytes[random.nextInt(31)];
        }
        return new String(newByte01).toUpperCase() + new String(newByte02);
    }
}
