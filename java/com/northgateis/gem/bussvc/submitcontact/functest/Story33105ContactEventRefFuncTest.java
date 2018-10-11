package com.northgateis.gem.bussvc.submitcontact.functest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Test;

import com.northgateis.gem.bussvc.framework.test.BusSvcStoryAcceptanceCriteriaReference;
import com.northgateis.pole.schema.ContactEventDto;
import com.northgateis.pole.schema.PutPoleObjectsRequestDto;
import com.northgateis.pole.schema.PutPoleObjectsResponseDto;

/**
 *  Class to check whether the Contact Event Reference number is assigned to the Contact Event
 */
public class Story33105ContactEventRefFuncTest extends AbstractSubmitContactFuncTestBase {
		
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			mingleRef = 33105,
			mingleTitle = "Bus Svcs - Contact Events - allocate a Contact Event Ref for a new Contact Event",
			acceptanceCriteriaRefs = "1,1.2,1.3",
			given = "I (as an Integrated system) have prepared the data to create Contact event.",
			when = "The CreateContactEvent service is called", 
			then = "The contact event will be created in POLE "
					+ "AND Contact Ref will contain the [currently default] Force ID "
					+ "AND Contact Ref will contain the 2digit year")
	public void testCreateContactFromWebServiceIsPersistedToPoleWith() throws Exception {
		String externalRefValue = "BusSvcTest:" + new Date();
		
		PutPoleObjectsRequestDto req = contils.createPutPoleObjectsRequest(
				utils.createBusinessServiceInfo(securityContextId), contils.createContactEventDto(externalRefValue, peAccountRef));
		
		PutPoleObjectsResponseDto resp = poleBusinessServices.putPoleObjects(req);		
		
		ContactEventDto createdContact = contils.retrieveContactFromPole(contils.getContactObjectRef(resp), 5, poleDirect, securityContextId, true);
		contils.checkContact(createdContact, externalRefValue, false, false, null, null, false);
		
		assertNotNull(createdContact);//sanity check		
		assertNotNull(createdContact.getContactEventReference());//sanity check
	    
		assertEquals(contils.getForceId(), createdContact.getForceId());

		String actualTwoDigitYear = createdContact.getContactEventReference().substring(createdContact.getContactEventReference().length() - 2);
		String expectedTwoDigitYear = Integer.toString(new GregorianCalendar().get(Calendar.YEAR) - 2000);
		
		assertEquals(expectedTwoDigitYear, actualTwoDigitYear);	
	}
}
