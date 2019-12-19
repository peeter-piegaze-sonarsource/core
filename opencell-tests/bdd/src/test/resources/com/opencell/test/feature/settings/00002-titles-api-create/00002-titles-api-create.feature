@settings
Feature: Create, modify and delete Title and civility

  Background: System is configured.

  @admin @superadmin
  Scenario Outline: Create a title and civilities by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then The title and civility is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                | dto      | api                           | statusCode | status  | errorCode                       | message                                                                     |
      | settings/00002-titles-api-create/SuccessTest.json       | TitleDto | /account/title/createOrUpdate |        200 | SUCCESS |                                 |                                                                             |
      | settings/00002-titles-api-create/SuccessTest.json       | TitleDto | /account/title                |        403 | FAIL    | ENTITY_ALREADY_EXISTS_EXCEPTION | Title with code=TEST already exists.                                        |
      | settings/00002-titles-api-create/SuccessTest1.json      | TitleDto | /account/title/createOrUpdate |        200 | SUCCESS |                                 |                                                                             |
      | settings/00002-titles-api-create/MISSING_PARAMETER.json | TitleDto | /account/title/createOrUpdate |        400 | FAIL    | MISSING_PARAMETER               | The following parameters are required or contain invalid values: titleCode. |
      | settings/00002-titles-api-create/INVALID_PARAMETER.json | TitleDto | /account/title/createOrUpdate |        400 | FAIL    | INVALID_PARAMETER               | Cannot deserialize value of type `java.lang.Boolean` from String           |
