package org.meveo.jms.test;

import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.jms.JmsConstants;
import org.meveo.service.script.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyScript extends Script {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(MyScript.class);
	
	private String lastContent;
	
	@Override
    public void execute(Map<String, Object> methodContext) throws BusinessException {
      LOGGER.info("My Script with -> {}", methodContext);
      lastContent = (String) methodContext.get(JmsConstants.CONTENT_KEY);
    }

	public String getLastContent() {
		return lastContent;
	}
	
	

}
