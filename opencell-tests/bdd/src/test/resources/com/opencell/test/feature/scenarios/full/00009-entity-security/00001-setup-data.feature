@full
Feature: Entity Security - Setup Data

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
      | jsonFile                                                                                                   | title                                              | dto              | api                                               | action | statusCode | status  | errorCode | message | entity | expected                                                                     |
      | scenarios/full/00009-entity-security/01-setup-data/restore-user-to-default.json                            | Restore User to default                            | UserDto          | /user                                             | PUT    |        200 | SUCCESS |           |         |        |                                                                              |
      | scenarios/full/00009-entity-security/01-setup-data/create-seller-SELLER_A.json                             | Create Seller SELLER_A                             | SellerDto        | /seller                                           | Create |        200 | SUCCESS |           |         |        |                                                                              |
      | scenarios/full/00009-entity-security/01-setup-data/find-seller-SELLER_A.json                               | Find Seller SELLER_A                               |                  | /seller?sellerCode=RS_FULL_18_SELLER_A            | GET    |        200 | SUCCESS |           |         | seller | scenarios/full/00009-entity-security/01-setup-data/find-seller-SELLER_A.json |
      | scenarios/full/00009-entity-security/01-setup-data/create-seller-SELLER_B.json                             | Create Seller SELLER_B                             | SellerDto        | /seller                                           | Create |        200 | SUCCESS |           |         |        |                                                                              |
      | scenarios/full/00009-entity-security/01-setup-data/find-seller-SELLER_B.json                               | Find Seller SELLER_B                               |                  | /seller?sellerCode=RS_FULL_18_SELLER_B            | GET    |        200 | SUCCESS |           |         | seller | scenarios/full/00009-entity-security/01-setup-data/find-seller-SELLER_B.json |
      | scenarios/full/00009-entity-security/01-setup-data/create-seller-SELLER_C.json                             | Create Seller SELLER_C                             | SellerDto        | /seller                                           | Create |        200 | SUCCESS |           |         |        |                                                                              |
      | scenarios/full/00009-entity-security/01-setup-data/create-accountHierarchy.json                            | Create Account Hierarchy                           |                  | /account/accountHierarchy/customerHierarchyUpdate | POST   |        200 | SUCCESS |           |         |        |                                                                              |
      | scenarios/full/00009-entity-security/01-setup-data/create-userHierarchyLevels.json                         | Create UserHierarchyLevels                         |                  | /hierarchy/userGroupLevel                         | POST   |        200 | SUCCESS |           |         |        |                                                                              |
      | scenarios/full/00009-entity-security/01-setup-data/create-user-in-level-1.json                             | Create User in level 1                             | UserDto          | /user                                             | POST   |        200 | SUCCESS |           |         |        |                                                                              |
      | scenarios/full/00009-entity-security/01-setup-data/create-user-in-level-2.json                             | Create User in level 2                             | UserDto          | /user                                             | POST   |        200 | SUCCESS |           |         |        |                                                                              |
      | scenarios/full/00009-entity-security/01-setup-data/create-user-in-level-2_1.json                           | Create User in level 2.1                           | UserDto          | /user                                             | POST   |        200 | SUCCESS |           |         |        |                                                                              |
      | scenarios/full/00009-entity-security/01-setup-data/create-user-in-level-2_2.json                           | Create User in level 2.2                           | UserDto          | /user                                             | POST   |        200 | SUCCESS |           |         |        |                                                                              |
      | scenarios/full/00009-entity-security/01-setup-data/create-user-in-level-3.json                             | Create User in level 3                             | UserDto          | /user                                             | POST   |        200 | SUCCESS |           |         |        |                                                                              |
      | scenarios/full/00009-entity-security/01-setup-data/create-userRole-with-userSelfManagement-permission.json | Create UserRole with userSelfManagement permission | RoleDto          | /role                                             | POST   |        200 | SUCCESS |           |         |        |                                                                              |
      | scenarios/full/00009-entity-security/01-setup-data/create-offerTemplate-no-seller.json                     | Create offer template - no seller                  | OfferTemplateDto | /catalog/offerTemplate                            | POST   |        200 | SUCCESS |           |         |        |                                                                              |
      | scenarios/full/00009-entity-security/01-setup-data/create-offerTemplate-seller_A.json                      | Create offer template - seller A                   | OfferTemplateDto | /catalog/offerTemplate                            | POST   |        200 | SUCCESS |           |         |        |                                                                              |
      | scenarios/full/00009-entity-security/01-setup-data/create-offerTemplate-seller_B.json                      | Create offer template - seller B                   | OfferTemplateDto | /catalog/offerTemplate                            | POST   |        200 | SUCCESS |           |         |        |                                                                              |
      | scenarios/full/00009-entity-security/01-setup-data/create-offerTemplate-seller_C.json                      | Create offer template - seller C                   | OfferTemplateDto | /catalog/offerTemplate                            | POST   |        200 | SUCCESS |           |         |        |                                                                              |
      | scenarios/full/00009-entity-security/01-setup-data/create-offerTemplate-seller_A_B_C.json                  | Create offer template - seller A,B,C               | OfferTemplateDto | /catalog/offerTemplate                            | POST   |        200 | SUCCESS |           |         |        |                                                                              |
      | scenarios/full/00009-entity-security/01-setup-data/set-securedEntities-true.json                           | Set securedEntities=true                           |                  | /                                                 |        |        200 | SUCCESS |           |         |        |                                                                              |
