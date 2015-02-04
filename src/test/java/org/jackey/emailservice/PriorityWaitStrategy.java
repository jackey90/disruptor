package org.jackey.emailservice;

import com.lmax.disruptor.AlertException;
import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.TimeoutException;
import com.lmax.disruptor.WaitStrategy;

public class PriorityWaitStrategy implements WaitStrategy {

	@Override
	public long waitFor(long sequence, Sequence cursor,
			Sequence dependentSequence, SequenceBarrier barrier)
					throws AlertException, InterruptedException, TimeoutException {
		long availableSequence;

		if ((availableSequence = dependentSequence.get()) < sequence) {
			//System.out.println(dependentSequence.getClass() + " waiting for" + sequence + ", availableSequence="+availableSequence);
			barrier.checkAlert();
		}
		return availableSequence;
	}

	@Override
	public void signalAllWhenBlocking() {

	}

}
