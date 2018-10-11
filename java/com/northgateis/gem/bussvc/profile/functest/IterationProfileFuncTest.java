package com.northgateis.gem.bussvc.profile.functest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.northgateis.gem.bussvc.AbstractBusinessServicesFunctionalTestBase;
import com.northgateis.gem.bussvc.framework.FrameworkServiceConstants;
import com.northgateis.gem.bussvc.framework.test.BusSvcStoryAcceptanceCriteriaReference;
import com.northgateis.gem.bussvc.poleobjects.constants.PoleObjectsServiceConstants;
import com.northgateis.pole.common.PoleNames;
import com.northgateis.pole.schema.GetPoleObjectsRequestDto;
import com.northgateis.pole.schema.GetPoleObjectsResponseDto;
import com.northgateis.pole.schema.IntegerCriterionDto;
import com.northgateis.pole.schema.MessageExtraInfoDto;
import com.northgateis.pole.schema.ModificationStatusDto;
import com.northgateis.pole.schema.PersonDto;
import com.northgateis.pole.schema.PoleObjectCriteriaDto;
import com.northgateis.pole.schema.PutPoleObjectsRequestDto;
import com.northgateis.pole.schema.PutPoleObjectsResponseDto;
import com.northgateis.pole.schema.RequestHeaderDto;
import com.northgateis.pole.schema.RetrievalTypeDto;

/**
 * Test class to test the retrieval of Persons using iteration profiles
 */
public class IterationProfileFuncTest extends AbstractBusinessServicesFunctionalTestBase {
	
	private PersonDto person;

	@Override
	protected void setupImpl() throws Exception {
		person = new PersonDto();
		person.setModificationStatus(ModificationStatusDto.CREATE);
		person.setSurname("IterationTest");
	}

	@Override
	protected void teardownImpl() throws Exception {
		deleteData();
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=42428,
			jiraRef = "CON-42980",
			mingleTitle = "EAG/FS/28/1 - Define iteration Profiles.",
			acceptanceCriteriaRefs = "Unknown",
			given = "To retrieve an existing Person using an iteration profile", 
			when = "The request is sent to BS", 
			then = "A response is sent back to the client with single result")
	public void testRetrievePersonUsingIterationProfile() throws Exception {
		
		createPersonInPole();
		
		GetPoleObjectsResponseDto getPoleObjectsResponseDto =
				poleBusinessServices.getPoleObjectsWithProfile(PoleNames.PERSON, person.getObjectRef(),
						"ITERATION_PROFILE_SHORT");
		assertEquals("Expecting one pole object to be returned", getPoleObjectsResponseDto.getPoleObjects().size(), 1);
		assertTrue("Expecting the returned pole object to be of type Person",
				getPoleObjectsResponseDto.getPoleObjects().get(0) instanceof PersonDto);
		assertEquals("Expecting the returned pole object to have a Surname of IterationTest",
				((PersonDto) getPoleObjectsResponseDto.getPoleObjects().get(0)).getSurname(), "IterationTest");
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=42428,
			jiraRef = "CON-42980",
			mingleTitle = "EAG/FS/28/1 - Define iteration Profiles.",
			acceptanceCriteriaRefs = "Unknown",
			given = "To retrieve an existing Person using an iteration profile that doesnt exist", 
			when = "The request is sent to BS", 
			then = "It errors saying no profile found")
	public void testRetrievePersonFailsWhenUsingUnknownIterationProfile() throws Exception {
		
		createPersonInPole();
		
		try {
			poleBusinessServices.getPoleObjectsWithProfile(PoleNames.PERSON, person.getObjectRef(),
					"ITERATION_PROFILE_BLAA");
			fail("Expecting no matching profile found error");
		} catch (Exception e) {
			assertTrue("Expecting no matching profile found error", e.getMessage().contains("No profile matching"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=42428,
			jiraRef = "CON-42980",
			mingleTitle = "EAG/FS/28/1 - Define iteration Profiles.",
			acceptanceCriteriaRefs = "Unknown",
			given = "To retrieve an existing Person using an iteration profile", 
			when = "The request is sent to BS over HTTP POST", 
			then = "A response is sent back to the client with single result")
	public void testRetrievePersonUsingIterationProfileOverHttpPost() throws Exception {
		
		createPersonInPole();
		
		GetPoleObjectsRequestDto gpoReq = new GetPoleObjectsRequestDto();
		gpoReq.setRetrievalType(RetrievalTypeDto.SEARCH);
		
		PoleObjectCriteriaDto poCrit = new PoleObjectCriteriaDto();
		poCrit.setObjectType(PoleNames.PERSON);
		poCrit.addFieldCriterion(new IntegerCriterionDto("objectRef", person.getObjectRef()));
		gpoReq.setPoleObjectCriteria(poCrit);
		
		MessageExtraInfoDto profileExtraInfo = new MessageExtraInfoDto();
		profileExtraInfo.setKey(PoleObjectsServiceConstants.BUS_SVC_PROFILE_EXTRA_INFO_KEY);
		profileExtraInfo.setValue("ITERATION_PROFILE_LONG");
		gpoReq.addExtraInfo(profileExtraInfo);
		
		GetPoleObjectsResponseDto getPoleObjectsResponseDto = poleBusinessServices.getPoleObjects(gpoReq);
		assertEquals("Expecting one pole object to be returned", getPoleObjectsResponseDto.getPoleObjects().size(), 1);
		assertTrue("Expecting the returned pole object to be of type Person",
				getPoleObjectsResponseDto.getPoleObjects().get(0) instanceof PersonDto);
		assertEquals("Expecting the returned pole object to have a Surname of IterationTest",
				((PersonDto) getPoleObjectsResponseDto.getPoleObjects().get(0)).getSurname(), "IterationTest");
	}
	
	private void createPersonInPole() throws Exception {
		PutPoleObjectsRequestDto ppoReq = createPutPoleObjectsRequest(person);
		
		PutPoleObjectsResponseDto ppoResp = poleDirect.putPoleObjects(ppoReq);
		
		getPersonObjectRef(ppoResp);
	}
	
	private PutPoleObjectsRequestDto createPutPoleObjectsRequest(PersonDto personForRequest) {
		PutPoleObjectsRequestDto ppoReq = new PutPoleObjectsRequestDto();
		ppoReq.addPoleObject(personForRequest);
		
		if (ppoReq.getHeader() == null) {
			ppoReq.setHeader(new RequestHeaderDto());
			ppoReq.getHeader().setClientName(FrameworkServiceConstants.BUSINESS_SERVICES_CLIENT_NAME);
		}
		ppoReq.getHeader().setPasswordHash(securityContextId);
		
		return ppoReq;
	}
	
	private void getPersonObjectRef(PutPoleObjectsResponseDto ppoResp) throws Exception {
		if (ppoResp.getObjectRefs().size() == 1) {
			person.setObjectRef(ppoResp.getObjectRefs().get(0));
		} else {
			throw new Exception("Failed to retrieve Person object ref");
		}
	}
	
	/**
	 * A method to cleanup Person after test
	 * @throws Exception
	 */
	private void deleteData() throws Exception {
		try {
			if (person != null && person.getObjectRef() != null
					&& person.getObjectRef() > 0) {
				person.setModificationStatus(ModificationStatusDto.REMOVE);
				PutPoleObjectsRequestDto ppoReq = createPutPoleObjectsRequest(person);
				poleDirect.putPoleObjects(ppoReq);
			}
		} catch (Exception e) {
			logger.debug("Failed to delete Person having objectRef {} ", person.getObjectRef(), e);
		}
	}
}
