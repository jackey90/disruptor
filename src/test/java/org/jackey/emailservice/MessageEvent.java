package org.jackey.emailservice;

import javax.jms.TextMessage;

import com.lmax.disruptor.EventFactory;

public final class MessageEvent {
	private TextMessage message;

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
}
