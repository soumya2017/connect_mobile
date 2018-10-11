package com.northgateis.gem.bussvc.attachments.functest;

import static com.northgateis.gem.bussvc.pole.utils.PoleDtoUtils.addBusinessServiceInfo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.UUID;

import javax.activation.DataHandler;

import org.junit.Assert;
import org.junit.Test;

import com.northgateis.gem.bussvc.AbstractBusinessServicesFunctionalTestBase;
import com.northgateis.gem.bussvc.attachment.schema.AttachmentType;
import com.northgateis.gem.bussvc.attachment.schema.GemMetaData;
import com.northgateis.gem.bussvc.attachment.schema.Status;
import com.northgateis.gem.bussvc.attachment.schema.UploadBinaryDataRequest;
import com.northgateis.gem.bussvc.attachment.schema.UploadBinaryDataRequest.PayLoad;
import com.northgateis.gem.bussvc.attachment.schema.UploadBinaryDataResponse;
import com.northgateis.gem.bussvc.framework.test.BusSvcStoryAcceptanceCriteriaReference;
import com.northgateis.gem.bussvc.poleobjects.constants.PoleObjectsServiceConstants;
import com.northgateis.gem.bussvc.submitincident.utils.IncidentPoleDtoUtils;
import com.northgateis.gem.bussvc.submitintelreport.utils.IntelReportPoleDtoUtils;
import com.northgateis.gem.bussvc.test.util.BusinessServicesTestUtils;
import com.northgateis.gem.bussvc.test.util.CommonPoleObjectTestUtils;
import com.northgateis.pole.client.PoleDtoBuildHelper;
import com.northgateis.pole.common.InvalidDataException;
import com.northgateis.pole.common.PoleNames;
import com.northgateis.pole.schema.DocumentDto;
import com.northgateis.pole.schema.DocumentVersionDto;
import com.northgateis.pole.schema.GetPoleObjectsRequestDto;
import com.northgateis.pole.schema.GetPoleObjectsResponseDto;
import com.northgateis.pole.schema.IncidentDocumentInfoDto;
import com.northgateis.pole.schema.IncidentDto;
import com.northgateis.pole.schema.IntelligenceReportDto;
import com.northgateis.pole.schema.LinkDto;
import com.northgateis.pole.schema.ModificationStatusDto;
import com.northgateis.pole.schema.PoleObjectDto;
import com.northgateis.pole.schema.PoleObjectFieldSelectorDto;
import com.northgateis.pole.schema.PutPoleObjectsRequestDto;
import com.northgateis.pole.schema.PutPoleObjectsResponseDto;
import com.northgateis.pole.schema.RetrievalTypeDto;
import com.northgateis.pole.schema.TagDto;


/**
 * Functional test class for attachment upload via BusinessServices.
 * 
 */
public class AttachmentsFuncTest extends AbstractBusinessServicesFunctionalTestBase {

	private IncidentDto incident;
	private static final int minglRef_36872 = 36872;
	private static final String mingleTitle_36872 = "WP208 - Bus Svcs - Attachments - Upload document & link it to main event ";
	private static final int minglRef_37396 = 37396;
	private static final String mingleTitle_37396 = "WP208 - Bus Svcs - Attachments - Save main event & upload document later";
	private static final int workPackage = 208;
	private IntelligenceReportDto intelReport;

	private static final String TEXT_CONTENT = "This is a test";
	private static final byte[] BYNARY_FILE_CONTENT = TEXT_CONTENT.getBytes();
	private static final String GENERIC_MIME_TYPE = "application/octet-stream";
	private static final String DOCVERSION_STATUS_NOT_UPLOADED = "NOT UPLOADED";
	private static final String DOCVERSION_STATUS_GENERATED = "GENERATED";

	@Override
	protected void setupImpl() throws Exception {
		incident = utils.createIncidentWithDocument(-1, Integer.valueOf(officerReportingId));
		intelReport = intelUtils.createIntelReportWithDocuments(-1, Integer.valueOf(officerReportingId));
	}
	
	@Override
	protected void teardownImpl() throws Exception {
		deleteData();
	}
	
	private void deleteData() throws Exception {		
		try {
			if (intelReport != null) {
				intelUtils.removeIntelligenceReportFromPole(intelReport.getObjectRef(), 2, poleDirect,
						securityContextId);
			}
		} catch (Exception e) {
			logger.debug("Failed to delete Intel Report having objectRef {} ", intelReport.getObjectRef(), e);
		}
		try {
			if (incident != null) {
				utils.removeIncidentFromPole(incident.getObjectRef(), 2, poleDirect,
						securityContextId);
			}
		} catch (Exception e) {
			logger.debug("Failed to delete Incident having objectRef {} ", incident.getObjectRef(), e);
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(mingleRef = minglRef_36872, 
											mingleTitle = mingleTitle_36872, 
											acceptanceCriteriaRefs = "CR25", 
											given = "MG11 document is uploaded via a 3rd party application and is linked to an Investigation and "
													+ "a Police officer but incidentDocumentInfo.associatedStaffMemberId is not valid", 
											when = "Event is submited", then = "Validation error is returned", workPackage = workPackage)
	public void testIncidentWhenLinkedMG11DocumentHasInvalidStaffMemberId() throws Exception {

		try {

			IncidentDocumentInfoDto incidentDocInfo = new IncidentDocumentInfoDto();
			incidentDocInfo.setAssociatedStaffMemberId(123);
			incidentDocInfo.setModificationStatus(ModificationStatusDto.CREATE);

			incident.getLinks().get(0).addIncidentDocumentDetail(incidentDocInfo);

			doCreateIncident(incident);
			fail("Expected an exception for invalid data");

		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(), ide.getMessage()
					.contains("associatedStaffMemberId reference data not found for EmployeeIteration"));

		}
	}

	@Test
	@BusSvcStoryAcceptanceCriteriaReference(mingleRef = minglRef_36872, 
											mingleTitle = mingleTitle_36872, 
											acceptanceCriteriaRefs = "CR24", 
											given = "MG11 document is uploaded via a 3rd party application and is linked to an Investigation and "
													+ "a Police officer but incidentDocumentInfo.associatedStaffMemberId is not valid", 
											when = "Event is submited", then = "Validation error is returned", workPackage = workPackage)
	public void testIncidentWhenLinkedMG11DocumentHasValidStaffMemberId() throws Exception {

			IncidentDocumentInfoDto incidentDocInfo = new IncidentDocumentInfoDto();
			incidentDocInfo.setAssociatedStaffMemberId(3);
			incidentDocInfo.setModificationStatus(ModificationStatusDto.CREATE);

			incident.getLinks().get(0).addIncidentDocumentDetail(incidentDocInfo);
			incident.getIncidentTxData().getCreateIncidentTxData().setCompleted(false);

			doCreateIncident(incident);
	}

	@Test
	@BusSvcStoryAcceptanceCriteriaReference(mingleRef = minglRef_36872, 
											mingleTitle = mingleTitle_36872, 
											acceptanceCriteriaRefs = "CR2", 
											given = "Document is linked to an event and document.name is null", 
											when = "Event is submited", then = "A mandatory validation is returned", workPackage = workPackage)
	public void testIntelReportWhenLinkedDocumentHasMissingName() throws Exception {

		try {
			DocumentDto document = (DocumentDto) intelReport.getLinks().get(0).getToPoleObject();
			document.setName(null);

			doCreateIntelReport(intelReport);
			fail("Expected an exception for invalid data");

		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(), ide.getMessage().contains("name must have a value"));

		}
	}

	@Test
	@BusSvcStoryAcceptanceCriteriaReference(mingleRef = minglRef_36872, 
											mingleTitle = mingleTitle_36872, 
											acceptanceCriteriaRefs = "CR6", 
											given = "Document is linked to an event and document.tag.tagValueLevel1 = Sound File and tagValueLevel2 != Sound File", 
											when = "Event is submited", then = "Error is returned", workPackage = workPackage)
	public void testIntelReportWithDocumentWhenTagLevel1DoesntMatchWithTabLevel2() throws Exception {

		try {
			DocumentDto document = (DocumentDto) intelReport.getLinks().get(0).getToPoleObject();
			document.getTags().get(0).setTagValueLevel1("SOUND");
			document.getTags().get(0).setTagValueLevel2("MG1");

			doCreateIntelReport(intelReport);
			fail("Expected an exception for invalid data");

		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("tagValueLevel2 has an incorrect CV value for list TAG_VALUE_LEVEL_2"));

		}
	}

	@Test
	@BusSvcStoryAcceptanceCriteriaReference(mingleRef = minglRef_36872, 
											mingleTitle = mingleTitle_36872, 
											acceptanceCriteriaRefs = "CR18", 
											given = "Document is linked to an event and child object  'documentVersion' is supplied and documentVersion.fileName is null", 
											when = "Event is submited", then = "A mandatory validation is returned", workPackage = workPackage)
	public void testIntelReportWithDocumentWhenFileNameIsNotGiven() throws Exception {

		try {
			DocumentDto document = (DocumentDto) intelReport.getLinks().get(0).getToPoleObject();
			document.getVersions().get(0).setFileName(null);

			doCreateIntelReport(intelReport);
			fail("Expected an exception for invalid data");

		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("fileName must have a value"));

		}
	}

	@Test
	@BusSvcStoryAcceptanceCriteriaReference(mingleRef = minglRef_36872, 
											mingleTitle = mingleTitle_36872, 
											acceptanceCriteriaRefs = "CR12", 
											given = "Document is linked to an event and uuid supplied is not in proper format", 
											when = "Event is submited", then = "Error is returned", workPackage = workPackage)
	public void testIntelReportWithDocumentWhenUuidIsInvalid() throws Exception {

		try {
			DocumentDto document = (DocumentDto) intelReport.getLinks().get(0).getToPoleObject();
			document.getVersions().get(0).setUuid("00830E0C-290C-4A6B-81F0");

			doCreateIntelReport(intelReport);
			fail("Expected an exception for invalid data");

		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("uuid is not valid for pattern"));

		}
	}

	@Test
	@BusSvcStoryAcceptanceCriteriaReference(mingleRef = minglRef_36872, 
											mingleTitle = mingleTitle_36872, 
											acceptanceCriteriaRefs = "CR21, CR22, CR23", 
											given = "Document is linked to an event and child object documentVersion is supplied", 
											when = "Event is submited", then = "documentVersion.hashAlgorithm, documentVersion.encryptionAlgorithm , documentVersion.status is set", workPackage = workPackage)
	public void testHappyPathWhereEventIsSavedAfterAttachmentUpload() throws Exception {
		
		DocumentDto document = (DocumentDto) intelReport.getLinks().get(0).getToPoleObject();

		String uuid = document.getVersions().get(0).getUuid();
		String attachmentHandle = uploadBinaryAttachment(createUploadBinaryRequest(uuid));

		Assert.assertTrue("UploadBinaryDataResponse.attachmentHandle is null", attachmentHandle != null);
		
		document.getVersions().get(0).setAttachmentHandle(attachmentHandle);

		doCreateIntelReport(intelReport);

	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(mingleRef = minglRef_36872, 
											mingleTitle = mingleTitle_36872, 
											acceptanceCriteriaRefs = "", 
											given = "Multiple documents are uploaded and are linked to an event", 
											when = "Event is submited", then = "All documents should be uploaded and linked corrected in pole", workPackage = workPackage)
	public void testEventSaveWhenMultipleAttachmentsAreUploaded() throws Exception {

			String attachment1Uuid = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
			String attachmentHandle1 = uploadBinaryAttachment(createUploadBinaryRequest(attachment1Uuid));
			Assert.assertTrue("UploadBinaryDataResponse.attachmentHandle is null", attachmentHandle1 != null);

			String attachment2Uuid = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
			String attachmentHandle2 = uploadBinaryAttachment(createUploadBinaryRequest(attachment2Uuid));
			Assert.assertTrue("UploadBinaryDataResponse.attachmentHandle is null", attachmentHandle2 != null);
			
			addDocumentLinkToIntelReport(attachment1Uuid, attachmentHandle1);
			addDocumentLinkToIntelReport(attachment2Uuid, attachmentHandle2);

			doCreateIntelReport(intelReport);

	}

	@Test
	@BusSvcStoryAcceptanceCriteriaReference(mingleRef = minglRef_37396, 
											mingleTitle = mingleTitle_37396, 
											acceptanceCriteriaRefs = "CR30", 
											given = "External system makes a call to BusinessService to create an event with Document objects & no request is made to DocumentManagement to upload the document", 
											when = "Event is submited", then = "BusinessService will create the Document and related objects with DocumentVersion.status = 'NotUploaded'", workPackage = workPackage)
	public void testWhenEventIsSavedAndAttachmentNotUploaded() throws Exception {

		DocumentDto document = (DocumentDto) intelReport.getLinks().get(0).getToPoleObject();
		document.getVersions().get(0).setStatus(DOCVERSION_STATUS_NOT_UPLOADED);
		document.getVersions().get(0).setDocumentSize(null);

		PutPoleObjectsResponseDto responseDto = doCreateIntelReport(intelReport);

		document = (DocumentDto) intelReport.getLinks().get(0).getToPoleObject();

		// Check if status is updated to NOT UPLOADED in pole
		Assert.assertTrue("Expected DocumentVersion.status to be set to 'NOT UPLOADED'",
				document.getVersions().size() > 0
						&& DOCVERSION_STATUS_NOT_UPLOADED.equals(document.getVersions().get(0).getStatus()));

		// Check if put pole response contains documents with attachmentHandle
		Assert.assertTrue("PutPoleObjectsResponseDto does not contain Document", responseDto.getPoleObjects().size() > 0
				&& responseDto.getPoleObjects().get(0).getEntityType().equals("Document"));
		
		DocumentDto doc = (DocumentDto) responseDto.getPoleObjects().get(0);
		Assert.assertTrue("Document.objectRef is not set in PutPoleObjectsResponseDto", doc.getObjectRef() > 0);
		Assert.assertTrue("Expected atleast one DocumentVersion in PutPoleObjectsResponseDto",
				doc.getVersions().size() > 0);
		
		DocumentVersionDto docVersion = doc.getVersions().get(0);
		Assert.assertTrue("DocumentVersion.objectRef is not set in PutPoleObjectsResponseDto",
				docVersion.getObjectRef() > 0);
		Assert.assertTrue("UUIDs does not match in request response",
				docVersion.getUuid().equals(document.getVersions().get(0).getUuid()));
		Assert.assertTrue("Attachment handle is not returned in PutPoleObjectsResponseDto",
				docVersion.getAttachmentHandle() != null);

	}
	
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(mingleRef = minglRef_37396, 
											mingleTitle = mingleTitle_37396, 
											acceptanceCriteriaRefs = "CR32", 
											given = "External system makes a call to BusinessService to create an event with Document objects & later calls DocumentManagement to upload the document", 
											when = "Event is submited", then = "BusinessService will create the Document and related objects with DocumentVersion.status = 'NotUploaded' " +
											"and after the file upload DocumentVersion.status is changed to 'Generated'", workPackage = workPackage)
	public void testHappyPathWhereEventIsSavedBeforeAttachmentUpload() throws Exception {

		DocumentDto document = (DocumentDto) intelReport.getLinks().get(0).getToPoleObject();
		document.getVersions().get(0).setStatus(DOCVERSION_STATUS_NOT_UPLOADED);
		document.getVersions().get(0).setDocumentSize(null);

		// Create an IntelReport with Document object linked to it
		PutPoleObjectsResponseDto responseDto = doCreateIntelReport(intelReport);

		document = (DocumentDto) intelReport.getLinks().get(0).getToPoleObject();
		
		// Check if hash algorithm and encryption algorithm are set on documentVersion
		DocumentVersionDto documentVersion = document.getVersions().get(0);
		
		Assert.assertTrue("HashAlgorithm must be null before attachment is uploaded",
				documentVersion.getHashAlgorithm() == null);
		Assert.assertTrue("EncryptionAlgorithm must be null before attachment is uploaded",
				documentVersion.getEncryptionAlgorithm() == null);
		Assert.assertTrue("DocumentVerson.status should be NOT UPLOADED",
				DOCVERSION_STATUS_NOT_UPLOADED.equals(documentVersion.getStatus()));
		
		DocumentDto docInPutResponse = (DocumentDto) responseDto.getPoleObjects().get(0);
		DocumentVersionDto docVersionInPutResponse = docInPutResponse.getVersions().get(0);
		
		Assert.assertTrue("Attachment handle is not returned in PutPoleObjectsResponseDto",
				docVersionInPutResponse.getAttachmentHandle() != null);
		
		// Upload binary attachment
		UploadBinaryDataRequest uploadRequest = createUploadBinaryRequest(documentVersion.getUuid());
		uploadRequest.setAttachmentHandle(docVersionInPutResponse.getAttachmentHandle());
		
		uploadBinaryAttachment(uploadRequest);
		
		// Check if hash algorithm and encryption algorithm are set on documentVersion
		checkForDocumentUpdateAfterAttachmentUpload(docInPutResponse.getObjectRef());
		
	}
	
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(mingleRef = minglRef_37396, 
											mingleTitle = mingleTitle_37396, 
											acceptanceCriteriaRefs = "CR32", 
											given = "External system makes a call to BusinessService to create an event with Document objects & later calls DocumentManagement to upload the document", 
											when = "Event is submited", then = "BusinessService will create the Document and related objects with DocumentVersion.status = 'NotUploaded' " +
											"and after the file upload DocumentVersion.status is changed to 'Generated'", workPackage = workPackage)
	public void testAttachmentUploadWhenDocumentIsAlreadyLocked() throws Exception {

		DocumentDto document = (DocumentDto) intelReport.getLinks().get(0).getToPoleObject();
		document.getVersions().get(0).setStatus(DOCVERSION_STATUS_NOT_UPLOADED);
		document.getVersions().get(0).setDocumentSize(null);

		// Create an IntelReport with Document object linked to it
		PutPoleObjectsResponseDto responseDto = doCreateIntelReport(intelReport);

		document = (DocumentDto) intelReport.getLinks().get(0).getToPoleObject();
		// The attachment handle will be returned in put pole response
		DocumentDto docInPutResponse = (DocumentDto) responseDto.getPoleObjects().get(0);
		DocumentVersionDto docVersionInPutResponse = docInPutResponse.getVersions().get(0);
		
		Assert.assertTrue("Attachment handle is not returned in PutPoleObjectsResponseDto",
				docVersionInPutResponse.getAttachmentHandle() != null);
		
		// Acquire lock on the created Document object as a User1
		intelUtils.acquireLockOnObject(poleBusinessServices, PoleNames.DOCUMENT, docInPutResponse.getObjectRef(), document.getVersion(), securityContextId);
		
		// Upload binary attachment
		UploadBinaryDataRequest uploadRequest = createUploadBinaryRequest(document.getVersions().get(0).getUuid());
		uploadRequest.setAttachmentHandle(docVersionInPutResponse.getAttachmentHandle());
		
		uploadBinaryAttachment(uploadRequest);
		
		// Asynchronous update will be started by attachment upload service as a DocMgmt trusted component. 
		// It will not finish as the Document is already locked
		
		// User1 updates some fields on Document and DocumentVersion
		updateDocument(document);
		
		// sleep for sometime
		Thread.sleep(5000);
		
		// User1 releases lock on document
		intelUtils.releaseLockOnObject(poleBusinessServices, PoleNames.DOCUMENT, docInPutResponse.getObjectRef(), securityContextId);
		
		// sleep for sometime
		Thread.sleep(5000);
		
		// Asynchronous update started by upload service should have acquired the lock and completed its processing.
		// Check if its complete.
		document = checkForDocumentUpdateAfterAttachmentUpload(docInPutResponse.getObjectRef());
		
		// Check if all updates are preserved
		Assert.assertTrue("Update done on Document.description is lost",
				"Test doc 123".equals(document.getDescription()));
		Assert.assertTrue("Update done on DocumentVersion.filename is lost",
				"xyz.txt".equals(document.getVersions().get(0).getFileName()));
	}
	
	
	private void updateDocument(DocumentDto document) {
		document.setDescription("Test doc 123");
		document.setModificationStatus(ModificationStatusDto.UPDATE);
		
		DocumentVersionDto documentVersion = document.getVersions().get(0);
		documentVersion.setFileName("xyz.txt");
		documentVersion.setModificationStatus(ModificationStatusDto.UPDATE);
		
		PutPoleObjectsRequestDto updateDocumentRequest = new PutPoleObjectsRequestDto();
		addBusinessServiceInfo(updateDocumentRequest, BusinessServicesTestUtils.createBusinessServiceInfo(securityContextId));
		updateDocumentRequest.addPoleObject(document);
		
		poleDirect.putPoleObjects(updateDocumentRequest);
	}
	
	
	private DocumentDto checkForDocumentUpdateAfterAttachmentUpload(Integer documentObjectRef) throws Exception {
		
		GetPoleObjectsRequestDto getPoleObjectsRequest = getDocumentRequest(documentObjectRef);
		GetPoleObjectsResponseDto getPoleObjectsResponseDto = null;
		boolean asyncUpdateSuccessful  = false;
		
		long timeStart = System.currentTimeMillis();
		while (!asyncUpdateSuccessful && System.currentTimeMillis() - timeStart < 30 * 1000) {
			getPoleObjectsResponseDto = poleDirect.getPoleObjects(getPoleObjectsRequest);
			
			if (getPoleObjectsResponseDto.getPoleObjects().size() > 0) {
				DocumentDto doc = (DocumentDto)getPoleObjectsResponseDto.getPoleObjects().get(0);
				DocumentVersionDto docVer = doc.getVersions().size() > 0 ? doc.getVersions().get(0) : null;
				if (docVer != null && docVer.getHashAlgorithm() != null && docVer.getEncryptionAlgorithm() != null && 
						DOCVERSION_STATUS_GENERATED.equals(docVer.getStatus())) {
					asyncUpdateSuccessful = true;
				}
			}
			
			if (! asyncUpdateSuccessful) {
				try { Thread.sleep(5000); } catch (Exception ex) {}
			} else {
				break;
			}
		}
		
		if (! asyncUpdateSuccessful) {
			throw new Exception("Asynchronous update to DocumentVersion is not finished even after " + ((System.currentTimeMillis() - timeStart) / 1000) + " seconds.");
		}
		return (DocumentDto)getPoleObjectsResponseDto.getPoleObjects().get(0);
	}
	
	private GetPoleObjectsRequestDto getDocumentRequest(Integer objectRef) {
		PoleObjectFieldSelectorDto documentFieldSelector =
				PoleDtoBuildHelper.buildPoleObjectFieldSelectorDto(PoleNames.DOCUMENT);
		documentFieldSelector.addChildObjectFieldSelector(
				PoleDtoBuildHelper.buildChildObjectFieldSelectorDto(PoleNames.DOCUMENT_VERSION));
		
		GetPoleObjectsRequestDto getPoleObjectsRequest = new GetPoleObjectsRequestDto();
		getPoleObjectsRequest.setPoleObjectCriteria(PoleDtoBuildHelper.buildPoleObjectCriteriaDto(PoleNames.DOCUMENT,
				PoleDtoBuildHelper.buildIntegerCriterionDto("objectRef", objectRef)));
		getPoleObjectsRequest.setPoleObjectFieldSelector(documentFieldSelector);
		getPoleObjectsRequest.setRetrievalType(RetrievalTypeDto.OPEN);
		
		intelUtils.setHeaderOnPoleRequest(getPoleObjectsRequest, securityContextId);
		
		return getPoleObjectsRequest;
	}
	
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(mingleRef = minglRef_36872, 
											mingleTitle = mingleTitle_36872, 
											acceptanceCriteriaRefs = "CR1", 
											given = "A 3rd party User tries to upload a document/ image/ video via business service and "
													+ "the attachment is not linked to any of the relevant event", 
											when = "Event is submited", then = "Validation error is returned", workPackage = workPackage)
	public void testWhenDocumentIsNotLinkedToAnyEvent() throws Exception {

		try {
			DocumentDto document = CommonPoleObjectTestUtils.getDocument();
			document.setResearched(Boolean.TRUE);
			document.addVersion(CommonPoleObjectTestUtils.getDocumentVersion());
			
			TagDto tag = new TagDto();
			tag.setModificationStatus(ModificationStatusDto.CREATE);
			tag.setTagValueLevel1("DOCUMENT");
			tag.setTagValueLevel2("NON MG FORM");	
			document.addTag(tag);

			PutPoleObjectsRequestDto putPoleObjectsRequest = new PutPoleObjectsRequestDto();
			addBusinessServiceInfo(putPoleObjectsRequest, BusinessServicesTestUtils.createBusinessServiceInfo(securityContextId));
			putPoleObjectsRequest.addPoleObject(document);
			
			poleBusinessServices.putPoleObjects(putPoleObjectsRequest);
			fail("Expected an exception for invalid data");

		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(), ide.getMessage()
					.contains("Document must be linked to an event or to a static object linked to an event"));

		}
	}
	
	private void addDocumentLinkToIntelReport(String uuid, String attachmentHandle) {
		DocumentDto document = CommonPoleObjectTestUtils.getDocument();
		document.addVersion(CommonPoleObjectTestUtils.getDocumentVersion());
		
		document.getVersions().get(0).setUuid(uuid);
		document.getVersions().get(0).setAttachmentHandle(attachmentHandle);
		
		TagDto tag = new TagDto();
		tag.setModificationStatus(ModificationStatusDto.CREATE);
		tag.setTagValueLevel1("DOCUMENT");
		tag.setTagValueLevel2("NON MG FORM");	
		document.addTag(tag);
		
		LinkDto intelReportDocumentLink = new LinkDto();
		intelReportDocumentLink.setModificationStatus(ModificationStatusDto.CREATE);
		intelReportDocumentLink.setResearched(Boolean.TRUE);
		intelReportDocumentLink.setSourcePoleObjectType(PoleNames.INTELLIGENCE_REPORT);
		intelReportDocumentLink.setSourcePoleObjectRef(intelReport.getObjectRef());
		intelReportDocumentLink.setToPoleObject(document);
		intelReportDocumentLink.setLinkReason(PoleObjectsServiceConstants.DOCUMENT_PRODUCTION_LINK_REASON);
		intelReport.addLink(intelReportDocumentLink);
	}

	private UploadBinaryDataRequest createUploadBinaryRequest(String uuid) {
		UploadBinaryDataRequest request = new UploadBinaryDataRequest();

		GemMetaData metadata = new GemMetaData();
		metadata.setClientName("BusSvcFuncTest");
		metadata.setSecurityContextId(securityContextId);
		request.setGemMetaData(metadata);

		PayLoad payload = new PayLoad();
		DataHandler datahandler = new DataHandler(BYNARY_FILE_CONTENT, GENERIC_MIME_TYPE);
		payload.setMessagePayLoad(datahandler);
		request.setPayLoad(payload);

		request.setBinaryDataType(AttachmentType.DOCUMENT);
		request.setUuid(uuid);

		return request;
	}

	private String uploadBinaryAttachment(UploadBinaryDataRequest request) {

		UploadBinaryDataResponse response = attachmentsService.uploadBinaryData(request);

		Assert.assertTrue("UploadBinaryDataResponse is null", response != null);
		Assert.assertTrue(response.getFailureMessage() == null ? "UploadBinaryDataResponse.status is not Success "
				: response.getFailureMessage(), response.getStatus() == Status.SUCCESS);

		return response.getAttachmentHandle();
	}

	private PutPoleObjectsResponseDto doCreateIntelReport(PoleObjectDto poleObjectDto) throws Exception {

		PutPoleObjectsRequestDto putPoleObjectsRequest = intelUtils.createPutPoleObjectsRequestDto(
				BusinessServicesTestUtils.createBusinessServiceInfo(securityContextId), (IntelligenceReportDto) poleObjectDto);

		PutPoleObjectsResponseDto poleObjectsResponse = poleBusinessServices.putPoleObjects(putPoleObjectsRequest);
		Integer incObjRef = IntelReportPoleDtoUtils.extractIntelReportObjectRef(poleObjectsResponse);

		intelReport = intelUtils.getIntelligenceReportFromPole(incObjRef, 2, poleBusinessServices, securityContextId);
		assertEquals(intelReport.getObjectRef(), incObjRef);
		
		return poleObjectsResponse;	
	}

	private void doCreateIncident(PoleObjectDto poleObjectDto) throws Exception {

		PutPoleObjectsRequestDto putPoleObjectsRequest = utils.createPutPoleObjectsRequestDto(
				BusinessServicesTestUtils.createBusinessServiceInfo(securityContextId), (IncidentDto) poleObjectDto);

		PutPoleObjectsResponseDto poleObjectsResponse = poleBusinessServices.putPoleObjects(putPoleObjectsRequest);
		Integer incObjRef = IncidentPoleDtoUtils.extractIncidentObjectRef(poleObjectsResponse);

		incident = utils.getIncidentFromPole(incObjRef, poleBusinessServices, securityContextId);
		assertEquals(incident.getObjectRef(), incObjRef);

	}

}
