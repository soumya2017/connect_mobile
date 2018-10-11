Feature: CON-42866 Perform task Delete Intelligence Report Request and check the outcome

@draft 
  Scenario: Perform task Delete Intelligence Report Request in valid statuses with status of 'LIVE'
    #BS-INCLUDE feature=CON-42860_LinkIntelligenceReport.feature, scenario=Happy Path status of 'REQUIRES LINKING'
    Given an Intelligence Report exists with status of 'LIVE'
    And the client runs the task 'Delete Intelligence Report Request'
    # Adding Log of Entries Details
    And the client provides a Report Log Entry
    And the client provides 'Entry Index' as '2'
    And the client provides 'Entry Text' as 'Intel Logs'
    And the client provides 'Entry Date' as 'today'
    And the client provides 'Entry Time' as '09:20:31.735'
    And the client provides 'Entered By Officer Id' as ${bs.cucumber.employeeId}
    And the client provides 'Entry Type' as 'PNC'
    # Here we are providing Transient data for the task performed
    And the client provides 'Intelligence Report To Be Deleted' as 'True'
    And the client provides 'Reason For Deletion' as 'No Longer needed'
    And the client provides 'Nominated Manager' as 'Frank Shunneltestone'
    # Now the task will be performed
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to 'LIVE'
    And a new task 'Authorise Intelligence Report Delete Request' is created for unit 'AMO'
    #Task History detail assertions
    And a new Task History Entry will be added to the 'Intelligence Report'
    And the Task History Entry will record 'IR To Be Deleted' as 'T'
    And the Task History Entry will record NominatedManager as 'Frank Shunneltestone'
@draft 
  Scenario: Perform task Delete Intelligence Report Request in valid statuses with status of 'LIVE SENSITIVE'
    #BS-INCLUDE feature=CON-42860_LinkIntelligenceReport.feature, scenario=Happy Path status of 'REQUIRES LINKING SENSITIVE'
    Given an Intelligence Report exists with status of 'LIVE SENSITIVE'
    And the client runs the task 'Delete Intelligence Report Request'
    # Adding Log of Entries Details
    And the client provides a Report Log Entry
    And the client provides 'Entry Index' as '2'
    And the client provides 'Entry Text' as 'Intel Logs'
    And the client provides 'Entry Date' as 'today'
    And the client provides 'Entry Time' as '09:20:31.735'
    And the client provides 'Entered By Officer Id' as ${bs.cucumber.employeeId}
    And the client provides 'Entry Type' as 'PNC'
    # Here we are providing Transient data for the task performed
   And the client provides 'Intelligence Report To Be Deleted' as 'True'
    And the client provides 'Reason For Deletion' as 'No Longer needed'
    And the client provides 'Nominated Manager' as 'Frank Shunneltestone'
    # Now the task will be performed
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to 'LIVE SENSITIVE'
    And a new task 'Authorise Intelligence Report Delete Request' is created for unit 'AMO'
    #Task History detail assertions
    And a new Task History Entry will be added to the 'Intelligence Report'
    And the Task History Entry will record 'IR To Be Deleted' as 'T'
    And the Task History Entry will record NominatedManager as 'Frank Shunneltestone'

@draft 
  Scenario: Perform task Delete Intelligence Report Request when Authorise Intelligence Report Delete Request is already present
    #BS-INCLUDE feature=CON-42866_AbilityToRequestForDeletionOfIntelligenceReport.feature, scenario=Perform task Delete Intelligence Report Request in valid statuses with status of 'LIVE'
    Given an Intelligence Report exists with status of 'LIVE'
    #And <existingtaskname> is not performed
    And the client runs the task 'Delete Intelligence Report Request'
    # Adding Log of Entries Details
    And the client provides a Report Log Entry
    And the client provides 'Entry Index' as '2'
    And the client provides 'Entry Text' as 'Intel Logs'
    And the client provides 'Entry Date' as 'today'
    And the client provides 'Entry Time' as '09:20:31.735'
    And the client provides 'Entered By Officer Id' as ${bs.cucumber.employeeId}
    And the client provides 'Entry Type' as 'PNC'
    # Here we are providing Transient data for the task performed
   And the client provides 'Intelligence Report To Be Deleted' as 'True'
    And the client provides 'Reason For Deletion' as 'No Longer needed'
     And the client provides 'Nominated Manager' as 'Frank Shunneltestone'
    # Now the task will be performed
    When the client submits the Intelligence Report to Business Services
   	Then Error will be returned containing 'Error Message'

  # Below are workflow parameters used and passed in above scripts
@draft 
  Scenario: Perform task Delete Intelligence Report Request in valid statuses
    #BS-INCLUDE feature=CON-42860_LinkIntelligenceReport.feature, scenario=Happy Path status of 'REQUIRES LINKING SENSITIVE'
    Given an Intelligence Report exists with status of 'LIVE SENSITIVE'
    And the client runs the task 'Delete Intelligence Report Request'
    # Adding Log of Entries Details
    And the client provides a Report Log Entry
    And the client provides 'Entry Index' as '2'
    And the client provides 'Entry Text' as 'Intel Logs'
    And the client provides 'Entry Date' as '2018-08-14'
    And the client provides 'Entry Time' as '09:20:31.735'
    And the client provides 'Entered By Officer Id' as ${bs.cucumber.employeeId}
    And the client provides 'Entry Type' as 'PNC'
    # Here we are providing Transient data for the task performed
    And the client provides 'IR To Be Deleted' as True
    And the client provides 'Reason For Deletion' as 'No Longer needed'
    And the client provides 'Nominated Manager' as 'Frank Shunneltestone'
    # Now the task will be performed
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to 'LIVE SENSITIVE'
    And a new task 'Authorise Intelligence Report Delete Request' is created for unit 'AMO'
    #Task History detail assertions
    And a new Task History Entry will be added to the 'Intelligence Report'
      And the Task History Entry will record 'IR To Be Deleted' as 'T'
    And the Task History Entry will record NominatedManager as 'Frank Shunneltestone'

@draft 
  Scenario Outline: Perform task Delete Intelligence Report Request for invalid values in fields
    #BS-INCLUDE feature=CON-42860_LinkIntelligenceReport.feature, scenario=Happy Path status of 'REQUIRES LINKING'
    Given an Intelligence Report exists with status of <inStatus>
    And the client runs the task <currentTaskName>
    # Adding Log of Entries Details
    And the client provides a Report Log Entry
    And the client provides 'Entry Index' as '2'
    And the client provides 'Entry Text' as 'Intel Logs'
    And the client provides 'Entry Date' as 'today'
    And the client provides 'Entry Time' as '09:20:31.735'
    And the client provides 'Entered By Officer Id' as ${bs.cucumber.employeeId}
    And the client provides 'Entry Type' as 'RES'
    # Here we are providing Transient data for the task performed
     And the client provides 'Intelligence Report To Be Deleted' as 'True'
    And the client provides 'Reason For Deletion' as <reasondeletion>
    And the client provides 'Nominated Manager' as 'Frank Shunneltestone'
    # Now the task will be performed
    When the client submits the Intelligence Report to Business Services
    Then Error will be returned for field 'enteredByOfficerId' with message 'enteredByOfficerId must have a value'

    # Below are workflow parameters used and passed in above scripts
    Examples: 
      | instatus       | reasondeletion   | outstatus      | currentTaskName                    | EOffID |  newTaskName                                  |
      | LIVE           | No Longer needed | LIVE           | Delete Intelligence Report Request |        |  Authorise Intelligence Report Delete Request |

@draft 
  Scenario Outline: Perform task Delete Intelligence Report Request for blank 'Reason For Deletion' and 'Nominate Manager To Authorize Request'
    #BS-INCLUDE feature=CON-42860_LinkIntelligenceReport.feature, scenario=Happy Path status of 'REQUIRES LINKING'
    Given an Intelligence Report exists with status of <inStatus>
    And the client runs the task <currentTaskName>
    # Adding Log of Entries Details
    And the client provides a Report Log Entry
    And the client provides 'Entry Index' as '2'
    And the client provides 'Entry Text' as 'Intel Logs'
    And the client provides 'Entry Date' as 'today'
    And the client provides 'Entry Time' as '09:20:31.735'
    And the client provides 'Entered By Officer Id' as ${bs.cucumber.employeeId}
    And the client provides 'Entry Type' as 'RES'
    # Here we are providing Transient data for the task performed
      And the client provides 'Intelligence Report To Be Deleted' as 'True'
    And the client provides 'Reason For Deletion' as <reasondeletion>
    # Now the task will be performed
    When the client submits the Intelligence Report to Business Services
    Then Error will be returned for field 'reasondeletion' with message 'reasondeletion must have a value'
    Then Error will be returned for field 'Nominated Manager' with message 'nominatedManager must have a value'

    # Below are workflow parameters used and passed in above scripts
    Examples: 
      | instatus       | reasondeletion | outstatus      | currentTaskName                    | EOffID                   |  newTaskName                                  |
      | LIVE           |                | LIVE           | Delete Intelligence Report Request | SUPT 42 ARM753 DESHPANDE |  Authorise Intelligence Report Delete Request |
