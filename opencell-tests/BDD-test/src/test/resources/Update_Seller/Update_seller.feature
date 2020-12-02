@TestResponseStatusForUpdateSeller
   # The objective of this test is to verify
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
         | localhost:8080       | 5  | Seller_ThangNguyen | new description | -1                | 200    |
