package com.northgateis.gem.bussvc.statecache.functest;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import com.northgateis.gem.bussvc.framework.FrameworkServiceConstants;
import com.northgateis.gem.bussvc.statecache.SavingOfDataConstants;
import com.northgateis.gem.security.schema.beans.generated.GemSecurityUserDTO;
import com.northgateis.pole.client.PoleDtoBuildHelper;
import com.northgateis.pole.common.LockAcquisitionFailureException;
import com.northgateis.pole.common.PoleNames;
import com.northgateis.pole.schema.AcquireLocksRequestDto;
import com.northgateis.pole.schema.AcquireLocksResponseDto;
import com.northgateis.pole.schema.AssignedAuxiliaryDataKeyDto;
import com.northgateis.pole.schema.AuxiliaryDataCriteriaDto;
import com.northgateis.pole.schema.ChildObjectFieldSelectorDto;
import com.northgateis.pole.schema.DocumentDto;
import com.northgateis.pole.schema.FlagDto;
import com.northgateis.pole.schema.FlagTeamDto;
import com.northgateis.pole.schema.FlagTeamTypeDto;
import com.northgateis.pole.schema.FlagTypeDto;
import com.northgateis.pole.schema.GemCaseDto;
import com.northgateis.pole.schema.GetAuxiliaryDataRequestDto;
import com.northgateis.pole.schema.GetAuxiliaryDataResponseDto;
import com.northgateis.pole.schema.GetPoleObjectsResponseDto;
import com.northgateis.pole.schema.LinkDto;
import com.northgateis.pole.schema.LinkFieldSelectorDto;
import com.northgateis.pole.schema.LockDetailDto;
import com.northgateis.pole.schema.LockDto;
import com.northgateis.pole.schema.MatchOperatorDto;
import com.northgateis.pole.schema.ModificationStatusDto;
import com.northgateis.pole.schema.PoleObjectCriteriaDto;
import com.northgateis.pole.schema.PoleObjectDto;
import com.northgateis.pole.schema.PoleObjectFieldSelectorDto;
import com.northgateis.pole.schema.PutAuxiliaryDataRequestDto;
import com.northgateis.pole.schema.PutAuxiliaryDataResponseDto;
import com.northgateis.pole.schema.PutPoleObjectsResponseDto;
import com.northgateis.pole.schema.QueryLocksRequestDto;
import com.northgateis.pole.schema.QueryLocksResponseDto;
import com.northgateis.pole.schema.RecordKeyDto;
import com.northgateis.pole.schema.RetrievalTypeDto;
import com.northgateis.pole.schema.StringCriterionDto;
import com.northgateis.pole.schema.TeamDto;
import com.northgateis.pole.schema.TeamEmployeeDto;

public class Story34889StateCacheFuncTests extends AbstractStateCacheFuncTestBase {
	
	private final static String CASE_PREFIX = "Story34889FuncTest";
	
	private final static String DOCUMENT_DESCRITPION = "StateCacheFuncTestDocumentDescription";
	private final static String DOCUMENT_NAME = "StateCacheFuncTestDocumentName";
	private final static String TEAM_NAME = "Story34889StateCacheFuncTestTeam";
	
	private final static String USER_ID = "Rob Hornsby";
	private final static String ALTERNATIVE_USER_ID = "Rob Hornsby 2";

	private String caseRef = null;

	@Override
	protected void setupImpl() throws Exception {
		// generate a unique case reference starting with the prefix so all cases created by this
		// functional test can be identified in Pole and deleted
		caseRef = CASE_PREFIX + new BigInteger(12, new Random(System.currentTimeMillis())).toString();
		// cases created previously by this test should have been deleted at the end of each test
		// but in case anything went wrong before we delete them here 
		tryToDeleteOldCases();
	}	
	
	@Override
	protected void teardownImpl() throws Exception {	
		try {
			// try to delete the case we created in the test any any ones from previous tests which we failed to delete
			tryToDeleteOldCases();
		} finally {
			securityContextId = null;
			viewOnlySecurityContextId = null;
		}
	}
	
	@Override
	protected void createMainSecurityContextId() throws Exception {
		//New user for every test due to need to safely reset session-id based state in the SOD
		securityContextId = busSvcUtils.getSecurityContextId(USER_ID, Arrays.asList("ATHENA_USER"), securityService);
	}	
	
	@Override
	protected void createViewOnlySecurityContextId() throws Exception {
		//New user for every test due to need to safely reset session-id based state in the SOD.
		//This second user hardly used!
		viewOnlySecurityContextId = busSvcUtils.getSecurityContextId(ALTERNATIVE_USER_ID, Arrays.asList("ATHENA_USER"), securityService);
	}
	
	/**
	 * Tests that if we create a Case as a primary app and then create a document linked to the case as a secondary app 
	 * then the document is only created in Pole and linked to the case when we do another put on the case as a primary
	 * app. I.E. it is the primary apps put of the main record which commits the objects in the state cache.
	 */
	@Test
	public void collateAndForwardTest() {
	
		GemCaseDto gemCase = prepareNewGemCase(null, caseRef);
		gemCase.setModificationStatus(ModificationStatusDto.CREATE);

		PutPoleObjectsResponseDto createCaseResponse = putPoleObject(gemCase, PRIMARY_APP_CLIENT_NAME);
		assertTrue("A case should have been created and so a single entity key should have been returned", 
				createCaseResponse.getEntityKeys().size() > 0);
		int createdCaseObjectRef = createCaseResponse.getEntityKeys().get(0).getObjectRef();
		
		// simulate getting the record from the primary app and an adornment after it was created
		getCaseFromBusinessServices(createdCaseObjectRef, PRIMARY_APP_CLIENT_NAME);
		gemCase = getCaseFromBusinessServices(createdCaseObjectRef, SECONDARY_APP_CLIENT_NAME);
		
		GemCaseDto skeletongGemCase = prepareNewGemCase(createdCaseObjectRef, null);
		DocumentDto document = createDocument();
		document.setModificationStatus(ModificationStatusDto.CREATE);
		
		LinkDto link = new LinkDto();
		link.setModificationStatus(ModificationStatusDto.CREATE);
		link.setToPoleObject(document);
		link.setLinkReason("DOCUMENT PRODUCTION");
		link.setSourcePoleObjectRef(createdCaseObjectRef);
		link.setSourcePoleObjectType(PoleNames.GEM_CASE);
		skeletongGemCase.addLink(link);
		
		PutPoleObjectsResponseDto putCaseAsSecondardAppResponse = putPoleObject(skeletongGemCase, SECONDARY_APP_CLIENT_NAME);
		assertTrue("Because the document was put by a secondary app it should not have been created in Pole and so no entity key should have been returned", 
				putCaseAsSecondardAppResponse.getEntityKeys().isEmpty());
		
		gemCase = getCaseFromBusinessServices(createdCaseObjectRef, PRIMARY_APP_CLIENT_NAME);
		gemCase.setModificationStatus(ModificationStatusDto.UPDATE);
		PutPoleObjectsResponseDto updateCaseResponse = putPoleObject(gemCase, PRIMARY_APP_CLIENT_NAME);
		assertEquals(2, updateCaseResponse.getEntityKeys().size());
		assertEquals("The last Put should have created the Document in Pole which we Put as a secondary app", 
				PoleNames.DOCUMENT, updateCaseResponse.getEntityKeys().get(1).getEntityType());
		Integer documentObjectRef = updateCaseResponse.getEntityKeys().get(1).getObjectRef();
		
		List<? extends PoleObjectDto> cases = getCaseWithLinkedDocuments(createdCaseObjectRef, PRIMARY_APP_CLIENT_NAME);
		assertTrue("Exactly one case should have been returned because get got it by object ref and type", 
				cases.size() == 1);
		assertTrue("A case should have been returned because get got it by object ref and type", 
				cases.get(0) instanceof GemCaseDto);
		GemCaseDto createdCase = (GemCaseDto) cases.get(0);
		String caseShouldHaveLinkedDocumentErrorMessage = "The case should have a single link to a document because Collate And forward should have added the document created by "
				+ "commonsvcs to the case created by GUARDIAN and committed them to Pole";
		assertEquals(caseShouldHaveLinkedDocumentErrorMessage, 1, createdCase.getLinks().size());
		assertTrue(caseShouldHaveLinkedDocumentErrorMessage, createdCase.getLinks().get(0).getToPoleObject() != null);
		assertTrue(caseShouldHaveLinkedDocumentErrorMessage, createdCase.getLinks().get(0).getToPoleObject() instanceof DocumentDto);
		DocumentDto documentAttachedToCase = (DocumentDto) createdCase.getLinks().get(0).getToPoleObject();
		assertEquals("Document objectRef should match the one committed in the Put by the primary app",
				documentObjectRef, documentAttachedToCase.getObjectRef());
		assertEquals("Document description should match the document we created as commonsvcs", 
				DOCUMENT_DESCRITPION, documentAttachedToCase.getDescription());
		assertEquals("Document name should match the document we created as commonsvcs", 
				DOCUMENT_NAME, documentAttachedToCase.getName());
	}
	
	/**
	 * Test that when we open a record as a primary app and trip a flag, the flag will be cached until we request the record with a
	 * transaction description of trips and that then the flag will be returned and then cleared from the cache. 
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCollateAndForwardAndForwardAndCacheResponseActionsWithFlagTrip() throws Exception {
	
		// To create a flag we need to be a member of the owning team so if the user is not in a team then create a team for them,
		GemSecurityUserDTO user = utils.getCurrentUser(securityContextId, securityService);
		int teamId = getTeamId(TEAM_NAME, Integer.parseInt(user.getPoleEmployeeId()));
		
		// create a new case record
		GemCaseDto gemCase = prepareNewGemCase(null, caseRef);
		gemCase.setModificationStatus(ModificationStatusDto.CREATE);
		PutPoleObjectsResponseDto createCaseResponse = putPoleObject(gemCase, PRIMARY_APP_CLIENT_NAME);
		
		assertTrue("A case should have been created and so a single entity key should have been returned", 
				createCaseResponse.getEntityKeys().size() > 0);
		int createdCaseObjectRef = createCaseResponse.getEntityKeys().get(0).getObjectRef();
		
		// simulate getting the record from the primary app and an adornment after it was created
		getCaseFromBusinessServices(createdCaseObjectRef, PRIMARY_APP_CLIENT_NAME);
		gemCase = getCaseFromBusinessServices(createdCaseObjectRef, SECONDARY_APP_CLIENT_NAME);
		
		GemCaseDto skeletongGemCase = prepareNewGemCase(createdCaseObjectRef, null);

		FlagDto flag = new FlagDto();
		flag.setModificationStatus(ModificationStatusDto.CREATE);
		flag.setMessage("Welcome to fun with flags");
		flag.setFlagType(FlagTypeDto.OVERT);
		flag.setTripOnSearchHit(true);
		flag.setTripOnOpen(true);
		flag.setTripOnUpdate(true);
		flag.setTripOnLink(true);
		
		FlagTeamDto flagTeam = new FlagTeamDto();
		flagTeam.setTeamId(teamId);
		flagTeam.setModificationStatus(ModificationStatusDto.CREATE);
		flagTeam.setType(FlagTeamTypeDto.OWNING);
		flag.addFlagTeam(flagTeam);
		
		skeletongGemCase.addFlag(flag);
		
		// add a flag to the case
		PutPoleObjectsResponseDto putCaseAsSecondardAppResponse = putPoleObject(skeletongGemCase, SECONDARY_APP_CLIENT_NAME);
		assertTrue("Because the case and flag were put by a secondary app it should not have been created in Pole and so no entity key should have been returned", 
				putCaseAsSecondardAppResponse.getEntityKeys().isEmpty());
		
		gemCase.setModificationStatus(ModificationStatusDto.UPDATE);
		
		// Update the case as the primary app, this should commit the flag to Pole
		putPoleObject(gemCase, PRIMARY_APP_CLIENT_NAME);
		
		// create a new security context as a different user because we will not tip the flag as the user who created it because they are in the owning team
		securityContextId = utils.getSecurityContextId(ALTERNATIVE_USER_ID, Arrays.asList("ATHENA_USER"), securityService);
		
		// now get the case from Pole, this will trip the flag, cache the flag trip and also return the flag trip to the client
		GetPoleObjectsResponseDto response = getFromPole(poleBusinessServices, createdCaseObjectRef, PoleNames.GEM_CASE, PRIMARY_APP_CLIENT_NAME, MEANINGLESS_TRANSACTION_DESCRIPTION);
		assertEquals(1, response.getFlagTrips().size());		
		
		// check that a subsequent get does not return the flag trip because it's transaction description is not 'trips'
		response = getFromPole(poleBusinessServices, createdCaseObjectRef, PoleNames.GEM_CASE, SECONDARY_APP_CLIENT_NAME, MEANINGLESS_TRANSACTION_DESCRIPTION);
		assertEquals(0, response.getFlagTrips().size());
		
		// check that only a get with a transaction description of 'trips' will return the cached flag
		response = getFromPole(poleBusinessServices, createdCaseObjectRef, PoleNames.GEM_CASE, SECONDARY_APP_CLIENT_NAME, SavingOfDataConstants.SOD_FLAG_TRIPS_IDENTIFIER);
		assertEquals(1, response.getFlagTrips().size());
		
		// check that further requests for the case with a transaction description of 'trips' will not return the flag because the cache was cleared
		response = getFromPole(poleBusinessServices, createdCaseObjectRef, PoleNames.GEM_CASE, SECONDARY_APP_CLIENT_NAME, SavingOfDataConstants.SOD_FLAG_TRIPS_IDENTIFIER);	
		assertEquals(0, response.getFlagTrips().size());
		
		// reset the owner of the security context to the user who created the flag so that we can delete the flag in the teardown method
		securityContextId = utils.getSecurityContextId(USER_ID, Arrays.asList("ATHENA_USER"), securityService);
	}
	
	/**
	 * Test that put pole objects from a secondary app are stored in the state cache and and subsequent get pole objects request from a secondary
	 * app will return the cached version.
	 */
	@Test
	public void testServeFromCacheIfPresent() {
	
		GemCaseDto gemCase = prepareNewGemCase(null, caseRef);
		gemCase.setModificationStatus(ModificationStatusDto.CREATE);

		PutPoleObjectsResponseDto createCaseResponse = putPoleObject(gemCase, PRIMARY_APP_CLIENT_NAME);
		assertTrue("A case should have been created and so a single entity key should have been returned", 
				createCaseResponse.getEntityKeys().size() > 0);
		int createdCaseObjectRef = createCaseResponse.getEntityKeys().get(0).getObjectRef();
		
		// simulate getting the record from the primary app and an adornment after it was created
		getCaseFromBusinessServices(createdCaseObjectRef, PRIMARY_APP_CLIENT_NAME);
		gemCase = getCaseFromBusinessServices(createdCaseObjectRef, SECONDARY_APP_CLIENT_NAME);
		
		GemCaseDto skeletongGemCase = prepareNewGemCase(createdCaseObjectRef, null);
		DocumentDto document = createDocument();
		document.setModificationStatus(ModificationStatusDto.CREATE);
		
		LinkDto link = new LinkDto();
		link.setModificationStatus(ModificationStatusDto.CREATE);
		link.setToPoleObject(document);
		link.setLinkReason("DOCUMENT PRODUCTION");
		link.setSourcePoleObjectRef(createdCaseObjectRef);
		link.setSourcePoleObjectType(PoleNames.GEM_CASE);
		skeletongGemCase.addLink(link);
		
		// add the document, but it will be stored in the state cache not Pole
		PutPoleObjectsResponseDto putCaseAsSecondardAppResponse = putPoleObject(skeletongGemCase, SECONDARY_APP_CLIENT_NAME);
		assertTrue("Because the document was put by a secondary app it should not have been created in Pole and so no entity key should have been returned", 
				putCaseAsSecondardAppResponse.getEntityKeys().isEmpty());
		
		// check that the secondary app sees the record from the state cache, this should call 'serveFromCacheIfPresent'
		gemCase = getCaseFromBusinessServices(createdCaseObjectRef, SECONDARY_APP_CLIENT_NAME);	
		assertTrue(hasLinkedDocument(gemCase));
		
		// do the same thing again to check the record was not popped from the cache
		gemCase = getCaseFromBusinessServices(createdCaseObjectRef, SECONDARY_APP_CLIENT_NAME);	
		assertTrue(hasLinkedDocument(gemCase));
		
		// check that as a primary app we don't see the record from the state cache
		gemCase = getCaseFromBusinessServices(createdCaseObjectRef, PRIMARY_APP_CLIENT_NAME);
		assertFalse(hasLinkedDocument(gemCase));
		
		// check that the record is not in Pole, i.e. confirm we are seeing the state cache version
		gemCase = getCaseDirectlyFromPole(createdCaseObjectRef, SECONDARY_APP_CLIENT_NAME);	
		assertFalse(hasLinkedDocument(gemCase));		
	}
	
	/**
	 * test the same functionality as collateAndForwardTest above but where gets are done a getAsXmlDocument request
	 */
	@Test
	public void collateAndForwardAsXmlTest() {
	
		if (isJsonMode()) {
			return; //GetPoleObjectsAsXmlDocument is not supported over REST.
		}
		
		GemCaseDto gemCase = prepareNewGemCase(null, caseRef);
		gemCase.setModificationStatus(ModificationStatusDto.CREATE);

		PutPoleObjectsResponseDto createCaseResponse = putPoleObject(gemCase, PRIMARY_APP_CLIENT_NAME);
		assertTrue("A case should have been created and so a single entity key should have been returned", 
				createCaseResponse.getEntityKeys().size() > 0);
		int createdCaseObjectRef = createCaseResponse.getEntityKeys().get(0).getObjectRef();
		
		// simulate getting the record from the primary app and an adornment after it was created
		getCaseFromBusinessServicesAsXml(createdCaseObjectRef, PRIMARY_APP_CLIENT_NAME);
		gemCase = getCaseFromBusinessServicesAsXml(createdCaseObjectRef, SECONDARY_APP_CLIENT_NAME);
		
		GemCaseDto skeletonCase = prepareNewGemCase(createdCaseObjectRef, null);
		DocumentDto document = createDocument();
		document.setModificationStatus(ModificationStatusDto.CREATE);
		
		LinkDto link = new LinkDto();
		link.setModificationStatus(ModificationStatusDto.CREATE);
		link.setToPoleObject(document);
		link.setLinkReason("DOCUMENT PRODUCTION");
		link.setSourcePoleObjectRef(createdCaseObjectRef);
		link.setSourcePoleObjectType(PoleNames.GEM_CASE);
		skeletonCase.addLink(link);
		
		PutPoleObjectsResponseDto putCaseAsSecondardAppResponse = putPoleObject(skeletonCase, SECONDARY_APP_CLIENT_NAME);
		assertTrue("Because the document was put by a secondary app it should not have been created in Pole and so no entity key should have been returned", 
				putCaseAsSecondardAppResponse.getEntityKeys().isEmpty());
		
		gemCase = getCaseFromBusinessServicesAsXml(createdCaseObjectRef, PRIMARY_APP_CLIENT_NAME);
		doLockCase(gemCase.getObjectRef(), gemCase.getVersion());
		gemCase.setModificationStatus(ModificationStatusDto.UPDATE);
		PutPoleObjectsResponseDto updateCaseResponse = putPoleObject(gemCase, PRIMARY_APP_CLIENT_NAME);
		assertEquals(2, updateCaseResponse.getEntityKeys().size());
		assertEquals("The last Put should have created the Document in Pole which we Put as a secondary app", 
				PoleNames.DOCUMENT, updateCaseResponse.getEntityKeys().get(1).getEntityType());
		Integer documentObjectRef = updateCaseResponse.getEntityKeys().get(1).getObjectRef();
		
		List<? extends PoleObjectDto> cases = getCaseWithLinkedDocuments(createdCaseObjectRef, PRIMARY_APP_CLIENT_NAME);
		assertEquals("Exactly one case should have been returned because get got it by object ref and type",
				1, cases.size());
		assertTrue("A case should have been returned because get got it by object ref and type", 
				cases.get(0) instanceof GemCaseDto);
		GemCaseDto createdCase = (GemCaseDto) cases.get(0);
		String caseShouldHaveLinkedDocumentErrorMessage = "The case should have a single link to a document because Collate And forward should have added the document created by "
				+ "commonsvcs to the case created by GUARDIAN and committed them to Pole";
		assertEquals(caseShouldHaveLinkedDocumentErrorMessage, 1, createdCase.getLinks().size());
		assertTrue(caseShouldHaveLinkedDocumentErrorMessage, createdCase.getLinks().get(0).getToPoleObject() != null);
		assertTrue(caseShouldHaveLinkedDocumentErrorMessage, createdCase.getLinks().get(0).getToPoleObject() instanceof DocumentDto);
		DocumentDto documentAttachedToCase = (DocumentDto) createdCase.getLinks().get(0).getToPoleObject();
		assertEquals("Document objectRef should match the one committed in the Put by the primary app",
				documentObjectRef, documentAttachedToCase.getObjectRef());
		assertEquals("Document description should match the document we created as commonsvcs", 
				DOCUMENT_DESCRITPION, documentAttachedToCase.getDescription());
		assertEquals("Document name should match the document we created as commonsvcs", 
				DOCUMENT_NAME, documentAttachedToCase.getName());
	}
	
	/**
	 * Utility method to prevent CCIS from grabbing locks (causes intermittent FT failures) 
	 * 
	 * @param objectRef
	 * @param version
	 * @return
	 */
	private AcquireLocksResponseDto doLockCase(Integer objectRef, Integer version) {

		AcquireLocksRequestDto acquireLocks = new AcquireLocksRequestDto();
		setHeaderOnRequest(acquireLocks, PRIMARY_APP_CLIENT_NAME, MEANINGLESS_TRANSACTION_DESCRIPTION);
		LockDto lock = new LockDto();
		RecordKeyDto recordKey = new RecordKeyDto();
		recordKey.setPk(Integer.toString(objectRef));
		recordKey.setRecordType(PoleNames.GEM_CASE);
		lock.addRecordKey(recordKey);
		lock.setRecordVersionOrDeepVersion(version);
		lock.setDeep(false);
		acquireLocks.addLock(lock);

		AcquireLocksResponseDto response = null;
		
		/*
		 * We sometimes get errors like this when running the tests:
		 * 
		 * com.northgateis.pole.common.LockAcquisitionFailureException: 
		 * No locks were acquired for user 13582/3a073a53-0c27-40f7-8a11-8f02fd7135c4 
		 * because 1 locks were unavailable: Case 1372041 cannot be acquired because it 
		 * is held by user 14676/e93f0f1b-602b-43c2-b440-9ff8039d2c36 at terminal 
		 * 'CCIS_SERVER', due to expire in 28800 second(s).
		 * 
		 * ...so this next bit is in a loop to avoid it.
		 */
		for (int attempts = 0; attempts < 5; attempts++) {
			try {
				response = poleBusinessServices.acquireLocks(acquireLocks);
			} catch (LockAcquisitionFailureException lafe) {
				logger.debug("Caught exception: ", lafe);
				if (attempts > 3) {					
					throw lafe;//rethrow original ex
				}
				try { Thread.sleep(2000); } catch (Exception ex) {}
			}
		}
		
		return response;
	}

	/**
	 * Test that a call to acquire locks in business services does acquire a lock in Pole.
	 */
	@Test
	public void acquireLocksTest() {
		GemCaseDto gemCase = prepareNewGemCase(null, caseRef);
		gemCase.setModificationStatus(ModificationStatusDto.CREATE);

		PutPoleObjectsResponseDto createCaseResponse = putPoleObject(gemCase, poleDirect, PRIMARY_APP_CLIENT_NAME);
		assertTrue("A case should have been created and so a single entity key should have been returned", 
				createCaseResponse.getObjectRefs().size() > 0);
		int createdCaseObjectRef = createCaseResponse.getObjectRefs().get(0);
		GetPoleObjectsResponseDto getPoleObjectsResponse = getFromPole(poleBusinessServices, createdCaseObjectRef, PoleNames.GEM_CASE, PRIMARY_APP_CLIENT_NAME, MEANINGLESS_TRANSACTION_DESCRIPTION);
		assertTrue("A case should have been created and so we should have been able to retrieve it by ID",  getPoleObjectsResponse.getPoleObjects().size() == 1);
		assertTrue("A case should have been created and so we should have been able to retrieve it by ID", getPoleObjectsResponse.getPoleObjects().get(0) instanceof GemCaseDto);
		
		AcquireLocksRequestDto acquireLocks = new AcquireLocksRequestDto();
		setHeaderOnRequest(acquireLocks, PRIMARY_APP_CLIENT_NAME, MEANINGLESS_TRANSACTION_DESCRIPTION);
		LockDto lock = new LockDto();
		RecordKeyDto recordKey = new RecordKeyDto();
		recordKey.setPk(Integer.toString(createdCaseObjectRef));
		recordKey.setRecordType(PoleNames.GEM_CASE);
		lock.addRecordKey(recordKey);
		lock.setRecordVersionOrDeepVersion(getPoleObjectsResponse.getPoleObjects().get(0).getVersion());
		lock.setDeep(false);
		acquireLocks.addLock(lock);

		AcquireLocksResponseDto response = null;
		
		/*
		 * We sometimes get errors like this when running the tests:
		 * 
		 * com.northgateis.pole.common.LockAcquisitionFailureException: 
		 * No locks were acquired for user 13582/3a073a53-0c27-40f7-8a11-8f02fd7135c4 
		 * because 1 locks were unavailable: Case 1372041 cannot be acquired because it 
		 * is held by user 14676/e93f0f1b-602b-43c2-b440-9ff8039d2c36 at terminal 
		 * 'CCIS_SERVER', due to expire in 28800 second(s).
		 * 
		 * ...so this next bit is in a loop to avoid it.
		 */
		for (int attempts = 0; attempts < 5; attempts++) {
			try {
				response = poleBusinessServices.acquireLocks(acquireLocks);
			} catch (LockAcquisitionFailureException lafe) {
				logger.debug("Caught exception: ", lafe);
				if (attempts > 3) {					
					throw lafe;//rethrow original ex
				}
				try { Thread.sleep(2000); } catch (Exception ex) {}
			}
		}
		
		assertTrue("We should have successfully acquired a lock and so the lock report should have been returned", response.getLockReports().size() == 1);
		assertTrue("We should have successfully acquired a lock and so LockAcquired should be true", response.getLockReports().get(0).isLockAcquired());
		
		// query directly to Pole to make sure the lock is in Pole
		QueryLocksRequestDto queryLocksRequestDto = new QueryLocksRequestDto();
		setHeaderOnRequest(queryLocksRequestDto, PRIMARY_APP_CLIENT_NAME, MEANINGLESS_TRANSACTION_DESCRIPTION);
		QueryLocksResponseDto queryLocksResponseDto = poleDirect.queryLocks(queryLocksRequestDto);
		assertTrue("Expected a lock. Lock count found:" 
				+ queryLocksResponseDto.getLockDetailList().size(), 
				queryLocksResponseDto.getLockDetailList().size() > 0);
		boolean foundLock = false;
		// it is possible this user has locks on more than one record to check we have a lock on the correct record
		for (LockDetailDto lockDetails : queryLocksResponseDto.getLockDetailList()) {
			if (Integer.toString(createdCaseObjectRef).equals(lockDetails.getRecordKeyPath().get(0).getPk())
					&& PoleNames.GEM_CASE.equals(lockDetails.getRecordKeyPath().get(0).getRecordType())) {
				foundLock = true;
			}
		}
		assertTrue("Could not find a lock for the correct record", foundLock);
	}

	private boolean hasLinkedDocument(GemCaseDto gemCase) {
		if (gemCase.getLinks() != null) {
			for (LinkDto link : gemCase.getLinks()) {
				if (PoleNames.DOCUMENT.equals(link.getToPoleObjectType()) || (link.getToPoleObject() != null && link.getToPoleObject() instanceof DocumentDto)) {
					return true;
				}
			}
		}
		return false;
	}

	// we need a team ID for the team to own the flag, it must be a team the alt user is not a member of so we use a special team for this test
	// if the team does not already exist then we create it
	private int getTeamId(String teamName, int poleEmployeeId) {
		GetAuxiliaryDataRequestDto getAuxiliaryDataRequestDto = new GetAuxiliaryDataRequestDto();
		
		AuxiliaryDataCriteriaDto auxiliaryDataCriteria = new AuxiliaryDataCriteriaDto();
		StringCriterionDto stringCriterionDto = new StringCriterionDto("name", teamName, MatchOperatorDto.EQUALS, false);
		auxiliaryDataCriteria.addFieldCriterion(stringCriterionDto);
		auxiliaryDataCriteria.setEntityType(PoleNames.TEAM);

		getAuxiliaryDataRequestDto.setAuxiliaryDataCriteria(auxiliaryDataCriteria);
		setHeaderOnRequest(getAuxiliaryDataRequestDto, PRIMARY_APP_CLIENT_NAME, MEANINGLESS_TRANSACTION_DESCRIPTION);
		getAuxiliaryDataRequestDto.setAuxiliaryDataCriteria(auxiliaryDataCriteria);
		GetAuxiliaryDataResponseDto resp = poleDirect.getAuxiliaryData(getAuxiliaryDataRequestDto);
		if (resp.getAuxiliaryDataList().isEmpty()) {
			// the team does not exist so we create it
			return createTeam(teamName, poleEmployeeId);
		} else {
			// the team already exists so we return it's ID
			return ( (TeamDto) resp.getAuxiliaryDataList().get(0)).getId();
		}
	}
	
	private int createTeam(String teamName, int poleEmployeeId) {
		
		TeamDto team = new TeamDto();
		team.setModificationStatus(ModificationStatusDto.CREATE);
		team.setName(teamName);
		team.setId(-1);
		TeamEmployeeDto teamEmployee = new TeamEmployeeDto();
		teamEmployee.setModificationStatus(ModificationStatusDto.CREATE);
		teamEmployee.setTeamId(-1);		
		teamEmployee.setEmployeeId(poleEmployeeId);
		
		PutAuxiliaryDataRequestDto auxDataRequest = new PutAuxiliaryDataRequestDto();
		auxDataRequest.addAuxiliaryData(team);
		auxDataRequest.addAuxiliaryData(teamEmployee);
		setHeaderOnRequest(auxDataRequest, SECONDARY_APP_CLIENT_NAME, MEANINGLESS_TRANSACTION_DESCRIPTION);
		PutAuxiliaryDataResponseDto auxDataResponse = poleDirect.putAuxiliaryData(auxDataRequest);
		for (AssignedAuxiliaryDataKeyDto key : auxDataResponse.getAuxiliaryDataKeys()) {
			if (PoleNames.TEAM.equals(key.getEntityType())) {
				return key.getPrimaryKey();
			}
		}
		return 0;
	}

	private List<? extends PoleObjectDto> getCaseWithLinkedDocuments(int caseObjectRef, String clientName) {

		PoleObjectCriteriaDto rootCriteriaDto = PoleDtoBuildHelper.buildPoleObjectCriteriaDto(PoleNames.GEM_CASE, PoleDtoBuildHelper.buildIntegerCriterionDto("objectRef", caseObjectRef));
		
		PoleObjectFieldSelectorDto rootSelector = new PoleObjectFieldSelectorDto();
		rootSelector.setObjectType(PoleNames.GEM_CASE);
		LinkFieldSelectorDto linkFieldSelectorDto = new LinkFieldSelectorDto();

		PoleObjectFieldSelectorDto documentFieldSelector = new PoleObjectFieldSelectorDto();
		linkFieldSelectorDto.setToPoleObjectFieldSelector(documentFieldSelector);
		documentFieldSelector.setObjectType(PoleNames.DOCUMENT);
		rootSelector.addLinkFieldSelector(linkFieldSelectorDto);
		
		return getPoleObjects(poleDirect, rootCriteriaDto, rootSelector, clientName, RetrievalTypeDto.OPEN);
	}
	
	private DocumentDto createDocument() {
		DocumentDto document = new DocumentDto();
		document.setObjectRef(-1);
		document.setDescription(DOCUMENT_DESCRITPION);
		document.setName(DOCUMENT_NAME);
		return document;
	}

	private void tryToDeleteOldCases() {
		deletePoleObjects(getExistingCases(SECONDARY_APP_CLIENT_NAME));
	}
	
	private List<? extends PoleObjectDto> getExistingCases(String clientName) {
		
		assertTrue(caseRef.length() > 18 && caseRef.length() <= 25);
		StringCriterionDto stringCriterionDto = new StringCriterionDto("caseRef", CASE_PREFIX + "%", MatchOperatorDto.LIKE, false);
		PoleObjectCriteriaDto rootCriteriaDto = PoleDtoBuildHelper.buildPoleObjectCriteriaDto(PoleNames.GEM_CASE, stringCriterionDto);
		PoleObjectFieldSelectorDto rootSelector = new PoleObjectFieldSelectorDto();
		rootSelector.setObjectType(PoleNames.GEM_CASE);
		ChildObjectFieldSelectorDto childObjectFieldSelectorDto = new ChildObjectFieldSelectorDto();
		childObjectFieldSelectorDto.setObjectType(PoleNames.FLAG);
		rootSelector.addChildObjectFieldSelector(childObjectFieldSelectorDto); 
		
		return getPoleObjects(poleDirect, rootCriteriaDto, rootSelector, clientName, RetrievalTypeDto.OPEN);
	}
	
	private void deletePoleObjects(List<? extends PoleObjectDto> poleObjects) {		
		for (PoleObjectDto object : poleObjects) {
			boolean failedToDeleteFlags = false;
			if (!object.getFlags().isEmpty()) {
				// if the object has flags then we have to remove them first before we can remove the parent object
				for (FlagDto flag : object.getFlags()) {
					flag.setModificationStatus(ModificationStatusDto.REMOVE);
				}
				failedToDeleteFlags = tryToDeleteSetupData(object);
			}
			if (!failedToDeleteFlags) {
				// if we could not remove
				object.setModificationStatus(ModificationStatusDto.REMOVE_GRAPH);
				tryToDeleteSetupData(object);
			}
		}
	}
	
	public boolean tryToDeleteSetupData(PoleObjectDto poleObject) {
		try {
			putPoleObject(poleObject, poleDirect, FrameworkServiceConstants.BUSINESS_SERVICES_CLIENT_NAME);
			return false;
		} catch (Exception e) {
			logger.info("Error " + e.getMessage() + " occurred while trying to delete an old " + poleObject.getEntityType() + " with object ref " + poleObject.getObjectRef() + " created " + 
					"by the test cases, this is not a big problem and is expected to happen from time to time, " + 
					"the test will attempt to delete them again next time it runs");
			return true;
		}
	}
	
}
