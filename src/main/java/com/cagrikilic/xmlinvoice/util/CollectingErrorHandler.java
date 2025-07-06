package com.cagrikilic.xmlinvoice.util;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CollectingErrorHandler implements ErrorHandler {
    private final List<String> errors = new ArrayList<>();

    @Override
    public void warning(SAXParseException exception) {
        errors.add("Warning: " + exception.getMessage());
    }

    @Override
    public void error(SAXParseException exception) {
        errors.add("Error: " + exception.getMessage());
    }

    @Override
    public void fatalError(SAXParseException exception) {
        errors.add("Fatal error: " + exception.getMessage());
    }
} 