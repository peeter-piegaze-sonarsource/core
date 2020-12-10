@TestUpdateEntity
   # The objective of this scenario is to verify whether any entity can
   # be updated by API
Feature: Testing method Update on an entity

   Background:  System is configured.

   Scenario Outline: Update an entity

      Given  Update "<entity>" with "<id>"
      When   All fields tested
      Then   The status is <status>

      Examples:
         | entity     |  id   |  status  |
         | seller     |  3    |   200    |
         | provider   |  1    |   200    |
         | user       |  3    |   200    |
