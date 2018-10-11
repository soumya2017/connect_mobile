package com.northgateis.gem.bussvc.poleobjects.blade.functest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Assume;
import org.junit.Test;

import com.northgateis.gem.bussvc.AbstractBusinessServicesFunctionalTestBase;
import com.northgateis.gem.bussvc.blade.BladeServiceConstants;
import com.northgateis.gem.bussvc.framework.FrameworkServiceConstants;
import com.northgateis.gem.bussvc.framework.test.BusSvcStoryAcceptanceCriteriaReference;
import com.northgateis.gem.bussvc.pole.utils.PoleDtoUtils;
import com.northgateis.gem.bussvc.test.util.BladeTestUtils;
import com.northgateis.pole.client.PoleDtoBuildHelper;
import com.northgateis.pole.common.InvalidDataException;
import com.northgateis.pole.schema.GetPoleObjectsRequestDto;
import com.northgateis.pole.schema.GetPoleObjectsResponseDto;
import com.northgateis.pole.schema.MessageExtraInfoDto;
import com.northgateis.pole.schema.PoleObjectCriteriaDto;
import com.northgateis.pole.schema.RetrievalTypeDto;

/**
 * Class contains all the Blade Federated Search Functional Tests
 *
 */
public class BladeSearchFuncTest extends AbstractBusinessServicesFunctionalTestBase {

	protected String clientName = FrameworkServiceConstants.BUSINESS_SERVICES_CLIENT_NAME;

	/**
	 * Overriding to provide more roles - needed for creating IntelligenceReports.
	 * 
	 * @throws Exception
	 */
	@Override
	protected void createMainSecurityContextId() throws Exception {
		if (securityContextId == null) {
			securityContextId = busSvcUtils.getSecurityContextId("Frank Shunneltestone",
					Arrays.asList("ATHENA_USER", "NorthgateSystemAdmin", "SysAdmin1", "SysAdmin2"), securityService);
		}
	}
	
	@Override
	protected void setupImpl() throws Exception {
        Assume.assumeTrue(isRunBladeTests());//likely to only be run overnight and on local dev machines.
		super.setupImpl();
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage = 157,
			mingleRef = 37208, 
			mingleTitle = "WP157 - Bus Svcs - Perform federated search", 
			acceptanceCriteriaRefs = "Negative scenario for invalid objectType", 
			given = "User sends federated search request with invalid objectType", 
			when = "Search submited", 
			then = "InvalidDataException should be returned")
	public void testBladeSearchFailueForInvalidObjectType() throws Exception {
		try {			
			GetPoleObjectsRequestDto req = new GetPoleObjectsRequestDto();
	
			PoleObjectCriteriaDto rootCriteriaDto = PoleDtoBuildHelper.buildPoleObjectCriteriaDto("invalidObjectType",
					PoleDtoBuildHelper.buildIntegerCriterionDto("objectRef", 12345));
			req.setPoleObjectCriteria(rootCriteriaDto);		
	
			req.setRetrievalType(RetrievalTypeDto.OPEN);		
			PoleDtoUtils.addBusinessServiceInfo(req, BladeTestUtils.createBusinessServiceInfo(securityContextId));		
	
			poleBusinessServices.getPoleObjects(req);	
			
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("Object type 'invalidObjectType' does not exist or is not a POLE object"));			
		}
	}

	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage = 157,
			mingleRef = 37208, 
			mingleTitle = "WP157 - Bus Svcs - Perform federated search", 
			acceptanceCriteriaRefs = "Happy Path Scenario for single data source", 
			given = "User sends valid federated search request with single data source NFLMS", 
			when = "Search submited", 
			then = "Appropriate result should come without errors")
	public void testBladeSearchSuccessForSingleDS() throws Exception {
		

		GetPoleObjectsRequestDto gpoRequest = bladeTestUtils.preparePersonRequestForDataSource(
				BladeTestUtils.createBusinessServiceInfo(securityContextId),
				new String[] { BladeTestUtils.NFLMS_DATA_SOURCE }, "Mouse", "Mickey");

		GetPoleObjectsResponseDto gpoResponse = poleBusinessServices.getPoleObjects(gpoRequest);

		assertCheckForDataSource(gpoResponse, BladeTestUtils.NFLMS_DATA_SOURCE, true);
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage = 157,
			mingleRef = 37208, 
			mingleTitle = "WP157 - Bus Svcs - Perform federated search", 
			acceptanceCriteriaRefs = "1.1,1.2,1.3", 
			given = "User have correctly prepared a federated search request for multiple data sources NFLMS,POLE", 
			when = "Search request submitted", 
			then = "The request will be sent to each of the Data Sources. The response will summarise the counts of"
					+ " records returned from each data source and appropriate result should come without errors")
	public void testBladeSearchSuccessForMultipleDS() throws Exception {

		GetPoleObjectsRequestDto gpoRequest = bladeTestUtils.preparePersonRequestForDataSource(
				BladeTestUtils.createBusinessServiceInfo(securityContextId),
				new String[] { BladeTestUtils.NFLMS_DATA_SOURCE, BladeTestUtils.POLE_DATA_SOURCE }, "Mouse", "Mickey");

		GetPoleObjectsResponseDto gpoResponse = poleBusinessServices.getPoleObjects(gpoRequest);
		
		assertCheckForDataSource(gpoResponse, BladeTestUtils.NFLMS_DATA_SOURCE, true);
		assertCheckForDataSource(gpoResponse, BladeTestUtils.POLE_DATA_SOURCE, true);
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage = 157,
			mingleRef = 37208, 
			mingleTitle = "WP157 - Bus Svcs - Perform federated search", 
			acceptanceCriteriaRefs = "4.1,4.2,4.3,4.4", 
			given = "User have incorrectly prepared a search request specifying Data Sources that they do not have"
					+ " access to", 
			when = "Search request submitted", 
			then = "Business Services will exclude calls to invalid Data Sources. "
					+ "Business Services will provide a message in the response to explain that the Data Source is "
					+ "not authorised for me. "
					+ "The overall transaction will not fail. "
					+ "Business Services will return other Data sources results.")
	public void testBladeSearchForMultipleDSWithoutBladeSecurityPermission() throws Exception {
		
		securityContextId = busSvcUtils.getSecurityContextId("Frank Shunneltestone",
				Arrays.asList("ATHENA_USER"), securityService);

		GetPoleObjectsRequestDto gpoRequest = bladeTestUtils.preparePersonRequestForDataSource(
				BladeTestUtils.createBusinessServiceInfo(securityContextId),
				new String[] { BladeTestUtils.NFLMS_DATA_SOURCE, BladeTestUtils.POLE_DATA_SOURCE }, "Mouse", "Mickey");

		GetPoleObjectsResponseDto gpoResponse = poleBusinessServices.getPoleObjects(gpoRequest);
		
		assertCheckForDataSource(gpoResponse, BladeTestUtils.NFLMS_DATA_SOURCE, false);
		assertCheckForDataSource(gpoResponse, BladeTestUtils.POLE_DATA_SOURCE, true);
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage = 157,
			mingleRef = 37208, 
			mingleTitle = "WP157 - Bus Svcs - Perform federated search", 
			acceptanceCriteriaRefs = "4.1,4.2,4.3,4.4", 
			given = "User have incorrectly prepared a search request specifying Data Sources "
					+ "that do not support searches for the specified ObjectType", 
			when = "Search request submitted", 
			then = "Business Services will exclude calls to invalid Data Sources. "
					+ "Business Services will provide a message in the response to explain that the Data Source is "
					+ "not authorised for me. "
					+ "The overall transaction will not fail. "
					+ "Business Services will return other Data sources results.")
	public void testBladeSearchForMultipleDSRequestContainingInvalidDS() throws Exception {

		GetPoleObjectsRequestDto gpoRequest = bladeTestUtils.preparePersonRequestForDataSource(
				BladeTestUtils.createBusinessServiceInfo(securityContextId),
				new String[] { BladeTestUtils.NFLMS_DATA_SOURCE, BladeTestUtils.POLE_DATA_SOURCE,
						BladeTestUtils.INVALID_DATA_SOURCE }, "Mouse", "Mickey");

		GetPoleObjectsResponseDto gpoResponse = poleBusinessServices.getPoleObjects(gpoRequest);
		
		assertCheckForDataSource(gpoResponse, BladeTestUtils.NFLMS_DATA_SOURCE, true);
		assertCheckForDataSource(gpoResponse, BladeTestUtils.POLE_DATA_SOURCE, true);
		assertCheckForDataSource(gpoResponse, BladeTestUtils.INVALID_DATA_SOURCE, false);
	}
	
	
	/**
	 * This FT covers defect CS #38270
	 * @throws Exception
	 */
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage = 157,
			mingleRef = 37208, 
			mingleTitle = "WP157 - Bus Svcs - Perform federated search", 
			acceptanceCriteriaRefs = "1.2", 
			given = "I have correctly prepared a federated search request for multiple data sources "
					+ "And Provided the single data source for location", 
			when = "Search submited", 
			then = "Appropriate result should come without errors")
	public void testBladeSingleDataSouceForLocation() throws Exception {

		GetPoleObjectsRequestDto gpoRequest = bladeTestUtils.prepareLocationRequestForDataSource(
				BladeTestUtils.createBusinessServiceInfo(securityContextId),
				new String[] { BladeTestUtils.NFLMS_DATA_SOURCE }, "CM16 1NE");

		GetPoleObjectsResponseDto gpoResponse = poleBusinessServices.getPoleObjects(gpoRequest);

		assertCheckForDataSource(gpoResponse, BladeTestUtils.NFLMS_DATA_SOURCE, true);
		
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage = 157,
			mingleRef = 37208, 
			mingleTitle = "WP157 - Bus Svcs - Perform federated search", 
			acceptanceCriteriaRefs = "1.2", 
			given = "I have correctly prepared a federated search request for multiple data sources "
					+ "And Provided the single data source for location", 
			when = "Search submited", 
			then = "Appropriate result should come without errors")
	public void testBladeMultipleDataSouceForLocation() throws Exception {

		GetPoleObjectsRequestDto gpoRequest = bladeTestUtils.prepareLocationRequestForDataSource(
				BladeTestUtils.createBusinessServiceInfo(securityContextId),
				new String[] { BladeTestUtils.NFLMS_DATA_SOURCE, BladeTestUtils.POLE_DATA_SOURCE },
				"CM16 1NE");

		GetPoleObjectsResponseDto gpoResponse = poleBusinessServices.getPoleObjects(gpoRequest);
		assertCheckForDataSource(gpoResponse, BladeTestUtils.NFLMS_DATA_SOURCE, true);
		assertCheckForDataSource(gpoResponse, BladeTestUtils.POLE_DATA_SOURCE, true);
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage = 157,
			mingleRef = 37208, 
			mingleTitle = "WP157 - Bus Svcs - Perform federated search", 
			acceptanceCriteriaRefs = "1.2", 
			given = "I have correctly prepared a federated search request for multiple data sources "
					+ "And Provided the single data source for location and user does not hold permission to search blade data source ", 
			when = "Search submited", 
			then = "Appropriate result should come without errors")
	public void testBladeMultipleDSForLocationWithoutBladeSecurityPermission() throws Exception {
		
		securityContextId = busSvcUtils.getSecurityContextId("Frank Shunneltestone",
				Arrays.asList("ATHENA_USER"), securityService);

		GetPoleObjectsRequestDto gpoRequest = bladeTestUtils.prepareLocationRequestForDataSource(
				BladeTestUtils.createBusinessServiceInfo(securityContextId),
				new String[] { BladeTestUtils.SUFFOLK_LOCATION_DATA_SOURCE,BladeTestUtils.POLE_DATA_SOURCE  }, "CM16 1NE");

		GetPoleObjectsResponseDto gpoResponse = poleBusinessServices.getPoleObjects(gpoRequest);

		assertCheckForDataSource(gpoResponse, BladeTestUtils.SUFFOLK_LOCATION_DATA_SOURCE, false);
		assertCheckForDataSource(gpoResponse, BladeTestUtils.POLE_DATA_SOURCE, true);
	}
	
	private void assertCheckForDataSource(GetPoleObjectsResponseDto gpoResponse, String dataSource,
			boolean isResultSuccess) {

		if (isResultSuccess) {
			MessageExtraInfoDto messageExtraInfoResult = gpoResponse.getExtraInfo(
					BladeServiceConstants.resultKey(dataSource));
			assertNotNull("ExtraInfo result should be returned for data source " + dataSource, 
					messageExtraInfoResult);
			assertEquals("Expected success from data source " + dataSource, BladeServiceConstants.SUCCESS,
					messageExtraInfoResult.getValue());

			MessageExtraInfoDto messageExtraInfoCount = gpoResponse.getExtraInfo(
					BladeServiceConstants.countKey(dataSource));
			assertNotNull("Count should be returned for data source " + dataSource,
					messageExtraInfoCount);
			assertEquals("Expected count for data source " + dataSource, false,
					Integer.parseInt(messageExtraInfoCount.getValue()) < 0);

		} else {
			MessageExtraInfoDto extraInfoResult = gpoResponse.getExtraInfo(
					BladeServiceConstants.resultKey(dataSource));
			assertNotNull("Expected error from data source " + dataSource,
					extraInfoResult);
			assertEquals("Expected error from data source " + dataSource, 
					BladeServiceConstants.ERROR, extraInfoResult.getValue());
		}

	}
		
}
