package com.northgateis.gem.bussvc.allocatekey.stopsearchperson.functest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.northgateis.gem.bussvc.api.jsonrest.AbstractFreestandingBusSvcFuncTestBase;
import com.northgateis.gem.bussvc.api.schema.AllocateKeysResponse;
import com.northgateis.gem.bussvc.ccis.CcisWebServiceStubRoute;
import com.northgateis.gem.bussvc.framework.camel.ErrorProcessor;
import com.northgateis.gem.bussvc.framework.error.RestAndSoapFaultErrorResponseFactory;
import com.northgateis.gem.bussvc.framework.exception.ValidationException;
import com.northgateis.gem.bussvc.framework.schema.BusinessServiceValidationError;
import com.northgateis.gem.bussvc.framework.schema.BusinessServiceValidationErrors;
import com.northgateis.gem.bussvc.framework.test.BusSvcStoryAcceptanceCriteriaReference;
import com.northgateis.pole.common.PoleNames;
/**
 * Test the rest service to generate unquie  numbers.
 * 
 * Key classes to observe that are ultimately tested by these FTs:
 * {@link AllocateKeyServiceRoute}
 * {@link SearchIdentiferHelperBean}
 * {@link CcisWebServiceStubRoute}
 * 
 * Key infrastructure tested by these FTs:
 * {@link RestAndSoapFaultErrorResponseFactory}
 * {@link ErrorProcessor}
 */
public class AllocateKeyServiceStopSearchPersonFuncTest extends AbstractFreestandingBusSvcFuncTestBase {
	
	private String FORCE_ID = "42";
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			jiraRef = "CCI-40493",
			mingleTitle = "Generate unique person identifier.", 
			acceptanceCriteriaRefs = "unknown", 
			given = "Child object searchOutOfForceOfficer is supplied and searchPersonInfo.searchPersonIdentifier is supplied",
 			when = "The request is sent to BS as as rest request", 
			then = "A  response back is sent to the client with the 1 new reference with unqiue value ")
	public void testPersonIdentiferHappyPathForOneKeyUsingRest() throws Exception {
		AllocateKeysResponse resp = businessServices.allocateKeys(FORCE_ID, PoleNames.SEARCH, 1, null, false);
		assertNotNull(resp);
		assertEquals("A single key should be allocated", 1, resp.getKeys().size());
	}
	
    @Test
	@BusSvcStoryAcceptanceCriteriaReference(
			jiraRef = "CCI-40493",
			mingleTitle = "Generate unique person identifier.", 
			acceptanceCriteriaRefs = "unknown", 
			given = "Child object searchOutOfForceOfficer is supplied and searchPersonInfo.searchPersonIdentifier is supplied", 
			when = "The request is sent to BS as a Soap Request ", 
			then = "A  response back is sent to the client with the 1 new reference with unqiue value ")
	public void testPersonIdentiferHappyPathForOneKeyUsingSoap() throws Exception {
		AllocateKeysResponse resp = businessServices.allocateKeys(FORCE_ID, PoleNames.SEARCH, 1, null, false);
		assertNotNull(resp);
		assertEquals("A single key should be allocated", 1, resp.getKeys().size());
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			jiraRef = "CCI-40493",
			mingleTitle = "Generate unique person identifier. ", 
			acceptanceCriteriaRefs = "unknown", 
			given = "Child object searchOutOfForceOfficer is supplied and searchPersonInfo.searchPersonIdentifier is supplied", 
			when = "The request is sent to BS", 
			then = "A  response back is sent to the client with multiple references with unqiue value ")
	public void testPersonIdentiferHappyPathForMultipleKeys() throws Exception {
		AllocateKeysResponse resp = businessServices.allocateKeys(FORCE_ID, PoleNames.SEARCH, 4, null, false);
		assertNotNull(resp);
		assertEquals("4 keys should be allocated", 4, resp.getKeys().size());
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			jiraRef = "CCI-40493",
			mingleTitle = "Generate unique person identifier. ", 
			acceptanceCriteriaRefs = "unknown", 
			given = "Child object searchOutOfForceOfficer is supplied and searchPersonInfo.searchPersonIdentifier is supplied", 
			when = "The request is sent to BS", 
			then = "An error is thrown mentioning -ve values are not allowed ")
	public void testPersonIdentiferNegativeKeys() throws Exception {
		try {
			businessServices.allocateKeys(FORCE_ID, PoleNames.SEARCH, -2, null, false);
			fail("Expected an exception for invalid data");
		} catch (ValidationException vde) {
			BusinessServiceValidationErrors bsve = new BusinessServiceValidationErrors();
			bsve = vde.getBusinessServiceValidationErrors();
			BusinessServiceValidationError bserr;
			bserr = bsve.getBusinessServiceValidationError().get(0);
			assertNotNull(bserr);
			assertTrue("ValidationException: " + bserr.getError(),
					bserr.getError().contains("The number of references is '-2'. It must be bigger than 0."));
		}
	}
}
