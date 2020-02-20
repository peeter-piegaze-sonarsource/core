package org.meveo.jms.v2;

import javax.jms.Session;

import org.meveo.commons.utils.ParamBean;

/**
 * The final JMS Properties
 * 
 * @author Axione
 */
public final class JmsProperties {
    private static final String ACTIVEMQ_PROPERTY = "activemq";
    private static final String URL_PROPERTY = ACTIVEMQ_PROPERTY + ".url";
    private static final String LOGIN_PROPERTY = ACTIVEMQ_PROPERTY + ".login";
    private static final String PASSWORD_PROPERTY = ACTIVEMQ_PROPERTY + ".password";
    private static final String TRANSACTED_PROPERTY = ACTIVEMQ_PROPERTY + ".transacted";
    private static final String ACKNOWLEDGE_MODE_PROPERTY = ACTIVEMQ_PROPERTY + ".acknowledge-mode";
    private static final String RESPONSE_QUEUE_PROPERTY = ACTIVEMQ_PROPERTY + ".response-queue";

    private static final String DEFAULT_URL = "tcp://activemq:61616";
    private static final String DEFAULT_LOGIN = "admin";
    private static final String DEFAULT_PASSWORD = "!admin123";
    private static final int DEFAULT_ACKNOWLEDGE_MODE = Session.AUTO_ACKNOWLEDGE;
    private static final String DEFAULT_RESPONSE_QUEUE = "OPENCELL_RESPONSE_QUEUE";

    private String url;
    private String login;
    private String password;
    private int acknowledgeMode;
    private String responseQueue;
    private boolean transacted;

    /**
     * Get one property
     * 
     * @param key - The key
     * @param defaultValue - The default value
     * 
     * @return The property value
     */
    private String getPropertyOrDefault(String key, String defaultValue) {
        return ParamBean.getInstance().getProperty(key, defaultValue);
    }

    /**
     * The url
     * 
     * @return  The url
     */
    private String urlProperty() {
        return getPropertyOrDefault(URL_PROPERTY, DEFAULT_URL);
    }

    /**
     * The login property
     * 
     * @return The login property
     */
    private String loginProperty() {
        return getPropertyOrDefault(LOGIN_PROPERTY, DEFAULT_LOGIN);
    }

    /**
     * The password property
     * 
     * @return The password property
     */
    private String passwordProperty() {
        return getPropertyOrDefault(PASSWORD_PROPERTY, DEFAULT_PASSWORD);
    }

    /**
     * Acknowledge mode property
     * 
     * @return acknowledge mode
     */
    private int acknowledgeModeProperty() {
        return Integer.parseInt(getPropertyOrDefault(ACKNOWLEDGE_MODE_PROPERTY, String.valueOf(DEFAULT_ACKNOWLEDGE_MODE)));
    }

    /**
     * Transacted or not
     * 
     * @return  transacted
     */
    private boolean transactedProperty() {
        return Boolean.parseBoolean(getPropertyOrDefault(TRANSACTED_PROPERTY, Boolean.toString(false)));
    }
    /**
     * Response queue property
     * 
     * @return response queue property
     */
    private String responseQueueProperty() {
        return getPropertyOrDefault(RESPONSE_QUEUE_PROPERTY, DEFAULT_RESPONSE_QUEUE);
    }

    /**
     * Construct it
     */
    public JmsProperties() {
        url = urlProperty();
        login = loginProperty();
        password = passwordProperty();
        acknowledgeMode = acknowledgeModeProperty();
        responseQueue = responseQueueProperty();
        transacted = transactedProperty();
    }

    public String getUrl() {
        return url;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public int getAcknowledgeMode() {
        return acknowledgeMode;
    }

    public String getResponseQueue() {
        return responseQueue;
    }

    public boolean isTransacted() {
        return transacted;
    }

}
