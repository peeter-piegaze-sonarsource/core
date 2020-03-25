@full
Feature: Custom Fields in Filters - Test Data Cleanup

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
      | jsonFile                                                                                                | title                            | dto | api                                                                      | action | statusCode | status  | errorCode | message | entity | expected |
      | scenarios/full/00008-customFields-in-filters/04-test-data-cleanup/remove-recurring-charge-template.json | Remove recurring charge template |     | /catalog/recurringChargeTemplate/RS_FULL_611_REC100                      | DEL    |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00008-customFields-in-filters/04-test-data-cleanup/remove-access.json                    | Remove Access                    |     | /account/access/RS_BASE_TEST_ACCP/RS_BASE_TEST_SUB/2016-06-01/2016-06-30 | DEL    |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00008-customFields-in-filters/04-test-data-cleanup/remove-billingAccount.json            | Remove Billing Account           |     | /account/billingAccount/RS_BASE_TEST_BA                                  | DEL    |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00008-customFields-in-filters/04-test-data-cleanup/remove-pricePlan-recurring.json       | Remove priceplan recurring       |     | /catalog/pricePlan/RS_BASE_TEST_REC1                                     | DEL    |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00008-customFields-in-filters/04-test-data-cleanup/remove-pricePlan-oneshot.json         | Remove priceplan OneShot         |     | /catalog/pricePlan/RS_BASE_TEST_OS1                                      | DEL    |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00008-customFields-in-filters/04-test-data-cleanup/remove-pricePlan-usage.json           | Remove priceplan usage           |     | /catalog/pricePlan/RS_BASE_TEST_USAGE1                                   | DEL    |        200 | SUCCESS |           |         |        |          |
