package com.example.demo.dtos.responses;



import lombok.AccessLevel;

import lombok.Data;

import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public  class Token {
    String access_token;
    String refresh_token;
}