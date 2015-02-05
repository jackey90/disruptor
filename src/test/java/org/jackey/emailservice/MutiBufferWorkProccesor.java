package org.jackey.emailservice;

import java.util.concurrent.atomic.AtomicBoolean;

import com.lmax.disruptor.AlertException;
import com.lmax.disruptor.DataProvider;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventProcessor;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.Sequencer;
import com.lmax.disruptor.TimeoutException;
import com.lmax.disruptor.WorkHandler;

public class MutiBufferWorkProccesor<T> implements EventProcessor {

	private final AtomicBoolean running = new AtomicBoolean(false);
    private final Sequence[] sequences;
    private final DataProvider<T>[] providers;
    private final SequenceBarrier[] barriers;
    private final EventHandler<T> handler;
    private final ExceptionHandler exceptionHandler;
    private final Sequence[] workSequences;
	private final MutiBufferProcessStrategy strategy;

	
	public MutiBufferWorkProccesor(final DataProvider<T>[] providers,
			final SequenceBarrier[] barriers, final EventHandler<T> handler,final ExceptionHandler exceptionHandler,
			final Sequence[] workSequences, final MutiBufferProcessStrategy strategy) {
		this.providers = providers;
		this.barriers = barriers;
		this.handler = handler;
		this.exceptionHandler = exceptionHandler;
		this.workSequences = workSequences;
		this.strategy = strategy;
		final int ringNum = providers.length;
		sequences = new Sequence[ringNum];
		for(int i = 0; i < ringNum; i++){
			sequences[i] = new Sequence(Sequencer.INITIAL_CURSOR_VALUE);
		}
	}

	@Override
	public void run() {
		if (!running.compareAndSet(false, true)) {
			throw new RuntimeException("Already running");
		}
		for (SequenceBarrier barrier : barriers) {
			barrier.clearAlert();
		}
		
		try {
			strategy.process(providers, barriers, handler, sequences);
		} catch (AlertException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Sequence getSequence() {
		return null;
	}

	public Sequence getSequenceForBuffer(int bufferIndex) {
		return sequences[bufferIndex];
	}

	@Override
	public void halt() {
		running.set(false);
	}
	
	@Override
	public boolean isRunning() {
		return running.get();
	}

	public Sequence[] getSequences() {
		return sequences;
	}

	public DataProvider<T>[] getProviders() {
		return providers;
	}

	public SequenceBarrier[] getBarriers() {
		return barriers;
	}

	public EventHandler<T> getHandler() {
		return handler;
	}

	public ExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

	public Sequence[] getWorkSequences() {
		return workSequences;
	}
	
}
