package com.northgateis.gem.bussvc.allocatekey.functest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.northgateis.gem.bussvc.api.jsonrest.AbstractFreestandingBusSvcFuncTestBase;
import com.northgateis.gem.bussvc.api.schema.AllocateKeysResponse;
import com.northgateis.gem.bussvc.ccis.CcisWebServiceStubRoute;
import com.northgateis.gem.bussvc.framework.camel.ErrorProcessor;
import com.northgateis.gem.bussvc.framework.exception.ValidationException;
import com.northgateis.gem.bussvc.framework.test.BusSvcStoryAcceptanceCriteriaReference;
import com.northgateis.gem.bussvc.services.allocatekeys.AllocateKeyProxyBean;
import com.northgateis.gem.bussvc.services.allocatekeys.AllocateKeysServiceRoute;
import com.northgateis.gem.bussvc.services.common.error.BusinessServiceRestAndSoapFaultErrorResponseFactory;
import com.northgateis.pole.common.PoleNames;

/**
 * Test the rest service to generate reference numbers.
 * 
 * Key classes to observe that are ultimately tested by these FTs:
 * {@link AllocateKeysServiceRoute}
 * {@link AllocateKeyProxyBean}
 * {@link CcisWebServiceStubRoute}
 * 
 * Key infrastructure tested by these FTs:
 * {@link BusinessServiceRestAndSoapFaultErrorResponseFactory}
 * {@link ErrorProcessor}
 */
public class AllocateKeyServiceFuncTest extends AbstractFreestandingBusSvcFuncTestBase {
	
	private static final String FORCE_ID = "42";
	private static final String UNIT_CODE = "42 CJU COLCHESTER MANAGERS";

	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			jiraRef = "CCI-2061",
			mingleRef = 36753, 
			mingleTitle = "WP361 - Business Service - Issue Multiple Business References - (Phase 2) v1", 
			acceptanceCriteriaRefs = "CR1.1,CR1.2,CR2.0", 
			given = "A request to generate 1 reference numbers for a incident and the forceId 42", 
			when = "The request is sent to BS", 
			then = "A Json response back is sent to the client with the 1 new reference")
	public void testAllocateOneKeyJsonRest() throws Exception {
		AllocateKeysResponse resp = businessServices.allocateKeys(FORCE_ID, PoleNames.INCIDENT, 1, null ,false);
		assertNotNull(resp);
		assertEquals("A single key should be allocated", 1, resp.getKeys().size());
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			jiraRef = "CCI-2061",
			mingleRef = 36753, 
			mingleTitle = "WP361 - Business Service - Issue Multiple Business References - (Phase 2) v1", 
			acceptanceCriteriaRefs = "CR3.1", 
			given = "A request to generate 4 reference numbers for a incident and the forceId 42", 
			when = "The request is sent to BS", 
			then = "A Json response back is sent to the client with the new 4 references")
	public void testAllocateFourKeysJsonRest() throws Exception {

		AllocateKeysResponse resp = businessServices.allocateKeys(FORCE_ID, PoleNames.INCIDENT, 4, null ,false);
		assertEquals("Keys should be allocated according to numberOfReferences", 4, resp.getKeys().size());
	}

	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			jiraRef = "CCI-2061",
			mingleRef = 36753, 
			mingleTitle = "WP361 - Business Service - Issue Multiple Business References - (Phase 2) v1", 
			acceptanceCriteriaRefs = "CR4.1,CR4.2", 
			given = "A request to generate 0 reference numbers for a incident and the forceId 42", 
			when = "The request is sent to BS", 
			then = "A ValidationException should be returned as result, as it's not possible to generate 0 references.")
	public void testAllocateZeroKeyJsonRest() throws Exception {
		try {
			businessServices.allocateKeys(FORCE_ID, PoleNames.INCIDENT, 0, null, false);
			fail("Expected an exception");
		} catch (ValidationException ve) {
			if (!ValidationException.convertToXml(ve).contains("numberOfReferences")) {
				throw ve;
			}
		}
	}

	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			jiraRef = "CCI-2061",
			mingleRef = 36753, 
			mingleTitle = "WP361 - Business Service - Issue Multiple Business References - (Phase 2) v1", 
			acceptanceCriteriaRefs = "CR4.1,CR4.2", 
			given = "A request to generate -10 reference numbers for a incident and the forceId 42", 
			when = "The request is sent to BS", 
			then = "a ValidationException should be returned as result, as it's not possible to generate -10 references.")
	public void testAllocateMinusTenKeysJsonRest() throws Exception {
		try {
			businessServices.allocateKeys(FORCE_ID, PoleNames.INCIDENT, 0, null, false);
			fail("Expected an exception");
		} catch (ValidationException ve) {
			if (!ValidationException.convertToXml(ve).contains("numberOfReferences")) {
				throw ve;
			}
		}
	}

	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			jiraRef = "CCI-2061",
			mingleRef = 36753, 
			mingleTitle = "WP361 - Business Service - Issue Multiple Business References - (Phase 2) v1", 
			acceptanceCriteriaRefs = "CR4.1,CR4.2", 
			given = "A request to generate 3 reference numbers for a incident and an invalid forceId", 
			when = "The request is sent to BS", 
			then = "An InfrastructureException should be returned as result, as it's not possible to generate references for an invalid force id.")
	public void testAllocateTwoKeysInvalidForceJsonRest() throws Exception {
		try {
			businessServices.allocateKeys("unknown force", PoleNames.INCIDENT, 3, null, false);
			fail("Expected an exception");
		} catch (ValidationException ve) {
			if (!ValidationException.convertToXml(ve).contains("force")) {
				throw ve;
			}
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			jiraRef = "CCI-2061",
			mingleRef = 36753, 
			mingleTitle = "WP361 - Business Service - Issue Multiple Business References - (Phase 2) v1", 
			acceptanceCriteriaRefs = "CR3.1", 
			given = "A request to generate 2 reference numbers for a case, the forceId 42 and the AMO unit", 
			when = "The request is sent to BS", 
			then = "A Json response back is sent to the client with the new 2 references")
	public void testAllocateTwoKeysForCaseJsonRest() throws Exception {
		AllocateKeysResponse resp = businessServices.allocateKeys(FORCE_ID, PoleNames.GEM_CASE, 2, UNIT_CODE, false);
		assertEquals("Keys should be allocated according to numberOfReferences", 2, resp.getKeys().size());
	}

	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			jiraRef = "CCI-2061",
			mingleRef = 36753, 
			mingleTitle = "WP361 - Business Service - Issue Multiple Business References - (Phase 2) v1", 
			acceptanceCriteriaRefs = "CR4.1,CR4.2", 
			given = "A request to generate 2 reference numbers for a case and the forceId 42, the unit is NOT specified", 
			when = "The request is sent to BS", 
			then = "A ValidationException should be returned as result, as the unit needs to be supplied")
	public void testAllocateTwoKeysForCaseWithoutUnitJsonRest() throws Exception {
		try {
			businessServices.allocateKeys(FORCE_ID, PoleNames.GEM_CASE, 2, null, false);
			fail("Expected an exception");
		} catch (ValidationException ve) {
			if (!ValidationException.convertToXml(ve).contains("unit")) {
				fail("Response error should complain about missing unit but was " + ve.getMessage());
			}
		}
	}

	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			jiraRef = "CCI-2061",
			mingleRef = 36753, 
			mingleTitle = "WP361 - Business Service - Issue Multiple Business References - (Phase 2) v1", 
			acceptanceCriteriaRefs = "CR4.1,CR4.2", 
			given = "A request to generate 2 reference numbers for a case andthe forceId 42, the unit is not valid", 
			when = "The request is sent to BS", 
			then = "An ValidationException should be returned as result, as the unit needs to be supplied")
	public void testAllocateTwoKeysForCaseWithInvalidUnitJsonRest() throws Exception {
		try {
			businessServices.allocateKeys(FORCE_ID, PoleNames.GEM_CASE, 2, "unknown unit", false);
			fail("Expected an exception");
		} catch (ValidationException ve) {
			if (!ValidationException.convertToXml(ve).contains("Invalid unit.")) {
				fail("Response error should complain about bad unit but was " + ve.getMessage());
			}
		}
	}

	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			jiraRef = "CCI-2061",
			mingleRef = 36753, 
			mingleTitle = "WP361 - Business Service - Issue Multiple Business References - (Phase 2) v1", 
			acceptanceCriteriaRefs = "CR4.1,CR4.2", 
			given = "A request to generate 2 reference numbers for a wrong event", 
			when = "The request is sent to BS", 
			then = "An ValidationException should be returned as result, as the references cannot be generated")
	public void testAllocateTwoKeysForAWrongEventUnitJsonRest() throws Exception {
		try {
			businessServices.allocateKeys(FORCE_ID, "unknown event", 2, null, false);
			fail("Expected an exception");
		} catch (ValidationException ve) {
			assertTrue(ValidationException.convertToXml(ve).contains("The event type unknown event"));
		}
	}
	
	@Test
	public void testAllocateTwoNationalReferenceNumberKeysJsonRest() throws Exception {
		AllocateKeysResponse resp = businessServices.allocateKeys(null, PoleNames.PSP, 2, null, true);
		assertEquals("Keys should be allocated according to numberOfReferences", 2, resp.getKeys().size());
	}
	
	@Test
	public void testAllocateTwoNationalReferenceNumberKeysForInvalidEventTypeJsonRest() throws Exception {
		try {
			businessServices.allocateKeys(FORCE_ID, "unknown event", 2, null, true);
			fail("Expected an exception");
		} catch (ValidationException ve) {
			assertTrue(ValidationException.convertToXml(ve).contains("The event type unknown event"));
		}
	}
}