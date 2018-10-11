#Author: kajal.yadav@northgateps.com
Feature: CON-42904 Ability to add an Enquiry Log entries to Intelligence reports 
@draft
Scenario Outline: Create Intelligence Report with completed flag set as No and add multiple log of enquiry 
	Given an Intelligence Report exists with status of 'NEW' 
	And system parameter INTEL_SENSITIVE_ACTIONCODE contains 'A1' 
	And the client is eligible to perform the task 
	And the client provides  'Title' as 'This is report Title' 
	And the client provides 'Date Submitted' as '2018-09-18' 
	And the client provides 'Force' as '42' 
	And the client provides 'Priority' as 'HIGH' 
	And the client provides 'Submitting Officer Id' as '40920' 
	#txData
	And the client provides 'Completed' as 'false' 
	And the client provides 'Force' as '42' 
	#childObject
	And the client provides an 'Intelligence Information Type' 
	And the client provides 'Type' as 'ASB' 
	#childObject name="reportLogEntry"
	And the client provides a 'Report Log Entry' 
	And the client provides 'Entry Index' as '1' 
	And the client provides 'Entry Date' as '2018-09-28' 
	And the client provides 'Entry Time' as '09:20:31.735' 
	And the client provides 'Entered By Officer Id' as '40920' 
	And the client provides 'Entry Type' as 'RES' 
	And the client provides 'Entry Text' as 'test for log entries' 
	And the client provides a 'Report Log Entry' 
	And the client provides 'Entry Index' as '2' 
	And the client provides 'Entry Date' as '2018-09-28' 
	And the client provides 'Entry Time' as '09:20:31.735' 
	And the client provides 'Entered By Officer Id' as '40920' 
	And the client provides 'Entry Type' as 'RES' 
	And the client provides 'Entry Text' as 'test for log entries second' 
	When the client submits the Intelligence Report to Business Services 
	Then the Intelligence Report will have its Status changed to <outStatus> 
	And a new task <newTaskName> is created for unit 'AMO' 
	And a new Task History Entry will be added to the Intelligence report 
	And the Task History Entry will record 'Completed' as 'false' 
	And the Task History Entry will record 'Force' as '42' 
	
	Examples: 
		| outStatus           | newTaskName                  |
		| REQUIRES COMPLETION | Complete Intelligence Report |
@draft		
Scenario: Perform task 'Complete Intelligence Report' with action taken as 'Accepted' with status of 'REQUIRES COMPLETION' 
	Given an Intelligence Report exists with status of 'REQUIRES COMPLETION' 
	And a task 'Complete Intelligence Report' exists against that Intelligence Report 
	And system parameter INTEL_SENSITIVE_ACTIONCODE contains 'A2' 
	And the client is eligible to perform the task 
	# Below data will be supplied in txData
	And the client provides "Action taken" as 'Completed' 
	And the client provides "Submit to intelligence unit" as '14640' 
	And the client provides "Linking unit" as '14640' 
	And the client provides a 'Report Log Entry' 
	And the client provides 'Entry Index' as '1' 
	And the client provides 'Entry Date' as '2018-09-28' 
	And the client provides 'Entry Time' as '09:20:31.735' 
	And the client provides 'Entered By Officer Id' as '40920' 
	And the client provides 'Entry Type' as 'RES' 
	And the client provides 'Entry Text' as 'test for log entries' 
	When the client submits the Intelligence Report to Business Services 
	# Below are the outcomes of the task performed
	Then a new task <newTaskName> is created for unit 'AMO' 
	And the Task History Entry will record "Action taken" as 'Completed' 
	And the Task History Entry will record "Submit to Intelligence unit" as '14640' 
	And the Task History Entry will record "Linking unit" as '14640' 
	And the Intelligence Report will have its Status changed to 'REQUIRES ASSESSMENT' 
@draft	
Scenario: Perform task 'Complete Intelligence Report' with action taken as 'Accepted' with status of 'REQUIRES COMPLETION SENSITIVE' 
	Given an Intelligence Report exists with status of 'REQUIRES COMPLETION SENSITIVE' 
	And a task 'Complete Intelligence Report' exists against that Intelligence Report 
	And system parameter INTEL_SENSITIVE_ACTIONCODE contains 'A2' 
	And the client is eligible to perform the task 
	# Below data will be supplied in txData
	And the client provides "Action taken" as 'Completed' 
	And the client provides "Submit to intelligence unit" as '14640' 
	And the client provides "Linking unit" as '14640' 
	#childObject name="reportLogEntry"
	And the client provides a 'Report Log Entry' 
	And the client provides 'Entry Index' as '1' 
	And the client provides 'Entry Date' as '2018-09-28' 
	And the client provides 'Entry Time' as '09:20:31.735' 
	And the client provides 'Entered By Officer Id' as '40920' 
	And the client provides 'Entry Type' as 'RES' 
	And the client provides 'Entry Text' as 'test for log entries' 
	And the client provides a 'Report Log Entry' 
	And the client provides 'Entry Index' as '2' 
	And the client provides 'Entry Date' as '2018-09-28' 
	And the client provides 'Entry Time' as '09:20:31.735' 
	And the client provides 'Entered By Officer Id' as '40920' 
	And the client provides 'Entry Type' as 'RES' 
	And the client provides 'Entry Text' as 'test for log entries second' 
	When the client submits the Intelligence Report to Business Services 
	# Below are the outcomes of the task performed
	Then a new task <newTaskName> is created for unit 'AMO' 
	And the Task History Entry will record "Action taken" as 'Completed' 
	And the Task History Entry will record "Submit to Intelligence unit" as '14640' 
	And the Task History Entry will record "Linking unit" as '14640' 
	And the Intelligence Report will have its Status changed to 'REQUIRES ASSESSMENT SENSITIVE' 
@draft	
Scenario: Perform task 'Complete Intelligence Report' with action taken as 'Intelligence Report Cancelled' with status of 'REQUIRES COMPLETION' 
	Given an Intelligence Report exists with status of 'REQUIRES COMPLETION' 
	And a task 'Complete Intelligence Report' exists against that Intelligence Report 
	And system parameter INTEL_SENSITIVE_ACTIONCODE contains 'A1' 
	And the client is eligible to perform the task 
	# Below data will be supplied in txData
	And the client provides Action taken as 'Intelligence Report Cancelled' 
	#childObject name="reportLogEntry"
	And the client provides a 'Report Log Entry' 
	And the client provides 'Entry Index' as '1' 
	And the client provides 'Entry Date' as '2018-09-28' 
	And the client provides 'Entry Time' as '09:20:31.735' 
	And the client provides 'Entered By Officer Id' as '40920' 
	And the client provides 'Entry Type' as 'RES' 
	And the client provides 'Entry Text' as 'test for log entries' 
	And the client provides a 'Report Log Entry' 
	And the client provides 'Entry Index' as '2' 
	And the client provides 'Entry Date' as '2018-09-28' 
	And the client provides 'Entry Time' as '09:20:31.735' 
	And the client provides 'Entered By Officer Id' as '40920' 
	And the client provides 'Entry Type' as 'RES' 
	And the client provides 'Entry Text' as 'test for log entries second' 
	When the client submits the Intelligence Report to Business Services 
	# Below are the outcomes of the task performed
	And the Task History Entry will record Action taken as 'Intelligence Report Cancelled' 
	And the Intelligence Report will have its Status changed to 'DELETED' 
@draft	
Scenario: Perform task 'Complete Intelligence Report' with action taken as 'Intelligence Report Cancelled' with status of 'REQUIRES COMPLETION SENSITIVE' 
	Given an Intelligence Report exists with status of 'REQUIRES COMPLETION SENSITIVE' 
	And a task 'Complete Intelligence Report' exists against that Intelligence Report 
	And system parameter INTEL_SENSITIVE_ACTIONCODE contains 'A2' 
	And the client is eligible to perform the task 
	# Below data will be supplied in txData
	And the client provides Action taken as 'Intelligence Report Cancelled' 
	#childObject name="reportLogEntry"
	And the client provides a 'Report Log Entry' 
	And the client provides 'Entry Index' as '1' 
	And the client provides 'Entry Date' as '2018-09-28' 
	And the client provides 'Entry Time' as '09:20:31.735' 
	And the client provides 'Entered By Officer Id' as '40920' 
	And the client provides 'Entry Type' as 'RES' 
	And the client provides 'Entry Text' as 'test for log entries' 
	And the client provides a 'Report Log Entry' 
	And the client provides 'Entry Index' as '2' 
	And the client provides 'Entry Date' as '2018-09-28' 
	And the client provides 'Entry Time' as '09:20:31.735' 
	And the client provides 'Entered By Officer Id' as '40920' 
	And the client provides 'Entry Type' as 'RES' 
	And the client provides 'Entry Text' as 'test for log entries second' 
	When the client submits the Intelligence Report to Business Services 
	# Below are the outcomes of the task performed
	And the Task History Entry will record Action taken as 'Intelligence Report Cancelled' 
	And the Intelligence Report will have its Status changed to 'DELETED' 
@draft	
Scenario: Create Intelligence Report with completed flag set as Yes and add multiple log of enquiry and status change to 'Require Assesment' 
	Given client opted to perform action 'Create' on an 'Intelligence Report' 
	And the client provides 'Title' as 'IntelReport' 
	And the client provides 'Date Submitted' as '16/06/2017' 
	And the client provides 'Time Submitted' as '09:20:31.735' 
	And the client provides 'Priority' as '1' 
	And the client provides 'Force' as ${bs.cucumber.forceId}
	And the client provides 'Owning Force Id' as '42' 
	And the client provides 'Bcu' as 'ESSEX NORTH' 
	And the client provides 'Station Code' as 'CHELMSFORD' 
	And the client provides 'Beat Code' as 'CHELMSFORD E05004096' 
	And the client provides 'UnsanHandling' as 'C' 
	And the client provides 'UnsanHandling Conditions' as 'Careful' 
	And the client provides 'Unsanitised Handling Conditions Reason' as 'Instructions' 
	And the client provides 'Unsanitised Action Code' as 'A1' 
	And the client provides 'Submission Sanitisation Code' as 'S1' 
	And the client provides 'Provenance' as 'dummy' 
	And the client provides 'Shared With' as 'dummy' 
	And the client provides 'First Know By Source' as 'dummy' 
	And the client provides 'Last Know By Source' as 'dummy' 
	And the client provides 'More Information From Source' as 'dummy' 
	#txData
	And the client provides 'Force Id' as '42' 
	And the client provides 'Submit To Information Manager' as 'AMO' 
	And the client provides 'Submit To Linking Unit' as 'AMO' 
	And the client provides 'Submitting Officer Id' as '20' 
	And the client provides 'Submitting Officer Display Value' as '20' 
	And the client provides 'Completed' as 'Yes' 
	And the client provides 'Priority Display Value' as 'priority1' 
	And the client provides an Intelligence Information Type 
	And the client provides 'Type' as 'ANIMAL/WILDLIFE RELATED' 
	And the client provides an Intelligence Source 
	And the client provides 'Source Type' as 'OL' 
	And the client provides 'Other Force Officer Force' as '52' 
	And the client provides 'Other Force Officer Details' as 'Officer Details' 
	And the client provides an Unsanitised Text 
	And the client provides 'Evaluation' as 'A' 
	And the client provides 'Text' as 'My first intel report to submit' 
	And the client provides 'Source' as '1' 
	And the client provides an Intelligence Risk 
	And the client provides 'Public Immunity Indicator' as 'F' 
	And the client provides 'Risk To Source Safety' as 'F' 
	And the client provides 'Should Be Sensitive' as 'F' 
	And the client provides 'Sensitive' as 'F' 
	#childObject name="reportLogEntry"
	And the client provides a 'Report Log Entry' 
	And the client provides 'Entry Index' as '1' 
	And the client provides 'Entry Date' as '2018-09-28' 
	And the client provides 'Entry Time' as '09:20:31.735' 
	And the client provides 'Entered By Officer Id' as '40920' 
	And the client provides 'Entry Type' as 'RES' 
	And the client provides 'Entry Text' as 'test for log entries' 
	#childObject name="reportLogEntry"
	And the client provides a 'Report Log Entry' 
	And the client provides 'Entry Index' as '2' 
	And the client provides 'Entry Date' as '2018-09-28' 
	And the client provides 'Entry Time' as '09:20:31.735' 
	And the client provides 'Entered By Officer Id' as '40920' 
	And the client provides 'Entry Type' as 'RES' 
	And the client provides 'Entry Text' as 'test for log entries' 
	When the client submits the 'Intelligence Report' to Business Services 
	Then the Intelligence Report will have its Status changed to 'REQUIRES ASSESSMENT' 
	And a new task 'Assess Intelligence Report' is created for unit 'AMO' 
	And a new Task History Entry will be added to the 'Intelligence Report' 
	And the Confidence Level is set as '3' 
@draft	
Scenario: Create Intelligence Report with completed flag set as Yes and add multiple log of enquiry and status change to 'Require Assesment Sensitive' 
	Given client opted to perform action 'Create' on an 'Intelligence Report' 
	And the client provides 'Title' as 'IntelReport' 
	And the client provides 'Date Submitted' as '16/06/2017' 
	And the client provides 'Time Submitted' as '09:20:31.735' 
	And the client provides 'Priority' as '1' 
	And the client provides 'Force' as '42' 
	And the client provides 'Owning Force Id' as '42' 
	And the client provides 'Bcu' as 'ESSEX NORTH' 
	And the client provides 'Station Code' as 'CHELMSFORD' 
	And the client provides 'Beat Code' as 'CHELMSFORD E05004096' 
	And the client provides 'UnsanHandling' as 'C' 
	And the client provides 'UnsanHandling Conditions' as 'Careful' 
	And the client provides 'Unsanitised Handling Conditions Reason' as 'Instructions' 
	And the client provides 'Unsanitised Action Code' as 'A2' 
	And the client provides 'Submission Sanitisation Code' as 'S1' 
	And the client provides 'Provenance' as 'dummy' 
	And the client provides 'Shared With' as 'dummy' 
	And the client provides 'First Know By Source' as 'dummy' 
	And the client provides 'Last Know By Source' as 'dummy' 
	And the client provides 'More Information From Source' as 'dummy' 
	#txData
	And the client provides 'Force Id' as '42' 
	And the client provides 'Submit To Information Manager' as 'AMO' 
	And the client provides 'Submit To Linking Unit' as 'AMO' 
	And the client provides 'Submitting Officer Id' as '20' 
	And the client provides 'Submitting Officer Display Value' as '20' 
	And the client provides 'Completed' as 'Yes' 
	And the client provides 'Priority Display Value' as 'priority1' 
	And the client provides an Intelligence Information Type 
	And the client provides 'Type' as 'ANIMAL/WILDLIFE RELATED' 
	And the client provides an Intelligence Source 
	And the client provides 'Source Type' as 'OL' 
	And the client provides 'Other Force Officer Force' as '52' 
	And the client provides 'Other Force Officer Details' as 'Officer Details' 
	And the client provides an Unsanitised Text 
	And the client provides 'Evaluation' as 'A' 
	And the client provides 'Text' as 'My first intel report to submit' 
	And the client provides 'Source' as '1' 
	And the client provides an Intelligence Risk 
	And the client provides 'Public Immunity Indicator' as 'F' 
	And the client provides 'Risk To Source Safety' as 'F' 
	And the client provides 'Should Be Sensitive' as 'F' 
	And the client provides 'Sensitive' as 'F' 
	And system parameter INTEL_SENSITIVE_ACTIONCODE contains 'A2' 
	#childObject name="reportLogEntry"
	And the client provides a 'Report Log Entry' 
	And the client provides 'Entry Index' as '1' 
	And the client provides 'Entry Date' as '2018-09-28' 
	And the client provides 'Entry Time' as '09:20:31.735' 
	And the client provides 'Entered By Officer Id' as '40920' 
	And the client provides 'Entry Type' as 'RES' 
	And the client provides 'Entry Text' as 'test for log entries' 
	#childObject name="reportLogEntry"
	And the client provides a 'Report Log Entry' 
	And the client provides 'Entry Index' as '2' 
	And the client provides 'Entry Date' as '2018-09-28' 
	And the client provides 'Entry Time' as '09:20:31.735' 
	And the client provides 'Entered By Officer Id' as '40920' 
	And the client provides 'Entry Type' as 'RES' 
	And the client provides 'Entry Text' as 'test for log entries' 
	When the client submits the 'Intelligence Report' to Business Services 
	Then the Intelligence Report will have its Status changed to 'REQUIRES ASSESSMENT SENSITIVE' 
	And a new task 'Assess Intelligence Report' is created for unit 'AMO' 
	And a new Task History Entry will be added to the 'Intelligence Report' 
	And the Confidence Level is set as '3' 
@draft	
Scenario: User performed Assess Intelligence task with action taken as Returned for correction and Intel status change to Require correction 
	Given an Intelligence Report exists with status of 'REQUIRES ASSESSMENT' 
	And a task 'Assess Intelligence Report' exists against that Intelligence Report 
	And system parameter INTEL_SENSITIVE_ACTIONCODE contains 'A2' 
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
	#childObject name="reportLogEntry"
	And the client provides a 'Report Log Entry' 
	And the client provides 'Entry Index' as '2' 
	And the client provides 'Entry Date' as '2018-09-28' 
	And the client provides 'Entry Time' as '09:20:31.735' 
	And the client provides 'Entered By Officer Id' as '40920' 
	And the client provides 'Entry Type' as 'RES' 
	And the client provides 'Entry Text' as 'test for log entries' 
	When the client submits the Intelligence Report to Business Services 
	Then the Intelligence Report will have its Status changed to 'REQUIRES CORRECTION' 
	And a new task 'Correct Intelligence Report' is created for unit 'AMO' 
	And a new Task History Entry will be added to the Intelligence Report 
	And the Task History Entry will record ActionTaken as 'RETURNED FOR CORRECTION' 
	And the Task History Entry will record RejectionReason as 'Correct this intelligence report' 
	And the Task History Entry will record ResubmissionDate as 'today+2' 
@draft	
Scenario: User performed Assess Intelligence task with action taken as Returned for correction and Intel status change to Require correction sensitive 
	Given an Intelligence Report exists with status of 'REQUIRES ASSESSMENT SENSITIVE' 
	And a task 'Assess Intelligence Report' exists against that Intelligence Report 
	And system parameter INTEL_SENSITIVE_ACTIONCODE contains 'A2' 
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
	#childObject name="reportLogEntry"
	And the client provides a 'Report Log Entry' 
	And the client provides 'Entry Index' as '2' 
	And the client provides 'Entry Date' as '2018-09-28' 
	And the client provides 'Entry Time' as '09:20:31.735' 
	And the client provides 'Entered By Officer Id' as '40920' 
	And the client provides 'Entry Type' as 'RES' 
	And the client provides 'Entry Text' as 'test for log entries' 
	When the client submits the Intelligence Report to Business Services 
	When the client submits the Intelligence Report to Business Services 
	Then the Intelligence Report will have its Status changed to 'REQUIRES CORRECTION SENSITIVE' 
	And a new task 'Correct Intelligence Report' is created for unit 'AMO' 
	And a new Task History Entry will be added to the Intelligence Report 
	And the Task History Entry will record ActionTaken as 'RETURNED FOR CORRECTION' 
	And the Task History Entry will record RejectionReason as 'Correct this intelligence report' 
	And the Task History Entry will record ResubmissionDate as 'today+2' 
@draft	
Scenario: User performed Assess Intelligence task with action taken as Returned for deletion and Intel status change to to be deleted 
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
	#And the client provides 'Next Reviewer Type' as 'UNIT'
	#childObject
	And the client provides an 'Sanitised Text' 
	And the client provides 'Text' as 'sanitisetext' 
	And the client provides 'Source' as '1' 
	And the client provides 'Evaluation' as 'A' 
	# childObject name="reportLogEntry"
	And the client provides a 'Report Log Entry' 
	And the client provides 'Entry Index' as '1' 
	And the client provides 'Entry Date' as '2018-09-14' 
	And the client provides 'Entry Time' as '09:20:31.735' 
	And the client provides 'Entered By Officer Id' as '40920' 
	And the client provides 'Entry Type' as 'RES' 
	And the client provides 'Entry Text' as 'test for log entries' 
	When the client submits the Intelligence Report to Business Services 
	Then the Intelligence Report will have its Status changed to 'TO BE DELETED' 
	And the task will be performed 
	And a new task 'Acknowledge Intelligence Report Returned Or Deleted' is created for unit 'AMO' 
	And a new Task History Entry will be added to the Intelligence Report 
	And the Task History Entry will record ActionTaken as 'RETURNED FOR DELETION' 
	And the Task History Entry will record 'RejectionReason' as 'This report is not valid' 
@draft	
Scenario Outline: User Performed Acknowledge request for deletion of Intelligence report task 
	Given an Intelligence Report exists with status of <inStatus> 
	And a task <existingTaskName> exists against that Intelligence Report 
	# Glue code will check if there are no validation errors
	And the client is eligible to perform the task 
	# Providing workflow fields
	And the client provides 'Action Taken' as <actionTaken> 
	# childObject name="reportLogEntry"
	And the client provides a 'Report Log Entry' 
	And the client provides 'Entry Index' as '1' 
	And the client provides 'Entry Date' as '2018-09-14' 
	And the client provides 'Entry Time' as '09:20:31.735' 
	And the client provides 'Entered By Officer Id' as '40920' 
	And the client provides 'Entry Type' as 'RES' 
	And the client provides 'Entry Text' as 'test for log entries' 
	# Now the task will be performed
	When the client submits the Intelligence Report to Business Services 
	Then the Intelligence Report will have its Status changed to <outStatus> 
	
	# Below are workflow parameters used and passed in above scripts
	Examples: 
		| instatus      | outstatus | existingTaskName                                    | <actionTaken>    |
		| TO BE DELETED | DELETED   | Acknowledge Intelligence Report Returned Or Deleted | Accept Rejection |
@draft			
Scenario Outline: User perform task 'Acknowledge Intelligence Report Returned Or Deleted' with Action taken as 'Request That Report Is Retained' 
	Given an Intelligence Report exists with status of 'TO BE DELETED' 
	And a task 'Acknowledge Intelligence Report Returned Or Deleted' exists against that Intelligence Report 
	And system parameter INTEL_SENSITIVE_ACTIONCODE contains 'A1' 
	And the client is eligible to perform the task 
	# Below data will be supplied in txData
	And the client provides  'Action Taken' as 'Request That Report Is Retained' 
	And the client provides  'Reason For Retention' as 'This is reason for retention' 
	# childObject name="reportLogEntry"
	And the client provides a 'Report Log Entry' 
	And the client provides 'Entry Index' as '1' 
	And the client provides 'Entry Date' as '2018-09-14' 
	And the client provides 'Entry Time' as '09:20:31.735' 
	And the client provides 'Entered By Officer Id' as '40920' 
	And the client provides 'Entry Type' as 'RES' 
	And the client provides 'Entry Text' as 'test for log entries' 
	When the client submits the Intelligence Report to Business Services 
	Then the Intelligence Report will have its Status changed to <OutStatus> 
	And a new task <NewTaskName> is created for unit 'AMO' 
	And a new Task History Entry will be added to the Intelligence report 
	And the Task History Entry will record 'Action Taken' as 'Request That Report Is Retained' 
	And the Task History Entry will record 'Reason For Retention' as 'This is reason for retention' 
	
	Examples: 
		| OutStatus           | NewTaskName                |
		| REQUIRES ASSESSMENT | Assess Intelligence Report |
@draft			
Scenario Outline: User perform task 'Acknowledge Intelligence Report Returned Or Deleted' with Action taken as 'Request That Report Is Retained' for Intel Report present in Sensitive status 
	Given an Intelligence Report exists with status of 'TO BE DELETED SENSITIVE' 
	And a task 'Acknowledge Intelligence Report Returned Or Deleted' exists against that Intelligence Report 
	And system parameter INTEL_SENSITIVE_ACTIONCODE contains 'A1' 
	And the client is eligible to perform the task 
	# Below data will be supplied in txData
	And the client provides  'Action Taken' as 'Request That Report Is Retained' 
	And the client provides  'Reason For Retention' as 'This is reason for retention' 
	# childObject name="reportLogEntry"
	And the client provides a 'Report Log Entry' 
	And the client provides 'Entry Index' as '1' 
	And the client provides 'Entry Date' as '2018-09-15' 
	And the client provides 'Entry Time' as '09:20:31.735' 
	And the client provides 'Entered By Officer Id' as '40920' 
	And the client provides 'Entry Type' as 'RES' 
	And the client provides 'Entry Text' as 'test for log entries' 
	When the client submits the Intelligence Report to Business Services 
	Then the Intelligence Report will have its Status changed to <OutStatus> 
	And a new task <NewTaskName> is created for unit 'AMO' 
	And a new Task History Entry will be added to the Intelligence report 
	And the Task History Entry will record 'Action Taken' as 'Request That Report Is Retained' 
	And the Task History Entry will record 'Reason For Retention' as 'This is reason for retention' 
	
	Examples: 
		| OutStatus                     | NewTaskName                |
		| REQUIRES ASSESSMENT SENSITIVE | Assess Intelligence Report |
@draft		
Scenario: User should be able to perform 'Link Intelligence Report' task if atleast one object link to Intel report in REQUIRES LINKING status 
	Given an Intelligence Report exists with status of 'REQUIRES LINKING' 
	And a task 'Link Intelligence report' exists against that Intelligence Report 
	And system parameter INTEL_SENSITIVE_ACTIONCODE contains 'A1' 
	# txData
	And the client provides 'Confirm linking complete' as 'true' 
	# details of person static object,Its a child object
	And the client provides a 'Person' 
	And the client provides 'Link Reason' as 'SUBJECT OF' 
	And the client provides 'Forename1' as 'Mark' 
	And the client provides 'Surname' as 'Taylor' 
	And the client provides 'Date Of Birth' as '1991-05-14' 
	And the client provides 'Place Of Birth' as 'London' 
	# childObject name="reportLogEntry"
	And the client provides a 'Report Log Entry' 
	And the client provides 'Entry Index' as '1' 
	And the client provides 'Entry Date' as '2018-09-14' 
	And the client provides 'Entry Time' as '09:20:31.735' 
	And the client provides 'Entered By Officer Id' as '40920' 
	And the client provides 'Entry Type' as 'RES' 
	And the client provides 'Entry Text' as 'test for log entries' 
	When the client submits the Intelligence Report to Business Services 
	Then the Intelligence Report will have its Status changed to 'LIVE' 
	And a new Task History Entry will be added to the Intelligence report 
	And the Task History Entry will record 'Confirm linking complete' as 'true' 
@draft	
Scenario: User should be able to perform 'Link Intelligence Report' task for Sensitive Intelligence Report, if atleast one object link to Intel report  in REQUIRES LINKING SENSITIVE status 
	Given an Intelligence Report exists with status of 'REQUIRES LINKING SENSITIVE' 
	And a task 'Link Intelligence report' exists against that Intelligence Report 
	And system parameter INTEL_SENSITIVE_ACTIONCODE contains 'A1' 
	# txData
	And the client provides 'Confirm linking complete' as 'true' 
	# details of person static object,Its a child object
	And the client provides a 'Person' 
	And the client provides 'Link Reason' as 'SUBJECT OF' 
	And the client provides 'Forename1' as 'Mark' 
	And the client provides 'Surname' as 'Taylor' 
	And the client provides 'Date Of Birth' as '1991-05-14' 
	And the client provides 'Place Of Birth' as 'London' 
	# childObject name="reportLogEntry"
	And the client provides a 'Report Log Entry' 
	And the client provides 'Entry Index' as '1' 
	And the client provides 'Entry Date' as '2018-09-14' 
	And the client provides 'Entry Time' as '09:20:31.735' 
	And the client provides 'Entered By Officer Id' as '40920' 
	And the client provides 'Entry Type' as 'RES' 
	And the client provides 'Entry Text' as 'test for log entries' 
	When the client submits the Intelligence Report to Business Services 
	Then the Intelligence Report will have its Status changed to 'LIVE SENSITIVE' 
	And a new Task History Entry will be added to the Intelligence report 
	And the Task History Entry will record 'Confirm linking complete' as 'true' 
@draft	
Scenario: User should be able to perform 'Link Intelligence Report' task if atleast one object link to Intel report in LIVE status 
	Given an Intelligence Report exists with status of 'LIVE' 
	And a task 'Link Intelligence report' exists against that Intelligence Report 
	And system parameter INTEL_SENSITIVE_ACTIONCODE contains 'A1' 
	# txData
	And the client provides 'Confirm linking complete' as 'true' 
	# details of person static object,Its a child object
	And the client provides a 'Person' 
	And the client provides 'Link Reason' as 'SUBJECT OF' 
	And the client provides 'Forename1' as 'Mark' 
	And the client provides 'Surname' as 'Taylor' 
	And the client provides 'Date Of Birth' as '1991-05-14' 
	And the client provides 'Place Of Birth' as 'London' 
	# childObject name="reportLogEntry"
	And the client provides a 'Report Log Entry' 
	And the client provides 'Entry Index' as '1' 
	And the client provides 'Entry Date' as '2018-09-14' 
	And the client provides 'Entry Time' as '09:20:31.735' 
	And the client provides 'Entered By Officer Id' as '40920' 
	And the client provides 'Entry Type' as 'RES' 
	And the client provides 'Entry Text' as 'test for log entries' 
	When the client submits the Intelligence Report to Business Services 
	Then the Intelligence Report will have its Status changed to 'LIVE' 
	And a new Task History Entry will be added to the Intelligence report 
	And the Task History Entry will record 'Confirm linking complete' as 'true' 
@draft	
Scenario: User should be able to perform 'Link Intelligence Report' task for Sensitive Intelligence Report, if atleast one object link to Intel report in LIVE SENSITIVE status 
	Given an Intelligence Report exists with status of 'LIVE SENSITIVE' 
	And a task 'Link Intelligence report' exists against that Intelligence Report 
	And system parameter INTEL_SENSITIVE_ACTIONCODE contains 'A1' 
	# txData
	And the client provides 'Confirm linking complete' as 'true' 
	# details of person static object,Its a child object
	And the client provides a 'Person' 
	And the client provides 'Link Reason' as 'SUBJECT OF' 
	And the client provides 'Forename1' as 'Mark' 
	And the client provides 'Surname' as 'Taylor' 
	And the client provides 'Date Of Birth' as '1991-05-14' 
	And the client provides 'Place Of Birth' as 'London' 
	# childObject name="reportLogEntry"
	And the client provides a 'Report Log Entry' 
	And the client provides 'Entry Index' as '1' 
	And the client provides 'Entry Date' as '2018-09-14' 
	And the client provides 'Entry Time' as '09:20:31.735' 
	And the client provides 'Entered By Officer Id' as '40920' 
	And the client provides 'Entry Type' as 'RES' 
	And the client provides 'Entry Text' as 'test for log entries' 
	When the client submits the Intelligence Report to Business Services 
	Then the Intelligence Report will have its Status changed to 'LIVE SENSITIVE' 
	And a new Task History Entry will be added to the Intelligence report 
	And the Task History Entry will record 'Confirm linking complete' as 'true' 
@draft	
Scenario: User should get Log of Enquiry mandatory validation message for Entry Date, Entry Time and Submitting officer ID 
	Given client opted to perform action 'Create' on an 'Intelligence Report' 
	And the client provides 'Title' as 'IntelReport' 
	And the client provides 'Date Submitted' as '16/06/2017' 
	And the client provides 'Time Submitted' as '09:20:31.735' 
	And the client provides 'Priority' as '1' 
	And the client provides 'Force' as ${bs.cucumber.forceId}
	And the client provides 'Owning Force Id' as '42' 
	And the client provides 'Bcu' as 'ESSEX NORTH' 
	And the client provides 'Station Code' as 'CHELMSFORD' 
	And the client provides 'Beat Code' as 'CHELMSFORD E05004096' 
	And the client provides 'UnsanHandling' as 'C' 
	And the client provides 'UnsanHandling Conditions' as 'Careful' 
	And the client provides 'Unsanitised Handling Conditions Reason' as 'Instructions' 
	And the client provides 'Unsanitised Action Code' as 'A1' 
	And the client provides 'Submission Sanitisation Code' as 'S1' 
	And the client provides 'Provenance' as 'dummy' 
	And the client provides 'Shared With' as 'dummy' 
	And the client provides 'First Know By Source' as 'dummy' 
	And the client provides 'Last Know By Source' as 'dummy' 
	And the client provides 'More Information From Source' as 'dummy' 
	#txData
	And the client provides 'Force Id' as '42' 
	And the client provides 'Submit To Information Manager' as 'AMO' 
	And the client provides 'Submit To Linking Unit' as 'AMO' 
	And the client provides 'Submitting Officer Id' as '20' 
	And the client provides 'Submitting Officer Display Value' as '20' 
	And the client provides 'Completed' as 'Yes' 
	And the client provides 'Priority Display Value' as 'priority1' 
	And the client provides an Intelligence Information Type 
	And the client provides 'Type' as 'ANIMAL/WILDLIFE RELATED' 
	And the client provides an Intelligence Source 
	And the client provides 'Source Type' as 'OL' 
	And the client provides 'Other Force Officer Force' as '52' 
	And the client provides 'Other Force Officer Details' as 'Officer Details' 
	And the client provides an Unsanitised Text 
	And the client provides 'Evaluation' as 'A' 
	And the client provides 'Text' as 'My first intel report to submit' 
	And the client provides 'Source' as '1' 
	And the client provides an Intelligence Risk 
	And the client provides 'Public Immunity Indicator' as 'F' 
	And the client provides 'Risk To Source Safety' as 'F' 
	And the client provides 'Should Be Sensitive' as 'F' 
	And the client provides 'Sensitive' as 'F' 
	#childObject name="reportLogEntry"
	And the client provides a 'Report Log Entry' 
	And the client provides 'Entry Index' as '2' 
	And the client provides 'Entry Type' as 'RES' 
	And the client provides 'Entry Text' as 'test for log entries' 
	When the client submits the 'Intelligence Report' to Business Services 
	Then the Intelligence Report will have its Status changed to 'REQUIRES ASSESSMENT' 
	And a new task 'Assess Intelligence Report' is created for unit 'AMO' 
	And a new Task History Entry will be added to the 'Intelligence Report' 
	And the Confidence Level is set as '3' 
	Then Error will be returned for field 'Entry Date' with message 'entryDate expected' 
	Then Error will be returned for field 'Entry Time' with message 'entryTime expected' 
	Then Error will be returned for field 'Entered By Officer Id' with message 'entryByOfficer expected' 
@draft	
Scenario: User should get Log of Enquiry mandatory validation message for Entry Type, Entry Text 
	Given client opted to perform action 'Create' on an 'Intelligence Report' 
	And the client provides 'Title' as 'IntelReport' 
	And the client provides 'Date Submitted' as '16/06/2017' 
	And the client provides 'Time Submitted' as '09:20:31.735' 
	And the client provides 'Priority' as '1' 
	And the client provides 'Force' as ${bs.cucumber.forceId}
	And the client provides 'Owning Force Id' as '42' 
	And the client provides 'Bcu' as 'ESSEX NORTH' 
	And the client provides 'Station Code' as 'CHELMSFORD' 
	And the client provides 'Beat Code' as 'CHELMSFORD E05004096' 
	And the client provides 'UnsanHandling' as 'C' 
	And the client provides 'UnsanHandling Conditions' as 'Careful' 
	And the client provides 'Unsanitised Handling Conditions Reason' as 'Instructions' 
	And the client provides 'Unsanitised Action Code' as 'A1' 
	And the client provides 'Submission Sanitisation Code' as 'S1' 
	And the client provides 'Provenance' as 'dummy' 
	And the client provides 'Shared With' as 'dummy' 
	And the client provides 'First Know By Source' as 'dummy' 
	And the client provides 'Last Know By Source' as 'dummy' 
	And the client provides 'More Information From Source' as 'dummy' 
	#txData
	And the client provides 'Force Id' as '42' 
	And the client provides 'Submit To Information Manager' as 'AMO' 
	And the client provides 'Submit To Linking Unit' as 'AMO' 
	And the client provides 'Submitting Officer Id' as '20' 
	And the client provides 'Submitting Officer Display Value' as '20' 
	And the client provides 'Completed' as 'Yes' 
	And the client provides 'Priority Display Value' as 'priority1' 
	And the client provides an Intelligence Information Type 
	And the client provides 'Type' as 'ANIMAL/WILDLIFE RELATED' 
	And the client provides an Intelligence Source 
	And the client provides 'Source Type' as 'OL' 
	And the client provides 'Other Force Officer Force' as '52' 
	And the client provides 'Other Force Officer Details' as 'Officer Details' 
	And the client provides an Unsanitised Text 
	And the client provides 'Evaluation' as 'A' 
	And the client provides 'Text' as 'My first intel report to submit' 
	And the client provides 'Source' as '1' 
	And the client provides an Intelligence Risk 
	And the client provides 'Public Immunity Indicator' as 'F' 
	And the client provides 'Risk To Source Safety' as 'F' 
	And the client provides 'Should Be Sensitive' as 'F' 
	And the client provides 'Sensitive' as 'F' 
	# childObject name="reportLogEntry"
	And the client provides a 'Report Log Entry' 
	And the client provides 'Entry Index' as '1' 
	And the client provides 'Entry Date' as '2018-09-14' 
	And the client provides 'Entry Time' as '09:20:31.735' 
	And the client provides 'Entered By Officer Id' as '40920' 
	When the client submits the 'Intelligence Report' to Business Services 
	Then the Intelligence Report will have its Status changed to 'REQUIRES ASSESSMENT' 
	And a new task 'Assess Intelligence Report' is created for unit 'AMO' 
	And a new Task History Entry will be added to the 'Intelligence Report' 
	And the Confidence Level is set as '3' 
	Then Error will be returned for field 'Entry Type' with message 'entryType expected' 
	Then Error will be returned for field 'Entry Text' with message 'entryText expected'