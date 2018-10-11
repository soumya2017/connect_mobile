@Generic
Feature: Perform task 'Supervisor Process Request To Amend Investigation Classification'

  ## It triggers two task based on value selected in actionTaken
  Scenario Outline: Perform task 'Supervisor Process Request To Amend Investigation Classification'
    # here glue code will hit pole to fetch supplied object
    Given an Investigation exists with with objectRef <objectRef> for performing task <existingTaskName>
    And an Investigation exists with status of <inStatus>
    And a task <existingTaskName> exists against that Investigation
    # here glue code will check if there are no validation errors
    And the client is eligible to perform the task
    # Here we are creating child TxDto which can be later Client provides into parent Dto  -- START
    Then Client provides <actionTaken> into actionTaken in the transaction data
    Then Client provides <rejectionReason> into rejectionReason in the transaction data
    And the client provides primaryClassification as <primaryClassification> in the transaction data
    And the client provides includedClassification as <includedClassification> in the transaction data
    And the client provides remarks as <remarks> in the transaction data
    And the client provides fcr as <oic> in the transaction data
    And the client provides fcr as <requestingStaffMember> in the transaction data
    When the client submits the Investigation and transaction data to Business Services
    Then the Investigation will have its Status changed to <outStatus>
    # And the task will be performed
    And a new Task History Entry will be added to the Investigation
    And a task <newTaskName> exists against that Investigation
    ## The following values should be asserted
    Then taskName with Acknowledge Rejection Of Investigation is removed from the workload tray
    Then the Task History Entry will record actionTaken as <actionTaken>
    Then the Task History Entry will record reasonForRejection as <rejectionReason>
    Then the Task History Entry will record reasonForRejection as <primaryClassification>
    Then the Task History Entry will record reasonForRejection as <includedClassification>
    Then the Task History Entry will record remarks as <remarks>
    Then the Task History Entry will record oic as <oic>
    Then the Task History Entry will record requestingStaffMember as <requestingStaffMember>

    #New to write gherkin to validate workflow parameters
    @SITB
    Examples: 
      | objectRef | existingTaskName                                                 | inStatus            | outStatus           | employeeIterationId | rejectionReason | actionTaken | newTaskName                                                                          | primaryClassification | includedClassification | remarks         | oic | requestingStaffMember |
      |   2120456 | Supervisor Process Request To Amend Investigation Classification | UNDER INVESTIGATION | UNDER INVESTIGATION |               14825 | I have rejected | REJECTED    | Acknowledge Rejection Of Request To Amend Investigation Classification By Supervisor | test primary          | test include           | testing remarks |  42 |                    42 |
      |   2120456 | Supervisor Process Request To Amend Investigation Classification | UNDER INVESTIGATION | UNDER INVESTIGATION |               14825 |                 | ACCEPTED    | FCR Process Request To Amend Investigation Classification                            | test primary          | test include           | testing remarks |  42 |                    42 |

    @NDT-2
    Examples: 
      | objectRef | existingTaskName                                                 | inStatus            | outStatus           | employeeIterationId | rejectionReason | actionTaken | newTaskName                                                                          | primaryClassification | includedClassification | remarks         | oic | requestingStaffMember |
      |   2120456 | Supervisor Process Request To Amend Investigation Classification | UNDER INVESTIGATION | UNDER INVESTIGATION |               14825 | I have rejected | REJECTED    | Acknowledge Rejection Of Request To Amend Investigation Classification By Supervisor | test primary          | test include           | testing remarks |  42 |                    42 |
      |   2120456 | Supervisor Process Request To Amend Investigation Classification | UNDER INVESTIGATION | UNDER INVESTIGATION |               14825 |                 | ACCEPTED    | FCR Process Request To Amend Investigation Classification                            | test primary          | test include           | testing remarks |  42 |                    42 |

    @MSDevTest
    Examples: 
      | objectRef | existingTaskName                                                 | inStatus            | outStatus           | employeeIterationId | rejectionReason | actionTaken | newTaskName                                                                          | primaryClassification | includedClassification | remarks         | oic | requestingStaffMember |
      |   2120456 | Supervisor Process Request To Amend Investigation Classification | UNDER INVESTIGATION | UNDER INVESTIGATION |               14825 | I have rejected | REJECTED    | Acknowledge Rejection Of Request To Amend Investigation Classification By Supervisor | test primary          | test include           | testing remarks |  42 |                    42 |
      |   2120456 | Supervisor Process Request To Amend Investigation Classification | UNDER INVESTIGATION | UNDER INVESTIGATION |               14825 |                 | ACCEPTED    | FCR Process Request To Amend Investigation Classification                            | test primary          | test include           | testing remarks |  42 |                    42 |
