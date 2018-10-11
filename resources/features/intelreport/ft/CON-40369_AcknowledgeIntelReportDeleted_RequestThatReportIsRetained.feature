Feature: CON-40369 Perform task 'Acknowledge Intelligence Report Returned Or Deleted' with Action taken as 'Request That Report Is Retained'

   Scenario: User should be able to perform task 'Acknowledge Intelligence Report Returned Or Deleted' with Action taken as 'Request That Report Is Retained'
    #BS-INCLUDE feature=CON-42856_AssessIntelligenceReport_ReturnedForDeletion.feature, scenario=User is able to perform task "Assess Intelligence Report" with Action taken 'Returned for deletion' with status of 'REQUIRES ASSESSMENT'
    Given an Intelligence Report exists with status of 'TO BE DELETED'
    And a task 'Acknowledge Intelligence Report Returned Or Deleted' exists against that Intelligence Report
    And system parameter INTEL_SENSITIVE_ACTIONCODE contains ${bs.cucumber.intelSensitiveActionCode}
    And the client is eligible to perform the task
    # Below data will be supplied in txData
    And the client provides 'Action Taken' as 'REQUEST REPORT RETAINED'
    And the client provides 'Reason For Retention' as 'This is reason for retention'
    # childObject name="reportLogEntry"
    And the client provides a 'Report Log Entry'
    And the client provides 'Entry Index' as '1'
    And the client provides 'Entry Date' as 'today'
    And the client provides 'Entry Time' as '09:20:31.735'
    And the client provides 'Entered By Officer Id' as '20'
    And the client provides 'Entry Type' as 'RES'
    And the client provides 'Entry Text' as 'test for log entries'
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to 'REQUIRES ASSESSMENT'
    And a new task 'Assess Intelligence Report' is created for unit ${bs.cucumber.unit}
    And a new Task History Entry will be added to the Intelligence report
    And the Task History Entry will record 'Action Taken' as 'REQUEST REPORT RETAINED'
    And the Task History Entry will record 'Retention Reason' as 'This is reason for retention'
  
    Scenario: User should be able to perform task 'Acknowledge Intelligence Report Returned Or Deleted' with Action taken as 'Request That Report Is Retained' for Intel Report present in Sensitive status
    #BS-INCLUDE feature=CON-42856_AssessIntelligenceReport_ReturnedForDeletion.feature, scenario=User is able to perform task "Assess Intelligence Report" with Action taken 'Returned for deletion' with status of 'REQUIRES ASSESSMENT SENSITIVE'
    Given an Intelligence Report exists with status of 'TO BE DELETED SENSITIVE'
    And a task 'Acknowledge Intelligence Report Returned Or Deleted' exists against that Intelligence Report
    And system parameter INTEL_SENSITIVE_ACTIONCODE contains ${bs.cucumber.intelSensitiveActionCode}
    And the client is eligible to perform the task
    # Below data will be supplied in txData
    And the client provides 'Action Taken' as 'REQUEST REPORT RETAINED'
    And the client provides 'Reason For Retention' as 'This is reason for retention'
    # childObject name="reportLogEntry"
    And the client provides a 'Report Log Entry'
    And the client provides 'Entry Index' as '1'
    And the client provides 'Entry Date' as 'today'
    And the client provides 'Entry Time' as '09:20:31.735'
    And the client provides 'Entered By Officer Id' as '20'
    And the client provides 'Entry Type' as 'RES'
    And the client provides 'Entry Text' as 'test for log entries'
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to 'REQUIRES ASSESSMENT SENSITIVE'
    And a new task 'Assess Intelligence Report' is created for unit ${bs.cucumber.unit}
    And a new Task History Entry will be added to the Intelligence report
    And the Task History Entry will record 'Action Taken' as 'REQUEST REPORT RETAINED'
    And the Task History Entry will record 'Retention Reason' as 'This is reason for retention'
