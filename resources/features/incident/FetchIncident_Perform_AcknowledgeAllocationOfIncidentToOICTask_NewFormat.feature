@Generic
Feature: Perform task 'Acknowledge Allocation Of Incident To OIC'

  #Background: Given an Incident has been assigned to an OIC
  #And an Acknowledge Allocation To OIC task has been raised against the new OIC’s supervisory unit
  Scenario Outline: Perform task 'Acknowledge Allocation Of Incident To OIC'
    Given an Incident exists with objectref <objectRef> for performing task Acknowledge Allocation Of Incident To OIC
    And an Incident exists with status of <inStatus>
    And a task Acknowledge Allocation Of Incident To OIC exists against that Investigation
    # here glue code will check if there are no validation errors
    And the client is eligible to perform the task
    #-- END OF PARENT TXDATA
    # Here we are creating child TxDto which can be later Client provides into parent Dto  -- START
    # And Create Incident.IncidentTxData.AcknowledgeAllocationOfIncidentToOicTxData
    And Client provides <employeeIterationId> into employeeIterationId
    And Client provides True into acknowledgeAllocation
    ##-- Submit Incident for Update
    When the client submits the Investigation and transaction data to Business Services
    Then the Investigation will have its Status changed to <outStatus>
    ## The following values should be asserted
    Then taskName with Acknowledge Allocation Of Investigation to OIC should be removed from the workload tray
    Then Task History entry should record acknowledgeAllocation as True
    Then Task History entry should record employeeIterationId as <employeeIterationId>

    #New to write gherkin to validate workflow parameters
    @SITB
    Examples: 
      | objectRef | inStatus            | outStatus           | employeeIterationId | taskName                                       |
      |   2120456 | UNDER INVESTIGATION | UNDER INVESTIGATION |               14825 | Acknowledge Allocation Of Investigation to OIC |

    @NDT-2
    Examples: 
      | objectRef | inStatus            | outStatus           | employeeIterationId | taskName                                       |
      |   2120456 | UNDER INVESTIGATION | UNDER INVESTIGATION |               14825 | Acknowledge Allocation Of Investigation to OIC |

    @MSDevTest
    Examples: 
      | objectRef | inStatus            | outStatus           | employeeIterationId | taskName                                       |
      |   2120456 | UNDER INVESTIGATION | UNDER INVESTIGATION |               14825 | Acknowledge Allocation Of Investigation to OIC |
