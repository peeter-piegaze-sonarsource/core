package org.meveo.jms;

import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ElementNotFoundException;
import org.meveo.admin.exception.InvalidScriptException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptInterface;

/**
 * Execute script
 * 
 * @author Axione
 *
 */
@Stateless
@Named
public class JmsMessageScriptListenerExecutor {
    private static final String JMS_USER = ParamBean.getInstance().getProperty("jms.user", "opencell.admin");
    
    @Inject
    private ScriptInstanceService scriptInstanceService;
    
    @Inject
    private CurrentUserProvider currentUserProvider;
    
    
    /**
     * Authentify the executor
     */
    private void authenticate() {
        currentUserProvider.forceAuthentication(JMS_USER, "");
    }
    
    /**
     * Create a new instance of script
     * 
     * @return The new instance ofg script
     * @throws InvalidScriptException - If can't create it
     * @throws ElementNotFoundException - If bad script code
     */
    private ScriptInterface scriptInterface(String scriptCode) throws ElementNotFoundException, InvalidScriptException {
        return scriptInstanceService.getScriptInstance(scriptCode);
    }
    
    /**
     * Execute the script
     * 
     * @param scriptCode - The script code
     * @param context - The context map 
     * @throws BusinessException - If busioness exception
     * @throws InvalidScriptException  - If bad script
     * @throws ElementNotFoundException - If unknow script
     */
    public void executeScript(String scriptCode, Map<String, Object> context) throws ElementNotFoundException, InvalidScriptException, BusinessException {
        authenticate();
        scriptInterface(scriptCode).execute(context);
    }


}
