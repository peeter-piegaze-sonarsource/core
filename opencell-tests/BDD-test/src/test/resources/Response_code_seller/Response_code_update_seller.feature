@TestResponseStatusForUpdateSeller
   # The objective of this scenario is to verify whether
   # there exists a seller with id in database.
   # If it is the case, the test is PASSED
   # Otherwise, the test is FAILED

Feature: Testing method Update on entity Seller

   Background:  System is configured.

   Scenario Outline: Update a seller

      Given  Update seller on "<server>"
      When   Field id filled by "<id>"
      Then   The status is "<status>"

      Examples:
         | server               | id | status |
         | localhost:8080       | 5  | 200    |
         | tnn.d2.opencell.work | 5  | 200    |
