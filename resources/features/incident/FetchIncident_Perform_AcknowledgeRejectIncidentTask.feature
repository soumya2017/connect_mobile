@Generic
Feature: Perform the Rejection flow and acknowledge the Rejection task

  Scenario Outline: Fetch Incident and perform Acknowledge Rejection Of Investigation task
    Given Client searches for created Incident with objectRef <objectRef>
    Then Client fetches Incident for editing
    Then Status should be <inStatus>
    Then Client Should have taskName with Acknowledge Rejection Of Investigation
    #Then taskName should be Acknowledge rejection of Investigation
    #Here we will create parent TxDto and Client provides child TxDto in it
    And Create Incident.IncidentTxData
    And Client provides Incident.IncidentTxData into Incident
    #-- END OF PARENT TXDATA
    # Here we are creating child TxDto which can be later Client provides into parent Dto  -- START
    And Create Incident.IncidentTxData.AcknowledgeRejectIncidentTxData
    Then Client provides <employeeIterationId> into employeeIterationId
    And Client provides True into acknowledgeRejection
    And Client provides I have rejected into reasonForRejection
    #And Client provides 5720277 into taskId
    And Client provides Incident.IncidentTxData.AcknowledgeRejectIncidentTxData into Incident.IncidentTxData
    ##-- END OF PARENT TXDATA
    When Client submits Incident for update
    Then Status should be <outStatus>
    ## The following values should be asserted
    Then taskName with Acknowledge Rejection Of Investigation is removed from the workload tray
    Then TaskHistory is stored # this can be further written for specific fields to verify in task history
    Then TaskHistory should contain acknowledgeRejection value
    Then TaskHistory should contain reasonForRejection value

    #New to write gherkin to validate workflow parameters
    @SITB
    Examples: 
      | objectRef | inStatus | outStatus | employeeIterationId |
      |   2120456 | REJECTED | REJECTED  |               14825 |

    @NDT-2
    Examples: 
      | objectRef | inStatus | outStatus | employeeIterationId |
      |   2120282 | REJECTED | REJECTED  |               14825 |
    #|   1162260 | REJECTED | REJECTED  |               14825 |
    
    
    @MSDevTest
    Examples: 
      | objectRef | inStatus | outStatus | employeeIterationId |
      |   1420339 | REJECTED | REJECTED  |                7272 |
