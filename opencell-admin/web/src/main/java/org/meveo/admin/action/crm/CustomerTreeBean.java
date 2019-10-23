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
package org.meveo.admin.action.crm;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import org.meveo.admin.action.BaseBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.AccountEntity;
import org.meveo.model.BaseEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IEntity;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.crm.Customer;
import org.meveo.model.mediation.Access;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.shared.Name;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.crm.impl.AccountEntitySearchService;
import org.meveo.service.medina.impl.AccessService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 * Standard backing bean for {@link AccountEntity} that allows build accounts hierarchy for richfaces tree component. In this Bean you can set icons and links used in tree.
 */
@Named
@ViewScoped
public class CustomerTreeBean extends BaseBean<AccountEntity> {

    private static final String SUBSCRIPTION_KEY = "subscription";
    private static final String ACCESS_KEY = "access";

    private static final long serialVersionUID = 1L;

    // This is a list of available FontAwesome currency symbols
    private static final List<String> CURRENCIES = new ArrayList<String>() {
        private static final long serialVersionUID = 3959294292718669361L;

        {
            add("GPB");
            add("KRW");
            add("INR");
            add("EUR");
            add("TRY");
            add("RUB");
            add("JPY");
            add("ILS");
            add("USD");
        }
    };

    /**
     * Injected @{link AccountEntity} service. Extends {@link PersistenceService}.
     */
    @Inject
    private AccountEntitySearchService accountEntitySearchService;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public CustomerTreeBean() {
        super(AccountEntity.class);
    }

    @Inject
    private CustomerAccountService customerAccountService;

    @Inject
    private BillingAccountService billingAccountService;

    @Inject
    private UserAccountService userAccountService;

    @Inject
    private SubscriptionService subscriptionService;

    @Inject
    private AccessService accessService;

    private TreeNode accountsHierarchy;

    private Long selectedEntityId;

    @SuppressWarnings("rawtypes")
    private Class selectedEntityClass;

    public boolean isVisible() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        Boolean visible = (Boolean) session.getAttribute("hierarchyPanel:visible");
        if (visible == null) {
            visible = true;
            session.setAttribute("hierarchyPanel:visible", visible);
        }
        return visible;
    }

    public void toggleVisibility() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        Boolean visible = (Boolean) session.getAttribute("hierarchyPanel:visible");
        if (visible == null) {
            visible = true;
            session.setAttribute("hierarchyPanel:visible", visible);
        } else {
            visible = !visible;
            session.setAttribute("hierarchyPanel:visible", visible);
        }
        log.debug("Visibility set to: " + visible);
    }

    /**
     * Override get instance method because AccountEntity is abstract class and can not be instantiated in {@link BaseBean}.
     */
    @Override
    public AccountEntity getInstance() throws InstantiationException, IllegalAccessException {
        return new AccountEntity() {
            private static final long serialVersionUID = 1L;

            @Override
            public ICustomFieldEntity[] getParentCFEntities() {
                return null;
            }
        };
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<AccountEntity> getPersistenceService() {
        return accountEntitySearchService;
    }

    /**
     * Build account hierarchy for Primefaces tree component. Check entity type that was provided then loads {@link Customer} entity that is on top on hierarchy, and delegates
     * building logic to private build() recursion.
     * 
     * @param entity entity to build hierarchy for
     * @param displayCompleteHierarchy Shall a complete (true) or a short version of account hierarchy will be displayed. Short version displays all siblings, immediate children
     *        and parent hierarchy all the way to the customer.
     * @return TreeNode faces object
     */
    public TreeNode buildAccountsHierarchy(BaseEntity entity, boolean displayCompleteHierarchy) {

        if (accountsHierarchy != null) {
            return accountsHierarchy;
        }

        if (entity == null || entity.getId() == null) {
            return null;
        }

        if (displayCompleteHierarchy) {
            accountsHierarchy = buildAccountsHierarchyComplete(entity);
        } else {
            accountsHierarchy = buildAccountsHierarchyShort(entity);
        }
        return accountsHierarchy;
    }

    private TreeNode buildAccountsHierarchyShort(BaseEntity entity) {

        TreeNode tree = new DefaultTreeNode("Root", null);

        IEntity parentEntity = getParentEntity(entity);

        TreeNodeData treeNodeData = new TreeNodeData(entity, true);
        TreeNode currentEntityNode = new DefaultTreeNode(treeNodeData.getType(), treeNodeData, null);
        currentEntityNode.setExpanded(true);

        // Add children of a current node
        List<? extends IEntity> children = getChildren(entity);
        if (children != null) {
            for (IEntity child : children) {
                treeNodeData = new TreeNodeData(child, true);
                @SuppressWarnings("unused")
                TreeNode childNode = new DefaultTreeNode(treeNodeData.getType(), treeNodeData, currentEntityNode);
            }
        }

        TreeNode lastNode = currentEntityNode;

        boolean firstParent = true;
        while (parentEntity != null) {

            treeNodeData = new TreeNodeData(parentEntity, false);
            TreeNode parentNode = new DefaultTreeNode(treeNodeData.getType(), treeNodeData, null);
            parentNode.setExpanded(true);

            // Add siblings of a current entity and current entity to the parent node
            if (firstParent) {
                children = getChildren(parentEntity);
                if (children != null) {
                    for (IEntity child : children) {
                        if (!child.getId().equals(entity.getId())) {
                            treeNodeData = new TreeNodeData(child, true);
                            @SuppressWarnings("unused")
                            TreeNode childNode = new DefaultTreeNode(treeNodeData.getType(), treeNodeData, parentNode);
                        } else {
                            parentNode.getChildren().add(lastNode);
                            lastNode.setParent(parentNode);
                        }
                    }
                }

                firstParent = false;

                // Or just add entity to a parent node
            } else {
                parentNode.getChildren().add(lastNode);
                lastNode.setParent(parentNode);
            }
            parentEntity = getParentEntity(parentEntity);
            lastNode = parentNode;
        }

        lastNode.setParent(tree);
        tree.getChildren().add(lastNode);

        return tree;

    }

    private TreeNode buildAccountsHierarchyComplete(BaseEntity entity) {

        selectedEntityId = entity.getId();

        Customer customer = null;

        if (entity instanceof Customer) {
            customer = (Customer) entity;
            selectedEntityClass = Customer.class;

        } else if (entity instanceof CustomerAccount) {
            CustomerAccount acc = (CustomerAccount) entity;
            acc = customerAccountService.refreshOrRetrieve(acc);
            customer = acc.getCustomer();
            selectedEntityClass = CustomerAccount.class;

        } else if (entity instanceof BillingAccount) {
            BillingAccount acc = (BillingAccount) entity;
            acc = billingAccountService.refreshOrRetrieve(acc);
            // this kind of check is not really necessary, because tree
            // hierarchy should not be shown when creating new page
            if (acc.getCustomerAccount() != null) {
                customer = acc.getCustomerAccount().getCustomer();
            }
            selectedEntityClass = BillingAccount.class;

        } else if (entity instanceof UserAccount) {
            UserAccount acc = (UserAccount) entity;
            acc = userAccountService.refreshOrRetrieve(acc);
            if (acc.getBillingAccount() != null && acc.getBillingAccount().getCustomerAccount() != null) {
                customer = acc.getBillingAccount().getCustomerAccount().getCustomer();
            }
            selectedEntityClass = UserAccount.class;

        } else if (entity instanceof Subscription) {
            Subscription s = (Subscription) entity;
            s = subscriptionService.refreshOrRetrieve(s);
            if (s.getUserAccount() != null && s.getUserAccount().getBillingAccount() != null && s.getUserAccount().getBillingAccount().getCustomerAccount() != null) {
                customer = s.getUserAccount().getBillingAccount().getCustomerAccount().getCustomer();
            }
            selectedEntityClass = Subscription.class;

        } else if (entity instanceof Access) {
            Access access = (Access) entity;
            access = accessService.refreshOrRetrieve(access);
            if (access.getSubscription() != null && access.getSubscription().getUserAccount() != null && access.getSubscription().getUserAccount().getBillingAccount() != null
                    && access.getSubscription().getUserAccount().getBillingAccount().getCustomerAccount() != null) {
                customer = access.getSubscription().getUserAccount().getBillingAccount().getCustomerAccount().getCustomer();
            }
            selectedEntityClass = Access.class;
        }
        if (customer != null && customer.getCode() != null) {
            return buildComplete(customer);
        } else {
            return null;
        }
    }

    private TreeNode buildComplete(BaseEntity entity) {
        TreeNode tree = new DefaultTreeNode("Root", null);
        return buildComplete(entity, tree);
    }

    /**
     * Builds accounts hierarchy for Primefaces tree component. Customer has list of CustomerAccounts which has list of BillingAccounts which has list of UserAccounts which has
     * list of Susbcriptions. Any of those entities can be provided for this method and it will return remaining hierarchy in Primefaces tree format.
     * 
     * @param entity Customer entity.
     * @return Primefaces tree hierarchy.
     */
    private TreeNode buildComplete(BaseEntity entity, TreeNode parent) {

        if (entity instanceof Customer) {
            Customer customer = (Customer) entity;
            TreeNodeData treeNodeData = new TreeNodeData(customer, selectedEntityClass == Customer.class && customer.getId().equals(selectedEntityId));
            TreeNode treeNode = new DefaultTreeNode(treeNodeData.getType(), treeNodeData, parent);
            if (treeNodeData.isSelected()) {
                expandTreeNode(treeNode);
            }

            List<CustomerAccount> customerAccounts = customerAccountService.listByCustomer(customer);
            if (customerAccounts != null) {
                for (int i = 0; i < customerAccounts.size(); i++) {
                    buildComplete(customerAccounts.get(i), treeNode);
                }
            }

            return parent;

        } else if (entity instanceof CustomerAccount) {
            CustomerAccount customerAccount = (CustomerAccount) entity;
            TreeNodeData treeNodeData = new TreeNodeData(customerAccount, selectedEntityClass == CustomerAccount.class && customerAccount.getId().equals(selectedEntityId));
            TreeNode treeNode = new DefaultTreeNode(treeNodeData.getType(), treeNodeData, parent);
            if (treeNodeData.isSelected()) {
                expandTreeNode(treeNode);
            }

            List<BillingAccount> billingAccounts = billingAccountService.listByCustomerAccount(customerAccount);
            if (billingAccounts != null) {
                for (int i = 0; i < billingAccounts.size(); i++) {
                    buildComplete(billingAccounts.get(i), treeNode);
                }
            }

            return parent;

        } else if (entity instanceof BillingAccount) {
            BillingAccount billingAccount = (BillingAccount) entity;

            TreeNodeData treeNodeData = new TreeNodeData(billingAccount, selectedEntityClass == BillingAccount.class && billingAccount.getId().equals(selectedEntityId));
            TreeNode treeNode = new DefaultTreeNode(treeNodeData.getType(), treeNodeData, parent);
            if (treeNodeData.isSelected()) {
                expandTreeNode(treeNode);
            }

            List<UserAccount> userAccounts = userAccountService.listByBillingAccount(billingAccount);
            if (userAccounts != null) {
                for (int i = 0; i < userAccounts.size(); i++) {
                    buildComplete(userAccounts.get(i), treeNode);
                }
            }

            return parent;

        } else if (entity instanceof UserAccount) {
            UserAccount userAccount = (UserAccount) entity;
            TreeNodeData treeNodeData = new TreeNodeData(userAccount, selectedEntityClass == UserAccount.class && userAccount.getId().equals(selectedEntityId));
            TreeNode treeNode = new DefaultTreeNode(treeNodeData.getType(), treeNodeData, parent);
            if (treeNodeData.isSelected()) {
                expandTreeNode(treeNode);
            }

            List<Subscription> subscriptions = subscriptionService.listByUserAccount(userAccount);
            if (subscriptions != null) {
                if (subscriptions != null) {
                    for (int i = 0; i < subscriptions.size(); i++) {
                        buildComplete(subscriptions.get(i), treeNode);
                    }
                }
            }

            return parent;

        } else if (entity instanceof Subscription) {
            Subscription subscription = (Subscription) entity;
            TreeNodeData treeNodeData = new TreeNodeData(subscription, selectedEntityClass == Subscription.class && subscription.getId().equals(selectedEntityId));
            TreeNode treeNode = new DefaultTreeNode(treeNodeData.getType(), treeNodeData, parent);
            if (treeNodeData.isSelected()) {
                expandTreeNode(treeNode);
            }

            List<Access> accesses = accessService.listBySubscription(subscription);
            if (accesses != null) {
                if (accesses != null) {
                    for (int i = 0; i < accesses.size(); i++) {
                        buildComplete(accesses.get(i), treeNode);
                    }
                }
            }

            return parent;

        } else if (entity instanceof Access) {
            Access access = (Access) entity;
            TreeNodeData treeNodeData = new TreeNodeData(access, selectedEntityClass == Access.class && access.getId().equals(selectedEntityId));
            TreeNode treeNode = new DefaultTreeNode(treeNodeData.getType(), treeNodeData, parent);

            if (treeNodeData.isSelected()) {
                expandTreeNode(treeNode);
            }

            return parent;
        }

        throw new IllegalStateException("Unsupported entity for hierarchy");
    }

    private void expandTreeNode(TreeNode treeNode) {
        treeNode.setExpanded(true);

        if (treeNode.getParent() != null) {
            expandTreeNode(treeNode.getParent());
        }
    }

    public String getIcon(String type) {
        if (type.equals(Customer.ACCOUNT_TYPE)) {
            return "/img/customer-icon.png";
        }

        if (type.equals(CustomerAccount.ACCOUNT_TYPE)) {
            return "/img/customerAccount-icon.png";
        }

        if (type.equals(BillingAccount.ACCOUNT_TYPE)) {
            return "/img/billingAccount-icon.png";
        }

        if (type.equals(UserAccount.ACCOUNT_TYPE)) {
            return "/img/userAccount-icon.png";
        }

        if (type.equals(SUBSCRIPTION_KEY)) {
            return "/img/subscription-icon.gif";
        }

        if (type.equals(ACCESS_KEY)) {
            return "/img/subscription-icon.gif";
        }

        return null;
    }

    private IEntity getParentEntity(IEntity entity) {

        if (entity instanceof CustomerAccount) {
            return ((CustomerAccount) entity).getCustomer();
        } else if (entity instanceof BillingAccount) {
            return ((BillingAccount) entity).getCustomerAccount();
        } else if (entity instanceof UserAccount) {
            return ((UserAccount) entity).getBillingAccount();
        } else if (entity instanceof Subscription) {
            return ((Subscription) entity).getUserAccount();
        } else if (entity instanceof Access) {
            return ((Access) entity).getSubscription();
        }
        return null;
    }

    private List<? extends IEntity> getChildren(IEntity entity) {

        if (entity instanceof Customer) {
            return ((Customer) entity).getCustomerAccounts();
        } else if (entity instanceof CustomerAccount) {
            return ((CustomerAccount) entity).getBillingAccounts();
        } else if (entity instanceof BillingAccount) {
            return ((BillingAccount) entity).getUsersAccounts();
        } else if (entity instanceof UserAccount) {
            return ((UserAccount) entity).getSubscriptions();
        } else if (entity instanceof Subscription) {
            return ((Subscription) entity).getAccessPoints();
        }
        return null;
    }

    public class TreeNodeData {
        private Long id;
        private String code;
        private String firstName;
        private String lastName;
        /**
         * Flag for toString() method to know if it needs to show firstName/lastName.
         */
        private boolean showName;
        private String type;
        private boolean selected;
        // currency is an optional property, initialize it with the setter
        private String currency;

        public TreeNodeData(IEntity entity, boolean selected) {
            super();
            this.selected = selected;
            this.id = (Long) entity.getId();

            if (entity instanceof Customer) {

                this.code = ((Customer) entity).getDescriptionOrCode();
                this.type = Customer.ACCOUNT_TYPE;

            } else if (entity instanceof CustomerAccount) {

                Name name = ((CustomerAccount) entity).getName();
                this.code = ((CustomerAccount) entity).getDescriptionOrCode();
                this.firstName = (name != null && name.getFirstName() != null) ? name.getFirstName() : "";
                this.lastName = (name != null && name.getLastName() != null) ? name.getLastName() : "";
                this.currency = ((CustomerAccount) entity).getTradingCurrency().getCurrencyCode();
                this.type = CustomerAccount.ACCOUNT_TYPE;

            } else if (entity instanceof BillingAccount) {

                Name name = ((BillingAccount) entity).getName();
                this.code = ((BillingAccount) entity).getDescriptionOrCode();
                this.firstName = (name != null && name.getFirstName() != null) ? name.getFirstName() : "";
                this.lastName = (name != null && name.getLastName() != null) ? name.getLastName() : "";
                this.type = BillingAccount.ACCOUNT_TYPE;

            } else if (entity instanceof UserAccount) {

                Name name = ((UserAccount) entity).getName();
                this.code = ((UserAccount) entity).getDescriptionOrCode();
                this.firstName = (name != null && name.getFirstName() != null) ? name.getFirstName() : "";
                this.lastName = (name != null && name.getLastName() != null) ? name.getLastName() : "";
                this.type = UserAccount.ACCOUNT_TYPE;

            } else if (entity instanceof Subscription) {
                this.code = ((Subscription) entity).getDescriptionOrCode();
                this.type = SUBSCRIPTION_KEY;

            } else if (entity instanceof Access) {
                this.code = ((Access) entity).getAccessUserId();
                this.type = ACCESS_KEY;
            }
        }

        public TreeNodeData(Long id, String code, String firstName, String lastName, boolean showName, String type, boolean selected) {
            super();
            this.id = id;
            this.code = code;
            this.firstName = firstName;
            this.lastName = lastName;
            this.showName = showName;
            this.type = type;
            this.selected = selected;
        }

        public Long getId() {
            return id;
        }

        public String getCode() {
            return code;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getType() {
            return type;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getFirstAndLastName() {
            String result = lastName;
            if (firstName != null) {
                result = firstName + " " + lastName;
            }
            return result;
        }

        public String getCurrencyIconClass() {
            String iconClass = "fa fa-";
            if (CustomerTreeBean.CURRENCIES.contains(currency)) {
                return iconClass + currency.toLowerCase();
            }
            return iconClass + "usd";

        }

        @Override
        public String toString() {
            if (!StringUtils.isBlank(code)) {
                StringBuilder builder = new StringBuilder(code);
                if (showName) {
                    builder.append(" ").append(firstName).append(" ").append(lastName);
                }
                return builder.toString();
            } else {
                return "";
            }
        }

        /**
         * Because in customer search any type of customer can appear, this method is used in UI to get link to concrete customer edit page.
         * 
         * 
         * @return Edit page url.
         */
        public String getView() {
            if (type.equals(Customer.ACCOUNT_TYPE)) {
                return "customerDetail";
            } else if (type.equals(CustomerAccount.ACCOUNT_TYPE)) {
                return "customerAccountDetail";
            } else if (type.equals(BillingAccount.ACCOUNT_TYPE)) {
                return "billingAccountDetail";
            } else if (type.equals(UserAccount.ACCOUNT_TYPE)) {
                return "userAccountDetail";
            } else if (type.equals(SUBSCRIPTION_KEY)) {
                return "subscriptionDetail";
            } else if (type.equals(ACCESS_KEY)) {
                return "accessDetail";
            } else {
                throw new IllegalStateException("Wrong customer type " + type + " provided in EL in .xhtml");
            }
        }

        public boolean isSelected() {
            return selected;
        }
    }

}