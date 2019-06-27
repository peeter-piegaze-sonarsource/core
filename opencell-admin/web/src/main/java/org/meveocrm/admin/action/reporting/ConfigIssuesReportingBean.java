package org.meveocrm.admin.action.reporting;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.BaseEntity;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.TradingLanguageService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.meveo.service.catalog.impl.CounterTemplateService;
import org.meveo.service.catalog.impl.InvoiceCategoryService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;
import org.meveo.service.catalog.impl.RecurringChargeTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.catalog.impl.TaxService;
import org.meveo.service.catalog.impl.UsageChargeTemplateService;
import org.meveo.service.script.ScriptInstanceService;
import org.primefaces.event.TabChangeEvent;

/**
 * @author Wassim Drira
 * @author melyoussoufi
 * @lastModifiedVersion 7.2.0
 *
 */
@Named
@ViewScoped
public class ConfigIssuesReportingBean extends BaseBean<BaseEntity> {

    private static final long serialVersionUID = 1L;

    @Inject
    private WalletOperationService walletOperationService;

    @Inject
    private UsageChargeTemplateService usageChargeTemplateService;

    @Inject
    private RecurringChargeTemplateService recurringChargeTemplateService;

    @Inject
    private OneShotChargeTemplateService oneShotChargeTemplateService;

    @Inject
    private TaxService taxService;

    @Inject
    private TradingLanguageService tradingLanguageService;

    @Inject
    private InvoiceCategoryService invoiceCategoryService;

    @Inject
    private InvoiceSubCategoryService invoiceSubCategoryService;

    @Inject
    private ServiceTemplateService serviceTemplateService;

    @Inject
    private CounterTemplateService counterTemplateService;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    private List<Entry<String, String>> jaspers;

    List<Tax> taxesNotAssociatedList = new ArrayList<Tax>();
    List<UsageChargeTemplate> usagesWithNotPricePlList = new ArrayList<UsageChargeTemplate>();
    List<RecurringChargeTemplate> recurringWithNotPricePlanList = new ArrayList<RecurringChargeTemplate>();
    List<OneShotChargeTemplate> oneShotChrgWithNotPricePlanList = new ArrayList<OneShotChargeTemplate>();
    List<TradingLanguage> languagesNotAssociatedList = new ArrayList<TradingLanguage>();
    List<InvoiceCategory> invoiceCatNotAssociatedList = new ArrayList<InvoiceCategory>();
    List<InvoiceSubCategory> invoiceSubCatNotAssociatedList = new ArrayList<InvoiceSubCategory>();
    List<ServiceTemplate> servicesWithNotOfferList = new ArrayList<ServiceTemplate>();
    List<UsageChargeTemplate> usagesChrgNotAssociatedList = new ArrayList<UsageChargeTemplate>();
    List<CounterTemplate> counterWithNotServicList = new ArrayList<CounterTemplate>();
    List<RecurringChargeTemplate> recurringNotAssociatedList = new ArrayList<RecurringChargeTemplate>();
    List<OneShotChargeTemplate> terminationNotAssociatedList = new ArrayList<OneShotChargeTemplate>();
    List<OneShotChargeTemplate> subNotAssociatedList = new ArrayList<OneShotChargeTemplate>();
    List<ScriptInstance> scriptInstanceWithErrorList = new ArrayList<ScriptInstance>();
    Map<String, String> jasperFilesList = new HashMap<String, String>();

    public int getNbrUsagesWithNotPricePlan() {
        return usageChargeTemplateService.getNbrUsagesChrgWithNotPricePlan();
    }

    public int getNbrRecurringWithNotPricePlan() {
        return recurringChargeTemplateService.getNbrRecurringChrgWithNotPricePlan();
    }

    public int getNbrOneShotWithNotPricePlan() {
        return oneShotChargeTemplateService.getNbrOneShotWithNotPricePlan();
    }

    public void constructChargesWithNotPricePlan(TabChangeEvent event) {
        usagesWithNotPricePlList = usageChargeTemplateService.getUsagesChrgWithNotPricePlan();
        recurringWithNotPricePlanList = recurringChargeTemplateService.getRecurringChrgWithNotPricePlan();
        oneShotChrgWithNotPricePlanList = oneShotChargeTemplateService.getOneShotChrgWithNotPricePlan();
    }

    public void constructTaxesNotAssociated(TabChangeEvent event) {
        taxesNotAssociatedList = taxService.getTaxesNotAssociated();
    }

    public void constructLanguagesNotAssociated(TabChangeEvent event) {
        languagesNotAssociatedList = tradingLanguageService.getLanguagesNotAssociated();
    }

    public void constructInvoiceCatNotAssociated(TabChangeEvent event) {
        invoiceCatNotAssociatedList = invoiceCategoryService.getInvoiceCatNotAssociated();
    }

    public void constructInvoiceSubCatNotAssociated(TabChangeEvent event) {
        invoiceSubCatNotAssociatedList = invoiceSubCategoryService.getInvoiceSubCatNotAssociated();
    }

    public void constructServicesWithNotOffer(TabChangeEvent event) {
        servicesWithNotOfferList = serviceTemplateService.getServicesWithNotOffer();
    }

    public void constructUsagesChrgNotAssociated(TabChangeEvent event) {
        usagesChrgNotAssociatedList = usageChargeTemplateService.getUsagesChrgNotAssociated();
    }

    public void constructCounterWithNotService(TabChangeEvent event) {
        counterWithNotServicList = counterTemplateService.getCounterWithNotService();
    }

    public void constructRecurringNotAssociated(TabChangeEvent event) {
        recurringNotAssociatedList = recurringChargeTemplateService.getRecurringChrgNotAssociated();
    }

    public void constructTermChrgNotAssociated(TabChangeEvent event) {
        terminationNotAssociatedList = oneShotChargeTemplateService.getTerminationChrgNotAssociated();
    }

    public void constructSubChrgNotAssociated(TabChangeEvent event) {
        subNotAssociatedList = oneShotChargeTemplateService.getSubscriptionChrgNotAssociated();
    }

    public void constructScriptInstancesWithError(TabChangeEvent event) {
        scriptInstanceWithErrorList = scriptInstanceService.getScriptInstancesWithError();
    }
    
    ConfigIssuesReportingDTO reportConfigDto;
	public void constructWalletOperation(TabChangeEvent event) {
    	 reportConfigDto = new ConfigIssuesReportingDTO();
    	 List<Object[]> WOStatus= walletOperationService.getNbrWalletsOperationByStatus();
    	 for (Object[] s: WOStatus) {
    		 reportConfigDto.setNbrWalletOpOpen(((Long)(s[0].equals(WalletOperationStatusEnum.OPEN)? s[1]: reportConfigDto.getNbrWalletOpOpen().longValue())).intValue());
             reportConfigDto.setNbrWalletOpRerated(((Long)(s[0].equals(WalletOperationStatusEnum.RERATED)? s[1]: reportConfigDto.getNbrWalletOpRerated().longValue())).intValue());
             reportConfigDto.setNbrWalletOpReserved(((Long)(s[0].equals(WalletOperationStatusEnum.RESERVED)? s[1]: reportConfigDto.getNbrWalletOpReserved().longValue())).intValue());
             reportConfigDto.setNbrWalletOpCancled(((Long)(s[0].equals(WalletOperationStatusEnum.CANCELED) ? s[1]: reportConfigDto.getNbrWalletOpCancled().longValue())).intValue());
             reportConfigDto.setNbrWalletOpTorerate(((Long)(s[0].equals(WalletOperationStatusEnum.TO_RERATE)? s[1]: reportConfigDto.getNbrWalletOpTorerate().longValue())).intValue());
             reportConfigDto.setNbrWalletOpTreated(((Long)(s[0].equals(WalletOperationStatusEnum.TREATED)? s[1]: reportConfigDto.getNbrWalletOpTreated().longValue())).intValue());
             reportConfigDto.setNbrWalletOpScheduled(((Long)(s[0].equals(WalletOperationStatusEnum.SCHEDULED)? s[1]: reportConfigDto.getNbrWalletOpScheduled().longValue())).intValue());
             
    	 }
	}
    public void constructEdr(TabChangeEvent event) { 
    	 reportConfigDto = new ConfigIssuesReportingDTO();
    	 List<Object[]>  EdrStatus= walletOperationService.getNbrEdrByStatus();
    	 for (Object[] e: EdrStatus) {
    		 reportConfigDto.setNbrEdrOpen(((Long)(e[0].equals(EDRStatusEnum.OPEN)? e[1]: reportConfigDto.getNbrEdrOpen().longValue())).intValue());
	         reportConfigDto.setNbrEdrRated(((Long)(e[0].equals(EDRStatusEnum.RATED)? e[1]: reportConfigDto.getNbrEdrRated().longValue())).intValue());
	         reportConfigDto.setNbrEdrRejected(((Long)(e[0].equals(EDRStatusEnum.REJECTED)? e[1]: reportConfigDto.getNbrEdrRejected().longValue())).intValue());
	         reportConfigDto.setNbrEdrMediating(((Long)(e[0].equals(EDRStatusEnum.MEDIATING)? e[1]: reportConfigDto.getNbrEdrMediating().longValue())).intValue());
	         reportConfigDto.setNbrEdrAggregated(((Long)(e[0].equals(EDRStatusEnum.AGGREGATED)? e[1]: reportConfigDto.getNbrEdrAggregated().longValue())).intValue());
    		 
    	}
    }
    

    private Map<String, String> getJasperFiles() throws IOException {
        Map<String, String> jasperFiles = new HashMap<String, String>();
        ParamBean paramBean = paramBeanFactory.getInstance();
        String jasperCommercial = paramBean.getProperty("jasper.invoiceTemplate.commercial", "invoice.jasper");
        String jasperAdjustment = paramBean.getProperty("jasper.invoiceTemplate.adjustment", "invoice.jasper");
        // check jaspers files
        File jasperDir = new File(paramBeanFactory.getChrootDir() + File.separator + "jasper");
        if (!jasperDir.exists()) {
            jasperDir.mkdirs();
        }
        log.info("Jaspers template used :" + jasperDir.getPath());
        File[] foldersList = jasperDir.listFiles();
        String commercialRep = null;
        String adjustRep = null;
        File commercialInvoice = null;
        File adjustInvoice = null;
        if (foldersList != null && foldersList.length > 0) {
            for (File f : foldersList) {
                adjustRep = f.getCanonicalPath() + File.separator + "invoiceAdjustmentPdf";
                adjustInvoice = new File(adjustRep + File.separator + jasperCommercial);
                if (!adjustInvoice.exists()) {
                    jasperFiles.put(adjustRep, jasperAdjustment);
                }
                commercialRep = f.getCanonicalPath() + File.separator + "pdf";
                commercialInvoice = new File(commercialRep + File.separator + jasperCommercial);
                if (!commercialInvoice.exists()) {
                    jasperFiles.put(commercialRep, jasperCommercial);
                }
            }
        }
        return jasperFiles;
    }

    public void getJasperFilesNotFound(TabChangeEvent event) throws IOException {
        jasperFilesList = getJasperFiles();
        if (jasperFilesList != null && jasperFilesList.size() > 0) {
            jaspers = new ArrayList<>(jasperFilesList.entrySet());
        }
        reportConfigDto = new ConfigIssuesReportingDTO();
        reportConfigDto.setNbrJasperDir(jasperFilesList.size());
       
    }


    public Integer getNbrChargesWithNotPricePlan() {
        return getNbrUsagesWithNotPricePlan() + getNbrRecurringWithNotPricePlan() + getNbrOneShotWithNotPricePlan();
    }

    public Integer getNbTaxesNotAssociated() {
        return taxService.getNbTaxesNotAssociated();
    }

    public Integer getNbLanguageNotAssociated() {
        return tradingLanguageService.getNbLanguageNotAssociated();
    }

    public Integer getNbInvCatNotAssociated() {
        return invoiceCategoryService.getNbInvCatNotAssociated();
    }

    public Integer getNbInvSubCatNotAssociated() {
        return invoiceSubCategoryService.getNbInvSubCatNotAssociated();
    }

    public Integer getNbServiceWithNotOffer() {
        return serviceTemplateService.getNbServiceWithNotOffer();
    }

    public Integer getNbrUsagesChrgNotAssociated() {
        return usageChargeTemplateService.getNbrUsagesChrgNotAssociated();
    }

    public Integer getNbrCounterWithNotService() {
        return counterTemplateService.getNbrCounterWithNotService();
    }

    public Integer getNbrRecurringChrgNotAssociated() {
        return recurringChargeTemplateService.getNbrRecurringChrgNotAssociated();
    }

    public Integer getNbrTerminationChrgNotAssociated() {
        return oneShotChargeTemplateService.getNbrTerminationChrgNotAssociated();
    }

    public Integer getNbrSubscriptionChrgNotAssociated() {
        return oneShotChargeTemplateService.getNbrSubscriptionChrgNotAssociated();
    }

    public long getNbrScriptInstanceWithError() {
        return scriptInstanceService.countScriptInstancesWithError();
    }

    public ConfigIssuesReportingDTO getReportConfigDto() {
        return reportConfigDto;
    }

    public List<Tax> getTaxesNotAssociatedList() {
        return taxesNotAssociatedList;
    }

    public List<UsageChargeTemplate> getUsagesWithNotPricePlList() {
        return usagesWithNotPricePlList;
    }

    public List<RecurringChargeTemplate> getRecurringWithNotPricePlanList() {
        return recurringWithNotPricePlanList;
    }

    public List<OneShotChargeTemplate> getOneShotChrgWithNotPricePlanList() {
        return oneShotChrgWithNotPricePlanList;
    }

    public List<TradingLanguage> getLanguagesNotAssociatedList() {
        return languagesNotAssociatedList;
    }

    public List<InvoiceCategory> getInvoiceCatNotAssociatedList() {
        return invoiceCatNotAssociatedList;
    }

    public List<InvoiceSubCategory> getInvoiceSubCatNotAssociatedList() {
        return invoiceSubCatNotAssociatedList;
    }

    public List<ServiceTemplate> getServicesWithNotOfferList() {
        return servicesWithNotOfferList;
    }

    public List<UsageChargeTemplate> getUsagesChrgNotAssociatedList() {
        return usagesChrgNotAssociatedList;
    }

    public List<CounterTemplate> getCounterWithNotServicList() {
        return counterWithNotServicList;
    }

    public List<RecurringChargeTemplate> getRecurringNotAssociatedList() {
        return recurringNotAssociatedList;
    }

    public List<OneShotChargeTemplate> getTerminationNotAssociatedList() {
        return terminationNotAssociatedList;
    }

    public List<OneShotChargeTemplate> getSubNotAssociatedList() {
        return subNotAssociatedList;
    }

    public List<ScriptInstance> getScriptInstanceWithErrorList() {
        return scriptInstanceWithErrorList;
    }

    @Override
    public IPersistenceService<BaseEntity> getPersistenceService() {
        return getPersistenceService();
    }

    @Override
    public String getEditViewName() {
        return "";
    }

    public List<Entry<String, String>> getJaspers() {
        return jaspers;
    }

    public void setJaspers(List<Entry<String, String>> jaspers) {
        this.jaspers = jaspers;
    }
    
    public Integer getNbrJasperDir() throws IOException {
    	return getJasperFiles().size();
    }

}