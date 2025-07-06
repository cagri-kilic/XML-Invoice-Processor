package com.cagrikilic.xmlinvoice.service;

import com.cagrikilic.xmlinvoice.model.dto.request.InvoiceRequestDto;
import com.cagrikilic.xmlinvoice.model.dto.response.InvoiceResponseDto;
import jakarta.xml.bind.JAXBException;
import org.xml.sax.SAXException;

import java.io.IOException;

public interface InvoiceService {
    InvoiceResponseDto handleInvoice(InvoiceRequestDto requestDto) throws JAXBException, IOException, SAXException;
} 