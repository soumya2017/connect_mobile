Feature: CON-42852 Perform task 'Assess Intelligence Report' for action 'Returned for correction' with status 'REQUIRES ASSESSMENT'

  Scenario: Happy Path Action Taken as Returned for Correction with status of 'REQUIRES ASSESSMENT'
    #BS-INCLUDE feature=CON-42850_CreateIntelligenceReport.feature, scenario=Happy Path for Create Intelligence Report In Require Assessment status
    Given an Intelligence Report exists with status of 'REQUIRES ASSESSMENT'
    And a task 'Assess Intelligence Report' exists against that Intelligence Report
    And system parameter INTEL_SENSITIVE_ACTIONCODE contains ${bs.cucumber.intelSensitiveActionCode}
    And the client is eligible to perform the task
    And the client provides 'San Handling' as 'C'
    And the client provides 'San Handling Conditions' as 'C'
    And the client provides 'Information Sanitised Action Code' as 'A1'
    And the client provides 'Information Sanitised Code' as 'S1'
    #txData
    And the client provides 'Action Taken' as 'RETURNED FOR CORRECTION'
    And the client provides 'Reason For Correction Or Deletion' as 'Correct this intelligence report'
    And the client provides 'Resubmission Date' as 'today+2'
    #childObject
    And the client provides an 'Sanitised Text'
    And the client provides 'Text' as 'sanitisetext'
    And the client provides 'Source' as '1'
    And the client provides 'Evaluation' as 'A'
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to 'REQUIRES CORRECTION'
    And a new task 'Correct Intelligence Report' is created for unit ${bs.cucumber.unit}
    And a new Task History Entry will be added to the Intelligence Report
    And the Task History Entry will record ActionTaken as 'RETURNED FOR CORRECTION'
    And the Task History Entry will record RejectionReason as 'Correct this intelligence report'
    And the Task History Entry will record ResubmissionDate as 'today+2'

  Scenario: Happy Path Action Taken as Returned for Correction with status of 'REQUIRES ASSESSMENT SENSITIVE'
    #BS-INCLUDE feature=CON-42850_CreateIntelligenceReport.feature, scenario=Happy Path for Create Intelligence Report In Require Assessment Sensitive status
    Given an Intelligence Report exists with status of 'REQUIRES ASSESSMENT SENSITIVE'
    And a task 'Assess Intelligence Report' exists against that Intelligence Report
    And system parameter INTEL_SENSITIVE_ACTIONCODE contains ${bs.cucumber.intelSensitiveActionCode}
    And the client is eligible to perform the task
    And the client provides 'San Handling' as 'C'
    And the client provides 'San Handling Conditions' as 'C'
    And the client provides 'Information Sanitised Action Code' as 'A2'
    And the client provides 'Information Sanitised Code' as 'S1'
    #txData
    And the client provides 'Action Taken' as 'RETURNED FOR CORRECTION'
    And the client provides 'Reason For Correction Or Deletion' as 'Correct this intelligence report'
    And the client provides 'Resubmission Date' as 'today+2'
    #childObject
    And the client provides an 'Sanitised Text'
    And the client provides 'Text' as 'sanitisetext'
    And the client provides 'Source' as '1'
    And the client provides 'Evaluation' as 'A'
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to 'REQUIRES CORRECTION SENSITIVE'
    And a new task 'Correct Intelligence Report' is created for unit ${bs.cucumber.unit}
    And a new Task History Entry will be added to the Intelligence Report
    And the Task History Entry will record ActionTaken as 'RETURNED FOR CORRECTION'
    And the Task History Entry will record RejectionReason as 'Correct this intelligence report'
    And the Task History Entry will record ResubmissionDate as 'today+2'

  Scenario: Perform Assess Intelligence Report task with Action Taken as Returned for Correction for Resubmission date as todays date with status of 'REQUIRES ASSESSMENT'
    #BS-INCLUDE feature=CON-42850_CreateIntelligenceReport.feature, scenario=Happy Path for Create Intelligence Report In Require Assessment status
    Given an Intelligence Report exists with status of 'REQUIRES ASSESSMENT'
    And a task 'Assess Intelligence Report' exists against that Intelligence Report
    And system parameter INTEL_SENSITIVE_ACTIONCODE contains ${bs.cucumber.intelSensitiveActionCode}
    And the client is eligible to perform the task
    And the client provides 'San Handling' as 'C'
    And the client provides 'San Handling Conditions' as 'C'
    And the client provides 'Information Sanitised Action Code' as 'A1'
    And the client provides 'Information Sanitised Code' as 'S1'
    #txData
    And the client provides 'Action Taken' as 'RETURNED FOR CORRECTION'
    And the client provides 'Reason For Correction Or Deletion' as 'Correct this intelligence report'
    And the client provides 'Resubmission Date' as 'today'
    #childObject
    And the client provides an 'Sanitised Text'
    And the client provides 'Text' as 'sanitisetext'
    And the client provides 'Source' as '1'
    And the client provides 'Evaluation' as 'A'
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to 'REQUIRES CORRECTION'
    And a new task 'Acknowledge User Failure To Correct Intelligence Report' is created for unit ${bs.cucumber.unit}
    And a new Task History Entry will be added to the Intelligence Report
    And the Task History Entry will record ActionTaken as 'RETURNED FOR CORRECTION'
    And the Task History Entry will record RejectionReason as 'Correct this intelligence report'
    And the Task History Entry will record ResubmissionDate as 'today'

  
  Scenario: Perform Assess Intelligence Report task with Action Taken as Returned for Correction for Resubmission date as todays date with status of 'REQUIRES ASSESSMENT SENSITIVE'
    #BS-INCLUDE feature=CON-42850_CreateIntelligenceReport.feature, scenario=Happy Path for Create Intelligence Report In Require Assessment Sensitive status
    Given an Intelligence Report exists with status of 'REQUIRES ASSESSMENT SENSITIVE'
    And a task 'Assess Intelligence Report' exists against that Intelligence Report
    And system parameter INTEL_SENSITIVE_ACTIONCODE contains ${bs.cucumber.intelSensitiveActionCode}
    And the client is eligible to perform the task
    And the client provides 'San Handling' as 'C'
    And the client provides 'San Handling Conditions' as 'C'
    And the client provides 'Information Sanitised Action Code' as 'A1'
    And the client provides 'Information Sanitised Code' as 'S1'
    #txData
    And the client provides 'Action Taken' as 'RETURNED FOR CORRECTION'
    And the client provides 'Reason For Correction Or Deletion' as 'Correct this intelligence report'
    And the client provides 'Resubmission Date' as 'today'
    #childObject
    And the client provides an 'Sanitised Text'
    And the client provides 'Text' as 'sanitisetext'
    And the client provides 'Source' as '1'
    And the client provides 'Evaluation' as 'A'
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to 'REQUIRES CORRECTION SENSITIVE'
    And a new task 'Acknowledge User Failure To Correct Intelligence Report' is created for unit ${bs.cucumber.unit}
    And a new Task History Entry will be added to the Intelligence Report
    And the Task History Entry will record ActionTaken as 'RETURNED FOR CORRECTION'
    And the Task History Entry will record RejectionReason as 'Correct this intelligence report'
    And the Task History Entry will record ResubmissionDate as 'today'
    
  
  Scenario Outline: Run the Assess Intelligence Report task with Action Taken as Returned for Correction and change to Sensitive Status with current status of 'REQUIRES ASSESSMENT'
    #BS-INCLUDE feature=CON-42850_CreateIntelligenceReport.feature, scenario=Happy Path for Create Intelligence Report In Require Assessment status
    Given an Intelligence Report exists with status of 'REQUIRES ASSESSMENT'
    And a task <existingTaskName> exists against that Intelligence Report
    And system parameter INTEL_SENSITIVE_ACTIONCODE contains ${bs.cucumber.intelSensitiveActionCode}
    And the client is eligible to perform the task
    And the client provides 'Unsanitised Action Code' as <actionCode>
    And the client provides 'San Handling' as 'C'
    And the client provides 'San Handling Conditions' as 'C'
    And the client provides 'Information Sanitised Action Code' as 'A1'
    And the client provides 'Information Sanitised Code' as 'S1'
    #txData
    And the client provides 'Action Taken' as 'RETURNED FOR CORRECTION'
    And the client provides 'Reason For Correction Or Deletion' as <reasonText>
    And the client provides 'Resubmission Date' as <Date>
    #childObject
    And the client provides an 'Sanitised Text'
    And the client provides 'Text' as 'sanitisetext'
    And the client provides 'Source' as '1'
    And the client provides 'Evaluation' as 'A'
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to <outStatus>
    And a new task <newTaskName> is created for unit ${bs.cucumber.unit}
    And a new Task History Entry will be added to the Intelligence Report
    And the Task History Entry will record ActionTaken as 'RETURNED FOR CORRECTION'
    And the Task History Entry will record RejectionReason as <reasonText>
    And the Task History Entry will record ResubmissionDate as <Date>

    Examples: 
      | existingTaskName           | actionCode | reasonText                       | outStatus                     | Date    | newTaskName                 |
      | Assess Intelligence Report | A2         | Correct this intelligence report | REQUIRES CORRECTION SENSITIVE | today+5 | Correct Intelligence Report |

  Scenario Outline: Run the Assess Intelligence Report task with Action Taken as Returned for Correction to remove Sensitive Status with current status of 'REQUIRES ASSESSMENT SENSITIVE'
    #BS-INCLUDE feature=CON-42850_CreateIntelligenceReport.feature, scenario=Happy Path for Create Intelligence Report In Require Assessment Sensitive status
    Given an Intelligence Report exists with status of 'REQUIRES ASSESSMENT SENSITIVE'
    And a task <existingTaskName> exists against that Intelligence Report
    And system parameter INTEL_SENSITIVE_ACTIONCODE contains ${bs.cucumber.intelSensitiveActionCode}
    And the client is eligible to perform the task
    And the client provides 'Unsanitised Action Code' as <actionCode>
    And the client provides 'San Handling' as 'C'
    And the client provides 'San Handling Conditions' as 'C'
    And the client provides 'Information Sanitised Action Code' as 'A1'
    And the client provides 'Information Sanitised Code' as 'S1'
    #txData
    And the client provides 'Action Taken' as 'RETURNED FOR CORRECTION'
    And the client provides 'Reason For Correction Or Deletion' as <reasonText>
    And the client provides 'Resubmission Date' as <Date>
    #childObject
    And the client provides an 'Sanitised Text'
    And the client provides 'Text' as 'sanitisetext'
    And the client provides 'Source' as '1'
    And the client provides 'Evaluation' as 'A'
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to <outStatus>
    And a new task <newTaskName> is created for unit ${bs.cucumber.unit}
    And a new Task History Entry will be added to the Intelligence Report
    And the Task History Entry will record ActionTaken as 'RETURNED FOR CORRECTION'
    And the Task History Entry will record RejectionReason as <reasonText>
    And the Task History Entry will record ResubmissionDate as <Date>

    Examples: 
      | existingTaskName           | actionCode | reasonText                       | outStatus           | Date    | newTaskName                 |
      | Assess Intelligence Report | A1         | Correct this intelligence report | REQUIRES CORRECTION | today+5 | Correct Intelligence Report |
#
  #Scenario Outline: Run the Assess Intelligence Report task with Action Taken as Returned for Correction by altering either of UnSanHandling Code,Source Code or Evaluation Code for Reval Flag True/False
    #BS-INCLUDE feature=CON-42850_CreateIntelligenceReport.feature, scenario=Happy Path for Create Intelligence Report In Require Assessment status
    #Given an Intelligence Report exists with status of <inStatus>
    #And a task <existingTaskName> exists against that Intelligence Report
    #And the client provides Evaluation as <Eval>
    #And the client provides Source Type as <ST>
    #And system parameter INTEL_SENSITIVE_ACTIONCODE contains 'A2'
    #And the client is eligible to perform the task
    #And the client provides 'Action Taken' as <actionTaken>
    #And the client provides 'Reason For Correction Or Deletion' as <reasonText>
    #And the client provides 'Resubmission Date' as <resubdate>
    #And the client provides 'Intel Reevaluated' as <RevalFlag>
    #And the client provides 'Reviewed Date Time' as <reviewdate>
    #When the client submits the Intelligence Report to Business Services
    #Then the Intelligence Report will have its Status changed to <outStatus>
    #And a new task <newTaskName> is created for unit 'AMO'
    #And the Task History Entry will record ActionTaken as <actionTaken>
    #And the Task History Entry will record RejectionReason as <reasonText>
    #And the Task History Entry will record ResubmissionDate as <Date>
#
    #Examples: 
      #| actionTaken             | inStatus            | existingTaskName           | sysParamValue | reasonText                       | outStatus                     | Date       | newTaskName                                             | Eval | ST | reviewdate | reviewtime | RevalFlag |
      #| RETURNED FOR CORRECTION | REQUIRES ASSESSMENT | Assess Intelligence Report | A2            | Correct this intelligence report | REQUIRES CORRECTION SENSITIVE | 2017-08-24 | Correct Intelligence Report                             | A1   | OL | 2017-08-24 | 10:00      | False     |
      #| RETURNED FOR CORRECTION | REQUIRES ASSESSMENT | Assess Intelligence Report | A2            | Correct this intelligence report | REQUIRES CORRECTION SENSITIVE | 2017-08-24 | Acknowledge User Failure To Correct Intelligence Report | A1   | OL | 2017-08-24 | 10:00      | False     |

  Scenario: Run the Assess Intelligence Report task with Action Taken as Returned for Correction by adding Log of Entries with status of 'REQUIRES ASSESSMENT'
    #BS-INCLUDE feature=CON-42850_CreateIntelligenceReport.feature, scenario=Happy Path for Create Intelligence Report In Require Assessment status
    Given an Intelligence Report exists with status of 'REQUIRES ASSESSMENT'
    And a task 'Assess Intelligence Report' exists against that Intelligence Report
    And the client is eligible to perform the task
    And the client provides 'San Handling' as 'C'
    And the client provides 'San Handling Conditions' as 'C'
    And the client provides 'Information Sanitised Action Code' as 'A1'
    And the client provides 'Information Sanitised Code' as 'S1'
    And the client provides 'Action Taken' as 'RETURNED FOR CORRECTION'
    And the client provides 'Reason For Correction Or Deletion' as 'Correct this intelligence report'
    And the client provides 'Resubmission Date' as 'today+5'
    #childObject
    And the client provides an 'Sanitised Text'
    And the client provides 'Text' as 'sanitisetext'
    And the client provides 'Source' as '1'
    And the client provides 'Evaluation' as 'A'
    #childObject
    And the client provides a Report Log Entry
    And the client provides 'Entry Index' as '2'
    And the client provides 'Entry Text' as 'Intel Logs'
    And the client provides 'Entry Date' as 'today'
    And the client provides 'Entry Time' as '09:20:31.735'
    And the client provides 'Entered By Officer Id' as '20'
    And the client provides 'Entry Type' as 'PNC'
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to 'REQUIRES CORRECTION'
    And a new task 'Correct Intelligence Report' is created for unit ${bs.cucumber.unit}
    And a new Task History Entry will be added to the Intelligence Report
    And the Task History Entry will record ActionTaken as 'RETURNED FOR CORRECTION'
    And the Task History Entry will record RejectionReason as 'Correct this intelligence report'
    And the Task History Entry will record ResubmissionDate as 'today+5'

  Scenario: Run the Assess Intelligence Report task with Action Taken as Returned for Correction by adding Log of Entries with status of 'REQUIRES ASSESSMENT SENSITIVE'
    #BS-INCLUDE feature=CON-42850_CreateIntelligenceReport.feature, scenario=Happy Path for Create Intelligence Report In Require Assessment Sensitive status
    Given an Intelligence Report exists with status of 'REQUIRES ASSESSMENT SENSITIVE'
    And a task 'Assess Intelligence Report' exists against that Intelligence Report
    And the client is eligible to perform the task
    And the client provides 'San Handling' as 'C'
    And the client provides 'San Handling Conditions' as 'C'
    And the client provides 'Information Sanitised Action Code' as 'A1'
    And the client provides 'Information Sanitised Code' as 'S1'
    And the client provides 'Action Taken' as 'RETURNED FOR CORRECTION'
    And the client provides 'Reason For Correction Or Deletion' as 'Correct this intelligence report'
    And the client provides 'Resubmission Date' as 'today+5'
    #childObject
    And the client provides an 'Sanitised Text'
    And the client provides 'Text' as 'sanitisetext'
    And the client provides 'Source' as '1'
    And the client provides 'Evaluation' as 'A'
    #childObject
    And the client provides a Report Log Entry
    And the client provides 'Entry Index' as '2'
    And the client provides 'Entry Text' as 'Intel Logs'
    And the client provides 'Entry Date' as 'today'
    And the client provides 'Entry Time' as '09:20:31.735'
    And the client provides 'Entered By Officer Id' as '20'
    And the client provides 'Entry Type' as 'PNC'
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to 'REQUIRES CORRECTION SENSITIVE'
    And a new task 'Correct Intelligence Report' is created for unit ${bs.cucumber.unit}
    And a new Task History Entry will be added to the Intelligence Report
    And the Task History Entry will record ActionTaken as 'RETURNED FOR CORRECTION'
    And the Task History Entry will record RejectionReason as 'Correct this intelligence report'
    And the Task History Entry will record ResubmissionDate as 'today+5'

  Scenario Outline: Run the Assess Intelligence Report task with Action Taken as Returned for Correction without passing Rejection Reason
    #BS-INCLUDE feature=CON-42850_CreateIntelligenceReport.feature, scenario=Happy Path for Create Intelligence Report In Require Assessment status
    Given an Intelligence Report exists with status of <inStatus>
    And a task <existingTaskName> exists against that Intelligence Report
    And system parameter INTEL_SENSITIVE_ACTIONCODE contains ${bs.cucumber.intelSensitiveActionCode}
    And the client is eligible to perform the task
    And the client provides 'Action Taken' as <actionTaken>
    And the client provides 'Reason For Correction Or Deletion' as <reasonText>
    And the client provides 'Resubmission Date' as <Date>
    When the client submits the Intelligence Report to Business Services
    Then Error will be returned for field 'reasonForCorrectionOrDeletion' with message 'reasonForCorrectionOrDeletion must have a value'

    Examples: 
      | actionTaken             | inStatus            | sysParamValue | reasonText | outStatus                     | newTaskName                 | Date    | existingTaskName           |
      | RETURNED FOR CORRECTION | REQUIRES ASSESSMENT | A1            |            | REQUIRES CORRECTION SENSITIVE | Correct Intelligence Report | today+2 | Assess Intelligence Report |
