package org.jackey.emailservice;

import java.util.Enumeration;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.TextMessage;

public class Message implements TextMessage {

	static Random r = new Random();
	private static AtomicLong i1 = new AtomicLong(0L);
	private static AtomicLong i2 = new AtomicLong(0L);
	private static AtomicLong i3 = new AtomicLong(0L);

	private long id;
	private int priority;

	public Message(int priority) {
		
		this.priority = priority;
		switch (priority) {
		case 1:
			id = i1.incrementAndGet();
			break;
		case 2:
			id = i2.incrementAndGet();
			break;
		default:
			id = i3.incrementAndGet();
			break;
		}
	}
	

	public int getPriority() {
		return priority;
	}


	public void setPriority(int priority) {
		this.priority = priority;
	}



	public void acknowledge() throws JMSException {

	}

	public void clearBody() throws JMSException {
		// TODO Auto-generated method stub

	}

	public void clearProperties() throws JMSException {
		// TODO Auto-generated method stub

	}

	public boolean getBooleanProperty(String arg0) throws JMSException {
		// TODO Auto-generated method stub
		return false;
	}

	public byte getByteProperty(String arg0) throws JMSException {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getDoubleProperty(String arg0) throws JMSException {
		// TODO Auto-generated method stub
		return 0;
	}

	public float getFloatProperty(String arg0) throws JMSException {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getIntProperty(String arg0) throws JMSException {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getJMSCorrelationID() throws JMSException {
		// TODO Auto-generated method stub
		return null;
	}

	public byte[] getJMSCorrelationIDAsBytes() throws JMSException {
		// TODO Auto-generated method stub
		return null;
	}

	public int getJMSDeliveryMode() throws JMSException {
		// TODO Auto-generated method stub
		return 0;
	}

	public Destination getJMSDestination() throws JMSException {
		// TODO Auto-generated method stub
		return null;
	}

	public long getJMSExpiration() throws JMSException {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getJMSMessageID() throws JMSException {
		return id + "";
	}

	public int getJMSPriority() throws JMSException {
		return priority;
	}

	public boolean getJMSRedelivered() throws JMSException {
		// TODO Auto-generated method stub
		return false;
	}

	public Destination getJMSReplyTo() throws JMSException {
		// TODO Auto-generated method stub
		return null;
	}

	public long getJMSTimestamp() throws JMSException {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getJMSType() throws JMSException {
		// TODO Auto-generated method stub
		return null;
	}

	public long getLongProperty(String arg0) throws JMSException {
		// TODO Auto-generated method stub
		return 0;
	}

	public Object getObjectProperty(String arg0) throws JMSException {
		// TODO Auto-generated method stub
		return null;
	}

	public Enumeration getPropertyNames() throws JMSException {
		// TODO Auto-generated method stub
		return null;
	}

	public short getShortProperty(String arg0) throws JMSException {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getStringProperty(String arg0) throws JMSException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean propertyExists(String arg0) throws JMSException {
		// TODO Auto-generated method stub
		return false;
	}

	public void setBooleanProperty(String arg0, boolean arg1)
			throws JMSException {
		// TODO Auto-generated method stub

	}

	public void setByteProperty(String arg0, byte arg1) throws JMSException {
		// TODO Auto-generated method stub

	}

	public void setDoubleProperty(String arg0, double arg1) throws JMSException {
		// TODO Auto-generated method stub

	}

	public void setFloatProperty(String arg0, float arg1) throws JMSException {
		// TODO Auto-generated method stub

	}

	public void setIntProperty(String arg0, int arg1) throws JMSException {
		// TODO Auto-generated method stub

	}

	public void setJMSCorrelationID(String arg0) throws JMSException {
		// TODO Auto-generated method stub

	}

	public void setJMSCorrelationIDAsBytes(byte[] arg0) throws JMSException {
		// TODO Auto-generated method stub

	}

	public void setJMSDeliveryMode(int arg0) throws JMSException {
		// TODO Auto-generated method stub

	}

	public void setJMSDestination(Destination arg0) throws JMSException {
		// TODO Auto-generated method stub

	}

	public void setJMSExpiration(long arg0) throws JMSException {
		// TODO Auto-generated method stub

	}

	public void setJMSMessageID(String arg0) throws JMSException {
		// TODO Auto-generated method stub

	}

	public void setJMSPriority(int arg0) throws JMSException {
		// TODO Auto-generated method stub

	}

	public void setJMSRedelivered(boolean arg0) throws JMSException {
		// TODO Auto-generated method stub

	}

	public void setJMSReplyTo(Destination arg0) throws JMSException {
		// TODO Auto-generated method stub

	}

	public void setJMSTimestamp(long arg0) throws JMSException {
		// TODO Auto-generated method stub

	}

	public void setJMSType(String arg0) throws JMSException {
		// TODO Auto-generated method stub

	}

	public void setLongProperty(String arg0, long arg1) throws JMSException {
		// TODO Auto-generated method stub

	}

	public void setObjectProperty(String arg0, Object arg1) throws JMSException {
		// TODO Auto-generated method stub

	}

	public void setShortProperty(String arg0, short arg1) throws JMSException {
		// TODO Auto-generated method stub

	}

	public void setStringProperty(String arg0, String arg1) throws JMSException {
		// TODO Auto-generated method stub

	}

	public String getText() throws JMSException {
		return generateString(20);
	}

	public void setText(String arg0) throws JMSException {
		// TODO Auto-generated method stub

	}

	public static String generateString(int length) {
		String data = "";
		for (int i = 0; i <= length; i++) {
			data = data + (char) (r.nextInt(25) + 97);
		}
		return data;
	}

	public static Message getMessage(int priority) {
		return new Message(priority);
	}
}
