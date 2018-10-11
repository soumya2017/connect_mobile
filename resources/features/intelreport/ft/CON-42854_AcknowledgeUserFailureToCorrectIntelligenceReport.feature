Feature: CON-42854 Perform task 'Acknowledge User Failure to Correct Intel Report'

  Scenario: Run the Acknowledge user failure to correct intelligence report task and check the outcome
  #BS-INCLUDE feature=CON-42852_AssessIntelligenceReport_ReturnedForCorrection.feature, scenario=Perform Assess Intelligence Report task with Action Taken as Returned for Correction for Resubmission date as todays date with status of 'REQUIRES ASSESSMENT'
    Given an Intelligence Report exists with status of 'REQUIRES CORRECTION'
    And a task 'Acknowledge User Failure To Correct Intelligence Report' exists against that Intelligence Report
    And the client is eligible to perform the task
    # Providing workflow fields
    And the client provides 'Acknowledge Report Not Corrected' as 'True'
    And the client provides 'Resubmission Date' as 'today'
    # Now the task will be performed
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to 'REQUIRES CORRECTION'
    And a new Task History Entry will be added to the Intelligence Report
    #Task History details assertions - No output parameters. So nothing to assert.   

  Scenario: Run the Acknowledge user failure to correct intelligence report task by adding Log of Enquiries
  #BS-INCLUDE feature=CON-42852_AssessIntelligenceReport_ReturnedForCorrection.feature, scenario=Perform Assess Intelligence Report task with Action Taken as Returned for Correction for Resubmission date as todays date with status of 'REQUIRES ASSESSMENT'
    Given an Intelligence Report exists with status of 'REQUIRES CORRECTION'
     And a task 'Acknowledge User Failure To Correct Intelligence Report' exists against that Intelligence Report
    And the client is eligible to perform the task  
    # Adding Log of Enquiries while executing
    And the client provides a Report Log Entry
    And the client provides 'Entry Index' as '2'
    And the client provides 'Entry Text' as 'Intel Logs'
    And the client provides 'Entry Date' as 'today'
    And the client provides 'Entry Time' as '09:20:31.735'
    And the client provides 'Entered By Officer Id' as ${bs.cucumber.employeeId}
    And the client provides 'Entry Type' as 'RES'
    # Providing workflow fields
    And the client provides 'Acknowledge Report Not Corrected' as 'True'
    # Now the task will be performed
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to 'REQUIRES CORRECTION'
    And a new Task History Entry will be added to the Intelligence Report
    #Task History details assertions - No output parameters. So nothing to assert.    

  Scenario Outline: Run the Acknowledge user failure to correct intelligence report task by passing invalid fields
  #BS-INCLUDE feature=CON-42852_AssessIntelligenceReport_ReturnedForCorrection.feature, scenario=Perform Assess Intelligence Report task with Action Taken as Returned for Correction for Resubmission date as todays date with status of 'REQUIRES ASSESSMENT'
    Given an Intelligence Report exists with status of 'REQUIRES CORRECTION'
    And a task <existingTaskName> exists against that Intelligence Report
     And the client is eligible to perform the task      
    # Adding Log of Enquiries while executing
    And the client provides a Report Log Entry
    And the client provides 'Entry Index' as '2'
    And the client provides 'Entry Text' as 'Intel Logs'
    And the client provides 'Entry Date' as 'today'
    And the client provides 'Entry Time' as '09:20:31.735'
    And the client provides 'Entered By Officer Id' as <EOffID>
    And the client provides 'Entry Type' as 'RES'
    # Providing workflow fields
    # Now the task will be performed
    When the client submits the Intelligence Report to Business Services
    Then Error will be returned for field 'enteredByOfficerId' with message 'enteredByOfficerId must have a value'
    Then Error will be returned for field 'acknowledgereportnotcorrected' with message 'acknowledgereportnotcorrected must have a value'

    # Below are workflow parameters used and passed in above scripts
    Examples: 
      | inStatus            | outStatus           | existingTaskName                                        | PIR             | EOffID | Flag |
      | REQUIRES CORRECTION | REQUIRES CORRECTION | Acknowledge User Failure To Correct Intelligence Report | Vivek Deshpande |        |      |
