package org.jackey.emailservice;

import javax.jms.TextMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lmax.disruptor.EventHandler;

public class MessageHandler implements EventHandler<MessageEvent> {

	private static Log logger = LogFactory.getLog(MessageHandler.class);

	public void onEvent(MessageEvent event, long sequence, boolean endOfBatch)
			throws Exception {
		if (!event.isProcesssed()) {
			event.setProcesssed(true);
			TextMessage message = event.getMessage();
			logger.info("MessageHandler JMSMessageID="
					+ message.getJMSMessageID());
			logger.info("MessageHandler Text=" + message.getText());
			logger.info("MessageHandler JMSPriority="
					+ message.getJMSPriority());
			Thread.sleep(50);
		}
	}

}
