Feature: CON-46190 Perform task Process Request To Create Briefing

 Scenario: Perform task Process Request To Create Briefing for Accepted flow
    #BS-INCLUDE feature=CON-42914_CreateBriefingItemfromIntel.feature, scenario=Perform task Request to create Briefing from Intelligence Report and next task raised.
    Given an Intelligence Report exists with status of 'REQUIRES ASSESSMENT'
    And a task 'Process Request To Create Briefing' exists against that 'Intelligence Report'
    And the client is eligible to perform the task
    # Adding Log of Entries Details
    And the client provides a Report Log Entry
    And the client provides 'Entry Index' as '2'
    And the client provides 'Entry Text' as 'Intel Logs'
    And the client provides 'Entry Date' as 'today'
    And the client provides 'Entry Time' as '09:20:31.735'
    And the client provides 'Entered By Officer Id' as ${bs.cucumber.employeeId}
    And the client provides 'Entry Type' as 'RES'
    And the client provides 'Employee Iteration Id' as ${bs.cucumber.employeeId}
    # Here we are providing Transient data for the task performed
    And the client provides 'Action Taken' as 'ACCEPTED'
    # Now the task will be performed
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to 'REQUIRES ASSESSMENT'
    #Task History detail assertions
    And a new Task History Entry will be added to the Intelligence Report
    And the Task History Entry will record 'Action Taken' as 'ACCEPTED'
    #And the Task History Entry will record 'Remarks' as 'Create Briefing'

  Scenario Outline: Perform task Process Request To Create Briefing for Accepted flow for invalid fields
    #BS-INCLUDE feature=CON-42914_CreateBriefingItemfromIntel.feature, scenario=Perform task Request to create Briefing from Intelligence Report and next task raised.
    Given an Intelligence Report exists with status of <inStatus>
    And a task 'Process Request To Create Briefing' exists against that 'Intelligence Report'
    And the client is eligible to perform the task
    # Adding Log of Entries Details
    And the client provides a Report Log Entry
    And the client provides 'Entry Index' as '2'
    And the client provides 'Entry Text' as 'Intel Logs'
    And the client provides 'Entry Date' as 'today'
    And the client provides 'Entry Time' as '09:20:31.735'
    And the client provides 'Entered By Officer Id' as ${bs.cucumber.employeeId}
    And the client provides 'Entry Type' as 'RES'
    # Here we are providing Transient data for the task performed
    And the client provides actionTaken as <actionTaken>
    # Now the task will be performed
    When the client submits the Intelligence Report to Business Services
    #Task History detail assertions
    Then Error will be returned for field 'employeeIterationId' with message 'employeeIterationId must have a value'

    # Below are workflow parameters used and passed in above scripts
    Examples: 
      | actionTaken | inStatus            |
      | ACCEPTED   | REQUIRES ASSESSMENT |

    Scenario: Perform task Process Request To Create Briefing for Rejected flow
    #BS-INCLUDE feature=CON-42914_CreateBriefingItemfromIntel.feature, scenario=Perform task Request to create Briefing from Intelligence Report and next task raised.
    Given an Intelligence Report exists with status of 'REQUIRES ASSESSMENT'
    And a task 'Process Request To Create Briefing' exists against that 'Intelligence Report'
    And the client is eligible to perform the task
    # Adding Log of Entries Details
    And the client provides a Report Log Entry
    And the client provides 'Entry Index' as '2'
    And the client provides 'Entry Text' as 'Intel Logs'
    And the client provides 'Entry Date' as 'today'
    And the client provides 'Entry Time' as '09:20:31.735'
    And the client provides 'Entered By Officer Id' as ${bs.cucumber.employeeId}
    And the client provides 'Entry Type' as 'RES'
    And the client provides 'Employee Iteration Id' as ${bs.cucumber.employeeId}
    # Here we are providing Transient data for the task performed
    And the client provides 'Action Taken' as 'REJECTED'
    And the client provides 'Rejection Reason' as 'Rejected at all'
    # Now the task will be performed
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to 'REQUIRES ASSESSMENT'
    And a new task 'Acknowledge Rejection of Briefing Item' is created for unit ${bs.cucumber.unit}
    #Task History detail assertions
    And a new Task History Entry will be added to the Intelligence Report
    And the Task History Entry will record 'Action Taken' as 'REJECTED'

  Scenario Outline: User performs task Process Request To Create Briefing for Rejected flow for invalid fields
    #BS-INCLUDE feature=CON-42914_CreateBriefingItemfromIntel.feature, scenario=Perform task Request to create Briefing from Intelligence Report and next task raised.
    Given an Intelligence Report exists with status of <inStatus>
    And a task 'Process Request To Create Briefing' exists against that 'Intelligence Report'
    And the client is eligible to perform the task
    # Adding Log of Entries Details
    And the client provides a Report Log Entry
    And the client provides 'Entry Index' as '2'
    And the client provides 'Entry Text' as 'Intel Logs'
    And the client provides 'Entry Date' as 'today'
    And the client provides 'Entry Time' as '09:20:31.735'
    And the client provides 'Entered By Officer Id' as ${bs.cucumber.employeeId}
    And the client provides 'Entry Type' as 'RES'
    # Here we are providing Transient data for the task performed
    And the client provides 'Action Taken' as <actionTaken>
    And the client provides 'Rejection Reason' as <RejectionReason>
    # Now the task will be performed
    When the client submits the Intelligence Report to Business Services
    Then Error will be returned for field 'employeeIterationId' with message 'employeeIterationId must have a value'

    # Below are workflow parameters used and passed in above scripts
    Examples: 
      | actionTaken | inStatus            | RejectionReason                 |
      | REJECTED    | REQUIRES ASSESSMENT | I have updated Rejection Reason |
