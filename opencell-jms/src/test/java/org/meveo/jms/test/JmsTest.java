package org.meveo.jms.test;

import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.meveo.admin.exception.ElementNotFoundException;
import org.meveo.admin.exception.InvalidScriptException;
import org.meveo.api.jms.JmsScriptApi;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.jms.JmsClientFactory;
import org.meveo.jms.JmsConnectionFactoryBean;
import org.meveo.jms.JmsMessageScriptFactory;
import org.meveo.jms.JmsMessageScriptListenerExecutor;
import org.meveo.jms.JmsObjectMapperBean;
import org.meveo.model.jms.JmsScript;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.jms.impl.JmsScriptService;
import org.meveo.service.script.ScriptInstanceService;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * The JMS Test
 * 
 * @author Axione
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest( {PersistenceService.class,
	              ScriptInstanceService.class, 
	              JmsScriptService.class,
	              JmsMessageScriptListenerExecutor.class,
	              CurrentUserProvider.class,
	              EjbUtils.class})
public abstract class JmsTest {
	protected static JmsConnectionFactoryBean jmsConnectionFactory = new JmsConnectionFactoryBean();
	protected static JmsObjectMapperBean jmsObjectMapperBean = new JmsObjectMapperBean();
	protected static JmsClientFactory jmsClientFactory = new JmsClientFactory();
	protected static ScriptInstanceService scriptInstanceService = PowerMockito.mock(ScriptInstanceService.class);
	protected static JmsScriptService jmsScriptService = PowerMockito.mock(JmsScriptService.class);
	protected static CurrentUserProvider currentUserProvider = PowerMockito.mock(CurrentUserProvider.class);
	protected static JmsMessageScriptListenerExecutor jmsScriptExecutor = new JmsMessageScriptListenerExecutor();
	
	protected static JmsScriptApi jmsScriptApi;
	protected static JmsMessageScriptFactory jmsMessageScriptFactory = new JmsMessageScriptFactory();
	protected static MyScript myScript = new MyScript();
	
	private static void configureClientFactories() {
		jmsConnectionFactory.startup();
		jmsObjectMapperBean.configure();
		ReflectionTestUtils.setField(jmsClientFactory, "connectionFactory", jmsConnectionFactory);
		ReflectionTestUtils.setField(jmsClientFactory, "objectMapperBean", jmsObjectMapperBean);
		jmsClientFactory.startup();
	}
	
	private static JmsScript createJmsScript() {
		String queueName = "my-queue";
		String scriptCode = MyScript.class.getName();
		String code = queueName + '#' + scriptCode;
		
		JmsScript jmsScript = new JmsScript();
		jmsScript.setQueueName(queueName);
		jmsScript.setScriptCode(scriptCode);
		jmsScript.setCode(code);
		jmsScript.setSchema("application/vnd.pebroc.event.osen.v0+json");
		return jmsScript;
	}
	
	private static void configureServices() throws ElementNotFoundException, InvalidScriptException {
		PowerMockito.mockStatic(EjbUtils.class);
		Mockito.when(scriptInstanceService.getScriptInstance(MyScript.class.getName())).thenReturn(myScript);
		
		JmsScript jmsScript = createJmsScript();
		Mockito.when(jmsScriptService.findByCode(jmsScript.getCode())).thenReturn(jmsScript);
		Mockito.when(jmsScriptService.list()).thenReturn(Arrays.asList(jmsScript));
		
		Mockito.when(EjbUtils.getServiceInterface("JmsScriptService")).thenReturn(jmsScriptService);
		
		
		jmsScriptApi = new JmsScriptApi();
		ReflectionTestUtils.setField(jmsScriptApi, "jmsScriptService", jmsScriptService);
		
	}
	
	private static void configureJmsMessageScriptExecutor() {
	    ReflectionTestUtils.setField(jmsScriptExecutor, "scriptInstanceService", scriptInstanceService);
	    ReflectionTestUtils.setField(jmsScriptExecutor, "currentUserProvider", currentUserProvider);
	}
	
	private static void configureMessageScriptFactory() {
		ReflectionTestUtils.setField(jmsMessageScriptFactory, "jmsClientFactory", jmsClientFactory);
		ReflectionTestUtils.setField(jmsMessageScriptFactory, "objectMapperBean", jmsObjectMapperBean);
		ReflectionTestUtils.setField(jmsMessageScriptFactory, "jmsScriptService", jmsScriptService);
		ReflectionTestUtils.setField(jmsMessageScriptFactory, "jmsScriptExecutor", jmsScriptExecutor);
		
		jmsMessageScriptFactory.configure();
	}
	
    @BeforeClass
    public static void setup() throws Exception {
    	configureClientFactories();
    	configureServices();
    	configureJmsMessageScriptExecutor();
    	configureMessageScriptFactory();
    }
}