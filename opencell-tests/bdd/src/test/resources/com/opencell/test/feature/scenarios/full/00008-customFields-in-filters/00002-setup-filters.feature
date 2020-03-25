@full
Feature: Custom Fields in Filters - Setup Filters

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
      | jsonFile                                                                                                               | title                                               | dto       | api                    | action         | statusCode | status  | errorCode | message | entity | expected |
      | scenarios/full/00008-customFields-in-filters/02-setup-filters/create-access-filter.json                                | Create Access Filter                                | FilterDto | /filter/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00008-customFields-in-filters/02-setup-filters/create-pricePlan-filter-rating-date.json                 | Create Price Plan Filter - Rating date              | FilterDto | /filter/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00008-customFields-in-filters/02-setup-filters/create-pricePlan-filter-by-code.json                     | Create Price Plan Filter by Code                    | FilterDto | /filter/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00008-customFields-in-filters/02-setup-filters/create-pricePlan-filter-by-amount.json                   | Create Price Plan Filter by Amount                  | FilterDto | /filter/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00008-customFields-in-filters/02-setup-filters/create-pricePlan-filter-by-age.json                      | Create Price Plan Filter Age                        | FilterDto | /filter/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00008-customFields-in-filters/02-setup-filters/create-billingAccount-filter.json                        | Create Billing Account Filter                       | FilterDto | /filter/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00008-customFields-in-filters/02-setup-filters/create-billingAccount-filter-enum.json                   | Create Billing Account Filter enum                  | FilterDto | /filter/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00008-customFields-in-filters/02-setup-filters/create-recurring-charge-template-filter-by-duration.json | Create Recurring charge template Filter by Duration | FilterDto | /filter/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00008-customFields-in-filters/02-setup-filters/create-recurring-charge-template-filter-by-prorata.json  | Create Recurring charge template Filter by Prorata  | FilterDto | /filter/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |           |         |        |          |
