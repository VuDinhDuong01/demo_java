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
import com.example.demo.entity.DetailOrderEntity;
import com.example.demo.entity.OrderEntity;
import com.example.demo.repositorys.DetailOrderRepository;
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
    DetailOrderRepository detailOrderRepository;

    public OrderEntity create(OrderRequest payload) {

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderDate(payload.getOrderDate());
        orderEntity.setPaymentMethod(payload.getPaymentMethod());
        orderEntity.setShipperAddress(payload.getShipperAddress());
        orderEntity.setPrice(payload.getPrice());
        orderEntity.setStatus(payload.getStatus());
        orderEntity.setUserId(payload.getUserId());
        orderEntity.setCreatedBy(payload.getUserId());
        OrderEntity saveOrder = orderRepository.save(orderEntity);

        Float totalPrice = payload.getQuantity() * payload.getPrice();

        DetailOrderEntity detailOrderEntity = new DetailOrderEntity();
        detailOrderEntity.setProductId(payload.getProductId());
        detailOrderEntity.setQuantity(payload.getQuantity());
        detailOrderEntity.setOrderId(saveOrder.getId());
        detailOrderEntity.setUtilPrice(saveOrder.getPrice());
        detailOrderEntity.setTotalPrice(totalPrice);
        detailOrderEntity.setStatus(saveOrder.getStatus());
        detailOrderRepository.save(detailOrderEntity);

        return saveOrder;
    }

    public OrderEntity update(OrderRequest payload) {
        OrderEntity findOrder = orderRepository.findById(payload.getOrderId()).orElse(null);

        if (findOrder == null) {
            throw new NotFoundException("order notfound");
        }

        DetailOrderEntity findDetailOrderEntity = detailOrderRepository.findById(findOrder.getUserId()).orElse(null);

        findOrder.setUpdatedBy(UUID.fromString(Utils.getUserId()));
        findOrder.setStatus(payload.getStatus());
        findOrder.setUserId(payload.getUserId());

        OrderEntity updateOrder = orderRepository.save(findOrder);

        Float totalPrice = updateOrder.getPrice() * payload.getQuantity();

        findDetailOrderEntity.setQuantity(payload.getQuantity());
        findDetailOrderEntity.setStatus(payload.getStatus());
        findDetailOrderEntity.setTotalPrice(totalPrice);
        detailOrderRepository.save(findDetailOrderEntity);
        return findOrder;

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

        Page<OrderEntity> response = detailOrderRepository.findAll(spec, paging);
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
