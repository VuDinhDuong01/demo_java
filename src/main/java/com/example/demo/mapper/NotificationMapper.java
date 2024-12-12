package com.example.demo.mapper;

import org.mapstruct.Mapper;

import com.example.demo.dtos.requests.NotificationRequest;
import com.example.demo.entity.NotificationEntity;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    NotificationEntity toNotification(NotificationRequest notifi);
}
