Feature: CON-46191 Perform task Acknowledge rejection of briefing item task

    Scenario: Perform task Acknowledge rejection of briefing item and check the outcome
    #BS-INCLUDE feature=CON-46190_ProcessRequestToCreateBriefing.feature,scenario=Perform task Process Request To Create Briefing for Rejected flow
    Given an Intelligence Report exists with status of 'REQUIRES ASSESSMENT'
    And a task 'Acknowledge Rejection Of Briefing Item' exists against that 'Intelligence Report'
    And the client is eligible to perform the task  
    # Adding Log of Entries Details
    And the client provides a 'Report Log Entry'
    And the client provides 'Entry Index' as '2'
    And the client provides 'Entry Text' as 'Intel Logs'
    And the client provides 'Entry Date' as 'today'
    And the client provides 'Entry Time' as '09:20:31.735'
    And the client provides 'Entered By Officer Id' as '20'
    And the client provides 'Entry Type' as 'PNC'
    # Here we are providing Transient data for the task performed
    And the client provides 'Acknowledge Rejection' as 'True'
    #And the client provides 'Briefing Unit' as 'AMO'
    # Now the task will be performed
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to 'REQUIRES ASSESSMENT'
    #Task History detail assertions
	  And a new Task History Entry will be added to the Intelligence Report
   
@draft
  Scenario: Perform task Acknowledge rejection of briefing item by passing invalid fields
    #BS-INCLUDE feature=CON-46190_ProcessRequestToCreateBriefing.feature,scenario=Perform task Process Request To Create Briefing for Rejected flow
    Given an Intelligence Report exists with status of 'REQUIRES ASSESSMENT'
     And a task 'Acknowledge Rejection Of Briefing Item' exists against that 'Intelligence Report'
    # Adding Log of Entries Details
    And the client provides a 'Report Log Entry'
    And the client provides 'Entry Index' as '2'
    And the client provides 'Entry Text' as 'Intel Logs'
    And the client provides 'Entry Date' as 'today'
    And the client provides 'Entry Time' as '09:20:31.735'
    And the client provides 'Entered By Officer Id' as '20'
    And the client provides 'Entry Type' as 'PNC'
    # Here we are providing Transient data for the task performed
   And the client provides 'Briefing Unit' as 'AMO'
    # Now the task will be performed
    When the client submits the Intelligence Report to Business Services
    Then Error will be returned for field 'Acknowledge Rejection' with message 'acknowledgeRejection must have a value'

  