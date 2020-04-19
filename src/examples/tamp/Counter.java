package tamp;

public class Counter {
    private long v;
    private Lock lock;

    public Counter(Lock lock) {
        this.lock = lock;
    }

    public long getAndIncrement() {
        lock.lock();
        try {
            long tmp = v;
            v = tmp + 1;
            return tmp;
        } finally {
            lock.unlock();
        }
    }
}
