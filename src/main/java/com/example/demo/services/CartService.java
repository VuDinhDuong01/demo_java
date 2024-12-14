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

import com.example.demo.dtos.requests.CartRequest;
import com.example.demo.dtos.requests.ConditionRequest;
import com.example.demo.dtos.requests.GetAllRequest;
import com.example.demo.entity.CartEntity;
import com.example.demo.exceptions.ForbiddenException;
import com.example.demo.exceptions.NotFoundException;

import com.example.demo.repositorys.CartRepository;
import com.example.demo.utils.Utils;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@Data
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartService {
    CartRepository cartRepository;
    com.example.demo.mapper.CartMapper CartMapper;

    public CartEntity create(CartRequest payload) {
        CartEntity findCart = cartRepository.findById(payload.getUserId()).orElse(null);
        if (findCart != null) {
            throw new ForbiddenException("cart đã tồn tại");
        }

        CartEntity bEntity = CartMapper.toCart(payload);
        bEntity.setCreatedBy(UUID.fromString(Utils.getUserId()));
        cartRepository.save(bEntity);

        return bEntity;
    }

    public CartEntity update(CartRequest payload) {
        CartEntity findCart = cartRepository.findById(payload.getIdCart()).orElse(null);
        if (findCart == null) {
            throw new NotFoundException("Cart notfound");
        }
        findCart.setUpdatedBy(UUID.fromString(Utils.getUserId()));
        findCart.setProductId(payload.getProductId());
        findCart.setUserId(payload.getUserId());
        findCart.setQuantity(payload.getQuantity());
        cartRepository.save(findCart);
        return findCart;

    }

    public String delete(CartRequest payload) {
        CartEntity findCart = cartRepository.findById(payload.getIdCart()).orElse(null);
        if (findCart == null) {
            throw new NotFoundException("Cart notfound");
        }
        cartRepository.deleteById(payload.getIdCart());
        return "delete Cart success";

    }

    public Map<String, Object> getAllCart(GetAllRequest payload) {
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

        Page<CartEntity> response = cartRepository.findAll(spec, paging);
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
