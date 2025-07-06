package com.cagrikilic.xmlinvoice.controller;

import com.cagrikilic.xmlinvoice.constant.SuccessMessages;
import com.cagrikilic.xmlinvoice.model.dto.request.InvoiceRequestDto;
import com.cagrikilic.xmlinvoice.model.dto.response.InvoiceResponseDto;
import com.cagrikilic.xmlinvoice.model.dto.common.ApiResponse;
import com.cagrikilic.xmlinvoice.service.InvoiceService;
import jakarta.xml.bind.JAXBException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xml.sax.SAXException;

import java.io.IOException;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
@Slf4j
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<ApiResponse<InvoiceResponseDto>> createInvoice(@RequestBody InvoiceRequestDto requestDto) throws JAXBException, IOException, SAXException {
        log.info("Request received to process invoice with base64 XML content");
        InvoiceResponseDto response = invoiceService.handleInvoice(requestDto);
        log.info("Invoice saved successfully");
        return ResponseEntity.ok(ApiResponse.created(SuccessMessages.INVOICE_SAVED_SUCCESSFULLY, response));
    }
} 