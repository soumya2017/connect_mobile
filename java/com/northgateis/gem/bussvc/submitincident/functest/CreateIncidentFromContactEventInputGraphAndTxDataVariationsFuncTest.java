package com.northgateis.gem.bussvc.submitincident.functest;

import static com.northgateis.gem.bussvc.pole.utils.PoleDtoUtils.extractBusinessServiceResultInfo;
import static com.northgateis.gem.bussvc.submitcontact.utils.ContactEventPoleDtoUtils.extractContactEventObjectRef;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.junit.Test;

import com.northgateis.gem.bussvc.framework.test.BusSvcStoryAcceptanceCriteriaReference;
import com.northgateis.gem.bussvc.submitcontact.constants.SubmitContactServiceConstants;
import com.northgateis.gem.bussvc.test.util.BusinessServicesTestUtils;
import com.northgateis.pole.common.PoleDate;
import com.northgateis.pole.schema.ContactEventDto;
import com.northgateis.pole.schema.IncidentDto;
import com.northgateis.pole.schema.LinkDto;
import com.northgateis.pole.schema.PolicyFileDto;
import com.northgateis.pole.schema.PutPoleObjectsRequestDto;
import com.northgateis.pole.schema.PutPoleObjectsResponseDto;

/**
 * Test class for testing Business Services to be driven from Hudson
 * 
 * TODO CODE REVIEW ^^^ Er yeah, obvious. ^^^  But this is *not* for Workflow! Misleading title. Changing
 * title from Story33681IncidentFromContactEventWorkflowValidationFuncTest to current value.
 */
public class CreateIncidentFromContactEventInputGraphAndTxDataVariationsFuncTest extends AbstractSubmitIncidentFuncTestBase {
	
	/**
	 * Overriding to provide more roles - needed for Incident data mods.
	 * 
	 * @throws Exception
	 */
	@Override
	protected void createMainSecurityContextId() throws Exception {
		if (securityContextId == null) {
			securityContextId = busSvcUtils.getSecurityContextId("Frank Shunneltestone", 
				Arrays.asList("ATHENA_USER", "CrimeInvestigator", "CrimeSupervisor", 
					"NorthgateSystemAdmin", "SysAdmin1", "SysAdmin2"), securityService);
		}
	}
	
	/**
	 * Test to check it updates the ContactEvent correctly when the ContactEvent object is contained in the
	 * Link that defines the link.
	 */
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			mingleRef = 33681, 
			mingleTitle = "Bus Svcs - Decision Support - call POLEFlow to complete WF and Close the CE when creating Investigation from CE (JSON REST /incident/create) ", 
			acceptanceCriteriaRefs = "3.2, 3.3", 
			given = "The call is validated by POLEFlow", 
			when = "The new Investigation is saved in POLE", 
			then = "The Contact Event will have new status value of CLOSED, " +
					"There will be am additional log entry added to Contact Event")
	public void testContactEventUpdatedWhenLinkedInLink() throws Exception {
		createIncidentFromContactEventRequest(true, true, Boolean.FALSE);
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=303, 
			jiraRef = "CCI-38964",
			mingleTitle = "WP303 Lancashire - Make C&C Reference Mandatory in Investigation Create", 
			acceptanceCriteriaRefs = "UNKNOWN", 
			given = "Create incident from contact event and system parameter set", 
			when = "candcReferenceMandatory not set", 
			then = "No Validation error that candcReferenceMandatory is required")
	public void testCandCRefNotMandatoryWhenCreateIncidentFromContactEventAndSysParamSetForCandCRefMandatory()
			throws Exception {
		try {
			SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
			PoleDate date = new PoleDate();
			date = date.addDays(-10);
			
			systemParamsCacheBean.setSystemParameter_TESTONLY("C_N_C_REFERENCE_MANDATORY_ON_AND_AFTER_DATE",
					dateFormatter.format(date));
			
			createIncidentFromContactEventRequest(true, true, null);
			
		} finally {
			systemParamsCacheBean.setSystemParameter_TESTONLY("C_N_C_REFERENCE_MANDATORY_ON_AND_AFTER_DATE", null);
		}
	}
	
	/**
	 * Test to check it updates the ContactEvent correctly when the ContactEvent object not contained
	 * on the Pole graph, and a Link references the ContactEvent by type and objectRef. 
	 */
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			mingleRef = 33681, 
			mingleTitle = "Bus Svcs - Decision Support - call POLEFlow to complete WF and Close the CE when creating Investigation from CE (JSON REST /incident/create) ", 
			acceptanceCriteriaRefs = "3.2, 3.3", 
			given = "The call is validated by POLEFlow", 
			when = "The new Investigation is saved in POLE", 
			then = "The Contact Event will have new status value of PROCESSED, " +
					"There will be am additional log entry added to Contact Event")
	public void testContactEventUpdatedWhenLinkedInLinkWithNoObject() throws Exception {
		createIncidentFromContactEventRequest(true, false, Boolean.FALSE);
	}
	
	/**
	 * Test to check it updates the ContactEvent correctly when the ContactEvent is one of the PoleObjects
	 * being submitted, and a Link references the ContactEvent by type and objectRef.
	 */
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			mingleRef = 33681, 
			mingleTitle = "Bus Svcs - Decision Support - call POLEFlow to complete WF and Close the CE when creating Investigation from CE (JSON REST /incident/create) ", 
			acceptanceCriteriaRefs = "3.2, 3.3", 
			given = "The call is validated by POLEFlow", 
			when = "The new Investigation is saved in POLE", 
			then = "The Contact Event will have new status value of PROCESSED, " +
					"There will be am additional log entry added to Contact Event")
	public void testContactEventUpdatedWhenLinkedAsExtraObjectFromLink() throws Exception {
		createIncidentFromContactEventRequest(false, true, Boolean.FALSE);
	}
	
	/**
	 * Test to check it updates the ContactEvent correctly when the ContactEvent object is contained in the
	 * LinkNode that defines the link.
	 */
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			mingleRef = 33681, 
			mingleTitle = "Bus Svcs - Decision Support - call POLEFlow to complete WF and Close the CE when creating Investigation from CE (JSON REST /incident/create) ", 
			acceptanceCriteriaRefs = "3.2, 3.3", 
			given = "The call is validated by POLEFlow", 
			when = "The new Investigation is saved in POLE", 
			then = "The Contact Event will have new status value of PROCESSED, " +
					"There will be am additional log entry added to Contact Event")
	public void testContactEventUpdatedWhenLinkedInLinkNode() throws Exception {
		createIncidentFromContactEventRequestUsingLinkNode(true, true);
	}
	
	/**
	 * Test to check it updates the ContactEvent correctly when the ContactEvent object not contained
	 * on the Pole graph, and a LinkNode references the ContactEvent by type and objectRef. 
	 */
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			mingleRef = 33681, 
			mingleTitle = "Bus Svcs - Decision Support - call POLEFlow to complete WF and Close the CE when creating Investigation from CE (JSON REST /incident/create) ", 
			acceptanceCriteriaRefs = "3.2, 3.3", 
			given = "The call is validated by POLEFlow", 
			when = "The new Investigation is saved in POLE", 
			then = "The Contact Event will have new status value of PROCESSED, " +
					"There will be am additional log entry added to Contact Event")
	public void testContactEventUpdatedWhenLinkedInLinkNodeWithNoObject() throws Exception {
		createIncidentFromContactEventRequestUsingLinkNode(true, false);
	}
	
	/**
	 * Test to check it updates the ContactEvent correctly when the ContactEvent is one of the PoleObjects
	 * being submitted, and a LinkNode references the ContactEvent by type and objectRef.
	 */
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			mingleRef = 33681, 
			mingleTitle = "Bus Svcs - Decision Support - call POLEFlow to complete WF and Close the CE when creating Investigation from CE (JSON REST /incident/create) ", 
			acceptanceCriteriaRefs = "3.2, 3.3", 
			given = "The call is validated by POLEFlow", 
			when = "The new Investigation is saved in POLE", 
			then = "The Contact Event will have new status value of PROCESSED, " +
					"There will be am additional log entry added to Contact Event")
	public void testContactEventUpdatedWhenLinkedAsExtraObjectFromLinkNode() throws Exception {
		createIncidentFromContactEventRequestUsingLinkNode(false, true);
	}
	
	/**
	 * Test to check it updates the ContactEvent correctly when the Pole Graph is flat and only contains
	 * an Incident object, and a collection of LinkNodes
	 */
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			mingleRef = 33681, 
			mingleTitle = "Bus Svcs - Decision Support - call POLEFlow to complete WF and Close the CE when creating Investigation from CE (JSON REST /incident/create) ", 
			acceptanceCriteriaRefs = "3.2, 3.3", 
			given = "The call is validated by POLEFlow", 
			when = "The new Investigation is saved in POLE", 
			then = "The Contact Event will have new status value of PROCESSED, " +
					"There will be am additional log entry added to Contact Event")
	public void testContactEventUpdatedWhenPoleGraphFlat() throws Exception {
		String peRef = PE_REF_PREFIX + ":33681" + new Date();
		
		PutPoleObjectsRequestDto req = contils.createPutPoleObjectsRequest(
				BusinessServicesTestUtils.createBusinessServiceInfo(securityContextId),
				contils.createContactEventDto(peRef, peAccountRef));
		
		PutPoleObjectsResponseDto resp = poleBusinessServices.putPoleObjects(req);
		
		ContactEventDto contactEvent = contils.retrieveContactFromPole(extractContactEventObjectRef(resp), 5,
				poleBusinessServices, securityContextId, false);
		contactEvent.setLinks(null);
		
		IncidentDto incident = utils.prepareIncidentWithNoLinks(new Integer(-1), Integer.valueOf(officerReportingId));
		
		PutPoleObjectsRequestDto createIncReq = utils.preparePpoRequestForCreateIncidentFromContact(
				BusinessServicesTestUtils.createBusinessServiceInfo(securityContextId), incident, contactEvent, false);
		utils.addWorkflowTxDataForCreateIncident(createIncReq, taskStaffId, taskUnitName);
		
		IncidentDto inc = (IncidentDto) createIncReq.getPoleObjects().get(0);
		inc.getIncidentTxData().getCreateIncidentTxData().setSubmittingOfficerId(officerReportingId);
		inc.getIncidentTxData().getCreateIncidentTxData().setSubmittingOfficerDisplayValue("Frank Test");
		
		resp = poleBusinessServices.putPoleObjects(createIncReq);
		assertTrue(extractBusinessServiceResultInfo(resp).isCompleted());
		assertNotNull(extractBusinessServiceResultInfo(resp).getTransactionId());
		assertNotNull(getIncidentObjectRef(resp));
		
		IncidentDto incidentToCheck = utils.removeIncidentFromPole(getIncidentObjectRef(resp), 5, poleDirect, securityContextId);

		checkIncidentAndContact(incidentToCheck, peRef);
	}
	
	private void createIncidentFromContactEventRequestUsingLinkNode(boolean flatGraph, boolean includeContact) throws Exception {
		String peRef = PE_REF_PREFIX + ":33681" + new Date();
		
		PutPoleObjectsRequestDto req = contils.createPutPoleObjectsRequest(
				BusinessServicesTestUtils.createBusinessServiceInfo(securityContextId),
				contils.createContactEventDto(peRef, peAccountRef));
		
		PutPoleObjectsResponseDto resp = poleBusinessServices.putPoleObjects(req);
		
		ContactEventDto contactEvent = contils.retrieveContactFromPole(extractContactEventObjectRef(resp), 5,
				poleBusinessServices, securityContextId, false);
		contactEvent.setLinks(null);
		
		IncidentDto incident = utils.createIncident(new Integer(-1), Integer.valueOf(officerReportingId));
		
		PutPoleObjectsRequestDto createIncReq = utils.createPutPoleObjectsRequestDto(
				BusinessServicesTestUtils.createBusinessServiceInfo(securityContextId), incident);
		utils.addWorkflowTxDataForCreateIncident(createIncReq, taskStaffId, taskUnitName);
		if (flatGraph && includeContact) {
			createIncReq.addLinkNode(utils.createLinkNodeToContact(createIncReq.getPoleObjects().get(0).getObjectRef(),
					contactEvent));
		} else {
			if (includeContact) {
				createIncReq.addPoleObject(contactEvent);
			}
			createIncReq.addLinkNode(utils.createLinkNodeToContact(createIncReq.getPoleObjects().get(0).getObjectRef(),
					contactEvent.getObjectRef()));
		}
		
		IncidentDto inc = (IncidentDto) createIncReq.getPoleObjects().get(0);
		inc.getIncidentTxData().getCreateIncidentTxData().setSubmittingOfficerId(officerReportingId);
		inc.getIncidentTxData().getCreateIncidentTxData().setSubmittingOfficerDisplayValue("Frank Test");
		
		resp = poleBusinessServices.putPoleObjects(createIncReq);
		assertTrue(extractBusinessServiceResultInfo(resp).isCompleted());
		assertNotNull(extractBusinessServiceResultInfo(resp).getTransactionId());
		assertNotNull(getIncidentObjectRef(resp));
		
		IncidentDto incidentToCheck = utils.removeIncidentFromPole(getIncidentObjectRef(resp), 5, poleDirect, securityContextId);

		checkIncidentAndContact(incidentToCheck, peRef);
	}
	
	private void createIncidentFromContactEventRequest(boolean flatGraph, boolean includeContact, 
			Boolean candcRefMandatory) throws Exception {
		String peRef = PE_REF_PREFIX + ":33681" + new Date();
		
		PutPoleObjectsRequestDto req = contils.createPutPoleObjectsRequest(
				BusinessServicesTestUtils.createBusinessServiceInfo(securityContextId),
				contils.createContactEventDto(peRef, peAccountRef));
		
		PutPoleObjectsResponseDto resp = poleBusinessServices.putPoleObjects(req);
		
		ContactEventDto contactEvent = contils.retrieveContactFromPole(extractContactEventObjectRef(resp), 5,
				poleBusinessServices, securityContextId, false);
		contactEvent.setLinks(null);
		
		IncidentDto incident = utils.createIncident(new Integer(-1), Integer.valueOf(officerReportingId));
		incident.setCandcReferenceMandatory(candcRefMandatory);
		
		PutPoleObjectsRequestDto createIncReq = utils.createPutPoleObjectsRequestDto(
				BusinessServicesTestUtils.createBusinessServiceInfo(securityContextId), incident);
		utils.addWorkflowTxDataForCreateIncident(createIncReq, taskStaffId, taskUnitName);
		if (flatGraph && includeContact) {
			createIncReq.getPoleObjects().get(0).addLink(utils.createLinkToContact(contactEvent,
					createIncReq.getPoleObjects().get(0).getObjectRef()));
		} else {
			if (includeContact) {
				createIncReq.addPoleObject(contactEvent);
			}
			createIncReq.getPoleObjects().get(0).addLink(utils.createLinkToContact(contactEvent.getObjectRef(),
					createIncReq.getPoleObjects().get(0).getObjectRef()));
		}
		
		IncidentDto inc = (IncidentDto) createIncReq.getPoleObjects().get(0);
		inc.getIncidentTxData().getCreateIncidentTxData().setSubmittingOfficerId(officerReportingId);
		inc.getIncidentTxData().getCreateIncidentTxData().setSubmittingOfficerDisplayValue("Frank Test");
		
		resp = poleBusinessServices.putPoleObjects(createIncReq);
		assertTrue(extractBusinessServiceResultInfo(resp).isCompleted());
		assertNotNull(extractBusinessServiceResultInfo(resp).getTransactionId());
		assertNotNull(getIncidentObjectRef(resp));
		
		IncidentDto incidentToCheck = utils.removeIncidentFromPole(getIncidentObjectRef(resp), 5, poleDirect, securityContextId);

		checkIncidentAndContact(incidentToCheck, peRef);
	}
	
	private void checkIncidentAndContact(IncidentDto incident, String peRef) throws Exception {
		
		assertEquals(
				"Expect specified Task staff ID to become OfficerRecordingId on the Incident.",
				new Integer(taskStaffId),
				incident.getOfficerRecordingId());
		assertEquals(
				"Expect specified Task unit name on the Incident as Owning Unit", 
				taskUnitName, 
				incident.getOwningUnit());
		
		ContactEventDto contactToCheck = null;
		for (LinkDto link : incident.getLinks()) {
			if (link.getToPoleObject() instanceof ContactEventDto) {
				contactToCheck = (ContactEventDto) link.getToPoleObject();
				break;
			}
		}
		
		assertNotNull(
				"Expect a link from the Incident back to the ContactEvent it was created from",
				contactToCheck);
		assertEquals(
				"Expect the ContactEvent to go to status of " + SubmitContactServiceConstants.PROCESSED_STATUS,
				SubmitContactServiceConstants.PROCESSED_STATUS,
				contactToCheck.getStatus());
		assertEquals(
				"Expect a single policy file entry on the CE after creating an Incident from it",
				1,
				contactToCheck.getPolicyFiles().size());
		
//		assertEquals(
//				"TODO CODE REVIEW #33681 Test out of date: external ref on CE now not used.",
//				1,
//				contactToCheck.getExternalReferences().size());
//		ExternalReferenceDto externalReference = contactToCheck.getExternalReferences().get(0);
//		assertEquals(
//				"TODO CODE REVIEW #33681 Test out of date: external ref on CE now not used.",
//				PE_EXT_REF_TYPE,
//				externalReference.getReferenceType());
//		assertEquals(
//				"TODO CODE REVIEW #33681 Test out of date: external ref on CE now not used.",
//				peRef, 
//				externalReference.getReferenceValue());
		
		PolicyFileDto contactLog = contactToCheck.getPolicyFiles().get(0);
		assertEquals(
				"The contact log entry added to the Incident should contain the Incident number",
				contactLogEntry.replace("<incidentref>", incident.getIncidentNumber()), 
				contactLog.getEntryText());
		assertEquals(
				"The contact log entry should be of specific type",
				contactLogEntryType, contactLog.getEntryType());
	}
}
