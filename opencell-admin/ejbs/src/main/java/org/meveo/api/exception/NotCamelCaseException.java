package org.meveo.api.exception;

import org.meveo.api.MeveoApiErrorCodeEnum;

public class NotCamelCaseException extends MeveoApiException {

    private static final long serialVersionUID = 4814463369593237028L;

    public NotCamelCaseException(String message) {
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
