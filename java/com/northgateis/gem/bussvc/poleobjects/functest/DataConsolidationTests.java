package com.northgateis.gem.bussvc.poleobjects.functest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Date;

import org.junit.Test;

import com.northgateis.gem.bussvc.pole.metadata.PoleMetadataCacheBean;
import com.northgateis.gem.bussvc.poleobjects.constants.PoleObjectsServiceConstants;
import com.northgateis.gem.bussvc.submitcontact.utils.ContactEventPoleDtoUtils;
import com.northgateis.gem.bussvc.test.util.ContactEventTestUtils;
import com.northgateis.pole.common.InvalidDataException;
import com.northgateis.pole.common.PoleNames;
import com.northgateis.pole.schema.AllocateObjectRefsRequestDto;
import com.northgateis.pole.schema.AllocateObjectRefsResponseDto;
import com.northgateis.pole.schema.ContactEventDto;
import com.northgateis.pole.schema.ContactEventPersonInfoDto;
import com.northgateis.pole.schema.DocumentDto;
import com.northgateis.pole.schema.EntityMetadataDto;
import com.northgateis.pole.schema.LinkDto;
import com.northgateis.pole.schema.ModificationStatusDto;
import com.northgateis.pole.schema.PutPoleObjectsRequestDto;
import com.northgateis.pole.schema.PutPoleObjectsResponseDto;
import com.northgateis.pole.schema.RequestHeaderDto;

/**
 * Functional test class for data consolidation
 */
public class DataConsolidationTests extends AbstractPoleObjectsFuncTestBase {
	
	private Integer contactEventObjectRef;
	
	private PoleMetadataCacheBean poleMetadataCache;

	private EntityMetadataDto contactEventMetadata;
	
	@Override
	protected void setupImpl() throws Exception {
		
		contactEventObjectRef = null;

		poleMetadataCache = PoleMetadataCacheBean.getInstance();
		if (poleMetadataCache == null) {
			poleMetadataCache = busSvcUtils.getPoleMetadataCacheAndConstraints(poleDirect, poleMetadataDirect, securityContextId);
		}
		
		contactEventMetadata = poleMetadataCache.getPoleMetadataCache().getEntityMetadataForName(PoleNames.CONTACT_EVENT);
	}
	
	/**
	 * Overriding to provide more roles
	 * 
	 * @throws Exception
	 */
	@Override
	protected void createMainSecurityContextId() throws Exception {
		if (securityContextId == null) {
			securityContextId = busSvcUtils.getSecurityContextId("Frank Shunneltestone", 
				Arrays.asList("BriefingAuthorise", "BriefingCreator", "CrimeInvestigator",
					"ATHENA_USER", "NorthgateSystemAdmin", "SysAdmin1", "SysAdmin2"), securityService);
		}
	}
	
	@Override
	protected void teardownImpl() throws Exception {
		deleteData();
	}
	
	private void deleteData() throws Exception {

		try { 
			if (contactEventObjectRef != null) {
				contils.retrieveContactFromPole(contactEventObjectRef, 2, poleDirect, securityContextId, true);
			}
		} catch (Exception e) {
			logger.debug("Failed to delete record", e);
		}
	}
	
	/**
	 * Test data consolidation when merging objects does not try to merge blank fields if they are
	 * not specified by the mask
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCheckStateCacheMergeDoesNotBlankFields() throws Exception {
		String peReference = PE_REF_PREFIX + new Date();
		
		AllocateObjectRefsRequestDto allocateObjectRefsRequest = new AllocateObjectRefsRequestDto();
		allocateObjectRefsRequest.addEntityType(PoleNames.CONTACT_EVENT);
		allocateObjectRefsRequest.setHeader(new RequestHeaderDto());
		allocateObjectRefsRequest.getHeader().setClientName("commonsvcs");
		allocateObjectRefsRequest.getHeader().setPasswordHash(securityContextId);
		AllocateObjectRefsResponseDto allocateObjectRefsResponse = poleDirect.allocateObjectRefs(allocateObjectRefsRequest);
		
		contactEventObjectRef = allocateObjectRefsResponse.getAllocatedObjectRefs().get(0).getObjectRef();
		
		DocumentDto document = new DocumentDto();
		document.setObjectRef(new Integer(-2));
		document.setModificationStatus(ModificationStatusDto.CREATE);
		document.setName("Alf");
		document.setDescription("Alfs State Cache Test Document");
		
		ContactEventDto scContactEvent = new ContactEventDto();
		scContactEvent.setObjectRef(contactEventObjectRef);
		scContactEvent.setObjectRefChecksum(allocateObjectRefsResponse.getAllocatedObjectRefs().get(0).getObjectRefChecksum());
		
		LinkDto contactEventDocumentLink = new LinkDto();
		contactEventDocumentLink.setModificationStatus(ModificationStatusDto.CREATE);
		contactEventDocumentLink.setSourcePoleObjectType(PoleNames.CONTACT_EVENT);
		contactEventDocumentLink.setSourcePoleObjectRef(contactEventObjectRef);
		contactEventDocumentLink.setToPoleObject(document);
		contactEventDocumentLink.setLinkReason(PoleObjectsServiceConstants.DOCUMENT_PRODUCTION_LINK_REASON);
		scContactEvent.addLink(contactEventDocumentLink);
		
		PutPoleObjectsRequestDto scReq =
				contils.createContactEventRequest(contils.createBusinessServiceInfo(securityContextId), scContactEvent);
		scReq.getHeader().setClientName("commonsvcs");
		scReq.getHeader().setTransactionDescription("DataConsolidation");
		
		poleBusinessServices.putPoleObjects(scReq);
		
		ContactEventDto contactEvent = contils.createContactEventDto(contactEventObjectRef, ModificationStatusDto.CREATE,
																	peReference, peAccountRef);
		contactEvent.setObjectRef(contactEventObjectRef);
		contactEvent.setObjectRefChecksum(allocateObjectRefsResponse.getAllocatedObjectRefs().get(0).getObjectRefChecksum());
		
		PutPoleObjectsRequestDto req =
				contils.createContactEventRequest(contils.createBusinessServiceInfo(securityContextId), contactEvent);
		req.getHeader().setClientName("cdash");
		req.getHeader().setTransactionDescription("DataConsolidation");
		
		PutPoleObjectsResponseDto resp = poleBusinessServices.putPoleObjects(req);
		assertNotNull(getBusinessServiceResultInfo(resp).getTransactionId());
		
		ContactEventDto contactEventToCheck = contils.retrieveContactFromPole(contactEventObjectRef, 2, poleDirect,
																				securityContextId, false);
		
		contils.checkContact(contactEventToCheck, peReference, false, true, null, null, false);
		assertEquals(ContactEventTestUtils.DEFAULT_CONTACT_EVENT_SUMMARY, contactEventToCheck.getContactSummary());
		
		boolean documentLinkFound = false;
		for (LinkDto link : contactEventToCheck.getLinks()) {
			if (link.getSourcePoleObjectKey().equals(contactEventToCheck.getEntityKey()) &&
					link.getLinkReason().equals(PoleObjectsServiceConstants.DOCUMENT_PRODUCTION_LINK_REASON)) {
				documentLinkFound = true;
				break;
			}
		}
		assertEquals(true, documentLinkFound);
	}
	
	/**
	 * Test data consolidation can handle updates to child objects on links
	 * 
	 * @throws Exception
	 */
	@Test
	public void testChildObjectOnLinkCanBeUpdated() throws Exception {
		String peReference = PE_REF_PREFIX + new Date();
		
		ContactEventDto contactEvent = contils.createContactEventDto(peReference, peAccountRef);
		for (LinkDto link : contactEvent.getLinks()) {
			if (link.getSourcePoleObjectKey().equals(contactEvent.getEntityKey()) &&
					link.getLinkReason().equals(PoleObjectsServiceConstants.VICTIM_LINK_REASON)) {
				ContactEventPersonInfoDto contactEventPersonInfo = new ContactEventPersonInfoDto();
				contactEventPersonInfo.setModificationStatus(ModificationStatusDto.CREATE);
				contactEventPersonInfo.setPersonElectsToBeUpdated("IN");
				contactEventPersonInfo.setTimesToAvoid("All day");
				link.addContactEventPersonInfo(contactEventPersonInfo);
				break;
			}
		}
		
		PutPoleObjectsRequestDto req =
				contils.createContactEventRequest(contils.createBusinessServiceInfo(securityContextId), contactEvent);
		
		PutPoleObjectsResponseDto resp = poleBusinessServices.putPoleObjects(req);
		assertNotNull(getBusinessServiceResultInfo(resp).getTransactionId());
		
		contactEventObjectRef = ContactEventPoleDtoUtils.extractContactEventObjectRef(resp);
		
		ContactEventDto contactEventToCheck = contils.retrieveContactFromPole(contactEventObjectRef, 2, poleDirect,
				securityContextId, false);
		
		LinkDto victimLink = null;
		for (LinkDto link : contactEventToCheck.getLinks()) {
			if (link.getSourcePoleObjectKey().equals(contactEventToCheck.getEntityKey()) &&
					link.getLinkReason().equals(PoleObjectsServiceConstants.VICTIM_LINK_REASON)) {
				victimLink = link;
				break;
			}
		}
		
		String timesToAvoid = "Some of the day";
		victimLink.getContactEventPersonInfoList().get(0).setModificationStatus(ModificationStatusDto.UPDATE);
		((ContactEventPersonInfoDto) victimLink.getContactEventPersonInfoList().get(0)).setTimesToAvoid(timesToAvoid);
		
		ContactEventDto updateContactEvent = new ContactEventDto();
		updateContactEvent.setObjectRef(contactEventObjectRef);
		updateContactEvent.addLink(victimLink);
		
		req = contils.createContactEventRequest(contils.createBusinessServiceInfo(securityContextId), updateContactEvent);
		
		resp = poleBusinessServices.putPoleObjects(req);
		assertNotNull(getBusinessServiceResultInfo(resp).getTransactionId());
		
		contactEventToCheck = contils.retrieveContactFromPole(contactEventObjectRef, 2, poleDirect,
				securityContextId, false);
		
		for (LinkDto link : contactEventToCheck.getLinks()) {
			if (link.getSourcePoleObjectKey().equals(contactEventToCheck.getEntityKey()) &&
					link.getLinkReason().equals(PoleObjectsServiceConstants.VICTIM_LINK_REASON)) {
				assertEquals(timesToAvoid,
						((ContactEventPersonInfoDto) victimLink.getContactEventPersonInfoList().get(0)).getTimesToAvoid());
				break;
			}
		}
	}
	
	/**
	 * Test data consolidation throws an exception if a field is being updated that hasnt been specified in
	 * the mask
	 * 
	 * @throws Exception
	 */
	@Test
	public void testNoSilentDataLossOnFieldNotSpecifedByMask() throws Exception {
		String peReference = PE_REF_PREFIX + new Date();
		
		ContactEventDto contactEvent = contils.createContactEventDto(peReference, peAccountRef);
		
		PutPoleObjectsRequestDto req =
				contils.createContactEventRequest(contils.createBusinessServiceInfo(securityContextId), contactEvent);
		
		PutPoleObjectsResponseDto resp = poleBusinessServices.putPoleObjects(req);
		assertNotNull(getBusinessServiceResultInfo(resp).getTransactionId());
		
		contactEventObjectRef = ContactEventPoleDtoUtils.extractContactEventObjectRef(resp);
		
		ContactEventDto contactEventToUpdate = contils.retrieveContactFromPole(contactEventObjectRef, 2, poleDirect,
				securityContextId, false);
		
		utils.prepareMaskForPoleObject(contactEventToUpdate, contactEventMetadata, "ContactSummary");
		contactEventToUpdate.setModificationStatus(ModificationStatusDto.UPDATE);
		contactEventToUpdate.setContactSummary("My updated summary");
		contactEventToUpdate.setDescription("My updated description");
		
		req = contils.createContactEventRequest(contils.createBusinessServiceInfo(securityContextId), contactEventToUpdate);
		
		try {
			poleBusinessServices.putPoleObjects(req);
			fail("Field update on field not specified by mask allowed");
		} catch (Exception e) {
			assertTrue(e instanceof InvalidDataException);
		}
	}
}
