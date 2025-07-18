
Feature:  BDD Scenarios of Tag API - Update Tag

  Background:
    Given table tag contains data:
      | id                                   | name      |
      | 17a281a6-0882-4460-9d95-9c28f5852db1 | Java      |
      | 18a81a6-0882-4460-9d95-9c28f5852db1  | Spring    |

  Scenario: Update an existing tag should return 202
    And the following tag to update:
      | name      |
      | DevOps    |
    When tag call update tag with id="17a281a6-0882-4460-9d95-9c28f5852db1"
    Then the tag response status is 202


  Scenario: Update a non existing tag should return 404
    And the following tag to update:
      | name      |
      | DevOps    |
    When tag call update tag with id="27a281a6-0882-4460-9d95-9c28f5852db1"
    Then the tag response status is 404


  Scenario Outline: Update tag with name not matching size constraints should return 400
    And the following tag to update:
      | name       |
      | <name>     |
    When tag call update tag with id="17a281a6-0882-4460-9d95-9c28f5852db1"
    Then the tag response status is 400

    Examples:
      | name                                                                                         |
      | J                                                                                             |
      | Review and finalize the quarterly financial report before submission to the board of directors |

  Scenario: Update tag with null name should return 400
    And the following tag to update:
      | name  |
      | null  |
    When tag call update tag with id="17a281a6-0882-4460-9d95-9c28f5852db1"
    Then the tag response status is 400

