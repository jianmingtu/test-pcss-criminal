package ca.bc.gov.open.pcsscriminalapplication.configuration;

import ca.bc.gov.open.pcsscriminalapplication.properties.PcssProperties;
import ca.bc.gov.open.pcsscriminalcommon.serializer.InstantDeserializer;
import ca.bc.gov.open.pcsscriminalcommon.serializer.InstantSerializer;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import javax.xml.soap.SOAPMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.soap.SoapVersion;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.SimpleWsdl11Definition;
import org.springframework.ws.wsdl.wsdl11.Wsdl11Definition;

@EnableWs
@Configuration
@EnableConfigurationProperties(PcssProperties.class)
public class SoapConfig extends WsConfigurerAdapter {

    @Autowired private PcssProperties pcssProperties;

    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(
            ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean<>(servlet, "/criminal/*");
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        var restTemplate =
                restTemplateBuilder
                        .basicAuthentication(
                                pcssProperties.getUserName(), pcssProperties.getPassword())
                        .setReadTimeout(Duration.ofMinutes(2))
                        .build();
        restTemplate.getMessageConverters().add(0, createMappingJacksonHttpMessageConverter());
        return restTemplate;
    }

    private MappingJackson2HttpMessageConverter createMappingJacksonHttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();

        converter.setObjectMapper(objectMapper());
        return converter;
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Instant.class, new InstantDeserializer());
        module.addSerializer(Instant.class, new InstantSerializer());
        objectMapper.registerModule(module);
        return objectMapper;
    }

    @Bean
    public SaajSoapMessageFactory messageFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(SOAPMessage.WRITE_XML_DECLARATION, "true");
        SaajSoapMessageFactory messageFactory = new SaajSoapMessageFactory();
        messageFactory.setMessageProperties(props);
        messageFactory.setSoapVersion(SoapVersion.SOAP_12);
        return messageFactory;
    }

    @Bean(name = "JusticePCSSCriminal.wsProvider:pcssCriminal")
    public Wsdl11Definition JusticePCSSWSDL() {
        SimpleWsdl11Definition wsdl11Definition = new SimpleWsdl11Definition();
        wsdl11Definition.setWsdl(new ClassPathResource("xsdSchemas/pcssCriminal.wsdl"));
        return wsdl11Definition;
    }

    @Bean(name = "JusticePCSSCriminal.wsProvider:pcssCriminalSecure")
    public Wsdl11Definition JusticeSecurePCSSWSDL() {
        SimpleWsdl11Definition wsdl11Definition = new SimpleWsdl11Definition();
        wsdl11Definition.setWsdl(new ClassPathResource("xsdSchemas/pcssCriminalSecure.wsdl"));
        return wsdl11Definition;
    }
}
