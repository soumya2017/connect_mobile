@Generic
Feature: Fetch Incident and perform Acknowledge Investigation Creation Completion Outstanding task

  Scenario Outline: Fetch Incident and perform Acknowledge Investigation Creation Completion Outstanding task
    Given Client searches for created Incident with objectRef <objectRef>
    Then Client fetchs Incident for editing
    Then Status should be <inStatus>
    Then Client Should have taskName with Acknowledge Investigation Creation Completion Outstanding
    #Here we will create parent TxDto and Client provides child TxDto in it
    And Create Incident.IncidentTxDataDto
    And Client provides Incident.IncidentTxDataDto into Incident
    #-- END OF PARENT TXDATA
    # Here we are creating child TxDto which can be later Client provides into parent Dto  -- START
    And Create Incident.IncidentTxDataDto.AcknowledgeIncidentCreationCompletionOutstanding
    And Client provides acknowledgeIncidentCreationCompletionOutstanding into Yes
    And Client provides Incident.IncidentTxDataDto.AcknowledgeIncidentCreationCompletionOutstanding into Incident.IncidentTxDataDto
    ##-- END OF PARENT TXDATA
    When Client submits IntelligenceReport for creation
    Then Status should be <outStatus>
    ## The name of the task got generated in creared gherkin - 'Assess Intelligence Report'
    Then TaskHistory is stored
    Then TaskHostry should contain acknowledgeIncidentCreationCompletionOutstanding value

    #New to write gherkin to validate workflow parameters
    # Then taskName should be <newTaskName>
    Examples: 
      | objectRef | inStatus            | outStatus           |
      |   1531431 | REQUIRES COMPLETION | REQUIRES COMPLETION |
      # comments :- as of now this is capturing  happy path
