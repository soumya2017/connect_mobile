package com.northgateis.gem.bussvc.submitcontact.functest;

import static com.northgateis.gem.bussvc.pole.utils.PoleDtoUtils.extractBusinessServiceResultInfo;
import static com.northgateis.gem.bussvc.submitcontact.utils.ContactEventPoleDtoUtils.extractContactEventObjectRef;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

import com.northgateis.gem.bussvc.framework.test.BusSvcStoryAcceptanceCriteriaReference;
import com.northgateis.gem.bussvc.pole.metadata.PoleMetadataCacheBean;
import com.northgateis.pole.common.PoleNames;
import com.northgateis.pole.schema.ContactEventDto;
import com.northgateis.pole.schema.EntityMetadataDto;
import com.northgateis.pole.schema.ModificationStatusDto;
import com.northgateis.pole.schema.PutPoleObjectsRequestDto;
import com.northgateis.pole.schema.PutPoleObjectsResponseDto;

/**
 * Tests to check that the validation framework can handle createdDateTime not being set in the common fields
 * of Pole objects
 */
public class CommonFieldsCreatedDateTimeFuncTest extends AbstractSubmitContactFuncTestBase {

	private ContactEventDto contactEvent;
	
	protected PoleMetadataCacheBean poleMetadataCache;
	
	@Override
	protected void setupImpl() throws Exception {
		
		poleMetadataCache = PoleMetadataCacheBean.getInstance();
		if (poleMetadataCache == null) {
			poleMetadataCache = busSvcUtils.getPoleMetadataCacheAndConstraints(poleDirect, poleMetadataDirect, securityContextId, poleUsername, polePassword);
		}
		
		String externalRef = PE_REF_PREFIX + ":" + new Date();
		
		PutPoleObjectsRequestDto req = contils.createPutPoleObjectsRequest(
				utils.createBusinessServiceInfo(securityContextId),
				contils.createContactEventDto(externalRef, peAccountRef));
		
		PutPoleObjectsResponseDto resp = poleBusinessServices.putPoleObjects(req);
		 
		contactEvent = contils.retrieveContactFromPole(extractContactEventObjectRef(resp), 5, poleDirect,
														securityContextId, false);
	}
	
	@Override
	protected void teardownImpl() throws Exception {
		try {			
			if (contactEvent != null) {
				contils.retrieveContactFromPole(contactEvent.getObjectRef(), 5, poleDirect, securityContextId, true);
			}
		} catch (Exception e) {
			logger.debug("Failed to delete record", e);
		}
		contactEvent = null;
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			mingleRef=32590,
			mingleTitle="Extended validation - expended scope: ALL POLE metadata",
			acceptanceCriteriaRefs="TODO",
			given="A graph containing a ContactEvent with createdDateTime in common fields set",
			when="The graph is validated",
			then="The graph should pass validation and be successfully saved to Pole"
	)
	public void testCreatedDateTimeCommonFieldSetOnContactEventPassesValidation() throws Exception {
		
		contactEvent.getCommonFields().setMask(null);
		
		updateContactEventAndRetrieveFromPole("createdDateTime set on ContactEvent common fields");
		
		assertNotNull(contactEvent.getCommonFields().getCreatedDateTime());
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			mingleRef=32590,
			mingleTitle="Extended validation - expended scope: ALL POLE metadata",
			acceptanceCriteriaRefs="TODO",
			given="A graph containing a ContactEvent with createdDateTime in common fields not set",
			when="The graph is validated",
			then="The graph should pass validation and be successfully saved to Pole"
	)
	public void testCreatedDateTimeCommonFieldNotSetOnContactEventPassesValidation() throws Exception {
		
		contactEvent.getCommonFields().setCreatedDateTime(null);
		contactEvent.getCommonFields().setMask(null);
		
		updateContactEventAndRetrieveFromPole("createdDateTime not set on ContactEvent common fields");
		
		assertNotNull(contactEvent.getCommonFields().getCreatedDateTime());
	}
	
	private void updateContactEventAndRetrieveFromPole(String description) throws Exception {
		EntityMetadataDto contactEventMetadata =
				poleMetadataCache.getPoleMetadataCache().getEntityMetadataForName(PoleNames.CONTACT_EVENT);
		
		utils.prepareMaskForPoleObject(contactEvent, contactEventMetadata, "ObjectRef", "Description");
		
		contactEvent.setDescription(description);
		contactEvent.setModificationStatus(ModificationStatusDto.UPDATE);
		
		PutPoleObjectsRequestDto req = contils.createPutPoleObjectsRequest(
				utils.createBusinessServiceInfo(securityContextId), contactEvent);
		
		PutPoleObjectsResponseDto resp = poleBusinessServices.putPoleObjects(req);
		assertTrue(extractBusinessServiceResultInfo(resp).isCompleted());
		
		contactEvent = contils.retrieveContactFromPole(contactEvent.getObjectRef(), 5, poleDirect, securityContextId, false);
		assertEquals(description, contactEvent.getDescription());
	}
}
