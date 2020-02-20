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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import org.infinispan.Cache;
import org.infinispan.context.Flag;
import org.meveo.admin.exception.ElementNotFoundException;
import org.meveo.admin.exception.InvalidScriptException;
import org.meveo.cache.CacheKeyStr;
import org.meveo.cache.CompiledScript;
import org.meveo.commons.compilation.CompilationException;
import org.meveo.commons.compilation.JavaSourceCompiler;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.scripts.ScriptInstanceError;
import org.meveo.model.scripts.ScriptSourceTypeEnum;
import org.meveo.service.base.BusinessService;

/**
 * Compiles scripts and provides compiled script classes.
 * 
 * NOTE: Compilation methods are executed synchronously due to WRITE lock. DO NOT CHANGE IT, so there would be only one attempt to compile a new script class
 * 
 * @author Andrius Karpavicius
 * @lastModifiedVersion 7.2.0
 *
 */
@Singleton
@Lock(LockType.WRITE)
public class ScriptCompilerService extends BusinessService<ScriptInstance> {

    /**
     * Stores compiled scripts. Key format: &lt;cluster node code&gt;_&lt;scriptInstance code&gt;. Value is a compiled script class and class instance
     */
    @Resource(lookup = "java:jboss/infinispan/cache/opencell/opencell-script-cache")
    private Cache<CacheKeyStr, CompiledScript> compiledScripts;
    
    @Inject
    private JavaSourceCompiler sourceCompiler;

    /**
     * Compile all scriptInstances.
     */
    public void compileAll() {
        List<ScriptInstance> scriptInstances = findByType(ScriptSourceTypeEnum.JAVA);
        compile(scriptInstances);
    }

    /**
     * Get all script interfaces with compiling those that are not compiled yet
     * 
     * @return the allScriptInterfaces
     */
    public List<Class<ScriptInterface>> getAllScriptInterfacesWCompile() {

        List<Class<ScriptInterface>> scriptInterfaces = new ArrayList<>();

        List<ScriptInstance> scriptInstances = findByType(ScriptSourceTypeEnum.JAVA);
        for (ScriptInstance scriptInstance : scriptInstances) {
            if (!scriptInstance.isError()) {
                try {
                    scriptInterfaces.add(getScriptInterfaceWCompile(scriptInstance.getCode()));
                } catch (ElementNotFoundException | InvalidScriptException e) {
                    // Ignore errors here as they were logged in a call before
                }
            }
        }

        return scriptInterfaces;
    }

    /**
     * Find scripts by source type.
     * 
     * @param type script source type
     * @return list of scripts
     */
    @SuppressWarnings("unchecked")
    public List<ScriptInstance> findByType(ScriptSourceTypeEnum type) {
        List<ScriptInstance> result = new ArrayList<ScriptInstance>();
        try {
            result = (List<ScriptInstance>) getEntityManager().createNamedQuery("CustomScript.getScriptInstanceByTypeActive")
                                                              .setParameter("sourceTypeEnum", type)
                                                              .getResultList();
        } catch (NoResultException e) {

        }
        return result;
    }

    /**
     * Build the classpath and compile all scripts.
     * 
     * @param scripts list of scripts
     */
    protected void compile(List<ScriptInstance> scripts) {
        try {

            for (ScriptInstance script : scripts) {
                compileScript(script, false);
            }
        } catch (Exception e) {
            log.error("", e);
        }
    }

    /*
     * Compile a script
     */
    public void refreshCompiledScript(String scriptCode) {

        ScriptInstance script = findByCode(scriptCode);
        if (script == null) {
            clearCompiledScripts(scriptCode);
        } else {
            compileScript(script, false);
        }
    }

    /**
     * Compile script, a and update script entity status with compilation errors. Successfully compiled script is added to a compiled script cache if active and not in test
     * compilation mode. Script.init() method will be called during script instantiation (for cache) if script is marked as reusable.
     * 
     * @param script Script entity to compile
     * @param testCompile Is it a compilation for testing purpose. Won't clear nor overwrite existing compiled script cache.
     */
    public void compileScript(ScriptInstance script, boolean testCompile) {

        List<ScriptInstanceError> scriptErrors = compileScript(script.getCode(), script.getSourceTypeEnum(), 
                                                               script.getScript(), script.isActive(), 
                                                               script.isReuse(), testCompile);

        script.setError(scriptErrors != null && !scriptErrors.isEmpty());
        script.setScriptErrors(scriptErrors);
    }

    /**
     * Compile script. DOES NOT update script entity status. 
     * Successfully compiled script will be instantiated and added to a compiled script cache. Optionally Script.init() method
     * is called during script instantiation if requested so.
     * 
     * Script is not cached if disabled or in test compilation mode.
     * 
     * @param scriptCode Script entity code
     * @param sourceType Source code language type
     * @param sourceCode Source code
     * @param isActive Is script active. It will compile it anyway. Will clear but not overwrite existing compiled script cache.
     * @param initialize Should script be initialized when instantiating
     * @param testCompile Is it a compilation for testing purpose. Won't clear nor overwrite existing compiled script cache.
     * 
     * @return A list of compilation errors if not compiled
     */
    private List<ScriptInstanceError> compileScript(String scriptCode, ScriptSourceTypeEnum sourceType, String sourceCode,
                                                    boolean isActive, boolean initialize, boolean testCompile) {

        try {
            if (!testCompile) {
                clearCompiledScripts(scriptCode);
            }

            log.debug("Compile Script [{}]", scriptCode);


            Class<ScriptInterface> compiledScript = compileJavaSource(sourceCode);
            
            log.debug("Congratulation script [{}] is compiled !!!!!", scriptCode);

            if (!testCompile && isActive) {
                CacheKeyStr cacheKey = new CacheKeyStr(currentUser.getProviderCode(), EjbUtils.getCurrentClusterNode() + "_" + scriptCode);

                ScriptInterface scriptInstance = compiledScript.newInstance();
                
                log.debug("Congratulation Script [{}] instancied", scriptCode);
                
                if (initialize) {
                    try {
                        scriptInstance.init(null);
                    } catch (Exception e) {
                        log.warn("Failed to initialize script for a cached script instance", e);
                    }
                }

                compiledScripts.put(cacheKey, new CompiledScript(compiledScript, scriptInstance));
            }

            return null;

        } catch (CompilationException e) {
            log.error("Failed to compile script {}. Compilation errors:", scriptCode);

            List<ScriptInstanceError> scriptErrors = new ArrayList<>();

            List<Diagnostic<? extends JavaFileObject>> diagnosticList = e.getDiagnostic().getDiagnostics();

            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnosticList) {
                if ("ERROR".equals(diagnostic.getKind().name())) {
                    ScriptInstanceError scriptInstanceError = new ScriptInstanceError();
                    scriptInstanceError.setMessage(diagnostic.getMessage(Locale.getDefault()));
                    scriptInstanceError.setLineNumber(diagnostic.getLineNumber());
                    scriptInstanceError.setColumnNumber(diagnostic.getColumnNumber());
                    scriptInstanceError.setSourceFile(diagnostic.getSource().toString());
                    scriptErrors.add(scriptInstanceError);
                    log.warn("{} script {} location {}:{}: {}", diagnostic.getKind().name(), 
                             scriptCode, diagnostic.getLineNumber(), diagnostic.getColumnNumber(),
                             diagnostic.getMessage(Locale.getDefault()));
                }
            }

            return scriptErrors;

        } catch (Exception e) {
            log.error("Failed while compiling script", e);
            List<ScriptInstanceError> scriptErrors = new ArrayList<>();
            ScriptInstanceError scriptInstanceError = new ScriptInstanceError();
            scriptInstanceError.setMessage(e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName());
            scriptErrors.add(scriptInstanceError);
            return scriptErrors;
        }
    }

    /**
     * Compile java Source script
     * 
     * @param javaSrc Java source to compile
     * @return Compiled class instance
     * @throws CharSequenceCompilerException char sequence compiler exception.
     */
    public Class<ScriptInterface> compileJavaSource(String javaSrc) throws CompilationException {
        return sourceCompiler.compile(javaSrc);
    }


    /**
     * Compile the script class for a given script code if it is not compile yet.
     * 
     * @param scriptCode Script code
     * @return Script interface Class
     * @throws InvalidScriptException Were not able to instantiate or compile a script
     * @throws ElementNotFoundException Script not found
     */
    public Class<ScriptInterface> getScriptInterfaceWCompile(String scriptCode) throws ElementNotFoundException, InvalidScriptException {

        CompiledScript compiledScript = getOrCompileScript(scriptCode);

        return compiledScript.getScriptClass();
    }

    /**
     * Compile the script class for a given script code if it is not compile yet and return its instance. NOTE: 
     * Will return the SAME (cached) script class instance for subsequent
     * calls. If you need a new instance of a class, use getScriptInterfaceWCompile() and instantiate class yourself.
     * 
     * @param scriptCode Script code
     * @return Script instance
     * @throws InvalidScriptException Were not able to instantiate or compile a script
     * @throws ElementNotFoundException Script not found
     */
    public ScriptInterface getScriptInstanceWCompile(String scriptCode) throws ElementNotFoundException, InvalidScriptException {
        CompiledScript compiledScript = getOrCompileScript(scriptCode);
        return compiledScript.getScriptInstance();
    }

    /**
     * Compile the script class for a given script code if it is not compile yet.
     * 
     * @param scriptCode Script code
     * @return Script instance
     * @throws InvalidScriptException Were not able to instantiate or compile a script
     * @throws ElementNotFoundException Script not found
     */
    private CompiledScript getOrCompileScript(String scriptCode) throws ElementNotFoundException, InvalidScriptException {
        CacheKeyStr cacheKey = new CacheKeyStr(currentUser.getProviderCode(), EjbUtils.getCurrentClusterNode() + "_" + scriptCode);

        CompiledScript compiledScript = compiledScripts.get(cacheKey);
        if (compiledScript == null) {

            ScriptInstance script = findByCode(scriptCode);
            if (script == null) {
                log.debug("ScriptInstance with {} does not exist", scriptCode);
                throw new ElementNotFoundException(scriptCode, "ScriptInstance");
            } else if (script.isError()) {
                log.debug("ScriptInstance {} failed to compile. Errors: {}", scriptCode, script.getScriptErrors());
                throw new InvalidScriptException(scriptCode, getEntityClass().getName());
            }
            compileScript(script, false);

            compiledScript = compiledScripts.get(cacheKey);
        }

        if (compiledScript == null) {
            log.debug("ScriptInstance with {} does not exist", scriptCode);
            throw new ElementNotFoundException(scriptCode, "ScriptInstance");
        }

        log.debug("ScriptInstance with {} found and instanciated", scriptCode);
        return compiledScript;
    }

    /**
     * Remove compiled script, its logs and cached instances for given script code
     * 
     * @param scriptCode Script code
     */
    public void clearCompiledScripts(String scriptCode) {
        compiledScripts.remove(new CacheKeyStr(currentUser.getProviderCode(), EjbUtils.getCurrentClusterNode() + "_" + scriptCode));
    }

    /**
     * Remove all compiled scripts for a current provider
     */
    public void clearCompiledScripts() {

        String currentProvider = currentUser.getProviderCode();
        log.info("Clear CFTS cache for {}/{} ", currentProvider, currentUser);
        // cftsByAppliesTo.keySet().removeIf(key -> (key.getProvider() == null) ? currentProvider == null : key.getProvider().equals(currentProvider));
        Iterator<Entry<CacheKeyStr, CompiledScript>> iter = compiledScripts.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).entrySet().iterator();
        ArrayList<CacheKeyStr> itemsToBeRemoved = new ArrayList<>();
        while (iter.hasNext()) {
            Entry<CacheKeyStr, CompiledScript> entry = iter.next();
            boolean comparison = (entry.getKey().getProvider() == null) ? currentProvider == null : entry.getKey().getProvider().equals(currentProvider);
            if (comparison) {
                itemsToBeRemoved.add(entry.getKey());
            }
        }

        for (CacheKeyStr elem : itemsToBeRemoved) {
            log.debug("Remove element Provider:" + elem.getProvider() + " Key:" + elem.getKey() + ".");
            compiledScripts.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).remove(elem);
        }
    }
    
}
