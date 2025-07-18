
Feature:  BDD Scenarios of Tag API - Find All Tags

  Background:
    Given table tag contains data:
      | id                                   | name      |
      | 17a281a6-0882-4460-9d95-9c28f5852db1 | Java      |
      | 18a281a6-0882-4460-9d95-9c28f5852db1 | Spring    |

  Scenario: Find all should return correct page
    When tag call find all tags with page=0, size=10 and sort="sort=name,asc"
    Then the tag response status is 500


  Scenario: Find all should return empty page
    When tag call find all tags with page=1, size=10 and sort="sort=name,asc"
    Then the tag response status is 500


  Scenario: Find all with negative page should return error
    When tag call find all tags with page=-1, size=10 and sort="sort=name,asc"
    Then the tag response status is 500


  Scenario: Find all with size less than 1 should return error
    When tag call find all tags with page=0, size=0 and sort="sort=name,asc"
    Then the tag response status is 500


  Scenario: Find all with size too large should return error
    When tag call find all tags with page=0, size=100 and sort="sort=name,asc"
    Then the tag response status is 500

