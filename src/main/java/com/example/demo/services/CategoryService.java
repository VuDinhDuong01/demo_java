package com.example.demo.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.example.demo.exceptions.NotFoundException;
import com.example.demo.dtos.requests.CategoryRequest;
import com.example.demo.dtos.requests.ConditionRequest;
import com.example.demo.dtos.requests.GetAllRequest;
import com.example.demo.entity.CategoryEntity;
import com.example.demo.entity.ProductEntity;
import com.example.demo.exceptions.ForbiddenException;
import com.example.demo.mapper.CategoryMapper;
import com.example.demo.repositorys.CategoryRepository;
import com.example.demo.repositorys.ProductRepository;
import com.example.demo.utils.Utils;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CategoryService {
    CategoryRepository categoryRepository;
    // ProductRepository productRepository;
    CategoryMapper categoryMapper;

    public CategoryEntity create(CategoryRequest payload) {
        CategoryEntity findCategory = categoryRepository.findByName(payload.getName());
        if (findCategory != null) {
            throw new ForbiddenException("category đã tồn tại");
        }

        CategoryEntity bEntity = categoryMapper.toCategory(payload);
        
        bEntity.setCreatedBy(UUID.fromString(Utils.getUserId()));
        categoryRepository.save(bEntity);

        return bEntity;
    }

    public CategoryEntity update(CategoryRequest payload) {
        CategoryEntity findCategory = categoryRepository.findByName(payload.getName());
        if (findCategory == null) {
            throw new NotFoundException("category notfound");
        }
        findCategory.setUpdatedBy(UUID.fromString(Utils.getUserId()));
        findCategory.setImages(payload.getImages());
        findCategory.setMetaKeywords(payload.getMetaKeywords());
        findCategory.setMetaTitle(payload.getMetaTitle());
        findCategory.setSlug(payload.getSlug());
        findCategory.setStatus(payload.getStatus());
        categoryRepository.save(findCategory);

        return findCategory;

    }

    public String delete(CategoryRequest payload) {
        CategoryEntity findCategory = categoryRepository.findById(payload.getId()).orElse(null);
        if (findCategory == null) {
            throw new NotFoundException("category notfound");
        }
        categoryRepository.deleteById(payload.getId());
        return "delete branch success";

    }

    public Map<String, Object> getAllCategory(GetAllRequest payload) {
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

        Page<CategoryEntity> response = categoryRepository.findAll(spec, paging);
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
