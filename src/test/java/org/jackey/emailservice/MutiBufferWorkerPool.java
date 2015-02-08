package org.jackey.emailservice;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.FatalExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.Sequencer;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.dsl.ConsumerRepository;
import com.lmax.disruptor.dsl.ProducerType;

/**
 * This pool carries multi buffers and multi processor, you can use strategy to
 * deciede how to consume these buffers.
 * 
 * @author jackey90.hj@gmail.com
 * @date Feb 5, 2015
 *
 */
public class MutiBufferWorkerPool<T> {
	private final AtomicBoolean started = new AtomicBoolean(false);
	private final Sequence[] workSequences;
	private final RingBuffer<T>[] ringBuffers;
	private final SequenceBarrier[] sequenceBarriers;
	private final MutiBufferWorkProccesor[] proccessors;
	private final ExceptionHandler[] exceptionHandlers;
	private  final WorkHandler<T>[] workHandlers;

	private final ConsumerRepository<T> consumerRepository = new ConsumerRepository();

	public MutiBufferWorkerPool(final RingBuffer<T>[] ringBuffers,
			final SequenceBarrier[] sequenceBarriers,
			final ExceptionHandler[] exceptionHandlers,
			final WorkHandler<T>... workHandlers){
		this.ringBuffers = ringBuffers;
		this.sequenceBarriers = sequenceBarriers;
		this.exceptionHandlers = exceptionHandlers;
		this.workHandlers = workHandlers;

		final int bufferNums = ringBuffers.length;
		workSequences = new Sequence[bufferNums];
		//each RingBuffer has a Work Sequence
		for(int i = 0; i < bufferNums; i++){
			workSequences[i] = new Sequence(Sequencer.INITIAL_CURSOR_VALUE);
		}

		final int workerNums = workHandlers.length;
		proccessors = new MutiBufferWorkProccesor[workerNums];
		for(int i = 0; i < workerNums; i++){
			proccessors[i] = new MutiBufferWorkProccesor(ringBuffers, sequenceBarriers, workHandlers[i], exceptionHandlers[i], workSequences, new PriorityProcessStrategy());
		}

		for(int i = 0; i < bufferNums; i++){
			ringBuffers[i].addGatingSequences(getGatingSequencesForBuffer(i));
		}

	}


	public Sequence[] getGatingSequencesForBuffer(int bufferIndex){
		final Sequence[] sequences = new Sequence[proccessors.length + 1];
		for (int i = 0;i < proccessors.length; i++) {
			sequences[i] = proccessors[i].getSequenceForBuffer(bufferIndex);
		}
		sequences[sequences.length - 1] = workSequences[bufferIndex];

		return sequences;
	}

	public void start(Executor executor){
		if (!started.compareAndSet(false, true))
		{
			throw new IllegalStateException("WorkerPool has already been started and cannot be restarted until halted.");
		}

		for (MutiBufferWorkProccesor<?> processor : proccessors)
		{
			executor.execute(processor);
		}
	}

	public void drainAndHalt(){

	}

	public void halt(){

	}

	public static class Test{
		public static void main(String[] args) {
			final int NUM_THREAD = 10;

			final long NUM1 = 10003;
			final long NUM2 = 10003;
			final long NUM3 = 10005;
			RingBuffer<MessageEvent>[] ringBuffers;
			SequenceBarrier[] barriers;

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

			WorkHandler<MessageEvent>[] handlers = new MessageHandler1[NUM_THREAD];
			ExceptionHandler[] exceptionHandlers = new FatalExceptionHandler[NUM_THREAD];
			for( i = 0; i < NUM_THREAD; i++){
				handlers[i] = new MessageHandler1();
				exceptionHandlers[i] = new FatalExceptionHandler();
			}

			MutiBufferWorkerPool<MessageEvent> pool = new MutiBufferWorkerPool<MessageEvent>(ringBuffers, barriers, exceptionHandlers, handlers);
			pool.start(executor);
			
			putData(ringBuffers,NUM1,NUM2,NUM3);
			
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			sum((MessageHandler1[])handlers);
		}
	}
	
	
	public static void sum(MessageHandler1[] handlers){
		long l1 = 0;
		long l2 = 0;
		long l3 = 0;
		for(MessageHandler1 h: handlers){
			l1 += h.getValue1().get();
			l2 += h.getValue2().get();
			l3 += h.getValue3().get();
		}
		System.out.println(l1);
		System.out.println(l2);
		System.out.println(l3);
	}
	
	public static void putData(final RingBuffer<MessageEvent>[] ringBuffers, final long NUM1,final long NUM2,final long NUM3){
		final ExecutorService executor = Executors.newFixedThreadPool(3);
		executor.execute(new Runnable() {

			@Override
			public void run() {
				long i = 0;
				while (true) {
					if (i++ >= NUM1) {
						break;
					}
					ringBuffers[0].publishEvent(MessageEvent.TRANSLATOR,
							Message.getMessage(1));
				}

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

//				i = 0;
//				while (true) {
//					if (i++ > NUM1) {
//						break;
//					}
//					ringBuffers[0].publishEvent(MessageEvent.TRANSLATOR,
//							Message.getMessage(1));
//				}
			}
		});

		executor.execute(new Runnable() {

			@Override
			public void run() {
				long i = 0;
				while (true) {
					if (i++ >= NUM2) {
						break;
					}
					ringBuffers[1].publishEvent(MessageEvent.TRANSLATOR,
							Message.getMessage(2));
				}

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

//				i = 0;
//				while (true) {
//					if (i++ > NUM2) {
//						break;
//					}
//					ringBuffers[1].publishEvent(MessageEvent.TRANSLATOR,
//							Message.getMessage(2));
//				}

			}
		});

		executor.execute(new Runnable() {

			@Override
			public void run() {
				long i = 0;
				while (true) {
					if (i++ >= NUM3) {
						break;
					}
					ringBuffers[2].publishEvent(MessageEvent.TRANSLATOR,
							Message.getMessage(3));
				}

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				
//				i = 0;
//				while (true) {
//					if (i++ > NUM3) {
//						break;
//					}
//					ringBuffers[2].publishEvent(MessageEvent.TRANSLATOR,
//							Message.getMessage(3));
//				}
			}
		});
	}

}
