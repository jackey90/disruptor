package org.jackey.emailservice;

import java.util.concurrent.atomic.AtomicBoolean;

import com.lmax.disruptor.AlertException;
import com.lmax.disruptor.DataProvider;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventProcessor;
import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.TimeoutException;

public class PriorityEventProcessor<T> implements EventProcessor {

	private final AtomicBoolean isRunning = new AtomicBoolean(false);
	private final DataProvider<T>[] providers;
	private final SequenceBarrier[] barriers;
	private final EventHandler<T> handler;
	private final Sequence[] sequences;

	public PriorityEventProcessor(DataProvider<T>[] providers,
			SequenceBarrier[] barriers, EventHandler<T> handler) {
		this.providers = providers;
		this.barriers = barriers;
		this.handler = handler;
		this.sequences = new Sequence[providers.length];
		for (int i = 0; i < sequences.length; i++) {
			sequences[i] = new Sequence(-1);
		}
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

	public Sequence[] getSequences() {
		return sequences;
	}

	@Override
	public void run() {
		if (!isRunning.compareAndSet(false, true)) {
			throw new RuntimeException("Already running");
		}
		for (SequenceBarrier barrier : barriers) {
			barrier.clearAlert();
		}

		final int len = providers.length;
		boolean processed = false;
		while (true) {
			try {
				for (int i = 0; i < len; i++) {
					Sequence sequence = sequences[i];
					final long previous = sequence.get();
					final long next = previous + 1;
					final long available = barriers[i].waitFor(next);
					if (available >= next) {
						handler.onEvent(providers[i].get(next), next,
								previous == available);
						sequence.set(next);
						processed = true;
						break;
					}
				}
				if (processed) {
					continue;
				} else {
					Thread.sleep(10000);
				}
			} catch (AlertException e) {
				if (!isRunning()) {
					break;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (TimeoutException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}

	}

	@Override
	public Sequence getSequence() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void halt() {
		isRunning.set(false);
		for (SequenceBarrier sb : barriers) {
			sb.alert();
		}
	}

	@Override
	public boolean isRunning() {
		return isRunning.get();
	}

}
