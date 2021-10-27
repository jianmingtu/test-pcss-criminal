package ca.bc.gov.open.pcsscriminalapplication.controller.appearancecontroller;

import ca.bc.gov.open.pcsscriminalapplication.controller.AppearanceController;
import ca.bc.gov.open.pcsscriminalapplication.exception.BadDateException;
import ca.bc.gov.open.pcsscriminalapplication.exception.ORDSException;
import ca.bc.gov.open.pcsscriminalapplication.properties.PcssProperties;
import ca.bc.gov.open.wsdl.pcss.one.AppearanceMethod;
import ca.bc.gov.open.wsdl.pcss.two.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.xml.ws.http.HTTPException;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("GetAppearanceCriminalApprMethod Test")
class GetAppearanceCriminalApprMethodTest {

    @Mock
    private RestTemplate restTemplateMock;

    @Mock
    private PcssProperties pcssPropertiesMock;

    @Mock
    private ObjectMapper objectMapperMock;

    private AppearanceController sut;

    @BeforeAll
    public void beforeAll() {

        MockitoAnnotations.openMocks(this);

        Mockito.when(pcssPropertiesMock.getHost()).thenReturn("http://localhost/");

        sut = new AppearanceController(restTemplateMock, pcssPropertiesMock, objectMapperMock);

    }

    @Test
    @DisplayName("Success: get returns expected object")
    public void successTestReturns() throws BadDateException, JsonProcessingException {

        GetAppearanceCriminalApprMethod getAppearanceCriminalApprMethodRequest = new GetAppearanceCriminalApprMethod();
        GetAppearanceCriminalApprMethodRequest getAppearanceCriminalApprMethodRequest1 = new GetAppearanceCriminalApprMethodRequest();
        ca.bc.gov.open.wsdl.pcss.one.GetAppearanceCriminalApprMethodRequest getAppearanceCriminalApprMethodRequest2 = new ca.bc.gov.open.wsdl.pcss.one.GetAppearanceCriminalApprMethodRequest();

        getAppearanceCriminalApprMethodRequest2.setAppearanceId("TEST");
        getAppearanceCriminalApprMethodRequest2.setRequestAgencyIdentifierId("TEST");
        getAppearanceCriminalApprMethodRequest2.setRequestDtm("2021-04-17");
        getAppearanceCriminalApprMethodRequest2.setRequestPartId("TEST");

        getAppearanceCriminalApprMethodRequest1.setGetAppearanceCriminalApprMethodRequest(getAppearanceCriminalApprMethodRequest2);

        getAppearanceCriminalApprMethodRequest.setGetAppearanceCriminalApprMethodRequest(getAppearanceCriminalApprMethodRequest1);

        ca.bc.gov.open.wsdl.pcss.one.GetAppearanceCriminalApprMethodResponse response = new ca.bc.gov.open.wsdl.pcss.one.GetAppearanceCriminalApprMethodResponse();
        response.setResponseMessageTxt("TEST");
        response.setResponseCd("TEST");

        AppearanceMethod apearanceMethod = new AppearanceMethod();
        response.setAppearanceMethod(Collections.singletonList(apearanceMethod));

        Mockito.when(restTemplateMock.exchange(any(String.class), any(), any(), any(Class.class))).thenReturn(ResponseEntity.ok(response));

        GetAppearanceCriminalApprMethodResponse result = sut.getAppearanceCriminalApprMethod(getAppearanceCriminalApprMethodRequest);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("TEST", result.getGetAppearanceCriminalApprMethodResponse().getGetAppearanceCriminalApprMethodResponse().getResponseMessageTxt());
        Assertions.assertEquals("TEST", result.getGetAppearanceCriminalApprMethodResponse().getGetAppearanceCriminalApprMethodResponse().getResponseCd());
        Assertions.assertEquals(1, result.getGetAppearanceCriminalApprMethodResponse().getGetAppearanceCriminalApprMethodResponse().getAppearanceMethod().size());

    }

    @Test
    @DisplayName("Error: ords throws exception")
    public void errorOrdsException() {

        GetAppearanceCriminalApprMethod getAppearanceCriminalApprMethodRequest = new GetAppearanceCriminalApprMethod();
        GetAppearanceCriminalApprMethodRequest getAppearanceCriminalApprMethodRequest1 = new GetAppearanceCriminalApprMethodRequest();
        ca.bc.gov.open.wsdl.pcss.one.GetAppearanceCriminalApprMethodRequest getAppearanceCriminalApprMethodRequest2 = new ca.bc.gov.open.wsdl.pcss.one.GetAppearanceCriminalApprMethodRequest();

        getAppearanceCriminalApprMethodRequest2.setAppearanceId("TEST");
        getAppearanceCriminalApprMethodRequest2.setRequestAgencyIdentifierId("TEST");
        getAppearanceCriminalApprMethodRequest2.setRequestDtm("2021-04-17");
        getAppearanceCriminalApprMethodRequest2.setRequestPartId("TEST");

        getAppearanceCriminalApprMethodRequest1.setGetAppearanceCriminalApprMethodRequest(getAppearanceCriminalApprMethodRequest2);

        getAppearanceCriminalApprMethodRequest.setGetAppearanceCriminalApprMethodRequest(getAppearanceCriminalApprMethodRequest1);

        Mockito.when(restTemplateMock.exchange(any(String.class), any(), any(), any(Class.class))).thenThrow(new HTTPException(400));

        Assertions.assertThrows(ORDSException.class, () -> sut.getAppearanceCriminalApprMethod(getAppearanceCriminalApprMethodRequest));

    }

    @Test
    @DisplayName("Error: with a bad date throw exception")
    public void whenBadDateExceptionThrown() throws BadDateException, JsonProcessingException {

        GetAppearanceCriminalApprMethod getAppearanceCriminalApprMethodRequest = new GetAppearanceCriminalApprMethod();

        Assertions.assertThrows(BadDateException.class, () -> sut.getAppearanceCriminalApprMethod(getAppearanceCriminalApprMethodRequest));

    }

}
