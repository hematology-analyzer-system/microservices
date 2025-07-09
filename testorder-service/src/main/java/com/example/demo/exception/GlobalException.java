package com.example.demo.exception;

import com.example.demo.dto.ApiResponse;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalException {
    @ExceptionHandler(value = ApiException.class)
    ResponseEntity<ApiResponse<Object>> handleApiException(ApiException e){
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .code(e.getHttpStatus().value())
                .message(e.getMessage())
                .result(null)
                .build();

        return new ResponseEntity<>(apiResponse, e.getHttpStatus());
    }

    @ExceptionHandler(value = RuntimeException.class)
    ResponseEntity<ApiResponse<Object>> handleRuntimeException(RuntimeException e){
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .code(500)
                .message("Internal server error: " + e.getMessage())
                .result(null)
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = BadRequestException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadRequest(BadRequestException ex) {
        ApiResponse<Object> apiResponse = ApiResponse.<Object>builder()
                .code(400)
                .message(ex.getMessage())
                .result(null)
                .build();

        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = ForbiddenActionException.class)
    public ResponseEntity<ApiResponse<Object>> handleForbiddenAction(ForbiddenActionException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.<Object>builder()
                        .code(403)
                        .message(ex.getMessage())
                        .result(null)
                        .build());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException ex) {
        String errorMsg = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));

        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .code(400)
                .message("Validation failed: " + errorMsg)
                .result(null)
                .build();

        return ResponseEntity.badRequest().body(apiResponse);
    }
}
