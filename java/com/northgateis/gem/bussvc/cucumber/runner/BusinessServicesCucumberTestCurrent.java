package com.northgateis.gem.bussvc.cucumber.runner;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;

/**
 * Support for LOCAL running by testers.
 * Runs ACTUAL FULL TESTS marked as '@current'...
 * Doesn't care about '@draft'. Runs anyway.
 * @author Vikas.Jain
 */
@RunWith(BusinessServicesCucumberTestRunner.class)
@CucumberOptions(features = {
					"../src/test/resources/features/incident/ft", 
					"../src/test/resources/features/intelreport/ft"
				}, 
				glue = {"com.northgateis.gem.bussvc.poleobjects.functest"},
				strict = true,
				tags = {"@current"},
				plugin = {"pretty",
						 	"html:target/cucumber-reports/Cucumber", 
						 	"junit:target/cucumber-reports/new.xml"},
				monochrome=true)
public class BusinessServicesCucumberTestCurrent {

}
