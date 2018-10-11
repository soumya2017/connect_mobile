#Author: abhishek.mishra@northgateps.com
Feature: CON-42855 Ability to submit an incomplete Intelligence report

 # In below scenario user should be able to submit the Intelligence Report so that it can be picked up for completion at a later point of time by the Intel Submitter.
 Scenario: Happy Path for Create Intelligence Report In Require Completion status
    Given client opted to perform action 'Create' on an 'Intelligence Report'
    And the client provides 'Title' as 'IntelReport'
    And the client provides 'Date Submitted' as '16/06/2017'
    And the client provides 'Time Submitted' as '09:20:31.735'
    And the client provides 'Priority' as '1'
    And the client provides 'Force' as '42'
    And the client provides 'Owning Force Id' as ${bs.cucumber.owningForceId}
    And the client provides 'Bcu' as 'ESSEX NORTH'
    And the client provides 'Station Code' as 'CHELMSFORD'
    And the client provides 'Beat Code' as 'CHELMSFORD E05004096'
    And the client provides 'UnsanHandling' as 'C'
    And the client provides 'UnsanHandling Conditions' as 'Careful'
    And the client provides 'Unsanitised Handling Conditions Reason' as 'Instructions'
    And the client provides 'Unsanitised Action Code' as 'A1'
    And the client provides 'Submission Sanitisation Code' as 'S1'
    And the client provides 'Provenance' as 'dummy'
    And the client provides 'Shared With' as 'dummy'
    And the client provides 'First Know By Source' as 'dummy'
    And the client provides 'Last Know By Source' as 'dummy'
    And the client provides 'More Information From Source' as 'dummy'
    #txData
    And the client provides 'Force Id' as ${bs.cucumber.forceId}
    And the client provides 'Submit To Information Manager' as 'AMO'
    And the client provides 'Submit To Linking Unit' as ${bs.cucumber.unit}
    And the client provides 'Submitting Officer Id' as ${bs.cucumber.employeeId}
    And the client provides 'Submitting Officer Display Value' as '20'
    And the client provides 'Completed' as 'No'
    And the client provides 'Priority Display Value' as 'priority1'
    And the client provides an Intelligence Information Type
    And the client provides 'Type' as 'KBOGUS'
    And the client provides an Intelligence Source
    And the client provides 'Source Type' as 'OL'
    And the client provides 'Other Force Officer Force' as '52'
    And the client provides 'Other Force Officer Details' as 'Officer Details'
    And the client provides an Unsanitised Text
    And the client provides 'Evaluation' as 'A'
    And the client provides 'Text' as 'My first intel report to submit'
    And the client provides 'Source' as '1'
    And the client provides an Intelligence Risk
    And the client provides 'Public Immunity Indicator' as 'F'
    And the client provides 'Risk To Source Safety' as 'F'
    And the client provides 'Should Be Sensitive' as 'F'
    And the client provides 'Sensitive' as 'F'
    When the client submits the 'Intelligence Report' to Business Services
    Then the Intelligence Report will have its Status changed to 'REQUIRES COMPLETION'
    And a new task 'Complete Intelligence Report' is created for unit ${bs.cucumber.unit}
    And a new Task History Entry will be added to the 'Intelligence Report'
    And Intelligence Report contains 'Confidence Level' as '3'

  Scenario: Happy Path for Create Intelligence Report In Require Completion Sensitive status
    Given client opted to perform action 'Create' on an 'Intelligence Report'
    And the client provides 'Title' as 'IntelReport'
    And the client provides 'Date Submitted' as '16/06/2017'
    And the client provides 'Time Submitted' as '09:20:31.735'
    And the client provides 'Priority' as '1'
    And the client provides 'Force' as '42'
    And the client provides 'Owning Force Id' as ${bs.cucumber.owningForceId}
    And the client provides 'Bcu' as 'ESSEX NORTH'
    And the client provides 'Station Code' as 'CHELMSFORD'
    And the client provides 'Beat Code' as 'CHELMSFORD E05004096'
    And the client provides 'UnsanHandling' as 'C'
    And the client provides 'UnsanHandling Conditions' as 'Careful'
    And the client provides 'Unsanitised Handling Conditions Reason' as 'Instructions'
    And the client provides 'Unsanitised Action Code' as 'A2'
    And the client provides 'Submission Sanitisation Code' as 'S1'
    And the client provides 'Provenance' as 'dummy'
    And the client provides 'Shared With' as 'dummy'
    And the client provides 'First Know By Source' as 'dummy'
    And the client provides 'Last Know By Source' as 'dummy'
    And the client provides 'More Information From Source' as 'dummy'
    #txData
    And the client provides 'Force Id' as ${bs.cucumber.forceId}
    And the client provides 'Submit To Information Manager' as 'AMO'
    And the client provides 'Submit To Linking Unit' as ${bs.cucumber.unit}
    And the client provides 'Submitting Officer Id' as ${bs.cucumber.employeeId}
    And the client provides 'Submitting Officer Display Value' as '20'
    And the client provides 'Completed' as 'No'
    And the client provides 'Priority Display Value' as 'priority1'
    And the client provides an Intelligence Information Type
    And the client provides 'Type' as 'KBOGUS'
    And the client provides an Intelligence Source
    And the client provides 'Source Type' as 'OL'
    And the client provides 'Other Force Officer Force' as '52'
    And the client provides 'Other Force Officer Details' as 'Officer Details'
    And the client provides an Unsanitised Text
    And the client provides 'Evaluation' as 'A'
    And the client provides 'Text' as 'My first intel report to submit'
    And the client provides 'Source' as '1'
    And the client provides an Intelligence Risk
    And the client provides 'Public Immunity Indicator' as 'F'
    And the client provides 'Risk To Source Safety' as 'F'
    And the client provides 'Should Be Sensitive' as 'F'
    And the client provides 'Sensitive' as 'F'
    And system parameter INTEL_SENSITIVE_ACTIONCODE contains 'A2'
    When the client submits the 'Intelligence Report' to Business Services
    Then the Intelligence Report will have its Status changed to 'REQUIRES COMPLETION SENSITIVE'
    And a new task 'Complete Intelligence Report' is created for unit ${bs.cucumber.unit}
    And a new Task History Entry will be added to the 'Intelligence Report'
    And Intelligence Report contains 'Confidence Level' as '3'