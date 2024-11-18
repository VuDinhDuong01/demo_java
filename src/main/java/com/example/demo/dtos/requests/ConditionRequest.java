package com.example.demo.dtos.requests;

import java.util.List;

import lombok.Data;

@Data
public class ConditionRequest {
     private String key;
    private List<String> value;
}
