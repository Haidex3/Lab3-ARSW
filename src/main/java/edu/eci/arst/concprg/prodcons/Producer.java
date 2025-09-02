/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arst.concprg.prodcons;

import java.util.Random;
import java.util.concurrent.BlockingQueue;

public class Producer extends Thread {

    private final BlockingQueue<Integer> queue;
    private int dataSeed = 0;
    private final Random rand;

    public Producer(BlockingQueue<Integer> queue) {
        this.queue = queue;
        rand = new Random(System.currentTimeMillis());
    }

    @Override
    public void run() {
        while (true) {
            dataSeed = dataSeed + rand.nextInt(100);
            try {
                queue.put(dataSeed);
                System.out.println("Producer added " + dataSeed);
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

