package ca.bc.gov.open.pcsscriminalapplication.controller.appearancecontroller;

import ca.bc.gov.open.pcsscriminalapplication.controller.AppearanceController;
import ca.bc.gov.open.pcsscriminalapplication.exception.BadDateException;
import ca.bc.gov.open.pcsscriminalapplication.exception.ORDSException;
import ca.bc.gov.open.pcsscriminalapplication.properties.PcssProperties;
import ca.bc.gov.open.pcsscriminalapplication.service.AppearanceValidator;
import ca.bc.gov.open.pcsscriminalapplication.utils.LogBuilder;
import ca.bc.gov.open.wsdl.pcss.one.ApprDetail;
import ca.bc.gov.open.wsdl.pcss.secure.two.GetAppearanceCriminalSecureResponse;
import ca.bc.gov.open.wsdl.pcss.three.YesNoType;
import ca.bc.gov.open.wsdl.pcss.two.GetAppearanceCriminal;
import ca.bc.gov.open.wsdl.pcss.two.GetAppearanceCriminalRequest;
import ca.bc.gov.open.wsdl.pcss.two.GetAppearanceCriminalResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.xml.ws.http.HTTPException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("GetAppearanceCriminal Test")
public class GetAppearanceCriminalTest {

    @Mock
    private RestTemplate restTemplateMock;

    @Mock
    private PcssProperties pcssPropertiesMock;

    @Mock
    private ObjectMapper objectMapperMock;

    @Mock
    private AppearanceValidator appearanceValidatorMock;

    private AppearanceController sut;

    @BeforeAll
    public void beforeAll() {

        MockitoAnnotations.openMocks(this);

        Mockito.when(pcssPropertiesMock.getHost()).thenReturn("http://localhost/");

        sut = new AppearanceController(restTemplateMock, pcssPropertiesMock, new LogBuilder(objectMapperMock), appearanceValidatorMock);

    }

    @Test
    @DisplayName("Success: get returns expected object")
    public void successTestReturns() throws BadDateException, JsonProcessingException {

        Mockito.when(appearanceValidatorMock.validateGetAppearanceCriminal(any())).thenReturn(new ArrayList<>());

        ca.bc.gov.open.wsdl.pcss.one.GetAppearanceCriminalResponse response = new ca.bc.gov.open.wsdl.pcss.one.GetAppearanceCriminalResponse();
        response.setFutureRecCount("1");
        response.setResponseCd("TEST");
        response.setHistoryRecCount("1");
        response.setResponseMessageTxt("TEST");

        response.setApprDetail(Collections.singletonList(new ApprDetail()));

        Mockito.when(restTemplateMock.exchange(any(String.class), any(), any(), any(Class.class))).thenReturn(ResponseEntity.ok(response));

        GetAppearanceCriminalResponse result = sut.getAppearanceCriminal( createTestRequest());

        Assertions.assertNotNull(result);
        Assertions.assertEquals("1", result.getGetAppearanceCriminalResponse().getGetAppearanceCriminalResponse().getFutureRecCount());
        Assertions.assertEquals("TEST", result.getGetAppearanceCriminalResponse().getGetAppearanceCriminalResponse().getResponseCd());
        Assertions.assertEquals("1", result.getGetAppearanceCriminalResponse().getGetAppearanceCriminalResponse().getHistoryRecCount());
        Assertions.assertEquals("TEST", result.getGetAppearanceCriminalResponse().getGetAppearanceCriminalResponse().getResponseMessageTxt());
        Assertions.assertEquals(1, result.getGetAppearanceCriminalResponse().getGetAppearanceCriminalResponse().getApprDetail().size());

    }

    @Test
    @DisplayName("Fail: post returns validation failure object")
    public void failTestReturns() throws JsonProcessingException {

        Mockito.when(appearanceValidatorMock.validateGetAppearanceCriminal(any())).thenReturn(Collections.singletonList("BAD DATA"));

        GetAppearanceCriminalResponse result = sut.getAppearanceCriminal(createTestRequest());

        Assertions.assertNotNull(result);
        Assertions.assertEquals("BAD DATA", result.getGetAppearanceCriminalResponse().getGetAppearanceCriminalResponse().getResponseMessageTxt());
        Assertions.assertEquals("-2", result.getGetAppearanceCriminalResponse().getGetAppearanceCriminalResponse().getResponseCd());

    }

    @Test
    @DisplayName("Error: ords throws exception")
    public void errorOrdsException() {

        Mockito.when(appearanceValidatorMock.validateGetAppearanceCriminal(any())).thenReturn(new ArrayList<>());

        Mockito.when(restTemplateMock.exchange(any(String.class), any(), any(), any(Class.class))).thenThrow(new HTTPException(400));

        Assertions.assertThrows(ORDSException.class, () -> sut.getAppearanceCriminal(createTestRequest()));

    }

    private GetAppearanceCriminal createTestRequest() {

        GetAppearanceCriminal getAppearanceCriminalRequest = new GetAppearanceCriminal();
        GetAppearanceCriminalRequest getAppearanceCriminalRequest1 = new GetAppearanceCriminalRequest();
        ca.bc.gov.open.wsdl.pcss.one.GetAppearanceCriminalRequest getAppearanceCriminalRequest2 = new ca.bc.gov.open.wsdl.pcss.one.GetAppearanceCriminalRequest();

        getAppearanceCriminalRequest2.setAppearanceId("TEST");
        getAppearanceCriminalRequest2.setFutureYN(YesNoType.Y);
        getAppearanceCriminalRequest2.setJustinNo("TEST");
        getAppearanceCriminalRequest2.setRequestAgencyIdentifierId("TEST");
        getAppearanceCriminalRequest2.setRequestDtm(Instant.now());
        getAppearanceCriminalRequest2.setRequestPartId("TEST");

        getAppearanceCriminalRequest1.setGetAppearanceCriminalRequest(getAppearanceCriminalRequest2);

        getAppearanceCriminalRequest.setGetAppearanceCriminalRequest(getAppearanceCriminalRequest1);

        return getAppearanceCriminalRequest;

    }

}
