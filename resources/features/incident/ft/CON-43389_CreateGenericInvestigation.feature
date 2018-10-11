#Author: abhishek.mishra@northgateps.com
@draft
Feature: CON-43389 Create Generic Investigation 
 @HappyPathTest 
Scenario Outline: 
	Create generic Investigation without linking any optional static/event objects. Supply data for all the fields in Investigation record. 

	Given client opted to perform action 'Create' on an 'Investigation Record' 
	#Basic details card
	And the client provides 'Reported Date' as '2018-02-02' 
	And the client provides 'Reported Time' as '09:20:31.735' 
	And the client provides 'Incident On Or From Date' as '2018-02-01' 
	And the client provides 'Incident At Or From Time' as '09:20:31.735' 
	And the client provides 'Incident To Date' as '2018-02-01' 
	And the client provides 'Incident To Time' as '11:20:31.735' 
	And the client provides 'How Reported' as '999' 
	And the client provides 'officerReportingId' as ${bs.cucumber.officerReportingId} 
	And the client provides 'Source Id' as 'SOCIAL WORKER' 
	And the client provides 'Victim Crown' as "false'" 
	And the client provides 'Incident Summary' as 'This is Investigation Summary' 
	And the client provides 'sourceFurtherInformation' as 'text data' 
	
	#txData
	And the client provides 'completed' as 'true' 
	And the client provides 'qaUnit' as ${bs.cucumber.qaUnit} 
	And the client provides 'imuUnit' as ${bs.cucumber.imuUnit} 
	And the client provides 'linkingUnit' as ${bs.cucumber.linkingUnit} 
	And the client provides 'forceId' as ${bs.cucumber.forceId}
	And the client provides 'outOfForce' as 'false' 
	
	
	#childObject name="incidentOtherReference"
	And the client provides 'Reference Type' as 'STORM' 
	And the client provides 'Reference Value' as '9933418767' 
	
	#staticObject name="location",Incident Location Card
	And the client provides 'Premises Type' as 'CAR PARK' 
	And the client provides 'Premises Number' as '101' 
	And the client provides 'Postcode' as 'CM1 1NE' 
	And the client provides 'Premises Name' as 'Norwich Ind. Estate' 
	And the client provides 'Sub Premises Name' as 'abc' 
	And the client provides 'Sub Sub Premises Name' as 'def' 
	And the client provides 'streetName' as 'James Street' 
	And the client provides 'Locality' as 'Castle Donnington' 
	And the client provides 'town' as 'Chelmsford' 
	And the client provides 'County' as 'Essex' 
	And the client provides 'Country' as 'UNITED KINGDOM' 
	#Force,district,sector code and beat code are mandatory fields
	And the client provides 'Force' as '42' 
	And the client provides 'Disctrict' as 'ESSEX NORTH' 
	And the client provides 'Sector Code' as 'CHELMSFORD' 
	And the client provides 'Beat Code' as 'CHELMSFORD E05004106' 
	And the client provides 'Local Name' as 'Forex Square' 
	
	#childObject name="incidentLogEntry", Enquiry log Card
	And the client provides a 'Incident Log Entry' 
	And the client provides 'Entry Index' as '1' 
	And the client provides 'Entry Date' as '2018-09-28' 
	And the client provides 'Entry Time' as '09:20:31.735' 
	And the client provides 'Entered By Officer Id' as '40920' 
	And the client provides 'Entry Type' as 'RES' 
	And the client provides 'Entry Text' as 'test for log entries' 
	
	#commonChildObject name="policyFile" ,Decision log card
	And the client provides a 'Policy File' 
	And the client provides 'Entry Date' as '2018-02-02' 
	And the client provides 'Entry time' as '09:20:31.735' 
	And the client provides 'enteredByOfficerId' as ${bs.cucumber.enteredByOfficerId} 
	And the client provides 'Entry Type' as 'S' 
	And the client provides 'entry text' as 'This is entry text' 
	
	
	#childObject name="incidentReferral",Referrals Card
	And the client provides an 'Incident referral' 
	And the client provides 'Submitted To Id' as ${bs.cucumber.submittedToId} 
	And the client provides 'Submitted Date' as '2018-10-02' 
	And the client provides 'submittedTime' as '12:20:31.735' 
	And the client provides 'referralStatus' as 'LIVE' 
	And the client provides 'detailText' as 'This is referral reason' 
	And the client provides 'referenceNumber' as '1001' 
	
	
	
	#childObject name="incidentClassification", Classifications Card
	And the client provides an 'Incident Classification' 
	And the client provides 'Investigation ClassificationUrn' as <Primary Offence> 
	And the client provides 'Classification Type' as 'PRIMARY' 
	And the client provides 'Investigation ClassificationUrn' as <Included Offence> 
	And the client provides 'Classification Type' as 'INCLUDED' 
	#childObject name="incidentAsbCategory", present on Classifications Card
	And the client provides an 'Incident Asb Category' 
	And the client provides 'Asb Category' as 'BALL GAMES' 
	And the client provides 'Asb Category' as 'TRESPASS' 
	#childObject name="incidentAsbKeyword", present on Classifications Card
	And the client provides an 'Incident Asb Keyword' 
	And the client provides 'Asb keyword' as 'MARINE' 
	And the client provides 'Asb keyword' as 'RURAL CRIME' 
	
	#childObject name="incidentMo", MO card
	And the client provides an 'Incident Mo' 
	And the client provides 'codeLevel1' as '1' 
	And the client provides 'codeLevel2' as '2' 
	And the client provides 'codeLevel3' as '3' 
	
	#initial MO is mandatory, present on Mo card
	And the client provides 'Initial Mo' as 'Knife and Fork' 
	And the cleint provides 'Detailed Mo' as 'This is detailed MO' 
	
	#commonChildObject name="tag", Tags card
	And the client provides a 'Tags' 
	And the client provides 'tagValueLevel1' as '42' 
	And the client provides 'tagValueLevel1' as 'V' 
	
	#childObject name="incidentOtherReference",present on Tags card
	And the client provides 'referenceType' as ${bs.cucumber.incident.referenceType} 
	And the client provides 'referenceNumber' as '987676667' 
	
	#staticObject name="propertyItem" ,property card
	And the client links a property with link reason 'Property' 
	And the client provides 'propertyCodeLevel1' as '1' 
	And the client provides 'propertyCodeLevel1' as '2' 
	And the client provides 'propertyCodeLevel1' as '3' 
	And the client provides 'manufacturer' as 'Apple' 
	And the client provides 'manufacturerText' as 'dummy' 
	
	And the client provides 'model' as 'dummy' 
	And the client provides 'propertyStatus' as 'dummy' 
	And the client provides 'colour1' as 'BLUE' 
	And the client provides 'colour2 ' as 'RED' 
	And the client provides 'quantity' as 'dummy' 
	And the client provides 'units' as 'dummy' 
	And the client provides 'antiqueCollectable' as 'dummy' 
	And the client provides 'modelVariant' as 'dummy' 
	And the client provides 'alarmFitted' as 'dummy' 
	####Will continue once property fields are updated in card definition sheet##
	
	#childObject name="incidentPoliceAttendance", present on officer/units card
	And the client provides 'incidentPoliceAttendance' 
	And the client provides 'officerId' as ${bs.cucumber.officerId} 
	And the client provides 'officerAttendingRole' as 'ATTENDED' 
	
	#childObject name="incidentDetectingUnit",present on officer/units card, present on officer/units card
	And the client provides 'incidentDetectingUnit' 
	And the client provides 'detectingUnit' as 'AMO' 
	
	#childObject name="incidentDetectingOfficer",present on officer/units card, present on officer/units card
	And the client provides 'incidentDetectingOfficer' 
	And the client provides 'detectingOicId' as '40920' 
	#User submits the Request to BS
	When the client submits the Investigation Record to Business Services 
	Then a new task 'QA New Investigation' is created for unit ${bs.cucumber.qaUnit} 
	And the Investigation Record will have its Status changed to 'REQUIRES QA' 
	And a new Task History Entry will be added to the Investigation Record 
	And the Task History Entry will record 'completed' as 'true' 
	And the Task History Entry will record 'qaUnit' as ${bs.cucumber.qaUnit} 
	And the Task History Entry will record 'imuUnit' as ${bs.cucumber.imuUnit} 
	And the Task History Entry will record 'linkingUnit' as ${bs.cucumber.linkingUnit} 
	And the Task History Entry will record 'forceId' as ${bs.cucumber.forceId}
	And the Task History Entry will record 'outOfForce' as 'false' 
	And 'investigationType' is set to <InvType> 
	
	Examples: 
		| Primary Offence | Included Offence | InvType |
		| 39.39.0.0       | AMO.NC.10.1      | CRIME    |
		| 40.40.0.0A      | AMO.NC.1.0       | CRIME    |
		| AMO.NC.2.20     | AMO.NC.2.23      | NONCRIME |
		| AMO.NC.2.22     | AMO.NC.2.20      | NONCRIME |
		| 42.42.0.0A      | 44.44.0.0        | CRIME    |
		
		
		Scenario: Create generic Investigation with only mandatory information 
			Given  client opted to perform action 'Create' on an 'Investigation Record' 
			#Basic details card
			And  the client provides 'Reported Date' as '2018-02-02' 
			And  the client provides 'Reported Time' as '09:20:31.735' 
			And  the client provides 'Incident On Or From Date' as '2018-02-01' 
			And  the client provides 'Incident At Or From Time' as '09:20:31.735' 
			
			And  the client provides 'How Reported' as '999' 
			And  the client provides 'officerReportingId' as ${bs.cucumber.officerReportingId} 
			And   the client provides 'Incident Summary' as 'This is Investigation Summary' 
			#txData
			And   the client provides 'completed' as 'true' 
			And   the client provides 'qaUnit' as ${bs.cucumber.qaUnit}
			And   the client provides 'imuUnit' as ${bs.cucumber.imuUnit} 
			And   the client provides 'linkingUnit' as ${bs.cucumber.linkingUnit} 
			And   the client provides 'forceId' as ${bs.cucumber.forceId} 
			
			#staticObject name="location",Incident Location Card
			#Force,district,sector code and beat code are mandatory fields
			And   the client provides 'Force' as '42' 
			And   the client provides 'Disctrict' as 'ESSEX NORTH' 
			And   the client provides 'Sector Code' as 'CHELMSFORD' 
			And   the client provides 'Beat Code' as 'CHELMSFORD E05004106' 
			
			#childObject name="incidentInitialClassification" 
			And   the client provides 'incidentInitialClassification' 
			And   the client provides 'incidentInitialClassification' as 'ARSON' 
			
			#Mo details
			And   the client provides 'Initial Mo' as 'Knife and Fork' 
			#User submits the Request to BS
			When   the client submits the Investigation Record to Business Services 
			Then   a new task 'QA New Investigation' is created for unit ${bs.cucumber.qaUnit}
			And   the Investigation Record will have its Status changed to 'REQUIRES QA' 
			And   a new Task History Entry will be added to the Investigation Record 
			And   the Task History Entry will record 'completed' as 'true' 
			And   the Task History Entry will record 'qaUnit' as ${bs.cucumber.qaUnit}
			And   the Task History Entry will record 'imuUnit' as ${bs.cucumber.imuUnit} 
			And   the Task History Entry will record 'linkingUnit' as ${bs.cucumber.linkingUnit} 
			And   the Task History Entry will record 'forceId' as ${bs.cucumber.forceId} 
			And   the Task History Entry will record 'outOfForce' as 'false' 
			
		Scenario: Create generic Investigation without supplying data for mandatory 
			fields. BS should give validation for mandatory fields. 
			
			Given   client opted to perform action 'Create' on an 'Investigation Record' 
			#txData
			And   the client provides 'completed' as 'true' 
			And   the client provides 'qaUnit' as ${bs.cucumber.qaUnit}
			And   the client provides 'imuUnit' as ${bs.cucumber.imuUnit} 
			And   the client provides 'linkingUnit' as ${bs.cucumber.linkingUnit} 
			And   the client provides 'forceId' as ${bs.cucumber.forceId} 
			#User submits the Request to BS
			When   the client submits the Investigation Record to Business Services 
			Then   Error will be returned for field 'Reported Date' with message 'reportedDate expected' 
			And   Error will be returned for field 'Reported Time' with message 'reportedTime expected' 
			And   Error will be returned for field 'Incident On Or From Date' with message 'incidentOnOrFromDate expected' 
			And   Error will be returned for field 'Incident At Or From Time' with message 'incidentAtOrFromTime expected' 
			And   Error will be returned for field 'How Reported' with message 'howReported expected' 
			And   Error will be returned for field 'officerReportingId' with message 'officerReportingId expected' 
			And   Error will be returned for field 'Incident Summary' with message 'incidentSummary expected' 
			And   Error will be returned for field 'Force' with message 'force expected' 
			And   Error will be returned for field 'Disctrict' with message 'disctrict expected' 
			And   Error will be returned for field 'Sector Code' with message 'sector Code expected' 
			And   Error will be returned for field 'Beat Code' with message 'beatCode expected' 
			And   Error will be returned for field 'Incident Classification' with message 'incidentClassification expected' 
			
		Scenario: Create generic Investigation without supplying mandatory 
			information 
			for 'Create generic Investigation' task and BS should give validation 
			
			
			Given   client opted to perform action 'Create' on an 'Investigation Record' 
			#Basic details card
			And   the client provides 'Reported Date' as '2018-02-02' 
			And   the client provides 'Reported Time' as '09:20:31.735' 
			And   the client provides 'Incident On Or From Date' as '2018-02-01' 
			And   the client provides 'Incident At Or From Time' as '09:20:31.735' 
			
			And   the client provides 'How Reported' as '999' 
			And   the client provides 'officerReportingId' as ${bs.cucumber.officerReportingId} 
			And   the client provides 'Incident Summary' as 'This is Investigation Summary' 
			#staticObject name="location",Incident Location Card
			And   the client provides 'Force' as '42' 
			And   the client provides 'Disctrict' as 'ESSEX NORTH' 
			And   the client provides 'Sector Code' as 'CHELMSFORD' 
			And   the client provides 'Beat Code' as 'CHELMSFORD E05004106' 
			
			#childObject name="incidentInitialClassification" 
			And   the client provides 'incidentInitialClassification' 
			And   the client provides 'incidentInitialClassification' as 'ARSON' 
			
			#Mo details
			And   the client provides 'Initial Mo' as 'Knife and Fork' 
			#User submits the Request to BS
			When   the client submits the Investigation Record to Business Services 
			Then   Error will be returned for field 'completed' with message 'completed expected' 
			And   Error will be returned for field 'qaUnit' with message 'qaUnit expected' 
			And   Error will be returned for field 'imuUnit' with message 'imuUnit expected' 
			And   Error will be returned for field 'linkingUnit' with message 'linkingUnit expected' 
			And   Error will be returned for field 'forceId' with message 'forceId expected' 
			
		Scenario: 
			Create generic Investigation with multiple Primary classification and BS should not allow this. 
		
			Given  client opted to perform action 'Create' on an 'Investigation Record' 
			#Basic details card
			And  the client provides 'Reported Date' as '2018-02-02' 
			And  the client provides 'Reported Time' as '09:20:31.735' 
			And  the client provides 'Incident On Or From Date' as '2018-02-01' 
			And  the client provides 'Incident At Or From Time' as '09:20:31.735' 
			
			And  the client provides 'How Reported' as '999' 
			And  the client provides 'officerReportingId' as ${bs.cucumber.officerReportingId} 
			And   the client provides 'Incident Summary' as 'This is Investigation Summary' 
			#txData
			And   the client provides 'completed' as 'true' 
			And   the client provides 'qaUnit' as ${bs.cucumber.qaUnit}
			And   the client provides 'imuUnit' as ${bs.cucumber.imuUnit} 
			And   the client provides 'linkingUnit' as ${bs.cucumber.linkingUnit} 
			And   the client provides 'forceId' as ${bs.cucumber.forceId} 
			
			#staticObject name="location",Incident Location Card
			#Force,district,sector code and beat code are mandatory fields
			And   the client provides 'Force' as '42' 
			And   the client provides 'Disctrict' as 'ESSEX NORTH' 
			And   the client provides 'Sector Code' as 'CHELMSFORD' 
			And   the client provides 'Beat Code' as 'CHELMSFORD E05004106' 
			#Mo details
			And   the client provides 'Initial Mo' as 'Knife and Fork' 
			
			And the client provides an 'Incident Classification' 
			And the client provides 'Investigation ClassificationUrn' as '39.39.0.0' 
			And the client provides 'Classification Type' as 'PRIMARY' 
			And the client provides 'Investigation ClassificationUrn' as '40.40.0.0A' 
			And the client provides 'Classification Type' as 'PRIMARY' 
			And the client provides 'Investigation ClassificationUrn' as '44.44.0.0' 
			And the client provides 'Classification Type' as 'INCLUDED' 
			#User submits the request
			When the client submits the Investigation Record to Business Services 
			Then Error will be returned for field 'Classification Type' with message 'One primary Incident classification is required.  Only one is allowed' 
			
			
		Scenario: 
			Create generic Investigation,supply Primary Classification as Non-Crime and Included Classification as Crime. BS should not allow this
		
			Given  client opted to perform action 'Create' on an 'Investigation Record' 
			#Basic details card
			And  the client provides 'Reported Date' as '2018-02-02' 
			And  the client provides 'Reported Time' as '09:20:31.735' 
			And  the client provides 'Incident On Or From Date' as '2018-02-01' 
			And  the client provides 'Incident At Or From Time' as '09:20:31.735' 
			
			And  the client provides 'How Reported' as '999' 
			And  the client provides 'officerReportingId' as ${bs.cucumber.officerReportingId} 
			And   the client provides 'Incident Summary' as 'This is Investigation Summary' 
			#txData
			And   the client provides 'completed' as 'true' 
			And   the client provides 'qaUnit' as ${bs.cucumber.qaUnit}
			And   the client provides 'imuUnit' as ${bs.cucumber.imuUnit} 
			And   the client provides 'linkingUnit' as ${bs.cucumber.linkingUnit} 
			And   the client provides 'forceId' as ${bs.cucumber.forceId} 
			
			#staticObject name="location",Incident Location Card
			#Force,district,sector code and beat code are mandatory fields
			And   the client provides 'Force' as '42' 
			And   the client provides 'Disctrict' as 'ESSEX NORTH' 
			And   the client provides 'Sector Code' as 'CHELMSFORD' 
			And   the client provides 'Beat Code' as 'CHELMSFORD E05004106' 
			#Mo details
			And   the client provides 'Initial Mo' as 'Knife and Fork' 
			
			And the client provides an 'Incident Classification' 
			And the client provides 'Investigation ClassificationUrn' as 'AMO.NC.2.22' 
			And the client provides 'Classification Type' as 'PRIMARY' 
			And the client provides 'Investigation ClassificationUrn' as '39.39.0.0' 
			And the client provides 'Classification Type' as 'INCLUDED' 
			#User submits the request
			When the client submits the Investigation Record to Business Services 
			Then Error will be returned for field 'Classification Type' with message 'The Investigation has a 'Non-Crime' type primary HO classification but a 'Crime' type included HO classification. This combination is not allowed' 
			
			
		Scenario: 
			Date fields of Basic details,Decision log,Enquiry log and Referral in Investigation record should not accept future date 
		
			Given  client opted to perform action 'Create' on an 'Investigation Record' 
			#Basic details card
			And  the client provides 'Reported Date' as '2019-02-02' 
			And  the client provides 'Reported Time' as '09:20:31.735' 
			And  the client provides 'Incident On Or From Date' as '2019-02-01' 
			And  the client provides 'Incident At Or From Time' as '09:20:31.735' 
			And the client provides 'Incident To Date' as '2019-02-01' 
			And the client provides 'Incident To Time' as '11:20:31.735' 
			
			And  the client provides 'How Reported' as '999' 
			And  the client provides 'officerReportingId' as ${bs.cucumber.officerReportingId} 
			And   the client provides 'Incident Summary' as 'This is Investigation Summary' 
			#txData
			And   the client provides 'completed' as 'true' 
			And   the client provides 'qaUnit' as ${bs.cucumber.qaUnit}
			And   the client provides 'imuUnit' as ${bs.cucumber.imuUnit} 
			And   the client provides 'linkingUnit' as ${bs.cucumber.linkingUnit} 
			And   the client provides 'forceId' as ${bs.cucumber.forceId} 
			
			#staticObject name="location",Incident Location Card
			#Force,district,sector code and beat code are mandatory fields
			And   the client provides 'Force' as '42' 
			And   the client provides 'Disctrict' as 'ESSEX NORTH' 
			And   the client provides 'Sector Code' as 'CHELMSFORD' 
			And   the client provides 'Beat Code' as 'CHELMSFORD E05004106' 
			
			#childObject name="incidentLogEntry", Enquiry log Card
			And the client provides a 'Incident Log Entry' 
			And the client provides 'Entry Index' as '1' 
			And the client provides 'Entry Date' as '2019-09-28' 
			And the client provides 'Entry Time' as '09:20:31.735' 
			And the client provides 'Entered By Officer Id' as '40920' 
			And the client provides 'Entry Type' as 'RES' 
			And the client provides 'Entry Text' as 'test for log entries' 
			
			#commonChildObject name="policyFile" ,Decision log card
			And the client provides a 'Policy File' 
			And the client provides 'Entry Date' as '2019-02-02' 
			And the client provides 'Entry time' as '09:20:31.735' 
			And the client provides 'enteredByOfficerId' as ${bs.cucumber.enteredByOfficerId} 
			And the client provides 'Entry Type' as 'S' 
			And the client provides 'entry text' as 'This is entry text' 
			
			
			#childObject name="incidentReferral",Referrals Card
			And the client provides an 'Incident referral' 
			And the client provides 'Submitted To Id' as ${bs.cucumber.submittedToId}
			And the client provides 'Submitted Date' as '2019-10-02' 
			And the client provides 'submittedTime' as '12:20:31.735' 
			And the client provides 'referralStatus' as 'LIVE' 
			And the client provides 'detailText' as 'This is referral reason' 
			And the client provides 'referenceNumber' as '1001' 
			
			
			#childObject name="incidentInitialClassification" 
			And   the client provides 'incidentInitialClassification' 
			And   the client provides 'incidentInitialClassification' as 'ARSON' 
			
			#Mo details
			And   the client provides 'Initial Mo' as 'Knife and Fork' 
			#User submits the Request to BS
			When   the client submits the Investigation Record to Business Services 
			Then Error will be returned for field 'Reported Date' with message 'Reported Date should not accept future date' 
			And  Error will be returned for field 'Incident On Or From Date' with message 'incidentOnOrFromDate should not accept future date' 
			And  Error will be returned for field 'Incident To Date' with message 'incidentToDate should not accept future date' 
			And  Error will be returned for field 'Entry Date' with message 'entryDate should not accept future date' 
			And  Error will be returned for field 'Entry Date' with message 'entryDate should not accept future date' 
			And  Error will be returned for field 'Submitted Date' with message 'submittedDate should not accept future date' 
			
		Scenario: 
			If Mandatory fields of 'Log of enquiries' card are not supplied, BS should give validation error 
		
			Given  client opted to perform action 'Create' on an 'Investigation Record' 
			#Basic details card
			And  the client provides 'Reported Date' as '2018-02-02' 
			And  the client provides 'Reported Time' as '09:20:31.735' 
			And  the client provides 'Incident On Or From Date' as '2018-02-01' 
			And  the client provides 'Incident At Or From Time' as '09:20:31.735' 
			And the client provides 'Incident To Date' as '2018-02-01' 
			And the client provides 'Incident To Time' as '11:20:31.735' 
			
			And  the client provides 'How Reported' as '999' 
			And  the client provides 'officerReportingId' as ${bs.cucumber.officerReportingId} 
			And   the client provides 'Incident Summary' as 'This is Investigation Summary' 
			#txData
			And   the client provides 'completed' as 'true' 
			And   the client provides 'qaUnit' as ${bs.cucumber.qaUnit}
			And   the client provides 'imuUnit' as ${bs.cucumber.imuUnit} 
			And   the client provides 'linkingUnit' as ${bs.cucumber.linkingUnit} 
			And   the client provides 'forceId' as ${bs.cucumber.forceId} 
			
			#staticObject name="location",Incident Location Card
			#Force,district,sector code and beat code are mandatory fields
			And   the client provides 'Force' as '42' 
			And   the client provides 'Disctrict' as 'ESSEX NORTH' 
			And   the client provides 'Sector Code' as 'CHELMSFORD' 
			And   the client provides 'Beat Code' as 'CHELMSFORD E05004106' 
			
			#childObject name="incidentInitialClassification" 
			And   the client provides 'incidentInitialClassification' 
			And   the client provides 'incidentInitialClassification' as 'ARSON' 
			
			#Mo details
			And   the client provides 'Initial Mo' as 'Knife and Fork' 
			
			#childObject name="incidentLogEntry", Enquiry log Card
			And the client provides a 'Incident Log Entry' 
			And the client provides 'Entry Index' as '1' 
			And the client provides 'Entry Date' as '2018-09-28' 
			And the client provides 'Entry Time' as '09:20:31.735' 
			And the client provides 'Entered By Officer Id' as '40920' 
			And the client provides 'Entry Type' as 'RES' 
			And the client provides 'Entry Text' as 'test for log entries' 
			
			And the client provides a 'Incident Log Entry' 
			And the client provides 'Entry Index' as '2' 
			And the client provides 'Entry Time' as '09:20:31.735' 
			And the client provides 'Entered By Officer Id' as '40920' 
			And the client provides 'Entry Type' as 'RES' 
			And the client provides 'Entry Text' as 'test for log entries' 
			
			#User submits the Request to BS
			When   the client submits the Investigation Record to Business Services 
			Then Error will be returned for field 'Entry Time' with message 'entryTime should be present' 
			And  Error will be returned for field 'Entered By Officer Id' with message 'enteredByOfficerId should be present' 
			And  Error will be returned for field 'Entry Type' with message 'entryType should be present' 
			And  Error will be returned for field 'Entry Text' with message 'entryText should be present' 
			And  Error will be returned for field 'Entry Date' with message 'entryDate should be present' 
			
		Scenario: 
			If Mandatory fields of 'Decision log'and 'Referrals' cards are not supplied, BS should give validation error 
		
			Given  client opted to perform action 'Create' on an 'Investigation Record' 
			#Basic details card
			And  the client provides 'Reported Date' as '2018-02-02' 
			And  the client provides 'Reported Time' as '09:20:31.735' 
			And  the client provides 'Incident On Or From Date' as '2018-02-01' 
			And  the client provides 'Incident At Or From Time' as '09:20:31.735' 
			And the client provides 'Incident To Date' as '2018-02-01' 
			And the client provides 'Incident To Time' as '11:20:31.735' 
			
			And  the client provides 'How Reported' as '999' 
			And  the client provides 'officerReportingId' as ${bs.cucumber.officerReportingId} 
			And   the client provides 'Incident Summary' as 'This is Investigation Summary' 
			#txData
			And   the client provides 'completed' as 'true' 
			And   the client provides 'qaUnit' as ${bs.cucumber.qaUnit}
			And   the client provides 'imuUnit' as ${bs.cucumber.imuUnit} 
			And   the client provides 'linkingUnit' as ${bs.cucumber.linkingUnit} 
			And   the client provides 'forceId' as ${bs.cucumber.forceId} 
			
			#staticObject name="location",Incident Location Card
			#Force,district,sector code and beat code are mandatory fields
			And   the client provides 'Force' as '42' 
			And   the client provides 'Disctrict' as 'ESSEX NORTH' 
			And   the client provides 'Sector Code' as 'CHELMSFORD' 
			And   the client provides 'Beat Code' as 'CHELMSFORD E05004106' 
			
			#childObject name="incidentInitialClassification" 
			And   the client provides 'incidentInitialClassification' 
			And   the client provides 'incidentInitialClassification' as 'ARSON' 
			
			#Mo details
			And   the client provides 'Initial Mo' as 'Knife and Fork' 
			
			#commonChildObject name="policyFile" ,Decision log card
			And the client provides a 'Policy File' 
			And the client provides 'Entry Type' as 'S' 
			And the client provides 'entry text' as 'This is entry text' 
			
			#childObject name="incidentReferral",Referrals Card
			And the client provides an 'Incident referral' 
			And the client provides 'Submitted To Id' as ${bs.cucumber.submittedToId} 
			
			And the client provides 'submittedTime' as '12:20:31.735' 
			And the client provides 'referralStatus' as 'LIVE' 
			And the client provides 'detailText' as 'This is referral reason' 
			And the client provides 'referenceNumber' as '1001' 
			#User submits the Request to BS
			When   the client submits the Investigation Record to Business Services 
			Then Error will be returned for field 'Entry Time' with message 'entryTime should be present' 
			And  Error will be returned for field 'Entered By Officer Id' with message 'enteredByOfficerId should be present' 
			And  Error will be returned for field 'Entry Date' with message 'entryDate should be present' 
			And Error will be returned for field 'Submitted Date' with message 'submitted Date should be present' 
		Scenario: 
			If 'Referral Time' is supplied but 'Referral Date' is not supplied,BS should give validation error
		
			Given  client opted to perform action 'Create' on an 'Investigation Record' 
			#Basic details card
			And  the client provides 'Reported Date' as '2018-02-02' 
			And  the client provides 'Reported Time' as '09:20:31.735' 
			And  the client provides 'Incident On Or From Date' as '2018-02-01' 
			And  the client provides 'Incident At Or From Time' as '09:20:31.735' 
			And the client provides 'Incident To Date' as '2018-02-01' 
			And the client provides 'Incident To Time' as '11:20:31.735' 
			
			And  the client provides 'How Reported' as '999' 
			And  the client provides 'officerReportingId' as ${bs.cucumber.officerReportingId} 
			And   the client provides 'Incident Summary' as 'This is Investigation Summary' 
			#txData
			And   the client provides 'completed' as 'true' 
			And   the client provides 'qaUnit' as ${bs.cucumber.qaUnit}
			And   the client provides 'imuUnit' as ${bs.cucumber.imuUnit} 
			And   the client provides 'linkingUnit' as ${bs.cucumber.linkingUnit} 
			And   the client provides 'forceId' as ${bs.cucumber.forceId} 
			
			#staticObject name="location",Incident Location Card
			#Force,district,sector code and beat code are mandatory fields
			And   the client provides 'Force' as '42' 
			And   the client provides 'Disctrict' as 'ESSEX NORTH' 
			And   the client provides 'Sector Code' as 'CHELMSFORD' 
			And   the client provides 'Beat Code' as 'CHELMSFORD E05004106' 
			
			#childObject name="incidentInitialClassification" 
			And   the client provides 'incidentInitialClassification' 
			And   the client provides 'incidentInitialClassification' as 'ARSON' 
			
			#Mo details
			And   the client provides 'Initial Mo' as 'Knife and Fork' 
			
			#childObject name="incidentReferral",Referrals Card
			And the client provides an 'Incident referral' 
			And the client provides 'Submitted To Id' as ${bs.cucumber.submittedToId} 
			And the client provides 'submittedTime' as '12:20:31.735' 
			And the client provides 'referralStatus' as 'LIVE' 
			And the client provides 'detailText' as 'This is referral reason' 
			And the client provides 'referenceNumber' as '1001' 
			#User submits the Request to BS
			When   the client submits the Investigation Record to Business Services 
			Then Error will be returned for field 'Submitted Date' with message 'submittedDate should be present' 
			
			
		Scenario: 
			BS should give validation error for conditional Mandatory fields on Officers/Units card 
			Given  client opted to perform action 'Create' on an 'Investigation Record' 
			#Basic details card
			And  the client provides 'Reported Date' as '2018-02-02' 
			And  the client provides 'Reported Time' as '09:20:31.735' 
			And  the client provides 'Incident On Or From Date' as '2018-02-01' 
			And  the client provides 'Incident At Or From Time' as '09:20:31.735' 
			And the client provides 'Incident To Date' as '2018-02-01' 
			And the client provides 'Incident To Time' as '11:20:31.735' 
			
			And  the client provides 'How Reported' as '999' 
			And  the client provides 'officerReportingId' as ${bs.cucumber.officerReportingId} 
			And   the client provides 'Incident Summary' as 'This is Investigation Summary' 
			#txData
			And   the client provides 'completed' as 'true' 
			And   the client provides 'qaUnit' as ${bs.cucumber.qaUnit}
			And   the client provides 'imuUnit' as ${bs.cucumber.imuUnit} 
			And   the client provides 'linkingUnit' as ${bs.cucumber.linkingUnit} 
			And   the client provides 'forceId' as ${bs.cucumber.forceId} 
			
			#staticObject name="location",Incident Location Card
			#Force,district,sector code and beat code are mandatory fields
			And   the client provides 'Force' as '42' 
			And   the client provides 'Disctrict' as 'ESSEX NORTH' 
			And   the client provides 'Sector Code' as 'CHELMSFORD' 
			And   the client provides 'Beat Code' as 'CHELMSFORD E05004106' 
			
			#childObject name="incidentInitialClassification" 
			And   the client provides 'incidentInitialClassification' 
			And   the client provides 'incidentInitialClassification' as 'ARSON' 
			
			#Mo details
			And   the client provides 'Initial Mo' as 'Knife and Fork' 
			
			#childObject name="incidentPoliceAttendance", present on officer/units card
			And the client provides 'incidentPoliceAttendance' 
			And the client provides 'officerId' as ${bs.cucumber.officerId} 
			And the client provides 'incidentPoliceAttendance' 
			And the client provides 'officerAttendingRole' as 'ATTENDED' 
			
			#c#childObject name="incidentOtherReference",present on Tags card
			And the client provides 'referenceNumber' as '987676667' 
			
			
			#User submits the Request to BS
			When   the client submits the Investigation Record to Business Services 
			Then Error will be returned for field 'officerAttendingRole' with message 'officerAttendingRole should be present' 
			And Error will be returned for field 'officerId' with message 'officerId should be present' 
			And Error will be returned for field 'referenceType' with message 'referenceType is mandatory if referenceNumber is supplied' 
			
			
		Scenario: 
			BS should give validation error for conditional Mandatory fields on Tags card 
			Given  client opted to perform action 'Create' on an 'Investigation Record' 
			#Basic details card
			And  the client provides 'Reported Date' as '2018-02-02' 
			And  the client provides 'Reported Time' as '09:20:31.735' 
			And  the client provides 'Incident On Or From Date' as '2018-02-01' 
			And  the client provides 'Incident At Or From Time' as '09:20:31.735' 
			And the client provides 'Incident To Date' as '2018-02-01' 
			And the client provides 'Incident To Time' as '11:20:31.735' 
			
			And  the client provides 'How Reported' as '999' 
			And  the client provides 'officerReportingId' as ${bs.cucumber.officerReportingId} 
			And   the client provides 'Incident Summary' as 'This is Investigation Summary' 
			#txData
			And   the client provides 'completed' as 'true' 
			And   the client provides 'qaUnit' as ${bs.cucumber.qaUnit}
			And   the client provides 'imuUnit' as ${bs.cucumber.imuUnit} 
			And   the client provides 'linkingUnit' as ${bs.cucumber.linkingUnit} 
			And   the client provides 'forceId' as ${bs.cucumber.forceId} 
			
			#staticObject name="location",Incident Location Card
			#Force,district,sector code and beat code are mandatory fields
			And   the client provides 'Force' as '42' 
			And   the client provides 'Disctrict' as 'ESSEX NORTH' 
			And   the client provides 'Sector Code' as 'CHELMSFORD' 
			And   the client provides 'Beat Code' as 'CHELMSFORD E05004106' 
			
			#childObject name="incidentInitialClassification" 
			And   the client provides 'incidentInitialClassification' 
			And   the client provides 'incidentInitialClassification' as 'ARSON' 
			
			#Mo details
			And   the client provides 'Initial Mo' as 'Knife and Fork' 
			
			#c#childObject name="incidentOtherReference",present on Tags card
			And the client provides 'referenceType' as ${bs.cucumber.incident.referenceType}
			And the client provides 'referenceNumber' as '987676667' 
			
			
			#User submits the Request to BS
			When   the client submits the Investigation Record to Business Services 
			Then Error will be returned for field 'referenceNumber' with message 'referenceNumber is mandatory if referenceType is supplied' 
			
			#ORGANISATION	
		Scenario: 
			Create a generic Investigation record with an Involved Organisation linked and Supply full details of Organisation 
			#Basic details card
			And  the client provides 'Reported Date' as '2018-02-02' 
			And  the client provides 'Reported Time' as '09:20:31.735' 
			And  the client provides 'Incident On Or From Date' as '2018-02-01' 
			And  the client provides 'Incident At Or From Time' as '09:20:31.735' 
			And the client provides 'Incident To Date' as '2018-02-01' 
			And the client provides 'Incident To Time' as '11:20:31.735' 
			
			And  the client provides 'How Reported' as '999' 
			And  the client provides 'officerReportingId' as ${bs.cucumber.officerReportingId} 
			And   the client provides 'Incident Summary' as 'This is Investigation Summary' 
			
			
			# details of staticObject name="organisation"
			And the client links a New 'Organisation' referrred to as 'Organisation1' with link reason of 'VICTIM' 
			And the client provides data on link 
			And the client provides 'name' as 'MET POlICE' 
			And the client provides 'branchDivision' as 'London' 
			And the client provides 'type' as '2' 
			And the client provides 'orgTypeLevel2' as '57' 
			And the client provides 'remarks' as 'This is remarks for Organisation' 
			#childObject name="organisationAlias"
			And the client provides 'organisationAlias' 
			And the client provides 'alias' as 'LONDON POLICE' 
			
			#staticObject name="location",This is address of Organisation
			And the client provides 'Premises Number' as '101' 
			And the client provides 'Postcode' as 'CM1 1GU' 
			And the client provides 'Premises Name' as 'Norwich Ind. Estate' 
			And the client provides 'Sub Premises Name' as 'abc' 
			And the client provides 'Sub Sub Premises Name' as 'def' 
			And the client provides 'streetName' as 'James Street' 
			And the client provides 'Locality' as 'Castle Donnington' 
			And the client provides 'town' as 'Chelmsford' 
			And the client provides 'County' as 'Essex' 
			And the client provides 'Country' as 'UNITED KINGDOM' 
			#Details of person to contact from organisation,childObject name="incidentPersonInfo"
			And the client provides 'incidentPersonInfo' 
			And the client provides 'organisationTitle' as 'MR' 
			And the client provides 'organisationSurname' as 'PATTINSON' 
			And the client provides 'organisationForename' as 'Robert' 
			And the client provides 'organisationPosition' 'Head of Police Force' 
			And the client provides 'organisationTelNumber' as '9933485968' 
			And the client provides 'organisationNotes' as 'This data is for Notes field' 
			#Victim code contract details
			And the client provides 'victimElectsToBeUpdated' as 'IN_BUSINESS' 
			And the client provides 'contactDirectly' as 'false' 
			And the client provides 'victimsPreferredContactMethod' as 'E-MAIL' 
			And the client provides 'contactNotes' as 'This is contact notes' 
			And the client provides 'timesToAvoid' as 'From 1800 Hours to 2400 Hours everyday' 
			And the client provides 'frequencyOfUpdates' as '3' 
			And the client provides 'oicAllocated' as 'true' 
			And the client provides 'oicChanged' as 'true' 
			And the client provides 'suspectArrested' as 'true' 
			And the client provides 'suspectCharged' as 'true' 
			And the client provides 'suspectBailed' as 'true' 
			And the client provides 'vpsOrBisOffered' as 'true' 
			And the client provides 'vpsOrBisTaken' as 'true' 
			And the client provides 'bisReadByInCourt' as 'BUSINESS_REP' 
			
			#Involved party updates details
			And the client provides 'incidentPersonInfoUpdate' 
			And the client provides 'officerId' as ${bs.cucumber.officerId}
			And the client provides 'updatedDateTime' as '2018-11-05 09:05:06:023' 
			And the client provides 'methodOfUpdate' as 'EMAIL' 
			And the client provides 'reasonForUpdate' as 'ARREST OF SUSPECT' 
			And the client provides 'remarks' as 'This is added for remarks field.' 
			
			#Association details for Involved Organisation
			And a link will be added to the 'Organisation1' for 'PERSON' referrred to as 'Association1' with link reason 'ALTERNATIVE CONTACT' 
			And the client provides 'title' as 'MR' 
			And the client provides 'surname' as 'FLEMING' 
			And the client provides 'forname1' as 'Jason' 
			And the client provides 'forname2' as 'Andy' 
			And the client provides 'forname3' as  'Marshla' 
			And the client provides 'gender' as 'MALE' 
			And the client provides 'dateOfBirth' as '1990-09-23' 
			And the client provides 'estimatedAge' as '28' 
			#Address of Association
			And a link will be added to the 'Association1' for 'LOCATION' referrred to as 'Location1' with link reason 'HOME ADDRESS' 
			And the client provides 'Premises Number' as '101' 
			And the client provides 'Postcode' as 'CM1 1NE' 
			And the client provides 'Premises Name' as 'Norwich Ind. Estate' 
			And the client provides 'Sub Premises Name' as 'abc' 
			And the client provides 'Sub Sub Premises Name' as 'def' 
			And the client provides 'streetName' as 'James Street' 
			And the client provides 'Locality' as 'Castle Donnington' 
			And the client provides 'town' as 'Chelmsford' 
			And the client provides 'County' as 'Essex' 
			And the client provides 'Country' as 'UNITED KINGDOM' 
			#Add Contact details of Association
			And a link will be added to the 'Association1' for 'COMMS' referrred to as 'Comms1' with link reason 'E-MAIL' 
			And the client provides 'commsType' as 'EMAIL' 
			And the client provides 'mainNumber' as 'abhi101291@yahoo.com' 
			
			#Add Preferred Contact for Association
			#childObject name="personPersonInfo"
			And the client provides 'personPersonInfo' 
			And the client provides 'isPreferredContact' as 'true' 
			
			
			
			#staticObject name="comms" table="COMMS", comms details of Organisation	
			And the client links from 'Organisation1' to a New 'Comms' referrred to as 'Comms1' with link reason of 'BUSINESS' 
			And the client provides data on 'Comms1' 
			And the client provides 'commsType' as 'E_MAIL BUSINESS' 
			And the client provides 'mainNumber' as 'hod@metpolice.com' 
			
			#txData
			And   the client provides 'completed' as 'true' 
			And   the client provides 'qaUnit' as ${bs.cucumber.qaUnit}
			And   the client provides 'imuUnit' as ${bs.cucumber.imuUnit} 
			And   the client provides 'linkingUnit' as ${bs.cucumber.linkingUnit} 
			And   the client provides 'forceId' as ${bs.cucumber.forceId} 
			
			
			
			#staticObject name="location",Incident Location Card
			#Force,district,sector code and beat code are mandatory fields
			And   the client provides 'Force' as '42' 
			And   the client provides 'Disctrict' as 'ESSEX NORTH' 
			And   the client provides 'Sector Code' as 'CHELMSFORD' 
			And   the client provides 'Beat Code' as 'CHELMSFORD E05004106' 
			
			#childObject name="incidentInitialClassification" 
			And   the client provides 'incidentInitialClassification' 
			And   the client provides 'incidentInitialClassification' as 'ARSON' 
			
			#Mo details
			And   the client provides 'Initial Mo' as 'Knife and Fork' 
			#User submits the Request to BS
			When   the client submits the Investigation Record to Business Services 
			Then   a new task 'QA New Investigation' is created for unit ${bs.cucumber.qaUnit}
			And   the Investigation Record will have its Status changed to 'REQUIRES QA' 
			And   a new Task History Entry will be added to the Investigation Record 
			And   the Task History Entry will record 'completed' as 'true' 
			And   the Task History Entry will record 'qaUnit' as ${bs.cucumber.qaUnit}
			And   the Task History Entry will record 'imuUnit' as ${bs.cucumber.imuUnit} 
			And   the Task History Entry will record 'linkingUnit' as ${bs.cucumber.linkingUnit} 
			And   the Task History Entry will record 'forceId' as ${bs.cucumber.forceId} 
			And   the Task History Entry will record 'outOfForce' as 'false' 
			
			
		Scenario: 
			BS should  give validation error if mandatory or conditional mandatory fields are not supplied for Involved Organisation in Investigation
			#Basic details card
			And  the client provides 'Reported Date' as '2018-02-02' 
			And  the client provides 'Reported Time' as '09:20:31.735' 
			And  the client provides 'Incident On Or From Date' as '2018-02-01' 
			And  the client provides 'Incident At Or From Time' as '09:20:31.735' 
			And the client provides 'Incident To Date' as '2018-02-01' 
			And the client provides 'Incident To Time' as '11:20:31.735' 
			
			And  the client provides 'How Reported' as '999' 
			And  the client provides 'officerReportingId' as ${bs.cucumber.officerReportingId} 
			And   the client provides 'Incident Summary' as 'This is Investigation Summary' 
			
			
			# details of staticObject name="organisation"
			And the client links a New 'Organisation' referrred to as 'Organisation1' with link reason of 'VICTIM' 
			And the client provides data on link 
			And the client provides 'name' as 'MET POlICE' 
			And the client provides 'branchDivision' as 'London' 
			And the client provides 'type' as '2' 
			And the client provides 'orgTypeLevel2' as '57' 
			And the client provides 'remarks' as 'This is remarks for Organisation' 
			#childObject name="organisationAlias"
			And the client provides 'organisationAlias' 
			And the client provides 'alias' as 'LONDON POLICE' 
			
			#staticObject name="location",This is address of Organisation
			And the client provides 'Premises Number' as '101' 
			And the client provides 'Postcode' as 'CM1 1GU' 
			And the client provides 'Premises Name' as 'Norwich Ind. Estate' 
			And the client provides 'Sub Premises Name' as 'abc' 
			And the client provides 'Sub Sub Premises Name' as 'def' 
			And the client provides 'streetName' as 'James Street' 
			And the client provides 'Locality' as 'Castle Donnington' 
			And the client provides 'town' as 'Chelmsford' 
			And the client provides 'County' as 'Essex' 
			And the client provides 'Country' as 'UNITED KINGDOM' 
			#Details of person to contact from organisation,childObject name="incidentPersonInfo"
			And the client provides 'incidentPersonInfo' 
			And the client provides 'organisationTitle' as 'MR' 
			And the client provides 'organisationSurname' as 'PATTINSON' 
			And the client provides 'organisationForename' as 'Robert' 
			And the client provides 'organisationPosition' 'Head of Police Force' 
			And the client provides 'organisationTelNumber' as '9933485968' 
			And the client provides 'organisationNotes' as 'This data is for Notes field' 
			#Victim code contract details
			#And the client do not provides 'victimElectsToBeUpdated' as 'IN_BUSINESS'
			
			And the client provides 'contactDirectly' as 'true' 
			And the client provides 'victimsPreferredContactMethod' as 'E-MAIL' 
			And the client provides 'contactNotes' as 'This is contact notes' 
			And the client provides 'timesToAvoid' as 'From 1800 Hours to 2400 Hours everyday' 
			#And the client do not provides 'frequencyOfUpdates' as '3'
			And the client provides 'oicAllocated' as 'true' 
			And the client provides 'oicChanged' as 'true' 
			And the client provides 'suspectArrested' as 'true' 
			And the client provides 'suspectCharged' as 'true' 
			And the client provides 'suspectBailed' as 'true' 
			And the client provides 'vpsOrBisOffered' as 'true' 
			And the client provides 'vpsOrBisTaken' as 'true' 
			And the client provides 'bisReadByInCourt' as 'BUSINESS_REP' 
			#Involved party updates details
			And the client provides 'incidentPersonInfoUpdate' 
			And the client provides 'officerId' as ${bs.cucumber.officerId}
			And the client provides 'updatedDateTime' as '2018-11-05 09:05:06:023' 
			#And the client do not provides 'methodOfUpdate' as 'EMAIL'
			#And the client do not provides 'reasonForUpdate' as 'ARREST OF SUSPECT'
			#And the client do not provides 'remarks' as 'This is added for remarks field.'
			
			And the client provides 'incidentPersonInfoUpdate' 
			#And the client do not provides 'officerId' as ${bs.cucumber.officerId}
			#And the client do not provides 'updatedDateTime' as '2018-11-05 09:05:06:023'
			And the client provides 'methodOfUpdate' as 'EMAIL' 
			And the client provides 'reasonForUpdate' as 'ARREST OF SUSPECT' 
			And the client provides 'remarks' as 'This is added for remarks field.' 
			
			
			
			#staticObject name="comms" table="COMMS", comms details of Organisation	
			And the client links from 'Organisation1' to a New 'Comms' referrred to as 'Comms1' with link reason of 'BUSINESS' 
			And the client provides data on 'Comms1' 
			And the client provides 'commsType' as 'E_MAIL BUSINESS' 
			And the client provides 'mainNumber' as 'hod@metpolice.com' 
			
			#txData
			And   the client provides 'completed' as 'true' 
			And   the client provides 'qaUnit' as ${bs.cucumber.qaUnit}
			And   the client provides 'imuUnit' as ${bs.cucumber.imuUnit} 
			And   the client provides 'linkingUnit' as ${bs.cucumber.linkingUnit} 
			And   the client provides 'forceId' as ${bs.cucumber.forceId} 
			
			#staticObject name="location",Incident Location Card
			#Force,district,sector code and beat code are mandatory fields
			And   the client provides 'Force' as '42' 
			And   the client provides 'Disctrict' as 'ESSEX NORTH' 
			And   the client provides 'Sector Code' as 'CHELMSFORD' 
			And   the client provides 'Beat Code' as 'CHELMSFORD E05004106' 
			
			#childObject name="incidentInitialClassification" 
			And   the client provides 'incidentInitialClassification' 
			And   the client provides 'incidentInitialClassification' as 'ARSON' 
			
			#Mo details
			And   the client provides 'Initial Mo' as 'Knife and Fork' 
			#User submits the Request to BS
			When   the client submits the Investigation Record to Business Services 
			Then Error will be returned for field 'victimElectsToBeUpdated' with message 'victimElectsToBeUpdated must be supplied' 
			And Error will be returned for field 'methodOfUpdate' with message 'methodOfUpdate must be supplied' 
			And Error will be returned for field 'reasonForUpdate' with message 'reasonForUpdate must be supplied' 
			And Error will be returned for field 'remarks' with message 'remarks must be supplied' 
			And Error will be returned for field 'officerId' with message 'officerId must be supplied' 
			And Error will be returned for field 'updatedDateTime' with message 'updatedDateTime must be supplied' 
			
			
		Scenario: 
			Fields related to Victim contract should be forbidden if Business Representative Opt out from receiving updates 
		
			#Basic details card
			And  the client provides 'Reported Date' as '2018-02-02' 
			And  the client provides 'Reported Time' as '09:20:31.735' 
			And  the client provides 'Incident On Or From Date' as '2018-02-01' 
			And  the client provides 'Incident At Or From Time' as '09:20:31.735' 
			And the client provides 'Incident To Date' as '2018-02-01' 
			And the client provides 'Incident To Time' as '11:20:31.735' 
			
			And  the client provides 'How Reported' as '999' 
			And  the client provides 'officerReportingId' as ${bs.cucumber.officerReportingId}
			And   the client provides 'Incident Summary' as 'This is Investigation Summary' 
			
			
			# details of staticObject name="organisation"
			And the client links a New 'Organisation' referrred to as 'Organisation1' with link reason of 'VICTIM' 
			And the client provides data on link 
			And the client provides 'name' as 'MET POlICE' 
			And the client provides 'branchDivision' as 'London' 
			And the client provides 'type' as '2' 
			And the client provides 'orgTypeLevel2' as '57' 
			And the client provides 'remarks' as 'This is remarks for Organisation' 
			#childObject name="organisationAlias"
			And the client provides 'organisationAlias' 
			And the client provides 'alias' as 'LONDON POLICE' 
			
			#staticObject name="location",This is address of Organisation
			And the client provides 'Premises Number' as '101' 
			And the client provides 'Postcode' as 'CM1 1GU' 
			And the client provides 'Premises Name' as 'Norwich Ind. Estate' 
			And the client provides 'Sub Premises Name' as 'abc' 
			And the client provides 'Sub Sub Premises Name' as 'def' 
			And the client provides 'streetName' as 'James Street' 
			And the client provides 'Locality' as 'Castle Donnington' 
			And the client provides 'town' as 'Chelmsford' 
			And the client provides 'County' as 'Essex' 
			And the client provides 'Country' as 'UNITED KINGDOM' 
			#Details of person to contact from organisation,childObject name="incidentPersonInfo"
			And the client provides 'incidentPersonInfo' 
			And the client provides 'organisationTitle' as 'MR' 
			And the client provides 'organisationSurname' as 'PATTINSON' 
			And the client provides 'organisationForename' as 'Robert' 
			And the client provides 'organisationPosition' 'Head of Police Force' 
			And the client provides 'organisationTelNumber' as '9933485968' 
			And the client provides 'organisationNotes' as 'This data is for Notes field' 
			#Victim code contract details
			And the client provides 'victimElectsToBeUpdated' as 'OUT_BUSINESS' 
			And the client provides 'contactDirectly' as 'true' 
			And the client provides 'victimsPreferredContactMethod' as 'E-MAIL' 
			And the client provides 'contactNotes' as 'This is contact notes' 
			And the client provides 'timesToAvoid' as 'From 1800 Hours to 2400 Hours everyday' 
			#And the client do not provides 'frequencyOfUpdates' as '3'
			And the client provides 'oicAllocated' as 'true' 
			And the client provides 'oicChanged' as 'true' 
			And the client provides 'suspectArrested' as 'true' 
			And the client provides 'suspectCharged' as 'true' 
			And the client provides 'suspectBailed' as 'true' 
			And the client provides 'vpsOrBisOffered' as 'true' 
			And the client provides 'vpsOrBisTaken' as 'true' 
			And the client provides 'bisReadByInCourt' as 'BUSINESS_REP' 
			
			#staticObject name="comms" table="COMMS", comms details of Organisation	
			And the client links from 'Organisation1' to a New 'Comms' referrred to as 'Comms1' with link reason of 'BUSINESS' 
			And the client provides data on 'Comms1' 
			And the client provides 'commsType' as 'E_MAIL BUSINESS' 
			And the client provides 'mainNumber' as 'hod@metpolice.com' 
			
			#txData
			And   the client provides 'completed' as 'true' 
			And   the client provides 'qaUnit' as ${bs.cucumber.qaUnit}
			And   the client provides 'imuUnit' as ${bs.cucumber.imuUnit}
			And   the client provides 'linkingUnit' as ${bs.cucumber.linkingUnit} 
			And   the client provides 'forceId' as ${bs.cucumber.forceId} 
			
			#staticObject name="location",Incident Location Card
			#Force,district,sector code and beat code are mandatory fields
			And   the client provides 'Force' as '42' 
			And   the client provides 'Disctrict' as 'ESSEX NORTH' 
			And   the client provides 'Sector Code' as 'CHELMSFORD' 
			And   the client provides 'Beat Code' as 'CHELMSFORD E05004106' 
			
			#childObject name="incidentInitialClassification" 
			And   the client provides 'incidentInitialClassification' 
			And   the client provides 'incidentInitialClassification' as 'ARSON' 
			
			#Mo details
			And   the client provides 'Initial Mo' as 'Knife and Fork' 
			#User submits the Request to BS
			When   the client submits the Investigation Record to Business Services 
			Then Error will be returned for field 'contactDirectly' with message 'contactDirectly must not be supplied' 
			And Error will be returned for field 'victimsPreferredContactMethod' with message 'victimsPreferredContactMethod must not be supplied' 
			And Error will be returned for field 'contactNotes' with message 'contactNotes must not be supplied' 
			And Error will be returned for field 'timesToAvoid' with message 'timesToAvoid must not be supplied' 
			And Error will be returned for field 'frequencyOfUpdates' with message 'frequencyOfUpdates must not be supplied' 
			And Error will be returned for field 'oicAllocated' with message 'oicAllocated must not be supplied' 
			And Error will be returned for field 'oicChanged' with message 'oicChanged must not be supplied' 
			And Error will be returned for field 'suspectArrested' with message 'suspectArrested must not be supplied' 
			And Error will be returned for field 'suspectCharged' with message 'suspectCharged must not be supplied' 
			And Error will be returned for field 'suspectBailed' with message 'suspectBailed must not be supplied' 
			
		Scenario: 
			'Frequency Of Updates' is mandatory if Business representative Opts In for receiving updates for Investigation 
		
			#Basic details card
			And  the client provides 'Reported Date' as '2018-02-02' 
			And  the client provides 'Reported Time' as '09:20:31.735' 
			And  the client provides 'Incident On Or From Date' as '2018-02-01' 
			And  the client provides 'Incident At Or From Time' as '09:20:31.735' 
			And the client provides 'Incident To Date' as '2018-02-01' 
			And the client provides 'Incident To Time' as '11:20:31.735' 
			
			And  the client provides 'How Reported' as '999' 
			And  the client provides 'officerReportingId' as ${bs.cucumber.officerReportingId}
			And   the client provides 'Incident Summary' as 'This is Investigation Summary' 
			
			
			# details of staticObject name="organisation"
			And the client links a New 'Organisation' referrred to as 'Organisation1' with link reason of 'VICTIM' 
			And the client provides data on link 
			And the client provides 'name' as 'MET POlICE' 
			And the client provides 'branchDivision' as 'London' 
			And the client provides 'type' as '2' 
			And the client provides 'orgTypeLevel2' as '57' 
			And the client provides 'remarks' as 'This is remarks for Organisation' 
			#childObject name="organisationAlias"
			And the client provides 'organisationAlias' 
			And the client provides 'alias' as 'LONDON POLICE' 
			
			#staticObject name="location",This is address of Organisation
			And the client provides 'Premises Number' as '101' 
			And the client provides 'Postcode' as 'CM1 1GU' 
			And the client provides 'Premises Name' as 'Norwich Ind. Estate' 
			And the client provides 'Sub Premises Name' as 'abc' 
			And the client provides 'Sub Sub Premises Name' as 'def' 
			And the client provides 'streetName' as 'James Street' 
			And the client provides 'Locality' as 'Castle Donnington' 
			And the client provides 'town' as 'Chelmsford' 
			And the client provides 'County' as 'Essex' 
			And the client provides 'Country' as 'UNITED KINGDOM' 
			#Details of person to contact from organisation,childObject name="incidentPersonInfo"
			And the client provides 'incidentPersonInfo' 
			And the client provides 'organisationTitle' as 'MR' 
			And the client provides 'organisationSurname' as 'HUDSON' 
			And the client provides 'organisationForename' as 'Andrew' 
			And the client provides 'organisationPosition' 'Head of Police Force' 
			And the client provides 'organisationTelNumber' as '9933485968' 
			And the client provides 'organisationNotes' as 'This data is for Notes field' 
			#Victim code contract details
			And the client provides 'victimElectsToBeUpdated' as 'IN_BUSINESS' 
			
			And the client provides 'contactDirectly' as 'true' 
			And the client provides 'victimsPreferredContactMethod' as 'E-MAIL' 
			And the client provides 'contactNotes' as 'This is contact notes' 
			And the client provides 'timesToAvoid' as 'From 1800 Hours to 2400 Hours everyday' 
			#And the client do not provides 'frequencyOfUpdates' as '3'
			And the client provides 'oicAllocated' as 'true' 
			And the client provides 'oicChanged' as 'true' 
			And the client provides 'suspectArrested' as 'true' 
			And the client provides 'suspectCharged' as 'true' 
			And the client provides 'suspectBailed' as 'true' 
			And the client provides 'vpsOrBisOffered' as 'true' 
			And the client provides 'vpsOrBisTaken' as 'true' 
			And the client provides 'bisReadByInCourt' as 'BUSINESS_REP' 
			#staticObject name="comms" table="COMMS", comms details of Organisation	
			And the client links from 'Organisation1' to a New 'Comms' referrred to as 'Comms1' with link reason of 'BUSINESS' 
			And the client provides data on 'Comms1' 
			And the client provides 'commsType' as 'E_MAIL BUSINESS' 
			And the client provides 'mainNumber' as 'hod@metpolice.com' 
			
			#txData
			And   the client provides 'completed' as 'true' 
			And   the client provides 'qaUnit' as ${bs.cucumber.qaUnit}
			And   the client provides 'imuUnit' as ${bs.cucumber.imuUnit}
			And   the client provides 'linkingUnit' as ${bs.cucumber.linkingUnit}
			And   the client provides 'forceId' as ${bs.cucumber.forceId} 
			
			#staticObject name="location",Incident Location Card
			#Force,district,sector code and beat code are mandatory fields
			And   the client provides 'Force' as '42' 
			And   the client provides 'Disctrict' as 'ESSEX NORTH' 
			And   the client provides 'Sector Code' as 'CHELMSFORD' 
			And   the client provides 'Beat Code' as 'CHELMSFORD E05004106' 
			
			#childObject name="incidentInitialClassification" 
			And   the client provides 'incidentInitialClassification' 
			And   the client provides 'incidentInitialClassification' as 'ARSON' 
			
			#Mo details
			And   the client provides 'Initial Mo' as 'Knife and Fork' 
			#User submits the Request to BS
			When   the client submits the Investigation Record to Business Services 
			Then Error will be returned for field 'frequencyOfUpdates' with message 'frequencyOfUpdates must be supplied' 
			
		Scenario: 
			Association must be supplied if Direct Contact is supplied as No for Involved Organisation
			#Basic details card
			And  the client provides 'Reported Date' as '2018-02-02' 
			And  the client provides 'Reported Time' as '09:20:31.735' 
			And  the client provides 'Incident On Or From Date' as '2018-02-01' 
			And  the client provides 'Incident At Or From Time' as '09:20:31.735' 
			And the client provides 'Incident To Date' as '2018-02-01' 
			And the client provides 'Incident To Time' as '11:20:31.735' 
			
			And  the client provides 'How Reported' as '999' 
			And  the client provides 'officerReportingId' as ${bs.cucumber.officerReportingId}
			And   the client provides 'Incident Summary' as 'This is Investigation Summary' 
			
			
			# details of staticObject name="organisation"
			And the client links a New 'Organisation' referrred to as 'Organisation1' with link reason of 'VICTIM' 
			And the client provides data on link 
			And the client provides 'name' as 'MET POlICE' 
			And the client provides 'branchDivision' as 'London' 
			And the client provides 'type' as '2' 
			And the client provides 'orgTypeLevel2' as '57' 
			And the client provides 'remarks' as 'This is remarks for Organisation' 
			#childObject name="organisationAlias"
			And the client provides 'organisationAlias' 
			And the client provides 'alias' as 'LONDON POLICE' 
			
			#staticObject name="location",This is address of Organisation
			And the client provides 'Premises Number' as '101' 
			And the client provides 'Postcode' as 'CM1 1GU' 
			And the client provides 'Premises Name' as 'Norwich Ind. Estate' 
			And the client provides 'Sub Premises Name' as 'abc' 
			And the client provides 'Sub Sub Premises Name' as 'def' 
			And the client provides 'streetName' as 'James Street' 
			And the client provides 'Locality' as 'Castle Donnington' 
			And the client provides 'town' as 'Chelmsford' 
			And the client provides 'County' as 'Essex' 
			And the client provides 'Country' as 'UNITED KINGDOM' 
			#Details of person to contact from organisation,childObject name="incidentPersonInfo"
			And the client provides 'incidentPersonInfo' 
			And the client provides 'organisationTitle' as 'MR' 
			And the client provides 'organisationSurname' as 'PATTINSON' 
			And the client provides 'organisationForename' as 'Robert' 
			And the client provides 'organisationPosition' 'Head of Police Force' 
			And the client provides 'organisationTelNumber' as '9933485968' 
			And the client provides 'organisationNotes' as 'This data is for Notes field' 
			#Victim code contract details
			And the client provides 'victimElectsToBeUpdated' as 'IN_BUSINESS' 
			And the client provides 'contactDirectly' as 'false' 
			And the client provides 'victimsPreferredContactMethod' as 'E-MAIL' 
			And the client provides 'contactNotes' as 'This is contact notes' 
			And the client provides 'timesToAvoid' as 'From 1800 Hours to 2400 Hours everyday' 
			And the client provides 'frequencyOfUpdates' as '3' 
			And the client provides 'oicAllocated' as 'true' 
			And the client provides 'oicChanged' as 'true' 
			And the client provides 'suspectArrested' as 'true' 
			And the client provides 'suspectCharged' as 'true' 
			And the client provides 'suspectBailed' as 'true' 
			And the client provides 'vpsOrBisOffered' as 'true' 
			And the client provides 'vpsOrBisTaken' as 'true' 
			And the client provides 'bisReadByInCourt' as 'BUSINESS_REP' 
			
			#Involved party updates details
			And the client provides 'incidentPersonInfoUpdate' 
			And the client provides 'officerId' as ${bs.cucumber.officerId}
			And the client provides 'updatedDateTime' as '2018-11-05 09:05:06:023' 
			And the client provides 'methodOfUpdate' as 'EMAIL' 
			And the client provides 'reasonForUpdate' as 'ARREST OF SUSPECT' 
			And the client provides 'remarks' as 'This is added for remarks field.' 
			
			
			
			#staticObject name="comms" table="COMMS", comms details of Organisation	
			And the client links from 'Organisation1' to a New 'Comms' referrred to as 'Comms1' with link reason of 'BUSINESS' 
			And the client provides data on 'Comms1' 
			And the client provides 'commsType' as 'E_MAIL BUSINESS' 
			And the client provides 'mainNumber' as 'hod@metpolice.com' 
			
			#txData
			And   the client provides 'completed' as 'true' 
			And   the client provides 'qaUnit' as ${bs.cucumber.qaUnit}
			And   the client provides 'imuUnit' as ${bs.cucumber.imuUnit}
			And   the client provides 'linkingUnit' as ${bs.cucumber.linkingUnit}
			And   the client provides 'forceId' as ${bs.cucumber.forceId}
			
			
			
			#staticObject name="location",Incident Location Card
			#Force,district,sector code and beat code are mandatory fields
			And   the client provides 'Force' as '42' 
			And   the client provides 'Disctrict' as 'ESSEX NORTH' 
			And   the client provides 'Sector Code' as 'CHELMSFORD' 
			And   the client provides 'Beat Code' as 'CHELMSFORD E05004106' 
			
			#childObject name="incidentInitialClassification" 
			And   the client provides 'incidentInitialClassification' 
			And   the client provides 'incidentInitialClassification' as 'ARSON' 
			
			#Mo details
			And   the client provides 'Initial Mo' as 'Knife and Fork' 
			#User submits the Request to BS
			When   the client submits the Investigation Record to Business Services 
			Then  Error message will be returned with message 'You must specify a preferred contact for Involved Organisation' 
		Scenario: 
			BS Gives validation error if Direct Contact is supplied as No for Involved Organisation and Preferred contact is supplied as No for Association of Involved Organisation 
			#Basic details card
			And  the client provides 'Reported Date' as '2018-02-02' 
			And  the client provides 'Reported Time' as '09:20:31.735' 
			And  the client provides 'Incident On Or From Date' as '2018-02-01' 
			And  the client provides 'Incident At Or From Time' as '09:20:31.735' 
			And the client provides 'Incident To Date' as '2018-02-01' 
			And the client provides 'Incident To Time' as '11:20:31.735' 
			
			And  the client provides 'How Reported' as '999' 
			And  the client provides 'officerReportingId' as ${bs.cucumber.officerReportingId}
			And   the client provides 'Incident Summary' as 'This is Investigation Summary' 
			
			
			# details of staticObject name="organisation"
			And the client links a New 'Organisation' referrred to as 'Organisation1' with link reason of 'VICTIM' 
			And the client provides data on link 
			And the client provides 'name' as 'MET POlICE' 
			And the client provides 'branchDivision' as 'London' 
			And the client provides 'type' as '2' 
			And the client provides 'orgTypeLevel2' as '57' 
			And the client provides 'remarks' as 'This is remarks for Organisation' 
			#childObject name="organisationAlias"
			And the client provides 'organisationAlias' 
			And the client provides 'alias' as 'LONDON POLICE' 
			
			#staticObject name="location",This is address of Organisation
			And the client provides 'Premises Number' as '101' 
			And the client provides 'Postcode' as 'CM1 1GU' 
			And the client provides 'Premises Name' as 'Norwich Ind. Estate' 
			And the client provides 'Sub Premises Name' as 'abc' 
			And the client provides 'Sub Sub Premises Name' as 'def' 
			And the client provides 'streetName' as 'James Street' 
			And the client provides 'Locality' as 'Castle Donnington' 
			And the client provides 'town' as 'Chelmsford' 
			And the client provides 'County' as 'Essex' 
			And the client provides 'Country' as 'UNITED KINGDOM' 
			#Details of person to contact from organisation,childObject name="incidentPersonInfo"
			And the client provides 'incidentPersonInfo' 
			And the client provides 'organisationTitle' as 'MR' 
			And the client provides 'organisationSurname' as 'PATTINSON' 
			And the client provides 'organisationForename' as 'Robert' 
			And the client provides 'organisationPosition' 'Head of Police Force' 
			And the client provides 'organisationTelNumber' as '9933485968' 
			And the client provides 'organisationNotes' as 'This data is for Notes field' 
			#Victim code contract details
			And the client provides 'victimElectsToBeUpdated' as 'IN_BUSINESS' 
			And the client provides 'contactDirectly' as 'false' 
			And the client provides 'victimsPreferredContactMethod' as 'E-MAIL' 
			And the client provides 'contactNotes' as 'This is contact notes' 
			And the client provides 'timesToAvoid' as 'From 1800 Hours to 2400 Hours everyday' 
			And the client provides 'frequencyOfUpdates' as '3' 
			And the client provides 'oicAllocated' as 'true' 
			And the client provides 'oicChanged' as 'true' 
			And the client provides 'suspectArrested' as 'true' 
			And the client provides 'suspectCharged' as 'true' 
			And the client provides 'suspectBailed' as 'true' 
			And the client provides 'vpsOrBisOffered' as 'true' 
			And the client provides 'vpsOrBisTaken' as 'true' 
			And the client provides 'bisReadByInCourt' as 'BUSINESS_REP' 
			
			#Involved party updates details
			And the client provides 'incidentPersonInfoUpdate' 
			And the client provides 'officerId' as ${bs.cucumber.officerId}
			And the client provides 'updatedDateTime' as '2018-11-05 09:05:06:023' 
			And the client provides 'methodOfUpdate' as 'EMAIL' 
			And the client provides 'reasonForUpdate' as 'ARREST OF SUSPECT' 
			And the client provides 'remarks' as 'This is added for remarks field.' 
			
			#Association details for Involved Organisation
			And a link will be added to the 'Organisation1' for 'PERSON' referrred to as 'Association1' with link reason 'ALTERNATIVE CONTACT' 
			And the client provides 'title' as 'MR' 
			And the client provides 'surname' as 'FLEMING' 
			And the client provides 'forname1' as 'Jason' 
			And the client provides 'forname2' as 'Andy' 
			And the client provides 'forname3' as  'Marshla' 
			And the client provides 'gender' as 'MALE' 
			And the client provides 'dateOfBirth' as '1990-09-23' 
			And the client provides 'estimatedAge' as '28' 
			#Address of Association
			And a link will be added to the 'Association1' for 'LOCATION' referrred to as 'Location1' with link reason 'HOME ADDRESS' 
			And the client provides 'Premises Number' as '101' 
			And the client provides 'Postcode' as 'CM1 1NE' 
			And the client provides 'Premises Name' as 'Norwich Ind. Estate' 
			And the client provides 'Sub Premises Name' as 'abc' 
			And the client provides 'Sub Sub Premises Name' as 'def' 
			And the client provides 'streetName' as 'James Street' 
			And the client provides 'Locality' as 'Castle Donnington' 
			And the client provides 'town' as 'Chelmsford' 
			And the client provides 'County' as 'Essex' 
			And the client provides 'Country' as 'UNITED KINGDOM' 
			#Add Contact details of Association
			And a link will be added to the 'Association1' for 'COMMS' referrred to as 'Comms1' with link reason 'E-MAIL' 
			And the client provides 'commsType' as 'EMAIL' 
			And the client provides 'mainNumber' as 'abhi101291@yahoo.com' 
			
			#Add Preferred Contact for Association
			#childObject name="personPersonInfo"
			And the client provides 'personPersonInfo' 
			And the client provides 'isPreferredContact' as 'false' 
			
			
			
			#staticObject name="comms" table="COMMS", comms details of Organisation	
			And the client links from 'Organisation1' to a New 'Comms' referrred to as 'Comms1' with link reason of 'BUSINESS' 
			And the client provides data on 'Comms1' 
			And the client provides 'commsType' as 'E_MAIL BUSINESS' 
			And the client provides 'mainNumber' as 'hod@metpolice.com' 
			
			#txData
			And   the client provides 'completed' as 'true' 
			And   the client provides 'qaUnit' as ${bs.cucumber.qaUnit}
			And   the client provides 'imuUnit' as ${bs.cucumber.imuUnit}
			And   the client provides 'linkingUnit' as ${bs.cucumber.linkingUnit}
			And   the client provides 'forceId' as ${bs.cucumber.forceId}
			
			
			
			#staticObject name="location",Incident Location Card
			#Force,district,sector code and beat code are mandatory fields
			And   the client provides 'Force' as '42' 
			And   the client provides 'Disctrict' as 'ESSEX NORTH' 
			And   the client provides 'Sector Code' as 'CHELMSFORD' 
			And   the client provides 'Beat Code' as 'CHELMSFORD E05004106' 
			
			#childObject name="incidentInitialClassification" 
			And   the client provides 'incidentInitialClassification' 
			And   the client provides 'incidentInitialClassification' as 'ARSON' 
			
			#Mo details
			And   the client provides 'Initial Mo' as 'Knife and Fork' 
			#User submits the Request to BS
			When   the client submits the Investigation Record to Business Services 
			Then  Error message will be returned with message 'You must specify a preferred contact for Involved Organisation' 
			
			
			
			
			
			
			
		