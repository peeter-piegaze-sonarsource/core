@full @ignore
Feature: Subscription workflow - With multiple invoice sub category country

  @admin
  Scenario Outline: <title>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"
    And The entity "<entity>" matches "<expected>"

    Examples: 
      | jsonFile                                                                                                                                                | title                                                                | dto                                  | api                                                         | action         | statusCode | status  | errorCode | message | entity | expected |
      | scenarios/full/00004-subscription-workflow/08-with-multiple-invoice-subCategory-country/create-tax.json                                                 | Create tax                                                           | TaxDto                               | /tax/createOrUpdate                                         | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/08-with-multiple-invoice-subCategory-country/create-invoice-subCategory.json                                 | Create Invoice sub category                                          | InvoiceSubCategoryDto                | /invoiceSubCategory/createOrUpdate                          | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      #| scenarios/full/00004-subscription-workflow/08-with-multiple-invoice-subCategory-country/create-invoice-subCategory-country_1-tax0-no-validity-date.json | Create invoice sub category country 1 - tax0 - us - no validity date | InvoiceSubCategoryCountryDto         | /invoiceSubCategoryCountry/createOrUpdate                   | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      #| scenarios/full/00004-subscription-workflow/08-with-multiple-invoice-subCategory-country/create-invoice-subCategory-country_2-tax0-us.json               | Create invoice sub category country 2 - tax0 - us                    | InvoiceSubCategoryCountryDto         | /invoiceSubCategoryCountry/createOrUpdate                   | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      #| scenarios/full/00004-subscription-workflow/08-with-multiple-invoice-subCategory-country/create-invoice-subCategory-country_3-tax0-us.json               | Create invoice sub category country 3 - tax0 - us                    | InvoiceSubCategoryCountryDto         | /invoiceSubCategoryCountry/createOrUpdate                   | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      #| scenarios/full/00004-subscription-workflow/08-with-multiple-invoice-subCategory-country/create-invoice-subCategory-country_4-tax0-us.json               | Create invoice sub category country 4 - tax0 - us                    | InvoiceSubCategoryCountryDto         | /invoiceSubCategoryCountry/createOrUpdate                   | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/08-with-multiple-invoice-subCategory-country/create-onshot.json                                              | Create one-shot                                                      | OneShotChargeTemplateDto             | /catalog/oneShotChargeTemplate/createOrUpdate               | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/08-with-multiple-invoice-subCategory-country/create-recurring.json                                           | Create recurring                                                     | RecurringChargeTemplateDto           | /catalog/recurringChargeTemplate/createOrUpdate             | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/08-with-multiple-invoice-subCategory-country/create-usage.json                                               | Create usage                                                         | UsageChargeTemplateDto               | /catalog/usageChargeTemplate/createOrUpdate                 | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/08-with-multiple-invoice-subCategory-country/create-service.json                                             | Create service                                                       | ServiceTemplateDto                   | /catalog/serviceTemplate/createOrUpdate                     | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/08-with-multiple-invoice-subCategory-country/create-offer.json                                               | Create offer                                                         | OfferTemplateDto                     | /catalog/offerTemplate/createOrUpdate                       | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/08-with-multiple-invoice-subCategory-country/create-product-charge.json                                      | Create product charge                                                | ProductChargeTemplateDto             | /catalogManagement/productChargeTemplate/createOrUpdate     | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/08-with-multiple-invoice-subCategory-country/create-product.json                                             | Create product                                                       | ProductTemplateDto                   | /catalogManagement/productTemplate/createOrUpdate           | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/08-with-multiple-invoice-subCategory-country/create-pricePlan-usage.json                                     | Create priceplan usage                                               | PricePlanMatrixDto                   | /catalog/pricePlan/createOrUpdate                           | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/08-with-multiple-invoice-subCategory-country/create-pricePlan-recurring.json                                 | Create priceplan recurring                                           | PricePlanMatrixDto                   | /catalog/pricePlan/createOrUpdate                           | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/08-with-multiple-invoice-subCategory-country/create-pricePlan-oneshot.json                                   | Create priceplan one-shot                                            | PricePlanMatrixDto                   | /catalog/pricePlan/createOrUpdate                           | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/08-with-multiple-invoice-subCategory-country/create-pricePlan-product.json                                   | Create priceplan product                                             | PricePlanMatrixDto                   | /catalog/pricePlan/createOrUpdate                           | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/08-with-multiple-invoice-subCategory-country/create-customer-hierarchy.json                                  | Create customer hierarchy                                            | CRMAccountHierarchyDto               | /account/accountHierarchy/createOrUpdateCRMAccountHierarchy | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/08-with-multiple-invoice-subCategory-country/apply-product-to-UA_1.json                                      | Apply product to UA 1                                                | ApplyProductRequestDto               | /account/userAccount/applyProduct                           | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/08-with-multiple-invoice-subCategory-country/apply-product-to-UA_2.json                                      | Apply product to UA 2                                                | ApplyProductRequestDto               | /account/userAccount/applyProduct                           | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/08-with-multiple-invoice-subCategory-country/apply-product-to-UA_3.json                                      | Apply product to UA 3                                                | ApplyProductRequestDto               | /account/userAccount/applyProduct                           | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/08-with-multiple-invoice-subCategory-country/apply-product-to-UA_4.json                                      | Apply product to UA 4                                                | ApplyProductRequestDto               | /account/userAccount/applyProduct                           | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/08-with-multiple-invoice-subCategory-country/create-subscription.json                                        | Create subscription                                                  | SubscriptionDto                      | /billing/subscription                                       | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/08-with-multiple-invoice-subCategory-country/apply-oneshot-chargeInstance_1.json                             | Apply one shot charge instance 1                                     | ApplyOneShotChargeInstanceRequestDto | /billing/subscription/applyOneShotChargeInstance            | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/08-with-multiple-invoice-subCategory-country/apply-oneshot-chargeInstance_2.json                             | Apply one shot charge instance 2                                     | ApplyOneShotChargeInstanceRequestDto | /billing/subscription/applyOneShotChargeInstance            | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/08-with-multiple-invoice-subCategory-country/apply-oneshot-chargeInstance_3.json                             | Apply one shot charge instance 3                                     | ApplyOneShotChargeInstanceRequestDto | /billing/subscription/applyOneShotChargeInstance            | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/08-with-multiple-invoice-subCategory-country/apply-oneshot-chargeInstance_4.json                             | Apply one shot charge instance 4                                     | ApplyOneShotChargeInstanceRequestDto | /billing/subscription/applyOneShotChargeInstance            | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00004-subscription-workflow/08-with-multiple-invoice-subCategory-country/activate-services.json                                          | Activate services                                                    | ActivateServicesRequestDto           | /billing/subscription/activateServices                      | POST           |        200 | SUCCESS |           |         |        |          |
