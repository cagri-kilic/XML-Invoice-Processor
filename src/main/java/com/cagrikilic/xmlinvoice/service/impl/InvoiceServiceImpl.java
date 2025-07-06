package com.cagrikilic.xmlinvoice.service.impl;

import com.cagrikilic.xmlinvoice.constant.ErrorMessages;
import com.cagrikilic.xmlinvoice.exception.XmlValidationException;
import com.cagrikilic.xmlinvoice.generated.local.Faktura;
import com.cagrikilic.xmlinvoice.mapper.InvoiceMapper;
import com.cagrikilic.xmlinvoice.model.dto.request.InvoiceRequestDto;
import com.cagrikilic.xmlinvoice.model.dto.response.InvoiceResponseDto;
import com.cagrikilic.xmlinvoice.model.entity.Invoice;
import com.cagrikilic.xmlinvoice.repository.InvoiceRepository;
import com.cagrikilic.xmlinvoice.service.InvoiceService;
import com.cagrikilic.xmlinvoice.util.CollectingErrorHandler;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;
    private final javax.xml.validation.Schema xmlSchema;
    private final JAXBContext jaxbContext;

    @Override
    @Transactional
    public InvoiceResponseDto handleInvoice(InvoiceRequestDto requestDto) throws JAXBException {
        String xmlContent = decodeBase64Xml(requestDto.base64xml());
        validateXmlAgainstXsd(xmlContent);
        Faktura faktura = unmarshalXml(xmlContent);
        validateFakturaData(faktura);
        Invoice invoice = invoiceMapper.fakturaToInvoice(faktura);
        Invoice savedInvoice = invoiceRepository.save(invoice);

        return new InvoiceResponseDto(savedInvoice);
    }

    private String decodeBase64Xml(String base64Xml) {
        byte[] decodedBytes = Base64.getDecoder().decode(base64Xml.trim());
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }

    private void validateXmlAgainstXsd(String xmlContent) {
        try {
            Validator validator = xmlSchema.newValidator();
            CollectingErrorHandler errorHandler = new CollectingErrorHandler();
            validator.setErrorHandler(errorHandler);
            Source xmlSource = new StreamSource(new StringReader(xmlContent));
            validator.validate(xmlSource);
            if (!errorHandler.getErrors().isEmpty()) {
                throw new XmlValidationException(errorHandler.getErrors());
            }
        } catch (SAXException | IOException e) {
            throw new XmlValidationException(List.of(e.getMessage()));
        }
    }

    private Faktura unmarshalXml(String xmlContent) throws JAXBException {
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return (Faktura) unmarshaller.unmarshal(new StringReader(xmlContent));
    }

    private void validateFakturaData(Faktura faktura) {
        if (faktura.getPodmiot1() == null ||
                faktura.getPodmiot1().getDaneIdentyfikacyjne() == null ||
                faktura.getPodmiot1().getDaneIdentyfikacyjne().getNIP() == null) {
            throw new IllegalArgumentException(ErrorMessages.NIP_NOT_FOUND);
        }

        if (faktura.getFa() == null) {
            throw new IllegalArgumentException(ErrorMessages.P1_AND_P2_NOT_FOUND);
        }

        if (faktura.getFa().getP1() == null) {
            throw new IllegalArgumentException(ErrorMessages.P1_NOT_FOUND);
        }

        if (faktura.getFa().getP2() == null) {
            throw new IllegalArgumentException(ErrorMessages.P2_NOT_FOUND);
        }
    }
} 