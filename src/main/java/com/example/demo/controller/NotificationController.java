package com.example.demo.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dtos.requests.NotificationRequest;
import com.example.demo.dtos.requests.GetAllRequest;
import com.example.demo.dtos.responses.BaseResponse;
import com.example.demo.entity.NotificationEntity;
import com.example.demo.services.NotificationService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Controller
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationController {
     NotificationService notificationService;

    @PostMapping("/notification")
    public BaseResponse<NotificationEntity> create(@RequestBody @Valid NotificationRequest body){

        NotificationEntity response = notificationService.create(body);

        return BaseResponse.<NotificationEntity>builder().result(response).build();
    }

    @PutMapping("/notification")
    public BaseResponse<NotificationEntity> update(@RequestBody @Valid NotificationRequest body) {
        NotificationEntity response = notificationService.update(body);
        return BaseResponse.<NotificationEntity>builder().result(response).build();
    }

    @DeleteMapping("/notification")
    public BaseResponse<String> delete(@RequestBody @Valid NotificationRequest body) {
        String response = notificationService.delete(body);
        return BaseResponse.<String>builder().result(response).build();
    }


    @PostMapping("/filter-notification")
    public BaseResponse<Map<String, Object>> filter(@RequestBody @Valid GetAllRequest body){

        Map<String , Object> response = notificationService.getAllNotification(body);
        return BaseResponse.<Map<String, Object>>builder().result(response).build();
    }
}
