@full
Feature: Charging and invoicing with discounts

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
      | jsonFile                                                                                                                                                                 | title                                                | dto                          | api                                                         | action         | statusCode | status  | errorCode | message | entity | expected |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/05-charging-and-invoicing-with-discounts/recurring-rating-job.json                               | RecurringRatingJob                                   |                              | /job/execute                                                | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/05-charging-and-invoicing-with-discounts/create-invoice-subCategory_1.json                       | Create Invoice sub category 1                        | InvoiceSubCategoryDto        | /invoiceSubCategory/createOrUpdate                          | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/05-charging-and-invoicing-with-discounts/create-invoice-subCategory_2.json                       | Create Invoice sub category 2                        | InvoiceSubCategoryDto        | /invoiceSubCategory/createOrUpdate                          | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/05-charging-and-invoicing-with-discounts/create-invoice-subCategory_3.json                       | Create Invoice sub category 3                        | InvoiceSubCategoryDto        | /invoiceSubCategory/createOrUpdate                          | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/05-charging-and-invoicing-with-discounts/create-invoice-subCategory-country-1.json               | Create invoice sub category country 1                | InvoiceSubCategoryCountryDto | /invoiceSubCategoryCountry/createOrUpdate                   | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/05-charging-and-invoicing-with-discounts/create-invoice-subCategory-country-2.json               | Create invoice sub category country 2                | InvoiceSubCategoryCountryDto | /invoiceSubCategoryCountry/createOrUpdate                   | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/05-charging-and-invoicing-with-discounts/create-invoice-subCategory-country-3.json               | Create invoice sub category country 3                | InvoiceSubCategoryCountryDto | /invoiceSubCategoryCountry/createOrUpdate                   | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/05-charging-and-invoicing-with-discounts/create-oneshot.json                                     | Create one-shot                                      | OneShotChargeTemplateDto     | /catalog/oneShotChargeTemplate                              | Create         |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/05-charging-and-invoicing-with-discounts/create-recurring.json                                   | Create recurring                                     | RecurringChargeTemplateDto   | /catalog/recurringChargeTemplate                            | Create         |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/05-charging-and-invoicing-with-discounts/create-usage.json                                       | Create usage                                         | UsageChargeTemplateDto       | /catalog/usageChargeTemplate                                | Create         |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/05-charging-and-invoicing-with-discounts/create-product-charge.json                              | Create product charge                                | ProductChargeTemplateDto     | /catalogManagement/productChargeTemplate/createOrUpdate     | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/05-charging-and-invoicing-with-discounts/create-service.json                                     | Create service                                       | ServiceTemplateDto           | /catalog/serviceTemplate/createOrUpdate                     | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/05-charging-and-invoicing-with-discounts/create-offer.json                                       | Create offer                                         | OfferTemplateDto             | /catalog/offerTemplate/createOrUpdate                       | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/05-charging-and-invoicing-with-discounts/create-product.json                                     | Create product                                       | ProductTemplateDto           | /catalogManagement/productTemplate/createOrUpdate           | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/05-charging-and-invoicing-with-discounts/create-pricePlan-oneshot.json                           | Create priceplan one-shot                            | PricePlanMatrixDto           | /catalog/pricePlan/createOrUpdate                           | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/05-charging-and-invoicing-with-discounts/create-pricePlan-recurring.json                         | Create priceplan recurring                           | PricePlanMatrixDto           | /catalog/pricePlan/createOrUpdate                           | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/05-charging-and-invoicing-with-discounts/create-pricePlan-usage.json                             | Create priceplan usage                               | PricePlanMatrixDto           | /catalog/pricePlan/createOrUpdate                           | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/05-charging-and-invoicing-with-discounts/create-pricePlan-product.json                           | Create priceplan product                             | PricePlanMatrixDto           | /catalog/pricePlan/createOrUpdate                           | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/05-charging-and-invoicing-with-discounts/create-discountPlan-one-subCategory.json                | Create DiscountPlan - one subcategory                | DiscountPlanDto              | /catalog/discountPlan                                       | Create         |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/05-charging-and-invoicing-with-discounts/create-discountPlanItem-one-subCategory.json            | Create DiscountPlanItem - one subcategory            | DiscountPlanItemDto          | /catalog/discountPlanItem                                   | Create         |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/05-charging-and-invoicing-with-discounts/create-discountPlan-all-categories.json                 | Create DiscountPlan - all categories                 | DiscountPlanDto              | /catalog/discountPlan                                       | Create         |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/05-charging-and-invoicing-with-discounts/create-discountPlanItem-all-categories.json             | Create DiscountPlanItem - all categories             | DiscountPlanItemDto          | /catalog/discountPlanItem                                   | Create         |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/05-charging-and-invoicing-with-discounts/create-customer-hierarchy-no-discount.json              | Create customer hierarchy - no discount              | CRMAccountHierarchyDto       | /account/accountHierarchy/createOrUpdateCRMAccountHierarchy | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/05-charging-and-invoicing-with-discounts/create-customer-hierarchy-discount-one-subCategory.json | Create customer hierarchy - discount one subcategory | CRMAccountHierarchyDto       | /account/accountHierarchy/createOrUpdateCRMAccountHierarchy | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/05-charging-and-invoicing-with-discounts/create-customer-hierarchy-discount-all-categories.json  | Create customer hierarchy - discount all categories  | CRMAccountHierarchyDto       | /account/accountHierarchy/createOrUpdateCRMAccountHierarchy | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/05-charging-and-invoicing-with-discounts/apply-product-on-UA-no-discount.json                    | Apply product on UA - no discount                    | ApplyProductRequestDto       | /account/userAccount/applyProduct                           | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/05-charging-and-invoicing-with-discounts/apply-product-on-UA-discount-one-subCategory.json       | Apply product on UA - discount one subcategory       | ApplyProductRequestDto       | /account/userAccount/applyProduct                           | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/05-charging-and-invoicing-with-discounts/apply-product-on-UA-discount-all-categories.json        | Apply product on UA - discount all categories        | ApplyProductRequestDto       | /account/userAccount/applyProduct                           | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/05-charging-and-invoicing-with-discounts/create-subscription-no-discount.json                    | Create subscription - no discount                    | SubscriptionDto              | /billing/subscription/createOrUpdate                        | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/05-charging-and-invoicing-with-discounts/activate-service-no-discount.json                       | Activate services - no discount                      | ActivateServicesRequestDto   | /billing/subscription/activateServices                      | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/05-charging-and-invoicing-with-discounts/create-accessPoint-no-discount.json                     | Create AccessPoint - no discount                     | AccessDto                    | /account/access/createOrUpdate                              | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/05-charging-and-invoicing-with-discounts/create-subscription-discount-one-subCategory.json       | Create subscription - discount one subcategory       | SubscriptionDto              | /billing/subscription/createOrUpdate                        | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/05-charging-and-invoicing-with-discounts/activate-service-discont-one-subCategory.json           | Activate services - discount one subcategory         | ActivateServicesRequestDto   | /billing/subscription/activateServices                      | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/05-charging-and-invoicing-with-discounts/create-accessPoint-one-subCategory.json                 | Create AccessPoint - discount one subcategory        | AccessDto                    | /account/access/createOrUpdate                              | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/05-charging-and-invoicing-with-discounts/create-subscription-discount-all.json                   | Create subscription - discount all                   | SubscriptionDto              | /billing/subscription/createOrUpdate                        | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/05-charging-and-invoicing-with-discounts/activate-service-discount-all.json                      | Activate services - discount all                     | ActivateServicesRequestDto   | /billing/subscription/activateServices                      | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/05-charging-and-invoicing-with-discounts/create-accessPoint-discount-all.json                    | Create AccessPoint - discount all                    | AccessDto                    | /account/access/createOrUpdate                              | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/05-charging-and-invoicing-with-discounts/charge-cdr-no-discount.json                             | Charge cdr - no discount                             | String                       | /billing/mediation/chargeCdr                                | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/05-charging-and-invoicing-with-discounts/charge-cdr-discount-one-subCategory.json                | Charge cdr - discount one subcategory                | String                       | /billing/mediation/chargeCdr                                | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/05-charging-and-invoicing-with-discounts/charge-cdr-discount-all.json                            | Charge cdr - discount all                            | String                       | /billing/mediation/chargeCdr                                | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/05-charging-and-invoicing-with-discounts/generate-no-discount.json                               | GenerateInvoice - no discount                        |                              | /invoice/generateInvoice                                    | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/05-charging-and-invoicing-with-discounts/generate-invoice-discount-one-subCategory.json          | GenerateInvoice - discount one subcategory           |                              | /invoice/generateInvoice                                    | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/05-charging-and-invoicing-with-discounts/generate-invoice-discount-all.json                      | GenerateInvoice - discount all                       |                              | /invoice/generateInvoice                                    | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/05-charging-and-invoicing-with-discounts/get-invoiceXML-no-discount.json                         | Get Invoice XML - no discount                        | String                       | /invoice/getXMLInvoiceWithType                              | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/05-charging-and-invoicing-with-discounts/get-invoiceXML-discount-one-subCategory.json            | Get Invoice XML - discount one subcategory           | String                       | /invoice/getXMLInvoiceWithType                              | POST           |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/05-charging-and-invoicing-with-discounts/get-invoiceXML-discount-all.json                        | Get Invoice XML - discount all                       | String                       | /invoice/getXMLInvoiceWithType                              | POST           |        200 | SUCCESS |           |         |        |          |
