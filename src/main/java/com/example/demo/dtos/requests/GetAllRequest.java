package com.example.demo.dtos.requests;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetAllRequest {
    String limit;
    String start;
    String sortField;
    String sortType;
    List<ConditionRequest> conditions;

}
