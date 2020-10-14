@full
Feature: Entity Security - User Account Tests

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
      | jsonFile                                                                                                          | title                                                        | dto     | api                                                          | action | statusCode | status  | errorCode                              | message                                  | entity         | expected                                                                                 |
      | scenarios/full/00009-entity-security/06-userAccounts-tests/restore-user-to-default.json                           | Restore User to default                                      | UserDto | /user                                                        | PUT    |        200 | SUCCESS |                                        |                                          |                |                                                                                          |
      | scenarios/full/00009-entity-security/06-userAccounts-tests/find-userAccount-UAA1.json                             | Find User Account UAA1                                       |         | /account/userAccount?userAccountCode=RS_FULL_18_UAA1         | GET    |        200 | SUCCESS |                                        |                                          | userAccount    | scenarios/full/00009-entity-security/06-userAccounts-tests/find-userAccount-UAA1.json    |
      | scenarios/full/00009-entity-security/06-userAccounts-tests/find-userAccount-UAB1.json                             | Find User Account UAB1                                       |         | /account/userAccount?userAccountCode=RS_FULL_18_UAB1         | GET    |        200 | SUCCESS |                                        |                                          | userAccount    | scenarios/full/00009-entity-security/06-userAccounts-tests/find-userAccount-UAB1.json    |
      | scenarios/full/00009-entity-security/06-userAccounts-tests/update-user-to-allow-access-to-SELLER_A-only.json      | Update User to Allow Access to SELLER_A only                 | UserDto | /user                                                        | PUT    |        200 | SUCCESS |                                        |                                          |                |                                                                                          |
      | scenarios/full/00009-entity-security/06-userAccounts-tests/find-userAccount-UAA1.json                             | Find User Account UAA1 still accessible                      |         | /account/userAccount?userAccountCode=RS_FULL_18_UAA1         | GET    |        200 | SUCCESS |                                        |                                          | userAccount    | scenarios/full/00009-entity-security/06-userAccounts-tests/find-userAccount-UAA1.json    |
      | scenarios/full/00009-entity-security/06-userAccounts-tests/find-userAccount-UAB1.json                             | Find User Account UAB1 not accessible                        |         | /account/userAccount?userAccountCode=RS_FULL_18_UAB1         | GET    |        401 | FAIL    | AUTHENTICATION_AUTHORIZATION_EXCEPTION | Access to entity details is not allowed. |                |                                                                                          |
      | scenarios/full/00009-entity-security/06-userAccounts-tests/update-user-to-allow-access-to-SELLER_B-only.json      | Update User to Allow Access to SELLER_B only                 | UserDto | /user                                                        | PUT    |        200 | SUCCESS |                                        |                                          |                |                                                                                          |
      | scenarios/full/00009-entity-security/06-userAccounts-tests/find-userAccount-UAA1.json                             | Find User Account UAA1 not accessible                        |         | /account/userAccount?userAccountCode=RS_FULL_18_UAA1         | GET    |        401 | FAIL    | AUTHENTICATION_AUTHORIZATION_EXCEPTION | Access to entity details is not allowed. |                |                                                                                          |
      | scenarios/full/00009-entity-security/06-userAccounts-tests/find-userAccount-UAB1.json                             | Find User Account UAB1 now accessible                        |         | /account/userAccount?userAccountCode=RS_FULL_18_UAB1         | GET    |        200 | SUCCESS |                                        |                                          | userAccount    | scenarios/full/00009-entity-security/06-userAccounts-tests/find-userAccount-UAB1.json    |
      | scenarios/full/00009-entity-security/06-userAccounts-tests/update-user-to-allow-access-to-CUSTA-only.json         | Update User to Allow Access to CUSTA only                    | UserDto | /user                                                        | PUT    |        200 | SUCCESS |                                        |                                          |                |                                                                                          |
      | scenarios/full/00009-entity-security/06-userAccounts-tests/find-userAccount-UAA1.json                             | Find User Account UAA1 now accessible again                  |         | /account/userAccount?userAccountCode=RS_FULL_18_UAA1         | GET    |        200 | SUCCESS |                                        |                                          | userAccount    | scenarios/full/00009-entity-security/06-userAccounts-tests/find-userAccount-UAA1.json    |
      | scenarios/full/00009-entity-security/06-userAccounts-tests/find-userAccount-UAB1.json                             | Find User Account UAB1 not accessible again                  |         | /account/userAccount?userAccountCode=RS_FULL_18_UAB1         | GET    |        401 | FAIL    | AUTHENTICATION_AUTHORIZATION_EXCEPTION | Access to entity details is not allowed. |                |                                                                                          |
      | scenarios/full/00009-entity-security/06-userAccounts-tests/update-user-to-allow-access-to-CUSTB-only.json         | Update User to Allow Access to CUSTB only                    | UserDto | /user                                                        | PUT    |        200 | SUCCESS |                                        |                                          |                |                                                                                          |
      | scenarios/full/00009-entity-security/06-userAccounts-tests/find-userAccount-UAA1.json                             | Find User Account UAA1 not accessible again                  |         | /account/userAccount?userAccountCode=RS_FULL_18_UAA1         | GET    |        401 | FAIL    | AUTHENTICATION_AUTHORIZATION_EXCEPTION | Access to entity details is not allowed. |                |                                                                                          |
      | scenarios/full/00009-entity-security/06-userAccounts-tests/find-userAccount-UAB1.json                             | Find User Account UAB1 now accessible again                  |         | /account/userAccount?userAccountCode=RS_FULL_18_UAB1         | GET    |        200 | SUCCESS |                                        |                                          | userAccount    | scenarios/full/00009-entity-security/06-userAccounts-tests/find-userAccount-UAB1.json    |
      | scenarios/full/00009-entity-security/06-userAccounts-tests/update-user-to-allow-access-to-CAA1-only.json          | Update User to Allow Access to CAA1 only                     | UserDto | /user                                                        | PUT    |        200 | SUCCESS |                                        |                                          |                |                                                                                          |
      | scenarios/full/00009-entity-security/06-userAccounts-tests/find-userAccount-UAA1.json                             | Find User Account UAA1 now accessible again.                 |         | /account/userAccount?userAccountCode=RS_FULL_18_UAA1         | GET    |        200 | SUCCESS |                                        |                                          | userAccount    | scenarios/full/00009-entity-security/06-userAccounts-tests/find-userAccount-UAA1.json    |
      | scenarios/full/00009-entity-security/06-userAccounts-tests/find-userAccount-UAB1.json                             | Find User Account UAB1 not accessible again.                 |         | /account/userAccount?userAccountCode=RS_FULL_18_UAB1         | GET    |        401 | FAIL    | AUTHENTICATION_AUTHORIZATION_EXCEPTION | Access to entity details is not allowed. |                |                                                                                          |
      | scenarios/full/00009-entity-security/06-userAccounts-tests/update-user-to-allow-access-to-CAB1-only.json          | Update User to Allow Access to CAB1 only                     | UserDto | /user                                                        | PUT    |        200 | SUCCESS |                                        |                                          |                |                                                                                          |
      | scenarios/full/00009-entity-security/06-userAccounts-tests/find-userAccount-UAA1.json                             | Find User Account UAA1 not accessible again.                 |         | /account/userAccount?userAccountCode=RS_FULL_18_UAA1         | GET    |        401 | FAIL    | AUTHENTICATION_AUTHORIZATION_EXCEPTION | Access to entity details is not allowed. |                |                                                                                          |
      | scenarios/full/00009-entity-security/06-userAccounts-tests/find-userAccount-UAB1.json                             | Find User Account UAB1 now accessible again.                 |         | /account/userAccount?userAccountCode=RS_FULL_18_UAB1         | GET    |        200 | SUCCESS |                                        |                                          | userAccount    | scenarios/full/00009-entity-security/06-userAccounts-tests/find-userAccount-UAB1.json    |
      | scenarios/full/00009-entity-security/06-userAccounts-tests/update-user-to-allow-access-to-BAA1-only.json          | Update User to Allow Access to BAA1 only                     | UserDto | /user                                                        | PUT    |        200 | SUCCESS |                                        |                                          |                |                                                                                          |
      | scenarios/full/00009-entity-security/06-userAccounts-tests/find-userAccount-UAA1.json                             | Find User Account UAA1 now accessible again..                |         | /account/userAccount?userAccountCode=RS_FULL_18_UAA1         | GET    |        200 | SUCCESS |                                        |                                          | userAccount    | scenarios/full/00009-entity-security/06-userAccounts-tests/find-userAccount-UAA1.json    |
      | scenarios/full/00009-entity-security/06-userAccounts-tests/find-userAccount-UAB1.json                             | Find User Account UAB1 not accessible again..                |         | /account/userAccount?userAccountCode=RS_FULL_18_UAB1         | GET    |        401 | FAIL    | AUTHENTICATION_AUTHORIZATION_EXCEPTION | Access to entity details is not allowed. |                |                                                                                          |
      | scenarios/full/00009-entity-security/06-userAccounts-tests/update-user-to-allow-access-to-BAB1-only.json          | Update User to Allow Access to BAB1 only                     | UserDto | /user                                                        | PUT    |        200 | SUCCESS |                                        |                                          |                |                                                                                          |
      | scenarios/full/00009-entity-security/06-userAccounts-tests/find-userAccount-UAA1.json                             | Find User Account UAA1 not accessible again..                |         | /account/userAccount?userAccountCode=RS_FULL_18_UAA1         | GET    |        401 | FAIL    | AUTHENTICATION_AUTHORIZATION_EXCEPTION | Access to entity details is not allowed. |                |                                                                                          |
      | scenarios/full/00009-entity-security/06-userAccounts-tests/find-userAccount-UAB1.json                             | Find User Account UAB1 now accessible again..                |         | /account/userAccount?userAccountCode=RS_FULL_18_UAB1         | GET    |        200 | SUCCESS |                                        |                                          | userAccount    | scenarios/full/00009-entity-security/06-userAccounts-tests/find-userAccount-UAB1.json    |
      | scenarios/full/00009-entity-security/06-userAccounts-tests/update-user-to-allow-access-to-UAA1-only.json          | Update User to Allow Access to UAA1 only                     | UserDto | /user                                                        | PUT    |        200 | SUCCESS |                                        |                                          |                |                                                                                          |
      | scenarios/full/00009-entity-security/06-userAccounts-tests/find-userAccount-UAA1.json                             | Find User Account UAA1 now accessible again...               |         | /account/userAccount?userAccountCode=RS_FULL_18_UAA1         | GET    |        200 | SUCCESS |                                        |                                          | userAccount    | scenarios/full/00009-entity-security/06-userAccounts-tests/find-userAccount-UAA1.json    |
      | scenarios/full/00009-entity-security/06-userAccounts-tests/find-userAccount-UAB1.json                             | Find User Account UAB1 not accessible again...               |         | /account/userAccount?userAccountCode=RS_FULL_18_UAB1         | GET    |        401 | FAIL    | AUTHENTICATION_AUTHORIZATION_EXCEPTION | Access to entity details is not allowed. |                |                                                                                          |
      | scenarios/full/00009-entity-security/06-userAccounts-tests/find-billingAccount-BAA1.json                          | Find Billing Account BAA1 not accessible                     |         | /account/billingAccount?billingAccountCode=RS_FULL_18_BAA1   | GET    |        401 | FAIL    | AUTHENTICATION_AUTHORIZATION_EXCEPTION | Access to entity details is not allowed. |                |                                                                                          |
      | scenarios/full/00009-entity-security/06-userAccounts-tests/find-billingAccount-BAB1.json                          | Find Billing Account BAB1 not accessible                     |         | /account/billingAccount?billingAccountCode=RS_FULL_18_BAB1   | GET    |        401 | FAIL    | AUTHENTICATION_AUTHORIZATION_EXCEPTION | Access to entity details is not allowed. |                |                                                                                          |
      | scenarios/full/00009-entity-security/06-userAccounts-tests/update-user-to-allow-access-to-CAA1-only.json          | Find Customer Account CAA1 not accessible                    |         | /account/customerAccount?customerAccountCode=RS_FULL_18_CAA1 | GET    |        401 | FAIL    | AUTHENTICATION_AUTHORIZATION_EXCEPTION | Access to entity details is not allowed. |                |                                                                                          |
      | scenarios/full/00009-entity-security/06-userAccounts-tests/update-user-to-allow-access-to-CAB1-only.json          | Find Customer Account CAB1 not accessible                    |         | /account/customerAccount?customerAccountCode=RS_FULL_18_CAB1 | GET    |        401 | FAIL    | AUTHENTICATION_AUTHORIZATION_EXCEPTION | Access to entity details is not allowed. |                |                                                                                          |
      | scenarios/full/00009-entity-security/06-userAccounts-tests/update-user-to-allow-access-to-CUSTA-only.json         | Find Customer CUSTA not accessible                           |         | /account/customer?customerCode=RS_FULL_18_CUSTA              | GET    |        401 | FAIL    | AUTHENTICATION_AUTHORIZATION_EXCEPTION | Access to entity details is not allowed. |                |                                                                                          |
      | scenarios/full/00009-entity-security/06-userAccounts-tests/update-user-to-allow-access-to-CUSTB-only.json         | Find Customer CUSTB not accessible                           |         | /account/customer?customerCode=RS_FULL_18_CUSTB              | GET    |        401 | FAIL    | AUTHENTICATION_AUTHORIZATION_EXCEPTION | Access to entity details is not allowed. |                |                                                                                          |
      | scenarios/full/00009-entity-security/06-userAccounts-tests/update-user-to-allow-access-to-SELLER_A-only.json      | Find Seller SELLER_A not accessible                          |         | /seller?sellerCode=RS_FULL_18_SELLER_A                       | GET    |        401 | FAIL    | AUTHENTICATION_AUTHORIZATION_EXCEPTION | Access to entity details is not allowed. |                |                                                                                          |
      | scenarios/full/00009-entity-security/06-userAccounts-tests/update-user-to-allow-access-to-SELLER_B-only.json      | Find Seller SELLER_B not accessible                          |         | /seller?sellerCode=RS_FULL_18_SELLER_B                       | GET    |        401 | FAIL    | AUTHENTICATION_AUTHORIZATION_EXCEPTION | Access to entity details is not allowed. |                |                                                                                          |
      | scenarios/full/00009-entity-security/06-userAccounts-tests/update-user-to-allow-access-to-UAB1-only.json          | Update User to Allow Access to UAB1 only                     | UserDto | /user                                                        | PUT    |        200 | SUCCESS |                                        |                                          |                |                                                                                          |
      | scenarios/full/00009-entity-security/06-userAccounts-tests/find-userAccount-UAA1.json                             | Find User Account UAA1 not accessible again...               |         | /account/userAccount?userAccountCode=RS_FULL_18_UAA1         | GET    |        200 | SUCCESS |                                        |                                          |                |                                                                                          |
      | scenarios/full/00009-entity-security/06-userAccounts-tests/find-userAccount-UAB1.json                             | Find User Account UAB1 now accessible again...               |         | /account/userAccount?userAccountCode=RS_FULL_18_UAB1         | GET    |        200 | SUCCESS |                                        |                                          | userAccount    | scenarios/full/00009-entity-security/06-userAccounts-tests/find-userAccount-UAB1.json    |
      | scenarios/full/00009-entity-security/06-userAccounts-tests/find-billingAccount-BAA1.json                          | Find Billing Account BAA1 still not accessible               |         | /account/billingAccount?billingAccountCode=RS_FULL_18_BAA1   | GET    |        401 | FAIL    | AUTHENTICATION_AUTHORIZATION_EXCEPTION | Access to entity details is not allowed. |                |                                                                                          |
      | scenarios/full/00009-entity-security/06-userAccounts-tests/find-billingAccount-BAB1.json                          | Find Billing Account BAB1 still not accessible               |         | /account/billingAccount?billingAccountCode=RS_FULL_18_BAB1   | GET    |        401 | FAIL    | AUTHENTICATION_AUTHORIZATION_EXCEPTION | Access to entity details is not allowed. |                |                                                                                          |
      | scenarios/full/00009-entity-security/06-userAccounts-tests/find-customerAccount-CAA1.json                         | Find Customer Account CAA1 still not accessible              |         | /account/customerAccount?customerAccountCode=RS_FULL_18_CAA1 | GET    |        401 | FAIL    | AUTHENTICATION_AUTHORIZATION_EXCEPTION | Access to entity details is not allowed. |                |                                                                                          |
      | scenarios/full/00009-entity-security/06-userAccounts-tests/find-customerAccount-CAB1.json                         | Find Customer Account CAB1 still not accessible              |         | /account/customerAccount?customerAccountCode=RS_FULL_18_CAB1 | GET    |        401 | FAIL    | AUTHENTICATION_AUTHORIZATION_EXCEPTION | Access to entity details is not allowed. |                |                                                                                          |
      | scenarios/full/00009-entity-security/06-userAccounts-tests/find-customer-CUSTA.json                               | Find Customer CUSTA still not accessible                     |         | /account/customer?customerCode=RS_FULL_18_CUSTA              | GET    |        401 | FAIL    | AUTHENTICATION_AUTHORIZATION_EXCEPTION | Access to entity details is not allowed. |                |                                                                                          |
      | scenarios/full/00009-entity-security/06-userAccounts-tests/find-customer-CUSTB.json                               | Find Customer CUSTB still not accessible                     |         | /account/customer?customerCode=RS_FULL_18_CUSTB              | GET    |        401 | FAIL    | AUTHENTICATION_AUTHORIZATION_EXCEPTION | Access to entity details is not allowed. |                |                                                                                          |
      | scenarios/full/00009-entity-security/06-userAccounts-tests/find-seller-SELLER_A.json                              | Find Seller SELLER_A still not accessible                    |         | /seller?sellerCode=RS_FULL_18_SELLER_A                       | GET    |        401 | FAIL    | AUTHENTICATION_AUTHORIZATION_EXCEPTION | Access to entity details is not allowed. |                |                                                                                          |
      | scenarios/full/00009-entity-security/06-userAccounts-tests/find-seller-SELLER_B.json                              | Find Seller SELLER_B still not accessible                    |         | /seller?sellerCode=RS_FULL_18_SELLER_B                       | GET    |        401 | FAIL    | AUTHENTICATION_AUTHORIZATION_EXCEPTION | Access to entity details is not allowed. |                |                                                                                          |
      | scenarios/full/00009-entity-security/06-userAccounts-tests/update-user-to-allow-access-to-BAA1-and-UAA1-only.json | Update User to Allow Access to BAA1 and UAA1 only            | UserDto | /user                                                        | PUT    |        200 | SUCCESS |                                        |                                          |                |                                                                                          |
      | scenarios/full/00009-entity-security/06-userAccounts-tests/find-billingAccount-BAA1.json                          | Find Billing Account BAA1 with all its UserAccounts included |         | /account/billingAccount?billingAccountCode=RS_FULL_18_BAA1   | GET    |        200 | SUCCESS |                                        |                                          | billingAccount | scenarios/full/00009-entity-security/06-userAccounts-tests/find-billingAccount-BAA1.json |
      | scenarios/full/00009-entity-security/06-userAccounts-tests/restore-user-to-default.json                           | Restore User to default                                      | UserDto | /user                                                        | PUT    |        200 | SUCCESS |                                        |                                          |                |                                                                                          |