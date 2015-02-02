package org.jackey.emailservice;

import java.util.Map;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.dsl.ProducerType;

public class PriorityDisruptor {

	private RingBuffer<MessageEvent>[] ringBuffers;
	private SequenceBarrier[] barriers;
	public static final int NUM_THREAD = 100;
	
	public PriorityDisruptor() {
		final Map<Integer,Integer> configMap = PriorityConfig.getInstance().getConfig();
		final int numOfRings = configMap.size();
		ringBuffers = new RingBuffer[numOfRings];
		barriers = new SequenceBarrier[numOfRings];
		int i = 0;
		for(Map.Entry<Integer, Integer> entry: configMap.entrySet()){
			final int priority = entry.getKey();
			final int bufferSize = entry.getValue();
			ringBuffers[i] = RingBuffer.create(ProducerType.SINGLE, MessageEvent.EVENT_FACTORY, bufferSize, new BlockingWaitStrategy());
			barriers[i] = ringBuffers[i].newBarrier();
		}
		
		PriorityEventProcessor<MessageEvent>[] processors = new PriorityEventProcessor[NUM_THREAD];
		for(i = 0; i < NUM_THREAD; i++){
			processors[i] = new PriorityEventProcessor(ringBuffers,barriers,new MessageHandler());
			for(int j = 0; j <ringBuffers.length; j++ ){
				ringBuffers[j].addGatingSequences(processors[i].getSequences()[j]);
			}
		}
		
		
	}

}
