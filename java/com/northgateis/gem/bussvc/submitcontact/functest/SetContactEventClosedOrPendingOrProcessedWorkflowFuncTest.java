package com.northgateis.gem.bussvc.submitcontact.functest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.junit.Assume;
import org.junit.Test;

import com.northgateis.gem.bussvc.framework.test.BusSvcStoryAcceptanceCriteriaReference;
import com.northgateis.gem.bussvc.framework.utils.xml.JaxbXmlMarshaller;
import com.northgateis.gem.bussvc.submitcontact.appliedlogic.UpdateContactEventWorkflowContextBean;
import com.northgateis.gem.bussvc.submitcontact.constants.SubmitContactServiceConstants;
import com.northgateis.gem.bussvc.test.util.ContactEventTestUtils;
import com.northgateis.gem.bussvc.test.util.TaskSummaryData;
import com.northgateis.pole.common.PoleNames;
import com.northgateis.pole.schema.ContactEventDto;
import com.northgateis.pole.schema.ContactEventTxDataDto;
import com.northgateis.pole.schema.ModificationStatusDto;
import com.northgateis.pole.schema.PutPoleObjectsRequestDto;
import com.northgateis.pole.schema.PutPoleObjectsResponseDto;
import com.northgateis.pole.schema.SetContactEventToClosedTxDataDto;

/**
 * Functional tests to test Workflow/Task changes while user Mark CE as Closed Or
 * user Mark CE as pending Or user want to Refer CE to a Partner
 * 
 * The functional test to test Workflow/Task state and Status on CE after:
 * 
 * 0. Initial creation of CE (sanity check)
 * 1. User Mark CE to Closed 
 * 2. User Mark CE to Pending 
 * 3. User Refer CE to Partner 
 * 
 * @author ganesh.bhat, sanket.khandekar
 */
public class SetContactEventClosedOrPendingOrProcessedWorkflowFuncTest extends AbstractSubmitContactFuncTestBase {
	
	private static final String TEST_OFFICER_DISPLAY_VALUE = "Supt 46 SR7169 UserA";
	private static final String TEST_REASON = "I am a test reason written by user";

	private static final String PROCESS_NEW_CONTACT = "Process New Contact";
	private static final String PROCESS_PENDING_CONTACT = "Process Pending Contact";

	private static final String REFER_TO_PARTNER_DEFAULT_DECISION_LOG_TEXT = "This is a test message to test the email.";

	private final String extRefType = "BusSvcTest";
	private String extRef;
	private ContactEventDto contactEvent;
	private Integer contactEventObjRef;

	@Override
	protected void setupImpl() throws Exception {
        Assume.assumeTrue(isRunWorkflowTests());

		contactEvent = null;
		extRef = extRefType + ":" + new Date();

		// Create a new ContactEvent with a new CreateContactEventTxData
		PutPoleObjectsRequestDto request = createContactEventPutPoleObjectRequest();
		logger.debug("Created ContactEvent PutPoleObjectsRequestDto: {}",
				JaxbXmlMarshaller.convertToPrettyPrintXml(request, PutPoleObjectsRequestDto.class));

		PutPoleObjectsResponseDto resp = poleBusinessServices.putPoleObjects(request);
		logger.debug("Created ContactEvent PutPoleObjectsResponseDto: {}",
				JaxbXmlMarshaller.convertToPrettyPrintXml(resp, PutPoleObjectsResponseDto.class));

		contactEventObjRef = resp.getObjectRefs().get(0);
		logger.debug("Created ContactEvent with obj ref: {}", contactEventObjRef);

		// Get Contact Event back from pole
		contactEvent = contils.retrieveContactFromPole(contactEventObjRef, 2, poleDirect, securityContextId, false);
	}

	@Override
	protected void teardownImpl() throws Exception {
		try {
			deleteData();
		} catch (Exception e) {
			logger.debug("Failed to delete record", e);
		}
	}

	private void deleteData() throws Exception {
		if (contactEvent != null) {
			contils.retrieveContactFromPole(contactEvent.getObjectRef(), 2, poleDirect, securityContextId, true);
		}
	}

	/**
	 * Test CE status and its workflow status after initial creation.
	 */
	@Test
	public void testSetContactEventUnprocessed() throws Exception {
		doAssertionsAfterContactEventInitiallyCreated();
	}
	
	private void doAssertionsAfterContactEventInitiallyCreated() throws Exception {
		// Object Ref of Contact Event from Response should be same as Contact Event retrieved from pole
		assertEquals(contactEventObjRef, contactEvent.getObjectRef());

		// Newly Created Contact Event in Pole will be of Status = "SubmitContactServiceConstants.UNPROCESSED_STATUS"
		assertEquals(SubmitContactServiceConstants.UNPROCESSED_STATUS, contactEvent.getStatus());

		// BusSvc creates a Contact Event in Pole. Since the PutPoleRequestObject graph contains transient data
		// VIZ ContactEventTxData and CreateContactEventTxData, BusSvc will populate additional work flow header
		// in the graph using which Pole will communicate with Pole flow Via APM to generate the Task.
		// Task = "Process New Contact"

		TaskSummaryData taskSummaryDataOfContactEvent = poleFlowUtils.getTaskRelatedInfomration(poleFlowSoapService,
				poleFlowSecurityToken, PoleNames.CONTACT_EVENT, contactEvent.getObjectRef(), securityContextId, 1,
				PROCESS_NEW_CONTACT, defaultBetweenAttemptsToCallWorkflow);

		assertEquals("Expect a single '" + PROCESS_NEW_CONTACT + "' task against the ContactEvent."
						,1,taskSummaryDataOfContactEvent.getCount());

		assertEquals("Expect a single '" + PROCESS_NEW_CONTACT + "' task against the ContactEvent.",PROCESS_NEW_CONTACT, 
				taskSummaryDataOfContactEvent.getName());
	}

	/**
	 * Test CE status and its work flow changes during Refer CE to Partner
	 */
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			mingleRef=35314,
			mingleTitle="Decision Support - refer to Partner by Email (with Doc Prod generated content) [#25230 Part2]",
			acceptanceCriteriaRefs="CR1,CR2,CR3,CR4,CR5",
			given="The information has been submitted by the Decision Support application",
			when="Business Services receives the request",
			then="A decision log entry is created against Contact Event,"
					+ "A decision log entry is created against Contact Event,"
					+ "A new Contact Event Action will be added to the ContactEvent (documenting the refer-to-partner "
					+ "activity; later visible in POLE viewer),"
					+ "The Contact Event will be marked as Processed,"
					+ "Any outstanding 'Process New Contact Event' or 'Process Pending Contact Event' "
					+ "task will be completed/cancelled (therefore the Contact Event will no longer appear in Workload tray)")
	public void testSetContactEventUnprocessedToProcessed() throws Exception {

		doAssertionsAfterContactEventInitiallyCreated();// sanity check

		// User Updates the Contact Event and Refer CE to Partner
		PutPoleObjectsRequestDto updateRequest = updateContactEventRequestWithPartnerTxData(contactEvent);
		poleBusinessServices.putPoleObjects(updateRequest);
		contactEvent = contils.retrieveContactFromPole(contactEventObjRef, 2, poleDirect, securityContextId, false);

		assertContactEventContainsDecisionLogEntry(contactEvent, REFER_TO_PARTNER_DEFAULT_DECISION_LOG_TEXT);

		assertEquals("A ContactEventAction should have been added to the CE after referring to partner", 1,
				contactEvent.getContactEventActionlist().size());

		assertEquals(
				"The new ContactEventAction added to the CE should have specific action performed, after referring to partner",
				UpdateContactEventWorkflowContextBean.ContactEventAction.REFERRED_TO_PARTNER.toString(),
				contactEvent.getContactEventActionlist().get(0).getActionPerformed());

		assertEquals(SubmitContactServiceConstants.PROCESSED_STATUS, contactEvent.getStatus());
		
		TaskSummaryData taskSummaryDataOfContactEvent = poleFlowUtils.getTaskRelatedInfomration(poleFlowSoapService,
				poleFlowSecurityToken, PoleNames.CONTACT_EVENT, contactEvent.getObjectRef(), securityContextId, 0, null,
				defaultBetweenAttemptsToCallWorkflow);

		assertEquals("Expect no task to remain on the ContactEvent after marking "+ SubmitContactServiceConstants.CLOSED_STATUS,
				0, taskSummaryDataOfContactEvent.getCount());
	}
	
	/**
	 * Test CE status and its work flow changes during Marking CE as Pending
	 */
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			mingleRef=35299,
			mingleTitle="Decision Support - Mark Contact Event as Pending [#35231 Part2]",
			acceptanceCriteriaRefs="CR1,CR2,CR2.1,CR2.2,CR2.3",
			given="The Contact Event is open in Decision Support screen",
			when="User clicks on 'Ok' button on the pop-up screen.",
			then="A decision log entry is created against Contact Event,"
					+ "The status of Contact Event will be changed to Pending,"
					+ "'Process New Contact task' will be completed / cancelled,"
					+ "A new task 'Process Pending Contact' will be raised in Reviewing Unit workload tray..")
	public void testSetContactEventUnprocessedToPending() throws Exception {

		doAssertionsAfterContactEventInitiallyCreated();// sanity check

		PutPoleObjectsRequestDto updateRequest = updateContactEventRequestWithCEPendingTxData(contactEvent);
		poleBusinessServices.putPoleObjects(updateRequest);
		contactEvent = contils.retrieveContactFromPole(contactEventObjRef, 2, poleDirect, securityContextId, false);

		assertContactEventContainsDecisionLogEntry(contactEvent, TEST_REASON);

		assertEquals(SubmitContactServiceConstants.PENDING_STATUS, contactEvent.getStatus());

		TaskSummaryData taskSummaryDataOfContactEvent = poleFlowUtils.getTaskRelatedInfomration(poleFlowSoapService,
				poleFlowSecurityToken, PoleNames.CONTACT_EVENT, contactEvent.getObjectRef(), securityContextId, 1,
				PROCESS_PENDING_CONTACT, defaultBetweenAttemptsToCallWorkflow);

		assertEquals("A single '" + PROCESS_PENDING_CONTACT + "' task should be open on the ContactEvent", 1,
				taskSummaryDataOfContactEvent.getCount());
		assertEquals("A single '" + PROCESS_PENDING_CONTACT + "' task should be open on the ContactEvent",
				PROCESS_PENDING_CONTACT, taskSummaryDataOfContactEvent.getName());
	}

	/**
	 * Test CE status and its work flow changes during Marking CE as Closed
	 */
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			mingleRef=35298,
			mingleTitle="Decision Support - mark CE as Closed / No Further Action [#35231 Part1]",
			acceptanceCriteriaRefs="CR1,CR2,CR3,CR4",
			given="The Contact Event is open in Decision Support screen",
			when="User clicks on No Further Action button",
			then="A decision log entry is created against Contact Event,"
					+ "The status of Contact Event will be changed to Closed,"
					+ "Process New Contact task will be completed / cancelled.")
	public void testSetContactEventUnprocessedToClosed() throws Exception {

		doAssertionsAfterContactEventInitiallyCreated();// sanity check

		TaskSummaryData taskSummaryDataOfContactEvent = poleFlowUtils.getTaskRelatedInfomration(poleFlowSoapService,
				poleFlowSecurityToken, PoleNames.CONTACT_EVENT, contactEvent.getObjectRef(), securityContextId, 1,
				PROCESS_NEW_CONTACT, defaultBetweenAttemptsToCallWorkflow);

		assertEquals("Expect a single '" + PROCESS_NEW_CONTACT + "' task to exist on the ContactEvent", 1,
				taskSummaryDataOfContactEvent.getCount());
		assertEquals("Expect a single '" + PROCESS_NEW_CONTACT + "' task to exist on the ContactEvent",
				PROCESS_NEW_CONTACT, taskSummaryDataOfContactEvent.getName());

		// Since this is Newly Contact Event created in Pole, the status of it should be
		// SubmitContactServiceConstants.UNPROCESSED_STATUS
		assertEquals(SubmitContactServiceConstants.UNPROCESSED_STATUS, contactEvent.getStatus());

		PutPoleObjectsRequestDto updateRequest = updateContactEventRequestWithCEClosedTxData(contactEvent);
		poleBusinessServices.putPoleObjects(updateRequest);
		contactEvent = contils.retrieveContactFromPole(contactEventObjRef, 2, poleDirect, securityContextId, false);

		assertContactEventContainsDecisionLogEntry(contactEvent, TEST_REASON);
		assertEquals(SubmitContactServiceConstants.CLOSED_STATUS, contactEvent.getStatus());

		taskSummaryDataOfContactEvent = poleFlowUtils.getTaskRelatedInfomration(poleFlowSoapService, poleFlowSecurityToken,
				PoleNames.CONTACT_EVENT, contactEvent.getObjectRef(), securityContextId, 0, null,
				defaultBetweenAttemptsToCallWorkflow);

		assertEquals("Expect no task to remain on the ContactEvent after marking "+ SubmitContactServiceConstants.CLOSED_STATUS,
				0, taskSummaryDataOfContactEvent.getCount());

	}

	private PutPoleObjectsRequestDto updateContactEventRequestWithPartnerTxData(ContactEventDto contactEventDto) {
		contactEventDto.setModificationStatus(ModificationStatusDto.UPDATE);
		// Add ReferContactEventToPartnerTxData and ContactEventTxData
		contils.addReferToPartnerTxData(contactEventDto);

		// Create the update request for the ContactEvent
		PutPoleObjectsRequestDto updateRequest = contils.createPutPoleObjectsRequest(
				ContactEventTestUtils.createBusinessServiceInfo(securityContextId), contactEventDto);
		return updateRequest;
	}

	private PutPoleObjectsRequestDto updateContactEventRequestWithCEPendingTxData(ContactEventDto contactEventDto) {
		contactEventDto.setModificationStatus(ModificationStatusDto.UPDATE);
		contils.addPendingContactTxData(contactEventDto);

		// Create the update request for the ContactEvent
		PutPoleObjectsRequestDto updateRequest = contils.createPutPoleObjectsRequest(
				ContactEventTestUtils.createBusinessServiceInfo(securityContextId), contactEventDto);
		return updateRequest;
	}
	
	private PutPoleObjectsRequestDto updateContactEventRequestWithCEClosedTxData(ContactEventDto contactEventDto) {

		contactEventDto.setModificationStatus(ModificationStatusDto.UPDATE);
		// Add SetContactEventToPendingTxDataDto and ContactEventTxData

		SetContactEventToClosedTxDataDto setContactEventToClosedTxDataDto = new SetContactEventToClosedTxDataDto();
		setContactEventToClosedTxDataDto.setModificationStatus(ModificationStatusDto.CREATE);
		setContactEventToClosedTxDataDto.setSubmittingOfficerId(officerReportingId);
		setContactEventToClosedTxDataDto.setSubmittingOfficerDisplayValue(TEST_OFFICER_DISPLAY_VALUE);
		setContactEventToClosedTxDataDto.setReason(TEST_REASON);

		ContactEventTxDataDto contactEventTxDataDto = new ContactEventTxDataDto();
		contactEventTxDataDto.setModificationStatus(ModificationStatusDto.CREATE);		
		contactEventTxDataDto.setSetContactEventToClosedTxData(setContactEventToClosedTxDataDto);

		contactEventDto.setContactEventTxData(contactEventTxDataDto);

		// Create the update request for the ContactEvent
		PutPoleObjectsRequestDto updateRequest = contils.createPutPoleObjectsRequest(
				ContactEventTestUtils.createBusinessServiceInfo(securityContextId), contactEventDto);
		return updateRequest;
	}

	/*
	 * Creates PutPoleObjectRequest of a newly created Contact Event
	 */
	private PutPoleObjectsRequestDto createContactEventPutPoleObjectRequest() {
		PutPoleObjectsRequestDto request = contils.createPutPoleObjectsRequest(
				ContactEventTestUtils.createBusinessServiceInfo(securityContextId), contils.createContactEventDto(extRef, peAccountRef));
		return request;
	}

	/**
	 * Assert that a new decision log policy file entry has been added into the graph.
	 */
	private void assertContactEventContainsDecisionLogEntry(ContactEventDto contactEvent, String decisionLogText) {
		assertEquals("DecisionLog size should be exactly 1. But found "+contactEvent.getPolicyFiles().size(), 
				contactEvent.getPolicyFiles().size(), 1);
		assertEquals(contactEvent.getPolicyFiles().get(0).getEntryText(), decisionLogText);
		assertNotNull(contactEvent.getPolicyFiles().get(0).getEntryDate());		
		assertEquals("EntryType of DecisionLog not matching!!!", contactLogEntryType,
				contactEvent.getPolicyFiles().get(0).getEntryType());
	}
}
