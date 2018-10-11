package com.northgateis.gem.bussvc.api.jsonrest;

import com.northgateis.gem.bussvc.AbstractBusinessServicesFunctionalTestBase;
import com.northgateis.gem.bussvc.framework.schema.BusinessServiceRequestInfo;

/**
 * Intermediate FT base-class for code that will test the non-POLE-based Business Services.
 * 
 * TODO CCI-2061 Make bsClient switchable in JSON REST vs XML SOAP modes. The same 
 * functionality should work in either mode...
 * 
 * @author dan.allford
 */
public abstract class AbstractFreestandingBusSvcFuncTestBase extends AbstractBusinessServicesFunctionalTestBase {

	/**
	 * Utility for adding header information for the tests. Only applicable to tests that extend this
	 * class. Could be factored-out into a utils/helper class in future.
	 * 
	 * @return
	 */
	protected BusinessServiceRequestInfo createBusinessServicesInfo() {

		BusinessServiceRequestInfo bsInfo = new BusinessServiceRequestInfo();
		bsInfo.setSecurityContextId(securityContextId);
		bsInfo.setClientName("BSFTs");
		return bsInfo;
	}
}
