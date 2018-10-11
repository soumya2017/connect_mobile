Feature: CON-42865 Perform task Transfer Intelligence Report to check Intel transfer

  
  Scenario: Perform task Transfer Intelligence Report with Log of Enquiries and check the outcome
    #BS-INCLUDE feature=CON-42850_CreateIntelligenceReport.feature, scenario=Happy Path for Create Intelligence Report In Require Assessment status
    Given an Intelligence Report exists with status of 'REQUIRES ASSESSMENT'
    And the client runs the task 'Transfer Intelligence Report'
    # Adding Log of Entries Details
    And the client provides a Report Log Entry
    And the client provides 'Entry Index' as '2'
    And the client provides 'Entry Text' as 'Intel Logs'
    And the client provides 'Entry Date' as 'today'
    And the client provides 'Entry Time' as '09:20:31.735'
    And the client provides 'Entered By Officer Id' as ${bs.cucumber.employeeId}
    And the client provides 'Entry Type' as 'RES'
    # Here we are submitting Task fields
    And the client provides 'Confirm Transfer' as 'True'
    And the client provides 'Force' as '46'
    And the client provides 'Submit To Intelligence Unit' as '46 INTELLIGENCE BUREAU KENT'
    And the client provides 'Linking Unit' as ${bs.cucumber.linkingUnit}
    # Now we are checking the outcome
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to 'REQUIRES ASSESSMENT'
    And a task 'Assess Intelligence Report' is created for unit ${bs.cucumber.unit}
    And 'Intelligence Report' contains '1' child 'External References'
    And 'External References' contains 'Reference Type' as 'Previous URN'
    #Assuming External reference contains the report reference of existing report. It is dynamic so better to check manually by QA.
    #Task History detail assertions
    And a new Task History Entry will be added to the 'Intelligence Report'
    And the Task History Entry will record 'PIRAuthorSupervisorActorName' as ${bs.cucumber.unit}
    And the Task History Entry will record 'PIRManagerActorName' as '46 INTELLIGENCE BUREAU KENT'
    And the Task History Entry will record 'PIRIndexerActorName' as ${bs.cucumber.linkingUnit}
  
  Scenario: Perform task Transfer Intelligence Report to same unit and check the error message thrown
    #BS-INCLUDE feature=CON-42850_CreateIntelligenceReport.feature, scenario=Happy Path for Create Intelligence Report In Require Assessment status
    Given an Intelligence Report exists with status of 'REQUIRES ASSESSMENT'
    And the client runs the task 'Transfer Intelligence Report'
    # Adding Log of Entries Details
    And the client provides a Report Log Entry
    And the client provides 'Entry Index' as '2'
    And the client provides 'Entry Text' as 'Intel Logs'
    And the client provides 'Entry Date' as 'today'
    And the client provides 'Entry Time' as '09:20:31.735'
    And the client provides 'Entered By Officer Id' as ${bs.cucumber.employeeId}
    And the client provides 'Entry Type' as 'RES'
    # Here we are submitting Task fields
    And the client provides 'Confirm Transfer' as 'True'
    And the client provides 'Force' as ${bs.cucumber.forceId}
    And the client provides 'Submit To Intelligence Unit' as ${bs.cucumber.unit}
    And the client provides 'Linking Unit' as ${bs.cucumber.unit}
    # Now we are checking the outcome
    When the client submits the Intelligence Report to Business Services
    Then Error will be returned containing 'You cannot create an Intelligence report with the same force and information manager as the existing one'
	
  Scenario: Perform task Transfer Intelligence Report for invalid fields
    #BS-INCLUDE feature=CON-42850_CreateIntelligenceReport.feature, scenario=Happy Path for Create Intelligence Report In Require Assessment status
    Given an Intelligence Report exists with status of 'REQUIRES ASSESSMENT'
    And the client runs the task 'Transfer Intelligence Report'
    # Adding Log of Entries Details
    And the client provides a Report Log Entry
    And the client provides 'Entry Index' as '2'
    And the client provides 'Entry Text' as 'Intel Logs'
    And the client provides 'Entry Date' as 'today'
    And the client provides 'Entry Time' as '09:20:31.735'
    And the client provides 'Entered By Officer Id' as ${bs.cucumber.employeeId}
    And the client provides 'Entry Type' as 'RES'
    # Here we are submitting Task fields
    And the client provides 'Confirm Transfer' as 'True'
    And the client provides 'Force' as ${bs.cucumber.forceId}
    # Now we are checking the outcome
    When the client submits the Intelligence Report to Business Services
    Then Error will be returned for field 'submitToIntelligenceUnit' with message 'submitToIntelligenceUnit must have a value'
    Then Error will be returned for field 'linkingUnit' with message 'linkingUnit must have a value'
