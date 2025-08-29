/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arst.concprg.prodcons;

import java.util.concurrent.BlockingQueue;

public class Consumer extends Thread {
    
    private final BlockingQueue<Integer> queue;
    
    public Consumer(BlockingQueue<Integer> queue){
        this.queue = queue;        
    }
    
    @Override
    public void run() {
        while (true) {
            try {
                // take() bloquea si la cola está vacía
                int elem = queue.take();
                System.out.println("Consumer consumes " + elem);
                // Consumo LENTO
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
