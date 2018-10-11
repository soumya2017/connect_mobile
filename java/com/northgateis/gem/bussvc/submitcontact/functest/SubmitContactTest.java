package com.northgateis.gem.bussvc.submitcontact.functest;

import static com.northgateis.gem.bussvc.pole.utils.PoleDtoUtils.extractBusinessServiceResultInfo;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;

import org.junit.Test;

import com.northgateis.gem.bussvc.test.util.BusinessServicesTestUtils;
import com.northgateis.pole.common.InvalidSessionException;
import com.northgateis.pole.common.UnauthorisedAccessException;
import com.northgateis.pole.schema.PutPoleObjectsRequestDto;
import com.northgateis.pole.schema.PutPoleObjectsResponseDto;

/**
 * Functional Tests for the Submit Incident web-service that do not related to any particular Story. 
 */
public class SubmitContactTest extends AbstractSubmitContactFuncTestBase {	
	
	/**
	 * Test creation of an incident fails when an invalid security context id is supplied
	 */
	@Test
	public void testCreateContactEventInvalidSecurityContext() throws Exception {
		PutPoleObjectsRequestDto req = contils.createPutPoleObjectsRequest(
				BusinessServicesTestUtils.createBusinessServiceInfo("Invalid Security Context ID"),
				contils.createContactEventDto("BusSvcTest:" + new Date(), peAccountRef));
		
		try {
			poleBusinessServices.putPoleObjects(req);
			fail("Sending Invalid Security Context ID should have resulted in an InfrastructureException wrapping an AuthenticationException but didn't");
		} catch (InvalidSessionException ise) {
			//expected
		}
	}
	
	/**
	 * Test creation of an incident fails when the user doesn't have the correct security permit
	 */
	@Test
	public void testCreateContactEventMissingSecurityPermit() throws Exception {
		
		PutPoleObjectsRequestDto req = contils.createPutPoleObjectsRequest(
				BusinessServicesTestUtils.createBusinessServiceInfo(viewOnlySecurityContextId),
				contils.createContactEventDto("BusSvcTest:" + new Date(), peAccountRef));
		
		try {
			poleBusinessServices.putPoleObjects(req);	
			fail("Sending Invalid Security Context ID should have resulted in an UnauthorisedAccessException");
		} catch (UnauthorisedAccessException uae) {
			// we're expecting anInfrastructureException exception so this is the correct code path to take
			assertTrue("Exception message content: " + uae.getMessage(), 
					uae.getMessage().contains("Calling security context does not hold a security permit required for this operation."));								
		}
	}

	/**
	 * Test creation of incident does not fail when the submit incident request contains no victim
	 */
	@Test
	public void testCreateContactEventVictimIsOptional() {
		PutPoleObjectsRequestDto req = contils.createPutPoleObjectsRequest(
				BusinessServicesTestUtils.createBusinessServiceInfo(securityContextId),
				contils.createContactMissingVictim("BusSvcTest:" + new Date(), peAccountRef));
		
		PutPoleObjectsResponseDto res = poleBusinessServices.putPoleObjects(req);
		
		assertTrue(extractBusinessServiceResultInfo(res).isCompleted());		
	}
}
