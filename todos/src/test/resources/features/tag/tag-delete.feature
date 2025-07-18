
Feature: BDD Scenarios of Tag API - Delete Tag

  Background:
    Given table tag contains data:
      | id                                   | name     |
      | 550e8400-e29b-41d4-a716-446655440001 | Java     |
      | 550e8400-e29b-41d4-a716-446655440002 | Spring   |

  Scenario: Delete an existing tag should return 204
    When tag call delete tag with id="550e8400-e29b-41d4-a716-446655440001"
    Then the tag response status is 204

  Scenario: Delete a non existing tag should return 404
    When tag call delete tag with id="660e8400-e29b-41d4-a716-446655440999"
    Then the tag response status is 404

