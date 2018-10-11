Feature: 'Link Static objects to event'
@draft
  Scenario: User should be able to perform  'SOME TASK'
    Given an Incident exists with status of 'anyStatus'
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
    And the client links a New 'Person' referrred to as 'FirstSubject' with link reason of 'SUBJECT OF' 
    And the client provides data on 'FirstSubject'
    And the client provides 'Forename1' as 'Mark'
    And the client provides 'Surname' as 'Taylor'
    And the client provides 'Date Of Birth' as '14/09/1991'
    And the client provides 'Place Of Birth' as 'London'

    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to 'LIVE'
    And a new Task History Entry will be added to the Intelligence report
    And a link will be added to the 'intelligence report' for 'Person' referrred to as 'FirstSubject' with link reason of 'SUBJECT OF'
	And 'FirstSubject' has 'Forename1' as 'Mark' 
	And 'FirstSubject' has 'Surname' as 'Taylor' 
	And 'FirstSubject' has 'Date Of Birth' as '1991-09-14' 
	And 'FirstSubject' has 'Place Of Birth' as 'London'   

@draft
  Scenario: User should be able to perform 'SOME TASK'
    Given an Incident exists with status of 'someStatus'
    And a task 'someTask' exists against that Intelligence Report
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
    And the client links a New 'Person' referrred to as 'FirstSubject' with link reason of 'SUBJECT OF' 
    And the client provides data on link 
    And the client provides a 'PeContactInfo'                            
    And the client provides 'EmailAddress' as 'abc@gmail.com' 
    And the client provides data on 'FirstSubject'
    And the client provides 'Forename1' as 'Mark'
    And the client provides 'Surname' as 'Taylor'
    And the client provides 'Date Of Birth' as '14/09/1991'
    And the client provides 'Place Of Birth' as 'London'
    And the client links from 'FirstSubject' to a New 'Location' referrred to as 'FirstSubjectLocation' with link reason of 'HOME ADDRESS' 
    And the client provides data on 'FirstSubjectLocation'
    And the client provides 'Street Name' as 'High Street'
    And the client provides 'Town' as 'StreetTown'

    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to 'somestatus'
    And a new Task History Entry will be added to the Intelligence report
    
    And a link will be added to the 'intelligence report' for 'Person' referrred to as 'FirstSubject' with link reason of 'SUBJECT OF'
    And the link has 'peContactInfo' as 'victimPeContactInfo'
	And 'victimPeContactInfo' has 'emailAddress' as 'abc@gmail.com'
	And 'FirstSubject' has 'Forename1' as 'Mark' 
	And 'FirstSubject' has 'Surname' as 'Taylor' 
	And 'FirstSubject' has 'Date Of Birth' as '1991-09-14' 
	And 'FirstSubject' has 'Place Of Birth' as 'London'   
	And a link will be added to the 'FirstSubject' for 'Location' referrred to as 'FirstSubjectAddress' with link reason of 'HOME ADDRESS'
	And 'FirstSubjectAddress' has 'streetName' as 'High Street' 
	And 'FirstSubjectAddress' has 'town' as 'StreetTown' 
	
	
	@draft
  Scenario: User should be able to perform 'SOME TASK'
    Given an Incident exists with status of 'someStatus'
    And a task 'someTask' exists against that Intelligence Report
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
    
      # Link Vehicle To Incident
    And the client links a New 'Vehicle' referrred to as 'BMW12345' with link reason of 'DAMAGED' 
    And the client provides data on link 
    And the client provides a 'IncidentVehicleInfo'                            
    And the client provides 'TimeVehicleOwned' as 'BMW' 
    And the client provides 'PolicyNumber' as '12345' 
    And the client provides data on 'BMW12345'
    And the client provides 'RegistrationNumber' as '1114'
    And the client provides 'RegistrationTypeGiven' as 'FULL'
    And the client provides 'Model' as 'U4-69'
    And the client provides 'Make' as 'U4'
    
    # Link Location To Incident
    And the client links a New 'Location' referrred to as 'IncidentLocation' with link reason of 'INCIDENT LOCATION' 
    And the client provides data on 'IncidentLocation'
    And the client provides 'Street Name' as 'Hoffmanns Way'
    And the client provides 'Town' as 'Chelmsford'
    And the client provides 'PremisesName' as 'Former 1'
    
    # Link Organisation To Incident
    And the client links a New 'Organisation' referrred to as 'associatedOrganisation' with link reason of 'ASSOCIATED' 
    And the client provides data on 'associatedOrganisation'
    And the client provides 'Name' as 'Northgate'
    And the client provides 'BranchDivision' as 'Northgate'
    
     # details of SUSPECT
    And the client links a New 'Person' referrred to as 'suspect' with link reason of 'SUSPECT' 
    And the client provides data on link 
    And the client provides a 'IncidentPersonInfo'                            
    And the client provides 'SuspectStatus' as 'SUSPECT' 
    And the client provides 'SuVictimSuspectRelationship' as 'RELATIONSHIP UNKNOWN' 
    And the client provides 'SuGroundsSummary' as 'Suspect status supporting information' 
    
    And the client provides a 'PeContactInfo'                            
    And the client provides 'InvitationToEngage' as 'NOT YET SENT' 
    
    And the client provides data on 'suspect'
    And the client provides 'Forename1' as 'Paul'
    And the client provides 'Surname' as 'DSOUZA'
    And the client provides 'Date Of Birth' as '14/09/1991'
    And the client provides 'Place Of Birth' as 'London'
    And the client links from 'suspect' to a New 'Location' referrred to as 'suspectLocation' with link reason of 'HOME ADDRESS' 
    And the client provides data on 'suspectLocation'
    And the client provides 'PremisesName' as 'Holborn Gate, Bell Pottinger Communications Ltd'
    And the client provides 'Street Name' as 'High Holborn'
    And the client provides 'Town' as 'London'
    
    And the client links from 'suspect' to a New 'Comms' referrred to as 'suspectComms' with link reason of 'E MAIL' 
    And the client provides data on 'suspectComms'
    And the client provides 'CommsType' as 'E-MAIL PERSONAL'
    And the client provides 'MainNumber' as 'northgate@abc.com'
    
    And the client links from 'suspect' to a New 'Organisation' referrred to as 'suspectOrganisation' with link reason of 'ASSOCIATED' 
    And the client provides data on 'suspectOrganisation'
    And the client provides 'Name' as 'Northgate'
    And the client provides 'BranchDivision' as 'Northgate'
    
  # details of VICTIM
    And the client links a New 'Person' referrred to as 'victim' with link reason of 'VICTIM' 
    And the client provides data on link 
    And the client provides a 'IncidentPersonInfo'                            
    And the client provides 'VsAgreedToVictimSupport' as 'true' 
    And the client provides 'VictimElectsToBeUpdated' as 'IN_BUSINESS' 
    And the client provides 'SuspectArrested' as 'true' 
    
    And the client provides a 'PeContactInfo'                            
    And the client provides 'InvitationToEngage' as 'NOT YET SENT' 
    
    And the client provides data on 'victim'
    And the client provides 'Forename1' as 'Tony'
    And the client provides 'Surname' as 'DSOUZA'
    And the client provides 'Date Of Birth' as '14/09/1991'
    And the client provides 'Place Of Birth' as 'London'
    And the client links from 'victim' to a New 'Location' referrred to as 'victimLocation' with link reason of 'HOME ADDRESS' 
    And the client provides data on 'victimLocation'
    And the client provides 'Street Name' as 'High Street'
    And the client provides 'Town' as 'Chelmsford'
    
    And the client links from 'victim' to a New 'Comms' referrred to as 'victimComms' with link reason of 'HOME TEL' 
    And the client provides data on 'victimComms'
    And the client provides 'CommsType' as 'E-MAIL PERSONAL'
    And the client provides 'MainNumber' as '7875628369'
    
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to 'somestatus'
    And a new Task History Entry will be added to the Intelligence report
    
  	And a link will be added to the 'incident' for 'Vehicle' referrred to as 'BMW12345' with link reason of 'DAMAGED'
    And the link has 'IncidentVehicleInfo' as 'IncidentVehicle'
	And 'IncidentVehicle' has 'TimeVehicleOwned' as 'BMW'
	And 'IncidentVehicle' has 'PolicyNumber' as '12345' 
	And 'IncidentVehicle' has 'Surname' as 'Taylor' 
	And 'IncidentVehicle' has 'Date Of Birth' as '1991-09-14' 
	And 'IncidentVehicle' has 'Place Of Birth' as 'London'   
	
	And 'BMW12345' has 'RegistrationNumber' as '1114'
	And 'BMW12345' has 'RegistrationTypeGiven' as 'FULL'
	And 'BMW12345' has 'Model' as 'U4-69'
	And 'BMW12345' has 'Make' as 'U4'
	
	And a link will be added to the 'incident' for 'Location' referrred to as 'IncidentLocation' with link reason of 'INCIDENT LOCATION'
	And 'IncidentLocation' has 'Street Name' as 'Hoffmanns Way'
	And 'IncidentLocation' has 'Town' as 'Chelmsford'
	And 'IncidentLocation' has 'PremisesName' as 'Former 1'
	
	And a link will be added to the 'incident' for 'Organisation' referrred to as 'associatedOrganisation' with link reason of 'ASSOCIATED'
	And 'associatedOrganisation' has 'Name' as 'Northgate'
	And 'associatedOrganisation' has 'BranchDivision' as 'Northgate'
	
	And a link will be added to the 'incident' for 'Person' referrred to as 'victim' with link reason of 'VICTIM'
	And the link has 'IncidentPersonInfo' as 'victimIncidentPerson'
	And 'victimIncidentPerson' has 'VsAgreedToVictimSupport' as 'true'
	And 'victimIncidentPerson' has 'VictimElectsToBeUpdated' as 'IN_BUSINESS'
	And 'victimIncidentPerson' has 'SuspectArrested' as 'true'
	
	And the link has 'peContactInfo' as 'victimPeContactInfo'
	And 'victimPeContactInfo' has 'InvitationToEngage' as 'NOT YET SENT'
	
	And 'victim' has 'Forename1' as 'Tony'
	And 'victim' has 'Surname' as 'DSOUZA'
	And 'victim' has 'Date Of Birth' as '14/09/1991'
	And 'victim' has 'Place Of Birth' as 'London'
	
	And a link will be added to the 'victim' for 'Location' referrred to as 'victimLocation' with link reason of 'HOME ADDRESS'
	And 'victimLocation' has 'PremisesName' as 'Holborn Gate, Bell Pottinger Communications Ltd' 
	And 'victimLocation' has 'streetName' as 'High Street' 
	And 'victimLocation' has 'town' as 'Chelmsford'
	
	And a link will be added to the 'victim' for 'Comms' referrred to as 'victimComms' with link reason of 'HOME TEL'
	And 'victimComms' has 'CommsType' as 'E-MAIL PERSONAL' 
	And 'victimComms' has 'MainNumber' as '7875628369' 
	
	And a link will be added to the 'victim' for 'Organisation' referrred to as 'victimOrganisation' with link reason of 'ASSOCIATED'
	And 'victimOrganisation' has 'Name' as 'Northgate' 
	And 'victimOrganisation' has 'BranchDivision' as 'Northgate'

	And a link will be added to the 'incident' for 'Person' referrred to as 'suspect' with link reason of 'suspect'
	And the link has 'IncidentPersonInfo' as 'IncidentPerson'
	And 'IncidentPerson' has 'SuspectStatus' as 'SUSPECT'
	And 'IncidentPerson' has 'SuVictimSuspectRelationship' as 'RELATIONSHIP UNKNOWN'
	And 'IncidentPerson' has 'SuGroundsSummary' as 'Suspect status supporting information'
	
	And the link has 'peContactInfo' as 'suspectPeContactInfo'
	And 'suspectPeContactInfo' has 'InvitationToEngage' as 'NOT YET SENT'
	
	And 'suspect' has 'Forename1' as 'Paul'
	And 'suspect' has 'Surname' as 'DSOUZA'
	And 'suspect' has 'Date Of Birth' as '14/09/1991'
	And 'suspect' has 'Place Of Birth' as 'London'
	
	And a link will be added to the 'suspect' for 'Location' referrred to as 'suspectLocation' with link reason of 'HOME ADDRESS'
	And 'suspectLocation' has 'PremisesName' as 'Holborn Gate, Bell Pottinger Communications Ltd' 
	And 'suspectLocation' has 'streetName' as 'High Holborn' 
	And 'suspectLocation' has 'town' as 'London'
	
	And a link will be added to the 'suspect' for 'Comms' referrred to as 'suspectComms' with link reason of 'E MAIL'
	And 'suspectComms' has 'CommsType' as 'E-MAIL PERSONAL' 
	And 'suspectComms' has 'MainNumber' as 'northgate@abc.com' 
	
	And a link will be added to the 'suspect' for 'Organisation' referrred to as 'suspectOrganisation' with link reason of 'ASSOCIATED'
	And 'suspectOrganisation' has 'Name' as 'Northgate' 
	And 'suspectOrganisation' has 'BranchDivision' as 'Northgate'
