package com.example.carKotlinCode;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class TimeOutEvent extends Thread {
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    abstract void timeOutFunction();

    @Override
    public void run() {
        this.timeOutFunction();
        this.disableWait();
    }

    private void disableWait() {
        try {
            this.lock.lock();
            this.condition.signalAll();
        } finally {
            this.lock.unlock();
        }
    }

    boolean wait(long time, TimeUnit unit) {
        if (!this.isAlive()) {
            return true;
        }
        boolean flag = false;
        try {
            this.lock.lock();
            flag = this.condition.await(time, unit);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (!flag) {
                this.interrupt();
            }
            this.lock.unlock();
        }
        return flag;
    }

}
