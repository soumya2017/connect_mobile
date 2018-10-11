package com.northgateis.gem.bussvc.submitincident.functest;

/**
 * Cheeky sub-class to allow testing the same set of tests as the base-class but
 * with the added 'salt' of the PERSON REPORTING and the VICTIM being the same Person. 
 * 
 * @author dan.allford
 */
public class CreateIncidentFromContactEventWithReporterAsVictimFuncTest 
	extends CreateIncidentFromContactEventFuncTest {

	@Override
	protected void setupImpl() throws Exception {
		super.setupImpl();
		
		super.reporterIsVictim = true;
	}
}
