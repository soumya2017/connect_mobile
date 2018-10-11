package com.northgateis.gem.bussvc.poleobjects.functest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.Test;

import com.northgateis.gem.bussvc.framework.test.BusSvcStoryAcceptanceCriteriaReference;
import com.northgateis.pole.common.InvalidDataException;
import com.northgateis.pole.schema.ExternalReferenceDto;
import com.northgateis.pole.schema.IncidentDto;
import com.northgateis.pole.schema.PutPoleObjectsRequestDto;
import com.northgateis.pole.schema.PutPoleObjectsResponseDto;

/**
 * Mingle-referenced functional tests for Story #31906. After creating Incident records in POLE,
 * the test retrieves and removes the Incident, for examination / assertions.
 * 
 * TODO CODE REVIEW Delete this class. Title / intent / actual tests are all contradictory and
 * duplicate code elsewhere.
 * 
 * @author dan.allford & vilin.patil
 */
public class Story33383UpdateContactFromPublicEngagementGetExistingRecordFuncTest 
	extends AbstractPoleObjectsFuncTestBase {
	
	/**
	 * Overriding to provide more roles - needed for updating ContactEvents.
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
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			mingleRef=33383,
			mingleTitle="Bus Svcs - Public Engagement - update Contact from Public Engagement (add a 'note')",
			acceptanceCriteriaRefs="UNKNOWN",
			given="I have prepared new Incident data in my application",
			when="I submit that to Business Services",
			then="The Incident will be saved in the POLE data store")	
	public void testCreateIncidentFromWebServiceIsPersistedToPole() throws Exception {
		
		PutPoleObjectsRequestDto req = utils.createPutPoleObjectsRequestDto(
				utils.createBusinessServiceInfo(securityContextId), utils.createIncident(new Integer(-1), officerReportingId));
		
		PutPoleObjectsResponseDto resp = poleBusinessServices.putPoleObjects(req);
		assertNotNull(getBusinessServiceResultInfo(resp).getTransactionId());
		assertNotNull(getIncidentObjectRef(resp));
		
		IncidentDto incidentToCheck = utils.removeIncidentFromPole(getIncidentObjectRef(resp), 5, poleDirect, securityContextId);
		utils.checkIncident(incidentToCheck, false);
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			mingleRef=33383,
			mingleTitle="Bus Svcs - Public Engagement - update Contact from Public Engagement (add a 'note')",
			acceptanceCriteriaRefs="UNKNOWN",
			given="I have prepared new Incident data in my application that would usually cause a validation or constraint error if calling putPoleObjectsRequest directly",
			when="I submit that to Business Services",
			then="The Incident will NOT be saved in the POLE data store")	
	public void testCreateIncidentFromWebServiceWithInvalidPoleDataIsNotPersistedToPole() throws Exception {
		
		PutPoleObjectsRequestDto req = utils.createPutPoleObjectsRequestDto(
				utils.createBusinessServiceInfo(securityContextId), utils.createIncident());
				
		//This is sure to upset POLE:
		req.getPoleObjects().get(0).addExternalReference(new ExternalReferenceDto("wibble_type", "wibble_value", "wibble_system"));		
		
		try {
			poleBusinessServices.putPoleObjects(req);
			fail("Sending Invalid data to Pole should have resulted in an InvalidDataException but didn't");
		} catch (InvalidDataException ide) {
			// we're expecting an invalid data exception so this is the correct code path to take
		} catch (Exception e) {
			fail("Sending Invalid data to Pole should have resulted in an InvalidDataException but instead resulted in an " + e.getClass().getSimpleName());
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			mingleRef=33383,
			mingleTitle="Bus Svcs - Public Engagement - update Contact from Public Engagement (add a 'note')",
			acceptanceCriteriaRefs="UNKNOWN",
			given="I have prepared new Incident data in my application that would usually cause a validation or constraint error if calling putPoleObjectsRequest directly",
			when="I submit that to Business Services",
			then="A SOAP Fault or other error will be returned")	
	public void testGetIncidentUsingInvalidPoleCriteriaResultsInErrorReturnedToClient() throws Exception {
		
		PutPoleObjectsRequestDto req = utils.createPutPoleObjectsRequestDto(
				utils.createBusinessServiceInfo(securityContextId), utils.createIncident());
				
		//This is sure to upset POLE:
		getIncident(req).addExternalReference(new ExternalReferenceDto("wibble_type", "wibble_value", "wibble_system"));	
		try {
			poleBusinessServices.putPoleObjects(req);
			fail("Sending Invalid data to Pole should have resulted in an InvalidDataException but didn't");
		} catch (InvalidDataException ide) {
			// we're expecting an invalid data exception so this is the correct code path to take
		} catch (Exception e) {
			fail("Sending Invalid data to Pole should have resulted in an InvalidDataException but instead resulted in an " + e.getClass().getSimpleName());
		}
	}	
}