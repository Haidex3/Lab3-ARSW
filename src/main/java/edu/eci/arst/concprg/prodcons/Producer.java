/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arst.concprg.prodcons;

import java.util.Queue;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Producer extends Thread {

    private final Queue<Integer> queue;
    private int dataSeed = 0;
    private final Random rand;
    private final long stockLimit;

    public Producer(Queue<Integer> queue, long stockLimit) {
        this.queue = queue;
        rand = new Random(System.currentTimeMillis());
        this.stockLimit = stockLimit;
    }

    @Override
    public void run() {
        while (true) {
            dataSeed = dataSeed + rand.nextInt(100);

            synchronized (queue) {
                queue.add(dataSeed);
                System.out.println("Producer added " + dataSeed);
                // Notifica a los consumidores en espera
                queue.notifyAll();
            }

            try {
                Thread.sleep(1000); // Producción lenta
            } catch (InterruptedException ex) {
                Logger.getLogger(Producer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

