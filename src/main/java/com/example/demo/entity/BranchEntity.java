package com.example.demo.entity;

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
@Table(name = "branch")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BranchEntity extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid")
    UUID id;

    String name;

    String slug;

    List<String> images;

    Integer status;

    @Column(name = "meta_title")
    String metaTitle;

    @Column(name = "meta_keywords")
    String metaKeywords;
}
