package com.northgateis.gem.bussvc.submitincident.functest;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.junit.Test;
import com.northgateis.gem.bussvc.AbstractBusinessServicesFunctionalTestBase;
import com.northgateis.gem.bussvc.framework.FrameworkServiceConstants;
import com.northgateis.gem.bussvc.framework.test.BusSvcStoryAcceptanceCriteriaReference;
import com.northgateis.gem.bussvc.pole.auxiliarydata.systemparams.SystemParamsCacheBean;
import com.northgateis.gem.bussvc.submitincident.utils.IncidentPoleDtoUtils;
import com.northgateis.gem.bussvc.test.util.BusinessServicesTestUtils;
import com.northgateis.pole.common.InvalidDataException;
import com.northgateis.pole.common.PoleDate;
import com.northgateis.pole.common.PoleNames;
import com.northgateis.pole.common.PoleTime;
import com.northgateis.pole.schema.AuxiliaryDataCriteriaDto;
import com.northgateis.pole.schema.CreateIncidentTxDataDto;
import com.northgateis.pole.schema.GetAuxiliaryDataRequestDto;
import com.northgateis.pole.schema.GetAuxiliaryDataResponseDto;
import com.northgateis.pole.schema.IncidentDto;
import com.northgateis.pole.schema.PoleObjectDto;
import com.northgateis.pole.schema.PutPoleObjectsRequestDto;
import com.northgateis.pole.schema.PutPoleObjectsResponseDto;
import com.northgateis.pole.schema.StringCriterionDto;
import com.northgateis.pole.schema.SystemParametersDto;

/**
 * Functional tests for Creating Incident and check validation CCI #35448 
 * 
 * WARNING: Acceptance criteria has not been written properly in mingle,
 * so this test is using its own acceptance criteria. 
 * 
 */
public class CreateIncidentFromTimeToTimeValidationFuncTest extends AbstractBusinessServicesFunctionalTestBase {
	private static final String INVESTIGATION_TIME_FIELDS_SYSTEM_PARAMETER_NAME = "INVESTIGATION_AND_CONTACT_EVENT_TIME_FIELDS_MANDATORY_ON_AND_AFTER_DATE";
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	private SystemParamsCacheBean systemParametersCache;

	private IncidentDto incidentDto;

	
	@Override
	protected void setupImpl() throws Exception {		
		incidentDto = utils.createIncident(-1, Integer.valueOf(officerReportingId));
		CreateIncidentTxDataDto txd = incidentDto.getIncidentTxData().getCreateIncidentTxData();
		txd.setCompleted(true);
		systemParametersCache = bsContext.getBean(SystemParamsCacheBean.class);

	}

	

	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=74,
			mingleRef = 35488, 
			mingleTitle = "WMP -- WP74 - Investigation Event Start and End Dates and Times v3", 
			acceptanceCriteriaRefs = "myCR1.1", 
			given = "An investigation that is going to be created, the fromTime and toDate and toTime are not populate."
					+ "The INVESTIGATION_AND_CONTACT_EVENT_TIME_FIELDS_MANDATORY_ON_AND_AFTER_DATE has been"
					+ "set up to a future date.", 
			when = "The incident is summitted to be created",
			then = "The incident is created, the fields not populated are not mandatory as the date in the system parameter"
					+ "is in the future.")
	public void testFutureSystemParameterRulesNoAppliedTimeNotPopulated() throws Exception{
		addInvestigationTimeToSystemParameterCache(getDate(2));
		doSummitIncident(incidentDto);
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=74,
			mingleRef = 35488, 
			mingleTitle = "WMP -- WP74 - Investigation Event Start and End Dates and Times v3", 
			acceptanceCriteriaRefs = "myCR1.2", 
			given = "An investigation that is going to be created, the fromTime and toDate and toTime are not populate."
					+ "The INVESTIGATION_AND_CONTACT_EVENT_TIME_FIELDS_MANDATORY_ON_AND_AFTER_DATE has been"
					+ "set up to the current date.", 
			when = "The incident is summitted to be created",
			then = "A BS validation exception is expected, as fromTime field is mandatory.")
	public void testCurrentDateSystemParameterRulesAppliedTimeNotPopulated() throws Exception{
		boolean expectedException = false;
		addInvestigationTimeToSystemParameterCache(getDate(0));
		incidentDto.setIncidentAtOrFromTime(null);
		try {
			doSummitIncident(incidentDto);
		}catch (InvalidDataException ide) {
			logger.error("validation exception: " + ide);
			if (!ide.getMessage().contains("incidentAtOrFromTime")) {
				throw ide;
			} else {
				expectedException = true;
			}
		}
		
		if (!expectedException) {
			fail("An InvalidDataException for the field incidentAtOrFromTime was expected.");
		}	
	}	
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=74,
			mingleRef = 35488, 
			mingleTitle = "WMP -- WP74 - Investigation Event Start and End Dates and Times v3", 
			acceptanceCriteriaRefs = "myCR1.3", 
			given = "An investigation that is going to be created, the fromTime but toDate and toTime are not populate."
					+ "The INVESTIGATION_AND_CONTACT_EVENT_TIME_FIELDS_MANDATORY_ON_AND_AFTER_DATE has been"
					+ "set up to a date in the passed.", 
			when = "The incident is summitted to be created",
			then = "A BS validation exception is expected, as fromTime field is mandatory.")
	public void testPassedDateSystemParameterRulesAppliedTimeNotPopulated() throws Exception{
		boolean expectedException = false;
		addInvestigationTimeToSystemParameterCache(getDate(-2));
		incidentDto.setIncidentAtOrFromTime(null);
		try {
			doSummitIncident(incidentDto);
		}catch (InvalidDataException ide) {
			logger.error("validation exception: " + ide);
			if (!ide.getMessage().contains("incidentAtOrFromTime")) {
				throw ide;
			} else {
				expectedException = true;
			}
		}
		
		if (!expectedException) {
			fail("An InvalidDataException for the field incidentAtOrFromTime was expected.");
		}	
	}		

	/**
	 * Check when the system parameter has been set up to a future date and the rules
	 * shouldn't be applied yet, so fromTime and toTime can be null although the toDate
	 * is populated
	 * 
	 * @throws Exception
	 */
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=74,
			mingleRef = 35488, 
			mingleTitle = "WMP -- WP74 - Investigation Event Start and End Dates and Times v3", 
			acceptanceCriteriaRefs = "myCR2.1", 
			given = "An investigation that is going to be created, the toDate is populated but the fromTime and "
					+ "toTime are not populate."
					+ "The INVESTIGATION_AND_CONTACT_EVENT_TIME_FIELDS_MANDATORY_ON_AND_AFTER_DATE has been"
					+ "set up to a future date.", 
			when = "The incident is summitted to be created",
			then = "The incident is created, the fiels not populated are not mandatory as the date in the system paramter"
					+ "is in the future.")
	public void testFurtureSystemParameterRulesNoAppliedTimeNotPopulatedToDatePopulated() throws Exception{
		addInvestigationTimeToSystemParameterCache(getDate(2));
		incidentDto.setIncidentToDate(new PoleDate());
		doSummitIncident(incidentDto);		
	}	
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=74,
			mingleRef = 35488, 
			mingleTitle = "WMP -- WP74 - Investigation Event Start and End Dates and Times v3", 
			acceptanceCriteriaRefs = "myCR2.2", 
			given = "An investigation that is going to be created, the toDate is populated but the fromTime and "
					+ "toTime are not populate."
					+ "The INVESTIGATION_AND_CONTACT_EVENT_TIME_FIELDS_MANDATORY_ON_AND_AFTER_DATE has been"
					+ "set up to the current date.", 
			when = "The incident is summitted to be created",
			then = "A BS validation exception is expected, as fromTime and toTime field are mandatory.")
	public void testCurrentDateSystemParameterRulesNoAppliedTimeNotPopulatedToDatePopulated() throws Exception{
		boolean expectedException = false;
		addInvestigationTimeToSystemParameterCache(getDate(0));
		incidentDto.setIncidentToDate(new PoleDate());
		try {
			doSummitIncident(incidentDto);
		}catch (InvalidDataException ide) {
			logger.error("validation exception: " + ide);
			if (!ide.getMessage().contains("incidentAtOrFromTime") && !ide.getMessage().contains("incidentToTime")) {
				throw ide;
			} else {
				expectedException = true;
			}
		}
		
		if (!expectedException) {
			fail("An InvalidDataException for the field incidentAtOrFromTime was expected.");
		}	
	}		

	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=74,
			mingleRef = 35488, 
			mingleTitle = "WMP -- WP74 - Investigation Event Start and End Dates and Times v3", 
			acceptanceCriteriaRefs = "myCR2.3", 
			given = "An investigation that is going to be created, the toDate is populated but the fromTime and "
					+ "toTime are not populate."
					+ "The INVESTIGATION_AND_CONTACT_EVENT_TIME_FIELDS_MANDATORY_ON_AND_AFTER_DATE has been"
					+ "set up to a date in the passed.", 
			when = "The incident is summitted to be created",
			then = "A BS validation exception is expected, as fromTime and toTime field are mandatory.")
	public void testFutureDateSystemParameterRulesNoAppliedTimeNotPopulatedToDatePopulated() throws Exception{
		boolean expectedException = false;
		addInvestigationTimeToSystemParameterCache(getDate(-2));
		incidentDto.setIncidentToDate(new PoleDate());
		try {
			doSummitIncident(incidentDto);
		}catch (InvalidDataException ide) {
			logger.error("validation exception: " + ide);
			if (!ide.getMessage().contains("incidentAtOrFromTime") && !ide.getMessage().contains("incidentToTime")) {
				throw ide;
			} else {
				expectedException = true;
			}
		}
		
		if (!expectedException) {
			fail("An InvalidDataException for the field incidentAtOrFromTime and the field incidentToTime was expected.");
		}	
	}		

	
	/**
	 * Test when fromTime is populated, toDate and toTime are not populated, so no validation exception is
	 * expected.
	 * <p>
	 * Precondition: the INVESTIGATION_AND_CONTACT_EVENT_TIME_FIELDS_MANDATORY_ON_AND_AFTER_DATE should
	 * be in the system parameter cache.
	 *  
	 * @throws Exception
	 */
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=74,
			mingleRef = 35488, 
			mingleTitle = "WMP -- WP74 - Investigation Event Start and End Dates and Times v3", 
			acceptanceCriteriaRefs = "myCR3.1", 
			given = "An investigation that is going to be created, the fromTime is pupulated but the "
					+ " toDate and toTime are not populated. "
					+ "The INVESTIGATION_AND_CONTACT_EVENT_TIME_FIELDS_MANDATORY_ON_AND_AFTER_DATE has been"
					+ "set up to a date in the passed.", 
			when = "The incident is summitted to be created",
			then = "Incident should be created.")
	public void testPassedDateSystemParameterDateFromTimePopulatedToDateToTimeNotPopulated() throws Exception {	
		addInvestigationTimeToSystemParameterCache(getDate(-2));
		incidentDto.setIncidentAtOrFromTime(incidentDto.getReportedTime());
		doSummitIncident(incidentDto);
	}
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=74,
			mingleRef = 35488, 
			mingleTitle = "WMP -- WP74 - Investigation Event Start and End Dates and Times v3", 
			acceptanceCriteriaRefs = "myCR3.2", 
			given = "An investigation that is going to be created, the fromTime is pupulated but the "
					+ " toDate and toTime are not populated. "
					+ "The INVESTIGATION_AND_CONTACT_EVENT_TIME_FIELDS_MANDATORY_ON_AND_AFTER_DATE has been"
					+ "set up to the current date.", 
			when = "The incident is summitted to be created",
			then = "Incident should be created.")
	public void testCurrentDateSystemParameterDateFromTimePopulatedToDateToTimeNotPopulated() throws Exception {	
		addInvestigationTimeToSystemParameterCache(getDate(0));
		incidentDto.setIncidentAtOrFromTime(incidentDto.getReportedTime());
		doSummitIncident(incidentDto);
	}	
	
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=74,
			mingleRef = 35488, 
			mingleTitle = "WMP -- WP74 - Investigation Event Start and End Dates and Times v3", 
			acceptanceCriteriaRefs = "myCR3.3", 
			given = "An investigation that is going to be created, the fromTime is pupulated but the "
					+ " toDate and toTime are not populated. "
					+ "The INVESTIGATION_AND_CONTACT_EVENT_TIME_FIELDS_MANDATORY_ON_AND_AFTER_DATE has been"
					+ "set up to a future date.", 
			when = "The incident is summitted to be created",
			then = "Incident should be created.")
	public void testFutureDateSystemParameterDateFromTimePopulatedToDateToTimeNotPopulated() throws Exception {	
		addInvestigationTimeToSystemParameterCache(getDate(2));
		incidentDto.setIncidentAtOrFromTime(incidentDto.getReportedTime());
		doSummitIncident(incidentDto);
	}	
	/**
	 * Test when fromTime and toDate are populated, but toTime is not populated, a validation exception is
	 * expected.
	 * <p>
	 * Precondition: the INVESTIGATION_AND_CONTACT_EVENT_TIME_FIELDS_MANDATORY_ON_AND_AFTER_DATE should
	 * be in the system parameter cache.
	 *  
	 * @throws Exception
	 */
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=74,
			mingleRef = 35488, 
			mingleTitle = "WMP -- WP74 - Investigation Event Start and End Dates and Times v3", 
			acceptanceCriteriaRefs = "myCR4.1", 
			given = "An investigation that is going to be created, the fromTime and toDate are pupulated"
					+ "but the toTime is not."
					+ "The INVESTIGATION_AND_CONTACT_EVENT_TIME_FIELDS_MANDATORY_ON_AND_AFTER_DATE has been"
					+ "set up to a date in the passed.", 
			when = "The incident is summitted to be created",
			then = "Incident should be created.")
	public void testPassedDateSystemParametertToTimeNotPopulated() throws Exception {
		boolean expectedException = false;
		addInvestigationTimeToSystemParameterCache(getDate(-2));
		incidentDto.setIncidentAtOrFromTime(incidentDto.getReportedTime());
		incidentDto.setIncidentToDate(new PoleDate());
		
		try {
			doSummitIncident(incidentDto);
		}catch (InvalidDataException ide) {
			logger.error("validation exception: " + ide);
			if (!ide.getMessage().contains("incidentToTime")) {
				throw ide;
			} else {
				expectedException = true;
			}
		}
		
		if (!expectedException) {
			fail("An InvalidDataException for the field incidentToTime was expected.");
		}
	}
	
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=74,
			mingleRef = 35488, 
			mingleTitle = "WMP -- WP74 - Investigation Event Start and End Dates and Times v3", 
			acceptanceCriteriaRefs = "myCR4.2", 
			given = "An investigation that is going to be created, the fromTime and toDate are pupulated"
					+ "but the toTime is not."
					+ "The INVESTIGATION_AND_CONTACT_EVENT_TIME_FIELDS_MANDATORY_ON_AND_AFTER_DATE has been"
					+ "set up to the current date.", 
			when = "The incident is summitted to be created",
			then = "Incident should be created.")
	public void testCurrentDateSystemParameterToTimeNotPopulated() throws Exception {
		boolean expectedException = false;
		addInvestigationTimeToSystemParameterCache(getDate(0));
		incidentDto.setIncidentAtOrFromTime(incidentDto.getReportedTime());
		incidentDto.setIncidentToDate(new PoleDate());
		
		try {
			doSummitIncident(incidentDto);
		}catch (InvalidDataException ide) {
			logger.error("validation exception: " + ide);
			if (!ide.getMessage().contains("incidentToTime")) {
				throw ide;
			} else {
				expectedException = true;
			}
		}
		
		if (!expectedException) {
			fail("An InvalidDataException for the field incidentToTime was expected.");
		}
	}
	
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=74,
			mingleRef = 35488, 
			mingleTitle = "WMP -- WP74 - Investigation Event Start and End Dates and Times v3", 
			acceptanceCriteriaRefs = "myCR4.3", 
			given = "An investigation that is going to be created, the fromTime and toDate are pupulated"
					+ "but the toTime is not."
					+ "The INVESTIGATION_AND_CONTACT_EVENT_TIME_FIELDS_MANDATORY_ON_AND_AFTER_DATE has been"
					+ "set up to the current date.", 
			when = "The incident is summitted to be created",
			then = "Incident should be created (rules shouldn't be applied).")
	public void testFutureDateSystemParameterToTimeNotPopulated() throws Exception {
		addInvestigationTimeToSystemParameterCache(getDate(2));
		incidentDto.setIncidentAtOrFromTime(incidentDto.getReportedTime());
		incidentDto.setIncidentToDate(new PoleDate());
		doSummitIncident(incidentDto);
	}	
	
	/**
	 * Test when everything is populated (fromTime, toDate and toTime), so no validation exception is
	 * expected.
	 * <p>
	 * Precondition: the INVESTIGATION_AND_CONTACT_EVENT_TIME_FIELDS_MANDATORY_ON_AND_AFTER_DATE should
	 * be in the system parameter cache.
	 *  
	 * @throws Exception
	 */
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=74,
			mingleRef = 35488, 
			mingleTitle = "WMP -- WP74 - Investigation Event Start and End Dates and Times v3", 
			acceptanceCriteriaRefs = "myCR5.1", 
			given = "An investigation that is going to be created, all mandatory fields are populated."
					+ "The INVESTIGATION_AND_CONTACT_EVENT_TIME_FIELDS_MANDATORY_ON_AND_AFTER_DATE has been"
					+ "set up to a date in the passed.", 
			when = "The incident is summitted to be created",
			then = "Incident should be created.")
	public void testPassedDateSystemParameterToTimePopulated() throws Exception {
		addInvestigationTimeToSystemParameterCache(getDate(-2));
		incidentDto.setIncidentAtOrFromTime(incidentDto.getReportedTime());
		incidentDto.setIncidentToDate(new PoleDate());		
		incidentDto.setIncidentToTime(new PoleTime());
		doSummitIncident(incidentDto);
	}
	
	
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=74,
			mingleRef = 35488, 
			mingleTitle = "WMP -- WP74 - Investigation Event Start and End Dates and Times v3", 
			acceptanceCriteriaRefs = "myCR5.2", 
			given = "An investigation that is going to be created, all mandatory fields are populated."
					+ "The INVESTIGATION_AND_CONTACT_EVENT_TIME_FIELDS_MANDATORY_ON_AND_AFTER_DATE has been"
					+ "set up to the current date.", 
			when = "The incident is summitted to be created",
			then = "Incident should be created.")
	public void testCurrentDateSystemParameterToTimePopulated() throws Exception {
		addInvestigationTimeToSystemParameterCache(getDate(0));
		incidentDto.setIncidentAtOrFromTime(incidentDto.getReportedTime());
		incidentDto.setIncidentToDate(new PoleDate());		
		incidentDto.setIncidentToTime(new PoleTime());
		doSummitIncident(incidentDto);
	}	

	
	@BusSvcStoryAcceptanceCriteriaReference(
			workPackage=74,
			mingleRef = 35488, 
			mingleTitle = "WMP -- WP74 - Investigation Event Start and End Dates and Times v3", 
			acceptanceCriteriaRefs = "myCR5.3", 
			given = "An investigation that is going to be created, all mandatory fields are populated."
					+ "The INVESTIGATION_AND_CONTACT_EVENT_TIME_FIELDS_MANDATORY_ON_AND_AFTER_DATE has been"
					+ "set up to a future date.", 
			when = "The incident is summitted to be created",
			then = "Incident should be created.")
	public void testFutureDateSystemParameterToTimePopulated() throws Exception {
		addInvestigationTimeToSystemParameterCache(getDate(2));
		incidentDto.setIncidentAtOrFromTime(incidentDto.getReportedTime());
		incidentDto.setIncidentToDate(new PoleDate());		
		incidentDto.setIncidentToTime(new PoleTime());
		doSummitIncident(incidentDto);
	}		
	@Override
	protected void teardownImpl() throws Exception {
		deleteData();
	}

	private void deleteData() throws Exception {
		try {
			if (incidentDto != null && incidentDto.getObjectRef() > 0) {
				utils.removeIncidentFromPole(incidentDto.getObjectRef(), 2, poleDirect, securityContextId);
			}
		} catch (Exception e) {
			logger.debug("Failed to delete Incident having objectRef {} ", incidentDto.getObjectRef(), e);
		}
	}	
	
	
	/**
	 * Submmit incident to BS to be created.
	 */
	protected void doSummitIncident(PoleObjectDto poleObjectDto) throws Exception {
		PutPoleObjectsRequestDto putPoleObjectsRequest = utils.createPutPoleObjectsRequestDto(
				BusinessServicesTestUtils.createBusinessServiceInfo(securityContextId), (IncidentDto) poleObjectDto);
		
		putPoleObjectsRequest.getHeader().setClientName(FrameworkServiceConstants.BUSINESS_SERVICES_CLIENT_NAME);

		
		PutPoleObjectsResponseDto poleObjectsResponse = poleBusinessServices.putPoleObjects(putPoleObjectsRequest);
		Integer incidentObjectRef = IncidentPoleDtoUtils.extractIncidentObjectRef(poleObjectsResponse);
		
		assertNotNull(incidentObjectRef);
		
		
		incidentDto = utils.getIncidentFromPole(incidentObjectRef, poleBusinessServices, securityContextId);
		assertNotNull(incidentDto);
		//utils.removeIncident(i,businessServices, securityContextId);
	}
	
	
	/**
	 * Add to the system parameters cache the system parameter INVESTIGATION_AND_CONTACT_EVENT_TIME_FIELDS_MANDATORY_ON_AND_AFTER_DATE 
	 * @param value
	 */
	private void addInvestigationTimeToSystemParameterCache(String value) {
		systemParametersCache.addEntityData_TEST_ONLY(getSystemParameterRequest(), getSystemParameterResponse(value));
	}
	
	/**
	 * Create a GetAuxiliaryDataRequestDto request to fetch the INVESTIGATION_AND_CONTACT_EVENT_TIME_FIELDS_MANDATORY_ON_AND_AFTER_DATE
	 * @return
	 */
	private static GetAuxiliaryDataRequestDto getSystemParameterRequest() {
		StringCriterionDto fieldCriterion = new StringCriterionDto();
		fieldCriterion.setFieldName(SystemParamsCacheBean.FIELD_CRITERION_NAME);
		fieldCriterion.setValue(INVESTIGATION_TIME_FIELDS_SYSTEM_PARAMETER_NAME);
		
		AuxiliaryDataCriteriaDto auxiliaryDataCriteria = new AuxiliaryDataCriteriaDto();
		auxiliaryDataCriteria.setEntityType(PoleNames.SYSTEM_PARAMETERS);
		auxiliaryDataCriteria.addFieldCriterion(fieldCriterion);
		
		GetAuxiliaryDataRequestDto request = new GetAuxiliaryDataRequestDto();
		request.setAuxiliaryDataCriteria(auxiliaryDataCriteria);
		return request;
	}
	
	/**
	 * Create a GetAuxiliaryDataResponseDto with INVESTIGATION_AND_CONTACT_EVENT_TIME_FIELDS_MANDATORY_ON_AND_AFTER_DATE information
	 * @param value
	 * @return
	 */
	private static GetAuxiliaryDataResponseDto getSystemParameterResponse(String value) {
		SystemParametersDto systemParam = new SystemParametersDto();
		systemParam.setName(INVESTIGATION_TIME_FIELDS_SYSTEM_PARAMETER_NAME);
		systemParam.setValue(value);
		
		GetAuxiliaryDataResponseDto response = new GetAuxiliaryDataResponseDto();
		response.addAuxiliaryData(systemParam);
		
		return response;
	}
	
	/**
	 * Return a String with a date in the format dd/MM/yyyy
	 * <p>
	 * The date is calculated based in the current date and the days parameter:
	 *   - If days == 0 returns current date.
	 *   - If days < 0 returns a date earlier (days is the number of days earlier).
	 *   - If days > 0 returns a future date (days is the number of days in the future).
	 * @param days
	 * @return
	 */
	private String getDate(int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, days);
		return DATE_FORMAT.format(calendar.getTime());
	}
}
