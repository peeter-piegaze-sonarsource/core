package org.meveo.service.payments.impl;

import java.util.Date;
import java.util.List;

import org.meveo.admin.exception.BusinessEntityException;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.model.filter.Filter;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.DDRequestLotOp;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.service.filter.FilterService;

/**
 * An abstract class to centralize some common methods such as getting the list of AOs to pay.
 *
 * @author Said Ramli
 * @author anasseh
 * @lastModifiedVersion 5.3
 */
public abstract class AbstractDDRequestBuilder implements DDRequestBuilderInterface {

    protected Object getServiceInterface(String serviceInterfaceName) {
        return EjbUtils.getServiceInterface(serviceInterfaceName);
    }

    @Override
    public List<AccountOperation> findListAoToPay(DDRequestLotOp ddrequestLotOp) throws BusinessException {

        FilterService filterService = (FilterService) getServiceInterface(FilterService.class.getSimpleName());
        AccountOperationService accountOperationService = (AccountOperationService) getServiceInterface(AccountOperationService.class.getSimpleName());

        Filter filter = ddrequestLotOp.getFilter();

        List<AccountOperation> listAoToPay;
        if (filter == null) {
            Date fromDueDate = ddrequestLotOp.getFromDueDate();
            Date toDueDate = ddrequestLotOp.getToDueDate();
            validateDates(fromDueDate, toDueDate);
            listAoToPay = accountOperationService
                    .getAOsToPayOrRefund(PaymentMethodEnum.DIRECTDEBIT, fromDueDate, toDueDate, ddrequestLotOp.getPaymentOrRefundEnum().getOperationCategoryToProcess(),
                            ddrequestLotOp.getSeller());
        } else {
            listAoToPay = (List<AccountOperation>) filterService.filteredListAsObjects(filter);
        }
        return listAoToPay;
    }

    @Override
    public List<Long> findListAoToPayIds(DDRequestLotOp ddrequestLotOp, int pageSize) throws BusinessException {
        AccountOperationService accountOperationService = (AccountOperationService) getServiceInterface(AccountOperationService.class.getSimpleName());

        Date from = ddrequestLotOp.getFromDueDate();
        Date to = ddrequestLotOp.getToDueDate();
        validateDates(from, to);
        OperationCategoryEnum operationCategoryToProcess = ddrequestLotOp.getPaymentOrRefundEnum().getOperationCategoryToProcess();
        return accountOperationService
                .getAOsToPayOrRefundIds(PaymentMethodEnum.DIRECTDEBIT, from, to, operationCategoryToProcess, ddrequestLotOp.getSeller(), pageSize);
    }

    private void validateDates(Date fromDueDate, Date toDueDate) {
        if (fromDueDate == null) {
            throw new BusinessEntityException("fromDuDate is empty");
        }
        if (toDueDate == null) {
            throw new BusinessEntityException("toDueDate is empty");
        }
        if (fromDueDate.after(toDueDate)) {
            throw new BusinessEntityException("fromDueDate is after toDueDate");
        }
    }
}
