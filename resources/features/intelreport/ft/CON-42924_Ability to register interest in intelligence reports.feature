Feature: CON-42924 Perform task Ability to register interest In Intelligence Reports
@draft
  Scenario Outline: Perform the task Register Interest In Intelligence Report for registering interest in Staff Member
  #BS-INCLUDE feature=CON-42850_CreateIntelligenceReport.feature, scenario=Happy Path for Create Intelligence Report In Require Assessment status
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
    And the client provides 'Register Staff Member Or Unit' as 'Staff Member'
    And the client provides 'Allocated To Staff Member Id' as <MemberId>
    And the client provides 'Reason For Interest' as <Reason for Interest>
    # Now we are checking the outcome
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to <outStatus>
	And a task <TaskName> exists against that 'Intelligence Report'
    #Task History detail assertions
    And the Task History Entry will record 'Register Staff Member Or Unit' as 'Staff Member'
    And the Task History Entry will record 'Allocated To Staff Member Id' as <MemberId>
    And the Task History Entry will record 'Reason For Interest' as <Reason for Interest>

    # Below are workflow parameters used and passed in above scripts
    Examples: 
      | instatus                      | outstatus                     | currentTaskName                          | EOffID                   | MemberId                 | Reason for Interest  | NewTaskName                     |
      | REQUIRES ASSESSMENT           | REQUIRES ASSESSMENT           | Register Interest In Intelligence Report | SUPT 42 ARM753 DESHPANDE | SUPT 42 ARM753 DESHPANDE | To register Interest | Interest In Intelligence Report |
      | REQUIRES ASSESSMENT SENSITIVE | REQUIRES ASSESSMENT SENSITIVE | Register Interest In Intelligence Report | SUPT 42 ARM753 DESHPANDE | SUPT 42 ARM753 DESHPANDE | To register Interest | Interest In Intelligence Report |
      #| REQUIRES LINKING              | REQUIRES LINKING              | Register Interest In Intelligence Report | SUPT 42 ARM753 DESHPANDE | SUPT 42 ARM753 DESHPANDE | To register Interest | Interest In Intelligence Report |
      #| REQUIRES LINKING SENSITIVE    | REQUIRES LINKING SENSITIVE    | Register Interest In Intelligence Report | SUPT 42 ARM753 DESHPANDE | SUPT 42 ARM753 DESHPANDE | To register Interest | Interest In Intelligence Report |
      #| LIVE                          | LIVE                          | Register Interest In Intelligence Report | SUPT 42 ARM753 DESHPANDE | SUPT 42 ARM753 DESHPANDE | To register Interest | Interest In Intelligence Report |
      #| LIVE SENSITIVE                | LIVE SENSITIVE                | Register Interest In Intelligence Report | SUPT 42 ARM753 DESHPANDE | SUPT 42 ARM753 DESHPANDE | To register Interest | Interest In Intelligence Report |

@draft
   Scenario Outline: Perform the task Register Interest In Intelligence Report for registering interest in Unit
  #BS-INCLUDE feature=CON-42850_CreateIntelligenceReport.feature, scenario=Happy Path for Create Intelligence Report In Require Assessment status
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
    And the client provides 'Reason For Interest' as <Reason for Interest>
    # Now we are checking the outcome
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to <outStatus>
    And a task <NewTaskName> is raised
    #Task History detail assertions
    And the Task History Entry will record 'Register Staff Member Or Unit' as 'Staff Member'
    And the Task History Entry will record 'Allocated To Staff Member Id' as <MemberId>
    And the Task History Entry will record 'Reason For Interest' as <Reason for Interest>

    # Below are workflow parameters used and passed in above scripts
    Examples: 
       | instatus                      | outstatus                     | currentTaskName                          | EOffID                   | Unit                      | Reason for Interest  | NewTaskName                     |
       | REQUIRES ASSESSMENT           | REQUIRES ASSESSMENT           | Register Interest In Intelligence Report | SUPT 42 ARM753 DESHPANDE | Intelligence Bureau Essex | To register Interest | Interest In Intelligence Report |
       | REQUIRES ASSESSMENT SENSITIVE | REQUIRES ASSESSMENT SENSITIVE | Register Interest In Intelligence Report | SUPT 42 ARM753 DESHPANDE | Intelligence Bureau Essex | To register Interest | Interest In Intelligence Report |
      #| REQUIRES LINKING              | REQUIRES LINKING              | Register Interest In Intelligence Report | SUPT 42 ARM753 DESHPANDE | Intelligence Bureau Essex | To register Interest | Interest In Intelligence Report |
      #| REQUIRES LINKING SENSITIVE    | REQUIRES LINKING SENSITIVE    | Register Interest In Intelligence Report | SUPT 42 ARM753 DESHPANDE | Intelligence Bureau Essex | To register Interest | Interest In Intelligence Report |
      #| LIVE                          | LIVE                          | Register Interest In Intelligence Report | SUPT 42 ARM753 DESHPANDE | Intelligence Bureau Essex | To register Interest | Interest In Intelligence Report |
      #| LIVE SENSITIVE                | LIVE SENSITIVE                | Register Interest In Intelligence Report | SUPT 42 ARM753 DESHPANDE | Intelligence Bureau Essex | To register Interest | Interest In Intelligence Report |

 @draft
    Scenario Outline: Perform the task Register Interest In Intelligence Report for Reason for Interest field greater than 2000 characters
   #BS-INCLUDE feature=CON-42850_CreateIntelligenceReport.feature, scenario=Happy Path for Create Intelligence Report In Require Assessment status
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
    And the client provides 'Reason For Interest' as <Reason for Interest>
    # Now we are checking the outcome
    When the client submits the Intelligence Report to Business Services
    Then Error will be returned for field 'Reason for Interest' with message 'Reason for Interest cannot be greater than 2000 characters'

    # Below are workflow parameters used and passed in above scripts
    Examples: 
      | instatus            | currentTaskName                          | EOffID                   | Unit                      | Reason for Interest                                                                                                                                                                                                                                                                                                                                   |
      | REQUIRES ASSESSMENT | Register Interest In Intelligence Report | SUPT 42 ARM753 DESHPANDE | Intelligence Bureau Essex | To register Interest To register InterestTo register InterestTo register InterestTo register InterestTo register InterestTo register InterestTo register InterestTo register InterestTo register InterestTo register InterestTo register InterestTo register InterestTo register InterestTo register InterestTo register InterestTo register Interest |
