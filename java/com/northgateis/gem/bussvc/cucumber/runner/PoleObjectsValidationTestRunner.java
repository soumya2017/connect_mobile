package com.northgateis.gem.bussvc.cucumber.runner;

import org.junit.Ignore;
import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

/**
 * This is the runner class for validation related cucumber based test cases.
 * 
 * @author harsh.shah/vikas.jain
 */
@Ignore
@RunWith(Cucumber.class)
@CucumberOptions(features = {"../src/test/resources/features/validation"}, 
				 glue = {"com.northgateis.gem.bussvc.poleobjects.cucumber.appliedlogic"}, 
				 strict = true)
public class PoleObjectsValidationTestRunner {
	//we can add tags as follows to run selected tests
	//, tags = "@PoleObjectsValidation,@CorrectIntelligenceReportTxData,@CompleteIntelligenceReportTxData"
}
