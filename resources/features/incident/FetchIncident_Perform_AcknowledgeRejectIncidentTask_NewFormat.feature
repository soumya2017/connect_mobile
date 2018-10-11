@Generic
Feature: Perform task 'Acknowledge Rejection Of Investigation'

  Scenario Outline: Perform task 'Acknowledge Rejection Of Investigation'
    # here glue code will hit pole to fetch supplied object
    Given an Investigation exists with with objectRef <objectRef> for performing task Acknowledge Rejection Of Investigation
    And an Investigation exists with status of <inStatus>
    And a task Acknowledge Rejection Of Investigation exists against that Investigation
    # here glue code will check if there are no validation errors
    And the client is eligible to perform the task
    # Here we are creating child TxDto which can be later Client provides into parent Dto  -- START
    Then Client provides <employeeIterationId> into employeeIterationId in the transaction data
    And Client provides True into acknowledgeRejection in the transaction data
    And Client provides <reasonForRejection> into reasonForRejection in the transaction data
    When the client submits the Investigation and transaction data to Business Services
    Then the Investigation will have its Status changed to <outStatus>
   # And the task will be performed
    And a new Task History Entry will be added to the Investigation
    ## The following values should be asserted
    Then taskName with Acknowledge Rejection Of Investigation is removed from the workload tray
    Then the Task History Entry will record acknowledgeRejection as True
    Then the Task History Entry will record reasonForRejection as <reasonForRejection>

    #New to write gherkin to validate workflow parameters
    @SITB
    Examples: 
      | objectRef | inStatus | outStatus | employeeIterationId | reasonForRejection |
      |   2120456 | REJECTED | REJECTED  |               14825 | I have rejected    |

    @NDT-2
    Examples: 
      | objectRef | inStatus | outStatus | employeeIterationId | reasonForRejection |
      |   2123258 | REJECTED | REJECTED  |               14825 | I have rejected    |

    #|   1162260 | REJECTED | REJECTED  |               14825 |
    @MSDevTest
    Examples: 
      | objectRef | inStatus | outStatus | employeeIterationId | reasonForRejection |
      |   2218279 | REJECTED | REJECTED  |               14825 | I have rejected    |
