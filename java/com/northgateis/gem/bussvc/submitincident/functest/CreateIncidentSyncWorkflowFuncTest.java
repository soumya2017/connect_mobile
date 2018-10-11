package com.northgateis.gem.bussvc.submitincident.functest;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;

import com.northgateis.gem.bussvc.framework.test.BusSvcStoryAcceptanceCriteriaReference;
import com.northgateis.gem.bussvc.pole.utils.PoleDtoUtils;
import com.northgateis.gem.bussvc.poleobjects.appliedlogic.workflow.AbstractWorkflowContextBean;
import com.northgateis.gem.bussvc.poleobjects.functest.AbstractPoleObjectsFuncTestBase;
import com.northgateis.gem.bussvc.submitincident.utils.IncidentPoleDtoUtils;
import com.northgateis.gem.bussvc.test.util.BusinessServicesTestUtils;
import com.northgateis.gem.bussvc.test.util.TaskSummaryData;
import com.northgateis.pole.common.PoleNames;
import com.northgateis.pole.schema.CreateIncidentTxDataDto;
import com.northgateis.pole.schema.IncidentDto;
import com.northgateis.pole.schema.PutPoleObjectsRequestDto;
import com.northgateis.pole.schema.PutPoleObjectsResponseDto;

/**
 * Tests the sync nature of workflow being adopted for 
 * CCI#36379 (CS#38051) "Infra 3260829: Changes to Async workflow processing"
 * where we want to allow SYNCHRONOUS workflow call to POLEFlow within the
 * original transaction.
 * 
 * That Story is based around 4 x Case tasks, but the easiest example object
 * to test this functionality around is actually the Create Incident scenario. Here
 * we need to add the WF header 'up front' as if CCI were calling us to Create
 * Incident *not* using TxData, but instead passing-in the WF header.
 * 
 * To activate the SYNCHRONOUS workflow, the following extraInfos must be provided:
 * 
 *  queueWorkflow=T
 *  queueWorkflowAsynch=F
 * 
 * See also the RouteTests in SyncWorkflowHeaderUpdateCaseServiceRouteTest which
 * uses an example Case, but isn't a functional test (so doesn't need to worry about the
 * immense Case data setup).
 * 
 * @author dan.allford
 */
public class CreateIncidentSyncWorkflowFuncTest extends AbstractPoleObjectsFuncTestBase {

	private static final String COMPLETE_INVESTIGATION_CREATION = "Complete Investigation Creation";
	
	private IncidentDto incident;
	
	private PutPoleObjectsRequestDto putPoleObjectsRequest;
	
	@Override
	protected void setupImpl() throws Exception {
		incident = utils.createIncident(-1, Integer.valueOf(officerReportingId));
		
		CreateIncidentTxDataDto txd = incident.getIncidentTxData().getCreateIncidentTxData();
		txd.setCompleted(Boolean.FALSE);
		txd.setQaUnit(null);
		txd.setQaUnitDisplayValue(null);
		txd.setLinkingUnit(null);
		txd.setLinkingUnitDisplayValue(null);
		
		putPoleObjectsRequest = utils.createPutPoleObjectsRequestDto(
				BusinessServicesTestUtils.createBusinessServiceInfo(securityContextId), incident);
	}
	
	@Override
	protected void teardownImpl() throws Exception {
		try {
			if (incident != null) {
				utils.removeIncidentFromPole(incident.getObjectRef(), 2, poleDirect, securityContextId);
			}
		} catch (Exception e) {
			logger.debug("Failed to delete Incident having objectRef {} ", incident.getObjectRef(), e);
		}
	}	

	/**
	 * Happy path for workflow synch-header.
	 * <p>
	 * Test when the case has PoleFlow headers to perform a Poleflow task synchronously. 
	 * @throws InterruptedException 
	 */
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=0,
		mingleRef=38051,
		mingleTitle="Infra 3260829: Changes to Async workflow processing",
		acceptanceCriteriaRefs="CR1.1,CR1.2,CR1.3",
		given="I have logged in to CONNECT "
				+ "AND I have navigated to the Case workload.",
		when="I perform the task: ‘Reviewer review submission for pre-charge decision’",
		then="The task will be performed successfully without delay. "
			+ "AND The ‘Reviewer review submission for pre-charge decision’ task is displayed on the task history. "
			+ "AND ‘Reviewer review submission for pre-charge decision’ task is removed from the Select task tab of the Case event.")
	public void testHappyPathForSynchWorkflowHeader() throws Exception {
		
		PoleDtoUtils.addOrUpdateExtraInfo(putPoleObjectsRequest,
				AbstractWorkflowContextBean.QUEUE_WORKFLOW_EXTRA_INFO_NAME, AbstractWorkflowContextBean.ENSEMBLE_TRUE);
		PoleDtoUtils.addOrUpdateExtraInfo(putPoleObjectsRequest,
				AbstractWorkflowContextBean.QUEUE_WORKFLOW_ASYNCH_EXTRA_INFO_NAME, AbstractWorkflowContextBean.ENSEMBLE_FALSE);
		
		createIncidentWithWorkflow();
	}
	
	@Ignore // FIX ME 
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=0,
		mingleRef=38051,
		mingleTitle="Infra 3260829: Changes to Async workflow processing",
		acceptanceCriteriaRefs="CR2.1,CR2.2,CR2.3",
		given="Workflow is unavailable, stressed, or unresponsive to immediate calls from Business Services.",
		when="I perform the task: ‘Reviewer review submission for pre-charge decision’",
		then="Task History will be immediately added/visible. "
			+ "AND Task will not be immediately removed from Perform Task tab. It will remain on this tab and in Workload tray. "
			+ "AND A warning message will be displayed to the user stating 'The workflow processing will be completed later. Please do not resubmit or cancel the Task.'")
	public void testHappyPathForAsyncWorkflowHeader() throws Exception {
		
		createIncidentWithWorkflow();
	}
	
	private void createIncidentWithWorkflow() throws Exception {
		
		PutPoleObjectsResponseDto poleObjectsResponse = poleBusinessServices.putPoleObjects(putPoleObjectsRequest);
		Integer incidentObjectRef = IncidentPoleDtoUtils.extractIncidentObjectRef(poleObjectsResponse);
		
		TaskSummaryData taskSummaryData = poleFlowUtils.getTaskRelatedInfomration(poleFlowSoapService,
				poleFlowSecurityToken, PoleNames.INCIDENT, incidentObjectRef, securityContextId, 2,
				COMPLETE_INVESTIGATION_CREATION, defaultBetweenAttemptsToCallWorkflow);
		
		assertEquals("Two tasks should exist on Incident created from CE.",						
				2, taskSummaryData.getCount());
		
		assertEquals("A single '" + COMPLETE_INVESTIGATION_CREATION
						+ "' task should exist on Incident created from CE.",
						COMPLETE_INVESTIGATION_CREATION, taskSummaryData.getName());
	}
}
