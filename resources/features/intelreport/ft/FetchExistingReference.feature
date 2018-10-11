Feature: Perform task 'Link Intelligence report'
@draft
Scenario: Create static object Person
	Given a static object 'Person' needs to persist in Pole referred to as 'ExistingPersonInPole'
	And the client provides 'surname' as 'cuke.person'
	And the client provides 'forename1' as 'cuke.Mark'
	And the client provides 'forename2' as 'Mark'
	And the client provides 'Place Of Birth' as 'London'
	And the client provides 'Date Of Birth' as '14/09/2017' 
	And the client provides 'Researched' as 'true' 
	When the client submits the 'ExistingPersonInPole' to Business Services
	Then 'ExistingPersonInPole' will persist in Pole
@draft
  Scenario: Happy Path status of 'REQUIRES LINKING' 
  #BS-INCLUDE feature=FetchExistingReference.feature, scenario=Create static object Person
  #BS-INCLUDE feature=CON-42859_AssessIntelligenceReport_Accepted.feature, scenario=Happy Path with status of 'REQUIRES ASSESSMENT'
    Given an Intelligence Report exists with status of 'REQUIRES LINKING'
    And a task 'Link Intelligence Report' exists against that Intelligence Report
   # And system parameter INTEL_SENSITIVE_ACTIONCODE contains <bs.cucumber.intelSensitiveActionCode>
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
    And the client links an 'Existing Person' referred to as 'FirstSubject' with link reason of 'SUBJECT OF' 
    And the client provides data on 'FirstSubject'
    And the client provides 'Object Ref' for the 'ExistingPersonInPole'
    
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to 'LIVE'
    And a new Task History Entry will be added to the Intelligence report
    And a link will be added to the 'Intelligence Report' for 'Person' referred to as 'FirstSubject' with link reason of 'SUBJECT OF'
	And 'FirstSubject' has 'Forename1' as 'cuke.Mark' 
	And 'FirstSubject' has 'Surname' as 'cuke.person' 
	And 'FirstSubject' has 'Date Of Birth' as '2017-09-14' 
	And 'FirstSubject' has 'Place Of Birth' as 'London'
