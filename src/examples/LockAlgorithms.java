import tamp.Counter;
import tamp.Lock;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * The Art of Multiprocessor Programming
 */
public final class LockAlgorithms {

    /**
     * They only calls counter.getAndIncrement
     */
    static class Player extends Thread {

        private Counter counter;

        public Player(int id, Counter counter) {
            this.counter = counter;
            this.setName(String.valueOf(id));
        }

        @Override
        public void run() {
            counter.getAndIncrement();
        }
    }

    static final int THREAD_NUM = 2;

    public static void main(String[] args) throws InterruptedException {
        Lock lock = new LockOne();
        Counter counter = new Counter(lock);
        for (int i = 0; i < THREAD_NUM; i++) {
            final Player player = new Player(i, counter);
            player.start();
        }
    }

    /**
     * LockOne
     * <p>
     * Interleave すると, デッドロックが起こる
     */
    public static class LockOne implements Lock {
        private boolean[] flags = new boolean[2];

        private static AtomicInteger IN_WAITING_SECTION = new AtomicInteger(0);

        @Override
        public void lock() {
            int i = this.getThreadId();
            int j = 1 - i;
            flags[i] = true;

            // finish doorway
            IN_WAITING_SECTION.getAndIncrement();
            while (flags[j]) {
                // wait
                assert IN_WAITING_SECTION.get() == 1;
            }
            System.out.println(String.format("[tid=%d] acquire", i));
        }

        @Override
        public void unlock() {
            int i = this.getThreadId();
            flags[i] = false;
            System.out.println(String.format("[tid=%d] release", getThreadId()));
        }
    }

    /**
     * LockTwo
     * <p>
     * 片方のスレッドの実行が先行すると, デッドロックが起こる
     */
    public static class LockTwo implements Lock {
        private int victim;

        @Override
        public void lock() {
            int i = this.getThreadId();
            victim = i;

            // finish doorway
            while (victim == i) {
                // wait
            }
            System.out.println(String.format("[tid=%d] acquire", i));
        }

        @Override
        public void unlock() {
            System.out.println(String.format("[tid=%d] release", getThreadId()));
        }
    }
}
