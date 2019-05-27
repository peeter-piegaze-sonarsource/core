package org.meveo.admin.job.cluster.message.topic;

import java.io.Serializable;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSDestinationDefinition;
import javax.jms.JMSDestinationDefinitions;
import javax.jms.Topic;

import org.meveo.admin.job.cluster.ClusterJobTopicDto;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 7.0
 */
@JMSDestinationDefinitions(value = {
		@JMSDestinationDefinition(name = "java:/topic/CLUSTERJOBTOPIC", interfaceName = "javax.jms.Topic", destinationName = "ClusterJobTopic") })
@Stateless
public class ClusterJobTopicPublisher implements Serializable {

	private static final long serialVersionUID = -3072345786391435959L;

	@Inject
	private Logger log;

	@Inject
	private JMSContext context;

	@Inject
	@CurrentUser
	protected MeveoUser currentUser;

	@Resource(lookup = "java:/topic/CLUSTERJOBTOPIC")
	private Topic topic;

	public void publishMessage(ClusterJobTopicDto messageDto) {

		if (!EjbUtils.isRunningInClusterMode()) {
			return;
		}

		try {
			messageDto.setSourceNode(EjbUtils.getCurrentClusterNode());
			log.trace("Publishing job topic between cluster nodes {}", messageDto);

			context.createProducer().send(topic, messageDto);

		} catch (Exception e) {
			log.error("Failed to publish job topic between cluster nodes {}", e.getMessage());
		}
	}
}
