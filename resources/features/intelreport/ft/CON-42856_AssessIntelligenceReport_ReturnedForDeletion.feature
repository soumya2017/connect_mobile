Feature: CON-42856 Perform task 'Assess Intelligence Report' for action 'RETURNED FOR DELETION'
 
  Scenario: User is able to perform task "Assess Intelligence Report" with Action taken 'Returned for deletion' with status of 'REQUIRES ASSESSMENT'
  #BS-INCLUDE feature=CON-42850_CreateIntelligenceReport.feature, scenario=Happy Path for Create Intelligence Report In Require Assessment status
    Given an Intelligence Report exists with status of 'REQUIRES ASSESSMENT'
    And a task 'Assess Intelligence Report' exists against that Intelligence Report
    And the client is eligible to perform the task
    And the client provides 'San Handling' as 'C'
	And the client provides 'San Handling Conditions' as 'C'
	And the client provides 'Information Sanitised Action Code' as 'A1'
	And the client provides 'Information Sanitised Code' as 'S1'
	#txdata
	And the client provides 'Action Taken' as 'RETURNED FOR DELETION'
    And the client provides 'Reason For Correction Or Deletion' as 'This report is not valid'
    #childObject
	And the client provides an 'Sanitised Text'
	And the client provides 'Text' as 'sanitisetext'
	And the client provides 'Source' as '1'
	And the client provides 'Evaluation' as 'A'
	When the client submits the Intelligence Report to Business Services
	Then the Intelligence Report will have its Status changed to 'TO BE DELETED'
	And the task will be performed
	And a new task 'Acknowledge Intelligence Report Returned Or Deleted' is created for unit ${bs.cucumber.unit}
	And a new Task History Entry will be added to the Intelligence Report
	And the Task History Entry will record ActionTaken as 'RETURNED FOR DELETION'
	And the Task History Entry will record 'RejectionReason' as 'This report is not valid'

Scenario: User is able to perform task "Assess Intelligence Report" with Action taken 'Returned for deletion' with status of 'REQUIRES ASSESSMENT SENSITIVE'
  #BS-INCLUDE feature=CON-42850_CreateIntelligenceReport.feature, scenario=Happy Path for Create Intelligence Report In Require Assessment Sensitive status
    Given an Intelligence Report exists with status of 'REQUIRES ASSESSMENT SENSITIVE'
    And a task 'Assess Intelligence Report' exists against that Intelligence Report
    And system parameter INTEL_SENSITIVE_ACTIONCODE contains ${bs.cucumber.intelSensitiveActionCode}
    And the client is eligible to perform the task
    And the client provides 'San Handling' as 'C'
	And the client provides 'San Handling Conditions' as 'C'
	And the client provides 'Information Sanitised Action Code' as 'A1'
	And the client provides 'Information Sanitised Code' as 'S1'
	#txdata
	And the client provides 'Action Taken' as 'RETURNED FOR DELETION'
    And the client provides 'Reason For Correction Or Deletion' as 'This report is not valid'
    #childObject
	And the client provides an 'Sanitised Text'
	And the client provides 'Text' as 'sanitisetext'
	And the client provides 'Source' as '1'
	And the client provides 'Evaluation' as 'A'
	When the client submits the Intelligence Report to Business Services
	Then the Intelligence Report will have its Status changed to 'TO BE DELETED SENSITIVE'
	And the task will be performed
	And a new task 'Acknowledge Intelligence Report Returned Or Deleted' is created for unit ${bs.cucumber.unit}
	And a new Task History Entry will be added to the Intelligence Report
	And the Task History Entry will record ActionTaken as 'RETURNED FOR DELETION'
	And the Task History Entry will record 'RejectionReason' as 'This report is not valid'


  # In the below scenario user should be able to perform task  "Assess Intelligence Report" with Action taken 'Returned for deletion' for Report which has been returned for correction in the past
#  @EdgeCase @AbhishekM
#  Scenario Outline: User is able to perform task "Assess Intelligence Report" with Action taken 'Returned for deletion' for report sent for correction in the past
#BS-INCLUDE feature=CON-42850_CreateIntelligenceReport.feature, scenario=Happy Path for Create Intelligence Report In Require Assessment Sensitive status
#    Given an Intelligence Report exists with status of <inStatus>
#    And a task <existingTaskName> exists against that Intelligence Report
#    And system parameter INTEL_SENSITIVE_ACTIONCODE is set as <sysParamValue>
#    # Below data will be supplied in txData
#    And the client provides  "Action Taken" as <actionTaken>
#    And the client provides  "Reason For Correction Or Deletion" as <reasonForDel>
#    # Below are not input from client. Not sure why we put it.
#    #And the client provides  "Intel Reevaluated" as <intelRe-val>
#    #And the client provides  "Reviewed By Employee IterationId" by as <intelRevBy>
#    #And the client provides  "Reviewed Date Time" as <revDateTime>
#    # Now the task will be performed
#    When the client submits the Intelligence Report to Business Services
#    Then the Intelligence Report will have its Status changed to <outStatus>
#    And the task will be performed
#    # Below are the outcomes of the task performed
#    Then a new task 'Acknowledge Intelligence Report Returned Or Deleted' is created for unit 'AMO'
#    And the Task History Entry will record "Action taken" as <actionTaken>
#    And the Task History Entry will record "Rejection Reason" as <reasonForDel>
#    And the Task History Entry will record "Intel Reevaluated" as <intelRe-val>
#    # Below do not come in output parameter so can not assert.
#    #And the Task History Entry will record "Intelligence report reviewed by" as <intelRevBy>
#    #And the Task History Entry will record "Review date / time" as <revDateTime>
#    And the Intelligence Report will have its Status changed to <outStatus>
#
#    Examples: 
#      | inStatus                      | existingTaskName          | sysParamValue | actionTaken           | reasonForDel            | intelRe-val | intelRevBy | revDateTime             | existingTaskName          | actionTaken           | reasonForDel            | intelRe-val | intelRevBy | revDateTime             | outStatus                |
#      | REQUIRES ASSESSMENT           | Assess Information Report | A1            | Returned for deletion | the report is not valid | F           |            |                         | Assess Information Report | Returned for deletion | the report is not valid | F           |            |                         | TO BE DELETED            |
#      | REQUIRES ASSESSMENT SENSITIVE | Assess Information Report | A1            | Returned for deletion | the report is not valid | F           |            |                         | Assess Information Report | Returned for deletion | the report is not valid | F           |            |                         | TO BE DELETED SENSITIVE  |
#      | REQUIRES ASSESSMENT SENSITIVE | Assess Information Report | A2            | Returned for deletion | the report is not valid | T           |      40711 | 2017-08-16 09:20:31.735 | Assess Information Report | Returned for deletion | the report is not valid | T           |      40711 | 2017-08-16 09:20:31.735 | TO BE DELETED  SENSITIVE |
#      | REQUIRES ASSESSMENT           | Assess Information Report | A1            | Returned for deletion | the report is not valid | T           |      40711 | 2017-08-16 09:20:31.735 | Assess Information Report | Returned for deletion | the report is not valid | T           |      40711 | 2017-08-16 09:20:31.735 | TO BE DELETED            |
    
    
  
  # In the below scenario user should be able to perform task  "Assess Intelligence Report" with Action taken 'Returned for deletion' for Report which has been returned for correction in the past
#  Scenario Outline: User is able to perform task "Assess Intelligence Report" with Action taken 'Returned for deletion' for report sent for correction in the past
#BS-INCLUDE feature=CON-42850_CreateIntelligenceReport.feature, scenario=Happy Path for Create Intelligence Report In Require Assessment status
#    Given an Intelligence Report exists with status of <inStatus>
#    And a task <existingTaskName> exists against that Intelligence Report
#    And system parameter INTEL_SENSITIVE_ACTIONCODE is set as <sysParamValue>
#    # Below data will be supplied in txData
#    And the client provides  "Action Taken" as <actionTaken>
#    And the client provides  "Reason For Correction Or Deletion" as <reasonForDel>
#    # Below are not input from client. Not sure why we put it.
#    #And the client provides  "Intel Reevaluated" as <intelRe-val>
#    #And the client provides  "Reviewed By Employee IterationId" by as <intelRevBy>
#    #And the client provides  "Reviewed Date Time" as <revDateTime>
#    # Now the task will be performed
#    When the client submits the Intelligence Report to Business Services
#    Then the Intelligence Report will have its Status changed to <outStatus>
#    And the task will be performed
#    # Below are the outcomes of the task performed
#    Then a new task 'Acknowledge Intelligence Report Returned Or Deleted' is created for unit 'AMO'
#    And the Task History Entry will record "Action taken" as <actionTaken>
#    And the Task History Entry will record "Rejection Reason" as <reasonForDel>
#    And the Task History Entry will record "Intel Reevaluated" as <intelRe-val>
#    # Below do not come in output parameter so can not assert.
#    #And the Task History Entry will record "Intelligence report reviewed by" as <intelRevBy>
#    #And the Task History Entry will record "Review date / time" as <revDateTime>
#    And the Intelligence Report will have its Status changed to <outStatus>
#
#    Examples: 
#      | inStatus                      | existingTaskName          | sysParamValue | actionTaken           | reasonForDel            | intelRe-val | intelRevBy | revDateTime             | existingTaskName          | actionTaken           | reasonForDel            | intelRe-val | intelRevBy | revDateTime             | outStatus                |
#      | REQUIRES ASSESSMENT           | Assess Information Report | A1            | Returned for deletion | the report is not valid | F           |            |                         | Assess Information Report | Returned for deletion | the report is not valid | F           |            |                         | TO BE DELETED            |
#      | REQUIRES ASSESSMENT SENSITIVE | Assess Information Report | A1            | Returned for deletion | the report is not valid | F           |            |                         | Assess Information Report | Returned for deletion | the report is not valid | F           |            |                         | TO BE DELETED SENSITIVE  |
#      | REQUIRES ASSESSMENT SENSITIVE | Assess Information Report | A2            | Returned for deletion | the report is not valid | T           |      40711 | 2017-08-16 09:20:31.735 | Assess Information Report | Returned for deletion | the report is not valid | T           |      40711 | 2017-08-16 09:20:31.735 | TO BE DELETED  SENSITIVE |
#      | REQUIRES ASSESSMENT           | Assess Information Report | A1            | Returned for deletion | the report is not valid | T           |      40711 | 2017-08-16 09:20:31.735 | Assess Information Report | Returned for deletion | the report is not valid | T           |      40711 | 2017-08-16 09:20:31.735 | TO BE DELETED            |
