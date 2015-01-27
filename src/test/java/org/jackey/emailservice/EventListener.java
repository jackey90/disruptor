package org.jackey.emailservice;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

public class EventListener {
	private static Log logger = LogFactory.getLog(EventListener.class);

	private static final long ITERATIONS = 1000000L;
	private static final int BUFFER_SIZE = 1 << 15;
	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	private final MessageHandler handler = new MessageHandler();
	private final RingBuffer<MessageEvent> ringBuffer;

	Disruptor<MessageEvent> disruptor = new Disruptor<MessageEvent>(
			MessageEvent.EVENT_FACTORY, BUFFER_SIZE, executor,
			ProducerType.SINGLE, new YieldingWaitStrategy());

	public  EventListener(){
		disruptor.handleEventsWith(handler);
		this.ringBuffer = disruptor.start();
	}

	public void test(){
		long start = System.currentTimeMillis();
		for (long l = 0; l < ITERATIONS; l++){
			ringBuffer.publishEvent(MessageEventTranslator.INSTANCE, Message.getMessage());
		}
		long opsPerSecond = (ITERATIONS * 1000L) / (System.currentTimeMillis() - start);
		System.out.println(opsPerSecond);
	}
	
	public static void main(String[] args) {
		new EventListener().test();
	}
}