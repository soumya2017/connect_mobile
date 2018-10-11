@Generic
Feature: Perform task ‘Request Investigation Classification be Amended’

  Scenario Outline:  Perform task ‘Request Investigation Classification be Amended’ Happy Path
    Given an Investigation exists with status of <inStatus>
    #These first few lines might be better as background / external-to-story setup
    #By default match against external name (user readable) not internal name.
    And a task ‘Request Investigation Classification be Amended’ exists against that Investigation
    And the client is eligible to perform the task
    #This next step is important as it guides the initial ‘read’ from BS, which will return the current state of the task.
    #It should also be possible for our test code to look-up the eventAction based on the task name
    #(like the client would) to then be able to do a ‘getPoleObjects’ electing to perform that task
    And the client elects to perform the task
    #note: we could assume that the ‘transaction data’ is one of the default holding points for fields,
    #thus removing the need to say ‘in the transaction data’ every time.
    #The same could be used on the Investigation (Incident) itself, and we only need to care about which
    #record we want to set the field on, when the same field exists on both (which it rarely should)
    And the client provides primaryClassification as <primaryClassification> in the transaction data
    And the client provides includedClassification as <includedClassification> in the transaction data
    And the client provides remarks as <remarks> in the transaction data
    And the client provides fcr as <fcr> in the transaction data
    When the client submits the Investigation and transaction data to Business Services
    Then the Investigation will have its Status changed to <outStatus>
    And the task will be performed
    And a new Task History Entry will be added to the Investigation
    And the Task History Entry will record primaryClassification as <primaryClassification>
    And the Task History Entry will record includedClassification as <includedClassification>
    And the Task History Entry will record fcr as <fcr>
    And the Task History Entry will record remarks as <remarks>

    #Todo: nobody has thought of asserting the POLE audit log in story scope (transaction description etc).
    Examples: 
      | inStatus            | outStatus           | primaryClassification | includedClassification | fcr  | remarks                                   |
      | UNDER INVESTIGATION | UNDER INVESTIGATION | test                  | test                   | test | Testing for performing taks remarks value |
