package com.northgateis.gem.bussvc.submitincident.functest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.junit.Assume;
import org.junit.Test;

import com.northgateis.gem.bussvc.framework.test.BusSvcStoryAcceptanceCriteriaReference;
import com.northgateis.gem.bussvc.poleobjects.functest.AbstractPoleObjectsFuncTestBase;
import com.northgateis.gem.bussvc.submitcontact.utils.ContactEventPoleDtoUtils;
import com.northgateis.gem.bussvc.submitincident.utils.IncidentPoleDtoUtils;
import com.northgateis.gem.bussvc.test.util.TaskSummaryData;
import com.northgateis.gem.framework.util.logger.GemLogger;
import com.northgateis.pole.common.LockAcquisitionFailureException;
import com.northgateis.pole.common.PoleNames;
import com.northgateis.pole.schema.ContactEventDto;
import com.northgateis.pole.schema.IncidentDto;
import com.northgateis.pole.schema.ModificationStatusDto;
import com.northgateis.pole.schema.PutPoleObjectsRequestDto;
import com.northgateis.pole.schema.PutPoleObjectsResponseDto;

/**
 * Functional tests for creation of an {@link IncidentDto} from a {@link ContactEventDto} with
 * particular focus on Workflow state and Incident/ContactEvent Status.
 * 
 * @author dan.allford
 */
public class CreateIncidentFromContactEventWorkflowFuncTest extends AbstractPoleObjectsFuncTestBase {

	private static final GemLogger logger = GemLogger.getLogger(CreateIncidentFromContactEventWorkflowFuncTest.class);
	
	private ContactEventDto contactEvent;	
	private IncidentDto inc;

	private static final String QA_NEW_INVESTIGATION_EXTERNAL_TASK_NAME = "QA New Investigation";
			
	@Override
	protected void setupImpl() throws Exception {
		
		Assume.assumeTrue(isRunWorkflowTests());
		
		contactEvent = null;
		inc = null;
		contactEvent = createNewContactEvent();
	}
	
	private ContactEventDto createNewContactEvent() throws Exception {
		
		String extRefValue = PE_REF_PREFIX + new Date();
		
		ContactEventDto ceDtoOrig = contils.createContactEventDto(extRefValue, peAccountRef);
		PutPoleObjectsRequestDto req = contils.createContactEventRequest(contils.createBusinessServiceInfo(securityContextId), ceDtoOrig);
				
		PutPoleObjectsResponseDto resp = poleBusinessServices.putPoleObjects(req);
		assertNotNull(getBusinessServiceResultInfo(resp).getTransactionId());
		
		return contils.retrieveContactFromPole(ContactEventPoleDtoUtils.extractContactEventObjectRef(resp), 5, poleDirect, securityContextId, false);
	}
	
	@Override
	protected void teardownImpl() throws Exception {
		deleteData();
	}
	
	private void deleteData() throws Exception {

		try { 
			if (contactEvent != null) {
				contils.retrieveContactFromPole(contactEvent.getObjectRef(), 2, poleDirect, securityContextId, false);
			}
		} catch (Exception e) {
			logger.debug("Failed to delete record", e);
		}
		
		try { 
			utils.removeIncidentFromPole(inc.getObjectRef(), 2, poleDirect, securityContextId); 
		} catch (Exception e) {
			logger.debug("Failed to delete record", e);
		}
	}

	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			mingleRef=33076,
			mingleTitle="Bus Svcs - Contact Events - create Investigation from Contact (JSON REST /incident/create)",
			acceptanceCriteriaRefs="NA",
			given="I have created an Incident from an UNPROCESSED ContactEvent",
			when="The transaction is processed by POLEFlow",
			then="A new Task should be added to the Incident AND no the Task on the CE should be completed."
	)	
	public void testCreateIncidentFromContactEventUnprocessedWithWorkflow() throws Exception {

		// After setup, CE should be in UNPROCESSED state. Now create an Incident from it:

		inc = utils.prepareIncidentWithNoLinks(-1, Integer.valueOf(officerReportingId));

		PutPoleObjectsRequestDto ppoDto = utils.preparePpoRequestForCreateIncidentFromContact(
				utils.createBusinessServiceInfo(securityContextId), inc, contactEvent, false);

		utils.addWorkflowTxDataForCreateIncident(ppoDto, taskStaffId, taskUnitName);

		contactEvent.setLinks(null);

		utils.addWorkflowTxDataForCreateIncident(ppoDto, taskStaffId, taskUnitName);

		PutPoleObjectsResponseDto createIncFromContactResponse = poleBusinessServices.putPoleObjects(ppoDto);

		Integer incObjRef = IncidentPoleDtoUtils.extractIncidentObjectRef(createIncFromContactResponse);
		inc = utils.getIncidentFromPole(incObjRef, poleDirect, securityContextId);

		TaskSummaryData taskSummaryData = poleFlowUtils.getTaskRelatedInfomration(poleFlowSoapService,
				poleFlowSecurityToken, PoleNames.CONTACT_EVENT, contactEvent.getObjectRef(), securityContextId, 0, null,
				defaultBetweenAttemptsToCallWorkflow);

		assertEquals("No task on ContactEvent should remain after creating an Incident from it", 0,	taskSummaryData.getCount());

		taskSummaryData = poleFlowUtils.getTaskRelatedInfomration(poleFlowSoapService,
				poleFlowSecurityToken, PoleNames.INCIDENT, incObjRef, securityContextId, 1,
				QA_NEW_INVESTIGATION_EXTERNAL_TASK_NAME, defaultBetweenAttemptsToCallWorkflow);

		assertEquals("A single '" + QA_NEW_INVESTIGATION_EXTERNAL_TASK_NAME	+ "' task should exist on Incident created from CE.",
									1, taskSummaryData.getCount());

		assertEquals("A single '" + QA_NEW_INVESTIGATION_EXTERNAL_TASK_NAME	+ "' task should exist on Incident created from CE.",
				QA_NEW_INVESTIGATION_EXTERNAL_TASK_NAME, taskSummaryData.getName());

	}
	
	
	/**
	 * TODO CODE REVIEW #33076 Story long 'completed'. Invalid scenario here creates 2 x Incs from a single
	 * CE which isn't actually legal! A better scenario would be to e.g. 
	 *     (1) refer to partner (setup - should mark the CE as processed) and then
	 *     (2) create a new Inc. TX should complete despite no outstanding CE task to complete. New task should go onto Inc.
	 * 
	 * @throws Exception
	 */
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			mingleRef=33076,
			mingleTitle="Bus Svcs - Contact Events - create Investigation from Contact (JSON REST /incident/create)",
			acceptanceCriteriaRefs="NA",
			given="I have created an Incident from a PROCESSED ContactEvent",
			when="The transaction is processed by POLEFlow",
			then="A new Task should be added to the Incident AND no Task changes should occur on the CE"
	)	
	public void testCreateIncidentFromContactEventStatusProcessedWithWorkflow() throws Exception {

		contactEvent.setModificationStatus(ModificationStatusDto.UPDATE);

		contils.addReferToPartnerTxData(contactEvent);

		PutPoleObjectsRequestDto updateRequest = contils.createPutPoleObjectsRequest(
				contils.createBusinessServiceInfo(securityContextId), contactEvent);

		poleBusinessServices.putPoleObjects(updateRequest);

		ContactEventDto updatecontactEvent = contils.retrieveContactFromPole(contactEvent.getObjectRef(), 5, poleDirect,
				securityContextId, false);

		updatecontactEvent.setLinks(null);

		inc = utils.prepareIncidentWithNoLinks(-1, Integer.valueOf(officerReportingId));

		PutPoleObjectsRequestDto ppoDto = utils.preparePpoRequestForCreateIncidentFromContact(
				utils.createBusinessServiceInfo(securityContextId), inc, updatecontactEvent, false);

		utils.addWorkflowTxDataForCreateIncident(ppoDto, taskStaffId, taskUnitName);

		PutPoleObjectsResponseDto createIncFromContactResponse = null;

		// TODO CODE REVIEW: make this a util method and/or lock the record by default in POLE upon creation before
		// APM/CCIS can grab it.
		// APM gets busy now, so this next bit must go into a loop
		for (int seconds = 0; seconds < ASYNC_DEPENDENCY_TEST_TIMEOUT; seconds = seconds + 2) {
			try {
				createIncFromContactResponse = poleBusinessServices.putPoleObjects(ppoDto);
				break;
			} catch (LockAcquisitionFailureException lockEx) {
				logger.warn("Could not obtain lock: " + lockEx.getMessage());
				try {
					Thread.sleep(2000);
				} catch (Exception ex) {
				}
			}
		}

		Integer incObjRef = IncidentPoleDtoUtils.extractIncidentObjectRef(createIncFromContactResponse);
		inc = utils.getIncidentFromPole(incObjRef, poleDirect, securityContextId);

		TaskSummaryData taskSummaryData = poleFlowUtils.getTaskRelatedInfomration(poleFlowSoapService,
								poleFlowSecurityToken, PoleNames.CONTACT_EVENT, contactEvent.getObjectRef(), securityContextId, 0, null,defaultBetweenAttemptsToCallWorkflow);

		assertEquals("No task on ContactEvent should remain after creating an Incident from it", 0,
				taskSummaryData.getCount());

		taskSummaryData = poleFlowUtils.getTaskRelatedInfomration(poleFlowSoapService,
								poleFlowSecurityToken, PoleNames.INCIDENT, incObjRef, securityContextId, 1,	QA_NEW_INVESTIGATION_EXTERNAL_TASK_NAME, defaultBetweenAttemptsToCallWorkflow);

		assertEquals("A single '" + QA_NEW_INVESTIGATION_EXTERNAL_TASK_NAME	+ "' task should exist on Incident created from CE.",
					1, taskSummaryData.getCount());

		assertEquals("A single '" + QA_NEW_INVESTIGATION_EXTERNAL_TASK_NAME + "' task should exist on Incident created from CE.",
						QA_NEW_INVESTIGATION_EXTERNAL_TASK_NAME, taskSummaryData.getName());

	}
	
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			mingleRef=33076,
			mingleTitle="Bus Svcs - Contact Events - create Investigation from Contact (JSON REST /incident/create)",
			acceptanceCriteriaRefs="NA",
			given="I have created an Incident from a PENDING ContactEvent",
			when="The transaction is processed by POLEFlow",
			then="A new Task should be added to the Incident AND the PENDING Task on the CE should be completed"
	)	
	public void testCreateIncidentFromContactEventStatusPendingWithWorkflow() throws Exception {

		TaskSummaryData taskSummaryDataOfContactEvent = poleFlowUtils.getTaskRelatedInfomration(poleFlowSoapService,
				poleFlowSecurityToken, PoleNames.CONTACT_EVENT, contactEvent.getObjectRef(), securityContextId, 1, null,
				defaultBetweenAttemptsToCallWorkflow);

		assertEquals("During setup the initially created ContactEvent should have a single PROCESS task against it", 1,
				taskSummaryDataOfContactEvent.getCount());

		contactEvent.setModificationStatus(ModificationStatusDto.UPDATE);

		contils.addPendingContactTxData(contactEvent);

		PutPoleObjectsRequestDto updateRequest = contils.createPutPoleObjectsRequest(
				contils.createBusinessServiceInfo(securityContextId), contactEvent);

		poleBusinessServices.putPoleObjects(updateRequest);

		ContactEventDto updatecontactEvent = contils.retrieveContactFromPole(contactEvent.getObjectRef(), 5, poleDirect,
				securityContextId, false);

		taskSummaryDataOfContactEvent = poleFlowUtils.getTaskRelatedInfomration(poleFlowSoapService, poleFlowSecurityToken,
				PoleNames.CONTACT_EVENT, contactEvent.getObjectRef(), securityContextId, 1, null,
				defaultBetweenAttemptsToCallWorkflow);
		
		assertEquals("After setup, the PENDING ContactEvent should have a single Task against it", 1,
				taskSummaryDataOfContactEvent.getCount());

		// End of extended setup: now create an Inc from the CE in PENDING status:

		updatecontactEvent.setLinks(null);

		inc = utils.prepareIncidentWithNoLinks(-1, Integer.valueOf(officerReportingId));

		PutPoleObjectsRequestDto ppoDto = utils.preparePpoRequestForCreateIncidentFromContact(
				utils.createBusinessServiceInfo(securityContextId), inc, updatecontactEvent, false);

		utils.addWorkflowTxDataForCreateIncident(ppoDto, taskStaffId, taskUnitName);

		PutPoleObjectsResponseDto createIncFromContactResponse = poleBusinessServices.putPoleObjects(ppoDto);

		Integer incObjRef = IncidentPoleDtoUtils.extractIncidentObjectRef(createIncFromContactResponse);
		inc = utils.getIncidentFromPole(incObjRef, poleDirect, securityContextId);

		taskSummaryDataOfContactEvent = poleFlowUtils.getTaskRelatedInfomration(poleFlowSoapService, poleFlowSecurityToken,
				PoleNames.CONTACT_EVENT, contactEvent.getObjectRef(), securityContextId, 0, null, defaultBetweenAttemptsToCallWorkflow);
		assertEquals("No task on ContactEvent should remain after creating an Incident from it", 0,
				taskSummaryDataOfContactEvent.getCount());

		TaskSummaryData taskSummaryDataOfIncident = null;
		
		for (int seconds = 0; seconds < 20 && (taskSummaryDataOfIncident == null 
				|| taskSummaryDataOfIncident.getCount() == 0); seconds = seconds + 5) {
	
			taskSummaryDataOfIncident = poleFlowUtils.getTaskRelatedInfomration(poleFlowSoapService,
				poleFlowSecurityToken, PoleNames.INCIDENT, incObjRef, securityContextId, 1,
				QA_NEW_INVESTIGATION_EXTERNAL_TASK_NAME, defaultBetweenAttemptsToCallWorkflow);
		}

		assertEquals("A single '" + QA_NEW_INVESTIGATION_EXTERNAL_TASK_NAME	+ "' task should exist on Incident created from CE.",
							1, taskSummaryDataOfIncident.getCount());

		assertEquals("A single '" + QA_NEW_INVESTIGATION_EXTERNAL_TASK_NAME	+ "' task should exist on Incident created from CE.",
			QA_NEW_INVESTIGATION_EXTERNAL_TASK_NAME, taskSummaryDataOfIncident.getName());

	}
}
