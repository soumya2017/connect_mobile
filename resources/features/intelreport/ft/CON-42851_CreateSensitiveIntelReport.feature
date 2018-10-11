Feature: CON-42851 Submit Sensitive Intelligence Report
	
    Scenario:  'REQUIRES LINKING SENSITIVE'
   #BS-INCLUDE feature=CON-42859_AssessIntelligenceReport_Accepted.feature, scenario=Happy Path status of 'REQUIRES ASSESSMENT SENSITIVE'
    Given an Intelligence Report exists with status of 'REQUIRES LINKING SENSITIVE'
    And a task 'Link Intelligence Report' exists against that Intelligence Report
    And system parameter INTEL_SENSITIVE_ACTIONCODE contains ${bs.cucumber.intelSensitiveActionCode}
    # txData
    And the client is eligible to perform the task
    And the client provides 'Confirm linking complete' as 'true'
    # childObject name="reportLogEntry"
    And the client provides a 'Report Log Entry'
    And the client provides 'Entry Index' as '1'
    And the client provides 'Entry Date' as '14/09/2017'
    And the client provides 'Entry Time' as '09:20:31.735'
    And the client provides 'Entered By Officer Id' as '20'
    And the client provides 'Entry Type' as 'PNC'
    And the client provides 'Entry Text' as 'test for log entries'
    
    # details of person static object,Its a child object
    And the client links a 'New Person' referred to as 'FirstSubject' with link reason of 'SUBJECT OF' 
    And the client provides data on 'FirstSubject'
    And the client provides 'Forename1' as 'Mark'
    And the client provides 'Surname' as 'Taylor'
    And the client provides 'Date Of Birth' as '14/09/1991'
    And the client provides 'Place Of Birth' as 'London'
    
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to 'LIVE SENSITIVE'
    And a new Task History Entry will be added to the Intelligence report
    And a link will be added to the 'Intelligence Report' for 'Person' referred to as 'FirstSubject' with link reason of 'SUBJECT OF'
	And 'FirstSubject' has 'Forename1' as 'Mark' 
	And 'FirstSubject' has 'Surname' as 'Taylor' 
	And 'FirstSubject' has 'Date Of Birth' as '1991-09-14' 
	And 'FirstSubject' has 'Place Of Birth' as 'London'
	
	
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
   
    Scenario: Perform task 'Complete Intelligence Report' with action taken as 'Accepted' with status of 'REQUIRES COMPLETION SENSITIVE'
    #BS-INCLUDE feature=CON-42855_SubmitIncompleteIntelligenceReport.feature, scenario=Happy Path for Create Intelligence Report In Require Completion Sensitive status
    Given an Intelligence Report exists with status of 'REQUIRES COMPLETION SENSITIVE'
    And a task 'Complete Intelligence Report' exists against that Intelligence Report
    And system parameter INTEL_SENSITIVE_ACTIONCODE contains ${bs.cucumber.intelSensitiveActionCode}
    And the client is eligible to perform the task
    # Below data will be supplied in txData
    And the client provides 'Submit To Information Manager' as 'AMO'
    And the client provides 'Linking Unit' as 'AMO'
    And the client provides 'Action Taken' as 'COMPLETED'
    When the client submits the Intelligence Report to Business Services
    # Below are the outcomes of the task performed    
    Then a new task 'Assess Intelligence Report' is created for unit ${bs.cucumber.unit}
    And a new Task History Entry will be added to the 'Intelligence Report'
    And the Task History Entry will record 'Action Taken' as 'COMPLETED'
    And the Task History Entry will record 'PIRManagerActorName' as 'AMO'
    And the Task History Entry will record 'PIRIndexerActorName' as 'AMO'  
    And the Intelligence Report will have its Status changed to 'REQUIRES ASSESSMENT SENSITIVE'
    
 
