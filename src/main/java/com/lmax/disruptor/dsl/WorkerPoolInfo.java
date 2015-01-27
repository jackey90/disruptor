package com.lmax.disruptor.dsl;

import com.lmax.disruptor.*;

import java.util.concurrent.Executor;

class WorkerPoolInfo<T> implements ConsumerInfo
{
    private final WorkerPool<T> workerPool;
    private final SequenceBarrier sequenceBarrier;
    private boolean endOfChain = true;

    public WorkerPoolInfo(final WorkerPool<T> workerPool, final SequenceBarrier sequenceBarrier)
    {
        this.workerPool = workerPool;
        this.sequenceBarrier = sequenceBarrier;
    }

    public Sequence[] getSequences()
    {
        return workerPool.getWorkerSequences();
    }

    public SequenceBarrier getBarrier()
    {
        return sequenceBarrier;
    }

    public boolean isEndOfChain()
    {
        return endOfChain;
    }

    public void start(final Executor executor)
    {
        workerPool.start(executor);
    }

    public void halt()
    {
        workerPool.halt();
    }

    public void markAsUsedInBarrier()
    {
        endOfChain = false;
    }

    public boolean isRunning()
    {
        return workerPool.isRunning();
    }
}
