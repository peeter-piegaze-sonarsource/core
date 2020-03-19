@full
Feature: Sub creation, Service Activation, Charging and invoicing - Wallet operations

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
      | jsonFile                                                                                                                                                            | title                                                                 | dto                                  | api                                                                                                                                                                                           | action         | statusCode | status  | errorCode | message | entity                                            | expected                                                                                                                                      |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/06-wallet-operation/create-wallet-operation.json                                            | Create wallet operation                                               | WalletOperationDto                   | /billing/wallet/operation                                                                                                                                                                     | Create         |        200 | SUCCESS |           |         |                                                   |                                                                                                                                               |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/06-wallet-operation/generate-invoice.json                                                   | GenerateInvoice                                                       |                                      | /invoice/generateInvoice                                                                                                                                                                      | POST           |        200 | SUCCESS |           |         |                                                   |                                                                                                                                               |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/06-wallet-operation/get-invoiceXML-with-type.json                                           | GetInvoiceXmlWithType                                                 |                                      | /invoice/getXMLInvoiceWithType                                                                                                                                                                | POST           |        200 | SUCCESS |           |         |                                                   |                                                                                                                                               |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/06-wallet-operation/find-wallet-operations.json                                             | Find wallet operations                                                |                                      | /billing/wallet/operation/list?query=wallet.userAccount.code:RS_FULL_200_UA_INVOICE\|limit=500                                                                                                | GET            |        200 | SUCCESS |           |         | walletOperations.find {it.code=='RS_FULL_200_WO'} | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/06-wallet-operation/find-wallet-operations-expected.json              |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/06-wallet-operation/find-wallet-operations-paging.json                                      | Find wallet operations - paging                                       |                                      | /billing/wallet/operation/list?query=wallet.userAccount.code:RS_FULL_200_UA_INVOICE&offset=10&limit=5&sortBy=operationDate&sortOrder=DESCENDING                                               | GET            |        200 | SUCCESS |           |         |                                                   |                                                                                                                                               |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/06-wallet-operation/find-wallet-operations-criteria.json                                    | Find wallet operations - criteria                                     |                                      | /billing/wallet/operation/list?query=wallet.userAccount.code:RS_FULL_200_UA_INVOICE\|fromRange operationDate:2015-02-01\|toRange operationDate:2015-02-01                                     | GET            |        200 | SUCCESS |           |         |                                                   | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/06-wallet-operation/find-wallet-operations-criteria-expected.json     |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/06-wallet-operation/apply-one-charge-entreprise.json                                        | Apply one charge - enterprise                                         | ApplyOneShotChargeInstanceRequestDto | /billing/subscription/applyOneShotChargeInstance                                                                                                                                              | POST           |        200 | SUCCESS |           |         |                                                   |                                                                                                                                               |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/06-wallet-operation/find-wallet-operation-wo-entreprise.json                                | Find wallet operations - wo enterprise                                |                                      | /billing/wallet/operation/list?query=wallet.userAccount.code:RS_FULL_200_UA_INVOICE\|chargeInstance.description:ApplyOneChargeEnterprise&limit=5&sortBy=operationDate&sortOrder=DESCENDING    | GET            |        200 | SUCCESS |           |         | walletOperations[0]                               | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/06-wallet-operation/find-wallet-operation-wo-entreprise-expected.json |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/06-wallet-operation/set-provider-to-not-entreprise.json                                     | Set provider to NOT enterprise                                        | ProviderDto                          | /provider                                                                                                                                                                                     | PUT            |        200 | SUCCESS |           |         |                                                   |                                                                                                                                               |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/06-wallet-operation/apply-one-charge-NOT-entreprise.json                                    | Apply one charge - NOT enterprise                                     | ApplyOneShotChargeInstanceRequestDto | /billing/subscription/applyOneShotChargeInstance                                                                                                                                              | POST           |        200 | SUCCESS |           |         |                                                   |                                                                                                                                               |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/06-wallet-operation/find-wallet-operation-wo-entreprise.json                                | Find wallet operations - wo NOT enterprise                            |                                      | /billing/wallet/operation/list?query=wallet.userAccount.code:RS_FULL_200_UA_INVOICE\|chargeInstance.description:ApplyOneChargeNOTEnterprise&limit=5&sortBy=operationDate&sortOrder=DESCENDING | GET            |        200 | SUCCESS |           |         | walletOperations[0]                               | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/06-wallet-operation/find-wallet-operation-wo-entreprise-expected.json |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/06-wallet-operation/apply-one-charge--charge-OTHER-description-from-PP.wodescriptionEL.json | Apply one charge - Charge OTHER - description from PP.wodescriptionEL | ApplyOneShotChargeInstanceRequestDto | /billing/subscription/applyOneShotChargeInstance                                                                                                                                              | POST           |        200 | SUCCESS |           |         |                                                   |                                                                                                                                               |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/06-wallet-operation/find-wallet-operation-charge-OTHER.json                                 | Find wallet operations - Charge OTHER                                 |                                      | /billing/wallet/operation/list?query=wallet.userAccount.code:RS_FULL_200_UA_INVOICE\|chargeInstance.description:RS_BASE_OS_OTHER&limit=5&sortBy=operationDate&sortOrder=DESCENDING            | GET            |        200 | SUCCESS |           |         |                                                   |                                                                                                                                               |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/06-wallet-operation/create-userAccount-invoice_2.json                                       | Create User Account Invoice 2                                         | UserAccountDto                       | /account/userAccount/createOrUpdate                                                                                                                                                           | CreateOrUpdate |        200 | SUCCESS |           |         |                                                   |                                                                                                                                               |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/06-wallet-operation/create-subscription-on-UA_2.json                                        | Create subscription on UA 2                                           | SubscriptionDto                      | /billing/subscription/createOrUpdate                                                                                                                                                          | CreateOrUpdate |        200 | SUCCESS |           |         |                                                   |                                                                                                                                               |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/06-wallet-operation/activate-service-3-on-subscription-of-UA-2.json                         | Activate services 3 on subscription of UA 2                           | ActivateServicesRequestDto           | /billing/subscription/activateServices                                                                                                                                                        | POST           |        200 | SUCCESS |           |         |                                                   |                                                                                                                                               |
