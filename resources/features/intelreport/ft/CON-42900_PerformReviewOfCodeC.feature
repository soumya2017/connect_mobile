Feature: CON-42900 Perform task 'Perform Review Of Code C' in Intelligence Report

  @draft
  Scenario Outline: Perform task 'Perform review of Code C' present in REQUIRES LINKING status
  #BS-INCLUDE feature=CON-42859_AssessIntelligenceReport_Accepted.feature,scenario=Happy Path with status of 'REQUIRES ASSESSMENT'
    Given an Intelligence Report exists with status of 'REQUIRES LINKING'
    And a task 'Perform review of Code C'  exists against that Intelligence Report
    #txData
    And the client provides 'New Handling Code' as <newHandlingCode>
    And the client provides 'Continued Handling Cond Text' as <contHandCondText>
    When the client submits the Intelligence Report to Business Services
    Then a new task 'Review Of Code C Performed' is created for unit ${bs.cucumber.unit}
    And a new Task History Entry will be added to the Intelligence report
    And the Task History Entry will record 'New Handling Code' as <newHandlingCode>
    And the Task History Entry will record 'Continued Handling Cond Text' as <contHandCondText>

    Examples: 
      | newHandlingCode | contHandCondText  | newHandlingCode | contHandCondText  |
      | C               | this is free text | C               | this is free text |
      | P               |                   |                 |                   |

 @draft 
  Scenario Outline: Perform task 'Perform review of Code C' present in REQUIRES LINKING SENSITIVE status
  #BS-INCLUDE feature=CON-42859_AssessIntelligenceReport_Accepted.feature,scenario=Happy Path status of 'REQUIRES ASSESSMENT SENSITIVE'
    Given an Intelligence Report exists with status of 'REQUIRES LINKING SENSITIVE'
    And a task 'Perform review of Code C'  exists against that Intelligence Report
    #txData
    And the client provides 'New Handling Code' as <newHandlingCode>
    And the client provides 'Continued Handling Cond Text' as <contHandCondText>
    When the client submits the Intelligence Report to Business Services
    Then a new task 'Review Of Code C Performed' is created for unit ${bs.cucumber.unit}
    And a new Task History Entry will be added to the Intelligence report
    And the Task History Entry will record 'New Handling Code' as <newHandlingCode>
    And the Task History Entry will record 'Continued Handling Cond Text' as <contHandCondText>

    Examples: 
      | newHandlingCode | contHandCondText  | newHandlingCode | contHandCondText  |
      | C               | this is free text | C               | this is free text |
      | P               |                   |                 |                   |

  
@draft
  Scenario Outline: Perform task 'Perform review of Code C' present in LIVE status
    Given an Intelligence Report exists with status of 'LIVE'
    And a task 'Perform review of Code C'  exists against that Intelligence Report
    #txData
    And the client provides 'New Handling Code' as <newHandlingCode>
    And the client provides 'Continued Handling Cond Text' as <contHandCondText>
    When the client submits the Intelligence Report to Business Services
    Then a new task 'Review Of Code C Performed' is created for unit ${bs.cucumber.unit}
    And a new Task History Entry will be added to the Intelligence report
    And the Task History Entry will record 'New Handling Code' as <newHandlingCode>
    And the Task History Entry will record 'Continued Handling Cond Text' as <contHandCondText>

    Examples: 
      | newHandlingCode | contHandCondText  | newHandlingCode | contHandCondText  |
      | P               | this is free text | P               | this is free text |
      | C               | this is free text | C               | this is free text |
	  
	  
@draft
  Scenario Outline: Perform task 'Perform review of Code C' present in LIVE SENSITIVE status
    Given an Intelligence Report exists with status of 'LIVE SENSITIVE'
    And a task 'Perform review of Code C'  exists against that Intelligence Report
    #txData
    And the client provides 'New Handling Code' as <newHandlingCode>
    And the client provides 'Continued Handling Cond Text' as <contHandCondText>
    When the client submits the Intelligence Report to Business Services
    Then a new task 'Review Of Code C Performed' is created for unit ${bs.cucumber.unit}
    And a new Task History Entry will be added to the Intelligence report
    And the Task History Entry will record 'New Handling Code' as <newHandlingCode>
    And the Task History Entry will record 'Continued Handling Cond Text' as <contHandCondText>

    Examples: 
      | newHandlingCode | contHandCondText  | newHandlingCode | contHandCondText  |
      | P               | this is free text | P               | this is free text |
      | C               | this is free text | C               | this is free text |
	  
	  
@draft
  Scenario Outline: Perform task 'Perform review of Code C' for flagged Intel report present in REQUIRES LINKING status
    Given an Intelligence Report exists with status of 'REQUIRES LINKING'
    And a task 'Perform review of Code C'  exists against that Intelligence Report
    #txData
    And the client provides 'New Handling Code' as <newHandlingCode>
    And the client provides 'Continued Handling Cond Text' as <contHandCondText>
    And the client provides 'Request Flag Removed' as <requestFlagRemoved>
    When the client submits the Intelligence Report to Business Services
    Then a new task 'Review Of Code C Performed' is created for unit ${bs.cucumber.unit}
    And a new Task History Entry will be added to the Intelligence report
    And the Task History Entry will record 'New Handling Code' as <newHandlingCode>
    And the Task History Entry will record 'Continued Handling Cond Text' as <contHandCondText>
    And the Task History Entry will record 'Request Flag Removed' as <requestFlagRemoved>

    Examples: 
      | newHandlingCode | contHandCondText  | requestFlagRemoved | newHandlingCode | contHandCondText  | requestFlagRemoved |
      | C               | this is free text | true               | C               | this is free text | true               |
      | C               | this is free text | false              | C               | this is free text | false              |
	  
	  
@draft
  Scenario Outline: Perform task 'Perform review of Code C' for flagged Intel report present in REQUIRES LINKING SENSITIVE status
    Given an Intelligence Report exists with status of 'REQUIRES LINKING SENSITIVE'
    And a task 'Perform review of Code C'  exists against that Intelligence Report
    #txData
    And the client provides 'New Handling Code' as <newHandlingCode>
    And the client provides 'Continued Handling Cond Text' as <contHandCondText>
    And the client provides 'Request Flag Removed' as <requestFlagRemoved>
    When the client submits the Intelligence Report to Business Services
    Then a new task 'Review Of Code C Performed' is created for unit ${bs.cucumber.unit}
    And a new Task History Entry will be added to the Intelligence report
    And the Task History Entry will record 'New Handling Code' as <newHandlingCode>
    And the Task History Entry will record 'Continued Handling Cond Text' as <contHandCondText>
    And the Task History Entry will record 'Request Flag Removed' as <requestFlagRemoved>

    Examples: 
      | newHandlingCode | contHandCondText  | requestFlagRemoved | newHandlingCode | contHandCondText  | requestFlagRemoved |
      | C               | this is free text | true               | C               | this is free text | true               |
      | C               | this is free text | false              | C               | this is free text | false              |

	 @draft
  Scenario Outline: Perform task 'Perform review of Code C' for flagged Intel report present in LIVE status
    Given an Intelligence Report exists with status of "LIVE'
    And a task 'Perform review of Code C'  exists against that Intelligence Report
    #txData
    And the client provides 'New Handling Code' as <newHandlingCode>
    And the client provides 'Continued Handling Cond Text' as <contHandCondText>
    And the client provides 'Request Flag Removed' as <requestFlagRemoved>
    When the client submits the Intelligence Report to Business Services
    Then a new task 'Review Of Code C Performed' is created for unit ${bs.cucumber.unit}
    And a new Task History Entry will be added to the Intelligence report
    And the Task History Entry will record 'New Handling Code' as <newHandlingCode>
    And the Task History Entry will record 'Continued Handling Cond Text' as <contHandCondText>
    And the Task History Entry will record 'Request Flag Removed' as <requestFlagRemoved>

    Examples: 
      | newHandlingCode | contHandCondText  | requestFlagRemoved | newHandlingCode | contHandCondText  | requestFlagRemoved |
      | C               | this is free text | true               | C               | this is free text | true               |
      | C               | this is free text | false              | C               | this is free text | false              |

@draft	
  Scenario Outline: Perform task 'Perform review of Code C' for flagged Intel report present in LIVE SENSITIVE status
    Given an Intelligence Report exists with status of 'LIVE SENSITIVE'
    And a task 'Perform review of Code C'  exists against that Intelligence Report
    #txData
    And the client provides 'New Handling Code' as <newHandlingCode>
    And the client provides 'Continued Handling Cond Text' as <contHandCondText>
    And the client provides 'Request Flag Removed' as <requestFlagRemoved>
    When the client submits the Intelligence Report to Business Services
    Then a new task 'Review Of Code C Performed' is created for unit ${bs.cucumber.unit}
    And a new Task History Entry will be added to the Intelligence report
    And the Task History Entry will record 'New Handling Code' as <newHandlingCode>
    And the Task History Entry will record 'Continued Handling Cond Text' as <contHandCondText>
    And the Task History Entry will record 'Request Flag Removed' as <requestFlagRemoved>

    Examples: 
      | newHandlingCode | contHandCondText  | requestFlagRemoved | newHandlingCode | contHandCondText  | requestFlagRemoved |
      | C               | this is free text | true               | C               | this is free text | true               |
      | C               | this is free text | false              | C               | this is free text | false              |

  @draft
  Scenario Outline: Perform task 'Perform review of Code C' for flagged Intel report present in REQUIRES LINKING status without entering data in field 'Request Flag Removed'
  #BS-INCLUDE feature=CON-42859_AssessIntelligenceReport_Accepted.feature,scenario=Happy Path with status of 'REQUIRES ASSESSMENT'
    Given an Intelligence Report exists with status of 'REQUIRES LINKING'
    And a task 'Perform review of Code C' exists against that Intelligence Report
    #txData
    And the client provides 'New Handling Code' as <newHandlingCode>
    And the client provides 'Continued Handling Cond Text' as <contHandCondText>
    When the client submits the Intelligence Report to Business Services
    Then Error will be returned for field 'Request Flag Removed' with message 'requestFlagRemoved must have a value'

    Examples: 
      | newHandlingCode | contHandCondText  |
      | C               | this is free text |
      | P               | this is free text |

  @draft
  Scenario Outline: Perform task 'Perform review of Code C' for flagged Intel report present in REQUIRES LINKING SENSITIVE status without entering data in field 'Request Flag Removed'
  #BS-INCLUDE feature=CON-42859_AssessIntelligenceReport_Accepted.feature,scenario=Happy Path status of 'REQUIRES ASSESSMENT SENSITIVE'
    Given an Intelligence Report exists with status of 'REQUIRES LINKING SENSITIVE'
    And a task 'Perform review of Code C'  exists against that Intelligence Report
    #txData
    And the client provides 'New Handling Code' as <newHandlingCode>
    And the client provides 'Continued Handling Cond Text' as <contHandCondText>
    When the client submits the Intelligence Report to Business Services
    Then Error will be returned for field 'Request Flag Removed' with message 'requestFlagRemoved must have a value'

    Examples: 
      | newHandlingCode | contHandCondText  |
      | C               | this is free text |
      | P               | this is free text |

 @draft
  Scenario Outline: Perform task 'Perform review of Code C' for flagged Intel report present in LIVE status without entering data in field 'Request Flag Removed'
    Given an Intelligence Report exists with status of 'LIVE'
    And a task 'Perform review of Code C'  exists against that Intelligence Report
    #txData
    And the client provides 'New Handling Code' as <newHandlingCode>
    And the client provides 'Continued Handling Cond Text' as <contHandCondText>
    When the client submits the Intelligence Report to Business Services
    Then Error will be returned for field 'Request Flag Removed' with message 'requestFlagRemoved must have a value'

    Examples: 
      | newHandlingCode | contHandCondText  |
      | C               | this is free text |
      | P               | this is free text |

	  @draft
  Scenario Outline: Perform task 'Perform review of Code C' for flagged Intel report present in LIVE SENSITIVE status without entering data in field 'Request Flag Removed'
    Given an Intelligence Report exists with status of 'LIVE SENSITIVE'
    And a task 'Perform review of Code C'  exists against that Intelligence Report
    #txData
    And the client provides 'New Handling Code' as <newHandlingCode>
    And the client provides 'Continued Handling Cond Text' as <contHandCondText>
    When the client submits the Intelligence Report to Business Services
    Then Error will be returned for field 'Request Flag Removed' with message 'requestFlagRemoved must have a value'

    Examples: 
      | newHandlingCode | contHandCondText  |
      | C               | this is free text |
      | P               | this is free text |

  @draft
  Scenario: Perform task 'Perform review of Code C' present in REQUIRES LINKING status without entering mandatory fields
  #BS-INCLUDE feature=CON-42859_AssessIntelligenceReport_Accepted.feature,scenario=Happy Path with status of 'REQUIRES ASSESSMENT'
    Given an Intelligence Report exists with status of 'REQUIRES LINKING'
    And a task 'Perform review of Code C'  exists against that Intelligence Report
    #txData
    And the client provides 'New Handling Code' as 'C'
    When the client submits the Intelligence Report to Business Services
    Then Error will be returned for field 'Continued Handling Cond Text' with message 'continuedHandlingCondText must have a value'

  @draft
  Scenario: Perform task 'Perform review of Code C' present in REQUIRES LINKING SENSITIVE status without entering mandatory fields
  #BS-INCLUDE feature=CON-42859_AssessIntelligenceReport_Accepted.feature,scenario=Happy Path status of 'REQUIRES ASSESSMENT SENSITIVE'
    Given an Intelligence Report exists with status of 'REQUIRES LINKING SENSITIVE'
    And a task 'Perform review of Code C'  exists against that Intelligence Report
    #txData
    And the client provides 'New Handling Code' as 'C'
    When the client submits the Intelligence Report to Business Services
    Then Error will be returned for field 'Continued Handling Cond Text' with message 'continuedHandlingCondText must have a value'

	@draft
  Scenario: Perform task 'Perform review of Code C' present in LIVE status without entering mandatory fields
    Given an Intelligence Report exists with status of 'C'
    And a task 'Perform review of Code C'  exists against that Intelligence Report
    #txData
    And the client provides 'New Handling Code' as <newHandlingCode>
    When the client submits the Intelligence Report to Business Services
    Then Error will be returned for field 'Continued Handling Cond Text' with message 'continuedHandlingCondText must have a value'

	@draft
  Scenario: Perform task 'Perform review of Code C' present in LIVE SENSITIVE status without entering mandatory fields
    Given an Intelligence Report exists with status of 'LIVE SENSITIVE'
    And a task 'Perform review of Code C'  exists against that Intelligence Report
    #txData
    And the client provides 'New Handling Code' as 'C'
    When the client submits the Intelligence Report to Business Services
    Then Error will be returned for field 'Continued Handling Cond Text' with message 'continuedHandlingCondText must have a value'
