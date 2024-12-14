package com.example.demo.mapper;

import org.mapstruct.Mapper;

import com.example.demo.dtos.requests.BranchRequest;
import com.example.demo.entity.BranchEntity;

@Mapper(componentModel = "spring")
public interface BranchMapper {
    BranchEntity toBranch(BranchRequest bRequest);
}
