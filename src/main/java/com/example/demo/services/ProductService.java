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

import com.example.demo.dtos.requests.ProductRequest;
import com.example.demo.entity.AuthEntity;
import com.example.demo.entity.BranchEntity;
import com.example.demo.entity.CategoryEntity;
import com.example.demo.entity.ProductEntity;
import com.example.demo.repositorys.AuthRepository;
import com.example.demo.repositorys.BranchRepository;
import com.example.demo.repositorys.CategoryRepository;
import com.example.demo.repositorys.ProductRepository;
import com.example.demo.utils.Utils;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import com.example.demo.exceptions.ForbiddenException;
import com.example.demo.exceptions.NotFoundException;
import com.example.demo.dtos.requests.ConditionRequest;
import com.example.demo.dtos.requests.GetAllRequest;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ProductService {
    ProductRepository productRepository;
    CategoryRepository categoryRepository;
    BranchRepository branchRepository;
    AuthRepository authRepository;
    com.example.demo.mapper.ProductMapper ProductMapper;

    public ProductEntity create(ProductRequest payload) {
        ProductEntity findBranch = productRepository.findByName(payload.getName());
        if (findBranch != null) {
            throw new ForbiddenException("Branch đã tồn tại");
        }

        CategoryEntity categoryEntities = categoryRepository.findById(payload.getCategoryId()).orElse(null);
        BranchEntity branch = branchRepository.findById(payload.getBranchId()).orElse(null);
        AuthEntity userCreated = authRepository.findById(Utils.getUserId()).orElse(null);
        ProductEntity bEntity = ProductMapper.toProduct(payload);
        bEntity.setCategoryEntities(categoryEntities);
        bEntity.setCreatedBy(UUID.fromString(Utils.getUserId()));
        bEntity.setUserCreated(userCreated);
        bEntity.setBranchProduct(branch);

        productRepository.save(bEntity);

        return bEntity;
    }

    public ProductEntity update(ProductRequest payload) {
        ProductEntity findProduct = productRepository.findById(UUID.fromString(payload.getId())).orElse(null);

        if (findProduct == null) {
            throw new NotFoundException("product not found");
        }

        CategoryEntity categoryEntities = categoryRepository.findById(payload.getCategoryId()).orElse(null);
        BranchEntity branch = branchRepository.findById(payload.getBranchId()).orElse(null);
        AuthEntity userUpdated = authRepository.findById(Utils.getUserId()).orElse(null);

        findProduct.setCreatedBy(UUID.fromString(Utils.getUserId()));
        findProduct.setImages(payload.getImages());
        findProduct.setName(payload.getName());
        findProduct.setMetaKeywords(payload.getMetaKeywords());
        findProduct.setMetaTitle(payload.getMetaTitle());
        findProduct.setSlug(payload.getSlug());
        findProduct.setStatus(payload.getStatus());
        findProduct.setBranchId(payload.getBranchId());
        findProduct.setCategoryId(payload.getCategoryId());
        findProduct.setDescription(payload.getDescription());
        findProduct.setHot(payload.getHot());
        findProduct.setBranchProduct(branch);
        findProduct.setCategoryEntities(categoryEntities);
        findProduct.setType(payload.getType());
        findProduct.setPopular(payload.getPopular());
        findProduct.setPriceIn(payload.getPriceIn());
        findProduct.setPriceOut(payload.getPriceOut());
        findProduct.setDescription(payload.getDescription());
        findProduct.setPriceSale(payload.getPriceSale());
        findProduct.setUserUpdated(userUpdated);

        productRepository.save(findProduct);

        return findProduct;

    }

    public String delete(ProductRequest payload) {

        ProductEntity findBranch = productRepository.findById(UUID.fromString(payload.getId())).orElse(null);
        if (findBranch == null) {
            throw new NotFoundException("product notfound");
        }
        productRepository.deleteById(UUID.fromString(payload.getId()));
        return "delete product success";

    }

    public Map<String, Object> getAllProduct(GetAllRequest payload) {
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

        Page<ProductEntity> response = productRepository.findAll(spec, paging);
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
