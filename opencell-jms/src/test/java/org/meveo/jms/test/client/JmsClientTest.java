package org.meveo.jms.test.client;

import java.util.concurrent.Callable;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.awaitility.Awaitility;
import org.junit.Test;
import org.meveo.jms.JmsClient;
import org.meveo.jms.JmsMessageProcessor;
import org.meveo.jms.test.JmsTest;
import org.meveo.jms.test.MyEvent;

public class JmsClientTest extends JmsTest {

	@Test
	public void testCreateClient() throws Exception {
		jmsClientFactory.createJmsClient()
		                .session()
		                .close();
	}
	
	@Test
	public void testMessageListner() {
		JmsClient sender = jmsClientFactory.createJmsClient();
		MyEvent event = new MyEvent();
		event.setFirstName("denis");
		event.setLastName("COLLET");
		
		JmsMessageProcessor jmsMessageProcessor = new JmsMessageProcessor() {
			
			@Override
			public TextMessage process(TextMessage message) throws JMSException {
				message.setStringProperty("replyTo", "reply-queue");
				message.setStringProperty("schema", "application/vnd.pebroc.event.osen.v0+json");
				return message;
			}
		};
		
		sender.convertAndSend("my-queue", event, jmsMessageProcessor);
		
		Awaitility.await().until(myScriptHasContent());
	}
	
	private Callable<Boolean> myScriptHasContent() {
		return () -> myScript.getLastContent().contains("denis");
	}

}
