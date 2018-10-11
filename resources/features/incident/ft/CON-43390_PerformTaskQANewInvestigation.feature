#Author: abhishek.mishra@northgateps.com
@draft
Feature: CON-43390 Perform task "QA New Investigation" for Investigation Record

  Scenario Outline: Perform task 'QA New Investigation' with action taken as 'Accepted' for Investigation records which do not have any static/event objects linked
    Given an Investigation Record exists with status of 'REQUIRES QA'
    And a task 'QA New Investigation'exists against that Investigation Record
    #txData
    And the client provides 'Action Taken' as 'ACCEPTED'
    #childObject name="incidentLogEntry"
    And the client provides an 'Incident Log Entry'
    And the client provides 'Entry Index' as '1'
    And the client provides 'Entry Date' as '2018-09-14'
    And the client provides 'Entry Time' as '09:20:31.735'
    And the client provides 'Entered By Officer Id' as '40920'
    And the client provides 'Entry Type' as <Entry Type>
    And the client provides 'Entry Text' as 'test for log entries'
    #childObject name="incidentClassification"
    And the client provides an 'Incident Classification'
    And the client provides 'Investigation ClassificationUrn' as <Primary Offence>
    And the client provides 'Classification Type' as 'PRIMARY'
    And the client provides 'Investigation ClassificationUrn' as <Included Offence>
    And the client provides 'Classification Type' as 'INCLUDED'
    #childObject name="incidentAsbCategory"
    And the client provides an 'Incident Asb Category'
    And the client provides 'Asb Category' as 'BALL GAMES'
    And the client provides 'Asb Category' as 'TRESPASS'
    #childObject name="incidentAsbKeyword"
    And the client provides an 'Incident Asb Keyword'
    And the client provides 'Asb keyword' as 'MARINE'
    And the client provides 'Asb keyword' as 'RURAL CRIME'
    #User submits the request
    When the client submits the Investigation Record to Business Services
    Then a new task 'Link Investigation' is created for unit 'AMO'
    And the Investigation Record will have its Status changed to 'REQUIRES LINKING'
    And a new Task History Entry will be added to the Investigation Record
    And the Task History Entry will record 'Action Taken' as 'ACCEPTED'
    And 'investigationType' is set to <Inv Type>

    Examples: 
      | Entry Type            | Primary Offence | Included Offence | Inv Type |
      | INVESTIGATION SUMMARY | 39.39.0.0       | AMO.NC.10.1      | CRIME    |
      | CCTV                  | 40.40.0.0A      | AMO.NC.1.0       | CRIME    |
      | PROPERTY              | AMO.NC.2.20     | AMO.NC.2.23      | NONCRIME |
      | BAD CHARACTER         | AMO.NC.2.22     | AMO.NC.2.20      | NONCRIME |
      | FORENSIC              | 42.42.0.0A      | 44.44.0.0        | CRIME    |

  Scenario Outline: Perform task 'QA New Investigation' with action taken as 'Accepted' in Investigation which has static objects linked with researched flag set to True
    Given an Investigation Record exists with status of 'REQUIRES QA'
    And a task 'QA New Investigation'exists against that Investigation Record
    #txData
    And the client provides 'Action Taken' as 'ACCEPTED'
    #childObject name="incidentLogEntry"
    And the client provides an 'Incident Log Entry'
    And the client provides 'Entry Index' as '1'
    And the client provides 'Entry Date' as '2018-09-14'
    And the client provides 'Entry Time' as '09:20:31.735'
    And the client provides 'Entered By Officer Id' as '40920'
    And the client provides 'Entry Type' as <Entry Type>
    And the client provides 'Entry Text' as 'test for log entries'
    #childObject name="incidentClassification"
    And the client provides an 'Incident Classification'
    And the client provides 'Investigation ClassificationUrn' as <Primary Offence>
    And the client provides 'Classification Type' as 'PRIMARY'
    And the client provides 'Investigation ClassificationUrn' as <Included Offence>
    And the client provides 'Classification Type' as 'INCLUDED'
    #childObject name="incidentAsbCategory"
    And the client provides an 'Incident Asb Category'
    And the client provides 'Asb Category' as 'BALL GAMES'
    And the client provides 'Asb Category' as 'TRESPASS'
    #childObject name="incidentAsbKeyword"
    And the client provides an 'Incident Asb Keyword'
    And the client provides 'Asb keyword' as 'MARINE'
    And the client provides 'Asb keyword' as 'RURAL CRIME'
    #User submits the request
    When the client submits the Investigation Record to Business Services
    Then a new task 'IMU Process Investigation' is created for unit 'AMO'
    And the Investigation Record will have its Status changed to 'REQUIRES IMU PROCESSING'
    And a new Task History Entry will be added to the Investigation Record
    And the Task History Entry will record 'Action Taken' as 'ACCEPTED'
    And 'investigationType' is set to <Inv Type>

    Examples: 
      | Entry Type            | Primary Offence | Included Offence | Inv Type |
      | INVESTIGATION SUMMARY | 39.39.0.0       | AMO.NC.10.1      | CRIME    |
      | CCTV                  | 40.40.0.0A      | AMO.NC.1.0       | CRIME    |
      | PROPERTY              | AMO.NC.2.20     | AMO.NC.2.23      | NONCRIME |
      | BAD CHARACTER         | AMO.NC.2.22     | AMO.NC.2.20      | NONCRIME |
      | FORENSIC              | 42.42.0.0A      | 44.44.0.0        | CRIME    |

  Scenario Outline: Perform task 'QA New Investigation' with action taken as 'Accepted' and add multiple included classification
    Given an Investigation Record exists with status of 'REQUIRES QA'
    And a task 'QA New Investigation'exists against that Investigation Record
    #txData
    And the client provides 'Action Taken' as 'ACCEPTED'
    #childObject name="incidentLogEntry"
    And the client provides an 'Incident Log Entry'
    And the client provides 'Entry Index' as '1'
    And the client provides 'Entry Date' as '2018-09-14'
    And the client provides 'Entry Time' as '09:20:31.735'
    And the client provides 'Entered By Officer Id' as '40920'
    And the client provides 'Entry Type' as 'PROPERTY'
    And the client provides 'Entry Text' as 'test for log entries'
    #childObject name="incidentClassification"
    And the client provides an 'Incident Classification'
    And the client provides 'Investigation ClassificationUrn' as '39.39.0.0'
    And the client provides 'Classification Type' as 'PRIMARY'
    And the client provides 'Investigation ClassificationUrn' as 'AMO.NC.10.1'
    And the client provides 'Classification Type' as 'INCLUDED'
    And the client provides 'Investigation ClassificationUrn' as 'AMO.NC.2.20'
    And the client provides 'Classification Type' as 'INCLUDED'
    And the client provides 'Investigation ClassificationUrn' as '44.44.0.0'
    And the client provides 'Classification Type' as 'INCLUDED'
    And the client provides 'Investigation ClassificationUrn' as '42.42.0.0A'
    And the client provides 'Classification Type' as 'INCLUDED'
    #childObject name="incidentAsbCategory"
    And the client provides an 'Incident Asb Category'
    And the client provides 'Asb Category' as 'BALL GAMES'
    And the client provides 'Asb Category' as 'TRESPASS'
    #childObject name="incidentAsbKeyword"
    And the client provides an 'Incident Asb Keyword'
    And the client provides 'Asb keyword' as 'MARINE'
    And the client provides 'Asb keyword' as 'RURAL CRIME'
    #User submits the request
    When the client submits the Investigation Record to Business Services
    Then a new task 'IMU Process Investigation' is created for unit 'AMO'
    And the Investigation Record will have its Status changed to 'REQUIRES IMU PROCESSING'
    And a new Task History Entry will be added to the Investigation Record
    And the Task History Entry will record 'Action Taken' as 'ACCEPTED'
    And 'investigationType' is set to 'CRIME'

  Scenario: Perform task 'QA New Investigation' with action taken as 'Rejected'
    Given an Investigation Record exists with status of 'REQUIRES QA'
    And a task 'QA New Investigation'exists against that Investigation Record
    #txData
    And the client provides 'Action Taken' as 'Rejected'
    And the client provides 'Reason For Rejection' as 'This is the reason for rejection'
    #childObject name="incidentLogEntry"
    And the client provides an 'Incident Log Entry'
    And the client provides 'Entry Index' as '1'
    And the client provides 'Entry Date' as '2018-09-14'
    And the client provides 'Entry Time' as '09:20:31.735'
    And the client provides 'Entered By Officer Id' as '40920'
    And the client provides 'Entry Type' as 'INVESTIGATION SUMMARY'
    And the client provides 'Entry Text' as 'test for log entries'
    #User submits the request
    When the client submits the Investigation Record to Business Services
    Then a new task 'Acknowledge rejection Of Investigation' is created for user '40920'
    And the Investigation Record will have its Status changed to 'REJECTED'
    And a new Task History Entry will be added to the Investigation Record
    And the Task History Entry will record 'Action Taken' as 'Rejected'
    And the Task History Entry will record 'Reason For Rejection' as 'This is the reason for rejection'

  Scenario: Perform task 'QA New Investigation' with action taken as 'Rejected' and do not supply data for mandatory fields
    Given an Investigation Record exists with status of 'REQUIRES QA'
    And a task 'QA New Investigation'exists against that Investigation Record
    #txData
    And the client provides 'Action Taken' as 'Rejected'
    #childObject name="incidentLogEntry"
    And the client provides an 'Incident Log Entry'
    And the client provides 'Entry Index' as '1'
    And the client provides 'Entry Date' as '2018-09-14'
    And the client provides 'Entry Time' as '09:20:31.735'
    And the client provides 'Entered By Officer Id' as '40920'
    #User submits the request
    When the client submits the Investigation Record to Business Services
    Then Error will be returned for field 'Reason For Rejection' with message 'reasonForRejection expected'
    Then Error will be returned for field 'Entry Type' with message 'entryType expected'
    Then Error will be returned for field 'Entry Text' with message 'entryText expected'

  Scenario: Perform task 'QA New Investigation' with action taken as 'Accepted' and supply multiple Primary Classifications
    Given an Investigation Record exists with status of 'REQUIRES QA'
    And a task 'QA New Investigation'exists against that Investigation Record
    #txData
    And the client provides 'Action Taken' as 'ACCEPTED'
    #childObject name="incidentClassification"
    And the client provides an 'Incident Classification'
    And the client provides 'Investigation ClassificationUrn' as '39.39.0.0'
    And the client provides 'Classification Type' as 'PRIMARY'
    And the client provides 'Investigation ClassificationUrn' as '40.40.0.0A'
    And the client provides 'Classification Type' as 'PRIMARY'
    And the client provides 'Investigation ClassificationUrn' as '44.44.0.0'
    And the client provides 'Classification Type' as 'INCLUDED'
    #User submits the request
    When the client submits the Investigation Record to Business Services
    Then Error will be returned for field 'Classification Type' with message 'One primary Incident classification is required.  Only one is allowed'

  Scenario: Perform task 'QA New Investigation' with action taken as 'Accepted' and supply Primary Classification as Non-Crime and Included Classification as Crime
    Given an Investigation Record exists with status of 'REQUIRES QA'
    And a task 'QA New Investigation'exists against that Investigation Record
    #txData
    And the client provides 'Action Taken' as 'ACCEPTED'
    #childObject name="incidentClassification"
    And the client provides an 'Incident Classification'
    And the client provides 'Investigation ClassificationUrn' as 'AMO.NC.2.22'
    And the client provides 'Classification Type' as 'PRIMARY'
    And the client provides 'Investigation ClassificationUrn' as '39.39.0.0'
    And the client provides 'Classification Type' as 'INCLUDED'
    #User submits the request
    When the client submits the Investigation Record to Business Services
    Then Error will be returned for field 'Classification Type' with message 'The Investigation has a 'Non-Crime' type primary HO classification but a 'Crime' type included HO classification. This combination is not allowed'

  Scenario: Perform task 'QA New Investigation' with action taken as 'Accepted' and do not supply investigationClassification
    Given an Investigation Record exists with status of 'REQUIRES QA'
    And a task 'QA New Investigation'exists against that Investigation Record
    #txData
    And the client provides 'Action Taken' as 'ACCEPTED'
    #User submits the request
    When the client submits the Investigation Record to Business Services
    Then Error will be returned for field 'incidentClassification' with message 'incidentClassification expected'

    
   