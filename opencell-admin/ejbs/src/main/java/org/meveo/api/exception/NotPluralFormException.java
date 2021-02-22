package org.meveo.api.exception;

import org.meveo.api.MeveoApiErrorCodeEnum;

/**
 * @author Thang Nguyen
 **/
public class NotPluralFormException extends MeveoApiException {

    private static final long serialVersionUID = 4814463369593237028L;

    public NotPluralFormException(String message) {
        super(message);
        setErrorCode(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
    }

    /**
     * Stacktrace is not of interest here
     */
    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }
}
