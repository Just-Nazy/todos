
Feature:  BDD Scenarios of Tag API - Add Tag

  Background:
    Given table tag contains data:
      | id | name |

  Scenario: Add tag should return 201
    And the following tag to add:
      | name        |
      | Java Expert |
    When tag call add tag
    Then the tag response status is 201


  Scenario Outline: Add tag with name not matching size constraints should return 400
    And the following tag to add:
      | name   |
      | <name> |
    When tag call add tag
    Then the tag response status is 400
    Examples:
      | name                                                                                     |
      | J                                                                                        |
      | VeryLongTagNameThatExceedsTheMaximumAllowedCharacterLimitForTagValidation               |

  Scenario: Add tag with null name should return 400
    And the following tag to add:
      | name |
      | null |
    When tag call add tag
    Then the tag response status is 400

  Scenario: Add tag with empty name should return 400
    And the following tag to add:
      | name |
      |      |
    When tag call add tag
    Then the tag response status is 400


  Scenario: Add tag with name containing only spaces should return 400
    And the following tag to add:
      | name |
      |      |
    When tag call add tag
    Then the tag response status is 400


  Scenario: Add duplicate tag should return 40
    Given table tag contains data:
      | id                                   | name        |
      | 550e8400-e29b-41d4-a716-446655440001 | Java Expert |
    And the following tag to add:
      | name        |
      | Java Expert |
    When tag call add tag
    Then the tag response status is 409

