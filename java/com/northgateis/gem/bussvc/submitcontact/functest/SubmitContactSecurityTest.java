package com.northgateis.gem.bussvc.submitcontact.functest;

import java.util.Arrays;

import org.junit.Test;

/**
 * Test class for testing retrieval of security contexts from the security service 
 */
public class SubmitContactSecurityTest extends AbstractSubmitContactFuncTestBase {

	/**
	 * Test to retrieve a security context id for Frank Shunneltestone
	 */
	@Test
	public void testGetSecurityContextForFrankShunneltestone() throws Exception {
		utils.getSecurityContextId("Frank Shunneltestone", Arrays.asList("ATHENA_USER"), securityService);
	}
	
	/**
	 * Test to retrieve a security context id for Frank Shunneltesttwo
	 */
	@Test
	public void testGetSecurityContextForFrankShunneltesttwo() throws Exception {
	    utils.getSecurityContextId("Frank Shunneltesttwo", Arrays.asList("ATHENA_USER", "SysAdmin1"), securityService);
	}
}
