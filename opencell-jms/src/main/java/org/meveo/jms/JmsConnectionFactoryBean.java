package org.meveo.jms;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.jms.Connection;
import javax.jms.JMSException;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.lang3.StringUtils;
import org.meveo.commons.utils.ParamBean;
import org.springframework.util.Assert;

/**
 * The JMS connection factory bean
 * 
 * @author Axione
 *
 */
@Stateless
@Named
public class JmsConnectionFactoryBean {
	private ActiveMQConnectionFactory jmsConnectionFactory;
	
	private static final String ACTIVEMQ_PROPERTY = "activemq";
	private static final String URL_PROPERTY = ACTIVEMQ_PROPERTY + ".url";
	private static final String LOGIN_PROPERTY = ACTIVEMQ_PROPERTY + ".login";
	private static final String PASSWORD_PROPERTY = ACTIVEMQ_PROPERTY + ".password";
	
	private String url = ParamBean.getInstance().getProperty(URL_PROPERTY, "");
	private String login = ParamBean.getInstance().getProperty(LOGIN_PROPERTY, "");
	private String password = ParamBean.getInstance().getProperty(PASSWORD_PROPERTY, "");
	
	@PostConstruct
	public void startup() {
		Assert.hasText(url, "ActiveMQ URL is null or empty !!!");
		jmsConnectionFactory = new ActiveMQConnectionFactory(url);
		
		if (StringUtils.isNotBlank(login)) {
			jmsConnectionFactory.setUserName(login);
			jmsConnectionFactory.setPassword(password);
		}
	}
	
	/**
	 * Create a new connection
	 * 
	 * @return a new connection
	 * @throws - If can't create jms connection
	 */
	public Connection createConnection() throws  JMSException {
		return jmsConnectionFactory.createConnection();
	}

}
