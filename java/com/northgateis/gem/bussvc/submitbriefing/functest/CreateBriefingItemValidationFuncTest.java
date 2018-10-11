package com.northgateis.gem.bussvc.submitbriefing.functest;

import static com.northgateis.gem.bussvc.pole.utils.PoleDtoUtils.addBusinessServiceInfo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import com.northgateis.gem.bussvc.framework.exception.ValidationException;
import com.northgateis.gem.bussvc.framework.test.BusSvcStoryAcceptanceCriteriaReference;
import com.northgateis.gem.bussvc.poleobjects.constants.PoleObjectsServiceConstants;
import com.northgateis.gem.bussvc.submitincident.utils.IncidentPoleDtoUtils;
import com.northgateis.gem.bussvc.submitintelreport.utils.IntelReportPoleDtoUtils;
import com.northgateis.gem.bussvc.test.util.BriefingItemTestUtils;
import com.northgateis.gem.bussvc.test.util.BusinessServicesTestUtils;
import com.northgateis.gem.bussvc.test.util.CommonPoleObjectTestUtils;
import com.northgateis.gem.bussvc.test.util.IncidentTestUtils;
import com.northgateis.gem.bussvc.test.util.IntelReportTestUtils;
import com.northgateis.pole.client.PoleDtoBuildHelper;
import com.northgateis.pole.common.EntityKey;
import com.northgateis.pole.common.PoleDate;
import com.northgateis.pole.common.PoleDateTime;
import com.northgateis.pole.common.PoleNames;
import com.northgateis.pole.schema.BriefingItemDto;
import com.northgateis.pole.schema.BriefingItemGeographicalAreaAndCategoryDto;
import com.northgateis.pole.schema.IncidentDto;
import com.northgateis.pole.schema.IntelligenceReportDto;
import com.northgateis.pole.schema.LinkDto;
import com.northgateis.pole.schema.LocationDto;
import com.northgateis.pole.schema.ModificationStatusDto;
import com.northgateis.pole.schema.PoleEntityDto;
import com.northgateis.pole.schema.PoleObjectCriteriaDto;
import com.northgateis.pole.schema.PoleObjectDto;
import com.northgateis.pole.schema.PutPoleObjectsRequestDto;
import com.northgateis.pole.schema.PutPoleObjectsResponseDto;
import com.northgateis.pole.schema.SearchDto;
import com.northgateis.pole.schema.VehicleDto;

/**
 * Functional tests for Creating Briefing and check validation 
 * 
 * @author amit.desai/harsh.shah
 */

public class CreateBriefingItemValidationFuncTest extends AbstractBriefingFuncTestBase {
	
	private BriefingItemDto briefingItemDto;
	private IncidentDto incidentDto;
	private IntelligenceReportDto intelReport;
	private SearchDto searchDto;	
	
	@Override
	protected void setupImpl() throws Exception {
		super.setupImpl();
		briefingItemDto = null;
		briefingItemDto = briefingTestUtils.
				createBriefingItem(-1, Integer.valueOf(officerReportingId));
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=109,		
		mingleTitle = "WMP - WP109 - Create Briefing Item - BS Validations", 
		acceptanceCriteriaRefs = "Happy Path", 
		given = "Create request for Briefing Item with all the required data as per validation", 
		when = "Request is sent to BS",
		then = "The Briefing Item and child objects are sucessfully saved to Pole")
	public void testHappyPathSaveBriefingItem() throws Exception {		
		testEntitySave(briefingItemDto);		
	}			
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=109,		
		mingleTitle = "WMP - WP109 - Create Briefing Item - BS Validations", 
		acceptanceCriteriaRefs = "CR1,CR3,CR5,CR7,CR15,CR30,CR31,CR32,CR33", 
		given = "Create request for Briefing Item without data as per validation"
				+ "CR1 - briefingItem.briefingItemTypeCode == null,"
				+ "CR3 - briefingItem.title == null,"
				+ "CR5 - briefingItem.priority == null,"
				+ "CR7 - briefingItem.validFromDateTime == null,"
				+ "CR15 - child object briefingItemGeographicalAreaAndCategory is not supplied,"		
				+ "CR30 - child object briefingItemComment is supplied,"
				+ "CR31 - child object briefingItemOutcome is supplied,"
				+ "CR32 - child object briefingItemAttendee is supplied,"
				+ "CR33 - child object briefingItemImage is supplied", 
		when = "Briefing is submitted",
		then = "Validation errors should be returned to the client as follows."
				+ "CR1 - Mandatory validation error,"
				+ "CR3 - Mandatory validation error,"
				+ "CR5 - Mandatory validation error,"
				+ "CR7 - Mandatory validation error,"
				+ "CR15 - Mandatory validation error,"	
				+ "CR30 - Unexpected child object error,"
				+ "CR31 - Unexpected child object error,"
				+ "CR32 - Unexpected child object error,"
				+ "CR33 - Unexpected child object error")
	public void testMandatoryOrForbiddenValidationErrorsForCreateBriefingItem() throws Exception {
		try {
			briefingItemDto.setBriefingItemTypeCode(null);
			briefingItemDto.setPriority(null);
			briefingItemDto.setTitle(null);
			briefingItemDto.setValidFromDateTime(null);
			briefingItemDto.setGeographicalAreasAndCategories(null);
			briefingTestUtils.addBriefingItemAttendee(briefingItemDto, officerReportingId);
			briefingTestUtils.addBriefingItemComment(briefingItemDto);
			briefingTestUtils.addBriefingItemImage(briefingItemDto);
			briefingTestUtils.addBriefingItemOutcome(briefingItemDto);			
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
					"Found 0 instance(s) of BriefingItemGeographicalAreaAndCategory but at least 1 instance(s) required"));			
			assertTrue("ValidationException: " + message, message.contains(
					"Unexpected child object [child] BriefingItemOutcomeDto {objectRef=-1; modificationStatus=Create}"));
			assertTrue("ValidationException: " + message, message.contains(
					"Unexpected child object [child] BriefingItemAttendeeDto {objectRef=-1; modificationStatus=Create}"));
			assertTrue("ValidationException: " + message, message.contains(
					"Unexpected child object [child] BriefingItemCommentDto {objectRef=-1; modificationStatus=Create}"));
			assertTrue("ValidationException: " + message, message.contains(
					"Unexpected child object [child] BriefingItemImageDto {objectRef=-1; modificationStatus=Create}"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=109,		
		mingleTitle = "WMP - WP109 - Create Briefing Item - BS Validations", 
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
			briefingItemDto.setBriefingItemTypeCode("XYZ");			
			briefingItemDto.setTitle("ABCDEFGHIJKLMNOPQRSTUVWZXY ABCDEFGHIJKLMNOPQRSTUVWZXY "
					+ "ABCDEFGHIJKLMNOPQRSTUVWZXY ABCDEFGHIJKLMNOPQRSTUVWZXY");
			briefingItemDto.setPriority("XYZ");
			PoleDate toDate = new PoleDate();
			PoleDate fromDate = toDate.addDays(10);
			briefingItemDto.setValidToDateTime(new PoleDateTime(toDate));
			briefingItemDto.setValidFromDateTime(new PoleDateTime(fromDate));
			briefingItemDto.setOnBehalfOf(111);
			
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
		mingleTitle = "WMP - WP109 - Create Briefing Item - BS Validations", 
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
			
			validateRequest();			
			fail("Expected an exception for invalid data");
		} catch (ValidationException ve) {
			String message = ValidationException.convertToXml(ve);			
			assertTrue("ValidationException: " + message,
					message.contains("Duplicate entries found for Unit Force hierarchy or Unit Department hierarchy."));			
			assertTrue("ValidationException: " + message,
					message.contains("force must have a value"));
			assertTrue("ValidationException: " + message,
					message.contains("force has an incorrect CV value for list PNC_FORCE_CODE"));			
		}
	}		
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=109,		
		mingleTitle = "WMP - WP109 - Create Briefing Item - BS Validations", 
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
									
			validateRequest();		
			fail("Expected an exception for invalid data");
		} catch (ValidationException ve) {	
			//CR19,CR20,CR21,CR22,CR23,CR24,CR25			
			String message = ValidationException.convertToXml(ve);
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
		mingleTitle = "WMP - WP109 - Create Briefing Item - BS Validations", 
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
									
			validateRequest();			
			fail("Expected an exception for invalid data");
		} catch (ValidationException ve) {					
			//CR26,CR27,CR28			
			String message = ValidationException.convertToXml(ve);
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
		mingleTitle = "WMP - WP109 - Create Briefing Item - BS Validations", 
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
			
			validateRequest();			
			fail("Expected an exception for invalid data");
		} catch (ValidationException ve) {
			//CR29			
			String message = ValidationException.convertToXml(ve);
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
	@Ignore("This test will always fail because it breaks business rules by trying to create BriefingItem at the same time as IntelligenceReport. These should be separate steps.")
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=109,
		jiraRef="CCI-42969",		
		mingleTitle = "WMP - WP109 - Create Briefing Item - BS Validations", 
		acceptanceCriteriaRefs = "CR35.1", 
		given = " An Intelligence Report is linked to a Briefing Item with Link Reason as 'Subject Of'" ,
		when = "Business Service receives the graph",
		then = " Link must be created " )
	public void testSaveIntelligenceReportLinkedToBriefingItem() throws Exception {		
		intelReport = intelUtils.createIntelReportWithNoLinks(new Integer(-1), officerReportingId);		
		LinkDto intelReportBriefingLink = new LinkDto();
		intelReportBriefingLink.setModificationStatus(ModificationStatusDto.CREATE);
		intelReportBriefingLink.setResearched(Boolean.TRUE);
		intelReportBriefingLink.setSourcePoleObjectType(PoleNames.INTELLIGENCE_REPORT);
		intelReportBriefingLink.setSourcePoleObjectRef(new Integer(-1));
		intelReportBriefingLink.setToPoleObject(briefingItemDto);
		intelReportBriefingLink.setLinkReason(PoleObjectsServiceConstants.SUBJECT_OF_LINK_REASON);
		intelReport.addLink(intelReportBriefingLink);
		testEntitySave(intelReport);		
	}
		
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=109,
		jiraRef="CCI-42969",		
		mingleTitle = "WMP - WP109 - Create Briefing Item - BS Validations", 
		acceptanceCriteriaRefs = "CR35.2", 
		given = " An Investigation is linked to a Briefing Item with Link Reason as 'Subject Of' " ,
		when = "Business Service receives the graph",
		then = " Link must be created " )
	public void testSaveInvestigationLinkedToBriefingItem() throws Exception {		
		incidentDto = utils.prepareIncidentWithResearchLinks(-1, Integer.valueOf(officerReportingId));
		LinkDto incidentBriefingLink = new LinkDto();
		incidentBriefingLink.setModificationStatus(ModificationStatusDto.CREATE);
		incidentBriefingLink.setResearched(Boolean.TRUE);
		incidentBriefingLink.setSourcePoleObjectType(PoleNames.INCIDENT);
		incidentBriefingLink.setSourcePoleObjectRef(new Integer(-1));
		incidentBriefingLink.setToPoleObject(briefingItemDto);
		incidentBriefingLink.setLinkReason(PoleObjectsServiceConstants.SUBJECT_OF_LINK_REASON);
		incidentDto.addLink(incidentBriefingLink);
		testEntitySave(incidentDto);		
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=109,
		jiraRef="CCI-42969",		
		mingleTitle = "WMP - WP109 - Create Briefing Item - BS Validations", 
		acceptanceCriteriaRefs = "CR35.3", 
		given = " A Stop Search is linked to a Briefing Item with Link Reason as 'Subject Of' " ,
		when = "Business Service receives the graph",
		then = " Link must be created " )
	public void testSaveStopSearchLinkedToBriefingItem() throws Exception {		
		searchDto =  stopSearchTestUtils.createStopSearch(-1);
		LinkDto searchBriefingLink = new LinkDto();
		searchBriefingLink.setModificationStatus(ModificationStatusDto.CREATE);
		searchBriefingLink.setResearched(Boolean.TRUE);
		searchBriefingLink.setSourcePoleObjectType(PoleNames.SEARCH );
		searchBriefingLink.setSourcePoleObjectRef(new Integer(-1));
		searchBriefingLink.setToPoleObject(briefingItemDto);
		searchBriefingLink.setLinkReason(PoleObjectsServiceConstants.SUBJECT_OF_LINK_REASON);
		searchDto.addLink(searchBriefingLink);
		testEntitySave(searchDto);		
	}
		
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=109,
		jiraRef="CCI-42969",		
		mingleTitle = "WMP - WP109 - Create Briefing Item - BS Validations", 
		acceptanceCriteriaRefs = "CR35.4", 
		given = " A vehicle is linked to a Briefing Item with Link Reason as 'Subject Of' " ,
		when = "Business Service receives the graph",
		then = " Link must be created " )
	public void testSaveVehicleLinkedToBriefingItem() throws Exception {		
		
		boolean isResearched = Boolean.TRUE;

		//Add Briefing Link to
		VehicleDto vehicleDto = CommonPoleObjectTestUtils.
				getVehicleObject("AB123CD", "HAT", "FULL", false, false, false, false, isResearched);
		vehicleDto.setObjectRef(new Integer(-111));
		LinkDto vehicleBriefingLink = new LinkDto();
		vehicleBriefingLink.setModificationStatus(ModificationStatusDto.CREATE);
		vehicleBriefingLink.setResearched(isResearched);
		vehicleBriefingLink.setSourcePoleObjectType(PoleNames.BRIEFING_ITEM );
		vehicleBriefingLink.setSourcePoleObjectRef(new Integer(-1));
		vehicleBriefingLink.setToPoleObject(vehicleDto);
		vehicleBriefingLink.setLinkReason(PoleObjectsServiceConstants.ASSOCIATED_WITH_LINK_REASON);
		briefingItemDto.addLink(vehicleBriefingLink);		
		testEntitySave(briefingItemDto);
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=109,
		jiraRef="CCI-42969",		
		mingleTitle = "WMP - WP109 - Create Briefing Item - BS Validations", 
		acceptanceCriteriaRefs = "CR35.5", 
		given = " A Location is linked to a Briefing Item wih Link Reason as 'Subject Of'" ,
		when = "Business Service receives the graph",
		then = " Link must be created " )
	public void testSaveLocationLinkedToBriefingItem() throws Exception {		
		LocationDto locationDto = CommonPoleObjectTestUtils.getLocation("1", "High Street", "Nowhere", 
				"Nowheresville", "NW1 1VL", "42", null, null);
		locationDto.setResearched(Boolean.TRUE );
		locationDto.setObjectRef(new Integer(-111));
		LinkDto locationBriefingLink = new LinkDto();
		locationBriefingLink.setModificationStatus(ModificationStatusDto.CREATE);
		locationBriefingLink.setResearched(Boolean.TRUE);
		locationBriefingLink.setSourcePoleObjectType(PoleNames.BRIEFING_ITEM);
		locationBriefingLink.setSourcePoleObjectRef(new Integer(-1));
		locationBriefingLink.setToPoleObject(locationDto);
		locationBriefingLink.setLinkReason(PoleObjectsServiceConstants.ASSOCIATED_WITH_LINK_REASON);
		briefingItemDto.addLink(locationBriefingLink);
		testEntitySave(briefingItemDto);		
	}
	
	@Override
	protected void teardownImpl() throws Exception {
		deleteData();
	}

	/**
	 * A method to cleanup BriefingData after test
	 * @throws Exception
	 */
	private void deleteData() throws Exception {
		try {
			if (briefingItemDto != null) {
				briefingTestUtils.removeBriefingItemFromPole(briefingItemDto.getObjectRef(), 2, poleDirect,
						securityContextId);
			}
			if (incidentDto != null) {
				utils.removeIncidentFromPole(incidentDto.getObjectRef(), 2, poleDirect, securityContextId);
			}
			if (intelReport != null) {
				intelUtils.removeIntelligenceReportFromPole(intelReport.getObjectRef(), 2, poleDirect,
						securityContextId);
			}
			if (searchDto != null) {
				stopSearchTestUtils.removeStopSearchFromPole(searchDto.getObjectRef(), 2, poleDirect,
						securityContextId);
			}			
		} catch (Exception e) {
			logger.debug("Failed to delete Incident having objectRef {} ", 
					briefingItemDto.getObjectRef(), e);
		}
	}
	
	/**
	 * A common method to save different entities based on type(eg Briefing,Intelligence Report,etc)
	 * 
	 * @param dto : The Input Dto for above type to be saved to pole
	 * @throws Exception
	 */
	private void testEntitySave(PoleObjectDto dto) throws Exception {
		PutPoleObjectsRequestDto ppoReq = null;
		PutPoleObjectsResponseDto poleObjectsResponse = null;
		
		if (dto instanceof BriefingItemDto) {
			ppoReq = briefingTestUtils.createPutPoleObjectsRequestDto(
					BriefingItemTestUtils.createBusinessServiceInfo(securityContextId), (BriefingItemDto) dto);
			
			BusinessServicesTestUtils.applyScopedTransactionExtraInfo(ppoReq, BriefingItemDto.class, "Create", null, null);
			
			poleObjectsResponse = poleBusinessServices.putPoleObjects(ppoReq);
			Integer objRef = briefingTestUtils.extractBriefingObjectRef(poleObjectsResponse);

			briefingItemDto = briefingTestUtils.getBriefingItemFromPole(objRef, 2, poleBusinessServices,
					securityContextId);
			assertEquals(briefingItemDto.getObjectRef(), objRef);
			
		} else if (dto instanceof IntelligenceReportDto) {
			ppoReq = intelUtils.createPutPoleObjectsRequestDto(
					IntelReportTestUtils.createBusinessServiceInfo(securityContextId), (IntelligenceReportDto) dto);
			poleObjectsResponse = poleBusinessServices.putPoleObjects(ppoReq);
			Integer objRef = IntelReportPoleDtoUtils.extractIntelReportObjectRef(poleObjectsResponse);
			IntelligenceReportDto intelReport;
			intelReport = intelUtils.getIntelligenceReportFromPole(objRef, 10, poleBusinessServices,
					securityContextId);
			assertEquals(intelReport.getObjectRef(), objRef);
			
		} else if (dto instanceof IncidentDto) {
			ppoReq = utils.createPutPoleObjectsRequestDto(
					IncidentTestUtils.createBusinessServiceInfo(securityContextId), (IncidentDto) dto);
			poleObjectsResponse = poleBusinessServices.putPoleObjects(ppoReq);
			Integer objRef = IncidentPoleDtoUtils.extractIncidentObjectRef(poleObjectsResponse);
			IncidentDto incidentDto;
			incidentDto = utils.getIncidentFromPole(objRef, poleBusinessServices, securityContextId);
			assertEquals(incidentDto.getObjectRef(), objRef);
			
		} else if (dto instanceof VehicleDto) {
			PutPoleObjectsRequestDto vehicleRequest = new PutPoleObjectsRequestDto();
			addBusinessServiceInfo(vehicleRequest, IncidentTestUtils.createBusinessServiceInfo(securityContextId));
			vehicleRequest.addPoleObject((VehicleDto) dto);
			poleObjectsResponse = poleBusinessServices.putPoleObjects(vehicleRequest);

			Integer objRef = briefingTestUtils.getObjectRefByType(poleObjectsResponse, PoleNames.VEHICLE);
			VehicleDto vehicleDto;
			PoleObjectCriteriaDto rootCriteriaDto = PoleDtoBuildHelper.buildPoleObjectCriteriaDto(PoleNames.VEHICLE,
					PoleDtoBuildHelper.buildIntegerCriterionDto("objectRef", objRef));
			vehicleDto = briefingTestUtils.getObjectFromPoleByType(rootCriteriaDto, poleBusinessServices,
					securityContextId, PoleNames.VEHICLE, VehicleDto.class);
			assertEquals(vehicleDto.getObjectRef(), objRef);
			
		} else if (dto instanceof LocationDto) {
			PutPoleObjectsRequestDto locationRequest = new PutPoleObjectsRequestDto();
			addBusinessServiceInfo(locationRequest, IncidentTestUtils.createBusinessServiceInfo(securityContextId));
			locationRequest.addPoleObject((LocationDto) dto);
			poleObjectsResponse = poleBusinessServices.putPoleObjects(locationRequest);

			Integer objRef = briefingTestUtils.getObjectRefByType(poleObjectsResponse, PoleNames.LOCATION);
			LocationDto locationDto;
			PoleObjectCriteriaDto rootCriteriaDto = PoleDtoBuildHelper.buildPoleObjectCriteriaDto(PoleNames.LOCATION,
					PoleDtoBuildHelper.buildIntegerCriterionDto("objectRef", objRef));
			locationDto = briefingTestUtils.getObjectFromPoleByType(rootCriteriaDto, poleBusinessServices,
					securityContextId, PoleNames.LOCATION, LocationDto.class);
			assertEquals(locationDto.getObjectRef(), objRef);
		}
	}
	
	private void validateRequest() throws Exception {
		PutPoleObjectsRequestDto putPoleObjectsRequest = briefingTestUtils.createPutPoleObjectsRequestDto(
				BusinessServicesTestUtils.createBusinessServiceInfo(securityContextId), briefingItemDto);
		
		Map<EntityKey, PoleEntityDto> originalObjectsMap = new HashMap<EntityKey, PoleEntityDto>();
		validator.validate(putPoleObjectsRequest, originalObjectsMap);
	}
}
