package com.northgateis.gem.bussvc.submitcontact.functest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.northgateis.gem.bussvc.dataconsolidation.appliedlogic.DataConsolidationBean;
import com.northgateis.gem.bussvc.dataconsolidation.appliedlogic.commonjobs.DataConsolidationJob;
import com.northgateis.gem.bussvc.framework.schema.BusinessServiceResponseInfo;
import com.northgateis.gem.bussvc.framework.test.BusSvcStoryAcceptanceCriteriaReference;
import com.northgateis.gem.bussvc.pole.metadata.PoleMetadataCacheBean;
import com.northgateis.gem.bussvc.pole.utils.PoleDtoUtils;
import com.northgateis.gem.bussvc.poleobjects.constants.PoleObjectsServiceConstants;
import com.northgateis.gem.bussvc.poleobjects.functest.AbstractPoleObjectsFuncTestBase;
import com.northgateis.gem.bussvc.submitcontact.utils.ContactEventPoleDtoUtils;
import com.northgateis.gem.framework.util.logger.GemLogger;
import com.northgateis.gem.statecache.schema.beans.GemMetaData;
import com.northgateis.gem.statecache.schema.beans.RetrieveFromCacheRequest;
import com.northgateis.gem.statecache.schema.beans.RetrieveFromCacheResponse;
import com.northgateis.gem.statecache.schema.beans.State;
import com.northgateis.gem.statecache.schema.beans.StateSearchCriteria;
import com.northgateis.pole.common.InvalidDataException;
import com.northgateis.pole.common.PoleNames;
import com.northgateis.pole.schema.ContactEventDto;
import com.northgateis.pole.schema.EntityMetadataDto;
import com.northgateis.pole.schema.GetPoleObjectsRequestDto;
import com.northgateis.pole.schema.GetPoleObjectsResponseDto;
import com.northgateis.pole.schema.IntegerCriterionDto;
import com.northgateis.pole.schema.LinkDto;
import com.northgateis.pole.schema.ModificationStatusDto;
import com.northgateis.pole.schema.PoleObjectCriteriaDto;
import com.northgateis.pole.schema.PutPoleObjectsRequestDto;
import com.northgateis.pole.schema.PutPoleObjectsResponseDto;
import com.northgateis.pole.schema.RetrievalTypeDto;
import com.northgateis.pole.schema.VehicleDto;

/**
 * Functional tests for creation of a {@link ContactEventDto}, And Support State cache Integration As of now It is
 * passing client name CDash
 * 
 * @author sanket.khandekar
 */
public class CreateOrUpdateContactFromDecisionSupportFuncTest extends AbstractPoleObjectsFuncTestBase {

	private static final GemLogger logger = GemLogger.getLogger(CreateOrUpdateContactFromDecisionSupportFuncTest.class);
	private final static String MISSING_OWNING_FORCE_ID_ERROR_MESSAGE_1 = "No owning force ID available for ContactEvent"; 
	private final static String MISSING_OWNING_FORCE_ID_ERROR_MESSAGE_2 = "Cannot assign a ContactRef to it.";

	private ContactEventDto contactEvent;
	
	protected PoleMetadataCacheBean poleMetadataCache;
			
	@Override
	protected void setupImpl() throws Exception {
		contactEvent = null;
		createContactTestUtil();
		
		poleMetadataCache = PoleMetadataCacheBean.getInstance();
		if (poleMetadataCache == null) {
			poleMetadataCache = utils.getPoleMetadataCacheAndConstraints(
					poleDirect, poleMetadataDirect, securityContextId, poleUsername, polePassword);
		}
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
	@BusSvcStoryAcceptanceCriteriaReference(
			mingleRef=34558,
			mingleTitle="Bus Svcs - Contact Events - Create Contact Event (JSON REST)  ",
			acceptanceCriteriaRefs="1",
			given="[CR1 - I have filled Contact Event data in Decision Support] , [CR2-The new Contact Event data of CR1 has been submitted] ",
			when="[CR1 - I choose to create a new Contact Event via Save / Done button] , [CR2-It is received by Business Services] ",
			then = "[CR1 - The data for the new Contact Event, any links and any information for linked-to static records,"
				   + "will be submitted to Business Services in POLEish JSON format in a single call.] , [CR2 - The data will be saved in POLE] ")
	public void testCreateContact() throws Exception {
		
		PutPoleObjectsResponseDto resp = createContactTestUtil();
		
		assertNotNull(getBusinessServiceResultInfo(resp).getTransactionId());

		BusinessServiceResponseInfo busSvcResponseInfo = PoleDtoUtils.extractBusinessServiceResultInfo(resp);

		assertTrue(busSvcResponseInfo.isCompleted());

		Integer ceObjRef = ContactEventPoleDtoUtils.extractContactEventObjectRef(resp);

		assertEquals(contactEvent.getObjectRef(), ceObjRef);
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			mingleRef=34560,
			mingleTitle="Bus Svcs - Contact Events - Update Contact Event (JSON REST)",
			acceptanceCriteriaRefs="CR2.3",
			given="invalid data.",
			when="invalid data ",
			then="The resulting error will be communicated back to the client UI (to Decision Support).")
	public void testInvalidData() throws Exception {
		
		ContactEventDto ceDtoOrig = contils.createContactEventDto(peReference, peAccountRef);
		ceDtoOrig.setOwningForceId(null);
		ceDtoOrig.setOfficerReportingId(null);
		
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
	@BusSvcStoryAcceptanceCriteriaReference(
			mingleRef=34560,
			mingleTitle="Bus Svcs - Contact Events - Update Contact Event (JSON REST)",
			acceptanceCriteriaRefs="1",
			given="[CR1 I have opened a Contact Event in Decision Support , CR2 The updated Contact Event data of CR1 has been submitted ]",
			when="[CR1 I choose to modify the data or add extra links to an existing Contact Event ,CR2 It is received by Business Services	]  ",
			then="[CR1 The data for the Updated Contact Event or any links to static records, will be submitted to Business Services in POLEish JSON format in a single call.., CR2 The data will be saved in POLE]")
	public void testUpdateContactEventAddLink() throws Exception {
		
		PutPoleObjectsRequestDto poleObjectsRequestDto = updateContactEventUtil();

		PutPoleObjectsResponseDto putPoleObjectsResponseDto = poleBusinessServices.putPoleObjects(poleObjectsRequestDto);

		ContactEventDto contactEventDto = contils.retrieveContactFromPole(contactEvent.getObjectRef(), 5, poleDirect,
				securityContextId, false);

		assertNotNull(contactEventDto);
		assertNotNull(getBusinessServiceResultInfo(putPoleObjectsResponseDto).getTransactionId());

		List<LinkDto> linkDtos = contactEventDto.getLinks();

		int vehicleLinksFound = 0;
		boolean newVehicleFound = false;
		for (LinkDto linkDto : linkDtos) {

			if (linkDto.getToPoleObject() instanceof VehicleDto
					&& linkDto.getLinkReason().equals(PoleObjectsServiceConstants.VEHICLE_USED_LINK_REASON)) {
				vehicleLinksFound++;
				if (((VehicleDto) linkDto.getToPoleObject()).getRegistrationNumber().equals("XYZ123")) {
					newVehicleFound = true;
				}
			}

		}

		assertEquals(2, vehicleLinksFound);
		assertTrue(newVehicleFound);
	}
	
	/**
	 * Imaginary acceptance criteria for this test. Proves a validator that prevents changing a value
	 * back to null once it has been set.
	 * 
	 * Test introduced to prove that {@link DataConsolidationBean} and {@link DataConsolidationJob}
	 * classes do carefully 'memento' the data retrieved from POLE such that it can be used in a
	 * comparison of this kind.
	 * 
	 * @throws Exception
	 */
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			mingleRef=34560,
			mingleTitle="Bus Svcs - Contact Events - Update Contact Event (JSON REST)",
			acceptanceCriteriaRefs="NA",
			given="I have submitted an update to a Contact Event.",
			when="There is accidental attempt by client code to nullify the CE reference",
			then="The update should fail validation.")
	public void testUpdateContactEventValidationFail() throws Exception {
		
		EntityMetadataDto contactMetadata =
				poleMetadataCache.getPoleMetadataCache().getEntityMetadataForName(PoleNames.CONTACT_EVENT);
		
		utils.prepareMaskForPoleObject(contactEvent, contactMetadata, "ObjectRef", "ContactEventReference");
		contactEvent.setModificationStatus(ModificationStatusDto.UPDATE);
		contactEvent.setContactEventTxData(null);
		contactEvent.setContactEventReference(null);
		
		PutPoleObjectsRequestDto putPoleObjectsRequestDto = contils
				.createPutPoleObjectsRequest(utils.createBusinessServiceInfo(securityContextId), contactEvent);

		try {
			poleBusinessServices.putPoleObjects(putPoleObjectsRequestDto);
			
			fail("Should have failed validation as contact event reference has been set to null");
		} catch (Exception e) {
			
		}
	}

	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			mingleRef=34560,
			mingleTitle="Bus Svcs - Contact Events - Update Contact Event (JSON REST)",
			acceptanceCriteriaRefs="1",
			given="I have opened a Contact Event in Decision Support.",
			when="I choose to modify the data or add extra links to an existing Contact Event ",
			then="The data for the Updated Contact Event or any links to static records,And data is going in state cache .")

	public void testContactEventAndVerifyInStateCache() throws Exception {

		List<String> statepoleObjectTypes = null;

		GetPoleObjectsRequestDto getPoleObjectsRequest = new GetPoleObjectsRequestDto();
		getPoleObjectsRequest.setRetrievalType(RetrievalTypeDto.OPEN);
		PoleObjectCriteriaDto poleObjectCriteriaDto = new PoleObjectCriteriaDto();
		poleObjectCriteriaDto.setObjectType("ContactEvent");
		poleObjectCriteriaDto.addFieldCriterion(new IntegerCriterionDto("objectRef", contactEvent.getObjectRef()));

		getPoleObjectsRequest.setPoleObjectCriteria(poleObjectCriteriaDto);

		utils.setHeaderOnPoleRequest(getPoleObjectsRequest, securityContextId);

		getPoleObjectsRequest.getHeader().setClientName("CDASH");

		// Open the Contact Event in the decision support so that the same Contact Event is cached.
		GetPoleObjectsResponseDto getPoleObjectsResponseDto = poleBusinessServices.getPoleObjects(getPoleObjectsRequest);

		RetrieveFromCacheRequest request = new RetrieveFromCacheRequest();
		request.setGemMetaData(new GemMetaData());
		request.getGemMetaData().setSourceSystem("BusSvcs");
		request.getGemMetaData().setSecurityContextId(securityContextId);
		StateSearchCriteria stateSearchCriteria = new StateSearchCriteria();
		stateSearchCriteria.setSessionId(securityContextId);
		request.getStateSearchCriteria().add(stateSearchCriteria);
		
		// Retrieve the Contact Event from State Cache using the same Security Context ID.
		RetrieveFromCacheResponse retrieveFromCacheResponse = savingOfDataService.retrieveFromCache(request);

		List<State> states = retrieveFromCacheResponse.getState();

		if (retrieveFromCacheResponse != null) {
			statepoleObjectTypes = new ArrayList<String>();
			for (State state : states) {
				statepoleObjectTypes.add(state.getStateMetaData().getPoleObjectType());

			}
		}
		
		// Assert to check the same Contact Event was Cached.
		assertNotNull(statepoleObjectTypes);
		assertNotNull(getPoleObjectsResponseDto.getAuditedTransactionId());
		assertTrue(statepoleObjectTypes.contains(getPoleObjectsRequest.getPoleObjectCriteria().getObjectType()));

	}
	
	/**
	 * This is util method to create contact in the setup  
	 * @return
	 * @throws Exception 
	 */
	private PutPoleObjectsResponseDto createContactTestUtil() throws Exception {
		
		ContactEventDto ceDtoOrig = contils.createContactEventDto(peReference, peAccountRef);
				
		PutPoleObjectsRequestDto req = contils.createContactEventRequest(contils.createBusinessServiceInfo(securityContextId), ceDtoOrig);

		PutPoleObjectsResponseDto poleObjectsResponseDto = poleBusinessServices.putPoleObjects(req);
		
		contactEvent = contils.retrieveContactFromPole(ContactEventPoleDtoUtils.extractContactEventObjectRef(poleObjectsResponseDto), 5,
				poleBusinessServices, securityContextId, false);
		
		return poleObjectsResponseDto;
	}
	
	/**
	 * This is Update contact util to use the update contact event 
	 * @return
	 */
	private PutPoleObjectsRequestDto updateContactEventUtil() {
		
		contactEvent.setModificationStatus(ModificationStatusDto.UPDATE);
		
		contactEvent.setLinks(null);

		contactEvent.addLink(contils.addNewVehicleLinkInExistingContact(contactEvent.getObjectRef()));

		PutPoleObjectsRequestDto putPoleObjectsRequestDto = contils
				.createPutPoleObjectsRequest(utils.createBusinessServiceInfo(securityContextId), contactEvent);
		return putPoleObjectsRequestDto ;
	}
}
