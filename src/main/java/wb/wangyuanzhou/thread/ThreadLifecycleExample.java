package wb.wangyuanzhou.thread;

import java.util.concurrent.TimeUnit;

/**
 * 线程生命周期
 * <pre>
 NEW（新建）：线程刚被创建，但还未启动。在创建线程对象后，还没有调用线程的start()方法时，线程处于这个状态。

 RUNNABLE（就绪）：线程可以被调度执行，但还未开始执行。线程可能在等待CPU时间片来执行。

 RUNNING（运行）：线程正在执行其任务代码。在此状态下，线程正在 CPU 上运行。

 BLOCKED（阻塞）：线程被阻塞，通常因为等待某个锁或资源。一个线程在等待进入同步块或方法时，会进入这个状态。

 WAITING（等待）：线程在等待某个条件的发生，不会消耗 CPU 时间。例如，线程调用了Object.wait()方法或Thread.sleep()方法。

 TIMED_WAITING（定时等待）：线程在等待一段时间，也不会消耗 CPU 时间。例如，线程调用了Thread.sleep(long millis)方法或Object.wait(long millis)方法。

 TERMINATED（终止）：线程已经执行完毕或因异常退出。线程的生命周期结束。
 *
 * SleepingThread状态: NEW
 * WaitingThread状态: NEW
 * ----------------after start method------------------------
 * SleepingThread状态: RUNNABLE
 * WaitingThread状态: RUNNABLE
 * SleepingThread状态run方法1: RUNNABLE
 * WaitingThread状态run方法1: RUNNABLE
 * SleepingThread 调用 sleep() 方法后 状态: TIMED_WAITING
 * WaitingThread 调用 wait() 方法后状态: WAITING
 * 唤醒WaitingThread
 * SleepingThread状态: TIMED_WAITING
 * WaitingThread状态: BLOCKED
 * WaitingThread状态run方法2: RUNNABLE
 * SleepingThread状态run方法2: RUNNABLE
 * SleepingThread状态: TERMINATED
 * WaitingThread状态: TERMINATED
 * </pre>
 */
public class ThreadLifecycleExample {
    
    public static void main(String[] args) {
        Object lock = new Object();

        Thread sleepingThread = new Thread(new SleepingTask());
        Thread waitingThread = new Thread(new WaitingTask(lock));

        System.out.println("SleepingThread状态: " + sleepingThread.getState()); // NEW
        System.out.println("WaitingThread状态: " + waitingThread.getState());  // NEW

        sleepingThread.start();
        waitingThread.start();

        System.out.println("----------------after start method------------------------");

        System.out.println("SleepingThread状态: " + sleepingThread.getState());// RUNNABLE
        System.out.println("WaitingThread状态: " + waitingThread.getState());// RUNNABLE

        try {
            Thread.sleep(1000); // 主线程休眠1秒，确保WaitingTask线程已经进入等待状态
            System.out.println("SleepingThread 调用 sleep() 方法后 状态: " + sleepingThread.getState());
            System.out.println("WaitingThread 调用 wait() 方法后状态: " + waitingThread.getState());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        synchronized (lock) {
            System.out.println("唤醒WaitingThread");
            lock.notify(); // 唤醒WaitingTask线程
        }

        try {
            System.out.println("SleepingThread状态: " + sleepingThread.getState());
            System.out.println("WaitingThread状态: " + waitingThread.getState());
            Thread.sleep(1000); // 主线程休眠1秒，以便观察线程状态的变化
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("SleepingThread状态: " + sleepingThread.getState());
        System.out.println("WaitingThread状态: " + waitingThread.getState());
    }
}

class SleepingTask implements Runnable {
    @Override
    public void run() {
        System.out.println("SleepingThread状态run方法1: " + Thread.currentThread().getState()); //RUNNABLE
        try {
            Thread.sleep(2000); // 休眠2秒，模拟阻塞状态
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("SleepingThread状态run方法2: " + Thread.currentThread().getState()); //RUNNABLE
    }
}

class WaitingTask implements Runnable {
    private final Object lock;

    public WaitingTask(Object lock) {
        this.lock = lock;
    }

    @Override
    public void run() {
        synchronized (lock) {
            System.out.println("WaitingThread状态run方法1: " + Thread.currentThread().getState()); // RUNNABLE
            try {
                lock.wait(); // 在这里等待被唤醒
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("WaitingThread状态run方法2: " + Thread.currentThread().getState());// RUNNABLE
        }
    }
}