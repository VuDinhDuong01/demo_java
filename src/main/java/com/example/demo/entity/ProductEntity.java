package com.example.demo.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "product")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductEntity extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid")
    UUID id;

    String name;

    String slug;

    List<String> images = new ArrayList<>();

    @Column(name = "meta_title")
    String metaTitle;

    @Column(name = "meta_keywords")
    String metaKeywords;

    @Column(name = "price_in")
    Float priceIn;

    @Column(name = "price_out")
    Float priceOut;

    @Column(name = "price_sale")
    Float priceSale;

    @Column(name = "branch_id")
    UUID branchId;

    @Column(name = "category_id")
    UUID CategoryId;

    @Column(name = "view_count")
    Integer viewCount;

    Integer status;

    Integer type;

    String description;

    Integer popular;

    Integer hot;
}
