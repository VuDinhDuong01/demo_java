package com.example.demo.configs;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PreFilter extends OncePerRequestFilter{
    
}
