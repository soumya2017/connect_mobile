package com.northgateis.gem.bussvc.poleobjects.functest;

import static com.northgateis.gem.bussvc.pole.utils.PoleDtoUtils.extractTransactionId;
import static com.northgateis.gem.bussvc.pole.utils.PoleDtoUtils.extractTxData;
import static com.northgateis.gem.bussvc.poleobjects.constants.PoleObjectsServiceConstants.EVENT_ACTION_EXTRA_INFO_KEY;
import static com.northgateis.gem.bussvc.poleobjects.constants.PoleObjectsServiceConstants.SCOPED_TRANSACTION_ID_EXTRA_INFO_KEY;
import static com.northgateis.gem.bussvc.poleobjects.constants.PoleObjectsServiceConstants.TASK_ID_EXTRA_INFO_KEY;
import static com.northgateis.gem.bussvc.util.CucumberReflectionUtils.getFullyQualifiedClass;
import static com.northgateis.gem.bussvc.util.CucumberReflectionUtils.getValueFromPoleGraph;
import static com.northgateis.gem.bussvc.util.CucumberReflectionUtils.setModificationStatus;
import static com.northgateis.gem.bussvc.util.CucumberReflectionUtils.setPropertyWithValue;
import static com.northgateis.gem.bussvc.util.CucumberReflectionUtils.setTxDataModificationStatus;
import static com.northgateis.gem.bussvc.util.GenericGlueCodeHelper.createExtraInfo;
import static com.northgateis.gem.bussvc.util.GenericGlueCodeHelper.extractObjectRef;
import static com.northgateis.gem.bussvc.util.GenericGlueCodeHelper.getChildTxDataName;
import static com.northgateis.gem.bussvc.util.GenericGlueCodeHelper.getConvertedDate;
import static com.northgateis.gem.bussvc.util.GenericGlueCodeHelper.getEventActionName;
import static com.northgateis.gem.bussvc.util.GenericGlueCodeHelper.getInternalTaskName;
import static com.northgateis.gem.bussvc.util.GenericGlueCodeHelper.getPoleGraphFromPole;
import static com.northgateis.gem.bussvc.util.GenericGlueCodeHelper.getTaskHistoryParametersListInMap;
import static com.northgateis.gem.bussvc.util.GenericGlueCodeHelper.submitPutPoleRequest;
import static com.northgateis.gem.bussvc.util.GenericGlueCodeHelper.taskHistoryInternalValue;
import static com.northgateis.gem.bussvc.util.LogFailureUtil.log;
import static com.northgateis.gem.bussvc.util.LogFailureUtil.logOrError;
import static com.northgateis.pole.client.PoleDtoBuildHelper.buildIntegerCriterionDto;
import static com.northgateis.pole.client.PoleDtoBuildHelper.buildPoleObjectCriteriaDto;
import static com.northgateis.pole.schema.ModificationStatusDto.CREATE;
import static com.northgateis.pole.schema.ModificationStatusDto.UPDATE;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.replace;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import com.northgateis.gem.bussvc.AbstractBusinessServicesFunctionalTestBase;
import com.northgateis.gem.bussvc.framework.exception.InfrastructureException;
import com.northgateis.gem.bussvc.framework.schema.BusinessServiceValidationError;
import com.northgateis.gem.bussvc.framework.schema.BusinessServiceValidationErrors;
import com.northgateis.gem.bussvc.util.CucumberReflectionUtils;
import com.northgateis.gem.bussvc.util.GenericGlueCodeHelper;
import com.northgateis.pole.common.InvalidDataException;
import com.northgateis.pole.common.PoleNames;
import com.northgateis.pole.schema.GemTaskHistoryDto;
import com.northgateis.pole.schema.GetPoleObjectsResponseDto;
import com.northgateis.pole.schema.LinkDto;
import com.northgateis.pole.schema.MessageExtraInfoDto;
import com.northgateis.pole.schema.ModificationStatusDto;
import com.northgateis.pole.schema.PoleEntityDto;
import com.northgateis.pole.schema.PoleObjectCriteriaDto;
import com.northgateis.pole.schema.PoleObjectDto;
import com.northgateis.pole.schema.PutPoleObjectsResponseDto;
import com.northgateis.pole.schema.StaticObjectDto;
import com.northgateis.poleflow.ws.schema.beans.Task;

import cucumber.api.PendingException;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Generic step definition class for Cucumber Functional Tests. These step
 * definitions can be executed in 2 modes. One can change the mode by setting
 * the System property logOnlyMode.
 * 
 * logOnlyMode=true : This is Default mode. In this mode all the tests will run
 * like parser and print the logs in output to give you the contextual
 * information about that step.
 * 
 * logOnlyMode=false : In this mode the actual glue code will execute in which
 * the PutPoleObjectrequest and GetPoleObjectRequest will be constructed using
 * reflection code and will be submitted to BusinessService. And assert the
 * event status, polegraph fields, task history and poleflow response.
 * 
 * {@link GenericGlueCodeHelper} : Helper code for creation and submission of
 * request to BS. 
 * {@link CucumberReflectionUtils} : Reflection code for creating
 * pole objects, setting values in that and fetching values from pole graph.
 * 
 * @author vikas.jain
 */
public class EventFunctionalCuRouteStepDef extends AbstractBusinessServicesFunctionalTestBase {

	/********************************************
	 *   Key constants for statefulObjects map. *
	 *******************************************/
	private static final String EVENT_LINKIDENTIFIER = "link";
	private static final String TRANSACTION_ID_IDENTIFIER = "Transaction Id";
	private static final String TASK_ID_IDENTIFIER = "Task Id";
	private static final String TX_DATA_OBJECT_IDENTIFIER = "Tx Data Object";
	private static final String TASK_HISTORY_MAP_IDENTIFIER = "Task History Map";
	private static final String VALIDATION_ERRORS_IDENTIFIER = "Validation Errors";
	private static final String GET_POLE_RESPONSE_IDENTIFIER = "Get Pole Response";
	private static final String OBJECT_REF_IDENTIFIER = "Object Ref";
	private static final String EXTERNAL_TASK_NAME_IDENTIFIER = "Task Name";
	private static final String TASK_HISTORY_COUNT_IDENTIFIER = "Task History Count";
	private static final String CURRENT_OBJECT_IDENTIFIER = "Currect Object";
	private static final String EVENT_NAME_IDENTIFIER = "Event Name";
	private static final String EVENT_OBJECT_IDENTIFIER = "Event Object";
	private static final String PARENT_LINK = "Parent Link";
	private static final String LINK = "Event Links";
	private static final String STATIC_OBJECT = "Static Object";
	
	private Map<String, StaticObjectDto> staticObjectsMap;
	private List<LinkDto> linkDtoList;

	/*****************************************************
	 * This can be reset by System Property logOnlyMode. *
	 *****************************************************/
	private boolean logOnlyMode = true;
	
	private boolean muteTearDown = false;
	
	/***************************************************************************************
	 * To store the state of different objects to share between multiple step definitions. *
	 ***************************************************************************************/
	private Map<String, Object> statefulObjects = new ConcurrentHashMap<>();

	@Before
	public void setupContext() throws Exception {
		String logMode = System.getProperty("logOnlyMode");
		logOnlyMode = logMode == null ? true : Boolean.parseBoolean(logMode);
		if (!logOnlyMode) {
			linkDtoList = new ArrayList<LinkDto>();
			staticObjectsMap = new HashMap<>();
			super.setup();
		}
	}

	@After
	public void afterContext() throws Exception {
		String muteTDown = System.getProperty("muteTearDown");
		this.muteTearDown = muteTDown == null ? false : Boolean.parseBoolean(muteTDown);
		if (!muteTearDown) {
			deleteData();
		}
		statefulObjects.clear();
	}

	/**
	 * Overridden to ensure in short term we have permission to read a newly
	 * created intel report. This is a bit of a hack and should be tidied up
	 * when we get proper seed-data setup later for general Cucumber testing.
	 * 
	 * @throws Exception
	 */
	@Override
	protected void createMainSecurityContextId() {
		try {
			if (securityContextId == null) {
				securityContextId = busSvcUtils.getSecurityContextId("Frank Shunneltestone",
						Arrays.asList("ATHENA_USER", "IntelPC", "IntelProfessional", "IntelResTaskLink", "IntelSubmit",
								"IntelSupervisor", "IntelView", "MIFullDataAccess", "MIReportAdmin", "MIReportViewer",
								"MIRestrictedData", "MISysAdmin", "NorthgateSystemAdmin", "PartnerAccess",
								"PARTNERADMIN", "PropertyOfficer", "PropertySupervisor", "PSDAuditor", "PSDAuditView",
								"PSPRACTITIONER", "PSSUPERVISOR", "SEALED_GROUP", "SGG-DEFAULT-ACCESS-CONNECT",
								"SGG-SYSTEM-ADMIN-CONNECT", "SpecialistCovertIntelGroupC", "SysAdmin1", "SysAdmin2"),
						securityService);
			}
		} catch (Exception e) {
			logger.debug(
					"Unable to get a security context id for the supplied iamId with the supplied iamRoles, using the security service url",
					e);
		}
	}

	/**
	 * This method maintains the map of the contextual state of the intel report
	 * i.e - current event, child event, object ref, txData, task history,
	 * external task name
	 * 
	 * EVENT_OBJECT_IDENTIFIER - holds the event object created from the event
	 * passed from gherkins eg: IntelligenceReport object
	 * CURRECT_OBJECT_IDENTIFIER - Refers to the current object. It might be
	 * parent object or child object or txdata. 
	 * TASK_HISTORY_COUNT_IDENTIFIER -
	 * Initially the value is set to zero. For every task performed it is
	 * incremented by one. Later this value is used in assertion to check that
	 * only one entry has gone to the task history table in POLE.
	 * TX_DATA_OBJECT_IDENTIFIER - holds the tx data object. The txdata object
	 * is identified from the taskVsEventActionAndTxData map defined in
	 * {@link} GenericGlueCodeHelper. 
	 * EXTERNAL_TASK_NAME_IDENTIFIER - holds the
	 * external task name passed from the gherkins
	 *
	 * @param id
	 * @param statefulObject
	 * 
	 */
	public void registerStatefulObject(final String id, final Object statefulObject) {
		this.statefulObjects.put(id, statefulObject);
	}

	/**
	 * This method fetches the contextual state of the intel report from the map
	 * 
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T fetchStatefulObject(final String id) {
		return (T) this.statefulObjects.get(id);
	}

	/**
	 * This step definition creates i.e instantiate an event, related txdata in
	 * any scenario.
	 * 
	 * @param action
	 * @param event
	 */
	@Given("^client opted to perform action '?(.*?)'? on an '?(.*?)'?$")
	public void createEvent(final String action, final String event) {
		if (logOnlyMode) {
			log(logger, "*********************************************");
			logOrError(logger, "Client initiated to create an " + event, null);
			logOrError(logger, "And event type object is identified based on " + event, null);
			logOrError(logger, "Then event object Type " + event + " is created", null);
		} else {

			final String eventName = replace(event, " ", "");
			final String externalTaskName = "Create " + event;

			final PoleEntityDto eventObject = createEntityAndSetModificationStatus(eventName, CREATE);

			final PoleEntityDto childObject = createEntityAndSetModificationStatus(eventName + "TxData", CREATE);
			setPropertyWithValue(eventObject, eventName + "TxData", childObject);

			final PoleEntityDto txDataObj = createEntityAndSetModificationStatus(getChildTxDataName(externalTaskName),
					CREATE);
			setPropertyWithValue(childObject, ((PoleEntityDto) txDataObj).getEntityType(), txDataObj);

			registerExternalTaskName(externalTaskName);
			registerTxDataObject(txDataObj);
			registerTaskHistoryCountIdentifier(0);
			registerEventObject(eventObject);
			registerCurrentObjectInContext(eventObject);
			registerEventName(eventName);
			registerParentLink(eventName);
		}
	}

	/**
	 * This step must be the very first line to check the event status before
	 * setting values in txData and perform task. Actual status is fetched from
	 * contextual map and asserted with the expected status coming from
	 * gherkins.
	 * 
	 * @param event
	 * @param expectedStatus
	 */
	@Given("^an '?(.*?)'? exists with status of '?(.*?)'?$")
	public void eventExististingStatus(final String event, final String expectedStatus) {
		if (logOnlyMode) {
			log(logger, "*********************************************");
			logOrError(logger, "If the task is to create an " + event
					+ " , then no object ref would be associated with it" + " and the status would always be NEW. ",
					null);
			logOrError(logger,
					"If task is not to create an " + event
							+ " ,then make a getPoleRequest call to business services to fetch the Event created on the basis of object ref. ",
					null);
			logOrError(logger, "The current report would have the status :" + expectedStatus, null);
			logOrError(logger, "The event will have ObjectRef, taskId and transactionId associated with it", null);
		} else {
			checkStatus(expectedStatus);
		}
	}

	/**
	 * This is very generic statement to set the values in current object in
	 * context, also if current object is event object or txData object then its
	 * try to set values in both.
	 * 
	 * @param label
	 * @param value
	 */
	@Given("^the client provides '?(.*?)'? as '?(.*?)'?$")
	public void clientInput(final String label, String value) {

		if (logOnlyMode) {
			log(logger, "*********************************************");
			logOrError(logger, "Client provides " + value + " and set it into set" + replace(label, " ", "")
					+ "() of current object", null);
		} else if (StringUtils.isNoneEmpty(value)) {
			value = convertDate(value);
			populateEventOrTxdataObject(replace(label, " ", ""), value);
		}
	}

	/**
	 * This statement is mainly to record the current external task name.
	 * 
	 * @param taskName
	 * @param event
	 */
	@Given("^a task '?(.*?)'? exists against that '?(.*?)'?$")
	public void taskExistsAgainstEvent(final String externalTaskName, final String event) {
		if (logOnlyMode) {
			log(logger, "*********************************************");
			logOrError(logger,
					"Internal task name will be fetched from Task Configuration from POLE on the basis of external task name "
							+ externalTaskName,
					null);
		} else {
			registerExternalTaskName(externalTaskName);
		}
	}
	

	/**
	 * Check that a notification is sent to the specified user containing the expected text.
	 * Employee iteration ID will be setup per envt that we run Cucumber on, by QA.
	 * @param employeeIterationId
	 * @param notificationContent
	 */
	@Given("^a notification is sent to employee iteration '?(.*?)'? containing text '?(.*?)'?$")
	public void notificationIsSentToEmployeeIterationContainingText(final Integer employeeIterationId, final String notificationContent) {
		if (logOnlyMode) {
			log(logger, "*********************************************");
			logOrError(logger,
					"A notification will be sent to user identified as employee iteration " + employeeIterationId
							+ " containing text '" + notificationContent + "'.", null);
		} else {
			notificationIsSentToEmployeeIterationContainingTextImpl(employeeIterationId, notificationContent);
		}
	}
	
	/**
	 * Check that a notification is sent to the specified user.
	 * Employee iteration ID will be setup per envt that we run Cucumber on, by QA.
	 * @param employeeIterationId
	 */
	@Given("^a notification is sent to employee iteration '?(.*?)'?$")
	public void notificationIsSentToEmployeeIteration(final Integer employeeIterationId) {
		if (logOnlyMode) {
			log(logger, "*********************************************");
			logOrError(logger,
					"A notification will be sent to user identified as employee iteration " + employeeIterationId, null);
		} else {
			notificationIsSentToEmployeeIterationContainingTextImpl(employeeIterationId, null);
		}
	}
	
	private void notificationIsSentToEmployeeIterationContainingTextImpl(final Integer employeeIterationId, String expectedText) {
		throw new PendingException("Glue code not yet implemented."
				+ "Implementation needs to loop for a few seconds (as notification is sent asynch by POLEflow).");
	}
	
	//TODO: Links, ChildObjects, assertion of fields in POLE graph, names of records in the graph.

	/**
	 * This statement is mainly used in create event scenario where child
	 * objects need to be set in in event object.
	 * 
	 * @param childEvent
	 */
	@Given("^the client provides (?:a|an) '?(.*?)'?$")
	public void createChildEvent(final String childEvent) {
		String childeventName = replace(childEvent, " ", "");
		if (logOnlyMode) {
			log(logger, "*********************************************");
			logOrError(logger,
					"Populate the associate dependencies in " + childeventName + " of the parent event",
					null);
		} else {   
			final PoleEntityDto childObject = createEntityAndSetModificationStatus(replace(childEvent, " ", ""), CREATE);
			
			if(fetchStatefulObject(CURRENT_OBJECT_IDENTIFIER) instanceof LinkDto || fetchStatefulObject(CURRENT_OBJECT_IDENTIFIER) instanceof StaticObjectDto ){
				setPropertyWithValue(fetchStatefulObject(CURRENT_OBJECT_IDENTIFIER), replace(childEvent, " ", ""),
						childObject);
			} else {
				setPropertyWithValue(fetchStatefulObject(EVENT_OBJECT_IDENTIFIER), replace(childEvent, " ", ""),
						childObject);
			}
			
			registerCurrentObjectInContext(childObject);
		}
	}

	/**
	 * This statement must be coming before setting values in txData while
	 * performing task. tx data preperation
	 */
	@Given("^the client is eligible to perform the task$")
	public void clientEligibleToPerformTask() {
		if (logOnlyMode) {
			log(logger, "*********************************************");
			logOrError(logger,
					"Based on object ref of Event Object, preparing getPoleRequest to fetch txdata and transaction id for next putpole response.",
					null);
		} else {

			final GetPoleObjectsResponseDto getPoleObjectsResponseDto = fetchPoleGraphFromPole();
			if (getPoleObjectsResponseDto != null) {
				final PoleObjectDto putPoleObjectDto = getPoleObjectsResponseDto.getPoleObjects().get(0);
				putPoleObjectDto.setModificationStatus(UPDATE);

				final PoleEntityDto txDataObject = (PoleEntityDto) extractTxData(putPoleObjectDto);
				setTxDataModificationStatus((PoleEntityDto) txDataObject, putPoleObjectDto, CREATE);

				registerTransactionId(extractTransactionId(getPoleObjectsResponseDto));
				registerTxDataObject(txDataObject);
				registerCurrentObjectInContext(txDataObject);
				registerEventObject(putPoleObjectDto);
			} else {
				fail("Error while getting pole response");
			}
		}
	}

	/**
	 * This step is to set / change system parameters value.
	 * 
	 * @param sysParamName
	 * @param value
	 */
	@Given("^system parameter '?(.*?)'? contains '?(.*?)'?$")
	public void setSystemParameter(final String sysParamName, final String value) {
		if (logOnlyMode) {
			log(logger, "*********************************************");
			logOrError(logger, "Assert the incoming value with the system parameter in the environment", null);
		} else if (!systemParamsCacheBean.getSystemParameter(sysParamName).contains(value)) {
			throw new PendingException(value + "of " + sysParamName + " not match on system parameters");
	  }
	}
	
	/**
	 * 
	 * @param linkName
	 */
	@Given("^the client provides data on '?(.*?)'?$")
	public void setLinkAsCurrentContext(final String linkName) {
		if (logOnlyMode) {
			log(logger, "*********************************************");
			logOrError(logger, " Create the link " + linkName, null);
		} else {
			final PoleEntityDto linkDto = fetchStatefulObject(linkName);
			registerCurrentObjectInContext(linkDto);
		}
	}
	
	/**
	 * This stepdef creates links for the event
	 * 
	 * @param link
	 * @param linkReason
	 * @param aliasName
	 */
	@Given("^the client links (?:a|an) '?(.*?)'? referred to as '?(.*?)' with link reason of '?(.*?)'$")
	public void createLinksForEvent(final String linkName, final String aliasName , final String linkReason) {
		if (logOnlyMode) {
			log(logger, "*********************************************");
			logOrError(logger, "Create the link object for the " + linkName, null);
		} else {
			StaticObjectDto link = null;
			String linkChild = linkName.split(" ")[1];
			final LinkDto linkDtoObject = createLinkDto(linkReason, linkChild);
			if(linkName.contains("New")) {
				link  = createLink(linkChild);
				
			} else if(linkName.contains("Existing")) {
				link  = (StaticObjectDto) instantiatePoleEntityObject(linkChild);
			}
			
			linkDtoObject.setToPoleObject((PoleObjectDto) link);
			addLinkToEvent(linkDtoObject);
			registerLinkAliases(aliasName, link);
			registerLinkChild(linkDtoObject);
			addLinks(linkDtoObject);
		}
	}

	/**
	 * This stepdef creates a linkDto object associated with the event
	 * 
	 * @param link
	 * @param linkReason
	 * @param aliasName
	 */
	@Given("^the client links from '?(.*?)'? to a New '?(.*?)'? referred to as '?(.*?)' with link reason of '?(.*?)'$")
	public void createLinkDtoForEvent(final String parentAlias, final String linkName, final String aliasName, final String linkReason) {
		if (logOnlyMode) {
			log(logger, "*********************************************");
			logOrError(logger, "Create the link object for the " + linkName, null);
		} else {
			final StaticObjectDto parentAliasObject = fetchParentLink(parentAlias);
			final LinkDto linkDtoObject = createLinkDto(linkReason, linkName);
			final StaticObjectDto link  = createLink(linkName);
			linkDtoObject.setToPoleObject((PoleObjectDto) link);
			parentAliasObject.addLink(linkDtoObject);
			registerLinkAliases(aliasName, link);
			registerLinkChild(linkDtoObject);
		}
	}

	/**
	 * This step is used when user initiated tasks need to be performed.
	 * 
	 * @param externalTaskName
	 * @throws Throwable
	 */
	@Given("^the client runs the task '?(.*?)'?$")
	public void clientPerformsUserInitiatedTask(String externalTaskName) throws Throwable {
		if (logOnlyMode) {
			log(logger, "*********************************************");
			logOrError(logger, "In this statement we are going to identify that this is a user initiated task",
					new PendingException());
		} else {
			registerExternalTaskName(externalTaskName);
			GetPoleObjectsResponseDto getPoleObjectsResponseDto =fetchPoleGraphFromPole();
			if (getPoleObjectsResponseDto != null) {
				final PoleObjectDto putPoleObjectDto = getPoleObjectsResponseDto.getPoleObjects().get(0);
				putPoleObjectDto.setModificationStatus(UPDATE);
				
				PoleEntityDto childObj = createEntityAndSetModificationStatus(fetchStatefulObject(EVENT_NAME_IDENTIFIER) + "TxData", CREATE);
				setPropertyWithValue(putPoleObjectDto, childObj.getEntityType(), childObj);
				
				PoleEntityDto txDataObj = createEntityAndSetModificationStatus(getChildTxDataName(externalTaskName), CREATE);
				setPropertyWithValue(childObj, txDataObj.getEntityType(), txDataObj);
				
				registerCurrentObjectInContext(txDataObj);
				registerEventObject(putPoleObjectDto);
				registerTransactionId(extractTransactionId(getPoleObjectsResponseDto));
				registerTxDataObject(txDataObj);
				
				//Removing task id from context because this step is for User Initiated task.
				statefulObjects.remove(TASK_ID_IDENTIFIER);
			}
		}
	}
	
	/**
	 * 
	 * @param field
	 * @param value
	 * @throws Throwable
	 */
	@Given("^the Task has '?(.*?)'? as '?(.*?)'?$")
	public void checkValuesFromPoleFlow(String field, String value) throws Throwable {
		if (logOnlyMode) {
			log(logger, "*********************************************");
			logOrError(logger, "Checking values from poleflow for "+field,
					null);
		} else {
			throw new PendingException();
		}
	}

	/**********************************************************************
	 ****************** End of Given step definitions *******************
	 **********************************************************************/

	/**
	 * In this step a PutPoleObjectRequest will be submitted to BusinessService
	 * and then a GetPoleRequest will be submitted with new object ref to assert
	 * the task history in next step.
	 * 
	 * @param event
	 */
	@When("^the client submits the '?(.*?)'? to Business Services$")
	public void clientSubmits(final String event) {
		if (logOnlyMode) {
			log(logger, "*********************************************");
			logOrError(logger, "A PutPoleObjectRequest will be submitted to Business Service to perform the task.",
					null);
		} else {
			try {

				final String externalTaskName = fetchStatefulObject(EXTERNAL_TASK_NAME_IDENTIFIER);
				final String taskId = fetchStatefulObject(TASK_ID_IDENTIFIER);
				final PoleObjectDto eventObject = fetchStatefulObject(EVENT_OBJECT_IDENTIFIER);
				final String eventName = fetchStatefulObject(EVENT_NAME_IDENTIFIER);
				Integer objectRef = fetchStatefulObject(OBJECT_REF_IDENTIFIER);
				PutPoleObjectsResponseDto response = null;
				
				if(eventObject != null ) {
					eventObject.setGemTaskHistoryEntries(null);									
	
					List<MessageExtraInfoDto> putPoleExtraInfoList = createPutPoleExtraInfoListWithTransactionIdAndTaskIdAndEventAction(
							externalTaskName, taskId);
	
					/*********** Put Pole Object request **********/
					
					response = submitPutPoleRequest(eventObject, externalTaskName,
							poleBusinessServices, taskId, putPoleExtraInfoList);
					
					if (response != null) {
						// This will run the first time when event created, skip in other submission.
						objectRef = objectRef == null ? extractObjectRef(response, eventName) : objectRef;
						registerObjectReference(objectRef);

						/***********
						 * Get Pole Object request with objectRef and eventAction
						 **********/
						final GetPoleObjectsResponseDto getPoleObjectsResponseDto = fetchPoleGraphFromPoleForTaskHistory();
						registerCurrentObjectInContext(getPoleObjectsResponseDto.getPoleObjects().get(0));
						registerEventObject(getPoleObjectsResponseDto.getPoleObjects().get(0));
						registerPoleResponse(getPoleObjectsResponseDto);
					}
					
				} else {
					final StaticObjectDto staticObject = staticObjectsMap.get(event);
					response = submitPutPoleRequest(fetchStatefulObject(CURRENT_OBJECT_IDENTIFIER), null,
							poleBusinessServices, null, null);
					if (response != null) {
						// This will run the first time when event created, skip in other submission.
						objectRef = response.getObjectRefs().get(0);
						staticObject.setObjectRef(objectRef);
	
						log(logger, "***** "+ event +" ***** Object ref = " + staticObjectsMap.get(event).getObjectRef() + "****");
						
						/***********
						 * Get Pole Object request with objectRef and eventAction
						 **********/
						
						final PoleObjectCriteriaDto rootCriteriaDto = buildPoleObjectCriteriaDto(fetchStatefulObject(EVENT_NAME_IDENTIFIER),
								buildIntegerCriterionDto("objectRef", response.getObjectRefs().get(0)));

						final GetPoleObjectsResponseDto getPoleObjectsResponseDto = getPoleGraphFromPole(fetchStatefulObject(EVENT_NAME_IDENTIFIER), rootCriteriaDto, null, poleBusinessServices,
								securityContextId);
						
						registerCurrentObjectInContext(getPoleObjectsResponseDto.getPoleObjects().get(0));
	
					}
				}
			} catch (InvalidDataException ide) {
				final BusinessServiceValidationErrors businessServiceValidationErrors = unmarshalBusinessServiceValidationErrorsFromXml(
						ide.getMessage());
				registerValidationErrors(businessServiceValidationErrors);
				logOrError(logger, "Validation errors from business service ", ide);
			}
		}
	}

	/**********************************************************************
	 ****************** End of When step definitions *******************
	 **********************************************************************/

	/**
	 * TODO: need to check what should be asserted here.
	 */
	@Then("^the task will be performed$")
	public void taskPerformed() {
		if (logOnlyMode) {
			log(logger, "*********************************************");
			logOrError(logger, "The task will be performed", null);
			logOrError(logger,
					"A new entry would be made in Task History table. Old task would be removed from holding tray of the user/unit. ",
					new PendingException());
		}
	}

	/**
	 * This step is asserting the output status in event.
	 * 
	 * @param event
	 * @param newStatus
	 */
	@Then("^the (.*) will have its Status changed to '?(.*?)'?$")
	public void outputStatus(final String event, final String newStatus) {
		if (logOnlyMode) {
			log(logger, "*********************************************");
			logOrError(logger,
					"As a result of task being performed successfully, event status would be changed or remain same based on the task and actionTaken.",
					null);
		} else {
			checkStatus(newStatus);
		}
	}

	/**
	 * Here first fetch all the task list from poleflow using object ref and the
	 * assert that if the new task exist in that list.
	 * 
	 * @param newTask
	 */
	@Then("^a(?: new)? task '?(.*?)'? is created for '?(.*?)'? '?(.*?)'?$")
	public void outputTask(String newTask, String unitOrStaff, String actorName) throws Throwable {
		if (logOnlyMode) {
			log(logger, "*********************************************");
			logOrError(logger, "A next task will be raised against the assosiated unit in the workflow", null);
		} else {
			boolean found = false;
			final Integer objectRef = fetchStatefulObject(OBJECT_REF_IDENTIFIER);
			final String eventName = fetchStatefulObject(EVENT_NAME_IDENTIFIER);
			Thread.sleep(5000);
			final List<Task> tasksCreated = poleFlowUtils.getWorkflowTasksCreated(poleFlowSoapService,
					poleFlowSecurityToken, eventName, objectRef, null, null, securityContextId);
			
			log(logger, "Getting list of task raised for object type " + eventName + " object ref " + objectRef);
			//TODO: actorName need to be asserted.
			for (Task task : tasksCreated) {
				String externalTaskName = task.getExternalTaskName();
				if (newTask.equalsIgnoreCase(externalTaskName)) {
					assertEquals(newTask, externalTaskName);
					registerTaskId(task.getTaskId());
					found = true;
					break;
				}
			}
			assertTrue("Task exist in Poleflow for the " + newTask, found);
		}
	}

	/**
	 * Here we will get the new task history entries from GetPoleObjectResponse.
	 * Only output parameters will be asserted in gherkins.
	 * 
	 * @param event
	 * @throws Exception 
	 */
	@Then("^a new Task History Entry will be added to the '?(.*?)'?$")
	public void taskHistoryEntryGenerated(final String event) throws Exception {
		if (logOnlyMode) {
			log(logger, "*********************************************");
			logOrError(logger,
					"A GetPoleObject Request containing object reference will be submitted to BusinessServices to fetch the task history details for the recently performed action. ",
					null);
		} else {
			final int lastCount = fetchStatefulObject(TASK_HISTORY_COUNT_IDENTIFIER);
			final List<GemTaskHistoryDto> taskHistoryList = fetchTaskHistoryListFromPoleResponse();
			registerTaskHistoryCountIdentifier(taskHistoryList.size());
			assertExpectedEntriesInTaskHistory(lastCount, taskHistoryList.size());
			registerTaskOutputParameters(taskHistoryList);

		}
	}
	
	@Then("^a link will be added to the '?(.*?)'? for '?(.*?)'? referred to as '?(.*?)'? with link reason of '?(.*?)'?$")
	public void assertLinksInPole(final String parentLink, final String expectedLinkName, final String aliasName, final String linkReason) throws Throwable {
		if (logOnlyMode) {
			log(logger, "*********************************************");
			logOrError(logger,
					"A GetPoleObject Request containing object reference will be submitted to BusinessServices to fetch the task history details for the recently performed action. ",
					null);
		} else {
			PoleObjectDto poleObjectDto = null;
			final List<LinkDto> actualLinkDtoList = fetchLinksFromPoleResponse();
			
			if (fetchStatefulObject(EVENT_NAME_IDENTIFIER).equals(parentLink.replace(" ", ""))){
				for(LinkDto actualLink : actualLinkDtoList){
					if(actualLink.getToPoleObjectType().equals(expectedLinkName) && actualLink.getLinkReason().equals(linkReason)) {
							poleObjectDto = actualLink.getToPoleObject();
							assertEquals(actualLink.getLinkReason(),linkReason);
					} 
				}
				final List<LinkDto> expectedLinkDtoList = fetchStatefulObject(LINK);
				assertEquals(expectedLinkDtoList.size(), actualLinkDtoList.size());
				
				registerStatefulObject(aliasName, poleObjectDto);
			} else{
				StaticObjectDto link = fetchStatefulObject(parentLink);
				List<LinkDto> linkd = link.getLinks();
				
				for(LinkDto links : linkd){
					if(links.getToPoleObject().equals(expectedLinkName)){
						registerStatefulObject(aliasName, link);
					}
				}
		        
			}
			//TODO: add assertion for link child objects like PeContactInfo
		}
	}
	
	@Then("^'?(.*?)'? has '?(.*?)'? as '?(.*?)'?$")
	public void assertLinkFieldsInResponse(final String aliasName, final String field, final String expectedValue) throws Throwable {
		if (logOnlyMode) {
			log(logger, "*********************************************");
			logOrError(logger,
					"A GetPoleObject Request containing object reference will be submitted to BusinessServices to fetch the task history details for the recently performed action. ",
					null);
		} else {
			final PoleObjectDto poleObjectDto = fetchStatefulObject(aliasName);
			Object value = getValueFromPoleGraph(poleObjectDto, replace(field, " ", ""));
			assertEquals(value.toString(), expectedValue);
		}
	}
	
	@SuppressWarnings("unchecked")
	private List<LinkDto> fetchLinksFromPoleResponse() throws Exception {

		final GetPoleObjectsResponseDto getPoleObjectsResponseDto = fetchStatefulObject(GET_POLE_RESPONSE_IDENTIFIER);
		return (List<LinkDto>) getValueFromPoleGraph(
				getPoleObjectsResponseDto.getPoleObjects().get(0), "links");
	}
	
	/**
	 * This is a generic step definition for assertion of task history params.
	 * 
	 * @param label
	 * @param value
	 */
	@Then("^the Task History Entry will record '?(.*?)'? as '?(.*?)'?$")
	public void assertTaskHistory(final String label, String value) {
		if (logOnlyMode) {
			log(logger, "*********************************************");
			logOrError(logger,
					"Assert the values of the Task History record to make sure the record exists in the POLE as expected. ",
					null);
		} else {
			final Map<String, String> thMap = fetchStatefulObject(TASK_HISTORY_MAP_IDENTIFIER);
			boolean found = false;

			for (Entry<String, String> entry : thMap.entrySet()) {
				if (entry.getKey().equalsIgnoreCase(replace(label," ",""))) {
					if (value != null && value.startsWith("today")) {
						value = getConvertedDate(value, "yyyyMMdd");
					}
					assertEquals(taskHistoryInternalValue(value), entry.getValue());
					found = true;
					break;
				}
			}
			assertTrue(found);
		}
	}

	/**
	 * Any validation error message can be verified using this steps.
	 * 
	 * @param field
	 * @param expectedMessage
	 */
	@Then("^Error will be returned for field '?(.*?)'? with message '?(.*?)'?$")
	public void exceptionThrownWithErrorTypeMandatory(String field, final String expectedMessage) {
		if (logOnlyMode) {
			log(logger, "*********************************************");
			logOrError(logger,
					"BusinessServiceVadiation Exception will be thrown and error type will be MANDATORY indicating mandatory fields are missing. ",
					null);
		} else {
			final BusinessServiceValidationErrors businessServiceValidationErrors = fetchStatefulObject(
					VALIDATION_ERRORS_IDENTIFIER);
			boolean found = false;
			field = replace(field, " ", "");
			if (businessServiceValidationErrors != null) {
				for (BusinessServiceValidationError error : businessServiceValidationErrors
						.getBusinessServiceValidationError()) {
					if (error.getField() != null && containsIgnoreCase(error.getField(), field)) {
						assertTrue(containsIgnoreCase(error.getError(), expectedMessage));
						found = true;
						break;
					}
				}
			}
			assertTrue("Expected error message found in validation error",found);
		}
	}

	/**
	 * Check the BusinessServiceValidation
	 * 
	 * @param field
	 * @param expectedMessage
	 */
	@Then("^Error will be returned containing '?(.*?)'?$")
	public void exceptionThrownFromBS(final String expectedMessage) {
		if (logOnlyMode) {
			log(logger, "*********************************************");
			logOrError(logger, "Assert any error message returned by Business Service.", null);
		} else {
			final BusinessServiceValidationErrors businessServiceValidationErrors = fetchStatefulObject(
					VALIDATION_ERRORS_IDENTIFIER);
			boolean found = false;
			if (businessServiceValidationErrors != null) {
				for (BusinessServiceValidationError error : businessServiceValidationErrors
						.getBusinessServiceValidationError()) {
					if (containsIgnoreCase(error.getError(), expectedMessage)) {
						found = true;
						break;
					}
				}
			}
			assertTrue("Expected error message found in validation error",found);
		}
	}
	
	
   /**
    * 
    * Assert the child object count in event.
    * 	
    * @param currentObjName
    * @param count
    * @param childObjectName
    * @throws Exception
    */
	@Then("^'?(.*?)'? contains '?(.*?)'? child '?(.*?)'?$")
	public void assertChildObjects(final String currentObjName, final int count, final String childObjectName)
			throws Exception {
		if (logOnlyMode) {
			log(logger, "*********************************************");
			logOrError(logger,
					"Assert the values in fetched event object along with child objects."
							+ "If the field names matches any field in event or child object, it will fetch that and assert it with expectedValue.",
					null);
		} else {
			final Object eventObj = currentObjName == fetchStatefulObject(EVENT_NAME_IDENTIFIER)
					? fetchStatefulObject(EVENT_OBJECT_IDENTIFIER) : fetchStatefulObject(CURRENT_OBJECT_IDENTIFIER);
			final Object resultObj = getValueFromPoleGraph(eventObj, replace(childObjectName, " ", ""));
			assertTrue("Result Object is a collection", resultObj instanceof Collection<?>);
			final Collection<?> colObj = (Collection<?>) resultObj;
			assertEquals("Result Object size matches", count, colObj.size());
			registerCurrentObjectInContext(colObj);
		}
	}

	/**
	 * Assert the pole graph values
	 * 
	 * @param currentObjName
	 * @param field
	 * @param expectedValue
	 * @throws Exception 
	 */
	@Then("^'?(.*?)'? contains '?(.*?)'? as '?(.*?)'?$")
	public void assertFetchedPoleGraphValues(final String currentObjName, final String field,
			final String expectedValue) throws Exception {
		if (logOnlyMode) {
			log(logger, "*********************************************");
			logOrError(logger,
					"Assert the values in fetched event object along with child objects."
							+ "If the field names matches any field in event or child object, it will fetch that and assert it with expectedValue.",
					null);
		} else {
			final Object objectToBeLookup = currentObjName == fetchStatefulObject(EVENT_NAME_IDENTIFIER)
					? fetchStatefulObject(EVENT_OBJECT_IDENTIFIER) : fetchStatefulObject(CURRENT_OBJECT_IDENTIFIER);
			if (objectToBeLookup instanceof Collection<?>) {
				final Collection<?> colObj = (Collection<?>) objectToBeLookup;
				for (Object object : colObj) {
					Object valueFromPoleGraph = getValueFromPoleGraph(object, replace(field, " ", ""));
					if (expectedValue.equals(valueFromPoleGraph)) {
						assertEquals("Found expected value in pole graph", expectedValue,
								getValueFromPoleGraph(objectToBeLookup, replace(field, " ", "")));
					}
				}
			} else {
				assertEquals("Found expected value in pole graph", expectedValue,
						getValueFromPoleGraph(objectToBeLookup, replace(field, " ", "")));
			}
		}
	}

	/**
	 * This statement is to check that the external task name provided is NOT on
	 * the given event/record.
	 * 
	 * @param taskName
	 * @param event
	 */
	@Then("^a task '?(.*?)'? does not exist against that '?(.*?)'?$")
	public void taskDoesNotExistAgainstEvent(final String taskName, final String event) {
		if (logOnlyMode) {
			log(logger, "*********************************************");
			logOrError(logger,
					"Internal task name will be fetched from Task Configuration from POLE on the basis of external task name "
							+ taskName + " and this will be used to search POLEflow to ensure that no task of that name"
							+ " exists against the record (typically proving that the task has been completed).",
					null);
		} else {
			throw new PendingException("Glue code not yet implemented.");
		}
	}

	/**********************************************************************
	 ******************** End of Then step definitions ********************
	 **********************************************************************/
	/**
	 * This method will instantiate the event object or child object or the tx
	 * data depending upon the argument passed.
	 * 
	 * @param objectName
	 * @return
	 */
	private PoleEntityDto instantiatePoleEntityObject(final String objectName) {
		try {
			return getFullyQualifiedClass(objectName).newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			logger.error("Unable to load class for Event" + objectName, e);
			throw new InfrastructureException(e);
		}
	}

	/**
	 * Setting value in current object. if current object is Event then try to
	 * set in txData, if current object is txData then try to set in event as
	 * well.
	 * 
	 * @param inputField
	 * @param value
	 */
	private void populateEventOrTxdataObject(final String inputField,
			final String value) {

		final PoleEntityDto currentObject = fetchStatefulObject(CURRENT_OBJECT_IDENTIFIER);
		final PoleEntityDto txDataObject = fetchStatefulObject(TX_DATA_OBJECT_IDENTIFIER);
		final PoleEntityDto eventObject = fetchStatefulObject(EVENT_OBJECT_IDENTIFIER);
		
		// TODO: hack??? IntelligenceReportDto & CreateIntelligenceReportTxDataDto
					// both has title field??
		if ("Title".equalsIgnoreCase(inputField)) {
			setPropertyWithValue(eventObject, inputField, value);
			return;
		}

		boolean foundInEvent = false;
		boolean foundInTxData = false;
		boolean foundInCurrent = setPropertyWithValue(currentObject, inputField, value);
		if (!foundInCurrent) {
			foundInTxData = setPropertyWithValue(txDataObject, inputField, value);
		} 
		if (!foundInCurrent && !foundInTxData) {
			foundInEvent = setPropertyWithValue(eventObject, inputField, value);
		}
		assertTrue("Not able to set the value", foundInCurrent || foundInEvent || foundInTxData);
	}

	private String convertDate(String value) {
		if (value != null && value.contains("today")) {
			value = getConvertedDate(value, "dd/MM/yyyy");
		}
		return value;
	}

	private void checkStatus(final String expectedStatus) {
		final GetPoleObjectsResponseDto getPoleObjectsResponseDto = fetchStatefulObject(GET_POLE_RESPONSE_IDENTIFIER);
		if (getPoleObjectsResponseDto != null) {
			assertEquals(expectedStatus, getPoleObjectsResponseDto.getPoleObjects().get(0).getStatus());
		} else {
			fail("Unable to get response from Get Pole Request");
		}
	}

	/***
	 *** Prepare Pole Object request with taskId and eventAction ***
	 */
	private List<MessageExtraInfoDto> createGetPoleExtraInfoListWithTaskIdAndEventAction() {
		final String externalTaskName = fetchStatefulObject(EXTERNAL_TASK_NAME_IDENTIFIER);
		final String taskId = fetchStatefulObject(TASK_ID_IDENTIFIER);

		final List<MessageExtraInfoDto> extraInfoList = createExtraInfo(getEventActionName(externalTaskName),
				EVENT_ACTION_EXTRA_INFO_KEY);
		if (taskId != null) {
			extraInfoList.addAll(createExtraInfo(taskId, TASK_ID_EXTRA_INFO_KEY));
		}
		return extraInfoList;
	}

	/***
	 *** Prepare Pole Object request with taskId and eventAction ***
	 */
	private List<MessageExtraInfoDto> createPutPoleExtraInfoListWithTransactionIdAndTaskIdAndEventAction(
			final String externalTaskName, final String taskId) {

		final String transactionId = fetchStatefulObject(TRANSACTION_ID_IDENTIFIER);
		final PoleEntityDto txData = fetchStatefulObject(TX_DATA_OBJECT_IDENTIFIER);
		List<MessageExtraInfoDto> extraInfoList = null;
		if(externalTaskName!=null) {
			 extraInfoList = createExtraInfo(getEventActionName(externalTaskName),
					EVENT_ACTION_EXTRA_INFO_KEY);
		}

		if (transactionId != null) {
			extraInfoList.addAll(createExtraInfo(transactionId, SCOPED_TRANSACTION_ID_EXTRA_INFO_KEY));
		}

		if (taskId != null) {
			setPropertyWithValue(txData, "taskId", taskId);
		}

		return extraInfoList;
	}

	@SuppressWarnings("unchecked")
	private List<GemTaskHistoryDto> fetchTaskHistoryListFromPoleResponse() throws Exception {

		final GetPoleObjectsResponseDto getPoleObjectsResponseDto = fetchStatefulObject(GET_POLE_RESPONSE_IDENTIFIER);
		return (List<GemTaskHistoryDto>) getValueFromPoleGraph(
				getPoleObjectsResponseDto.getPoleObjects().get(0), "gemTaskHistoryEntries");
	}

	private void registerTaskOutputParameters(final List<GemTaskHistoryDto> taskHistoryList) {

		final String taskName = getInternalTaskName(fetchStatefulObject(EXTERNAL_TASK_NAME_IDENTIFIER));
		Integer taskHistoryObjRef = 0;
		for (GemTaskHistoryDto gemTaskHistoryDto : taskHistoryList) {
			if (taskName.equalsIgnoreCase(gemTaskHistoryDto.getTaskName())
					&& gemTaskHistoryDto.getParameterList() != null && gemTaskHistoryDto.getObjectRef() > taskHistoryObjRef) {
				registerTaskHistoryParameterList(
						getTaskHistoryParametersListInMap(gemTaskHistoryDto.getParameterList()));
				taskHistoryObjRef = gemTaskHistoryDto.getObjectRef();
			}
		}
	}

	private void assertExpectedEntriesInTaskHistory(int lastCount, int size) {
		assertEquals(lastCount + 1, size);
	}

	/**
	 * Delete the created intel from POLE
	 */
	private void deleteData() {
		final Integer objectRef = fetchStatefulObject(OBJECT_REF_IDENTIFIER);
		final String eventName = fetchStatefulObject(EVENT_NAME_IDENTIFIER);
		logger.info("Going to delete {} from pole for object ref {}", eventName, objectRef);
		try {
			if (objectRef != null && PoleNames.INTELLIGENCE_REPORT.equalsIgnoreCase(eventName)) {
				intelUtils.removeIntelligenceReportFromPole(objectRef, 2, poleDirect, securityContextId);
				logger.info("Deleted {} from pole for object ref {}", eventName, objectRef);
			}
		} catch (Exception e) {
			logger.debug("Failed to delete Intel Report with objectRef {} ", objectRef, e);
		}
	}

	private PoleEntityDto createEntityAndSetModificationStatus(String eventName, ModificationStatusDto status) {

		log(logger, "************* Creating event object " + eventName + " ******************");

		PoleEntityDto eventObject = instantiatePoleEntityObject(eventName);
		setModificationStatus((PoleEntityDto) eventObject, status);

		log(logger, "************* Event Object " + eventName + " Created *******************");

		return eventObject;
	}

	private GetPoleObjectsResponseDto fetchPoleGraphFromPole() {

		final String eventName = fetchStatefulObject(EVENT_NAME_IDENTIFIER);
		final Integer objectRef = fetchStatefulObject(OBJECT_REF_IDENTIFIER);

		final PoleObjectCriteriaDto rootCriteriaDto = buildPoleObjectCriteriaDto(eventName,
				buildIntegerCriterionDto("objectRef", objectRef));
		final List<MessageExtraInfoDto> getPoleExtraInfoList = createGetPoleExtraInfoListWithTaskIdAndEventAction();

		return getPoleGraphFromPole(eventName, rootCriteriaDto, getPoleExtraInfoList, poleBusinessServices,
				securityContextId);

	}
	
	private GetPoleObjectsResponseDto fetchPoleGraphFromPoleForTaskHistory() {

		final String eventName = fetchStatefulObject(EVENT_NAME_IDENTIFIER);
		final Integer objectRef = fetchStatefulObject(OBJECT_REF_IDENTIFIER);

		final PoleObjectCriteriaDto rootCriteriaDto = buildPoleObjectCriteriaDto(eventName,
				buildIntegerCriterionDto("objectRef", objectRef));

		return getPoleGraphFromPole(eventName, rootCriteriaDto, null, poleBusinessServices,
				securityContextId);

	}

	private LinkDto createLinkDto(final String linkReason, final String linkName) {
		final LinkDto linkDtoObject = (LinkDto) instantiatePoleEntityObject("Link");
		setModificationStatus(linkDtoObject, CREATE);
		linkDtoObject.setResearched(Boolean.TRUE);
		linkDtoObject.setLinkReason(linkReason);
		linkDtoObject.setToPoleObjectType(linkName);
		addObjectReference(linkDtoObject);
		addSource(linkDtoObject);
		return linkDtoObject;
	}
	
	private void addLinkToEvent(final LinkDto linkDtoObject) {
		final PoleObjectDto eventObject = (PoleObjectDto) fetchStatefulObject(EVENT_OBJECT_IDENTIFIER);
		eventObject.addLink(linkDtoObject);
	}

	private void addSource(final LinkDto linkDtoObject) {
		final String event = fetchStatefulObject(EVENT_NAME_IDENTIFIER);
		linkDtoObject.setSourcePoleObjectType(event);
		final String parentLink = fetchStatefulObject(PARENT_LINK);
		linkDtoObject.setFromPoleObjectType(parentLink);
	}

	private void addObjectReference(final LinkDto linkDtoObject) {
		final Integer objectRef = fetchStatefulObject(OBJECT_REF_IDENTIFIER);
		linkDtoObject.setSourcePoleObjectRef(objectRef);
		final String parentLink = fetchStatefulObject(PARENT_LINK);
		if(parentLink.equals(fetchStatefulObject(EVENT_NAME_IDENTIFIER))){
			linkDtoObject.setFromPoleObjectRef(objectRef);
		} else {
			linkDtoObject.setFromPoleObjectRef(null);
		}
	}
	
	/** Create a link object i.e Person, Location, Vehicle
	 * 
	 * @param link
	 * @return
	 */
	private StaticObjectDto createLink(final String link) {
		final StaticObjectDto linkChild = (StaticObjectDto) instantiatePoleEntityObject(link);
		linkChild.setStatus("NEW");
		linkChild.setResearched(Boolean.TRUE);
		setModificationStatus((PoleEntityDto) linkChild, CREATE);
		return linkChild;
	}
	
	private StaticObjectDto fetchParentLink(final String parentAlias) {
		final StaticObjectDto parentAliasObject = fetchStatefulObject(parentAlias);
		final String linkEvent = parentAliasObject.getClass().getSimpleName().substring(0,parentAliasObject.getClass().getSimpleName().length()-3);
		registerStatefulObject(PARENT_LINK, linkEvent);
		return parentAliasObject;
	}
	
	private void addLinks(final LinkDto linkDtoObject) {
		linkDtoList.add(linkDtoObject);
		registerStatefulObject(LINK, linkDtoList);
	}

	/**
	 * Register all objects in stateful map that will be used later in other
	 * step definitions.
	 */

	private void registerTxDataObject(final PoleEntityDto txDataObject) {
		registerStatefulObject(TX_DATA_OBJECT_IDENTIFIER, txDataObject);
	}

	private void registerTransactionId(final String transactionId) {
		registerStatefulObject(TRANSACTION_ID_IDENTIFIER, transactionId);
	}

	private void registerExternalTaskName(final String taskName) {
		registerStatefulObject(EXTERNAL_TASK_NAME_IDENTIFIER, taskName);
	}

	private void registerTaskHistoryCountIdentifier(final int size) {
		registerStatefulObject(TASK_HISTORY_COUNT_IDENTIFIER, size);
	}

	private void registerEventName(final String eventName) {
		registerStatefulObject(EVENT_NAME_IDENTIFIER, eventName);
	}

	private void registerCurrentObjectInContext(final Object currentObject) {
		registerStatefulObject(CURRENT_OBJECT_IDENTIFIER, currentObject);
	}

	private void registerEventObject(final PoleEntityDto eventObject) {
		registerStatefulObject(EVENT_OBJECT_IDENTIFIER, eventObject);
	}

	private void registerTaskId(final String taskId) {
		registerStatefulObject(TASK_ID_IDENTIFIER, taskId);
	}

	private void registerPoleResponse(final GetPoleObjectsResponseDto getPoleObjectsResponseDto) {
		registerStatefulObject(GET_POLE_RESPONSE_IDENTIFIER, getPoleObjectsResponseDto);
	}

	private void registerObjectReference(final Integer objectRef) {
		registerStatefulObject(OBJECT_REF_IDENTIFIER, objectRef);
	}

	private void registerValidationErrors(final BusinessServiceValidationErrors businessServiceValidationErrors) {
		registerStatefulObject(VALIDATION_ERRORS_IDENTIFIER, businessServiceValidationErrors);
	}

	private void registerTaskHistoryParameterList(final Map<String, String> taskHistoryParametersListInMap) {
		registerStatefulObject(TASK_HISTORY_MAP_IDENTIFIER, taskHistoryParametersListInMap);
	}

	private void registerLinkAliases(final String aliasName, final PoleEntityDto linkChild) {
		registerStatefulObject(aliasName, linkChild);
	}

	private void registerLinkChild(final LinkDto linkDtoObject) {
		registerStatefulObject(EVENT_LINKIDENTIFIER, linkDtoObject);
	}
	
	private void registerParentLink(String eventName) {
		registerStatefulObject(PARENT_LINK, eventName);		
	}
	
	@Given("^a static object '?(.*?)'? needs to persist in Pole referred to as '?(.*?)'?$")
	public void createStaticObject(final String staticObjectName, final String aliasName) {
		final StaticObjectDto eventObject = (StaticObjectDto) createEntityAndSetModificationStatus(staticObjectName, CREATE);
		eventObject.setStatus("NEW");
		registerStatefulObject(STATIC_OBJECT, eventObject);	
		registerCurrentObjectInContext(eventObject);
		registerEventName(staticObjectName);
		staticObjectsMap.put(aliasName, eventObject);
	}

	@Given("^the client provides '?(.*?)'? for the '?(.*?)'?$")
	public void addObjectRefToLink(final String objectReference, final String aliasName) {
		StaticObjectDto staticObject = staticObjectsMap.get(aliasName);
		StaticObjectDto actualStaticObject  = fetchStatefulObject(CURRENT_OBJECT_IDENTIFIER);
		actualStaticObject.setObjectRef(staticObject.getObjectRef());
		actualStaticObject.setResearched(true);
	}
	
	@Then("^'?(.*?)'? will persist in Pole?$")
	public void assertStaticObject(final String aliasName) {
		//fetch
		StaticObjectDto expectedStaticObject = staticObjectsMap.get(aliasName);
		StaticObjectDto actualStaticObject = fetchStatefulObject(CURRENT_OBJECT_IDENTIFIER);
		log(logger, "********ASSERTING OBJECTREF*********************");
		assertEquals(expectedStaticObject.getObjectRef(), actualStaticObject.getObjectRef());
	}
}