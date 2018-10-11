package com.northgateis.gem.bussvc.briefingitem.functest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.northgateis.gem.bussvc.briefingitem.utils.BriefingItemPoleDtoUtils;
import com.northgateis.gem.bussvc.framework.test.BusSvcStoryAcceptanceCriteriaReference;
import com.northgateis.gem.bussvc.poleobjects.constants.PoleObjectsServiceConstants;
import com.northgateis.gem.bussvc.submitbriefing.functest.AbstractBriefingFuncTestBase;
import com.northgateis.gem.bussvc.test.util.BusinessServicesTestUtils;
import com.northgateis.gem.bussvc.test.util.CommonPoleObjectTestUtils;
import com.northgateis.pole.client.PoleClientUtils;
import com.northgateis.pole.common.PoleNames;
import com.northgateis.pole.schema.BriefingItemDto;
import com.northgateis.pole.schema.BriefingItemGeographicalAreaAndCategoryDto;
import com.northgateis.pole.schema.GetPoleObjectsRequestDto;
import com.northgateis.pole.schema.GetPoleObjectsResponseDto;
import com.northgateis.pole.schema.IntegerCriterionDto;
import com.northgateis.pole.schema.LinkDto;
import com.northgateis.pole.schema.MessageExtraInfoDto;
import com.northgateis.pole.schema.ModificationStatusDto;
import com.northgateis.pole.schema.PersonDto;
import com.northgateis.pole.schema.PoleObjectCriteriaDto;
import com.northgateis.pole.schema.PoleObjectDto;
import com.northgateis.pole.schema.PutPoleObjectsRequestDto;
import com.northgateis.pole.schema.PutPoleObjectsResponseDto;
import com.northgateis.pole.schema.RetrievalTypeDto;

/**
 * Functional Test class to test the TemplatedProfile JsonRest Request for BriefingItem specific scenarios.
 * 
 * @author mustafa.attaree
 * 
 */
public class SearchBriefingItemJsonRestFuncTest extends AbstractBriefingFuncTestBase {

	private BriefingItemDto briefingItemDto;

	@Override
	protected void setupImpl() throws Exception {
		briefingItemDto = briefingTestUtils.createBriefingItem(-1, Integer.valueOf(officerReportingId));
	}

	@Override
	protected void teardownImpl() throws Exception {
		deleteData();
	}

	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage = 109,
		jiraRef = "CCI-2124",
		mingleTitle = "WMP -- WP109 - Briefing items viewing by default",
		acceptanceCriteriaRefs = "Unknown",
		given = "To retrieve existing BriefingItem event using templatedProfile by its objectRef", 
		when = "The request is sent to BS", 
		then = "A Json response back is sent to the client with single result")
	public void testValidRequestWithObjRefReturnsJsonData() throws Exception {				
		
		PersonDto person = new PersonDto();		
		person.setStatus(CommonPoleObjectTestUtils.POLE_OBJECT_NEW_STATUS);
		person.setModificationStatus(ModificationStatusDto.CREATE);
		person.setObjectRef(-123);
		person.setSurname("Shah");
		person.setForename1("Harsh");
		person.setForename2("Harsh" + "Two");
		person.setForename3("Harsh" + "Three");
		person.setResearched(Boolean.FALSE);
		
		LinkDto personBriefingLink = new LinkDto();
		personBriefingLink.setModificationStatus(ModificationStatusDto.CREATE);
		personBriefingLink.setResearched(Boolean.FALSE);
		personBriefingLink.setSourcePoleObjectType(PoleNames.BRIEFING_ITEM);
		personBriefingLink.setSourcePoleObjectRef(new Integer(-1));
		personBriefingLink.setToPoleObject(person);
		personBriefingLink.setLinkReason(PoleObjectsServiceConstants.ASSOCIATED_WITH_LINK_REASON);
		
		briefingItemDto.addLink(personBriefingLink);
		
		// Generating PutPoleObjectsRequestDto request
		PutPoleObjectsRequestDto putPoleObjectsRequestDto = briefingTestUtils.createPutPoleObjectsRequestDto(
				BusinessServicesTestUtils.createBusinessServiceInfo(securityContextId),	briefingItemDto);				

		// Saving graph to Pole and fetching PutPoleObjectsResponseDto response
		PutPoleObjectsResponseDto putPoleObjectsResponseDto = poleBusinessServices.putPoleObjects(
				putPoleObjectsRequestDto);

		// extracting actual created BriefingItem objectRef from PutPoleObjectsResponseDto response
		Integer poleGenBriefingItemObjectRef = BriefingItemPoleDtoUtils.extractBriefingItemObjectRef(
				putPoleObjectsResponseDto);								

		// fetch the BriefingItem back with templatedProfile functionality
		GetPoleObjectsResponseDto getPoleObjectsResponseDto = poleBusinessServices.getPoleObjectsWithProfile(
				PoleNames.BRIEFING_ITEM, poleGenBriefingItemObjectRef, "briefingitem");

		// assert data
		briefingItemDto = (BriefingItemDto) getPoleObjectsResponseDto.getPoleObjects().get(0);		
		assertEquals("Pole generated object reference should match object reference in BriefingItemDto ",
				briefingItemDto.getObjectRef(), poleGenBriefingItemObjectRef);
		 
		BriefingItemGeographicalAreaAndCategoryDto briefingItemGeographicalAreaAndCategoryDto = 
				briefingItemDto.getGeographicalAreasAndCategories().get(0);
		assertNotNull("Expected single BriefingItemGeographicalAreaAndCategoryDto",
				briefingItemGeographicalAreaAndCategoryDto != null);		
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage = 109,
		jiraRef = "CCI-2124",
		mingleTitle = "WMP -- WP109 - Briefing items viewing by default",
		acceptanceCriteriaRefs = "Unknown",
		given = "To retrieve all BriefingItem events list based on logged in user using templatedProfile", 
		when = "The request is sent to BS", 
		then = "A Json response back is sent to the client with list of result")
	@SuppressWarnings("unchecked")
	public void testValidRequestWithoutObjRefReturnsListOfJsonData() throws Exception {	
				
		Map<String, String> queryParams = new HashMap<String, String>();
		queryParams.put("extendedSearch", "true");
		// Get the list of briefingItem
		GetPoleObjectsResponseDto getPoleObjectsResponseDto = poleBusinessServices.getPoleObjectsWithProfileWithQueryParams(
				PoleNames.BRIEFING_ITEM, null, "briefingitemall", queryParams);

		List<PoleObjectDto> poleObjectsDto = (List<PoleObjectDto>) getPoleObjectsResponseDto.getPoleObjects();
		assertTrue("BriefingItemList.size() >= 0 ", poleObjectsDto.size() >= 0);		
	}
	
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage = 109,
		jiraRef = "CCI-2124",
		mingleTitle = "WMP -- WP109 - Briefing items viewing by default",
		acceptanceCriteriaRefs = "Unknown",
		given = "To retrieve a BriefingItem events list based on IntegerInCriteria for objectRefs over jsonRest http "
				+ "post request", 
		when = "The request is sent to BS", 
		then = "A Json response back is sent to the client with list of result")	
	public void testValidGpoRequestOverJsonRestWithHttpPostReturnsSuccess() throws Exception {
		
		// JsonRequest
		String getPoleRequest = "{" + "\"_type\": \"com.northgateis.pole.schema.GetPoleObjectsRequestDto\","
				+ "\"poleObjectCriteria\": {" + "\"_type\": \"com.northgateis.pole.schema.PoleObjectCriteriaDto\","
				+ "\"objectType\": \"BriefingItem\"," + "\"fieldCriteria\": [{"
				+ "\"_type\": \"com.northgateis.pole.schema.IntegerInCriterionDto\"," + "\"not\": false,"
				+ "\"applyToOuterJoin\": false," + "\"matchNull\": false," + "\"fieldName\": \"objectRef\","
				+ "\"values\": [1234]" + "}]" + "}," + "\"poleObjectFieldSelector\": {"
				+ "	\"_type\": \"com.northgateis.pole.schema.PoleObjectFieldSelectorDto\","
				+ "	\"objectType\": \"BriefingItem\"," + "	\"fieldNames\": [\"objectRef\"," + "	\"status\","
				+ "	\"title\"," + "	\"type\"," + "	\"priority\"," + "	\"validFromDate\"," + "	\"validToDate\"],"
				+ "	\"childObjectFieldSelectors\": []," + "	\"linkFieldSelectors\": []" + "}" + "}";
		
		// Unmarshal jsonRequest to appropriate request class
		GetPoleObjectsRequestDto request = PoleClientUtils.unmarshalJson2(getPoleRequest, GetPoleObjectsRequestDto.class);
		request.setRetrievalType(RetrievalTypeDto.SEARCH);
		
		// Execute the request over http post jsonRest
		GetPoleObjectsResponseDto response = poleBusinessServices.getPoleObjects(request);
		
		List<? extends PoleObjectDto> poleObjects = response.getPoleObjects();
		assertTrue("PoleObjects.size() >=0", poleObjects.size() >= 0);
	}		
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage = 109,
		jiraRef = "CCI-2124",
		mingleTitle = "WMP -- WP109 - Briefing items viewing by default",
		acceptanceCriteriaRefs = "Unknown",
		given = "To retrieve existing BriefingItem event using templatedProfile by its objectRef in edit mode", 
		when = "The request is sent to BS", 
		then = "A Json response back is sent to the client with single result")
	public void testValidRequestWithObjRefWithEditProfileReturnsJsonData() throws Exception {				
		
		PersonDto person = new PersonDto();		
		person.setStatus(CommonPoleObjectTestUtils.POLE_OBJECT_NEW_STATUS);
		person.setModificationStatus(ModificationStatusDto.CREATE);
		person.setObjectRef(-123);
		person.setSurname("Shah");
		person.setForename1("Harsh");
		person.setForename2("Harsh" + "Two");
		person.setForename3("Harsh" + "Three");
		person.setResearched(Boolean.FALSE);
		
		LinkDto personBriefingLink = new LinkDto();
		personBriefingLink.setModificationStatus(ModificationStatusDto.CREATE);
		personBriefingLink.setResearched(Boolean.FALSE);
		personBriefingLink.setSourcePoleObjectType(PoleNames.BRIEFING_ITEM);
		personBriefingLink.setSourcePoleObjectRef(new Integer(-1));
		personBriefingLink.setToPoleObject(person);
		personBriefingLink.setLinkReason(PoleObjectsServiceConstants.ASSOCIATED_WITH_LINK_REASON);
		
		briefingItemDto.addLink(personBriefingLink);
		
		// Generating PutPoleObjectsRequestDto request
		PutPoleObjectsRequestDto putPoleObjectsRequestDto = briefingTestUtils.createPutPoleObjectsRequestDto(
				BusinessServicesTestUtils.createBusinessServiceInfo(securityContextId),	briefingItemDto);				

		// Saving graph to Pole and fetching PutPoleObjectsResponseDto response
		PutPoleObjectsResponseDto putPoleObjectsResponseDto = poleBusinessServices.putPoleObjects(
				putPoleObjectsRequestDto);

		// extracting actual created BriefingItem objectRef from PutPoleObjectsResponseDto response
		Integer poleGenBriefingItemObjectRef = BriefingItemPoleDtoUtils.extractBriefingItemObjectRef(
				putPoleObjectsResponseDto);								

		// fetch the BriefingItem back with templatedProfile functionality
		GetPoleObjectsResponseDto getPoleObjectsResponseDto = poleBusinessServices.getPoleObjectsWithProfile(
				PoleNames.BRIEFING_ITEM, poleGenBriefingItemObjectRef, "briefingitemedit");

		// assert data
		briefingItemDto = (BriefingItemDto) getPoleObjectsResponseDto.getPoleObjects().get(0);		
		assertEquals("Pole generated object reference should match object reference in BriefingItemDto ",
				briefingItemDto.getObjectRef(), poleGenBriefingItemObjectRef);
		 
		BriefingItemGeographicalAreaAndCategoryDto briefingItemGeographicalAreaAndCategoryDto = 
				briefingItemDto.getGeographicalAreasAndCategories().get(0);
		assertNotNull("Expected single BriefingItemGeographicalAreaAndCategoryDto",
				briefingItemGeographicalAreaAndCategoryDto != null);		
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage = 109,
		jiraRef = "CCI-2124",
		mingleTitle = "WMP -- WP109 - Briefing items viewing by default",
		acceptanceCriteriaRefs = "Unknown",
		given = "To retrieve existing BriefingItem event using templatedProfile by its objectRef in edit mode", 
		when = "The request is sent to BS over HTTP POST", 
		then = "A Json response back is sent to the client with single result")
	public void testValidRequestWithObjRefWithEditProfileOverHttpPostReturnsJsonData() throws Exception {				
		
		PersonDto person = new PersonDto();		
		person.setStatus(CommonPoleObjectTestUtils.POLE_OBJECT_NEW_STATUS);
		person.setModificationStatus(ModificationStatusDto.CREATE);
		person.setObjectRef(-123);
		person.setSurname("Shah");
		person.setForename1("Harsh");
		person.setForename2("Harsh" + "Two");
		person.setForename3("Harsh" + "Three");
		person.setResearched(Boolean.FALSE);
		
		LinkDto personBriefingLink = new LinkDto();
		personBriefingLink.setModificationStatus(ModificationStatusDto.CREATE);
		personBriefingLink.setResearched(Boolean.FALSE);
		personBriefingLink.setSourcePoleObjectType(PoleNames.BRIEFING_ITEM);
		personBriefingLink.setSourcePoleObjectRef(new Integer(-1));
		personBriefingLink.setToPoleObject(person);
		personBriefingLink.setLinkReason(PoleObjectsServiceConstants.ASSOCIATED_WITH_LINK_REASON);
		
		briefingItemDto.addLink(personBriefingLink);
		
		// Generating PutPoleObjectsRequestDto request
		PutPoleObjectsRequestDto putPoleObjectsRequestDto = briefingTestUtils.createPutPoleObjectsRequestDto(
				BusinessServicesTestUtils.createBusinessServiceInfo(securityContextId),	briefingItemDto);				

		// Saving graph to Pole and fetching PutPoleObjectsResponseDto response
		PutPoleObjectsResponseDto putPoleObjectsResponseDto = poleBusinessServices.putPoleObjects(
				putPoleObjectsRequestDto);

		// extracting actual created BriefingItem objectRef from PutPoleObjectsResponseDto response
		Integer poleGenBriefingItemObjectRef = BriefingItemPoleDtoUtils.extractBriefingItemObjectRef(
				putPoleObjectsResponseDto);								

		// fetch the BriefingItem back with templatedProfile functionality over HTTP POST
		GetPoleObjectsRequestDto gpoReq = new GetPoleObjectsRequestDto();
		PoleObjectCriteriaDto poCrit = new PoleObjectCriteriaDto();
		poCrit.setObjectType(PoleNames.BRIEFING_ITEM);
		poCrit.addFieldCriterion(new IntegerCriterionDto("objectRef", poleGenBriefingItemObjectRef));
		gpoReq.setPoleObjectCriteria(poCrit);
		
		MessageExtraInfoDto profileExtraInfo = new MessageExtraInfoDto();
		profileExtraInfo.setKey(PoleObjectsServiceConstants.BUS_SVC_PROFILE_EXTRA_INFO_KEY);
		profileExtraInfo.setValue("briefingitemedit");
		gpoReq.addExtraInfo(profileExtraInfo);
		
		GetPoleObjectsResponseDto getPoleObjectsResponseDto = poleBusinessServices.getPoleObjects(gpoReq);

		// assert data
		briefingItemDto = (BriefingItemDto) getPoleObjectsResponseDto.getPoleObjects().get(0);		
		assertEquals("Pole generated object reference should match object reference in BriefingItemDto ",
				briefingItemDto.getObjectRef(), poleGenBriefingItemObjectRef);
		 
		BriefingItemGeographicalAreaAndCategoryDto briefingItemGeographicalAreaAndCategoryDto = 
				briefingItemDto.getGeographicalAreasAndCategories().get(0);
		assertNotNull("Expected single BriefingItemGeographicalAreaAndCategoryDto",
				briefingItemGeographicalAreaAndCategoryDto != null);		
	}

	/**
	 * A method to cleanup BriefingData after test
	 * @throws Exception
	 */
	private void deleteData() throws Exception {
		try {
			if (briefingItemDto != null && briefingItemDto.getObjectRef() != null
					&& briefingItemDto.getObjectRef() > 0) {
				briefingTestUtils.removeBriefingItemFromPole(briefingItemDto.getObjectRef(), 2, poleDirect,
						securityContextId);
			}
		} catch (Exception e) {
			logger.debug("Failed to delete Incident having objectRef {} ", briefingItemDto.getObjectRef(), e);
		}
	}
}
