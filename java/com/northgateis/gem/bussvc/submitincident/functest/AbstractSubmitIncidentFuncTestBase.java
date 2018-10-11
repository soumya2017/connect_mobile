package com.northgateis.gem.bussvc.submitincident.functest;

import java.util.Arrays;

import com.northgateis.gem.bussvc.AbstractBusinessServicesFunctionalTestBase;

/**
 * Common base-class for all Functional Tests targeted at the Submit Incident web-service.
 * 
 * @author vilin.patil & dan.allford
 */                                  
public abstract class AbstractSubmitIncidentFuncTestBase extends AbstractBusinessServicesFunctionalTestBase {		
	
	/**
	 * Overriding to provide more roles - needed for creating Incidents.
	 * 
	 * @throws Exception
	 */
	@Override
	protected void createMainSecurityContextId() throws Exception {
		if (securityContextId == null) {
			securityContextId = busSvcUtils.getSecurityContextId("Frank Shunneltestone", 
				Arrays.asList("ATHENA_USER", "CrimeIMUManager", "CrimeInvestigator", "CrimeSupervisor", 
					"NorthgateSystemAdmin", "SysAdmin1", "SysAdmin2"), securityService);
		}
	}
}