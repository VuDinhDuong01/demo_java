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

import com.example.demo.dtos.requests.ConditionRequest;
import com.example.demo.dtos.requests.GetAllRequest;
import com.example.demo.dtos.requests.OrderRequest;
import com.example.demo.entity.OrderEntity;
import com.example.demo.mapper.OrderMapper;
import com.example.demo.repositorys.OrderRepository;
import com.example.demo.utils.Utils;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import com.example.demo.exceptions.NotFoundException;
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class OrderService {
    OrderRepository orderRepository;
    OrderMapper orderMapper;

    public OrderEntity create(OrderRequest payload) {
        // OrderEntity findOrder = orderRepository.findByName(payload.getName());
        // if (findCategory != null) {
        //     throw new ForbiddenException("category đã tồn tại");
        // }

        OrderEntity bEntity = orderMapper.toOrder(payload);
        bEntity.setCreatedBy(Utils.getUserId());
        orderRepository.save(bEntity);

        return bEntity;
    }

    public OrderEntity update(OrderRequest payload) {
        OrderEntity findOrder = orderRepository.findById(payload.getId()).orElse(null);
        if (findOrder == null) {
            throw new NotFoundException("category notfound");
        }
        findOrder.setUpdatedBy(Utils.getUserId());
        findOrder.setAddress(payload.getAddress());
        findOrder.setUsername(payload.getUsername());
        findOrder.setPhone(payload.getPhone());
        findOrder.setDisplay(payload.getDisplay());
        findOrder.setStatus(payload.getStatus());
        findOrder.setMessage(payload.getMessage());
        orderRepository.save(findOrder);

        return findOrder;

    }

    public String delete(OrderRequest payload) {
        OrderEntity findOrder = orderRepository.findById(payload.getId()).orElse(null);
        if (findOrder == null) {
            throw new NotFoundException("order notfound");
        }
        orderRepository.deleteById(payload.getId());
        return "delete branch success";

    }

    public Map<String, Object> getAllOrder(GetAllRequest payload) {
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

        Page<OrderEntity> response = orderRepository.findAll(spec, paging);
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
