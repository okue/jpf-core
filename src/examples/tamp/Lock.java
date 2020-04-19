package tamp;

public interface Lock {
    public void lock();

    public void unlock();

    default int getThreadId() {
        return Integer.parseInt(Thread.currentThread().getName());
    }
}
