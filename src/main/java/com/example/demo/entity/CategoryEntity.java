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
@Table(name = "category")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryEntity extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid")
    UUID id;

    String name;

    String slug ;
    List<String> images = new ArrayList<>();

    @Column(name = "meta_title")
    String metaTitle;

    @Column(name = "meta_keywords")
    String metaKeywords;

    Integer status ;

    Integer type;
    
    String parentId ;
}
