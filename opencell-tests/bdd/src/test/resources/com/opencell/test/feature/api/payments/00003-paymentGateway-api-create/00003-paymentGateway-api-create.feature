@payments
Feature: Create/Update Payment Gateway by API

  Background: The system is configured

  @admin @superadmin
  Scenario Outline: <status> <action> Payment Gateway by API
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<action>" "<api>"
    Then The payment gateway is created
    And Validate that the statusCode is "<statusCode>"
    And The status is "<status>"
    And The message  is "<message>"
    And The errorCode  is "<errorCode>"

    Examples: 
      | jsonFile                                                            | dto               | api                                    | action         | statusCode | status  | errorCode                        | message                                                                                         |
      | api/payments/00003-paymentGateway-api-create/SuccessTest.json       | PaymentGatewayDto | /payment/paymentGateway/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                                                 |
      | api/payments/00003-paymentGateway-api-create/SuccessTest.json       | PaymentGatewayDto | /payment/paymentGateway/               | CreateOrUpdate |        403 | FAIL    | ENTITY_ALREADY_EXISTS_EXCEPTION  | PaymentGateway with code=TEST already exists.                                                   |
      | api/payments/00003-paymentGateway-api-create/DO_NOT_EXIST.json      | PaymentGatewayDto | /payment/paymentGateway/               | Update         |        404 | FAIL    | ENTITY_DOES_NOT_EXISTS_EXCEPTION | PaymentGateway with code=NOT_EXIST does not exists.                                             |
      | api/payments/00003-paymentGateway-api-create/SuccessTest1.json      | PaymentGatewayDto | /payment/paymentGateway/               | Update         |        200 | SUCCESS |                                  |                                                                                                 |
      | api/payments/00003-paymentGateway-api-create/SuccessTest1.json      | PaymentGatewayDto | /payment/paymentGateway/createOrUpdate | CreateOrUpdate |        200 | SUCCESS |                                  |                                                                                                 |
      | api/payments/00003-paymentGateway-api-create/MISSING_PARAMETER.json | PaymentGatewayDto | /payment/paymentGateway/createOrUpdate | CreateOrUpdate |        400 | FAIL    | MISSING_PARAMETER                | The following parameters are required or contain invalid values: code, type, paymentMethodType. |
      | api/payments/00003-paymentGateway-api-create/INVALID_PARAMETER.json | PaymentGatewayDto | /payment/paymentGateway/createOrUpdate | CreateOrUpdate |        400 | FAIL    | INVALID_PARAMETER                | Cannot deserialize value of type `org.meveo.model.payments.PaymentGatewayTypeEnum` from String  |
