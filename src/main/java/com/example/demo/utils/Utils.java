package com.example.demo.utils;

import lombok.Data;

@Data
public class Utils {
    public static String randomToken(){
        String token ="";
        for(int i =0 ;i<= 9 ;i++){
            token = token + Math.floor(Math.random()*10);
        }
        return token;
    }
}
