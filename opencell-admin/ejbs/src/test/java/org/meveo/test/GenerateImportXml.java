/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.test;

import java.io.File;
import java.util.Date;

import org.meveo.commons.utils.JAXBUtils;
import org.meveo.model.jaxb.account.Address;
import org.meveo.model.jaxb.account.BankCoordinates;
import org.meveo.model.jaxb.account.BillingAccount;
import org.meveo.model.jaxb.account.BillingAccounts;
import org.meveo.model.jaxb.account.Name;
import org.meveo.model.jaxb.account.UserAccount;
import org.meveo.model.jaxb.account.UserAccounts;
import org.meveo.model.jaxb.customer.Customer;
import org.meveo.model.jaxb.customer.CustomerAccount;
import org.meveo.model.jaxb.customer.CustomerAccounts;
import org.meveo.model.jaxb.customer.Customers;
import org.meveo.model.jaxb.customer.Seller;
import org.meveo.model.jaxb.customer.Sellers;
import org.meveo.model.jaxb.subscription.Access;
import org.meveo.model.jaxb.subscription.Accesses;
import org.meveo.model.jaxb.subscription.ServiceInstance;
import org.meveo.model.jaxb.subscription.Services;
import org.meveo.model.jaxb.subscription.Status;
import org.meveo.model.jaxb.subscription.Subscription;
import org.meveo.model.jaxb.subscription.Subscriptions;
import org.meveo.model.shared.DateUtils;

/**
 * @author R.AITYAAZZA
 *
 */
public class GenerateImportXml {

	/**
	 * @param args
	 */
	/************************** configuration properties ***********************************/
	private static int count = 5;
	private static int startIndex = 1;
	private static int BC_NUMBER = 1;
	private static String billingCyclePrefix = "CYC_INV_MT";

	private static String customersFile = "/tmp/CUSTOMER.xml";
	private static String accountsFile = "/tmp/ACCOUNT.xml";
	private static String subscriptionsFile = "/tmp/SUB.xml";
	private static String customerBrand = "OBS";
	private static String customerCategory = "CLIENT";
	private static String serviceCode = "FCAV1_ORGA";
	private static String creditCategory = "VIP";
	private static String offerCode = "FCA_V1";

	/***********************************************************************/

	/**
	 * @param args an array of string argument for the program
	 */
	public static void main(String[] args) {
		System.out.println("Start import...");
		
		try {
			Sellers sellers = new Sellers();
			Seller seller = new Seller();
			seller.setCode("OBS");
			seller.setDescription("french seller");
			seller.setTradingCountryCode("FR");
			seller.setTradingCurrencyCode("EUR");
			seller.setTradingLanguageCode("FRA");
			sellers.getSeller().add(seller);
			Customers customers = new Customers();
			seller.setCustomers(customers);

			BillingAccounts billingAccounts = new BillingAccounts();

			ServiceInstance serviceInstance = new ServiceInstance();
			serviceInstance.setCode(serviceCode);
			serviceInstance.setSubscriptionDate("2014-02-20");
			Status status = new Status();
			status.setDate("2014-02-20");
			status.setValue("ACTIVE");
			serviceInstance.setStatus(status);
			serviceInstance.setQuantity("1");

			Subscriptions subscriptions = new Subscriptions();

			int j = 1;
			int bcPerBC = count / BC_NUMBER;
			int bcCounter = bcPerBC;

			String billingCycle = "";
			for (int i = startIndex; i <= count; i++) {
				if (i == startIndex) {
					billingCycle = billingCyclePrefix + "_" + j;
					j++;
				} else if (i == startIndex + bcCounter) {
					billingCycle = billingCyclePrefix + "_" + j;
					j++;
					bcCounter = bcCounter + bcPerBC;
				}
				// xml of customers
				Customer customer = new Customer();
				customer.setCode("CUST" + i);
				customer.setCustomerCategory(customerCategory);
				customer.setCustomerBrand(customerBrand);
				customer.setDesCustomer("customer " + i);
				customers.getCustomer().add(customer);

				CustomerAccounts customerAccounts = new CustomerAccounts();
				CustomerAccount ca = new CustomerAccount();
				ca.setCode("CA" + i);
				ca.setCreditCategory(creditCategory);
				ca.setExternalRef1("ORCC25P" + i);
				ca.setTradingCurrencyCode("EUR");
				Name name = new Name();
				name.setFirstName("firstName" + i);
				name.setLastName("lastName" + i);
				name.setTitle("SA");
				ca.setName(name);
				Address address = new Address();
				address.setAddress1("Porte Gauche");
				address.setCity("La Defense");
				address.setCountry("France");
				ca.setAddress(address);
				ca.setPaymentMethod("DIRECTDEBIT");
				customerAccounts.getCustomerAccount().add(ca);
				customer.setCustomerAccounts(customerAccounts);

				// xml of billingAccounts

				BillingAccount ba = new BillingAccount();
				ba.setCustomerAccountId("CA" + i);
				ba.setCode("BA" + i);
				ba.setPaymentMethod("CHECK");
				ba.setBillingCycle(billingCycle);
				ba.setSubscriptionDate("2014-02-20");
				ba.setExternalRef1("ORCC25" + i);
				ba.setTradingCountryCode("FR");
				ba.setTradingLanguageCode("FRA");

				org.meveo.model.jaxb.account.Name nameBa = new org.meveo.model.jaxb.account.Name();
				nameBa.setFirstName("firstName" + i);
				nameBa.setLastName("lastName" + i);
				nameBa.setTitle("SA");
				ba.setName(nameBa);
				org.meveo.model.jaxb.account.Address addressBa = new org.meveo.model.jaxb.account.Address();
				addressBa.setAddress1("Porte Gauche");
				addressBa.setZipCode("92000");
				addressBa.setCity("la Defense");
				addressBa.setCountry("France");
				ba.setAddress(addressBa);
				ba.setElectronicBilling("0");
				BankCoordinates bankCoordinate = new BankCoordinates(); 
				bankCoordinate.setKey("");
				bankCoordinate.setAccountName("xx");
				ba.setBankCoordinates(bankCoordinate);
				billingAccounts.getBillingAccount().add(ba);

				UserAccounts usersAccounts = new UserAccounts();
				UserAccount userAccount = new UserAccount();
				userAccount.setCode("UA" + i);
				userAccount.setSubscriptionDate("2014-02-20");
				userAccount.setExternalRef1("25001-0" + i);

				org.meveo.model.jaxb.account.Name nameUA = new org.meveo.model.jaxb.account.Name();
				nameUA.setTitle("M");
				nameUA.setFirstName("firstName" + i);
				nameUA.setLastName("lastName" + i);
				userAccount.setName(nameUA);

				org.meveo.model.jaxb.account.Address addressUA = new org.meveo.model.jaxb.account.Address();
				addressUA.setAddress1("Porte Gauche");
				addressUA.setZipCode("92000" + i);
				addressUA.setCity("la Defense");
				addressUA.setCountry("France");
				userAccount.setAddress(addressUA);

				ba.setUserAccounts(usersAccounts);
				usersAccounts.getUserAccount().add(userAccount);

				// XML subscriptions
				Subscription sub = new Subscription();
				sub.setCode("SUB" + i);
				sub.setUserAccountId("UA" + i);
				sub.setOfferCode(offerCode);
				sub.setSubscriptionDate("2014-02-20");
				Status statuSub = new Status();
				statuSub.setDate("2014-02-20");
				statuSub.setValue("ACTIVE");
				sub.setStatus(statuSub);
				Services services = new Services();
				services.getServiceInstance().add(serviceInstance);
				sub.setServices(services);
				subscriptions.getSubscription().add(sub);

				Accesses accessPoints = new Accesses();
				sub.setAccesses(accessPoints);

				// check opencell-admin.properties for meveo.dateFormat, which is
				// the default
				Access access1 = new Access();
				access1.setAccessUserId("MSISDN" + i);
                access1.setStartDate(DateUtils.formatDateWithPattern(new Date(), "dd/MM/yyyy"));
				access1.setEndDate(DateUtils.formatDateWithPattern(
						DateUtils.addDaysToDate(new Date(), 7), "dd/MM/yyyy"));
				accessPoints.getAccess().add(access1);

				Access access2 = new Access();
				access2.setAccessUserId("IMSI" + i);
				access1.setStartDate(DateUtils.formatDateWithPattern(
						new Date(), "dd/MM/yyyy"));
				access1.setEndDate(DateUtils.formatDateWithPattern(
						DateUtils.addDaysToDate(new Date(), 7), "dd/MM/yyyy"));
				accessPoints.getAccess().add(access2);
			}

			JAXBUtils.marshaller(sellers, new File(customersFile));
			JAXBUtils.marshaller(billingAccounts, new File(accountsFile));
			JAXBUtils.marshaller(subscriptions, new File(subscriptionsFile));

			System.out.println("Import completed successfully.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
