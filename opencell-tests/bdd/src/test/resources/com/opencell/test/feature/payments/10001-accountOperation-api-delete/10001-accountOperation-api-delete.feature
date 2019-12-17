@payments
Feature: Delete Account Operation by API

  Background: The classic offer is already executed
              Create Account Operation is already executed


  @admin @superadmin
  Scenario Outline: Delete Account Operation by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the delete "<api>"
    Then The entity is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                              | dto                 | api                | statusCode | status  | errorCode                        | message                                           |
      | payments/00001-accountOperation-api-create/Success.json               | AccountOperationDto | /accountOperation/ |        200 | SUCCESS |                                  |                                                   |
      | payments/10001-accountOperation-api-delete/ENTITY_DOES_NOT_EXIST.json | AccountOperationDto | /accountOperation/ |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | AccountOperation with code=NOT_EXIST does not exists. |
