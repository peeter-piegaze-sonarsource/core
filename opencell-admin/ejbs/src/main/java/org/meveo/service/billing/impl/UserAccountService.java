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
package org.meveo.service.billing.impl;

import org.meveo.admin.exception.AccountAlreadyExistsException;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ElementNotResiliatedOrCanceledException;
import org.meveo.audit.logging.annotations.MeveoAudit;
import org.meveo.model.billing.*;
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.service.base.AccountService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Date;
import java.util.List;

@Stateless
public class UserAccountService extends AccountService<UserAccount> {

	@Inject
	private WalletService walletService;
	
	public void createUserAccount(BillingAccount billingAccount, UserAccount userAccount)
			throws BusinessException {

		log.debug("creating userAccount with details {}", new Object[] { userAccount});

		UserAccount existingUserAccount = findByCode(userAccount.getCode());
		if (existingUserAccount != null) {
			throw new AccountAlreadyExistsException(userAccount.getCode());
		}

		userAccount.setBillingAccount(billingAccount);
		create(userAccount);
		WalletInstance wallet = new WalletInstance();
		wallet.setCode(WalletTemplate.PRINCIPAL);
		wallet.setUserAccount(userAccount);
		walletService.create(wallet);

		userAccount.setWallet(wallet);
	}

	@MeveoAudit
	public UserAccount userAccountTermination(UserAccount userAccount, Date terminationDate,
			SubscriptionTerminationReason terminationReason) throws BusinessException {

		SubscriptionService subscriptionService = getManagedBeanInstance(SubscriptionService.class);
		if (terminationDate == null) {
			terminationDate = new Date();
		}
		List<Subscription> subscriptions = userAccount.getSubscriptions();
		for (Subscription subscription : subscriptions) {		
			subscriptionService.terminateSubscription(subscription, terminationDate, terminationReason, subscription.getOrderNumber());
		}
		userAccount.setTerminationReason(terminationReason);
		userAccount.setTerminationDate(terminationDate);
		userAccount.setStatus(AccountStatusEnum.TERMINATED);
		return update(userAccount);
	}

	@MeveoAudit
	public UserAccount userAccountCancellation(UserAccount userAccount, Date cancelationDate)
			throws BusinessException {

		SubscriptionService subscriptionService = getManagedBeanInstance(SubscriptionService.class);

		if (cancelationDate == null) {
			cancelationDate = new Date();
		}
		List<Subscription> subscriptions = userAccount.getSubscriptions();
		for (Subscription subscription : subscriptions) {
			subscriptionService.subscriptionCancellation(subscription, cancelationDate);
		}
		userAccount.setTerminationDate(cancelationDate);
		userAccount.setStatus(AccountStatusEnum.CANCELED);
		return update(userAccount);
	}

	@MeveoAudit
	public UserAccount userAccountReactivation(UserAccount userAccount, Date activationDate)
			throws BusinessException {
		if (activationDate == null) {
			activationDate = new Date();
		}
		if (userAccount.getStatus() != AccountStatusEnum.TERMINATED
				&& userAccount.getStatus() != AccountStatusEnum.CANCELED) {
			throw new ElementNotResiliatedOrCanceledException("user account", userAccount.getCode());
		}

		userAccount.setStatus(AccountStatusEnum.ACTIVE);
		return update(userAccount);
	}

	public List<UserAccount> listByBillingAccount(BillingAccount billingAccount) {
		return billingAccount.getUsersAccounts();
	}
	
}
