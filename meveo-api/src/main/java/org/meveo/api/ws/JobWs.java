package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.job.TimerEntityDto;
import org.meveo.model.jobs.TimerInfoDto;

@WebService
public interface JobWs extends IBaseWs {

	@WebMethod
	ActionStatus executeTimer(@WebParam(name = "timer") TimerInfoDto postData);
	
	@WebMethod
	public ActionStatus create(@WebParam(name = "timer") TimerEntityDto postData);

}
