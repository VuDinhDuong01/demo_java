package com.example.demo.services;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RedisService {

    private final Integer TIME_OUT = 5*60*1000;

    RedisTemplate<String, String> redisTemplate;

    public void SaveOTP(String key, String valueOTP){
         redisTemplate.opsForValue().set(key, valueOTP,Duration.ofSeconds(200) );
    }

    public String  getOTP(String key){
        return (String) redisTemplate.opsForValue().get(key);
    }

    public  void deleteOTP(String key){
        redisTemplate.delete(key);
    }
}
