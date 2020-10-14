@full
Feature: Entity Security - Customer Account Tests

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
      | jsonFile                                                                                                                | title                                                      | dto     | api                                                          | action | statusCode | status  | errorCode                              | message                                  | entity          | expected                                                                                       |
      | scenarios/full/00009-entity-security/04-customer-accounts-tests/restore-user-to-default.json                            | Restore User to default                                    | UserDto | /user                                                        | PUT    |        200 | SUCCESS |                                        |                                          |                 |                                                                                                |
      | scenarios/full/00009-entity-security/04-customer-accounts-tests/find-customerAccount-CAA1.json                          | Find Customer Account CAA1                                 |         | /account/customerAccount?customerAccountCode=RS_FULL_18_CAA1 | GET    |        200 | SUCCESS |                                        |                                          | customerAccount | scenarios/full/00009-entity-security/04-customer-accounts-tests/find-customerAccount-CAA1.json |
      | scenarios/full/00009-entity-security/04-customer-accounts-tests/find-customerAccount-CAB1.json                          | Find Customer Account CAB1                                 |         | /account/customerAccount?customerAccountCode=RS_FULL_18_CAB1 | GET    |        200 | SUCCESS |                                        |                                          | customerAccount | scenarios/full/00009-entity-security/04-customer-accounts-tests/find-customerAccount-CAB1.json |
      | scenarios/full/00009-entity-security/04-customer-accounts-tests/find-customer-CUSTA.json                                | Find Customer CUSTA                                        |         | /account/customer?customerCode=RS_FULL_18_CUSTA              | GET    |        200 | SUCCESS |                                        |                                          | customer        | scenarios/full/00009-entity-security/04-customer-accounts-tests/find-customer-CUSTA.json       |
      | scenarios/full/00009-entity-security/04-customer-accounts-tests/find-customer-CUSTB.json                                | Find Customer CUSTB                                        |         | /account/customer?customerCode=RS_FULL_18_CUSTB              | GET    |        200 | SUCCESS |                                        |                                          | customer        | scenarios/full/00009-entity-security/04-customer-accounts-tests/find-customer-CUSTB.json       |
      | scenarios/full/00009-entity-security/04-customer-accounts-tests/find-seller-SELLER_A.json                               | Find Seller SELLER_A                                       |         | /seller?sellerCode=RS_FULL_18_SELLER_A                       | GET    |        200 | SUCCESS |                                        |                                          | seller          | scenarios/full/00009-entity-security/04-customer-accounts-tests/find-seller-SELLER_A.json      |
      | scenarios/full/00009-entity-security/04-customer-accounts-tests/find-seller-SELLER_B.json                               | Find Seller SELLER_B                                       |         | /seller?sellerCode=RS_FULL_18_SELLER_B                       | GET    |        200 | SUCCESS |                                        |                                          | seller          | scenarios/full/00009-entity-security/04-customer-accounts-tests/find-seller-SELLER_B.json      |
      | scenarios/full/00009-entity-security/04-customer-accounts-tests/update-user-to-allow-access-to-SELLER_A-only.json       | Update User to Allow Access to SELLER_A only               | UserDto | /user                                                        | PUT    |        200 | SUCCESS |                                        |                                          |                 |                                                                                                |
      | scenarios/full/00009-entity-security/04-customer-accounts-tests/find-customerAccount-CAA1.json                          | Find Customer Account CAA1 still accessible                |         | /account/customerAccount?customerAccountCode=RS_FULL_18_CAA1 | GET    |        200 | SUCCESS |                                        |                                          | customerAccount | scenarios/full/00009-entity-security/04-customer-accounts-tests/find-customerAccount-CAA1.json |
      | scenarios/full/00009-entity-security/04-customer-accounts-tests/find-customerAccount-CAB1.json                          | Find Customer Account CAB1 not accessible                  |         | /account/customerAccount?customerAccountCode=RS_FULL_18_CAB1 | GET    |        401 | FAIL    | AUTHENTICATION_AUTHORIZATION_EXCEPTION | Access to entity details is not allowed. |                 |                                                                                                |
      | scenarios/full/00009-entity-security/04-customer-accounts-tests/update-user-to-allow-access-to-SELLER_B-only.json       | Update User to Allow Access to SELLER_B only               | UserDto | /user                                                        | PUT    |        200 | SUCCESS |                                        |                                          |                 |                                                                                                |
      | scenarios/full/00009-entity-security/04-customer-accounts-tests/find-customerAccount-CAA1.json                          | Find Customer Account CAA1 not accessible                  |         | /account/customerAccount?customerAccountCode=RS_FULL_18_CAA1 | GET    |        401 | FAIL    | AUTHENTICATION_AUTHORIZATION_EXCEPTION | Access to entity details is not allowed. |                 |                                                                                                |
      | scenarios/full/00009-entity-security/04-customer-accounts-tests/find-customerAccount-CAB1.json                          | Find Customer Account CAB1 now accessible                  |         | /account/customerAccount?customerAccountCode=RS_FULL_18_CAB1 | GET    |        200 | SUCCESS |                                        |                                          | customerAccount | scenarios/full/00009-entity-security/04-customer-accounts-tests/find-customerAccount-CAB1.json |
      | scenarios/full/00009-entity-security/04-customer-accounts-tests/update-user-to-allow-access-to-CUSTA-only.json          | Update User to Allow Access to CUSTA only                  | UserDto | /user                                                        | PUT    |        200 | SUCCESS |                                        |                                          |                 |                                                                                                |
      | scenarios/full/00009-entity-security/04-customer-accounts-tests/find-customerAccount-CAA1.json                          | Find Customer Account CAA1 now accessible again            |         | /account/customerAccount?customerAccountCode=RS_FULL_18_CAA1 | GET    |        200 | SUCCESS |                                        |                                          | customerAccount | scenarios/full/00009-entity-security/04-customer-accounts-tests/find-customerAccount-CAA1.json |
      | scenarios/full/00009-entity-security/04-customer-accounts-tests/find-customerAccount-CAB1.json                          | Find Customer Account CAB1 not accessible again            |         | /account/customerAccount?customerAccountCodcustomerAccounte=RS_FULL_18_CAB1 | GET    |        401 | FAIL    | AUTHENTICATION_AUTHORIZATION_EXCEPTION | Access to entity details is not allowed. |                 |                                                                                                |
      | scenarios/full/00009-entity-security/04-customer-accounts-tests/update-user-to-allow-access-to-CUSTB-only.json          | Update User to Allow Access to CUSTB only                  | UserDto | /user                                                        | PUT    |        200 | SUCCESS |                                        |                                          |                 |                                                                                                |
      | scenarios/full/00009-entity-security/04-customer-accounts-tests/find-customerAccount-CAA1.json                          | Find Customer Account CAA1 not accessible again            |         | /account/customerAccount?customerAccountCode=RS_FULL_18_CAA1 | GET    |        401 | FAIL    | AUTHENTICATION_AUTHORIZATION_EXCEPTION | Access to entity details is not allowed. |                 |                                                                                                |
      | scenarios/full/00009-entity-security/04-customer-accounts-tests/find-customerAccount-CAB1.json                          | Find Customer Account CAB1 now accessible again            |         | /account/customerAccount?customerAccountCode=RS_FULL_18_CAB1 | GET    |        200 | SUCCESS |                                        |                                          | customerAccount | scenarios/full/00009-entity-security/04-customer-accounts-tests/find-customerAccount-CAB1.json |
      | scenarios/full/00009-entity-security/04-customer-accounts-tests/update-user-to-allow-access-to-CAA1-only.json           | Update User to Allow Access to CAA1 only                   | UserDto | /user                                                        | PUT    |        200 | SUCCESS |                                        |                                          |                 |                                                                                                |
      | scenarios/full/00009-entity-security/04-customer-accounts-tests/find-customerAccount-CAA1.json                          | Find Customer Account CAA1 now accessible again.           |         | /account/customerAccount?customerAccountCode=RS_FULL_18_CAA1 | GET    |        200 | SUCCESS |                                        |                                          | customerAccount | scenarios/full/00009-entity-security/04-customer-accounts-tests/find-customerAccount-CAA1.json |
      | scenarios/full/00009-entity-security/04-customer-accounts-tests/find-customerAccount-CAB1.json                          | Find Customer Account CAB1 not accessible again.           |         | /account/customerAccount?customerAccountCode=RS_FULL_18_CAB1 | GET    |        401 | FAIL    | AUTHENTICATION_AUTHORIZATION_EXCEPTION | Access to entity details is not allowed. |                 |                                                                                                |
      | scenarios/full/00009-entity-security/04-customer-accounts-tests/find-customer-CUSTA.json                                | Find Customer CUSTA not accessible                         |         | /account/customer?customerCode=RS_FULL_18_CUSTA              | GET    |        401 | FAIL    | AUTHENTICATION_AUTHORIZATION_EXCEPTION | Access to entity details is not allowed. |                 |                                                                                                |
      | scenarios/full/00009-entity-security/04-customer-accounts-tests/find-customer-CUSTA.json                                | Find Customer CUSTB not accessible                         |         | /account/customer?customerCode=RS_FULL_18_CUSTB              | GET    |        401 | FAIL    | AUTHENTICATION_AUTHORIZATION_EXCEPTION | Access to entity details is not allowed. |                 |                                                                                                |
      | scenarios/full/00009-entity-security/04-customer-accounts-tests/find-seller-SELLER_A.json                               | Find Seller SELLER_A not accessible                        |         | /seller?sellerCode=RS_FULL_18_SELLER_A                       | GET    |        401 | FAIL    | AUTHENTICATION_AUTHORIZATION_EXCEPTION | Access to entity details is not allowed. |                 |                                                                                                |
      | scenarios/full/00009-entity-security/04-customer-accounts-tests/find-seller-SELLER_B.json                               | Find Seller SELLER_B not accessible                        |         | /seller?sellerCode=RS_FULL_18_SELLER_B                       | GET    |        401 | FAIL    | AUTHENTICATION_AUTHORIZATION_EXCEPTION | Access to entity details is not allowed. |                 |                                                                                                |
      | scenarios/full/00009-entity-security/04-customer-accounts-tests/update-user-to-allow-access-to-CAB1-only.json           | Update User to Allow Access to CAB1 only                   | UserDto | /user                                                        | PUT    |        200 | SUCCESS |                                        |                                          |                 |                                                                                                |
      | scenarios/full/00009-entity-security/04-customer-accounts-tests/find-customerAccount-CAA1.json                          | Find Customer Account CAA1 not accessible again.           |         | /account/customerAccount?customerAccountCode=RS_FULL_18_CAA1 | GET    |        401 | FAIL    | AUTHENTICATION_AUTHORIZATION_EXCEPTION | Access to entity details is not allowed. |                 |                                                                                                |
      | scenarios/full/00009-entity-security/04-customer-accounts-tests/find-customerAccount-CAB1.json                          | Find Customer Account CAB1 now accessible again.           |         | /account/customerAccount?customerAccountCode=RS_FULL_18_CAB1 | GET    |        200 | SUCCESS |                                        |                                          | customerAccount | scenarios/full/00009-entity-security/04-customer-accounts-tests/find-customerAccount-CAB1.json |
      | scenarios/full/00009-entity-security/04-customer-accounts-tests/find-customer-CUSTA.json                                | Find Customer CUSTA still not accessible                   |         | /account/customer?customerCode=RS_FULL_18_CUSTA              | GET    |        401 | FAIL    | AUTHENTICATION_AUTHORIZATION_EXCEPTION | Access to entity details is not allowed. |                 |                                                                                                |
      | scenarios/full/00009-entity-security/04-customer-accounts-tests/find-customer-CUSTB.json                                | Find Customer CUSTB still not accessible                   |         | /account/customer?customerCode=RS_FULL_18_CUSTB              | GET    |        401 | FAIL    | AUTHENTICATION_AUTHORIZATION_EXCEPTION | Access to entity details is not allowed. |                 |                                                                                                |
      | scenarios/full/00009-entity-security/04-customer-accounts-tests/find-seller-SELLER_A.json                               | Find Seller SELLER_A still not accessible                  |         | /seller?sellerCode=RS_FULL_18_SELLER_A                       | GET    |        401 | FAIL    | AUTHENTICATION_AUTHORIZATION_EXCEPTION | Access to entity details is not allowed. |                 |                                                                                                |
      | scenarios/full/00009-entity-security/04-customer-accounts-tests/find-seller-SELLER_B.json                               | Find Seller SELLER_B still not accessible                  |         | /seller?sellerCode=RS_FULL_18_SELLER_B                       | GET    |        401 | FAIL    | AUTHENTICATION_AUTHORIZATION_EXCEPTION | Access to entity details is not allowed. |                 |                                                                                                |
      | scenarios/full/00009-entity-security/04-customer-accounts-tests/update-user-to-allow-access-to-CUSTA-and-CAA1-only.json | Update User to Allow Access to CUSTA and CAA1 only         | UserDto | /user                                                        | PUT    |        200 | SUCCESS |                                        |                                          |                 |                                                                                                |
      | scenarios/full/00009-entity-security/04-customer-accounts-tests/find-customerAccount-CAA1.json                          | Find Customer Account CAA1 now accessible again..          |         | /account/customerAccount?customerAccountCode=RS_FULL_18_CAA1 | GET    |        200 | SUCCESS |                                        |                                          |                 |                                                                                                |
      | scenarios/full/00009-entity-security/04-customer-accounts-tests/find-customer-CUSTA.json                                | Find Customer CUSTA with all its CustomerAccounts included |         | /account/customer?customerCode=RS_FULL_18_CUSTA              | GET    |        200 | SUCCESS |                                        |                                          | customer        | scenarios/full/00009-entity-security/04-customer-accounts-tests/find-customer-CUSTA.json       |
      | scenarios/full/00009-entity-security/04-customer-accounts-tests/restore-user-to-default.json                            | Restore User to default.                                   | UserDto | /user                                                        | PUT    |        200 | SUCCESS |                                        |                                          |                 |                                                                                                |