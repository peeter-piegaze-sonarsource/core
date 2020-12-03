@TestUpdateSeller
   # The objective of this scenario is to verify whether
Feature: Testing method Update on entity Seller

   Background:  System is configured.

   Scenario Outline: Update a seller

      Given  Update seller on "<server>"
      When   Field id filled by "<id>"
      And    Field code filled by "<code>"
      And    Field description filled by "<description>"
      And    Field tradingCurrency filled by "<tradingCurrencyId>"
      Then   The status is "<status>"


      Examples:
         | server               | id | code               | description     | tradingCurrencyId | status |
         | tnn.d2.opencell.work       | 5  | Seller_ThangNguyen | new description | -1                | 200    |
