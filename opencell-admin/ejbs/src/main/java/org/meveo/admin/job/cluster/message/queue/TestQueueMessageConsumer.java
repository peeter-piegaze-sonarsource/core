package org.meveo.admin.job.cluster.message.queue;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion version
 */
//@MessageDriven(name = "java:/queue/MEDIATIONJOBQUEUE", activationConfig = {
//		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/MEDIATIONJOBQUEUE"),
//		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
//		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue") })
public class TestQueueMessageConsumer  { //implements MessageListener {

//	@Inject
//	private Logger log;
//
//	@Override
//	public void onMessage(Message message) {
//		try {
//			log.debug("message receive {}", ((ObjectMessage) message).getObject());
//		} catch (JMSException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

}
