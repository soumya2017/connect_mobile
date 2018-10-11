package com.northgateis.gem.bussvc.submitcontact.functest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.northgateis.gem.bussvc.dataconsolidation.appliedlogic.commonjobs.ContactEventDataConsolidationJob;
import com.northgateis.gem.bussvc.framework.test.BusSvcStoryAcceptanceCriteriaReference;
import com.northgateis.gem.bussvc.framework.utils.xml.JaxbXmlMarshaller;
import com.northgateis.gem.bussvc.pole.camel.KeyAllocationContext;
import com.northgateis.gem.bussvc.pole.metadata.PoleMetadataCacheBean;
import com.northgateis.gem.bussvc.poleobjects.constants.PoleObjectsServiceConstants;
import com.northgateis.gem.bussvc.poleobjects.functest.AbstractPoleObjectsFuncTestBase;
import com.northgateis.gem.bussvc.submitcontact.constants.SubmitContactServiceConstants;
import com.northgateis.gem.bussvc.submitcontact.dataconsolidation.CreateContactEventDataConsolidationBean;
import com.northgateis.gem.bussvc.submitcontact.routes.CreateContactServiceRoute;
import com.northgateis.gem.bussvc.test.util.CommonPoleObjectTestUtils;
import com.northgateis.gem.bussvc.test.util.ContactEventTestUtils;
import com.northgateis.pole.common.EntityKey;
import com.northgateis.pole.common.InvalidDataException;
import com.northgateis.pole.common.PoleNames;
import com.northgateis.pole.schema.CommsDto;
import com.northgateis.pole.schema.ContactEventDto;
import com.northgateis.pole.schema.ExternalReferenceDto;
import com.northgateis.pole.schema.LinkDto;
import com.northgateis.pole.schema.LinkNodeDto;
import com.northgateis.pole.schema.LocationDto;
import com.northgateis.pole.schema.ModificationStatusDto;
import com.northgateis.pole.schema.PeContactInfoDto;
import com.northgateis.pole.schema.PersonDto;
import com.northgateis.pole.schema.PutPoleObjectsRequestDto;
import com.northgateis.pole.schema.PutPoleObjectsResponseDto;

/**
 * Tests for Data Consolidation concerns when creating a {@link ContactEventDto}
 * 
 * These currently include tests relating to a number of stories:
 * 
 * (1) #35809: Match against existing researched {@link PersonDto} records that are linked-to by
 * the same Public Engagement Ref contained in {@link PeContactInfoDto}.peAccountRef.
 *
 * (2) #35810: On the back of '1', match against Comms records that are linked-to from the {@link PersonDto}
 * records of '1' where email or telephone number matches an existing, researched {@link CommsDto}
 * record already in POLE.
 * 
 * (3) #35808: Match Locations using UPRN against researched records that have the same UPRN.
 * 
 * @author dan.allford and robert.hornsby
 */
@RunWith(JUnit4.class)
public class CreateContactEventDcFuncTest extends AbstractPoleObjectsFuncTestBase {

	protected ContactEventDto preExistingContactEvent;
	private PersonDto preExistingPerson;
	private CommsDto preExistingComms;
	private LocationDto preExistingLocation;

	protected String preExistingCeReference;
	protected String preExistingCommsValue;
	protected String preExistingLocationUprn;

	protected String targetCeReference;
	protected ContactEventDto targetContactEvent;
	
	protected PersonDto targetPerson;
	protected LinkDto targetPersonLink = null;
	protected PersonDto targetVictim;
	protected LinkDto targetVictimLink = null;
	private CommsDto targetComms;
	private LinkDto targetCommsLink;
	private CommsDto targetVictimComms;
	private LinkDto targetVictimCommsLink;
	private LocationDto targetLocation;
	private LinkDto targetLocationLink;
	private LocationDto targetPersonReportingLocation;
	private LinkDto targetPersonReportingLocationLink;
	private LocationDto targetVictimLocation;
	private LinkDto targetVictimLocationLink;
	
	/** 
	 * See also {@link CreateContactEventReportedByVictimDcFuncTest}
	 * 
	 * Made protected so that a tricksy sub-class can test the entire suite with the added 'salt'
	 * of PERSON REPORTING being the same Person as VICTIM.
	 */
	protected boolean reporterIsVictim = false;
	
	public CreateContactEventDcFuncTest() {
	}
	
	@Override
	protected void setupImpl() throws Exception {
		
		if (PoleMetadataCacheBean.getInstance() == null) {
			busSvcUtils.getPoleMetadataCacheAndConstraints(poleDirect, poleMetadataDirect, securityContextId, poleUsername, polePassword);
		}

		preExistingCeReference = PE_REF_PREFIX + generatePseudoUniqueTimestampString();
		preExistingCommsValue = "john.commoner" + System.currentTimeMillis() + "@iamafish.com";
		preExistingLocationUprn = PE_UPRN_EXT_REF_TYPE + System.currentTimeMillis();

		targetCeReference = PE_REF_PREFIX + generatePseudoUniqueTimestampString() + ".234";
		targetContactEvent = null;
		targetPerson = null;
		targetComms = null;
	}
	
	/*
	 * Default 'per test' setup (steered by the test) sets Comms, Person and Location to researched
	 * making them all matchable.
	 */
	private void doSetup() throws Exception {
		doSetup(true, peAccountRef, null, 
				true, preExistingCommsValue, 
				true, preExistingLocationUprn);
	}
	
	protected void doSetup(boolean researchedPerson, String personPeAccountRef, String victimPeAccountRef, 
			boolean researchedComms, String commsValue, 
			boolean researchedLocation, String locationUprn) throws Exception {		

		createPreExistingContactEvent(researchedPerson, personPeAccountRef, victimPeAccountRef,
				researchedComms, commsValue, 
				researchedLocation, locationUprn);
		extractRecordsLinkedToPreExistingCe();
	}
	
	@Override
	protected void teardownImpl() throws Exception {
		try {
			contils.retrieveContactFromPole(targetContactEvent.getObjectRef(), 
				10, poleDirect, securityContextId, true);
		} catch (Exception ex) {
			logger.warn("Failing to delete target CE :" + ex.getMessage());
		}
		try {
			contils.retrieveContactFromPole(preExistingContactEvent.getObjectRef(), 
				10, poleDirect, securityContextId, true);
		} catch (Exception ex) {
			logger.warn("Failing to delete pre-existing CE :" + ex.getMessage());
		}
		
		preExistingContactEvent = null;
		targetContactEvent = null;
		
		super.teardownImpl();
	}
		
	// ********************************************************************
	// ***                      PERSON - #35809                         ***
	// ********************************************************************

	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		mingleRef=35809,
		mingleTitle="Bus Svcs - Public Engagement - Match incoming Person data against "
				+ "researched Person records in POLE using Public Engagement Account Ref",
		acceptanceCriteriaRefs="CR3.1,CR3.2",
		given="Public Engagement has provided new incident details to CONNECT in the form of "
				+ "a 'Create ContactEvent' request via PEQM"
				+ "AND Business Services has found one or more Links from ContactEvent to Person"
				+ "AND Business Services has queried POLE for existing Persons identifiable by their"
				+ "PeAccountInfo held on Link records between ContactEvent and Person",
		when="An existing Person is found in POLE that is researched",
		then="A new iteration of that Person will be created in POLE against the Group record found by the Query"
				+ "AND The new iteration will contain the data submitted from Public Engagement"
	)
	public void testPersonIterationCreatedInSameGroupWhenMatchedOnPeAccountRef() throws Exception {

		doSetup();
		
		prepareTargetContactEvent(peAccountRef, null, targetCeReference, false, 
				preExistingCommsValue, false, 
				null, false);
		doDataConsolidationForCreateContactEvent();
		extractRecordsLinkedToTargetCe();

		assertEquals("Expect new CE's person to be added to the same group as first person", 
				preExistingPerson.getIterationGroupRef(), targetPerson.getIterationGroupRef());
			
		assertNotEquals("Expect new CE's person to have different object ref to the first person", 
				preExistingPerson.getObjectRef(), targetPerson.getObjectRef());

		assertTrue("Expect new CE's person to be created as researched",
				targetPerson.getResearched());
		
		checkLinkResearchedComparedToObject(targetPersonLink, targetPerson);
	}

	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		mingleRef=35809,
		mingleTitle="Bus Svcs - Public Engagement - Match incoming Person data against "
				+ "researched Person records in POLE using Public Engagement Account Ref",
		acceptanceCriteriaRefs="CR4",
		given="Public Engagement has provided new incident details to CONNECT in the form of "
				+ "a 'Create ContactEvent' request via PEQM"
				+ "AND Business Services has found one or more Links from ContactEvent to Person"
				+ "AND Business Services has queried POLE for existing Persons identifiable by their"
				+ "PeAccountInfo held on Link records between ContactEvent and Person",
		when="An existing Person is found in POLE that is researched",
		then="A separate Person record (free-standing single Iteration - no group) will be created "
				+ "in POLE, as per the data that was originally submitted to Business Services."
	)
	public void testPersonNewGroupCreatedIfOnlyMatchIsToUnresearchedRecord() throws Exception {

		doSetup(false, peAccountRef, //the "false" (unresearched person) should prevent matching of 
									//person and comms, because comms is chained to the match of person.
				null,
				true, preExistingCommsValue,
				true, preExistingLocationUprn);
		
		prepareTargetContactEvent(peAccountRef, null, targetCeReference, false,
				preExistingCommsValue, false, 
				null, false);
		doDataConsolidationForCreateContactEvent();
		extractRecordsLinkedToTargetCe();

		assertNotEquals("Expect new CE's person to be added to different group to first person", 
				preExistingPerson.getIterationGroupRef(), targetPerson.getIterationGroupRef());

		assertNotEquals("Expect new CE's person to have different object ref to the first person", 
				preExistingPerson.getObjectRef(), targetPerson.getObjectRef());
		
		assertFalse("Expect new CE's person to be created as unresearched", targetPerson.getResearched());
		
		checkLinkResearchedComparedToObject(targetPersonLink, targetPerson);
	}

	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		mingleRef=35809,
		mingleTitle="Bus Svcs - Public Engagement - Match incoming Person data against "
				+ "researched Person records in POLE using Public Engagement Account Ref",
		acceptanceCriteriaRefs="CR5",
		given="Public Engagement has provided new incident details to CONNECT in the form of "
				+ "a 'Create ContactEvent' request via PEQM"
				+ "AND Business Services has found one or more Links from ContactEvent to Person"
				+ "AND Business Services has queried POLE for existing Persons identifiable by their"
				+ "PeAccountInfo held on Link records between ContactEvent and Person",
		when="No existing Person is found in POLE by the Query.",
		then="A separate Person record (free-standing single Iteration - no group) will be created in "
				+ "POLE, as per the data that was originally submitted to Business Services."
	)
	public void testPersonGroupCreatedForPersonWithDifferentPeAccountRef() throws Exception {

		doSetup(true, peAccountRef, null,
				true, preExistingCommsValue,
				true, preExistingLocationUprn);
		
		prepareTargetContactEvent(
				generatePeAccountRef() + "%$" /* different */, null, targetCeReference, false,
				preExistingCommsValue, false, 
				null, false);
		
		doDataConsolidationForCreateContactEvent();
		extractRecordsLinkedToTargetCe();

		assertNotEquals("Expect second person to be added to different group to first person", 
				preExistingPerson.getIterationGroupRef(), targetPerson.getIterationGroupRef());
			
		assertNotEquals("Expect second person to have different object ref to the first person", 
				preExistingPerson.getObjectRef(), targetPerson.getObjectRef());
		
		checkLinkResearchedComparedToObject(targetPersonLink, targetPerson);
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			mingleRef=35809,
			mingleTitle="Bus Svcs - Public Engagement - Match incoming Person data against "
					+ "researched Person records in POLE using Public Engagement Account Ref",
			acceptanceCriteriaRefs="TODO #35809",
			given="Public Engagement has provided new incident details to CONNECT in the form of "
					+ "a 'Create ContactEvent' request via PEQM"
					+ "AND Business Services has found one or more Links from ContactEvent to Person"
					+ "AND Business Services has queried POLE for existing Persons identifiable by their"
					+ "PeAccountInfo held on Link records between ContactEvent and Person",
			when="An existing Person is found in POLE that is researched"
					+ "AND the persons on the incoming record have the same PE Account Ref but are different",
			then="Business Services will throw a validation exception"
		)
	public void testMultiplePersonsWithSamePeAccountRefDifferentObjectRefFails() throws Exception {
		
		if (!reporterIsVictim) {
			try {
				doSetup(true, peAccountRef, null,
						false, null,
						false, null);
				
				prepareTargetContactEvent(peAccountRef, peAccountRef, targetCeReference, false,
						null, false, 
						null, false);
				doDataConsolidationForCreateContactEvent();
				
				fail("Created a contact event with 2 different people with the same PE Account Ref");
			} catch (Exception e) {
				assertTrue(e instanceof InvalidDataException);
				assertTrue(e.getMessage().contains("Abandoning PersonDto provided by client"));
			}
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=215,
		mingleRef=37106,
		mingleTitle="WP215 - Bus Svcs - Validation Framework - Links to Person Object",
		acceptanceCriteriaRefs="TODO",
		given="A new 'Create ContactEvent' request is sent in with an illegal link to an existing person"
				+ "AND Business Services has queried POLE for existing Persons",
		when="An existing Person is found in POLE that is researched",
		then="A new iteration of that Person will be created in POLE against the Group record found by the Query"
				+ "AND The new iteration will not be validated"
	)
	public void testPersonIterationCreatedInSameGroupMarkedAsNotForValidation() throws Exception {

		doSetup();
		
		ContactEventDto contactEvent = contils.createSimpleContactEvent(null, null);
		
		LinkNodeDto linkNode = new LinkNodeDto();
		linkNode.setModificationStatus(ModificationStatusDto.CREATE);
		linkNode.setSourcePoleObjectType(PoleNames.CONTACT_EVENT);
		linkNode.setSourcePoleObjectRef(contactEvent.getObjectRef());
		linkNode.setLinkReason(PoleObjectsServiceConstants.PERSON_REPORTING_LINK_REASON);
		linkNode.setToPoleObjectType(PoleNames.PERSON);
		linkNode.setToPoleObjectRef(preExistingPerson.getObjectRef());
		
		PutPoleObjectsRequestDto ppoReq = contils.createPutPoleObjectsRequest(
				ContactEventTestUtils.createBusinessServiceInfo(securityContextId), contactEvent);
		preExistingPerson.setLinks(null);
		ppoReq.addPoleObject(preExistingPerson);
		ppoReq.addLinkNode(linkNode);
		
		CreateContactEventDataConsolidationBean dcb = new CreateContactEventDataConsolidationBean(ppoReq,
				PoleMetadataCacheBean.getInstance(), new KeyAllocationContext(), new ArrayList<String>(),
				systemParamsCacheBean, linkTypeCacheBean);
		dcb.flattenPoleGraph_ForTestOnly();
		
		ContactEventDataConsolidationJob dcj = new ContactEventDataConsolidationJob(PoleMetadataCacheBean.getInstance(),
				dcb, contactEvent.getEntityKey(), false);
		dcj.correctIllegalLinksToUnresearchedRecords(dcb);
		
		LinkNodeDto linkPersonCopy = dcb.getLinkNodes().get(0);
		EntityKey personCopyKey = new EntityKey(linkPersonCopy.getToPoleObjectRef(), PoleNames.PERSON);
		
		assertEquals("Expecting only object to be in list of objects not to be validated", 1,
				dcb.getCopyCreateObjectsToBeExcludedFromValidation().entrySet().size());
		assertNotNull("Expecting the person copy object to be in list of objects not to be validated",
				dcb.getCopyCreateObjectsToBeExcludedFromValidation().get(personCopyKey));
	}
	
	// *********************************************************************
	// ***                       COMMS - #35810                          ***
	// *********************************************************************

	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		mingleRef=35810,
		mingleTitle="Bus Svcs - Public Engagement - Match researched Comms records by email or telephone "
				+ "after matching Person based on Public Engagement Account Ref",
		acceptanceCriteriaRefs="CR1,CR4",
		given="My submitted Person data has been matched against an existing Person record in CONNECT by use of PeAccountRef information"
				+ "AND Business Services has queried POLE for researched Comms data matching Email or Telephone Number",
		when="Comms data is found and it is researched",
		then="The existing Comms record in POLE will be linked-to in place of the submitted Comms data, which will be discarded."
				+ "Therefore no new Comms data will be created in POLE."
	)
	public void testCommsNotUsedMatchNewRecordWhenUnresearched() throws Exception {

		doTestCommsMatchAfterPersonMatch(true, false, preExistingCommsValue);

		assertNotEquals("Expect second comms to be different to first comms", 
			preExistingComms.getObjectRef(), targetComms.getObjectRef());
		
		assertFalse("Expect new Comms to be unresearched", targetComms.getResearched());
		
		checkLinkResearchedComparedToObject(targetCommsLink, targetComms);
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		mingleRef=35810,
		mingleTitle="Bus Svcs - Public Engagement - Match researched Comms records by email or telephone "
			+ "after matching Person based on Public Engagement Account Ref",
		acceptanceCriteriaRefs="CR1,CR3",
		given="My submitted Person data has been matched against an existing Person record "
			+ "in CONNECT by use of PeAccountRef information"
			+ "AND Business Services has queried POLE for researched Comms data matching Email or Telephone Number",
		when="Comms data is found but it has not been marked as researched",
		then="The submitted Comms data will be used. It will be considered 'unresearched'."
	)
	public void testCommsMatchReusesExistingRecordAfterMatchingPersonFirst() throws Exception {

		doTestCommsMatchAfterPersonMatch(true, true, preExistingCommsValue);

		assertEquals("Expect second comms to be identical to first comms", 
				preExistingComms.getObjectRef(), targetComms.getObjectRef());

		assertTrue("Expect new/old Comms to be researched", targetComms.getResearched());
		
		checkLinkResearchedComparedToObject(targetCommsLink, targetComms);
	}
	
	/**
	 * Negative test to show that Comms won't be matched unless Person matched first
	 * 
	 * @throws Exception
	 */
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		mingleRef=35810,
		mingleTitle="Bus Svcs - Public Engagement - Match researched Comms records by email or telephone "
			+ "after matching Person based on Public Engagement Account Ref",
		acceptanceCriteriaRefs="CR1,CR3",
		given="My submitted Person data has been matched against an existing Person record "
			+ "in CONNECT by use of PeAccountRef information"
			+ "AND Business Services has queried POLE for researched Comms data matching Email or Telephone Number",
		when="Comms data is found but it has not been marked as researched",
		then="The submitted Comms data will be used. It will be considered 'unresearched'."
	)
	public void testCommsMatchImpossibleWithoutPersonMatch() throws Exception {

		doTestCommsMatchAfterPersonMatch(false, true, preExistingCommsValue);

		assertNotEquals("Expect second comms to be different to first comms", 
			preExistingComms.getObjectRef(), targetComms.getObjectRef());
		
		assertFalse("Expect new Comms to be unresearched", targetComms.getResearched());
		
		checkLinkResearchedComparedToObject(targetCommsLink, targetComms);
	}
	
	/**
	 * Negative test to show that different Comms "values" (for "mainNumber" and "emailAddress") will
	 * result in the existing Comms record NOT being linked to, for the second CE.
	 * 
	 * @throws Exception
	 */
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		mingleRef=35810,
		mingleTitle="Bus Svcs - Public Engagement - Match researched Comms records by email or telephone "
			+ "after matching Person based on Public Engagement Account Ref",
		acceptanceCriteriaRefs="CR1,CR3",
		given="My submitted Person data has been matched against an existing Person record "
			+ "in CONNECT by use of PeAccountRef information"
			+ "AND Business Services has queried POLE for researched Comms data matching Email or Telephone Number",
		when="Comms data is found but it has not been marked as researched",
		then="The submitted Comms data will be used. It will be considered 'unresearched'."
	)
	public void testCommsMatchRequiresIdenticalCommsValue() throws Exception {
		
		doTestCommsMatchAfterPersonMatch(false, true, "i_am_not_the_s@me_comms_value.com");

		assertNotEquals("Expect second comms to be different to first comms", 
			preExistingComms.getObjectRef(), targetComms.getObjectRef());

		assertFalse("Expect new Comms to be unresearched", targetComms.getResearched());
		
		checkLinkResearchedComparedToObject(targetCommsLink, targetComms);
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			mingleRef=35810,
			mingleTitle="Bus Svcs - Public Engagement - Match researched Comms records by email or telephone "
					+ "after matching Person based on Public Engagement Account Ref",
			acceptanceCriteriaRefs="CR1,CR4",
			given="My submitted Person data has been matched against an existing Person record in CONNECT by use of PeAccountRef information"
					+ "AND Business Services has queried POLE for researched Comms data matching Email or Telephone Number",
			when="Comms data is found and it is researched",
			then="The existing Comms record in POLE will be linked-to in place of the submitted Comms data, which will be discarded."
					+ "Therefore no new Comms data will be created in POLE."
		)
	public void testMultipleLinksToResearchedCommsSetToResearched() throws Exception {
		
		String victimPeAccountRef = generatePeAccountRef() + "%$*";
		
		doSetup(true, peAccountRef, victimPeAccountRef,
				true, preExistingCommsValue,
				false, null);
		
		prepareTargetContactEvent(peAccountRef, victimPeAccountRef, targetCeReference, false,
				preExistingCommsValue, true, 
				null, false);
		doDataConsolidationForCreateContactEvent();
		extractRecordsLinkedToTargetCe();
		
		assertEquals("Expect Comms to be reused / link to be made to existing Comms", 
				preExistingComms.getObjectRef(), targetComms.getObjectRef());

		assertTrue("Expect Comms to be the previously saved researched record", 
				targetComms.getResearched());
		
		checkLinkResearchedComparedToObject(targetCommsLink, targetComms);
		
		assertEquals("Expect Victim Comms to be reused / link to be made to existing Comms", 
				preExistingComms.getObjectRef(), targetVictimComms.getObjectRef());

		assertTrue("Expect Victim Comms to be the previously saved researched record", 
				targetVictimComms.getResearched());
		
		checkLinkResearchedComparedToObject(targetVictimCommsLink, targetVictimComms);
	}

	/**
	 * Helper method for all scenarios relating to #35810 (Comms match after Person match)
	 * 
	 * @param existingPersonResearched when set to true, sets the Person to researched, which
	 * 	should result in a match, paving the way for a match of Comms
	 * @param existingCommsResearched when set to true, sets the pre-existing Comms to
	 * 	researched, which makes them candidate for matching against a new CE's Comms.
	 * @param newCommsValue value of the new Comms record. Forms matching criteria
	 * 	against existing Comms records in POLE.
	 * @throws Exception
	 */
	private void doTestCommsMatchAfterPersonMatch(
			boolean existingPersonResearched, boolean existingCommsResearched, String newCommsValue) throws Exception {

		doSetup(existingPersonResearched, peAccountRef, null,
				existingCommsResearched, newCommsValue,
				true, preExistingLocationUprn);
		
		prepareTargetContactEvent(peAccountRef, null, targetCeReference, false,
				preExistingCommsValue, false, 
				null, false);
		doDataConsolidationForCreateContactEvent();
		extractRecordsLinkedToTargetCe();
	}
	
	// *******************************************************************
	// ***                     LOCATION - #35808                       ***
	// *******************************************************************
	
	/**
	 * Temporary code to buy ourselves time with the customer until we can do the Gazetteer lookup
	 * enhancement that calls Compass. - DA 14/02/2017. Revert to earlier version once enhanced
	 * functionality is added to match based on call to Compass.
	 * 
	 * @throws Exception
	 */
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		mingleRef=35808,
		mingleTitle="Bus Svcs - UPRN Location Matching - avoid duplicated Locations with same UPRN in POLE ",
		acceptanceCriteriaRefs="NA",
		given="I have submitted my QAS-obtained Location-data within the incident details I provided to the Public Engagement portal"
			+ "AND CONNECT has queried POLE for existing Location data that has the same UPRN stored against it",
		when="No Location record exists in POLE containing the same UPRN",
		then="The new Location created based on the incoming data will be ***UNRESEARCHED - TEMP CODE - DA 14/02/2017***"
	)
	public void testLocationWithNewUprnUsesSubmittedData_UNRESEARCHED_TEMP() throws Exception {

		doSetup(false, peAccountRef, null,
				false, preExistingCommsValue,
				true, preExistingLocationUprn);
		
		String uniqueUprn = "ABC" + String.valueOf(System.currentTimeMillis());
		doTestLocationMatch(uniqueUprn, false); //TODO CODE REVIEW: Found someone had hacked a single value 
												//in here which ALWAYS MATCHED a record in POLE regardless if the 
												//functionality was working. >:-(

		assertNotEquals("Expect second Location to be different to first Location", 
			preExistingLocation.getObjectRef(), targetLocation.getObjectRef());

		assertFalse("Expect new Location to be unresearched", targetLocation.getResearched());
		
		checkLinkResearchedComparedToObject(targetLocationLink, targetLocation);
	}
	
	/**
	 * Temporary code to buy ourselves time with the customer until we can do the Gazetteer lookup
	 * enhancement that calls Compass. - DA 14/02/2017. Revert to earlier version once enhanced
	 * functionality is added to match based on call to Compass.
	 * 
	 * @throws Exception
	 */
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		mingleRef=35808,
		mingleTitle="Bus Svcs - UPRN Location Matching - avoid duplicated Locations with same UPRN in POLE ",
		acceptanceCriteriaRefs="CR1,CR3.1,CR3.2,CR3.3",
		given="I have submitted my QAS-obtained Location-data within the incident details I provided to the Public Engagement portal"
				+ "AND CONNECT has queried POLE for existing Location data that has the same UPRN stored against it",
		when="Location record exists in POLE containing the same UPRN which is unresearched.",
		then="The Location data submitted to Public Engagement as part of the incident details will be used to create a new Location record in POLE"
				+ "AND The newly created Location record in POLE will have the UPRN stored against it as an external ref"
				+ "AND The new Location created based on the incoming data will be ***UNRESEARCHED - TEMP CODE - DA 14/02/2017***"
	)
	public void testLocationRecordUnresearchedIgnoredInFavourOfSubmittedData_UNRESEARCHED_TEMP() throws Exception {

		doSetup(false, peAccountRef, null,
				false, preExistingCommsValue,
				false, preExistingLocationUprn);
		
		doTestLocationMatch(preExistingLocationUprn, false);

		assertNotEquals("Expect second Location to be different to pre-existing Location", 
			preExistingLocation.getObjectRef(), targetLocation.getObjectRef());

		assertFalse("Expect new Location to be changed to unresearched", targetLocation.getResearched());
		
		checkLinkResearchedComparedToObject(targetLocationLink, targetLocation);
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		mingleRef=35808,
		mingleTitle="Bus Svcs - UPRN Location Matching - avoid duplicated Locations with same UPRN in POLE ",
		acceptanceCriteriaRefs="CR1,CR4",
		given="I have submitted my QAS-obtained Location-data within the incident details I provided to the Public Engagement portal"
				+ "AND CONNECT has queried POLE for existing Location data that has the same UPRN stored against it",
		when="Location record exists in POLE containing the same UPRN which is researched.",
		then="Location found in POLE will be linked to. (No additional Location will be created in POLE)."
	)
	public void testLocationMatchedByUprnReusesExistingPoleRecord() throws Exception {

		doSetup(false, peAccountRef, null,
				false, preExistingCommsValue,
				true, preExistingLocationUprn);
		
		doTestLocationMatch(preExistingLocationUprn, false);

		assertEquals("Expect Location to be reused / link to be made to existing Location", 
			preExistingLocation.getObjectRef(), targetLocation.getObjectRef());

		assertTrue("Expect Location to be the previously saved researched record", 
				targetLocation.getResearched());
		
		checkLinkResearchedComparedToObject(targetLocationLink, targetLocation);
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		mingleRef=35808,
		mingleTitle="Bus Svcs - UPRN Location Matching - avoid duplicated Locations with same UPRN in POLE ",
		acceptanceCriteriaRefs="CR1,CR4",
		given="I have submitted my QAS-obtained Location-data within the incident details I provided to the Public Engagement portal"
				+ "AND CONNECT has queried POLE for existing Location data that has the same UPRN stored against it",
		when="Location record exists in POLE containing the same UPRN which is researched.",
		then="Location found in POLE will be linked to. (No additional Location will be created in POLE)."
	)
	public void testMultipleLinksToLocationMatchedByUprnSetToResearched() throws Exception {

		doSetup(false, peAccountRef, "",
				false, preExistingCommsValue,
				true, preExistingLocationUprn);
		
		doTestLocationMatch(preExistingLocationUprn, true);

		assertEquals("Expect Location to be reused / link to be made to existing Location", 
			preExistingLocation.getObjectRef(), targetLocation.getObjectRef());

		assertTrue("Expect Location to be the previously saved researched record", 
				targetLocation.getResearched());
		
		checkLinkResearchedComparedToObject(targetLocationLink, targetLocation);
		
		assertEquals("Expect Person Reporting Location to be reused / link to be made to existing Location", 
				preExistingLocation.getObjectRef(), targetPersonReportingLocation.getObjectRef());

		assertTrue("Expect Person Reporting Location to be the previously saved researched record", 
				targetPersonReportingLocation.getResearched());
		
		checkLinkResearchedComparedToObject(targetPersonReportingLocationLink, targetPersonReportingLocation);
		
		assertEquals("Expect Victim Location to be reused / link to be made to existing Location", 
				preExistingLocation.getObjectRef(), targetVictimLocation.getObjectRef());

		assertTrue("Expect Victim Location to be the previously saved researched record", 
				targetVictimLocation.getResearched());
		
		checkLinkResearchedComparedToObject(targetVictimLocationLink, targetVictimLocation);
	}
	
	// *******************************************************************
	// ***                     LOCATION - #32656                       ***
	// *******************************************************************
		
	/**
	 * Note : Compass accepts only Long values for UPRN  
	 */
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		mingleRef=35808,
		mingleTitle="Bus Svcs - UPRN Location Matching - avoid duplicated Locations with same UPRN in POLE ",
		acceptanceCriteriaRefs="NA",
		given="I have submitted my QAS-obtained Location-data within the incident details I provided to the Public Engagement portal"
			+ "AND CONNECT has queried POLE for existing Location data that has the same UPRN stored against it",
		when="No Location record exists in POLE containing the same UPRN, compass search will be performed, "
				+ "no location found in compass too.",
		then="The new Location created based on the incoming data will be UNRESEARCHED"
	)
	public void testLocationWithNewUprnWithCompassSearchUsesSubmittedData() throws Exception {
		String originalPreExistingLocationUprn = preExistingLocationUprn;
		preExistingLocationUprn = String.valueOf(System.currentTimeMillis());
		
		doSetup(false, peAccountRef, null,
				false, preExistingCommsValue,
				true, preExistingLocationUprn);
		
		String uniqueUprn = String.valueOf(System.currentTimeMillis());
		doTestLocationMatch(uniqueUprn, false);

		assertNotEquals("Expect second Location to be different to first Location", 
			preExistingLocation.getObjectRef(), targetLocation.getObjectRef());
		
		assertNotEquals("Expect uprn of both location to be different", 
				CommonPoleObjectTestUtils.extractUprnFromLocation(preExistingLocation),
				CommonPoleObjectTestUtils.extractUprnFromLocation(targetLocation));		

		assertFalse("Expect new Location to be unresearched", targetLocation.getResearched());
		
		checkLinkResearchedComparedToObject(targetLocationLink, targetLocation);
		
		preExistingLocationUprn = originalPreExistingLocationUprn;
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		mingleRef=35808,
		mingleTitle="Bus Svcs - UPRN Location Matching - avoid duplicated Locations with same UPRN in POLE ",
		acceptanceCriteriaRefs="CR1,CR3.1,CR3.2,CR3.3",
		given="I have submitted my QAS-obtained Location-data within the incident details I provided to the Public Engagement portal"
				+ "AND CONNECT has queried POLE for existing Location data that has the same UPRN stored against it",
		when="Location record exists in POLE containing the same UPRN which is unresearched. So compass search will be performed,"
				+ " but no location found in compass too.",
		then="The Location data submitted to Public Engagement as part of the incident details will be used to create a new Location record in POLE"
				+ "AND The newly created Location record in POLE will have the UPRN stored against it as an external ref"
				+ "AND The new Location created based on the incoming data will be UNRESEARCHED"
	)
	public void testLocationRecordUnresearchedIgnoredInFavourOfSubmittedData() throws Exception {
		String originalPreExistingLocationUprn = preExistingLocationUprn;
		preExistingLocationUprn = String.valueOf(System.currentTimeMillis());
		
		doSetup(false, peAccountRef, null,
				false, preExistingCommsValue,
				false, preExistingLocationUprn);
		
		doTestLocationMatch(preExistingLocationUprn, false);

		assertNotEquals("Expect second Location to be different to pre-existing Location", 
			preExistingLocation.getObjectRef(), targetLocation.getObjectRef());
		
		assertEquals("Expect uprn of both location to same different", 
				CommonPoleObjectTestUtils.extractUprnFromLocation(preExistingLocation),
				CommonPoleObjectTestUtils.extractUprnFromLocation(targetLocation));		

		assertFalse("Expect new Location to be changed to unresearched", targetLocation.getResearched());
		
		checkLinkResearchedComparedToObject(targetLocationLink, targetLocation);
		
		preExistingLocationUprn = originalPreExistingLocationUprn;
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		mingleRef=35808,
		mingleTitle="Bus Svcs - UPRN Location Matching - avoid duplicated Locations with same UPRN in POLE ",
		acceptanceCriteriaRefs="CR1,CR3.1,CR3.2,CR3.3",
		given="I have submitted my QAS-obtained Location-data within the incident details I provided to the Public Engagement portal"
				+ "AND CONNECT has queried POLE for existing Location data that has the same UPRN stored against it",
		when="Location record exists in POLE containing the same UPRN which is researched. So compass search will not be done",
		then="Location found in POLE will be linked to. (No additional Location will be created in POLE)."
	)
	public void testLocationMatchedByUprnReusesExistingPoleRecordWithCompassSearchEnabled() throws Exception {
		String originalPreExistingLocationUprn = preExistingLocationUprn;
		preExistingLocationUprn = String.valueOf(System.currentTimeMillis());
		
		doSetup(false, peAccountRef, null,
				false, preExistingCommsValue,
				true, preExistingLocationUprn);
		
		doTestLocationMatch(preExistingLocationUprn, false);

		assertEquals("Expect Location to be reused / link to be made to existing Location", 
				preExistingLocation.getObjectRef(), targetLocation.getObjectRef());
		
		assertEquals("Expect uprn of both location to same different", 
				CommonPoleObjectTestUtils.extractUprnFromLocation(preExistingLocation),
				CommonPoleObjectTestUtils.extractUprnFromLocation(targetLocation));
		
		assertTrue("Expect Location to be the previously saved researched record", 
					targetLocation.getResearched());
						
		checkLinkResearchedComparedToObject(targetLocationLink, targetLocation);
		
		preExistingLocationUprn = originalPreExistingLocationUprn;
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		mingleRef=35808,
		mingleTitle="Bus Svcs - UPRN Location Matching - avoid duplicated Locations with same UPRN in POLE ",
		acceptanceCriteriaRefs="CR1,CR3.1,CR3.2,CR3.3",
		given="I have submitted my QAS-obtained Location-data within the incident details I provided to the Public Engagement portal"
				+ "AND CONNECT has queried POLE for existing Location data that has the same UPRN stored against it",
		when="Location record do not exists in POLE containing the same UPRN which is researched. So compass search will be performed."
				+ " Matching Compass Record Found.",
		then="New Pole record will be created based on the data found from compass record and the newly created record will be linked."
	)
	public void testLocationMatchedByUprnReusesExistingCompassRecordWithCompassSearchEnabled() throws Exception {
		String originalPreExistingLocationUprn = preExistingLocationUprn;
		// matching valid compass UPRN value (do not change else test will fail or replace it with another matching valid compass UPRN) 
		preExistingLocationUprn = "100032205544"; // UPRN which return location with locality field - As it was missed
													 // in Validation configuration xml validation was failing. So just
													 // to ensure it works fine with locality field specified UPRN which
													 // return location with locality field.
		
		doSetup(false, peAccountRef, null,
				false, preExistingCommsValue,
				false, preExistingLocationUprn);
		
		doTestLocationMatch(preExistingLocationUprn, false);

		assertNotEquals("Expect new location is created based on compass data in pole.", 
				preExistingLocation.getObjectRef(), targetLocation.getObjectRef());
		
		assertEquals("Expect uprn of both location to be same", 
				CommonPoleObjectTestUtils.extractUprnFromLocation(preExistingLocation),
				CommonPoleObjectTestUtils.extractUprnFromLocation(targetLocation));
		
		assertTrue("Expect Location to be the newly saved researched record", 
					targetLocation.getResearched());
						
		checkLinkResearchedComparedToObject(targetLocationLink, targetLocation);
		
		preExistingLocationUprn = originalPreExistingLocationUprn;
	}

	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		mingleRef=35808, //See also note for defect 37959
		mingleTitle="Bus Svcs - UPRN Location Matching - avoid duplicated Locations with same UPRN in POLE ",
		acceptanceCriteriaRefs="CR1,CR3.1,CR3.2,CR3.3",
		given="I have submitted my QAS-obtained Location-data within the incident details I provided to the Public Engagement portal"
				+ "AND CONNECT has queried POLE for existing Location data that has the same UPRN stored against it"
				+ "AND [ps for Defect #37959] the Location is UNRESEARCHED",
		when="Location record do not exists in POLE containing the same UPRN which is researched. So compass search will be performed."
				+ " No matching Compass Record is Found as invalid UPRN is passed. So compass will return empty response.",
		then="The Location data submitted to Public Engagement as part of the incident details will be used to create a new Location record in POLE"
				+ "AND The newly created Location record in POLE will have the UPRN stored against it as an external ref"
				+ "AND The new Location created based on the incoming data will be UNRESEARCHED"
	)
	public void testUnresearchedLocationWithUprnUnmatchedInCompassIsToleratedAsNewUnresearchedRecord() throws Exception {
		String originalPreExistingLocationUprn = preExistingLocationUprn;
		// matching valid compass UPRN value (do not change else test will fail or replace it with another matching valid compass UPRN) 
		preExistingLocationUprn = String.valueOf(System.currentTimeMillis());
		
		doSetup(false, peAccountRef, null,
				false, preExistingCommsValue,
				false, preExistingLocationUprn);//IMPORTANT: unresearched Location - must be tolerated when it doesn't match UPRN
		String uniqueUprn = "invalid_uprn_13190662568";
		doTestLocationMatch(uniqueUprn, false);

		assertNotEquals("Expect new location is created based on PE data(user entered data) in pole.", 
				preExistingLocation.getObjectRef(), targetLocation.getObjectRef());
		
		assertNotEquals("Expect uprn of both location to be different.", 
				CommonPoleObjectTestUtils.extractUprnFromLocation(preExistingLocation),
				CommonPoleObjectTestUtils.extractUprnFromLocation(targetLocation));
		
		assertFalse("Expect Location to be the newly saved un-researched record", 
					targetLocation.getResearched());
						
		checkLinkResearchedComparedToObject(targetLocationLink, targetLocation);
		
		preExistingLocationUprn = originalPreExistingLocationUprn;
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
	public void testResearchedLocationWithUprnUnmatchedInCompassIsNotTolerated() throws Exception {

		// matching valid compass UPRN value (do not change else test will fail or replace it with another matching valid compass UPRN) 
		preExistingLocationUprn = String.valueOf(System.currentTimeMillis());
		
		doSetup(false, peAccountRef, null,
				false, preExistingCommsValue,
				true, preExistingLocationUprn);//IMPORTANT: researched Location - must be matched on UPRN 

		String uniqueUprn = "invalid_uprn_" + preExistingLocationUprn;

		prepareTargetContactEvent(peAccountRef, null, targetCeReference, false, 
				preExistingCommsValue, false, 
				uniqueUprn, false);

		PutPoleObjectsRequestDto ppoReq = contils.createPutPoleObjectsRequest(
				ContactEventTestUtils.createBusinessServiceInfo(securityContextId), targetContactEvent);
				
		//Quick loop through the links to find the Location link.
		//Setting it to researched=true to upset UPRN matching by mandating a match. 
		for (LinkDto linkDto : targetContactEvent.getLinks()) {
			if (linkDto.getToPoleObject() instanceof LocationDto) {

				linkDto.setResearched(true);
				LocationDto location = (LocationDto) linkDto.getToPoleObject();
				location.setResearched(true);
			}
		}
		
		try {
			
			PutPoleObjectsResponseDto response = poleBusinessServices.putPoleObjects(ppoReq);
			
			String resXml = JaxbXmlMarshaller.convertToXml(response);
			fail("Researched Location with invalid UPRN should not be tolerated. Received response: " + resXml);
			
		} catch (Exception e) {
			assertTrue(e instanceof InvalidDataException);
			assertTrue(e.getMessage().contains("UPRN"));
		}
	}
	
	/**
	 * Send a new CE into Business Services with the location, which may or may not match
	 * existing records in POLE.
	 * 
	 * @param newLocationUprn value of the new Comms record. Forms matching criteria
	 * 	against existing Comms records in POLE.
	 * @throws Exception
	 */
	private void doTestLocationMatch(String newLocationUprn, boolean assignUprnToAllLocations) throws Exception {
		
		prepareTargetContactEvent(peAccountRef, null, targetCeReference, false, 
				preExistingCommsValue, false, 
				newLocationUprn, assignUprnToAllLocations);
		doDataConsolidationForCreateContactEvent();
		extractRecordsLinkedToTargetCe();
	}
	
	// *********************************************************************
	// ***                  HELPER / UTILITY METHODS                     ***
	// *********************************************************************

	/**
	 * Has a bunch of options to simulate different scenarios. Especially it has been expanded to
	 * facilitate "double matching" where 2 x incoming records match a single existing
	 * record (in which case, our code must be smart enough to avoid creating duplicate records).
	 * 
	 * @param peAccountRef the PE Account Ref to be added onto the new CE
	 * @param targetCePeRef if set, will be assigned to a PeContactInfo on the Person Reporting
	 * @param assignPeAccRefToAllPersons if set, will result in targetCePeRef being assigned to
	 * 	       Person Reporting AND Victim records. NOTE: DOING SO IS UNHEALTHY and should 
	 *         elicit a validation error.
	 * @param targetCommsValue if set, will be assigned to the Comms within the Person Reporting
	 * @param assignCommsValueToAllComms if set, will result in the same Comms value being applied
	 * 	       to more than one Comms record, to simulate a "multiple Comms matching" scenario.
	 * @param targetUprnValue if set, will be assigned to an external ref of type UPRN
	 * @param assignUprnToAllLocations if set, will result in the same UPRN value being applied
	 * 	       to more than one Location record, to simulate a "multiple Location matching"
	 *         scenario.
	 */
	protected void prepareTargetContactEvent(
			String peAccountRef, String victimPeAccountRef,
			String targetCePeRef, boolean assignPeAccRefToAllPersons, 
			String targetCommsValue, boolean assignCommsValueToAllComms,
			String targetUprnValue, boolean assignUprnToAllLocations) {
		
		targetContactEvent = contils.createContactEventDto(null, null, targetCePeRef, peAccountRef, reporterIsVictim);		
		targetContactEvent.setDescription("BURGLARY NUMBER TWO:" + targetCePeRef);

		for (LinkDto linkDto : targetContactEvent.getLinks()) {
			if (linkDto.getToPoleObject() instanceof PersonDto) {
				
				PersonDto person = (PersonDto) linkDto.getToPoleObject();
				
				if (linkDto.getLinkReason().equals(PoleObjectsServiceConstants.PERSON_REPORTING_LINK_REASON)) {
					
					linkDto.setPeContactInfoList(new ArrayList<PeContactInfoDto>());
					if (targetCePeRef != null) {
						PeContactInfoDto peContactInfo = new PeContactInfoDto();
						peContactInfo.setModificationStatus(ModificationStatusDto.CREATE);
						peContactInfo.setPeAccountRef(peAccountRef);
						peContactInfo.setPeReference(targetCePeRef);
						linkDto.addPeContactInfo(peContactInfo);
					}
					
					for (LinkDto linkFromPerson : person.getLinks()) {
						if (linkFromPerson.getLinkReason().equals(PoleObjectsServiceConstants.EMAIL_LINK_REASON)) {

							CommsDto comms = (CommsDto)linkFromPerson.getToPoleObject();
							comms.setEmailAddress(null);
							comms.setMainNumber(targetCommsValue);//for some reason the system uses the "main number" field even for email address
						}
					}
					
				} else if (assignPeAccRefToAllPersons) {
					
					linkDto.setPeContactInfoList(new ArrayList<PeContactInfoDto>());
					
					if (targetCePeRef != null) {
						PeContactInfoDto peContactInfo = new PeContactInfoDto();
						peContactInfo.setModificationStatus(ModificationStatusDto.CREATE);
						peContactInfo.setPeAccountRef(peAccountRef);
						peContactInfo.setPeReference(targetCePeRef);
						linkDto.addPeContactInfo(peContactInfo);
					}
				} else if (linkDto.getLinkReason().equals(PoleObjectsServiceConstants.VICTIM_LINK_REASON)) {
					
					linkDto.setPeContactInfoList(new ArrayList<PeContactInfoDto>());
					if (targetCePeRef != null && victimPeAccountRef != null) {
						PeContactInfoDto peContactInfo = new PeContactInfoDto();
						peContactInfo.setModificationStatus(ModificationStatusDto.CREATE);
						peContactInfo.setPeAccountRef(victimPeAccountRef);
						peContactInfo.setPeReference(targetCePeRef);
						linkDto.addPeContactInfo(peContactInfo);
					}
					PersonDto victim = (PersonDto)linkDto.getToPoleObject();
					linkDto.setResearched(victim.getResearched());					
				}
				
				for (LinkDto linkFromPerson : person.getLinks()) {
					if (assignUprnToAllLocations && linkFromPerson.getToPoleObject() instanceof LocationDto) {

						LocationDto location = (LocationDto) linkFromPerson.getToPoleObject();
						if (targetUprnValue != null) {
							ExternalReferenceDto extRef = new ExternalReferenceDto(
									PoleObjectsServiceConstants.UPRN_EXT_REF_TYPE, targetUprnValue);
							extRef.setModificationStatus(ModificationStatusDto.CREATE);
							location.addExternalReference(extRef);
						}
					} else if (assignCommsValueToAllComms && linkFromPerson.getLinkReason().equals(PoleObjectsServiceConstants.EMAIL_LINK_REASON)) {
						
						CommsDto comms = (CommsDto) linkFromPerson.getToPoleObject();
						comms.setEmailAddress(null);
						comms.setMainNumber(targetCommsValue);//for some reason the system uses the "main number" field even for email address
					}
				}
				
			} else if (linkDto.getToPoleObject() instanceof LocationDto) {
				
				if (linkDto.getLinkReason().equals(SubmitContactServiceConstants.CONTACT_LOCATION_LINK_REASON)) {
					LocationDto location = (LocationDto) linkDto.getToPoleObject();
					if (targetUprnValue != null) {
						ExternalReferenceDto extRef = new ExternalReferenceDto(
								PoleObjectsServiceConstants.UPRN_EXT_REF_TYPE, targetUprnValue);
						extRef.setModificationStatus(ModificationStatusDto.CREATE);
						location.addExternalReference(extRef);
					}
				}	
			} else if (linkDto.getToPoleObject() == null
					&& PoleNames.PERSON.equalsIgnoreCase(linkDto.getToPoleObjectType())
					&& linkDto.getLinkReason().equalsIgnoreCase(PoleObjectsServiceConstants.VICTIM_LINK_REASON)) {
				// When reporterIsVictim at that time target contact event victim link do not contain toPoleObject and
				// only contains toPoleObjectType & toPoleObjectRef so add this else if loop
				linkDto.setResearched(null);				
			}
		}
	}

	/**
	 * Create a pre-existing ContactEvent in POLE with a bunch of data in it that could potentially
	 * be matched by the secondary ContactEvent (the one that we're going to test).
	 * 
	 * @throws Exception
	 */
	private void createPreExistingContactEvent(
			boolean researchedPerson, String personPeAccountRef, String victimPeAccountRef,
			boolean researchedComms, String commsValue, 
			boolean researchedLocation, String locationUprn) throws Exception {

		preExistingContactEvent = contils.createContactEventDto(preExistingCeReference, peAccountRef);		
		preExistingContactEvent.setDescription("BURGLARY NUMBER ONE:" + preExistingCeReference);
		
		for (LinkDto linkDto : preExistingContactEvent.getLinks()) {
			if (linkDto.getToPoleObject() instanceof PersonDto) {
				
				if (linkDto.getLinkReason().equals(PoleObjectsServiceConstants.PERSON_REPORTING_LINK_REASON)) {
	
					PeContactInfoDto peContactInfo = new PeContactInfoDto();
					peContactInfo.setModificationStatus(ModificationStatusDto.CREATE);
					peContactInfo.setPeAccountRef(personPeAccountRef);
					peContactInfo.setPeReference(preExistingCeReference);
					linkDto.setPeContactInfoList(null);
					linkDto.addPeContactInfo(peContactInfo);
					linkDto.setResearched(researchedPerson);
				
					PersonDto person = (PersonDto)linkDto.getToPoleObject();
					person.setResearched(researchedPerson);
					person.setIterated(researchedPerson);
	
					for (LinkDto linkFromPerson : person.getLinks()) {
						if (linkFromPerson.getLinkReason().equals(PoleObjectsServiceConstants.EMAIL_LINK_REASON)) {
	
							CommsDto comms = (CommsDto)linkFromPerson.getToPoleObject();
							comms.setResearched(researchedComms);
							comms.setEmailAddress(null);
							comms.setMainNumber(commsValue);//for some reason, we always use mainNumber, not email
							linkFromPerson.setResearched(researchedComms);
						}
					}
				} else if (linkDto.getLinkReason().equals(PoleObjectsServiceConstants.VICTIM_LINK_REASON)) {
	
					linkDto.setPeContactInfoList(null);
					
					if (victimPeAccountRef != null) {
						PeContactInfoDto peContactInfo = new PeContactInfoDto();
						peContactInfo.setModificationStatus(ModificationStatusDto.CREATE);
						peContactInfo.setPeAccountRef(victimPeAccountRef);
						peContactInfo.setPeReference(preExistingCeReference);
						linkDto.addPeContactInfo(peContactInfo);						
					}
					
					linkDto.setResearched(true);
					
					PersonDto person = (PersonDto)linkDto.getToPoleObject();
					person.setResearched(true); //always set these to true as proof that "researched" alone doesn't count
					person.setIterated(true);   //for a match, without a PeContactInfo on the link.
					
				}
			} else if (linkDto.getToPoleObject() instanceof LocationDto) {
				
				if (linkDto.getLinkReason().equals(SubmitContactServiceConstants.CONTACT_LOCATION_LINK_REASON)) {
					
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
		PutPoleObjectsRequestDto ppoReq = contils.createPutPoleObjectsRequest(
				ContactEventTestUtils.createBusinessServiceInfo(securityContextId), preExistingContactEvent);
		
		PutPoleObjectsResponseDto ppoResponse = poleDirect.putPoleObjects(ppoReq);
		Integer ceObjectRef1 = ppoResponse.getObjectRefs().get(0);
		
		preExistingContactEvent = contils.retrieveContactFromPole(ceObjectRef1, 10, poleDirect, securityContextId, false);
	}
	
	private void extractRecordsLinkedToPreExistingCe() {
		
		preExistingPerson = null;
		preExistingComms = null;
		preExistingLocation = null;
		
		for (LinkDto linkDto : preExistingContactEvent.getLinks()) {
			if (linkDto.getLinkReason().equals(PoleObjectsServiceConstants.PERSON_REPORTING_LINK_REASON)) {
				preExistingPerson = (PersonDto) linkDto.getToPoleObject();
				//now delve deeper into the graph for the nested comms.
				for (LinkDto personLinkDto : preExistingPerson.getLinks()) {
					if (personLinkDto.getLinkReason().equals(PoleObjectsServiceConstants.EMAIL_LINK_REASON)) {
						preExistingComms = (CommsDto) personLinkDto.getToPoleObject();
					}
				}
			} else if (linkDto.getLinkReason().equals(SubmitContactServiceConstants.CONTACT_LOCATION_LINK_REASON)) {
				preExistingLocation = (LocationDto) linkDto.getToPoleObject();
			}
		}
	}
	
	/**
	 * Call the actual DC layer in {@link CreateContactServiceRoute}
	 */
	protected void doDataConsolidationForCreateContactEvent() throws Exception {

		PutPoleObjectsRequestDto ppoReq = contils.createPutPoleObjectsRequest(
				ContactEventTestUtils.createBusinessServiceInfo(securityContextId), targetContactEvent);
				
		PutPoleObjectsResponseDto response = poleBusinessServices.putPoleObjects(ppoReq);
		Integer ceObjectRef = response.getObjectRefs().get(0);
		
		targetContactEvent = contils.retrieveContactFromPole(ceObjectRef, 
				10, poleDirect, securityContextId, false);
	}
	
	/*
	 * Pulls Location, Person (reporting) and their Comms out of the target CE.
	 */
	protected void extractRecordsLinkedToTargetCe() {

		targetPerson = null;
		targetPersonLink = null;
		targetVictimLink = null;
		targetComms = null;
		targetLocation = null;
		
		for (LinkDto linkDto : targetContactEvent.getLinks()) {
			if (linkDto.getLinkReason().equals(PoleObjectsServiceConstants.PERSON_REPORTING_LINK_REASON)) {
				targetPerson = (PersonDto) linkDto.getToPoleObject();
				targetPersonLink = linkDto;
				for (LinkDto personLinkDto : targetPerson.getLinks()) {
					if (personLinkDto.getLinkReason().equals(PoleObjectsServiceConstants.EMAIL_LINK_REASON)) {
						targetComms = (CommsDto) personLinkDto.getToPoleObject();
						targetCommsLink = personLinkDto;
					} else if (personLinkDto.getLinkReason().equals(PoleObjectsServiceConstants.HOME_ADDRESS_LINK_REASON)) {
						targetPersonReportingLocation = (LocationDto) personLinkDto.getToPoleObject();
						targetPersonReportingLocationLink = personLinkDto;
					}
				}
			} else if (linkDto.getLinkReason().equals(PoleObjectsServiceConstants.VICTIM_LINK_REASON)) {
				targetVictim = (PersonDto) linkDto.getToPoleObject();
				targetVictimLink = linkDto;
				for (LinkDto personLinkDto : targetPerson.getLinks()) {
					if (personLinkDto.getLinkReason().equals(PoleObjectsServiceConstants.EMAIL_LINK_REASON)) {
						targetVictimComms = (CommsDto) personLinkDto.getToPoleObject();
						targetVictimCommsLink = personLinkDto;
					} else if (personLinkDto.getLinkReason().equals(PoleObjectsServiceConstants.HOME_ADDRESS_LINK_REASON)) {
						targetVictimLocation = (LocationDto) personLinkDto.getToPoleObject();
						targetVictimLocationLink = personLinkDto;
					}
				}
			} else if (linkDto.getLinkReason().equals(SubmitContactServiceConstants.CONTACT_LOCATION_LINK_REASON)) {
				targetLocation = (LocationDto) linkDto.getToPoleObject();
				targetLocationLink = linkDto;
			}
		}
	}
}
