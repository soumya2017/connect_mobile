package com.northgateis.gem.bussvc.submitcontact.functest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

import com.northgateis.gem.bussvc.framework.test.BusSvcStoryAcceptanceCriteriaReference;
import com.northgateis.gem.bussvc.poleobjects.constants.PoleObjectsServiceConstants;
import com.northgateis.gem.bussvc.poleobjects.functest.AbstractPoleObjectsFuncTestBase;
import com.northgateis.gem.bussvc.submitcontact.constants.SubmitContactServiceConstants;
import com.northgateis.gem.bussvc.submitcontact.utils.ContactEventPoleDtoUtils;
import com.northgateis.pole.common.PoleNames;
import com.northgateis.pole.schema.CommsDto;
import com.northgateis.pole.schema.ContactEventDto;
import com.northgateis.pole.schema.EventDto;
import com.northgateis.pole.schema.LinkDto;
import com.northgateis.pole.schema.LinkNodeDto;
import com.northgateis.pole.schema.LocationDto;
import com.northgateis.pole.schema.PeContactInfoDto;
import com.northgateis.pole.schema.PersonDto;
import com.northgateis.pole.schema.PutPoleObjectsRequestDto;
import com.northgateis.pole.schema.PutPoleObjectsResponseDto;

public abstract class AbstractCreateEventFromContactEventFuncTest extends AbstractPoleObjectsFuncTestBase  {

	protected ContactEventDto contactEvent;
	
	/**
	 * The derived event to be created from the ContactEvent.
	 */
	protected EventDto newEvent;
	
	/**
	 * Equivalent link reason on newEvent to the "CONTACT LOCATION" on the {@link ContactEventDto}.
	 */
	protected String newEventLocationLinkReason;

	/**
	 * The link reason that CE>>PERSON REPORTING turns into on the derived event e.g. for
	 * PPE it turns into "WITNESS" and Intel Report it turns into "SUBJECT OF".......
	 */
	protected String newEventPersonReportingLinkReason;

	/**
	 * Sets the expectation about whether the victim on the CE goes over to the derived event or not. For
	 * Incident it should be true. For PPE and IntelReport it should be false.
	 */
	protected boolean expectingVictimMigratedToNewEvent;
	
	/**
	 * When set to true, we're expecting the {@link PeContactInfoDto} of the {@link ContactEventDto}
	 * that identifies the ACCOUNT HOLDER to be migrated onto the event being derived from it.
	 */
	protected boolean expectPeContactInfoToMigrate;

	/*
	 * Linked data on the original CE which is candidate to go over onto the Event being created.
	 */
	private LocationDto ceLocation = null;
	private LinkDto ceLocationLink = null;

	/*
	 * Linked data on the original CE which is candidate to go over onto the Event being created.
	 */
	private PersonDto ceReporter = null;
	private LinkDto ceReporterLink = null;

	/*
	 * Linked data on the original CE which is candidate to go over onto the Event being created.
	 */
	private CommsDto ceReporterEmail = null;
	private LinkDto ceReporterEmailLink = null;

	/*
	 * Linked data on the original CE which is candidate to go over onto the Event being created.
	 */
	private LocationDto ceReporterAddress = null;
	private LinkDto ceReporterAddressLink = null;

	/*
	 * Linked data on the original CE which is candidate to go over onto the Event being created.
	 */
	private PersonDto ceVictim = null;
	private LinkDto ceVictimLink = null;

	/*
	 * Linked data on the original CE which is candidate to go over onto the Event being created.
	 */
	private CommsDto ceVictimEmail = null;
	private LinkDto ceVictimEmailLink = null;

	/*
	 * Linked data on the original CE which is candidate to go over onto the Event being created.
	 */
	private LocationDto ceVictimAddress = null;
	private LinkDto ceVictimAddressLink = null;
	
	/*
	 * Linked data on the original CE which is candidate to go over onto the Event being created.
	 */
	private LocationDto newEventLocation;
	private LinkDto newEventLocationLink;

	/*
	 * Linked data on the original CE which is candidate to go over onto the Event being created.
	 */
	private PersonDto newEventReporter;
	private LinkDto newEventReporterLink;
	
	/*
	 * Linked data on the original CE which is candidate to go over onto the Event being created.
	 */
	private LocationDto newEventReporterAddress;
	private LinkDto newEventReporterAddressLink;
	
	/*
	 * Linked data on the original CE which is candidate to go over onto the Event being created.
	 */
	private CommsDto newEventReporterEmail;
	private LinkDto newEventReporterEmailLink;

	/*
	 * Linked data on the original CE which is candidate to go over onto the Event being created.
	 */
	private PersonDto newEventVictim;
	private LinkDto newEventVictimLink;
	
	/*
	 * Linked data on the original CE which is candidate to go over onto the Event being created.
	 */
	private LocationDto newEventVictimAddress;
	private LinkDto newEventVictimAddressLink;
	
	/*
	 * Linked data on the original CE which is candidate to go over onto the Event being created.
	 */
	private CommsDto newEventVictimEmail;
	private LinkDto newEventVictimEmailLink;
	
	/*
	 * Used to format a date for unique PE reference
	 */
	private SimpleDateFormat peReferenceGenDateFormat = new SimpleDateFormat("ddMMyyyyHHmmss");

	/*
	 * The reference for this ContactEvent from Public Engagement.
	 */
	private String peReference = null;
	
	/**
	 * Made protected so that a tricksy sub-class can test the entire suite with the added 'salt'
	 * of PERSON REPORTING being the same Person as VICTIM.
	 */
	protected boolean reporterIsVictim = false;
	
	@Override
	protected void setupImpl() throws Exception {
		
		reporterIsVictim = false;
		
		ceLocation = null;
		ceLocationLink = null;
		
		ceReporter = null;
		ceReporterLink = null;
		
		ceReporterAddress = null;
		ceReporterAddressLink = null;
		
		ceReporterEmail = null;
		ceReporterEmailLink = null;
		
		ceVictim = null;
		ceVictimLink = null;
		
		ceVictimAddress = null;
		ceVictimAddressLink = null;
		
		ceVictimEmail = null;
		ceVictimEmailLink = null;
		
		newEventLocation = null;
		newEventLocationLink = null;
		
		newEventReporter = null;
		newEventReporterLink = null;
		
		newEventReporterAddress = null;
		newEventReporterAddressLink = null;
		
		newEventReporterEmail = null;
		newEventReporterEmailLink = null;
		
		newEventVictim = null;
		newEventVictimLink = null;
		
		newEventVictimAddress = null;
		newEventVictimAddressLink = null;
		
		newEventVictimEmail = null;
		newEventVictimEmailLink = null;
		
		peReference = null;
		contactEvent = null;
		newEvent = null;
	}
	
	private void prepareForCreateContactEvent(boolean reporterIsVictim) throws Exception {

		peReference = PE_REF_PREFIX + peReferenceGenDateFormat.format(new Date());
		contactEvent = contils.createContactEventDto(null, null, peReference, peAccountRef, reporterIsVictim);
	}
	
	
	private void createContactEvent() throws Exception {
		
		PutPoleObjectsRequestDto req = contils.createPutPoleObjectsRequest(
				utils.createBusinessServiceInfo(securityContextId), contactEvent);
		
		PutPoleObjectsResponseDto resp = poleBusinessServices.putPoleObjects(req);
		contactEvent = contils.retrieveContactFromPole(
				ContactEventPoleDtoUtils.extractContactEventObjectRef(resp), 5, poleBusinessServices, securityContextId, false);
		
		sanityCheckPersonReportingOnCeHasPeContactInfo();
	}

	private void sanityCheckPersonReportingOnCeHasPeContactInfo() {
		for (LinkDto link : contactEvent.getLinks()) {
			if (link.getLinkReason().equals(PoleObjectsServiceConstants.PERSON_REPORTING_LINK_REASON)) {
				if (link.getPeContactInfoList().size() != 1) {
					fail("Failed to find PeContactInfo on Person Reporting link for contact event");
				}
				assertEquals(peAccountRef, link.getPeContactInfoList().get(0).getPeAccountRef());
				break;
			}
		}
	}
	
	@Override
	protected void teardownImpl() throws Exception {
		try { 
			if (contactEvent != null) {
				contils.retrieveContactFromPole(contactEvent.getObjectRef(), 2, poleDirect, securityContextId, false);
			}
		} catch (Exception e) {
			logger.debug("Failed to delete record", e);
		}
		
		deleteData();
	}
	
	protected abstract void deleteData() throws Exception;
	
	/**
	 * When existing, PE Account Info on links between CE and Person should be provided by CCI 
	 * on the link passed-in to Business Services e.g on the Person Reporting link on the 
	 * to-be-created derived event (Inc, PPE, whatever...). Business Services does its clever
	 * 'link fixing' functionality (fixing the duff links from CCI) but during that process it's
	 * important than none of the child objects on the links e.g. PeContactInfo are lost. 

	 * @throws Exception
	 */
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			mingleRef=35885,
			mingleTitle="Bus Svcs - Decision Support - ISP Phase 1 - correctly migrate Static Object "
				+ "and Person record data when creating Incident, Intel Report or PPE from Contact Event",
			acceptanceCriteriaRefs="TODO #35885",
			given="PeContactInfo is supplied on the Person Reporting link (Inc, PPE, whatever...)",
			when="the event is created from the contact event",
			then="PeContactInfo is copied from the person reporting link on the contact event onto the person reporting"
					+ " link on the new event"
		)
	public void testPeContactInfoIsCopiedFromContactEventToNewEvent() throws Exception {
		
		prepareForCreateContactEvent(reporterIsVictim);
		createContactEvent();
		
		PutPoleObjectsRequestDto ppoDto = prepareCreateEventFromContactEventRequest();
		createEventFromContactEvent(ppoDto);

		extractLinkedDataFromEventForReference();
		
		if (expectPeContactInfoToMigrate) {			
			//Then we've found the derived link where the PeContactInfo should have
			//gone onto (should have migrated onto).
			
			assertEquals("Failed to find PeContactInfo on " + newEventPersonReportingLinkReason 
				+ " link (equivalent to PERSON REPORTING) on new event",
				1, newEventReporterLink.getPeContactInfoList().size());
			
			PeContactInfoDto derivedContactInfo = newEventReporterLink.getPeContactInfoList().get(0);
			
			assertEquals("Expect PE Account Ref to be migrated",
					peAccountRef, derivedContactInfo.getPeAccountRef());
			assertEquals("Expect PE Reference to be migrated",
					peReference, derivedContactInfo.getPeReference());

		} else {
			assertEquals("PeContactInfo should not be migrated to derived link " 
				+ newEventPersonReportingLinkReason 
				+ " (equivalent to PERSON REPORTING) on new event",
				0, newEventReporterLink.getPeContactInfoList().size());
		}
	}
	
	/**
	 * Tests that unresearched Static Object data on the CE will be linked to the resulting event correctly
	 * (by deep copy).
	 * 
	 * Tests that unresearched Person data on the CE will be linked to the resulting event correctly
	 * (by deep copy into fresh group). 
	 * 
	 * @throws Exception
	 */
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		mingleRef=35885,
		mingleTitle="Bus Svcs - Decision Support - ISP Phase 1 - correctly migrate Static Object "
			+ "and Person record data when creating Incident, Intel Report or PPE from Contact Event",
		acceptanceCriteriaRefs="CRPer2.1,CRPer2.2,CRLoc2",
		given="The system is creating an Incident or PPE or Intelligence Report from the Contact Event, "
				+ "including Static Object data",
		when="The Static Data on the ContactEvent is Unresearched"
			+ "AND The Person on the ContactEvent is Unresearched",
		then="A new deep copy of the Unresearched Static Objects will be created."
			+ "AND The deep copies will be linked to the new Incident or PPE or Intelligence Report"
			+ "AND The unresearched Person data will be 'deep copied' into "
			+ "a new Person Iteration in its own group, separate to the Person on the CE."
			+ "AND The newly copied Person Iteration will be linked to the new Incident or PPE or Intelligence Report"
	)
	public void testUnresearchedStaticDataAndPersonDataIsDeepCopiedIntoNewEvent() throws Exception {
		
		prepareForCreateContactEvent(reporterIsVictim);
		extractLinkedDataFromContactEventForReference();//with placeholder object refs
		
		ceLocation.setResearched(false);
		ceLocationLink.setResearched(false);
		ceVictim.setResearched(false);
		ceVictimLink.setResearched(false);
		ceVictim.setIterated(false);
		ceVictimEmail.setResearched(false);
		ceVictimEmailLink.setResearched(false);
		ceVictimAddress.setResearched(false);
		ceVictimAddressLink.setResearched(false);
		ceReporter.setResearched(false);
		ceReporterLink.setResearched(false);
		ceReporter.setIterated(false);
		ceReporterEmail.setResearched(false);
		ceReporterEmailLink.setResearched(false);
		ceReporterAddress.setResearched(false);
		ceReporterAddressLink.setResearched(false);
		
		makeContactEventEmailsUnique();
		createContactEvent();
		extractLinkedDataFromContactEventForReference();//now with proper object refs

		PutPoleObjectsRequestDto ppoDto = prepareCreateEventFromContactEventRequest();
		createEventFromContactEvent(ppoDto);

		extractLinkedDataFromEventForReference();

		assertNotNull(newEventLocation);
		checkLinkResearchedComparedToObject(newEventLocationLink, newEventLocation);
		
		assertNotNull(newEventReporter);
		checkLinkResearchedComparedToObject(newEventReporterLink, newEventReporter);
		assertNotNull(newEventReporterAddress);
		checkLinkResearchedComparedToObject(newEventReporterAddressLink, newEventReporterAddress);
		assertNotNull(newEventReporterEmail);
		checkLinkResearchedComparedToObject(newEventReporterEmailLink, newEventReporterEmail);
		
		if (expectingVictimMigratedToNewEvent) {
			assertNotNull(newEventVictim);
			checkLinkResearchedComparedToObject(newEventVictimLink, newEventVictim);
			assertNotNull(newEventVictimAddress);
			checkLinkResearchedComparedToObject(newEventVictimAddressLink, newEventVictimAddress);
			assertNotNull(newEventVictimEmail);
			checkLinkResearchedComparedToObject(newEventVictimEmailLink, newEventVictimEmail);
		}
		
		assertNotEquals(ceLocation.getObjectRef(), newEventLocation.getObjectRef());

		assertNotEquals("Person always gets a new Iteration", 
				ceReporter.getObjectRef(), newEventReporter.getObjectRef());
		assertNotEquals("Unresearched Person Iteration should go in new group", 
				ceReporter.getIterationGroupRef(), newEventReporter.getIterationGroupRef());
		assertNotEquals("Unresearched Static Objects must be copied", 
				ceReporterAddress.getObjectRef(), newEventReporterAddress.getObjectRef());
		assertNotEquals("Unresearched Static Objects must be copied", 
				ceReporterEmail.getObjectRef(), newEventReporterEmail.getObjectRef());

		if (expectingVictimMigratedToNewEvent) {
			assertNotEquals("Person always gets a new Iteration", 
					ceVictim.getObjectRef(), newEventVictim.getObjectRef());
			assertNotEquals("Unresearched Person Iteration should go in new group", 
					ceVictim.getIterationGroupRef(), newEventVictim.getIterationGroupRef());
			assertNotEquals("Unresearched Static Objects must be copied", 
					ceVictimAddress.getObjectRef(), newEventVictimAddress.getObjectRef());
			assertNotEquals("Unresearched Static Objects must be copied", 
					ceVictimEmail.getObjectRef(), newEventVictimEmail.getObjectRef());
		}
	}
	
	
	/**
	 * Tests that unresearched Static Object data on the CE will be linked to the resulting event correctly (by deep copy).
	 * 
	 * Tests that unresearched Person data on the CE will be linked to the resulting event correctly (by
	 * deep copy into fresh group). 
	 * 
	 * @throws Exception
	 */
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		mingleRef=35885,
		mingleTitle="Bus Svcs - Decision Support - ISP Phase 1 - correctly migrate Static Object "
				+ "and Person record data when creating Incident, Intel Report or PPE from Contact Event",
		acceptanceCriteriaRefs="CRPer1.1,CRPer1.2,CRLoc1",
		given="The system is creating an Incident or PPE or Intelligence Report from the Contact Event, "
				+ "including Static Object data",
		when="The Static Data on the ContactEvent has been marked as Researched"
				+ "AND The Person on the ContactEvent has been marked as Researched",
		then="The same Static Data record will be linked to the new Incident or PPE or Intelligence Report"
				+ "AND A new Iteration of the CE's Person data will be created, in the same group"
				+ "AND The new Iteration will be linked to the new Incident or PPE or Intelligence Report"
	)
	public void testResearchedStaticDataIsLinkedToAndResearchedPersonDataIsDeepCopiedToNewIterationOnNewEvent() throws Exception {
		
		prepareForCreateContactEvent(reporterIsVictim);
		extractLinkedDataFromContactEventForReference();//with placeholder object refs
		
		//Note: setting various researched=true values is a cheat! Usually researching would 
		//happen as a distinct transaction at some other time, not during creation of the CE.
		//Note: we *must* set the equivalent link to researched=true whenever we set the
		//linked-to PO researched=true.
		
		ceLocation.setResearched(true);
		ceLocationLink.setResearched(true);
		
		ceVictim.setResearched(true);
		ceVictim.setIterated(true);
		ceVictimLink.setResearched(true);
		
		ceVictimEmail.setResearched(true);
		ceVictimEmailLink.setResearched(true);
		
		ceVictimAddress.setResearched(true);
		ceVictimAddressLink.setResearched(true);
		
		ceReporter.setResearched(true);
		ceReporter.setIterated(true);
		ceReporterLink.setResearched(true);
		
		ceReporterEmail.setResearched(true);
		ceReporterEmailLink.setResearched(true);
		
		ceReporterAddress.setResearched(true);
		ceReporterAddressLink.setResearched(true);
		
		createContactEvent();
		extractLinkedDataFromContactEventForReference();//now with proper object refs

		PutPoleObjectsRequestDto ppoDto = prepareCreateEventFromContactEventRequest();
		createEventFromContactEvent(ppoDto);

		extractLinkedDataFromEventForReference();

		assertNotNull(newEventLocation);
		checkLinkResearchedComparedToObject(newEventLocationLink, newEventLocation);
		
		assertNotNull(newEventReporter);
		checkLinkResearchedComparedToObject(newEventReporterLink, newEventReporter);
		assertNotNull(newEventReporterAddress);
		checkLinkResearchedComparedToObject(newEventReporterAddressLink, newEventReporterAddress);
		assertNotNull(newEventReporterEmail);
		checkLinkResearchedComparedToObject(newEventReporterEmailLink, newEventReporterEmail);
		
		if (expectingVictimMigratedToNewEvent) {
			assertNotNull(newEventVictim);
			checkLinkResearchedComparedToObject(newEventVictimLink, newEventVictim);
			assertNotNull(newEventVictimAddress);
			checkLinkResearchedComparedToObject(newEventVictimAddressLink, newEventVictimAddress);
			assertNotNull(newEventVictimEmail);
			checkLinkResearchedComparedToObject(newEventVictimEmailLink, newEventVictimEmail);
		}
		
		assertEquals(ceLocation.getObjectRef(), newEventLocation.getObjectRef());

		assertNotEquals("Person always gets a new Iteration", 
				ceReporter.getObjectRef(), newEventReporter.getObjectRef());
		assertEquals("Researched Person Iteration should go in same group", 
				ceReporter.getIterationGroupRef(), newEventReporter.getIterationGroupRef());
		assertEquals("Researched Static Objects must be linked-to without copy", 
				ceReporterAddress.getObjectRef(), newEventReporterAddress.getObjectRef());
		assertEquals("Researched Static Objects must be linked-to without copy", 
				ceReporterEmail.getObjectRef(), newEventReporterEmail.getObjectRef());

		if (expectingVictimMigratedToNewEvent) {
			assertNotEquals("Person always gets a new Iteration", 
					ceVictim.getObjectRef(), newEventVictim.getObjectRef());
			assertEquals("Researched Person Iteration should go in same group", 
					ceVictim.getIterationGroupRef(), newEventVictim.getIterationGroupRef());
			assertEquals("Researched Static Objects must be linked-to without copy", 
					ceVictimAddress.getObjectRef(), newEventVictimAddress.getObjectRef());
			assertEquals("Researched Static Objects must be linked-to without copy", 
					ceVictimEmail.getObjectRef(), newEventVictimEmail.getObjectRef());
		}
	}


	/**
	 * Combincation / wildcard test similar to the two connected to #35885. Ensures that
	 * there is discerning choices being made over which Researched/Unresearched records to
	 * deep-copy vs link-to, on the derived event.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMixtureOfResearchedAndUnresearchedDataIsCopiedAccordingToRules() throws Exception {
		
		if (reporterIsVictim) {
			logger.info("Cannot use a mixture of researched and unresearched data when reporter and victim are the same Person");
			return;
		}
		
		prepareForCreateContactEvent(reporterIsVictim);
		extractLinkedDataFromContactEventForReference();//with placeholder object refs
		
		//Note: setting various researched=true values is a cheat! Usually researching would 
		//happen as a distinct transaction at some other time, not during creation of the CE.
		//Note: we *must* set the equivalent link to researched=true whenever we set the
		//linked-to PO researched=true.
		
		ceLocation.setResearched(true);
		ceLocationLink.setResearched(true);

		ceReporter.setResearched(false);
		ceReporterLink.setResearched(false);
		ceReporter.setIterated(false);
		
		ceReporterEmail.setResearched(true);
		ceReporterEmailLink.setResearched(true);
		
		ceReporterAddress.setResearched(true);
		ceReporterAddressLink.setResearched(true);
		
		ceVictim.setResearched(true);
		ceVictim.setIterated(true);
		ceVictimLink.setResearched(true);
		
		ceVictimEmail.setResearched(false);
		ceVictimEmailLink.setResearched(false);
		
		ceVictimAddress.setResearched(false);
		ceVictimAddressLink.setResearched(false);
		
		makeContactEventEmailsUnique();
		createContactEvent();
		extractLinkedDataFromContactEventForReference();//now with proper object refs

		PutPoleObjectsRequestDto ppoDto = prepareCreateEventFromContactEventRequest();
		createEventFromContactEvent(ppoDto);

		extractLinkedDataFromEventForReference();

		assertNotNull(newEventLocation);
		checkLinkResearchedComparedToObject(newEventLocationLink, newEventLocation);
		
		assertNotNull(newEventReporter);
		assertNotNull(newEventReporterLink.getResearched());
		checkLinkResearchedComparedToObject(newEventReporterLink, newEventReporter);
		assertNotNull(newEventReporterAddress);
		checkLinkResearchedComparedToObject(newEventReporterAddressLink, newEventReporterAddress);
		assertNotNull(newEventReporterEmail);
		checkLinkResearchedComparedToObject(newEventReporterEmailLink, newEventReporterEmail);
		
		if (expectingVictimMigratedToNewEvent) {
			assertNotNull(newEventVictim);
			checkLinkResearchedComparedToObject(newEventVictimLink, newEventVictim);
			assertNotNull(newEventVictimAddress);
			assertNotNull(newEventVictimAddressLink.getResearched());
			checkLinkResearchedComparedToObject(newEventVictimAddressLink, newEventVictimAddress);
			assertNotNull(newEventVictimEmail);
			assertNotNull(newEventVictimEmailLink.getResearched());
			checkLinkResearchedComparedToObject(newEventVictimEmailLink, newEventVictimEmail);
		}
		
		assertEquals(ceLocation.getObjectRef(), newEventLocation.getObjectRef());

		assertNotEquals("Person always gets a new Iteration", 
				ceReporter.getObjectRef(), newEventReporter.getObjectRef());
		
		if (expectingVictimMigratedToNewEvent) {
			assertNotEquals("Person always gets a new Iteration", 
				ceVictim.getObjectRef(), newEventVictim.getObjectRef());
		}

		//Assertions on the REPORTER
		//...then these assertions effectively match those on the VICTIM (as it's the same object).
		//Note therefore that this code is inefficient and a bit obfuscated. The option of testing
		//behaviour when VICTIM == PERSON REPORTING came along *after* the tests were initially written
		assertNotEquals("Unresearched Person Iteration should go in new group", 
				ceReporter.getIterationGroupRef(), newEventReporter.getIterationGroupRef());
		assertEquals("Researched Static Objects must be linked-to without copy", 
				ceReporterAddress.getObjectRef(), newEventReporterAddress.getObjectRef());
		assertEquals("Researched Static Objects must be linked-to without copy", 
				ceReporterEmail.getObjectRef(), newEventReporterEmail.getObjectRef());
		
		if (expectingVictimMigratedToNewEvent) {
			//Assertions on the VICTIM
			assertEquals("Researched Person Iteration should go in same group", 
				ceVictim.getIterationGroupRef(), newEventVictim.getIterationGroupRef());
			assertNotEquals("Unresearched Static Objects must be copied", 
					ceVictimAddress.getObjectRef(), newEventVictimAddress.getObjectRef());
			assertNotEquals("Unresearched Static Objects must be copied", 
					ceVictimEmail.getObjectRef(), newEventVictimEmail.getObjectRef());
		}
	}
	
	@Test
	public void testMultipleLinksToSameResearchedStaticObjectLinksMarkedToResearched() throws Exception {
		
		prepareForCreateContactEvent(reporterIsVictim);
		extractLinkedDataFromContactEventForReference();//with placeholder object refs
		
		ceLocation.setResearched(true);
		ceLocationLink.setResearched(true);
		
		makeContactEventEmailsUnique();
		createContactEvent();
		extractLinkedDataFromContactEventForReference();//now with proper object refs

		PutPoleObjectsRequestDto ppoDto = prepareCreateEventFromContactEventRequest();
		
		for (LinkNodeDto link : ppoDto.getLinkNodes()) {
			if (link.getLinkReason().equals(PoleObjectsServiceConstants.HOME_ADDRESS_LINK_REASON)
					&& PoleNames.LOCATION.equals(link.getToPoleObjectType())) {
				link.setToPoleObjectRef(ceLocation.getObjectRef());
				break;
			}
		}
		
		createEventFromContactEvent(ppoDto);

		extractLinkedDataFromEventForReference();
		
		assertNotNull(newEventLocation);
		checkLinkResearchedComparedToObject(newEventLocationLink, newEventLocation);
		
		assertNotNull(newEventReporter);
		checkLinkResearchedComparedToObject(newEventReporterLink, newEventReporter);
		assertNotNull(newEventReporterAddress);
		checkLinkResearchedComparedToObject(newEventReporterAddressLink, newEventReporterAddress);
		assertNotNull(newEventReporterEmail);
		checkLinkResearchedComparedToObject(newEventReporterEmailLink, newEventReporterEmail);
		
		if (expectingVictimMigratedToNewEvent) {
			assertNotNull(newEventVictim);
			checkLinkResearchedComparedToObject(newEventVictimLink, newEventVictim);
			assertNotNull(newEventVictimAddress);
			checkLinkResearchedComparedToObject(newEventVictimAddressLink, newEventVictimAddress);
			assertNotNull(newEventVictimEmail);
			checkLinkResearchedComparedToObject(newEventVictimEmailLink, newEventVictimEmail);
		}
		
		assertEquals("Researched Static Objects must be linked-to without copy", 
				ceLocation.getObjectRef(), newEventLocation.getObjectRef());

		assertNotEquals("Person always gets a new Iteration", 
				ceReporter.getObjectRef(), newEventReporter.getObjectRef());
		
		if (expectingVictimMigratedToNewEvent) {
			assertNotEquals("Person always gets a new Iteration", 
				ceVictim.getObjectRef(), newEventVictim.getObjectRef());
		}

		//Assertions on the REPORTER
		//...then these assertions effectively match those on the VICTIM (as it's the same object).
		//Note therefore that this code is inefficient and a bit obfuscated. The option of testing
		//behaviour when VICTIM == PERSON REPORTING came along *after* the tests were initially written
		assertNotEquals("Unresearched Person Iteration should go in new group", 
				ceReporter.getIterationGroupRef(), newEventReporter.getIterationGroupRef());
		assertEquals("Researched Static Objects must be linked-to without copy", 
				ceLocation.getObjectRef(), newEventReporterAddress.getObjectRef());
		assertNotEquals("Unresearched Static Objects must be copied", 
				ceReporterEmail.getObjectRef(), newEventReporterEmail.getObjectRef());
		
		if (expectingVictimMigratedToNewEvent) {
			//Assertions on the VICTIM
			assertNotEquals("Unresearched Person Iteration should go in new group", 
				ceVictim.getIterationGroupRef(), newEventVictim.getIterationGroupRef());
			if (!reporterIsVictim) {
				assertNotEquals("Unresearched Static Objects must be copied", 
						ceVictimAddress.getObjectRef(), newEventVictimAddress.getObjectRef());
			} else {
				assertEquals("Researched Static Objects must be linked-to without copy", 
						ceLocation.getObjectRef(), newEventVictimAddress.getObjectRef());
			}
			assertNotEquals("Unresearched Static Objects must be copied", 
					ceVictimEmail.getObjectRef(), newEventVictimEmail.getObjectRef());
		}
	}
	
	@Test
	public void testMultipleLinksToSameResearchedPersonLinksMarkedToResearched() throws Exception {
		
		if (expectingVictimMigratedToNewEvent && reporterIsVictim) {
		
			prepareForCreateContactEvent(reporterIsVictim);
			extractLinkedDataFromContactEventForReference();//with placeholder object refs
			
			ceReporter.setResearched(true);
			ceReporter.setIterated(true);
			ceReporterLink.setResearched(true);
			
			// because ceVictim = ceReporter
			ceVictimLink.setResearched(true);
			
			makeContactEventEmailsUnique();
			createContactEvent();
			extractLinkedDataFromContactEventForReference();//now with proper object refs
	
			PutPoleObjectsRequestDto ppoDto = prepareCreateEventFromContactEventRequest();
			
			ceReporter.setIterated(false);
			
			createEventFromContactEvent(ppoDto);
	
			extractLinkedDataFromEventForReference();
			
			assertNotNull(newEventLocation);
			checkLinkResearchedComparedToObject(newEventLocationLink, newEventLocation);
			
			assertNotNull(newEventReporter);
			checkLinkResearchedComparedToObject(newEventReporterLink, newEventReporter);
			assertNotNull(newEventReporterAddress);
			checkLinkResearchedComparedToObject(newEventReporterAddressLink, newEventReporterAddress);
			assertNotNull(newEventReporterEmail);
			checkLinkResearchedComparedToObject(newEventReporterEmailLink, newEventReporterEmail);
	
			assertNotEquals("Person always gets a new Iteration", 
					ceReporter.getObjectRef(), newEventReporter.getObjectRef());
			
			assertEquals("Reporter and Victim are equal", 
				newEventReporter.getObjectRef(), newEventVictim.getObjectRef());
			
			//Assertions on the REPORTER
			assertEquals("Researched Person Iteration should go in same group", 
					ceReporter.getIterationGroupRef(), newEventReporter.getIterationGroupRef());
			assertNotEquals("Unresearched Static Objects must be copied", 
					ceReporterAddress.getObjectRef(), newEventVictimAddress.getObjectRef());
			assertNotEquals("Unresearched Static Objects must be copied", 
					ceReporterEmail.getObjectRef(), newEventReporterEmail.getObjectRef());
		}
	}
	
	/**
	 * This prevents any matching against email/mainNumber (Story #35810) which would
	 * throw these tests off - influencing with occasional matches in POLE. Therefore
	 * the email addresses and phone numbers need to be unique.
	 */
	private void makeContactEventEmailsUnique() {

		ceReporterEmail.setMainNumber("" + System.currentTimeMillis() + ceReporterEmail.getMainNumber());

		ceVictimEmail.setMainNumber("" + System.currentTimeMillis()+3 + ceVictimEmail.getMainNumber());
	}

	private void extractLinkedDataFromEventForReference() {

		for (LinkDto link : newEvent.getLinks()) {
			
			if (link.getLinkReason().equals(newEventLocationLinkReason) && link.getToPoleObject() instanceof LocationDto) {
				newEventLocation = (LocationDto) link.getToPoleObject();
				newEventLocationLink = link;
				
			} else if (link.getLinkReason().equals(newEventPersonReportingLinkReason) && link.getToPoleObject() instanceof PersonDto) {
				newEventReporter = (PersonDto) link.getToPoleObject();
				newEventReporterLink = link;
				
				for (LinkDto linkFromPersonReporting : newEventReporter.getLinks()) {
					
					if (linkFromPersonReporting.getLinkReason().equals(PoleObjectsServiceConstants.HOME_ADDRESS_LINK_REASON)) {
						newEventReporterAddress = (LocationDto) linkFromPersonReporting.getToPoleObject();
						newEventReporterAddressLink = linkFromPersonReporting;
					} else if (linkFromPersonReporting.getLinkReason().equals(PoleObjectsServiceConstants.EMAIL_LINK_REASON)) {
						newEventReporterEmail = (CommsDto) linkFromPersonReporting.getToPoleObject();
						newEventReporterEmailLink = linkFromPersonReporting;
					}
				}
				
			} else if (link.getLinkReason().equals(PoleObjectsServiceConstants.VICTIM_LINK_REASON)) {
				newEventVictim = (PersonDto) link.getToPoleObject();
				newEventVictimLink = link;
				
				for (LinkDto linkFromVictim : newEventVictim.getLinks()) {
					
					if (linkFromVictim.getLinkReason().equals(PoleObjectsServiceConstants.HOME_ADDRESS_LINK_REASON)) {
						newEventVictimAddress = (LocationDto) linkFromVictim.getToPoleObject();
						newEventVictimAddressLink = linkFromVictim;
					} else if (linkFromVictim.getLinkReason().equals(PoleObjectsServiceConstants.EMAIL_LINK_REASON)) {
						newEventVictimEmail = (CommsDto) linkFromVictim.getToPoleObject();	
						newEventVictimEmailLink = linkFromVictim;
					}
				}
			}
		}
	}

	private void extractLinkedDataFromContactEventForReference() {

		for (LinkDto link : contactEvent.getLinks()) {
			
			if (link.getLinkReason().equals(SubmitContactServiceConstants.CONTACT_LOCATION_LINK_REASON)) {
				ceLocation = (LocationDto) link.getToPoleObject();
				ceLocationLink = link;
				
			} else if (link.getLinkReason().equals(PoleObjectsServiceConstants.PERSON_REPORTING_LINK_REASON)) {
				ceReporter = (PersonDto) link.getToPoleObject();
				ceReporterLink = link;

				if (ceReporter == null) {
					//Cope with special case where the PO at the end of this link is null because
					//victim == person reporting. Therefore POLE would have already returned the nested
					//PersonDto and won't duplicate. Detect here and swap-in the victim already found.
					if (ceVictim != null && ceVictim.getObjectRef().equals(link.getToPoleObjectRef())) {
						ceReporter = ceVictim;
					}
				}
				
				for (LinkDto linkFromPersonReporting : ceReporter.getLinks()) {
					if (linkFromPersonReporting.getLinkReason().equals(PoleObjectsServiceConstants.HOME_ADDRESS_LINK_REASON)) {
						ceReporterAddressLink = linkFromPersonReporting;
						ceReporterAddress = (LocationDto) linkFromPersonReporting.getToPoleObject();
					} else if (linkFromPersonReporting.getLinkReason().equals(PoleObjectsServiceConstants.EMAIL_LINK_REASON)) {
						ceReporterEmailLink = linkFromPersonReporting;
						ceReporterEmail = (CommsDto) linkFromPersonReporting.getToPoleObject();
					}
				}
			} else if (link.getLinkReason().equals(PoleObjectsServiceConstants.VICTIM_LINK_REASON)) {
				ceVictim = (PersonDto) link.getToPoleObject();
				ceVictimLink = link;
				
				if (ceVictim == null) {
					//Cope with special case where the PO at the end of this link is null because
					//victim == person reporting. Therefore POLE would have already returned the nested
					//PersonDto and won't duplicate. Detect here and swap-in the person reporting already found.
					if (ceReporter != null && ceReporter.getObjectRef().equals(link.getToPoleObjectRef())) {
						ceVictim = ceReporter;
					}
				}
				
				for (LinkDto linkFromVictim : ceVictim.getLinks()) {
					if (linkFromVictim.getLinkReason().equals(PoleObjectsServiceConstants.HOME_ADDRESS_LINK_REASON)) {
						ceVictimAddressLink = linkFromVictim;
						ceVictimAddress = (LocationDto) linkFromVictim.getToPoleObject();
					} else if (linkFromVictim.getLinkReason().equals(PoleObjectsServiceConstants.EMAIL_LINK_REASON)) {
						ceVictimEmailLink = linkFromVictim;
						ceVictimEmail = (CommsDto) linkFromVictim.getToPoleObject();
					}					
				}
			}
		}	
	}

	/**
	 * Prepares a new PutPoleObjectsRequestDto for creating a new event(e.g. Incident, IntelReport, PPE)
	 * from a Contact Event.  Places the new event into the newEvent variable.
	 * 
	 * @return
	 */
	protected abstract PutPoleObjectsRequestDto prepareCreateEventFromContactEventRequest();

	/**
	 * Actually creates the new event being created from the Contact event in Pole.  Also retrieves
	 * the newly created event back from Pole, and places it in the newEvent variable.  This is so we have
	 * the new event and links with objects in a predictable format, and the correct object references.
	 * 
	 * @param ppoRequest
	 * @throws Exception
	 */
	protected abstract void createEventFromContactEvent(PutPoleObjectsRequestDto ppoRequest) throws Exception;
}
