package com.northgateis.gem.bussvc.poleobjects.appliedlogic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestWatcher;
import org.springframework.context.ApplicationContext;
import org.w3c.dom.Document;

import com.northgateis.gem.bussvc.extendedmetadata.PoleExtendedMetadataCache;
import com.northgateis.gem.bussvc.extendedmetadata.schema.ExtendedFieldMetadata;
import com.northgateis.gem.bussvc.extendedmetadata.schema.PoleEntityRuleset;
import com.northgateis.gem.bussvc.extendedmetadata.validation.ExtendedMetadataValidator;
import com.northgateis.gem.bussvc.framework.FrameworkServiceConstants;
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
import com.northgateis.gem.bussvc.pole.pattern.PatternRetriever;
import com.northgateis.gem.bussvc.security.SecuritymessagesPort;
import com.northgateis.gem.bussvc.test.retrievers.auxiliarydata.TestAuxiliaryDataRetriever;
import com.northgateis.gem.bussvc.test.retrievers.auxiliarydata.TestRiskAssessmentAuxiliaryDataHelper;
import com.northgateis.gem.bussvc.test.retrievers.cvlist.TestCvListRetriever;
import com.northgateis.gem.bussvc.test.retrievers.linktype.TestLinkTypeRetriever;
import com.northgateis.gem.bussvc.test.retrievers.pattern.TestPatternRetriever;
import com.northgateis.gem.bussvc.test.util.BusinessServicesTestUtils;
import com.northgateis.gem.bussvc.test.util.IntelReportTestUtils;
import com.northgateis.gem.framework.util.CamelPlatform;
import com.northgateis.gem.framework.util.logger.GemLogger;
import com.northgateis.pole.common.DateTimeUtils;
import com.northgateis.pole.common.PoleNames;
import com.northgateis.pole.schema.AuxiliaryDataCriteriaDto;
import com.northgateis.pole.schema.EmployeeIterationDto;
import com.northgateis.pole.schema.FieldTypeDto;
import com.northgateis.pole.schema.GetAuxiliaryDataRequestDto;
import com.northgateis.pole.schema.GetAuxiliaryDataResponseDto;
import com.northgateis.pole.schema.IntegerCriterionDto;
import com.northgateis.pole.schema.ModificationStatusDto;
import com.northgateis.pole.schema.Pole;
import com.northgateis.pole.schema.PutAuxiliaryDataRequestDto;
import com.northgateis.pole.schema.PutPoleObjectsRequestDto;
import com.northgateis.pole.schema.RequestHeaderDto;
import com.northgateis.pole.schema.RiskAssessmentTemplateDto;
import com.northgateis.pole.schema.RiskAssessmentTemplateObjectDto;
import com.northgateis.pole.schema.RiskAssessmentTemplateQuestionDto;
import com.northgateis.pole.schema.RiskAssessmentTemplateVersionConditionDto;
import com.northgateis.pole.schema.RiskAssessmentTemplateVersionConditionSetDto;
import com.northgateis.pole.schema.RiskAssessmentTemplateVersionDto;
import com.northgateis.pole.schema.RiskAssessmentTemplateVersionQuestionDto;
import com.northgateis.pole.schema.StringCriterionDto;

/**
 * Common base class for all functional tests relating to extended metadata validation
 * 
 * @author harsh.shah
 *
 * TODO CCI-42871: Class is deprecated now. Should not be used anywhere. I will remove this class once checked in 
 * changes build success and works fine.
 */
@Deprecated
public abstract class AbstractExtendedMetadataValidationFuncTestBase {

	public static final String FUNC_TEST_SPRING_CONTEXT_LOCATION = "classpath:gembusinessservices-functest-context.xml";

	static {
		GemLogger.setFactory(CamelPlatform.getInstance());
	}

	protected final static GemLogger logger = GemLogger.getLogger(AbstractExtendedMetadataValidationFuncTestBase.class);

	/**
	 * Records / monitors the coverage annotations for reporting purposes.
	 */
	@Rule
	public TestWatcher testWatcher = new BusSvcTestWatcher();

	protected static String owningForceId;

	protected static String extendedMetadataLocation;

	protected static BusinessServicesTestUtils utils;
	
	protected static IntelReportTestUtils intelUtils;

	protected static SecuritymessagesPort securityService;

	protected static Pole poleDirect;
	
	protected static Pole poleDirectForMetadata;

	protected static PoleMetadataCacheBean poleMetadataCache;

	protected static ApplicationContext context;
	
	protected static CamelContext camelContext;
	
	protected static ProducerTemplate producer;
	
	protected static MockEndpoint mockAfter;

	protected static String securityContextId;

	protected static AuxiliaryDataRetriever auxiliaryDataRetriever;

	protected static TestRiskAssessmentAuxiliaryDataHelper riskAssessmentAuxiliaryDataHelper;

	protected static Properties properties;

	protected static boolean isTemplateExists = false;

	protected static PoleExtendedMetadataCache poleExtendedMetadataCache;

	protected static ExtendedMetadataValidator validator;

	protected static PutPoleObjectsRequestDto ppoRequest;

	protected static PutPoleObjectsRequestDto ppoRequestForXpath;

	protected static AuxiliaryDataCacheBean auxiliaryDataCacheBean;

	protected static LinkTypeCacheBean linkTypeCache;
	
	protected static LinkTypeByLinkTagCacheBean linkTypeByLinkTagCacheBean;

	protected static UnresearchedLinkExemptHelper unresearchedLinkExemptHelper;

	protected static TestLinkTypeRetriever linkTypeRetriever;

	protected static TestCvListRetriever cvListRetriever;

	protected static CvListCacheBean cvListCacheBean;

	protected static PatternCacheBean patternCacheBean;

	protected static SystemParamsCacheBean systemParamsCacheBean;

	protected static Integer officerReportingId;

	protected static RiskAssessmentTemplateDataCache riskAssessmentTemplateDataCache;

	protected static PatternRetriever patternRetriever;

	@Before
	public void setup() throws Exception {
		initStaticStuff();
		initValidatorAndRelatedStuff();
		if (riskAssessmentTemplateDataCache == null && getXmlFileName() != null && getXmlFileName().get(0).contains("riskassessmentsvalidation")) {
			riskAssessmentTemplateDataCache = new RiskAssessmentTemplateDataCache();
			riskAssessmentTemplateDataCache.initialise(
					riskAssessmentAuxiliaryDataHelper.getRiskAssessmentAuxiliaryDataMap());
		}
	}

	/**
	 * @throws Exception
	 */
	private static final void initValidatorAndRelatedStuff() throws Exception {
		if (validator == null) {
			if (poleExtendedMetadataCache == null) {
				poleExtendedMetadataCache = new PoleExtendedMetadataCache();
				poleExtendedMetadataCache.initialise(new File(extendedMetadataLocation), poleMetadataCache);
			}

			auxiliaryDataCacheBean = new AuxiliaryDataCacheBean(auxiliaryDataRetriever);

			linkTypeRetriever = new TestLinkTypeRetriever(poleDirect, securityContextId, utils);
			linkTypeCache = new LinkTypeCacheBean(linkTypeRetriever);

			linkTypeByLinkTagCacheBean = new LinkTypeByLinkTagCacheBean(linkTypeCache);
			
			unresearchedLinkExemptHelper = new UnresearchedLinkExemptHelper(linkTypeByLinkTagCacheBean, auxiliaryDataCacheBean);

			cvListRetriever = new TestCvListRetriever(poleDirect, securityContextId, utils);
			cvListCacheBean = new CvListCacheBean(cvListRetriever);

			patternCacheBean = new PatternCacheBean(patternRetriever);

			systemParamsCacheBean = new SystemParamsCacheBean(auxiliaryDataRetriever);

			validator = new ExtendedMetadataValidator(poleExtendedMetadataCache, cvListCacheBean,
					auxiliaryDataCacheBean, riskAssessmentTemplateDataCache, patternCacheBean, new ArrayList<String>(),
					systemParamsCacheBean, unresearchedLinkExemptHelper);
		}
	}

	protected List<String> getXmlFileName(){
		//TODO:override this whenever needed; not restricted for every implementation
		return null;
		}

	private static final void initStaticStuff() throws Exception {

		// Savvy optimization on duplicated initialization
		if (context == null) {
			DateTimeUtils.setTimeZone("GMT");

			properties = new Properties();
			properties.load(new FileInputStream(
					new File("../conf/gembusinessservicestest/gembusinessservices-functest-context.properties")));

			context = new ClassPathXmlApplicationContext(FUNC_TEST_SPRING_CONTEXT_LOCATION);

			owningForceId = properties.getProperty("owningForceId");
			officerReportingId = Integer.parseInt(properties.getProperty("officerReportingId"));
			extendedMetadataLocation = properties.getProperty("test-extended-metadata-location");

			utils = new BusinessServicesTestUtils(owningForceId);
			intelUtils = new IntelReportTestUtils(owningForceId);
			
			securityService = context.getBean("securityService", SecuritymessagesPort.class);
			poleDirect = context.getBean("poleService", Pole.class);
			//TODO SANKET
			poleDirectForMetadata = context.getBean("poleServiceForMetadata", Pole.class);
			
			if (securityContextId == null) {
				securityContextId = utils.getSecurityContextId("Frank Shunneltestone", Arrays.asList("ATHENA_USER"),
						securityService);
			}

			String poleUsername = properties.getProperty("poleMetaDefaultUserName");
			String polePassword = properties.getProperty("poleMetaDefaultPassword");
			
			if (poleMetadataCache == null) {
				poleMetadataCache = utils.getPoleMetadataCacheAndConstraints(
						poleDirect, poleDirectForMetadata, securityContextId, poleUsername, polePassword);
			}

			auxiliaryDataRetriever = new TestAuxiliaryDataRetriever(poleDirect, securityContextId, utils);
			patternRetriever = new TestPatternRetriever(poleDirect, securityContextId, utils);
			riskAssessmentAuxiliaryDataHelper = new TestRiskAssessmentAuxiliaryDataHelper(auxiliaryDataRetriever);

			// If block to check if the required RiskAssessmentTemplate data used in validation tests exists on the env.
			// If not then create the required data.
			if (!isTemplateExists) {
				if (checkIfTestTemplateExist()) {
					isTemplateExists = true;
				}
				if (!isTemplateExists) {
					createRequiredTestRiskAssessmentTemplateData();
					isTemplateExists = true;
				}
			}
			
		}
	}

	/**
	 * Test method to create required RiskAssessment template data for RiskAssessmentDataValidation Tests
	 */
	@SuppressWarnings("unchecked")
	private static void createRequiredTestRiskAssessmentTemplateData() {
		try {
			PutAuxiliaryDataRequestDto putAuxDataRequest = new PutAuxiliaryDataRequestDto();
			putAuxDataRequest.setHeader(getPoleRequestHeader());

			List<RiskAssessmentTemplateDto> riskAssessmentTemplates = (List<RiskAssessmentTemplateDto>) riskAssessmentAuxiliaryDataHelper.getRiskAssessmentTemplate(
					false);
			for (RiskAssessmentTemplateDto riskAssessmentTemplate : riskAssessmentTemplates) {
				riskAssessmentTemplate.setModificationStatus(ModificationStatusDto.CREATE);
			}
			putAuxDataRequest.addAuxiliaryDataList(riskAssessmentTemplates);

			List<RiskAssessmentTemplateObjectDto> riskAssessmentTemplateObjects = (List<RiskAssessmentTemplateObjectDto>) riskAssessmentAuxiliaryDataHelper.getRiskAssessmentTemplateObject(
					false);
			for (RiskAssessmentTemplateObjectDto riskAssessmentTemplateObject : riskAssessmentTemplateObjects) {
				riskAssessmentTemplateObject.setModificationStatus(ModificationStatusDto.CREATE);
			}
			putAuxDataRequest.addAuxiliaryDataList(riskAssessmentTemplateObjects);

			List<RiskAssessmentTemplateQuestionDto> riskAssessmentTemplateQuestions = (List<RiskAssessmentTemplateQuestionDto>) riskAssessmentAuxiliaryDataHelper.getRiskAssessmentTemplateQuestion(
					false);
			for (RiskAssessmentTemplateQuestionDto riskAssessmentTemplateQuestion : riskAssessmentTemplateQuestions) {
				riskAssessmentTemplateQuestion.setModificationStatus(ModificationStatusDto.CREATE);
			}
			putAuxDataRequest.addAuxiliaryDataList(riskAssessmentTemplateQuestions);

			List<RiskAssessmentTemplateVersionDto> riskAssessmentTemplateVersions = (List<RiskAssessmentTemplateVersionDto>) riskAssessmentAuxiliaryDataHelper.getRiskAssessmentTemplateVersion(
					false);
			for (RiskAssessmentTemplateVersionDto riskAssessmentTemplateVersion : riskAssessmentTemplateVersions) {
				riskAssessmentTemplateVersion.setModificationStatus(ModificationStatusDto.CREATE);
			}
			putAuxDataRequest.addAuxiliaryDataList(riskAssessmentTemplateVersions);

			List<RiskAssessmentTemplateVersionConditionDto> riskAssessmentTemplateVersionConditions = (List<RiskAssessmentTemplateVersionConditionDto>) riskAssessmentAuxiliaryDataHelper.getRiskAssessmentTemplateVersionCondition(
					false);
			for (RiskAssessmentTemplateVersionConditionDto riskAssessmentTemplateVersionCondition : riskAssessmentTemplateVersionConditions) {
				riskAssessmentTemplateVersionCondition.setModificationStatus(ModificationStatusDto.CREATE);
			}
			putAuxDataRequest.addAuxiliaryDataList(riskAssessmentTemplateVersionConditions);

			List<RiskAssessmentTemplateVersionConditionSetDto> riskAssessmentTemplateVersionConditionSets = (List<RiskAssessmentTemplateVersionConditionSetDto>) riskAssessmentAuxiliaryDataHelper.getRiskAssessmentTemplateVersionConditionSet(
					false);
			for (RiskAssessmentTemplateVersionConditionSetDto riskAssessmentTemplateVersionConditionSet : riskAssessmentTemplateVersionConditionSets) {
				riskAssessmentTemplateVersionConditionSet.setModificationStatus(ModificationStatusDto.CREATE);
			}
			putAuxDataRequest.addAuxiliaryDataList(riskAssessmentTemplateVersionConditionSets);

			List<RiskAssessmentTemplateVersionQuestionDto> riskAssessmentTemplateVersionQuestions = (List<RiskAssessmentTemplateVersionQuestionDto>) riskAssessmentAuxiliaryDataHelper.getRiskAssessmentTemplateVersionQuestion(
					false);
			for (RiskAssessmentTemplateVersionQuestionDto riskAssessmentTemplateVersionQuestion : riskAssessmentTemplateVersionQuestions) {
				riskAssessmentTemplateVersionQuestion.setModificationStatus(ModificationStatusDto.CREATE);
			}
			putAuxDataRequest.addAuxiliaryDataList(riskAssessmentTemplateVersionQuestions);

			poleDirect.putAuxiliaryData(putAuxDataRequest);

		} catch (Exception e) {
			logger.error("Error while creating RiskAssessment template data for RiskAssessmentValidation Tests: ", e);
		}
	}

	/**
	 * Test method to check if required RiskAssessment template data exists or not for RiskAssessmentDataValidation
	 * Tests on the environment.
	 * 
	 * @return
	 */
	private static boolean checkIfTestTemplateExist() {
		GetAuxiliaryDataRequestDto getAuxDataRequest = new GetAuxiliaryDataRequestDto();
		getAuxDataRequest.setHeader(getPoleRequestHeader());

		AuxiliaryDataCriteriaDto getAuxDataCriteria = new AuxiliaryDataCriteriaDto();
		getAuxDataCriteria.setEntityType(PoleNames.RISK_ASSESSMENT_TEMPLATE);
		StringCriterionDto templateCodeCriteria = new StringCriterionDto();
		templateCodeCriteria.setFieldName("templateCode");
		templateCodeCriteria.setValue(RiskAssessmentTemplateDataCache.RA_BS_FT_TEMPLATE_CODE);
		getAuxDataCriteria.addFieldCriterion(templateCodeCriteria);

		getAuxDataRequest.setAuxiliaryDataCriteria(getAuxDataCriteria);
		GetAuxiliaryDataResponseDto raTemplateData = poleDirect.getAuxiliaryData(getAuxDataRequest);

		if (raTemplateData.getAuxiliaryDataList().size() > 0) {
			return true;
		}
		return false;
	}

	/**
	 * @return
	 */
	private static RequestHeaderDto getPoleRequestHeader() {
		RequestHeaderDto requestHeader = new RequestHeaderDto();
		requestHeader.setUsername("");
		requestHeader.setPasswordHash(securityContextId);
		requestHeader.setClientName(FrameworkServiceConstants.BUSINESS_SERVICES_CLIENT_NAME);
		return requestHeader;
	}

	@After
	public void teardown() {
	}

	protected Object getXPathValue(PutPoleObjectsRequestDto ppoRequest, String xpathExpr, QName xpathType)
			throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = null;
		String stringPpoRequest = JaxbXmlMarshaller.convertToXml(ppoRequest, PutPoleObjectsRequestDto.class);
		try (ByteArrayInputStream is = new ByteArrayInputStream(stringPpoRequest.getBytes())) {
			doc = builder.parse(is);
		}
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		XPathExpression expr = xpath.compile(xpathExpr);
		return expr.evaluate(doc, xpathType);
	}

	protected int getExpiredEmployeeIterationId() {

		int expiredEmployeeId = 1;

		IntegerCriterionDto fieldCriterion = new IntegerCriterionDto();
		fieldCriterion.setFieldName("id");
		fieldCriterion.setValue(expiredEmployeeId);

		AuxiliaryDataCriteriaDto auxiliaryDataCriteria = new AuxiliaryDataCriteriaDto();
		auxiliaryDataCriteria.setEntityType(PoleNames.EMPLOYEE_ITERATION);
		auxiliaryDataCriteria.addFieldCriterion(fieldCriterion);

		GetAuxiliaryDataRequestDto getAuxiliaryDataRequest = new GetAuxiliaryDataRequestDto();
		getAuxiliaryDataRequest.setAuxiliaryDataCriteria(auxiliaryDataCriteria);

		GetAuxiliaryDataResponseDto getAuxiliaryDataResponse = auxiliaryDataRetriever.getAuxiliaryData(
				getAuxiliaryDataRequest);
		while (expiredEmployeeId < 100) {
			if (getAuxiliaryDataResponse.getAuxiliaryDataList().size() > 0) {
				EmployeeIterationDto employeeIteration = (EmployeeIterationDto) getAuxiliaryDataResponse.getAuxiliaryDataList().get(
						0);

				if (employeeIteration.getValidToDate() != null
						&& employeeIteration.getValidToDate().before(new Date())) {
					return expiredEmployeeId;
				}
			}

			expiredEmployeeId++;
			fieldCriterion.setValue(expiredEmployeeId);

			getAuxiliaryDataResponse = auxiliaryDataRetriever.getAuxiliaryData(getAuxiliaryDataRequest);
		}
		return -1;
	}

	protected void checkFieldsInScope(PoleEntityRuleset ruleset, String... fields) throws Exception {

		Set<String> fieldsSet = new HashSet<String>();
		for (int i = 0; i < fields.length; i++) {
			fieldsSet.add(fields[i]);
		}

		boolean incorrectFieldsInScope = false;
		for (ExtendedFieldMetadata fieldMetadata : ruleset.getFields().getField()) {
			assertNotNull("Poles field metadata should be set", fieldMetadata.getFieldMetadata());
			if (fieldsSet.contains(fieldMetadata.getFieldName())) {
				fieldsSet.remove(fieldMetadata.getFieldName());
				if (FieldTypeDto.STRING.equals(fieldMetadata.getFieldMetadata().getFieldType())) {
					checkStringFieldWidth(fieldMetadata);
				}
			} else {
				incorrectFieldsInScope = true;
			}
		}
		assertEquals("Fields " + fieldsSet + " expected to be in scope", 0, fieldsSet.size());
		assertFalse("Unexpected fields in scope", incorrectFieldsInScope);
	}

	protected void checkStringFieldWidth(ExtendedFieldMetadata fieldMetadata) throws Exception {
		if (fieldMetadata.getFieldMetadata().getLength() != null) {
			assertEquals("The length of the field in the extended metadata should match pole metadata length",
					fieldMetadata.getFieldWidth(), fieldMetadata.getFieldMetadata().getLength());
		}
	}	
}
