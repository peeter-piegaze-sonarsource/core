@full
Feature: Subscription workflow - Apply Product

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
      | jsonFile                                                                                      | title                        | dto                      | api                                                               | action         | statusCode | status  | errorCode | message | entity       | expected                                                                           |
      | scenarios/full/00004-subscription-workflow/05-apply-product/create-CFT-product-100.json       | Create CFT Product 100       | CustomFieldTemplateDto   | /entityCustomization/field/createOrUpdate                         | CreateOrUpdate |        200 | SUCCESS |           |         |              |                                                                                    |
      | scenarios/full/00004-subscription-workflow/05-apply-product/create-CFT-product-101.json       | Create CFT Product 101       | CustomFieldTemplateDto   | /entityCustomization/field/createOrUpdate                         | CreateOrUpdate |        200 | SUCCESS |           |         |              |                                                                                    |
      | scenarios/full/00004-subscription-workflow/05-apply-product/create-product-charge-100.json    | Create Product charge100     | ProductChargeTemplateDto | /catalogManagement/productChargeTemplate/createOrUpdate           | CreateOrUpdate |        200 | SUCCESS |           |         |              |                                                                                    |
      | scenarios/full/00004-subscription-workflow/05-apply-product/create-product-100.json           | Create Product 100           | ProductTemplateDto       | /catalogManagement/productTemplate/createOrUpdate                 | CreateOrUpdate |        200 | SUCCESS |           |         |              |                                                                                    |
      | scenarios/full/00004-subscription-workflow/05-apply-product/create-pricePlan-product-100.json | Create priceplan product 100 | PricePlanMatrixDto       | /catalog/pricePlan/createOrUpdate                                 | CreateOrUpdate |        200 | SUCCESS |           |         |              |                                                                                    |
      | scenarios/full/00004-subscription-workflow/05-apply-product/create-subscription.json          | Create subscription          | SubscriptionDto          | /billing/subscription/createOrUpdate                              | CreateOrUpdate |        200 | SUCCESS |           |         |              |                                                                                    |
      | scenarios/full/00004-subscription-workflow/05-apply-product/apply-product_2-to-UA.json        | Apply product 2 to UA        | ApplyProductRequestDto   | /account/userAccount/applyProduct                                 | POST           |        200 | SUCCESS |           |         |              |                                                                                    |
      | scenarios/full/00004-subscription-workflow/05-apply-product/apply-product_2-to-UA_2.json      | Apply product 2 to UA_2      | ApplyProductRequestDto   | /account/userAccount/applyProduct                                 | POST           |        200 | SUCCESS |           |         |              |                                                                                    |
      | scenarios/full/00004-subscription-workflow/05-apply-product/apply-product_2-to-SUB_2.json     | Apply product 2 to SUB_2     | ApplyProductRequestDto   | /billing/subscription/applyProduct                                | POST           |        200 | SUCCESS |           |         |              |                                                                                    |
      | scenarios/full/00004-subscription-workflow/05-apply-product/apply-product-100-to-UA.json      | Apply product 100 to UA      | ApplyProductRequestDto   | /account/userAccount/applyProduct                                 | POST           |        200 | SUCCESS |           |         |              |                                                                                    |
      | scenarios/full/00004-subscription-workflow/05-apply-product/apply-product-100-to-SUB_2.json   | Apply product 100 to SUB_2   | ApplyProductRequestDto   | /billing/subscription/applyProduct                                | POST           |        200 | SUCCESS |           |         |              |                                                                                    |
      | scenarios/full/00004-subscription-workflow/05-apply-product/remove-CFT-product-100.json       | Remove CFT Product 100       |                          | /customFieldTemplate/RS_FULL_164_CF_PROD_PRICE100/ProductInstance | DEL            |        200 | SUCCESS |           |         |              |                                                                                    |
      | scenarios/full/00004-subscription-workflow/05-apply-product/remove-CFT-product-101.json       | Remove CFT Product 101       |                          | /customFieldTemplate/RS_FULL_164_CF_PROD_PRICE101/ProductInstance | DEL            |        200 | SUCCESS |           |         |              |                                                                                    |
      | scenarios/full/00004-subscription-workflow/05-apply-product/find-subscription.json            | Find subscription            |                          | /billing/subscription?subscriptionCode=RS_FULL_164_SUB_PROD       | GET            |        200 | SUCCESS |           |         | subscription | scenarios/full/00004-subscription-workflow/05-apply-product/find-subscription.json |
