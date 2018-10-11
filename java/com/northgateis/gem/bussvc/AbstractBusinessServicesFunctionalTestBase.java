package com.northgateis.gem.bussvc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

import org.apache.camel.CamelContext;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestWatcher;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.northgateis.gem.bussvc.api.AbstractBusinessServicesClient.RequestInfoProvider;
import com.northgateis.gem.bussvc.api.BusinessServicesClient;
import com.northgateis.gem.bussvc.api.rest.BusinessServicesJsonRestClient;
import com.northgateis.gem.bussvc.api.schema.BusinessServicesPort;
import com.northgateis.gem.bussvc.api.soap.BusinessServicesSoapClient;
import com.northgateis.gem.bussvc.attachment.api.AttachmentPort;
import com.northgateis.gem.bussvc.extendedmetadata.PoleExtendedMetadataCache;
import com.northgateis.gem.bussvc.extendedmetadata.validation.ExtendedMetadataValidator;
import com.northgateis.gem.bussvc.framework.schema.BusinessServiceRequestInfo;
import com.northgateis.gem.bussvc.framework.schema.BusinessServiceResponseInfo;
import com.northgateis.gem.bussvc.framework.schema.BusinessServiceValidationErrors;
import com.northgateis.gem.bussvc.framework.test.BusSvcTestWatcher;
import com.northgateis.gem.bussvc.framework.utils.xml.JaxbXmlMarshaller;
import com.northgateis.gem.bussvc.pole.auxiliarydata.AuxiliaryDataCacheBean;
import com.northgateis.gem.bussvc.pole.auxiliarydata.AuxiliaryDataRetriever;
import com.northgateis.gem.bussvc.pole.auxiliarydata.riskassessment.RiskAssessmentTemplateDataCache;
import com.northgateis.gem.bussvc.pole.auxiliarydata.systemparams.SystemParamsCacheBean;
import com.northgateis.gem.bussvc.pole.cvlists.CvListCacheBean;
import com.northgateis.gem.bussvc.pole.linktype.LinkTypeByLinkTagCacheBean;
import com.northgateis.gem.bussvc.pole.linktype.LinkTypeCacheBean;
import com.northgateis.gem.bussvc.pole.linktype.appliedlogic.UnresearchedLinkExemptHelper;
import com.northgateis.gem.bussvc.pole.metadata.PoleMetadataCacheBean;
import com.northgateis.gem.bussvc.pole.pattern.PatternCacheBean;
import com.northgateis.gem.bussvc.pole.profile.ProfileRegistry;
import com.northgateis.gem.bussvc.pole.utils.PoleDtoUtils;
import com.northgateis.gem.bussvc.poleflow.POLEflowSoap;
import com.northgateis.gem.bussvc.poleobjects.client.AbstractPoleBusinessServicesClient.PoleRequestHeaderProvider;
import com.northgateis.gem.bussvc.poleobjects.client.PoleBusinessServicesClient;
import com.northgateis.gem.bussvc.poleobjects.client.PoleBusinessServicesJsonRestClient;
import com.northgateis.gem.bussvc.poleobjects.client.PoleBusinessServicesSoapClient;
import com.northgateis.gem.bussvc.poleobjects.constants.PoleObjectsServiceConstants;
import com.northgateis.gem.bussvc.security.SecuritymessagesPort;
import com.northgateis.gem.bussvc.statecache.SavingOfDataPort;
import com.northgateis.gem.bussvc.submitincident.utils.IncidentPoleDtoUtils;
import com.northgateis.gem.bussvc.submitintelreport.utils.IntelReportPoleDtoUtils;
import com.northgateis.gem.bussvc.submitppe.utils.PpePoleDtoUtils;
import com.northgateis.gem.bussvc.test.retrievers.auxiliarydata.TestAuxiliaryDataRetriever;
import com.northgateis.gem.bussvc.test.retrievers.auxiliarydata.TestRiskAssessmentAuxiliaryDataHelper;
import com.northgateis.gem.bussvc.test.retrievers.cvlist.TestCvListRetriever;
import com.northgateis.gem.bussvc.test.retrievers.linktype.TestLinkTypeRetriever;
import com.northgateis.gem.bussvc.test.retrievers.pattern.TestPatternRetriever;
import com.northgateis.gem.bussvc.test.util.BladeTestUtils;
import com.northgateis.gem.bussvc.test.util.BriefingItemTestUtils;
import com.northgateis.gem.bussvc.test.util.BusinessServicesTestUtils;
import com.northgateis.gem.bussvc.test.util.ContactEventTestUtils;
import com.northgateis.gem.bussvc.test.util.GemCaseTestUtils;
import com.northgateis.gem.bussvc.test.util.IncidentNoteTestUtils;
import com.northgateis.gem.bussvc.test.util.IncidentTestUtils;
import com.northgateis.gem.bussvc.test.util.IntelReportTestUtils;
import com.northgateis.gem.bussvc.test.util.PoleFlowTestUtils;
import com.northgateis.gem.bussvc.test.util.PpeTestUtils;
import com.northgateis.gem.bussvc.test.util.StopSearchTestUtils;
import com.northgateis.gem.framework.util.CamelPlatform;
import com.northgateis.gem.framework.util.logger.GemLogger;
import com.northgateis.pole.common.DateTimeUtils;
import com.northgateis.pole.common.PoleDateTime;
import com.northgateis.pole.common.PoleNames;
import com.northgateis.pole.common.PoleTime;
import com.northgateis.pole.schema.AuxiliaryDataCriteriaDto;
import com.northgateis.pole.schema.AuxiliaryDataDto;
import com.northgateis.pole.schema.EmployeeIterationDto;
import com.northgateis.pole.schema.GetAuxiliaryDataRequestDto;
import com.northgateis.pole.schema.GetAuxiliaryDataResponseDto;
import com.northgateis.pole.schema.IncidentDto;
import com.northgateis.pole.schema.IntegerCriterionDto;
import com.northgateis.pole.schema.IntelligenceReportDto;
import com.northgateis.pole.schema.LinkDto;
import com.northgateis.pole.schema.Pole;
import com.northgateis.pole.schema.PpeDto;
import com.northgateis.pole.schema.PutPoleObjectsRequestDto;
import com.northgateis.pole.schema.PutPoleObjectsResponseDto;
import com.northgateis.pole.schema.RequestHeaderDto;
import com.northgateis.pole.schema.StaticObjectDto;
import com.northgateis.poleflow.ws.schema.beans.Security;

/**
 * Super class of all business services functional tests. 
 * 
 * Allows swapping between the restful interface and the SOAP interface with 
 * the boolean parameter jsonRestMode. Also allows FTs to be run in 'local mode' to
 * allow easy running on Hudson as part of the continuous build, regardless of the 
 * state of deployed environments.
 */
public abstract class AbstractBusinessServicesFunctionalTestBase {

	static {
		GemLogger.setFactory(CamelPlatform.getInstance());
	}
	
	/**
	 * One logger for the whole class hierarchy named after the instance of test class.
	 * 
	 * Note: this is 'pseudo static' so at the top of the class.
	 */
	protected final GemLogger logger = GemLogger.getLogger(this.getClass());

	/**
	 * Records / monitors the coverage annotations for reporting purposes.
	 */
	@Rule
	public TestWatcher testWatcher = new BusSvcTestWatcher();
	
	protected static String owningForceId;
	
	protected static ExtendedMetadataValidator validator;
	
	/**
	 * This may or may not match the production code conventions, but it's the constant we use in our FTs.
	 */
	protected static final String PE_REF_PREFIX = "PUBLIC ENGAGEMENT";
	protected static final String PE_ACC_REF_PREFIX = "PE_ACC_";
	protected static final String PE_UPRN_EXT_REF_TYPE = "FT_UPRN_";
	protected static final String OFFICER_DISPLAY_VALUE = "Frank Shunneltestone";
	protected static final Integer ASYNC_DEPENDENCY_TEST_TIMEOUT = 60;
	
	protected static String businessServicesRestUrl;//pole
	protected static String customBusinessServicesRestUrl;//'custom' bs (freestanding)
	
	/**
	 * Business services that are based on the POLE API.
	 * 
	 * Will be a PoleBusinessServicesClient wrapping either the RESTful end point
	 * or the SOAP endpoint depending on the value of jsonRestMode.
	 */
	protected static PoleBusinessServicesClient poleBusinessServices;	
	
	/**
	 * 'Freestanding' business services that are not a proxy to POLE.
	 * 
	 * Will be a BusinessServicesClient wrapping either the RESTful end point
	 * or the SOAP endpoint depending on the value of jsonRestMode.
	 */
	protected static BusinessServicesClient businessServices;
	
	/**
	 * To be used for asserting state, especially in JSON-mode testing, where full-richness of
	 * object graphs are not availiable over JSON/REST 'GET' calls through Business Services (unlike SOAP).
	 * 
	 * DO NOT USE FOR LOGIC BEING TESTED! Obviously, it by-passes the Business Services layer.
	 */
	protected static Pole poleDirect;	
	protected static Pole poleMetadataDirect;	
	
	/**
	 * For querying effects of Workflow-based operations.
	 */
	protected static POLEflowSoap poleFlowSoapService;
	
	protected static SecuritymessagesPort securityService;
	protected static SavingOfDataPort savingOfDataService;
	protected static AttachmentPort attachmentsService;
	
	protected static String extendedMetadataLocation;
	
	protected static String contactLogEntry;
	protected static String contactLogEntryForIntelReport;
	protected static String contactLogEntryForPpe;
	protected static String contactLogEntryType;
	
	protected static String taskStaffId;
	protected static String taskUnitName;
	protected static String unitCode;
	protected static String taskStaffName;
	protected static String taskForce;
	protected static Integer officerReportingId;

	protected static String busSvcsFlagTeamNameForFuncTest;
	protected static Integer employeeId;

	protected static boolean jsonRestMode;

	protected static boolean localMode;
	protected static boolean runWorkflowTests;
	protected static boolean runBladeTests;
	protected static String attachmentKeystoreLocation;

	protected static ApplicationContext context;
	protected static ApplicationContext bsContext;	
	protected static CamelContext bsLocalModeCamelContext;

	protected static BusinessServicesTestUtils busSvcUtils;
	protected static IncidentTestUtils utils;
	protected static ContactEventTestUtils contils;	
	protected static PpeTestUtils ppetils;
	protected static IntelReportTestUtils intelUtils;
	protected static PoleFlowTestUtils poleFlowUtils;
	protected static GemCaseTestUtils gemCaseUtils;
	protected static BladeTestUtils bladeTestUtils;
	protected static BriefingItemTestUtils briefingTestUtils;
	protected static StopSearchTestUtils stopSearchTestUtils;
	protected static IncidentNoteTestUtils incidentNoteTestUtils;
		
	/**
	 * Security context used for typical operations.
	 */
	protected String securityContextId;
	
	/**
	 * Security context where permissions are known to restrict to view-only - for testing some security/permissions scenarios.
	 */
	protected String viewOnlySecurityContextId;
	
	/**
	 * Security creds for talking to POLEFlow. Lifecycle is chained to securityContextId for the tests.
	 */
	protected Security poleFlowSecurityToken;
	
	/**
	 * Created afresh for each test, with reasonable confidence of uniqueness.
	 */
	protected String peAccountRef;
	
	/**
	 * Created afresh for each test, with reasonable confidence of uniqueness.
	 */
	protected String peReference;
	
	private static final SimpleDateFormat systemMillisFormat = new SimpleDateFormat("yyyyDDDHHmmssSSS");
	
	protected Integer defaultBetweenAttemptsToCallWorkflow = 2000; 
	
	protected static Integer employeeIterationId;
	
	protected static CvListCacheBean cvListDataCacheDataBean;						
	
	protected static SystemParamsCacheBean systemParamsCacheBean;
	
	protected static AuxiliaryDataCacheBean auxiliaryDataCacheBean;
	
	protected static LinkTypeCacheBean linkTypeCacheBean;
	
	protected static LinkTypeByLinkTagCacheBean linkTypeByLinkTagCacheBean;
	
	protected static UnresearchedLinkExemptHelper unresearchedLinkExemptHelper;

	protected static ProfileRegistry profileRegistry;
	
	protected static AuxiliaryDataCacheBean validatorAuxiliaryDataCacheBean;
	
	protected static String poleUsername;
	protected static String polePassword;
	
	protected static String businessServiceTrustedComponentSecurityContextId;
	
	protected static final String generatePseudoUniqueTimestampString() {
		return systemMillisFormat.format(new Date());
	}

	@Before
	public final void setup() throws Exception {
		
		if (context == null) {
			doStaticSetup(logger);
		}
		
		if (securityContextId == null) {
			createMainSecurityContextId();
		}
		
		if (viewOnlySecurityContextId == null) {
			createViewOnlySecurityContextId();
		}
		
		if (poleFlowSecurityToken == null) {
			poleFlowSecurityToken = new Security();
			poleFlowSecurityToken.setUsername(PoleFlowTestUtils.USERNAME);
			poleFlowSecurityToken.setPasswordHash(securityContextId);
		}
		
		peAccountRef = generatePeAccountRef(); 
		peReference = generatePeReference();
		
		// set the Employee Iteration Id from EmployeId replace the Officer Reporting ID
		initEmployeeIterationId();
		// Setting ContactEventTestUtils and gemCaseUtils as these are using officerReportingId
		contils = new ContactEventTestUtils(owningForceId, officerReportingId);		
		gemCaseUtils = new GemCaseTestUtils(owningForceId, officerReportingId, unitCode);
		bladeTestUtils = new BladeTestUtils(owningForceId);
		
		createStaticValidator();

		//TODO WP347 - how handle 'viewOnlySecurityContextId' scenarios? :-(
		
		businessServices.setRequestInfoProvider(new RequestInfoProvider() {
				@Override
				public BusinessServiceRequestInfo getRequestInfo() {
					BusinessServiceRequestInfo reqInfo = new BusinessServiceRequestInfo();
					reqInfo.setSecurityContextId(securityContextId);
					return reqInfo;
				}
			});
		
		poleBusinessServices.setRequestHeaderProvider(new PoleRequestHeaderProvider() {
				@Override
				public RequestHeaderDto getPoleRequestHeader() {
					
					RequestHeaderDto header = new RequestHeaderDto();
					header.setPasswordHash(securityContextId);
					header.setClientName(PoleObjectsServiceConstants.BUSINESS_SERVICES_CLIENT_NAME);
					return header;
				}
			});
		
		setupImpl();
	}
	
	protected String generatePeAccountRef() {
		return PE_ACC_REF_PREFIX + generatePseudoUniqueTimestampString();
	}
	
	protected String generatePeReference() {
		return PE_REF_PREFIX + generatePseudoUniqueTimestampString();
	}
	
	/**
	 * To be implemented in concrete test sub-classes.
	 * @throws Exception
	 */
	protected void setupImpl() throws Exception {
	}
	
	protected void createStaticValidator() throws Exception {
		if (validator == null) {
			//Uses business services trusted component security context id for the retrievers, otherwise as
			//the security context is changed by certain tests, the security context passed to the retrievers
			//times out on a long test run.
			PoleMetadataCacheBean poleMetadataCache = utils.getPoleMetadataCacheAndConstraints(poleDirect,
					poleMetadataDirect, securityContextId, poleUsername, polePassword);		
			
			PoleExtendedMetadataCache poleExtendedMetadataCache = new PoleExtendedMetadataCache();
			poleExtendedMetadataCache.initialise(new File(extendedMetadataLocation), poleMetadataCache);

			AuxiliaryDataRetriever auxiliaryDataRetriever =
					new TestAuxiliaryDataRetriever(poleDirect, businessServiceTrustedComponentSecurityContextId, utils);

			RiskAssessmentTemplateDataCache riskAssessmentTemplateDataCache = new RiskAssessmentTemplateDataCache();									
			riskAssessmentTemplateDataCache.initialise(
					new TestRiskAssessmentAuxiliaryDataHelper(auxiliaryDataRetriever).getRiskAssessmentAuxiliaryDataMap());			
		
			validatorAuxiliaryDataCacheBean = new AuxiliaryDataCacheBean(auxiliaryDataRetriever);
			
			LinkTypeCacheBean linkTypeCache = new LinkTypeCacheBean(
					new TestLinkTypeRetriever(poleDirect, businessServiceTrustedComponentSecurityContextId, utils));
			
			LinkTypeByLinkTagCacheBean linkTypeByLinkTagCacheBean = new LinkTypeByLinkTagCacheBean(linkTypeCache);
			
			UnresearchedLinkExemptHelper unresearchedLinkExemptHelper = new UnresearchedLinkExemptHelper(
					linkTypeByLinkTagCacheBean, validatorAuxiliaryDataCacheBean);
			
			validator = new ExtendedMetadataValidator(poleExtendedMetadataCache,
					new CvListCacheBean(
							new TestCvListRetriever(poleDirect, businessServiceTrustedComponentSecurityContextId, utils)),
					validatorAuxiliaryDataCacheBean, riskAssessmentTemplateDataCache,
					new PatternCacheBean(
							new TestPatternRetriever(poleDirect, businessServiceTrustedComponentSecurityContextId, utils)),
					Arrays.asList("testnonvalidatedclient"), systemParamsCacheBean, unresearchedLinkExemptHelper);
		}
	}

	@After
	public final void teardown() throws Exception {
		teardownImpl();
		
		if (securityContextId == null) {
			poleFlowSecurityToken = null;
		}
	}

	/**
	 * To be implemented in concrete test sub-classes.
	 * @throws Exception
	 */
	protected void teardownImpl() throws Exception {
	}
	
	@AfterClass
	public final static void clearPoleflowTasks() throws Exception {
		
		if (BusinessServicesTestUtils.getObjectsDeletedForTests().size() > 1) {
			//TODO CON-40832
			/*String securityContextId = busSvcUtils.getSecurityContextId("Frank Shunneltestone", Arrays.asList("ATHENA_USER"),
					securityService);
			
			Security poleFlowSecurityToken = new Security();
			poleFlowSecurityToken.setUsername(PoleFlowTestUtils.USERNAME);
			poleFlowSecurityToken.setPasswordHash(securityContextId);
			
			for (EntityKey entityKey : BusinessServicesTestUtils.getObjectsDeletedForTests()) {
				
				StringBuffer taskNames = new StringBuffer();
				try {
					Holder<TaskList> actorTaskHolder = new Holder<TaskList>();
	
					poleFlowSoapService.getTasks(poleFlowSecurityToken, entityKey.getEntityType(),
							entityKey.getObjectRef().toString(), "AMO", "", null, actorTaskHolder);
	
					for (Task task : actorTaskHolder.value.getTask()) {
	
						// Role Name is the actual Internal task Name not good API from Assemble But we have use role name
						// for uniqueness
						if (taskNames.length() > 0) {
							taskNames.append(",");
						}
						taskNames.append(task.getRoleName());
					}
					
					ArrayOfLongStringPairOfxmlParamsKeyLongString xmlParams =
							new ArrayOfLongStringPairOfxmlParamsKeyLongString();
					
					PairOfxmlParamsKeyLongString xmlParam = new PairOfxmlParamsKeyLongString();
					xmlParam.setXmlParamsKey("PIRManagerActorName");
					xmlParam.setValue("AMO");
					xmlParams.getLongString().add(xmlParam);
					
					xmlParam = new PairOfxmlParamsKeyLongString();
					xmlParam.setXmlParamsKey("TaskPriority");
					xmlParam.setValue("1");
					xmlParams.getLongString().add(xmlParam);
					
					xmlParam = new PairOfxmlParamsKeyLongString();
					xmlParam.setXmlParamsKey("Version");
					xmlParam.setValue("2");
					xmlParams.getLongString().add(xmlParam);
					
					String res = poleFlowSoapService.completeTasks(poleFlowSecurityToken, entityKey.getEntityType(),
							entityKey.getObjectRef().toString(), "AMO", taskNames.toString(),
							xmlParams, "Assigned");
					
					if (res != null) {
						res = null;
					}
					
					actorTaskHolder = new Holder<TaskList>();
					
					poleFlowSoapService.getTasks(poleFlowSecurityToken, entityKey.getEntityType(),
							entityKey.getObjectRef().toString(), "AMO", "", null, actorTaskHolder);
					
					actorTaskHolder = null;
					
				} catch (Exception ignore) {
					taskNames = null;
				}
			}*/
			BusinessServicesTestUtils.clearObjectsDeletedForTests();
		}
	}
	
	/**
	 * Default only creates a new user when needed. Can be overridden in sub-classes to always create
	 * a new user for each test and/or to vary the roles assigned.
	 * 
	 * @throws Exception
	 */
	protected void createMainSecurityContextId() throws Exception {
		if (securityContextId == null) {
			securityContextId = busSvcUtils.getSecurityContextId("Frank Shunneltestone", Arrays.asList("ATHENA_USER"),
					securityService);
		}
	}

	/**
	 * Default only creates a new user when needed. Can be overridden in sub-classes to always create
	 * a new user for each test and/or to vary the roles assigned.
	 * 
	 * @throws Exception
	 */
	protected void createViewOnlySecurityContextId() throws Exception {
		if (viewOnlySecurityContextId == null) {
			viewOnlySecurityContextId = busSvcUtils.getSecurityContextId("Frank Shunneltesttwo", Arrays.asList("ViewOnly"),
					securityService);
		}
	}
	
	private static void doStaticSetup(GemLogger logger) throws Exception {

		//(1) ENV
		//enable local mode by setting this env var to "true"
		localMode = "true".equals(System.getenv("localMode"));
		//enable running of workflow tests by setting this env var to "true"
		runWorkflowTests = "true".equals(System.getenv("runWorkflowTests"));
		//enable running of blade tests by setting this env var to "true"
		runBladeTests = "true".equals(System.getenv("runBladeTests"));
		
		//(2) COMMAND LINE
		if (!localMode) {
			//enable local mode by setting -DlocalMode=true on command line
			localMode = "true".equals(System.getProperty("localMode"));
		}
		if (!runWorkflowTests) {
			//enable running of workflow tests by setting -DrunWorkflowTests=true on command line
			runWorkflowTests = "true".equals(System.getProperty("runWorkflowTests"));
		}
		if (!runBladeTests) {
			//enable running of blade tests by setting -DrunBladeTests=true on command line
			runBladeTests = "true".equals(System.getProperty("runBladeTests"));
		}
		
		Properties properties = new Properties();
		properties.load(new FileInputStream(new File("../conf/gembusinessservicestest/gembusinessservices-functest-context.properties")));
		
		String attachmentKeystoreLocation = properties.getProperty("attachment-keystore-location");
		
		if (attachmentKeystoreLocation != null && (!"".equals(attachmentKeystoreLocation))) {
			logger.info("Using attachmentKeystoreLocation of " + attachmentKeystoreLocation + " for these tests obtained from config system.");
			
		} else {
			// Now try System Property [[[ -Dattachment-keystore-location="blah" ]]] on the command line
			attachmentKeystoreLocation = System.getProperty("attachment-keystore-location");
			
			if (attachmentKeystoreLocation != null && (!"".equals(attachmentKeystoreLocation))) {
				logger.info("Using attachmentKeystoreLocation of " + attachmentKeystoreLocation
						+ " for these tests obtained from System properties (command line -D parameter).");
				properties.setProperty("attachment-keystore-location", System.getProperty("attachment-keystore-location"));
			}
		}
		
		extendedMetadataLocation = properties.getProperty("test-extended-metadata-location");

		contactLogEntry = properties.getProperty("contactLogEntry");
		contactLogEntryForIntelReport = properties.getProperty("contactLogEntryForIntelReport");
		contactLogEntryForPpe = properties.getProperty("contactLogEntryForPpe");
		contactLogEntryType = properties.getProperty("contactLogEntryType");
		
		taskUnitName = properties.getProperty("taskUnitName");
		unitCode = properties.getProperty("unitCode");
		taskStaffName = properties.getProperty("taskStaffName");
		taskForce = properties.getProperty("taskForce");
		owningForceId = properties.getProperty("owningForceId");
		officerReportingId = Integer.parseInt(properties.getProperty("officerReportingId"));
		employeeId = officerReportingId;
		jsonRestMode = Boolean.parseBoolean(properties.getProperty("jsonRestMode"));

		poleUsername = properties.getProperty("poleMetaDefaultUserName");
		polePassword = properties.getProperty("poleMetaDefaultPassword");
		
		busSvcsFlagTeamNameForFuncTest = properties.getProperty("busSvcsFlagTeamNameForFuncTest");

		if (!jsonRestMode) {
			try {
				//This can be set from Hudson to do a JSON-mode test run.
				jsonRestMode = Boolean.parseBoolean(System.getProperty("jsonRestMode"));
				logger.info("Overridding from system property: jsonRestMode=" + jsonRestMode);
			} catch (Exception ex) {
				//ignore. No env variable set.
			}
		}
		
		DateTimeUtils.setTimeZone("GMT");

		//Setup utils:
		
		busSvcUtils = new BusinessServicesTestUtils(owningForceId);
		utils = new IncidentTestUtils(owningForceId);
		ppetils = new PpeTestUtils(owningForceId);
		intelUtils = new IntelReportTestUtils(owningForceId);
		poleFlowUtils = new PoleFlowTestUtils(Integer.valueOf(ASYNC_DEPENDENCY_TEST_TIMEOUT));
		//CODE REVIEW ACTION:REMOVED HARDCODING FOR OFFICERID
		briefingTestUtils = new BriefingItemTestUtils(owningForceId,officerReportingId);
		stopSearchTestUtils = new StopSearchTestUtils(owningForceId);
		incidentNoteTestUtils = new IncidentNoteTestUtils(owningForceId);
		
		//...add any further properties file values here if they need to be accessed from the test code...
		
		if (localMode) {
			//then start up the actual components we wish to call, in their "local mode" (see equivalent for PEQM).
			bsContext = new ClassPathXmlApplicationContext("classpath:gembusinessservices-local-functest-support-context.xml");
			systemParamsCacheBean = bsContext.getBean("systemParamsCacheBean", SystemParamsCacheBean.class);
			auxiliaryDataCacheBean = bsContext.getBean("auxiliaryDataCacheBean", AuxiliaryDataCacheBean.class);
			profileRegistry = bsContext.getBean("profileRegistry", ProfileRegistry.class);
			unresearchedLinkExemptHelper = bsContext.getBean("unresearchedLinkExemptHelper", UnresearchedLinkExemptHelper.class);
			linkTypeCacheBean = bsContext.getBean("linkTypeCacheBean", LinkTypeCacheBean.class);
			linkTypeByLinkTagCacheBean = bsContext.getBean("linkTypeByLinkTagCacheBean", LinkTypeByLinkTagCacheBean.class);
			bsLocalModeCamelContext = bsContext.getBean("camel", CamelContext.class);
		}
		
		context = new ClassPathXmlApplicationContext("classpath:gembusinessservices-functest-context.xml");

		poleMetadataDirect = context.getBean("poleServiceForMetadata", Pole.class);
		poleDirect = context.getBean("poleService", Pole.class);
		securityService = context.getBean("securityService", SecuritymessagesPort.class);
		poleFlowSoapService = context.getBean("poleFlowSoapService", POLEflowSoap.class);
		savingOfDataService = context.getBean("savingOfDataService", SavingOfDataPort.class);
		cvListDataCacheDataBean = context.getBean("cvListCacheBean", CvListCacheBean.class);		
		attachmentsService = context.getBean("attachmentsService", AttachmentPort.class);		
		//...add any further references to Spring Context beans or Camel Context routes or endpoints here, as needed...				
		
		//Some tests are written for REST API only and will need to run regardless of whether the other
		//main services are being tested in JSON mode or in SOAP mode. Therefore always initialise
		//this property
		businessServicesRestUrl = properties.getProperty("businessServicesRestUrl");
		customBusinessServicesRestUrl = properties.getProperty("customBusinessServicesRestUrl");
		
		//SOAP OR REST...			
		if (jsonRestMode) {

			//POLE
			PoleBusinessServicesJsonRestClient poleBsJsonClient = new PoleBusinessServicesJsonRestClient();//TODO REQUEST HEADER PROVIDER
			poleBsJsonClient.setAddress(businessServicesRestUrl);
			poleBusinessServices = poleBsJsonClient;
			
			//BS
			BusinessServicesJsonRestClient bs = new BusinessServicesJsonRestClient();
			bs.setAddress(customBusinessServicesRestUrl);
			businessServices = bs;
					
		} else {
			
			//POLE
			Pole pole = context.getBean("poleBusinessServicesClient", Pole.class);
			poleBusinessServices = new PoleBusinessServicesSoapClient(pole);
			
			//BS
			BusinessServicesPort bsPort = context.getBean("businessServicesClient", BusinessServicesPort.class);
			businessServices = new BusinessServicesSoapClient(bsPort);
		}
		
		businessServiceTrustedComponentSecurityContextId = 
				busSvcUtils.getBusinessServicesTrustedComponentSecurityContextId(securityService);
	}

	protected boolean isJsonMode() {
		return jsonRestMode;
	}

	protected BusinessServiceResponseInfo getBusinessServiceResultInfo(PutPoleObjectsResponseDto res) {
		return PoleDtoUtils.extractBusinessServiceResultInfo(res);
	}

	protected String getIncidentRef(PutPoleObjectsResponseDto response) {
		return IncidentPoleDtoUtils.extractIncidentRef(response);
	}

	protected IncidentDto getIncident(PutPoleObjectsRequestDto request) {
		return IncidentPoleDtoUtils.extractIncident(request);
	}
	
	protected Integer getIncidentObjectRef(PutPoleObjectsResponseDto response) {
		return IncidentPoleDtoUtils.extractIncidentObjectRef(response);
	}

	protected String getIntelReportRef(PutPoleObjectsResponseDto response) {
		return IntelReportPoleDtoUtils.extractIntelReportRef(response);
	}

	protected IntelligenceReportDto getIntelligenceReport(PutPoleObjectsRequestDto request) {
		return IntelReportPoleDtoUtils.extractIntelReport(request);
	}
	
	protected Integer getIntelReportObjectRef(PutPoleObjectsResponseDto response) {
		return IntelReportPoleDtoUtils.extractIntelReportObjectRef(response);
	}

	protected String getPpeRef(PutPoleObjectsResponseDto response) {
		return PpePoleDtoUtils.extractPpeRef(response);
	}

	protected PpeDto getPpe(PutPoleObjectsRequestDto request) {
		return PpePoleDtoUtils.extractPpe(request);
	}
	
	protected Integer getPpeObjectRef(PutPoleObjectsResponseDto response) {
		return PpePoleDtoUtils.extractPpeObjectRef(response);
	}
	
	/**
	 * Check the passed link has the same researched value as the poleObject passed, which would be the
	 * object reference by the "to" part of the link
	 * 
	 * @param link
	 * @param poleObject
	 * @throws Exception
	 */
	protected void checkLinkResearchedComparedToObject(LinkDto link, StaticObjectDto poleObject) throws Exception {
		if (poleObject.getResearched() != null && poleObject.getResearched()) {
			assertEquals("Expect link 'researched' field to match the equivalent value on the 'to' object" + link, 
				poleObject.getResearched(), link.getResearched());
		} else {
			//then the POLE object is unresearched...
			
			if (link.getResearched() != null && link.getResearched()) {
				fail("Researched link " + link.getLinkReason() + " pointing to an unresearched object "
							+ poleObject.getEntityKey());
			}
		}
	}
	
	protected boolean isRunWorkflowTests() {
		return !localMode || runWorkflowTests;
	}
	
	
	protected boolean isRunBladeTests() {
		return !localMode || runBladeTests;
	}

	/**
	 *  This method will set valid Iteration Id from EmployeeId  
	 */
	private void initEmployeeIterationId() {

		if (AbstractBusinessServicesFunctionalTestBase.employeeIterationId == null) {

			IntegerCriterionDto fieldCriterion = new IntegerCriterionDto();
			fieldCriterion.setFieldName("employeeId");
			fieldCriterion.setValue(officerReportingId);
			
			AuxiliaryDataCriteriaDto auxiliaryDataCriteria = new AuxiliaryDataCriteriaDto();
			auxiliaryDataCriteria.setEntityType(PoleNames.EMPLOYEE_ITERATION);
			auxiliaryDataCriteria.addFieldCriterion(fieldCriterion);
			
			GetAuxiliaryDataRequestDto getAuxiliaryDataRequest = new GetAuxiliaryDataRequestDto();
			getAuxiliaryDataRequest.setAuxiliaryDataCriteria(auxiliaryDataCriteria);

			GetAuxiliaryDataResponseDto getAuxiliaryDataResponse = busSvcUtils.getAuxiliaryData(poleDirect,
					securityContextId, getAuxiliaryDataRequest);

			for (AuxiliaryDataDto auxiliaryDataDto : getAuxiliaryDataResponse.getAuxiliaryDataList()) {

				EmployeeIterationDto employeeIteration = (EmployeeIterationDto) auxiliaryDataDto;
				if (employeeIteration.getValidToDate() == null
						|| employeeIteration.getValidToDate().after(new Date())) {
					employeeIterationId = employeeIteration.getId();
					officerReportingId = employeeIterationId;
					taskStaffId = employeeIterationId.toString();
					break;
				}
			}
		}
	}
	
	/**
	 * When we set value to PoleTime field is ignores date and milliseconds. It only considers hours:minutes:seconds.
	 * To handle this scenario in FT for assertion added this method.
	 * 
	 * @return
	 * @throws ParseException
	 */
	protected PoleTime getCurrentPoleTimeIgnoringMilliseconds() throws ParseException {
		PoleDateTime currentDateTime = new PoleDateTime();		
		SimpleDateFormat sdf1 = new SimpleDateFormat();
		sdf1.applyPattern("HH:mm:ss"); 
		Date date = sdf1.parse(sdf1.format(currentDateTime));				
		PoleTime currentTime = new PoleTime(date);
		return currentTime;
	}
	
	protected BusinessServiceValidationErrors unmarshalBusinessServiceValidationErrorsFromXml(String xml) {

		if (xml.contains("<?xml")) {
			xml = xml.substring(xml.indexOf("<?xml"), xml.lastIndexOf(">") + 1);
		}
		BusinessServiceValidationErrors businessServiceValidationErrors = null;

		if (xml != null && !xml.equals("")) {

			businessServiceValidationErrors = JaxbXmlMarshaller.convertFromXml(xml,
					BusinessServiceValidationErrors.class);
		}
		return businessServiceValidationErrors;

	}
}
