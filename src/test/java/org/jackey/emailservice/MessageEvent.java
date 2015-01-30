package org.jackey.emailservice;

import javax.jms.TextMessage;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventTranslatorOneArg;

public final class MessageEvent {
	private volatile TextMessage message;
	private volatile boolean processsed;
	
	public static final MessageEventTranslator TRANSLATOR = new MessageEventTranslator();
	
	public TextMessage getMessage() {
		return message;
	}

	public void setMessage(final TextMessage message) {
		this.message = message;
	}

	public static final EventFactory<MessageEvent> EVENT_FACTORY = new EventFactory<MessageEvent>() {
		public MessageEvent newInstance() {
			return new MessageEvent();
		}
	};
	
	public static class MessageEventTranslator implements EventTranslatorOneArg<MessageEvent, TextMessage> {
		
		public void translateTo(final MessageEvent event,final long sequence,
				final TextMessage message) {
			event.setMessage(message);
		}
	}
	
}
