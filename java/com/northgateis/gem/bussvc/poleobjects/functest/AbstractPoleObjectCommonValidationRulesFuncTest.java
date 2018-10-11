package com.northgateis.gem.bussvc.poleobjects.functest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.northgateis.gem.bussvc.AbstractBusinessServicesFunctionalTestBase;
import com.northgateis.gem.bussvc.framework.FrameworkServiceConstants;
import com.northgateis.gem.bussvc.framework.exception.ValidationException;
import com.northgateis.gem.bussvc.framework.schema.BusinessServiceValidationError;
import com.northgateis.gem.bussvc.framework.schema.BusinessServiceValidationErrors;
import com.northgateis.gem.bussvc.framework.test.BusSvcStoryAcceptanceCriteriaReference;
import com.northgateis.gem.bussvc.pole.utils.PoleDtoUtils;
import com.northgateis.gem.bussvc.test.util.BusinessServicesTestUtils;
import com.northgateis.gem.bussvc.test.util.CommonPoleObjectTestUtils;
import com.northgateis.pole.common.EntityKey;
import com.northgateis.pole.common.InvalidDataException;
import com.northgateis.pole.common.PoleDate;
import com.northgateis.pole.schema.CommsDto;
import com.northgateis.pole.schema.ExternalReferenceDto;
import com.northgateis.pole.schema.IncidentDto;
import com.northgateis.pole.schema.LinkDto;
import com.northgateis.pole.schema.LocationAlternativeNameDto;
import com.northgateis.pole.schema.LocationDto;
import com.northgateis.pole.schema.MarkerDto;
import com.northgateis.pole.schema.ModificationStatusDto;
import com.northgateis.pole.schema.OperationDto;
import com.northgateis.pole.schema.OrganisationDto;
import com.northgateis.pole.schema.PersonAliasDto;
import com.northgateis.pole.schema.PersonDressItemDto;
import com.northgateis.pole.schema.PersonDrivingLicenceDto;
import com.northgateis.pole.schema.PersonDto;
import com.northgateis.pole.schema.PersonEmploymentDto;
import com.northgateis.pole.schema.PersonFeatureDto;
import com.northgateis.pole.schema.PersonFeatureKeywordDto;
import com.northgateis.pole.schema.PersonHairDetailDto;
import com.northgateis.pole.schema.PersonNomsIdDto;
import com.northgateis.pole.schema.PoleEntityDto;
import com.northgateis.pole.schema.PoleObjectDto;
import com.northgateis.pole.schema.PutPoleObjectsRequestDto;
import com.northgateis.pole.schema.RiskAssessmentDto;
import com.northgateis.pole.schema.VehicleDto;
import com.northgateis.pole.schema.VehicleFeatureDto;
import com.northgateis.pole.schema.VehicleInspectionDto;
import com.northgateis.pole.schema.VehiclePncFieldsDto;


/**
 * Class contains all the common objects validation tests that are not event specific and is applied to all events
 * equally. It validates static objects linked to event and their links.
 *
 */
public abstract class AbstractPoleObjectCommonValidationRulesFuncTest
		extends AbstractBusinessServicesFunctionalTestBase {

	public final static String INVALID_LINK_REASON = "Invalid_LinkReason";		
	
	protected String clientName = FrameworkServiceConstants.BUSINESS_SERVICES_CLIENT_NAME;
	
	/**
	 * Implementation classes should return a PoleObjectDto
	 * @return
	 */
	protected abstract PoleObjectDto createPoleObjectDto();		

	protected abstract void doCreateEventObject(Boolean completedFlag, PoleObjectDto poleObjectDto) throws Exception;
	
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
	
	/** ============================ #37152 - Common child objects Gherkins ============================ **/
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37152, 
			mingleTitle = "Business Service- Validation Framework -Common child objects rules", 
			acceptanceCriteriaRefs = "CR15.1,CR15.2,CR15.3,CR15.4", 
			given = "Person is linked with valid Marker details. Happy path scenario",
			when = "Event object Submited", 
			then = "No Validation error should be returned")
	public void testEventObjectWhenPersonIsLinkedAndHavingValidMarker()
			throws Exception {
		PoleObjectDto poleObject = createPoleObjectDto();
		for (LinkDto linkDto : poleObject.getLinks()) {
			PoleObjectDto toPoleObject = linkDto.getToPoleObject();
			if (toPoleObject instanceof PersonDto) {
				PersonDto person = (PersonDto) toPoleObject;
				assertTrue("No Marker Defined.", person.getMarkers().size() > 0);
				break;
			} 
		}
		doCreateEventObject(true, poleObject);
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37152, 
			mingleTitle = "Business Service- Validation Framework -Common child objects rules", 
			acceptanceCriteriaRefs = "CR15.1,CR15.2,CR15.3,CR15.4", 
			given = "Person is linked with invalid/null Marker details",
			when = "Event object Submited", 
			then = "Validation errors should be returned")
	public void testEventObjectWhenPersonIsLinkedAndHavingInvalidMarker()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof PersonDto) {
					PersonDto person = (PersonDto) toPoleObject;
					assertTrue("No Marker Defined.", person.getMarkers().size() > 0);
					if (person.getMarkers().size() > 0) {
						MarkerDto marker = person.getMarkers().get(0);
						marker.setMarkerSource(null);
						marker.setFromDate(null);
						marker.setMarkerType(null);
						marker.setMarkerValue(null);
					}
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
			//TODO CODE REVIEW CCI-2061 - I realise its a long standing problem, but why do we look for error> at all
			//Im not going to mark all of them, but thinking about it, not sure it makes any sense to look for anymore
			//than the text we are interested in.  Just a thought.
					ide.getMessage().contains("error>markerSource must have a value</"));
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>markerType must have a value</"));
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>markerValue must have a value</"));
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>fromDate must have a value</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37152, 
			mingleTitle = "Business Service- Validation Framework -Common child objects rules", 
			acceptanceCriteriaRefs = "CR16.1", 
			given = "Person is linked with valid ExternalReference details. Happy path scenario.",
			when = "Event object Submited", 
			then = "No Validation error should be returned")
	public void testEventObjectWhenPersonIsLinkedAndHavingValidExternalReference()
			throws Exception {
		PoleObjectDto poleObject = createPoleObjectDto();
		for (LinkDto linkDto : poleObject.getLinks()) {
			PoleObjectDto toPoleObject = linkDto.getToPoleObject();
			if (toPoleObject instanceof PersonDto) {
				PersonDto person = (PersonDto) toPoleObject;
				assertTrue("No ExternalReferences Defined.", person.getExternalReferences().size() > 0);
				break;
			} 
		}
		doCreateEventObject(true, poleObject);
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37152, 
			mingleTitle = "Business Service- Validation Framework -Common child objects rules", 
			acceptanceCriteriaRefs = "CR16.1", 
			given = "Person is linked with invalid ExternalReference details",
			when = "Event object Submited", 
			then = "Validation errors should be returned")
	public void testEventObjectWhenPersonIsLinkedAndHavingInvalidExternalReference()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof PersonDto) {
					PersonDto person = (PersonDto) toPoleObject;
					assertTrue("No ExternalReference Defined.", person.getExternalReferences().size() > 0);
					if (person.getExternalReferences().size() > 0) {
						ExternalReferenceDto externalReference = person.getExternalReferences().get(0);
						externalReference.setReferenceValue(null);						
					}
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>referenceValue must have a value</"));			
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37152, 
			mingleTitle = "Business Service- Validation Framework -Common child objects rules", 
			acceptanceCriteriaRefs = "CR17.1", 
			given = "Person is linked with valid Tag details. Happy path scenario.",
			when = "Event object Submited", 
			then = "No Validation error should be returned")
	public void testEventObjectWhenPersonIsLinkedAndHavingValidTag()
			throws Exception {
		PoleObjectDto poleObject = createPoleObjectDto();
		for (LinkDto linkDto : poleObject.getLinks()) {
			PoleObjectDto toPoleObject = linkDto.getToPoleObject();
			if (toPoleObject instanceof PersonDto) {
				PersonDto person = (PersonDto) toPoleObject;
				assertTrue("No Tag Defined.", person.getTags().size() > 0);
				break;
			} 
		}
		doCreateEventObject(true, poleObject);
	}
	
	/**
	 * Below next two functional tests are general for all allowable static objects so provided generic mingle
	 * reference 37152 which is defined for common child objects. It covers link scenarios for stories #37109 to #37113
	 **/
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37152, 
			mingleTitle = "Business Service- Validation Framework - Links to static objects", 
			acceptanceCriteriaRefs = "All allowable static objects", 
			given = "Static object is linked with valid link reasons",
			when = "Event object Submited", 
			then = "No Validation error should be returned")
	public void testEventObjectWhenStaticObjectIsLinkedWithValidLinkReason()
			throws Exception {
		PoleObjectDto poleObject = createPoleObjectDto();
		//The link reasons should already be valid from the code that created the pole object, no need to change
		//the link reasons here
		doCreateEventObject(true, poleObject);
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37152, 
			mingleTitle = "Business Service- Validation Framework - Links to static objects", 
			acceptanceCriteriaRefs = "All allowable static objects", 
			given = "Static object is linked with invalid link reasons", 
			when = "Event object Submited", 
			then = "Validation error should be returned")
	public void testEventObjectWhenStaticObjectIsLinkedWithInvalidLinkReason()
			throws Exception {
		PoleObjectDto poleObject = null;
		try {
			poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof VehicleDto) {
					linkDto.setLinkReason(INVALID_LINK_REASON);
				} else if (toPoleObject instanceof OrganisationDto) {
					linkDto.setLinkReason(INVALID_LINK_REASON);
				} else if (toPoleObject instanceof OperationDto) {
					linkDto.setLinkReason(INVALID_LINK_REASON);
				} else if (toPoleObject instanceof CommsDto) {
					linkDto.setLinkReason(INVALID_LINK_REASON);
				} else if (!(linkDto.getSourcePoleObject() instanceof IncidentDto) &&  toPoleObject instanceof LocationDto) {
					linkDto.setLinkReason(INVALID_LINK_REASON);
				} else if (toPoleObject instanceof RiskAssessmentDto) {
					linkDto.setLinkReason(INVALID_LINK_REASON);
				} else if (toPoleObject instanceof PersonDto) {
					linkDto.setLinkReason(INVALID_LINK_REASON);
				}
			}
			doCreateEventObject(false, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			BusinessServiceValidationErrors businessServiceValidationErrors 
				= unmarshalBusinessServiceValidationErrorsFromXml(ide.getMessage());
			assertNotNull(businessServiceValidationErrors);
			boolean invalidVehicleLinkFound = false;
			boolean invalidOrganisationLinkFound = false;
			boolean invalidOperationLinkFound = false;
			boolean invalidCommsLinkFound = false;
			boolean invalidLocationLinkFound = false;
			boolean invalidRiskAssessmentLinkFound = false;
			boolean invalidPersonLinkFound = false;
			List<BusinessServiceValidationError> bsValidationErrors = businessServiceValidationErrors.getBusinessServiceValidationError();
			for (BusinessServiceValidationError bsValidationError : bsValidationErrors) {
				String error = bsValidationError.getError();
				if (error.contains("Unexpected link LinkNodeDto")) {
					if (error.contains("Vehicle") && error.contains("Invalid_LinkReason")) {
						invalidVehicleLinkFound = true;
					} else if (error.contains("Organisation") && error.contains("Invalid_LinkReason")) {
						invalidOrganisationLinkFound = true;
					} else if (error.contains("Operation") && error.contains("Invalid_LinkReason")) {
						invalidOperationLinkFound = true;
					} else if (error.contains("Comms") && error.contains("Invalid_LinkReason")) {
						invalidCommsLinkFound = true;
					} else if (error.contains("Location") && error.contains("Invalid_LinkReason")) {
						invalidLocationLinkFound = true;
					} else if (error.contains("RiskAssessment") && error.contains("Invalid_LinkReason")) {
						invalidRiskAssessmentLinkFound = true;
					} else if (error.contains("Person") && error.contains("Invalid_LinkReason")) {
						invalidPersonLinkFound = true;
					}
				}
			}
			assertTrue("Expected to find invalid Vehicle link", invalidVehicleLinkFound);
			assertTrue("Expected to find invalid Organisation link", invalidOrganisationLinkFound);
			assertTrue("Expected to find invalid Operation link", invalidOperationLinkFound);
			assertTrue("Expected to find invalid Comms link", invalidCommsLinkFound);
			assertTrue("Expected to find invalid Location link", invalidLocationLinkFound);
			assertTrue("Expected to find invalid RiskAssessment link", invalidRiskAssessmentLinkFound);
			assertTrue("Expected to find invalid Person link", invalidPersonLinkFound);
		}
	}

	/** ============================ #37109 - Vehicle Gherkins ============================ **/
	
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37109, 
			mingleTitle = "Business Service- Validation Framework - Links to vehicle objects", 
			acceptanceCriteriaRefs = "CR9.1", 
			given = "Vechicle is linked and link is researched & vehicle is supplied with proper values", 
			when = "Event object Submited", 
			then = "No Validation error should be returned")
	public void testEventObjectWhenVehicleIsLinkedAndLinkIsResearchedAndVehilceWithProperValue()
			throws Exception {
		PoleObjectDto poleObject = createPoleObjectDto();
		for (LinkDto linkDto : poleObject.getLinks()) {
			PoleObjectDto toPoleObject = linkDto.getToPoleObject();
			if (toPoleObject instanceof VehicleDto) {
				linkDto.setResearched(true);
				((VehicleDto) toPoleObject).setResearched(true);
				break;
			}
		}
		doCreateEventObject(true, poleObject);			
	}

	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37109, 
			mingleTitle = "Business Service- Validation Framework - Links to vehicle objects", 
			acceptanceCriteriaRefs = "CR9.2.1", given = "Vehicle is link and RegiStrationTypeGiven == null", 
			when = "Event object Submited", 
			then = "Validation error should return")
	public void testEventObjectWhenVehicleIsLinkAndRegistrationTypeGivenMandatory() throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				if (linkDto.getToPoleObject() instanceof VehicleDto) {
					VehicleDto vehicleDto = (VehicleDto) linkDto.getToPoleObject();
					vehicleDto.setRegistrationTypeGiven(null);
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>registrationTypeGiven must have a value</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37109, 
			mingleTitle = "Business Service- Validation Framework - Links to vehicle objects", 
			acceptanceCriteriaRefs = "CR9.2.2", 
			given = "Vehicle is link and RegiStrationTypeGiven with invalid CV value", 
			when = "Event object Submited", 
			then = "Validation error should return")
	public void testEventObjectWhenVehicleIsLinkAndRegistrationTypeGivenMandatoryWithInvalidCvValue() 
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				if (linkDto.getToPoleObject() instanceof VehicleDto) {
					VehicleDto vehicleDto = (VehicleDto) linkDto.getToPoleObject();
					vehicleDto.setRegistrationTypeGiven("abcdefgh");
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(), ide.getMessage().contains(
					"error>registrationTypeGiven has an incorrect CV value for list REG_TYPE</"));
		}
	}

	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37109, 
			mingleTitle = "Business Service- Validation Framework - Links to vehicle objects", 
			acceptanceCriteriaRefs = "CR9.3.1", 
			given = "Vehicle is link and RegiStrationTypeGivn == FULL AND registrationNumber == null", 
			when = "Event object Submited", 
			then = "Validation error should return")
	public void testEventObjectWhenVehicleIsLinkAndRegistrationTypeFull() throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				if (linkDto.getToPoleObject() instanceof VehicleDto) {
					VehicleDto vehicleDto = (VehicleDto) linkDto.getToPoleObject();
					vehicleDto.setRegistrationTypeGiven("FULL");					
					vehicleDto.setRegistrationNumber(null);					
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>registrationNumber must have a value</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37109, 
			mingleTitle = "Business Service- Validation Framework - Links to vehicle objects", 
			acceptanceCriteriaRefs = "CR9.3.2", 
			given = "Vehicle is link and RegiStrationTypeGivn == PARTIAL AND registrationNumber == null", 
			when = "Event object Submited", 
			then = "No error should return")
	public void testEventObjectWhenVehicleIsLinkAndRegistrationTypePartial() throws Exception {
		PoleObjectDto poleObject = createPoleObjectDto();
		for (LinkDto linkDto : poleObject.getLinks()) {
			if (linkDto.getToPoleObject() instanceof VehicleDto) {
				VehicleDto vehicleDto = (VehicleDto) linkDto.getToPoleObject();
				vehicleDto.setRegistrationTypeGiven("PARTIAL");
				vehicleDto.setRegistrationNumber(null);
				break;
			}
		}
		doCreateEventObject(true, poleObject);
	}

	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37109, 
			mingleTitle = "Business Service- Validation Framework - Links to vehicle objects", 
			acceptanceCriteriaRefs = "CR9.4.1", 
			given = "Vehicle is link and RegiStrationTypeGivn == PARTIAL AND registrationNumber == 'A12- ab'", 
			when = "Event object Submited", 
			then = "Validation error should return")
	public void testEventObjectWhenVehicleIsLinkAndRegistrationTypePARTIALWithInvalidRegistrationNumber() 
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				if (linkDto.getToPoleObject() instanceof VehicleDto) {
					VehicleDto vehicleDto = (VehicleDto) linkDto.getToPoleObject();
					vehicleDto.setRegistrationTypeGiven("PARTIAL");					
					vehicleDto.setRegistrationNumber("A12- ab");					
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(), ide.getMessage().contains(
					"error>registrationNumber is not valid for pattern (^[A-Z0-9-]*$)?</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37109, 
			mingleTitle = "Business Service- Validation Framework - Links to vehicle objects", 
			acceptanceCriteriaRefs = "CR9.4.2", 
			given = "Vehicle is link and RegiStrationTypeGivn == FULL AND registrationNumber == 'A12- ab'", 
			when = "Event object Submited", 
			then = "Validation error should return")
	public void testEventObjectWhenVehicleIsLinkAndRegistrationTypeFULLWithInvalidRegistrationNumber() 
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				if (linkDto.getToPoleObject() instanceof VehicleDto) {
					VehicleDto vehicleDto = (VehicleDto) linkDto.getToPoleObject();
					vehicleDto.setRegistrationTypeGiven("FULL");					
					vehicleDto.setRegistrationNumber("A12- ab");					
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(), ide.getMessage().contains(
					"error>registrationNumber is not valid for pattern (^[A-Z0-9-]*$)?</"));
		} 
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37109, 
			mingleTitle = "Business Service- Validation Framework - Links to vehicle objects", 
			acceptanceCriteriaRefs = "CR9.4.3", 
			given = "Vehicle is link and RegiStrationTypeGivn == PARTIAL AND registrationNumber == 'A12 - M1'", 
			when = "Event object Submited", 
			then = "No error should return")
	public void testEventObjectWhenVehicleIsLinkAndRegistrationTypePARTIALWithValidRegistrationNumber() 
			throws Exception {		
		PoleObjectDto poleObject = createPoleObjectDto();
		for (LinkDto linkDto : poleObject.getLinks()) {
			if (linkDto.getToPoleObject() instanceof VehicleDto) {
				VehicleDto vehicleDto = (VehicleDto) linkDto.getToPoleObject();
				vehicleDto.setRegistrationTypeGiven("PARTIAL");
				vehicleDto.setRegistrationNumber("A12 - M1");
				break;
			}
		}
		doCreateEventObject(true, poleObject);			
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37109, 
			mingleTitle = "Business Service- Validation Framework - Links to vehicle objects", 
			acceptanceCriteriaRefs = "CR9.4.4", 
			given = "Vehicle is link and RegiStrationTypeGivn == FULL AND registrationNumber == 'A12 - M1'", 
			when = "Event object Submited", 
			then = "No error should return")
	public void testEventObjectWhenVehicleIsLinkAndRegistrationTypeFULLWithValidRegistrationNumber() throws Exception {		
		PoleObjectDto poleObject = createPoleObjectDto();
		for (LinkDto linkDto : poleObject.getLinks()) {
			if (linkDto.getToPoleObject() instanceof VehicleDto) {
				VehicleDto vehicleDto = (VehicleDto) linkDto.getToPoleObject();
				vehicleDto.setRegistrationTypeGiven("FULL");
				vehicleDto.setRegistrationNumber("A12 - M1");
				break;
			}
		}
		doCreateEventObject(true, poleObject);		
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37109, 
			mingleTitle = "Business Service- Validation Framework - Links to vehicle objects", 
			acceptanceCriteriaRefs = "CR9.5", 
			given = "Vehicle is link and RegiStrationTypeGivn == NONE AND registrationNumber != null", 
			when = "Event object Submited", 
			then = "Validation error should return")
	public void testEventObjectWhenVehicleIsLinkAndRegistrationTypeNONE() throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				if (linkDto.getToPoleObject() instanceof VehicleDto) {
					VehicleDto vehicleDto = (VehicleDto) linkDto.getToPoleObject();
					if (vehicleDto.getRegistrationTypeGiven() != null) {
						vehicleDto.setRegistrationTypeGiven("NONE");
						vehicleDto.setRegistrationNumber("Blah123");
					}
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>registrationNumber must not have a value</"));
		}
	}

	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37109, 
			mingleTitle = "Business Service- Validation Framework - Links to vehicle objects", 
			acceptanceCriteriaRefs = "CR9.6", 
			given = "Vehicle is link AND foreignVehicle == null ", 
			when = "Event object Submited", 
			then = "Validation error should return")
	public void testEventObjectWhenVehicleIsLinkAndforeignVehicleIsMandatory() throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				if (linkDto.getToPoleObject() instanceof VehicleDto) {
					VehicleDto vehicleDto = (VehicleDto) linkDto.getToPoleObject();
					vehicleDto.setForeignVehicle(null);
					vehicleDto.setResearched(true);
					// For static objects link.research == toPoleObject.research
					linkDto.setResearched(true);
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>foreignVehicle must have a value</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37109, 
			mingleTitle = "Business Service- Validation Framework - Links to vehicle objects", 
			acceptanceCriteriaRefs = "CR9.7.1", 
			given = "Vechicle is linked and foreignVehicle = TRUE & registrationCountry == null", 
			when = "Event object Submited", 
			then = "Validation error should be returned")
	public void testEventObjectWhenVehicleIsLinkedAndForeignVehicleWithCountryNull()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				if (linkDto.getToPoleObject() instanceof VehicleDto) {
					VehicleDto vehicleDto = (VehicleDto) linkDto.getToPoleObject();
					vehicleDto.setForeignVehicle(Boolean.TRUE);
					vehicleDto.setRegistrationCountry(null);
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>registrationCountry must have a value</"));			
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37109, 
			mingleTitle = "Business Service- Validation Framework - Links to vehicle objects", 
			acceptanceCriteriaRefs = "CR9.7.2", 
			given = "Vechicle is linked and foreignVehicle = TRUE & registrationCountry != null & "
					+ "invalid country value 'abcdefgh'", 
			when = "Event object Submited", 
			then = "Validation error should be returned")
	public void testEventObjectWhenVehicleIsLinkedAndForeignVehicleWithInvalidCountry()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				if (linkDto.getToPoleObject() instanceof VehicleDto) {
					VehicleDto vehicleDto = (VehicleDto) linkDto.getToPoleObject();
					vehicleDto.setForeignVehicle(Boolean.TRUE);
					vehicleDto.setRegistrationCountry("abcdefgh");
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(), ide.getMessage().contains(
					"error>registrationCountry has an incorrect CV value for list COUNTRY</"));		
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37109, 
			mingleTitle = "Business Service- Validation Framework - Links to vehicle objects", 
			acceptanceCriteriaRefs = "CR9.7.3", 
			given = "Vechicle is linked and foreignVehicle = TRUE & registrationCountry != null "
					+ "& valid country value 'UNITED KINGDOM'", 
			when = "Event object Submited", 
			then = "No error should be returned")
	public void testEventObjectWhenVehicleIsLinkedAndForeignVehicleWithValidCountry()
		throws Exception {		
		PoleObjectDto poleObject = createPoleObjectDto();
		for (LinkDto linkDto : poleObject.getLinks()) {
			if (linkDto.getToPoleObject() instanceof VehicleDto) {
				VehicleDto vehicleDto = (VehicleDto) linkDto.getToPoleObject();
				vehicleDto.setForeignVehicle(Boolean.TRUE);
				vehicleDto.setRegistrationCountry("UNITED KINGDOM");
				break;
			}
		}
		doCreateEventObject(true, poleObject);			
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37109, 
			mingleTitle = "Business Service- Validation Framework - Links to vehicle objects", 
			acceptanceCriteriaRefs = "CR9.8", 
			given = "Vechicle is linked and foreignVehicle = FALSE & (vrmTransformed != FALSE || "
					+ "modelTransformed != FALSE || chassisTransformed != FALSE)", 
			when = "Event object Submited", 
			then = "Validation error should be returned")
	public void testEventObjectWhenVehicleIsLinkedAndDomesticVehicleWithVrmAndModelAndChassisTransformedTrue()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				if (linkDto.getToPoleObject() instanceof VehicleDto) {
					VehicleDto vehicleDto = (VehicleDto) linkDto.getToPoleObject();
					vehicleDto.setForeignVehicle(Boolean.FALSE);
					vehicleDto.setVrmTransformed(Boolean.TRUE);
					vehicleDto.setModelTransformed(Boolean.TRUE);
					vehicleDto.setChassisTransformed(Boolean.TRUE);
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>vrmTransformed does not match the expected value false</"));
			assertTrue("", ide.getMessage().contains(
					"error>modelTransformed does not match the expected value false</"));
			assertTrue("", ide.getMessage().contains(
					"error>chassisTransformed does not match the expected value false</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37109, 
			mingleTitle = "Business Service- Validation Framework - Links to vehicle objects", 
			acceptanceCriteriaRefs = "CR9.9", 
			given = "Vechicle is linked and foreignVehicle = TRUE & (vrmTransformed == null || "
					+ "modelTransformed == null || chassisTransformed == null)", 
			when = "Event object Submited", 
			then = "Validation error should be returned")
	public void testEventObjectWhenVehicleIsLinkedAndForeignVehicleWithVrmAndModelAndChassisTransformedNull()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				if (linkDto.getToPoleObject() instanceof VehicleDto) {
					VehicleDto vehicleDto = (VehicleDto) linkDto.getToPoleObject();
					vehicleDto.setForeignVehicle(Boolean.TRUE);
					vehicleDto.setRegistrationCountry("UNITED KINGDOM");
					vehicleDto.setVrmTransformed(null);
					vehicleDto.setModelTransformed(null);
					vehicleDto.setChassisTransformed(null);
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>vrmTransformed must have a value</"));
			assertTrue("", ide.getMessage().contains("error>modelTransformed must have a value</"));
			assertTrue("", ide.getMessage().contains("error>chassisTransformed must have a value</"));
		}
	}
	
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37109, 
			mingleTitle = "Business Service- Validation Framework - Links to vehicle objects", 
			acceptanceCriteriaRefs = "CR9.10.1", 
			given = "Vechicle is linked and VehicleInspection.doorsTotal.length > 6", 
			when = "Event object Submited", 
			then = "Validation error should be returned")
	public void testEventObjectWhenVehicleIsLinkedAndVehicleInspectionDoorsTotalLengthMoreThan6Digit()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				if (linkDto.getToPoleObject() instanceof VehicleDto) {
					VehicleDto vehicleDto = (VehicleDto) linkDto.getToPoleObject();
					for (VehicleInspectionDto vehicleInspectionD : vehicleDto.getInspections()) {
						vehicleInspectionD.setDoorsTotal("123456789");
					}
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>doorsTotal is too long</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37109, 
			mingleTitle = "Business Service- Validation Framework - Links to vehicle objects", 
			acceptanceCriteriaRefs = "CR9.10.2", 
			given = "Vechicle is linked and VehicleInspection.doorsTotal.length < 6 but contains non-numeric values", 
			when = "Event object Submited", 
			then = "Validation error should be returned")
	public void testEventObjectWhenVehicleIsLinkedAndVehicleInspectionDoorsTotalLengthEqual6WithNonumericValues()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				if (linkDto.getToPoleObject() instanceof VehicleDto) {
					VehicleDto vehicleDto = (VehicleDto) linkDto.getToPoleObject();
					for (VehicleInspectionDto vehicleInspectionD : vehicleDto.getInspections()) {
						vehicleInspectionD.setDoorsTotal("aaa");
					}
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>doorsTotal is not valid for pattern (^[0-9]*$)?</"));
		}
	}

	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37109, 
			mingleTitle = "Business Service- Validation Framework - Links to vehicle objects", 
			acceptanceCriteriaRefs = "CR9.11", 
			given = "Vechicle is linked and VehicleInspection.mileage.length > 6", 
			when = "Event object Submited", 
			then = "Validation error should be returned")
	public void testEventObjectWhenVehicleIsLinkedAndVehicleInspectionMileageLengthMoreThan6Digit() throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				if (linkDto.getToPoleObject() instanceof VehicleDto) {
					VehicleDto vehicleDto = (VehicleDto) linkDto.getToPoleObject();
					for (VehicleInspectionDto vehicleInspectionD : vehicleDto.getInspections()) {
						vehicleInspectionD.setMileage(123456767);
					}
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>mileage is too long</"));
		}
	}
	

	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37109, 
			mingleTitle = "Business Service- Validation Framework - Links to vehicle objects", 
			acceptanceCriteriaRefs = "CR9.12", 
			given = "Vechicle is linked and VehicleInspection.readingDate > currentDate",
			when = "Event object Submited", 
			then = "Validation error should be returned")
	public void testEventObjectWhenVehicleIsLinkedAndVehicleInspectionReadingDateIsGreaterThanCurrentDate()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				if (linkDto.getToPoleObject() instanceof VehicleDto) {
					VehicleDto vehicleDto = (VehicleDto) linkDto.getToPoleObject();
					for (VehicleInspectionDto vehicleInspectionD : vehicleDto.getInspections()) {
						vehicleInspectionD.setReadingDate(new PoleDate().addDays(5));
					}
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>readingDate cannot be greater than todays date</"));
		}
	}

	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37109, 
			mingleTitle = "Business Service- Validation Framework - Links to vehicle objects", 
			acceptanceCriteriaRefs = "CR9.14", 
			given = "Vechicle is linked and VehicleInspection.registrationYear.length > 4", 
			when = "Event object Submited", 
			then = "Validation error should be returned")
	public void testEventObjectWhenVehicleIsLinkedAndVehicleInspectionRegistrationYearLengthMoreThan4Digit()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				if (linkDto.getToPoleObject() instanceof VehicleDto) {
					VehicleDto vehicleDto = (VehicleDto) linkDto.getToPoleObject();
					for (VehicleInspectionDto vehicleInspectionD : vehicleDto.getInspections()) {
						vehicleInspectionD.setRegistrationYear(20017);
					}
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>registrationYear is too long</"));
		}
	}
	
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37109, 
			mingleTitle = "Business Service- Validation Framework - Links to vehicle objects", 
			acceptanceCriteriaRefs = "CR9.15.1", 
			given = "Vechicle is linked and VehicleFeature.vehicleFeature is supplied (constrained value)", 
			when = "Event object Submited", 
			then = "Validation error should be returned")
	public void testEventObjectWhenVehicleIsLinkedAndVehicleFeatureWithInvalidCVValue()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				if (linkDto.getToPoleObject() instanceof VehicleDto) {
					VehicleDto vehicleDto = (VehicleDto) linkDto.getToPoleObject();
					for (VehicleFeatureDto vehicleFeature : vehicleDto.getFeatures()) {
						vehicleFeature.setVehicleFeature("CV INVALID");
					}
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(), ide.getMessage().contains(
					"error>vehicleFeature has an incorrect CV value for list VEHICLE_FEATURE</"));		
		}
	}
	
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37109, 
			mingleTitle = "Business Service- Validation Framework - Links to vehicle objects", 
			acceptanceCriteriaRefs = "CR9.15.2", 
			given = "Vechicle is linked and VehicleFeature.vehicleFeature is supplied (constrained value)", 
			when = "Event object Submited", 
			then = "No should be returned")
	public void testEventObjectWhenVehicleIsLinkedAndVehicleFeatureWithValidCVValue() throws Exception {
		PoleObjectDto poleObject = createPoleObjectDto();
		for (LinkDto linkDto : poleObject.getLinks()) {
			if (linkDto.getToPoleObject() instanceof VehicleDto) {
				VehicleDto vehicleDto = (VehicleDto) linkDto.getToPoleObject();
				for (VehicleFeatureDto vehicleFeature : vehicleDto.getFeatures()) {
					vehicleFeature.setVehicleFeature("SUNROOF");
				}
				break;
			}
		}
		doCreateEventObject(true, poleObject);
	}
	
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37109, 
			mingleTitle = "Business Service- Validation Framework - Links to vehicle objects", 
			acceptanceCriteriaRefs = "CR9.15.3", 
			given = "Vechicle is linked and VehicleFeature.vehicleFeature is not supplied", 
			when = "Event object Submited", 
			then = "No error should be returned")
	public void testEventObjectWhenVehicleIsLinkedAndVehicleFeatureNotSupplied() throws Exception {
		PoleObjectDto poleObject = createPoleObjectDto();
		for (LinkDto linkDto : poleObject.getLinks()) {
			if (linkDto.getToPoleObject() instanceof VehicleDto) {
				VehicleDto vehicleDto = (VehicleDto) linkDto.getToPoleObject();
				vehicleDto.setFeatures(null);
				break;
			}
		}
		doCreateEventObject(true, poleObject);
	}
	
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37109, 
			mingleTitle = "Business Service- Validation Framework - Links to vehicle objects", 
			acceptanceCriteriaRefs = "CR9.16", 
			given = "Vechicle is linked and VehiclePncFieldsDto is supplied which is not allowed", 
			when = "Event object Submited", 
			then = "Validation error should be returned")
	public void testEventObjectWhenVehicleIsLinkedAndVehiclePncFieldsSupplied()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				if (linkDto.getToPoleObject() instanceof VehicleDto) {
					VehicleDto vehicleDto = (VehicleDto) linkDto.getToPoleObject();
					VehiclePncFieldsDto vehiclePncFields = new VehiclePncFieldsDto();				
					vehiclePncFields.setModificationStatus(ModificationStatusDto.CREATE);
					vehiclePncFields.setInsuranceHolder("Test User");					
					vehicleDto.setPncFields(vehiclePncFields);
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>Unexpected child object [child] VehiclePncFieldsDto"));			
		}
	}		
	
	/** ============================ #37110 - Organisation Gherkins ============================ **/
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37110, 
			mingleTitle = "Business Service- Validation Framework - Links to Organisation Object", 
			acceptanceCriteriaRefs = "CR10.1", 
			given = "Organisation is linked and link is researched & organisation is researched "
					+ "& and user passes any fields on organisation", 
			when = "Event object Submited", 
			then = "Validation error should be returned")
	public void testEventWhenOrganisationIsLinkedAndLinkIsResearchedAndOrgansationObjectWithProperValue()
			throws Exception {		
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof OrganisationDto) {
					linkDto.setResearched(true);
					((OrganisationDto) toPoleObject).setResearched(true);
					break;
				}
			}
			doCreateEventObject(true, poleObject);			
	}
	
/** ============================ #37112 - Comms Gherkins ============================ **/
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37112, 
			mingleTitle = "Business Service- Validation Framework - Links to Comms Object", 
			acceptanceCriteriaRefs = "CR12.1", 
			given = "Comms is linked and link is researched & commsType null", 
			when = "Event object Submited", 
			then = "Validation error should be returned")
	public void testEventWhenCommsIsLinkedAndLinkIsResearchedAndCommsTypeNull()
			throws Exception {	
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof CommsDto) {
					((CommsDto) toPoleObject).setCommsType(null);
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>commsType must have a value</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37112, 
			mingleTitle = "Business Service- Validation Framework - Links to Comms Object", 
			acceptanceCriteriaRefs = "CR12.2", 
			given = "Comms is linked and link is researched & mainNumber null", 
			when = "Event object Submited", 
			then = "Validation error should be returned")
	public void testEventWhenCommsIsLinkedAndLinkIsResearchedAndMainNumberNull()
			throws Exception {	
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof CommsDto) {
					((CommsDto) toPoleObject).setMainNumber(null);
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>mainNumber must have a value</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37112, 
			mingleTitle = "Business Service- Validation Framework - Links to Comms Object", 
			acceptanceCriteriaRefs = "CR12.3", 
			given = "Comms is linked and link is researched & emailAddress is not null", 
			when = "Event object Submited", 
			then = "Validation error should be returned")
	public void testEventWhenCommsIsLinkedAndLinkIsResearchedAndEmainNotNull()
			throws Exception {	
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof CommsDto) {
					((CommsDto) toPoleObject).setEmailAddress("harsh.shah@northgateps.com");
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>emailAddress must not have a value</"));
		}
	}
	
	/** ============================ #37113 - Location Gherkins ============================ **/
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37113, 
			mingleTitle = "Business Service- Validation Framework - Links to Location object", 
			acceptanceCriteriaRefs = "CR13.2", 
			given = "Location is linked and link is researched and location force is null", 
			when = "Event object Submited", 
			then = "Validation error should be returned")
	public void testEventWhenLocationIsLinkedAndLocationIsResearchedButForceNull()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof LocationDto) {
					((LocationDto) toPoleObject).setResearched(true);
					((LocationDto) toPoleObject).setForce(null);					
					// setting link to researched=true because for static object link.researched =
					// toPoleObject.Researched
					linkDto.setResearched(true);
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>force must have a value</"));			
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37113, 
			mingleTitle = "Business Service- Validation Framework - Links to Location object", 
			acceptanceCriteriaRefs = "CR13.3", 
			given = "Location is linked and link is researched/unresearched and locationAlternativeName supplied", 
			when = "Event object Submited", 
			then = "No error should be returned")
	public void testEventWhenLocationIsLinkedAndLocationAlternativeNameSupplied()
			throws Exception {		
		PoleObjectDto poleObject = createPoleObjectDto();
		for (LinkDto linkDto : poleObject.getLinks()) {
			PoleObjectDto toPoleObject = linkDto.getToPoleObject();
			if (toPoleObject instanceof LocationDto) {

				LocationDto location = (LocationDto) toPoleObject;
				location.setResearched(true);
				
				LocationAlternativeNameDto alternativeName = new LocationAlternativeNameDto();
				alternativeName.setModificationStatus(ModificationStatusDto.CREATE);
				alternativeName.setAlternativeName("Test Location 37113");
				location.addAlternativeName(alternativeName);
				
				// setting link to researched=true because for static object link.researched =
				// toPoleObject.Researched
				linkDto.setResearched(true);
				
				break;
			}
		}
		doCreateEventObject(true, poleObject);				
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37113, 
			mingleTitle = "Business Service- Validation Framework - Links to Location object", 
			acceptanceCriteriaRefs = "CR13.4", 
			given = "Location is linked and link is researched/unresearched and postcode contains non alphanumeric"
					+ " characters", 
			when = "Event object Submited", 
			then = "Validation error should be returned")
	public void testEventWhenLocationIsLinkedAndPostcodeWithNonAplhaNumericCharacters()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof LocationDto) {
					linkDto.setResearched(true);
					LocationDto location = (LocationDto) toPoleObject;
					location.setResearched(true);
					location.setPostcode("ABC@-123");
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>postcode is not valid for pattern (^[A-Z0-9 a-z]*$)?</"));		
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37111, 
			mingleTitle = "Business Service- Validation Framework - Links to Operation object", 
			acceptanceCriteriaRefs = "CR11.1", 
			given = "a Operation Object is linked to an Event or an Object", 
			when = "operation.startDate > operation.endDate", 
			then = "Validation error should be returned")
	public void testEventObjectWhenOperationIsLinkedAndOperationStartDateIsGreaterThanEndDate()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();			
			PoleDate startDate = new PoleDate(); // Current Date + 5 days
			startDate = startDate.addDays(5);			
			PoleDate endDate = new PoleDate(); // Current Date - 5 days
			endDate = endDate.addDays(-5);
			
			for (LinkDto linkDto : poleObject.getLinks()) {
				if (linkDto.getToPoleObject() instanceof OperationDto) {
					OperationDto operation = (OperationDto) linkDto.getToPoleObject();
					operation.setStartDate(startDate);
					operation.setEndDate(endDate);
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>startDate cannot be greater than endDate</"));			
		}
	}
	
	/** ============================ #37106 - Person Gherkins ============================ **/
	
	private void testPersonAliasSurnameNull(String aliasType) throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof PersonDto) {
					PersonDto person = (PersonDto) toPoleObject;
					for (PersonAliasDto personAlias : person.getAliases()) {
						if (aliasType.equals(personAlias.getAliasType())) {
							personAlias.setSurname(null);
							break;
						}
					}
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>surname must have a value</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106,
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "CR8.1", 
			given = "a child object personAlias is present", 
			when = "alias.aliasType = 'Alias Name' and alias.surname == null || "
					+ "(alias.forename1&alias.forename2&alias.forename3)== null", 
			then = "Validation error should be returned")
	public void testEventWhenPersonIsLinkedAndAliasNameSurnameNull()
			throws Exception {
		testPersonAliasSurnameNull("ALIAS NAME");
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106,
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "CR8.6", 
			given = "a child object personAlias is present", 
			when = "alias.aliasType = 'Alias Name And Date Of Birth' and alias.surname == null || "
					+ "(alias.forename1&alias.forename2&alias.forename3)== null", 
			then = "Validation error should be returned")
	public void testEventWhenPersonIsLinkedAndAliasNameAndDobSurnameNull()
			throws Exception {
		testPersonAliasSurnameNull("ALIAS NAME AND DATE OF BIRTH");
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106,
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "CR8.12", 
			given = "a child object personAlias is present", 
			when = "alias.aliasType = 'Previous Name' and alias.surname == null || "
					+ "(alias.forename1&alias.forename2&alias.forename3)== null", 
			then = "Validation error should be returned")
	public void testEventWhenPersonIsLinkedAndAliasPreviousNameSurnameNull()
			throws Exception {
		testPersonAliasSurnameNull("PREVIOUS NAME");
	}
	
	private void testPersonAliasNameSameAsPerson(String aliasType) throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof PersonDto) {
					PersonDto person = (PersonDto) toPoleObject;
					for (PersonAliasDto personAlias : person.getAliases()) {
						if (aliasType.equals(personAlias.getAliasType())) {
							personAlias.setSurname(person.getSurname());
							personAlias.setForename1(person.getForename1());
							personAlias.setForename2(person.getForename2());
							personAlias.setForename3(person.getForename3());
							break;
						}
					}
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>Alias name cannot be the same as the Persons name</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106,
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "CR8.2,CR8.3,CR8.4,CR8.5", 
			given = "a child object personAlias is present", 
			when = "alias.aliasType = 'Alias Name' and person.surname == alias.surname", 
			then = "Validation error should be returned")
	public void testEventWhenPersonIsLinkedAndAliasNameNameSameAsPerson()
			throws Exception {	
		testPersonAliasNameSameAsPerson("ALIAS NAME");
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106,
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "CR8.7,CR8.46,CR8.47,CR8.48", 
			given = "a child object personAlias is present", 
			when = "alias.aliasType = 'Alias Name And Date Of Birth' and person.surname == alias.surname", 
			then = "Validation error should be returned")
	public void testEventWhenPersonIsLinkedAndAliasNameAndDobNameSameAsPerson()
			throws Exception {	
		testPersonAliasNameSameAsPerson("ALIAS NAME AND DATE OF BIRTH");
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106,
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "CR8.13,CR8.14,CR8.15,CR8.16", 
			given = "a child object personAlias is present", 
			when = "alias.aliasType = 'Previous Name' and person.surname == alias.surname",
			then = "Validation error should be returned")
	public void testEventWhenPersonIsLinkedAndAliasPreviousNameNameSameAsPerson()
			throws Exception {	
		testPersonAliasNameSameAsPerson("PREVIOUS NAME");
	}
	
	private void testPersonAliasForenamesNull(String aliasType) throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof PersonDto) {
					PersonDto person = (PersonDto) toPoleObject;
					for (PersonAliasDto personAlias : person.getAliases()) {
						if (aliasType.equals(personAlias.getAliasType())) {
							personAlias.setForename1(null);
							personAlias.setForename2(null);
							personAlias.setForename3(null);
							break;
						}
					}
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>One of the forename fields must have a value</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "CR8.1", 
			given = "a child object personAlias is present", 
			when = "alias.aliasType = 'Alias Name' and alias.surname == null || "
					+ "(alias.forename1&alias.forename2&alias.forename3)== null", 
			then = "Validation error should be returned")
	public void testEventWhenPersonIsLinkedAndAliasNameForenamesNull()
			throws Exception {	
		testPersonAliasForenamesNull("ALIAS NAME");
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "CR8.6", 
			given = "a child object personAlias is present", 
			when = "alias.aliasType = 'Alias Name And Date Of Birth' and alias.surname == null || "
					+ "(alias.forename1&alias.forename2&alias.forename3)== null", 
			then = "Validation error should be returned")
	public void testEventWhenPersonIsLinkedAndAliasNameAndDobForenamesNull()
			throws Exception {	
		testPersonAliasForenamesNull("ALIAS NAME AND DATE OF BIRTH");
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "CR8.12", 
			given = "a child object personAlias is present", 
			when = "alias.aliasType = 'Previous Name' and alias.surname == null || "
					+ "(alias.forename1&alias.forename2&alias.forename3)== null", 
			then = "Validation error should be returned")
	public void testEventWhenPersonIsLinkedAndAliasPreviousNameForenamesNull()
			throws Exception {	
		testPersonAliasForenamesNull("PREVIOUS NAME");
	}
	
	private void testPersonAliasDobNull(String aliasType) throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof PersonDto) {
					PersonDto person = (PersonDto) toPoleObject;
					for (PersonAliasDto personAlias : person.getAliases()) {
						if (aliasType.equals(personAlias.getAliasType())) {
							personAlias.setDateOfBirth(null);
							break;
						}
					}
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>dateOfBirth must have a value</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "CR8.8", 
			given = "a child object personAlias is present", 
			when = "alias.aliasType = 'Alias Name And Date Of Birth' and alias.dateOfBirth == null", 
			then = "Validation error should be returned")
	public void testEventWhenPersonIsLinkedAndAliasNameAndDobDobNull()
			throws Exception {	
		testPersonAliasDobNull("ALIAS NAME AND DATE OF BIRTH");
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "CR8.10", 
			given = "a child object personAlias is present", 
			when = "alias.aliasType = 'Alias Date Of Birth' and alias.dateOfBirth == null", 
			then = "Validation error should be returned")
	public void testEventWhenPersonIsLinkedAndAliasDobDobNull()
			throws Exception {	
		testPersonAliasDobNull("ALIAS DATE OF BIRTH");
	}
	
	private void testPersonAliasDobSameAsPerson(String aliasType) throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof PersonDto) {
					PersonDto person = (PersonDto) toPoleObject;
					for (PersonAliasDto personAlias : person.getAliases()) {
						if (aliasType.equals(personAlias.getAliasType())) {
							personAlias.setDateOfBirth(person.getDateOfBirth());
							break;
						}
					}
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>Alias dateOfBirth cannot be the same as Person dateOfBirth</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "CR8.9", 
			given = "a child object personAlias is present", 
			when = "alias.aliasType = 'Alias Name And Date Of Birth' and person.dateOfBirth == alias.dateOfBirth", 
			then = "Validation error should be returned")
	public void testEventWhenPersonIsLinkedAndAliasNameAndDobDobSameAsPerson()
			throws Exception {	
		testPersonAliasDobSameAsPerson("ALIAS NAME AND DATE OF BIRTH");
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "CR8.11", 
			given = "a child object personAlias is present", 
			when = "alias.aliasType = 'Alias Date Of Birth' and person.dateOfBirth == alias.dateOfBirth", 
			then = "Validation error should be returned")
	public void testEventWhenPersonIsLinkedAndAliasDobDobSameAsPerson()
			throws Exception {	
		testPersonAliasDobSameAsPerson("ALIAS DATE OF BIRTH");
	}
	
	private void testPersonAliasDobGreaterThanCurrentDate(String aliasType) throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof PersonDto) {
					PersonDto person = (PersonDto) toPoleObject;
					for (PersonAliasDto personAlias : person.getAliases()) {
						if (aliasType.equals(personAlias.getAliasType())) {
							personAlias.setDateOfBirth(new PoleDate().addDays(5));
							break;
						}
					}
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>dateOfBirth cannot be greater than todays date</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "UNKNOWN", 
			given = "a child object personAlias is present", 
			when = "alias.aliasType = 'Alias Name And Date Of Birth' and alias.dateOfBirth is greater than todays date", 
			then = "Validation error should be returned")
	public void testEventWhenPersonIsLinkedAndAliasNameAndDobDobGreaterThanCurrentDate()
			throws Exception {	
		testPersonAliasDobGreaterThanCurrentDate("ALIAS NAME AND DATE OF BIRTH");
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "UNKNOWN", 
			given = "a child object personAlias is present", 
			when = "alias.aliasType = 'Alias Date Of Birth' and alias.dateOfBirth is greater than todays date", 
			then = "Validation error should be returned")
	public void testEventWhenPersonIsLinkedAndAliasDobDobGreaterThanCurrentDate()
			throws Exception {	
		testPersonAliasDobGreaterThanCurrentDate("ALIAS DATE OF BIRTH");
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "TODO", 
			given = "a child object personEmployment is present", 
			when = "employment.occupationCodeLevel1 == null and employment.occupationCodeLevel2 == null", 
			then = "No validation error should be returned")
	public void testEventWhenPersonIsLinkedAndEmploymentOccupationLevel1NotSetOccupationLevel2NotSet()
			throws Exception {
		PoleObjectDto poleObject = createPoleObjectDto();
		for (LinkDto linkDto : poleObject.getLinks()) {
			PoleObjectDto toPoleObject = linkDto.getToPoleObject();
			if (toPoleObject instanceof PersonDto) {
				PersonDto person = (PersonDto) toPoleObject;
				PersonEmploymentDto personEmployment = person.getEmployments().get(0);
				personEmployment.setOccupationCodeLevel1(null);
				personEmployment.setOccupationCodeLevel2(null);
				break;
			}
		}
		doCreateEventObject(true, poleObject);
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "CR8.17", 
			given = "a child object personEmployment is present", 
			when = "employment.occupationCodeLevel1 is supplied and employment.occupationCodeLevel2 == null", 
			then = "No validation error should be returned")
	public void testEventWhenPersonIsLinkedAndEmploymentOccupationLevel1SetOccupationLevel2NotSet()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof PersonDto) {
					PersonDto person = (PersonDto) toPoleObject;
					PersonEmploymentDto personEmployment = person.getEmployments().get(0);
					personEmployment.setOccupationCodeLevel2(null);
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>occupationCodeLevel2 must be supplied when occupationCodeLevel1 is set</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "CR8.18", 
			given = "a child object personFeature is present", 
			when = "personFeature.featureType == null", 
			then = "Validation error should be returned")
	public void testEventWhenPersonIsLinkedAndFeatureFeatureTypeNotSet()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof PersonDto) {
					PersonDto person = (PersonDto) toPoleObject;
					PersonFeatureDto personFeature = person.getFeatures().get(0);
					personFeature.setFeatureType(null);
					personFeature.setBodyPart(null);
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>featureType must have a value</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "CR8.19", 
			given = "a child object personFeature is present", 
			when = "personFeature.featureType is supplied and personFeature.bodyPart == null", 
			then = "Validation error should be returned")
	public void testEventWhenPersonIsLinkedAndFeatureFeatureTypeSetBodyPartNotSet()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof PersonDto) {
					PersonDto person = (PersonDto) toPoleObject;
					PersonFeatureDto personFeature = person.getFeatures().get(0);
					personFeature.setBodyPart(null);
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>bodyPart must be supplied when featureType is set</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "CR8.20", 
			given = "a child object personFeaturePosition is present", 
			when = "the above has been validated and personFeaturePosition.bodyLaterality == null", 
			then = "Validation error should be returned")
	public void testEventWhenPersonIsLinkedAndFeaturePositionBodyLateralityNotSet()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof PersonDto) {
					PersonDto person = (PersonDto) toPoleObject;
					PersonFeatureDto personFeature = person.getFeatures().get(0);
					personFeature.getFeaturePositions().get(0).setBodyLaterality(null);
					personFeature.setBodyPart(null);
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>bodyLaterality must have a value</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "CR8.24", 
			given = "a Person Object is linked to an Event or an Object", 
			when = "person.dateOfBirth > current date", 
			then = "Validation error should be returned")
	public void testEventWhenPersonIsLinkedAndDobGreaterThanCurrentDate()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof PersonDto) {
					PersonDto person = (PersonDto) toPoleObject;
					person.setDateOfBirth(new PoleDate().addDays(1));
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>dateOfBirth cannot be greater than todays date</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "CR8.25", 
			given = "a Person Object is linked to an Event or an Object", 
			when = "person.dateOfDeath > current date", 
			then = "Validation error should be returned")
	public void testEventWhenPersonIsLinkedAndDateOfDeathGreaterThanCurrentDate()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof PersonDto) {
					PersonDto person = (PersonDto) toPoleObject;
					person.setDateOfDeath(new PoleDate().addDays(1));
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>dateOfDeath cannot be greater than todays date</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "CR8.26", 
			given = "	a child object personNimMarker is supplied on a person object", 
			when = "personNimMarker.startDate > personNimMarker.endDate", 
			then = "Validation error should be returned")
	public void testEventWhenPersonIsLinkedAndNimMarkerStartDateGreaterThanEndDate()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof PersonDto) {
					PersonDto person = (PersonDto) toPoleObject;
					person.getNimMarkers().get(0).setEndDate(new PoleDate().addDays(-5));
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>startDate cannot be greater than endDate</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "CR8.27", 
			given = "a child object personEmployment is present", 
			when = "personEmployment.startDate > current date", 
			then = "Validation error should be returned")
	public void testEventWhenPersonIsLinkedAndEmploymentStartDateGreaterThanCurrentDate()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof PersonDto) {
					PersonDto person = (PersonDto) toPoleObject;
					person.getEmployments().get(0).setStartDate(new PoleDate().addDays(1));
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>startDate cannot be greater than todays date</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "CR8.28", 
			given = "a child object personEmployment is present", 
			when = "personEmployment.startDate > personEmployment.endDate", 
			then = "Validation error should be returned")
	public void testEventWhenPersonIsLinkedAndEmploymentStartDateGreaterThanEndDate()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof PersonDto) {
					PersonDto person = (PersonDto) toPoleObject;
					person.getEmployments().get(0).setEndDate(new PoleDate().addDays(-5));
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>startDate cannot be greater than endDate</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "CR8.30", 
			given = "a child object personPassport is present", 
			when = "personPassport.passportNumber is supplied and personPassport.countryOfIssue == null", 
			then = "Validation error should be returned")
	public void testEventWhenPersonIsLinkedAndPassportNumberSetCountryOfIssueNotSet()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof PersonDto) {
					PersonDto person = (PersonDto) toPoleObject;
					person.getPassports().get(0).setCountryOfIssue(null);
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>countryOfIssue must be supplied when passportNumber is set</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "TODO", 
			given = "a child object personPassport is present", 
			when = "personPassport.passportNumber not set and personPassport.countryOfIssue == null", 
			then = "No validation error should be returned")
	public void testEventWhenPersonIsLinkedAndPassportNumberNotSetCountryOfIssueNotSet()
			throws Exception {
		PoleObjectDto poleObject = createPoleObjectDto();
		for (LinkDto linkDto : poleObject.getLinks()) {
			PoleObjectDto toPoleObject = linkDto.getToPoleObject();
			if (toPoleObject instanceof PersonDto) {
				PersonDto person = (PersonDto) toPoleObject;
				person.getPassports().get(0).setPassportNumber(null);
				person.getPassports().get(0).setCountryOfIssue(null);
				break;
			}
		}
		doCreateEventObject(true, poleObject);
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "CR8.31", 
			given = "a child object personPassport is present", 
			when = "personPassport.issueDate > current date", 
			then = "Validation error should be returned")
	public void testEventWhenPersonIsLinkedAndPassportIssueDateGreaterThanCurrentDate()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof PersonDto) {
					PersonDto person = (PersonDto) toPoleObject;
					person.getPassports().get(0).setIssueDate(new PoleDate().addDays(2));
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>issueDate cannot be greater than todays date</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "CR8.32", 
			given = "a child object personCourtOrder is present", 
			when = "personCourtOrder.validFromDate > personCourtOrder.expiryDate", 
			then = "Validation error should be returned")
	public void testEventWhenPersonIsLinkedAndCourtOrderValidFromDateGreaterThanExpiryDate()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof PersonDto) {
					PersonDto person = (PersonDto) toPoleObject;
					person.getCourtOrders().get(0).setValidFromDate(new PoleDate());
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>validFromDate cannot be greater than expiryDate</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "CR8.33", 
			given = "a Person Object is linked to an Event or an Object", 
			when = "person.ageFrom > 3 digits", 
			then = "Validation error should be returned")
	public void testEventWhenPersonIsLinkedAndAgeFromGreaterThan3CharactersInLength()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof PersonDto) {
					PersonDto person = (PersonDto) toPoleObject;
					person.setAgeFrom(new Integer(1000));
					person.setAgeTo(new Integer(1001));
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>ageFrom is too long</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "CR8.34", 
			given = "a Person Object is linked to an Event or an Object", 
			when = "person.ageFrom is negative", 
			then = "Validation error should be returned")
	public void testEventWhenPersonIsLinkedAndAgeFromIsNegative()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof PersonDto) {
					PersonDto person = (PersonDto) toPoleObject;
					person.setAgeFrom(new Integer(-1));
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>ageFrom must be greater than zero</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "CR8.34", 
			given = "a Person Object is linked to an Event or an Object", 
			when = "person.ageFrom is zero", 
			then = "Validation error should be returned")
	public void testEventWhenPersonIsLinkedAndAgeFromIsZero()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof PersonDto) {
					PersonDto person = (PersonDto) toPoleObject;
					person.setAgeFrom(new Integer(0));
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>ageFrom must be greater than zero</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "CR8.35", 
			given = "a Person Object is linked to an Event or an Object", 
			when = "person.ageFrom > ageTo", 
			then = "Validation error should be returned")
	public void testEventWhenPersonIsLinkedAndAgeFromIsGreaterThanAgeTo()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof PersonDto) {
					PersonDto person = (PersonDto) toPoleObject;
					person.setAgeFrom(new Integer(21));
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>ageFrom cannot be greater than ageTo</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "CR8.29", 
			given = "a child object personDrivingLicence is present", 
			when = "personDrivingLicence.licenceNumber is not supplied as per acceptable format	", 
			then = "Validation error should be returned")
	public void testEventWhenPersonIsLinkedAndDrivingLicenceNumberIncorrectFormatNoCountryOfIssue()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof PersonDto) {
					PersonDto person = (PersonDto) toPoleObject;
					person.getDrivingLicences().get(0).setCountryOfIssue(null);
					person.getDrivingLicences().get(0).setLicenceNumber("ABC99901010A95RT");
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>Driving Licence Number in incorrect format</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "TODO", 
			given = "a child object personDrivingLicence is present", 
			when = "personDrivingLicence.licenceNumber is not supplied as per acceptable format	and "
					+ "country of issue is not set", 
			then = "Validation error should be returned")
	public void testEventWhenPersonIsLinkedAndDrivingLicenceNumberIncorrectFormat()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof PersonDto) {
					PersonDto person = (PersonDto) toPoleObject;
					person.getDrivingLicences().get(0).setLicenceNumber("ABC99901010A95RT");
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>Driving Licence Number in incorrect format</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "CR8.29", 
			given = "a child object personDrivingLicence is present", 
			when = "personDrivingLicence.licenceNumber is not supplied as per acceptable format	", 
			then = "Validation error should be returned")
	public void testEventWhenPersonIsLinkedAndDrivingLicenceNumberLessThan16Characters()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof PersonDto) {
					PersonDto person = (PersonDto) toPoleObject;
					person.getDrivingLicences().get(0).setLicenceNumber("ABC9990195RT");
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>Driving Licence Number in incorrect format</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "CR8.29", 
			given = "a child object personDrivingLicence is present", 
			when = "personDrivingLicence.licenceNumber is not supplied as per acceptable format	", 
			then = "Validation error should be returned")
	public void testEventWhenPersonIsLinkedAndDrivingLicenceNumberTooLong()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof PersonDto) {
					PersonDto person = (PersonDto) toPoleObject;
					PersonDrivingLicenceDto personDrivingLicence = person.getDrivingLicences().get(0);
					personDrivingLicence.setLicenceNumber(personDrivingLicence.getLicenceNumber() + "ABC123");
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>Driving Licence Number in incorrect format</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "TODO", 
			given = "a child object personDrivingLicence is present", 
			when = "personDrivingLicence.licenceNumber contains obliques in the correct places", 
			then = "No validation error should be returned")
	public void testEventWhenPersonIsLinkedAndDrivingLicenceNumberContainsObliques()
			throws Exception {
		PoleObjectDto poleObject = createPoleObjectDto();
		for (LinkDto linkDto : poleObject.getLinks()) {
			PoleObjectDto toPoleObject = linkDto.getToPoleObject();
			if (toPoleObject instanceof PersonDto) {
				PersonDto person = (PersonDto) toPoleObject;
				String licenceNumber = person.getDrivingLicences().get(0).getLicenceNumber();
				StringBuffer licenceNumberOblique = new StringBuffer();
				licenceNumberOblique.append(licenceNumber.substring(0, 5));
				licenceNumberOblique.append("/");
				licenceNumberOblique.append(licenceNumber.substring(5, 11));
				licenceNumberOblique.append("/");
				licenceNumberOblique.append(licenceNumber.substring(11));
				person.getDrivingLicences().get(0).setLicenceNumber(licenceNumberOblique.toString());
				break;
			}
		}
		doCreateEventObject(true, poleObject);
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "TODO", 
			given = "a child object personDrivingLicence is present", 
			when = "personDrivingLicence.licenceNumber is supplied  and persons surname is not supplied", 
			then = "Validation error should be returned")
	public void testEventWhenPersonIsLinkedAndDrivingLicenceNumberNoSurnameSet()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof PersonDto) {
					PersonDto person = (PersonDto) toPoleObject;
					person.setSurname(null);
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>Person surname needs to have a value to validate Driving Licence Number</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "TODO", 
			given = "a child object personDrivingLicence is present", 
			when = "personDrivingLicence.licenceNumber is supplied  and persons dob is not supplied", 
			then = "Validation error should be returned")
	public void testEventWhenPersonIsLinkedAndDrivingLicenceNumberNoDobSet()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof PersonDto) {
					PersonDto person = (PersonDto) toPoleObject;
					person.setDateOfBirth(null);
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>Person date of birth needs to have a value to validate Driving Licence Number</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "TODO", 
			given = "a child object personDrivingLicence is present", 
			when = "personDrivingLicence.licenceNumber is not supplied as per acceptable format but is not a uk licence", 
			then = "No validation error should be returned")
	public void testEventWhenPersonIsLinkedAndDrivingLicenceNumberIncorrectFormatNotUk()
			throws Exception {
		PoleObjectDto poleObject = createPoleObjectDto();
		for (LinkDto linkDto : poleObject.getLinks()) {
			PoleObjectDto toPoleObject = linkDto.getToPoleObject();
			if (toPoleObject instanceof PersonDto) {
				PersonDto person = (PersonDto) toPoleObject;
				person.getDrivingLicences().get(0).setLicenceNumber("ABC99901010A95RT");
				person.getDrivingLicences().get(0).setCountryOfIssue("UNITED STATES");
				break;
			}
		}
		doCreateEventObject(true, poleObject);
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "TODO", 
			given = "a child object personDrivingLicence is present", 
			when = "personDrivingLicence.licenceNumber is supplied as per acceptable format for a female", 
			then = "No validation error should be returned")
	public void testEventWhenPersonIsLinkedAndDrivingLicenceNumberCorrectForFemale()
			throws Exception {
		PoleObjectDto poleObject = createPoleObjectDto();
		for (LinkDto linkDto : poleObject.getLinks()) {
			PoleObjectDto toPoleObject = linkDto.getToPoleObject();
			if (toPoleObject instanceof PersonDto) {
				PersonDto person = (PersonDto) toPoleObject;
				person.setGender("2");
				StringBuffer paddedSurname = new StringBuffer();
				if (person.getSurname().length() > 5) {
					paddedSurname.append(person.getSurname().substring(0, 5).toUpperCase());
				} else {
					paddedSurname.append(person.getSurname().toUpperCase());
					while (paddedSurname.length() < 5) {
						paddedSurname.append("9");
					}
				}
				char forenameChar = person.getForename1().toUpperCase().charAt(0);
				person.getDrivingLicences().get(0).setLicenceNumber(paddedSurname + "853121" + forenameChar + forenameChar + "7AB");
				break;
			}
		}
		doCreateEventObject(true, poleObject);
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "CR8.21", 
			given = "a Person Object is linked to an Event or an Object", 
			when = "person.pncId is supplied is not as per acceptable format", 
			then = "Validation error should be returned")
	public void testEventWhenPersonIsLinkedAndPncIdInvalid()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof PersonDto) {
					PersonDto person = (PersonDto) toPoleObject;
					person.setPncId("pncId");
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>pncId is not in the correct format</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "CR8.21", 
			given = "a Person Object is linked to an Event or an Object", 
			when = "person.pncId is supplied is not as per acceptable format", 
			then = "Validation error should be returned")
	public void testEventWhenPersonIsLinkedAndPncIdInvalidLeadingZero()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof PersonDto) {
					PersonDto person = (PersonDto) toPoleObject;
					person.setPncId("1234/05X");
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>pncId is not in the correct format</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "CR8.21", 
			given = "a Person Object is linked to an Event or an Object", 
			when = "person.pncId is supplied is not as per acceptable format", 
			then = "Validation error should be returned")
	public void testEventWhenPersonIsLinkedAndPncIdInvalidIncorrectCheckChar()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof PersonDto) {
					PersonDto person = (PersonDto) toPoleObject;
					person.setPncId("1234/5O");
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>pncId is not in the correct format</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "CR8.22", 
			given = "a Person Object is linked to an Event or an Object", 
			when = "person.croId is supplied is not as per acceptable format", 
			then = "Validation error should be returned")
	public void testEventWhenPersonIsLinkedAndCroIdInvalid()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof PersonDto) {
					PersonDto person = (PersonDto) toPoleObject;
					person.setCroId("SF22/023Z");
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>croId is not in the correct format</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "CR8.22", 
			given = "a Person Object is linked to an Event or an Object", 
			when = "person.croId is supplied is not as per acceptable format", 
			then = "Validation error should be returned")
	public void testEventWhenPersonIsLinkedAndCroIdInvalidSFYearLessThan39()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof PersonDto) {
					PersonDto person = (PersonDto) toPoleObject;
					person.setCroId("SF37/23Z");
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>croId is not in the correct format</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "CR8.22", 
			given = "a Person Object is linked to an Event or an Object", 
			when = "person.croId is supplied is not as per acceptable format", 
			then = "Validation error should be returned")
	public void testEventWhenPersonIsLinkedAndCroIdInvalidSFYearGreaterThan95()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof PersonDto) {
					PersonDto person = (PersonDto) toPoleObject;
					person.setCroId("SF97/23Z");
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>croId is not in the correct format</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "CR8.22", 
			given = "a Person Object is linked to an Event or an Object", 
			when = "person.croId is supplied is not as per acceptable format", 
			then = "Validation error should be returned")
	public void testEventWhenPersonIsLinkedAndCroIdInvalidLeadingZeroForSF()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof PersonDto) {
					PersonDto person = (PersonDto) toPoleObject;
					person.setCroId("SF91/023Z");
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>croId is not in the correct format</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "CR8.22", 
			given = "a Person Object is linked to an Event or an Object", 
			when = "person.croId is supplied is not as per acceptable format", 
			then = "Validation error should be returned")
	public void testEventWhenPersonIsLinkedAndCroIdInvalidLeadingZero()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof PersonDto) {
					PersonDto person = (PersonDto) toPoleObject;
					person.setCroId("023/91A");
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>croId is not in the correct format</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "UNKNOWN", 
			given = "a Person Object is linked to an Event or an Object", 
			when = "person.croId is supplied is as per acceptable format for non SF", 
			then = "No validation error should be returned")
	public void testEventWhenPersonIsLinkedAndCroIdCorrectNonSf()
			throws Exception {
		PoleObjectDto poleObject = createPoleObjectDto();
		for (LinkDto linkDto : poleObject.getLinks()) {
			PoleObjectDto toPoleObject = linkDto.getToPoleObject();
			if (toPoleObject instanceof PersonDto) {
				PersonDto person = (PersonDto) toPoleObject;
				person.setCroId("23/91T");
				break;
			}
		}
		doCreateEventObject(true, poleObject);
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "CR8.22", 
			given = "a Person Object is linked to an Event or an Object", 
			when = "person.croId is supplied is not as per acceptable format", 
			then = "Validation error should be returned")
	public void testEventWhenPersonIsLinkedAndCroIdInvalidIncorrectCheckChar()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof PersonDto) {
					PersonDto person = (PersonDto) toPoleObject;
					person.setCroId("SF91/23I");
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>croId is not in the correct format</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "CR8.36,CR8.37,CR8.38,CR8.39,CR8.40,CR8.41,CR8.42,CR8.43,CR8.44,CR8.45", 
			given = "a Person Object is linked to an Event or an Object", 
			when = "personGp child object is supplied and personNomsId child object is supplied and"
					+ "personFeatureKeyword child object is supplied and personDisability child object is supplied and"
					+ "personDisabilityNoted child object is supplied and personNationality child object is supplied and"
					+ "personDressItem child object is supplied and personPermit child object is supplied and"
					+ "personModusOperandi child object is supplied and personHairDetail child object is supplied", 
			then = "No validation error should be returned")
	public void testEventWhenPersonIsLinkedAndNomsSupplied()
			throws Exception {
		clientName = "guardian";
		PoleObjectDto poleObject = createPoleObjectDto();
		for (LinkDto linkDto : poleObject.getLinks()) {
			PoleObjectDto toPoleObject = linkDto.getToPoleObject();
			if (toPoleObject instanceof PersonDto) {
				PersonDto person = (PersonDto) toPoleObject;
				PersonNomsIdDto personNoms = new PersonNomsIdDto();
				personNoms.setModificationStatus(ModificationStatusDto.CREATE);
				personNoms.setNomsId("1234");
				person.addNomsId(personNoms);
				break;
			}
		}
		doCreateEventObject(true, poleObject);
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "TODO", 
			given = "a Person Object is linked to an Event or an Object", 
			when = "personNomsId child object is supplied and the clientName is not guardian", 
			then = "Validation error should be returned")
	public void testEventWhenPersonIsLinkedAndNomsSuppliedWithIncorrectClientName()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof PersonDto) {
					PersonDto person = (PersonDto) toPoleObject;
					PersonNomsIdDto personNoms = new PersonNomsIdDto();
					personNoms.setModificationStatus(ModificationStatusDto.CREATE);
					personNoms.setNomsId("1234");
					person.addNomsId(personNoms);
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>Unexpected child object"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "CR8.49", 
			given = "a child personDressItem is present", 
			when = "typeLevel1 == null", 
			then = "Validation error should be returned")
	public void testEventWhenPersonIsLinkedAndDressItemTypeLevel1NotSet()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof PersonDto) {
					PersonDto person = (PersonDto) toPoleObject;
					PersonDressItemDto personDressItem = person.getDressItems().get(0);
					personDressItem.setTypeLevel1(null);
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>typeLevel1 must have a value</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "UNKNOWN", 
			given = "a child personHairDetail is present", 
			when = "hairDyedGreying is set to Dyed, and hairDyedColour doesnt have a value", 
			then = "Validation error should be returned")
	public void testEventWhenPersonIsLinkedAndHairDetailRequiresHairDyedColourNotSet()
			throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof PersonDto) {
					PersonDto person = (PersonDto) toPoleObject;
					PersonHairDetailDto personHairDetail = person.getHairDetails().get(0);
					personHairDetail.setHairDyedGreying("1");
					break;
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
					ide.getMessage().contains("error>hairDyedColour must have a value</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "UNKNOWN", 
			given = "a child personHairDetail is present", 
			when = "hairDyedGreying is set to Dyed, and hairDyedColour does have a value", 
			then = "No validation error should be returned")
	public void testEventWhenPersonIsLinkedAndHairDetailRequiresHairDyedColourSet()
			throws Exception {
		PoleObjectDto poleObject = createPoleObjectDto();
		for (LinkDto linkDto : poleObject.getLinks()) {
			PoleObjectDto toPoleObject = linkDto.getToPoleObject();
			if (toPoleObject instanceof PersonDto) {
				PersonDto person = (PersonDto) toPoleObject;
				PersonHairDetailDto personHairDetail = person.getHairDetails().get(0);
				personHairDetail.setHairDyedGreying("1");
				personHairDetail.setHairDyedColour("1");
				break;
			}
		}
		doCreateEventObject(true, poleObject);
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "UNKNOWN", 
			given = "a child personHairDetail is present", 
			when = "hairDyedGreying is set to Greying, and hairDyedColour doesnt have a value", 
			then = "No validation error should be returned")
	public void testEventWhenPersonIsLinkedAndHairDetailHairDyedColourNotSetBecauseGrey()
			throws Exception {
		PoleObjectDto poleObject = createPoleObjectDto();
		for (LinkDto linkDto : poleObject.getLinks()) {
			PoleObjectDto toPoleObject = linkDto.getToPoleObject();
			if (toPoleObject instanceof PersonDto) {
				PersonDto person = (PersonDto) toPoleObject;
				PersonHairDetailDto personHairDetail = person.getHairDetails().get(0);
				personHairDetail.setHairDyedGreying("2");
				break;
			}
		}
		doCreateEventObject(true, poleObject);
	}

	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=215,
		mingleRef = 37106, 
		mingleTitle = "WP-215 (#37106): No validation on entering numeric and special characters in Person's name and its Alias name", 
		acceptanceCriteriaRefs = "UNKNOWN", 
		given = "a child person Alias is present", 
		when = "surname, forname1, forename2, forename3 is containing special character", 
		then = "Validation error should be returned")
	public void testEventWhenPersonAliasWithSpecialCharacterInSurnameAndForename() throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof PersonDto) {
					PersonDto person = (PersonDto) toPoleObject;
					PersonAliasDto personAlias = person.getAliases().get(0);
					personAlias.setSurname("Smith9");
					personAlias.setForename1("Sa\"m");
					personAlias.setForename2("Mar_k");
					personAlias.setForename3("Bell#");	
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
				ide.getMessage().contains("error>forename1 is not valid for pattern ^[A-Za-z\\-\\'\\s\\.]+$</"));
			assertTrue("ValidationException: " + ide.getMessage(),
				ide.getMessage().contains("error>forename2 is not valid for pattern ^[A-Za-z\\-\\'\\s\\.]+$</"));
			assertTrue("ValidationException: " + ide.getMessage(),
				ide.getMessage().contains("error>forename3 is not valid for pattern ^[A-Za-z\\-\\'\\s\\.]+$</"));
			assertTrue("ValidationException: " + ide.getMessage(),
				ide.getMessage().contains("error>surname is not valid for pattern ^[A-Za-z\\-\\'\\s\\.]+$</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
		workPackage=215,
		mingleRef = 37106, 
		mingleTitle = "WP-215 (#37106): No validation on entering numeric and special characters in Person's name and its Alias name", 
		acceptanceCriteriaRefs = "UNKNOWN", 
		given = "a person is present", 
		when = "surname, forname1, forename2, forename3 is containing special character", 
		then = "Validation error should be returned")
	public void testEventWhenPersonWithSpecialCharacterInSurnameAndForename() throws Exception {
		try {
			PoleObjectDto poleObject = createPoleObjectDto();
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof PersonDto) {
					PersonDto person = (PersonDto) toPoleObject;
					person.setSurname("Smith9");
					person.setForename1("Sa\"m");
					person.setForename2("Mar_k");
					person.setForename3("Bell#");	
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(),
				ide.getMessage().contains("error>forename1 is not valid for pattern ^[A-Za-z\\-\\'\\s\\.]+$</"));
			assertTrue("ValidationException: " + ide.getMessage(),
				ide.getMessage().contains("error>forename2 is not valid for pattern ^[A-Za-z\\-\\'\\s\\.]+$</"));
			assertTrue("ValidationException: " + ide.getMessage(),
				ide.getMessage().contains("error>forename3 is not valid for pattern ^[A-Za-z\\-\\'\\s\\.]+$</"));
			assertTrue("ValidationException: " + ide.getMessage(),
				ide.getMessage().contains("error>surname is not valid for pattern ^[A-Za-z\\-\\'\\s\\.]+$</"));
		}
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "TODO", 
			given = "a Person Object is linked to an Event or an Object", 
			when = "person.croId is supplied is per acceptable format", 
			then = "No validation error should be returned")
	public void testEventWhenPersonIsLinkedAndCroIdValidExample1()
			throws Exception {
		PoleObjectDto poleObject = createPoleObjectDto();
		for (LinkDto linkDto : poleObject.getLinks()) {
			PoleObjectDto toPoleObject = linkDto.getToPoleObject();
			if (toPoleObject instanceof PersonDto) {
				PersonDto person = (PersonDto) toPoleObject;
				person.setCroId("444471/92V");
				break;
			}
		}
		doCreateEventObject(true, poleObject);
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "TODO", 
			given = "a Person Object is linked to an Event or an Object", 
			when = "person.croId is supplied is per acceptable format", 
			then = "No validation error should be returned")
	public void testEventWhenPersonIsLinkedAndCroIdValidExample2()
			throws Exception {
		PoleObjectDto poleObject = createPoleObjectDto();
		for (LinkDto linkDto : poleObject.getLinks()) {
			PoleObjectDto toPoleObject = linkDto.getToPoleObject();
			if (toPoleObject instanceof PersonDto) {
				PersonDto person = (PersonDto) toPoleObject;
				person.setCroId("44/92X");
				break;
			}
		}
		doCreateEventObject(true, poleObject);
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "TODO", 
			given = "a Person Object is linked to an Event or an Object", 
			when = "person.croId is supplied is per acceptable format", 
			then = "No validation error should be returned")
	public void testEventWhenPersonIsLinkedAndSFCroIdValidExample1()
			throws Exception {
		PoleObjectDto poleObject = createPoleObjectDto();
		for (LinkDto linkDto : poleObject.getLinks()) {
			PoleObjectDto toPoleObject = linkDto.getToPoleObject();
			if (toPoleObject instanceof PersonDto) {
				PersonDto person = (PersonDto) toPoleObject;
				person.setCroId("SF95/999999F");
				break;
			}
		}
		doCreateEventObject(true, poleObject);
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "TODO", 
			given = "a Person Object is linked to an Event or an Object", 
			when = "person.croId is supplied is per acceptable format", 
			then = "No validation error should be returned")
	public void testEventWhenPersonIsLinkedAndSFCroIdValidExample2()
			throws Exception {
		PoleObjectDto poleObject = createPoleObjectDto();
		for (LinkDto linkDto : poleObject.getLinks()) {
			PoleObjectDto toPoleObject = linkDto.getToPoleObject();
			if (toPoleObject instanceof PersonDto) {
				PersonDto person = (PersonDto) toPoleObject;
				person.setCroId("SF95/499D");
				break;
			}
		}
		doCreateEventObject(true, poleObject);
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "TODO", 
			given = "a Person Object is linked to an Event or an Object", 
			when = "person.croId is supplied is per acceptable format", 
			then = "No validation error should be returned")
	public void testEventWhenPersonIsLinkedAndSFCroIdValidExample3()
			throws Exception {
		PoleObjectDto poleObject = createPoleObjectDto();
		for (LinkDto linkDto : poleObject.getLinks()) {
			PoleObjectDto toPoleObject = linkDto.getToPoleObject();
			if (toPoleObject instanceof PersonDto) {
				PersonDto person = (PersonDto) toPoleObject;
				person.setCroId("SF61/654321T");
				break;
			}
		}
		doCreateEventObject(true, poleObject);
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "TODO", 
			given = "a Person Object is linked to an Event or an Object", 
			when = "person.pncId is supplied is per acceptable format", 
			then = "No validation error should be returned")
	public void testEventWhenPersonIsLinkedAndPncIdValidExample1()
			throws Exception {
		PoleObjectDto poleObject = createPoleObjectDto();
		for (LinkDto linkDto : poleObject.getLinks()) {
			PoleObjectDto toPoleObject = linkDto.getToPoleObject();
			if (toPoleObject instanceof PersonDto) {
				PersonDto person = (PersonDto) toPoleObject;
				person.setPncId("2000/1234Q");
				break;
			}
		}
		doCreateEventObject(true, poleObject);
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 37106, 
			mingleTitle = "WP215 - Bus Svcs - Validation Framework - Links to Person Object", 
			acceptanceCriteriaRefs = "TODO", 
			given = "a Person Object is linked to an Event or an Object", 
			when = "person.pncId is supplied is per acceptable format", 
			then = "No validation error should be returned")
	public void testEventWhenPersonIsLinkedAndPncIdValidExample2()
			throws Exception {
		PoleObjectDto poleObject = createPoleObjectDto();
		for (LinkDto linkDto : poleObject.getLinks()) {
			PoleObjectDto toPoleObject = linkDto.getToPoleObject();
			if (toPoleObject instanceof PersonDto) {
				PersonDto person = (PersonDto) toPoleObject;
				person.setPncId("95/1234567Q");
				break;
			}
		}
		doCreateEventObject(true, poleObject);
	}
	
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=215,
			mingleRef = 38055, 
			mingleTitle = "WP-215(#37106): #37106: No validation is set between featureType, bodyPart, bodyLaterality and featureKeywordType", 
			acceptanceCriteriaRefs = "TODO", 
			given = "a Person Object is linked to an Event or an Object", 
			when = "Feature detail is supplied and keyword is not matching the combination of cv list ", 
			then = "No validation error should be returned")
	public void testEventWhenPersonIsLinkedAndSupplyFeatureKeywordsWithInvalidCombination()
			throws Exception {
		PoleObjectDto poleObject = createPoleObjectDto();
		try {
			for (LinkDto linkDto : poleObject.getLinks()) {
				PoleObjectDto toPoleObject = linkDto.getToPoleObject();
				if (toPoleObject instanceof PersonDto) {
					PersonDto person = (PersonDto) toPoleObject;
					PersonFeatureDto personFeature = new PersonFeatureDto();
					personFeature.setModificationStatus(ModificationStatusDto.CREATE);
					personFeature.setFeatureType("MARK");
					personFeature.setBodyPart("ABDOMEN");
					person.addFeature(personFeature);
					PersonFeatureKeywordDto personFeatureKeyword = new PersonFeatureKeywordDto();
					personFeatureKeyword.setModificationStatus(ModificationStatusDto.CREATE);
					personFeatureKeyword.setFeatureKeywordType("1");
					personFeature.addFeatureKeyword(personFeatureKeyword);
				}
			}
			doCreateEventObject(true, poleObject);
			fail("Expected an exception for invalid data");
		} catch (InvalidDataException ide) {
			assertTrue("ValidationException: " + ide.getMessage(), ide.getMessage().contains(
					"error>keywordType 1 not maching the value with PERSON_FEATURE_KEYWORD_MARK</"));
		}
	}
	
	/**
	 * An example of how tests for validation rules could be sped up just to test the specific rule we are
	 * interested in.
	 * 
	 * The reason there are two tests is, because if you run it one, it can looks slower than the other tests.
	 * This is because its using its own validator with its own caches, which arent populated.  Once the caches
	 * are populated as they would be by the normal test run, the test runs very quickly.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPersonWhenCroIdInvalidExampleForSimplerTest() throws Exception {
		try {
			PersonDto person = CommonPoleObjectTestUtils.getPerson(new Integer(-1), "Bloggs", "Fred");
			person.setCroId("SF22/023Z");
			
			PutPoleObjectsRequestDto putPoleObjectsRequest = new PutPoleObjectsRequestDto();
			PoleDtoUtils.addBusinessServiceInfo(putPoleObjectsRequest,
					BusinessServicesTestUtils.createBusinessServiceInfo(securityContextId));
			putPoleObjectsRequest.addPoleObject(person);
			
			Map<EntityKey, PoleEntityDto> originalObjectsMap = new HashMap<EntityKey, PoleEntityDto>();
			validator.validate(putPoleObjectsRequest, originalObjectsMap);
			fail("Expected a validation exception");
		} catch (ValidationException ve) {
			assertTrue("ValidationException: " + ValidationException.convertToXml(ve),
				ValidationException.convertToXml(ve).contains("error>croId is not in the correct format</"));
		}
	}
	
	@Test
	public void testPersonWhenCroIdInvalidExampleForSimplerTestTwo() throws Exception {
		testPersonWhenCroIdInvalidExampleForSimplerTest();
	}
}
