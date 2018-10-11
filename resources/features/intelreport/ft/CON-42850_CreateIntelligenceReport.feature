Feature: CON-42850 Submit (Create) Intelligence Report

  #Below are the happy path to use in chaining.
  Scenario: Happy Path for Create Intelligence Report In Require Assessment status
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
    And the client provides 'Completed' as 'Yes'
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
    Then the Intelligence Report will have its Status changed to 'REQUIRES ASSESSMENT'
    And a new task 'Assess Intelligence Report' is created for unit ${bs.cucumber.unit}
    And a new Task History Entry will be added to the 'Intelligence Report'
    And Intelligence Report contains 'Confidence Level' as '3'

  Scenario: Happy Path for Create Intelligence Report In Require Assessment Sensitive status
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
    And the client provides 'Completed' as 'Yes'
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
    And system parameter INTEL_SENSITIVE_ACTIONCODE contains ${bs.cucumber.intelSensitiveActionCode}
    When the client submits the 'Intelligence Report' to Business Services
    Then the Intelligence Report will have its Status changed to 'REQUIRES ASSESSMENT SENSITIVE'
    And a new task 'Assess Intelligence Report' is created for unit 'AMO'
    And a new Task History Entry will be added to the 'Intelligence Report'
    And Intelligence Report contains 'Confidence Level' as '3'

  #Below are the edge case scenarios for Create Intelligence report
  Scenario Outline: Ability to Submit an Intelligence Report for mandatory/conditional mandatory checks and verify Confidence Levels
    Given client opted to perform action 'Create' on an 'Intelligence Report'
    And the client provides 'Title' as 'IntelReport'
    And the client provides 'Date Submitted' as '16/06/2017'
    And the client provides 'Time Submitted' as '09:20:31.735'
    And the client provides 'Priority' as '1'
    And the client provides 'Force' as '42'
    And the client provides 'Owning Force Id' as ${bs.cucumber.owningForceId}
    And the client provides 'UnsanHandling' as <HandlingCode>
    And the client provides 'Unsanitised Handling Conditions Reason' as <HandlingReason>
    And the client provides 'UnsanHandling Conditions' as <HandlingCondition>
    And the client provides 'Unsanitised Action Code' as <ActionCode>
    And the client provides 'Unsanitised Action A3 Usage' as <ActionA3Usage>
    And the client provides 'Submission Sanitisation Code' as <Sanitisation>
    And the client provides 'Dissemination Recommendations' as 'officer comments'
    And the client provides 'Provenance' as 'aaaa'
    And the client provides 'Shared With' as 'bbbb'
    And the client provides 'First Know By Source' as 'cccc'
    And the client provides 'Last Know By Source' as 'dddd'
    And the client provides 'More Information From Source' as 'eeeee'
    And the client provides 'Submit To Information Manager' as 'AMO'
    And the client provides 'Submit To Linking Unit' as ${bs.cucumber.unit}
    #txData
    And the client provides 'Force Id' as ${bs.cucumber.forceId}
    And the client provides 'Completed' as 'Yes'
    And the client provides 'Submitting Officer Id' as ${bs.cucumber.employeeId}
    And the client provides 'Submitting Officer Display Value' as '20'
    And the client provides 'Priority Display Value' as 'priority1'
    And the client provides an Intelligence Information Type
    And the client provides 'Type' as 'KBOGUS'
    And the client provides an Intelligence Source
    And the client provides 'Source Type' as <SourceType>
    And the client provides 'Source Officer' as <SourceOfficer>
    And the client provides an Unsanitised Text
    And the client provides 'Text' as 'My first intel report to submit'
    And the client provides 'Source' as <SourceEvaluation>
    And the client provides 'Unsanitised Evaluation Source Not Reliable' as <reasonNotReliable>
    And the client provides 'Evaluation' as <intelAssessment>
    And the client provides 'Unsanitised Evaluation Information Reason' as <reasonForFalseSuspection>
    And the client provides an Intelligence Risk
    And the client provides 'Public Immunity Indicator' as <indicator>
    And the client provides 'Risk To Source Safety' as <Risk>
    And the client provides 'Risk To Source Why' as <riskreason>
    And the client provides 'How Risk Reduced' as <ReducedRisk>
    And the client provides 'Should Be Sensitive' as <isSensitive>
    And the client provides 'Sensitive' as <sensitive>
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to 'REQUIRES ASSESSMENT'
    And the task will be performed
    And a new task 'Assess Intelligence Report' is created for unit ${bs.cucumber.unit}
    And a new Task History Entry will be added to the 'Intelligence Report'
     And Intelligence Report contains 'Confidence Level' as <Level>

    Examples: 
      | SourceType | SourceOfficer | SourceEvaluation | reasonNotReliable | intelAssessment | reasonForFalseSuspection | HandlingCode | HandlingReason | HandlingCondition | ActionCode | ActionA3Usage | Sanitisation | Indicator | Risk | riskreason | ReducedRisk       | isSensitive | sensitive | Level |
      | M          |               |                1 |                   | A               |                          | P            |                |                   |            |               |              | T         | T    | For Test   | By taking actions | T           | T         |     3 |
      | P          |         39238 |                3 | test abc          | E               | False info               | C            | Test xyz       | Test pqr          | A3         | A3 code use   | S1           | F         | F    |            |                   | F           | F         |     1 |
      | P          |         39238 |                2 |                   | B               |                          | C            | Test xyz       | Test pqr          | A3         | A3 code use   | S1           | F         | F    |            |                   | F           | F         |     2 |
      | P          |         39238 |                1 |                   | C               |                          | C            | Test xyz       | Test pqr          | A3         | A3 code use   | S1           | F         | F    |            |                   | F           | F         |     2 |
      | P          |         39238 |                3 | test abc          | D               |                          | C            | Test xyz       | Test pqr          | A3         | A3 code use   | S1           | F         | F    |            |                   | F           | F         |     1 |
      | P          |         39238 |                2 |                   | E               | False info               | C            | Test xyz       | Test pqr          | A3         | A3 code use   | S1           | F         | F    |            |                   | F           | F         |     1 |
      | P          |         39238 |                2 |                   | D               |                          | C            | Test xyz       | Test pqr          | A3         | A3 code use   | S1           | F         | F    |            |                   | F           | F         |     1 |
      | P          |         39238 |                1 |                   | B               |                          | C            | Test xyz       | Test pqr          | A3         | A3 code use   | S1           | F         | F    |            |                   | F           | F         |     3 |
      | P          |         39238 |                3 | test abc          | D               |                          | C            | Test xyz       | Test pqr          | A3         | A3 code use   | S1           | F         | F    |            |                   | F           | F         |     1 |

  @Generic @VivekD @EdgeCase
  Scenario Outline: Submit an Intelligence Report and check the validation for Conditional mandatory fields
    Given client opted to perform action 'Create' on an 'Intelligence Report'
    And the client provides 'Date Submitted' as '16/06/2017'
    And the client provides 'Time Submitted' as '09:20:31.735'
    And the client provides 'Priority' as '1'
    And the client provides 'Force' as 42
    And the client provides 'Owning Force Id' as ${bs.cucumber.owningForceId}
    And the client provides 'Bcu' as 'ESSEX NORTH'
    And the client provides 'Station Code' as 'CHELMSFORD'
    And the client provides 'Beat Code' as 'CHELMSFORD E05004096'
    And the client provides 'UnsanHandling' as <HandlingCode>
    And the client provides 'Unsanitised Handling Conditions Reason' as <HandlingReason>
    And the client provides 'UnsanHandling Conditions' as <HandlingCondition>
    And the client provides 'Unsanitised Action Code' as <ActionCode>
    And the client provides 'Unsanitised Action A3 Usage' as <ActionA3Usage>
    And the client provides 'Submission Sanitisation Code' as <Sanitisation>
    And the client provides 'Dissemination Recommendations' as 'officer comments'
    And the client provides 'Provenance' as 'aaaa'
    And the client provides 'Shared With' as 'bbbb'
    And the client provides 'First Know By Source' as 'cccc'
    And the client provides 'Last Know By Source' as 'dddd'
    And the client provides 'More Information From Source' as 'eeeee'
    And the client provides 'Submit To Information Manager' as 'AMO'
    And the client provides 'Submit To Linking Unit' as ${bs.cucumber.unit}
    #txData
    And the client provides 'Completed' as 'Yes'
    And the client provides 'Submitting Officer Id' as ${bs.cucumber.employeeId}
    And the client provides 'Submitting Officer Display Value' as '20'
    And the client provides 'Priority Display Value' as 'priority1'
    And the client provides an Intelligence Information Type
    And the client provides 'Type' as 'KBOGUS'
    And the client provides an Intelligence Source
    And the client provides 'Source Type' as <SourceType>
    And the client provides 'Source Officer' as <SourceOfficer>
    And the client provides an Unsanitised Text
    And the client provides 'text' as 'My first intel report to submit'
    And the client provides 'Source' as <SourceEvaluation>
    And the client provides 'Unsanitised Evaluation Source Not Reliable' as <reasonNotReliable>
    And the client provides 'Evaluation' as <intelAssessment>
    And the client provides 'Unsanitised Evaluation Information Reason' as <reasonForFalseSuspection>
    And the client provides an Intelligence Risk
    And the client provides 'Public Immunity Indicator' as <indicator>
    And the client provides 'Risk To Source Safety' as <risk>
    And the client provides 'Risk To Source Why' as <riskreason>
    And the client provides 'How Risk Reduced' as <ReducedRisk>
    And the client provides 'Should Be Sensitive' as <isSensitive>
    And the client provides 'Sensitive' as <sensitive>
    When the client submits the Intelligence Report to Business Services
    Then Error will be returned for field 'Title' with message 'Title must have a value'

    Examples: 
      | SourceType | SourceOfficer | SourceEvaluation | reasonNotReliable | intelAssessment | reasonForFalseSuspection | HandlingCode | HandlingReason | HandlingCondition | ActionCode | ActionA3Usage | Sanitisation | Indicator | Risk | riskreason | ReducedRisk       | isSensitive | sensitive |
      | PUBLIC     |               |                1 |                   | A               |                          | P            |                |                   |            |               |              | T         | T    | For Test   | By taking actions | T           | T         |
