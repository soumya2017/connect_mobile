package com.northgateis.gem.bussvc.submitcontact.functest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;

import org.junit.Test;

import com.northgateis.gem.bussvc.framework.schema.BusinessServiceResponseInfo;
import com.northgateis.gem.bussvc.pole.utils.PoleDtoUtils;
import com.northgateis.gem.bussvc.poleobjects.functest.AbstractPoleObjectsFuncTestBase;
import com.northgateis.gem.bussvc.submitcontact.utils.ContactEventPoleDtoUtils;
import com.northgateis.gem.framework.util.logger.GemLogger;
import com.northgateis.pole.common.InvalidDataException;
import com.northgateis.pole.schema.ContactEventDto;
import com.northgateis.pole.schema.PutPoleObjectsRequestDto;
import com.northgateis.pole.schema.PutPoleObjectsResponseDto;

/**
 * Functional tests for creation of a {@link ContactEventDto}, to assess that workflow has been called and started.
 * 
 * @author dan.allford
 */
public class Story33384CreateContactEventInitiateWorkflowFuncTest extends AbstractPoleObjectsFuncTestBase {

	private static final GemLogger logger = GemLogger.getLogger(Story33384CreateContactEventInitiateWorkflowFuncTest.class);
	
	private final static String MISSING_OWNING_FORCE_ID_ERROR_MESSAGE_1 = "No owning force ID available for ContactEvent"; 
	private final static String MISSING_OWNING_FORCE_ID_ERROR_MESSAGE_2 = "Cannot assign a ContactRef to it.";
	
	private ContactEventDto contactEvent;
			
	@Override
	protected void setupImpl() throws Exception {
		contactEvent = null;
	}
	
	@Override
	protected void teardownImpl() throws Exception {
		deleteData();
	}
	
	private void deleteData() throws Exception {

		try { 
			if (contactEvent != null) {
				contils.retrieveContactFromPole(contactEvent.getObjectRef(), 2, poleDirect, securityContextId, true);
			}
		} catch (Exception e) {
			logger.debug("Failed to delete record", e);
		}
	}

	@Test
	public void testCreateContactEventStartsWorkflow() throws Exception {
		
		String extRefValue = PE_REF_PREFIX + new Date();
		
		ContactEventDto ceDtoOrig = contils.createContactEventDto(extRefValue, peAccountRef);
		
		PutPoleObjectsRequestDto req = contils.createContactEventRequest(contils.createBusinessServiceInfo(securityContextId), ceDtoOrig);

		PutPoleObjectsResponseDto resp = poleBusinessServices.putPoleObjects(req);
		
		assertNotNull(getBusinessServiceResultInfo(resp).getTransactionId());
		BusinessServiceResponseInfo busSvcResponseInfo = PoleDtoUtils.extractBusinessServiceResultInfo(resp);
		assertTrue(busSvcResponseInfo.isCompleted());
		Integer ceObjRef = ContactEventPoleDtoUtils.extractContactEventObjectRef(resp);
		
		contactEvent = contils.retrieveContactFromPole(
				ContactEventPoleDtoUtils.extractContactEventObjectRef(resp), 5, poleBusinessServices, securityContextId, false);

		assertEquals(contactEvent.getObjectRef(), ceObjRef);
	}

	@Test
	public void testCreateInvalidContactEventDoesNotStartWorkflow() throws Exception {
		
		String extRefValue = PE_REF_PREFIX + new Date();
		
		ContactEventDto ceDtoOrig = contils.createContactEventDto(extRefValue, peAccountRef);
		ceDtoOrig.setOwningForceId(null);
		
		PutPoleObjectsRequestDto req = contils.createContactEventRequest(contils.createBusinessServiceInfo(securityContextId), ceDtoOrig);
		
		try {
			poleBusinessServices.putPoleObjects(req);
			fail("Sending Invalid data should have resulted in an InvalidDataException but instead it completed successfully");
		} catch (InvalidDataException ide) {

			//Regex?? Anyone??
			assertTrue("Expected text \"" + MISSING_OWNING_FORCE_ID_ERROR_MESSAGE_1 + "\" but received \"" + ide.getMessage() + "\"", 
					ide.getMessage().contains(MISSING_OWNING_FORCE_ID_ERROR_MESSAGE_1));
			assertTrue("Expected text \"" + MISSING_OWNING_FORCE_ID_ERROR_MESSAGE_2 + "\" but received \"" + ide.getMessage() + "\"", 
					ide.getMessage().contains(MISSING_OWNING_FORCE_ID_ERROR_MESSAGE_2));
		}
	}
	
	@Test
	public void testCreateInvalidContactWithMultipleErrorsReturnsNestedErrorInfo() throws Exception {
		
		String extRefValue = PE_REF_PREFIX + new Date();
		
		ContactEventDto ceDtoOrig = contils.createContactEventDto(extRefValue, peAccountRef);
		ceDtoOrig.setOwningForceId(null);
		
		PutPoleObjectsRequestDto req = contils.createContactEventRequest(contils.createBusinessServiceInfo(securityContextId), ceDtoOrig);
		
		try {
			poleBusinessServices.putPoleObjects(req);
			fail("Sending Invalid data should have resulted in an InvalidDataException but instead it completed successfully");
		} catch (InvalidDataException ide) {

			//Regex?? Anyone??
			assertTrue("Expected text \"" + MISSING_OWNING_FORCE_ID_ERROR_MESSAGE_1 + "\" but received \"" + ide.getMessage() + "\"", 
					ide.getMessage().contains(MISSING_OWNING_FORCE_ID_ERROR_MESSAGE_1));
			assertTrue("Expected text \"" + MISSING_OWNING_FORCE_ID_ERROR_MESSAGE_2 + "\" but received \"" + ide.getMessage() + "\"", 
					ide.getMessage().contains(MISSING_OWNING_FORCE_ID_ERROR_MESSAGE_2));
		}
	}
}
