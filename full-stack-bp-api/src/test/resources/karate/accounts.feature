Feature: Accounts API

  Background:
    * url baseUrl
    * def ownerId = 'KAR-ACC-OWNER'
    # Pre-create a client to own the test accounts. Tolerate 409 if a previous run left it.
    Given path '/clients'
    And request
      """
      {
        "clientId": "#(ownerId)",
        "name": "Account Owner",
        "gender": "MALE",
        "age": 35,
        "identification": "KAR-ID-ACC-001",
        "password": "pwd1234",
        "status": "ACTIVE"
      }
      """
    When method POST
    Then assert responseStatus == 201 || responseStatus == 409

  Scenario: Create, retrieve, update and delete an account
    Given path '/accounts'
    And request
      """
      {
        "accountNumber": "KAR-ACC-001",
        "accountType": "SAVINGS",
        "initialBalance": 500.00,
        "status": "ACTIVE",
        "clientId": "#(ownerId)"
      }
      """
    When method POST
    Then status 201
    And match response.accountNumber == 'KAR-ACC-001'
    And match response.currentBalance == 500.00
    And match response.clientId == ownerId
    * def accountId = response.id

    Given path '/accounts', accountId
    When method GET
    Then status 200

    Given path '/accounts', accountId
    And request { status: 'INACTIVE' }
    When method PUT
    Then status 200
    And match response.status == 'INACTIVE'

    Given path '/accounts', accountId
    When method DELETE
    Then status 204

  Scenario: Creating an account for a non-existent client returns 404
    Given path '/accounts'
    And request
      """
      {
        "accountNumber": "KAR-ACC-FAIL",
        "accountType": "CHECKING",
        "initialBalance": 100,
        "status": "ACTIVE",
        "clientId": "GHOST-CLIENT"
      }
      """
    When method POST
    Then status 404
    And match response.message contains "GHOST-CLIENT"
