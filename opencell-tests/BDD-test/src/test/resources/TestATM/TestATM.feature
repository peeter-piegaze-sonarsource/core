@TestATM
Feature: Account is in credit?
  We would like to know if the account is credited while asking for an amount

  Scenario Outline: Account is in credit
    Given Given an ATM and I have <account> euros in my account
    When I ask for an amount of <amount> euros
    Then My account is <remainingAccount> euros
    And The ATM returns <amount> euros to me


    Examples:
      | account    | amount   | remainingAccount     |
      | 100 | 10 | 90 |
      | 100 | 10 | 90 |
