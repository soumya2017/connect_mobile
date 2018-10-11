package com.northgateis.gem.bussvc.submitcontact.functest;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.northgateis.gem.bussvc.framework.test.BusSvcStoryAcceptanceCriteriaReference;
import com.northgateis.gem.bussvc.pole.utils.PoleDtoUtils;
import com.northgateis.pole.schema.ContactEventDto;
import com.northgateis.pole.schema.PeContactInfoDto;

/**
 * Tests for Data Consolidation concerns when creating a {@link ContactEventDto} where
 * the Reporter and Victim are the same Person. This is a cheeky sub-class of the main
 * test class, purely for the purpose of running the same batch of tests again.
 * 
 * @author dan.allford
 */
public class CreateContactEventReportedByVictimDcFuncTest extends CreateContactEventDcFuncTest {

	public CreateContactEventReportedByVictimDcFuncTest() {
	}
	
	@Override
	protected void setupImpl() throws Exception {
		reporterIsVictim = true;
		super.setupImpl();
	}
	
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
	public void testMultipleLinksToSamePersonWithPeAccountRefSetToResearched() throws Exception {
		doSetup(true, peAccountRef, null,
				true, preExistingCommsValue,
				true, preExistingLocationUprn);
		
		prepareTargetContactEvent(
				peAccountRef, null, targetCeReference, false,
				preExistingCommsValue, true, 
				null, false);
		
		extractRecordsLinkedToTargetCe();
		
		targetVictimLink.addPeContactInfo((PeContactInfoDto) 
				PoleDtoUtils.copyPoleObject(targetPersonLink.getPeContactInfoList().get(0), true));

		doDataConsolidationForCreateContactEvent();
		
		extractRecordsLinkedToTargetCe();
		
		assertTrue("The PersonReporting should be researched",
				targetPerson.getResearched());
		
		checkLinkResearchedComparedToObject(targetPersonLink, targetPerson);
		
		assertTrue("The Victim should be researched",
				targetVictim.getResearched());
		
		checkLinkResearchedComparedToObject(targetVictimLink, targetVictim);
	}
}
