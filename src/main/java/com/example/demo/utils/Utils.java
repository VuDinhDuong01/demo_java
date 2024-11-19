package com.example.demo.utils;

import java.sql.Date;
import java.time.Duration;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Utils {
    public static String randomToken() {
        String token = "";
        for (int i = 1 ;i<=6; i++) {
            token = token + (int)(Math.floor(Math.random() * 10));
        }
        return token;
    }

    public static boolean checkVerifyTimer(Date time) {
        // current time;
        LocalDateTime currentTime = LocalDateTime.now();

        LocalDateTime localTime = time.toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDateTime();

        Duration duration = Duration.between(currentTime, localTime);
        return duration.toMinutes() > 5;
    }
}
