package org.meveo.service.script.payment;

import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.sepa.DDRejectFileInfos;
import org.meveo.service.script.ScriptInterface;

/**
 * The Interface DDRequestBuilderScriptInterface.
 *
 * @author anasseh
 * @lastModifiedVersion 5.2
 */
public interface DDRequestBuilderScriptInterface extends ScriptInterface {
   
    /**
     * Generate the dd request file.
     *
     * @param methodContext the method context
     * @throws BusinessException the business exception
     */
    public void generateDDRequestLotFile(Map<String, Object> methodContext) throws BusinessException ;

  
    /**
     * Build DD request file name.
     *
     * @param methodContext the method context
     * @return the DD file name
     * @throws BusinessException the business exception
     */
    public String getDDFileName(Map<String, Object> methodContext) throws BusinessException;
    
    /**
     * Return the prefix for the DD reject file.
     * @param methodContext the method context 
     * @return The prefix.
     * @throws BusinessException the BusinessException.
     */
    public String getDDRejectFilePrefix(Map<String, Object> methodContext) throws BusinessException;
    
    /**
     * Return the extension for the DD reject file.
     * @param methodContext the method context 
     * @return The extension.
     * @throws BusinessException the BusinessException.
     */
    public String getDDRejectFileExtension(Map<String, Object> methodContext) throws BusinessException;
    
    /**
     * Process the dd reject file.
     * 
     * @param methodContext the context.
     * @return The ddRejectFileInfos
     * @throws BusinessException the BusinessException.
     */
    public DDRejectFileInfos processDDRejectedFile(Map<String, Object> methodContext) throws BusinessException;


    /**
     * Generate the SCT request file.
     *
     * @param methodContext the method context
     * @throws BusinessException the business exception
     */
    void generateSCTRequestLotFile(Map<String, Object> methodContext) throws BusinessException;


    /**
     * Build SCT request file name.
     *
     * @param methodContext the method context
     * @return the SCT file name
     * @throws BusinessException the business exception
     */
    String getSCTFileName(Map<String, Object> methodContext) throws BusinessException;

}