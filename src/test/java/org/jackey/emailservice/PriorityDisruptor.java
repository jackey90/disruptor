package org.jackey.emailservice;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.dsl.ProducerType;

public class PriorityDisruptor {

	private static Log logger = LogFactory.getLog(PriorityDisruptor.class);
	
	private RingBuffer<MessageEvent>[] ringBuffers;
	private SequenceBarrier[] barriers;
	public static final int NUM_THREAD = 1000;

	public static final long NUM1 = 10000;
	public static final long NUM2 = 100000;
	public static final long NUM3 = 100000;

	public PriorityDisruptor() {
		final Map<Integer, Integer> configMap = PriorityConfig.getInstance()
				.getConfig();
		final int numOfRings = configMap.size();
		ringBuffers = new RingBuffer[numOfRings];
		barriers = new SequenceBarrier[numOfRings];
		int i = 0;
		for (Map.Entry<Integer, Integer> entry : configMap.entrySet()) {
			final int priority = entry.getKey();
			final int bufferSize = entry.getValue();
			ringBuffers[i] = RingBuffer.create(ProducerType.SINGLE,
					MessageEvent.EVENT_FACTORY, bufferSize,
					new PriorityWaitStrategy());
			barriers[i] = ringBuffers[i].newBarrier();
			i++;
		}

		final ExecutorService executor = Executors
				.newFixedThreadPool(NUM_THREAD);

		PriorityEventProcessor<MessageEvent>[] processors = new PriorityEventProcessor[NUM_THREAD];
		for (i = 0; i < NUM_THREAD; i++) {
			processors[i] = new PriorityEventProcessor(ringBuffers, barriers,
					new MessageHandler());
			for (int j = 0; j < ringBuffers.length; j++) {
				ringBuffers[j]
						.addGatingSequences(processors[i].getSequences()[j]);
			}

			executor.execute(processors[i]);
		}

	}

	public RingBuffer<MessageEvent>[] getRingBuffers() {
		return ringBuffers;
	}

	public void setRingBuffers(RingBuffer<MessageEvent>[] ringBuffers) {
		this.ringBuffers = ringBuffers;
	}

	public SequenceBarrier[] getBarriers() {
		return barriers;
	}

	public void setBarriers(SequenceBarrier[] barriers) {
		this.barriers = barriers;
	}

	public static void main(String[] args) {
		
		logger.info("PriorityDisruptor***************************************************begin");
		PriorityDisruptor p = new PriorityDisruptor();
		final RingBuffer<MessageEvent>[] ringBuffers = p.getRingBuffers();


		final ExecutorService executor = Executors.newFixedThreadPool(3);
		executor.execute(new Runnable() {

			@Override
			public void run() {
				int i = 0;
				while (true) {
					if (i++ > NUM1) {
						break;
					}
					ringBuffers[0].publishEvent(MessageEvent.TRANSLATOR,
							Message.getMessage(1));
				}
			}
		});

		executor.execute(new Runnable() {

			@Override
			public void run() {
				int i = 0;
				while (true) {
					if (i++ > NUM2) {
						break;
					}
					ringBuffers[1].publishEvent(MessageEvent.TRANSLATOR,
							Message.getMessage(2));
				}
			}
		});

		executor.execute(new Runnable() {

			@Override
			public void run() {
				int i = 0;
				while (true) {
					if (i++ > NUM3) {
						break;
					}
					ringBuffers[2].publishEvent(MessageEvent.TRANSLATOR,
							Message.getMessage(3));
				}
			}
		});

	}
}
