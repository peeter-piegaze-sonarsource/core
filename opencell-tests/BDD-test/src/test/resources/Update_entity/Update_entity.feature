@TestUpdateEntity
   # The objective of this scenario is to verify whether any entity can
   # be updated by API
Feature: Testing method Update on an entity

   Background:  System is configured.

   Scenario Outline: Update an entity

      Given  Update "<entity>" with "<id>" on "<env>"
      When   Fields filled by "<description>"
      Then   The status is <status>

      Examples:
         | entity          | env                          | id  | status | description     |
         | seller          | https://baq.d2.opencell.work | 3   | 200    | A description   |
#         | provider        | https://tnn.d2.opencell.work | 1   | 200    | src/test/resources/Update_entity/Provider.json        |
#         | user            | https://tnn.d2.opencell.work | 3   | 200    | src/test/resources/Update_entity/User.json            |
#         | tradingCurrency | https://tnn.d2.opencell.work | -2  | 200    | src/test/resources/Update_entity/TradingCurrency.json |

