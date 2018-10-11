#Author: abhishek.mishra@northgateps.com
Feature: CON-42901 Perform a 'Review Of Code C Performed' task for intelligence reports
@draft
  Scenario Outline: Perform task 'Review Of Code C Performed' present in REQUIRES LINKING status
    #BS-INCLUDE feature=CON-42900_PerformReviewOfCodeC.feature,scenario=Perform task 'Perform review of Code C' present in REQUIRES LINKING status
    Given an Intelligence Report exists with status of 'REQUIRES LINKING'
    And a task 'Review Of Code C Performed'  exists against that Intelligence Report
    #txData
    And the client provides 'Confirm' as 'true'
    And the client provides 'Intel Reevaluated' as <IntelReevaluated>
    And the client provides 'New Evaludation Code' as <NewEvaludationCode>
    And the client provides 'Reviewed By Employee Iteration Id' as '40920'
    And the client provides 'Reviewed Date Time' as <reviewedDateTime>
    And the client provides 'Next Review Date' as <NextReviewDate>
    And the client provides 'Next Reviewed By Employee Iteration Id' as <EmpReviewer>
    And the client provides 'Next Reviewed By Unit Actor' as <UnitReviewer>
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to 'REQUIRES LINKING'
    And a new Task History Entry will be added to the Intelligence report
    And the Task History Entry will record 'Confirm' as 'true'
    And the Task History Entry will record 'Intel Reevaluated' as <IntelReevaluated>
    And the Task History Entry will record 'New Handling Code' as <newHandlingCode>
    And the Task History Entry will record 'New Evaludation Code' as <NewEvaludationCode>
    And the Task History Entry will record 'Reviewed By Employee Iteration Id' as '40920'
    And the Task History Entry will record 'Reviewed Date Time' as <reviewedDateTime>
    And the Task History Entry will record 'Next Review Date' as <NextReviewDate>
    And the Task History Entry will record 'Next Reviewed By Employee Iteration Id' as <EmpReviewer>
    And the Task History Entry will record 'Next Reviewed By Unit Actor' as <UnitReviewer>

    Examples: 
      | IntelReevaluated | NewEvaludationCode | reviewedDateTime        | NextReviewDate | EmpReviewer | UnitReviewer | IntelReevaluated | NewEvaludationCode | reviewedDateTime        | NextReviewDate | EmpReviewer | UnitReviewer |
      | false            | C                  | 2018-09-26 09:20:31.735 | 2018-10-05     |       44570 |              | false            | C                  | 2018-09-26 09:20:31.735 | 2018-10-05     |       44570 |              |
      | false            | C                  | 2018-09-26 09:20:31.735 | 2018-10-05     |             | AMO          | false            | C                  | 2018-09-26 09:20:31.735 | 2018-10-05     |             | AMO          |
@draft
  Scenario Outline: Perform task 'Review Of Code C Performed' present in REQUIRES LINKING SENSITIVE status
    #BS-INCLUDE feature=CON-42900_PerformReviewOfCodeC.feature,scenario=Perform task 'Perform review of Code C' present in REQUIRES LINKING SENSITIVE status
    Given an Intelligence Report exists with status of 'REQUIRES LINKING SENSITIVE'
    And a task 'Review Of Code C Performed'  exists against that Intelligence Report
    #txData
    And the client provides 'Confirm' as 'true'
    And the client provides 'Intel Reevaluated' as <IntelReevaluated>
    And the client provides 'New Evaludation Code' as <NewEvaludationCode>
    And the client provides 'Reviewed By Employee Iteration Id' as '40920'
    And the client provides 'Reviewed Date Time' as <reviewedDateTime>
    And the client provides 'Next Review Date' as <NextReviewDate>
    And the client provides 'Next Reviewed By Employee Iteration Id' as <EmpReviewer>
    And the client provides 'Next Reviewed By Unit Actor' as <UnitReviewer>
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to 'REQUIRES LINKING SENSITIVE'
    And a new Task History Entry will be added to the Intelligence report
    And the Task History Entry will record 'Confirm' as 'true'
    And the Task History Entry will record 'Intel Reevaluated' as <IntelReevaluated>
    And the Task History Entry will record 'New Handling Code' as <newHandlingCode>
    And the Task History Entry will record 'New Evaludation Code' as <NewEvaludationCode>
    And the Task History Entry will record 'Reviewed By Employee Iteration Id' as '40920'
    And the Task History Entry will record 'Reviewed Date Time' as <reviewedDateTime>
    And the Task History Entry will record 'Next Review Date' as <NextReviewDate>
    And the Task History Entry will record 'Next Reviewed By Employee Iteration Id' as <EmpReviewer>
    And the Task History Entry will record 'Next Reviewed By Unit Actor' as <UnitReviewer>

    Examples: 
      | IntelReevaluated | NewEvaludationCode | reviewedDateTime        | NextReviewDate | EmpReviewer | UnitReviewer | IntelReevaluated | NewEvaludationCode | reviewedDateTime        | NextReviewDate | EmpReviewer | UnitReviewer |
      | false            | C                  | 2018-09-26 09:20:31.735 | 2018-10-05     |       44570 |              | false            | C                  | 2018-09-26 09:20:31.735 | 2018-10-05     |       44570 |              |
      | false            | C                  | 2018-09-26 09:20:31.735 | 2018-10-05     |             | AMO          | false            | C                  | 2018-09-26 09:20:31.735 | 2018-10-05     |             | AMO          |

@draft
  Scenario Outline: Perform task 'Review Of Code C Performed' present in LIVE status
    #BS-INCLUDE feature=CON-42900_PerformReviewOfCodeC.feature,scenario=Perform task 'Perform review of Code C' present in LIVE status
    Given an Intelligence Report exists with status of <InStatus>
    And a task 'Review Of Code C Performed'  exists against that Intelligence Report
    #txData
    And the client provides 'Confirm' as 'true'
    And the client provides 'Intel Reevaluated' as <IntelReevaluated>
    And the client provides 'New Evaludation Code' as <NewEvaludationCode>
    And the client provides 'Reviewed By Employee Iteration Id' as '40920'
    And the client provides 'Reviewed Date Time' as <reviewedDateTime>
    And the client provides 'Next Review Date' as <NextReviewDate>
    And the client provides 'Next Reviewed By Employee Iteration Id' as <EmpReviewer>
    And the client provides 'Next Reviewed By Unit Actor' as <UnitReviewer>
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to <outStatus>
    And a new Task History Entry will be added to the Intelligence report
    And the Task History Entry will record 'Confirm' as 'true'
    And the Task History Entry will record 'Intel Reevaluated' as <IntelReevaluated>
    And the Task History Entry will record 'New Handling Code' as <newHandlingCode>
    And the Task History Entry will record 'New Evaludation Code' as <NewEvaludationCode>
    And the Task History Entry will record 'Reviewed By Employee Iteration Id' as '40920'
    And the Task History Entry will record 'Reviewed Date Time' as <reviewedDateTime>
    And the Task History Entry will record 'Next Review Date' as <NextReviewDate>
    And the Task History Entry will record 'Next Reviewed By Employee Iteration Id' as <EmpReviewer>
    And the Task History Entry will record 'Next Reviewed By Unit Actor' as <UnitReviewer>

    Examples: 
      | IntelReevaluated | NewEvaludationCode | reviewedDateTime        | NextReviewDate | EmpReviewer | UnitReviewer | IntelReevaluated | NewEvaludationCode | reviewedDateTime        | NextReviewDate | EmpReviewer | UnitReviewer |
      | false            | C                  | 2018-09-26 09:20:31.735 | 2018-10-05     |       44570 |              | false            | C                  | 2018-09-26 09:20:31.735 | 2018-10-05     |       44570 |              |
      | false            | C                  | 2018-09-26 09:20:31.735 | 2018-10-05     |             | AMO          | false            | C                  | 2018-09-26 09:20:31.735 | 2018-10-05     |             | AMO          |

@draft
  Scenario Outline: Perform task 'Review Of Code C Performed' present in LIVE SENSITIVE status
    #BS-INCLUDE feature=CON-42900_PerformReviewOfCodeC.feature,scenario=Perform task 'Perform review of Code C' present in LIVE SENSITIVE status
    Given an Intelligence Report exists with status of 'LIVE SENSITIVE'
    And a task 'Review Of Code C Performed'  exists against that Intelligence Report
    #txData
    And the client provides 'Confirm' as 'true'
    And the client provides 'Intel Reevaluated' as <IntelReevaluated>
    And the client provides 'New Evaludation Code' as <NewEvaludationCode>
    And the client provides 'Reviewed By Employee Iteration Id' as '40920'
    And the client provides 'Reviewed Date Time' as <reviewedDateTime>
    And the client provides 'Next Review Date' as <NextReviewDate>
    And the client provides 'Next Reviewed By Employee Iteration Id' as <EmpReviewer>
    And the client provides 'Next Reviewed By Unit Actor' as <UnitReviewer>
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to 'LIVE SENSITIVE'
    And a new Task History Entry will be added to the Intelligence report
    And the Task History Entry will record 'Confirm' as 'true'
    And the Task History Entry will record 'Intel Reevaluated' as <IntelReevaluated>
    And the Task History Entry will record 'New Handling Code' as <newHandlingCode>
    And the Task History Entry will record 'New Evaludation Code' as <NewEvaludationCode>
    And the Task History Entry will record 'Reviewed By Employee Iteration Id' as '40920'
    And the Task History Entry will record 'Reviewed Date Time' as <reviewedDateTime>
    And the Task History Entry will record 'Next Review Date' as <NextReviewDate>
    And the Task History Entry will record 'Next Reviewed By Employee Iteration Id' as <EmpReviewer>
    And the Task History Entry will record 'Next Reviewed By Unit Actor' as <UnitReviewer>

    Examples: 
      | IntelReevaluated | NewEvaludationCode | reviewedDateTime        | NextReviewDate | EmpReviewer | UnitReviewer | IntelReevaluated | NewEvaludationCode | reviewedDateTime        | NextReviewDate | EmpReviewer | UnitReviewer |
      | false            | C                  | 2018-09-26 09:20:31.735 | 2018-10-05     |       44570 |              | false            | C                  | 2018-09-26 09:20:31.735 | 2018-10-05     |       44570 |              |
      | false            | C                  | 2018-09-26 09:20:31.735 | 2018-10-05     |             | AMO          | false            | C                  | 2018-09-26 09:20:31.735 | 2018-10-05     |             | AMO          |

@draft
  Scenario Outline: Perform task 'Review Of Code C Performed' for flagged Intel report present in REQUIRES LINKING status
    #BS-INCLUDE feature=CON-42900_PerformReviewOfCodeC.feature,scenario=Perform task 'Perform review of Code C' for flagged Intel report present in REQUIRES LINKING status
    Given an Intelligence Report exists with status of 'REQUIRES LINKING'
    And a task 'Review Of Code C Performed'  exists against that Intelligence Report
    #txData
    And the client provides 'Confirm' as 'true'
    And the client provides 'Intel Reevaluated' as <IntelReevaluated>
    And the client provides 'New Evaludation Code' as <NewEvaludationCode>
    And the client provides 'Reviewed By Employee Iteration Id' as '40920'
    And the client provides 'Reviewed Date Time' as <reviewedDateTime>
    And the client provides 'To Be Removed' as <FlagRemovalApproval>
    And the client provides 'Next Review Date' as <NextReviewDate>
    And the client provides 'Next Reviewed By Employee Iteration Id' as <EmpReviewer>
    And the client provides 'Next Reviewed By Unit Actor' as <UnitReviewer>
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to 'REQUIRES LINKING'
    And a new Task History Entry will be added to the Intelligence report
    And the Task History Entry will record 'Confirm' as 'true'
    And the Task History Entry will record 'Intel Reevaluated' as <IntelReevaluated>
    And the Task History Entry will record 'New Evaludation Code' as <NewEvaludationCode>
    And the Task History Entry will record 'Reviewed By Employee Iteration Id' as '40920'
    And the Task History Entry will record 'Reviewed Date Time' as <reviewedDateTime>
    And the Task History Entry will record 'To Be Removed' as <FlagRemovalApproval>
    And the Task History Entry will record 'Next Review Date' as <NextReviewDate>
    And the Task History Entry will record 'Next Reviewed By Employee Iteration Id' as <EmpReviewer>
    And the Task History Entry will record 'Next Reviewed By Unit Actor' as <UnitReviewer>
    And All the Secure/Closed Flags added on Intel report get expired

    Examples: 
      | IntelReevaluated | NewEvaludationCode | reviewedDateTime        | FlagRemovalApproval | NextReviewDate | EmpReviewer | UnitReviewer | IntelReevaluated | NewEvaludationCode | reviewedDateTime        | FlagRemovalApproval | NextReviewDate | EmpReviewer | UnitReviewer |
      | false            | C                  | 2018-09-26 09:20:31.735 | true                | 2018-10-05     |       44570 |              | false            | C                  | 2018-09-26 09:20:31.735 | true                | 2018-10-05     |       44570 |              |
      | false            | P                  | 2018-09-26 09:20:31.735 | false               |                |             |              | false            | P                  | 2018-09-26 09:20:31.735 | false               |                |             |              |

@draft
  Scenario Outline: Perform task 'Review Of Code C Performed' for flagged Intel report present in REQUIRES LINKING SENSITIVE status
    #BS-INCLUDE feature=CON-42900_PerformReviewOfCodeC.feature,scenario=Perform task 'Perform review of Code C' for flagged Intel report present in REQUIRES LINKING SENSITIVE status
    Given an Intelligence Report exists with status of 'REQUIRES LINKING SENSITIVE'
    And a task 'Review Of Code C Performed'  exists against that Intelligence Report
    #txData
    And the client provides 'Confirm' as 'true'
    And the client provides 'Intel Reevaluated' as <IntelReevaluated>
    And the client provides 'New Evaludation Code' as <NewEvaludationCode>
    And the client provides 'Reviewed By Employee Iteration Id' as '40920'
    And the client provides 'Reviewed Date Time' as <reviewedDateTime>
    And the client provides 'To Be Removed' as <FlagRemovalApproval>
    And the client provides 'Next Review Date' as <NextReviewDate>
    And the client provides 'Next Reviewed By Employee Iteration Id' as <EmpReviewer>
    And the client provides 'Next Reviewed By Unit Actor' as <UnitReviewer>
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to 'REQUIRES LINKING SENSITIVE'
    And a new Task History Entry will be added to the Intelligence report
    And the Task History Entry will record 'Confirm' as 'true'
    And the Task History Entry will record 'Intel Reevaluated' as <IntelReevaluated>
    And the Task History Entry will record 'New Evaludation Code' as <NewEvaludationCode>
    And the Task History Entry will record 'Reviewed By Employee Iteration Id' as '40920'
    And the Task History Entry will record 'Reviewed Date Time' as <reviewedDateTime>
    And the Task History Entry will record 'To Be Removed' as <FlagRemovalApproval>
    And the Task History Entry will record 'Next Review Date' as <NextReviewDate>
    And the Task History Entry will record 'Next Reviewed By Employee Iteration Id' as <EmpReviewer>
    And the Task History Entry will record 'Next Reviewed By Unit Actor' as <UnitReviewer>
    And All the Secure/Closed Flags added on Intel report get expired

    Examples: 
      | IntelReevaluated | NewEvaludationCode | reviewedDateTime        | FlagRemovalApproval | NextReviewDate | EmpReviewer | UnitReviewer | IntelReevaluated | NewEvaludationCode | reviewedDateTime        | FlagRemovalApproval | NextReviewDate | EmpReviewer | UnitReviewer |
      | false            | C                  | 2018-09-26 09:20:31.735 | true                | 2018-10-05     |       44570 |              | false            | C                  | 2018-09-26 09:20:31.735 | true                | 2018-10-05     |       44570 |              |
      | false            | P                  | 2018-09-26 09:20:31.735 | false               |                |             |              | false            | P                  | 2018-09-26 09:20:31.735 | false               |                |             |              |

@draft
  Scenario Outline: Perform task 'Review Of Code C Performed' for flagged Intel report present in LIVE status
    #BS-INCLUDE feature=CON-42900_PerformReviewOfCodeC.feature,scenario=Perform task 'Perform review of Code C' for flagged Intel report present in LIVE status
    Given an Intelligence Report exists with status of 'LIVE'
    And a task 'Review Of Code C Performed'  exists against that Intelligence Report
    #txData
    And the client provides 'Confirm' as 'true'
    And the client provides 'Intel Reevaluated' as <IntelReevaluated>
    And the client provides 'New Evaludation Code' as <NewEvaludationCode>
    And the client provides 'Reviewed By Employee Iteration Id' as '40920'
    And the client provides 'Reviewed Date Time' as <reviewedDateTime>
    And the client provides 'To Be Removed' as <FlagRemovalApproval>
    And the client provides 'Next Review Date' as <NextReviewDate>
    And the client provides 'Next Reviewed By Employee Iteration Id' as <EmpReviewer>
    And the client provides 'Next Reviewed By Unit Actor' as <UnitReviewer>
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to 'LIVE'
    And a new Task History Entry will be added to the Intelligence report
    And the Task History Entry will record 'Confirm' as 'true'
    And the Task History Entry will record 'Intel Reevaluated' as <IntelReevaluated>
    And the Task History Entry will record 'New Evaludation Code' as <NewEvaludationCode>
    And the Task History Entry will record 'Reviewed By Employee Iteration Id' as '40920'
    And the Task History Entry will record 'Reviewed Date Time' as <reviewedDateTime>
    And the Task History Entry will record 'To Be Removed' as <FlagRemovalApproval>
    And the Task History Entry will record 'Next Review Date' as <NextReviewDate>
    And the Task History Entry will record 'Next Reviewed By Employee Iteration Id' as <EmpReviewer>
    And the Task History Entry will record 'Next Reviewed By Unit Actor' as <UnitReviewer>
    And All the Secure/Closed Flags added on Intel report get expired

    Examples: 
      | IntelReevaluated | NewEvaludationCode | reviewedDateTime        | FlagRemovalApproval | NextReviewDate | EmpReviewer | UnitReviewer | IntelReevaluated | NewEvaludationCode | reviewedDateTime        | FlagRemovalApproval | NextReviewDate | EmpReviewer | UnitReviewer |
      | false            | C                  | 2018-09-26 09:20:31.735 | true                | 2018-10-05     |       44570 |              | false            | C                  | 2018-09-26 09:20:31.735 | true                | 2018-10-05     |       44570 |              |
      | false            | P                  | 2018-09-26 09:20:31.735 | false               |                |             |              | false            | P                  | 2018-09-26 09:20:31.735 | false               |                |             |              |

@draft
  Scenario Outline: Perform task 'Review Of Code C Performed' for flagged Intel report present in LIVE SENSITIVE status
    #BS-INCLUDE feature=CON-42900_PerformReviewOfCodeC.feature,scenario=Perform task 'Perform review of Code C' for flagged Intel report present in LIVE SENSITIVE status
    Given an Intelligence Report exists with status of 'LIVE SENSITIVE'
    And a task 'Review Of Code C Performed'  exists against that Intelligence Report
    #txData
    And the client provides 'Confirm' as 'true'
    And the client provides 'Intel Reevaluated' as <IntelReevaluated>
    And the client provides 'New Evaludation Code' as <NewEvaludationCode>
    And the client provides 'Reviewed By Employee Iteration Id' as '40920'
    And the client provides 'Reviewed Date Time' as <reviewedDateTime>
    And the client provides 'To Be Removed' as <FlagRemovalApproval>
    And the client provides 'Next Review Date' as <NextReviewDate>
    And the client provides 'Next Reviewed By Employee Iteration Id' as <EmpReviewer>
    And the client provides 'Next Reviewed By Unit Actor' as <UnitReviewer>
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to 'LIVE SENSITIVE'
    And a new Task History Entry will be added to the Intelligence report
    And the Task History Entry will record 'Confirm' as 'true'
    And the Task History Entry will record 'Intel Reevaluated' as <IntelReevaluated>
    And the Task History Entry will record 'New Evaludation Code' as <NewEvaludationCode>
    And the Task History Entry will record 'Reviewed By Employee Iteration Id' as '40920'
    And the Task History Entry will record 'Reviewed Date Time' as <reviewedDateTime>
    And the Task History Entry will record 'To Be Removed' as <FlagRemovalApproval>
    And the Task History Entry will record 'Next Review Date' as <NextReviewDate>
    And the Task History Entry will record 'Next Reviewed By Employee Iteration Id' as <EmpReviewer>
    And the Task History Entry will record 'Next Reviewed By Unit Actor' as <UnitReviewer>
    And All the Secure/Closed Flags added on Intel report get expired

    Examples: 
      | IntelReevaluated | NewEvaludationCode | reviewedDateTime        | FlagRemovalApproval | NextReviewDate | EmpReviewer | UnitReviewer | IntelReevaluated | NewEvaludationCode | reviewedDateTime        | FlagRemovalApproval | NextReviewDate | EmpReviewer | UnitReviewer |
      | false            | C                  | 2018-09-26 09:20:31.735 | true                | 2018-10-05     |       44570 |              | false            | C                  | 2018-09-26 09:20:31.735 | true                | 2018-10-05     |       44570 |              |
      | false            | P                  | 2018-09-26 09:20:31.735 | false               |                |             |              | false            | P                  | 2018-09-26 09:20:31.735 | false               |                |             |              |
@draft
  Scenario: Perform task 'Review Of Code C Performed' for Intel report present in REQUIRES LINKING status without entering data in mandatory fields
    #BS-INCLUDE feature=CON-42900_PerformReviewOfCodeC.feature,scenario=Perform task 'Perform review of Code C' for flagged Intel report present in REQUIRES LINKING status
    Given an Intelligence Report exists with status of 'REQUIRES LINKING'
    And a task 'Review Of Code C Performed'  exists against that Intelligence Report
    #txData
    And the client provides 'Confirm' as 'true'
    When the client submits the Intelligence Report to Business Services
    Then Error will be returned for field 'Intel Reevaluated' with message 'intelReevaluated expected'
    Then Error will be returned for field 'New Evaludation Code' with message 'newEvaludationCode expected'
    Then Error will be returned for field 'Reviewed By Employee Iteration Id' with message 'reviewedByEmployeeIterationId expected'
    Then Error will be returned for field 'Reviewed Date Time' with message 'reviewedDateTime expected'
    Then Error will be returned for field 'To Be Removed' with message 'toBeRemoved expected'
@draft
  Scenario: Perform task 'Review Of Code C Performed' for Intel report present in REQUIRES LINKING SENSITIVE status without entering data in mandatory fields
    #BS-INCLUDE feature=CON-42900_PerformReviewOfCodeC.feature,scenario=Perform task 'Perform review of Code C' for flagged Intel report present in REQUIRES LINKING SENSITIVE status
    Given an Intelligence Report exists with status of 'REQUIRES LINKING SENSITIVE'
    And a task 'Review Of Code C Performed'  exists against that Intelligence Report
    #txData
    And the client provides 'Confirm' as 'true'
    When the client submits the Intelligence Report to Business Services
    Then Error will be returned for field 'Intel Reevaluated' with message 'intelReevaluated expected'
    Then Error will be returned for field 'New Evaludation Code' with message 'newEvaludationCode expected'
    Then Error will be returned for field 'Reviewed By Employee Iteration Id' with message 'reviewedByEmployeeIterationId expected'
    Then Error will be returned for field 'Reviewed Date Time' with message 'reviewedDateTime expected'
    Then Error will be returned for field 'To Be Removed' with message 'toBeRemoved expected'
@draft
  Scenario: Perform task 'Review Of Code C Performed' for Intel report present in LIVE status without entering data in mandatory fields
    #BS-INCLUDE feature=CON-42900_PerformReviewOfCodeC.feature,scenario=Perform task 'Perform review of Code C' for flagged Intel report present in LIVE status
    Given an Intelligence Report exists with status of 'LIVE'
    And a task 'Review Of Code C Performed'  exists against that Intelligence Report
    #txData
    And the client provides 'Confirm' as 'true'
    When the client submits the Intelligence Report to Business Services
    Then Error will be returned for field 'Intel Reevaluated' with message 'intelReevaluated expected'
    Then Error will be returned for field 'New Evaludation Code' with message 'newEvaludationCode expected'
    Then Error will be returned for field 'Reviewed By Employee Iteration Id' with message 'reviewedByEmployeeIterationId expected'
    Then Error will be returned for field 'Reviewed Date Time' with message 'reviewedDateTime expected'
    Then Error will be returned for field 'To Be Removed' with message 'toBeRemoved expected'
@draft
  Scenario: Perform task 'Review Of Code C Performed' for Intel report present in LIVE SENSITIVE status without entering data in mandatory fields
    #BS-INCLUDE feature=CON-42900_PerformReviewOfCodeC.feature,scenario=Perform task 'Perform review of Code C' for flagged Intel report present in LIVE SENSITIVE status
    Given an Intelligence Report exists with status of 'LIVE SENSITIVE'
    And a task 'Review Of Code C Performed'  exists against that Intelligence Report
    #txData
    And the client provides 'Confirm' as 'true'
    When the client submits the Intelligence Report to Business Services
    Then Error will be returned for field 'Intel Reevaluated' with message 'intelReevaluated expected'
    Then Error will be returned for field 'New Evaludation Code' with message 'newEvaludationCode expected'
    Then Error will be returned for field 'Reviewed By Employee Iteration Id' with message 'reviewedByEmployeeIterationId expected'
    Then Error will be returned for field 'Reviewed Date Time' with message 'reviewedDateTime expected'
    Then Error will be returned for field 'To Be Removed' with message 'toBeRemoved expected'
@draft
  Scenario Outline: Perform task 'Review Of Code C Performed' for Intel report present in REQUIRES LINKING status without entering data in mandatory fields
    #BS-INCLUDE feature=CON-42900_PerformReviewOfCodeC.feature,scenario=Perform task 'Perform review of Code C' for flagged Intel report present in REQUIRES LINKING status
    Given an Intelligence Report exists with status of REQUIRES LINKING
    And a task 'Review Of Code C Performed'  exists against that Intelligence Report
    #txData
    And the client provides 'Confirm' as 'true'
    And the client provides 'Intel Reevaluated' as <IntelReevaluated>
    And the client provides 'New Evaludation Code' as <NewEvaludationCode>
    And the client provides 'Reviewed By Employee Iteration Id' as '40920'
    And the client provides 'Reviewed Date Time' as <reviewedDateTime>
    And the client provides 'To Be Removed' as <FlagRemovalApproval>
    And the client provides 'Next Review Date' as <NextReviewDate>
    And the client provides 'Next Reviewed By Employee Iteration Id' as <EmpReviewer>
    And the client provides 'Next Reviewed By Unit Actor' as <UnitReviewer>
    When the client submits the Intelligence Report to Business Services
    Then Error will be returned for field 'NextReviewDate' with message 'nextReviewDate expected'
    Then Error will be returned for field 'nextReviewedByEmployeeIterationId' with message 'Either nextReviewedByEmployeeIterationId or nextReviewedByUnitActor must have a value'
    Then Error will be returned for field 'nextReviewedByUnitActor' with message 'Either nextReviewedByEmployeeIterationId or nextReviewedByUnitActor must have a value'

    Examples: 
      | IntelReevaluated | NewEvaludationCode | reviewedDateTime        | FlagRemovalApproval |
      | false            | C                  | 2018-09-26 09:20:31.735 | false               |
      | true             | C                  | 2018-09-26 09:20:31.735 | false               |
@draft
  Scenario Outline: Perform task 'Review Of Code C Performed' for Intel report present in REQUIRES LINKING SENSITIVE status without entering data in mandatory fields
    #BS-INCLUDE feature=CON-42900_PerformReviewOfCodeC.feature,scenario=Perform task 'Perform review of Code C' for flagged Intel report present in REQUIRES LINKING SENSITIVE status
    Given an Intelligence Report exists with status of 'REQUIRES LINKING SENSITIVE'
    And a task 'Review Of Code C Performed'  exists against that Intelligence Report
    #txData
    And the client provides 'Confirm' as 'true'
    And the client provides 'Intel Reevaluated' as <IntelReevaluated>
    And the client provides 'New Evaludation Code' as <NewEvaludationCode>
    And the client provides 'Reviewed By Employee Iteration Id' as '40920'
    And the client provides 'Reviewed Date Time' as <reviewedDateTime>
    And the client provides 'To Be Removed' as <FlagRemovalApproval>
    And the client provides 'Next Review Date' as <NextReviewDate>
    And the client provides 'Next Reviewed By Employee Iteration Id' as <EmpReviewer>
    And the client provides 'Next Reviewed By Unit Actor' as <UnitReviewer>
    When the client submits the Intelligence Report to Business Services
    Then Error will be returned for field 'NextReviewDate' with message 'nextReviewDate expected'
    Then Error will be returned for field 'nextReviewedByEmployeeIterationId' with message 'Either nextReviewedByEmployeeIterationId or nextReviewedByUnitActor must have a value'
    Then Error will be returned for field 'nextReviewedByUnitActor' with message 'Either nextReviewedByEmployeeIterationId or nextReviewedByUnitActor must have a value'

    Examples: 
      | IntelReevaluated | NewEvaludationCode | reviewedDateTime        | FlagRemovalApproval |
      | false            | C                  | 2018-09-26 09:20:31.735 | false               |
      | true             | C                  | 2018-09-26 09:20:31.735 | false               |
@draft
  Scenario Outline: Perform task 'Review Of Code C Performed' for Intel report present in LIVE status without entering data in mandatory fields
    #BS-INCLUDE feature=CON-42900_PerformReviewOfCodeC.feature,scenario=Perform task 'Perform review of Code C' for flagged Intel report present in LIVE status
    Given an Intelligence Report exists with status of LIVE
    And a task 'Review Of Code C Performed'  exists against that Intelligence Report
    #txData
    And the client provides 'Confirm' as 'true'
    And the client provides 'Intel Reevaluated' as <IntelReevaluated>
    And the client provides 'New Evaludation Code' as <NewEvaludationCode>
    And the client provides 'Reviewed By Employee Iteration Id' as '40920'
    And the client provides 'Reviewed Date Time' as <reviewedDateTime>
    And the client provides 'To Be Removed' as <FlagRemovalApproval>
    And the client provides 'Next Review Date' as <NextReviewDate>
    And the client provides 'Next Reviewed By Employee Iteration Id' as <EmpReviewer>
    And the client provides 'Next Reviewed By Unit Actor' as <UnitReviewer>
    When the client submits the Intelligence Report to Business Services
    Then Error will be returned for field 'NextReviewDate' with message 'nextReviewDate expected'
    Then Error will be returned for field 'nextReviewedByEmployeeIterationId' with message 'Either nextReviewedByEmployeeIterationId or nextReviewedByUnitActor must have a value'
    Then Error will be returned for field 'nextReviewedByUnitActor' with message 'Either nextReviewedByEmployeeIterationId or nextReviewedByUnitActor must have a value'

    Examples: 
      | IntelReevaluated | NewEvaludationCode | reviewedDateTime        | FlagRemovalApproval |
      | false            | C                  | 2018-09-26 09:20:31.735 | false               |
      | true             | C                  | 2018-09-26 09:20:31.735 | false               |
@draft
  Scenario Outline: Perform task 'Review Of Code C Performed' for Intel report present in LIVE SENSITIVE status without entering data in mandatory fields
    #BS-INCLUDE feature=CON-42900_PerformReviewOfCodeC.feature,scenario=Perform task 'Perform review of Code C' for flagged Intel report present in LIVE SENSITIVE status
    Given an Intelligence Report exists with status of 'LIVE SENSITIVE'
    And a task 'Review Of Code C Performed'  exists against that Intelligence Report
    #txData
    And the client provides 'Confirm' as 'true'
    And the client provides 'Intel Reevaluated' as <IntelReevaluated>
    And the client provides 'New Evaludation Code' as <NewEvaludationCode>
    And the client provides 'Reviewed By Employee Iteration Id' as '40920'
    And the client provides 'Reviewed Date Time' as <reviewedDateTime>
    And the client provides 'To Be Removed' as <FlagRemovalApproval>
    And the client provides 'Next Review Date' as <NextReviewDate>
    And the client provides 'Next Reviewed By Employee Iteration Id' as <EmpReviewer>
    And the client provides 'Next Reviewed By Unit Actor' as <UnitReviewer>
    When the client submits the Intelligence Report to Business Services
    Then Error will be returned for field 'NextReviewDate' with message 'nextReviewDate expected'
    Then Error will be returned for field 'nextReviewedByEmployeeIterationId' with message 'Either nextReviewedByEmployeeIterationId or nextReviewedByUnitActor must have a value'
    Then Error will be returned for field 'nextReviewedByUnitActor' with message 'Either nextReviewedByEmployeeIterationId or nextReviewedByUnitActor must have a value'

    Examples: 
      | IntelReevaluated | NewEvaludationCode | reviewedDateTime        | FlagRemovalApproval |
      | false            | C                  | 2018-09-26 09:20:31.735 | false               |
      | true             | C                  | 2018-09-26 09:20:31.735 | false               |
