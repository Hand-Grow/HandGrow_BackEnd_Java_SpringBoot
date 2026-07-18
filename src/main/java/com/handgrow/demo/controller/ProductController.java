package com.handgrow.demo.controller;

import com.handgrow.demo.dto.request.CreateProductRequest;
import com.handgrow.demo.dto.response.ProductResponse;
import com.handgrow.demo.service.ProductService;
import com.handgrow.demo.util.SecurityUtils;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    private UUID getAccountId(Principal principal) {
        return SecurityUtils.extractAccountId((org.springframework.security.core.Authentication) principal);
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody CreateProductRequest request, Principal principal) {
        return ResponseEntity.ok(productService.createProduct(getAccountId(principal), request));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts(Pageable pageable) {
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    @GetMapping("/enterprise/me")
    public ResponseEntity<List<ProductResponse>> getMyProducts(Principal principal, Pageable pageable) {
        return ResponseEntity.ok(productService.getProductsByEnterprise(getAccountId(principal), pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }
}
