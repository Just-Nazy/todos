
Feature:  BDD Scenarios of Tag API - Find Tag By Id

  Background:
    Given table tag contains data:
      | id                                   | name      |
      | 17a281a6-0882-4460-9d95-9c28f5852db1 | Java      |
      | 18a81a6-0882-4460-9d95-9c28f5852db1  | Spring    |

  Scenario: Find by existing id should return corresponding tag
    When tag call find tag by id with id="17a281a6-0882-4460-9d95-9c28f5852db1"
    Then the tag response status is 200


  Scenario: Find by id with a non existing id should return 404
    When tag call find tag by id with id="27a281a6-0882-4460-9d95-9c28f5852db1"
    Then the tag response status is 404

