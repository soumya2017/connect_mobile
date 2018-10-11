package com.northgateis.gem.bussvc.poleobjects.functest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.junit.Ignore;
import org.junit.Test;

import com.northgateis.gem.bussvc.framework.test.BusSvcStoryAcceptanceCriteriaReference;
import com.northgateis.gem.bussvc.submitcontact.utils.ContactEventPoleDtoUtils;
import com.northgateis.pole.schema.ContactEventDto;
import com.northgateis.pole.schema.PutPoleObjectsRequestDto;
import com.northgateis.pole.schema.PutPoleObjectsResponseDto;

/**
 * Mingle-referenced functional tests for Opening a {@link ContactEventDto} over JSON/REST.
 * 
 * TODO #33073 Tests for all error codes.
 * 
 * "On 21 June 2016 at 14:19, Christopher Smith <christopher.smith@northgateps.com> wrote:
 * Hi Dan
 * 
 * These are the defaults we use...
 * 
 * 400 = Bad Request - for incorrect / missing request params etc
 * 500 = Internal Server Error - for general errors that might occur
 * 422 = Data Validation Error - for errors that occur in the data validation layer
 * 401 = Unauthorised
 * 403 = Forbidden
 * 
 * I don't think we actually return a 403 error at the moment given the way we are notified that an object cannot be opened by the state cache / POLE.
 * 
 * Chris"
 * 
 * @author dan.allford
 */
public class Story33073OpenContactInDecisionSupportOverJsonRestFuncTest 
	extends AbstractPoleObjectsFuncTestBase {

	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			mingleRef=33073,
			mingleTitle="Bus Svcs - Contact Events - open Contact in Decision Support (JSON/REST)",
			acceptanceCriteriaRefs="UNKNOWN",
			given="TODO 33073 I have an object ref for a contact event",
			when="TODO 33073 I request that contact event from Business Services over JSON/REST",
			then="TODO 33073 The contact event should be returned in POLE JSON format")	
	public void testOpenContactEventOverJsonRest() throws Exception {
		
		String extRefPrefix = "PUBLIC ENGAGEMENT";
		String extRefValue = extRefPrefix + new Date();
		
		ContactEventDto ceDtoOrig = contils.createContactEventDto(extRefValue, peAccountRef);
		PutPoleObjectsRequestDto req = contils.createContactEventRequest(contils.createBusinessServiceInfo(securityContextId), ceDtoOrig);
		
		//PoleDtoUtils.addExtraInfo(req, "BusSvcWorkflowDataQALinkingUnit", "AMO");
		
		PutPoleObjectsResponseDto resp = poleBusinessServices.putPoleObjects(req);
		assertNotNull(getBusinessServiceResultInfo(resp).getTransactionId());
		
		ContactEventDto ceDto = contils.retrieveContactFromPole(ContactEventPoleDtoUtils.extractContactEventObjectRef(resp), 5, poleDirect, securityContextId, true);

		//Workflow seems to be changing status from "NEW" to "UNPROCESSED"
		//assertEquals(ceDtoOrig.getStatus(), ceDto.getStatus());
		
		assertEquals(ceDtoOrig.getContactSummary(), ceDto.getContactSummary());
		assertNotNull(ceDto.getContactEventReference());//should be set as usual. See story #33105
	}
	
	@Test
	@Ignore // TODO #33076 handle HTTP response codes on the "PUT"
	@BusSvcStoryAcceptanceCriteriaReference(
			mingleRef=33073,
			mingleTitle="Bus Svcs - Contact Events - open Contact in Decision Support (JSON/REST)",
			acceptanceCriteriaRefs="UNKNOWN",
			given="TODO 33073 I have an invalid object ref for a contact event",
			when="TODO 33073 I use the invalid object ref to retrieve a contact event from Business Services over JSON/REST",
			then="TODO 33073 The service should return an HTTP 400 response indicating a bad request")	
	public void testOpenContactEventWithInvalidObjectRefResultsInHttp400BadRequest() throws Exception {
		
		PutPoleObjectsRequestDto req = contils.createContactEventRequest(
				contils.createBusinessServiceInfo(securityContextId), contils.createContactEventDto(null, null));
		
		PutPoleObjectsResponseDto resp = poleBusinessServices.putPoleObjects(req);
		assertNotNull(getBusinessServiceResultInfo(resp).getTransactionId());
		
		contils.retrieveContactFromPole(12345 /* doesn't exist */, 5, poleBusinessServices, securityContextId, true);
	}
		
}