package com.example.demo.dtos.responses;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class BaseResponse<T> {
    String error;
    int status;
    T result;
}
