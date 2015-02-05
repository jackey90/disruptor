package org.jackey.emailservice;

import javax.jms.TextMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lmax.disruptor.LifecycleAware;
import com.lmax.disruptor.WorkHandler;

public class MessageHandler1 implements WorkHandler<MessageEvent>,LifecycleAware{

	private static Log logger = LogFactory.getLog(MessageHandler1.class);
	
	@Override
	public void onEvent(MessageEvent event) throws Exception {
		TextMessage message = event.getMessage();
		logger.info("JMSPriority=" + message.getJMSPriority() + "JMSMessageID="
				+ message.getJMSMessageID());
		logger.info("MessageHandler1 Text=" + message.getText());
		Thread.sleep(100);
	}

	@Override
	public void onStart() {
		
	}

	@Override
	public void onShutdown() {
		
	}
	
}
