package com.cagrikilic.xmlinvoice.model.dto.request;

import com.cagrikilic.xmlinvoice.constant.ErrorMessages;

public record InvoiceRequestDto(String base64xml) {

    public InvoiceRequestDto {
        if (base64xml == null || base64xml.trim().isEmpty()) {
            throw new IllegalArgumentException(ErrorMessages.BASE64_XML_CANNOT_BE_NULL_OR_EMPTY);
        }
    }
} 