Feature: CON-42868 Perform task MoPI Grouping Amendment to amend Mopi Group

  Scenario Outline: Perform task MoPI Grouping Amendment to change Mopi Group
    #BS-INCLUDE feature=CON-42859_AssessIntelligenceReport_Accepted.feature, scenario=Happy Path with status of 'REQUIRES ASSESSMENT'
    Given an Intelligence Report exists with status of <Instatus>
    And the client runs the task <CurrentTaskName>
    # Adding Log of Entries Details
    And the client provides a Report Log Entry
    And the client provides 'Entry Index' as '2'
    And the client provides 'Entry Text' as 'Intel Logs'
    And the client provides 'Entry Date' as 'today'
    And the client provides 'Entry Time' as '09:20:31.735'
    And the client provides 'Entered By Officer Id' as ${bs.cucumber.employeeId}
    And the client provides 'Entry Type' as 'RES'
    # Here we are submitting Task fields
    And the client provides 'Mopi Group' as <Group>
    And the client provides 'Reason For Change' as <ReasonChange>
    # Now we are checking the outcome
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to <Outstatus>    
    And the task will be performed
    #Task History detail assertions
    #No Output parameters so nothing to assert from task history.
    #And the Task History Entry will record 'Mopi Group' as <Group>
    #And the Task History Entry will record 'Reason For Change' as <ReasonChange>
    And Intelligence Report contains 'Mopi Group' as <Group>

    # Below are workflow parameters used and passed in above scripts
    Examples: 
      | Instatus         | Outstatus        | CurrentTaskName      		| Group   | ReasonChange          |
      | REQUIRES LINKING | REQUIRES LINKING | MoPI Grouping Amendment | 2 			| Add reason for change |
		 #| REQUIRES LINKING SENSITIVE | REQUIRES LINKING SENSITIVE | MoPI Grouping Amendment | Group 3 | Add reason for change |
		 #| LIVE                       | LIVE                       | MoPI Grouping Amendment | Group 1 | Add reason for change |
		 #| LIVE SENSITIVE             | LIVE SENSITIVE             | MoPI Grouping Amendment | Group 4 | Add reason for change |
	
	# Scenario needs to be tested manually. Can not test via gherkins as Mopi Group takes 20mins to set via Mopi Processor 
  #Scenario Outline: Perform task MoPI Grouping Amendment to change Mopi Group for same Mopi Group earlier
    #BS-INCLUDE feature=CON-42859_AssessIntelligenceReport_Accepted.feature, scenario=Happy Path with status of 'REQUIRES ASSESSMENT'
    #Given an Intelligence Report exists with status of <Instatus>
    #And the client runs the task <CurrentTaskName>
    # Here we are submitting Task fields
    #And the client provides 'Mopi Group' as <Group>
    #And the client provides 'Reason For Change' as <ReasonChange>
    # Now we are checking the outcome
    #When the client submits the Intelligence Report to Business Services
    #Then Error will be returned containing 'Please ensure you select a MoPI value other than the one currently assigned to this report'
#
    # Below are workflow parameters used and passed in above scripts
    #Examples: 
      #| Instatus         | CurrentTaskName      	 | Group   | ReasonChange |
      #| REQUIRES LINKING | MoPI Grouping Amendment | 3 			 | Text Change  |

	
  Scenario Outline: Perform task MoPI Grouping Amendment for blank Mopi Group and Reason for Change
    #BS-INCLUDE feature=CON-42859_AssessIntelligenceReport_Accepted.feature, scenario=Happy Path with status of 'REQUIRES ASSESSMENT'
    Given an Intelligence Report exists with status of <Instatus>
    And the client runs the task <CurrentTaskName>
    # Adding Log of Entries Details
    And the client provides a Report Log Entry
    And the client provides 'Entry Index' as '2'
    And the client provides 'Entry Text' as 'Intel Logs'
    And the client provides 'Entry Date' as 'today'
    And the client provides 'Entry Time' as '09:20:31.735'
    And the client provides 'Entered By Officer Id' as ${bs.cucumber.employeeId}
    And the client provides 'Entry Type' as 'RES'
    # Here we are submitting Task fields
    And the client provides 'Mopi Group' as <Group>
    And the client provides 'Reason For Change' as <ReasonChange>
    # Now we are checking the outcome
    When the client submits the Intelligence Report to Business Services
    Then Error will be returned for field 'Mopi Group' with message 'mopiGroup must have a value'
    Then Error will be returned for field 'Reason For Change' with message 'reasonForChange must have a value'

    # Below are workflow parameters used and passed in above scripts
    Examples: 
      | Instatus         | Outstatus        | CurrentTaskName      		| Group | ReasonChange |
      | REQUIRES LINKING | REQUIRES LINKING | MoPI Grouping Amendment |       |              |
     #| REQUIRES LINKING SENSITIVE | REQUIRES LINKING SENSITIVE | MoPI Grouping Amendment |       |              | 
     #| LIVE                       | LIVE                       | MoPI Grouping Amendment |       |              | 
     #| LIVE SENSITIVE             | LIVE SENSITIVE             | MoPI Grouping Amendment |       |              | 
