Feature: CON-42919 Perform task Authorise Intelligence Report Delete Request

  @draft
  Scenario: Perform task Authorise Intelligence Report Delete Request with Authorize deletion as Yes with status of 'LIVE'
    #BS-INCLUDE feature=CON-42866_AbilityToRequestForDeletionOfIntelligenceReport.feature, scenario=Perform task Delete Intelligence Report Request in valid statuses with status of 'LIVE'
    Given an Intelligence Report exists with status of 'LIVE'
    And a task 'Authorise Intelligence Report Delete Request' exists against that 'Intelligence Report'
    And the client is eligible to perform the task
    # Adding Log of Entries Details
    And the client provides a Report Log Entry
    And the client provides 'Entry Index' as '2'
    And the client provides 'Entry Text' as 'Intel Logs'
    And the client provides 'Entry Date' as '2018-08-14'
    And the client provides 'Entry Time' as '09:20:31.735'
    And the client provides 'Entered By Officer Id' as '20'
    And the client provides 'Entry Type' as 'RESEARCH NOTES'
    # Here we are submitting Task fields
    And the client provides 'Authorize IR Deletion' as 'True'
    #manual check by QA 'static text' as 'Any actions to delete this report cannot be undone'
    # Now we are checking the outcome
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to 'DELETED'
    #Task History detail assertions
    And a new Task History Entry will be added to the 'Intelligence Report'
    And the Task History Entry will record 'Authorize IR Deletion' as 'True'
    
   @draft
  Scenario: Perform task Authorise Intelligence Report Delete Request with Authorize deletion as Yes with status of 'LIVE SENSITIVE'
    #BS-INCLUDE feature=CON-42866_AbilityToRequestForDeletionOfIntelligenceReport.feature, scenario=User should be able to perform 'Link Intelligence Report' task for Sensitive Intelligence Report, if atleast one object link to Intel report  in REQUIRES LINKING SENSITIVE status
    Given an Intelligence Report exists with status of 'LIVE SENSITIVE'
    And a task 'Authorise Intelligence Report Delete Request' exists against that 'Intelligence Report'
    And the client is eligible to perform the task
    # Adding Log of Entries Details
    And the client provides a Report Log Entry
    And the client provides 'Entry Index' as '2'
    And the client provides 'Entry Text' as 'Intel Logs'
    And the client provides 'Entry Date' as '2018-08-14'
    And the client provides 'Entry Time' as '09:20:31.735'
    And the client provides 'Entered By Officer Id' as '20'
    And the client provides 'Entry Type' as 'PNC'
    # Here we are submitting Task fields
    And the client provides 'Authorize IR Deletion' as 'True'
     #manual check by QA 'static text' as 'Any actions to delete this report cannot be undone'
    # Now we are checking the outcome
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to 'DELETED'
    #Task History detail assertions
    And a new Task History Entry will be added to the 'Intelligence Report'
    And the Task History Entry will record 'Authorize IR Deletion' as 'True'

  @draft
  Scenario: Perform task Authorise Intelligence Report Delete Request with Authorize deletion as No
    #BS-INCLUDE feature=CON-42866_AbilityToRequestForDeletionOfIntelligenceReport.feature, scenario=Perform task Delete Intelligence Report Request in valid statuses with status of 'LIVE'
    Given an Intelligence Report exists with status of 'LIVE'
    And a task 'Authorise Intelligence Report Delete Request' exists against that 'Intelligence Report'
    And the client is eligible to perform the task
    # Adding Log of Entries Details
    And the client provides a Report Log Entry
    And the client provides 'Entry Index' as '2'
    And the client provides 'Entry Text' as 'Intel Logs'
    And the client provides 'Entry Date' as '2018-08-14'
    And the client provides 'Entry Time' as '09:20:31.735'
    And the client provides 'Entered By Officer Id' as '20'
    And the client provides 'Entry Type' as 'PNC'
    # Here we are submitting Task fields
    And the client provides 'Authorize IR Deletion' as 'False'
    #manual check by QA 'static text' as 'Any actions to delete this report cannot be undone'
    And the client provides 'Reason for Rejection' as 'Intel should not be rejected'
    # Now we are checking the outcome
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to 'LIVE'
    #Task History detail assertions
    And the Task History Entry will record 'Authorize IR Deletion' as 'False'
    And the Task History Entry will record 'Reason for Rejection' as 'Intel should not be rejected'
   
  @draft
  Scenario: Perform task Authorise Intelligence Report Delete Request with blank Rejection Reason
    #BS-INCLUDE feature=CON-42866_AbilityToRequestForDeletionOfIntelligenceReport.feature, scenario=Perform task Delete Intelligence Report Request in valid statuses with status of 'LIVE'
    Given an Intelligence Report exists with status of 'LIVE'
    And a task 'Authorise Intelligence Report Delete Request' exists against that 'Intelligence Report'
    And the client is eligible to perform the task
    # Adding Log of Entries Details
    And the client provides a Report Log Entry
    And the client provides 'Entry Index' as '2'
    And the client provides 'Entry Text' as 'Intel Logs'
    And the client provides 'Entry Date' as '2018-08-14'
    And the client provides 'Entry Time' as '09:20:31.735'
    And the client provides 'Entered By Officer Id' as '20'
    And the client provides 'Entry Type' as 'PNC'
    # Here we are submitting Task fields
    And the client provides 'Authorize IR Deletion' as 'False'
    #manual check by QA 'static text' as 'Any actions to delete this report cannot be undone'
    # Now we are checking the outcome
    When the client submits the Intelligence Report to Business Services
    Then Error will be returned for field 'Reason for Rejection' with message 'Reason for Rejection must have a value'
