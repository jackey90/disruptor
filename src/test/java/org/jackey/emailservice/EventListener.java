package org.jackey.emailservice;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import sun.rmi.runtime.Log;

import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

public class EventListener {
	private static Log logger = LogFactory.getLog(EventListener.class);
	
	private static final int BUFFER_SIZE = 1 << 15;
	private final ExecutorService executor = Executors.newFixedThreadPool(10);

	Disruptor<MessageEvent> disruptor = new Disruptor<MessageEvent>(
			MessageEvent.EVENT_FACTORY, BUFFER_SIZE, executor,
			ProducerType.SINGLE, new YieldingWaitStrategy());
	
	
}