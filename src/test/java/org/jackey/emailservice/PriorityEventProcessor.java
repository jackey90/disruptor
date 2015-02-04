package org.jackey.emailservice;

import java.util.concurrent.atomic.AtomicBoolean;

import com.lmax.disruptor.AlertException;
import com.lmax.disruptor.DataProvider;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventProcessor;
import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.TimeoutException;

public class PriorityEventProcessor<T> implements EventProcessor{

	private final AtomicBoolean isRunning = new AtomicBoolean(false);
	private final DataProvider<T>[] providers;
	private final SequenceBarrier[] barriers;
	private final EventHandler<T> handler;
	private final Sequence[] sequences;

	public PriorityEventProcessor(DataProvider<T>[] providers, SequenceBarrier[] barriers, EventHandler<T> handler){
		this.providers = providers;
		this.barriers = barriers;
		this.handler = handler;
		this.sequences = new Sequence[providers.length];
		for (int i = 0; i < sequences.length; i++)
		{
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
		if (!isRunning.compareAndSet(false, true))
		{
			throw new RuntimeException("Already running");
		}
		for (SequenceBarrier barrier : barriers)
		{
			barrier.clearAlert();
		}

		final int barrierLength = barriers.length;
		T event = null;
		while (true)
		{
			try
			{
				for (int i = 0; i < barrierLength; i++)
				{
					long available = barriers[i].waitFor(-1);
					Sequence sequence = sequences[i];

					long previous = sequence.get();

					for (long l = previous + 1; l <= available; l++)
					{
						handler.onEvent(providers[i].get(l), l, previous == available);
					}
					sequence.set(available);
				}
				Thread.yield();
			}
			catch (AlertException e)
			{
				if (!isRunning())
				{
					break;
				}
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			catch (TimeoutException e)
			{
				e.printStackTrace();
			}
			catch (Exception e)
			{
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
	}

	@Override
	public boolean isRunning() {
		return isRunning.get();
	}

}
