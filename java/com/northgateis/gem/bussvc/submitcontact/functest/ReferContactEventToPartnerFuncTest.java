package com.northgateis.gem.bussvc.submitcontact.functest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.northgateis.gem.bussvc.framework.FrameworkServiceConstants;
import com.northgateis.gem.bussvc.framework.schema.BusinessServiceResponseInfo;
import com.northgateis.gem.bussvc.pole.utils.PoleDtoUtils;
import com.northgateis.gem.framework.util.logger.GemLogger;
import com.northgateis.pole.common.PoleException;
import com.northgateis.pole.schema.ContactEventDto;
import com.northgateis.pole.schema.DocumentDto;
import com.northgateis.pole.schema.LinkDto;
import com.northgateis.pole.schema.ModificationStatusDto;
import com.northgateis.pole.schema.PutPoleObjectsRequestDto;
import com.northgateis.pole.schema.PutPoleObjectsResponseDto;
import com.northgateis.pole.schema.RequestHeaderDto;

/**
 * Functional tests for update of a {@link ContactEventDto}, which contains a 'ReferToPartner' performed action.
 * 
 * This test is a bit tricky in that it has no access to action performed in APM module, hence it has to rely on
 * checking the audit 'trail' a few steps further on after the ContactEvent has been updated so that it can 
 * be checked for  Notification and/or Doc Gen Function have been invoked.
 * 
 * UPDATE: There is no need to get a an actual Portal Security context ID for accessing the Audit data as 
 *         'Frank Shunneltestone' has more IAM roles added to his name which allows him to access the Audit data.
 *         
 *         ALSO, the success of this func test will be depended on if there is an entry in the 'ReferablePartner' 
 *         table that contains the PartnerCode (as specified in properties) and it has a legit Template exist in
 *         the env that this test is being run.
 */
public class ReferContactEventToPartnerFuncTest extends AbstractSubmitContactFuncTestBase {

	private static final GemLogger logger = GemLogger.getLogger(ReferContactEventToPartnerFuncTest.class);

	private static final String DOCUMENT_PRODUCTION = "DOCUMENT PRODUCTION";
	private static final String TERMINAL_NAME_BUSINESS_SERVICES = "BUSINESS_SERVICES";
	private static final String REFER_TO_PARTNER_PARTNER_CODE = "LOCAL AUTHORITY";

	@Override
	protected void createMainSecurityContextId() throws Exception {
		
		//We need a specific *wider* set of security roles for these tests, to ensure that we
		//can query audit at the end of each test
		securityContextId = utils.getSecurityContextId(OFFICER_DISPLAY_VALUE, 
			Arrays.asList("ATHENA_USER", "GEM_PORTAL", "NorthgateSystemAdmin", "SysAdmin1", "SysAdmin2"), securityService);
	}
	
	@Override
	protected void teardownImpl() throws Exception {
		securityContextId = null;
	}
	
	/**
	 * A ContactEvent (with Action=REFERRED_TO_PARTNER and ContactEventTxData) is being sent to Bus Svcs By CCI. 
	 * Bus svcs will then in turn generate the document and link the document to the ContactEvent. 
	 * 
	 * So, for this particular test to work, it requires the Env has the PartnerCode exist in the 'ReferablePartner' 
	 * table so that email can be send with/without the generated doc (if the template exist in that Evn).
	 * 
	 * NOTE : Here we cannot test whether the mail is sent to the concerned person or not. 
	 */
	@Test
	public void testReferToPartnerByEmailWithDocumentHappyPath() throws Exception {

		Integer contactEventObjRef = createNewContactEventWithCreateContactEventTxData();

		Long auditedTransactionId = updateContactEvent(contactEventObjRef, true, REFER_TO_PARTNER_PARTNER_CODE);
		assertNotNull("Update ContactEvent transaction ID should be returned", auditedTransactionId);

		ContactEventDto contactEventDtoWithDocLink = getContactEventFromPole(contactEventObjRef);
		LinkDto linkDtoWithDocument = getLinkDto(contactEventDtoWithDocLink);
		validateLinkWithDocument(linkDtoWithDocument);
	}

	/**
	 * A ContactEvent (without ContactEventTxData) is being sent is being sent to Bus Svcs By CCI. 
	 * Bus svcs will then save the concactEvent as it is. 
	 * 
	 * So, for this particular test to work, it requires the Env has the PartnerCode exist in the 'ReferablePartner' 
	 * table so that email can be send with/without the generated doc (if the template exist in that Evn).
	 * 
	 * NOTE : Here we cannot test whether the mail is sent to the concerned person or not. 
	 */
	@Test
	public void testReferToPartnerByEmailWithoutTransientDataNoDocumentLinkIsGenerated() throws Exception {

		Integer contactEventObjRef = createNewContactEventWithCreateContactEventTxData();

		Long auditedTransactionId = updateContactEvent(contactEventObjRef, false, null);
		assertEquals("Update ContactEvent - transaction ID != null: ", true, auditedTransactionId != null);

		ContactEventDto contactEventDtoWithDocLink = getContactEventFromPole(contactEventObjRef);
		LinkDto linkDtoWithDocument = getLinkDto(contactEventDtoWithDocLink);
		assertNull("link with document is present.", linkDtoWithDocument);
	}

	private void validateLinkWithDocument(LinkDto linkDtoWithDocument) {
		assertEquals("LinkReason not matching.", DOCUMENT_PRODUCTION, linkDtoWithDocument.getLinkReason());
		assertEquals("Link is not researched.", true, linkDtoWithDocument.getResearched());

		DocumentDto docDto = (DocumentDto) linkDtoWithDocument.getToPoleObject();
		assertNotNull("documentDto is null.", docDto);
		
	}

	private LinkDto getLinkDto(ContactEventDto contactEventDtoWithDocLink) {
		List<LinkDto> linkDtoList = contactEventDtoWithDocLink.getLinks();
		LinkDto linkDtoWithDocument = null;
		for (LinkDto linkDto : linkDtoList) {
			if (DOCUMENT_PRODUCTION.equalsIgnoreCase(linkDto.getLinkReason())) {
				linkDtoWithDocument = linkDto;
				break;
			}
		}
		return linkDtoWithDocument;
	}

	/**
	 * Create a new ContactEvent with a new CreateContactEventTxData in POLE.
	 */
	private Integer createNewContactEventWithCreateContactEventTxData() {
		
		logger.info(" Create a new ContactEvent and save to POLE");
		ContactEventDto contactEvent = contils.createSimpleContactEvent(null, null);
		contils.addCreateContactTxData(contactEvent);
		
		PutPoleObjectsRequestDto request = new PutPoleObjectsRequestDto();
        request.setPoleObjects(Arrays.asList(contactEvent));
		request.setHeader(createHeaderRequestForPoleRequest());
		
		PutPoleObjectsResponseDto resp = poleBusinessServices.putPoleObjects(request);
		Integer contactEventObjRef = resp.getObjectRefs().get(0);
		logger.debug("Created ContactEvent with obj ref: {}", contactEventObjRef);
		
		BusinessServiceResponseInfo bsResponse = PoleDtoUtils.extractBusinessServiceResultInfo(resp);
		assertNotNull("Response: ", bsResponse.getTransactionId());
		return contactEventObjRef;
	}
	
	/**
	 * Get the newly created ContactEvent back from POLE and then perform an Update to it with/without a new 
 	 * 'ContactEventTxData' + 'ReferContactEventToPartnerTxData' which contain the PartnerCode.
	 */
	private Long updateContactEvent(Integer contactEventObjRef, boolean withContactEventTxData, String partnerCode) throws Exception {
		
		logger.info(" Get ContactEvent ({}) from POLE", contactEventObjRef);
		ContactEventDto contactEventDto = getContactEventFromPole(contactEventObjRef);
		Long auditedTransactionId = null;
		try {
			PutPoleObjectsRequestDto updateRequest = createUpdateContactEventRequest(
					contactEventDto, withContactEventTxData, partnerCode);
			logger.info(" Update ContactEvent to POLE");
			PutPoleObjectsResponseDto updateResp = poleBusinessServices.putPoleObjects(updateRequest);
			auditedTransactionId = updateResp.getAuditedTransactionId();
	        logger.debug(" Update ContactEvent - obj ref: {}", updateResp.getObjectRefs().get(0));
	        logger.debug(" Update ContactEvent - AuditedTransactionId : {}", auditedTransactionId);
	        
	        BusinessServiceResponseInfo bsUpdateResponse = PoleDtoUtils.extractBusinessServiceResultInfo(updateResp);
	        logger.debug(" BS Update Response TransactionId : {}", bsUpdateResponse.getTransactionId());
			assertNotNull("BS Update Response TransactionId should not be null", bsUpdateResponse.getTransactionId());
		} catch (PoleException e) {
			logger.error("  PoleException occurred: {}", e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error("  Exception occurred: {}", e.getMessage());
			throw e;
		}
		return auditedTransactionId;
	}
	
	/**
	 * Create update request for the CotnactEvent with transient data ReferContactEventToPartnerTxData.
	 * Note: There is no need to add ContactEventAction as Business Services is adding to it already.
	 */
	private PutPoleObjectsRequestDto createUpdateContactEventRequest(
			ContactEventDto contactEventDto, boolean withTxData, String partnerCode) {

		contactEventDto.setModificationStatus(ModificationStatusDto.UPDATE);
		if (withTxData) {
			// Add ReferContactEventToPartnerTxData and ContactEventTxData
			contils.addReferToPartnerTxData(contactEventDto);
			contactEventDto.getContactEventTxData().getReferContactEventToPartnerTxData().setPartnerCode(partnerCode);
		}

		// Create the update request for the ContactEvent
		PutPoleObjectsRequestDto createRequest = contils.createPutPoleObjectsRequest(
				contils.createBusinessServiceInfo(securityContextId), contactEventDto);
		return createRequest;
	}

	private RequestHeaderDto createHeaderRequestForPoleRequest() {
		RequestHeaderDto requestHeader = new RequestHeaderDto();
		requestHeader.setClientName(FrameworkServiceConstants.BUSINESS_SERVICES_CLIENT_NAME);
		requestHeader.setPasswordHash(securityContextId);
		requestHeader.setTerminalName(TERMINAL_NAME_BUSINESS_SERVICES);
		return requestHeader;
	}

	/**
	 * Create a get Pole object request for ContactEvent.
	 * @throws Exception 
	 */
	private ContactEventDto getContactEventFromPole(Integer objectRef) throws Exception {
		ContactEventDto contactEvent = contils.retrieveContactFromPole(objectRef, 15, poleBusinessServices, securityContextId, false);
		return contactEvent;
	}
}
