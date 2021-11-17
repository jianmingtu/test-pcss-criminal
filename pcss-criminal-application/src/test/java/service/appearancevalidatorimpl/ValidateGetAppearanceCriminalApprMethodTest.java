package service.appearancevalidatorimpl;

import ca.bc.gov.open.pcsscriminalapplication.service.impl.AppearanceValidatorImpl;
import ca.bc.gov.open.pcsscriminalcommon.utils.InstantUtils;
import ca.bc.gov.open.wsdl.pcss.one.GetAppearanceCriminalApprMethodRequest;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;

import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("ValidateGetAppearanceCriminalApprMethod Test")
public class ValidateGetAppearanceCriminalApprMethodTest {

    private static final String LONG_STRING = "VERYLONGSTRINGTOTESTTHELENGTHRESTRICTION";
    private static final String VALUE = "TEST";

    AppearanceValidatorImpl sut;

    @BeforeAll
    public void BeforeAll() {

        sut = new AppearanceValidatorImpl();

    }


    @Test
    @DisplayName("Success: all validations succeed")
    public void successTestReturns() {

        GetAppearanceCriminalApprMethodRequest getAppearanceCriminalApprMethodRequest = new GetAppearanceCriminalApprMethodRequest();
        getAppearanceCriminalApprMethodRequest.setAppearanceId(VALUE);
        getAppearanceCriminalApprMethodRequest.setRequestDtm(InstantUtils.parse("2013-03-25 13:04:22.1"));
        getAppearanceCriminalApprMethodRequest.setRequestAgencyIdentifierId(VALUE);
        getAppearanceCriminalApprMethodRequest.setRequestPartId(VALUE);

        List<String> result = sut.validateGetAppearanceCriminalApprMethod(getAppearanceCriminalApprMethodRequest);

        Assertions.assertTrue(result.isEmpty());

    }

    @Test
    @DisplayName("Fail: all validations fail")
    public void failTestReturns() {

        GetAppearanceCriminalApprMethodRequest getAppearanceCriminalApprMethodRequest = new GetAppearanceCriminalApprMethodRequest();
        getAppearanceCriminalApprMethodRequest.setAppearanceId(LONG_STRING);
        getAppearanceCriminalApprMethodRequest.setRequestDtm(InstantUtils.parse("2001-DEC-26"));
        getAppearanceCriminalApprMethodRequest.setRequestAgencyIdentifierId(LONG_STRING);
        getAppearanceCriminalApprMethodRequest.setRequestPartId(LONG_STRING);

        List<String> result = sut.validateGetAppearanceCriminalApprMethod(getAppearanceCriminalApprMethodRequest);

        Assertions.assertEquals(4, result.size());
        Assertions.assertEquals("RequestAgencyIdentifierId is not valid,RequestPartId is not valid,RequestDtm is not valid,AppearanceId is not valid", StringUtils.join(result, ","));

    }

}
