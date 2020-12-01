@ignore
Feature: Testing method CRUD (Create, Read, Update, Delete) on entity Seller

   Background:  System is configured.

   Scenario: Create a seller

Given  Create a seller
When   Field "code" is filled by a code
And    Field "description" is filled by a description
And    Field "tradingCurrency" is filled by a tradingCurrency
Then   A seller is created
And    I see success message

   Scenario: Read a seller

Given  Read a seller
When   Field "id" is filled by an id
Then   The seller is read
And    I see success message

   Scenario: Update a seller

Given  Update a seller
When   Field "code" is filled by a code,
And    Field "description" is filled by a description
Then   The seller is updated
And    I see success message

   Scenario: Delete a seller

Given  Delete a seller
When   Field "code " is filled in with a code
Then   The seller is deleted
And    I see success  <message>