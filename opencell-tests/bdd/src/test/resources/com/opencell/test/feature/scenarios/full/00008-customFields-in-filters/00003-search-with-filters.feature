@full
Feature: Custom Fields in Filters - Search with Filters

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
      | jsonFile                                                                                                | title                            | dto       | api                                        | action | statusCode | status  | errorCode | message | entity | expected |
      | scenarios/full/00008-customFields-in-filters/03-search-with-filters/search-pp-by-date-date.json         | Search PP by date - date         | FilterDto | /filteredList/listByFilter?from=0&size=100 | POST   |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00008-customFields-in-filters/03-search-with-filters/search-pp-by-code-string.json       | Search PP by code - string       | FilterDto | /filteredList/listByFilter?from=0&size=100 | POST   |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00008-customFields-in-filters/03-search-with-filters/search-pp-by-amount-decimal.json    | Search PP by amount - decimal    | FilterDto | /filteredList/listByFilter?from=0&size=100 | POST   |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00008-customFields-in-filters/03-search-with-filters/search-pp-by-age-long.json          | Search PP by age - long          | FilterDto | /filteredList/listByFilter?from=0&size=100 | POST   |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00008-customFields-in-filters/03-search-with-filters/search-rct-by-duration-integer.json | Search RCT by duration - integer | FilterDto | /filteredList/listByFilter?from=0&size=100 | POST   |        200 | SUCCESS |           |         |        |          |
      | scenarios/full/00008-customFields-in-filters/03-search-with-filters/search-rct-by-prorata-boolean.json  | Search RCT by prorata - boolean  | FilterDto | /filteredList/listByFilter?from=0&size=100 | POST   |        200 | SUCCESS |           |         |        |          |
