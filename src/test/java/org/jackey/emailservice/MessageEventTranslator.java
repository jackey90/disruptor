package org.jackey.emailservice;

import javax.jms.TextMessage;

import com.lmax.disruptor.EventTranslatorOneArg;

public class MessageEventTranslator implements EventTranslatorOneArg<MessageEvent, TextMessage> {

	public void translateTo(MessageEvent event, long sequence,
			TextMessage message) {
		event.setMessage(message);
	}

}
