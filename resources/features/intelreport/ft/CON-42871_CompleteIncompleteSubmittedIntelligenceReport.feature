Feature: CON-42871 Perform task 'Complete Intelligence Report'
   
   Scenario: Perform task 'Complete Intelligence Report' with action taken as 'Accepted' with status of 'REQUIRES COMPLETION'
   #BS-INCLUDE feature=CON-42855_SubmitIncompleteIntelligenceReport.feature, scenario=Happy Path for Create Intelligence Report In Require Completion status
    Given an Intelligence Report exists with status of 'REQUIRES COMPLETION'
    And a task 'Complete Intelligence Report' exists against that Intelligence Report
    And system parameter INTEL_SENSITIVE_ACTIONCODE contains ${bs.cucumber.intelSensitiveActionCode}
    And the client is eligible to perform the task  
    # Below data will be supplied in txData
    And the client provides 'Action taken' as 'COMPLETED'
    And the client provides 'Submit To Information Manager' as 'AMO'
    And the client provides 'Linking Unit' as 'AMO'
    When the client submits the Intelligence Report to Business Services
    # Below are the outcomes of the task performed 
    And a new Task History Entry will be added to the 'Intelligence Report'   
    Then a new task 'Assess Intelligence Report' is created for unit 'AMO'          
    And the Task History Entry will record 'Action taken' as 'COMPLETED'
    And the Task History Entry will record 'PIRManagerActorName' as 'AMO'
    And the Task History Entry will record 'PIRIndexerActorName' as 'AMO'        
    And the Intelligence Report will have its Status changed to 'REQUIRES ASSESSMENT'
    
     
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

  @draft
  Scenario Outline: Perform task 'Complete Intelligence Report' with action taken as 'Accepted' with status of 'REQUIRES COMPLETION' when user chnages the values in intelligence report.
   #BS-INCLUDE feature=CON-42855_SubmitIncompleteIntelligenceReport.feature, scenario=Happy Path for Create Intelligence Report In Require Completion status
    Given an Intelligence Report exists with status of 'REQUIRES COMPLETION'
    And a task 'Complete Intelligence Report' exists against that Intelligence Report
    And system parameter INTEL_SENSITIVE_ACTIONCODE contains ${bs.cucumber.intelSensitiveActionCode}
    And system parameter INTEL_SOURCE_EVALUATION_VALUE_TO_ENABLE_FIELDS contains ${bs.cucumber.intelSourceEvaluationValueToEnableFields}
    And system parameter INTEL_INTELLIGENCE_ASSESSMENT_VALUE_TO_ENABLE_FIELDS contains ${bs.cucumber.intelIntelligenceAssessmentValueToEnableFields}
    And system parameter INTEL_HANDLING_CODE_VALUE_TO_ENABLE_FIELDS contains ${bs.cucumber.intelHandlingCodeValueToEnableFields}
    And the client is eligible to perform the task
    And the client provides 'Source Type' as <SourceOfIntel>
    And the client provides 'Source Officer' as <CollarId>
    And the client provides 'Other Force Officer Force' as <OtherForce>
    And the client provides 'Other Force Officer Details' as <OfficerDetails>
    And the client provides 'Other Source' as <OtherSource>
    #Handling Code related info
    And the client provides 'Un San Handling' as 'C'
    And the client provides 'Unsanitised Handling Conditions Reason' as 'These are handling conditions'
    And the client provides 'Un San Handling Conditions' as 'These are detailed handling conditions'
    #Additional info
    And the client provides 'Dissemination Recommendations' as 'This is additional information'
    #Provenance related info,Mandatory for Completed flag-True
    And the client provides 'Provenance' as 'Source have seen something very suspicious'
    And the client provides 'Shared With' as 'His parents'
    And the client provides 'First Know By Source' as 'On the day before Christmas i.e 24/12/2017'
    And the client provides 'Last Know By Source' as 'Today also he knows everything'
    And the client provides 'More Information From Source' as 'Yes, he can provide more information'
    #Action Code, Mandatory if Handling code is C
    And the client provides 'Unsanitised Action Code' as 'A3'
    And the client provides 'Information Sanitised Action Code A3 Usage' as 'Usage instructions for A3 action code'
    And the client provides 'Submission Sanitisation Code' as 'S1'
    # Below data will be supplied in txData
    And the client provides 'Action Taken' as 'COMPLETED'
    And the client provides 'Submit To Information Manager' as 'AMO'
    And the client provides 'Submit To Linking Unit' as 'AMO'
    #childObject name="reportUnsanitisedText"
    And the client provides a 'Report Unsanitised Text'
    And the client provides 'Text' as 'This is submission text'
    And the client provides 'Source' as '1'
    And the client provides 'Evaluation' as 'A'
    #childObject name="reportIntelligenceRisk"
    And the client provides a 'Report Intelligence Risk'
    And the client provides 'Public Immunity Indicator' as 'true'
    And the client provides 'Risk To Source Safety' as 'true'
    And the client provides 'Risk To Source Why' as 'He is very rich'
    And the client provides 'How Risk Reduced' as 'By giving police protection'
    And the client provides 'Should Be Sensitive' as 'true'
    And the client provides 'Sensitive' as 'true'
    When the client submits the Intelligence Report to Business Services
    # Below are the outcomes of the task performed
    And a new Task History Entry will be added to the Intelligence Report
    Then the Intelligence Report will have its Status changed to <OutStatus>
    And a new task <NewTaskName> is created for unit ${bs.cucumber.unit}
    And a new Task History Entry will be added to the Intelligence report
    And the Task History Entry will record 'Action Taken' as 'COMPLETED'
    And the Task History Entry will record 'PIRManagerActorName' as 'AMO'
    And the Task History Entry will record 'PIRIndexerActorName' as 'AMO'

    Examples: 
      | SourceOfIntel | CollarId | OtherForce | OfficerDetails | OtherSource    | OutStatus           | NewTaskName                |
      | M             |          |            |                |                | REQUIRES ASSESSMENT | Assess Intelligence report |
      | P             |    40920 |            |                |                | REQUIRES ASSESSMENT | Assess Intelligence report |
      | OS            |          |            |                | Mr. Jack Wilson| REQUIRES ASSESSMENT | Assess Intelligence report |
      | OL            |          |         46 | Insp John Smith|                | REQUIRES ASSESSMENT | Assess Intelligence report |
      
  @draft
  Scenario Outline: Perform task 'Complete Intelligence Report' with action taken as 'Accepted' for Sensitive Intel Report with status of 'REQUIRES COMPLETION SENSITIVE' when user chnages the values in intelligence report.
  #BS-INCLUDE feature=CON-42855_SubmitIncompleteIntelligenceReport.feature, scenario=Happy Path for Create Intelligence Report In Require Completion status
    Given an Intelligence Report exists with status of 'REQUIRES COMPLETION SENSITIVE'
    And a task 'Complete Intelligence Report' exists against that Intelligence Report
   And system parameter INTEL_SENSITIVE_ACTIONCODE contains ${bs.cucumber.intelSensitiveActionCode}
    And system parameter INTEL_SOURCE_EVALUATION_VALUE_TO_ENABLE_FIELDS contains ${bs.cucumber.intelSourceEvaluationValueToEnableFields}
    And system parameter INTEL_INTELLIGENCE_ASSESSMENT_VALUE_TO_ENABLE_FIELDS contains ${bs.cucumber.intelIntelligenceAssessmentValueToEnableFields}
    And system parameter INTEL_HANDLING_CODE_VALUE_TO_ENABLE_FIELDS contains ${bs.cucumber.intelHandlingCodeValueToEnableFields}
    And the client is eligible to perform the task
    And the client provides 'Source Type' as <SourceOfIntel>
    And the client provides 'Source Officer' as <CollarId>
    And the client provides 'Other Force Officer Force' as <OtherForce>
    And the client provides 'Other Force Officer Details' as <OfficerDetails>
    And the client provides 'Other Source' as <OtherSource>
    #Handling Code related info
    And the client provides 'Un San Handling' as 'C'
    And the client provides 'Unsanitised Handling Conditions Reason' as 'These are handling conditions'
    And the client provides 'Un San Handling Conditions' as 'These are detailed handling conditions'
    #Additional info
    And the client provides 'Dissemination Recommendations' as 'This is additional information'
    #Provenance related info,Mandatory for Completed flag-True
    And the client provides 'Provenance' as 'Source have seen something very suspicious'
    And the client provides 'Shared With' as 'His parents'
    And the client provides 'First Know By Source' as 'On the day before Christmas i.e 24/12/2017'
    And the client provides 'Last Know By Source' as 'Today also he knows everything'
    And the client provides 'More Information From Source' as 'Yes, he can provide more information'
    #Action Code, Mandatory if Handling code is C
    And the client provides 'Unsanitised Action Code' as 'A3'
    And the client provides 'Information Sanitised Action Code A3 Usage' as 'Usage instructions for A3 action code'
    And the client provides 'Submission Sanitisation Code' as 'S1'
    # Below data will be supplied in txData
    And the client provides 'Action Taken' as 'COMPLETED'
    And the client provides 'Submit To Information Manager' as 'AMO'
    And the client provides 'Submit To Linking Unit' as 'AMO'
    #childObject name="reportUnsanitisedText"
    And the client provides a 'Report Unsanitised Text'
    And the client provides 'Text' as 'This is submission text'
    And the client provides 'Source' as '1'
    And the client provides 'Evaluation' as 'A'
    #childObject name="reportIntelligenceRisk"
    And the client provides a 'Report Intelligence Risk'
    And the client provides 'Public Immunity Indicator' as 'true'
    And the client provides 'Risk To Source Safety' as 'true'
    And the client provides 'Risk To Source Why' as 'He is very rich'
    And the client provides 'How Risk Reduced' as 'By giving police protection'
    And the client provides 'Should Be Sensitive' as 'true'
    And the client provides 'Sensitive' as 'true'
    When the client submits the Intelligence Report to Business Services
    # Below are the outcomes of the task performed
    Then the Intelligence Report will have its Status changed to <OutStatus>
    And a new task <NewTaskName> is created for unit ${bs.cucumber.unit}
    And a new Task History Entry will be added to the Intelligence report
    And the Task History Entry will record 'Action Taken' as 'COMPLETED'
    And the Task History Entry will record 'Submit To Information Manager' as 'AMO'
    And the Task History Entry will record 'Submit To Linking Unit' as 'AMO'
    And Intelligence Report contains 'Confidence Level' as '3'

    Examples: 
      | SourceOfIntel   | CollarId  | OtherForce  | OfficerDetails  | OtherSource     | OutStatus                     | NewTaskName                |
      | M               |           |             |                 |                 | REQUIRES ASSESSMENT SENSITIVE | Assess Intelligence report |
      | P               |     40920 |             |                 |                 | REQUIRES ASSESSMENT SENSITIVE | Assess Intelligence report |
      | OS              |           |             |                 | Mr. Jack Wilson | REQUIRES ASSESSMENT SENSITIVE | Assess Intelligence report |
      | OL              |           |          46 | Insp John Smith |                 | REQUIRES ASSESSMENT SENSITIVE | Assess Intelligence report |


  Scenario: Perform task 'Complete Intelligence Report' with action taken as 'CANCELLED'
   #BS-INCLUDE feature=CON-42855_SubmitIncompleteIntelligenceReport.feature, scenario=Happy Path for Create Intelligence Report In Require Completion status
    Given an Intelligence Report exists with status of 'REQUIRES COMPLETION'
    And a task 'Complete Intelligence Report' exists against that Intelligence Report
    And system parameter INTEL_SENSITIVE_ACTIONCODE contains ${bs.cucumber.intelSensitiveActionCode}
    And system parameter INTEL_SOURCE_EVALUATION_VALUE_TO_ENABLE_FIELDS contains ${bs.cucumber.intelSourceEvaluationValueToEnableFields}
    And system parameter INTEL_INTELLIGENCE_ASSESSMENT_VALUE_TO_ENABLE_FIELDS contains ${bs.cucumber.intelIntelligenceAssessmentValueToEnableFields}
    And system parameter INTEL_HANDLING_CODE_VALUE_TO_ENABLE_FIELDS contains ${bs.cucumber.intelHandlingCodeValueToEnableFields}
    And the client is eligible to perform the task
    # Below data will be supplied in txData
    And the client provides 'Action Taken' as 'CANCELLED'
    When the client submits the Intelligence Report to Business Services
    # Below are the outcomes of the task performed
    And a new Task History Entry will be added to the 'Intelligence Report'
    Then the Intelligence Report will have its Status changed to 'DELETED'
    And the Task History Entry will record 'Action Taken' as 'CANCELLED'

  Scenario: Perform task 'Complete Intelligence Report' with action taken as 'CANCELLED' with status of 'REQUIRES COMPLETION'
   #BS-INCLUDE feature=CON-42855_SubmitIncompleteIntelligenceReport.feature, scenario=Happy Path for Create Intelligence Report In Require Completion status
    Given an Intelligence Report exists with status of 'REQUIRES COMPLETION'
    And a task 'Complete Intelligence Report' exists against that Intelligence Report
    And the client is eligible to perform the task
    # Below data will be supplied in txData
    And the client provides Action taken as 'CANCELLED'
    When the client submits the Intelligence Report to Business Services
    # Below are the outcomes of the task performed  
    And the Intelligence Report will have its Status changed to 'DELETED'  
    And a new Task History Entry will be added to the Intelligence Report  
    And the Task History Entry will record Action taken as 'CANCELLED'
    

  Scenario: Perform task 'Complete Intelligence Report' with action taken as 'CANCELLED' with status of 'REQUIRES COMPLETION SENSITIVE'
  #BS-INCLUDE feature=CON-42855_SubmitIncompleteIntelligenceReport.feature, scenario=Happy Path for Create Intelligence Report In Require Completion Sensitive status
    Given an Intelligence Report exists with status of 'REQUIRES COMPLETION SENSITIVE'
    And a task 'Complete Intelligence Report' exists against that Intelligence Report
    And system parameter INTEL_SENSITIVE_ACTIONCODE contains ${bs.cucumber.intelSensitiveActionCode}
    And the client is eligible to perform the task  
    # Below data will be supplied in txData
    And the client provides Action taken as 'CANCELLED'
    When the client submits the Intelligence Report to Business Services
    # Below are the outcomes of the task performed   
    And a new Task History Entry will be added to the Intelligence Report 
    And the Task History Entry will record Action taken as 'CANCELLED'
    And the Intelligence Report will have its Status changed to 'DELETED'   


  Scenario: Perform task 'Complete Intelligence Report' with action taken as 'CANCELLED'  for Sensitive Intel Report
  #BS-INCLUDE feature=CON-42855_SubmitIncompleteIntelligenceReport.feature, scenario=Happy Path for Create Intelligence Report In Require Completion Sensitive status
    Given an Intelligence Report exists with status of 'REQUIRES COMPLETION SENSITIVE'
    And a task 'Complete Intelligence Report' exists against that Intelligence Report
    And system parameter INTEL_SENSITIVE_ACTIONCODE contains ${bs.cucumber.intelSensitiveActionCode}
    And system parameter INTEL_SOURCE_EVALUATION_VALUE_TO_ENABLE_FIELDS contains ${bs.cucumber.intelSourceEvaluationValueToEnableFields}
    And system parameter INTEL_INTELLIGENCE_ASSESSMENT_VALUE_TO_ENABLE_FIELDS contains ${bs.cucumber.intelIntelligenceAssessmentValueToEnableFields}
    And system parameter INTEL_HANDLING_CODE_VALUE_TO_ENABLE_FIELDS contains ${bs.cucumber.intelHandlingCodeValueToEnableFields}
    And the client is eligible to perform the task
    # Below data will be supplied in txData
    And the client provides 'Action Taken' as 'CANCELLED'
    When the client submits the Intelligence Report to Business Services
    # Below are the outcomes of the task performed
    Then the Intelligence Report will have its Status changed to 'DELETED'
    And a new Task History Entry will be added to the 'Intelligence Report'
    And the Task History Entry will record 'Action Taken' as 'CANCELLED'    
    