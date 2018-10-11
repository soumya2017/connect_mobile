#Author: abhishek.mishra@northgateps.com
Feature: CON-42902 Perform task "Acknowledge Failure To Perform Review" in Intel report

  @draft
  Scenario: Perform task "Acknowledge Failure To Perform Review" for Intel Report present in REQUIRES LINKING status
    #BS-INCLUDE feature=CON-42859_AssessIntelligenceReport_Accepted.feature,scenario=Happy Path with status of 'REQUIRES ASSESSMENT'
    Given an Intelligence Report exists with status of 'REQUIRES LINKING'
    And a task 'Acknowledge Failure To Perform Review'  exists against that Intelligence Report
    #txData
    And the client provides 'Acknowledge' as 'true'
    And the client provides 'remarks' as 'This is remarks'
    When the client submits the Intelligence Report to Business Services
    Then a new Task History Entry will be added to the Intelligence report
    And the Task History Entry will record 'Acknowledge' as 'true'
    And the Task History Entry will record 'remarks' as 'This is remarks'

  @draft
  Scenario: Perform task "Acknowledge Failure To Perform Review" for Intel Report present in REQUIRES LINKING SENSITIVE status and do not supply Acknowledgement Remarks
    #BS-INCLUDE feature=CON-42859_AssessIntelligenceReport_Accepted.feature,scenario=Happy Path status of 'REQUIRES ASSESSMENT SENSITIVE'
    Given an Intelligence Report exists with status of 'REQUIRES LINKING SENSITIVE'
    And a task 'Acknowledge Failure To Perform Review'  exists against that Intelligence Report
    #txData
    And the client provides 'Acknowledge' as 'true'
    When the client submits the Intelligence Report to Business Services
    Then a new Task History Entry will be added to the Intelligence report
    And the Task History Entry will record 'Acknowledge' as 'true'

  @draft
  Scenario: Perform task "Acknowledge Failure To Perform Review" for Intel Report present in LIVE status
    Given an Intelligence Report exists with status of 'LIVE'
    And a task 'Acknowledge Failure To Perform Review'  exists against that Intelligence Report
    #txData
    And the client provides 'Acknowledge' as 'true'
    And the client provides 'remarks' as 'This is remarks'
    When the client submits the Intelligence Report to Business Services
    Then a new Task History Entry will be added to the Intelligence report
    And the Task History Entry will record 'Acknowledge' as 'true'
    And the Task History Entry will record 'remarks' as 'This is remarks'

  @draft
  Scenario: Perform task "Acknowledge Failure To Perform Review" for Intel Report present in LIVE SENSITIVE status
    Given an Intelligence Report exists with status of 'LIVE SENSITIVE'
    And a task 'Acknowledge Failure To Perform Review'  exists against that Intelligence Report
    #txData
    And the client provides 'Acknowledge' as 'true'
    And the client provides 'remarks' as 'This is remarks'
    When the client submits the Intelligence Report to Business Services
    Then a new Task History Entry will be added to the Intelligence report
    And the Task History Entry will record 'Acknowledge' as 'true'
    And the Task History Entry will record 'remarks' as 'This is remarks'

  @draft
  Scenario: Perform task "Acknowledge Failure To Perform Review" for Intel Report present in REQUIRES LINKING status without entering mandatory data
    #BS-INCLUDE feature=CON-42859_AssessIntelligenceReport_Accepted.feature,scenario=Happy Path with status of 'REQUIRES ASSESSMENT'
    Given an Intelligence Report exists with status of 'REQUIRES LINKING'
    And a task 'Acknowledge Failure To Perform Review'  exists against that Intelligence Report
    #txData
    And the client provides 'remarks' as 'This is remarks'
    # childObject name="reportLogEntry"
    And the client provides a 'Report Log Entry'
    And the client provides 'Entry Index' as '1'
    And the client provides 'Entry Date' as '2018-10-01'
    And the client provides 'Entry Time' as '09:20:31.735'
    And the client provides 'Entered By Officer Id' as '40920'
    When the client submits the Intelligence Report to Business Services
    Then Error will be returned for field 'Acknowledge' with message 'acknowledge expected'
    Then Error will be returned for field 'Entry Type' with message 'entryType expected'
    Then Error will be returned for field 'Entry Text' with message 'entryText expected'

  @draft
  Scenario: Perform task "Acknowledge Failure To Perform Review" for Intel Report present in REQUIRES LINKING SENSITIVE status without entering mandatory data
    #BS-INCLUDE feature=CON-42859_AssessIntelligenceReport_Accepted.feature,scenario=Happy Path status of 'REQUIRES ASSESSMENT SENSITIVE'
    Given an Intelligence Report exists with status of 'REQUIRES LINKING SENSITIVE'
    And a task 'Acknowledge Failure To Perform Review'  exists against that Intelligence Report
    #Do not supply any fields txData in this scenario
    # childObject name="reportLogEntry"
    And the client provides a 'Report Log Entry'
    And the client provides 'Entry Index' as '1'
    And the client provides 'Entry Date' as '2018-10-01'
    And the client provides 'Entry Time' as '09:20:31.735'
    And the client provides 'Entered By Officer Id' as '40920'
    When the client submits the Intelligence Report to Business Services
    Then Error will be returned for field 'Acknowledge' with message 'acknowledge expected'
    Then Error will be returned for field 'Entry Type' with message 'entryType expected'
    Then Error will be returned for field 'Entry Text' with message 'entryText expected'

  @draft
  Scenario: Perform task "Acknowledge Failure To Perform Review" for Intel Report present in LIVE status without entering mandatory data
    Given an Intelligence Report exists with status of 'LIVE'
    And a task 'Acknowledge Failure To Perform Review'  exists against that Intelligence Report
    #txData
    And the client provides 'remarks' as 'This is remarks'
    # childObject name="reportLogEntry"
    And the client provides a 'Report Log Entry'
    And the client provides 'Entry Index' as '1'
    And the client provides 'Entry Date' as '2018-10-01'
    And the client provides 'Entry Time' as '09:20:31.735'
    And the client provides 'Entered By Officer Id' as '40920'
    When the client submits the Intelligence Report to Business Services
    Then Error will be returned for field 'Acknowledge' with message 'acknowledge expected'
    Then Error will be returned for field 'Entry Type' with message 'entryType expected'
    Then Error will be returned for field 'Entry Text' with message 'entryText expected'

  @draft
  Scenario: Perform task "Acknowledge Failure To Perform Review" for Intel Report present in LIVE SENSITIVE status without entering mandatory data
    Given an Intelligence Report exists with status of 'LIVE SENSITIVE'
    And a task 'Acknowledge Failure To Perform Review'  exists against that Intelligence Report
    #txData
    And the client provides 'remarks' as 'This is remarks'
    # childObject name="reportLogEntry"
    And the client provides a 'Report Log Entry'
    And the client provides 'Entry Index' as '1'
    And the client provides 'Entry Date' as '2018-10-01'
    And the client provides 'Entry Time' as '09:20:31.735'
    And the client provides 'Entered By Officer Id' as '40920'
    When the client submits the Intelligence Report to Business Services
    Then Error will be returned for field 'Acknowledge' with message 'acknowledge expected'
    Then Error will be returned for field 'Entry Type' with message 'entryType expected'
    Then Error will be returned for field 'Entry Text' with message 'entryText expected'

  @draft
  Scenario: Add Enquiry Log while Performing task "Acknowledge Failure To Perform Review" for Intel Report present in REQUIRES LINKING status
    #BS-INCLUDE feature=CON-42859_AssessIntelligenceReport_Accepted.feature,scenario=Happy Path with status of 'REQUIRES ASSESSMENT'
    Given an Intelligence Report exists with status of 'REQUIRES LINKING'
    And a task 'Acknowledge Failure To Perform Review'  exists against that Intelligence Report
    #txData
    And the client provides 'Acknowledge' as 'true'
    And the client provides 'remarks' as 'This is remarks'
    When the client submits the Intelligence Report to Business Services
    # childObject name="reportLogEntry"
    And the client provides a 'Report Log Entry'
    And the client provides 'Entry Index' as '1'
    And the client provides 'Entry Date' as '2018-10-01'
    And the client provides 'Entry Time' as '09:20:31.735'
    And the client provides 'Entered By Officer Id' as '40920'
    And the client provides 'Entry Type' as 'RES'
    And the client provides 'Entry Text' as 'test for log entries'
    Then a new Task History Entry will be added to the Intelligence report
    And the Task History Entry will record 'Acknowledge' as 'true'
    And the Task History Entry will record 'remarks' as 'This is remarks'

  @draft
  Scenario: Add Enquiry Log while Performing task "Acknowledge Failure To Perform Review" for Intel Report present in REQUIRES LINKING SENSITIVE status
    #BS-INCLUDE feature=CON-42859_AssessIntelligenceReport_Accepted.feature,scenario=Happy Path status of 'REQUIRES ASSESSMENT SENSITIVE'
    Given an Intelligence Report exists with status of 'REQUIRES LINKING SENSITIVE'
    And a task 'Acknowledge Failure To Perform Review'  exists against that Intelligence Report
    #txData
    And the client provides 'Acknowledge' as 'true'
    And the client provides 'remarks' as 'This is remarks'
    # childObject name="reportLogEntry"
    And the client provides a 'Report Log Entry'
    And the client provides 'Entry Index' as '1'
    And the client provides 'Entry Date' as '2018-10-01'
    And the client provides 'Entry Time' as '09:20:31.735'
    And the client provides 'Entered By Officer Id' as '40920'
    And the client provides 'Entry Type' as 'RES'
    And the client provides 'Entry Text' as 'test for log entries'
    When the client submits the Intelligence Report to Business Services
    Then a new Task History Entry will be added to the Intelligence report
    And the Task History Entry will record 'Acknowledge' as 'true'
    And the Task History Entry will record 'remarks' as 'This is remarks'

  @draft
  Scenario: Add Enquiry Log while Performing task "Acknowledge Failure To Perform Review" for Intel Report present in LIVE status
    Given an Intelligence Report exists with status of 'LIVE'
    And a task 'Acknowledge Failure To Perform Review'  exists against that Intelligence Report
    #txData
    And the client provides 'Acknowledge' as 'true'
    And the client provides 'remarks' as 'This is remarks'
    # childObject name="reportLogEntry"
    And the client provides a 'Report Log Entry'
    And the client provides 'Entry Index' as '1'
    And the client provides 'Entry Date' as '2018-10-01'
    And the client provides 'Entry Time' as '09:20:31.735'
    And the client provides 'Entered By Officer Id' as '40920'
    And the client provides 'Entry Type' as 'RES'
    And the client provides 'Entry Text' as 'test for log entries'
    When the client submits the Intelligence Report to Business Services
    Then a new Task History Entry will be added to the Intelligence report
    And the Task History Entry will record 'Acknowledge' as 'true'
    And the Task History Entry will record 'remarks' as 'This is remarks'

  @draft
  Scenario: Add Enquiry Log while Performing task "Acknowledge Failure To Perform Review" for Intel Report present in LIVE SENSITIVE status
    Given an Intelligence Report exists with status of 'LIVE SENSITIVE'
    And a task 'Acknowledge Failure To Perform Review'  exists against that Intelligence Report
    #txData
    And the client provides 'Acknowledge' as 'true'
    And the client provides 'remarks' as 'This is remarks'
    # childObject name="reportLogEntry"
    And the client provides a 'Report Log Entry'
    And the client provides 'Entry Index' as '1'
    And the client provides 'Entry Date' as '2018-10-01'
    And the client provides 'Entry Time' as '09:20:31.735'
    And the client provides 'Entered By Officer Id' as '40920'
    And the client provides 'Entry Type' as 'RES'
    And the client provides 'Entry Text' as 'test for log entries'
    When the client submits the Intelligence Report to Business Services
    Then a new Task History Entry will be added to the Intelligence report
    And the Task History Entry will record 'Acknowledge' as 'true'
    And the Task History Entry will record 'remarks' as 'This is remarks'
