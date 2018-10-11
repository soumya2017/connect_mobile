Feature: CON-42917 Perform task Remove Interest In Intelligence Report

@draft
  Scenario Outline: Perform the task Remove Interest In Intelligence Report to remove interest for Unit
  #BS-INCLUDE feature=CON-42859_AssessIntelligenceReport_Accepted.feature, scenario=Happy Path with status of 'REQUIRES ASSESSMENT'
    Given an Intelligence Report exists with status of <inStatus>
    And the client runs the task <currentTaskName>
    # Adding Log of Entries Details
    And the client provides a Report Log Entry
    And the client provides 'Entry Index' as '2'
    And the client provides 'Entry Text' as 'Intel Logs'
    And the client provides 'Entry Date' as '2018-08-14'
    And the client provides 'Entry Time' as '09:20:31.735'
    And the client provides 'Entered By Officer Id' as <EOffID>
    And the client provides 'Entry Type' as 'RESEARCH NOTES'
    # Here we are submitting Task fields
    And the client provides 'Register Staff Member Or Unit' as 'Unit'
    And the client provides 'Allocated To Unit' as <Unit>
    # Now we are checking the outcome
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to <outStatus>
	  And a task 'Interest In Intelligence Report' does not exist against that 'Intelligence Report'
    #Task History detail assertions
    And the Task History Entry will record 'Register Staff Member Or Unit' as 'Staff Member'
    And the Task History Entry will record 'Allocated To Unit' as <Unit>

    # Below are workflow parameters used and passed in above scripts
    Examples: 
      | instatus                   | outstatus                  | currentTaskName                        | EOffID                   | Unit                      | 
      | REQUIRES LINKING           | REQUIRES LINKING           | Remove Interest In Intelligence Report | SUPT 42 ARM753 DESHPANDE | Intelligence Bureau Essex | 
      | REQUIRES LINKING SENSITIVE | REQUIRES LINKING SENSITIVE | Remove Interest In Intelligence Report | SUPT 42 ARM753 DESHPANDE | Intelligence Bureau Essex | 
      #| LIVE                       | LIVE                       | Remove Interest In Intelligence Report | SUPT 42 ARM753 DESHPANDE | Intelligence Bureau Essex | 
      #| LIVE SENSITIVE             | LIVE SENSITIVE             | Remove Interest In Intelligence Report | SUPT 42 ARM753 DESHPANDE | Intelligence Bureau Essex | 


@draft
  Scenario Outline: Perform the task Remove Interest In Intelligence Report for Staff Member
  #BS-INCLUDE feature=CON-42859_AssessIntelligenceReport_Accepted.feature, scenario=Happy Path with status of 'REQUIRES ASSESSMENT'
    Given an Intelligence Report exists with status of <inStatus>
    And the client runs the task <currentTaskName>
    # Here we are submitting Task fields
    And the client provides 'Register Staff Member Or Unit' as 'Staff Member'
    And the client provides 'Allocated To Staff Member Id' as <MemberId>
    # Now we are checking the outcome
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to <outStatus>
    And a task 'Interest In Intelligence Report' does not exist against that 'Intelligence Report'
    #Task History detail assertions
    And the Task History Entry will record 'Register Staff Member Or Unit' as 'Staff Member'
    And the Task History Entry will record 'Allocated To Staff Member Id' as <MemberId>

    # Below are workflow parameters used and passed in above scripts
    Examples: 
      | instatus                   | outstatus                  | currentTaskName                        | MemberId                 | 
      | REQUIRES LINKING           | REQUIRES LINKING           | Remove Interest In Intelligence Report | SUPT 42 ARM753 DESHPANDE |
      | REQUIRES LINKING SENSITIVE | REQUIRES LINKING SENSITIVE | Remove Interest In Intelligence Report | SUPT 42 ARM753 DESHPANDE | 
      | LIVE                       | LIVE                       | Remove Interest In Intelligence Report | SUPT 42 ARM753 DESHPANDE | 
      | LIVE SENSITIVE             | LIVE SENSITIVE             | Remove Interest In Intelligence Report | SUPT 42 ARM753 DESHPANDE | 

  
 @draft
  Scenario Outline: Perform the task Remove Interest In Intelligence Report not passing Unit
  #BS-INCLUDE feature=CON-42859_AssessIntelligenceReport_Accepted.feature, scenario=Happy Path with status of 'REQUIRES ASSESSMENT'
    Given an Intelligence Report exists with status of <inStatus>
    And the client runs the task <currentTaskName>
    # Here we are submitting Task fields
    And the client provides 'Register Staff Member Or Unit' as 'Unit'
    And the client provides 'Allocated To Unit' as <Unit>
    # Now we are checking the outcome
    When the client submits the Intelligence Report to Business Services
    Then Error will be returned for field 'Allocated To Unit' with message 'Allocated To Unit should not be blank'

    # Below are workflow parameters used and passed in above scripts
    Examples: 
      | instatus                   | currentTaskName                        | Unit |
      | REQUIRES LINKING           | Remove Interest In Intelligence Report |      |
      | REQUIRES LINKING SENSITIVE | Remove Interest In Intelligence Report |      |
      #| LIVE                       | Remove Interest In Intelligence Report |      |
      #| LIVE SENSITIVE             | Remove Interest In Intelligence Report |      |

 
  @draft
  Scenario Outline: Perform the task Remove Interest In Intelligence Report not passing Staff Member
  #BS-INCLUDE feature=CON-42859_AssessIntelligenceReport_Accepted.feature, scenario=Happy Path with status of 'REQUIRES ASSESSMENT'
    Given an Intelligence Report exists with status of <inStatus>
    And the client runs the task <currentTaskName>
    # Here we are submitting Task fields
    And the client provides 'Register Staff Member Or Unit' as 'Staff Member'
    And the client provides 'Allocated To Staff Member Id' as <MemberId>
    # Now we are checking the outcome
    When the client submits the Intelligence Report to Business Services
    Then Error will be returned for field 'Allocated To Staff Member Id' with message 'Allocated To Staff Member Id should not be blank'

    # Below are workflow parameters used and passed in above scripts
    Examples: 
      | instatus                   | currentTaskName                        | MemberId |
      | REQUIRES LINKING           | Remove Interest In Intelligence Report |          |
      | REQUIRES LINKING SENSITIVE | Remove Interest In Intelligence Report |          |
      #| LIVE                       | Remove Interest In Intelligence Report |          |
      #| LIVE SENSITIVE             | Remove Interest In Intelligence Report |          |

 

@draft
  Scenario Outline: Perform the task Remove Interest In Intelligence Report for Intel for which Interest is not registered
  #BS-INCLUDE feature=CON-42859_AssessIntelligenceReport_Accepted.feature, scenario=Happy Path with status of 'REQUIRES ASSESSMENT'
    Given an Intelligence Report exists with status of <inStatus>
    And the client runs the task <currentTaskName>
    # Adding Log of Entries Details
    And the client provides a Report Log Entry
    And the client provides 'Entry Index' as '2'
    And the client provides 'Entry Text' as 'Intel Logs'
    And the client provides 'Entry Date' as '2018-08-14'
    And the client provides 'Entry Time' as '09:20:31.735'
    And the client provides 'Entered By Officer Id' as <EOffID>
    And the client provides 'Entry Type' as 'RESEARCH NOTES'
    # Here we are submitting Task fields
    And the client provides 'Register Staff Member Or Unit' as 'Unit'
    And the client provides 'Allocated To Unit' as <Unit>
    # Now we are checking the outcome
    When the client submits the Intelligence Report to Business Services
    Then Error will be returned containing 'Task cannot be performed as there is no interest registered'

    # Below are workflow parameters used and passed in above scripts
    Examples: 
      | instatus                   | currentTaskName                        | EOffID                   | Unit                      |
      | REQUIRES LINKING           | Remove Interest In Intelligence Report | SUPT 42 ARM753 DESHPANDE | Intelligence Bureau Essex |
      | REQUIRES LINKING SENSITIVE | Remove Interest In Intelligence Report | SUPT 42 ARM753 DESHPANDE | Intelligence Bureau Essex |
     # | LIVE                       | Remove Interest In Intelligence Report | SUPT 42 ARM753 DESHPANDE | Intelligence Bureau Essex |
     # | LIVE SENSITIVE             | Remove Interest In Intelligence Report | SUPT 42 ARM753 DESHPANDE | Intelligence Bureau Essex |
