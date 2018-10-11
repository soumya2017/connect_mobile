#Author: abhishek.mishra@northgateps.com
Feature: CON-42859 Perform task 'Assess Intelligence Report' for action 'Accepted'

  Scenario: Happy Path with status of 'REQUIRES ASSESSMENT'
  #BS-INCLUDE feature=CON-42850_CreateIntelligenceReport.feature, scenario=Happy Path for Create Intelligence Report In Require Assessment status
    Given an Intelligence Report exists with status of 'REQUIRES ASSESSMENT'
    And a task 'Assess Intelligence Report' exists against that Intelligence Report
    And the client is eligible to perform the task
    And the client provides 'San Handling' as 'C'
    And the client provides 'San Handling Conditions' as 'C'
    And the client provides 'Information Sanitised Action Code' as 'A1'
    And the client provides 'Information Sanitised Code' as 'S1'
   #txdata
    And the client provides 'Action Taken' as 'ACCEPTED'
    And the client provides 'Mopi Grouping' as '1'
    And the client provides 'Next Reviewer Type' as 'UNIT'
    And the client provides 'Next Reviewed By Unit Actor' as 'AMO'
    #childObject
    And the client provides an 'Sanitised Text'
    And the client provides 'Text' as 'sanitisetext'
    And the client provides 'Source' as '1'
    And the client provides 'Evaluation' as 'A'
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to 'REQUIRES LINKING'
    And the task will be performed
    And a new task 'Link Intelligence Report' is created for unit ${bs.cucumber.unit}
    And a new Task History Entry will be added to the Intelligence Report
    And the Task History Entry will record ActionTaken as 'ACCEPTED'

  #In below scenario user should be able to perform perform 'Assess Intelligence Report' task for the first it gets generated
  Scenario Outline: I should be able to perform 'Assess Intelligence Report' task with action taken as Accepted
  #BS-INCLUDE feature=CON-42850_CreateIntelligenceReport.feature, scenario=Happy Path for Create Intelligence Report In Require Assessment status
    Given an Intelligence Report exists with status of 'REQUIRES ASSESSMENT'
    And a task 'Assess Intelligence Report' exists against that Intelligence Report
    And the client is eligible to perform the task
    And the client provides 'San Handling' as <HandlingCode>
    And the client provides 'San Handling Conditions' as <HandingConditions>
    And the client provides 'Information Sanitised Action Code' as <ActionCode>
    And the client provides 'Information Sanitised Action Code A3 Usage' as <ActionCodeA3Usage>
    And the client provides 'Information Sanitised Code' as <SanitisationCode>
    #txdata
    And the client provides 'Action Taken' as 'ACCEPTED'
    And the client provides 'Mopi Grouping' as '1'
    And the client provides 'Next Reviewer Type' as 'UNIT'
    And the client provides 'Next Reviewed By Unit Actor' as 'AMO'
    #childObject
    And the client provides an 'Sanitised Text'
    And the client provides 'Text' as 'This is Intelligence Text''
    And the client provides 'Source' as '1'
    And the client provides 'Evaluation' as <Evaluation>
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to 'REQUIRES LINKING'
    And the task will be performed
    And a new task 'Link Intelligence Report' is created for unit ${bs.cucumber.unit}
    And a new Task History Entry will be added to the Intelligence Report
    And the Task History Entry will record ActionTaken as 'ACCEPTED'
    
    Examples: 
     | Evaluation | HandlingCode  | HandingConditions               | ActionCode | ActionCodeA3Usage            | SanitisationCode | 
     | A          | C             | Detailed handling instructions  | A1         |                              | S1               |        
     | A          | C             | Detailed handling instructions  | A3         | Code A3 usage instructions   | S1               |       
     | C          | P             |                                 |            |                              |                  |                       

  # In below scenario user should be able to perform perform 'Assess Intelligence Report' task for Intel reports which were sent for correction to Intel submitter
  Scenario: I should be able to perform 'Assess Intelligence Report' task with action taken as Accepted for Intel reports which are corrected by Intel submitter
    #BS-INCLUDE feature=CON-42853_CorrectInformationReport.feature, scenario=Happy Path with status of 'REQUIRES CORRECTION'
   Given an Intelligence Report exists with status of 'REQUIRES ASSESSMENT'
    And a task 'Assess Intelligence Report' exists against that Intelligence Report
    And the client is eligible to perform the task
    And the client provides 'San Handling' as 'C'
    And the client provides 'San Handling Conditions' as 'C'
    And the client provides 'Information Sanitised Action Code' as 'A1'
    And the client provides 'Information Sanitised Code' as 'S1'
   #txdata
    And the client provides 'Action Taken' as 'ACCEPTED'
    And the client provides 'Mopi Grouping' as '1'
    And the client provides 'Next Reviewer Type' as 'UNIT'
    And the client provides 'Next Reviewed By Unit Actor' as 'AMO'
    And the client provides 'Intel Reevaluated' as 'True'
    And the client provides 'reviewedByEmployeeIterationId' as ${bs.cucumber.submittedToId}
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to 'REQUIRES LINKING'
    And the task will be performed
    And a new task 'Link Intelligence Report' is created for unit ${bs.cucumber.unit}
    And a new Task History Entry will be added to the Intelligence Report
    And the Task History Entry will record ActionTaken as 'ACCEPTED'

   
Scenario: Happy Path status of 'REQUIRES ASSESSMENT SENSITIVE'
#BS-INCLUDE feature=CON-42850_CreateIntelligenceReport.feature, scenario=Happy Path for Create Intelligence Report In Require Assessment Sensitive status
	Given an Intelligence Report exists with status of 'REQUIRES ASSESSMENT SENSITIVE'
	And a task 'Assess Intelligence Report' exists against that Intelligence Report
	And the client is eligible to perform the task
	And the client provides 'San Handling' as 'C'
	And the client provides 'San Handling Conditions' as 'C'
	And the client provides 'Information Sanitised Action Code' as 'A1'
	And the client provides 'Information Sanitised Code' as 'S1'
	#txdata
	And the client provides 'Action Taken' as 'ACCEPTED'
	And the client provides 'Mopi Grouping' as '1'
	And the client provides 'Next Reviewer Type' as 'UNIT'
	And the client provides 'Next Reviewed By Unit Actor' as 'AMO'
	#childObject
	And the client provides an 'Sanitised Text'
	And the client provides 'Text' as 'sanitisetext'
	And the client provides 'Source' as '1'
	And the client provides 'Evaluation' as 'A'
	When the client submits the Intelligence Report to Business Services
	Then the Intelligence Report will have its Status changed to 'REQUIRES LINKING SENSITIVE'
	And the task will be performed
	And a new task 'Link Intelligence Report' is created for unit ${bs.cucumber.unit}
	And a new Task History Entry will be added to the Intelligence Report
	And the Task History Entry will record ActionTaken as 'ACCEPTED'
	
  #In below scenario user should be able to perform perform 'Assess Intelligence Report' task for the first it gets generated
  Scenario Outline: I should be able to perform 'Assess Intelligence Report' task with action taken as Accepted with status of 'REQUIRES ASSESSMENT SENSITIVE'
  #BS-INCLUDE feature=CON-42850_CreateIntelligenceReport.feature, scenario=Happy Path for Create Intelligence Report In Require Assessment Sensitive status
    Given an Intelligence Report exists with status of 'REQUIRES ASSESSMENT SENSITIVE'
    And a task 'Assess Intelligence Report' exists against that Intelligence Report
    And the client is eligible to perform the task
    And the client provides 'San Handling' as <HandlingCode>
    And the client provides 'San Handling Conditions' as <HandingConditions>
    And the client provides 'Information Sanitised Action Code' as <ActionCode>
    And the client provides 'Information Sanitised Action Code A3 Usage' as <ActionCodeA3Usage>
    And the client provides 'Information Sanitised Code' as <SanitisationCode>
    # txdata
    And the client provides 'Action Taken' as 'ACCEPTED'
    And the client provides 'Mopi Grouping' as '1'
    And the client provides 'Next Reviewer Type' as 'UNIT'
    And the client provides 'Next Reviewed By Unit Actor' as 'AMO'
   # childObject
    And the client provides an 'Sanitised Text'
    And the client provides 'Text' as 'This is Intelligence Text''
    And the client provides 'Source' as '1'
    And the client provides 'Evaluation' as <Evaluation>
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to 'REQUIRES LINKING SENSITIVE'
    And the task will be performed
    And a new task 'Link Intelligence Report' is created for unit ${bs.cucumber.unit}
    And a new Task History Entry will be added to the Intelligence Report
    And the Task History Entry will record ActionTaken as 'ACCEPTED'

  Examples: 
     | Evaluation | HandlingCode  | HandingConditions               | ActionCode | ActionCodeA3Usage            | SanitisationCode | 
     | A          | C             | Detailed handling instructions  | A1         |                              | S1               |        
     | A          | C             | Detailed handling instructions  | A3         | Code A3 usage instructions   | S1               |       
     | C          | P             |                                 |            |                              |                  |                       
    
  #In below scenario user should be able to perform perform 'Assess Intelligence Report' task for Intel reports which were sent for correction to Intel submitter
  Scenario: I should be able to perform 'Assess Intelligence Report' task with action taken as Accepted for Intel reports which are corrected by Intel submitter
   #BS-INCLUDE feature=CON-42853_CorrectInformationReport.feature, scenario=Happy Path with status of 'REQUIRES CORRECTION SENSITIVE'
    Given an Intelligence Report exists with status of 'REQUIRES ASSESSMENT SENSITIVE'
	And a task 'Assess Intelligence Report' exists against that Intelligence Report
	And the client is eligible to perform the task
	And the client provides 'San Handling' as 'C'
	And the client provides 'San Handling Conditions' as 'C'
	And the client provides 'Information Sanitised Action Code' as 'A1'
	And the client provides 'Information Sanitised Code' as 'S1'
	#txdata
	And the client provides 'Action Taken' as 'ACCEPTED'
	And the client provides 'Mopi Grouping' as '1'
	And the client provides 'Next Reviewer Type' as 'UNIT'
	And the client provides 'Next Reviewed By Unit Actor' as 'AMO'
	 And the client provides 'Intel Reevaluated' as 'True'
    And the client provides 'reviewedByEmployeeIterationId' as ${bs.cucumber.submittedToId}
	When the client submits the Intelligence Report to Business Services
	Then the Intelligence Report will have its Status changed to 'REQUIRES LINKING SENSITIVE'
	And the task will be performed
	And a new task 'Link Intelligence Report' is created for unit ${bs.cucumber.unit}
	And a new Task History Entry will be added to the Intelligence Report
	And the Task History Entry will record ActionTaken as 'ACCEPTED'  