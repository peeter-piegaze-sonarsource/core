@TestUpdateSeller
Feature: Testing method Update on entity Subscription

   Background:  System is configured.

   # This piece of code verifies if "id" exists in the database.
   # If this is the case, return status 200 OK.
   # Otherwise, return status 400 Bad Request.
   # Attention: This piece of code tests the implementation of the
   # method "findById" of class PersistenceService.java
   Scenario: Verify existence of a subscription's id in database

      Given  A subscription
      When   Field "id" filled by id
      Then   The status is 200

   # This piece of code verifies method "update" in class
   # SubscriptionService.java
   Scenario: Update a subscription

      Given  Update subscription
      When   Field "id" filled by id
      And    Field "code" filled by code
      And    Field "description" filled by description
      And    Field "subscriptionDate" filled by subscriptionDate
      And    Field "userAccount" filled by userAccount
      And    Field "seller" filled by seller
      Then   The subscription is updated