package org.jackey.emailservice;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.Sequencer;
import com.lmax.disruptor.WorkProcessor;
import com.lmax.disruptor.dsl.ConsumerRepository;

/**
 * This pool carries multi buffers and multi processor, you can use strategy to
 * deciede how to consume these buffers.
 * 
 * @author jackey90.hj@gmail.com
 * @date Feb 5, 2015
 *
 */
public class MutiBufferWorkerPool<T> {
	private final AtomicBoolean started = new AtomicBoolean(false);
	private final Sequence[] workSequences;
	private final RingBuffer<T>[] ringBuffers;
	private final SequenceBarrier[] sequenceBarriers;
	private final MutiBufferWorkProccesor[] proccessors;

	private final ConsumerRepository<T> consumerRepository = new ConsumerRepository();

	public MutiBufferWorkerPool(final RingBuffer<T>[] ringBuffers,
			final SequenceBarrier[] sequenceBarriers,
			final ExceptionHandler[] exceptionHandlers,
			final MutiBufferWorkHandler<T>... workHandlers){
		this.ringBuffers = ringBuffers;
		this.sequenceBarriers = sequenceBarriers;
		final int bufferNums = ringBuffers.length;
		workSequences = new Sequence[bufferNums];
		for(int i = 0; i < bufferNums; i++){
			workSequences[i] = new Sequence(Sequencer.INITIAL_CURSOR_VALUE);
		}

		final int workerNums = workHandlers.length;
		proccessors = new MutiBufferWorkProccesor[workerNums];
		for(int i = 0; i < workerNums; i++){
			proccessors[i] = new MutiBufferWorkProccesor(ringBuffers, sequenceBarriers, workHandlers[i], exceptionHandlers[i], workSequences, new PriorityProcessStrategy());
		}

		for(int i = 0; i < bufferNums; i++){
			ringBuffers[i].addGatingSequences(getGatingSequencesForBuffer(i));
		}

	}


	public Sequence[] getGatingSequencesForBuffer(int bufferIndex){
		final Sequence[] sequences = new Sequence[proccessors.length + 1];
		for (int i = 0;i < proccessors.length; i++) {
			sequences[i] = proccessors[i].getSequenceForBuffer(bufferIndex);
		}
		sequences[sequences.length - 1] = workSequences[bufferIndex];

		return sequences;
	}

	public void start(Executor executor){
		if (!started.compareAndSet(false, true))
		{
			throw new IllegalStateException("WorkerPool has already been started and cannot be restarted until halted.");
		}

		for (MutiBufferWorkProccesor<?> processor : proccessors)
		{
			executor.execute(processor);
		}
	}

	public void drainAndHalt(){
		
	}


}
