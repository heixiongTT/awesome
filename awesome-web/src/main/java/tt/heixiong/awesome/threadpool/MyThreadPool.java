package tt.heixiong.awesome.threadpool;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class MyThreadPool {

    private Integer maxSize = 10;

    private Integer minSize = 2;

    private Integer coreSize = 4;

    private Set<Worker> works;

    public MyThreadPool(Integer maxSize, Integer minSize, Integer coreSize) {
        this.maxSize = maxSize;
        this.minSize = minSize;
        this.coreSize = coreSize;
        this.works = new ConcurrentHashSet<>();
    }

    protected static class ConcurrentHashSet<T> extends AbstractSet<T> {

        /**
         * The delegate map.
         */
        private final ConcurrentMap<T, Boolean> delegate;

        /**
         * Creates a concurrent hash set.
         */
        protected ConcurrentHashSet() {
            delegate = new ConcurrentHashMap<T, Boolean>();
        }

        @Override
        public boolean add(T value) {
            return delegate.put(value, Boolean.TRUE) == null;
        }

        @Override
        public boolean remove(Object value) {
            return delegate.remove(value) != null;
        }

        /**
         * {@inheritDoc}
         */
        public Iterator<T> iterator() {
            return delegate.keySet().iterator();
        }

        /**
         * {@inheritDoc}
         */
        public int size() {
            return delegate.size();
        }
    }

    private class Worker extends AbstractQueuedSynchronizer implements Runnable {

        @Override
        public void run() {

        }
    }
}
