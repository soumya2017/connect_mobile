package com.northgateis.gem.bussvc.submitincident.functest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.northgateis.gem.bussvc.framework.test.BusSvcStoryAcceptanceCriteriaReference;
import com.northgateis.gem.bussvc.poleobjects.functest.AbstractPoleObjectLocationFuncTestBase;
import com.northgateis.gem.bussvc.submitincident.constants.SubmitIncidentServiceConstants;
import com.northgateis.gem.bussvc.test.util.BusinessServicesTestUtils;
import com.northgateis.gem.bussvc.test.util.CommonPoleObjectTestUtils;
import com.northgateis.pole.schema.EventDto;
import com.northgateis.pole.schema.IncidentDto;
import com.northgateis.pole.schema.PoleObjectDto;
import com.northgateis.pole.schema.PutPoleObjectsRequestDto;
import com.northgateis.pole.schema.PutPoleObjectsResponseDto;

/**
 * Functional tests for locations with UPRN linked to an Incident
 */
public class CreateIncidentLocationUprnDcFuncTest extends AbstractPoleObjectLocationFuncTestBase {
	
	@Override
	protected void teardownImpl() throws Exception {
		if (targetPoleObject != null) {
			try {
				utils.removeIncidentFromPole(targetPoleObject.getObjectRef(), 
					10, poleDirect, securityContextId);
			} catch (Exception ex) {
				logger.warn("Failing to delete target Incident :" + ex.getMessage());
			}
			targetPoleObject = null;
		}
		if (preExistingPoleObject != null) {
			try {
				utils.removeIncidentFromPole(preExistingPoleObject.getObjectRef(), 
					10, poleDirect, securityContextId);
			} catch (Exception ex) {
				logger.warn("Failing to delete pre-existing Incident :" + ex.getMessage());
			}
			preExistingPoleObject = null;
		}
		super.teardownImpl();
	}

	@Override
	protected EventDto createMainEventDto() {
		return utils.createIncident(new Integer(-1), officerReportingId);
	}
	
	@Override
	protected EventDto createMainEventInPoleDirect(PoleObjectDto poleObject) throws Exception {
		PutPoleObjectsRequestDto ppoRequest =  utils.createPutPoleObjectsRequestDto(
				BusinessServicesTestUtils.createBusinessServiceInfo(securityContextId), (IncidentDto) poleObject);
		
		PutPoleObjectsResponseDto ppoResponse = poleDirect.putPoleObjects(ppoRequest);
		Integer incidentObjectRef = ppoResponse.getObjectRefs().get(0);
		
		return utils.getIncidentFromPole(incidentObjectRef, poleDirect, securityContextId);
	}
	
	@Override
	protected EventDto createMainEventInBusinessServices(PoleObjectDto poleObject) throws Exception {
		PutPoleObjectsRequestDto ppoRequest =  utils.createPutPoleObjectsRequestDto(
				BusinessServicesTestUtils.createBusinessServiceInfo(securityContextId), (IncidentDto) poleObject);
		
		PutPoleObjectsResponseDto ppoResponse = poleBusinessServices.putPoleObjects(ppoRequest);
		Integer incidentObjectRef = ppoResponse.getObjectRefs().get(0);
		
		return utils.getIncidentFromPole(incidentObjectRef, poleDirect, securityContextId);
	}

	@Override
	protected String getLocationLinkReason() {
		return SubmitIncidentServiceConstants.INCIDENT_LOCATION_LINK_REASON;
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		mingleRef=37959,//DEFECT
		mingleTitle="Getting error while trying to link a location having UPRN with Intel Report.",
		acceptanceCriteriaRefs="",
		given="I have QAS-obtained Location-data along with Incident details"
				+ "AND no existing researched Location data for the same UPRN exists in POLE"
				+ "AND no existing Location data for the same UPRN exists in Compass"
				+ "AND the new Location data is marked as UNRESEARCHED",
		when="The data is submitted to Business Services Create Incident service",
		then="A new Location record will be created in POLE"
				+ "AND the overall transaction will succeed"
				+ "AND the new Location will be marked as UNRESEARCHED"
				+ "AND the new Location will retain the submitted (possibly invalid) UPRN"
					+ "for eventual manual researching/user attention."
	)
	public void testUnresearchedLocationUnmatchedByUprnDoesNotFailTransaction() throws Exception {

		preExistingLocationUprn = "123456789012";//invalid
		
		doTestLocationMatch(preExistingLocationUprn, false);
		
		assertEquals("Expect uprn of both locations to be the same", preExistingLocationUprn,
				CommonPoleObjectTestUtils.extractUprnFromLocation(targetLocation));
		assertFalse("Expect Location to be the newly saved unresearched record", 
					targetLocation.getResearched());
		
		checkLinkResearchedComparedToObject(targetLocationLink, targetLocation);
	}
}
