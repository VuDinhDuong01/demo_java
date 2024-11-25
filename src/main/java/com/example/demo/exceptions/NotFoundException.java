package com.example.demo.exceptions;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotFoundException extends RuntimeException{
    final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
    String message;
}
