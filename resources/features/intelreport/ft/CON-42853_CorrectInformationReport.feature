Feature: CON-42853 Perform task 'Correct Intelligence Report' with status 'REQUIRES CORRECTION'

  Scenario: Happy Path with status of 'REQUIRES CORRECTION'
    #BS-INCLUDE feature=CON-42852_AssessIntelligenceReport_ReturnedForCorrection.feature, scenario=Happy Path Action Taken as Returned for Correction with status of 'REQUIRES ASSESSMENT'
    Given an Intelligence Report exists with status of 'REQUIRES CORRECTION'
    And a task 'Correct Intelligence Report' exists against that Intelligence Report
    # Glue code will check if there are no validation errors
    And the client is eligible to perform the task
    # Providing Task Details
    And the client provides 'Confirm Report Corrected' as 'Yes'
    # Now the task will be performed
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to 'REQUIRES ASSESSMENT'
    And a new task 'Assess Intelligence Report' is created for unit ${bs.cucumber.unit}
    And a new Task History Entry will be added to the 'Intelligence Report'

  #No output parameters to assert in task history.
  Scenario: Happy Path with status of 'REQUIRES CORRECTION SENSITIVE'
    #BS-INCLUDE feature=CON-42852_AssessIntelligenceReport_ReturnedForCorrection.feature, scenario=Happy Path Action Taken as Returned for Correction with status of 'REQUIRES ASSESSMENT SENSITIVE'
    Given an Intelligence Report exists with status of 'REQUIRES CORRECTION SENSITIVE'
    And a task 'Correct Intelligence Report' exists against that Intelligence Report
    # Glue code will check if there are no validation errors
    And the client is eligible to perform the task
    # Providing Task Details
    And the client provides 'Confirm Report Corrected' as 'Yes'
    # Now the task will be performed
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to 'REQUIRES ASSESSMENT SENSITIVE'
    And a new task 'Assess Intelligence Report' is created for unit ${bs.cucumber.unit}
    And a new Task History Entry will be added to the 'Intelligence Report'

  #No output parameters to assert in task history.
  Scenario: Run the Correct Intelligence Report task by adding Log of Enquiries
    #BS-INCLUDE feature=CON-42852_AssessIntelligenceReport_ReturnedForCorrection.feature, scenario=Happy Path Action Taken as Returned for Correction with status of 'REQUIRES ASSESSMENT'
    Given an Intelligence Report exists with status of 'REQUIRES CORRECTION'
    And a task 'Correct Intelligence Report' exists against that Intelligence Report
    And the client is eligible to perform the task
    #txData
    And the client provides 'Confirm Report Corrected' as 'Yes'
    #childObject
    And the client provides a Report Log Entry
    And the client provides 'Entry Index' as '2'
    And the client provides 'Entry Text' as 'Intel Logs'
    And the client provides 'Entry Date' as 'today'
    And the client provides 'Entry Time' as '09:20:31.735'
    And the client provides 'Entered By Officer Id' as '20'
    And the client provides 'Entry Type' as 'PNC'
    # Now the task will be performed
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to 'REQUIRES ASSESSMENT'
    And a new task 'Assess Intelligence Report' is created for unit ${bs.cucumber.unit}
    And a new Task History Entry will be added to the 'Intelligence Report'
