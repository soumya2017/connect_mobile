Feature: CON-42860 Perform task 'Link Intelligence report'

  Scenario: Happy Path status of 'REQUIRES LINKING' 
  #BS-INCLUDE feature=CON-42859_AssessIntelligenceReport_Accepted.feature, scenario=Happy Path with status of 'REQUIRES ASSESSMENT'
    Given an Intelligence Report exists with status of 'REQUIRES LINKING'
    And a task 'Link Intelligence Report' exists against that Intelligence Report
   # And system parameter INTEL_SENSITIVE_ACTIONCODE contains ${bs.cucumber.intelSensitiveActionCode}
    # txData
    And the client is eligible to perform the task
    And the client provides 'Confirm Linking Complete' as 'Yes'
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
    
    # Link Organisation To Incident
    And the client links a 'New Organisation' referred to as 'associatedOrganisation' with link reason of 'SUBJECT OF' 
    And the client provides data on 'associatedOrganisation'
    And the client provides 'Name' as 'Northgate'
    And the client provides 'Branch Division' as 'Northgate'
    
     # Link Vehicle To Incident
    And the client links a 'New Vehicle' referred to as 'BMW12345' with link reason of 'SUBJECT OF' 
    And the client provides data on 'BMW12345'
    And the client provides 'Foreign Vehicle' as 'true' 
    And the client provides 'Vrm Transformed' as 'true'
    And the client provides 'Model Transformed' as 'true'
    And the client provides 'Chassis Transformed' as 'true'
    And the client provides 'Registration Number' as '1114'
    And the client provides 'Registration Type Given' as 'FULL'
    And the client provides 'Model' as 'U4-69'
    And the client provides 'Make' as 'U4'
    And the client provides 'Registration Country' as 'UNITED KINGDOM'
    
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to 'LIVE'
    And a new Task History Entry will be added to the Intelligence report
    And a link will be added to the 'Intelligence Report' for 'Person' referred to as 'FirstSubject' with link reason of 'SUBJECT OF'
	And 'FirstSubject' has 'Forename1' as 'Mark' 
	And 'FirstSubject' has 'Surname' as 'Taylor' 
	And 'FirstSubject' has 'Date Of Birth' as '1991-09-14' 
	And 'FirstSubject' has 'Place Of Birth' as 'London'
	
	And a link will be added to the 'Intelligence Report' for 'Vehicle' referred to as 'BMW12345' with link reason of 'SUBJECT OF'
	And 'BMW12345' has 'Foreign Vehicle' as 'true' 
	And 'BMW12345' has 'Vrm Transformed' as 'true' 
	And 'BMW12345' has 'Model Transformed' as 'true' 
	And 'BMW12345' has 'Chassis Transformed' as 'true'
	And 'BMW12345' has 'Registration Number' as '1114' 
	And 'BMW12345' has 'Registration Type Given' as 'FULL' 
	And 'BMW12345' has 'Model' as 'U4-69'
	And 'BMW12345' has 'Make' as 'U4'
	And 'BMW12345' has 'Registration Country' as 'UNITED KINGDOM'
	
	And a link will be added to the 'Intelligence Report' for 'Organisation' referred to as 'associatedOrganisation' with link reason of 'SUBJECT OF'
	And 'associatedOrganisation' has 'Name' as 'Northgate' 
	And 'associatedOrganisation' has 'Branch Division' as 'Northgate'

  Scenario: Happy Path status of 'REQUIRES LINKING SENSITIVE'
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
#    And the client provides data on link
#    And the client provides a 'PeContactInfo'                            
#    And the client provides 'emailAddress' as 'mark@abc.com' 
    And the client provides data on 'FirstSubject'
    And the client provides 'Forename1' as 'Mark'
    And the client provides 'Surname' as 'Taylor'
    And the client provides 'Date Of Birth' as '14/09/1991'
    And the client provides 'Place Of Birth' as 'London'
    And the client links from 'FirstSubject' to a New 'Location' referred to as 'subjectLocation' with link reason of 'HOME ADDRESS' 
    And the client provides data on 'subjectLocation'
    And the client provides 'PremisesName' as 'Holborn Gate, Bell Pottinger Communications Ltd'
    And the client provides 'Street Name' as 'High Holborn'
    And the client provides 'Town' as 'London'
    And the client provides 'Force' as '42'
    
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to 'LIVE SENSITIVE'
    And a new Task History Entry will be added to the Intelligence report
    And a link will be added to the 'Intelligence Report' for 'Person' referred to as 'FirstSubject' with link reason of 'SUBJECT OF'
	And 'FirstSubject' has 'Forename1' as 'Mark' 
	And 'FirstSubject' has 'Surname' as 'Taylor' 
	And 'FirstSubject' has 'Date Of Birth' as '1991-09-14' 
	And 'FirstSubject' has 'Place Of Birth' as 'London'
	And a link will be added to the 'FirstSubject' for 'Location' referred to as 'subjectLocation' with link reason of 'HOME ADDRESS'
	And 'subjectLocation' has 'PremisesName' as 'Holborn Gate, Bell Pottinger Communications Ltd'
    And 'subjectLocation' has 'Street Name' as 'High Holborn'
    And 'subjectLocation' has 'Town' as 'London'
    And 'subjectLocation' has 'Force' as '42'
	

  Scenario: User should not be able to perform 'Link Intelligence Report' task if there is no object link to Intel Report in REQUIRES LINKING status
  #BS-INCLUDE feature=CON-42859_AssessIntelligenceReport_Accepted.feature, scenario=Happy Path with status of 'REQUIRES ASSESSMENT'
    Given an Intelligence Report exists with status of 'REQUIRES LINKING'
    And a task 'Link Intelligence Report' exists against that Intelligence Report
    And system parameter INTEL_SENSITIVE_ACTIONCODE contains ${bs.cucumber.intelSensitiveActionCode}
    And the client is eligible to perform the task
    And the client provides 'Confirm linking complete' as 'false'
    When the client submits the Intelligence Report to Business Services
    Then Error will be returned containing 'You Must Create At Least One Link To Person, Location, Vehicle etc.'

  Scenario: User should not be able to perform 'Link Intelligence Report' task if there is no object link to Intel Report in REQUIRES LINKING SENSITIVE status
  #BS-INCLUDE feature=CON-42859_AssessIntelligenceReport_Accepted.feature, scenario=Happy Path status of 'REQUIRES ASSESSMENT SENSITIVE'
    Given an Intelligence Report exists with status of 'REQUIRES LINKING SENSITIVE'
    And a task 'Link Intelligence Report' exists against that Intelligence Report
    And system parameter INTEL_SENSITIVE_ACTIONCODE contains ${bs.cucumber.intelSensitiveActionCode}
    And the client is eligible to perform the task
    And the client provides 'Confirm linking complete' as 'false'
    When the client submits the Intelligence Report to Business Services
    Then Error will be returned containing 'You Must Create At Least One Link To Person, Location, Vehicle etc.'
 