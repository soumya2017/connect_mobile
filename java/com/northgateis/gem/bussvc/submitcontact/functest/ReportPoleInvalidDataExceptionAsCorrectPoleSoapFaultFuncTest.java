package com.northgateis.gem.bussvc.submitcontact.functest;

import static org.junit.Assert.fail;

import org.junit.Test;

import com.northgateis.gem.bussvc.framework.test.BusSvcStoryAcceptanceCriteriaReference;
import com.northgateis.pole.common.InvalidDataException;
import com.northgateis.pole.schema.ExternalReferenceDto;
import com.northgateis.pole.schema.PutPoleObjectsRequestDto;

/**
 * Mingle-referenced functional tests for Story #31906. After creating Incident records in POLE,
 * the test retrieves and removes the Incident, for examination / assertions.
 * 
 * @author dan.allford & vilin.patil
 */
public class ReportPoleInvalidDataExceptionAsCorrectPoleSoapFaultFuncTest 
	extends AbstractSubmitContactFuncTestBase {
	
	/**
	 * POLE validation errors must be passed back to the caller without loss of error/validation detail.
	 * 
	 * @throws Exception
	 */
	@Test	
	@BusSvcStoryAcceptanceCriteriaReference(
		mingleRef=32400,
		mingleTitle="Bus Svcs - Submit Incident - report POLE validation errors to web service client",
		acceptanceCriteriaRefs="1",
		given="I have prepared Incident Data with errors in it that are not being explicitly checked by the Submit Incident web-service",
		when="I call the Submit Incident web-service",
		then="The error from the resulting call to POLE will be returned to me in the same way that errors from the Submit Incident service would be returned.")
	public void testSyncCallWithSoapFaultFromPoleReportedExplicitly() throws Exception {
		
		PutPoleObjectsRequestDto req = contils.createPutPoleObjectsRequest(
				utils.createBusinessServiceInfo(securityContextId), contils.createContactMissingPublicEngagementRef());
				
		//This is sure to upset POLE:
		contils.getContactEvent(req).addExternalReference(new ExternalReferenceDto("wibble_type", "wibble_value", "wibble_system"));
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