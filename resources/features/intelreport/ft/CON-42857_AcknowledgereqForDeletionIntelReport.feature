Feature: CON-42857 Perform task Acknowledge request for deletion of Intelligence report
  
  Scenario: Perform Acknowledge request for deletion of Intelligence report task and check the outcome
   #BS-INCLUDE feature=CON-42856_AssessIntelligenceReport_ReturnedForDeletion.feature, scenario=User is able to perform task "Assess Intelligence Report" with Action taken 'Returned for deletion' with status of 'REQUIRES ASSESSMENT'
    Given an Intelligence Report exists with status of 'TO BE DELETED'
    And a task 'Acknowledge Intelligence Report Returned Or Deleted' exists against that Intelligence Report
    # Glue code will check if there are no validation errors
    And the client is eligible to perform the task
    # Providing workflow fields
    And the client provides 'Action Taken' as 'ACCEPT REJECTION'
    # Now the task will be performed
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to 'DELETED'

  Scenario: Perform Acknowledge request for deletion of Intelligence report task for sensitivity check
  #BS-INCLUDE feature=CON-42856_AssessIntelligenceReport_ReturnedForDeletion.feature, scenario=User is able to perform task "Assess Intelligence Report" with Action taken 'Returned for deletion' with status of 'REQUIRES ASSESSMENT SENSITIVE'
    Given an Intelligence Report exists with status of 'TO BE DELETED SENSITIVE'
    And a task 'Acknowledge Intelligence Report Returned Or Deleted' exists against that Intelligence Report
    # Glue code will check if there are no validation errors
    And the client is eligible to perform the task
    #System Parameter is set
    And system parameter INTEL_SENSITIVE_ACTIONCODE contains ${bs.cucumber.intelSensitiveActionCode}
    # Providing workflow fields
    And the client provides 'Action Taken' as 'ACCEPT REJECTION'
    # Now the task will be performed
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to 'DELETED'
  
  Scenario: Perform Acknowledge request for deletion of Intelligence report task by adding Log of Enquiries
  #BS-INCLUDE feature=CON-42856_AssessIntelligenceReport_ReturnedForDeletion.feature, scenario=User is able to perform task "Assess Intelligence Report" with Action taken 'Returned for deletion' with status of 'REQUIRES ASSESSMENT'
    Given an Intelligence Report exists with status of 'TO BE DELETED'
    And a task 'Acknowledge Intelligence Report Returned Or Deleted' exists against that Intelligence Report
    # Adding Log of Enquiries while executing
      And the client provides a Report Log Entry
    And the client provides 'Entry Index' as '2'
    And the client provides 'Entry Text' as 'Intel Logs'
    And the client provides 'Entry Date' as 'today'
    And the client provides 'Entry Time' as '09:20:31.735'
    And the client provides 'Entered By Officer Id' as '20'
    And the client provides 'Entry Type' as 'RES'
    # Glue code will check if there are no validation errors
    And the client is eligible to perform the task
    # Providing workflow fields
    And the client provides 'Action Taken' as 'ACCEPT REJECTION'
    # Now the task will be performed
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to 'DELETED'
    #And the task will be performed

  Scenario: Perform Acknowledge request for deletion of Intelligence report task by passing invalid fields
  #BS-INCLUDE feature=CON-42856_AssessIntelligenceReport_ReturnedForDeletion.feature, scenario=User is able to perform task "Assess Intelligence Report" with Action taken 'Returned for deletion' with status of 'REQUIRES ASSESSMENT'
    Given an Intelligence Report exists with status of 'TO BE DELETED'
    And a task 'Acknowledge Intelligence Report Returned Or Deleted' exists against that Intelligence Report
    # Adding Log of Enquiries while executing
    And the client provides a Report Log Entry
    And the client provides 'Entry Index' as '2'
    And the client provides 'Entry Text' as 'Intel Logs'
    And the client provides 'Entry Date' as 'today'
    And the client provides 'Entry Time' as '09:20:31.735'
    And the client provides 'Entered By Officer Id' as '20'
    And the client provides 'Entry Type' as 'PNC'
    # Glue code will check if there are no validation errors
    And the client is eligible to perform the task
    # Providing workflow fields
    # Now the task will be performed
    When the client submits the Intelligence Report to Business Services
    Then Error will be returned for field 'ActionTaken' with message 'ActionTaken must have a value'

   
