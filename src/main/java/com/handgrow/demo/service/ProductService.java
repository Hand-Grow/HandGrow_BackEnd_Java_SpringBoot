package com.handgrow.demo.service;

import com.handgrow.demo.dto.request.CreateProductRequest;
import com.handgrow.demo.dto.response.ProductResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    ProductResponse createProduct(UUID enterpriseAccountId, CreateProductRequest request);
    List<ProductResponse> getProductsByEnterprise(UUID enterpriseAccountId, Pageable pageable);
    List<ProductResponse> getAllProducts(Pageable pageable);
    ProductResponse getProductById(UUID id);
}
