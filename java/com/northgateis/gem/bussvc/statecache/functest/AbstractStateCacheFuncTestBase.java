package com.northgateis.gem.bussvc.statecache.functest;

import java.util.ArrayList;
import java.util.List;

import com.northgateis.gem.bussvc.framework.utils.xml.JaxbXmlMarshaller;
import com.northgateis.gem.bussvc.poleobjects.functest.AbstractPoleObjectsFuncTestBase;
import com.northgateis.gem.framework.util.logger.GemLogger;
import com.northgateis.pole.client.PoleDtoBuildHelper;
import com.northgateis.pole.common.PoleNames;
import com.northgateis.pole.schema.GemCaseDto;
import com.northgateis.pole.schema.GetPoleObjectsAsXmlDocumentRequestDto;
import com.northgateis.pole.schema.GetPoleObjectsAsXmlDocumentResponseDto;
import com.northgateis.pole.schema.GetPoleObjectsRequestDto;
import com.northgateis.pole.schema.GetPoleObjectsResponseDto;
import com.northgateis.pole.schema.Pole;
import com.northgateis.pole.schema.PoleObjectCriteriaDto;
import com.northgateis.pole.schema.PoleObjectDto;
import com.northgateis.pole.schema.PoleObjectFieldSelectorDto;
import com.northgateis.pole.schema.PoleRequestDto;
import com.northgateis.pole.schema.PutPoleObjectsRequestDto;
import com.northgateis.pole.schema.PutPoleObjectsResponseDto;
import com.northgateis.pole.schema.RequestHeaderDto;
import com.northgateis.pole.schema.RetrievalTypeDto;

public abstract class AbstractStateCacheFuncTestBase extends AbstractPoleObjectsFuncTestBase {
	
	protected final static String PRIMARY_APP_CLIENT_NAME = "GUARDIAN";
	protected final static String SECONDARY_APP_CLIENT_NAME = "commonsvcs";
	
	protected final static String MEANINGLESS_TRANSACTION_DESCRIPTION = "blah";
	protected final GemLogger logger = GemLogger.getLogger(this.getClass());

	protected PutPoleObjectsResponseDto putPoleObjects(List<? extends PoleObjectDto> poleObjects, String clientName) {
		PutPoleObjectsRequestDto poleRequest = createPutPoleObjectsRequest(poleObjects, clientName);		
		PutPoleObjectsResponseDto response = poleBusinessServices.putPoleObjects(poleRequest);
		return response;
	}
	
	protected PutPoleObjectsResponseDto putPoleObject(PoleObjectDto poleObject, String clientName) {
		List<PoleObjectDto> poleObjects = new ArrayList<PoleObjectDto>();
		poleObjects.add(poleObject);
		return putPoleObjects(poleObjects, clientName);
	}
	
	protected PutPoleObjectsResponseDto putPoleObject(PoleObjectDto poleObject, Pole pole, String clientName) {
		List<PoleObjectDto> poleObjects = new ArrayList<PoleObjectDto>();
		poleObjects.add(poleObject);
		PutPoleObjectsRequestDto poleRequest = createPutPoleObjectsRequest(poleObjects, clientName);		
		PutPoleObjectsResponseDto response = pole.putPoleObjects(poleRequest);
		return response;
	}
	
	protected PutPoleObjectsResponseDto putPoleObject(Pole pole, PoleObjectDto poleObject, String clientName) {
		List<PoleObjectDto> poleObjects = new ArrayList<PoleObjectDto>();
		poleObjects.add(poleObject);
		PutPoleObjectsRequestDto poleRequest = createPutPoleObjectsRequest(poleObjects, clientName);
		return pole.putPoleObjects(poleRequest);
	}
	
	
	protected PutPoleObjectsRequestDto createPutPoleObjectsRequest(List<? extends PoleObjectDto> poleObjects, String clientName) {
		PutPoleObjectsRequestDto poleRequest = new PutPoleObjectsRequestDto();
		setHeaderOnRequest(poleRequest, clientName, MEANINGLESS_TRANSACTION_DESCRIPTION);
		poleRequest.addPoleObjects(poleObjects);
		return poleRequest;
	}
	
	protected GemCaseDto getCaseDirectlyFromPole(int caseObjectRef, String clientName) {
		PoleObjectCriteriaDto rootCriteriaDto = PoleDtoBuildHelper.buildPoleObjectCriteriaDto(PoleNames.GEM_CASE, PoleDtoBuildHelper.buildIntegerCriterionDto("objectRef", caseObjectRef));
		List<? extends PoleObjectDto> poleObjects = getPoleObjects(poleDirect, rootCriteriaDto, null, clientName, RetrievalTypeDto.OPEN);
		return poleObjects.size() > 0? (GemCaseDto) poleObjects.get(0) : null;
	}
	
	protected GemCaseDto getCaseFromBusinessServices(int caseObjectRef, String clientName) {
		PoleObjectCriteriaDto rootCriteriaDto = PoleDtoBuildHelper.buildPoleObjectCriteriaDto(PoleNames.GEM_CASE, PoleDtoBuildHelper.buildIntegerCriterionDto("objectRef", caseObjectRef));
		List<? extends PoleObjectDto> poleObjects = getPoleObjects(poleBusinessServices, rootCriteriaDto, null, clientName, RetrievalTypeDto.OPEN);
		return poleObjects.size() > 0? (GemCaseDto) poleObjects.get(0) : null;
	}
	
	protected GemCaseDto getCaseFromBusinessServicesAsXml(int caseObjectRef, String clientName) {
		PoleObjectCriteriaDto rootCriteriaDto = PoleDtoBuildHelper.buildPoleObjectCriteriaDto(PoleNames.GEM_CASE, PoleDtoBuildHelper.buildIntegerCriterionDto("objectRef", caseObjectRef));
		String doc = getPoleObjectsAsXml(poleBusinessServices, rootCriteriaDto, null, clientName, RetrievalTypeDto.OPEN);
		GetPoleObjectsResponseDto dto = JaxbXmlMarshaller.convertFromXml(doc, GetPoleObjectsResponseDto.class);
		return dto.getPoleObjects().size() > 0? (GemCaseDto) dto.getPoleObjects().get(0) : null;
	}
	
	protected GetPoleObjectsResponseDto getCase(Pole pole, int objectRef, String objectType, String clientName, String transactionDescription) {
		PoleObjectCriteriaDto rootCriteriaDto = PoleDtoBuildHelper.buildPoleObjectCriteriaDto(objectType, PoleDtoBuildHelper.buildIntegerCriterionDto("objectRef", objectRef));
		return getFromPole(poleBusinessServices, rootCriteriaDto, null, clientName, RetrievalTypeDto.OPEN, transactionDescription);
	}
	
	protected List<? extends PoleObjectDto> getPoleObjects(Pole pole, PoleObjectCriteriaDto criteria, PoleObjectFieldSelectorDto selector,
			String clientName, RetrievalTypeDto retrievalType) {		
		GetPoleObjectsResponseDto response = getFromPole(pole, criteria, selector, clientName, retrievalType, MEANINGLESS_TRANSACTION_DESCRIPTION);
		return response.getPoleObjects();
	}
	
	protected String getPoleObjectsAsXml(Pole pole, PoleObjectCriteriaDto criteria, PoleObjectFieldSelectorDto selector,
			String clientName, RetrievalTypeDto retrievalType) {		
		GetPoleObjectsAsXmlDocumentResponseDto response = getFromPoleAsXml(pole, criteria, selector, clientName, retrievalType, MEANINGLESS_TRANSACTION_DESCRIPTION);
		return response.getDocument();
	}
	
	protected GetPoleObjectsResponseDto getFromPole(Pole pole, int objectRef, String poleObjectType, String clientName, String transactionDescription) {
		PoleObjectCriteriaDto criteria = PoleDtoBuildHelper.buildPoleObjectCriteriaDto(poleObjectType, PoleDtoBuildHelper.buildIntegerCriterionDto("objectRef", objectRef));

		return getFromPole(pole, criteria, null, clientName, RetrievalTypeDto.OPEN, transactionDescription);
	}
	
	protected GetPoleObjectsResponseDto getFromPole(Pole pole, PoleObjectCriteriaDto criteria, PoleObjectFieldSelectorDto selector,
			String clientName, RetrievalTypeDto retrievalType, String transactionDescription) {
		GetPoleObjectsRequestDto getPoleObjectsRequest = new GetPoleObjectsRequestDto();
		getPoleObjectsRequest.setRetrievalType(retrievalType);
		setHeaderOnRequest(getPoleObjectsRequest, clientName, transactionDescription);
		getPoleObjectsRequest.setPoleObjectCriteria(criteria);
		getPoleObjectsRequest.setPoleObjectFieldSelector(selector);
		
		return pole.getPoleObjects(getPoleObjectsRequest);
	}
	
	protected GetPoleObjectsAsXmlDocumentResponseDto getFromPoleAsXml(Pole pole, PoleObjectCriteriaDto criteria, PoleObjectFieldSelectorDto selector,
			String clientName, RetrievalTypeDto retrievalType, String transactionDescription) {
		GetPoleObjectsAsXmlDocumentRequestDto getPoleObjectsRequest = new GetPoleObjectsAsXmlDocumentRequestDto();
		getPoleObjectsRequest.setRetrievalType(retrievalType);
		setHeaderOnRequest(getPoleObjectsRequest, clientName, transactionDescription);
		getPoleObjectsRequest.setPoleObjectCriteria(criteria);
		getPoleObjectsRequest.setPoleObjectFieldSelector(selector);
		
		return pole.getPoleObjectsAsXmlDocument(getPoleObjectsRequest);
	}
	
	protected GemCaseDto prepareNewGemCase(Integer objectRef, String caseRef) {
		GemCaseDto gemCase = new GemCaseDto();
		gemCase.setObjectRef(objectRef);
		gemCase.setCaseRef(caseRef);
		return gemCase;
	}
	
	protected void setHeaderOnRequest(PoleRequestDto request, String clientName, String transactionDescription) {
		
		RequestHeaderDto requestHeader = new RequestHeaderDto();
		requestHeader.setUsername("");
		requestHeader.setClientName(clientName);
		requestHeader.setPasswordHash(securityContextId);
		requestHeader.setTransactionDescription(transactionDescription);
		request.setHeader(requestHeader);
	}

}
