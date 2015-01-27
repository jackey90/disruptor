package org.jackey.emailservice;

import javax.jms.TextMessage;

import com.lmax.disruptor.EventHandler;

public class MessageHandler implements EventHandler<MessageEvent>{

	public void onEvent(MessageEvent event, long sequence, boolean endOfBatch)
			throws Exception {
		TextMessage message = event.getMessage();
		
	}

}
