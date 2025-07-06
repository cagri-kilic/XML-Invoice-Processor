package com.cagrikilic.xmlinvoice.exception;

import com.cagrikilic.xmlinvoice.constant.ErrorMessages;
import com.cagrikilic.xmlinvoice.constant.HttpStatusConstants;
import com.cagrikilic.xmlinvoice.model.dto.common.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.xml.bind.JAXBException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        log.error("Validation error: {}", errors, ex);

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(ErrorMessages.VALIDATION_FAILED, HttpStatusConstants.BAD_REQUEST, errors));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(EntityNotFoundException ex) {
        log.error("Entity not found: {}", ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage(), HttpStatusConstants.NOT_FOUND));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalState(IllegalStateException ex) {
        log.error("Illegal state: {}", ex.getMessage(), ex);

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(ex.getMessage(), HttpStatusConstants.BAD_REQUEST));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        log.error("Illegal argument: {}", ex.getMessage(), ex);

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(ex.getMessage(), HttpStatusConstants.BAD_REQUEST));
    }

    @ExceptionHandler(JAXBException.class)
    public ResponseEntity<ApiResponse<Void>> handleJAXBException(JAXBException ex) {
        log.error("JAXB parsing error: {}", ex.getMessage(), ex);

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(ErrorMessages.XML_PARSING_FAILED, HttpStatusConstants.BAD_REQUEST));
    }

    @ExceptionHandler(XmlValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleXmlValidationException(XmlValidationException ex) {
        log.error("XML validation error: {}", ex.getErrors());
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(ErrorMessages.XML_VALIDATION_FAILED, HttpStatusConstants.BAD_REQUEST, ex.getErrors()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleAll(Exception ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(
                        ErrorMessages.UNEXPECTED_ERROR,
                        HttpStatusConstants.INTERNAL_SERVER_ERROR));
    }
}
