package org.meveo.service.payments.impl;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.DDRequestItem;
import org.meveo.model.payments.DDRequestLOT;
import org.meveo.service.base.PersistenceService;

/**
 * The Class DDRequestItemService.
 *
 * @author anasseh
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.2
 */
@Stateless
public class DDRequestItemService extends PersistenceService<DDRequestItem> {

  
   
    /**
     * Creates the DD request item.
     *
     * @param amountToPay the amount to pay
     * @param ddRequestLOT the dd request LOT
     * @param caFullName the ca full name
     * @param errorMsg the error msg
     * @param listAO the list AO
     * @return the DD request item
     * @throws BusinessException the business exception
     */
    public DDRequestItem createDDRequestItem(BigDecimal amountToPay, DDRequestLOT ddRequestLOT, String caFullName, String errorMsg, List<AccountOperation> listAO)
            throws BusinessException {
        DDRequestItem ddDequestItem = new DDRequestItem();
        ddDequestItem.setErrorMsg(errorMsg);
        ddDequestItem.updateAudit(currentUser);
        ddDequestItem.setAmount(amountToPay);
//        ddDequestItem.setDdRequestLOT(ddRequestLOT);
        ddDequestItem.setBillingAccountName(caFullName);
        ddDequestItem.setDueDate(listAO.get(0).getDueDate());
        ddDequestItem.setPaymentInfo(listAO.get(0).getPaymentInfo());
        ddDequestItem.setPaymentInfo1(listAO.get(0).getPaymentInfo1());
        ddDequestItem.setPaymentInfo2(listAO.get(0).getPaymentInfo2());
        ddDequestItem.setPaymentInfo3(listAO.get(0).getPaymentInfo3());
        ddDequestItem.setPaymentInfo4(listAO.get(0).getPaymentInfo4());
        ddDequestItem.setPaymentInfo5(listAO.get(0).getPaymentInfo5());
        ddDequestItem.setAccountOperations(listAO);
        if(listAO.size() == 1 && !StringUtils.isBlank(listAO.get(0).getReference())) {
            ddDequestItem.setReference(listAO.get(0).getReference());
        }
        create(ddDequestItem);
        for (AccountOperation ao : listAO) {
            ao.setDdRequestItem(ddDequestItem);
        }
        log.info("dd request item: {} amount {} ", ddDequestItem.getId(), amountToPay);
        return ddDequestItem;
    }

    /**
     * Update the DD lot for all items with given id
     * @param ids a list of DD request item
     * @param lotId a id of the DD request lot
     */
    public void updateDDRequestItems(List<Long> ids, Long lotId) {
        try {
            int count = 0;
            SubListCreator<Long> subListCreator = new SubListCreator<>(ids, 1_000);
            while (subListCreator.isHasNext()) {
                Query query = getEntityManager().createQuery("update DDRequestItem d set d.ddRequestLOT.id = :lotId where d.id in (:ids)");
                query.setParameter("ids", subListCreator.getNextWorkSet());
                query.setParameter("lotId", lotId);
                count += query.executeUpdate();
            }
            log.info("update {} DD request items {}", count, ids.size());
        } catch (Exception e) {
            log.error("Error when updating DDR items", e);
        }
    }
}
