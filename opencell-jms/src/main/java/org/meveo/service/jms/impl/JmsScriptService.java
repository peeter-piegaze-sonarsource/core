package org.meveo.service.jms.impl;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.jms.JmsMessageScriptFactory;
import org.meveo.model.jms.JmsScript;
import org.meveo.service.base.BusinessService;

/**
 * The jms service script
 * 
 * @author Axione
 */
@Stateless
public class JmsScriptService extends BusinessService<JmsScript> {
    @Inject
    private JmsMessageScriptFactory scriptFactory;
    
    /**
     * Create it
     * 
     * @param jmsScript - The script
     * @throws BusinessException - If can't create or activate it
     */
    public void create(JmsScript jmsScript) throws BusinessException {
        super.create(jmsScript);
        scriptFactory.attachScript(jmsScript);
    }
    
    /**
     * Update it
     * 
     * @param jmsScript - The script
     * @throws BusinessException 
     */
    public JmsScript update(JmsScript jmsScript) throws BusinessException {
        scriptFactory.detachScript(jmsScript);
        super.update(jmsScript);
        scriptFactory.attachScript(jmsScript);
        
        return jmsScript;
    }
    
    public void remove(JmsScript jmsScript) throws BusinessException {
        super.remove(jmsScript);
        scriptFactory.detachScript(jmsScript);
    }
	
}
