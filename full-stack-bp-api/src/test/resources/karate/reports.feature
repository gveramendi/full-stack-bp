Feature: Account statement report

  Background:
    * url baseUrl
    * def ownerId = 'KAR-REP-OWNER'
    Given path '/clients'
    And request
      """
      {
        "clientId": "#(ownerId)",
        "name": "Report Tester",
        "gender": "FEMALE",
        "age": 28,
        "identification": "KAR-ID-REP-001",
        "password": "pwd1234",
        "status": "ACTIVE"
      }
      """
    When method POST
    Then assert responseStatus == 201 || responseStatus == 409

  Scenario: Generate the statement and verify totals + base64 PDF
    # Create an account and one movement to populate the statement
    Given path '/accounts'
    And request
      """
      { "accountNumber": "KAR-REP-ACC", "accountType": "SAVINGS",
        "initialBalance": 1000.00, "status": "ACTIVE", "clientId": "#(ownerId)" }
      """
    When method POST
    Then status 201
    * def accountId = response.id

    Given path '/movements'
    And request { accountId: '#(accountId)', movementType: 'DEPOSIT', amount: 250.00 }
    When method POST
    Then status 201

    # Request the statement
    Given path '/reports'
    And params { clientId: '#(ownerId)', from: '2026-01-01', to: '2026-12-31' }
    When method GET
    Then status 200
    And match response.clientId == ownerId
    And match response.accounts == '#array'
    # The PDF must be present and start with the magic header (%PDF-, base64-encoded → "JVBE")
    And match response.pdfBase64 == '#string'
    And match response.pdfBase64 == '#regex JVBER.*'
    # Account totals must include the deposit
    * def myAccount = karate.filter(response.accounts, function(a){ return a.accountNumber == 'KAR-REP-ACC' })
    And match myAccount[0].totalCredits == 250.00
