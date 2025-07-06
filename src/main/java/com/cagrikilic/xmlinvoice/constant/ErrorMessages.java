package com.cagrikilic.xmlinvoice.constant;

public final class ErrorMessages {
    private ErrorMessages() {
        throw new IllegalStateException("Constant class");
    }

    public static final String VALIDATION_FAILED = "Validation failed";
    public static final String UNEXPECTED_ERROR = "An unexpected error occurred. Please try again later.";
    public static final String XML_PARSING_FAILED = "Failed to parse XML";
    public static final String XML_VALIDATION_FAILED = "XML validation failed";
    public static final String NIP_NOT_FOUND = "NIP not found in XML";
    public static final String P1_NOT_FOUND = "P1 not found in XML";
    public static final String P2_NOT_FOUND = "P2 not found in XML";
    public static final String P1_AND_P2_NOT_FOUND = "P1 and P2 not found in XML";
    public static final String BASE64_XML_CANNOT_BE_NULL_OR_EMPTY = "Base64 XML content cannot be null or empty";
    public static final String INVOICE_CANNOT_BE_NULL = "Invoice cannot be null";
}
