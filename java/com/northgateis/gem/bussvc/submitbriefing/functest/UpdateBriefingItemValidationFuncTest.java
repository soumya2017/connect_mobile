package com.northgateis.gem.bussvc.submitbriefing.functest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.northgateis.gem.bussvc.framework.exception.ValidationException;
import com.northgateis.gem.bussvc.framework.test.BusSvcStoryAcceptanceCriteriaReference;
import com.northgateis.gem.bussvc.test.util.BusinessServicesTestUtils;
import com.northgateis.gem.bussvc.test.util.CommonPoleObjectTestUtils;
import com.northgateis.pole.common.EntityKey;
import com.northgateis.pole.common.PoleDate;
import com.northgateis.pole.common.PoleDateTime;
import com.northgateis.pole.common.PoleNames;
import com.northgateis.pole.common.PoleTimePoint;
import com.northgateis.pole.schema.BriefingItemCommentDto;
import com.northgateis.pole.schema.BriefingItemDto;
import com.northgateis.pole.schema.BriefingItemGeographicalAreaAndCategoryDto;
import com.northgateis.pole.schema.LinkDto;
import com.northgateis.pole.schema.LocationDto;
import com.northgateis.pole.schema.ModificationStatusDto;
import com.northgateis.pole.schema.PoleEntityDto;
import com.northgateis.pole.schema.PutPoleObjectsRequestDto;
import com.northgateis.pole.schema.PutPoleObjectsResponseDto;

/**
 * Functional tests for Update BriefingItem and check validation
 * 
 * @author harsh.shah
 */

public class UpdateBriefingItemValidationFuncTest extends AbstractBriefingFuncTestBase {

	private BriefingItemDto briefingItemDto;
	public static final String SUBJECT_OF_LINK_REASON = "SUBJECT OF";
	public static final String ASSOCIATED_WITH_LINK_REASON = "ASSOCIATED WITH";	

	@Override
	protected void setupImpl() throws Exception {
		super.setupImpl();				
		briefingItemDto = null;
		briefingItemDto = briefingTestUtils.createBriefingItem(-1, Integer.valueOf(officerReportingId));
	}		

	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage = 109, 
		jiraRef = "CCI-43361",
		mingleTitle = "WMP - WP109 - Update Briefing Item - BS Validations",
		acceptanceCriteriaRefs = "NA(Happy Path)",
		given = "BriefingItem is existing and we update its title field",
		when = "Briefing Item is Submitted",
		then = "The Briefing Item should sucessfully be updated to Pole")
	public void testHappyPathUpdateBriefingItem() throws Exception {
		String initialTitle = "Test Title 1";
		briefingItemDto.setTitle(initialTitle);
		processRequest();
		String updatedTitle = "Test Title 2";
		briefingItemDto.setTitle(updatedTitle);
		briefingItemDto.setModificationStatus(ModificationStatusDto.UPDATE);
		processRequest();
		assertEquals("Briefing Item title should be '" + updatedTitle + "'", updatedTitle, briefingItemDto.getTitle());
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=109,		
		jiraRef = "CCI-43361",
		mingleTitle = "WMP - WP109 - Update Briefing Item - BS Validations", 
		acceptanceCriteriaRefs = "CR1,CR3,CR5,CR7,CR30,CR31,CR32,CR33,CR37,CR38", 
		given = "Create request for Briefing Item without data as per validation"
				+ "CR1 - briefingItem.briefingItemTypeCode == null,"
				+ "CR3 - briefingItem.title == null,"
				+ "CR5 - briefingItem.priority == null,"
				+ "CR7 - briefingItem.validFromDateTime == null,"		
				+ "CR30 - child object briefingItemComment is supplied,"
				+ "CR31 - child object briefingItemOutcome is supplied,"
				+ "CR32 - child object briefingItemAttendee is supplied,"
				+ "CR33 - child object briefingItemImage is supplied"
				+ "CR37 - briefingItemComment.comment is not supplied"
				+ "CR38 - briefingItemComment.comment length is more than 500 chars", 
		when = "Briefing is submitted",
		then = "Validation errors should be returned to the client as follows."
				+ "CR1 - Mandatory validation error,"
				+ "CR3 - Mandatory validation error,"
				+ "CR5 - Mandatory validation error,"
				+ "CR7 - Mandatory validation error,"	
				+ "CR30 - No error,"
				+ "CR31 - Unexpected child object error,"
				+ "CR32 - Unexpected child object error,"
				+ "CR33 - Unexpected child object error,"
				+ "CR37 - Mandatory validation error,"
				+ "CR38 - length validation error")
	public void testMandatoryOrForbiddenValidationErrorsForCreateBriefingItem() throws Exception {
		try {
			// create briefing
			processRequest();
			// modify data
			briefingItemDto.setBriefingItemTypeCode(null);
			briefingItemDto.setPriority(null);
			briefingItemDto.setTitle(null);
			briefingItemDto.setValidFromDateTime(null);			
			briefingTestUtils.addBriefingItemAttendee(briefingItemDto, officerReportingId);
			
			// BriefingItemComment is allowed so no error will be returned for the same. So commented respective assert
			// statement in catch block.
			
			// CR37
			briefingTestUtils.addBriefingItemComment(briefingItemDto);
			briefingItemDto.getComments().get(0).setComment(null);
			
			// CR38
			BriefingItemCommentDto bfcdto = new BriefingItemCommentDto();
			bfcdto.setObjectRef(-286);
			bfcdto.setModificationStatus(ModificationStatusDto.CREATE);		
			bfcdto.setCreatedDateTime(new PoleTimePoint(new PoleDate()));			
			bfcdto.setComment(
					  "ABCDEFGHIJKLMNOPQRSTUVWXYZ ABCDEFGHIJKLMNOPQRSTUVWXYZ ABCDEFGHIJKLMNOPQRSTUVWXYZ ABCDEFGHIJKLMNOPQRSTUVWXYZ"
					+ "ABCDEFGHIJKLMNOPQRSTUVWXYZ ABCDEFGHIJKLMNOPQRSTUVWXYZ ABCDEFGHIJKLMNOPQRSTUVWXYZ ABCDEFGHIJKLMNOPQRSTUVWXYZ"
					+ "ABCDEFGHIJKLMNOPQRSTUVWXYZ ABCDEFGHIJKLMNOPQRSTUVWXYZ ABCDEFGHIJKLMNOPQRSTUVWXYZ ABCDEFGHIJKLMNOPQRSTUVWXYZ"
					+ "ABCDEFGHIJKLMNOPQRSTUVWXYZ ABCDEFGHIJKLMNOPQRSTUVWXYZ ABCDEFGHIJKLMNOPQRSTUVWXYZ ABCDEFGHIJKLMNOPQRSTUVWXYZ"
					+ "ABCDEFGHIJKLMNOPQRSTUVWXYZ ABCDEFGHIJKLMNOPQRSTUVWXYZ ABCDEFGHIJKLMNOPQRSTUVWXYZ ABCDEFGHIJKLMNOPQRSTUVWXYZ "
					+ "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
			briefingItemDto.addComment(bfcdto);
			
			briefingTestUtils.addBriefingItemImage(briefingItemDto);
			briefingTestUtils.addBriefingItemOutcome(briefingItemDto);
			// update briefing
			briefingItemDto.setModificationStatus(ModificationStatusDto.UPDATE);
			validateRequest();		
			fail("Expected an exception for invalid data");
		} catch (ValidationException ve) {
			String message = ValidationException.convertToXml(ve);			
			assertTrue("ValidationException: " + message,
					message.contains("briefingItemTypeCode must have a value"));
			assertTrue("ValidationException: " + message,
					message.contains("priority must have a value"));
			assertTrue("ValidationException: " + message,
					message.contains("title must have a value"));
			assertTrue("ValidationException: " + message,
					message.contains("validFromDateTime must have a value"));					
			assertTrue("ValidationException: " + message, message.contains(
					"Unexpected child object [child] BriefingItemOutcomeDto {objectRef=-1; modificationStatus=Create}"));
			assertTrue("ValidationException: " + message, message.contains(
					"Unexpected child object [child] BriefingItemAttendeeDto {objectRef=-1; modificationStatus=Create}"));
			/*assertTrue("ValidationException: " + ide.getMessage(), ide.getMessage().contains(
					"Unexpected child object [child] BriefingItemCommentDto {objectRef=-1; modificationStatus=Create}"));*/
			assertTrue("ValidationException: " + message, message.contains(
					"Unexpected child object [child] BriefingItemImageDto {objectRef=-1; modificationStatus=Create}"));
			assertTrue("ValidationException: " + message,
					message.contains("comment must have a value"));
			assertTrue("ValidationException: " + message,
					message.contains("comment is too long"));			
		}
	}
	
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=109,		
		jiraRef = "CCI-43361",
		mingleTitle = "WMP - WP109 - Update Briefing Item - BS Validations", 
		acceptanceCriteriaRefs = "CR2,CR4,CR6,CR11,CR14", 
		given = "Create request for Briefing Item without data as per validation"
				+ "CR2 - briefingItem.briefingItemTypeCode is not valid briefing type,"
				+ "CR4 - briefingItem.title length is more than 100 chars,"
				+ "CR6 - briefingItem.priority is not valid priority,"
				+ "CR11 - briefingItem.validToDateTime is earlier than briefingItem.validFromDateTime,"				
				+ "CR14 - briefingItem.onBehalfOf is not valid", 
		when = "Briefing is submitted",
		then = "Validation errors should be returned to the client as follows."
				+ "CR2 - Mandatory validation error,"
				+ "CR4 - Mandatory validation error,"
				+ "CR6 - Mandatory validation error,"
				+ "CR11 - Mandatory validation error,"				
				+ "CR14 - Unexpected child object error")
	public void testInvalidDataValidationErrorsForCreateBriefingItem() throws Exception {
		try {
			// create briefing
			processRequest();
			briefingItemDto.setBriefingItemTypeCode("XYZ");			
			briefingItemDto.setTitle("ABCDEFGHIJKLMNOPQRSTUVWZXY ABCDEFGHIJKLMNOPQRSTUVWZXY "
					+ "ABCDEFGHIJKLMNOPQRSTUVWZXY ABCDEFGHIJKLMNOPQRSTUVWZXY");
			briefingItemDto.setPriority("XYZ");
			PoleDate toDate = new PoleDate();
			PoleDate fromDate = toDate.addDays(10);
			briefingItemDto.setValidToDateTime(new PoleDateTime(toDate));
			briefingItemDto.setValidFromDateTime(new PoleDateTime(fromDate));
			briefingItemDto.setOnBehalfOf(111);
			
			// update briefing
			briefingItemDto.setModificationStatus(ModificationStatusDto.UPDATE);
			validateRequest();					
			fail("Expected an exception for invalid data");
		} catch (ValidationException ve) {
			String message = ValidationException.convertToXml(ve);			
			assertTrue("ValidationException: " + message, message.contains("title is too long"));
			assertTrue("ValidationException: " + message,
					message.contains("priority has an incorrect CV value for list BRIEFING_PRIORITY"));
			assertTrue("ValidationException: " + message,
					message.contains("onBehalfOf reference data not found for EmployeeIteration"));
			assertTrue("ValidationException: " + message,
					message.contains("briefingItemTypeCode reference data not found for BriefingItemType"));
			assertTrue("ValidationException: " + message,
					message.contains("validFromDateTime cannot be greater than validToDateTime"));			
		}
	}
	
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=109,		
		jiraRef = "CCI-43361",
		mingleTitle = "WMP - WP109 - Update Briefing Item - BS Validations",
		acceptanceCriteriaRefs = "CR16,CR17,CR18", 
		given = "Create request for Briefing Item without data as per validation"
				+ "CR16 - More than one child object briefingItemGeographicalAreaAndCategory are supplied && "
				+ "Geographical or Department hierarchy supplied in two or more child objects "
				+ "(briefingItemGeographicalAreaAndCategory) are same."
				+ "CR17 - briefingItemGeographicalAreaAndCategory.Force is not supplied"
				+ "CR18 - briefingItemGeographicalAreaAndCategory.Force is not valid", 
		when = "Briefing is submitted",
		then = "Validation errors should be returned to the client as follows."
				+ "CR16 - Duplicate entries error should be returned"
				+ "CR17 - Mandatory validation error"
				+ "CR18 - Incorrect value validaiton error")
	public void testGeographicalAreaAndCategoryChildValidationErrors1() throws Exception {
		try {			
			// create briefing
			processRequest();
			
			String force = briefingItemDto.getGeographicalAreasAndCategories().get(0).getForce();
			
			//CR16
			BriefingItemGeographicalAreaAndCategoryDto bfgacdto = new 
					BriefingItemGeographicalAreaAndCategoryDto();
			bfgacdto.setObjectRef(-2);
			bfgacdto.setModificationStatus(ModificationStatusDto.CREATE);
			bfgacdto.setForce(force);
			briefingItemDto.addGeographicalAreaAndCategory(bfgacdto);
			
			//CR17			
			BriefingItemGeographicalAreaAndCategoryDto bfgacdto1 = new 
					BriefingItemGeographicalAreaAndCategoryDto();
			bfgacdto1.setObjectRef(-3);
			bfgacdto1.setModificationStatus(ModificationStatusDto.CREATE);
			bfgacdto1.setBcu("BCU");												
			briefingItemDto.addGeographicalAreaAndCategory(bfgacdto1);
			
			//CR18
			BriefingItemGeographicalAreaAndCategoryDto bfgacdto2 = new 
					BriefingItemGeographicalAreaAndCategoryDto();
			bfgacdto2.setObjectRef(-4);
			bfgacdto2.setModificationStatus(ModificationStatusDto.CREATE);
			bfgacdto2.setForce("FORCE");						
			briefingItemDto.addGeographicalAreaAndCategory(bfgacdto2);						
			
			// update briefing
			briefingItemDto.setModificationStatus(ModificationStatusDto.UPDATE);
			validateRequest();			
			fail("Expected an exception for invalid data");
		} catch (ValidationException ve) {
			String convertToXml = ValidationException.convertToXml(ve);
			assertTrue("ValidationException: " + convertToXml,
					convertToXml.contains("Duplicate entries found for Unit Force hierarchy or Unit Department hierarchy."));			
			assertTrue("ValidationException: " + convertToXml,
					convertToXml.contains("force must have a value"));
			assertTrue("ValidationException: " + convertToXml,
					convertToXml.contains("force has an incorrect CV value for list PNC_FORCE_CODE"));			
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=109,		
		jiraRef = "CCI-43361",
		mingleTitle = "WMP - WP109 - Update Briefing Item - BS Validations",
		acceptanceCriteriaRefs = "CR19,CR20,CR21,CR22,CR23,CR24,CR25", 
		given = "Create request for Briefing Item without data as per validation"
				+ "CR19 - valid briefingItemGeographicalAreaAndCategory.Force is supplied "
				+ "briefingItemGeographicalAreaAndCategory.bcu does not belong to the supplied Force"
				+ "CR20 - valid briefingItemGeographicalAreaAndCategory.Force && "
				+ "briefingItemGeographicalAreaAndCategory.bcu are supplied && "
				+ "briefingItemGeographicalAreaAndCategory.district does not belong to the supplied "
				+ "Force and bcu heirarchy"
				+ "CR21 - valid briefingItemGeographicalAreaAndCategory.Force && "
				+ "briefingItemGeographicalAreaAndCategory.bcu && briefingItemGeographicalAreaAndCategory.district "
				+ "are supplied && briefingItemGeographicalAreaAndCategory.ward does not belong to the supplied Force,"
				+ " bcu and district heirarchy"
				+ "CR22 - valid briefingItemGeographicalAreaAndCategory.Force && " 
				+ "briefingItemGeographicalAreaAndCategory.bcu && briefingItemGeographicalAreaAndCategory.district "
				+ " && briefingItemGeographicalAreaAndCategory.ward are supplied && "
				+ "briefingItemGeographicalAreaAndCategory.unitCode does not belong to the supplied Force, bcu, "
				+ "district and ward heirarchy"
				+ "CR23 - valid briefingItemGeographicalAreaAndCategory.Force is supplied && "
				+ "briefingItemGeographicalAreaAndCategory.unitCode does not belong to the supplied Force"
				+ "CR24 - valid briefingItemGeographicalAreaAndCategory.Force && "
				+ "briefingItemGeographicalAreaAndCategory.bcu are supplied && "
				+ "briefingItemGeographicalAreaAndCategory.unitCode does not belong to the supplied Force and "
				+ "bcu heirarchy"
				+ "CR25 - valid briefingItemGeographicalAreaAndCategory.Force && "
				+ "briefingItemGeographicalAreaAndCategory.bcu && "
				+ "briefingItemGeographicalAreaAndCategory.district are supplied && "
				+ "briefingItemGeographicalAreaAndCategory.unitCode does not belong to the supplied Force, bcu and "
				+ "district heirarchy", 
		when = "Briefing is submitted",
		then = "Validation errors should be returned to the client as follows."
				+ "CR19/CR20/CR21/CR22/CR23/CR24/CR25 - Missing reference data error should be returned")
	public void testGeographicalAreaAndCategoryChildValidationErrors2() throws Exception {
		try {
			// create briefing
			processRequest();
			
			String force = briefingItemDto.getGeographicalAreasAndCategories().get(0).getForce();
			briefingItemDto.setGeographicalAreasAndCategories(null);
			//CR19,CR20,CR21,CR22,CR23,CR24,CR25
			BriefingItemGeographicalAreaAndCategoryDto bfgacdto2 = new 
					BriefingItemGeographicalAreaAndCategoryDto();
			bfgacdto2.setObjectRef(-4);
			bfgacdto2.setModificationStatus(ModificationStatusDto.CREATE);
			bfgacdto2.setForce(force);
			bfgacdto2.setBcu("BCU");
			bfgacdto2.setDistrict("DISTRICT");
			bfgacdto2.setWard("WARD");
			bfgacdto2.setUnitCode("UNIT_CODE");
			
			briefingItemDto.addGeographicalAreaAndCategory(bfgacdto2);						
									
			// update briefing
			briefingItemDto.setModificationStatus(ModificationStatusDto.UPDATE);
			validateRequest();		
			fail("Expected an exception for invalid data");
		} catch (ValidationException ve) {	
			String message = ValidationException.convertToXml(ve);
			//CR19,CR20,CR21,CR22,CR23,CR24,CR25			
			assertTrue("ValidationException: " + message,
					message.contains("BCU or District or Ward details do not match Unit Force hierarchy."));						
			// other cv list related errors
			assertTrue("ValidationException: " + message,
					message.contains("bcu has an incorrect CV value for list BCU"));
			assertTrue("ValidationException: " + message,
					message.contains("district has an incorrect CV value for list DISTRICT"));
			assertTrue("ValidationException: " + message,
					message.contains("ward has an incorrect CV value for list WARD"));
			assertTrue("ValidationException: " + message,
					message.contains("unitCode reference data not found for Unit"));					
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=109,		
		jiraRef = "CCI-43361",
		mingleTitle = "WMP - WP109 - Update Briefing Item - BS Validations",
		acceptanceCriteriaRefs = "CR26,CR27,CR28", 
		given = "Create request for Briefing Item without data as per validation"				
				+ "CR26 - briefingItemGeographicalAreaAndCategory.departmentCode is not valid"
				+ "CR27 - valid briefingItemGeographicalAreaAndCategory.Force is supplied && "
				+ "briefingItemGeographicalAreaAndCategory.departmentCode does not belong to the supplied Force"
				+ "CR28 - valid briefingItemGeographicalAreaAndCategory.Force && "
				+ "briefingItemGeographicalAreaAndCategory.departmentCode are supplied && "
				+ "briefingItemGeographicalAreaAndCategory.unitCode does not belong to the supplied Force, "
				+ "department heirarchy", 
		when = "Briefing is submitted",
		then = "Validation errors should be returned to the client as follows."				
				+ "CR26/CR27/CR28 - Missing reference data error should be returned")
	public void testGeographicalAreaAndCategoryChildValidationErrors3() throws Exception {
		try {
			// create briefing
			processRequest();
			
			String force = briefingItemDto.getGeographicalAreasAndCategories().get(0).getForce();
			briefingItemDto.setGeographicalAreasAndCategories(null);			
			
			//CR26,CR27,CR28
			BriefingItemGeographicalAreaAndCategoryDto bfgacdto3 = new 
					BriefingItemGeographicalAreaAndCategoryDto();
			bfgacdto3.setObjectRef(-5);
			bfgacdto3.setModificationStatus(ModificationStatusDto.CREATE);
			bfgacdto3.setForce(force);
			bfgacdto3.setDepartmentCode("DEPARTMENT_CODE");
			bfgacdto3.setUnitCode("UNIT_CODE");
			
			briefingItemDto.addGeographicalAreaAndCategory(bfgacdto3);
									
			// update briefing
			briefingItemDto.setModificationStatus(ModificationStatusDto.UPDATE);
			validateRequest();			
			fail("Expected an exception for invalid data");
		} catch (ValidationException ve) {	
			String message = ValidationException.convertToXml(ve);
			//CR26,CR27,CR28			
			assertTrue("ValidationException: " + message,
					message.contains("Department details do not match Unit Department hierarchy."));
			// other cv list related errors			
			assertTrue("ValidationException: " + message,
					message.contains("unitCode reference data not found for Unit"));
			assertTrue("ValidationException: " + message,
					message.contains("departmentCode has an incorrect CV value for list DEPARTMENT_CODE"));			
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=109,		
		jiraRef = "CCI-43361",
		mingleTitle = "WMP - WP109 - Update Briefing Item - BS Validations",
		acceptanceCriteriaRefs = "CR29", 
		given = "Create request for Briefing Item without data as per validation"
				+ "CR29 - briefingItemGeographicalAreaAndCategory.Force OR briefingItemGeographicalAreaAndCategory.bcu "
				+ "OR briefingItemGeographicalAreaAndCategory.district OR briefingItemGeographicalAreaAndCategory.ward "
				+ "is supplied && briefingItemGeographicalAreaAndCategory.departmentCode is supplied", 
		when = "Briefing is submitted",
		then = "Validation errors should be returned to the client as follows."
				+ "CR29 - Forbidden validation error")
	public void testGeographicalAreaAndCategoryChildValidationErrors4() throws Exception {
		try {	
			// create briefing
			processRequest();
			
			String force = briefingItemDto.getGeographicalAreasAndCategories().get(0).getForce();
			briefingItemDto.setGeographicalAreasAndCategories(null);
			
			BriefingItemGeographicalAreaAndCategoryDto bfgacdto2 = new 
					BriefingItemGeographicalAreaAndCategoryDto();
			bfgacdto2.setObjectRef(-4);
			bfgacdto2.setModificationStatus(ModificationStatusDto.CREATE);
			bfgacdto2.setForce(force);
			bfgacdto2.setBcu("BCU");
			bfgacdto2.setDistrict("DISTRICT");
			bfgacdto2.setWard("WARD");
			bfgacdto2.setDepartmentCode("DEPARTMENT_CODE");
			bfgacdto2.setUnitCode("UNIT_CODE");
			
			briefingItemDto.addGeographicalAreaAndCategory(bfgacdto2);
			
			// update briefing
			briefingItemDto.setModificationStatus(ModificationStatusDto.UPDATE);
			validateRequest();			
			fail("Expected an exception for invalid data");
		} catch (ValidationException ve) {
			String message = ValidationException.convertToXml(ve);
			//CR29			
			assertTrue("ValidationException: " + message, message.contains(
					"BCU,District,Ward and Department related information is supplied in a single record which is forbidden."));
			// other cv list related errors
			assertTrue("ValidationException: " + message,
					message.contains("bcu has an incorrect CV value for list BCU"));
			assertTrue("ValidationException: " + message,
					message.contains("district has an incorrect CV value for list DISTRICT"));
			assertTrue("ValidationException: " + message,
					message.contains("ward has an incorrect CV value for list WARD"));
			assertTrue("ValidationException: " + message,
					message.contains("unitCode reference data not found for Unit"));
		}
	}
	
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage = 109, 
		jiraRef = "CCI-43361",
		mingleTitle = "WMP - WP109 - Update Briefing Item - BS Validations",
		acceptanceCriteriaRefs = "NA(Remove link scenario)",
		given = "BriefingItem is existing and we try to remove link",
		when = "Briefing Item is Submitted",
		then = "The Briefing Item should sucessfully be updated and link should be removed")
	public void testUpdateBriefingItemRemoveLinkSuccess() throws Exception {
		LocationDto locationDto = CommonPoleObjectTestUtils.getLocation("1", "High Street", "Nowhere", 
				"Nowheresville", "NW1 1VL", "42", null, null);
		locationDto.setResearched(Boolean.TRUE );
		locationDto.setObjectRef(new Integer(-1));
		LinkDto locationBriefingLink = new LinkDto();
		locationBriefingLink.setModificationStatus(ModificationStatusDto.CREATE);
		locationBriefingLink.setResearched(Boolean.TRUE);
		locationBriefingLink.setSourcePoleObjectType(PoleNames.BRIEFING_ITEM);
		locationBriefingLink.setSourcePoleObjectRef(new Integer(-1));
		locationBriefingLink.setToPoleObject(locationDto);
		locationBriefingLink.setLinkReason(ASSOCIATED_WITH_LINK_REASON);
		briefingItemDto.addLink(locationBriefingLink);				
		// create briefing
		processRequest();
		assertEquals("Briefing Item links size should be '1'", briefingItemDto.getLinks().size(), 1);
		
		LinkDto existingLinkDto = briefingItemDto.getLinks().get(0);
		
		LinkDto scenarioLinkDto = new LinkDto(); 
		scenarioLinkDto.setObjectRef(existingLinkDto.getObjectRef());
		scenarioLinkDto.setModificationStatus(ModificationStatusDto.REMOVE);
		
		briefingItemDto.setLinks(null);
		briefingItemDto.addLink(scenarioLinkDto);			   
		
		processRequest();
		assertEquals("Briefing Item links size should be '0'", briefingItemDto.getLinks().size(), 0);
	}
	

	@Override
	protected void teardownImpl() throws Exception {
		deleteData();
	}

	/**
	 * A method to cleanup BriefingData after test
	 * 
	 * @throws Exception
	 */
	private void deleteData() throws Exception {
		try {
			if (briefingItemDto != null) {
				briefingTestUtils.removeBriefingItemFromPole(briefingItemDto.getObjectRef(), 2, poleDirect,
						securityContextId);
			}
		} catch (Exception e) {
			logger.debug("Failed to delete Incident having objectRef {} ", briefingItemDto.getObjectRef(), e);
		}
	}

	private void processRequest() throws Exception {

		PutPoleObjectsRequestDto putPoleObjectsRequest = briefingTestUtils.createPutPoleObjectsRequestDto(
				BusinessServicesTestUtils.createBusinessServiceInfo(securityContextId), briefingItemDto);

		PutPoleObjectsResponseDto poleObjectsResponse = poleBusinessServices.putPoleObjects(putPoleObjectsRequest);
		assertNotNull(getBusinessServiceResultInfo(poleObjectsResponse).getTransactionId());

		Integer briefingItemObjectRef = briefingTestUtils.extractBriefingItemObjectRef(poleObjectsResponse);
		if(briefingItemObjectRef == null) {
			briefingItemObjectRef = poleObjectsResponse.getObjectRefs().get(0);
		}

		briefingItemDto = briefingTestUtils.getBriefingItemFromPole(briefingItemObjectRef, 10, poleDirect,
				securityContextId);
		assertEquals(briefingItemDto.getObjectRef(), briefingItemObjectRef);
	}
	
	private void validateRequest() throws Exception {
		PutPoleObjectsRequestDto putPoleObjectsRequest = briefingTestUtils.createPutPoleObjectsRequestDto(
				BusinessServicesTestUtils.createBusinessServiceInfo(securityContextId), briefingItemDto);
		
		Map<EntityKey, PoleEntityDto> originalObjectsMap = new HashMap<EntityKey, PoleEntityDto>();
		validator.validate(putPoleObjectsRequest, originalObjectsMap);
	}
}
