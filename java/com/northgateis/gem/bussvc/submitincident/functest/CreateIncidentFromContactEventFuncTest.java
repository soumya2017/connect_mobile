package com.northgateis.gem.bussvc.submitincident.functest;

import static org.junit.Assert.assertEquals;

import com.northgateis.gem.bussvc.poleobjects.constants.PoleObjectsServiceConstants;
import com.northgateis.gem.bussvc.submitcontact.functest.AbstractCreateEventFromContactEventFuncTest;
import com.northgateis.gem.bussvc.submitincident.constants.SubmitIncidentServiceConstants;
import com.northgateis.gem.bussvc.submitincident.utils.IncidentPoleDtoUtils;
import com.northgateis.pole.schema.ContactEventDto;
import com.northgateis.pole.schema.IncidentDto;
import com.northgateis.pole.schema.LinkDto;
import com.northgateis.pole.schema.PeContactInfoDto;
import com.northgateis.pole.schema.PutPoleObjectsRequestDto;
import com.northgateis.pole.schema.PutPoleObjectsResponseDto;

/**
 * End-to-end functional tests for Create {@link IncidentDto} from {@link ContactEventDto}
 * operation. This includes tests for  transfer of link data e.g. {@link PeContactInfoDto} and 
 * handling of attempted 'illegal links' from the client - where we resolve with a deep copy (#35885).
 * 
 * See also {@link CreateIncidentFromContactEventWithReporterAsVictimFuncTest} which runs the SAME SET 
 * OF TESTS AS THIS CLASS but with the added 'salt' of PERSON REPORTING and VICTIM being the same Person.
 * 
 * @author dan.allford
 */
public class CreateIncidentFromContactEventFuncTest extends AbstractCreateEventFromContactEventFuncTest {
	
	@Override
	protected void setupImpl() throws Exception {
		super.setupImpl();
		
		newEventLocationLinkReason = SubmitIncidentServiceConstants.INCIDENT_LOCATION_LINK_REASON;
		newEventPersonReportingLinkReason = PoleObjectsServiceConstants.PERSON_REPORTING_LINK_REASON;
		expectingVictimMigratedToNewEvent = true;
		expectPeContactInfoToMigrate = true;
	}
	
	@Override
	protected void deleteData() throws Exception {
		try { 
			utils.removeIncidentFromPole(newEvent.getObjectRef(), 2, poleDirect, securityContextId); 
		} catch (Exception e) {
			logger.debug("Failed to delete record", e);
		}
	}
	
	@Override
	protected PutPoleObjectsRequestDto prepareCreateEventFromContactEventRequest() {
		newEvent = utils.prepareIncidentWithNoLinks(-1, Integer.valueOf(officerReportingId));		
		PutPoleObjectsRequestDto ppoDto = utils.preparePpoRequestForCreateIncidentFromContact(
				utils.createBusinessServiceInfo(securityContextId), (IncidentDto) newEvent, contactEvent, true);

		for (LinkDto link : newEvent.getLinks()) {
			if (link.getLinkReason().equals(PoleObjectsServiceConstants.VICTIM_LINK_REASON)) {
				link.setPeContactInfoList(null);
				break;
			}
		}

		contactEvent.setLinks(null);//ironically, DC will 're-load' these out of POLE during creation of the Incident

		utils.addWorkflowTxDataForCreateIncident(ppoDto, taskStaffId, taskUnitName);
		
		return ppoDto;
	}
	
	@Override
	protected void createEventFromContactEvent(PutPoleObjectsRequestDto ppoRequest) throws Exception {

		IncidentDto inc = (IncidentDto)(ppoRequest.getPoleObjects().get(0));		
		inc.getIncidentTxData().getCreateIncidentTxData().setSubmittingOfficerId(officerReportingId);
		inc.getIncidentTxData().getCreateIncidentTxData().setSubmittingOfficerDisplayValue("Frank Test");
		
		PutPoleObjectsResponseDto createIncFromContactResponse = poleBusinessServices.putPoleObjects(ppoRequest);
		Integer incObjRef = IncidentPoleDtoUtils.extractIncidentObjectRef(createIncFromContactResponse);
		
		newEvent = utils.getIncidentFromPole(incObjRef, poleDirect, securityContextId);

		assertEquals(newEvent.getObjectRef(), incObjRef);
	}
}
