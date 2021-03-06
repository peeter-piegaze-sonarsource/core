@custom @ignore
Feature: Delete Entity Customization by API

  Background: The classic offer is already executed
              Create Entity Customization by API is already executed


  @admin @superadmin
  Scenario Outline: Delete Entity Customization by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the delete "<api>"
    Then The entity is deleted
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                                     | dto                     | api                          | statusCode | status  | errorCode                        | message                                                   |
      | api/custom/00001-entityCustomization-api-create/SuccessTest.json           | CustomEntityTemplateDto | /entityCustomization/entity/ |        200 | SUCCESS |                                  |                                                           |
      | api/custom/10001-entityCustomization-api-delete/ENTITY_DOES_NOT_EXIST.json | CustomEntityTemplateDto | /entityCustomization/entity/ |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | CustomEntityTemplate with code=NOT_EXIST does not exists. |
