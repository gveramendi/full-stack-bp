Feature: Clients API

  Background:
    * url baseUrl

  Scenario: Full CRUD lifecycle of a client
    # Create
    Given path '/clients'
    And request
      """
      {
        "clientId": "KAR-CLI-001",
        "name": "Karate Tester",
        "gender": "OTHER",
        "age": 30,
        "identification": "KAR-ID-001",
        "address": "Test address",
        "phone": "555-0001",
        "password": "pwd1234",
        "status": "ACTIVE"
      }
      """
    When method POST
    Then status 201
    And match response.clientId == 'KAR-CLI-001'
    And match response.name == 'Karate Tester'
    # Password must never be returned
    And match response contains { password: '#notpresent' }

    # Get by id
    Given path '/clients', 'KAR-CLI-001'
    When method GET
    Then status 200
    And match response.identification == 'KAR-ID-001'

    # Patch (status change)
    Given path '/clients', 'KAR-CLI-001'
    And request { phone: '555-9999', status: 'INACTIVE' }
    When method PATCH
    Then status 200
    And match response.phone == '555-9999'
    And match response.status == 'INACTIVE'

    # Delete
    Given path '/clients', 'KAR-CLI-001'
    When method DELETE
    Then status 204

    # Confirm deletion
    Given path '/clients', 'KAR-CLI-001'
    When method GET
    Then status 404

  Scenario: Validation error on missing required fields
    Given path '/clients'
    And request { name: 'No clientId', gender: 'OTHER', age: 25 }
    When method POST
    Then status 400
    And match response.error == 'Bad Request'
    And match response.message == 'Validation failed'
    And match response.issues == '#[_ > 0]'

  Scenario: Get a non-existent client returns 404
    Given path '/clients', 'GHOST-CLIENT'
    When method GET
    Then status 404
    And match response.message contains 'not found'
