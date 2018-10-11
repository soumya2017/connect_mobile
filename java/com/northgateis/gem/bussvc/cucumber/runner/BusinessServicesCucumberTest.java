package com.northgateis.gem.bussvc.cucumber.runner;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;

/**
 * This is the runner class for cucumber based test cases.
 * @author Vikas.Jain
 *
 */
@RunWith(BusinessServicesCucumberTestRunner.class)
@CucumberOptions(features = {
					"../src/test/resources/features/incident/ft", 
					"../src/test/resources/features/intelreport/ft"
				}, 
				glue = {"com.northgateis.gem.bussvc.poleobjects.functest"},
				strict = true,
				plugin = {"pretty", 
						 	"html:target/cucumber-reports/Cucumber", 
						 	"junit:target/cucumber-reports/new.xml"},
				monochrome=true)
public class BusinessServicesCucumberTest {

}
