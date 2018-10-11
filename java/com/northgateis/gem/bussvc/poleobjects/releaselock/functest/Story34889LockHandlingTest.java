package com.northgateis.gem.bussvc.poleobjects.releaselock.functest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.junit.Test;

import com.northgateis.gem.bussvc.framework.schema.BusinessServiceResponseInfo;
import com.northgateis.gem.bussvc.framework.test.BusSvcStoryAcceptanceCriteriaReference;
import com.northgateis.gem.bussvc.framework.utils.xml.JaxbXmlMarshaller;
import com.northgateis.gem.bussvc.pole.utils.PoleDtoUtils;
import com.northgateis.gem.bussvc.statecache.functest.AbstractStateCacheFuncTestBase;
import com.northgateis.gem.bussvc.submitcontact.utils.ContactEventPoleDtoUtils;
import com.northgateis.gem.framework.util.logger.GemLogger;
import com.northgateis.gem.statecache.schema.beans.GemMetaData;
import com.northgateis.gem.statecache.schema.beans.RetrieveFromCacheRequest;
import com.northgateis.gem.statecache.schema.beans.RetrieveFromCacheResponse;
import com.northgateis.gem.statecache.schema.beans.State;
import com.northgateis.gem.statecache.schema.beans.StateSearchCriteria;
import com.northgateis.pole.common.PoleNames;
import com.northgateis.pole.schema.ContactEventDto;
import com.northgateis.pole.schema.ContactEventTxDataDto;
import com.northgateis.pole.schema.CreateContactEventTxDataDto;
import com.northgateis.pole.schema.DocumentDto;
import com.northgateis.pole.schema.GetPoleObjectsResponseDto;
import com.northgateis.pole.schema.LinkDto;
import com.northgateis.pole.schema.LockDto;
import com.northgateis.pole.schema.ModificationStatusDto;
import com.northgateis.pole.schema.PoleObjectDto;
import com.northgateis.pole.schema.PutPoleObjectsRequestDto;
import com.northgateis.pole.schema.PutPoleObjectsResponseDto;
import com.northgateis.pole.schema.RecordKeyDto;
import com.northgateis.pole.schema.ReleaseLocksRequestDto;
import com.northgateis.pole.schema.RequestHeaderDto;

/**
 * Functional tests for release locks 
 * 
 * @author ganesh.bhat
 */
public class Story34889LockHandlingTest extends AbstractStateCacheFuncTestBase {

	private static final GemLogger logger = GemLogger.getLogger(Story34889LockHandlingTest.class);
	
	private final static String DOCUMENT_DESCRITPION = "StateCacheFuncTestDocumentDescription";
	private final static String DOCUMENT_NAME = "StateCacheFuncTestDocumentName";
	private final static String MEANINGLESS_TRANSACTION_DESCRIPTION = "blah";
	
	private static final String start = StringEscapeUtils.escapeXml(
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><getPoleObjectsResponse xmlns=\"http://www.northgate-is.com/pole/types\"> ");
	
	private static final String end = StringEscapeUtils.escapeXml(" </getPoleObjectsResponse>");
	
	protected final static String PRIMARY_APP_CLIENT_NAME = "CDASH";
	protected final static String SECONDARY_APP_CLIENT_NAME = "commonsvcs";
	
	private ContactEventDto contactEvent;

	@Override
	protected void setupImpl() throws Exception {
		contactEvent = null;
	}

	@Override
	protected void teardownImpl() throws Exception {
		deleteData();
	}

	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			mingleRef=34889,
			mingleTitle="Bus Svcs - Framework - augment GET and PUT operations with the State Cache for applications running in the Portal.",
			acceptanceCriteriaRefs="1",
			given="I have opened a main record via Business Services which is now displayed in the Portal application for edit.",
			when="I cancel my edit, calling 'release locks'",
			then="The SoD-held state (e.g. a new Risk Assessment) should be emptied (abandoning pending adornment window adornment/overlay data changes).")
	public void testStateCacheClearOnReleaseLockForContactEvent() throws Exception {

		List<? extends PoleObjectDto> poleObjectsDirect = null;
		
		// Create a Simple Contact Event with out documents or any links. This
		// Contact Event is present both in Pole and also Cached.
		// Also class member "contactEvent" holds the reference to this Contact Event.
		createContactEvent();

		// Retrieve the Contact Event which was cached from SOD.
		List<String> statepoleObjectTypes = querySavingOfData();
		
		// Assert that Contact Event was indeed cached
		assertFalse(statepoleObjectTypes.isEmpty());

		GetPoleObjectsResponseDto cachedDataGet = JaxbXmlMarshaller.convertFromXml(statepoleObjectTypes.get(0),
				GetPoleObjectsResponseDto.class);
		List<? extends PoleObjectDto> poleObjects = cachedDataGet.getPoleObjects();
		
		
		assertNotNull(poleObjects);
		// Assert to confirm the instance is of ContactEvent.
		assertTrue("Contact Event should have been returned in the response",
				poleObjects.get(0) instanceof ContactEventDto);		

		// Get the Contact Event directly from pole.
		GetPoleObjectsResponseDto getPoleObjectsResponseDto = getFromPole(poleDirect, contactEvent.getObjectRef(),
				PoleNames.CONTACT_EVENT, PRIMARY_APP_CLIENT_NAME, MEANINGLESS_TRANSACTION_DESCRIPTION);
		poleObjectsDirect = getPoleObjectsResponseDto.getPoleObjects();

		assertNotNull(poleObjectsDirect);
		// Assert to check Contact Event also present in the pole.
		assertTrue("Contact Event should have been returned in the response",
				poleObjectsDirect.get(0) instanceof ContactEventDto);
		
		// Release the lock now
		ReleaseLocksRequestDto releaseLockReq = createReleaseLockExchange(contactEvent.getEntityType(),
				contactEvent.getObjectRef(), PRIMARY_APP_CLIENT_NAME, MEANINGLESS_TRANSACTION_DESCRIPTION, securityContextId);// THE KEY BIT! "forward" circumvents the cache clearing.
		poleBusinessServices.releaseLocks(releaseLockReq);

		// After Release Locks, Query "State Cache" again to check the contact
		// event which was cached is cleared.
		statepoleObjectTypes = querySavingOfData();
		assertTrue(statepoleObjectTypes.isEmpty());

		// Again get the Contact Event directly from pole.
		getPoleObjectsResponseDto = getFromPole(poleDirect, contactEvent.getObjectRef(), PoleNames.CONTACT_EVENT,
				PRIMARY_APP_CLIENT_NAME, MEANINGLESS_TRANSACTION_DESCRIPTION);
		poleObjectsDirect = getPoleObjectsResponseDto.getPoleObjects();

		// Check CE still exists in the pole despite of the release lock
		assertNotNull(poleObjectsDirect);
		assertTrue("Contact Event should have been returned in the response",
				poleObjectsDirect.get(0) instanceof ContactEventDto);
		assertEquals("There should be only one Contact Event", 1, poleObjectsDirect.size());
		
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			mingleRef=34889,
			mingleTitle="Bus Svcs - Framework - augment GET and PUT operations with the State Cache for applications running in the Portal.",
			acceptanceCriteriaRefs="1",
			given="I have opened a main record via Business Services which is now displayed in the Portal application for edit.",
			when="I cancel my edit, calling 'release locks'",
			then="The SoD-held state (e.g. a new Risk Assessment) should be emptied (abandoning pending adornment window adornment/overlay data changes).")
	public void testStateCacheClearOnReleaseLockForContactEventWithDocuments() throws Exception {

		List<? extends PoleObjectDto> poleObjectsDirect = null;
		
		// Create the contact event in the Pole with document. We had fetched the Contact Event from the Pole and linked the document with it.
		// So Contact Event should be in the pole, however the document is not in the pole but in the State Cache.
 		createContactEventWithDocument();
 	
 		// So now we have the Contact Event with documents, in the State Cache.  
 		// Original Contact Event should be in the pole, however any amendment made to Contact Event or Linked Document will be only in the State Cache
 		// AND not in the pole.
 		// Retrieve the Contact Event with document which was cached from SOD. Which will be values in the List in the Xml format. 
		List<String> statepoleObjectTypes = querySavingOfData();
		String xmlResponseParsableFormat = StringEscapeUtils.unescapeXml(getXmlResponseParsableFormat(statepoleObjectTypes));
		
		GetPoleObjectsResponseDto cachedDataGet = JaxbXmlMarshaller.convertFromXml(xmlResponseParsableFormat, GetPoleObjectsResponseDto.class);
		List<? extends PoleObjectDto> poleObjects = cachedDataGet.getPoleObjects();
		
		assertNotNull(poleObjects);		
		assertTrue("Contact Event should have been returned in the response", 
				poleObjects.get(0) instanceof ContactEventDto);		
		assertEquals("There should be only one Contact Event",1, poleObjects.size());
		
		// assert the cache contains a contact event with a linked document
		assertTrue("Indeed Contact Event should have the linked documents",hasLinkedDocument((ContactEventDto) poleObjects.get(0)));
		
		// Get the Contact Event directly from pole. So we expect that this contact event does not have the linked document.
		GetPoleObjectsResponseDto getPoleObjectsResponseDto = getFromPole(poleDirect, contactEvent.getObjectRef(), PoleNames.CONTACT_EVENT, PRIMARY_APP_CLIENT_NAME, MEANINGLESS_TRANSACTION_DESCRIPTION);
		poleObjectsDirect = getPoleObjectsResponseDto.getPoleObjects();

		//assert the response does not have any linked documents
		assertTrue("Contact Event should have been returned in the response", 
				poleObjectsDirect.get(0) instanceof ContactEventDto);
		assertEquals("There should be only one Contact Event",1, poleObjectsDirect.size());
		assertNotNull(poleObjectsDirect);
		assertFalse("Contact Event should not have any linked documents", 
				hasLinkedDocument((ContactEventDto) poleObjectsDirect.get(0)));

		// Release the lock now
		ReleaseLocksRequestDto releaseLockReq = createReleaseLockExchange(
				contactEvent.getEntityType(), contactEvent.getObjectRef(), "CDASH", MEANINGLESS_TRANSACTION_DESCRIPTION,securityContextId);// THE KEY BIT! "forward" circumvents the cache clearing.
		poleBusinessServices.releaseLocks(releaseLockReq);
		//	After Release Locks, Query "State Cache" again to check the contact event which was cached is cleared.		
		statepoleObjectTypes = querySavingOfData();
		assertTrue(statepoleObjectTypes.isEmpty());
		
		getPoleObjectsResponseDto = getFromPole(poleDirect, contactEvent.getObjectRef(), PoleNames.CONTACT_EVENT, PRIMARY_APP_CLIENT_NAME, MEANINGLESS_TRANSACTION_DESCRIPTION);
		poleObjectsDirect = getPoleObjectsResponseDto.getPoleObjects();
		
		//assert the response does not have any linked documents
		assertTrue("Contact Event should have been returned in the response", 
				poleObjectsDirect.get(0) instanceof ContactEventDto);
		assertEquals("There should be only one Contact Event",1, poleObjectsDirect.size());
		assertNotNull(poleObjectsDirect);
		assertFalse("Contact Event should not have any linked documents", 
				hasLinkedDocument((ContactEventDto) poleObjectsDirect.get(0)));
	}
	
	// ******************** Helper methods ********************//
	private boolean hasLinkedDocument(ContactEventDto gemCase) {
		for (LinkDto link : gemCase.getLinks()) {
			if (PoleNames.DOCUMENT.equals(link.getToPoleObjectType())
					|| (link.getToPoleObject() != null && link.getToPoleObject() instanceof DocumentDto)) {
				return true;
			}
		}
		return false;
	}
	
	/*
	 * This function manipulates the xml to parsable format
	 */
	private String getXmlResponseParsableFormat(List<String> statepoleObjectTypes) {

		String xmlResponseParsableFormat = null;
		for (String xmlValue : statepoleObjectTypes) {
			if (xmlValue != null && xmlValue.indexOf("getPoleObjectsResponse") == -1) {
				xmlResponseParsableFormat = start + xmlValue + end;
			}
		}
		return xmlResponseParsableFormat;
	}

	/* Helper method to Query Saving Of Data */
	private List<String> querySavingOfData() {

		List<String> statepoleObjectTypes = new ArrayList<String>();
		RetrieveFromCacheRequest request = new RetrieveFromCacheRequest();
		request.setGemMetaData(new GemMetaData());
		request.getGemMetaData().setSourceSystem("BusSvcs");
		request.getGemMetaData().setSecurityContextId(securityContextId);
		StateSearchCriteria stateSearchCriteria = new StateSearchCriteria();
		stateSearchCriteria.setSessionId(securityContextId);
		request.getStateSearchCriteria().add(stateSearchCriteria);

		RetrieveFromCacheResponse retrieveFromCacheResponse = savingOfDataService.retrieveFromCache(request);

		List<State> states = retrieveFromCacheResponse.getState();

		if (retrieveFromCacheResponse != null) {
			for (State state : states) {
				statepoleObjectTypes.add(state.getOpaqueState());
			}
		}
		return statepoleObjectTypes;
	}

	/* Helper method to create document */
	private DocumentDto createDocument() {
		DocumentDto document = new DocumentDto();
		document.setObjectRef(-1);
		document.setDescription(DOCUMENT_DESCRITPION);
		document.setName(DOCUMENT_NAME);
		return document;
	}

	/* Helper method to delete the data from pole */
	private void deleteData() throws Exception {

		try {
			if (contactEvent != null) {
				contils.retrieveContactFromPole(contactEvent.getObjectRef(), 2, poleDirect, securityContextId,
						true);
			}
		} catch (Exception e) {
			logger.debug("Failed to delete record", e);
		}
	}

	/* Helper method to Create Contact Event without documents */
	private void createContactEvent() throws Exception {

		// Create the simple plain contact event as a Java Object
		ContactEventDto ceDtoOrig = contils.createSimpleContactEvent(null, null);

		ceDtoOrig.setContactEventTxData(new ContactEventTxDataDto());
		ceDtoOrig.getContactEventTxData().setModificationStatus(ModificationStatusDto.CREATE);
		ceDtoOrig.getContactEventTxData().setCreateContactEventTxData(new CreateContactEventTxDataDto());
		ceDtoOrig.getContactEventTxData().getCreateContactEventTxData().setModificationStatus(ModificationStatusDto.CREATE);
		ceDtoOrig.getContactEventTxData().getCreateContactEventTxData().setSubmittingOfficerId(officerReportingId);
		ceDtoOrig.getContactEventTxData().getCreateContactEventTxData().setSubmittingOfficerDisplayValue(String.valueOf(officerReportingId) + " Allford (Ch.Insp.)");
		
		// Put the Contact Event in the pole as primary app
		PutPoleObjectsRequestDto req = new PutPoleObjectsRequestDto();
		req.addPoleObject(ceDtoOrig);
		setHeaderOnRequest(req, PRIMARY_APP_CLIENT_NAME, MEANINGLESS_TRANSACTION_DESCRIPTION);
		PutPoleObjectsResponseDto resp = poleBusinessServices.putPoleObjects(req);

		assertNotNull(getBusinessServiceResultInfo(resp).getTransactionId());
		BusinessServiceResponseInfo busSvcResponseInfo = PoleDtoUtils.extractBusinessServiceResultInfo(resp);
		assertTrue(busSvcResponseInfo.isCompleted());

		Integer ceObjRef = ContactEventPoleDtoUtils.extractContactEventObjectRef(resp);

		// Get the Contact Event from the pole and hold this in the Class
		// Variable "contactEvent" which can be used in the test method.
		// Here the contact event will get Cached
		GetPoleObjectsResponseDto response = getFromPole(poleBusinessServices, ceObjRef, PoleNames.CONTACT_EVENT,
				PRIMARY_APP_CLIENT_NAME, MEANINGLESS_TRANSACTION_DESCRIPTION);
		contactEvent = (ContactEventDto) response.getPoleObjects().get(0);

		assertEquals(contactEvent.getObjectRef(), ceObjRef);
	}
	
	/* Helper method to Create Contact Event with documents */
	private void createContactEventWithDocument() throws Exception {

		createContactEvent();

		// Contact Event is already created as primary App in the pole.
		// Next we move to create the documents.
		DocumentDto document = createDocument();
		document.setModificationStatus(ModificationStatusDto.CREATE);
		LinkDto link = new LinkDto();
		link.setModificationStatus(ModificationStatusDto.CREATE);
		link.setToPoleObject(document);
		link.setLinkReason("DOCUMENT PRODUCTION");
		link.setSourcePoleObjectRef(contactEvent.getObjectRef());
		link.setSourcePoleObjectType(PoleNames.CONTACT_EVENT);

		// Link the document with previously created Contact Event
		contactEvent.addLink(link);

		// attempt to add the document, but it will be stored in the state cache
		// not Pole
		PutPoleObjectsResponseDto putCaseAsSecondardAppResponse = putPoleObject(contactEvent,
				SECONDARY_APP_CLIENT_NAME);
		assertTrue(
				"Because the document was put by a secondary app it should not have been created in Pole and so no entity key should have been returned",
				putCaseAsSecondardAppResponse.getEntityKeys().isEmpty());
	}

	/* Helper method to Create Release Lock */
	private ReleaseLocksRequestDto createReleaseLockExchange(String objectType, Integer objectRef, String clientName,
			String txDescription, String securityContextId) throws Exception {
		ReleaseLocksRequestDto releaseLocksRequest = new ReleaseLocksRequestDto();
		LockDto lock = new LockDto();
		RecordKeyDto recordKey = new RecordKeyDto();
		recordKey.setRecordType(objectType);
		recordKey.setPk(String.valueOf(objectRef));
		lock.addRecordKey(recordKey);
		releaseLocksRequest.addLock(lock);
		RequestHeaderDto header = new RequestHeaderDto();
		header.setPasswordHash(securityContextId);
		header.setClientName(clientName);
		header.setTransactionDescription(txDescription);
		releaseLocksRequest.setHeader(header);
		return releaseLocksRequest;
	}
}