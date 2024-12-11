package com.example.demo.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dtos.requests.BranchRequest;
import com.example.demo.dtos.requests.GetAllRequest;
import com.example.demo.dtos.responses.BaseResponse;
import com.example.demo.entity.BranchEntity;
import com.example.demo.services.BranchService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Controller
@RestController
@RequestMapping("/api/v1")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class BranchController {

    BranchService branchService;

    @PostMapping("/branch")
    public BaseResponse<BranchEntity> create(@RequestBody @Valid BranchRequest body){

        BranchEntity response = branchService.create(body);

        return BaseResponse.<BranchEntity>builder().result(response).build();
    }

    @PutMapping("/branch")
    public BaseResponse<BranchEntity> update(@RequestBody @Valid BranchRequest body) {

        BranchEntity response = branchService.update(body);

        return BaseResponse.<BranchEntity>builder().result(response).build();
    }


    @DeleteMapping("/branch")
    public BaseResponse<String> delete(@RequestBody @Valid BranchRequest body) {
        String response = branchService.delete(body);
        return BaseResponse.<String>builder().result(response).build();
    }


    @GetMapping("/filter-branch")
    public BaseResponse<Map<String, Object>> filter(@RequestBody @Valid GetAllRequest body){

        Map<String , Object> response = branchService.getAllBranch(body);
        return BaseResponse.<Map<String, Object>>builder().result(response).build();
    }
}
