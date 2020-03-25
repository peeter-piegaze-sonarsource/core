@full
Feature: Custom Fields in Filters - Setup Test Data

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
      | jsonFile                                                                                              | title                            | dto                        | api                                             | action         | statusCode | status  | errorCode | message | entity | expected |
      | scenarios/full/00008-customFields-in-filters/01-setup-test-data/create-test-subscription.json         | Create Test Subscription         | SubscriptionDto            | /billing/subscription/createOrUpdate            | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00008-customFields-in-filters/01-setup-test-data/create-access.json                    | Create Access                    | AccessDto                  | /account/access/createOrUpdate                  | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00008-customFields-in-filters/01-setup-test-data/crete-billingAccount.json             | Create Billing Account           | BillingAccountDto          | /account/billingAccount/createOrUpdate          | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00008-customFields-in-filters/01-setup-test-data/create-pricePlan-recurring.json       | Create priceplan recurring       | PricePlanMatrixDto         | /catalog/pricePlan/createOrUpdate               | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00008-customFields-in-filters/01-setup-test-data/create-pricePlan-oneshot.json         | Create priceplan OneShot         | PricePlanMatrixDto         | /catalog/pricePlan/createOrUpdate               | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00008-customFields-in-filters/01-setup-test-data/create-pricePlan-usage.json           | Create priceplan usage           | PricePlanMatrixDto         | /catalog/pricePlan/createOrUpdate               | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00008-customFields-in-filters/01-setup-test-data/create-recurring-charge-template.json | Create recurring charge template | RecurringChargeTemplateDto | /catalog/recurringChargeTemplate/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
