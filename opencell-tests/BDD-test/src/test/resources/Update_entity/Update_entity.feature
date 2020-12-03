@TestUpdateEntity
   # The objective of this scenario is to verify whether any entity can
   # be updated by API
Feature: Testing method Update on an entity

   Background:  System is configured.

   Scenario Outline: Update an entity

      Given  Update "<entity>" with "<id>" on "<env>"
      When   Fields filled by "<code>", "<description>"
      Then   The status is "<status>"

      Examples:
         | entity   | env                   | id  | code               | description   | status |
         | seller   | http://localhost:8080 | 3   | Seller_ThangNguNgu | A description | 200    |
         | provider | http://localhost:8080 | 1   | Prov_ThangNguNgu   | A Prov Desc   | 200    |



