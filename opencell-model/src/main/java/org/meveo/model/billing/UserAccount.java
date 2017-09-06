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
package org.meveo.model.billing;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.meveo.model.AccountEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ICustomFieldEntity;

@Entity
@CustomFieldEntity(cftCodePrefix = "UA")
@ExportIdentifier({ "code" })
@DiscriminatorValue(value = "ACCT_UA")
@Table(name = "billing_user_account")
public class UserAccount extends AccountEntity {

    public static final String ACCOUNT_TYPE = ((DiscriminatorValue) UserAccount.class.getAnnotation(DiscriminatorValue.class)).value();

    private static final long serialVersionUID = 1L;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 10)
    private AccountStatusEnum status = AccountStatusEnum.ACTIVE;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "status_date")
    private Date statusDate = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "subscription_date")
    private Date subscriptionDate = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "termination_date")
    private Date terminationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_account_id")
    private BillingAccount billingAccount;

    @OneToMany(mappedBy = "userAccount", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    // TODO : Add orphanRemoval annotation.
    // @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<Subscription> subscriptions;

    @OneToMany(mappedBy = "userAccount", fetch = FetchType.LAZY)
    private List<InvoiceAgregate> invoiceAgregates;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // TODO : Add orphanRemoval annotation.
    // @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    @JoinColumn(name = "wallet_id")
    private WalletInstance wallet;

    @OneToMany(mappedBy = "userAccount", fetch = FetchType.LAZY)
    @MapKey(name = "code")
    // TODO : Add orphanRemoval annotation.
    // @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    Map<String, WalletInstance> prepaidWallets;

    @OneToMany(mappedBy = "userAccount", fetch = FetchType.LAZY)
    @MapKey(name = "code")
    // TODO : Add orphanRemoval annotation.
    // @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    // key is the counter template code
    Map<String, CounterInstance> counters;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "termin_reason_id")
    private SubscriptionTerminationReason terminationReason;

    public UserAccount() {
        accountType = ACCOUNT_TYPE;
    }

    public BillingAccount getBillingAccount() {
        return billingAccount;
    }

    public void setBillingAccount(BillingAccount billingAccount) {
        this.billingAccount = billingAccount;
    }

    public AccountStatusEnum getStatus() {
        return status;
    }

    public void setStatus(AccountStatusEnum status) {
        if (this.status != status) {
            this.statusDate = new Date();
        }
        this.status = status;
    }

    public Date getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }

    public Date getSubscriptionDate() {
        return subscriptionDate;
    }

    public void setSubscriptionDate(Date subscriptionDate) {
        this.subscriptionDate = subscriptionDate;
    }

    public Date getTerminationDate() {
        return terminationDate;
    }

    public void setTerminationDate(Date terminationDate) {
        this.terminationDate = terminationDate;
    }

    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public WalletInstance getWallet() {
        return wallet;
    }

    public void setWallet(WalletInstance wallet) {
        this.wallet = wallet;
    }

    public Map<String, WalletInstance> getPrepaidWallets() {
        return prepaidWallets;
    }

    public void setPrepaidWallets(Map<String, WalletInstance> prepaidWallets) {
        this.prepaidWallets = prepaidWallets;
    }

    public Map<String, CounterInstance> getCounters() {
        return counters;
    }

    public void setCounters(Map<String, CounterInstance> counters) {
        this.counters = counters;
    }

    public List<InvoiceAgregate> getInvoiceAgregates() {
        return invoiceAgregates;
    }

    public void setInvoiceAgregates(List<InvoiceAgregate> invoiceAgregates) {
        this.invoiceAgregates = invoiceAgregates;
    }

    public SubscriptionTerminationReason getTerminationReason() {
        return terminationReason;
    }

    public void setTerminationReason(SubscriptionTerminationReason terminationReason) {
        this.terminationReason = terminationReason;
    }

    public WalletInstance getWalletInstance(String walletCode) {
        WalletInstance result = wallet;
        if (!"PRINCIPAL".equals(walletCode) && prepaidWallets.containsKey(walletCode)) {
            result = prepaidWallets.get(walletCode);
        }
        return result;
    }

    @Override
    public ICustomFieldEntity[] getParentCFEntities() {
        return new ICustomFieldEntity[] { billingAccount };
    }

    @Override
    public BusinessEntity getParentEntity() {
        return billingAccount;
    }

    @Override
    public Class<? extends BusinessEntity> getParentEntityType() {
        return BillingAccount.class;
    }
}
