package org.jackey.emailservice;

import java.util.concurrent.atomic.AtomicLong;

import javax.jms.TextMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lmax.disruptor.LifecycleAware;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.util.PaddedLong;

public class MessageHandler1 implements WorkHandler<MessageEvent>,LifecycleAware{

	private static Log logger = LogFactory.getLog(MessageHandler1.class);
	private final AtomicLong value1 = new AtomicLong(0);
	private final AtomicLong value2 = new AtomicLong(0);
	private final AtomicLong value3 = new AtomicLong(0);
	
	@Override
	public void onEvent(MessageEvent event) throws Exception {
		TextMessage message = event.getMessage();
		logger.info("JMSPriority=" + message.getJMSPriority() + "JMSMessageID="
				+ message.getJMSMessageID());
		switch(message.getJMSPriority()){
		case 1:
			value1.incrementAndGet();
			break;
		case 2:
			value2.incrementAndGet();
			break;
		default:
			value3.incrementAndGet();
			break;
		}
	}
	

	public AtomicLong getValue1() {
		return value1;
	}


	public AtomicLong getValue2() {
		return value2;
	}




	public AtomicLong getValue3() {
		return value3;
	}




	@Override
	public void onStart() {
		
	}

	@Override
	public void onShutdown() {
		
	}
	
}
