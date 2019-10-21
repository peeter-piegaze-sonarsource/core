package org.meveo.api.rest.payment.impl;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.account.TransferAccountOperationDto;
import org.meveo.api.dto.payment.AccountOperationDto;
import org.meveo.api.dto.payment.LitigationRequestDto;
import org.meveo.api.dto.payment.MatchOperationRequestDto;
import org.meveo.api.dto.payment.UnMatchingOperationRequestDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.payment.AccountOperationResponseDto;
import org.meveo.api.dto.response.payment.AccountOperationsResponseDto;
import org.meveo.api.dto.response.payment.MatchedOperationsResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.payment.AccountOperationApi;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.payment.AccountOperationRs;
import org.meveo.model.payments.PaymentMethodEnum;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

/**
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @lastModifiedVersion 8.0.0
 */
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class AccountOperationRsImpl extends BaseRs implements AccountOperationRs {

    @Inject
    private AccountOperationApi accountOperationApi;

    @Override
    public ActionStatus create(AccountOperationDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            result.setMessage("" + accountOperationApi.create(postData));
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public AccountOperationsResponseDto listGet(String customerAccountCode, String query, String fields, Integer offset, Integer limit, String sortBy, SortOrder sortOrder) {

        AccountOperationsResponseDto result = new AccountOperationsResponseDto();

        PagingAndFiltering pagingAndFiltering = new PagingAndFiltering(customerAccountCode != null ? "customerAccount.code:" + customerAccountCode : query, null, offset, limit,
                sortBy, sortOrder);
        
        try {
            result = accountOperationApi.list(pagingAndFiltering);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public AccountOperationsResponseDto listPost(PagingAndFiltering pagingAndFiltering) {

        AccountOperationsResponseDto result = new AccountOperationsResponseDto();

        try {
            result = accountOperationApi.list(pagingAndFiltering);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus matchOperations(MatchOperationRequestDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            accountOperationApi.matchOperations(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus unMatchingOperations(UnMatchingOperationRequestDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            accountOperationApi.unMatchingOperations(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus addLitigation(LitigationRequestDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            accountOperationApi.addLitigation(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus cancelLitigation(LitigationRequestDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            accountOperationApi.cancelLitigation(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public AccountOperationResponseDto find(Long id) {
        AccountOperationResponseDto result = new AccountOperationResponseDto();
        try {
            result.setAccountOperation(accountOperationApi.find(id));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus updatePaymentMethod(String customerAccountCode, Long aoId, PaymentMethodEnum paymentMethod) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            accountOperationApi.updatePaymentMethod(customerAccountCode, aoId, paymentMethod);
        } catch (Exception e) {
            processException(e, result);
        }
        return result;
    }

    @Override
    public MatchedOperationsResponseDto listMatchedOperations(Long accountOperationId) {
        MatchedOperationsResponseDto result = new MatchedOperationsResponseDto();
        try {
            result.setMatchedOperations(accountOperationApi.listMatchedOperations(accountOperationId));

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus transferAccountOperation(TransferAccountOperationDto transferAccountOperationDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            accountOperationApi.transferAccountOperation(transferAccountOperationDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
}