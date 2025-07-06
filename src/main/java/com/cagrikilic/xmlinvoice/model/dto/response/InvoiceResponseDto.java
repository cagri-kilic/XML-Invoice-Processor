package com.cagrikilic.xmlinvoice.model.dto.response;

import com.cagrikilic.xmlinvoice.constant.ErrorMessages;
import com.cagrikilic.xmlinvoice.model.entity.Invoice;

public record InvoiceResponseDto(Invoice invoice) {

    public InvoiceResponseDto {
        if (invoice == null) {
            throw new IllegalArgumentException(ErrorMessages.INVOICE_CANNOT_BE_NULL);
        }
    }
} 