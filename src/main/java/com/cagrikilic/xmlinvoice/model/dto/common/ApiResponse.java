package com.cagrikilic.xmlinvoice.model.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.cagrikilic.xmlinvoice.constant.ApiResponseStatus;
import com.cagrikilic.xmlinvoice.constant.HttpStatusConstants;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private String status;
    private int httpStatus;
    private String message;
    private T data;
    private List<String> errorDetails;
    private Instant timestamp;

    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .status(ApiResponseStatus.SUCCESS)
                .httpStatus(HttpStatusConstants.OK)
                .message(message)
                .timestamp(Instant.now())
                .build();
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .status(ApiResponseStatus.SUCCESS)
                .httpStatus(HttpStatusConstants.OK)
                .message(message)
                .data(data)
                .timestamp(Instant.now())
                .build();
    }

    public static <T> ApiResponse<T> created(String message, T data) {
        return ApiResponse.<T>builder()
                .status(ApiResponseStatus.SUCCESS)
                .httpStatus(HttpStatusConstants.CREATED)
                .message(message)
                .data(data)
                .timestamp(Instant.now())
                .build();
    }

    public static <T> ApiResponse<T> error(String message, int httpStatus) {
        return ApiResponse.<T>builder()
                .status(ApiResponseStatus.ERROR)
                .httpStatus(httpStatus)
                .message(message)
                .timestamp(Instant.now())
                .build();
    }

    public static <T> ApiResponse<T> error(String message, int httpStatus, List<String> errorDetails) {
        return ApiResponse.<T>builder()
                .status(ApiResponseStatus.ERROR)
                .httpStatus(httpStatus)
                .message(message)
                .errorDetails(errorDetails)
                .timestamp(Instant.now())
                .build();
    }
}
