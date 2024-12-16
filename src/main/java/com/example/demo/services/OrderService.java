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

import com.example.demo.dtos.requests.ConditionRequest;
import com.example.demo.dtos.requests.GetAllRequest;
import com.example.demo.dtos.requests.OrderRequest;
import com.example.demo.dtos.responses.OrderResponse;
import com.example.demo.entity.AuthEntity;
import com.example.demo.entity.OrderEntity;
import com.example.demo.entity.ProductEntity;
import com.example.demo.repositorys.AuthRepository;

import com.example.demo.repositorys.OrderRepository;
import com.example.demo.repositorys.ProductRepository;
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

    AuthRepository authRepository;
    ProductRepository productRepository;
    com.example.demo.mapper.OrderMapper orderMapper;

    public OrderResponse create(OrderRequest payload) {

        AuthEntity userOrder = authRepository.findById(payload.getUserId().toString()).orElse(null);
        ProductEntity productOrder = productRepository.findById(payload.getProductId()).orElse(null);

        OrderEntity orderMap = orderMapper.toOrder(payload);
        OrderResponse orderResponse = new OrderResponse();
        
        orderResponse.setId(orderMap.getId());
        orderResponse.setUserId(payload.getUserId());
        orderResponse.setOrderDate(payload.getOrderDate());
        orderResponse.setPrice(payload.getPrice());
        orderResponse.setPaymentMethod(payload.getPaymentMethod());
        orderResponse.setShipperAddress(payload.getShipperAddress());
        orderResponse.setStatus(payload.getStatus());
        orderResponse.setProductId(payload.getProductId());
        orderResponse.setQuantity(payload.getQuantity());
        orderResponse.setPriceSale(payload.getPriceSale());

        orderResponse.setUserOrder(userOrder);
        orderResponse.setUserCreated(userOrder);
        orderResponse.setProductOrder(productOrder);

        orderRepository.save(orderMap);

        return orderResponse;
    }


    @SuppressWarnings("unused")
    public OrderResponse update(OrderRequest payload) {

        ProductEntity productOrder = productRepository.findById(payload.getProductId()).orElse(null);
        OrderEntity findOrder = orderRepository.findById(payload.getOrderId()).orElse(null);
        AuthEntity userOrder = authRepository.findById(findOrder.getCreatedBy().toString()).orElse(null);
        AuthEntity userUpdateOrder = authRepository.findById(Utils.getUserId()).orElse(null);
        if (findOrder == null) {
            throw new NotFoundException("order not found");
        }

        findOrder.setUpdatedBy(UUID.fromString(Utils.getUserId()));
        findOrder.setStatus(payload.getStatus());
        findOrder.setUserId(payload.getUserId());
        findOrder.setQuantity(payload.getQuantity());
        findOrder.setPaymentMethod(payload.getPaymentMethod());
        findOrder.setOrderDate(payload.getOrderDate());

        OrderEntity updateOrder = orderRepository.save(findOrder);

        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setId(updateOrder.getId());
        orderResponse.setUserId(UUID.fromString(Utils.getUserId()));
        orderResponse.setOrderDate(updateOrder.getOrderDate());
        orderResponse.setPrice(updateOrder.getPrice());
        orderResponse.setPaymentMethod(updateOrder.getPaymentMethod());
        orderResponse.setShipperAddress(updateOrder.getShipperAddress());
        orderResponse.setStatus(updateOrder.getStatus());
        orderResponse.setProductId(updateOrder.getProductId());
        orderResponse.setQuantity(updateOrder.getQuantity());
        orderResponse.setPriceSale(updateOrder.getPriceSale());
        orderResponse.setUserOrder(userOrder);
        orderResponse.setUserCreated(userOrder);
        orderResponse.setProductOrder(productOrder);
        orderResponse.setUserUpdated(userUpdateOrder);
        orderResponse.setUserUpdated(userOrder);
        return orderResponse;

    }
    public List<OrderEntity> getOrderByUser(){
        List<OrderEntity> listOrder = orderRepository.findOrderByUser(UUID.fromString(Utils.getUserId()));
        return listOrder;
    }


    public String delete(OrderRequest payload) {
        OrderEntity findOrder = orderRepository.findById(payload.getOrderId()).orElse(null);
        if (findOrder == null) {
            throw new NotFoundException("order notfound");
        }
        orderRepository.deleteById(payload.getOrderId());
        return "delete order success";
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
