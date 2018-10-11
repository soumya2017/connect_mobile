Feature: CON-42862 Perform task Re-assess report sensitivity


  Scenario: Perform task Re-assess report sensitivity when new action code selected by the user is present in the system parameter 'INTEL_SENSITIVE_ACTIONCODE' with current status of 'REQUIRES ASSESSMENT SENSITIVE'
    #BS-INCLUDE feature=CON-42850_CreateIntelligenceReport.feature, scenario=Happy Path for Create Intelligence Report In Require Assessment status
    Given an Intelligence Report exists with status of 'REQUIRES ASSESSMENT'
    And the client runs the task 'Reassess Report Sensitivity'
    #System Parameter check
    And system parameter INTEL_SENSITIVE_ACTIONCODE contains 'A2'
    # Here we are providing Transient data for the task performed
    And the client provides 'Unsanitised Action Code' as 'A2'
    And the client provides 'Reassess Sensitivity' as 'True'
    And the client provides 'Reason For Change' as 'Reassess the sensitivity'
    # Now the task will be performed
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to 'REQUIRES ASSESSMENT SENSITIVE'
    #Task History details assertions
    And a new Task History Entry will be added to the Intelligence Report

    #No Output Params to assert.
    # Below are workflow parameters used and passed in above scripts
   

  Scenario Outline: Perform task Re-assess report sensitivity when new action code selected by the user is not present in the system parameter 'INTEL_SENSITIVE_ACTIONCODE' with current status of 'REQUIRES ASSESSMENT'
    #BS-INCLUDE feature=CON-42850_CreateIntelligenceReport.feature, scenario=Happy Path for Create Intelligence Report In Require Assessment Sensitive status
    Given an Intelligence Report exists with status of <InStatus>
    And the client runs the task <CurrentTaskName>
    #System Parameter check
    And system parameter INTEL_SENSITIVE_ACTIONCODE contains <SysParamValue>
    # Here we are providing Transient data for the task performed
    And the client provides 'Unsanitised Action Code' as 'A1'
    And the client provides 'Reassess Sensitivity' as 'True'
    And the client provides 'Reason For Change' as <ReasonChange>
    # Now the task will be performed
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to <OutStatus>
    #Task History details assertions
    And a new Task History Entry will be added to the Intelligence Report

    #No Output Params to assert.
    # Below are workflow parameters used and passed in above scripts
    Examples: 
      | InStatus                      | CurrentTaskName             | SysParamValue | ReasonChange          | OutStatus            |
      | REQUIRES ASSESSMENT SENSITIVE | Reassess Report Sensitivity | A2            | Change in Sensitivity | REQUIRES ASSESSMENT  |

  
  Scenario Outline: Perform task Re-assess report sensitivity when new action code selected by the user is present in the system parameter 'INTEL_SENSITIVE_ACTIONCODE' with current status of 'REQUIRES LINKING'
    #BS-INCLUDE feature=CON-42859_AssessIntelligenceReport_Accepted.feature, scenario=Happy Path with status of 'REQUIRES ASSESSMENT'
    Given an Intelligence Report exists with status of <InStatus>
    And the client runs the task <CurrentTaskName>
    #System Parameter check
    And system parameter INTEL_SENSITIVE_ACTIONCODE contains <SysParamValue>
    # Here we are providing Transient data for the task performed
    And the client provides 'Unsanitised Action Code' as 'A2'
    And the client provides 'Reassess Sensitivity' as 'True'
    And the client provides 'Reason For Change' as <ReasonChange>
    # Now the task will be performed
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to <OutStatus>
    #Task History details assertions
    And a new Task History Entry will be added to the Intelligence Report

    #No Output Params to assert.
    # Below are workflow parameters used and passed in above scripts
    Examples: 
      | InStatus         | CurrentTaskName             | SysParamValue | ReasonChange          | OutStatus        					|
      | REQUIRES LINKING | Reassess Report Sensitivity | A2            | Change in Sensitivity | REQUIRES LINKING SENSITIVE |

  

    Scenario Outline: Perform task Re-assess report sensitivity when new action code selected by the user is unaltered and user performs the task.
    #BS-INCLUDE feature=CON-42850_CreateIntelligenceReport.feature, scenario=Happy Path for Create Intelligence Report In Require Assessment status
    Given an Intelligence Report exists with status of <InStatus>
    #And the client provides 'Unsanitised Action Code' as <CodeA3Usage>
    And the client runs the task <CurrentTaskName>
    #System Parameter check
    And system parameter INTEL_SENSITIVE_ACTIONCODE contains <SysParamValue>
    # Here we are providing Transient data for the task performed
    And the client provides 'Reassess Sensitivity' as 'True'
    And the client provides 'Reason For Change' as <ReasonChange>
    # Now the task will be performed
    When the client submits the Intelligence Report to Business Services
    Then Error will be returned containing 'A change in action code is required to be able to submit the task'

    # Below are workflow parameters used and passed in above scripts
    Examples: 
      | InStatus            | CurrentTaskName             | SysParamValue | ReasonChange          | CodeA3Usage |
      | REQUIRES ASSESSMENT | Reassess Report Sensitivity | A2            | Change in Sensitivity | A1          |

  
	@draft
  Scenario Outline: Perform task Re-assess report sensitivity when new action code selected by the user is unaltered and user performs the task.
    #BS-INCLUDE feature=CON-42860_LinkIntelligenceReport.feature, scenario=Happy Path status of 'REQUIRES LINKING SENSITIVE'
    Given an Intelligence Report exists with status of <InStatus>
    And the client provides Action code A3 usage instruction as <CodeA3Usage>
    And the client runs the task <CurrentTaskName>
    #System Parameter check
    And system parameter INTEL_SENSITIVE_ACTIONCODE contains <SysParamValue>
    # Here we are providing Transient data for the task performed
    And the client provides 'Reassess Sensitivity' as 'True'
    And the client provides 'Reason For Change' as <ReasonChange>
    # Now the task will be performed
    When the client submits the Intelligence Report to Business Services
    Then Error will be returned containing 'A change in action code is required to be able to submit the task'

    # Below are workflow parameters used and passed in above scripts
    Examples: 
      | InStatus       | CurrentTaskName             | SysParamValue | ReasonChange          | CodeA3Usage |
      | LIVE SENSITIVE | Reassess Report Sensitivity | A1            | Change in Sensitivity | A2          |
