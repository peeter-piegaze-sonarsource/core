/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.script;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.infinispan.Cache;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ElementNotFoundException;
import org.meveo.admin.exception.InvalidPermissionException;
import org.meveo.admin.exception.InvalidScriptException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.cache.CacheKeyStr;
import org.meveo.cache.CompiledScript;
import org.meveo.commons.compilation.CompilationException;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.event.monitoring.ClusterEventDto.CrudActionEnum;
import org.meveo.event.monitoring.ClusterEventPublisher;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.scripts.ScriptInstanceError;
import org.meveo.model.security.Role;
import org.meveo.service.base.BusinessService;

/**
 * Script service implementation.
 * 
 * @author Andrius Karpavicius
 * @lastModifiedVersion 7.2.0
 *
 */
@Stateless
public class ScriptInstanceService extends BusinessService<ScriptInstance> {

    @Inject
    private ResourceBundle resourceMessages;

    @Inject
    private ClusterEventPublisher clusterEventPublisher;

    @Inject
    private ScriptCompilerService scriptCompilerService;

    /**
     * Stores compiled scripts. Key format: &lt;cluster node code&gt;_&lt;scriptInstance code&gt;. Value is a compiled script class and class instance
     */
    @Resource(lookup = "java:jboss/infinispan/cache/opencell/opencell-script-cache")
    private Cache<CacheKeyStr, CompiledScript> compiledScripts;

    /**
     * Get all ScriptInstances with error.
     *
     * @return list of custom script.
     */
    public List<ScriptInstance> getScriptInstancesWithError() {
        return ((List<ScriptInstance>) getEntityManager().createNamedQuery("CustomScript.getScriptInstanceOnError", ScriptInstance.class)
                                                         .setParameter("isError", Boolean.TRUE)
                                                         .getResultList());
    }

    /**
     * Count scriptInstances with error.
     * 
     * @return number of script instances with error.
     */
    public long countScriptInstancesWithError() {
        return ((Long) getEntityManager().createNamedQuery("CustomScript.countScriptInstanceOnError", Long.class)
                                         .setParameter("isError", Boolean.TRUE).getSingleResult());
    }

    /**
     * Only users having a role in executionRoles can execute the script, not having the role should throw an InvalidPermission exception that extends
     * businessException. A script with no executionRoles can be executed by any user.
     *
     * @param scriptInstance instance of script
     * @throws InvalidPermissionException invalid permission exception.
     */
    public void isUserHasExecutionRole(ScriptInstance scriptInstance) throws InvalidPermissionException {
        if (scriptInstance != null && scriptInstance.getExecutionRoles() != null && !scriptInstance.getExecutionRoles().isEmpty()) {
            Set<Role> execRoles = scriptInstance.getExecutionRoles();
            for (Role role : execRoles) {
                if (currentUser.hasRole(role.getName())) {
                    return;
                }
            }
            throw new InvalidPermissionException();
        }
    }

    /**
     * @param scriptInstance instance of script
     * @return true if user have the souring role.
     */
    public boolean isUserHasSourcingRole(ScriptInstance scriptInstance) {
        if (scriptInstance != null && scriptInstance.getSourcingRoles() != null && !scriptInstance.getSourcingRoles().isEmpty()) {
            Set<Role> sourcingRoles = scriptInstance.getSourcingRoles();
            for (Role role : sourcingRoles) {
                if (currentUser.hasRole(role.getName())) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    /**
     * This is used to invoke a method in a new transaction from a script.<br>
     * This will prevent DB errors in the script from affecting notification history creation.
     *
     * @param workerName The name of the API or service that will be invoked.
     * @param methodName The name of the method that will be invoked.
     * @param parameters The array of parameters accepted by the method. They must be specified in exactly the same order as the target method.
     * @throws BusinessException business exception.
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void callWithNewTransaction(String workerName, String methodName, Object... parameters) throws BusinessException {
        try {
            Object worker = EjbUtils.getServiceInterface(workerName);
            String workerClassName = ReflectionUtils.getCleanClassName(worker.getClass().getName());
            Class<?> workerClass = Class.forName(workerClassName);
            Method method = null;
            if (parameters.length < 1) {
                method = workerClass.getDeclaredMethod(methodName);
            }
            else {
                String className = null;
                Object parameter = null;
                Class<?>[] parameterTypes = new Class<?>[parameters.length];
                for (int i = 0; i < parameters.length; i++) {
                    parameter = parameters[i];
                    className = ReflectionUtils.getCleanClassName(parameter.getClass().getName());
                    parameterTypes[i] = Class.forName(className);
                }
                method = workerClass.getDeclaredMethod(methodName, parameterTypes);
            }
            method.setAccessible(true);
            method.invoke(worker, parameters);
        }
        catch (Exception e) {
            if (e.getCause() != null) {
                throw new BusinessException(e.getCause());
            }
            else {
                throw new BusinessException(e);
            }
        }
    }

    @Override
    public void create(ScriptInstance script) throws BusinessException {

        String className = ScriptUtils.getClassName(script.getScript());
        if (className == null) {
            throw new BusinessException(resourceMessages.getString("message.scriptInstance.sourceInvalid"));
        }
        String fullClassName = ScriptUtils.getFullClassname(script.getScript());

        if (ScriptUtils.isOverwritesJavaClass(fullClassName)) {
            throw new BusinessException(resourceMessages.getString("message.scriptInstance.classInvalid", fullClassName));
        }
        script.setCode(fullClassName);

        super.create(script);
        scriptCompilerService.compileScript(script, false);

        clusterEventPublisher.publishEvent(script, CrudActionEnum.create);
    }

    @Override
    public ScriptInstance update(ScriptInstance script) throws BusinessException {

        String className = ScriptUtils.getClassName(script.getScript());
        if (className == null) {
            throw new BusinessException(resourceMessages.getString("message.scriptInstance.sourceInvalid"));
        }

        String fullClassName = ScriptUtils.getFullClassname(script.getScript());
        if (ScriptUtils.isOverwritesJavaClass(fullClassName)) {
            throw new BusinessException(resourceMessages.getString("message.scriptInstance.classInvalid", fullClassName));
        }

        script.setCode(fullClassName);

        script = super.update(script);

        scriptCompilerService.compileScript(script, false);

        clusterEventPublisher.publishEvent(script, CrudActionEnum.update);
        return script;
    }

    @Override
    public void remove(ScriptInstance script) throws BusinessException {
        super.remove(script);
        clusterEventPublisher.publishEvent(script, CrudActionEnum.remove);
    }

    @Override
    public ScriptInstance enable(ScriptInstance script) throws BusinessException {
        script = super.enable(script);
        clusterEventPublisher.publishEvent(script, CrudActionEnum.enable);
        return script;
    }

    @Override
    public ScriptInstance disable(ScriptInstance script) throws BusinessException {
        script = super.disable(script);
        clusterEventPublisher.publishEvent(script, CrudActionEnum.disable);
        return script;
    }

    /**
     * Execute the script identified by a script code. No init nor finalize methods are called.
     *
     * @param scriptCode ScriptInstanceCode
     * @param context Context parameters (optional)
     * @return Context parameters. Will not be null even if "context" parameter is null.
     * @throws InvalidPermissionException Insufficient access to run the script
     * @throws ElementNotFoundException Script not found
     * @throws BusinessException General execution exception
     */
    public Map<String, Object> execute(String scriptCode, Map<String, Object> context)
            throws InvalidPermissionException, ElementNotFoundException, BusinessException {

        ScriptInstance scriptInstance = findByCode(scriptCode);
        // Check access to the script
        isUserHasExecutionRole(scriptInstance);

        log.trace("Script {} to be executed with parameters {}", scriptCode, context);

        if (context == null) {
            context = new HashMap<String, Object>();
        }
        context.put(Script.CONTEXT_ACTION, scriptCode);
        context.put(Script.CONTEXT_CURRENT_USER, currentUser);
        context.put(Script.CONTEXT_APP_PROVIDER, appProvider);

        ScriptInterface classInstance = getScriptInstance(scriptCode);
        classInstance.execute(context);

        log.trace("Script {} executed with parameters {}", scriptCode, context);
        return context;
    }

    /**
     * Execute action on an entity/event. Does not call init() nor finalize() methods of the script.
     * 
     * @param entityOrEvent Entity or event to execute action on
     * @param scriptCode Script to execute, identified by a code
     * @param encodedParameters Additional parameters encoded in URL like style param=value&amp;param=value
     * @return Context parameters. Will not be null even if "context" parameter is null.
     * @throws InvalidPermissionException Insufficient access to run the script
     * @throws ElementNotFoundException Script not found
     * @throws BusinessException Any execution exception
     */
    public Map<String, Object> execute(Object entityOrEvent, String scriptCode, String encodedParameters) throws BusinessException {
        return execute(entityOrEvent, scriptCode, ScriptUtils.parseParameters(encodedParameters));
    }

    /**
     * Execute action on an entity/event. Does not call init() nor finalize() methods of the script.
     * 
     * @param entityOrEvent Entity or event to execute action on. Will be added to context under Script.CONTEXT_ENTITY key.
     * @param scriptCode Script to execute, identified by a code. Will be added to context under Script.CONTEXT_ACTION key.
     * @param context Additional parameters
     * @return Context parameters. Will not be null even if "context" parameter is null.
     * @throws InvalidScriptException Were not able to instantiate or compile a script
     * @throws ElementNotFoundException Script not found
     * @throws InvalidPermissionException Insufficient access to run the script
     * @throws BusinessException Any execution exception
     */
    public Map<String, Object> execute(Object entityOrEvent, String scriptCode, Map<String, Object> context) throws BusinessException {

        if (context == null) {
            context = new HashMap<>();
        }
        context.put(Script.CONTEXT_ENTITY, entityOrEvent);
        Map<String, Object> result = execute(scriptCode, context);
        return result;
    }

    /**
     * Execute action on an entity/event. Reuse an existing, earlier initialized script interface. Does not call init() nor finalize() methods of the
     * script.
     * 
     * @param entityOrEvent Entity or event to execute action on. Will be added to context under Script.CONTEXT_ENTITY key.
     * @param scriptCode Script to execute, identified by a code. Will be added to context under Script.CONTEXT_ACTION key.
     * @param context Additional parameters
     * @return Context parameters. Will not be null even if "context" parameter is null.
     * @throws InvalidScriptException Were not able to instantiate or compile a script
     * @throws ElementNotFoundException Script not found
     * @throws InvalidPermissionException Insufficient access to run the script
     * @throws BusinessException Any execution exception
     */
    public Map<String, Object> executeCached(Object entityOrEvent, String scriptCode, Map<String, Object> context) throws BusinessException {

        if (context == null) {
            context = new HashMap<String, Object>();
        }
        context.put(Script.CONTEXT_ENTITY, entityOrEvent);

        return executeCached(scriptCode, context);
    }

    /**
     * Execute action on an entity/event. Reuse an existing, earlier initialized script interface. Does not call init() nor finalize() methods of the
     * script.
     * 
     * @param scriptCode Script to execute, identified by a code. Will be added to context under Script.CONTEXT_ACTION key.
     * @param context Additional parameters
     * @return Context parameters. Will not be null even if "context" parameter is null.
     * @throws InvalidScriptException Were not able to instantiate or compile a script
     * @throws ElementNotFoundException Script not found
     * @throws InvalidPermissionException Insufficient access to run the script
     * @throws BusinessException Any execution exception
     */
    public Map<String, Object> executeCached(String scriptCode, Map<String, Object> context) throws BusinessException {

        log.trace("Script (cached) {} to be executed with parameters {}", scriptCode, context);

        if (context == null) {
            context = new HashMap<String, Object>();
        }
        context.put(Script.CONTEXT_ACTION, scriptCode);
        context.put(Script.CONTEXT_CURRENT_USER, currentUser);
        context.put(Script.CONTEXT_APP_PROVIDER, appProvider);

        ScriptInterface classInstance = getCachedScriptInstance(scriptCode);
        classInstance.execute(context);

        log.trace("Script (cached) {} executed with parameters {}", scriptCode, context);
        return context;
    }

    /**
     * Execute action on an entity/event. DOES call init() and finalize() methods of the script.
     * 
     * @param entityOrEvent Entity or event to execute action on. Will be added to context under Script.CONTEXT_ENTITY key.
     * @param scriptCode Script to execute, identified by a code. Will be added to context under Script.CONTEXT_ACTION key.
     * @param context Additional parameters
     * @return Context parameters. Will not be null even if "context" parameter is null.
     * @throws InvalidScriptException Were not able to instantiate or compile a script
     * @throws ElementNotFoundException Script not found
     * @throws InvalidPermissionException Insufficient access to run the script
     * @throws BusinessException Any execution exception
     */
    public Map<String, Object> executeWInitAndFinalize(Object entityOrEvent, String scriptCode, Map<String, Object> context)
            throws BusinessException {

        if (context == null) {
            context = new HashMap<>();
        }
        context.put(Script.CONTEXT_ENTITY, entityOrEvent);
        Map<String, Object> result = executeWInitAndFinalize(scriptCode, context);
        return result;
    }

    /**
     * Execute script. DOES call init() or finalize() methods of the script.
     * 
     * @param scriptCode Script to execute, identified by a code. Will be added to context under Script.CONTEXT_ACTION key.
     * @param context Method context
     * @return Context parameters. Will not be null even if "context" parameter is null.
     * @throws InvalidScriptException Were not able to instantiate or compile a script
     * @throws ElementNotFoundException Script not found
     * @throws InvalidPermissionException Insufficient access to run the script
     * @throws BusinessException Any execution exception
     */
    public Map<String, Object> executeWInitAndFinalize(String scriptCode, Map<String, Object> context) throws BusinessException {

        log.trace("Script {} to be executed with parameters {}", scriptCode, context);

        if (context == null) {
            context = new HashMap<String, Object>();
        }
        context.put(Script.CONTEXT_ACTION, scriptCode);
        context.put(Script.CONTEXT_CURRENT_USER, currentUser);
        context.put(Script.CONTEXT_APP_PROVIDER, appProvider);

        ScriptInterface classInstance = getScriptInstance(scriptCode);
        classInstance.init(context);
        classInstance.execute(context);
        classInstance.finalize(context);

        log.trace("Script {} executed with parameters {}", scriptCode, context);
        return context;
    }

    /**
     * Execute script. DOES call init() or finalize() methods of the script.
     * 
     * @param compiledScript Compiled script class
     * @param context Method context
     * 
     * @return Context parameters. Will not be null even if "context" parameter is null.
     * @throws BusinessException Any execution exception
     */
    protected Map<String, Object> executeWInitAndFinalize(ScriptInterface compiledScript, Map<String, Object> context) throws BusinessException {

        if (context == null) {
            context = new HashMap<String, Object>();
        }
        context.put(Script.CONTEXT_CURRENT_USER, currentUser);
        context.put(Script.CONTEXT_APP_PROVIDER, appProvider);

        log.trace("Script {} to be executed with parameters {}", compiledScript.getClass(), context);

        compiledScript.init(context);
        compiledScript.execute(context);
        compiledScript.finalize(context);

        log.trace("Script {} executed with parameters {}", compiledScript.getClass(), context);
        return context;
    }

    /**
     * Wrap the logger and execute script.
     *
     * @param scriptInstance Script to test
     * @param context context used in execution of script.
     * @return Log messages
     */
    public String test(ScriptInstance scriptInstance, Map<String, Object> context) {
        try {

            isUserHasExecutionRole(scriptInstance);
            String javaSrc = scriptInstance.getScript();
            javaSrc = javaSrc.replaceAll("\\blog.", "logTest.");
            Class<ScriptInterface> compiledScript = scriptCompilerService.compileJavaSource(javaSrc);
            ScriptInterface scriptClassInstance = compiledScript.newInstance();

            executeWInitAndFinalize(scriptClassInstance, context);

            String logMessages = scriptClassInstance.getLogMessages();
            return logMessages;

        }
        catch (CompilationException e) {
            log.error("Failed to compile script {}. Compilation errors:", scriptInstance.getCode());

            List<ScriptInstanceError> scriptErrors = new ArrayList<>();

            if (e.getDiagnostic() != null) {

                for (Diagnostic<? extends JavaFileObject> diagnostic : e.getDiagnostic().getDiagnostics()) {
                    if ("ERROR".equals(diagnostic.getKind().name())) {
                        ScriptInstanceError scriptInstanceError = new ScriptInstanceError();
                        scriptInstanceError.setMessage(diagnostic.getMessage(Locale.getDefault()));
                        scriptInstanceError.setLineNumber(diagnostic.getLineNumber());
                        scriptInstanceError.setColumnNumber(diagnostic.getColumnNumber());
                        scriptInstanceError.setSourceFile(diagnostic.getSource().toString());
                        scriptErrors.add(scriptInstanceError);
                        log.warn("{} script {} location {}:{}: {}", diagnostic.getKind().name(), scriptInstance.getCode(), diagnostic.getLineNumber(),
                                diagnostic.getColumnNumber(), diagnostic.getMessage(Locale.getDefault()));
                    }
                }
              
            } else {
                 ScriptInstanceError error = new ScriptInstanceError();
                 error.setMessage(e.getMessage());
                 scriptErrors.add(error);
            }
            
            scriptInstance.setError(scriptErrors != null && !scriptErrors.isEmpty());
            scriptInstance.setScriptErrors(scriptErrors);

            return "Compilation errors";

        }
        catch (Exception e) {
            log.error("Script test failed", e);
            return ExceptionUtils.getStackTrace(e);
        }
    }

    /**
     * Compile script, a and update script entity status with compilation errors. Successfully compiled script is added to a compiled script cache if
     * active and not in test compilation mode. Pass-through to ScriptCompilerService.compileScript().
     * 
     * @param script Script entity to compile
     * @param testCompile Is it a compilation for testing purpose. Won't clear nor overwrite existing compiled script cache.
     */
    public void compileScript(ScriptInstance script, boolean testCompile) {
        scriptCompilerService.compileScript(script, testCompile);
    }

    /**
     * Find the script class for a given script code
     * 
     * @param scriptCode Script code
     * @return Script interface Class
     * @throws InvalidScriptException Were not able to instantiate or compile a script
     * @throws ElementNotFoundException Script not found
     */
    public Class<ScriptInterface> getScriptInterface(String scriptCode) throws ElementNotFoundException, InvalidScriptException {
        CacheKeyStr cacheKey = new CacheKeyStr(currentUser.getProviderCode(), EjbUtils.getCurrentClusterNode() + "_" + scriptCode);

        CompiledScript compiledScript = compiledScripts.get(cacheKey);
        if (compiledScript == null) {
            return scriptCompilerService.getScriptInterfaceWCompile(scriptCode);
        }

        return compiledScript.getScriptClass();
    }

    /**
     * Get a compiled script class
     * 
     * @param scriptCode Script code
     * @return A compiled script class
     * @throws InvalidScriptException Were not able to instantiate or compile a script
     * @throws ElementNotFoundException Script not found
     */
    public ScriptInterface getScriptInstance(String scriptCode) throws ElementNotFoundException, InvalidScriptException {
        Class<ScriptInterface> scriptClass = getScriptInterface(scriptCode);

        try {
            ScriptInterface script = scriptClass.newInstance();
            return script;

        }
        catch (InstantiationException | IllegalAccessException e) {
            log.error("Failed to instantiate script {}", scriptCode, e);
            throw new InvalidScriptException(scriptCode, getEntityClass().getName());
        }
    }

    /**
     * Get a the same/single/cached instance of compiled script class. A subsequent call to this method will retun the same instance of scipt.
     * 
     * @param scriptCode Script code
     * @return A compiled script class
     * @throws ElementNotFoundException ElementNotFoundException
     * @throws InvalidScriptException InvalidScriptException
     */
    public ScriptInterface getCachedScriptInstance(String scriptCode) throws ElementNotFoundException, InvalidScriptException {
        CacheKeyStr cacheKey = new CacheKeyStr(currentUser.getProviderCode(), EjbUtils.getCurrentClusterNode() + "_" + scriptCode);

        CompiledScript compiledScript = compiledScripts.get(cacheKey);
        if (compiledScript == null) {
            return scriptCompilerService.getScriptInstanceWCompile(scriptCode);
        }
        return compiledScript.getScriptInstance();
    }

    /**
     * Get a summary of cached information.
     * 
     * @return A list of a map containing cache information with cache name as a key and cache as a value
     */
    // @Override
    @SuppressWarnings("rawtypes")
    public Map<String, Cache> getCaches() {
        Map<String, Cache> summaryOfCaches = new HashMap<String, Cache>();
        summaryOfCaches.put(compiledScripts.getName(), compiledScripts);

        return summaryOfCaches;
    }

    /**
     * Refresh cache by name. Removes <b>current provider's</b> data from cache and populates it again
     * 
     * @param cacheName Name of cache to refresh or null to refresh all caches
     */
    // @Override
    @Asynchronous
    public void refreshCache(String cacheName) {

        if (cacheName == null || cacheName.equals(compiledScripts.getName()) || cacheName.contains(compiledScripts.getName())) {
            scriptCompilerService.clearCompiledScripts();
            scriptCompilerService.compileAll();
        }
    }

    /**
     * Populate cache by name
     * 
     * @param cacheName Name of cache to populate or null to populate all caches
     */
    // @Override
    public void populateCache(String cacheName) {

        if (cacheName == null || cacheName.equals(compiledScripts.getName())) {
            scriptCompilerService.compileAll();
        }
    }

}