    Feature: CON-42869 Perform task Disseminate Intelligence Report internally

    @draft
    Scenario Outline: Perform task Disseminate Intelligence Report internally for Requires Assessment/Assessment Sensitive status for all recipients
    #BS-INCLUDE feature=CON-42859_AssessIntelligenceReport_Accepted.feature, scenario=Happy Path with status of 'REQUIRES ASSESSMENT'
    Given an Intelligence Report exists with status of <inStatus>
    And the client runs the task <currentTaskName>
    #System Parameter check
    And system parameter INTEL_SENSITIVE_ACTIONCODE contains <sysParamValue>
    # Here we are providing Transient data for the task performed
    And the client provides 'Staff Member To Disseminate To List' as <Staff Member>
    And the client provides 'Roles To Disseminate To List' as <Roles>
    And the client provides 'Units To Disseminate To List' as <Units>
    And the client provides 'Dissemination Message Text' as <DText>
    And the client provides 'Hold dDissemination Until Pir Is Made Live' as <Flag>
    # Now the task will be performed
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to <outStatus>
    And a system message should be sent to all recipients as 'DText Message'
    #Task History details assertions
    And the Task History Entry will record 'Staff Member To Disseminate To List' as <Staff Member>
    And the Task History Entry will record 'Roles To Disseminate To List' as <Roles>
    And the Task History Entry will record 'Units To Disseminate To List' as <Units>
    And the Task History Entry will record 'Dissemination Message Text' as <DText>
    And the Task History Entry will record 'Hold dDissemination Until Pir Is Made Live' as <Flag>

    # Below are workflow parameters used and passed in above scripts
    Examples: 
      | instatus                      | currentTaskName                            | sysParamValue | Staff Member             | outstatus                     | Roles           | Units | DText                 | Flag  |
      | REQUIRES ASSESSMENT           | Disseminate intelligence report internally | A2            | Supt 42 ARM753 Deshpande | REQUIRES ASSESSMENT SENSITIVE | Arrest Inputter | AMO   | Dissemination message | False |
      | REQUIRES ASSESSMENT SENSITIVE | Disseminate intelligence report internally | A1            | Supt 42 ARM753 Deshpande | REQUIRES ASSESSMENT SENSITIVE | Arrest Inputter | AMO   | Dissemination message | False |

      @draft
  Scenario Outline: Perform task Disseminate Intelligence Report internally for Requires Linking/Linking Sensitive status for all recipients
   #BS-INCLUDE feature=CON-42859_AssessIntelligenceReport_Accepted.feature, scenario=Happy Path with status of 'REQUIRES ASSESSMENT'
    Given an Intelligence Report exists with status of <inStatus>
    And the client runs the task <currentTaskName>
    #System Parameter check
    And system parameter INTEL_SENSITIVE_ACTIONCODE contains <sysParamValue>
    # Here we are providing Transient data for the task performed
    And the client provides 'Staff Member To Disseminate To List' as <Staff Member>
    And the client provides 'Roles To Disseminate To List' as <Roles>
    And the client provides 'Units To Disseminate To List' as <Units>
    And the client provides 'Dissemination Message Text' as <DText>
    And the client provides 'Hold dDissemination Until Pir Is Made Live' as <Flag>
    # Now the task will be performed
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to <outStatus>
    And a system message should be sent to all recipients as 'DText Message'
    #Task History details assertions
    And the Task History Entry will record 'Staff Member To Disseminate To List' as <Staff Member>
    And the Task History Entry will record 'Roles To Disseminate To List' as <Roles>
    And the Task History Entry will record 'Units To Disseminate To List' as <Units>
    And the Task History Entry will record 'Dissemination Message Text' as <DText>
    And the Task History Entry will record 'Hold dDissemination Until Pir Is Made Live' as <Flag>

    # Below are workflow parameters used and passed in above scripts
    Examples: 
      | instatus                   | currentTaskName                            | sysParamValue | Staff Member           | outstatus                  | Roles               | Units | DText                 | Flag  |
      | REQUIRES LINKING           | Disseminate intelligence report internally | A2            | Ch Insp 42 FE6948 Sane | REQUIRES LINKING SENSITIVE | Operational Officer | AMO   | Dissemination message | False |
      | REQUIRES LINKING SENSITIVE | Disseminate intelligence report internally | A1            | Ch Insp 42 FE6948 Sane | REQUIRES LINKING SENSITIVE | Operational Officer | AMO   | Dissemination message | False |

    @draft
  Scenario Outline: Perform task Disseminate Intelligence Report internally for Live/Live Sensitive status for all recipients
    #BS-INCLUDE feature=CON-42859_AssessIntelligenceReport_Accepted.feature, scenario=Happy Path with status of 'REQUIRES ASSESSMENT'
    Given an Intelligence Report exists with status of <inStatus>
    And the client runs the task <currentTaskName>
    #System Parameter check
    And system parameter INTEL_SENSITIVE_ACTIONCODE contains <sysParamValue>
    # Here we are providing Transient data for the task performed
    And the client provides 'Staff Member To Disseminate To List' as <Staff Member>
    And the client provides 'Roles To Disseminate To List' as <Roles>
    And the client provides 'Units To Disseminate To List' as <Units>
    And the client provides 'Dissemination Message Text' as <DText>
    And the client provides 'Hold dDissemination Until Pir Is Made Live' as <Flag>
    # Now the task will be performed
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to <outStatus>
    And a system message should be sent to all recipients as 'DText Message'
    #Task History details assertions
    And the Task History Entry will record 'Staff Member To Disseminate To List' as <Staff Member>
    And the Task History Entry will record 'Roles To Disseminate To List' as <Roles>
    And the Task History Entry will record 'Units To Disseminate To List' as <Units>
    And the Task History Entry will record 'Dissemination Message Text' as <DText>
    And the Task History Entry will record 'Hold dDissemination Until Pir Is Made Live' as <Flag>

    # Below are workflow parameters used and passed in above scripts
    Examples: 
      | instatus       | currentTaskName                            | sysParamValue | Staff Member           | outstatus      | Roles               | Units | DText                 | Flag  |
      | LIVE           | Disseminate intelligence report internally | A2            | Ch Insp 42 FE6948 Sane | LIVE SENSITIVE | Operational Officer | AMO   | Dissemination message | False |
      | LIVE SENSITIVE | Disseminate intelligence report internally | A1            | Ch Insp 42 FE6948 Sane | LIVE SENSITIVE | Operational Officer | AMO   | Dissemination message | False |

@draft
  Scenario Outline: Perform task Disseminate Intelligence Report internally for Requires Assessment/Assessment Sensitive status without passing any recipients
    #BS-INCLUDE feature=CON-42850_CreateIntelligenceReport.feature, scenario=Happy Path for Create Intelligence Report In Require Assessment status
    Given an Intelligence Report exists with status of <inStatus>
    And the client runs the task <currentTaskName>
    # Here we are providing Transient data for the task performed
    And the client provides 'Staff Member To Disseminate To List' as <Staff Member>
    And the client provides 'Roles To Disseminate To List' as <Roles>
    And the client provides 'Units To Disseminate To List' as <Units>
    And the client provides 'Dissemination Message Text' as <DText>
    And the client provides 'Hold dDissemination Until Pir Is Made Live' as <Flag>
    # Now the task will be performed
    When the client submits the Intelligence Report to Business Services
    And Error will be returned containing 'You Must Specify at Least One Recipient'

    # Below are workflow parameters used and passed in above scripts
    Examples: 
      | instatus            | currentTaskName                            | sysParamValue | Staff Member             | outstatus                     | Roles | Units | DText | Flag  |
      | REQUIRES ASSESSMENT | Disseminate intelligence report internally | A2            | Supt 42 ARM753 Deshpande | REQUIRES ASSESSMENT SENSITIVE |       |       |       | False |

    @draft
  Scenario Outline: Perform task Disseminate Intelligence Report internally for Requires Assessment/Assessment Sensitive status for multiple Staff Member/Roles/Units
    #BS-INCLUDE feature=CON-42850_CreateIntelligenceReport.feature, scenario=Happy Path for Create Intelligence Report In Require Assessment status
    Given an Intelligence Report exists with status of <inStatus>
    And the client runs the task <currentTaskName>
    #System Parameter check
    And system parameter INTEL_SENSITIVE_ACTIONCODE contains <sysParamValue>
    # Here we are providing Transient data for the task performed
    And the client provides 'Staff Member To Disseminate To List' as <Staff Member1>
    And the client provides 'Staff Member To Disseminate To List' as <Staff Member2>
    And the client provides 'Roles To Disseminate To List' as <Roles1>
    And the client provides 'Roles To Disseminate To List' as <Roles2>
    And the client provides 'Units To Disseminate To List' as <Units1>
    And the client provides 'Units To Disseminate To List' as <Units2>
    And the client provides 'Dissemination Message Text' as <DText>
    And the client provides 'Hold dDissemination Until Pir Is Made Live' as <Flag>
    # Now the task will be performed
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to <outStatus>
    And a system message should be sent to all recipients as 'DText Message'
    #Task History details assertions
    And the Task History Entry will record 'Staff Member To Disseminate To List' as <Staff Member1>
    And the Task History Entry will record 'Staff Member To Disseminate To List' as <Staff Member2>
    And the Task History Entry will record 'Roles To Disseminate To List' as <Roles1>
    And the Task History Entry will record 'Roles To Disseminate To List' as <Roles2>
    And the Task History Entry will record 'Units To Disseminate To List' as <Units1>
    And the Task History Entry will record 'Units To Disseminate To List' as <Units2>
    And the Task History Entry will record 'Dissemination Message Text' as <DText>
    And the Task History Entry will record 'Hold dDissemination Until Pir Is Made Live' as <Flag>

    # Below are workflow parameters used and passed in above scripts
    Examples: 
      | instatus                      | currentTaskName                            | sysParamValue | Staff Member1            | outstatus                     | Roles1          | Units1 | DText                 | Flag  | Staff Member2          | Roles2             | Units2                    |
      | REQUIRES ASSESSMENT           | Disseminate intelligence report internally | A2            | Supt 42 ARM753 Deshpande | REQUIRES ASSESSMENT SENSITIVE | Arrest Inputter | AMO    | Dissemination message | False | Ch Insp 42 FE6948 Sane | Crime Investigator | Intelligence Bureau Essex |
      | REQUIRES ASSESSMENT SENSITIVE | Disseminate intelligence report internally | A1            | Supt 42 ARM753 Deshpande | REQUIRES ASSESSMENT SENSITIVE | Arrest Inputter | AMO    | Dissemination message | False | Ch Insp 42 FE6948 Sane | Crime Investigator | Intelligence Bureau Essex |

 @draft
  Scenario Outline: Perform task Disseminate Intelligence Report internally for Requires Assessment/Assessment Sensitive status with Hold Dissemination fLAg as True
   #BS-INCLUDE feature=CON-42859_AssessIntelligenceReport_Accepted.feature, scenario=Happy Path with status of 'REQUIRES ASSESSMENT'
    Given an Intelligence Report exists with status of <inStatus>
    And the client runs the task <currentTaskName>
    #System Parameter check
    And system parameter INTEL_SENSITIVE_ACTIONCODE contains <sysParamValue>
    # Here we are providing Transient data for the task performed
    And the client provides 'Staff Member To Disseminate To List' as <Staff Member>
    And the client provides 'Roles To Disseminate To List' as <Roles>
    And the client provides 'Units To Disseminate To List' as <Units>
    And the client provides 'Dissemination Message Text' as <DText>
    And the client provides 'Hold dDissemination Until Pir Is Made Live' as <Flag>
    # Now the task will be performed
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to <outStatus>
    #Task History details assertions
    And the Task History Entry will record 'Staff Member To Disseminate To List' as <Staff Member>
    And the Task History Entry will record 'Roles To Disseminate To List' as <Roles>
    And the Task History Entry will record 'Units To Disseminate To List' as <Units>
    And the Task History Entry will record 'Dissemination Message Text' as <DText>
    And the Task History Entry will record 'Hold dDissemination Until Pir Is Made Live' as <Flag>

    # Below are workflow parameters used and passed in above scripts
    Examples: 
      | instatus                   | currentTaskName                            | sysParamValue | Staff Member             | outstatus                     | Roles           | Units | DText                 | Flag |
      | REQUIRES LINKING           | Disseminate intelligence report internally | A2            | Supt 42 ARM753 Deshpande | REQUIRES ASSESSMENT SENSITIVE | Arrest Inputter | AMO   | Dissemination message | True |
      | REQUIRES LINKING SENSITIVE | Disseminate intelligence report internally | A1            | Supt 42 ARM753 Deshpande | REQUIRES ASSESSMENT SENSITIVE | Arrest Inputter | AMO   | Dissemination message | True |

    @draft
  Scenario Outline: Check above Intel when transitioned to Live Dissemination message is thrown
   #BS-INCLUDE feature=CON-42859_AssessIntelligenceReport_Accepted.feature, scenario=Happy Path with status of 'REQUIRES ASSESSMENT'
    Given an Intelligence Report exists with status of <inStatus>
    And the client runs the task <currentTaskName>
    #System Parameter check
    And system parameter INTEL_SENSITIVE_ACTIONCODE contains <sysParamValue>
    # Here we are providing Transient data for the task performed
    And the client provides 'Staff Member To Disseminate To List' as <Staff Member>
    And the client provides 'Roles To Disseminate To List' as <Roles>
    And the client provides 'Units To Disseminate To List' as <Units>
    And the client provides 'Dissemination Message Text' as <DText>
    And the client provides 'Hold dDissemination Until Pir Is Made Live' as <Flag>
    # Now the task will be performed
    When the client submits the Intelligence Report to Business Services
    Then the Intelligence Report will have its Status changed to <outStatus>
    And a system message should be sent to all recipients as 'DText Message'
    #Task History details assertions
    And the Task History Entry will record 'Staff Member To Disseminate To List' as <Staff Member>
    And the Task History Entry will record 'Roles To Disseminate To List' as <Roles>
    And the Task History Entry will record 'Units To Disseminate To List' as <Units>
    And the Task History Entry will record 'Dissemination Message Text' as <DText>
    And the Task History Entry will record 'Hold dDissemination Until Pir Is Made Live' as <Flag>

    # Below are workflow parameters used and passed in above scripts
    Examples: 
      | instatus       | currentTaskName                            | sysParamValue | Staff Member           | outstatus      | Roles               | Units | DText                 | Flag  |
      | LIVE           | Disseminate intelligence report internally | A2            | Ch Insp 42 FE6948 Sane | LIVE SENSITIVE | Operational Officer | AMO   | Dissemination message | False |
      | LIVE SENSITIVE | Disseminate intelligence report internally | A1            | Ch Insp 42 FE6948 Sane | LIVE SENSITIVE | Operational Officer | AMO   | Dissemination message | False |

@draft
  Scenario Outline: Perform task Disseminate Intelligence Report internally for Requires Assessment/Assessment Sensitive status for same Staff Member/Roles/Units repeated twice
    #BS-INCLUDE feature=CON-42850_CreateIntelligenceReport.feature, scenario=Happy Path for Create Intelligence Report In Require Assessment status
    Given an Intelligence Report exists with status of <inStatus>
    And the client runs the task <currentTaskName>
    # Here we are providing Transient data for the task performed
    And the client provides 'Staff Member To Disseminate To List' as <Staff Member1>
    And the client provides 'Staff Member To Disseminate To List' as <Staff Member2>
    And the client provides 'Roles To Disseminate To List' as <Roles1>
    And the client provides 'Roles To Disseminate To List' as <Roles2>
    And the client provides 'Units To Disseminate To List' as <Units1>
    And the client provides 'Units To Disseminate To List' as <Units2>
    And the client provides 'Dissemination Message Text' as <DText>
    And the client provides 'Hold dDissemination Until Pir Is Made Live' as <Flag>
    # Now the task will be performed
    When the client submits the Intelligence Report to Business Services
    Then Error will be returned containing 'You have already selected this recipient'

    # Below are workflow parameters used and passed in above scripts
    Examples: 
      | instatus            | currentTaskName                            | Staff Member1            | Roles1          | Units1 | DText                 | Flag  | Staff Member2            | Roles2          | Units2 |
      | REQUIRES ASSESSMENT | Disseminate intelligence report internally | Supt 42 ARM753 Deshpande | Arrest Inputter | AMO    | Dissemination message | False | Supt 42 ARM753 Deshpande | Arrest Inputter | AMO    |

  @draft
  Scenario Outline: Perform task Disseminate Intelligence Report internally for Requires Assessment/Assessment Sensitive status for blank 'Dissemination Message Text' and 'Hold dDissemination Until Pir Is Made Live'
    #BS-INCLUDE feature=CON-42850_CreateIntelligenceReport.feature, scenario=Happy Path for Create Intelligence Report In Require Assessment status
    Given an Intelligence Report exists with status of <inStatus>
    And the client runs the task <currentTaskName>
    # Here we are providing Transient data for the task performed
    And the client provides 'Staff Member To Disseminate To List' as <Staff Member1>
    And the client provides 'Staff Member To Disseminate To List' as <Staff Member2>
    And the client provides 'Roles To Disseminate To List' as <Roles1>
    And the client provides 'Roles To Disseminate To List' as <Roles2>
    And the client provides 'Units To Disseminate To List' as <Units1>
    And the client provides 'Units To Disseminate To List' as <Units2>
    And the client provides 'Dissemination Message Text' as <DText>
    And the client provides 'Hold dDissemination Until Pir Is Made Live' as <Flag>
    # Now the task will be performed
    When the client submits the Intelligence Report to Business Services
    Then Error will be returned for field 'Dissemination Message Text' with message 'Dissemination Message Text should not be blank'
    Then Error will be returned for field 'Hold dDissemination Until Pir Is Made Live' with message 'Hold dDissemination Until Pir Is Made Live should not be blank'

    # Below are workflow parameters used and passed in above scripts
    Examples: 
      | instatus            | currentTaskName                            | Staff Member1            | Roles1          | Units1 | DText | Flag | Staff Member2            | Roles2          | Units2 |
      | REQUIRES ASSESSMENT | Disseminate intelligence report internally | Supt 42 ARM753 Deshpande | Arrest Inputter | AMO    |       |      | Supt 42 ARM753 Deshpande | Arrest Inputter | AMO    |
