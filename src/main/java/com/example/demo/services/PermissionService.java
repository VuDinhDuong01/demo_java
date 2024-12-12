package com.example.demo.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import org.springframework.stereotype.Service;

import com.example.demo.dtos.requests.ConditionRequest;
import com.example.demo.dtos.requests.GetAllRequest;
import com.example.demo.dtos.requests.PermissionRequest;
import com.example.demo.entity.PermissionEntity;
import com.example.demo.exceptions.InValidException;
import com.example.demo.exceptions.NotFoundException;
import com.example.demo.repositorys.PermissionRepository;
import com.example.demo.utils.Utils;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class PermissionService {

    private PermissionRepository permissionRepository;

    public String createPermission(PermissionRequest.CreatePermissionRequest payload) {
        PermissionEntity checkPermissionExist = permissionRepository.findByName(payload.getName());
        if (checkPermissionExist != null) {
            throw new InValidException("permission existed");
        }

        String user_id = Utils.getUserId();

        PermissionEntity permissionEntity = new PermissionEntity();
        permissionEntity.setName(payload.getName());
        permissionEntity.setDescription(payload.getDescription());
        permissionEntity.setCreatedBy(UUID.fromString(Utils.getUserId()));
        permissionRepository.save(permissionEntity);
        return "create permission success";
    }

    public String updatePermission(PermissionRequest.UpdatePermissionRequest payload) {

        PermissionEntity checkPermissionExist = permissionRepository.findById(payload.getId()).orElse(null);
        if (checkPermissionExist == null) {
            throw new NotFoundException("permission not found");
        }

        PermissionEntity permissionEntity = new PermissionEntity();
        permissionEntity.setName(payload.getName());
        permissionEntity.setDescription(payload.getDescription());
        permissionEntity.setUpdatedBy(UUID.fromString(Utils.getUserId()));
        permissionRepository.save(permissionEntity);
        return "update permission success";
    }

    public String deletePermission(PermissionRequest.DeletePermissionRequest payload) {
        List<PermissionEntity> permissions = permissionRepository.findAllById(payload.getId()).stream().toList();

        for (PermissionEntity per : permissions) {
            per.setIsDelete(true);
            permissionRepository.save(per);
        }
        return "delete permission success";
    }

    public Map<String, Object> getAllPermission(GetAllRequest payload) {

        Integer limit = payload.getLimit() != null ? Integer.parseInt(payload.getLimit()) : 10;
        Integer start = payload.getStart() != null ? Integer.parseInt(payload.getStart()) : 0;
        String sortField = payload.getSortField() != null ? payload.getSortField() : "createdAt";
        String sortType = payload.getSortType() != null ? payload.getSortType() : "asc";
        Sort sort = sortPage(sortField, sortType);
        Pageable paging = PageRequest.of(start, limit, sort);

        List<Predicate> predicates = new ArrayList<>();
        List<String> dateField = new ArrayList<>();
        dateField.add("createdAt");
        dateField.add("updatedAt");
        Specification<PermissionEntity> spec = (root, query, criteriaBuilder) -> {
            predicates.add(criteriaBuilder.notEqual(root.get("isDelete"), true));
            for (ConditionRequest cond : payload.getConditions()) {
                Path<String> key = root.get(cond.getKey());
                List<String> condition = cond.getValue();

                if (dateField.contains(key) && condition.size() >= 2) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd");
                    try {
                        Date startDate = simpleDateFormat.parse(condition.get(0));
                        Date endDate = simpleDateFormat.parse(condition.get(1));
                        predicates.add(criteriaBuilder.between(key.as(Date.class), startDate, endDate));
                    } catch (ParseException e) {

                        e.printStackTrace();
                    }

                } else if (!dateField.contains(key) && condition.size() >= 2) {
                    predicates.add(key.in(condition));
                }
                predicates.add(criteriaBuilder.like(key, "%" + condition.get(0).trim() + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<PermissionEntity> result = permissionRepository.findAll(spec, paging);

        Map<String, Object> mapResult = new HashMap<>();
        mapResult.put("results", result.getContent().size() > 0 ? result.getContent() : new ArrayList());
        mapResult.put("current", result.getPageable().getPageNumber());
        mapResult.put("totalPage", result.getTotalPages());
        mapResult.put("totalElement", result.getTotalElements());
        return mapResult;

    }

    private Sort sortPage(String sortField, String sortType) {
        return sortType == "asc" ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
    }


    
}
