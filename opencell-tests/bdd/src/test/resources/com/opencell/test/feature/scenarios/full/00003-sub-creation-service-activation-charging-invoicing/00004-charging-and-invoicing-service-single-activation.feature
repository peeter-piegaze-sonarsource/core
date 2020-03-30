@full @ignore
Feature: Sub creation, Service Activation, Charging and invoicing - Charging and invoicing - service single activation

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
      | jsonFile                                                                                                                                                        | title                          | dto                       | api                                                                                                  | action | statusCode | status  | errorCode | message | entity  | expected                                                                                                                                                        |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/04-charging-and-invoicing-service-single-activation/recurring-rating-job.json           | RecurringRatingJob             |         | /job/execute                                                                                         | POST   |        200 | SUCCESS |           |         |         |                                                                                                                                                                 |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/04-charging-and-invoicing-service-single-activation/apply-product-on-UA.json            | Apply product on UA            | ApplyProductRequestDto    | /account/userAccount/applyProduct                                                                    | POST   |        200 | SUCCESS |           |         |         |                                                                                                                                                                 |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/04-charging-and-invoicing-service-single-activation/apply-product.json                  | Apply product                  | ApplyProductRequestDto    | /billing/subscription/applyProduct                                                                   | POST   |        200 | SUCCESS |           |         |         |                                                                                                                                                                 |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/04-charging-and-invoicing-service-single-activation/charge-cdr_1.json                   | Charge cdr                     | String                    | /billing/mediation/chargeCdr                                                                         | POST   |        200 | SUCCESS |           |         |         |                                                                                                                                                                 |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/04-charging-and-invoicing-service-single-activation/charge-cdr_2.json                   | Charge cdr 2                   | String                    | /billing/mediation/chargeCdr                                                                         | POST   |        200 | SUCCESS |           |         |         |                                                                                                                                                                 |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/04-charging-and-invoicing-service-single-activation/generate-invoice.json               | GenerateInvoice                |  | /invoice/generateInvoice                                                                             | POST   |        200 | SUCCESS |           |         |         |                                                                                                                                                                 |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/04-charging-and-invoicing-service-single-activation/get-invoice-by-number-and-type.json | Get Invoice by number and type |                           | /invoice?invoiceNumber=Fact_200000000202&invoiceType=RS_BASE_TEST-Typee_200&includeTransactions=true | GET    |        200 | SUCCESS |           |         | invoice | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/04-charging-and-invoicing-service-single-activation/get-invoice-by-number-and-type.json |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/04-charging-and-invoicing-service-single-activation/get-invoiceXML-with-type.json       | GetInvoiceXmlWithType          | String                    | /invoice/getXMLInvoiceWithType                                                                       | POST   |        200 | SUCCESS |           |         |         |                                                                                                                                                                 |
      | scenarios/full/00003-sub-creation-service-activation-charging-invoicing/04-charging-and-invoicing-service-single-activation/get-invoicePDF-with-type.json       | GetInvoicePdfWithType          | String                    | /invoice/getPdfInvoiceWithType                                                                       | POST   |        200 | SUCCESS |           |         |         |                                                                                                                                                                 |
