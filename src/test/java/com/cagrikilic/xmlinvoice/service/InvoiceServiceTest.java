package com.cagrikilic.xmlinvoice.service;

import com.cagrikilic.xmlinvoice.constant.ErrorMessages;
import com.cagrikilic.xmlinvoice.exception.XmlValidationException;
import com.cagrikilic.xmlinvoice.generated.local.Faktura;
import com.cagrikilic.xmlinvoice.generated.local.TPodmiot1;
import com.cagrikilic.xmlinvoice.generated.local.TAdres;
import com.cagrikilic.xmlinvoice.generated.local.TKodWaluty;
import com.cagrikilic.xmlinvoice.generated.local.TRodzajFaktury;
import com.cagrikilic.xmlinvoice.mapper.InvoiceMapper;
import com.cagrikilic.xmlinvoice.model.dto.request.InvoiceRequestDto;
import com.cagrikilic.xmlinvoice.model.dto.response.InvoiceResponseDto;
import com.cagrikilic.xmlinvoice.model.entity.Invoice;
import com.cagrikilic.xmlinvoice.repository.InvoiceRepository;
import com.cagrikilic.xmlinvoice.service.impl.InvoiceServiceImpl;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private InvoiceMapper invoiceMapper;

    @Mock
    private Schema xmlSchema;

    @Mock
    private JAXBContext jaxbContext;

    @Mock
    private Validator validator;

    @Mock
    private jakarta.xml.bind.Unmarshaller unmarshaller;

    @InjectMocks
    private InvoiceServiceImpl invoiceService;

    private InvoiceRequestDto validRequestDto;
    private Faktura validFaktura;
    private Invoice validInvoice;
    private InvoiceResponseDto expectedResponseDto;

    @BeforeEach
    void setUp() throws Exception {
        String validBase64Xml = "PEludm9pY2U+SU5WLTEwMDE8L0ludm9pY2VOdW1iZXI+PC9JbnZvaWNlPg==";
        validRequestDto = new InvoiceRequestDto(validBase64Xml);

        validFaktura = new Faktura();
        validFaktura.setPodmiot1(createValidPodmiot1());
        validFaktura.setFa(createValidFa());

        validInvoice = new Invoice();
        validInvoice.setId(1L);
        validInvoice.setNip("1234567890");
        validInvoice.setP1("Test P1");
        validInvoice.setP2("Test P2");
        validInvoice.setCreatedAt(Instant.now());

        expectedResponseDto = new InvoiceResponseDto(validInvoice);
    }

    @Test
    void handleInvoice_WithValidRequest_ShouldReturnSuccessResponse() throws Exception {
        when(xmlSchema.newValidator()).thenReturn(validator);
        when(jaxbContext.createUnmarshaller()).thenReturn(unmarshaller);
        when(unmarshaller.unmarshal(any(java.io.StringReader.class))).thenReturn(validFaktura);
        when(invoiceMapper.fakturaToInvoice(validFaktura)).thenReturn(validInvoice);
        when(invoiceRepository.save(validInvoice)).thenReturn(validInvoice);

        InvoiceResponseDto result = invoiceService.handleInvoice(validRequestDto);

        assertNotNull(result);
        assertEquals(expectedResponseDto.invoice(), result.invoice());
        verify(invoiceRepository).save(validInvoice);
        verify(invoiceMapper).fakturaToInvoice(validFaktura);
    }

    @Test
    void handleInvoice_WithNullBase64Xml_ShouldThrowIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new InvoiceRequestDto(null));
        assertEquals(ErrorMessages.BASE64_XML_CANNOT_BE_NULL_OR_EMPTY, exception.getMessage());
    }

    @Test
    void handleInvoice_WithInvalidBase64Encoding_ShouldThrowIllegalArgumentException() {
        InvoiceRequestDto invalidRequest = new InvoiceRequestDto("invalid-base64!");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> invoiceService.handleInvoice(invalidRequest));
        assertTrue(exception.getMessage().contains("Illegal base64 character"));
    }

    @Test
    void handleInvoice_WithXmlValidationError_ShouldThrowXmlValidationException() throws Exception {
        when(xmlSchema.newValidator()).thenReturn(validator);
        doThrow(new XmlValidationException(List.of())).when(validator).validate(any());

        assertThrows(XmlValidationException.class, () -> invoiceService.handleInvoice(validRequestDto));
    }

    @Test
    void handleInvoice_WithJAXBException_ShouldThrowJAXBException() throws Exception {
        when(xmlSchema.newValidator()).thenReturn(validator);
        when(jaxbContext.createUnmarshaller()).thenReturn(unmarshaller);
        doThrow(new JAXBException("Parsing failed")).when(unmarshaller).unmarshal(any(java.io.StringReader.class));

        assertThrows(JAXBException.class, () -> invoiceService.handleInvoice(validRequestDto));
    }

    @Test
    void handleInvoice_WithNullPodmiot1_ShouldThrowIllegalArgumentException() throws Exception {
        when(xmlSchema.newValidator()).thenReturn(validator);
        when(jaxbContext.createUnmarshaller()).thenReturn(unmarshaller);
        Faktura invalidFaktura = new Faktura();
        invalidFaktura.setPodmiot1(null);
        invalidFaktura.setFa(createValidFa());
        when(unmarshaller.unmarshal(any(java.io.StringReader.class))).thenReturn(invalidFaktura);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> invoiceService.handleInvoice(validRequestDto));
        assertEquals(ErrorMessages.NIP_NOT_FOUND, exception.getMessage());
    }

    @Test
    void handleInvoice_WithNullDaneIdentyfikacyjne_ShouldThrowIllegalArgumentException() throws Exception {
        when(xmlSchema.newValidator()).thenReturn(validator);
        when(jaxbContext.createUnmarshaller()).thenReturn(unmarshaller);
        Faktura invalidFaktura = new Faktura();
        var podmiot1 = createValidPodmiot1();
        podmiot1.setDaneIdentyfikacyjne(null);
        invalidFaktura.setPodmiot1(podmiot1);
        invalidFaktura.setFa(createValidFa());
        when(unmarshaller.unmarshal(any(java.io.StringReader.class))).thenReturn(invalidFaktura);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> invoiceService.handleInvoice(validRequestDto));
        assertEquals(ErrorMessages.NIP_NOT_FOUND, exception.getMessage());
    }

    @Test
    void handleInvoice_WithNullNIP_ShouldThrowIllegalArgumentException() throws Exception {
        when(xmlSchema.newValidator()).thenReturn(validator);
        when(jaxbContext.createUnmarshaller()).thenReturn(unmarshaller);
        Faktura invalidFaktura = new Faktura();
        var podmiot1 = createValidPodmiot1();
        podmiot1.getDaneIdentyfikacyjne().setNIP(null);
        invalidFaktura.setPodmiot1(podmiot1);
        invalidFaktura.setFa(createValidFa());
        when(unmarshaller.unmarshal(any(java.io.StringReader.class))).thenReturn(invalidFaktura);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> invoiceService.handleInvoice(validRequestDto));
        assertEquals(ErrorMessages.NIP_NOT_FOUND, exception.getMessage());
    }

    @Test
    void handleInvoice_WithNullFa_ShouldThrowIllegalArgumentException() throws Exception {
        when(xmlSchema.newValidator()).thenReturn(validator);
        when(jaxbContext.createUnmarshaller()).thenReturn(unmarshaller);
        Faktura invalidFaktura = new Faktura();
        invalidFaktura.setPodmiot1(createValidPodmiot1());
        invalidFaktura.setFa(null);
        when(unmarshaller.unmarshal(any(java.io.StringReader.class))).thenReturn(invalidFaktura);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> invoiceService.handleInvoice(validRequestDto));
        assertEquals(ErrorMessages.P1_AND_P2_NOT_FOUND, exception.getMessage());
    }

    @Test
    void handleInvoice_WithNullP1_ShouldThrowIllegalArgumentException() throws Exception {
        when(xmlSchema.newValidator()).thenReturn(validator);
        when(jaxbContext.createUnmarshaller()).thenReturn(unmarshaller);
        Faktura invalidFaktura = new Faktura();
        invalidFaktura.setPodmiot1(createValidPodmiot1());
        var fa = createValidFa();
        fa.setP1(null);
        invalidFaktura.setFa(fa);
        when(unmarshaller.unmarshal(any(java.io.StringReader.class))).thenReturn(invalidFaktura);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> invoiceService.handleInvoice(validRequestDto));
        assertEquals(ErrorMessages.P1_NOT_FOUND, exception.getMessage());
    }

    @Test
    void handleInvoice_WithNullP2_ShouldThrowIllegalArgumentException() throws Exception {
        when(xmlSchema.newValidator()).thenReturn(validator);
        when(jaxbContext.createUnmarshaller()).thenReturn(unmarshaller);
        Faktura invalidFaktura = new Faktura();
        invalidFaktura.setPodmiot1(createValidPodmiot1());
        var fa = createValidFa();
        fa.setP2(null);
        invalidFaktura.setFa(fa);
        when(unmarshaller.unmarshal(any(java.io.StringReader.class))).thenReturn(invalidFaktura);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> invoiceService.handleInvoice(validRequestDto));
        assertEquals(ErrorMessages.P2_NOT_FOUND, exception.getMessage());
    }

    @Test
    void handleInvoice_WithRepositorySaveFailure_ShouldThrowRuntimeException() {
        assertThrows(RuntimeException.class, () -> invoiceService.handleInvoice(validRequestDto));
    }

    private Faktura.Podmiot1 createValidPodmiot1() {
        var podmiot1 = new Faktura.Podmiot1();
        var daneIdentyfikacyjne = new TPodmiot1();
        daneIdentyfikacyjne.setNIP("1234567890");
        daneIdentyfikacyjne.setNazwa("Test Nazwa");
        podmiot1.setDaneIdentyfikacyjne(daneIdentyfikacyjne);
        podmiot1.setAdres(new TAdres());
        return podmiot1;
    }

    private Faktura.Fa createValidFa() throws DatatypeConfigurationException {
        var fa = new Faktura.Fa();
        fa.setP1(javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar("2024-01-01"));
        fa.setP2("Test P2");
        fa.setKodWaluty(TKodWaluty.AED);
        fa.setAdnotacje(new Faktura.Fa.Adnotacje());
        fa.setRodzajFaktury(TRodzajFaktury.ZAL);
        fa.setP15(java.math.BigDecimal.ONE);
        return fa;
    }
} 