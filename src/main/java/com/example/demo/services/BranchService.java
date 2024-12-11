package com.example.demo.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.demo.dtos.requests.BranchRequest;
import com.example.demo.dtos.requests.ConditionRequest;
import com.example.demo.dtos.requests.GetAllRequest;
import com.example.demo.entity.BranchEntity;
import com.example.demo.exceptions.ForbiddenException;
import com.example.demo.exceptions.NotFoundException;
import com.example.demo.repositorys.BranchRepository;
import com.example.demo.utils.Utils;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class BranchService {
    BranchRepository branchRepository;
    com.example.demo.mapper.BranchMapper branchMapper;

    public BranchEntity create(BranchRequest payload) {
        BranchEntity findBranch = branchRepository.findByName(payload.getName());
        if (findBranch != null) {
            throw new ForbiddenException("Branch đã tồn tại");
        }

        BranchEntity bEntity = branchMapper.toBranch(payload);
        bEntity.setCreatedBy(Utils.getUserId());
        branchRepository.save(bEntity);

        return bEntity;
    }

    public BranchEntity update(BranchRequest payload) {
        BranchEntity findBranch = branchRepository.findByName(payload.getName());
        if (findBranch == null) {
            throw new NotFoundException("Branch notfound");
        }
        findBranch.setCreatedBy(Utils.getUserId());
        findBranch.setImages(payload.getImages());
        findBranch.setMetaKeywords(payload.getMetaKeywords());
        findBranch.setMetaTitle(payload.getMetaTitle());
        findBranch.setSlug(payload.getSlug());
        findBranch.setStatus(payload.getStatus());
        branchRepository.save(findBranch);

        return findBranch;

    }

    public String delete(BranchRequest payload) {
        BranchEntity findBranch = branchRepository.findById(payload.getId()).orElse(null);
        if (findBranch == null) {
            throw new NotFoundException("Branch notfound");
        }
        branchRepository.deleteById(payload.getId());
        return "delete branch success";

    }

    public Map<String, Object> getAllBranch(GetAllRequest payload) {
        String fieldSort = payload.getSortField() != null ? payload.getSortField() : "createdAt";
        String fieldType = payload.getSortType() != null ? payload.getSortType() : "asc";
        Sort sort = sort(fieldSort, fieldType);

        Integer limit = payload.getLimit() != null ? Integer.parseInt(payload.getLimit()) : 10;
        Integer start = payload.getStart() != null ? Integer.parseInt(payload.getStart()) : 0;

        Pageable paging = PageRequest.of(start, limit, sort);

        Specification spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicate = new ArrayList<>();
            Set<String> date = new HashSet<>();
            date.add("createdAt");
            date.add("updatedAt");
            for (ConditionRequest condition : payload.getConditions()) {
                Path<Object> getKey = root.get(condition.getKey());
                List<String> getValue = condition.getValue();
                if ("status".equals(condition.getKey())) {
                    predicate.add(getKey.in(getValue));
                } else if (date.contains(condition.getKey())) {
                    System.out.println("updatedAt");
                }
                predicate.add(criteriaBuilder.like(getKey.as(String.class), "%" + getValue.get(0).trim() + "%"));
            }
            return criteriaBuilder.and(predicate.toArray(new Predicate[0]));
        };

        Page<BranchEntity> response = branchRepository.findAll(spec, paging);
        Map<String, Object> result = new HashMap<>();
        result.put("results", response.getContent().size() > 0 ? response.getContent() : new ArrayList());
        result.put("current", response.getPageable().getPageNumber());
        result.put("totalPage", response.getTotalPages());
        result.put("totalElement", response.getTotalElements());
        return result;
    }

    private Sort sort(String filedSort, String fieldType) {
        return fieldType == "asc" ? Sort.by(filedSort).ascending() : Sort.by(filedSort).descending();
    }
}
