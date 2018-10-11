package com.northgateis.gem.bussvc.poleobjects.functest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.Test;

import com.northgateis.gem.bussvc.dataconsolidation.appliedlogic.DataConsolidationBean;
import com.northgateis.gem.bussvc.dataconsolidation.appliedlogic.commonjobs.LocationByUprnDataConsolidationJob;
import com.northgateis.gem.bussvc.framework.test.BusSvcStoryAcceptanceCriteriaReference;
import com.northgateis.gem.bussvc.pole.camel.KeyAllocationContext;
import com.northgateis.gem.bussvc.pole.metadata.PoleMetadataCacheBean;
import com.northgateis.gem.bussvc.poleobjects.constants.PoleObjectsServiceConstants;
import com.northgateis.gem.bussvc.test.util.CommonPoleObjectTestUtils;
import com.northgateis.pole.common.InvalidDataException;
import com.northgateis.pole.schema.EventDto;
import com.northgateis.pole.schema.ExternalReferenceDto;
import com.northgateis.pole.schema.GetPoleObjectsResponseDto;
import com.northgateis.pole.schema.LinkDto;
import com.northgateis.pole.schema.LocationDto;
import com.northgateis.pole.schema.ModificationStatusDto;
import com.northgateis.pole.schema.PoleEntityDto;
import com.northgateis.pole.schema.PoleObjectDto;
import com.northgateis.pole.schema.PutPoleObjectsRequestDto;

/**
 * Class contains common tests for when locations with a UPRN are supplied linked to an event
 */
public abstract class AbstractPoleObjectLocationFuncTestBase extends AbstractPoleObjectsFuncTestBase {
	
	protected PoleObjectDto preExistingPoleObject;
	protected LocationDto preExistingLocation;
	protected String preExistingLocationUprn;
	
	protected PoleObjectDto targetPoleObject;
	protected LocationDto targetLocation;
	protected LinkDto targetLocationLink;
	
	@Override
	protected void setupImpl() throws Exception {
		
		preExistingLocationUprn = PE_UPRN_EXT_REF_TYPE + System.currentTimeMillis();
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=215,
		mingleRef=37097,
		mingleTitle="Business Services - UPRN Location Match for Validation Framework",
		acceptanceCriteriaRefs="CR1",
		given="I sent across a POLE Graph giving my Location information a UPRN and "
				+ "There is a Location record already existing within Athena's POLE database",
		when="I submit my Location information to Business Services",
		then="Business Services will replace the submitted LOCATION record data in the incoming POLE graph for "
				+ "a cross-reference (Link) to the pre-existing Location already within POLE"
				+ "AND no new location will be created"
	)
	public void testLocationMatchedByUprnReusesExistingPoleRecord() throws Exception {
		
		doSetup(true, preExistingLocationUprn);
		
		doTestLocationMatch(preExistingLocationUprn);

		assertEquals("Expect Location to be reused / link to be made to existing Location", 
				preExistingLocation.getObjectRef(), targetLocation.getObjectRef());
		
		assertEquals("Expect uprn of both locations to be the same", 
				CommonPoleObjectTestUtils.extractUprnFromLocation(preExistingLocation),
				CommonPoleObjectTestUtils.extractUprnFromLocation(targetLocation));
		
		assertTrue("Expect Location to be the previously saved researched record", 
					targetLocation.getResearched());
		
		checkLinkResearchedComparedToObject(targetLocationLink, targetLocation);
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=215,
		mingleRef=37097,
		mingleTitle="Business Services - UPRN Location Match for Validation Framework",
		acceptanceCriteriaRefs="CR2",
		given="I sent across a POLE Graph giving my Location information a UPRN and "
				+ "There is NO [ps RESEARCHED] Location record already with that UPRN existing within Athena's POLE database, "
				+ "but there is a Location record with that UPRN within the Compass database",
		when="I submit my Location information to Business Services",
		then="Business Services will create a new Location record within the Athena POLE database containing the "
				+ "Location information - be it any location either linked to Person or Incident or Intelligence"
	)
	public void testLocationMatchedByUprnReusesExistingCompassRecord() throws Exception {
		// matching valid compass UPRN value (do not change else test will fail or replace it with another matching valid compass UPRN)
		//preExistingLocationUprn = "200004268593"; // = 1, Warder Close, Dunmow, Essex, CM6 3TT
		//preExistingLocationUprn = "200004268594"; // = 2, Warder Close, Dunmow, Essex, CM6 3TT
		//preExistingLocationUprn = "200004268595"; // = 3, Warder Close, Dunmow, Essex, CM6 3TT
		//preExistingLocationUprn = "200004268596"; // = 4, Warder Close, Dunmow, Essex, CM6 3TT
		//preExistingLocationUprn = "200004268597"; // = 5, Warder Close, Dunmow, Essex, CM6 3TT
		//preExistingLocationUprn = "100031315292"; // = 12 Enfield Street Beeston  NG9 1DL
		//preExistingLocationUprn = "100091232298";
		preExistingLocationUprn = "100032205544"; // UPRN which return location with locality field - As it was missed
													 // in Validation configuration xml validation was failing. So just
													 // to ensure it works fine with locality field specified UPRN which
													 // return location with locality field.

		
		doTestLocationMatch(preExistingLocationUprn);
		
		assertEquals("Expect uprn of both locations to be the same", preExistingLocationUprn,
				CommonPoleObjectTestUtils.extractUprnFromLocation(targetLocation));
		
		assertTrue("Expect Location to be the newly saved researched record", 
					targetLocation.getResearched());
		
		checkLinkResearchedComparedToObject(targetLocationLink, targetLocation);
	}	
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		mingleRef=37959,
		mingleTitle="Getting error while trying to link a location having UPRN with Intel Report.",
		acceptanceCriteriaRefs="DEFECT",
		given="I have submitted my QAS-obtained Location-data within the incident details I provided to the Public Engagement portal"
				+ "AND CONNECT has queried POLE for existing Location data that has the same UPRN stored against it"
				+ "AND [ps for Defect #37959] the Location is RESEARCHED",
		when="An existing Location record with matching UPRN does not exist in POLE."
				+ "AND No matching Compass Record is found either for the UPRN.",
		then="No Location record will be created in POLE"
				+ "AND the overall transaction will fail"
				+ "AND a validation error will be returned stating that the UPRN is invalid"
	)
	public void testResearchedLocationUnmatchedByUprnFailsTransaction() throws Exception {
		
		preExistingLocationUprn = "123456789012";//invalid

		try {
			doTestLocationMatch(preExistingLocationUprn, true);
			
			fail("Transaction succeeded when Location with invalid UPRN was marked as researched.");
		} catch (InvalidDataException ide) {
			assertTrue(ide.getMessage().contains("UPRN"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=215,
		mingleRef=37097,
		mingleTitle="Business Services - UPRN Location Match for Validation Framework",
		acceptanceCriteriaRefs="TODO",
		given="I sent across a POLE Graph giving my Location information a UPRN and "
				+ "There is NO Location record already with that UPRN existing within Athena's POLE database, "
				+ "but there is a Location record with that UPRN within the Compass database",
		when="I submit my Location information to Business Services",
		then="The location record from Compass will be marked as excluded from validation"
	)
	public void testLocationMatchedByUprnFromCompassIsMarkedAsExcludedFromValidation() throws Exception {

		EventDto mainEvent = createMainEventDto();
		
		addUprnToLocation(mainEvent, false, preExistingLocationUprn);
		
		LocationDto location = null;
		for (LinkDto linkDto : mainEvent.getLinks()) {
			if (linkDto.getToPoleObject() instanceof LocationDto) {
				
				if (linkDto.getLinkReason().equals(getLocationLinkReason())) {
					location = (LocationDto) linkDto.getToPoleObject();
					break;
				}
			}
		}
		
		PutPoleObjectsRequestDto ppoReq = new PutPoleObjectsRequestDto();
		ppoReq.addPoleObject(mainEvent);
		
		DataConsolidationBean dcb = new DataConsolidationBean(ppoReq, PoleMetadataCacheBean.getInstance(),
				new KeyAllocationContext(), new ArrayList<String>(), systemParamsCacheBean, linkTypeCacheBean);
		dcb.flattenPoleGraph_ForTestOnly();
		
		LocationByUprnDataConsolidationJob dcj = new LocationByUprnDataConsolidationJob(PoleMetadataCacheBean.getInstance(),
				dcb, location);
		
		//We cant easily get the location from Compass, so create a dummy one
		LocationDto locationFromCompass = new LocationDto();
		locationFromCompass.setPremisesName("MY TEST LOCATION");
		
		ExternalReferenceDto externalReference = new ExternalReferenceDto();
		externalReference.setReferenceType("UPRN");
		locationFromCompass.addExternalReference(externalReference);
		
		externalReference = new ExternalReferenceDto();
		externalReference.setReferenceType("OTHER");
		locationFromCompass.addExternalReference(externalReference);
		
		GetPoleObjectsResponseDto getPoleObjectsResponse = new GetPoleObjectsResponseDto();
		getPoleObjectsResponse.addPoleObject(locationFromCompass);
		
		dcj.processResponse(getPoleObjectsResponse);
		
		assertEquals("Expecting 3 objects to be in list of objects not to be validated", 3,
				dcb.getCopyCreateObjectsToBeExcludedFromValidation().entrySet().size());
		
		boolean locationFound = false;
		boolean uprnExtRefFound = false;
		boolean otherExtRefFound = false;
		for (PoleEntityDto poleEntity : dcb.getCopyCreateObjectsToBeExcludedFromValidation().values()) {
			if (poleEntity instanceof LocationDto) {
				LocationDto validationLocation = (LocationDto) poleEntity;
				if (validationLocation.getPremisesName().equals("MY TEST LOCATION")) {
					locationFound = true;
				}
			} else if (poleEntity instanceof ExternalReferenceDto) {
				ExternalReferenceDto extRef = (ExternalReferenceDto) poleEntity;
				if (extRef.getReferenceType().equals("UPRN")) {
					uprnExtRefFound = true;
				} else if (extRef.getReferenceType().equals("OTHER")) {
					otherExtRefFound = true;
				}
			}
		}
		assertTrue("Expecting location to be excluded from validation", locationFound);
		assertTrue("Expecting external reference to be excluded from validation", uprnExtRefFound);
		assertTrue("Expecting external reference to be excluded from validation", otherExtRefFound);
	}
	
	/**
	 * Implementation classes should return an EventDto that doesnt exist in Pole with an appropriate 
	 * location link.
	 * 
	 * @return
	 */
	protected abstract EventDto createMainEventDto();
	
	/**
	 * Implementation classes should create the mainEvent (e.g. Incident, Intell...) in Pole directly,
	 * and should then return the newly created PoleEvent retrieved from Pole containing all the 
	 * objectRefs (full graph).
	 * 
	 * @param mainEvent
	 * @return
	 * @throws Exception
	 */
	protected abstract EventDto createMainEventInPoleDirect(PoleObjectDto mainEvent) throws Exception;
	
	/**
	 * Implementation classes should create the mainEvent (e.g. Incident, Intell...) in Pole using Business Services,
	 * and return the newly created PoleEvent retrieved from Pole containing all the objectRefs (full graph).
	 * 
	 * @param mainEvent
	 * @return
	 * @throws Exception
	 */
	protected abstract EventDto createMainEventInBusinessServices(PoleObjectDto mainEvent) throws Exception;
	
	/**
	 * The link reason to be used for finding the location linked to the event
	 * 
	 * @return
	 */
	protected abstract String getLocationLinkReason();
	
	private void doSetup(boolean researchedLocation, String locationUprn) throws Exception {
		EventDto mainEvent = createMainEventDto();
		
		addUprnToLocation(mainEvent, researchedLocation, locationUprn);

		preExistingPoleObject = createMainEventInPoleDirect(mainEvent);
		
		for (LinkDto linkDto : preExistingPoleObject.getLinks()) {
			if (linkDto.getToPoleObject() instanceof LocationDto) {
				
				if (linkDto.getLinkReason().equals(getLocationLinkReason())) {
					preExistingLocation = (LocationDto) linkDto.getToPoleObject();
					break;
				}
			}
		}
	}

	private void doTestLocationMatch(String uprn) throws Exception {
		doTestLocationMatch(uprn, false);
	}

	protected void doTestLocationMatch(String uprn, boolean researchedLocation) throws Exception {
		EventDto mainEvent = createMainEventDto();
		
		addUprnToLocation(mainEvent, researchedLocation, uprn);
		
		targetPoleObject = createMainEventInBusinessServices(mainEvent);
		
		for (LinkDto linkDto : targetPoleObject.getLinks()) {
			if (linkDto.getToPoleObject() instanceof LocationDto) {
				
				if (linkDto.getLinkReason().equals(getLocationLinkReason())) {
					targetLocation = (LocationDto) linkDto.getToPoleObject();
					targetLocationLink = linkDto;
					break;
				}
			}
		}
	}
	
	private void addUprnToLocation(PoleObjectDto poleObject, boolean researchedLocation, String locationUprn) {
		for (LinkDto linkDto : poleObject.getLinks()) {
			if (linkDto.getToPoleObject() instanceof LocationDto) {
				
				if (linkDto.getLinkReason().equals(getLocationLinkReason())) {
					
					LocationDto location = (LocationDto) linkDto.getToPoleObject();
					location.setResearched(researchedLocation);
					linkDto.setResearched(researchedLocation);
					if (locationUprn != null) {
						ExternalReferenceDto extRef = new ExternalReferenceDto(
								PoleObjectsServiceConstants.UPRN_EXT_REF_TYPE, locationUprn);
						extRef.setModificationStatus(ModificationStatusDto.CREATE);
						location.addExternalReference(extRef);
					}
				}
			}
		}
	}
}
