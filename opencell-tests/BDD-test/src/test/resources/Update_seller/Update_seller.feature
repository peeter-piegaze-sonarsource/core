@TestUpdateSeller
   # The objective of this scenario is to verify whether an entity Seller
   # can be updated by API
Feature: Testing method Update on entity Seller

   Background:  System is configured.

   Scenario Outline: Update a seller

      Given  Update seller on "<env>"
      When   Field id filled by "<id>"
      And    Field code filled by "<code>"
      And    Field description filled by "<description>"
      And    Field tradingCurrency filled by "<tradingCurrencyId>"
      Then   The status is "<status>"


      Examples:
         | env                   | id | code               | description     | tradingCurrencyId | status |
         | http://localhost:8080 | 3  | Seller_ThangNguNgu | new description | -1                | 200    |
         | http://localhost:8080 | 4  | Seller_ThangHoolaa | new description | -1                | 200    |
