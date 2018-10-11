package com.northgateis.gem.bussvc.forcepriority.functest;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.northgateis.gem.bussvc.AbstractBusinessServicesFunctionalTestBase;
import com.northgateis.gem.bussvc.forcepriority.routes.SearchForcePriorityServiceRoute;
import com.northgateis.gem.bussvc.framework.test.BusSvcStoryAcceptanceCriteriaReference;
import com.northgateis.pole.common.PoleNames;
import com.northgateis.pole.schema.AuxiliaryDataDto;
import com.northgateis.pole.schema.ForcePriorityDto;
import com.northgateis.pole.schema.GetAuxiliaryDataResponseDto;

/**
 * Functional Test class to test the GetAuxiliaryData JsonRest Request on the RESTful API.
 * 
 * These tests are only valid when Business Services is offering its JSON/REST API,
 * which is only enabled during JUnit UT runs by -DjettyMode=true & -DjettyPort=1234
 * 
 * To run this test the following must be provided to the JVM runtime:
 * 
 * 1 x System Parameters for the main business services runtime, to tell it
 * to start jetty on a given port.
 * -DjettyMode=true
 * 
 * 2 x Environment Variables for the unit-tests to tell them to use JSON/REST
 * calling a specific port.
 * jsonRestMode=true
 * jsonRestJettyPort=8765
 * 
 * Key classes to observe those are tested by these FTs:
 * {@link SearchForcePriorityServiceRoute}
 * 
 * e.g. Url -
 * http://x.y.z.w:port/gembusinessservices/rest/pole/services/auxiliarydata/forcepriority/profile/forcepriority?extendedSearch=true
 * 
 * @author harsh.shah
 */
public class SearchForcePriorityJsonRestFuncTest extends AbstractBusinessServicesFunctionalTestBase {

	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			jiraRef = "CCI-2098",
			mingleTitle = "WMP -- WP109 - Local priorities field", 
			acceptanceCriteriaRefs = "NA", 
			given = "A JsonRest request to get ForcePriority auxiliaryData is made with extendedSearch=true as url "
					+ "query parameter with specific profile 'forcepriority'",
			when = "The request is sent to BS", 
			then = "A Json response is sent back to the client with result based on logged in user force/unit")
	public void testValidForcePriorityRequestReturnsJsonData() throws Exception {		

		Map<String, String> queryParams = new HashMap<String, String>();
		queryParams.put("extendedSearch", "true");
		GetAuxiliaryDataResponseDto resp = poleBusinessServices.getAuxiliaryDataWithProfileWithQueryParams(
				PoleNames.FORCE_PRIORITY, "forcepriority", queryParams);

		assertNotNull(resp);
		// this is to ensure pole have been call and not the empty result returned from the route itself
		assertNotNull(resp.getAuditedTransactionId());		

		if (resp.getAuxiliaryDataList().size() > 0) {
			AuxiliaryDataDto auxiliaryDataDto = resp.getAuxiliaryDataList().get(0);
			assertEquals("Expected entityType " + PoleNames.FORCE_PRIORITY + " to be returned",
					PoleNames.FORCE_PRIORITY, auxiliaryDataDto.getEntityType());
			ForcePriorityDto fpDto = (ForcePriorityDto) auxiliaryDataDto;
			assertNotNull("Expected not null Force code", fpDto.getForceCode());
			assertNull("Expected null BCU code", fpDto.getBcuCode());
			assertNull("Expected null Department code", fpDto.getDepartmentCode());
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			jiraRef = "CCI-2098",
			mingleTitle = "WMP -- WP109 - Local priorities field", 
			acceptanceCriteriaRefs = "NA", 
			given = "A JsonRest request to get LocalPriority auxiliaryData is made with extendedSearch=true as url "
					+ "query parameter with specific profile 'localpriority'",
			when = "The request is sent to BS", 
			then = "A Json response is sent back to the client with result based on logged in user force/unit")
	public void testValidLocalPriorityRequestReturnsJsonData() throws Exception {		

		Map<String, String> queryParams = new HashMap<String, String>();
		queryParams.put("extendedSearch", "true");
		GetAuxiliaryDataResponseDto resp = poleBusinessServices.getAuxiliaryDataWithProfileWithQueryParams(
				PoleNames.FORCE_PRIORITY, "localpriority", queryParams);

		assertNotNull(resp);					

		if (resp.getAuxiliaryDataList().size() > 0) {
			AuxiliaryDataDto auxiliaryDataDto = resp.getAuxiliaryDataList().get(0);
			assertEquals("Expected entityType " + PoleNames.FORCE_PRIORITY + " to be returned",
					PoleNames.FORCE_PRIORITY, auxiliaryDataDto.getEntityType());
			ForcePriorityDto fpDto = (ForcePriorityDto) auxiliaryDataDto;
			assertNotNull("Expected not null Force code", fpDto.getForceCode());
			assertTrue("Expected not null BCU code or Department code",
					(fpDto.getBcuCode() != null || fpDto.getDepartmentCode() != null));		
		}
	}

}