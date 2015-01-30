package org.jackey.emailservice;

import com.lmax.disruptor.EventProcessor;
import com.lmax.disruptor.Sequence;

public class PriorityEventProcessor implements EventProcessor{

	@Override
	public void run() {
		
	}

	@Override
	public Sequence getSequence() {
		return null;
	}

	@Override
	public void halt() {
		
	}

	@Override
	public boolean isRunning() {
		return false;
	}

}
