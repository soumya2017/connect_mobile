package com.northgateis.gem.bussvc.submitcontact.functest;

import static com.northgateis.gem.bussvc.pole.utils.PoleDtoUtils.extractBusinessServiceResultInfo;
import static com.northgateis.gem.bussvc.pole.utils.PoleDtoUtils.extractPoleObject;
import static com.northgateis.gem.bussvc.submitcontact.utils.ContactEventPoleDtoUtils.extractContactEventObjectRef;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;

import org.junit.Test;

import com.northgateis.gem.bussvc.framework.test.BusSvcStoryAcceptanceCriteriaReference;
import com.northgateis.gem.bussvc.test.util.ContactEventTestUtils;
import com.northgateis.pole.common.InvalidDataException;
import com.northgateis.pole.common.PoleNames;
import com.northgateis.pole.schema.ContactEventDto;
import com.northgateis.pole.schema.ExternalReferenceDto;
import com.northgateis.pole.schema.PutPoleObjectsRequestDto;
import com.northgateis.pole.schema.PutPoleObjectsResponseDto;

/**
 * Mingle-referenced functional tests for Story #31906. After creating Contact records in POLE,
 * the test retrieves and removes the Contact, for examination / assertions.
 * 
 * @author dan.allford & vilin.patil
 */
public class Story31906CreateSimpleContactFuncTest extends AbstractSubmitContactFuncTestBase {

	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			mingleRef=31906,
			mingleTitle="Bus Svcs - Submit Contact - create simple Contact over SOAP web service",
			acceptanceCriteriaRefs="1",
			given="I have prepared new Contact data in my application",
			when="I submit that to Business Services",
			then="The Contact will be saved in the POLE data store")	
	public void testCreateContactFromWebServiceIsPersistedToPole() throws Exception {
		String externalRef = PE_REF_PREFIX + ":" + new Date();
		
		PutPoleObjectsRequestDto req = contils.createPutPoleObjectsRequest(
				ContactEventTestUtils.createBusinessServiceInfo(securityContextId), contils.createContactEventDto(externalRef, peAccountRef));
		
		PutPoleObjectsResponseDto resp = poleBusinessServices.putPoleObjects(req);
		assertTrue(extractBusinessServiceResultInfo(resp).isCompleted());
		assertNotNull(extractBusinessServiceResultInfo(resp).getTransactionId());
		assertNotNull(extractContactEventObjectRef(resp));
		
		//Deliberately leave this one in the database: do not delete it. Healthy ContactEvent useful for UI-based testing. 
		ContactEventDto contactToCheck = contils.retrieveContactFromPole(extractContactEventObjectRef(resp), 5, poleDirect, securityContextId, false);
		contils.checkContact(contactToCheck, externalRef, false, false, null, null, false);
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			mingleRef=31906,
			mingleTitle="Bus Svcs - Submit Contact - create simple Contact over SOAP web service",
			acceptanceCriteriaRefs="2",
			given="I have prepared new Contact data in my application that would usually cause a validation or constraint error if calling putPoleObjectsRequest directly",
			when="I submit that to Business Services",
			then="The Contact will NOT be saved in the POLE data store")	
	public void testCreateContactFromWebServiceWithInvalidPoleDataIsNotPersistedToPole() throws Exception {
		
		PutPoleObjectsRequestDto req = contils.createPutPoleObjectsRequest(
				ContactEventTestUtils.createBusinessServiceInfo(securityContextId), contils.createContactMissingPublicEngagementRef());
				
		//This is sure to upset POLE:
		extractPoleObject(req, ContactEventDto.class, PoleNames.CONTACT_EVENT).addExternalReference(new ExternalReferenceDto("wibble_type", "wibble_value", "wibble_system"));
		try {
			poleBusinessServices.putPoleObjects(req);
			fail("Sending Invalid data to Pole should have resulted in an InvalidDataException but didn't");
		} catch (InvalidDataException ide) {
			// we're expecting an invalid data exception so this is the correct code path to take
		} catch (Exception e) {
			throw new AssertionError(
					"Sending Invalid data to Pole should have resulted in an InvalidDataException but instead resulted in an " + e.getClass().getSimpleName(), e);
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			mingleRef=31906,
			mingleTitle="Bus Svcs - Submit Contact - create simple Contact over SOAP web service",
			acceptanceCriteriaRefs="3",
			given="I have prepared new Contact data in my application that would usually cause a validation or constraint error if calling putPoleObjectsRequest directly",
			when="I submit that to Business Services",
			then="A SOAP Fault or other error will be returned")	
	public void testCreateContactFromWebServiceWithInvalidPoleDataResultsInErrorReturnedToClient() throws Exception {
		
		PutPoleObjectsRequestDto req = contils.createPutPoleObjectsRequest(
				ContactEventTestUtils.createBusinessServiceInfo(securityContextId), contils.createContactMissingPublicEngagementRef());
				
		//This is sure to upset POLE:
		extractPoleObject(req, ContactEventDto.class, PoleNames.CONTACT_EVENT).addExternalReference(new ExternalReferenceDto("wibble_type", "wibble_value", "wibble_system"));
		try {
			poleBusinessServices.putPoleObjects(req);
			fail("Sending Invalid data to Pole should have resulted in an InvalidDataException but didn't");
		} catch (InvalidDataException ide) {
			// we're expecting an invalid data exception so this is the correct code path to take
		} catch (Exception e) {
			throw new AssertionError(
					"Sending Invalid data to Pole should have resulted in an InvalidDataException but instead resulted in an " + e.getClass().getSimpleName(), e);
		}
	}

		
		
}