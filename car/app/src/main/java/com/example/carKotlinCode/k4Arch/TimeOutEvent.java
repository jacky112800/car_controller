import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class TimeOutEvent extends Thread {
    Lock lock = new ReentrantLock();
    Condition condition = lock.newCondition();

    abstract void timeOutFunction();

    @Override
    public void run() {
        timeOutFunction();
        disable_wait();
    }

    private void disable_wait() {
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

    public static void main(String[] args) {
        TimeOutEvent timeOutEvent = new TimeOutEvent() {
            @Override
            void timeOutFunction() {
                //TODO: overwrite this
                System.out.println("do something");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        timeOutEvent.start();
        System.out.println(timeOutEvent.wait(2000, TimeUnit.MILLISECONDS));
        try {
            timeOutEvent.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
