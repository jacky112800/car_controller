import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


public class TimeOutEvent extends Thread {
    private final Lock rLock = new ReentrantLock();
    private final Condition condition = rLock.newCondition();

    @Override
    public void run() {
        timeOutFunction();
        disable_wait();
    }

    public void timeOutFunction() {
        /*
         * implement time out function
         */
    }

    void disable_wait() {
        try {
            this.rLock.lock();
            this.condition.signalAll();
        } finally {
            this.rLock.unlock();
        }
    }

    boolean wait(long time, TimeUnit unit) {
        boolean flag = false;
        if (!this.isAlive()) {
            return true;
        }
        try {
            rLock.lock();
            flag = this.condition.await(time, unit);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (!flag) {
                this.interrupt();
            }
            rLock.unlock();
        }
        return flag;
    }


    public static void main(String[] args) {

        TimeOutEvent timeOutEvent = new TimeOutEvent() {
            @Override
            public void timeOutFunction() {
                try {
                    System.out.println("Start func");
                    Thread.sleep(2000);
                    System.out.println("End func");
                } catch (InterruptedException e) {
                    System.out.println("timeout function thread interrupt");
                }
            }
        };
        timeOutEvent.start();
        System.out.println("is function finish before waiting time: " + timeOutEvent.wait(3000, TimeUnit.MILLISECONDS));
    }
}
