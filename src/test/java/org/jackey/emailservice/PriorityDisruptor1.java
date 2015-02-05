package org.jackey.emailservice;

import java.util.Map;

import com.lmax.disruptor.FatalExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WorkerPool;
import com.lmax.disruptor.dsl.ProducerType;

public class PriorityDisruptor1 {
	final Map<Integer, Integer> configMap = PriorityConfig.getInstance()
			.getConfig();
	final int numOfRings = configMap.size();
	
	private final RingBuffer<MessageEvent>[] ringBuffers;
	private final WorkerPool<MessageEvent>[] workerPools;
	public static final int NUM_THREAD = 1000;

	private final MessageHandler1[] handles = new MessageHandler1[NUM_THREAD];
	{
		for(int i = 0; i < NUM_THREAD; i++){
			handles[i] = new MessageHandler1();
		}
	}
	
	public static final long NUM1 = 10000;
	public static final long NUM2 = 100000;
	public static final long NUM3 = 100000;
	
	public PriorityDisruptor1(){
		ringBuffers = new RingBuffer[numOfRings];
		workerPools = new WorkerPool[numOfRings];
		int i = 0;
		for (Map.Entry<Integer, Integer> entry : configMap.entrySet()) {
			final int priority = entry.getKey();
			final int bufferSize = entry.getValue();
			ringBuffers[i] = RingBuffer.create(ProducerType.SINGLE,
					MessageEvent.EVENT_FACTORY, bufferSize,
					new PriorityWaitStrategy());
			workerPools[i] = new WorkerPool(ringBuffers[i],ringBuffers[i].newBarrier(),new FatalExceptionHandler(),);
			i++;
		}
	}
}
