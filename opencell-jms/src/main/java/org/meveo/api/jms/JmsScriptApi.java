package org.meveo.api.jms;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.jms.JmsScriptDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.jms.JmsScript;
import org.meveo.service.jms.impl.JmsScriptService;

/**
 * The jms script api
 * 
 * @author Axione
 *
 */
@Stateless
public class JmsScriptApi extends BaseCrudApi<JmsScript, JmsScriptDto> {
	
	@Inject
	private JmsScriptService jmsScriptService;

	@Override
	public JmsScriptDto find(String code) throws MeveoApiException {
		if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
            handleMissingParameters();
        }
		
		JmsScript jmsScript = jmsScriptService.findByCode(code);
		
		if (jmsScript == null) {
            throw new EntityDoesNotExistsException(JmsScript.class, code);
        }
				
		return new JmsScriptDto(jmsScript);
	}

	@Override
	public JmsScript create(JmsScriptDto dtoData) throws MeveoApiException, BusinessException {
		validate(dtoData);
		JmsScript jmsScript = jmsScriptFromDTO(dtoData);
		jmsScriptService.create(jmsScript);
		
		return jmsScript;
	}

	@Override
	public JmsScript update(JmsScriptDto dtoData) throws MeveoApiException, BusinessException {
		validate(dtoData);
		JmsScript jmsScript = jmsScriptService.findByCode(dtoData.getCode());
		
		if (jmsScript == null) {
            throw new EntityDoesNotExistsException(JmsScript.class, dtoData.getCode());
        }
		
		JmsScript jmsScriptUpdate = jmsScriptFromDTO(dtoData);
		
        jmsScriptUpdate = jmsScriptService.update(jmsScriptUpdate);
        
        return jmsScriptUpdate;
	}
	
	@Override
	public void remove(String code) throws MissingParameterException, EntityDoesNotExistsException, BusinessException {
		JmsScript jmsScript = jmsScriptService.findByCode(code);
		
		if (jmsScript == null) {
            throw new EntityDoesNotExistsException(JmsScript.class, code);
        }
		jmsScriptService.remove(jmsScript);
		
	}
	
	/**
	 * List all jms script
	 *  
	 * @return jms script list
	 */
	public List<JmsScriptDto> list() {
	    return jmsScriptService.list()
	                           .stream()
	                           .map(JmsScriptDto::new)
	                           .collect(Collectors.toList());
	}
	
	/**
	 * Validate and construct JMS
	 * 
	 * @param dto - The dto
	 * @return the script
	 * @throws MeveoApiException 
	 */
	private JmsScript jmsScriptFromDTO(JmsScriptDto dto) throws MeveoApiException {
		validate(dto);
		JmsScript jmsScript = new JmsScript();
		jmsScript.setQueueName(dto.getQueueName());
		jmsScript.setScriptCode(dto.getScriptCode());
		jmsScript.setSchema(dto.getSchema());
		jmsScript.setCode(dto.getQueueName() + '#' + dto.getScriptCode());

		return jmsScript;
	}
	
}
