package com.example.demo.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.demo.dtos.responses.BaseResponse;
import jakarta.validation.constraints.Null;

@ControllerAdvice
public class GlobalException {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<BaseResponse<Null>> resourceRuntimeException(RuntimeException ex) {
        BaseResponse<Null> error = BaseResponse.<Null>builder().error(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST.value()).result(null).build();

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> resourceMethodArgumentNotValidException(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        BaseResponse<Object> error = BaseResponse.<Object>builder()
                .status(ex.getStatusCode().value())
                .result(errors)
                .error(ex.getBody().getDetail())
                .build();

        return ResponseEntity.badRequest().body(error);
    }
}
