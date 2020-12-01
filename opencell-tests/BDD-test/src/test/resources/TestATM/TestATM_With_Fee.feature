@TestATM
Feature: Account is in credit with fee?
  We would like to know if the account is credited with fee while asking for an amount

  Scenario: Account is in credit with fee
    Given Given an ATM and I have 100 euros in my account with a fee of 5 euros
    When I ask for an amount of 10 euros
    Then My account is 85 euros
    And The ATM returns 10 euros to me
