@TestUpdateEntity
   # The objective of this scenario is to verify whether any entity can
   # be updated by API
Feature: Testing method Update on an entity

   Background:  System is configured.

   Scenario Outline: Update an entity

      Given  Update "<entity>" with "<id>" on "<env>"
      When   Fields filled by "<code>", "<description>"
      Then   The status is <status>

      Examples:
         | entity          | env                          | id  | code               | description   | status |
         | seller          | https://baq.d2.opencell.work | 3   | Seller_ThangNguNgu | A description | 200    |
         | provider        | https://tnn.d2.opencell.work | 1   | Prov_ThangNguNgu   | A Prov Desc   | 200    |
         | user            | https://tnn.d2.opencell.work | 3   | A_new_User_Nguyen  | A User Desc   | 200    |
         | tradingCurrency | https://tnn.d2.opencell.work | -2  | A_new_User_Nguyen  | BA MA CHI     | 200    |



