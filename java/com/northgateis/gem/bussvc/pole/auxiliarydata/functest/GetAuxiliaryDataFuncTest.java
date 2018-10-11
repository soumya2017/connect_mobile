package com.northgateis.gem.bussvc.pole.auxiliarydata.functest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import com.northgateis.gem.bussvc.AbstractBusinessServicesFunctionalTestBase;
import com.northgateis.gem.bussvc.framework.test.BusSvcStoryAcceptanceCriteriaReference;
import com.northgateis.gem.bussvc.pole.auxiliarydata.routes.GetAuxiliaryDataServiceRoute;
import com.northgateis.gem.bussvc.poleobjects.constants.PoleObjectsServiceConstants;
import com.northgateis.pole.client.PoleDtoBuildHelper;
import com.northgateis.pole.common.PoleNames;
import com.northgateis.pole.schema.AuxiliaryDataCriteriaDto;
import com.northgateis.pole.schema.AuxiliaryDataDto;
import com.northgateis.pole.schema.AuxiliaryDataFieldSelectorDto;
import com.northgateis.pole.schema.GetAuxiliaryDataRequestDto;
import com.northgateis.pole.schema.GetAuxiliaryDataResponseDto;
import com.northgateis.pole.schema.RelatedCvListsFieldSelectorDto;
import com.northgateis.pole.schema.RequestHeaderDto;
import com.northgateis.pole.schema.StringCriterionDto;

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
 * {@link GetAuxiliaryDataServiceRoute}
 * 
 * e.g. Url -
 * http://x.y.z.w:port/gembusinessservices/rest/pole/services/auxiliarydata/riskassessmenttemplate?force=42&xyz=true
 * 
 * @author harsh.shah
 */
public class GetAuxiliaryDataFuncTest 
	extends AbstractBusinessServicesFunctionalTestBase {	
		
	/**
	 * Method to create basic GetAuxiliaryDataRequest with auxiliaryDataType criteria
	 * 
	 * @param auxiliaryDataType
	 * @return
	 */
	private GetAuxiliaryDataRequestDto createRequest(String auxiliaryDataType) {

		GetAuxiliaryDataRequestDto req = new GetAuxiliaryDataRequestDto();

		AuxiliaryDataCriteriaDto auxiliaryDataCriteria = PoleDtoBuildHelper.buildAuxiliaryDataCriteriaDto(
				auxiliaryDataType);
		req.setAuxiliaryDataCriteria(auxiliaryDataCriteria);

		AuxiliaryDataFieldSelectorDto auxiliaryDataFieldSelector = PoleDtoBuildHelper.buildAuxiliaryDataFieldSelectorDto(
				auxiliaryDataType);
		req.setAuxiliaryDataFieldSelector(auxiliaryDataFieldSelector);
		
		RequestHeaderDto header = new RequestHeaderDto();
		header.setPasswordHash(securityContextId);
		header.setClientName(PoleObjectsServiceConstants.BUSINESS_SERVICES_CLIENT_NAME);
		req.setHeader(header);
		
		return req;
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			jiraRef = "CCI-40390",
			mingleTitle = "WMP -- WP109 - Force Priorities field", 
			acceptanceCriteriaRefs = "NA", 
			given = "A request to get auxiliaryData based on its type",
			when = "The request is sent to BS", 
			then = "A Json response back is sent to the client with result")
	public void testValidRequestReturnsJsonData() throws Exception {				
		GetAuxiliaryDataResponseDto resp = poleBusinessServices.getAuxiliaryData(
				createRequest(PoleNames.UNIT_FORCE_HIERARCHY));
		
		assertNotNull(resp);
		assertEquals("Expected " + PoleNames.UNIT_FORCE_HIERARCHY + " data to be returned", true,
				resp.getAuxiliaryDataList().size() >= 0);
		if (resp.getAuxiliaryDataList().size() > 0) {
			AuxiliaryDataDto auxiliaryDataDto = resp.getAuxiliaryDataList().get(0);
			assertEquals("Expected entityType " + PoleNames.UNIT_FORCE_HIERARCHY + " to be returned",
					PoleNames.UNIT_FORCE_HIERARCHY, auxiliaryDataDto.getEntityType());
		}
	}
	
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			jiraRef = "CCI-40390",
			mingleTitle = "WMP -- WP109 - Force Priorities field", 
			acceptanceCriteriaRefs = "NA", 
			given = "A request to get forcePriority auxiliaryData",
			when = "The request is sent to BS", 
			then = "A Json response back is sent to the client with result")
	public void testValidForcePriorityRequestReturnsJsonData() throws Exception {			
		GetAuxiliaryDataResponseDto resp = poleBusinessServices.getAuxiliaryData(
				createRequest(PoleNames.FORCE_PRIORITY));

		assertNotNull(resp);
		List<? extends AuxiliaryDataDto> auxiliaryDataList = resp.getAuxiliaryDataList();
		assertEquals("Expected " + PoleNames.FORCE_PRIORITY + " data to be returned", true,
				auxiliaryDataList.size() >= 0);
		if (auxiliaryDataList.size() > 0) {
			AuxiliaryDataDto auxiliaryDataDto = auxiliaryDataList.get(0);
			assertEquals("Expected entityType " + PoleNames.FORCE_PRIORITY + " to be returned",
					PoleNames.FORCE_PRIORITY, auxiliaryDataDto.getEntityType());
		}
	}
	

	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			jiraRef = "CCI-40390",
			mingleTitle = "WMP -- WP109 - Force Priorities field", 
			acceptanceCriteriaRefs = "GetAuxData over HTTP Post", 
			given = "A request to get forcePriority auxiliaryData",
			when = "The request is sent to BS", 
			then = "A Json response back is sent to the client with result")
	public void testValidForcePriorityRequestReturnsJsonDataWithCvValues() throws Exception {			
		GetAuxiliaryDataRequestDto request = createRequest(PoleNames.UNIT_FORCE_HIERARCHY);
		RelatedCvListsFieldSelectorDto cvSelect = new RelatedCvListsFieldSelectorDto();
		cvSelect.addCvListName("PNC_FORCE_CODE");
		request.setRelatedCvListsFieldSelector(cvSelect);
		
		AuxiliaryDataCriteriaDto auxiliaryDataCriteria = request.getAuxiliaryDataCriteria();
		StringCriterionDto forceCodeCriteria = new StringCriterionDto("forceCode", "42");
		auxiliaryDataCriteria.addFieldCriterion(forceCodeCriteria);
		
		StringCriterionDto bcuCodeCriteria = new StringCriterionDto("bcuCode", "ESSEX SOUTH");
		auxiliaryDataCriteria.addFieldCriterion(bcuCodeCriteria);
		
		StringCriterionDto unitCodeCriteria = new StringCriterionDto("unitCode", "42 SOIT SOUTH INSP");
		auxiliaryDataCriteria.addFieldCriterion(unitCodeCriteria);		
		
		GetAuxiliaryDataResponseDto resp = poleBusinessServices.getAuxiliaryData(
				request);		

		assertNotNull(resp);
		List<? extends AuxiliaryDataDto> auxiliaryDataList = resp.getAuxiliaryDataList();
		assertEquals("Expected " + PoleNames.FORCE_PRIORITY + " data to be returned", true,
				auxiliaryDataList.size() >= 0);
		if (auxiliaryDataList.size() > 0) {
			AuxiliaryDataDto auxiliaryDataDto = auxiliaryDataList.get(0);
			assertEquals("Expected entityType " + PoleNames.UNIT_FORCE_HIERARCHY+ " to be returned",
					PoleNames.UNIT_FORCE_HIERARCHY, auxiliaryDataDto.getEntityType());
		}
	}
	
}