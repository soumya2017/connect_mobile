package com.northgateis.gem.bussvc.cucumber.runner;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;

/**
 * Support for LOCAL running by testers.
 * Synonymous with "logOnlyMode=true" in the launcher that accompanies this class.
 * 
 * Runs only scenarios marked as '@current'
 * Will always include .feature scenarios, even if marked '@draft'!!!!
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
				tags = "@current",
				plugin = {"pretty",
						 	"html:target/cucumber-reports/Cucumber", 
						 	"junit:target/cucumber-reports/new.xml"},
				monochrome=true)
public class BusinessServicesCucumberGherkinCheckCurrent {

}
