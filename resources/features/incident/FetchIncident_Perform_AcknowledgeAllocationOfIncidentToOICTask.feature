@Generic
Feature: Perform Acknowledge Allocation Of Incident To OIC task

  Background: Given an Incident has been assigned to an OIC
    And an Acknowledge Allocation To OIC task has been raised against the new OIC’s supervisory unit

  Scenario Outline: Fetch Incident and Perform Acknowledge Allocation Of Incident To OIC task
    Given Client searches for created Incident with objectRef <objectRef>
    Then Client fetches Incident for editing
    Then Status should be <inStatus>
    Then Client Should have taskName with <taskName>
    #Here we will create parent TxDto and Client provides child TxDto in it
    And Create Incident.IncidentTxData
    And Client provides Incident.IncidentTxData into Incident
    #-- END OF PARENT TXDATA
    # Here we are creating child TxDto which can be later Client provides into parent Dto  -- START
    And Create Incident.IncidentTxData.AcknowledgeAllocationOfIncidentToOicTxData
    Then Client provides <employeeIterationId> into employeeIterationId
    And Client provides True into acknowledgeAllocation
    And Client provides Incident.IncidentTxData.AcknowledgeAllocationOfIncidentToOicTxData into Incident.IncidentTxData
    ##-- END OF PARENT TXDATA
    When Client submits Incident for update
    Then Status should be <outStatus>
    ## The following values should be asserted
    Then taskName with <taskName> should be removed from the workload tray
    Then TaskHistory is stored # this can be further written for specific fields to verify in task history
    Then TaskHistory should contain acknowledgeAllocation value
    Then TaskHistory should contain employeeIterationId value

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
