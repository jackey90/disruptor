package org.jackey.emailservice;

import java.util.concurrent.atomic.AtomicBoolean;

import com.lmax.disruptor.AlertException;
import com.lmax.disruptor.DataProvider;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventProcessor;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.LifecycleAware;
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
	private final WorkHandler<T> handler;
	private final ExceptionHandler exceptionHandler;
	private final Sequence[] workSequences;
	private final AtomicBoolean[] processedSequences;
	private final MutiBufferProcessStrategy strategy;

	public MutiBufferWorkProccesor(final DataProvider<T>[] providers,
			final SequenceBarrier[] barriers, final WorkHandler<T> handler,
			final ExceptionHandler exceptionHandler,
			final Sequence[] workSequences,
			final MutiBufferProcessStrategy strategy) {
		this.providers = providers;
		this.barriers = barriers;
		this.handler = handler;
		this.exceptionHandler = exceptionHandler;
		this.workSequences = workSequences;
		this.strategy = strategy;
		final int ringNum = providers.length;
		sequences = new Sequence[ringNum];
		processedSequences = new AtomicBoolean[ringNum];
		for (int i = 0; i < ringNum; i++) {
			sequences[i] = new Sequence(Sequencer.INITIAL_CURSOR_VALUE);
			processedSequences[i] = new AtomicBoolean(true);
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
		notifyStart();
		while (true) {
			try {
				while (true) {
					process(0, Long.MAX_VALUE);
					process(1, Long.MAX_VALUE);
					process(2, Long.MAX_VALUE);
					Thread.sleep(100);
				}
			} catch (AlertException e) {
				if (!running.get()) {
					break;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (TimeoutException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		notifyShutdown();
		running.set(false);
	}

	private void process(int index, long times) throws Exception {
		long nextSequence = sequences[index].get() + 1;
		boolean processedSequence = processedSequences[index].get();
		long i = 0;
		long cachedAvailableSequence = Long.MIN_VALUE;

		while (i++ <= times) {
			if (processedSequence) {
				processedSequence = false;
				do {
					nextSequence = workSequences[index].get() + 1L;
					sequences[index].set(nextSequence - 1L);
				} while (!workSequences[index].compareAndSet(nextSequence - 1L,
						nextSequence));
			}

			cachedAvailableSequence = barriers[index].waitFor(nextSequence);

			// have available event
			if (cachedAvailableSequence >= nextSequence) {
				handler.onEvent(providers[index].get(nextSequence));
				processedSequence = true;
			}
			// no event to process currently
			else {
				System.out.println("cachedAvailableSequence="
						+ cachedAvailableSequence + " nextSequence="
						+ nextSequence);
				break;
			}
		}

		processedSequences[index].set(processedSequence);
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

	private void notifyStart() {
		if (handler instanceof LifecycleAware) {
			try {
				((LifecycleAware) handler).onStart();
			} catch (final Throwable ex) {
				exceptionHandler.handleOnStartException(ex);
			}
		}
	}

	private void notifyShutdown() {
		if (handler instanceof LifecycleAware) {
			try {
				((LifecycleAware) handler).onShutdown();
			} catch (final Throwable ex) {
				exceptionHandler.handleOnShutdownException(ex);
			}
		}
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

	public WorkHandler<T> getHandler() {
		return handler;
	}

	public ExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

	public Sequence[] getWorkSequences() {
		return workSequences;
	}

}
