package edu.eci.arsw.highlandersim;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Immortal extends Thread {

    private ImmortalUpdateReportCallback updateCallback = null;

    private int defaultDamageValue;

    private final List<Immortal> immortalsPopulation;

    private final String name;

    private final Random r = new Random(System.currentTimeMillis());

    private static volatile boolean paused = false;

    private static final Object pauseLock = new Object();

    private final Object lock = new Object();

    private static volatile boolean stopped = false;

    private final AtomicInteger health;

    private volatile boolean alive = true;

    public Immortal(String name, List<Immortal> immortalsPopulation, int initialHealth, int defaultDamageValue,
            ImmortalUpdateReportCallback ucb) {
        super(name);
        this.updateCallback = ucb;
        this.name = name;
        this.immortalsPopulation = immortalsPopulation;
        this.health = new AtomicInteger(initialHealth);
        this.defaultDamageValue = defaultDamageValue;
    }

    @Override
    public void run() {
        while (!stopped) {
            // Pause
            synchronized (pauseLock) {
                while (paused && !stopped) {
                    try {
                        pauseLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            }
            Immortal im;

            int myIndex = immortalsPopulation.indexOf(this);

            int nextFighterIndex = r.nextInt(immortalsPopulation.size());

            // avoid self-fight
            if (nextFighterIndex == myIndex) {
                nextFighterIndex = ((nextFighterIndex + 1) % immortalsPopulation.size());
            }

            im = immortalsPopulation.get(nextFighterIndex);

            this.fight(im);

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    public static void setStopped() {
        stopped = true;
        synchronized (pauseLock) {
            pauseLock.notifyAll();
        }
    }

    public static void pauseAll() {
        paused = true;
    }

    public static void resumeAll() {
        synchronized (pauseLock) {
            paused = false;
            pauseLock.notifyAll();
        }
    }

    public void fight(Immortal i2) {

        Immortal first = this;
        Immortal second = i2;
        int h1 = System.identityHashCode(first);
        int h2 = System.identityHashCode(second);
        if (h1 > h2) {
            Immortal tmp = first;
            first = second;
            second = tmp;
        }

        synchronized (first.lock) {
            synchronized (second.lock) {
                if (i2.isAlive_()) {
                    i2.changeHealth(-defaultDamageValue);
                    this.changeHealth(defaultDamageValue);
                    updateCallback.processReport("Fight: " + this + " vs " + i2 + "\n");
                    if (i2.isAlive_()) {
                        i2.die();
                        updateCallback.processReport(i2 + " has been removed from the game!\n");
                    }
                } else {
                    updateCallback.processReport(this + " says: " + i2 + " is already dead!\n");
                }
            }
        }
    }

    public void changeHealth(int v) {
        if (health.addAndGet(v) <= 0){
            this.alive = false;
        }
    }

    public int getHealth() {
        return health.get();
    }

    @Override
    public String toString() {

        return name + "[" + health + "]";
    }

    public boolean isAlive_() {
        return alive;
    }

    public void die() {
        alive = false;
    }

}
