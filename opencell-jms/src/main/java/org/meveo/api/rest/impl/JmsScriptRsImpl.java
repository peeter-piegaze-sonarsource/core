package org.meveo.api.rest.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.jms.JmsScriptDto;
import org.meveo.api.dto.jms.JmsScriptListResponseDto;
import org.meveo.api.dto.jms.JmsScriptResponseDto;
import org.meveo.api.jms.JmsScriptApi;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.JmsScriptRs;

/**
 * Implementation of rest service for jms script
 *  
 * @author Axione
 */
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class JmsScriptRsImpl extends BaseRs implements JmsScriptRs {
	
	@Inject
	private JmsScriptApi jmsScriptApi;
	

	@Override
	public JmsScriptResponseDto create(JmsScriptDto postData) {
		JmsScriptResponseDto response = new JmsScriptResponseDto();
		ActionStatus actionStatus = new ActionStatus(ActionStatusEnum.SUCCESS, "jms script created");
		try {
		  	response.setJmsScriptDto(new JmsScriptDto(jmsScriptApi.create(postData)));
		}
		catch (Exception e) {
			processException(e, new ActionStatus());
		}
		
		response.setActionStatus(actionStatus);
		
		return response;
	}

	@Override
	public JmsScriptResponseDto update(JmsScriptDto postData) {
		JmsScriptResponseDto response = new JmsScriptResponseDto();
		ActionStatus actionStatus = new ActionStatus(ActionStatusEnum.SUCCESS, "jms script updated");

		try {
		  	response.setJmsScriptDto(new JmsScriptDto(jmsScriptApi.update(postData)));
		}
		catch (Exception e) {
			processException(e, new ActionStatus());
		}
		
		response.setActionStatus(actionStatus);
		
		return response;

	}

	@Override
	public ActionStatus remove(String jmsScriptCode) {
		ActionStatus actionStatus = new ActionStatus(ActionStatusEnum.SUCCESS, "jms script removed");
		
		try {
			jmsScriptApi.remove(jmsScriptCode);
		}
		catch (Exception e) {
			processException(e, actionStatus);
		}
		
		return actionStatus;
	}

	@Override
	public JmsScriptResponseDto find(String jmsScriptCode) {
        ActionStatus actionStatus = new ActionStatus(ActionStatusEnum.SUCCESS, "jms script find");
        JmsScriptResponseDto response = new JmsScriptResponseDto();
		
		try {
			response.setJmsScriptDto(jmsScriptApi.find(jmsScriptCode));
		}
		catch (Exception e) {
			processException(e, actionStatus);
		}
		
		response.setActionStatus(actionStatus);
		
		return response;
	}

	@Override
	public JmsScriptResponseDto createOrUpdate(JmsScriptDto postData) {
		ActionStatus actionStatus = new ActionStatus(ActionStatusEnum.SUCCESS, "jms script created or updated");
		JmsScriptResponseDto response = new JmsScriptResponseDto();
		
		try {
			response.setJmsScriptDto(new JmsScriptDto(jmsScriptApi.createOrUpdate(postData)));
		}
		catch (Exception e) {
			processException(e, actionStatus);
		}
		
		response.setActionStatus(actionStatus);
        
		return response;
	}

	@Override
	public ActionStatus enable(String jmsScriptCode) {
		ActionStatus actionStatus = new ActionStatus(ActionStatusEnum.SUCCESS, "jms script enabled");
		
		try {
			jmsScriptApi.enableOrDisable(jmsScriptCode, true);
		}
		catch (Exception e) {
			processException(e, actionStatus);
		}
		return actionStatus;
	}

	@Override
	public ActionStatus disable(String code) {
		ActionStatus actionStatus = new ActionStatus(ActionStatusEnum.SUCCESS, "jms script disabled");
		
		try {
			jmsScriptApi.enableOrDisable(code, false);
		}
		catch (Exception e) {
			processException(e, actionStatus);
		}

		return actionStatus;
	}

    @Override
    public JmsScriptListResponseDto list() {
        JmsScriptListResponseDto response = new JmsScriptListResponseDto();
        ActionStatus actionStatus = new ActionStatus(ActionStatusEnum.SUCCESS, "jms list");
        
        try {
            response.setJmsScripts(jmsScriptApi.list());
        }
        catch (Exception e) {
            processException(e, actionStatus);
        }
        
        response.setActionStatus(actionStatus);
        
        return response;
        
    }

}
