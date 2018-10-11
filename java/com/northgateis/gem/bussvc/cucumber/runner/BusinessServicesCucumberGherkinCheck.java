package com.northgateis.gem.bussvc.cucumber.runner;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;

/**
 * USED BY HUDSON
 * 
 * Runs Gherkin check for all .feature files EXCEPT those marked as @draft
 * 
 * @author Vikas.Jain
 */
@RunWith(BusinessServicesCucumberTestRunner.class)
@CucumberOptions(features = {
					"../src/test/resources/features/incident/ft", 
					"../src/test/resources/features/intelreport/ft"
				}, 
				glue = {"com.northgateis.gem.bussvc.poleobjects.functest"},
				strict = true,
				tags ="~@draft",
				plugin = {"pretty",
						 	"html:target/cucumber-reports/Cucumber", 
						 	"junit:target/cucumber-reports/new.xml"},
				monochrome=true)
public class BusinessServicesCucumberGherkinCheck {

}
