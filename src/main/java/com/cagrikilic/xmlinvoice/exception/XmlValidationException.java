package com.cagrikilic.xmlinvoice.exception;

import java.util.List;

import com.cagrikilic.xmlinvoice.constant.ErrorMessages;
import lombok.Data;

@Data
public class XmlValidationException extends RuntimeException {
    private final List<String> errors;
    public XmlValidationException(List<String> errors) {
        super(ErrorMessages.XML_VALIDATION_FAILED);
        this.errors = errors;
    }
} 