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
package org.meveo.admin.action.payments;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.BusinessEntity;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.OtherTransactionGeneral;
import org.meveo.model.payments.Payment;
import org.meveo.model.payments.PaymentVentilation;
import org.meveo.model.payments.VentilationActionStatusEnum;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.payments.impl.OCCTemplateService;
import org.meveo.service.payments.impl.OtherTransactionGeneralService;
import org.meveo.service.payments.impl.PaymentService;
import org.meveo.service.payments.impl.PaymentVentilationService;
import org.omnifaces.cdi.Param;
import org.primefaces.model.LazyDataModel;

/**
 * Standard backing bean for {@link PaymentVentilation} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their
 * create, edit, view, delete operations). It works with Manaty custom JSF components.
 */
@Named
@ViewScoped
public class PaymentVentilationBean extends BaseBean<PaymentVentilation> {

    private static final long serialVersionUID = 1L;

    /**
     * Injected @{link MatchingAmount} service. Extends {@link PersistenceService}
     */
    @Inject
    private PaymentVentilationService paymentVentilationService;

    @Inject
    private OCCTemplateService oCCTemplateService;

    @Inject
    private OtherTransactionGeneralService otherTransactionGeneralService;

    @Inject
    private PaymentService paymentService;

    @Inject
    @Param
    private Long otgId;
    
    @Inject
    @Param
    private Long backEntityId;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public PaymentVentilationBean() {
        super(PaymentVentilation.class);
    }

    @PostConstruct
    public void init() {
        if (otgId != null) {
            getEntity().setOriginalOT(otherTransactionGeneralService.findById(otgId));
        }
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<PaymentVentilation> getPersistenceService() {
        return paymentVentilationService;
    }

    private Payment createPayment(PaymentVentilation paymentVentilation, OCCTemplate occTemplate) throws BusinessException {
        BigDecimal ventilationAmout = paymentVentilation.getVentilationAmount();
        OtherTransactionGeneral originalOTG = (OtherTransactionGeneral) paymentVentilation.getOriginalOT();

        Payment payment = new Payment();
        payment.setCustomerAccount(paymentVentilation.getCustomerAccount());
        payment.setDescription(originalOTG.getDescription());
        payment.setPaymentMethod(originalOTG.getPaymentMethod());
        payment.setAmount(ventilationAmout);
        payment.setUnMatchingAmount(ventilationAmout);
        payment.setMatchingAmount(BigDecimal.ZERO);
        payment.setAccountingCode(occTemplate.getAccountingCode());
        payment.setOccCode(occTemplate.getCode());
        payment.setOccDescription(occTemplate.getDescription());
        payment.setTransactionCategory(occTemplate.getOccCategory());
        payment.setReference(originalOTG.getReference());
        payment.setTransactionDate(originalOTG.getTransactionDate());
        payment.setDueDate(originalOTG.getDueDate());
        payment.setMatchingStatus(MatchingStatusEnum.O);
        // Additional payment information 1 - Bank Code
        payment.setPaymentInfo1(originalOTG.getPaymentInfo1());
        // Additional payment information 2 - Branch Code
        payment.setPaymentInfo2(originalOTG.getPaymentInfo2());
        // Additional payment information 3 - Account Number
        payment.setPaymentInfo3(originalOTG.getPaymentInfo3());
        paymentService.create(payment);

        return payment;

    }

    private OtherTransactionGeneral createVentilatedOTG() throws BusinessException {
        ParamBean paramBean = paramBeanFactory.getInstance();
        BigDecimal ventilationAmout = entity.getVentilationAmount();
        OCCTemplate occTemplate = getOCCTemplate(paramBean.getProperty("occ.payment.rec.dr", "PAY_REC_DR"));
        return createOTG(occTemplate, entity, ventilationAmout, ventilationAmout, BigDecimal.ZERO, MatchingStatusEnum.L);
    }

    private OtherTransactionGeneral createUnventilatedOTG(PaymentVentilation paymentVentilation) throws BusinessException {
        ParamBean paramBean = paramBeanFactory.getInstance();
        BigDecimal ventilationAmout = paymentVentilation.getVentilationAmount();
        OCCTemplate occTemplate = getOCCTemplate(paramBean.getProperty("occ.payment.rec.cr", "PAY_REC_CR"));
        return createOTG(occTemplate, paymentVentilation, ventilationAmout, BigDecimal.ZERO, ventilationAmout, MatchingStatusEnum.O);

    }

    private OtherTransactionGeneral createOTG(OCCTemplate occTemplate, PaymentVentilation paymentVentilation, BigDecimal amount, BigDecimal ventilatedAmout,
            BigDecimal unventilatedAmout, MatchingStatusEnum status) throws BusinessException {

        OtherTransactionGeneral originalOTG = (OtherTransactionGeneral) paymentVentilation.getOriginalOT();

        OtherTransactionGeneral otg = new OtherTransactionGeneral();
        otg.setGeneralLedger(originalOTG.getGeneralLedger());
        otg.setDescription(originalOTG.getDescription());
        otg.setPaymentMethod(originalOTG.getPaymentMethod());
        otg.setAmount(amount);
        otg.setMatchingAmount(ventilatedAmout);
        otg.setUnMatchingAmount(unventilatedAmout);
        otg.setAccountingCode(occTemplate.getAccountingCode());
        otg.setOccCode(occTemplate.getCode());
        otg.setOccDescription(occTemplate.getDescription());
        otg.setTransactionCategory(occTemplate.getOccCategory());
        otg.setReference(originalOTG.getReference());
        otg.setTransactionDate(originalOTG.getTransactionDate());
        otg.setDueDate(originalOTG.getDueDate());
        otg.setMatchingStatus(status);
        // Additional payment information 1 - Bank Code
        otg.setPaymentInfo1(originalOTG.getPaymentInfo1());
        // Additional payment information 2 - Branch Code
        otg.setPaymentInfo2(originalOTG.getPaymentInfo2());
        // Additional payment information 3 - Account Number
        otg.setPaymentInfo3(originalOTG.getPaymentInfo3());

        otg.setPaymentInfo7(originalOTG.getPaymentInfo7());
        otherTransactionGeneralService.create(otg);

        return otg;

    }

    @ActionMethod
    public String unventilate(PaymentVentilation paymentVentilation) throws BusinessException {
        AccountOperation ao = paymentVentilation.getAccountOperation();

        if (ao != null && ao.getMatchingStatus() != MatchingStatusEnum.O) {
            throw new ValidationException("Unauthorized ventilation", "paymentVentilation.unauthorized");
        }
        paymentVentilation.setVentilationActionStatus(VentilationActionStatusEnum.U);
        super.saveOrUpdate(paymentVentilation);

        createUnventilatedOTG(paymentVentilation);
        ParamBean paramBean = paramBeanFactory.getInstance();
        OCCTemplate occTemplate = getOCCTemplate(paramBean.getProperty("occ.payment.411100.dr", "411100_DR"));
        createPayment(paymentVentilation, occTemplate); //// Account operation Debit

        messages.info(new BundleKey("messages", "update.successful"));
        return "/pages/payments/otherTransactions/ventilateTransaction.xhtml?otgId="+paymentVentilation.getOriginalOT().getId()+"&edit=true&backView=backToSellerFromOT&backEntityId="+backEntityId+"&faces-redirect=true";
    }


    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {
        BigDecimal ventilationAmout = entity.getVentilationAmount();
        BigDecimal unventilatedAmount = entity.getOriginalOT().getUnMatchingAmount();
        OtherTransactionGeneral originalOTG = (OtherTransactionGeneral) entity.getOriginalOT();

        if (ventilationAmout.compareTo(BigDecimal.ZERO) <= 0 || unventilatedAmount.compareTo(ventilationAmout) < 0) {
            throw new ValidationException("Ventilation amount is not valid", "paymentVentilation.invalidAmount");
        }
        originalOTG.setUnMatchingAmount(unventilatedAmount.subtract(ventilationAmout));
        originalOTG.setMatchingAmount(originalOTG.getMatchingAmount().add(ventilationAmout));
        MatchingStatusEnum matchingStatus = originalOTG.getUnMatchingAmount().compareTo(BigDecimal.ZERO) == 0 ? MatchingStatusEnum.L : MatchingStatusEnum.P;
        originalOTG.setMatchingStatus(matchingStatus);
        try {
            otherTransactionGeneralService.update(originalOTG);
            entity.setVentilationDate(new Date());
            super.saveOrUpdate(entity);
            entity.setNewOT(createVentilatedOTG());
            ParamBean paramBean = paramBeanFactory.getInstance();
            OCCTemplate occTemplate = getOCCTemplate(paramBean.getProperty("occ.payment.411100.cr", "411100_CR"));
            entity.setAccountOperation(createPayment(entity, occTemplate)); //// Account operation Credit
            super.saveOrUpdate(killConversation);
        } catch (BusinessException e) {
            log.error(e.getMessage(), e);
            messages.error(new BundleKey("messages", "error.unexpected"));
        }

        return "/pages/payments/otherTransactions/ventilateTransaction.xhtml?otgId="+entity.getOriginalOT().getId()+"&edit=true&backView=backToSellerFromOT&backEntityId="+backEntityId+"&faces-redirect=true";

    }

    public LazyDataModel<PaymentVentilation> getPaymentVentilations() {
        if (entity != null && entity.getOriginalOT() != null && !entity.getOriginalOT().isTransient()) {
            filters.put("originalOT", entity.getOriginalOT());
            return getLazyDataModel();
        } else {
            return null;
        }
    }

    @Override
    protected List<String> getFormFieldsToFetch() {
        return Arrays.asList("originalOT");
    }

    @Override
    protected List<String> getListFieldsToFetch() {
        return Arrays.asList("originalOT");
    }

    /**
     * get OCC template by code.
     *
     * @param occTemplateCode the occ template code
     * @return OCC Template
     * @throws BusinessException Business Exception
     */
    private OCCTemplate getOCCTemplate(String occTemplateCode) throws BusinessException {
        OCCTemplate occTemplate = oCCTemplateService.findByCode(occTemplateCode);
        if (occTemplate == null) {
            throw new BusinessException("Cannot find OCC Template with code=" + occTemplateCode);
        }
        return occTemplate;
    }
}