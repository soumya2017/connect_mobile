Feature: CON-42869 - Ability to internally disseminate Intelligence reports

@draft
Scenario Outline: Perform task for 'Requires Assessment'/'Requires Assessment Sensitive' status for multiple Staff Member/Roles/Units
   #BS-INCLUDE feature=CON-42850_CreateIntelligenceReport.feature, scenario=Happy Path for Create Intelligence Report In Require Assessment status
   Given an Intelligence Report exists with status of <inStatus>
   And the client runs the task <currentTaskName>
   #System Parameter check
   And system parameter INTEL_SENSITIVE_ACTIONCODE contains <sysParamValue>
   
   # Here we are providing Transient data for the task performed
   # Provide 2x employee iterations
   And the client provides 'Disseminate Employee Iteration'
   And the client provides employeeIterationId as <Staff Member1>
   And the client provides 'Disseminate Employee Iteration'
   And the client provides employeeIterationId as <Staff Member2>
   
   # Provide 2x roles
   And the client provides 'Disseminate To Role'
   And the client provides roleCode as <Roles1>
   And the client provides 'Disseminate To Role'
   And the client provides roleCode as <Roles2>
   
   # Provide 2x units
   And the client provides 'Disseminate To Unit'
   And the client provides roleCode as <Units1>
   And the client provides 'Disseminate To UNit'
   And the client provides roleCode as <Units2>
   
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