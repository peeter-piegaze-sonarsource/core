@TestUpdateEntity
   # The objective of this scenario is to verify whether any entity can
   # be updated by API
Feature: Testing method Update on an entity

   Background:  System is configured.

   Scenario Outline: Update an entity

      Given  Update "<entity>" with "<id>"
      When   Fields filled by "<jsonPath>"
      Then   The status is <status>

      Examples:
         | entity          | id  | jsonPath    |  status  |
#         | seller          |  3   | 200    | A description        |
#         | provider        |  1   | 200    | Provider.json        |
         | user            | 3   | src/test/resources/BDDexperimentation/Update_entity/User.json  |   200    |
#         | tradingCurrency | -2  | 200    | TradingCurrency.json |

