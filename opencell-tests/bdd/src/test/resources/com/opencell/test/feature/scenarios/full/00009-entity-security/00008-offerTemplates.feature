@full
Feature: Entity Security - Offer templates

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
      | jsonFile                                                                                                     | title                                               | dto     | api                                                                | action | statusCode | status  | errorCode                              | message                                  | entity        | expected                                                                                                              |
      | scenarios/full/00009-entity-security/08-offerTemplates/restore-user-to-default.json                          | Restore User to default                             | UserDto | /user                                                              | PUT    |        200 | SUCCESS |                                        |                                          |               |                                                                                                                       |
      | scenarios/full/00009-entity-security/08-offerTemplates/list-offerTemplate-show-all.json                      | List offerTemplates - show all                      |         | /catalog/offerTemplate/list?limit=500                              | GET    |        200 | SUCCESS |                                        |                                          | offerTemplate | scenarios/full/00009-entity-security/08-offerTemplates/list-offerTemplate-show-all.json                               |
      | scenarios/full/00009-entity-security/08-offerTemplates/update-user-to-allow-access-to-SELLER_A-only.json     | Update User to Allow Access to SELLER_A only        | UserDto | /user                                                              | PUT    |        200 | SUCCESS |                                        |                                          |               |                                                                                                                       |
      | scenarios/full/00009-entity-security/08-offerTemplates/list-offerTemplate-show_A.json                        | List offerTemplates - show A                        |         | /catalog/offerTemplate/list?limit=500                              | GET    |        200 | SUCCESS |                                        |                                          | offerTemplate | scenarios/full/00009-entity-security/08-offerTemplates/list-offerTemplate-show_A.json                                 |
      | scenarios/full/00009-entity-security/08-offerTemplates/find-offerTemplate-no_seller.json                     | Find offerTemplate - no seller                      |         | /catalog/offerTemplate?offerTemplateCode=RS_FULL_18_OFFER_NOSELLER | GET    |        200 | SUCCESS |                                        |                                          | offerTemplate | scenarios/full/00009-entity-security/08-offerTemplates/find-offerTemplate-no_seller.json                              |
      | scenarios/full/00009-entity-security/08-offerTemplates/find-offerTemplate-A.json                             | Find offerTemplate - A                              |         | /catalog/offerTemplate?offerTemplateCode=RS_FULL_18_OFFER_SELLER_A | GET    |        200 | SUCCESS |                                        |                                          | offerTemplate | scenarios/full/00009-entity-security/08-offerTemplates/find-offerTemplate-A.json                                      |
      | scenarios/full/00009-entity-security/08-offerTemplates/find-offerTemplate-B.json                             | Find offerTemplate - B fails                        |         | /catalog/offerTemplate?offerTemplateCode=RS_FULL_18_OFFER_SELLER_B | GET    |        401 | FAIL    | AUTHENTICATION_AUTHORIZATION_EXCEPTION | Access to entity details is not allowed. |               |                                                                                                                       |
      | scenarios/full/00009-entity-security/08-offerTemplates/update-user-to-allow-access-to-SELLER_B-only.json     | Update User to Allow Access to SELLER_B only        | UserDto | /user                                                              | PUT    |        200 | SUCCESS |                                        |                                          |               |                                                                                                                       |
      | scenarios/full/00009-entity-security/08-offerTemplates/list-offerTemplate-show-B_and_C.json                  | List offerTemplates - show B and C                  |         | /catalog/offerTemplate/list?limit=500                              | GET    |        200 | SUCCESS |                                        |                                          | offerTemplate | scenarios/full/00009-entity-security/08-offerTemplates/list-offerTemplate-show-B_and_C.json                           |
      | scenarios/full/00009-entity-security/08-offerTemplates/find-offerTemplate-B.json                             | Find offerTemplate - B 1                            |         | /catalog/offerTemplate?offerTemplateCode=RS_FULL_18_OFFER_SELLER_B | GET    |        200 | SUCCESS |                                        |                                          | offerTemplate | scenarios/full/00009-entity-security/08-offerTemplates/find-offerTemplate-B.json                                      |
      | scenarios/full/00009-entity-security/08-offerTemplates/find-offerTemplate-C.json                             | Find offerTemplate - C                              |         | /catalog/offerTemplate?offerTemplateCode=RS_FULL_18_OFFER_SELLER_C | GET    |        200 | SUCCESS |                                        |                                          | offerTemplate | scenarios/full/00009-entity-security/08-offerTemplates/find-offerTemplate-C.json                                      |
      | scenarios/full/00009-entity-security/08-offerTemplates/find-offerTemplate-A.json                             | Find offerTemplate - A  fails                       |         | /catalog/offerTemplate?offerTemplateCode=RS_FULL_18_OFFER_SELLER_A | GET    |        401 | FAIL    | AUTHENTICATION_AUTHORIZATION_EXCEPTION | Access to entity details is not allowed. |               |                                                                                                                       |
      | scenarios/full/00009-entity-security/08-offerTemplates/update-user-to-allow-access-to-SELLER_C-only.json     | Update User to Allow Access to SELLER_C only        | UserDto | /user                                                              | PUT    |        200 | SUCCESS |                                        |                                          |               |                                                                                                                       |
      | scenarios/full/00009-entity-security/08-offerTemplates/list-offerTemplate-show_C.json                        | List offerTemplates - show C                        |         | /catalog/offerTemplate/list?limit=500                              | GET    |        200 | SUCCESS |                                        |                                          | offerTemplate | scenarios/full/00009-entity-security/08-offerTemplates/list-offerTemplate-show_C.json                                 |
      | scenarios/full/00009-entity-security/08-offerTemplates/find-offerTemplate-C.json                             | Find offerTemplate - C 1                            |         | /catalog/offerTemplate?offerTemplateCode=RS_FULL_18_OFFER_SELLER_C | GET    |        200 | SUCCESS |                                        |                                          | offerTemplate | scenarios/full/00009-entity-security/08-offerTemplates/find-offerTemplate-C.json                                      |
      | scenarios/full/00009-entity-security/08-offerTemplates/find-offerTemplate-A.json                             | Find offerTemplate - A  fails 1                     |         | /catalog/offerTemplate?offerTemplateCode=RS_FULL_18_OFFER_SELLER_A | GET    |        401 | FAIL    | AUTHENTICATION_AUTHORIZATION_EXCEPTION | Access to entity details is not allowed. |               |                                                                                                                       |
      | scenarios/full/00009-entity-security/08-offerTemplates/find-offerTemplate-B.json                             | Find offerTemplate - B fails 1                      |         | /catalog/offerTemplate?offerTemplateCode=RS_FULL_18_OFFER_SELLER_B | GET    |        401 | FAIL    | AUTHENTICATION_AUTHORIZATION_EXCEPTION | Access to entity details is not allowed. |               |                                                                                                                       |
      | scenarios/full/00009-entity-security/08-offerTemplates/restore-user-to-default.json                          | Restore User to default.                            | UserDto | /user                                                              | PUT    |        200 | SUCCESS |                                        |                                          |               |                                                                                                                       |
      | scenarios/full/00009-entity-security/08-offerTemplates/list-offerTemplate-by-filter-show-only-C_and_A.json   | List offerTemplates by filter - show only C and A   |         | /catalog/offerTemplate/list                                        | POST   |        200 | SUCCESS |                                        |                                          | offerTemplate | scenarios/full/00009-entity-security/08-offerTemplates/list-offerTemplate-by-filter-show-only-C_and_A-expected.json   |
      | scenarios/full/00009-entity-security/08-offerTemplates/list-offerTemplate-by-filter-do-not-show-C_and_A.json | List offerTemplates by filter - do not show C and A |         | /catalog/offerTemplate/list                                        | POST   |        200 | SUCCESS |                                        |                                          | offerTemplate | scenarios/full/00009-entity-security/08-offerTemplates/list-offerTemplate-by-filter-do-not-show-C_and_A-expected.json |
