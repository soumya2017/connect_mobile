package com.northgateis.gem.bussvc.submitcase.functest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import org.apache.camel.ExchangePattern;
import org.apache.camel.ProducerTemplate;
import org.junit.Before;
import org.junit.Test;

import com.northgateis.gem.bussvc.framework.FrameworkInternalConstants;
import com.northgateis.gem.bussvc.framework.security.SecurityContext;
import com.northgateis.gem.bussvc.framework.test.BusSvcStoryAcceptanceCriteriaReference;
import com.northgateis.gem.bussvc.pole.utils.PoleDtoUtils;
import com.northgateis.gem.bussvc.poleobjects.functest.AbstractPoleObjectsFuncTestBase;
import com.northgateis.gem.bussvc.test.util.ContactEventTestUtils;
import com.northgateis.gem.bussvc.test.util.GemCaseTestUtils;
import com.northgateis.pole.common.PoleNames;
import com.northgateis.pole.schema.GemCaseDto;
import com.northgateis.pole.schema.GemCasePersonInfoDto;
import com.northgateis.pole.schema.GemCasePersonUpdateDto;
import com.northgateis.pole.schema.MessageExtraInfoDto;
import com.northgateis.pole.schema.PutPoleObjectsRequestDto;
import com.northgateis.pole.schema.PutPoleObjectsResponseDto;
import com.northgateis.pole.schema.RequestHeaderDto;

/**
 * Func test for updating case from Public engagement
 * 
 * After discussion with Dan about why the other data consolidation tests dont use specific routes when testing
 * data consolidation like this class does, it became apparant when writing the other data consolidation tests
 * that using the specific route causes issues, so Dan has asked that the approach taken in this class is not
 * copied, but to use the approach in the other data consolidation tests.
 * 
 * @author josesevilla.escalona
 *
 */
public class UpdateCaseFromPublicEngagementFuncTest extends AbstractPoleObjectsFuncTestBase {
	
	private static final String WORKFLOW_ACK_TASK = "Acknowledge Public Engagement Update Notification";
	private static final String WORKFLOW_NAME = "Start Ack PE Update Notification";
	
	private static final String DATA_CONSOLIDATION_ENDPOINT = "direct:updateGemCaseDataConsolidation";
	private static final String ADD_WORKFLOW_HEADER_ENDPOINT = "direct:updateGemCaseWorkflow";
	public static final String SYS_PARAM_UPDATES_FROM_PE = "UPDATE_FROM_PE";
	public static final String SYS_PARAM_PE_UPDATE_REASON = "PE_UPDATE_REASON";
	
	private String peReference = null;
	private Integer gemCaseObjectRef = null;

	private boolean expectWorkflow;
	
	private SecurityContext securityContext = null;
	
	ProducerTemplate dataConsolidationTemplate = null;
	ProducerTemplate poleFlowHeaderTemplate = null;
	
	/**
	 * Overriding to provide more roles - needed for updating Cases.
	 * 
	 * @throws Exception
	 */
	@Override
	protected void createMainSecurityContextId() throws Exception {
		if (securityContextId == null) {
			securityContextId = busSvcUtils.getSecurityContextId("Frank Shunneltestone", 
				Arrays.asList("SGG-SUPERVISOR-WARRANTED-CONNECT", 
						"CJUCaseWorker1", "CJUCaseWorker2", "CJUCaseWorker3", 
						"AthenaAB54", "ATHENA_USER", 
						"NorthgateSystemAdmin", "SysAdmin1", "SysAdmin2"), securityService);
		}
	}
	
	@Before
	public void initTest() throws Exception {
		
		expectWorkflow = true;
		
		if (gemCaseObjectRef == null) {
			peReference = PE_REF_PREFIX + new Date();
			gemCaseObjectRef = createNewCase(peReference, true);
			try { Thread.sleep(10000); } catch (Exception ex) {} // Short term workaround for CCIS grabbing the lock!
			securityContext = new SecurityContext(securityContextId);
		}
		
		if (securityContext == null) {
			securityContext = new SecurityContext(securityContextId);
		}
		
		if (localMode) {
			if (dataConsolidationTemplate == null) {
				dataConsolidationTemplate = bsLocalModeCamelContext.createProducerTemplate();
				dataConsolidationTemplate.setDefaultEndpointUri(DATA_CONSOLIDATION_ENDPOINT);			
			}
			
			if (poleFlowHeaderTemplate == null) {
				poleFlowHeaderTemplate = bsLocalModeCamelContext.createProducerTemplate();
				poleFlowHeaderTemplate.setDefaultEndpointUri(ADD_WORKFLOW_HEADER_ENDPOINT);
			}
		}
	}
	
	/**
	 * Test that data consolidation route update the case with the 
	 * last case version on Pole and the PoleFlow header are added.
	 * 
	 * WARNING: Please don't do data consolidation test in this way in other unit tests.
	 */
	@Test
	public void testDataConsolidationAndWorkflowHeader() {		
		if (localMode) {
			// test data consolidation
			PutPoleObjectsRequestDto putFromPEQM = gemCaseUtils.createPEPutPoleObjectRequest(
					ContactEventTestUtils.createBusinessServiceInfo(securityContextId), gemCaseObjectRef);
			putFromPEQM.getHeader().setClientName("peqm");
			PutPoleObjectsRequestDto putFromDC =  testDataConsolidation(putFromPEQM);
			
			// test poleFlow header using the data consolidation result.
			testWorkflowHeaders(putFromDC);
		}		
	}	
	
	/**
	 * TODO #37186 make the 'mute non PEQM' functionality dependant on PPO Request not containing a new kind
	 * of TxData instead of relying on the clientName hack. See #37185 too.
	 * 
	 * Test that data consolidation route update the case with the 
	 * last case version on Pole and the PoleFlow header are added.
	 * 
	 * WARNING: Please don't do data consolidation test in this way in other unit tests.
	 */
	@Test
	public void testDataConsolidationAndNoWorkflowHeaderWhenClientNotPeqm() {
		if (localMode) {
			
			expectWorkflow = false;
			
			// test data consolidation
			PutPoleObjectsRequestDto putFromPEQM = gemCaseUtils.createPEPutPoleObjectRequest(
					ContactEventTestUtils.createBusinessServiceInfo(securityContextId), gemCaseObjectRef);
			putFromPEQM.setHeader(new RequestHeaderDto());
			PutPoleObjectsRequestDto putFromDC =  testDataConsolidation(putFromPEQM);
			
			// test poleFlow header using the data consolidation result.
			testWorkflowHeaders(putFromDC);
		}		
	}
	
	/**
	 * Test that the update case service route update the data on POLE and
	 * the workflow tasks are raised.
	 * @throws Exception 
	 */
	@Test
	@BusSvcStoryAcceptanceCriteriaReference(
			mingleRef=35457,
			mingleTitle="Bus Svcs- Updates from Public Engagement",
			acceptanceCriteriaRefs="CR1, CR2",
			given="A case linked to a witness/victim and this link has a PE Reference",
			when="The associated PE incident is updated with additional detals",
			then="A new entry is added to the case note with the details sent across from PE:"
				+ "  - entryDate = Current Date (GMT / BST needs to be considered) "
				+ "  - entryTime = Current Time (GMT / BST needs to be considered) "
				+ "  - entryType = This needs to be the value stored in new system parameter "
					+ "(PE_UPDATE_CASE, Initial value \"CASE NOTE\") "
				+ "  - entryText = Text from public engagement"
				+ "  - enteredByOfficerId = NULL."
				+ "A new task -‘Acknowledge Public Engagement Update Notification’ is rasied on PoleFlow"
		)
	public void testUpdateCaseHappyPath() throws Exception {
		
		PutPoleObjectsRequestDto putFromPEQM = gemCaseUtils.createPEPutPoleObjectRequest(
				ContactEventTestUtils.createBusinessServiceInfo(securityContextId), gemCaseObjectRef);
		putFromPEQM.getHeader().setClientName("peqm");
		poleBusinessServices.putPoleObjects(putFromPEQM);
		
		
		// check the information stored on Pole
		GemCaseDto gemCaseUpdated = gemCaseUtils.retrieveCaseFromPole(
				peReference, 5, poleDirect, securityContextId, false);		
		for (GemCasePersonInfoDto gemCasePersonInfo : gemCaseUpdated.getLinks().get(0).getGemCasePersonInfoList()) {
			for (GemCasePersonUpdateDto gemCasePersonUpdate : gemCasePersonInfo.getUpdates()) {
				if(gemCasePersonUpdate != null) {
					assertEquals("Case link Contact log method of update", systemParamsCacheBean.getSystemParameter(SYS_PARAM_UPDATES_FROM_PE),
							gemCasePersonUpdate.getMethodOfUpdate());
					assertEquals("Case link Contact log reason for update", systemParamsCacheBean.getSystemParameter(SYS_PARAM_PE_UPDATE_REASON),
							gemCasePersonUpdate.getReasonForUpdate());
					assertEquals("Case link Contact log text", "This is a test", gemCasePersonUpdate.getRemarksText());
					assertNull("Case enteredByOfficerId", gemCasePersonUpdate.getOfficerId());
				}
			}
		}
		
		if (localMode) {
			logger.info("Not running all assertions in this test because it has a "
				+ "dependency on workflow so should not be run during daytime/continuous build.");
			return;
		}

		// check that workflow tasks have been raised.		
		assertTrue("Workflow task has not been raised for " + GemCaseTestUtils.IAMUSER, 
				poleFlowUtils.isWorkflowTaskCreated(poleFlowSoapService, poleFlowSecurityToken, PoleNames.GEM_CASE,
						gemCaseObjectRef, WORKFLOW_ACK_TASK, GemCaseTestUtils.IAMUSER, securityContextId));
		
		assertTrue("Workflow task has not been raised for " + unitCode,				
				poleFlowUtils.isWorkflowTaskCreated(poleFlowSoapService, poleFlowSecurityToken, PoleNames.GEM_CASE,
						gemCaseObjectRef, WORKFLOW_ACK_TASK, unitCode, securityContextId));
	}

	/**
	 * Test that data consolidation route update the case with the 
	 * last case version on Pole.
	 * 
	 * WARNING: Please don't do data consolidation test in this way in other unit tests.
	 * 
	 * @return the poleGraph result after applying the data consolidation logic.
	 */
	private PutPoleObjectsRequestDto testDataConsolidation(PutPoleObjectsRequestDto putFromPEQM) {
		PutPoleObjectsRequestDto putResult = (PutPoleObjectsRequestDto) dataConsolidationTemplate.sendBodyAndProperty(
				DATA_CONSOLIDATION_ENDPOINT, ExchangePattern.InOut, putFromPEQM, 
				FrameworkInternalConstants.TRUSTED_COMPONENT_SECURITY_CONTEXT_MEX_PROP_KEY, securityContext);
		
		PoleDtoUtils.unflattenPoleGraph(putResult);
		
		assertNotNull("Data consolidation result", putResult);
		assertTrue("There is not one object in the poleGraph", putResult.getPoleObjects().size() == 1);
		assertTrue("The poleGraph should contains two records in related reference data list", 
				putResult.getRelatedReferenceDataList().size() == 2);
		
		GemCaseDto gemCase = (GemCaseDto)putResult.getPoleObjects().get(0);
		
		// check case info
		assertEquals("Case object ref", gemCaseObjectRef, gemCase.getObjectRef());
		// officer in charge has been populated with the global property officerReportingId
		assertEquals("officer in charge id", officerReportingId, gemCase.getOfficerInChargeId()); 
		assertEquals("Witness Care Unit Code", unitCode, gemCase.getWitnessCareUnitId());
		
		for (GemCasePersonInfoDto gemCasePersonInfo : gemCase.getLinks().get(0).getGemCasePersonInfoList()) {
			for (GemCasePersonUpdateDto gemCasePersonUpdate : gemCasePersonInfo.getUpdates()) {
				if(gemCasePersonUpdate != null) {
					assertEquals("Case link Contact log method of update", systemParamsCacheBean.getSystemParameter(SYS_PARAM_UPDATES_FROM_PE),
							gemCasePersonUpdate.getMethodOfUpdate());
					assertEquals("Case link Contact log reason for update", systemParamsCacheBean.getSystemParameter(SYS_PARAM_PE_UPDATE_REASON),
							gemCasePersonUpdate.getReasonForUpdate());
					assertEquals("Case link Contact log text", "This is a test", gemCasePersonUpdate.getRemarksText());
					assertNull("Case enteredByOfficerId", gemCasePersonUpdate.getOfficerId());
				}
			}
		}
		
		return putResult;
	}
	
	
	/**
	 * Check if the workflow headers are added in the poleGraph
	 */
	public void testWorkflowHeaders(PutPoleObjectsRequestDto putRequest) {
		PutPoleObjectsRequestDto putResult = (PutPoleObjectsRequestDto)
				poleFlowHeaderTemplate.sendBody(ADD_WORKFLOW_HEADER_ENDPOINT, ExchangePattern.InOut, putRequest);
		
		// create a map with extraInfo data to check poleFlow headers
		HashMap<String, String> extraInfoMap = new HashMap<>(); 
		int numberOfWorkflowDataXml = processExtraInfoAndPopulateMap(putResult, extraInfoMap);
		
		if (expectWorkflow) {
			
			// Check poleFlow header 
			String multipleWorkflowInfoIndex = extraInfoMap.get("multipleWorkflowInfoIndex");
			assertNotNull("multipleWorkflowInfoIndex was not defined", multipleWorkflowInfoIndex);
			assertTrue("the value in multipleWorkflowInfoIndex is not correct", multipleWorkflowInfoIndex.equalsIgnoreCase(
					workflowIndexValue(putRequest.getPoleObjects().get(0).getObjectRef())));
			assertTrue("There are not two workflowDataXml", numberOfWorkflowDataXml == 2);
			checkWorkflowDataXml(extraInfoMap.get("workflowDataXml1"), unitCode);
			
		} else {

			// Check poleFlow header 
			String multipleWorkflowInfoIndex = extraInfoMap.get("multipleWorkflowInfoIndex");
			assertNull("multipleWorkflowInfoIndex should not be defined", multipleWorkflowInfoIndex);
			assertEquals("There should be no workflowDataXml", 0, numberOfWorkflowDataXml);
		}
	}
		
	
	/**
	 * Create a new case linked to a person and the peContactInfo populated with the peReference.
	 * @param peReference
	 * @return
	 */
	private Integer createNewCase(String peReference, boolean witnessCare) {
		PutPoleObjectsRequestDto createRequest = gemCaseUtils.createPutPoleObjectRequest(
				ContactEventTestUtils.createBusinessServiceInfo(securityContextId), peReference, witnessCare);
		
		PutPoleObjectsResponseDto resp = poleBusinessServices.putPoleObjects(createRequest);
		assertNotNull(PoleDtoUtils.extractBusinessServiceResultInfo(resp).getTransactionId());
		assertTrue(resp.getObjectRefs().size() == 1);
		
		return resp.getObjectRefs().get(0);
	}
	
	
	/**
	 * Populate map with extraInfo data and return the number of workflowDataXml headers found.
	 */	
	private int processExtraInfoAndPopulateMap(PutPoleObjectsRequestDto poleReq, HashMap<String, String> extraInfoMap) {
		
		int numberOfWorkflowDataXml = 0;
		
		for (MessageExtraInfoDto extraInfo : poleReq.getExtraInfoList()) {
			if (extraInfo.getKey().equalsIgnoreCase("workflowDataXml")) {
				extraInfoMap.put(extraInfo.getKey() + numberOfWorkflowDataXml++, extraInfo.getValue());
			} else {
				extraInfoMap.put(extraInfo.getKey(), extraInfo.getValue());
			}
		}
		return numberOfWorkflowDataXml;
	}
	
	/**
	 * Check the workflowDataXml value.
	 * 
	 * @param workflowDataXml
	 * @param user
	 */
	private void checkWorkflowDataXml(String workflowDataXml, String user) {
		assertTrue("Wokflow name", workflowDataXml.contains("<workflowName>" + WORKFLOW_NAME + "</workflowName>"));
		assertTrue("actor name "+ user, workflowDataXml.contains(
				"<parametersItem parametersKey=\"AllocatedToActorName\">" + user +"</parametersItem>"));
	}
	
	/**
	 * Create the poleFlow index header value.
	 * 
	 * @param objectRef
	 * @return
	 */
	private String workflowIndexValue(int objectRef) {
		return "index=0;objecttype=gemCase;objectRef=" + objectRef 
				+ "|index=1;objectType=gemCase;objectRef=" + objectRef;
	}

}
