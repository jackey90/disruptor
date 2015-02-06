package org.jackey.emailservice;

import com.lmax.disruptor.AlertException;
import com.lmax.disruptor.DataProvider;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.TimeoutException;

public interface MutiBufferProcessStrategy {
	
	public boolean process(final DataProvider[] providers,
			SequenceBarrier[] barriers, EventHandler handler,
			Sequence[] sequences)throws AlertException, InterruptedException, TimeoutException;
	
}
