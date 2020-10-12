package org.meveo.api.exception;

import org.meveo.api.MeveoApiErrorCodeEnum;

public class IncompatibleServiceException extends MeveoApiException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8652809410295678097L;
	public static final String SERVICE_INCOMPATIBLE = "Service Incompatible ";
	
	public IncompatibleServiceException(String services) {
		super(SERVICE_INCOMPATIBLE + services);
		setErrorCode(MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION);
	}
	
	public IncompatibleServiceException(Throwable e) {
		super(e);
		setErrorCode(MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION);
	}
}
