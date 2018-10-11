package com.northgateis.gem.bussvc.submitincident.functest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.northgateis.gem.bussvc.framework.exception.ValidationException;
import com.northgateis.gem.bussvc.framework.schema.BusinessServiceValidationError;
import com.northgateis.gem.bussvc.framework.schema.BusinessServiceValidationErrors;
import com.northgateis.gem.bussvc.framework.test.BusSvcStoryAcceptanceCriteriaReference;
import com.northgateis.gem.bussvc.pole.utils.PoleDtoUtils;
import com.northgateis.gem.bussvc.poleobjects.appliedlogic.workflow.ObjectActionPlanTaskHelperBean;
import com.northgateis.gem.bussvc.poleobjects.constants.PoleObjectsServiceConstants;
import com.northgateis.gem.bussvc.poleobjects.functest.AbstractPoleObjectCommonValidationRulesFuncTest;
import com.northgateis.gem.bussvc.submitcontact.utils.ContactEventPoleDtoUtils;
import com.northgateis.gem.bussvc.submitincident.constants.SubmitIncidentServiceConstants;
import com.northgateis.gem.bussvc.submitincident.utils.IncidentPoleDtoUtils;
import com.northgateis.gem.bussvc.test.util.BusinessServicesTestUtils;
import com.northgateis.gem.bussvc.test.util.CommonPoleObjectTestUtils;
import com.northgateis.gem.bussvc.test.util.GraphSetObjectRefGraphWalkerTestUtil;
import com.northgateis.gem.bussvc.test.util.IncidentTestUtils;
import com.northgateis.gem.bussvc.test.util.TaskSummaryData;
import com.northgateis.pole.common.EntityKey;
import com.northgateis.pole.common.InvalidDataException;
import com.northgateis.pole.common.PoleDate;
import com.northgateis.pole.common.PoleDateTime;
import com.northgateis.pole.common.PoleNames;
import com.northgateis.pole.common.PoleTime;
import com.northgateis.pole.common.PoleTimePoint;
import com.northgateis.pole.schema.ActionPlanUpdateDto;
import com.northgateis.pole.schema.ContactEventDto;
import com.northgateis.pole.schema.CreateIncidentTxDataDto;
import com.northgateis.pole.schema.EventDto;
import com.northgateis.pole.schema.ExternalReferenceDto;
import com.northgateis.pole.schema.IncidentClassification2Dto;
import com.northgateis.pole.schema.IncidentClassificationDto;
import com.northgateis.pole.schema.IncidentDto;
import com.northgateis.pole.schema.IncidentInitialClassificationDto;
import com.northgateis.pole.schema.IncidentInterestDto;
import com.northgateis.pole.schema.IncidentLogEntryDto;
import com.northgateis.pole.schema.IncidentPersonEventDto;
import com.northgateis.pole.schema.IncidentPersonInfoDto;
import com.northgateis.pole.schema.IncidentPersonInfoUpdateDto;
import com.northgateis.pole.schema.IncidentPersonVulnerabilityDto;
import com.northgateis.pole.schema.IncidentPoliceAttendanceDto;
import com.northgateis.pole.schema.IncidentReferralDto;
import com.northgateis.pole.schema.LinkDto;
import com.northgateis.pole.schema.LocationDto;
import com.northgateis.pole.schema.ModificationStatusDto;
import com.northgateis.pole.schema.ObjectActionPlanDto;
import com.northgateis.pole.schema.OrganisationDto;
import com.northgateis.pole.schema.PeContactInfoDto;
import com.northgateis.pole.schema.PersonDto;
import com.northgateis.pole.schema.PoleEntityDto;
import com.northgateis.pole.schema.PoleObjectDto;
import com.northgateis.pole.schema.PolicyFileDto;
import com.northgateis.pole.schema.PutPoleObjectsRequestDto;
import com.northgateis.pole.schema.PutPoleObjectsResponseDto;
import com.northgateis.pole.schema.VehicleDto;

/**
 * Functional tests for Creating Incident and check validation #37149 ,#37152 
 * 
 * 
 */
public class CreateIncidentValidationFuncTest extends AbstractPoleObjectCommonValidationRulesFuncTest {

	private IncidentDto incidentDto;
	
	/**
	 * When non-null, will be added to the incident before submitting to BS.
	 */
	private ObjectActionPlanDto objectActionPlan;
	
	/**
	 * When non-null, will be added to the incident before submitting to BS.
	 */
	private PolicyFileDto policyFile;
	
	/**
	 * To be set to true in functional tests where we are dealing with objectActionPlans and need 
	 * to assert workflow has been succesful. If set to true, and if the test does not
	 * provide an objectActionPlan, then the 'submit' util method will add a default
	 * objectActionPlan to the graph before submission.
	 */
	private boolean incidentWithValidObjectActionPlan;
	
	public static final String INCIDENT_LOCATION_LINK_REASON = "INCIDENT LOCATION";
	private static final String QA_NEW_INVESTIGATION_EXTERNAL_TASK_NAME = "QA New Investigation";
	private static final String PERFORM_TASK_TASK_NAME = "Perform Task";
	private static final String COMPLETE_INVESTIGATION_CREATION = "Complete Investigation Creation";
	public static final String REQUIRES_COMPLETION =  "REQUIRES COMPLETION";
	public static final String REQUIRES_QA =  "REQUIRES QA";

	private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
	
	@Override
	protected void setupImpl() throws Exception {
		
		incidentDto = null;
		objectActionPlan = null;
		policyFile = null;
		
		incidentWithValidObjectActionPlan = false;
		
		incidentDto = utils.createIncident(-1, Integer.valueOf(officerReportingId));
		
	}

	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=215,
		mingleRef = 37149, 
		mingleTitle = "Business Service- Validaton framework- Create Investigation", 
		acceptanceCriteriaRefs = "CR1.1,CR1.2", 
		given = "txData.completeFlag == null", 
		when = "Incident Submited",
		then = "The Completed Flag will be defaulted to TRUE AND "
				+ "All further validations will act as if Completed Flag was specified as TRUE")
	public void testHappyPathCompletedFlagDefaultsToTrue() throws Exception {		
		CreateIncidentTxDataDto txd = incidentDto.getIncidentTxData().getCreateIncidentTxData();
		txd.setCompleted(null);
		// Completed flag will set as null then {@link CreateIncidentDataConsolidationBean} method name setDefaultForMissingCompletedFlag.
		doCreateEventObject(null, incidentDto);
		// assertion for REQUIRES_QA which is set only for completed = true 
		assertEquals("Expect a Incident Status  " + REQUIRES_QA + " when completed flag set as false ",
				REQUIRES_QA, incidentDto.getStatus());
		
	}

	/**
	 * This test is a bit of a 'happy path' / default test, and simulates
	 * the logic that will occur when driven from the existing
	 * Decision Support app.
	 * 
	 * @throws Exception
	 */
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=215,
		mingleRef = 37149, 
		mingleTitle = "Business Service- Validaton framework- Create Investigation", 
		acceptanceCriteriaRefs = "CR2.1", 
		given = "txData.completeFlag == Anything AND incident.reportedDate == null ",
		when = "Incident Submited",
		then = "Mandatory validation error returned.")
	public void testIncidentWhenReportDateIsMandatory() throws Exception {

		try {
			incidentDto.setReportedDate(null);
			doCreateEventObject(false, incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			
			BusinessServiceValidationErrors businessServiceValidationErrors 
				= unmarshalBusinessServiceValidationErrorsFromXml(ide.getMessage());
			
			assertNotNull(businessServiceValidationErrors);

			assertOnValidationException(
					"/putPoleObjectsRequest/poleObjects/incident[objectRef='-1']/reportedDate/text()", "MANDATORY",
					"reportedDate must have a value", businessServiceValidationErrors);
		}

	}
	
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=215,
		mingleRef = 37149, 
		mingleTitle = "Business Service- Validaton framework- Create Investigation", 
		acceptanceCriteriaRefs = "CR2.1", 
		given = "txData.completeFlag == Anything AND incident.reportedDate > current date ",
		when = "Incident Submited",
		then = "Mandatory validation error returned.")
	public void testIncidentWhenReportedDateIsInFutureData() throws Exception {

		try {
			PoleDate poledate = new PoleDate().addDays(2); // Current Date  + 2 days
			incidentDto.setReportedDate(poledate);
			doCreateEventObject(false, incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>reportedDate cannot be greater than todays date</"));
		}
	}
	

	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=215,
		mingleRef = 37149, 
		mingleTitle = "Business Service- Validaton framework- Create Investigation", 
		acceptanceCriteriaRefs = "CR2.2", 
		given = "incident reportedDate is less than incident incidentOnOrFromDate",
		when = "Incident Submited",
		then = "Mandatory validation error returned.")
	public void testIncidentWhenIncidetReportedDateIslessThanOnOrFromDate() throws Exception {

		try {
			incidentDto.setReportedDate(new PoleDate().addDays(-2));
			doCreateEventObject(true, incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>incidentOnOrFromDate cannot be greater than reportedDate</"));
		}
	}
	

	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=215,
		mingleRef = 37149, 
		mingleTitle = "Business Service- Validaton framework- Create Investigation", 
		acceptanceCriteriaRefs = "CR2.3", 
		given = "Incident reportedTime is less than incident.incidentAtOrFromTime",
		when = "Incident Submited",
		then = "Mandatory validation error returned.")
	public void testIncidentWhenincidentreportedTimeIslessThanincidentAtOrFromTime() throws Exception {

		try {
			long time =  new PoleTime().getTime()+100000;
			incidentDto.setIncidentAtOrFromTime(new PoleTime(time));
			doCreateEventObject(true, incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>incidentAtOrFromTime cannot be greater than reportedTime</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=215,
		mingleRef = 37149, 
		mingleTitle = "Business Service- Validaton framework- Create Investigation", 
		acceptanceCriteriaRefs = "CR2.4", 
		given = "Incident reportedTime is less than incident.incidentAtOrFromTime",
		when = "Incident Submited",
		then = "Mandatory validation error returned.")
	public void testIncidentWhenincidentToDateGreaterThanCurrentDate() throws Exception {

		try {
			incidentDto.setIncidentToDate(new PoleDate().addDays(2));
			doCreateEventObject(true, incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>incidentToDate cannot be greater than todays date</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=215,
		mingleRef = 37149, 
		mingleTitle = "Business Service- Validaton framework- Create Investigation", 
		acceptanceCriteriaRefs = "CR2.5", 
		given = "incident.incidentToDate && incidentToTime is earlier than incident.incidentOnOrFromDate && incident.incidentAtOrFromTime",
		when = "Incident Submited",
		then = "Validation error returned.")
	public void testIncidentWhenincidentToDateToTimeIsEarlierIncidentOnOrFromDateOrFromTime() throws Exception {

		try {
			long time =  new PoleTime().getTime()+100000;
			incidentDto.setIncidentToDate(new PoleDate());
			incidentDto.setIncidentToTime(new PoleTime());
			incidentDto.setIncidentOnOrFromDate(new PoleDate());
			incidentDto.setIncidentAtOrFromTime(new PoleTime(time));
			doCreateEventObject(true, incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>incidentAtOrFromTime cannot be greater than incidentToTime</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=215,
		mingleRef = 37149, 
		mingleTitle = "Business Service- Validaton framework- Create Investigation", 
		acceptanceCriteriaRefs = "CR2.5.1", 
		given = "incident.incidentToDatetime is earlier than incident.incidentFromDateTime",
		when = "Incident Submited",
		then = "Validation error returned.")
	public void testIncidentWhenincidentToDateTimeisearlierIncidentFromDateTime() throws Exception {

		try {
			long time =  new PoleDateTime().getTime()+1000000;
			incidentDto.setIncidentToDateTime(new PoleDateTime());
			incidentDto.setIncidentFromDateTime(new PoleDateTime(time));
			doCreateEventObject(false, incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>incidentToDateTime cannot be greater than incidentFromDateTime</"));
		}
	}
	
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=215,
		mingleRef = 38320, 
		mingleTitle = "WP343 - Business Service - Create Investigation - Business Service Operation - Create Generic Investigation (Phase 1) v1 ", 
		acceptanceCriteriaRefs = "CR2.6", 
		given = "incident.reportedTime == null",
		when = "Incident Submited",
		then = "Validation error returned.")
	public void testIncidentWhenincidentreportedTimeMandatoryForCompletedFlagT() throws Exception {

		try {
			incidentDto.setReportedTime(null);
			doCreateEventObject(true, incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>reportedTime must have a value</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=215,
		mingleRef = 37149, 
		mingleTitle = "Business Service- Validaton framework- Create Investigation", 
		acceptanceCriteriaRefs = "CR2.7", 
		given = "txData.completeFlag == Anything AND incident.incidentOnOrFromDate == null ",
		when = "Incident Submited",
		then = "Mandatory validation error returned.")
	public void testIncidentWhenIncidentOnOrFromDateIsMandatory() throws Exception {

		try {
			incidentDto.setIncidentOnOrFromDate(null);
			doCreateEventObject(false, incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>incidentOnOrFromDate must have a value</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=215,
		mingleRef = 38100, 
		mingleTitle = "MS DT- Issues related to INCOMPLETE INVESTIGATION", 
		acceptanceCriteriaRefs = "unknown", 
		given = "txData.completeFlag == F AND incident.victimCrown == null",
		when = "Incident Submited",
		then = "Incident should created.")
	public void testIncidentWhenVictimCrownIsNotMandatoryForCompletedF() throws Exception {
			incidentDto.setVictimCrown(null);
			doCreateEventObject(false, incidentDto);
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=215,
		mingleRef = 38104, 
		mingleTitle = "MS DT- Issues related to INCOMPLETE INVESTIGATION", 
		acceptanceCriteriaRefs = "unknown", 
		given = "incidentToTime present and incidentAtOrFromTime is null  ",
		when = "Incident Submited",
		then = "Mandatory validation error returned.")
	public void testIncidentWhenincidentAtOrFromTimeIsMandatory() throws Exception {
		try {
			incidentDto.setIncidentAtOrFromTime(null);
			incidentDto.setIncidentToTime(new PoleTime());
			doCreateEventObject(true, incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>incidentAtOrFromTime must have a value</"));
		}
	}				
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=215,
		mingleRef = 37149, 
		mingleTitle = "Business Service- Validaton framework- Create Investigation", 
		acceptanceCriteriaRefs = "CR2.8", 
		given = "Incident Created from contact event and victim is crown is not supply ",
		when = "Incident Submited  from contact event",
		then = "No Error Incident should created .")
	public void testIncidentWhenVictimCrownIsNotMandatoryForDecisionSupport() throws Exception {

		ContactEventDto contactEvent = null ;
		
		String extRefValue = PE_REF_PREFIX + new Date();
		
		ContactEventDto ceDtoOrig = contils.createContactEventDto(extRefValue, peAccountRef);
		PutPoleObjectsRequestDto req = contils.createContactEventRequest(BusinessServicesTestUtils.createBusinessServiceInfo(securityContextId), ceDtoOrig);
				
		PutPoleObjectsResponseDto resp = poleBusinessServices.putPoleObjects(req);
		assertNotNull(getBusinessServiceResultInfo(resp).getTransactionId());
		
		contactEvent = contils.retrieveContactFromPole(ContactEventPoleDtoUtils.extractContactEventObjectRef(resp), 5, poleDirect, securityContextId, false);
		
		incidentDto = utils.prepareIncidentWithNoLinks(-1, Integer.valueOf(officerReportingId));

		incidentDto.setVictimCrown(null);
		incidentDto.setReferrals(null);
		incidentDto.setObjectActionPlans(null);

		PutPoleObjectsRequestDto ppoDto = utils.preparePpoRequestForCreateIncidentFromContact(
				BusinessServicesTestUtils.createBusinessServiceInfo(securityContextId), incidentDto, contactEvent, false);

		utils.addWorkflowTxDataForCreateIncident(ppoDto, taskStaffId, taskUnitName);


		utils.addWorkflowTxDataForCreateIncident(ppoDto, taskStaffId, taskUnitName);
			
		PutPoleObjectsResponseDto createIncFromContactResponse = poleBusinessServices.putPoleObjects(ppoDto);
		
		Integer incObjRef = IncidentPoleDtoUtils.extractIncidentObjectRef(createIncFromContactResponse);
		incidentDto = utils.getIncidentFromPole(incObjRef, poleDirect, securityContextId);
		
		assertEquals("Incident is retrive from pole ",true,incidentDto.getObjectRef() > 0);		
		
	}
	
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=215,
		mingleRef = 37149, 
		mingleTitle = "Business Service- Validaton framework- Create Investigation", 
		acceptanceCriteriaRefs = "CR2.9", 
		given = "txData.completeFlag == Anything AND incident.incidentSummary == null ",
		when = "Incident Submited",
		then = "Mandatory validation error returned.")
	public void testIncidentWhenincidentSummaryIsMandatory() throws Exception {

		try {
			incidentDto.setIncidentSummary(null);
			doCreateEventObject(false, incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>incidentSummary must have a value</"));
		}
	}
	
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=215,
		mingleRef = 37149, 
		mingleTitle = "Business Service- Validaton framework- Create Investigation", 
		acceptanceCriteriaRefs = "CR2.10", 
		given = "txData.completeFlag == Anything AND (incident.txData.forceId == null && incident.owningForceId == null) ",
		when = "Incident Submited",
		then = "Mandatory validation error returned.")
	public void testIncidentWhenForceIdAndOwningForceIdIsMandatory() throws Exception {

		try {
			incidentDto.setOwningForceId(null);
			incidentDto.getIncidentTxData().getCreateIncidentTxData().setForceId(null);
			doCreateEventObject(false, incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>owningForceId must have a value</"));
		}
	}
	
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=215,
		mingleRef = 37149, 
		mingleTitle = "Business Service- Validaton framework- Create Investigation", 
		acceptanceCriteriaRefs = "CR2.11", 
		given = "txData.completeFlag == Anything AND incident.txData.imuUnit == null ",
		when = "Incident Submited",
		then = "Mandatory validation error returned.")
	public void testIncidentWhenImuUnitIsMandatory() throws Exception {

		try {
			incidentDto.getIncidentTxData().getCreateIncidentTxData().setImuUnit(null);
			doCreateEventObject(false, incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>imuUnit must have a value</"));
		}
	}		
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=215,
		mingleRef = 37149, 
		mingleTitle = "Business Service- Validaton framework- Create Investigation", 
		acceptanceCriteriaRefs = "CR2.14", 
		given = "txData.completeFlag == Anything AND incident.officerReportingId == null ",
		when = "Incident Submited",
		then = "Mandatory validation error returned.")
	public void testIncidentWhenOfficerReportingIdIsMandatory() throws Exception {

		try {
			incidentDto.setOfficerReportingId(null);
			doCreateEventObject(false, incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>officerReportingId must have a value</"));
		}
	}
	
	/**
	 * This test is a bit of a 'happy path' / default test, and simulates
	 * the logic that will occur when driven from the existing
	 * Decision Support app.
	 * 
	 * @throws Exception
	 */
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=215,
		mingleRef = 37149, 
		mingleTitle = "Business Service- Validaton framework- Create Investigation", 
		acceptanceCriteriaRefs = "CR3.1", 
		given = "txData.completeFlag == F", 
		when = "Incident Submited", 
		then = "Incident status will be set to REQUIRES COMPLETION")
	public void testHappyPathCompletedFlagDefaultsToFalse() throws Exception {
		
		doCreateEventObject(false, incidentDto);		
		assertEquals("Expect a Incident Status  " + REQUIRES_COMPLETION + " when completed flag set as false ",
				REQUIRES_COMPLETION, incidentDto.getStatus());
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37149, 
			mingleTitle = "Business Service- Validaton framework- Create Investigation", 
			acceptanceCriteriaRefs = "CR4.1,CR4.2", 
			given = "txData.completeFlag == true and Qaunit and Linking Unit is null", 
			when = "Incident Submited", 
			then = "Validation error should be returned")
	public void testIncidentWhenHavingQAUnitAndLinkUnitMandatoryForCompletedTrue()
			throws Exception {
		try {
			incidentDto.getIncidentTxData().getCreateIncidentTxData().setQaUnit(null);
			incidentDto.getIncidentTxData().getCreateIncidentTxData().setLinkingUnit(null);
			doCreateEventObject(true, incidentDto);		
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>qaUnit must have a value</"));
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>linkingUnit must have a value</"));
		
		}
	}
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=215,
		mingleRef = 37149, 
		mingleTitle = "Business Service- Validaton framework- Create Investigation", 
		acceptanceCriteriaRefs = "CR4.3", 
		given = "a location is linked to the Incident location either re-searched or un-researched And with Link Reason "
				+ "INCIDENT LOCATION"
				+ " RISK ASSESSMENT LOCATION"
				+ "SECONDARY INCIDENT LOCATION "
				+"And location.force is null ",
		when = "Incident Submited",
		then = "Mandatory validation error returned.")
	public void testIncidentWhenLocationisLinkWhenForceIsMandatory() throws Exception {

		try {
			for (LinkDto linkDto : incidentDto.getLinks()) {
				if (linkDto.getToPoleObject() instanceof LocationDto
						&& linkDto.getLinkReason().equalsIgnoreCase(INCIDENT_LOCATION_LINK_REASON)) {
					((LocationDto) linkDto.getToPoleObject()).setForce(null);				
					break;
				}
			}
			doCreateEventObject(true, incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>Location must have a force value set</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37149, 
			mingleTitle = "Business Service- Validaton framework- Create Investigation", 
			acceptanceCriteriaRefs = "CR4.4,CR4.5", 
			given = "txData.completeFlag == true and initialMo  and howReported  is null", 
			when = "Incident Submited", 
			then = "Validation error should be returned")
	public void testIncidentWheninitialMoAndhowReportedMandatoryForCompletedTrue()
			throws Exception {
		try {
			incidentDto.setInitialMo(null);
			incidentDto.setHowReported(null);
			doCreateEventObject(true, incidentDto);	
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>initialMo must have a value</"));
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>howReported must have a value</"));
		
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=215,
		mingleRef = 37149, 
		mingleTitle = "Business Service- Validaton framework- Create Investigation", 
		acceptanceCriteriaRefs = "CR4.6", 
		given = "child object incidentInitialClassification==null and investigationClassification child is supplied "
				+ " And  investigationClassification.investigationClassificationUrn  == null"
				+ " and investigationClassification.classificationType=null ",
		when = "Incident Submited",
		then = "Mandatory validation error returned.")
	public void testIncidentWhenInitalClassifcationIsNullAndIncidentClassificationUrlAndTypeNull() throws Exception {

		try {
			incidentDto.setIncidentInitialClassifications(null);	
			IncidentClassificationDto incidentClassificationDto = new IncidentClassificationDto();
			incidentClassificationDto.setModificationStatus(ModificationStatusDto.CREATE);
			incidentClassificationDto.setInvestigationClassificationUrn(null);
			incidentClassificationDto.setClassificationType(null);
			incidentDto.addIncidentClassification(incidentClassificationDto);
			doCreateEventObject(false, incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>investigationClassificationUrn must have a value</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=215,
		mingleRef = 37149, 
		mingleTitle = "Business Service- Validaton framework- Create Investigation", 
		acceptanceCriteriaRefs = "CR4.6.1", 
		given = "txData.completeFlag == 'T' "
				+ "AND Child object incidentInitialClassification && investigationClassification is supplied "
				+ "AND InvestigationClassification.investigationClassificationUrn !=null "
				+ "AND classificationType != null",
		when = "Incident Submited",
		then = "validation error returned.")
	public void testIncidentWhenInitalClassifcationIncidentClassificationSuppliedTogether() throws Exception {

		try {
			utils.addClassification(incidentDto, "58C.58.0.0", "PRIMARY");
			utils.addIncidentInitialClassification(incidentDto);
			doCreateEventObject(true, incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>IncidentInitialClassification and investigationClassification cannot be supplied together</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=215,
		mingleRef = 37149, 
		mingleTitle = "Business Service- Validaton framework- Create Investigation", 
		acceptanceCriteriaRefs = "CR4.6.2", 
		given = "Child object incidentClassification is supplied and ClassificationType = INCLUDED",
		when = "Incident Submited",
		then = "validation error returned.")
	public void testIncidentClassificationSuppliedOnlyClassificationTypeIncluded() throws Exception {

		try {
			incidentDto.setIncidentInitialClassifications(null);	
			utils.addClassification(incidentDto,"58C.58.0.0","INCLUDED");
			doCreateEventObject(false, incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>One primary Incident classification is required.  Only one is allowed</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=215,
		mingleRef = 37149, 
		mingleTitle = "Business Service- Validaton framework- Create Investigation", 
		acceptanceCriteriaRefs = "CR4.7", 
		given = "child object incidentClassification more than 1 is supplied and ClassificationType = PRIMARY and ClassificationType = INCLUDED "
				+ "and completed Flag T",
		when = "Incident Submited",
		then = "No Error")
	public void testIncidentClassificationSuppliedForCompletedAndPrimary() throws Exception {

			incidentDto.setIncidentInitialClassifications(null);	
			utils.addClassification(incidentDto,"58C.58.0.0","INCLUDED");
			utils.addClassification(incidentDto,"58C.58.0.0","PRIMARY");
			doCreateEventObject(true, incidentDto);
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=215,
		mingleRef = 38110, 
		mingleTitle = "MS DT- BS hould validate that If primary offence is NON-CRIME offence then INCLUDED classification should not be CRIME offence", 
		acceptanceCriteriaRefs = "CR4.7", 
		given = "child object incidentClassification more than 1 is supplied and ClassificationType = PRIMARY and ClassificationType = INCLUDED "
				+ "and completed Flag T and primay offence is NON-CRIME offence then INCLUDED classification  CRIME offence",
		when = "Incident Submited",
		then = "validation error returned.")
	public void testIncidentClassificationSuppliedForOffenseValidator() throws Exception {
		try {
			incidentDto.setIncidentInitialClassifications(null);
			utils.addClassification(incidentDto, "AMO.NC.10.3", "PRIMARY");
			utils.addClassification(incidentDto, "58C.58.0.0", "INCLUDED");
			doCreateEventObject(true, incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(), ide.getMessage().contains(
					"error>The Investigation has a 'Non-Crime' type primary HO classification but a 'Crime' type included HO classification. "
					+ "This combination is not allowed</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=215,
		mingleRef = 37149, 
		mingleTitle = "Business Service- Validaton framework- Create Investigation", 
		acceptanceCriteriaRefs = "CR4.7", 
		given = "child object incidentClassification more than 1 is supplied and ClassificationType = PRIMARY and ClassificationType = INCLUDED "
				+ "and completed Flag F",
		when = "Incident Submited",
		then = "No Error")
	public void testIncidentClassificationSuppliedForCompletedFlagFIncludedAndPrimary() throws Exception {

			incidentDto.setIncidentInitialClassifications(null);	
			utils.addClassification(incidentDto,"58C.58.0.0","INCLUDED");
			utils.addClassification(incidentDto,"58C.58.0.0","PRIMARY");
			doCreateEventObject(false, incidentDto);
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=215,
		mingleRef = 37149, 
		mingleTitle = "Business Service- Validaton framework- Create Investigation", 
		acceptanceCriteriaRefs = "CR4.8", 
		given = "Child object incidentClassification is supplied and ClassificationType = PRIMARY",
		when = "Incident Submited",
		then = "validation error returned.")
	public void testIncidentClassificationSuppliedforPrimaryMoreThanOne() throws Exception {

		try {
			incidentDto.setIncidentInitialClassifications(null);	
			utils.addClassification(incidentDto,"58C.58.0.0","PRIMARY");
			utils.addClassification(incidentDto,"58C.58.0.0","PRIMARY");
			doCreateEventObject(false, incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>One primary Incident classification is required.  Only one is allowed</"));
		}
	}
	
	/**
	 * This test is a bit of a 'happy path' / default test, and simulates
	 * the logic that will occur when driven from the existing
	 * Decision Support app.
	 * 
	 * @throws Exception
	 */
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=215,
		mingleRef = 37149, 
		mingleTitle = "Business Service- Validaton framework- Create Investigation", 
		acceptanceCriteriaRefs = "CR4.9", 
		given = "txData.completeFlag == T", 
		when = "Incident Submited", 
		then = "Incident status will be set to REQUIRES QA")
	public void testIncidentForCompletedFlagTrue() throws Exception {
		
		doCreateEventObject(true, incidentDto);		
		assertEquals("Expect a Incident Status  " + REQUIRES_QA + " when completed flag set as false ",
				REQUIRES_QA, incidentDto.getStatus());
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=215,
		mingleRef = 37149, 
		mingleTitle = "Business Service- Validaton framework- Create Investigation", 
		acceptanceCriteriaRefs = "CR5.1,CR5.2", 
		given = "a Person is linked to an Investigation  and the linkreason can be one from below "
				+"SUSPECT (ELIMINATED)"
				+"SUSPECT (INSUFFICIENT TO PROCEED)"
				+"SUSPECT (INTERVIEWED)"
				+"SUSPECT (PROSECUTION PREVENTED)"
				+"SUSPECT (NO ACTION) EVIDENTIAL DIFF."
				+"SUSPECT (NOT INTERVIEWED)"
				+"SUSPECT (NO ACTION) TIME LIMIT EXPIRED"
				+"SUSPECT PRE ATHENA"
				+"SUSPECT"
				+ "And a IncidentPersonInfo child object is provided on the link"
				+ "IncidentPersonInfo.suspectStatus  is null ",
		when = "Incident Submited",
		then = "Mandatory validation error returned.")
	public void testIncidentWhenvssuspectStatusIsMandatoryOnProvidedLink() throws Exception {

		try {
			PersonDto person = new PersonDto();		
			person.setStatus(CommonPoleObjectTestUtils.POLE_OBJECT_NEW_STATUS);
			person.setModificationStatus(ModificationStatusDto.CREATE);
			person.setSurname("TestSuspect");
			person.setForename1("TestSuspect");
			person.setNonInnocent(false);
			
			LinkDto personLink = new LinkDto();
			personLink.setModificationStatus(ModificationStatusDto.CREATE);
			personLink.setSourcePoleObjectType(PoleNames.INCIDENT);
			personLink.setSourcePoleObjectRef(incidentDto.getObjectRef());
			personLink.setToPoleObject(person);
			personLink.setLinkReason(SubmitIncidentServiceConstants.SUSPECT_LINK_REASON);
			
			IncidentPersonInfoDto incidentPersonInfo = new IncidentPersonInfoDto();
			incidentPersonInfo.setModificationStatus(ModificationStatusDto.CREATE);
			incidentPersonInfo.setIsVictimVulnerable(false);
			personLink.addIncidentPersonInfo(incidentPersonInfo);
			incidentDto.addLink(personLink);
			doCreateEventObject(true, incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>suspectStatus must have a value</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=215,
		mingleRef = 37149, 
		mingleTitle = "Business Service- Validaton framework- Create Investigation", 
		acceptanceCriteriaRefs = "CR5.3", 
		given = "a Person is linked to an Investigation  and the linkreason can be one from below "
				+ " ASSOCIATED"
				+ " VICTIM,"
				+ "INVOLVED PARTY,"
				+ "MISSING PERSON,CHILD"
				+ " IN HOUSEHOLD,VICTIM,"
				+ "WITNESS "
				+ "And a IncidentPersonInfo child object is provided on the link"
				+ "IncidentPersonInfo.vsAgreedToVictimSupport  is null ",
		when = "Incident Submited",
		then = "Mandatory validation error returned.")
	public void testIncidentWhenvsAgreedToVictimSupportIsMandatoryOnProvidedLink() throws Exception {

		try {
			for (LinkDto linkDto : incidentDto.getLinks()) {
				if (linkDto.getToPoleObject() instanceof PersonDto
						&& linkDto.getLinkReason().equalsIgnoreCase("VICTIM")) {

					for (IncidentPersonInfoDto incidentPersonInfoDto : linkDto.getIncidentPersonInfoList()) {
						incidentPersonInfoDto.setVsAgreedToVictimSupport(null);
						incidentPersonInfoDto.setIsVictimVulnerable(false);
						break;
					}
				}
			}
			doCreateEventObject(true, incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>vsAgreedToVictimSupport must have a value</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=215,
		mingleRef = 37149, 
		mingleTitle = "Business Service- Validaton framework- Create Investigation", 
		acceptanceCriteriaRefs = "CR5.4", 
		given = "a Person is linked to an Investigation and  "
				+ "the link has a link reason as any of the below either re-searched or un-researched "
				+ "VICTIM "
				+ "WITNESS and a peContactInfo child object is provided on the link"
				+ "peContactInfo.invitationToEngage == null "  ,
		when = "Incident Submited",
		then = "validation error returned.")
	public void testIncidentWheninvitationToEngageIsMandatoryForVicitm() throws Exception {

		try {
			for (LinkDto linkDto : incidentDto.getLinks()) {
				if (linkDto.getToPoleObject() instanceof PersonDto
						&& linkDto.getLinkReason().equalsIgnoreCase("VICTIM")) {
					
					PeContactInfoDto peContactInfoDto = new PeContactInfoDto();
					peContactInfoDto.setModificationStatus(ModificationStatusDto.CREATE);
					peContactInfoDto.setInvitationToEngage(null);
					linkDto.addPeContactInfo(peContactInfoDto);
				}
			}
			doCreateEventObject(true, incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>invitationToEngage must have a value</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=215,
		mingleRef = 37149, 
		mingleTitle = "Business Service- Validaton framework- Create Investigation", 
		acceptanceCriteriaRefs = "CR5.5", 
		given = "a Person is linked to an Investigation and "
				+ "the link has a link reason as any of the below either re-searched or un-researched "
				+ "ASSOCIATED VICTIM "
				+ "MISSING PERSON "
				+ "CHILD IN HOUSEHOLD "
				+ "INVOLVED PARTY"
				+ "VICTIM "
				+ "WITNESS and "
				+ "a IncidentPersonInfo child object is provided on the link"
				+ "and IncidentPersonInfo.victimElectsToBeUpdated == null",
		when = "Incident Submited",
		then = "validation error returned.")
	public void testIncidentWhenvictimElectsToBeUpdatedIsMandatoryForVicitm() throws Exception {

		try {
			for (LinkDto linkDto : incidentDto.getLinks()) {
				if (linkDto.getToPoleObject() instanceof PersonDto
						&& linkDto.getLinkReason().equalsIgnoreCase("VICTIM")) {

					for (IncidentPersonInfoDto incidentPersonInfoDto : linkDto.getIncidentPersonInfoList()) {
						incidentPersonInfoDto.setVictimElectsToBeUpdated(null);
						incidentPersonInfoDto.setIsVictimVulnerable(false);
						break;
					}
				}
			}
			doCreateEventObject(true, incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>victimElectsToBeUpdated must have a value</"));
		}
	}
		
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=215,
		mingleRef = 37149, 
		mingleTitle = "Business Service- Validaton framework- Create Investigation", 
		acceptanceCriteriaRefs = "CR5.9", 
		given = "a Person is linked to an Investigation and "
				+ "the link has a link reason as any of the below either re-searched or un-researched "
				+ "ASSOCIATED VICTIM "
				+ "MISSING PERSON "
				+ "CHILD IN HOUSEHOLD "
				+ "INVOLVED PARTY"
				+ "VICTIM "
				+ "WITNESS"
				+" a incidentPersonEvent child object is provided on the link and eventType is null ",
		when = "Incident Submited",
		then = "validation error returned.")
	public void testIncidentEventTypeIsMandatoryForVicitm() throws Exception {

		try {
			for (LinkDto linkDto : incidentDto.getLinks()) {
				if (linkDto.getToPoleObject() instanceof PersonDto
						&& linkDto.getLinkReason().equalsIgnoreCase("VICTIM")) {

					IncidentPersonEventDto IncidentPersonEvent = new  IncidentPersonEventDto() ;
					IncidentPersonEvent.setModificationStatus(ModificationStatusDto.CREATE);
					IncidentPersonEvent.setEventType(null);
					linkDto.addIncidentPersonEvent(IncidentPersonEvent);
				}
			}
			doCreateEventObject(true, incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>eventType must have a value</"));
		}
	}
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=215,
		mingleRef = 37149, 
		mingleTitle = "Business Service- Validaton framework- Create Investigation", 
		acceptanceCriteriaRefs = "CR5.10", 
		given = "a Person is linked to an Investigation  and the linkreason can be one from below "
				+ " ASSOCIATED" //TODO unreadable / confusing split on [space] of link reason.
				+ " VICTIM,"
				+ "INVOLVED PARTY,"
				+ "MISSING PERSON,CHILD"//TODO unreadable / confusing split on [space] of link reason.
				+ "IN HOUSEHOLD,VICTIM,"
				+ "WITNESS "
				+ "and a IncidentPersonInfo child object is provided on the link"
				+ "and Contact Directly set as false "
				+ "and No association is provided",
		when = "Incident Submited",
		then = "Mandatory validation error returned.")
	public void testIncidentWhenNoAssociationIsProvidedForSpecifiedLinkReason() throws Exception {

		try {
			for (LinkDto linkDto : incidentDto.getLinks()) {
				if (linkDto.getToPoleObject() instanceof PersonDto
						&& linkDto.getLinkReason().equalsIgnoreCase("VICTIM")) {

					for (IncidentPersonInfoDto incidentPersonInfoDto : linkDto.getIncidentPersonInfoList()) {
						incidentPersonInfoDto.setContactDirectly(false);
						incidentPersonInfoDto.setIsVictimVulnerable(false);
					}

				}
			}
			doCreateEventObject(true, incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>No association is provided</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=215,
		mingleRef = 37149, 
		mingleTitle = "Business Service- Validaton framework- Create Investigation", 
		acceptanceCriteriaRefs = "CR5.11", 
		given = "a Person is linked to an Investigation  and the linkreason can be one from below "
				+ " ASSOCIATED" //TODO unreadable / confusing split on [space] of link reason.
				+ " VICTIM,"
				+ "INVOLVED PARTY,"
				+ "MISSING PERSON,"//TODO unreadable / confusing split on [space] of link reason.
				+ "CHILD IN HOUSEHOLD,"
				+ "VICTIM,"
				+ "WITNESS "
				+ "and a IncidentPersonInfo child object is provided on the link"
				+ "IncidentPersonInfo.victimElectsToBeUpdated ==Opt out - Business victim "
				+ "|| Opt out - Pre charge || Opt out - Post charge"
				+ "contactDirectly && victimsPreferredContactMethod "
				+ "&& contactNotes && timesToAvoid && frequencyOfUpdates "
				+ "&& oicAllocated && oicChanged && suspectArrested "
				+ "&& suspectCharged suspectBailed is supplied",
		when = "Incident Submited",
		then = "validation error returned.")
	public void testIncidentIncidentPersonInfoFieldForbiddenForVicitm() throws Exception {

		try {
			for (LinkDto linkDto : incidentDto.getLinks()) {
				if (linkDto.getToPoleObject() instanceof PersonDto
						&& linkDto.getLinkReason().equalsIgnoreCase("VICTIM")) {

					for (IncidentPersonInfoDto incidentPersonInfo : linkDto.getIncidentPersonInfoList()) {
						incidentPersonInfo.setVictimElectsToBeUpdated("OUT_BUSINESS");
						incidentPersonInfo.setIsVictimVulnerable(false);
						break;
					}
				}
			}
			doCreateEventObject(true, incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>victimsPreferredContactMethod must not have a value</"));
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>timesToAvoid must not have a value</"));
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>frequencyOfUpdates must not have a value</"));
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>oicAllocated must not have a value</"));
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>victimsPreferredContactMethod must not have a value</"));
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>oicChanged must not have a value</"));
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>suspectArrested must not have a value</"));
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>suspectBailed must not have a value</"));
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>contactDirectly must not have a value</"));
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>contactNotes must not have a value</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=215,
		mingleRef = 37149, 
		mingleTitle = "Business Service- Validaton framework- Create Investigation", 
		acceptanceCriteriaRefs = "CR5.12", 
		given = "a Person is linked to an Investigation  and the linkreason can be one from below "
				+ " ASSOCIATED"
				+ " VICTIM,"
				+ "INVOLVED PARTY,"
				+ "MISSING PERSON,CHILD"
				+ "IN HOUSEHOLD,VICTIM,"
				+ "WITNESS "
				+ "and There is a person to person link with link reason as described person"
				+ "and dpType is null ",
		when = "Incident Submited",
		then = "Mandatory validation error returned.")
	public void testIncidentWhenSpecifiedDescribePersonWhenDpTypeIsNull() throws Exception {

		try {
			for (LinkDto linkDto : incidentDto.getLinks()) {
				if (linkDto.getToPoleObject() instanceof PersonDto
						&& linkDto.getLinkReason().equalsIgnoreCase("VICTIM")) {

					PersonDto personDto = (PersonDto)linkDto.getToPoleObject() ;
					PersonDto person = new PersonDto();		
					person.setStatus(CommonPoleObjectTestUtils.POLE_OBJECT_NEW_STATUS);
					person.setModificationStatus(ModificationStatusDto.CREATE);
					person.setSurname("Test");
					person.setForename1("Test");
					person.setNonInnocent(false);
					
					IncidentPersonInfoDto incidentPersonInfo = new IncidentPersonInfoDto();
					incidentPersonInfo.setModificationStatus(ModificationStatusDto.CREATE);
					incidentPersonInfo.setIsVictimVulnerable(false);
					incidentPersonInfo.setDpType(null);
					incidentPersonInfo.setDpNamedAs(null);
					
					LinkDto personLink = new LinkDto();
					personLink.setModificationStatus(ModificationStatusDto.CREATE);
					personLink.setSourcePoleObjectType(PoleNames.INCIDENT);
					personLink.setSourcePoleObjectRef(incidentDto.getObjectRef());
					personLink.setFromPoleObjectType(PoleNames.PERSON);
					personLink.setFromPoleObjectRef(linkDto.getToPoleObjectRef());
					personLink.setToPoleObject(person);
					personLink.setLinkReason(SubmitIncidentServiceConstants.DESCRIBED_PERSON);
					personLink.addIncidentPersonInfo(incidentPersonInfo);
					personDto.addLink(personLink);
				}
			}
			doCreateEventObject(true, incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>dpType must have a value</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=215,
		mingleRef = 37149, 
		mingleTitle = "Business Service- Validaton framework- Create Investigation", 
		acceptanceCriteriaRefs = "CR5.13", 
		given = "a Person is linked to an Investigation  and the linkreason can be one from below "
				+ " ASSOCIATED"
				+ " VICTIM,"
				+ "INVOLVED PARTY,"
				+ "MISSING PERSON,CHILD"
				+ "IN HOUSEHOLD,VICTIM,"
				+ "WITNESS "
				+ "and There is a person to person link with link reason as described person"
				+ "and dpNamedAs is null ",
		when = "Incident Submited",
		then = "Mandatory validation error returned.")
	public void testIncidentWhenSpecifiedDescribePersonAndDpTypeIsDescribed() throws Exception {

		try {
			for (LinkDto linkDto : incidentDto.getLinks()) {
				if (linkDto.getToPoleObject() instanceof PersonDto
						&& linkDto.getLinkReason().equalsIgnoreCase("VICTIM")) {

					PersonDto personDto = (PersonDto)linkDto.getToPoleObject() ;
					PersonDto person = new PersonDto();		
					person.setStatus(CommonPoleObjectTestUtils.POLE_OBJECT_NEW_STATUS);
					person.setModificationStatus(ModificationStatusDto.CREATE);
					person.setSurname("Test");
					person.setForename1("Test");
					person.setNonInnocent(false);
					
					IncidentPersonInfoDto incidentPersonInfo = new IncidentPersonInfoDto();
					incidentPersonInfo.setModificationStatus(ModificationStatusDto.CREATE);
					incidentPersonInfo.setIsVictimVulnerable(false);
					incidentPersonInfo.setDpType("DESCRIBED");
					incidentPersonInfo.setDpNamedAs("Test123");
					
					LinkDto personLink = new LinkDto();
					personLink.setModificationStatus(ModificationStatusDto.CREATE);
					personLink.setSourcePoleObjectType(PoleNames.INCIDENT);
					personLink.setSourcePoleObjectRef(incidentDto.getObjectRef());
					personLink.setFromPoleObjectType(PoleNames.PERSON);
					personLink.setFromPoleObjectRef(linkDto.getToPoleObjectRef());
					personLink.setToPoleObject(person);
					personLink.setLinkReason(SubmitIncidentServiceConstants.DESCRIBED_PERSON);
					personLink.addIncidentPersonInfo(incidentPersonInfo);
					personDto.addLink(personLink);
				}
			}
			doCreateEventObject(true, incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>dpNamedAs must not have a value</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=215,
		mingleRef = 37149, 
		mingleTitle = "Business Service- Validaton framework- Create Investigation", 
		acceptanceCriteriaRefs = "UNKNOWN", 
		given = "Investigation is created with incidentClassification2",
		when = "Incident Submited",
		then = "Mandatory validation error returned.")
	public void testIncidentIincidentClassification2IsPresent() throws Exception {

		try {
			IncidentClassification2Dto  classification2Dto = new IncidentClassification2Dto();
			classification2Dto.setModificationStatus(ModificationStatusDto.CREATE);
			classification2Dto.setClassificationStatus("NEW");
			incidentDto.addIncidentClassification2(classification2Dto);
			doCreateEventObject(true, incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>Unexpected child object [child] IncidentClassification2Dto"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=215,
		mingleRef = 37149, 
		mingleTitle = "Business Service- Validaton framework- Create Investigation", 
		acceptanceCriteriaRefs = "UNKNOWN", 
		given = "Investigation is created with Primary Classification",
		when = "Incident Submited",
		then = "Classification Should set from Incident classifciaiton aux table ")
	public void testIncidentWhenPrimaryClassificationPresent() throws Exception {
			incidentDto.setIncidentInitialClassifications(null);
			utils.addClassification(incidentDto,"58C.58.0.0","PRIMARY");
			doCreateEventObject(true, incidentDto);
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37149, 
			mingleTitle = "Business Service- Validaton framework- Create Investigation",
			acceptanceCriteriaRefs = "CR6.1", 
			given ="child object incidentPoliceAttendance is supplied and incidentPoliceAttendance.officerId == null", 
			when = "Incident Submited", 
			then = "Validation error should be returned")
	public void testIncidentWhenpoliceAttendanceOfficerIdIsMandatory()
			throws Exception {
		try {
			IncidentPoliceAttendanceDto policeAttendance = new IncidentPoliceAttendanceDto();
			policeAttendance.setModificationStatus(ModificationStatusDto.CREATE);
			incidentDto.addPoliceAttendance(policeAttendance);
			doCreateEventObject(false, incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>officerId must have a value</"));			
		}
	}
	

	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37149, 
			mingleTitle = "Business Service- Validaton framework- Create Investigation",
			acceptanceCriteriaRefs = "CR6.2", 
			given ="child object incidentPoliceAttendance is supplied and incidentPoliceAttendance.officerId == null", 
			when = "Incident Submited", 
			then = "Validation error should be returned")
	public void testIncidentWhenpoliceAttendanceofficerAttendingRoleIsMandatory()
			throws Exception {
		try {
			IncidentPoliceAttendanceDto policeAttendance = new IncidentPoliceAttendanceDto();
			policeAttendance.setModificationStatus(ModificationStatusDto.CREATE);
			policeAttendance.setOfficerId(officerReportingId);
			policeAttendance.setOfficerAttendingRole(null);
			incidentDto.addPoliceAttendance(policeAttendance);
			doCreateEventObject(false, incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>officerAttendingRole must have a value</"));			
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37149, 
			mingleTitle = "Business Service- Validaton framework- Create Investigation",
			acceptanceCriteriaRefs = "CR8.1 To 8.5", 
			given ="child object incidentLogEntry is present and entryDate == null && entryTime == null && enteredByOfficerId =null "
					+ " && entryType  == null && entryText =null ", 
			when = "Incident Submited", 
			then = "Validation error should be returned")
	public void testIncidentWhenValidationForLogEntry()
			throws Exception {
		try {
			
			IncidentLogEntryDto logEntry = new IncidentLogEntryDto();
			logEntry.setModificationStatus(ModificationStatusDto.CREATE);
			logEntry.setEntryDate(null);
			logEntry.setEntryTime(null);
			logEntry.setEntryType(null);
			logEntry.setEntryText(null);
			logEntry.setEnteredByOfficerId(null);
			incidentDto.addIncidentLogEntry(logEntry);
			doCreateEventObject(false, incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>entryText must have a value</"));
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>entryDate must have a value</"));		
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>entryTime must have a value</"));		
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>enteredByOfficerId must have a value</"));		
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>entryType must have a value</"));		
			
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37149, 
			mingleTitle = "Business Service- Validaton framework- Create Investigation",
			acceptanceCriteriaRefs = "CR9.1", 
			given ="Child object incidentReferral is present and incidentReferral.submittedDate > current date", 
			when = "Incident Submited", 
			then = "Validation error should be returned")
	public void testIncidentWhenIncidentReferralSubmittedDateInFuture()
			throws Exception {
		try {
			
			for (IncidentReferralDto incidentReferral : incidentDto.getReferrals()) {
				incidentReferral.setSubmittedDate(new PoleDate().addDays(2));
				break;
			}
			doCreateEventObject(false, incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>submittedDate cannot be greater than todays date</"));			
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=215,
		mingleRef = 37149, 
		mingleTitle = "Business Service- Validaton framework- Create Investigation", 
		acceptanceCriteriaRefs = "CR10.2 to CR10.6", 
		given = "a Person is linked to an Investigation and "
				+ "the link has a link reason as any of the below either re-searched or un-researched "
				+ "ASSOCIATED VICTIM "
				+ "MISSING PERSON "
				+ "CHILD IN HOUSEHOLD "
				+ "INVOLVED PARTY"
				+ "VICTIM "
				+ "WITNESS",
		when = "Incident Submited",
		then = "validation error returned.")
	public void testIncidentForincidentPersonInfoUpdateForVicitm() throws Exception {

		try {
			for (LinkDto linkDto : incidentDto.getLinks()) {
				if (linkDto.getToPoleObject() instanceof PersonDto
						&& linkDto.getLinkReason().equalsIgnoreCase("VICTIM")) {

					for (IncidentPersonInfoDto incidentPersonInfo : linkDto.getIncidentPersonInfoList()) {
						for (IncidentPersonInfoUpdateDto personInfoUpdate : incidentPersonInfo.getUpdates()) {
							personInfoUpdate.setOfficerId(null);
							incidentPersonInfo.setIsVictimVulnerable(false);
							personInfoUpdate.setUpdatedDateTime(null);
							personInfoUpdate.setReasonForUpdate(null);
							personInfoUpdate.setMethodOfUpdate(null);
							personInfoUpdate.setRemarks(null);
							personInfoUpdate.setDocument(null);
							break;
						}
					}
				}
			}
			doCreateEventObject(true, incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>updatedDateTime must have a value</"));
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>methodOfUpdate must have a value</"));
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>reasonForUpdate must have a value</"));
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>remarks must have a value</"));
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>methodOfUpdate must have a value</"));
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>officerId must have a value</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37149, 
			mingleTitle = "Business Service- Validaton framework- Create Investigation", 
			acceptanceCriteriaRefs = "CR12.1", 
			given = "officerInCaseId != null", 
			when = "Incident Submited", 
			then = "Validation error should return")
	public void testIncidentWhenOfficerInCaseIdNotNull() throws Exception {
		try {
			incidentDto.setOfficerInCaseId(12345);
			doCreateEventObject(false, incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>officerInCaseId must not have a value</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37149, 
			mingleTitle = "Business Service- Validaton framework- Create Investigation", 
			acceptanceCriteriaRefs = "CR12.2", 
			given = "officerInCaseUnitCode != null", 
			when = "Incident Submited", 
			then = "Validation error should return")
	public void testIncidentWhenOfficerInCaseUnitCodeNotNull() throws Exception {
		try {
			incidentDto.setOfficerInCaseUnitCode("AMO");
			doCreateEventObject(false, incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>officerInCaseUnitCode must not have a value</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=215,
		mingleRef = 37149, 
		mingleTitle = "Business Service- Validaton framework- Create Investigation",
		acceptanceCriteriaRefs = "CR13.1", 
		given = "Child object incident InitialClassification is not null  && investigationClassification is both null ",
		when = "Incident Submited",
		then = "Incident should created .")
	public void testIncidentWhenInvestigationclassificationisNullAndIntialClassificationIsPresent() throws Exception {
		   // null both classification 
			incidentDto.setIncidentInitialClassifications(null);	
			incidentDto.setIncidentClassifications(null);	
			// setting intial classificaiton 
			IncidentInitialClassificationDto incidentInitialClassificationDto = new IncidentInitialClassificationDto();		
			incidentInitialClassificationDto.setInitialClassification(IncidentTestUtils.ADULT_ABUSE_INITIAL_CLASSIFICATION);			
			incidentInitialClassificationDto.setModificationStatus(ModificationStatusDto.CREATE);
			utils.addIncidentInitialClassification(incidentDto);
			doCreateEventObject(false, incidentDto);
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=215,
		mingleRef = 37149, 
		mingleTitle = "Business Service- Validaton framework- Create Investigation",
		acceptanceCriteriaRefs = "CR13.2", 
		given = "Child object incidentInitialClassification && investigationClassification is both null ",
		when = "Incident Submited",
		then = "validation error returned.")
	public void testIncidentWhenInitalClassifcationIncidentClassificationBothNullCompletedFlagN() throws Exception {

		try {
			incidentDto.setIncidentInitialClassifications(null);
			incidentDto.setIncidentClassifications(null);
			doCreateEventObject(true, incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(), ide.getMessage().contains(
					"error>IncidentInitialClassification and IncidentClassification cannot both be null. At least one should be supplied.</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=215,
		mingleRef = 38321, 
		mingleTitle = "WP343 - Business Service - Create Investigation - Business Service Operation - Create Assault Investigation (Phase 1) v1",
		acceptanceCriteriaRefs = "UNKNOWN", 
		given = "Child object incidentInitialClassification && investigationClassification is both null ",
		when = "Incident Submited",
		then = "Incident should created with workflow for completd flag N")
	public void testIncidentWhenInitalClassifcationIncidentClassificationBothNullForCompletedFlagT() throws Exception {
			incidentDto.setIncidentInitialClassifications(null);	
			incidentDto.setIncidentClassifications(null);	
			doCreateEventObject(false, incidentDto);
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=215,
		mingleRef = 37149, 
		mingleTitle = "Business Service- Validaton framework- Create Investigation", 
		acceptanceCriteriaRefs = "UNKNOWN", 
		given = "Incident is created witout any location ",
		when = "Incident Submited",
		then = "Mandatory validation error returned.")
	public void testIncidentWhenLocationIsMandatory() throws Exception {

		try {
			for (LinkDto linkDto : incidentDto.getLinks()) {
				if (linkDto.getToPoleObject() instanceof LocationDto
						&& linkDto.getLinkReason().equalsIgnoreCase(INCIDENT_LOCATION_LINK_REASON)) {
					incidentDto.removeLink(linkDto);
					break;
				}
			}
			doCreateEventObject(true, incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>Incident Location is mandatory for completed flag true</"));
		}
	}
	
	
	/** ============================ #37152 - Common child objects Gherkins ============================ **/
	
	/**
	 * Note: TODO: Need to refactor below object action plan specific functional test into common class when BS will
	 * support GemCase eventually.
	 */
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37152, 
			mingleTitle = "Business Service- Validation Framework -Common child objects rules", 
			acceptanceCriteriaRefs = "CR18.1", 
			given = "Incident has ObjectActionPlan with valid details. Happy path scenario", 
			when = "Incident Submited", 
			then = "No validation error should be returned")
	public void testNewActionPlanIsAcceptedWhenValid()
			throws Exception {
		
		PoleObjectDto poleObject = createPoleObjectDto();
		
		objectActionPlan = CommonPoleObjectTestUtils.getObjectActionPlan(officerReportingId);
		
		doCreateEventObject(true, poleObject);			
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37152, 
			mingleTitle = "Business Service- Validation Framework -Common child objects rules", 
			acceptanceCriteriaRefs = "CR18.1,CR18.8", 
			given = "Incident has ObjectActionPlan && (raiseTask = null or seqNo = null)", 
			when = "Incident Submited", 
			then = "Validation error should be returned")
	public void testNewActionPlanMandatesRaiseTaskAndSeqNo()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();

			objectActionPlan = CommonPoleObjectTestUtils.getObjectActionPlan(officerReportingId);
			objectActionPlan.setRaiseTask(null);
			objectActionPlan.setSeqNo(null);
			
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>raiseTask must have a value</"));
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>seqNo must have a value</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37152, 
			mingleTitle = "Business Service- Validation Framework -Common child objects rules", 
			acceptanceCriteriaRefs = "CR18.1", 
			given = "Incident has ObjectActionPlan. RaiseTask value other than T/F", 
			when = "Incident Submited", 
			then = "Validation error should be returned")
	public void testNewActionPlanIsRejectedWhenHaveInValidValueForRaiseTask()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();

			objectActionPlan = CommonPoleObjectTestUtils.getObjectActionPlan(officerReportingId);
			objectActionPlan.setRaiseTask("Invalid Value");

			doCreateEventObject(true, poleObject);
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>raiseTask has an incorrect CV value for list YES_NO</"));			
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37152, 
			mingleTitle = "Business Service- Validation Framework -Common child objects rules", 
			acceptanceCriteriaRefs = "CR18.2,CR18.4", 
			given = "Incident has ObjectActionPlan && raiseTask = NO "
					+ "&& dateCreate = null || statusCode = null", 
			when = "Incident Submited", 
			then = "Validation error should be returned")
	public void testNewActionPlanMandatesDateCreatedAndStatusCode()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();

			objectActionPlan = CommonPoleObjectTestUtils.getObjectActionPlan(officerReportingId);
			objectActionPlan.setDateCreated(null);
			objectActionPlan.setStatusCode(null);

			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>dateCreated must have a value</"));
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>statusCode must have a value</"));			
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37152, 
			mingleTitle = "Business Service- Validation Framework -Common child objects rules", 
			acceptanceCriteriaRefs = "CR18.3,CR18.6", 
			given = "Incident has ObjectActionPlan "
					+ "&& dateCompleted > currentDate || dateCreated > currentDate", 
			when = "Incident Submited", 
			then = "Validation error should be returned")
	public void testNewActionPlanDateCreatedAndDateCompletedCannotBeInFuture()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();

			objectActionPlan = CommonPoleObjectTestUtils.getObjectActionPlan(officerReportingId);
			PoleDate value = new PoleDate();
			value = value.addDays(1);
			objectActionPlan.setDateCompleted(value);
			objectActionPlan.setDateCreated(value);

			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>dateCreated cannot be greater than todays date</"));
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>dateCompleted cannot be greater than todays date</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37152, 
			mingleTitle = "Business Service- Validation Framework -Common child objects rules", 
			acceptanceCriteriaRefs = "CR18.5,CR18.7", 
			given = "Incident has ObjectActionPlan && raiseTask=No && statusCode=Completed && "
					+ "(dateComplete=null or officeCompletingAction=null)", 
			when = "Incident Submited", 
			then = "Validation error should be returned")
	public void testCompletedActionPlanMandatesDateCompletedAndOfficerCompletingAction()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();

			objectActionPlan = CommonPoleObjectTestUtils.getObjectActionPlan(officerReportingId);
			objectActionPlan.setStatusCode("COMPLETED");
			objectActionPlan.setDateCompleted(null);
			objectActionPlan.setOfficerCompletingAction(null);

			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>dateCompleted must have a value</"));			                                          
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>officerCompletingAction must have a value</"));
		}
	}
	
	/**
	 * Test to check if event have objectActionPlan with RaiseTask=Yes
	 * Then following to be validated.
	 * 1) dateCreated become mandatory
	 * 2) dateDue become mandatory
	 * 3) notifyWhenComplete become mandatory
	 * 4) either requestToStaffId or requestToUnit become mandatory
	 * 5) either replyToStaffId or replyToUnit become mandatory
	 */
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37152, 
			mingleTitle = "Business Service- Validation Framework -Common child objects rules", 
			acceptanceCriteriaRefs = "CR18.9,CR18.10,CR18.11,CR18.14,CR18.17", 
			given = "Incident has ObjectActionPlan && raiseTask=Yes "
					+ "&& ( dateCreated = null or dateDue = null or (requestToStaffId = null && requestToUnitId = null) "
					+     "or (replyToStaffId = null && replyToUnitId = null)"
					+ ")"
					+ " or notifyWhenComplete = null", 
			when = "Incident Submited", 
			then = "Validation error should be returned")
	public void testNewActionPlanWithTaskMandatesOtherFields()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();

			objectActionPlan = CommonPoleObjectTestUtils.getObjectActionPlan(officerReportingId);
			objectActionPlan.setRaiseTask(ObjectActionPlanTaskHelperBean.RAISE_TASK_TRUE);
			objectActionPlan.setDateCreated(null);
			objectActionPlan.setDateDue(null);
			objectActionPlan.setRequestToStaffId(null);
			objectActionPlan.setRequestToUnit(null);
			objectActionPlan.setReplyToStaffId(null);
			objectActionPlan.setReplyToUnit(null);

			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>dateCreated must have a value</"));			                                          
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>dateDue must have a value</"));
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>notifyWhenComplete must have a value</"));			
			assertTrue("ValidationException: " + ide.getMessage(), ide.getMessage().contains(
					"error>Fields either requestToStaffId or requestToUnit should have value when task have been "
					+ "raised for ObjectActionPlan but not both.</"));
			assertTrue("ValidationException: " + ide.getMessage(), ide.getMessage().contains(
					"error>Fields either replyToStaffId or replyToUnit should have value when task have been raised "
					+ "for ObjectActionPlan but not both.</"));
			
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37152, 
			mingleTitle = "Business Service- Validation Framework -Common child objects rules", 
			acceptanceCriteriaRefs = "CR18.12, CR18.13", 
			given = "Incident has ObjectActionPlan && raiseTask=Yes && "
					+ "requestToStaffId != null && requestToUnitId != null", 
			when = "Incident Submited", 
			then = "Validation error should be returned")
	public void testNewActionPlanWithTaskDisallowsRequestToBothStaffAndUnit()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			IncidentDto incident = (IncidentDto) poleObject;

			objectActionPlan = CommonPoleObjectTestUtils.getObjectActionPlan(officerReportingId);
			objectActionPlan.setRaiseTask(ObjectActionPlanTaskHelperBean.RAISE_TASK_TRUE);			
			objectActionPlan.setRequestToStaffId(officerReportingId);
			objectActionPlan.setRequestToUnit(incident.getIncidentTxData().getCreateIncidentTxData().getImuUnit());
			objectActionPlan.setReplyToStaffId(officerReportingId);

			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {					
			assertTrue("ValidationException: " + ide.getMessage(), ide.getMessage().contains(
					"error>Fields either requestToStaffId or requestToUnit should have value when task have been "
					+ "raised for ObjectActionPlan but not both.</"));
		}
	}	
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37152, 
			mingleTitle = "Business Service- Validation Framework -Common child objects rules", 
			acceptanceCriteriaRefs = "CR18.15, CR18.16", 
			given = "Incident has ObjectActionPlan && raiseTask=Yes && "
					+ "&& replyToStaffId != null && replyToUnitId != null", 
			when = "Incident Submited", 
			then = "Validation error should be returned")
	public void testNewActionPlanWithTaskDisallowsReplyToBothStaffAndUnit()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			IncidentDto incident = (IncidentDto) poleObject;

			objectActionPlan = CommonPoleObjectTestUtils.getObjectActionPlan(officerReportingId);
			objectActionPlan.setRaiseTask(ObjectActionPlanTaskHelperBean.RAISE_TASK_TRUE);			
			objectActionPlan.setRequestToStaffId(officerReportingId);
			objectActionPlan.setReplyToStaffId(officerReportingId);
			objectActionPlan.setReplyToUnit(incident.getIncidentTxData().getCreateIncidentTxData().getImuUnit());

			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {					
			assertTrue("ValidationException: " + ide.getMessage(), ide.getMessage().contains(
					"error>Fields either replyToStaffId or replyToUnit should have value when task have been raised "
					+ "for ObjectActionPlan but not both.</"));
		}
	}

	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37152, 
			mingleTitle = "Business Service- Validation Framework -Common child objects rules", 
			acceptanceCriteriaRefs = "CR18.14", 
			given = "Incident has ObjectActionPlan && raiseTask=Yes"
					+ "&& requestToStaffId == null && requestToUnitId == null",
			when = "Incident Submited", 
			then = "Validation error should be returned")
	public void testNewActionPlanWithTaskMandatesRequestToUnitOrStaff()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();

			objectActionPlan = CommonPoleObjectTestUtils.getObjectActionPlan(officerReportingId);
			objectActionPlan.setRaiseTask(ObjectActionPlanTaskHelperBean.RAISE_TASK_TRUE);			
			objectActionPlan.setRequestToStaffId(null);
			objectActionPlan.setRequestToUnit(null);
			objectActionPlan.setReplyToStaffId(officerReportingId);

			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {					
			assertTrue("ValidationException: " + ide.getMessage(), ide.getMessage().contains(
					"error>Fields either requestToStaffId or requestToUnit should have value when task have been "
					+ "raised for ObjectActionPlan but not both.</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37152, 
			mingleTitle = "Business Service- Validation Framework -Common child objects rules", 
			acceptanceCriteriaRefs = "CR18.14", 
			given = "Incident has ObjectActionPlan && raiseTask=Yes"
					+ "&& replyToStaffId == null && replyToUnitId == null", 
			when = "Incident Submited", 
			then = "Validation error should be returned")
	public void testNewActionPlanWithTaskMandatesReplyToUnitOrStaff()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();

			objectActionPlan = CommonPoleObjectTestUtils.getObjectActionPlan(officerReportingId);
			objectActionPlan.setRaiseTask(ObjectActionPlanTaskHelperBean.RAISE_TASK_TRUE);
			objectActionPlan.setRequestToStaffId(officerReportingId);
			objectActionPlan.setReplyToStaffId(null);
			objectActionPlan.setReplyToUnit(null);

			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(), ide.getMessage().contains(
					"error>Fields either replyToStaffId or replyToUnit should have value when task have been raised "
					+ "for ObjectActionPlan but not both.</"));
			
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37152, 
			mingleTitle = "Business Service- Validation Framework -Common child objects rules", 
			acceptanceCriteriaRefs = "CR18.18", 
			given = "Incident has ObjectActionPlan && raiseTask=Yes && statusCode = null", 
			when = "Incident Submited", 
			then = "Validation error should be returned")
	public void testNewActionPlanWithTaskMandatesStatusCode()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();

			objectActionPlan = CommonPoleObjectTestUtils.getObjectActionPlan(officerReportingId);
			objectActionPlan.setRaiseTask(ObjectActionPlanTaskHelperBean.RAISE_TASK_TRUE);			
			objectActionPlan.setStatusCode(null);

			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {					
			assertTrue("ValidationException: " + ide.getMessage(), ide.getMessage().contains(
					"error>statusCode must have a value</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37152, 
			mingleTitle = "Business Service- Validation Framework -Common child objects rules", 
			acceptanceCriteriaRefs = "CR18.19", 
			given = "Incident has ObjectActionPlan && raiseTask=Yes && statusCode = COMPLETED", 
			when = "Incident Submited", 
			then = "Validation error should be returned")
	public void testCompletedActionPlanCannotHaveTaskRaisedAgainstIt()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();

			objectActionPlan = CommonPoleObjectTestUtils.getObjectActionPlan(officerReportingId);
			objectActionPlan.setRaiseTask(ObjectActionPlanTaskHelperBean.RAISE_TASK_TRUE);			
			objectActionPlan.setStatusCode("COMPLETED");

			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {					
			assertTrue("ValidationException: " + ide.getMessage(), ide.getMessage().contains(
					"error>statusCode does not match the expected value OUTSTANDING</"));						
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37152, 
			mingleTitle = "Business Service- Validation Framework -Common child objects rules", 
			acceptanceCriteriaRefs = "CR18.20", 
			given = "Incident has ObjectActionPlan && raiseTask=Yes "
					+ "&& all CR 18.1 to 18.19 are validated (means ObjectActionPlan has all the required details)", 
			when = "Incident Submited", 
			then = "No error should be returned and a workflow task should be initiated as 'Perform Task'")
	public void testActionPlanWithTaskGeneratesTaskToWorkflow()
			throws Exception {

		incidentWithValidObjectActionPlan = true;
		
		PoleObjectDto poleObject = createPoleObjectDto();
		EventDto event = (EventDto) poleObject;

		objectActionPlan = new ObjectActionPlanDto(); 
		objectActionPlan.setRaiseTask(ObjectActionPlanTaskHelperBean.RAISE_TASK_TRUE);
		objectActionPlan.setDateCreated(new PoleDate());
		objectActionPlan.setDateCompleted(null);
		objectActionPlan.setSeqNo(1);
		objectActionPlan.setDateDue(new PoleDate().addDays(2));						
		objectActionPlan.setTaskCreatedDateTime(new PoleTimePoint());
		objectActionPlan.setRequestToUnit(null);
		objectActionPlan.setRequestToStaffId(officerReportingId);
		objectActionPlan.setReplyToStaffId(null);
		objectActionPlan.setReplyToUnit("42 ESSEX MARAT (POLICE)");
		objectActionPlan.setNotifyWhenComplete("T");			
		objectActionPlan.setStatusCode("OUTSTANDING");		
		objectActionPlan.setDetails("This is test for details #37152 CR 18.20");			
		objectActionPlan.setActionPlanUpdates(null);
		event.addObjectActionPlan(objectActionPlan);
		
		doCreateEventObject(true, poleObject);
		
	}
	
	
	/**
	 * Test to check if event have objectActionPlan with RaiseTask=No
	 * Then following to be validated.
	 * 1) notifyWhenComplete become forbidden
	 * 2) requestToStaffId & requestToUnit become forbidden
	 * 3) replyToStaffId & replyToUnit become forbidden
	 */@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37152, 
			mingleTitle = "Business Service- Validation Framework -Common child objects rules", 
			acceptanceCriteriaRefs = "CR18.21,CR18.22,CR18.23,CR18.24,CR18.25", 
			given = "Incident has ObjectActionPlan && raiseTask=NO "
					+ "&& (requestToStaffId != null"
					+ "|| requestToUnitId != null"
					+ "|| replyToStaffId != null "
					+ "|| replyToUnitId != null"
					+ "|| notifyWhenComplete != null)", 
			when = "Incident Submited", 
			then = "Validation error should be returned")
	public void testActionPlanWithoutTaskCannotHaveTaskRelatedFieldsSet()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			IncidentDto incident = (IncidentDto) poleObject;

			objectActionPlan = CommonPoleObjectTestUtils.getObjectActionPlan(officerReportingId);
			objectActionPlan.setRaiseTask(ObjectActionPlanTaskHelperBean.RAISE_TASK_FALSE);
			objectActionPlan.setNotifyWhenComplete("T");
			objectActionPlan.setRequestToStaffId(officerReportingId);
			objectActionPlan.setRequestToUnit(incident.getIncidentTxData().getCreateIncidentTxData().getImuUnit());
			objectActionPlan.setReplyToStaffId(officerReportingId);
			objectActionPlan.setReplyToUnit(incident.getIncidentTxData().getCreateIncidentTxData().getImuUnit());
			
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {					
			assertTrue("ValidationException: " + ide.getMessage(), ide.getMessage().contains(
					"error>notifyWhenComplete must not have a value</"));
			assertTrue("ValidationException: " + ide.getMessage(), ide.getMessage().contains(
					"error>requestToStaffId must not have a value</"));
			assertTrue("ValidationException: " + ide.getMessage(), ide.getMessage().contains(
					"error>requestToUnit must not have a value</"));
			assertTrue("ValidationException: " + ide.getMessage(), ide.getMessage().contains(
					"error>replyToStaffId must not have a value</"));
			assertTrue("ValidationException: " + ide.getMessage(), ide.getMessage().contains(
					"error>replyToUnit must not have a value</"));			
		}
	}
	
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37152, 
			mingleTitle = "Business Service- Validation Framework -Common child objects rules", 
			acceptanceCriteriaRefs = "CR18.26,CR18.27,CR18.28,CR18.29", 
			given = "Incident has ObjectActionPlan && actionPlanUpdate is supplied with null values", 
			when = "Incident Submited", 
			then = "Validation error should be returned")
	public void testActionPlanUpdateFieldsAreMandatory()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();

			objectActionPlan = CommonPoleObjectTestUtils.getObjectActionPlan(officerReportingId);
			ActionPlanUpdateDto actionPlanUpdate = new ActionPlanUpdateDto();
			actionPlanUpdate.setModificationStatus(ModificationStatusDto.CREATE);
			actionPlanUpdate.setUpdateDate(null);
			actionPlanUpdate.setUpdateRemarks(null);
			actionPlanUpdate.setUpdateTime(null);
			actionPlanUpdate.setUpdateUserId(null);
			objectActionPlan.addActionPlanUpdate(actionPlanUpdate);
			
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {					
			assertTrue("ValidationException: " + ide.getMessage(), ide.getMessage().contains(
					"error>updateDate must have a value</"));
			assertTrue("ValidationException: " + ide.getMessage(), ide.getMessage().contains(
					"error>updateRemarks must have a value</"));
			assertTrue("ValidationException: " + ide.getMessage(), ide.getMessage().contains(
					"error>updateTime must have a value</"));
			assertTrue("ValidationException: " + ide.getMessage(), ide.getMessage().contains(
					"error>updateUserId must have a value</"));						
		}
	}
	
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37152, 
			mingleTitle = "Business Service- Validation Framework -Common child objects rules", 
			acceptanceCriteriaRefs = "CR19.1 to CR 19.9", 
			given = "Incident has PolicyFile with valid details. Happy path scenario", 
			when = "Incident Submited", 
			then = "No validation error should be returned")
	public void testIncidentWhenHavingValidPolicyFileDetails()
			throws Exception {
		
		PoleObjectDto poleObject = createPoleObjectDto();

		policyFile = CommonPoleObjectTestUtils.getPolicyFile(officerReportingId);
		doCreateEventObject(true, poleObject);
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37152, 
			mingleTitle = "Business Service- Validation Framework -Common child objects rules", 
			acceptanceCriteriaRefs = "CR19.1 to CR 19.9", 
			given = "Incident has PolicyFile with invalid details", 
			when = "Incident Submited", 
			then = "Validation error should be returned")
	public void testIncidentWhenHavingInvalidPolicyFileDetails()
			throws Exception {
		
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			
			policyFile = CommonPoleObjectTestUtils.getPolicyFile(officerReportingId);
			//mandatory fields
			policyFile.setModificationStatus(ModificationStatusDto.CREATE);
			policyFile.setEntryDate(null);
			policyFile.setEntryTime(null);
			policyFile.setEnteredByOfficerId(null);
			//forbidden fields
			policyFile.setSensitive(true);
			policyFile.setOutcomeCv("BS Test Outcome Cv");
			policyFile.setOutcomeText("BS Test Outecome Text");
			policyFile.setOutcomeRecommendation(true);
			policyFile.setShareEntry(true);
			policyFile.setSharingRationale("BS Test Outcome Rationale");									
			
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>entryDate must have a value</"));
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>entryTime must have a value</"));
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>enteredByOfficerId must have a value</"));
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>sensitive must not have a value</"));
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>outcomeCv must not have a value</"));
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>outcomeText must not have a value</"));
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>outcomeRecommendation must not have a value</"));
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>shareEntry must not have a value</"));
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>sharingRationale must not have a value</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37152, 
			mingleTitle = "Business Service- Validation Framework -Common child objects rules", 
			acceptanceCriteriaRefs = "N/A", 
			given = "Incident has ObjectActionPlan && (raiseTask = F && statusCode = OUTSTANDING && taskCreatedDateTime"
					+ " != null)", 
			when = "Incident Submited", 
			then = "Validation error should be returned")
	public void testNewActionPlanForbidTaskCreatedDateTime()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();

			objectActionPlan = CommonPoleObjectTestUtils.getObjectActionPlan(officerReportingId);
			objectActionPlan.setRaiseTask(ObjectActionPlanTaskHelperBean.RAISE_TASK_FALSE);
			objectActionPlan.setStatusCode("OUTSTANDING");
			objectActionPlan.setTaskCreatedDateTime(new PoleTimePoint());

			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>taskCreatedDateTime must not have a value</"));			
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37152, 
			mingleTitle = "Business Service- Validation Framework -Common child objects rules", 
			acceptanceCriteriaRefs = "N/A", 
			given = "Incident has ObjectActionPlan with raiseTask = T & statusCode = OUTSTANDING", 
			when = "Incident Submited with dateCompleted != null && officeCompletingAction != null && remarks != null"
					+ " && dateDue < today", 
			then = "Validation error should be returned")
	public void testNewActionPlanForbidFewFieldsIfRaiseTaskTrueAndStatusOutstanding()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();

			objectActionPlan = CommonPoleObjectTestUtils.getObjectActionPlan(officerReportingId);
			objectActionPlan.setRaiseTask(ObjectActionPlanTaskHelperBean.RAISE_TASK_TRUE);
			objectActionPlan.setStatusCode("OUTSTANDING");
			objectActionPlan.setDateCompleted(new PoleDate());
			objectActionPlan.setOfficerCompletingAction(officerReportingId);
			objectActionPlan.setRemarks("This is test");
			objectActionPlan.setDateDue(new PoleDate().addDays(-2));

			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>dateCompleted must not have a value</"));	
			assertTrue(ide.getMessage().contains("error>officerCompletingAction must not have a value</"));
			assertTrue(ide.getMessage().contains("error>remarks must not have a value</"));
			assertTrue(ide.getMessage().contains("error>dateDue cannot be lesser than todays date</"));			
		}
	}
	
	/** ============================ #37109 - Vehicle Gherkins ============================ **/

	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37109, 
			mingleTitle = "Business Service- Validation Framework - Links to vehicle objects", 
			acceptanceCriteriaRefs = "CR9.1.1", 
			given = "Vechicle is linked and link is unresearched", 
			when = "Incident Submited", 
			then = "No error should be returned")
	public void testIncidentWhenVehicleIsLinkedAndLinkIsUnresearched() throws Exception {		
		PoleObjectDto poleObject = createPoleObjectDto();
		for (LinkDto linkDto : poleObject.getLinks()) {
			if (linkDto.getToPoleObject() instanceof VehicleDto) {
				linkDto.setResearched(false);
				break;
			}
		}
		doCreateEventObject(true, poleObject);		
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37109, 
			mingleTitle = "Business Service- Validation Framework - Links to vehicle objects", 
			acceptanceCriteriaRefs = "CR9.1.2", 
			given = "Vechicle is linked and link is unresearched but vehicle is researched or vice versa", 
			when = "Incident Submited", 
			then = "Validation error should be returned")
	public void testIncidentWhenVehicleIsLinkedAndLinkIsUnresearchedAndVehicleIsResearched()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof VehicleDto) {
					linkDto.setResearched(false);
					((VehicleDto) toPoleObject).setResearched(true);
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>researched does not match the expected value {current}.toPoleObject.researched</"));			
		}
	}
	
	/** ============================ #37110 - Organisation Gherkins ============================ **/
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37110, 
			mingleTitle = "Business Service- Validation Framework - Links to Organisation Object", 
			acceptanceCriteriaRefs = "CR10.1.1", 
			given = "Organisation is linked and link is unresearched", 
			when = "Incident Submited", 
			then = "No error should be returned")
	public void testIncidentWhenOrganisationIsLinkedAndLinkIsUnresearched() throws Exception {		
		PoleObjectDto poleObject = createPoleObjectDto();
		for (LinkDto linkDto : poleObject.getLinks()) {
			if (linkDto.getToPoleObject() instanceof OrganisationDto) {
				linkDto.setResearched(false);
				break;
			}
		}
		doCreateEventObject(true, poleObject);		
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37110, 
			mingleTitle = "Business Service- Validation Framework - Links to Organisation objects", 
			acceptanceCriteriaRefs = "CR10.1.2", 
			given = "Organisation is linked and link is unresearched but organisation is researched or vice versa", 
			when = "Incident Submited", 
			then = "Validation error should be returned")
	public void testIncidentWhenOrganisationIsLinkedAndLinkIsUnresearchedAndOrganisationIsResearched()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof OrganisationDto) {
					linkDto.setResearched(false);
					((OrganisationDto) toPoleObject).setResearched(true);
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>researched does not match the expected value {current}.toPoleObject.researched</"));			
		}
	}
	
	/** ============================ #37113 - Location Gherkins ============================ **/
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37113, 
			mingleTitle = "Business Service- Validation Framework - Links to Location object", 
			acceptanceCriteriaRefs = "CR13.1.1", 
			given = "Location is linked and link is unresearched", 
			when = "Incident Submited", 
			then = "No error should be returned")
	public void testIncidentWhenLocationIsLinkedAndLinkIsUnresearched() throws Exception {		
		PoleObjectDto poleObject = createPoleObjectDto();
		for (LinkDto linkDto : poleObject.getLinks()) {
			if (linkDto.getToPoleObject() instanceof LocationDto
					&& linkDto.getLinkReason().equalsIgnoreCase(INCIDENT_LOCATION_LINK_REASON)) {
				linkDto.setResearched(false);
				((LocationDto)linkDto.getToPoleObject()).setResearched(false);
				break;
			}
		}
		doCreateEventObject(true, poleObject);		
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37113, 
			mingleTitle = "Business Service- Validation Framework - Links to Location object", 
			acceptanceCriteriaRefs = "CR13.1.2", 
			given = "Location is linked and link is unresearched but Location is researched or vice versa", 
			when = "Incident Submited", 
			then = "Validation error should be returned")
	public void testIncidentWhenLocationIsLinkedAndLinkIsUnresearchedAndLocationIsResearched()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof LocationDto
						&& linkDto.getLinkReason().equalsIgnoreCase(INCIDENT_LOCATION_LINK_REASON)) {
					linkDto.setResearched(false);
					((LocationDto) toPoleObject).setResearched(true);
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>researched does not match the expected value {current}.toPoleObject.researched</"));			
		}
	}

	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 38044, 
			mingleTitle = "MS DT- BSvcs allowing data more than max length for PNC MO field of Investigation", 
			acceptanceCriteriaRefs = "UNKNOWN", 
			given = "When more than 768 characters are entered in PNC MO field", 
			when = "Incident Submited", 
			then = "Validation error should be returned")
	public void testIncidentWhenPNCMoFieldsLenghtIsMoreThan768 () throws Exception {
		try {
			IncidentDto poleObject = (IncidentDto) createPoleObjectDto();
			String initialMo = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate eleifend tellus. Aenean leo ligula, porttitor eu, consequat vitae, eleifend ac, enim. Aliquam lorem ante, dapibus in, viverra quis, feugiat a, tellus. Phasellus viverra nulla ut metus varius laoreet. Quisque rutrum. Aenean imperdiet. Etiam ultricies nisi vel augue. Curabitur ullamcorper ultricies nisi. Nam eget dui. Etiam rhoncus. Maecenas tempus, tellus eget condimentum rhoncus, sem quam semper libero, sit amet adipiscing sem neque sed ipsum. N";
			poleObject.setInitialMo(initialMo);

			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>initialMo is too long</"));			
		}
	}

	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 38090, 
			mingleTitle = "MS DT-For incidentLogEntry, 'entryType' accepts invalid data which is not present in CV list LOG_ENTRY_TYPE",
			acceptanceCriteriaRefs = "UNKNOWN", 
			given ="child object incidentLogEntry is present and entryType does not match the data present in CV List 'LOG_ENTRY_TYPE' ",
			when = "Incident Submited",
			then = "Validation error should be returned")
	public void testIncidentWhenForLogEntryEntryTextContainsValueNotPresentInCvList() throws Exception {
		try {
			IncidentLogEntryDto logEntry = new IncidentLogEntryDto();
			logEntry.setModificationStatus(ModificationStatusDto.CREATE);
			logEntry.setEntryDate(new PoleDate());
			logEntry.setEntryTime(new PoleTime());
			logEntry.setEntryText("Test Dummy");
			logEntry.setEnteredByOfficerId(Integer.valueOf(officerReportingId));
			logEntry.setEntryType("notPresentInCVList");

			incidentDto.addIncidentLogEntry(logEntry);
			doCreateEventObject(false, incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>entryType has an incorrect CV value for list LOG_ENTRY_TYPE</"));
		}
	}

	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 38044, 
			mingleTitle = "MS DT- For Referral tab, 'Referral date' should be conditional mandatory on 'Referral time'", 
			acceptanceCriteriaRefs = "UNKNOWN", 
			given = "When only ReferralTime is added, but ReferralDate is not added", 
			when = "Incident Submited", 
			then = "Validation error should be returned")
	public void testIncidentWhenReferralDateNotAddedButReferralTimeAdded () throws Exception {
		try {
			IncidentDto incidentDto = (IncidentDto) createPoleObjectDto();
			IncidentReferralDto referralDto = incidentDto.getReferrals().get(0);
			referralDto.setSubmittedTime(new PoleTime());

			doCreateEventObject(true, incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>submittedDate must have a value</"));			
		}
	}

	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=343,
			mingleRef = 38320, 
			mingleTitle = "WP343 - Business Service - Create Investigation - Business Service Operation - Create "
					+ "Generic Investigation (Phase 1) v1", 
			acceptanceCriteriaRefs = "CR6.2", 
			given = "IncidentPersonInfo child object is provided on the link && IncidentPersonInfo.victimElectsToBeUpdated "
					+ "contains following values"
					+ "(Opt in Full service - Standard || Opt in Full service - Enhanced || Opt in Partial service - Standard "
					+ "|| Opt in Partial service - Enhanced || Opt in - Business victim is supplied)"
					+ " && IncidentPersonInfo.frequencyOfUpdates is supplied with a value greater than 3 numerical digits", 
			when = "Incident Submited", 
			then = "Validation error should be returned")
	public void testIncidentWhenFrequencyOfUpdatesFieldAddedWithNumericsAndAlphabetsAndSpecialCharacters () throws Exception {
		try {
			for (LinkDto linkDto : incidentDto.getLinks()) {
				if (linkDto.getToPoleObject() instanceof PersonDto 
						&& linkDto.getLinkReason().equalsIgnoreCase("VICTIM")) {

					for (IncidentPersonInfoDto incidentPersonInfo : linkDto.getIncidentPersonInfoList()) {
						incidentPersonInfo.setVictimElectsToBeUpdated("IN_STANDARD");
						incidentPersonInfo.setIsVictimVulnerable(false);
						incidentPersonInfo.setFrequencyOfUpdates("1sfdsf$");
						break;
					}
				}
			}
			doCreateEventObject(true, incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>frequencyOfUpdates is not valid for pattern ^[0-9]{1,3}$</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=343,
			mingleRef = 38320, 
			mingleTitle = "WP343 - Business Service - Create Investigation - Business Service Operation - Create Generic Investigation (Phase 1) v1", 
			acceptanceCriteriaRefs = "CR2.8,CR2.12,CR2.13,CR5.5a,CR5.5b,CR5.5c,CR5.5d,CR5.5e,CR6", 
			given = "Incident is supplied with victimCrown,incidentCreatedDate,incidentCreatedTime as null && "
					+ "a Person/Organisation is linked to Incident with any of the following linkReasons"
					+ "('VICTIM' or 'ASSOCIATED VICTIM' or 'INVOLVED PARTY' or 'MISSING PERSON' or 'CHILD IN HOUSEHOLD' or 'WITNESS')"
					+ "&& an IncidentPersonInfo child object is provided on the link where field "
					+ "'Victim Elects To Be Updated' contains any of the following values"
					+ "(IN_BUSINESS or IN_STANDARD or IN_ENHANCED or PARTIAL_STANDARD or PARTIAL_ENHANCED)", 
			when = "Incident Create request submited", 
			then = "Incident should be create without any errors and appropriate fields on Incident and"
					+ " link.IncidentPersonInfo should be defaulted as follows."
					+ "Incident fields:"
					+ "victimCrown = 'F', incidentCreatedDate = current date, incidentCreatedTime = current GMT time"
					+ "IncidentPersonInfo fields:"
					+ "oicAllocated = true, oicChanged = true, suspectArrested = true, suspectCharged = true,"
					+ " suspectBailed = true, frequencyOfUpdates = sysparam.CRIME_VICTIM_CONTACT_FREQUENCY")
	public void testIncidentWhenSuplliedWithFewRequiredNullFieldsAndThenThoseFieldsDefaulted() throws Exception {		
		// set completed true because VictimCrown is applicable only when completed = true
		// rest all fields are applicable in all scenarios irrespective of value of completed flag
		incidentDto.getIncidentTxData().getCreateIncidentTxData().setCompleted(true);
		incidentDto.setVictimCrown(null);
		incidentDto.setIncidentCreatedDate(null);
		incidentDto.setIncidentCreatedTime(null);
		for (LinkDto linkDto : incidentDto.getLinks()) {
			if (linkDto.getToPoleObject() instanceof PersonDto 
					&& linkDto.getLinkReason().equalsIgnoreCase("VICTIM")) {

				for (IncidentPersonInfoDto incidentPersonInfo : linkDto.getIncidentPersonInfoList()) {
					incidentPersonInfo.setVictimElectsToBeUpdated("IN_STANDARD");
					incidentPersonInfo.setOicAllocated(null);
					incidentPersonInfo.setOicChanged(null);
					incidentPersonInfo.setSuspectArrested(null);
					incidentPersonInfo.setSuspectCharged(null);
					incidentPersonInfo.setSuspectBailed(null);
					incidentPersonInfo.setFrequencyOfUpdates(null);
					incidentPersonInfo.setUpdates(null);
					break;
				}
			}
		}
		doCreateEventObject(true, incidentDto);
		assertNotNull("VictimCrown is null !!", incidentDto.getVictimCrown());
		assertNotNull("IncidentCreatedDate is null !!", incidentDto.getIncidentCreatedDate());
		assertNotNull("IncidentCreatedTime is null !!", incidentDto.getIncidentCreatedTime());
		
		for (LinkDto linkDto : incidentDto.getLinks()) {
			if (linkDto.getToPoleObject() instanceof PersonDto 
					&& linkDto.getLinkReason().equalsIgnoreCase("VICTIM")) {
				for (IncidentPersonInfoDto incidentPersonInfo : linkDto.getIncidentPersonInfoList()) {
					
					assertNotNull("OicAllocated is null !!", incidentPersonInfo.getOicAllocated());
					assertNotNull("OicChanged is null !!", incidentPersonInfo.getOicChanged());
					assertNotNull("SuspectArrested is null !!", incidentPersonInfo.getSuspectArrested());
					assertNotNull("SuspectCharged is null !!", incidentPersonInfo.getSuspectCharged());
					assertNotNull("SuspectBailed is null !!", incidentPersonInfo.getSuspectBailed());
					assertNotNull("FrequencyOfUpdates is null !!", incidentPersonInfo.getFrequencyOfUpdates());
					break;
				}
			}
		}			
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=343,
			mingleRef = 38320, 
			mingleTitle = "WP343 - Business Service - Create Investigation - Business Service Operation - Create Generic Investigation (Phase 1) v1", 
			acceptanceCriteriaRefs = "CR2.8.1,CR2.14,CR2.15,CR5.5f,CR6.1", 
			given = "Incident is supplied with valued of fields victimCrown='T',incidentCreatedDate = currentDate ,incidentCreatedTime = currentTime && "
					+ "a Person/Organisation is linked to Incident with any of the following linkReasons"
					+ "('VICTIM' or 'ASSOCIATED VICTIM' or 'INVOLVED PARTY' or 'MISSING PERSON' or 'CHILD IN HOUSEHOLD' or 'WITNESS')"
					+ "&& an IncidentPersonInfo child object is provided on the link where field "
					+ "'Victim Elects To Be Updated' contains any of the following values"
					+ "(IN_BUSINESS or IN_STANDARD or IN_ENHANCED or PARTIAL_STANDARD or PARTIAL_ENHANCED)"
					+ "&& fields oicAllocated, oicChanged, suspectArrested, suspectCharged, suspectBailed, frequencyOfUpdates "
					+ "are supplied as 'false' and frequencyOfUpdates = 123", 
			when = "Incident Create request submited", 
			then = "Incident should be create without any errors and supplied fields values should be retained by BS"
					+ " link.IncidentPersonInfo should be defaulted as follows."
					+ "Incident fields:"
					+ "victimCrown = 'T', incidentCreatedDate = current date, incidentCreatedTime = current GMT time"
					+ "IncidentPersonInfo fields:"
					+ "oicAllocated = false, oicChanged = false, suspectArrested = false, suspectCharged = false,"
					+ " suspectBailed = false, frequencyOfUpdates = 123")
	public void testIncidentWhenSuplliedWithFewRequiredFieldsAndThoseFieldsValueSholudBeRetained() throws Exception {	
		String victimCrown = "T";		
		PoleDate currentDate = new PoleDate();		
		PoleTime currentTime = getCurrentPoleTimeIgnoringMilliseconds();				
		
		String frequencyOfUpdate = "123";
		boolean oicAllocated = false; 
		boolean oicChanged = false;
		boolean suspectArrested = false;
		boolean suspectCharged = false;
		boolean suspectBailed = false;
		
		// set completed true because VictimCrown is applicable only when completed = true
		// rest all fields are applicable in all scenarios irrespective of value of completed flag
		incidentDto.getIncidentTxData().getCreateIncidentTxData().setCompleted(true);
		incidentDto.setVictimCrown(victimCrown);		
		incidentDto.setIncidentCreatedDate(currentDate);				
		incidentDto.setIncidentCreatedTime(currentTime);
		
		for (LinkDto linkDto : incidentDto.getLinks()) {
			if (linkDto.getToPoleObject() instanceof PersonDto 
					&& linkDto.getLinkReason().equalsIgnoreCase("VICTIM")) {

				for (IncidentPersonInfoDto incidentPersonInfo : linkDto.getIncidentPersonInfoList()) {
					incidentPersonInfo.setVictimElectsToBeUpdated("IN_STANDARD");
					incidentPersonInfo.setOicAllocated(oicAllocated);
					incidentPersonInfo.setOicChanged(oicChanged);
					incidentPersonInfo.setSuspectArrested(suspectArrested);
					incidentPersonInfo.setSuspectCharged(suspectCharged);
					incidentPersonInfo.setSuspectBailed(suspectBailed);
					incidentPersonInfo.setFrequencyOfUpdates(frequencyOfUpdate);
					incidentPersonInfo.setUpdates(null);
					break;
				}
			}
		}
		doCreateEventObject(true, incidentDto);
		assertEquals("VictimCrown orginal value not retained !!", victimCrown, incidentDto.getVictimCrown());
		assertEquals("IncidentCreatedDate orginal value not retained !!", currentDate, incidentDto.getIncidentCreatedDate());
		assertEquals("IncidentCreatedTime orginal value not retained !!", currentTime, incidentDto.getIncidentCreatedTime());
		
		for (LinkDto linkDto : incidentDto.getLinks()) {
			if (linkDto.getToPoleObject() instanceof PersonDto 
					&& linkDto.getLinkReason().equalsIgnoreCase("VICTIM")) {
				for (IncidentPersonInfoDto incidentPersonInfo : linkDto.getIncidentPersonInfoList()) {
					
					assertEquals("OicAllocated orginal value not retained !!", oicAllocated, incidentPersonInfo.getOicAllocated());
					assertEquals("OicChanged orginal value not retained !!", oicChanged, incidentPersonInfo.getOicChanged());
					assertEquals("SuspectArrested orginal value not retained !!", suspectArrested, incidentPersonInfo.getSuspectArrested());
					assertEquals("SuspectCharged orginal value not retained !!", suspectCharged, incidentPersonInfo.getSuspectCharged());
					assertEquals("SuspectBailed orginal value not retained !!", suspectBailed, incidentPersonInfo.getSuspectBailed());
					assertEquals("FrequencyOfUpdates orginal value not retained !!", frequencyOfUpdate, incidentPersonInfo.getFrequencyOfUpdates());
					break;
				}
			}
		}			
	}	
	
	 
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=368,
			mingleRef = 38664, 
			jiraRef = "CCI-38781",
			mingleTitle = "WP368 - Business Service - Create Investigation - Business Service Operation "
					+ "- Create RTC Investigation (Phase 1) v1 - MPS-BS Sprint 1", 
			acceptanceCriteriaRefs = "CR14.1,CR15", 
			given = "Create Incidnent request supplied with currentClassification field value passed as "
					+ "'RTC Investigation' and completed flag == true and Incident intialMo not supplied", 
			when = "Incident Create request is submitted", 
			then = "No Validation error must be returned and an Investigation must be created")
	public void testCreateIncidentWithCurrentClassificationRtcInvestigation()
			throws Exception {
			incidentDto.setInitialMo(null);
			incidentDto.setCurrentClassification(IncidentTestUtils.RTC_CURRENT_CLASSIFICATION);
			doCreateEventObject(true, incidentDto);
	}
	
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=368,
			mingleRef = 38665, 
			jiraRef = "CCI-38780",
			mingleTitle = "WP369 - Business Service - Create Investigation - Business Service Operation - "
						+ "Create Missing Person Investigation (Phase 1) v1 - MPS-BS Sprint 1", 
			acceptanceCriteriaRefs = "CR14.1,CR15", 
			given = "Create Incidnent request supplied with currentClassification field value passed as "
					+ "'Missing Person Investigation' and completed flag == true and Incident intialMo not supplied", 
			when = "Incident Create request is submitted", 
			then = "No Validation error must be returned and an Investigation must be created")
	public void testCreatetIncidentWithCurrentClassificationMissingPersonInvestigation()
			throws Exception {
			incidentDto.setInitialMo(null);
			incidentDto.setCurrentClassification(IncidentTestUtils.MISSING_PERSON_CURRENT_CLASSIFICATION);
			doCreateEventObject(true, incidentDto);
	}
	
	
	private void setupIncidentForVictimVulnerableMandatoryTests(PutPoleObjectsRequestDto putPoleObjectsRequest, 
			String victimVulnerableDate) {
		
		if (victimVulnerableDate != null) {
			systemParamsCacheBean.setSystemParameter_TESTONLY("VICTIM_VULNERABLE_MANDATORY_ON_AND_AFTER_DATE",
				victimVulnerableDate);
		}
		
		incidentDto.getIncidentTxData().getCreateIncidentTxData().setCompleted(Boolean.FALSE);
		
		PersonDto person = CommonPoleObjectTestUtils.getPerson(new Integer(-1), "Bloggs", "Fred");
		
		IncidentPersonInfoDto incidentPersonInfo = new IncidentPersonInfoDto();
		incidentPersonInfo.setModificationStatus(ModificationStatusDto.CREATE);
		incidentPersonInfo.setVsAgreedToVictimSupport(Boolean.FALSE);
		incidentPersonInfo.setVictimElectsToBeUpdated("OUT");
		
		LinkDto incidentPersonLink = new LinkDto();
		incidentPersonLink.setModificationStatus(ModificationStatusDto.CREATE);
		incidentPersonLink.setSourcePoleObjectRef(incidentDto.getObjectRef());
		incidentPersonLink.setSourcePoleObjectType(PoleNames.INCIDENT);
		incidentPersonLink.setFromPoleObjectRef(incidentDto.getObjectRef());
		incidentPersonLink.setFromPoleObjectType(PoleNames.INCIDENT);
		incidentPersonLink.setLinkReason(SubmitIncidentServiceConstants.VICTIM_LINK_REASON);
		incidentPersonLink.setToPoleObject(person);
		
		incidentPersonLink.addIncidentPersonInfo(incidentPersonInfo);
		
		incidentDto.setLinks(Arrays.asList(incidentPersonLink));
		
		GraphSetObjectRefGraphWalkerTestUtil graphSetObjectRefGraphWalker = new GraphSetObjectRefGraphWalkerTestUtil();
		graphSetObjectRefGraphWalker.walk(incidentDto);
		
		PoleDtoUtils.addBusinessServiceInfo(putPoleObjectsRequest,
				BusinessServicesTestUtils.createBusinessServiceInfo(securityContextId));
		putPoleObjectsRequest.addPoleObject(incidentDto);
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=303, 
			jiraRef = "CCI-38963",
			mingleTitle = "WP303 Is Victim Vulnerable - Mandatory in Investigation", 
			acceptanceCriteriaRefs = "1", 
			given = "System parameter ON", 
			when = "Investigation is saved with linked victim", 
			then = "Victim Vulnerable indicator needs to be populated")
	public void testIsVictimVulnerableMandatoryWhenSysParamSet() throws Exception {
		try {
			PutPoleObjectsRequestDto putPoleObjectsRequest = new PutPoleObjectsRequestDto();
			
			setupIncidentForVictimVulnerableMandatoryTests(putPoleObjectsRequest, getCncRefMandatoryDate());

			Map<EntityKey, PoleEntityDto> originalObjectsMap = new HashMap<EntityKey, PoleEntityDto>();
			validator.validate(putPoleObjectsRequest, originalObjectsMap);
			fail("Expected a validation exception");
		} catch (ValidationException ve) {
			assertTrue("ValidationException: " + ValidationException.convertToXml(ve),
					ValidationException.convertToXml(ve).contains("error>isVictimVulnerable is required when the incident being created is "
							+ "after the date specified in the system parameter VICTIM_VULNERABLE_MANDATORY_ON_AND_AFTER_DATE, "
							+ "or if being created from a Contact Event, and the date the Contact Event was created is "
							+ "after the date specified in the system parameter</"));
		} finally {
			systemParamsCacheBean.setSystemParameter_TESTONLY("VICTIM_VULNERABLE_MANDATORY_ON_AND_AFTER_DATE", null);
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=303, 
			jiraRef = "CCI-38963",
			mingleTitle = "WP303 Is Victim Vulnerable - Mandatory in Investigation", 
			acceptanceCriteriaRefs = "TODO", 
			given = "System parameter set to a date in the future", 
			when = "Investigation is saved with linked victim and Victim Vulnerable Indicator is NULL", 
			then = "No validation error")
	public void testIsVictimVulnerableNotMandatoryWhenSysParamSetInFuture() throws Exception {
		try {
			PutPoleObjectsRequestDto putPoleObjectsRequest = new PutPoleObjectsRequestDto();
			
			Calendar now = Calendar.getInstance();
			now.add(Calendar.YEAR, 1);
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			
			setupIncidentForVictimVulnerableMandatoryTests(putPoleObjectsRequest, sdf.format(now.getTime()));
	
			Map<EntityKey, PoleEntityDto> originalObjectsMap = new HashMap<EntityKey, PoleEntityDto>();
			validator.validate(putPoleObjectsRequest, originalObjectsMap);

		} finally {
			systemParamsCacheBean.setSystemParameter_TESTONLY("VICTIM_VULNERABLE_MANDATORY_ON_AND_AFTER_DATE", null);
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=303, 
			jiraRef = "CCI-38963",
			mingleTitle = "WP303 Is Victim Vulnerable - Mandatory in Investigation", 
			acceptanceCriteriaRefs = "4", 
			given = "System parameter ON and Investigation is saved with linked victim", 
			when = "Victim Vulnerable indicator = NO", 
			then = "Reason vulnerable is not mandatory")
	public void testPersonVulnerabilityNotMandatoryWhenIsVictimVulnerableFalse() throws Exception {
		try {
			PutPoleObjectsRequestDto putPoleObjectsRequest = new PutPoleObjectsRequestDto();
			
			setupIncidentForVictimVulnerableMandatoryTests(putPoleObjectsRequest, getCncRefMandatoryDate());
			
			IncidentPersonInfoDto incidentPersonInfo = incidentDto.getLinks().get(0).getIncidentPersonInfoList().get(0);
			incidentPersonInfo.setIsVictimVulnerable(Boolean.FALSE);

			Map<EntityKey, PoleEntityDto> originalObjectsMap = new HashMap<EntityKey, PoleEntityDto>();
			validator.validate(putPoleObjectsRequest, originalObjectsMap);
		} finally {
			systemParamsCacheBean.setSystemParameter_TESTONLY("VICTIM_VULNERABLE_MANDATORY_ON_AND_AFTER_DATE", null);
		}
	}

	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=303, 
			jiraRef = "CCI-38963",
			mingleTitle = "WP303 Is Victim Vulnerable - Mandatory in Investigation", 
			acceptanceCriteriaRefs = "2", 
			given = "System parameter ON and Investigation is saved with linked victim", 
			when = "Victim Vulnerable indicator = YES", 
			then = "Reason vulnerable is mandatory")
	public void testPersonVulnerabilityMandatoryWhenIsVictimVulnerableTrue() throws Exception {
		try {
			PutPoleObjectsRequestDto putPoleObjectsRequest = new PutPoleObjectsRequestDto();
			
			setupIncidentForVictimVulnerableMandatoryTests(putPoleObjectsRequest, getCncRefMandatoryDate());
			
			IncidentPersonInfoDto incidentPersonInfo = incidentDto.getLinks().get(0).getIncidentPersonInfoList().get(0);
			incidentPersonInfo.setIsVictimVulnerable(Boolean.TRUE);

			Map<EntityKey, PoleEntityDto> originalObjectsMap = new HashMap<EntityKey, PoleEntityDto>();
			validator.validate(putPoleObjectsRequest, originalObjectsMap);
			fail("Expected a validation exception");
		} catch (ValidationException ve) {
			assertTrue("ValidationException: " + ValidationException.convertToXml(ve),
					ValidationException.convertToXml(ve).contains("error>Found 0 instance(s) of IncidentPersonVulnerability but at "
							+ "least 1 instance(s) required</"));
		} finally {
			systemParamsCacheBean.setSystemParameter_TESTONLY("VICTIM_VULNERABLE_MANDATORY_ON_AND_AFTER_DATE", null);
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=303, 
			jiraRef = "CCI-38963",
			mingleTitle = "WP303 Is Victim Vulnerable - Mandatory in Investigation", 
			acceptanceCriteriaRefs = "3", 
			given = "System parameter ON and Investigation is saved with linked victim", 
			when = "Victim Vulnerable indicator = YES and vulnerable reason is not populated", 
			then = "Reason vulnerable is mandatory")
	public void testPersonVulnerabilityVulnerableIndicatorMandatoryWhenIsVictimVulnerableTrue() throws Exception {
		try {
			PutPoleObjectsRequestDto putPoleObjectsRequest = new PutPoleObjectsRequestDto();
			
			setupIncidentForVictimVulnerableMandatoryTests(putPoleObjectsRequest, null);
			
			IncidentPersonInfoDto incidentPersonInfo = incidentDto.getLinks().get(0).getIncidentPersonInfoList().get(0);
			incidentPersonInfo.setIsVictimVulnerable(Boolean.TRUE);
			
			IncidentPersonVulnerabilityDto personVulnerability = new IncidentPersonVulnerabilityDto();
			personVulnerability.setObjectRef(new Integer(-1));
			personVulnerability.setModificationStatus(ModificationStatusDto.CREATE);
			incidentPersonInfo.addCrimePersonVulnerability(personVulnerability);

			Map<EntityKey, PoleEntityDto> originalObjectsMap = new HashMap<EntityKey, PoleEntityDto>();
			validator.validate(putPoleObjectsRequest, originalObjectsMap);
			fail("Expected a validation exception");
		} catch (ValidationException ve) {
			assertTrue("ValidationException: " + ValidationException.convertToXml(ve),
					ValidationException.convertToXml(ve).contains("error>vulnerableIndicator must have a value</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=303, 
			jiraRef = "CCI-38963",
			mingleTitle = "WP303 Is Victim Vulnerable - Mandatory in Investigation", 
			acceptanceCriteriaRefs = "UNKNOWN", 
			given = "System parameter ON and Investigation is saved with linked victim", 
			when = "Victim Vulnerable indicator = YES and Reason vulnerable is set", 
			then = "No validation error")
	public void testHappyPathWhenIsVictimVulnerableTrue() throws Exception {
		PutPoleObjectsRequestDto putPoleObjectsRequest = new PutPoleObjectsRequestDto();
		
		setupIncidentForVictimVulnerableMandatoryTests(putPoleObjectsRequest, null);
		
		IncidentPersonInfoDto incidentPersonInfo = incidentDto.getLinks().get(0).getIncidentPersonInfoList().get(0);
		incidentPersonInfo.setIsVictimVulnerable(Boolean.TRUE);
		
		IncidentPersonVulnerabilityDto personVulnerability = new IncidentPersonVulnerabilityDto();
		personVulnerability.setObjectRef(new Integer(-1));
		personVulnerability.setModificationStatus(ModificationStatusDto.CREATE);
		personVulnerability.setVulnerableIndicator("DISABILITY");
		incidentPersonInfo.addCrimePersonVulnerability(personVulnerability);

		Map<EntityKey, PoleEntityDto> originalObjectsMap = new HashMap<EntityKey, PoleEntityDto>();
		validator.validate(putPoleObjectsRequest, originalObjectsMap);
	}
	
	private void setupIncidentFromContactEventForVictimVulnerableMandatoryTests(PutPoleObjectsRequestDto putPoleObjectsRequest, 
			String victimVulnerableDate) {
		
		if (victimVulnerableDate != null) {
			systemParamsCacheBean.setSystemParameter_TESTONLY("VICTIM_VULNERABLE_MANDATORY_ON_AND_AFTER_DATE",
				victimVulnerableDate);
		}
		
		incidentDto.getIncidentTxData().getCreateIncidentTxData().setCompleted(Boolean.FALSE);
		
		PersonDto person = CommonPoleObjectTestUtils.getPerson(new Integer(-1), "Bloggs", "Fred");
		
		IncidentPersonInfoDto incidentPersonInfo = new IncidentPersonInfoDto();
		incidentPersonInfo.setModificationStatus(ModificationStatusDto.CREATE);
		incidentPersonInfo.setVsAgreedToVictimSupport(Boolean.FALSE);
		incidentPersonInfo.setVictimElectsToBeUpdated("OUT");
		
		LinkDto incidentPersonLink = new LinkDto();
		incidentPersonLink.setModificationStatus(ModificationStatusDto.CREATE);
		incidentPersonLink.setSourcePoleObjectRef(incidentDto.getObjectRef());
		incidentPersonLink.setSourcePoleObjectType(PoleNames.INCIDENT);
		incidentPersonLink.setFromPoleObjectRef(incidentDto.getObjectRef());
		incidentPersonLink.setFromPoleObjectType(PoleNames.INCIDENT);
		incidentPersonLink.setLinkReason(SubmitIncidentServiceConstants.VICTIM_LINK_REASON);
		incidentPersonLink.setToPoleObject(person);
		
		incidentPersonLink.addIncidentPersonInfo(incidentPersonInfo);
		
		ContactEventDto contactEvent = contils.createSimpleContactEvent(null, null);
		contactEvent.setCreatedDateTime(new PoleTimePoint());
		
		LinkDto incidentContactLink = new LinkDto();
		incidentContactLink.setModificationStatus(ModificationStatusDto.CREATE);
		incidentContactLink.setSourcePoleObjectRef(incidentDto.getObjectRef());
		incidentContactLink.setSourcePoleObjectType(PoleNames.INCIDENT);
		incidentContactLink.setLinkReason(PoleObjectsServiceConstants.ASSOCIATED_LINK_REASON);
		incidentContactLink.setToPoleObject(contactEvent);
		
		incidentDto.setLinks(Arrays.asList(incidentPersonLink, incidentContactLink));
		
		GraphSetObjectRefGraphWalkerTestUtil graphSetObjectRefGraphWalker = new GraphSetObjectRefGraphWalkerTestUtil();
		graphSetObjectRefGraphWalker.walk(incidentDto);
		
		PoleDtoUtils.addBusinessServiceInfo(putPoleObjectsRequest,
				BusinessServicesTestUtils.createBusinessServiceInfo(securityContextId));
		putPoleObjectsRequest.addPoleObject(incidentDto);
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=303, 
			jiraRef = "CCI-38963",
			mingleTitle = "WP303 Is Victim Vulnerable - Mandatory in Investigation", 
			acceptanceCriteriaRefs = "UNKNOWN", 
			given = "Create Incident from Contact Event and System parameter ON", 
			when = "Investigation is saved with linked victim", 
			then = "Victim Vulnerable indicator needs to be populated")
	public void testIsVictimVulnerableMandatoryWhenSysParamSetCreateFromContactEvent() throws Exception {
		try {
			PutPoleObjectsRequestDto putPoleObjectsRequest = new PutPoleObjectsRequestDto();
			
			setupIncidentFromContactEventForVictimVulnerableMandatoryTests(putPoleObjectsRequest, getCncRefMandatoryDate());

			Map<EntityKey, PoleEntityDto> originalObjectsMap = new HashMap<EntityKey, PoleEntityDto>();
			validator.validate(putPoleObjectsRequest, originalObjectsMap);
			fail("Expected a validation exception");
		} catch (ValidationException ve) {
			assertTrue("ValidationException: " + ValidationException.convertToXml(ve),
					ValidationException.convertToXml(ve).contains("error>isVictimVulnerable is required when the incident being created is "
							+ "after the date specified in the system parameter VICTIM_VULNERABLE_MANDATORY_ON_AND_AFTER_DATE, "
							+ "or if being created from a Contact Event, and the date the Contact Event was created is "
							+ "after the date specified in the system parameter</"));
		} finally {
			systemParamsCacheBean.setSystemParameter_TESTONLY("VICTIM_VULNERABLE_MANDATORY_ON_AND_AFTER_DATE", null);
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=303, 
			jiraRef = "CCI-38963",
			mingleTitle = "WP303 Is Victim Vulnerable - Mandatory in Investigation", 
			acceptanceCriteriaRefs = "TODO", 
			given = "Create Incident from Contact Event and System parameter set to a date in the future", 
			when = "Investigation is saved with linked victim and Victim Vulnerable Indicator is NULL", 
			then = "No validation error")
	public void testIsVictimVulnerableNotMandatoryWhenSysParamSetInFutureCreateFromContactEvent() throws Exception {
		try {
			PutPoleObjectsRequestDto putPoleObjectsRequest = new PutPoleObjectsRequestDto();
			
			Calendar now = Calendar.getInstance();
			now.add(Calendar.YEAR, 1);
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			
			setupIncidentFromContactEventForVictimVulnerableMandatoryTests(putPoleObjectsRequest, sdf.format(now.getTime()));
	
			Map<EntityKey, PoleEntityDto> originalObjectsMap = new HashMap<EntityKey, PoleEntityDto>();
			validator.validate(putPoleObjectsRequest, originalObjectsMap);

		} finally {
			systemParamsCacheBean.setSystemParameter_TESTONLY("VICTIM_VULNERABLE_MANDATORY_ON_AND_AFTER_DATE", null);
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=303, 
			jiraRef = "CCI-38963",
			mingleTitle = "WP303 Is Victim Vulnerable - Mandatory in Investigation", 
			acceptanceCriteriaRefs = "UNKNOWN", 
			given = "Create Incident from Contact Event and System parameter ON and Investigation is saved with linked victim", 
			when = "Victim Vulnerable indicator = NO", 
			then = "Reason vulnerable is not mandatory")
	public void testPersonVulnerabilityNotMandatoryWhenIsVictimVulnerableFalseCreateFromContactEvent() throws Exception {
		try {
			PutPoleObjectsRequestDto putPoleObjectsRequest = new PutPoleObjectsRequestDto();
			
			setupIncidentFromContactEventForVictimVulnerableMandatoryTests(putPoleObjectsRequest, getCncRefMandatoryDate());
			
			LinkDto incidentPersonLink = null;
			for (LinkDto link : incidentDto.getLinks()) {
				if (link.getLinkReason().equals(SubmitIncidentServiceConstants.VICTIM_LINK_REASON)) {
					incidentPersonLink = link;
					break;
				}
			}
			IncidentPersonInfoDto incidentPersonInfo = incidentPersonLink.getIncidentPersonInfoList().get(0);
			incidentPersonInfo.setIsVictimVulnerable(Boolean.FALSE);

			Map<EntityKey, PoleEntityDto> originalObjectsMap = new HashMap<EntityKey, PoleEntityDto>();
			validator.validate(putPoleObjectsRequest, originalObjectsMap);
		} finally {
			systemParamsCacheBean.setSystemParameter_TESTONLY("VICTIM_VULNERABLE_MANDATORY_ON_AND_AFTER_DATE", null);
		}
	}

	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=303, 
			jiraRef = "CCI-38963",
			mingleTitle = "WP303 Is Victim Vulnerable - Mandatory in Investigation", 
			acceptanceCriteriaRefs = "UNKNOWN", 
			given = "Create Incident from Contact Event and System parameter ON and Investigation is saved with linked victim", 
			when = "Victim Vulnerable indicator = YES", 
			then = "Reason vulnerable is mandatory")
	public void testPersonVulnerabilityMandatoryWhenIsVictimVulnerableTrueCreateFromContactEvent() throws Exception {
		try {
			PutPoleObjectsRequestDto putPoleObjectsRequest = new PutPoleObjectsRequestDto();
			
			
			setupIncidentFromContactEventForVictimVulnerableMandatoryTests(putPoleObjectsRequest, getCncRefMandatoryDate());
			
			LinkDto incidentPersonLink = null;
			for (LinkDto link : incidentDto.getLinks()) {
				if (link.getLinkReason().equals(SubmitIncidentServiceConstants.VICTIM_LINK_REASON)) {
					incidentPersonLink = link;
					break;
				}
			}
			IncidentPersonInfoDto incidentPersonInfo = incidentPersonLink.getIncidentPersonInfoList().get(0);
			incidentPersonInfo.setIsVictimVulnerable(Boolean.TRUE);

			Map<EntityKey, PoleEntityDto> originalObjectsMap = new HashMap<EntityKey, PoleEntityDto>();
			validator.validate(putPoleObjectsRequest, originalObjectsMap);
			fail("Expected a validation exception");
		} catch (ValidationException ve) {
			assertTrue("ValidationException: " + ValidationException.convertToXml(ve),
					ValidationException.convertToXml(ve).contains("error>Found 0 instance(s) of IncidentPersonVulnerability but at "
							+ "least 1 instance(s) required</"));
		} finally {
			systemParamsCacheBean.setSystemParameter_TESTONLY("VICTIM_VULNERABLE_MANDATORY_ON_AND_AFTER_DATE", null);
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=303, 
			jiraRef = "CCI-38963",
			mingleTitle = "WP303 Is Victim Vulnerable - Mandatory in Investigation", 
			acceptanceCriteriaRefs = "UNKNOWN", 
			given = "Create Incident from Contact Event and System parameter ON and Investigation is saved with linked victim", 
			when = "Victim Vulnerable indicator = YES and vulnerable reason is not populated", 
			then = "Reason vulnerable is mandatory")
	public void testPersonVulnerabilityVulnerableIndicatorMandatoryWhenIsVictimVulnerableTrueCreateFromContactEvent()
			throws Exception {
		try {
			PutPoleObjectsRequestDto putPoleObjectsRequest = new PutPoleObjectsRequestDto();
			
			setupIncidentFromContactEventForVictimVulnerableMandatoryTests(putPoleObjectsRequest, null);
			
			LinkDto incidentPersonLink = null;
			for (LinkDto link : incidentDto.getLinks()) {
				if (link.getLinkReason().equals(SubmitIncidentServiceConstants.VICTIM_LINK_REASON)) {
					incidentPersonLink = link;
					break;
				}
			}
			IncidentPersonInfoDto incidentPersonInfo = incidentPersonLink.getIncidentPersonInfoList().get(0);
			incidentPersonInfo.setIsVictimVulnerable(Boolean.TRUE);
			
			IncidentPersonVulnerabilityDto personVulnerability = new IncidentPersonVulnerabilityDto();
			personVulnerability.setObjectRef(new Integer(-1));
			personVulnerability.setModificationStatus(ModificationStatusDto.CREATE);
			incidentPersonInfo.addCrimePersonVulnerability(personVulnerability);

			Map<EntityKey, PoleEntityDto> originalObjectsMap = new HashMap<EntityKey, PoleEntityDto>();
			validator.validate(putPoleObjectsRequest, originalObjectsMap);
			fail("Expected a validation exception");
		} catch (ValidationException ve) {
			assertTrue("ValidationException: " + ValidationException.convertToXml(ve),
					ValidationException.convertToXml(ve).contains("error>vulnerableIndicator must have a value</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=303, 
			jiraRef = "CCI-38963",
			mingleTitle = "WP303 Is Victim Vulnerable - Mandatory in Investigation", 
			acceptanceCriteriaRefs = "UNKNOWN", 
			given = "Create Incident from Contact Event and System parameter ON and Investigation is saved with linked victim", 
			when = "Victim Vulnerable indicator = YES and Reason vulnerable is set", 
			then = "No validation error")
	public void testHappyPathWhenIsVictimVulnerableTrueCreateFromContactEvent() throws Exception {
		PutPoleObjectsRequestDto putPoleObjectsRequest = new PutPoleObjectsRequestDto();
		
		setupIncidentFromContactEventForVictimVulnerableMandatoryTests(putPoleObjectsRequest, null);
		
		LinkDto incidentPersonLink = null;
		for (LinkDto link : incidentDto.getLinks()) {
			if (link.getLinkReason().equals(SubmitIncidentServiceConstants.VICTIM_LINK_REASON)) {
				incidentPersonLink = link;
				break;
			}
		}
		IncidentPersonInfoDto incidentPersonInfo = incidentPersonLink.getIncidentPersonInfoList().get(0);
		incidentPersonInfo.setIsVictimVulnerable(Boolean.TRUE);
		
		IncidentPersonVulnerabilityDto personVulnerability = new IncidentPersonVulnerabilityDto();
		personVulnerability.setObjectRef(new Integer(-1));
		personVulnerability.setModificationStatus(ModificationStatusDto.CREATE);
		personVulnerability.setVulnerableIndicator("DISABILITY");
		incidentPersonInfo.addCrimePersonVulnerability(personVulnerability);

		Map<EntityKey, PoleEntityDto> originalObjectsMap = new HashMap<EntityKey, PoleEntityDto>();
		validator.validate(putPoleObjectsRequest, originalObjectsMap);
	}
	
	private void setupIncidentForCandCReferenceMandatoryTests(PutPoleObjectsRequestDto putPoleObjectsRequest, 
			String candcReferenceDate) {
		
		if (candcReferenceDate != null) {
			systemParamsCacheBean.setSystemParameter_TESTONLY("C_N_C_REFERENCE_MANDATORY_ON_AND_AFTER_DATE",
					candcReferenceDate);
		}
		
		incidentDto.getIncidentTxData().getCreateIncidentTxData().setCompleted(Boolean.FALSE);

		incidentDto.setLinks(null);
		
		GraphSetObjectRefGraphWalkerTestUtil graphSetObjectRefGraphWalker = new GraphSetObjectRefGraphWalkerTestUtil();
		graphSetObjectRefGraphWalker.walk(incidentDto);
		
		PoleDtoUtils.addBusinessServiceInfo(putPoleObjectsRequest,
				BusinessServicesTestUtils.createBusinessServiceInfo(securityContextId));
		putPoleObjectsRequest.addPoleObject(incidentDto);
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=303, 
			jiraRef = "CCI-38964",
			mingleTitle = "WP303 Lancashire - Make C&C Reference Mandatory in Investigation Create", 
			acceptanceCriteriaRefs = "UNKNOWN", 
			given = "System parameter set to a date in the future", 
			when = "candcReferenceMandatory not set", 
			then = "No validation error")
	public void testCandcReferenceMandatoryNotMandatoryWhenSysParamSetInFuture() throws Exception {
		try {
			PutPoleObjectsRequestDto putPoleObjectsRequest = new PutPoleObjectsRequestDto();
			
			Calendar now = Calendar.getInstance();
			now.add(Calendar.YEAR, 1);
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			
			setupIncidentForCandCReferenceMandatoryTests(putPoleObjectsRequest, sdf.format(now.getTime()));
	
			Map<EntityKey, PoleEntityDto> originalObjectsMap = new HashMap<EntityKey, PoleEntityDto>();
			validator.validate(putPoleObjectsRequest, originalObjectsMap);
		} finally {
			systemParamsCacheBean.setSystemParameter_TESTONLY("C_N_C_REFERENCE_MANDATORY_ON_AND_AFTER_DATE", null);
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=303, 
			jiraRef = "CCI-38964",
			mingleTitle = "WP303 Lancashire - Make C&C Reference Mandatory in Investigation Create", 
			acceptanceCriteriaRefs = "UNKNOWN", 
			given = "System parameter set", 
			when = "candcReferenceMandatory not set", 
			then = "Validation error that candcReferenceMandatory is required")
	public void testCandcReferenceMandatoryMandatoryWhenSysParamSet() throws Exception {
		try {
			PutPoleObjectsRequestDto putPoleObjectsRequest = new PutPoleObjectsRequestDto();
			
			setupIncidentForCandCReferenceMandatoryTests(putPoleObjectsRequest, getCncRefMandatoryDate());
			
			incidentDto.setCandcReferenceMandatory(null);
			
			Map<EntityKey, PoleEntityDto> originalObjectsMap = new HashMap<EntityKey, PoleEntityDto>();
			validator.validate(putPoleObjectsRequest, originalObjectsMap);
			fail("Expected a validation exception");
		} catch (ValidationException ve) {
			assertTrue("ValidationException: " + ValidationException.convertToXml(ve),
					ValidationException.convertToXml(ve).contains("error>candcReferenceMandatory must have a value</"));
		} finally {
			systemParamsCacheBean.setSystemParameter_TESTONLY("C_N_C_REFERENCE_MANDATORY_ON_AND_AFTER_DATE", null);
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=303, 
			jiraRef = "CCI-38964",
			mingleTitle = "WP303 Lancashire - Make C&C Reference Mandatory in Investigation Create", 
			acceptanceCriteriaRefs = "UNKNOWN", 
			given = "System parameter set", 
			when = "candcReferenceMandatory set to false", 
			then = "No validation error")
	public void testCandcReferenceNotMandatoryWhenCandcReferenceMandatoryFalse() throws Exception {
		try {
			PutPoleObjectsRequestDto putPoleObjectsRequest = new PutPoleObjectsRequestDto();
			
			setupIncidentForCandCReferenceMandatoryTests(putPoleObjectsRequest, getCncRefMandatoryDate());
			incidentDto.setCandcReferenceMandatory(Boolean.FALSE);

			Map<EntityKey, PoleEntityDto> originalObjectsMap = new HashMap<EntityKey, PoleEntityDto>();
			validator.validate(putPoleObjectsRequest, originalObjectsMap);
		} finally {
			systemParamsCacheBean.setSystemParameter_TESTONLY("C_N_C_REFERENCE_MANDATORY_ON_AND_AFTER_DATE", null);
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=303, 
			jiraRef = "CCI-38964",
			mingleTitle = "WP303 Lancashire - Make C&C Reference Mandatory in Investigation Create", 
			acceptanceCriteriaRefs = "UNKNOWN", 
			given = "System parameter set", 
			when = "candcReferenceMandatory set to true", 
			then = "Validation error that C&C Reference required")
	public void testCandcReferenceMandatoryWhenCandcReferenceMandatoryTrue() throws Exception {
		try {
			PutPoleObjectsRequestDto putPoleObjectsRequest = new PutPoleObjectsRequestDto();
			
			setupIncidentForCandCReferenceMandatoryTests(putPoleObjectsRequest, getCncRefMandatoryDate());
			incidentDto.setCandcReferenceMandatory(Boolean.TRUE);

			Map<EntityKey, PoleEntityDto> originalObjectsMap = new HashMap<EntityKey, PoleEntityDto>();
			validator.validate(putPoleObjectsRequest, originalObjectsMap);
			fail("Expected a validation exception");
		} catch (ValidationException ve) {
			assertTrue("ValidationException: " + ValidationException.convertToXml(ve),
					ValidationException.convertToXml(ve).contains("error>One, and only one, C&amp;C Reference is required when "
							+ "candcReferenceMandatory is set to true</"));
		} finally {
			systemParamsCacheBean.setSystemParameter_TESTONLY("C_N_C_REFERENCE_MANDATORY_ON_AND_AFTER_DATE", null);
		}
	}


	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=303, 
			jiraRef = "CCI-38964",
			mingleTitle = "WP303 Lancashire - Make C&C Reference Mandatory in Investigation Create", 
			acceptanceCriteriaRefs = "UNKNOWN", 
			given = "System parameter set", 
			when = "candcReferenceMandatory not set", 
			then = "Validation error that C&C Reference required")
	public void testCandcReferenceMandatoryWhenSysParamSetAndCancReferenceMandatoryNotSet() throws Exception {
		try {
			systemParamsCacheBean.setSystemParameter_TESTONLY("C_N_C_REFERENCE_MANDATORY_ON_AND_AFTER_DATE", getCncRefMandatoryDate());
			incidentDto.setCandcReferenceMandatory(null);
			doCreateEventObject(true, incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("One, and only one, C&amp;C Reference is required when candcReferenceMandatory is set to true"));
		} finally {
			systemParamsCacheBean.setSystemParameter_TESTONLY("C_N_C_REFERENCE_MANDATORY_ON_AND_AFTER_DATE", null);
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=303, 
			jiraRef = "CCI-38964",
			mingleTitle = "WP303 Lancashire - Make C&C Reference Mandatory in Investigation Create", 
			acceptanceCriteriaRefs = "UNKNOWN", 
			given = "System parameter set", 
			when = "candcReferenceMandatory and C&C Reference set", 
			then = "No validation error")
	public void testHappyPathWhenCandcReferenceMandatoryTrue() throws Exception {
		PutPoleObjectsRequestDto putPoleObjectsRequest = new PutPoleObjectsRequestDto();
		
		setupIncidentForCandCReferenceMandatoryTests(putPoleObjectsRequest, null);
		incidentDto.setCandcReferenceMandatory(Boolean.TRUE);
		
		ExternalReferenceDto externalRef = new ExternalReferenceDto();
		externalRef.setObjectRef(new Integer(-1));
		externalRef.setModificationStatus(ModificationStatusDto.CREATE);
		externalRef.setReferenceType("STORM");
		externalRef.setReferenceValue("1234");
		incidentDto.addExternalReference(externalRef);

		Map<EntityKey, PoleEntityDto> originalObjectsMap = new HashMap<EntityKey, PoleEntityDto>();
		validator.validate(putPoleObjectsRequest, originalObjectsMap);
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=275,
		jiraRef = "CON-41759", 
		mingleTitle = "WP275 - C&C Interface - Create Investigation - BS Validations", 
		acceptanceCriteriaRefs = "CR21", 
		given = "Child object incidentInterest is supplied and Either "
				+ "incidentInterest.interestedUnitId OR incidentInterest.interestedEmployeeId "
				+ "is supplied and incidentInterest.interestStartDate is set to current date",
		when = "Incident is submitted",
		then = "Register Interest In Investigation task is performed successfully and data is saved in POLE")
	public void testHappyPathForInterestedParty() throws Exception {		
		CreateIncidentTxDataDto txd = incidentDto.getIncidentTxData().getCreateIncidentTxData();
		txd.setCompleted(true);
		incidentDto.setCandcReferenceMandatory(false);
		
		List<IncidentInterestDto> listOfIncidentInterestDto = new ArrayList<IncidentInterestDto>();
		IncidentInterestDto incidentInterestDto = new IncidentInterestDto();
		incidentInterestDto.setModificationStatus(ModificationStatusDto.CREATE);
		incidentInterestDto.setInterestedUnitId("AMO");
		incidentInterestDto.setInterestReason("Test task");
		incidentInterestDto.setInterestStartDate(new PoleDate());
		
		listOfIncidentInterestDto.add(incidentInterestDto);
		
		incidentDto.setInterestedParties(listOfIncidentInterestDto);
		doCreateEventObject(null, incidentDto);
		// assertion for REQUIRES_QA which is set only for completed = true 
		assertEquals("Expect a Incident Status  " + REQUIRES_QA + " when completed flag set as false ",
				REQUIRES_QA, incidentDto.getStatus());
		
	}

	@Override
	protected void teardownImpl() throws Exception {
		deleteData();
	}

	private void deleteData() throws Exception {
		try {
			if (incidentDto != null) {
				utils.removeIncidentFromPole(incidentDto.getObjectRef(), 2, poleDirect, securityContextId);
			}
		} catch (Exception e) {
			logger.debug("Failed to delete Incident having objectRef {} ", incidentDto.getObjectRef(), e);
		}
	}	
	
	private void assertOnValidationException(String expectedFiled, String expectedErrorType, String expectedError,
			BusinessServiceValidationErrors businessServiceValidationErrors) {
		
		BusinessServiceValidationError businessServiceValidationError =  businessServiceValidationErrors.getBusinessServiceValidationError().get(0);
		
		assertEquals("Validation on field ", expectedFiled, businessServiceValidationError.getField());
		assertEquals("Validation on errorType ", expectedErrorType, businessServiceValidationError.getErrorType());
		assertEquals("Validation on error ", expectedError, businessServiceValidationError.getError());
	}

	@Override
	protected PoleObjectDto createPoleObjectDto() {		
		return incidentDto;
	}
	
	@Override
	protected void doCreateEventObject(Boolean completedFlag, PoleObjectDto poleObjectDto) throws Exception {
		
		PutPoleObjectsRequestDto putPoleObjectsRequest = utils.createPutPoleObjectsRequestDto(
				BusinessServicesTestUtils.createBusinessServiceInfo(securityContextId), (IncidentDto) poleObjectDto);
		
		putPoleObjectsRequest.getHeader().setClientName(clientName);

		CreateIncidentTxDataDto txd = incidentDto.getIncidentTxData().getCreateIncidentTxData();
		txd.setCompleted(completedFlag);
		if (Boolean.FALSE.equals(completedFlag)) {
			txd.setQaUnit(null);
			txd.setQaUnitDisplayValue(null);
			txd.setLinkingUnit(null);
			txd.setLinkingUnitDisplayValue(null);
		} else {
			//we're treating it with a default of true. which means we don't need to nullify the fields above to be valid.
		}

		if (objectActionPlan != null) {
			incidentDto.addObjectActionPlan(objectActionPlan);
		}

		if (policyFile != null) {
			incidentDto.addPolicyFile(policyFile);
		}
		
		if (incidentWithValidObjectActionPlan && objectActionPlan == null) {
			//create a default one with common stuff in it..
			incidentDto.addObjectActionPlan(CommonPoleObjectTestUtils.getObjectActionPlan(officerReportingId));
		}
		
		PutPoleObjectsResponseDto poleObjectsResponse = poleBusinessServices.putPoleObjects(putPoleObjectsRequest);
		Integer incidentObjectRef = IncidentPoleDtoUtils.extractIncidentObjectRef(poleObjectsResponse);				

		incidentDto = utils.getIncidentFromPole(incidentObjectRef, poleBusinessServices, securityContextId);
		assertEquals(incidentDto.getObjectRef(), incidentObjectRef);
		
		
		List<IncidentClassificationDto> classificationDtos = incidentDto.getIncidentClassifications();
			
		for (IncidentClassificationDto classificationDto : classificationDtos) {

			if (classificationDto.getClassificationType().equals("PRIMARY")) {
			
				assertEquals("CRIME", incidentDto.getInvestigationType());
				break;
			}

		}

		if (isRunWorkflowTests()) {
			
			if (Boolean.FALSE.equals(completedFlag)) {
				
				TaskSummaryData taskSummaryData = poleFlowUtils.getTaskRelatedInfomration(poleFlowSoapService,
						poleFlowSecurityToken, PoleNames.INCIDENT, incidentObjectRef, securityContextId, 2,
						COMPLETE_INVESTIGATION_CREATION, defaultBetweenAttemptsToCallWorkflow);
				
				assertEquals("Two tasks should exist on Incident created from CE.",						
						2, taskSummaryData.getCount());
				
				assertEquals("A single '" + COMPLETE_INVESTIGATION_CREATION
								+ "' task should exist on Incident created from CE.",
								COMPLETE_INVESTIGATION_CREATION, taskSummaryData.getName());
			} else {
				
				int taskCount = 0;
				if (incidentWithValidObjectActionPlan) {
					taskCount = 2;
				} else {
					taskCount = 1;
				}
				TaskSummaryData taskSummaryData = poleFlowUtils.getTaskRelatedInfomration(poleFlowSoapService,
						poleFlowSecurityToken, PoleNames.INCIDENT, incidentObjectRef, securityContextId, taskCount,
						QA_NEW_INVESTIGATION_EXTERNAL_TASK_NAME, defaultBetweenAttemptsToCallWorkflow);

				assertEquals(taskCount + " task(s) should exist on Incident",
						taskCount, taskSummaryData.getCount());
				
				assertEquals(
						"A single '" + QA_NEW_INVESTIGATION_EXTERNAL_TASK_NAME
								+ "' task should exist on Incident",
						QA_NEW_INVESTIGATION_EXTERNAL_TASK_NAME,
						taskSummaryData.getName());							
			}		
			
			if (incidentWithValidObjectActionPlan) {
				TaskSummaryData taskSummaryData = poleFlowUtils.getTaskRelatedInfomration(poleFlowSoapService,
						poleFlowSecurityToken, PoleNames.INCIDENT, incidentObjectRef, securityContextId, null,
						PERFORM_TASK_TASK_NAME, defaultBetweenAttemptsToCallWorkflow);
								
				assertEquals("A single '" + PERFORM_TASK_TASK_NAME
						+ "' task should exist on Incident",
						PERFORM_TASK_TASK_NAME, taskSummaryData.getName());
			}
		}
	}
	
	/**
	 * This method is used for adding the C_N_C_REFERENCE_MANDATORY_ON_AND_AFTER_DATE on the system parameter in the method 
	 * setSystemParameter_TESTONLY the date needs to be set per test, because the date affects if the system parameter is ignored or not
	 * This is for the support for the ruleset IncidentCandCReference 
	 * {systemParameter}[C_N_C_REFERENCE_MANDATORY_ON_AND_AFTER_DATE] notEquals null and
										{currentDate} greaterThanOrEquals {systemParameter}[C_N_C_REFERENCE_MANDATORY_ON_AND_AFTER_DATE]
	 */
	private String getCncRefMandatoryDate() {
		PoleDate date = new PoleDate();
		date = date.addDays(-10);
		return  dateFormatter.format(date);
	}
}
