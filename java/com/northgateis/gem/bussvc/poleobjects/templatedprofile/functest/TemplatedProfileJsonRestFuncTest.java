package com.northgateis.gem.bussvc.poleobjects.templatedprofile.functest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.northgateis.gem.bussvc.api.jsonrest.AbstractFreestandingBusSvcFuncTestBase;
import com.northgateis.gem.bussvc.framework.FrameworkServiceConstants;
import com.northgateis.gem.bussvc.framework.test.BusSvcStoryAcceptanceCriteriaReference;
import com.northgateis.gem.bussvc.pole.profile.Profile;
import com.northgateis.gem.bussvc.pole.profile.ProfileRegistry;
import com.northgateis.gem.bussvc.poleobjects.client.PoleBusinessServicesJsonRestClient;
import com.northgateis.gem.bussvc.poleobjects.routes.GetPoleObjectsServiceRoute;
import com.northgateis.pole.common.EntityNotFoundException;
import com.northgateis.pole.common.InvalidDataException;
import com.northgateis.pole.common.PoleNames;
import com.northgateis.pole.schema.ChildObjectFieldSelectorDto;
import com.northgateis.pole.schema.FlagDto;
import com.northgateis.pole.schema.FlagTypeDto;
import com.northgateis.pole.schema.GetPoleObjectsRequestDto;
import com.northgateis.pole.schema.GetPoleObjectsResponseDto;
import com.northgateis.pole.schema.PoleObjectCriteriaDto;
import com.northgateis.pole.schema.PoleObjectDto;
import com.northgateis.pole.schema.PoleObjectFieldSelectorDto;
import com.northgateis.pole.schema.RequestHeaderDto;
import com.northgateis.pole.schema.RetrievalTypeDto;
import com.northgateis.pole.schema.SortOrderDto;

/**
 * Functional Test class to test the TemplatedProfile JsonRest Request. This
 * feature allows GetPoleObjectsRequestDto 'templates' to be given a name
 * and used on the RESTful API.
 * 
 * The tests use a single profile "test-ft-simpleloc" which exists in the
 * business services codebase. This means we've got test code in the core
 * deployable codebase which isn't a good thing - but we have no better ideas
 * right now.
 * 
 * These tests are only valid when Business Services is offering its JSON/REST API, 
 * which is only enabled during JUnit UT runs by -DjettyMode=true & -DjettyPort=1234
 * 
 * To run this test the following must be provided to the JVM runtime:
 * 
 * 1 x System Parameters for the main business services runtime, to tell it
 * to start jetty on a given port.
 *   -DjettyMode=true
 * 
 * 2 x Environment Variables for the unit-tests to tell them to use JSON/REST
 * calling a specific port.
 *   jsonRestMode=true
 *   jsonRestJettyPort=8765
 *   
 * Key classes to observe those are tested by these FTs:
 * {@link ProfileRegistry}
 * {@link GetPoleObjectsServiceRoute}
 * 
 * e.g. Url - http://x.y.z.w:port/gembusinessservices/rest/pole/services/location/772013/profile/test-ft-simpleloc
 * 
 * @author harsh.shah
 */
public class TemplatedProfileJsonRestFuncTest extends AbstractFreestandingBusSvcFuncTestBase {

	//TODO CODE REVIEW CON-39772 This file can't be in the core codebase config.
	//We need to think of another way. Should be possible when doing local root tests.
	
	// test profile file name
	private static final String PROFILE_TEST_FT_SIMPLELOC = "test-ft-simpleloc";

	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			jiraRef = "CCI-39772",
			mingleTitle = "WP347 - Log Updates - Templated Profiles", 
			acceptanceCriteriaRefs = "NA", 
			given = "A request to get location data with valid profile and objectRef which is present in pole",
			when = "The request is sent to BS", 
			then = "A Json response back is sent to the client with single result")
	public void testValidRequestReturnsJsonData() throws Exception {		
		
		String objectType = PoleNames.LOCATION;
		String profileName = PROFILE_TEST_FT_SIMPLELOC;
		
		PoleObjectDto existingPoleObject = getExistingPoleObject(objectType);
		Integer objectRef = existingPoleObject.getObjectRef();

		GetPoleObjectsResponseDto resp = poleBusinessServices.getPoleObjectsWithProfile(
				objectType, objectRef, profileName);

		assertNotNull(resp);
		assertEquals("Expected a single location to be returned", 1, resp.getPoleObjects().size());
		PoleObjectDto receivedPoleObjectDto = resp.getPoleObjects().get(0);
		assertEquals("Expected a single location to be returned", objectRef, receivedPoleObjectDto.getObjectRef());
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			jiraRef = "CCI-39772",
			mingleTitle = "WP347 - Log Updates - Templated Profiles", 
			acceptanceCriteriaRefs = "NA", 
			given = "A request to get location data with invalid profile",
			when = "The request is sent to BS", 
			then = "Error should be returned to the client")
	public void testInvalidProfileNameReturns400BadRequest() throws Exception {		
		
		String objectType = PoleNames.LOCATION;
		String profileName = "invalidProfileName";
		Integer objectRef = 12345;
		
		try {
			poleBusinessServices.getPoleObjectsWithProfile(objectType, objectRef, profileName);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("Error response should talk about an invalid profile",
					ide.getMessage().contains("No profile matching " + profileName));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			jiraRef = "CCI-39772",
			mingleTitle = "WP347 - Log Updates - Templated Profiles", 
			acceptanceCriteriaRefs = "NA", 
			given = "A request to get location data with invalid url instead of /profile/",
			when = "The request is sent to BS", 
			then = "Error should be returned to the client")
	public void testInvalidProfileUrlReturns400BadRequest() throws Exception {
		// This test is only applicable to JsonRest request as its bad URL based
		
		if (!jsonRestMode) {
			logger.warn("Not running this test.");
			return;
		}
		
		String objectType = PoleNames.LOCATION;
		String badProfilePath = "p_r-o_filEEE/";
		String invalidProfileName = "invalidProfileName";
		Integer objectRef = 12345;
		
		try {
			//This is the only place where jsonBs is used. We need it to be able to access
			//the 'extended path' notion.
			PoleBusinessServicesJsonRestClient jsonBs 
				= (PoleBusinessServicesJsonRestClient) poleBusinessServices;
			jsonBs.getPoleObjectsWithExtendedPath(
					objectType.toLowerCase(), objectRef, badProfilePath + invalidProfileName);
			fail("Expected an exception for invalid data");
		} catch (EntityNotFoundException enfe) { //TODO CON-39772: still not sure about this to represent a 404...
			assertTrue("Exception: " + enfe.getMessage(),
				enfe.getMessage().contains(badProfilePath));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			jiraRef = "CCI-39772",
			mingleTitle = "WP347 - Log Updates - Templated Profiles", 
			acceptanceCriteriaRefs = "NA", 
			given = "A request to get location data with valid profile and objectRef which is not present in pole",
			when = "The request is sent to BS", 
			then = "Empty response should be returned to the client")
	public void testEntityNotFoundReturnsEmptyResponse() throws Exception {	
		
		String objectType = PoleNames.LOCATION;
		String profileName = PROFILE_TEST_FT_SIMPLELOC;
		Integer objectRef = 12345;

		GetPoleObjectsResponseDto resp = poleBusinessServices.getPoleObjectsWithProfile(
				objectType, objectRef, profileName);
	
		assertNotNull(resp);
		assertEquals("Expected 0 results", 0, resp.getPoleObjects().size());	
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			jiraRef = "CCI-39772",
			mingleTitle = "WP347 - Log Updates - Templated Profiles", 
			acceptanceCriteriaRefs = "NA", 
			given = "A request to get invalid objectType(objectType not supporetd by pole) data with valid profile and "
					+ "objectRef",
			when = "The request is sent to BS", 
			then = "Error should be returned to the client")
	public void testInvalidObjectTypeReturns400BadRequestInvalidDataException() throws Exception {		
		
		String objectType = "invalidObjectType";
		String profileName = PROFILE_TEST_FT_SIMPLELOC;
		Integer objectRef = 12345;
		try {
			poleBusinessServices.getPoleObjectsWithProfile(objectType, objectRef, profileName);

			fail("Expected an exception for invalid data");		
		} catch (InvalidDataException ide) {
			assertTrue("Error response should talk about invalid objectType", 
					ide.getMessage().contains("Object type '" + objectType.toLowerCase() + "' does not exist or is not a POLE object."));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			jiraRef = "CCI-39772",
			mingleTitle = "WP347 - Log Updates - Templated Profiles", 
			acceptanceCriteriaRefs = "NA", 
			given = "Test to check profile data reloading when templatedProfilesReloading=true",
			when = "The profile file gets changed", 
			then = "Profile should get reloaded")
	public void testTemplatedProfileReloadingWhenEnabled() throws Exception {
		
		if (!localMode) {
			//This test messes about with the profile registry so cannot be run when
			//not in local mode.
			logger.warn("Not running this test.");
			return;
		}
		
		// check profile already loaded		
		Profile profile = profileRegistry.getProfile(PROFILE_TEST_FT_SIMPLELOC);
		assertNotNull("Profile Not Found", profile);
		
		boolean originalReloadingEnabled = profileRegistry.getTemplatedProfilesReloading_TEST_ONLY();
		//override original TemplatedProfilesReloading value to true
		profileRegistry.setTemplatedProfilesReloading_TEST_ONLY(true);	

		// remove the profile from profiles map
		profileRegistry.removeTemplatedProfileXmlFile_TEST_ONLY(PROFILE_TEST_FT_SIMPLELOC);
		
		// commented below 2 lines as now getProfile method lazy load the profile so they are no longer valid
		/*profile = profileRegistry.getProfile(PROFILE_TEST_FT_SIMPLELOC);
		assertTrue("Profile must be null", profile == null);*/
		
		// change file lastModified time
		String filePath = profileRegistry.getTemplatedProfilesLocation() + "/profile-" + PROFILE_TEST_FT_SIMPLELOC
				+ ".xml";		
		File file = new File(filePath);
		if (file != null) {			
			file.setLastModified(new Date().getTime());			
		}
		
		// retry every 3 seconds till profile gets reloaded
		long timeStart = System.currentTimeMillis();		
		long retryMaxMilliseconds = GetPoleObjectsServiceRoute.templatedProfileReloadFrequency * 10;
		while (System.currentTimeMillis() - timeStart < retryMaxMilliseconds) {
			profile = profileRegistry.getProfile(PROFILE_TEST_FT_SIMPLELOC);
			if (profile != null) {
				break;
			}
			Thread.sleep(3000);
		}		
		
		// check profile reloaded
		assertTrue("Profile must be found", profile!=null);
		// restore original TemplatedProfilesReloading value
		profileRegistry.setTemplatedProfilesReloading_TEST_ONLY(originalReloadingEnabled);
	}			
	
	
	/**
	 * Method to get single existing pole object by its type having on secure/close flag on it
	 * 
	 * @param eventType
	 * @return
	 */
	public PoleObjectDto getExistingPoleObject(String eventType) {
		GetPoleObjectsRequestDto gpoRequest = new GetPoleObjectsRequestDto();
		gpoRequest.setRetrievalType(RetrievalTypeDto.SEARCH);
		
		RequestHeaderDto requestHeader = new RequestHeaderDto();
		requestHeader.setUsername("");
		requestHeader.setClientName(FrameworkServiceConstants.BUSINESS_SERVICES_CLIENT_NAME);
		requestHeader.setPasswordHash(securityContextId);
		requestHeader.setTransactionDescription("Test 39772 templated profile story");
		gpoRequest.setHeader(requestHeader);
		
		PoleObjectCriteriaDto poCriteria = new PoleObjectCriteriaDto();
		poCriteria.setObjectType(eventType);						
		gpoRequest.setPoleObjectCriteria(poCriteria);
					
		PoleObjectFieldSelectorDto poSelector = new PoleObjectFieldSelectorDto();
		poSelector.setObjectType(eventType);
		poSelector.addFieldName("objectRef");
		poSelector.addFieldName("createdDateTime");			
		poSelector.setMaxRecords(10);
		
		ChildObjectFieldSelectorDto flagFieldSelector = new ChildObjectFieldSelectorDto();
		flagFieldSelector.setObjectType(PoleNames.FLAG);
		flagFieldSelector.setSelectDeep(false);

		poSelector.addChildObjectFieldSelector(flagFieldSelector);
		
		SortOrderDto poSortOrder = new SortOrderDto();
		poSortOrder.setFieldName("createdDateTime");
		poSortOrder.setAscending(false);			
		poSelector.addSortOrder(poSortOrder);
		
		gpoRequest.setPoleObjectFieldSelector(poSelector);						
		
		GetPoleObjectsResponseDto gpoResponse = poleDirect.getPoleObjects(gpoRequest);
		
		List<? extends PoleObjectDto> poleObjects = gpoResponse.getPoleObjects();
		if(poleObjects.isEmpty()) {
			return null;
		}
		
		logger.info("=============== check for flags =================");
		// Find a record without closed or secure flag
		PoleObjectDto poleObjectDto = null;		
		for (PoleObjectDto poleObject : poleObjects) {
			List<FlagDto> flags = poleObject.getFlags();
			boolean isRestrictiveFlagFound = false;
			for (FlagDto flag : flags) {
				if (flag.getFlagType().equals(FlagTypeDto.SECURE) || flag.getFlagType().equals(FlagTypeDto.CLOSED)) {
					isRestrictiveFlagFound = true;
					break;
				}
			}
			if (!isRestrictiveFlagFound) {
				poleObjectDto = poleObject;
				break;
			}
		}		
		
		return poleObjectDto;		
	}
}