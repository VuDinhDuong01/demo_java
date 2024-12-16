package com.example.demo.dtos.requests;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ProductRequest {
     String name;

    String slug="";

    List<String> images = new ArrayList<>();


    String metaTitle = "";

    String metaKeywords = "";

    String id;
    
    
    Float priceIn= (float) 0;

    
    Float priceOut =(float)0;

    
    Float priceSale = (float) 0;


    UUID branchId;

 
    UUID categoryId;

    Integer viewCount;

    Integer status=0;

    Integer type=0;

    String description="";

    Integer popular=0;

    Integer hot=0;
}
