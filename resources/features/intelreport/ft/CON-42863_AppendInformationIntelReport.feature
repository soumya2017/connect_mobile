  Feature: CON-42863 Perform task Append Information to amend the submission text
 

  Scenario: Perform task Append Information with Submission text supplied for all statuses including Sensitive with Log of Enquiries
  #BS-INCLUDE feature=CON-42859_AssessIntelligenceReport_Accepted.feature,scenario=Happy Path with status of 'REQUIRES ASSESSMENT'
    Given an Intelligence Report exists with status of 'REQUIRES LINKING'
    And the client runs the task 'Append Information'
    # Adding Log of Entries Details
    And the client provides a Report Log Entry
    And the client provides 'Entry Index' as '2'
    And the client provides 'Entry Text' as 'Intel Logs'
    And the client provides 'Entry Date' as 'today'
    And the client provides 'Entry Time' as '09:20:31.735'
    And the client provides 'Entered By Officer Id' as '20'
    And the client provides 'Entry Type' as 'PNC'
    #System Parameter is set
    And system parameter INTEL_SENSITIVE_ACTIONCODE contains 'A2'
    # Here we are submitting Submission Text field
    And the client provides 'Submission Text' as 'Submission text field'
    # Now the task will be performed
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to 'REQUIRES LINKING'
    #Task History detail assertions
    And a new Task History Entry will be added to the 'Intelligence Report'
    #CON-42863: No output parameter for this task. So need to check 'Submission Text' in taskHistory.
    #Text Format would be (repSanTxtDto.getText() + "\n --------------------- Appended information --------------------- \n" + "Submission text field") 
    And 'Intelligence Report' contains '1' child 'Sanitised Text'
    And 'Sanitised Text' contains 'Text' as 'sanitisetext\n --------------------- Appended information --------------------- \nSubmission text field'
    
    
    
   Scenario: Perform task Append Information with Submission text supplied for all statuses for second level check
   #BS-INCLUDE feature=CON-42863_AppendInformationIntelReport.feature,scenario=Perform task Append Information with Submission text supplied for all statuses including Sensitive with Log of Enquiries
    Given an Intelligence Report exists with status of 'REQUIRES LINKING'
    And the client runs the task 'Append Information'
    # Adding Log of Entries Details
    And the client provides a Report Log Entry
    And the client provides 'Entry Index' as '2'
    And the client provides 'Entry Text' as 'Intel Logs'
    And the client provides 'Entry Date' as 'today'
    And the client provides 'Entry Time' as '09:20:31.735'
    And the client provides 'Entered By Officer Id' as '20'
    And the client provides 'Entry Type' as 'PNC'
    #System Parameter is set
    And system parameter INTEL_SENSITIVE_ACTIONCODE contains 'A2'
    # Here we are submitting Submission Text field
    And the client provides 'Submission Text' as 'Third Assertion'
    # Now the task will be performed
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to 'REQUIRES LINKING'
    #Task History detail assertions
    And a new Task History Entry will be added to the 'Intelligence Report'
    #CON-42863: No output parameter for this task. So need to check 'Submission Text' in taskHistory.
    #Text Format would be (repSanTxtDto.getText() + "\n --------------------- Appended information --------------------- \n" + "Submission text field") 
    And 'Intelligence Report' contains '1' child 'Sanitised Text'
    And 'Sanitised Text' contains 'Text' as 'sanitisetext\n --------------------- Appended information --------------------- \nSubmission text field\n --------------------- Appended information --------------------- \nThird Assertion'
    

   Scenario: Perform task Append Information with Submission text for max field validation more than 2000 characters
   #BS-INCLUDE feature=CON-42859_AssessIntelligenceReport_Accepted.feature,scenario=Happy Path with status of 'REQUIRES ASSESSMENT'
    Given an Intelligence Report exists with status of 'REQUIRES LINKING'
    And the client runs the task 'Append Information'
    # Adding Log of Entries Details
    And the client provides a Report Log Entry
    And the client provides 'Entry Index' as '2'
    And the client provides 'Entry Text' as 'Intel Logs'
    And the client provides 'Entry Date' as 'today'
    And the client provides 'Entry Time' as '09:20:31.735'
    And the client provides 'Entered By Officer Id' as '20'
    And the client provides 'Entry Type' as 'PNC'
    #System Parameter is set
    And system parameter INTEL_SENSITIVE_ACTIONCODE contains 'A2'
    # Here we are submitting Submission Text field
    And the client provides 'Submission Text' as 'XABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345ABCDE12345'
    # Now the task will be performed
    When the client submits the Intelligence Report to Business Services
    Then Error will be returned for field 'Submission Text' with message 'submissionText is too long'
 