package com.northgateis.gem.bussvc.submitincident.functest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.northgateis.gem.bussvc.framework.FrameworkServiceConstants;
import com.northgateis.gem.bussvc.framework.test.BusSvcStoryAcceptanceCriteriaReference;
import com.northgateis.gem.bussvc.submitincident.appliedlogic.AllocateInvestigationWorkflowContextBean;
import com.northgateis.gem.bussvc.submitincident.appliedlogic.ImuProcessInvestigationWorkflowContextBean;
import com.northgateis.gem.bussvc.submitincident.appliedlogic.constants.IncidentStatus;
import com.northgateis.gem.bussvc.submitincident.constants.SubmitIncidentServiceConstants;
import com.northgateis.gem.bussvc.test.util.BusinessServicesTestUtils;
import com.northgateis.pole.common.InvalidDataException;
import com.northgateis.pole.schema.GemTaskHistoryDto;
import com.northgateis.pole.schema.IncidentDto;
import com.northgateis.pole.schema.PutPoleObjectsRequestDto;
import com.northgateis.pole.schema.PutPoleObjectsResponseDto;

/**
 * Functional Test For Allocate Investigation
 //TODO Some of FTs are still pending
 * @author amit.desai
 *
 */
public class AllocateInvestigationValidationFuncTest extends UpdateIncidentValidationFuncTest{

	@Override
	protected void setupImpl() throws Exception {	
		if (!isRunWorkflowTests()) {
			return;
		}
		incidentDto =  utils.prepareIncidentWithResearchLinks(-1, Integer.valueOf(officerReportingId));
		incidentDto = createCompletedInvestigation(incidentDto);		
		incidentDto = getUpdateIncidentDtoWithQANewInvestigationtxData(incidentDto);		
		super.doProcessEventObjectWithWorkFlow(incidentDto);
		incidentDto = getUpdateIncidentDtoWithImuProcessInvestigationtxData(incidentDto.getObjectRef());
		incidentDto.getIncidentTxData().getImuProcessInvestigationTxData().setActionTaken(
				ImuProcessInvestigationWorkflowContextBean.ACTION_TAKEN_SEND_FOR_ALLOCATION);
		incidentDto.getIncidentTxData().getImuProcessInvestigationTxData().setAllocatedUnitCode(unitCode);
		super.doProcessEventObjectWithWorkFlow(incidentDto);
		incidentDto = getUpdateIncidentDtoWithAllocateInvestigationtxData(incidentDto.getObjectRef());
	}	
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=344,
			jiraRef = "CON-38793",
			mingleTitle = "WP344 - Business Service - Update Investigation - Business Service Operation - "
					+ "Allocate Investigation",
			acceptanceCriteriaRefs = "CR1", 
			given = "An existing Investigation exists with the 'Allocate Investigation' workflow task present "
					+ "on the task dashboard of Connect Express && "
					+ "CR1 (incident status != 'REQUIRES ALLOCATION')  ",
			when = "Business services receives the pole graph. Incident Submitted.",
			then = "Validation error for mismatch of Investigation status 'REQUIRES ALLOCATION'")
	public void testAllocateInvestigationTaskWithInvalidStatusForAllocateToOIC() throws Exception {
		if (!isRunWorkflowTests()) {
			return;
		}
		try {
			//Setting the invalid status
			incidentDto.setStatus(SubmitIncidentServiceConstants.DESCRIBED_PERSON );
			incidentDto.getIncidentTxData().getAllocateInvestigationTxData() .setActionTaken(
					AllocateInvestigationWorkflowContextBean.ALLOCATE_TO_OIC);
			doProcessEventObjectWithWorkFlow(incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("status does not match the expected value REQUIRES ALLOCATION"));
		}
	}
		
	//Currently the FT is ignored as requires further modifications
	@Ignore
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=344,
			jiraRef = "CON-38793",
			mingleTitle = "WP344 - Business Service - Update Investigation - Business Service Operation - "
					+ "Allocate Investigation",
			acceptanceCriteriaRefs = "CR1.1", 
			given = "An existing Investigation exists with the 'Allocate Investigation' workflow task present on the "
					+ "task dashboard of Connect Express && "
					+ " Allocate Investigation task is already present in task history  ",
			when = "Business services receives the pole graph. Incident Submitted.",
			then = "Validation error is returned, ")
	public void testAllocateInvestigationTaskWithAllocateInvestigationTaskHistoryPresent() throws Exception {
		if (!isRunWorkflowTests()) {
			return;
		}
		try {
			incidentDto.setStatus(IncidentStatus.REQUIRES_ALLOCATION.toString()  );
			incidentDto.getIncidentTxData().getAllocateInvestigationTxData().setActionTaken(
					AllocateInvestigationWorkflowContextBean.ALLOCATE_TO_OIC);
			incidentDto.getIncidentTxData().getAllocateInvestigationTxData().setAllocatedToStaffEmployeeIterationId(
					officerReportingId);
			doProcessEventObjectWithWorkFlow(incidentDto);
			boolean taskHistoryFound = false;
			List<GemTaskHistoryDto> gemTaskHistoryEntries = incidentDto.getGemTaskHistoryEntries();
			String taskId = null;
			for (GemTaskHistoryDto gemTaskHistoryDto : gemTaskHistoryEntries) {
				if (ImuProcessInvestigationWorkflowContextBean.INTERNAL_TASK_NAME.equalsIgnoreCase(
						gemTaskHistoryDto.getTaskName())) {
					taskHistoryFound = true;
					taskId = gemTaskHistoryDto.getTaskId();
				}
			}
			assertEquals("Allocate Investigation Task History found", true, taskHistoryFound);
			incidentDto.setStatus(IncidentStatus.REQUIRES_ALLOCATION.toString()  );
			incidentDto.getIncidentTxData().getAllocateInvestigationTxData().setActionTaken(
					AllocateInvestigationWorkflowContextBean.ALLOCATE_TO_OIC);
			incidentDto.getIncidentTxData().getAllocateInvestigationTxData().setAllocatedToStaffEmployeeIterationId(
					officerReportingId);
			incidentDto.getIncidentTxData().getAllocateInvestigationTxData().setTaskId(new Long(taskId));
			doProcessEventObjectWithWorkFlow(incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("The Entity has been updated"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=344,
			jiraRef = "CON-38793",
			mingleTitle = "WP344 - Business Service - Update Investigation - Business Service Operation - "
					+ "Allocate Investigation",
			acceptanceCriteriaRefs = "CR2", 
			given = "An existing Investigation exists with the 'Allocate Investigation' "
					+ "workflow task present on the task dashboard of Connect Express && "
					+ "CR2 (action taken == null) && ",
			when = "Business services receives the pole graph. Incident Submitted.",
			then = "Validation error for ActionTaken")
	public void testAllocateInvestigationTaskAllocateToOICActionTakenNull() throws Exception {
		if (!isRunWorkflowTests()) {
			return;
		}
		try {
			incidentDto.setStatus(IncidentStatus.REQUIRES_ALLOCATION.toString());
			incidentDto.getIncidentTxData().getAllocateInvestigationTxData() .setActionTaken(null);
			doProcessEventObjectWithWorkFlow(incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("actionTaken must have a value"));
		}
	}
	
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=344,
			jiraRef = "CON-38793",
			mingleTitle = "WP344 - Business Service - Update Investigation - Business Service Operation - "
					+ "Allocate Investigation",
			acceptanceCriteriaRefs = "CR3,3.1,3.2,3.3", 
			given = "An existing Investigation exists with the 'Allocate Investigation' "
					+ "workflow task present on the task dashboard of Connect Express && "
					+ "CR3 (action taken == Transfer to another Team && Unit Name Not Null && Reason for Transfer not null) ",
			when = "Business services receives the pole graph. Incident Submitted.",
			then = "Data Saved in Pole without any error")
	public void testAllocateInvestigationTaskTransferToTeamUnitNameAndReasonForTransferGiven() throws Exception {
		if (!isRunWorkflowTests()) {
			return;
		}
		try {
			//Setting the invalid status
			incidentDto.setStatus(IncidentStatus.REQUIRES_ALLOCATION.toString());
			incidentDto.getIncidentTxData().getAllocateInvestigationTxData()
					.setActionTaken(AllocateInvestigationWorkflowContextBean.ACTION_TAKEN_TRANSFERRED);
			incidentDto.getIncidentTxData().getAllocateInvestigationTxData().setAllocatedToUnitCode(unitCode);
			incidentDto.getIncidentTxData().getAllocateInvestigationTxData().setReasonForTransfer("TEST TRANSFER");
			doProcessEventObjectWithWorkFlow(incidentDto);
			incidentDto = utils.getIncidentFromPole(incidentDto.getObjectRef(), poleDirect, securityContextId);
			// CR3.1
			assertEquals("Incident Status", IncidentStatus.REQUIRES_ALLOCATION.toString(),
					incidentDto.getStatus());
			// CR3.2
			boolean taskHistoryFound = false;
			List<GemTaskHistoryDto> gemTaskHistoryEntries = incidentDto.getGemTaskHistoryEntries();
			for (GemTaskHistoryDto gemTaskHistoryDto : gemTaskHistoryEntries) {
				if (AllocateInvestigationWorkflowContextBean.INTERNAL_TASK_NAME.equalsIgnoreCase(
						gemTaskHistoryDto.getTaskName())) {
					taskHistoryFound = true;
				}
			}
			assertEquals("Required taskHistory found", true, taskHistoryFound);
			
		} catch (InvalidDataException ide) {
			
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=344,
			jiraRef = "CON-38793",
			mingleTitle = "WP344 - Business Service - Update Investigation - Business Service Operation - "
					+ "Allocate Investigation",
			acceptanceCriteriaRefs = "CR4,CR5", 
			given = "An existing Investigation exists with the 'Allocate Investigation' workflow task present"
					+ " on the task dashboard of Connect Express && "
					+ "CR4,CR5 (action taken == Transfer to another Team && Unit Name is Null && Reason For Transfer is null) ",
			when = "Business services receives the pole graph. Incident Submitted.",
			then = "Validation error for allocatedToUnitCode")
	public void testAllocateInvestigationTaskTransferToTeamUnitNameAndReasonForTransferNotGiven() throws Exception {
		if (!isRunWorkflowTests()) {
			return;
		}
		try {
			incidentDto.setStatus(IncidentStatus.REQUIRES_ALLOCATION.toString());
			incidentDto.getIncidentTxData().getAllocateInvestigationTxData()
					.setActionTaken(AllocateInvestigationWorkflowContextBean.ACTION_TAKEN_TRANSFERRED);
			//CR4
			incidentDto.getIncidentTxData().getAllocateInvestigationTxData().setAllocatedToUnitCode(null);
			//CR5
			incidentDto.getIncidentTxData().getAllocateInvestigationTxData().setReasonForTransfer(null);
			doProcessEventObjectWithWorkFlow(incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			//CR4
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("allocatedToUnitCode must have a value"));
			//CR5
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("reasonForTransfer must have a value"));
		}
	}
	
	//This FT needs modification
	@Ignore
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=344,
			jiraRef = "CON-38793",
			mingleTitle = "WP344 - Business Service - Update Investigation - Business Service Operation - "
					+ "Allocate Investigation",
			acceptanceCriteriaRefs = "CR6", 
			given = "An existing Investigation exists with the 'Allocate Investigation' workflow"
					+ " task present on the task dashboard of Connect Express && "
					+ "CR6 (action taken == Transfer to another Team && Unit Does Not belong to Force and Not Valid Imu Unit) ",
			when = "Business services receives the pole graph. Incident Submitted.",
			then = "Validation error for unitName")
	public void testAllocateInvestigationTaskTransferToTeamUnitDoesNotBelongToForce() throws Exception {
		if (!isRunWorkflowTests()) {
			return;
		}
		try {
			incidentDto.setStatus(IncidentStatus.REQUIRES_ALLOCATION.toString());
			incidentDto.getIncidentTxData().getAllocateInvestigationTxData()
					.setActionTaken(AllocateInvestigationWorkflowContextBean.ACTION_TAKEN_TRANSFERRED);
			incidentDto.getIncidentTxData().getAllocateInvestigationTxData().setAllocatedToUnitCode("PLB");
			doProcessEventObjectWithWorkFlow(incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("allocatedToUnitCode must be valid"));
			}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=344,
			jiraRef = "CON-38793",
			mingleTitle = "WP344 - Business Service - Update Investigation - Business Service Operation - "
					+ "Allocate Investigation",
			acceptanceCriteriaRefs = "CR7", 
			given = "An existing Investigation exists with the 'Allocate Investigation'"
					+ " workflow task present on the task dashboard of Connect Express && "
					+ "CR7 (action taken == Transfer to another Team && Allocate to Staff Member is given) ",
			when = "Business services receives the pole graph. Incident Submitted.",
			then = "Forbidden validation error for OIC")
	public void testAllocateInvestigationTaskTransferToTeamAllocateToStaffMemberIsGiven() throws Exception {
		if (!isRunWorkflowTests()) {
			return;
		}
		try {
			//Setting the invalid status
			incidentDto.setStatus(IncidentStatus.REQUIRES_ALLOCATION.toString());
			incidentDto.getIncidentTxData().getAllocateInvestigationTxData() .setActionTaken(
					AllocateInvestigationWorkflowContextBean.ACTION_TAKEN_TRANSFERRED);
			incidentDto.getIncidentTxData().getAllocateInvestigationTxData()
					.setAllocatedToStaffEmployeeIterationId(officerReportingId);
			doProcessEventObjectWithWorkFlow(incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			//CR4
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("allocatedToStaffEmployeeIterationId must not have a value"));
			}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=344,
			jiraRef = "CON-38793",
			mingleTitle = "WP344 - Business Service - Update Investigation - Business Service Operation - "
					+ "Allocate Investigation",
			acceptanceCriteriaRefs = "CR8", 
			given = "An existing Investigation exists with the 'Allocate Investigation' "
					+ "workflow task present on the task dashboard of Connect Express && "
					+ "CR8 (action taken == Allocate to OIC && OIC Name is null) ",
			when = "Business services receives the pole graph. Incident Submitted.",
			then = "Mandatory validation error for OIC")
	public void testAllocateInvestigationTaskAllocateToOICandOICNameIsNull() throws Exception {
		if (!isRunWorkflowTests()) {
			return;
		}
		try {
			//Setting the invalid status
			incidentDto.setStatus(IncidentStatus.REQUIRES_ALLOCATION.toString());
			incidentDto.getIncidentTxData().getAllocateInvestigationTxData() .setActionTaken(
					AllocateInvestigationWorkflowContextBean.ALLOCATE_TO_OIC );
			incidentDto.getIncidentTxData().getAllocateInvestigationTxData()
					.setAllocatedToStaffEmployeeIterationId(null);
			doProcessEventObjectWithWorkFlow(incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			//CR4
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("allocatedToStaffEmployeeIterationId must have a value"));
			}
	}

	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=344,
			jiraRef = "CON-38793",
			mingleTitle = "WP344 - Business Service - Update Investigation - Business Service Operation - "
					+ "Allocate Investigation",
			acceptanceCriteriaRefs = "CR11", 
			given = "An existing Investigation exists with the 'Allocate Investigation'"
					+ " workflow task present on the task dashboard of Connect Express && "
					+ "CR11 (action taken == Allocate to OIC && Reason for Transfer is Not null) ",
			when = "Business services receives the pole graph. Incident Submitted.",
			then = "Forbidden validation error for reason for transfer")
	public void testAllocateInvestigationTaskAllocateToOICandReasonForTransferIsNotNull() throws Exception {
		if (!isRunWorkflowTests()) {
			return;
		}
		try {
			//Setting the invalid status
			incidentDto.setStatus(IncidentStatus.REQUIRES_ALLOCATION.toString());
			incidentDto.getIncidentTxData().getAllocateInvestigationTxData() .setActionTaken(
					AllocateInvestigationWorkflowContextBean.ALLOCATE_TO_OIC );
			incidentDto.getIncidentTxData().getAllocateInvestigationTxData()
					.setAllocatedToStaffEmployeeIterationId(officerReportingId);
			incidentDto.getIncidentTxData().getAllocateInvestigationTxData().setReasonForTransfer("TEST");
			doProcessEventObjectWithWorkFlow(incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("reasonForTransfer must not have a value"));
			}
	}
	
	@Ignore
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=344,
			jiraRef = "CON-38793",
			mingleTitle = "WP344 - Business Service - Update Investigation - Business Service Operation - "
					+ "Allocate Investigation",
			acceptanceCriteriaRefs = "CR12", 
			given = "An existing Investigation exists with the 'Allocate Investigation' workflow task "
					+ "present on the task dashboard of Connect Express && "
					+ "CR12 (action taken == Transfer to another Team && Reason for Transfer is Not null) ",
			when = "Business services receives the pole graph. Incident Submitted.",
			then = "Forbidden validation error for reason for transfer")
	public void testAllocateInvestigationTaskTransferToTeaandReasonForTransferIsNotNull() throws Exception {
		if (!isRunWorkflowTests()) {
			return;
		}
		try {
			//Setting the invalid status
			incidentDto.setStatus(IncidentStatus.REQUIRES_ALLOCATION.toString());
			incidentDto.getIncidentTxData().getAllocateInvestigationTxData() .setActionTaken(
					AllocateInvestigationWorkflowContextBean.ACTION_TAKEN_TRANSFERRED );
			incidentDto.getIncidentTxData().getAllocateInvestigationTxData().setAllocatedToUnitCode(unitCode);
			incidentDto.getIncidentTxData().getAllocateInvestigationTxData().setReasonForTransfer("TEST");
			doProcessEventObjectWithWorkFlow(incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("reasonForTransfer must not have a value"));
			}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=344,
			jiraRef = "CON-38793",
			mingleTitle = "WP344 - Business Service - Update Investigation - Business Service Operation - "
					+ "Allocate Investigation",
			acceptanceCriteriaRefs = "CR13", 
			given = "An existing Investigation exists with the 'Allocate Investigation' workflow task "
					+ "present on the task dashboard of Connect Express && "
					+ "CR13 (action taken == Allocate to An OIC && unitName is Not null) ",
			when = "Business services receives the pole graph. Incident Submitted.",
			then = "Forbidden validation error for reason for transfer")
	public void testAllocateInvestigationTaskAllocateToOICandReasonFandUnitNameIsNoNull() throws Exception {
		if (!isRunWorkflowTests()) {
			return;
		}
		try {
			//Setting the invalid status
			incidentDto.setStatus(IncidentStatus.REQUIRES_ALLOCATION.toString());
			incidentDto.getIncidentTxData().getAllocateInvestigationTxData() .setActionTaken(
					AllocateInvestigationWorkflowContextBean.ALLOCATE_TO_OIC);
			incidentDto.getIncidentTxData().getAllocateInvestigationTxData().setAllocatedToUnitCode(unitCode);
			doProcessEventObjectWithWorkFlow(incidentDto);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("allocatedToUnitCode must not have a value"));
			}
	}
	
	
	@Override
	protected void doProcessEventObjectWithWorkFlow(IncidentDto incident) throws Exception {

		PutPoleObjectsRequestDto putPoleObjectsRequest = utils.createPutPoleObjectsRequestDto(
				BusinessServicesTestUtils.createBusinessServiceInfo(securityContextId), incident);

		putPoleObjectsRequest.getHeader().setClientName(FrameworkServiceConstants.BUSINESS_SERVICES_CLIENT_NAME);

		PutPoleObjectsResponseDto poleObjectsResponse = poleBusinessServices.putPoleObjects(putPoleObjectsRequest);
		assertNotNull(getBusinessServiceResultInfo(poleObjectsResponse).getTransactionId());
	}
}
