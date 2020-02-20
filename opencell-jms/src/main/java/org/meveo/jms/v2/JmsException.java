package org.meveo.jms.v2;

import java.text.MessageFormat;

/**
 * A JMS Exception
 * 
 * @author Axione
 *
 */
public class JmsException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private Reason reason;

    /**
     * Reason of exception
     */
    public enum Reason {
        /**
         * Can't create a connection
         */
        CONNECTION_CREATE_ERROR("Erreur de création de la connexion JMS"),

        /**
         * Can't start a connection
         */
        CONNECTION_START_ERROR("Erreur d'ouverture de la connexion JMS"),

        /**
         * Stop a connection
         */
        CONNECTION_STOP_ERRROR("Erreur d'arrêt de la connexion JMS"),

        /**
         * Stop a connection
         */
        CONNECTION_CLOSE_ERRROR("Erreur de fermeture de la connexion JMS"),

        /**
         * Create session error;
         */
        SESSION_CREATE_ERROR("Erreur de création de la session JMS"),

        /**
         * Close session error
         */
        SESSION_CLOSE_ERROR("Erreur de fermeture de la session JMS"),

        /**
         * Create queue error
         */
        QUEUE_CREATE_ERROR("Erreur de cvréation de la queue JMS [{}]"),

        /**
         * Create consumer error
         */
        CONSUMER_CREATE_ERROR("Erreuer de création du consommateur de message sur la queue [{}]"),

        /**
         * Invalid destination type
         */
        INVALID_DESTINATION_TYPE("Destination [{}] invalide"),

        /**
         * Create Producer Error
         */
        PRODUCER_CREATE_ERROR("Erreur de création du producteur de message sur la queue [{}]]"),

        /**
         * Close Producer Error
         */
        PRODUCER_CLOSE_ERROR("Erreur de fermeture du producteur de message sur la queue [{}]]");

        /**
         * Construct it
         * 
         * @param messageFormat - The message format
         */
        Reason(String messageFormat) {
            this.messageFormat = messageFormat;
        }

        /**
         * The message
         */
        private String messageFormat;

        /**
         * Format the message
         * 
         * @param arguments - The argumenrts for the message
         * @return The message
         */
        public String message(Object... arguments) {
            return MessageFormat.format(messageFormat, arguments);
        }
    }

    /**
     * Construct it
     * 
     * @param reason - The reason
     * @param cause - The cause
     * @param arguments - TThe arguments
     */
    public JmsException(Reason reason, Exception cause, Object... arguments) {
        super(reason.message(arguments), cause);
        this.reason = reason;
    }

    /**
     * Construct it
     * 
     * @param reason - The reason
     * @param arguments - The arguments
     */
    public JmsException(Reason reason, Object... arguments) {
        super(reason.message(arguments));
        this.reason = reason;
    }

    /**
     * The reason
     * 
     * @return reason
     */
    public Reason reason() {
        return reason;
    }
}
