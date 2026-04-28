Feature: Movement business rules (sign convention, insufficient balance, daily limit)

  Background:
    * url baseUrl
    * def ownerId = 'KAR-MOV-OWNER'
    Given path '/clients'
    And request
      """
      {
        "clientId": "#(ownerId)",
        "name": "Movement Tester",
        "gender": "MALE",
        "age": 30,
        "identification": "KAR-ID-MOV-001",
        "password": "pwd1234",
        "status": "ACTIVE"
      }
      """
    When method POST
    Then assert responseStatus == 201 || responseStatus == 409

  Scenario: DEPOSIT increases balance and stores positive value
    Given path '/accounts'
    And request
      """
      { "accountNumber": "KAR-MOV-DEP",   "accountType": "SAVINGS",
        "initialBalance": 100.00, "status": "ACTIVE", "clientId": "#(ownerId)" }
      """
    When method POST
    Then status 201
    * def accountId = response.id

    Given path '/movements'
    And request { accountId: '#(accountId)', movementType: 'DEPOSIT', amount: 50.00 }
    When method POST
    Then status 201
    And match response.movementType == 'DEPOSIT'
    And match response.value == 50.00
    And match response.balance == 150.00

  Scenario: WITHDRAWAL stores a negative value and decreases balance
    Given path '/accounts'
    And request
      """
      { "accountNumber": "KAR-MOV-WD",    "accountType": "SAVINGS",
        "initialBalance": 500.00, "status": "ACTIVE", "clientId": "#(ownerId)" }
      """
    When method POST
    Then status 201
    * def accountId = response.id

    Given path '/movements'
    And request { accountId: '#(accountId)', movementType: 'WITHDRAWAL', amount: 200.00 }
    When method POST
    Then status 201
    And match response.movementType == 'WITHDRAWAL'
    And match response.value == -200.00
    And match response.balance == 300.00

  Scenario: WITHDRAWAL exceeding current balance returns 422 "Insufficient balance"
    Given path '/accounts'
    And request
      """
      { "accountNumber": "KAR-MOV-INS",   "accountType": "SAVINGS",
        "initialBalance": 50.00, "status": "ACTIVE", "clientId": "#(ownerId)" }
      """
    When method POST
    Then status 201
    * def accountId = response.id

    Given path '/movements'
    And request { accountId: '#(accountId)', movementType: 'WITHDRAWAL', amount: 100.00 }
    When method POST
    Then status 422
    And match response.message == 'Insufficient balance'

  Scenario: Daily withdrawal limit ($1000) is enforced per account
    Given path '/accounts'
    And request
      """
      { "accountNumber": "KAR-MOV-DAILY", "accountType": "SAVINGS",
        "initialBalance": 5000.00, "status": "ACTIVE", "clientId": "#(ownerId)" }
      """
    When method POST
    Then status 201
    * def accountId = response.id

    # First withdrawal of 800 — within limit
    Given path '/movements'
    And request { accountId: '#(accountId)', movementType: 'WITHDRAWAL', amount: 800.00 }
    When method POST
    Then status 201

    # Second withdrawal of 300 — would total 1100, exceeds the $1000/day cap
    Given path '/movements'
    And request { accountId: '#(accountId)', movementType: 'WITHDRAWAL', amount: 300.00 }
    When method POST
    Then status 422
    And match response.message == 'Daily withdrawal limit exceeded'

    # Same-day deposit is still allowed (the limit only restricts withdrawals)
    Given path '/movements'
    And request { accountId: '#(accountId)', movementType: 'DEPOSIT', amount: 1000.00 }
    When method POST
    Then status 201
