@accounts
Feature: Create/Update a Customer by API

  Background: The classic offer is already executed

  @admin @superadmin
  Scenario Outline: <status> <action> a Customer by API <errorCode>
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The customer is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                           | dto         | api                              | action         | statusCode | status  | errorCode                        | message                                                                            |
      | api/accounts/00001-customer-api-create/SuccessTest.json            | CustomerDto | /account/customer/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                                    |
      | api/accounts/00001-customer-api-create/SuccessTest.json            | CustomerDto | /account/customer/               | Create         |        403 | FAIL    | ENTITY_ALREADY_EXISTS_EXCEPTION  | Customer with code=TEST already exists.                                            |
      | api/accounts/00001-customer-api-create/DO_NOT_EXIST.json           | CustomerDto | /account/customer/               | Update         |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | Customer with code=NOT_EXIST does not exists.                                      |
      | api/accounts/00001-customer-api-create/SuccessTest1.json           | CustomerDto | /account/customer/               | Update         |        200 | SUCCESS |                                  |                                                                                    |
      | api/accounts/00001-customer-api-create/SuccessTest1.json           | CustomerDto | /account/customer/createOrUpdate | Create         |        200 | SUCCESS |                                  |                                                                                    |
      | api/accounts/00001-customer-api-create/MISSING_PARAMETER.json      | CustomerDto | /account/customer/createOrUpdate | Create         |        400 | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values: customerCategory. |
      | api/accounts/00001-customer-api-create/INVALID_PARAMETER.json      | CustomerDto | /account/customer/createOrUpdate | Create         |        400 | FAIL    | INVALID_PARAMETER                | Cannot deserialize value of type `java.util.Date` from String                      |
      | api/accounts/00001-customer-api-create/ENTITY_DOES_NOT_EXIST1.json | CustomerDto | /account/customer/createOrUpdate | Create         |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | CustomerCategory with code=CLASSIC does not exists.                                |
      | api/accounts/00001-customer-api-create/ENTITY_DOES_NOT_EXIST2.json | CustomerDto | /account/customer/createOrUpdate | Create         |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | CustomerCategory with code=TEST does not exists.                                   |
      | api/accounts/00001-customer-api-create/ENTITY_DOES_NOT_EXIST3.json | CustomerDto | /account/customer/createOrUpdate | Create         |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | CustomerCategory with code=CLASSIC does not exists.                                |
