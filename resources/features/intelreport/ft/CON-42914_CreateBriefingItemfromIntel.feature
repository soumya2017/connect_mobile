Feature: CON-42914 Perform task Request to create Briefing from Intelligence Report task 

 Scenario: Perform task Request to create Briefing from Intelligence Report and next task raised.
  #BS-INCLUDE feature=CON-42850_CreateIntelligenceReport.feature, scenario=Happy Path for Create Intelligence Report In Require Assessment status
    Given an Intelligence Report exists with status of 'REQUIRES ASSESSMENT'
    And the client runs the task 'Request To Create Briefing Item'
    #System Parameter is set
    #And system parameter INTEL_SENSITIVE_ACTIONCODE contains 'A2'
    # Adding Log of Entries Details
    And the client provides a Report Log Entry
    And the client provides 'Entry Index' as '2'
    And the client provides 'Entry Text' as 'Intel Logs'
    And the client provides 'Entry Date' as 'today'
    And the client provides 'Entry Time' as '09:20:31.735'
    And the client provides 'Entered By Officer Id' as ${bs.cucumber.employeeId}
    And the client provides 'Entry Type' as 'PNC'
    And the client provides 'Send Request To Unit' as '42 INTELLIGENCE BUREAU ESSEX'
    And the client provides 'Remarks' as 'Create Briefing'
    And the client provides 'Employee Iteration Id' as ${bs.cucumber.employeeId}
    # Here we are providing Transient data for the task performed
    # Now the task will be performed
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to 'REQUIRES ASSESSMENT'
    And a new task 'Process Request To Create Briefing' is created for unit ${bs.cucumber.unit}
    #Task History detail assertions
	And a new Task History Entry will be added to the Intelligence Report
    And the Task History Entry will record 'Briefing Data' as 'Create Briefing'
    And the Task History Entry will record 'Briefing Unit Actor Name' as '42 INTELLIGENCE BUREAU ESSEX'
    # Below are workflow parameters used and passed in above scripts

  Scenario Outline: Perform task Request to create Briefing from Intelligence Report with Invalid fields
  #BS-INCLUDE feature=CON-42850_CreateIntelligenceReport.feature, scenario=Happy Path for Create Intelligence Report In Require Assessment status
    Given an Intelligence Report exists with status of <inStatus>
    And the client runs the task <currentTaskName>
    # Here we are providing Transient data for the task performed
    And the client provides 'Remarks' as <reasonText>
    # Now the task will be performed
    When the client submits the Intelligence Report to Business Services
    Then Error will be returned containing 'Unit to send request to was not supplied'
    # Below are workflow parameters used and passed in above scripts
    
    Examples: 
      | inStatus			| reasonText            | outstatus           | currentTaskName                 | 
      | REQUIRES ASSESSMENT | Create Briefing check | REQUIRES ASSESSMENT | Request To Create Briefing Item | 
