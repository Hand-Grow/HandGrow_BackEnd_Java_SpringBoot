package com.handgrow.demo.service.impl;

import com.handgrow.demo.dto.request.CreateProductRequest;
import com.handgrow.demo.dto.response.ProductResponse;
import com.handgrow.demo.entity.Enterprise;
import com.handgrow.demo.entity.Product;
import com.handgrow.demo.exception.AppException;
import com.handgrow.demo.exception.ErrorCode;
import com.handgrow.demo.mapper.ProductMapper;
import com.handgrow.demo.repository.EnterpriseRepository;
import com.handgrow.demo.repository.ProductRepository;
import com.handgrow.demo.service.ProductService;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final EnterpriseRepository enterpriseRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ProductResponse createProduct(UUID enterpriseAccountId, CreateProductRequest request) {
        Enterprise enterprise = enterpriseRepository
                .findByAccountId(enterpriseAccountId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Product product = productMapper.toEntity(request);
        product.setEnterprise(enterprise);

        Product saved = productRepository.save(product);
        return productMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByEnterprise(UUID enterpriseAccountId, Pageable pageable) {
        Enterprise enterprise = enterpriseRepository
                .findByAccountId(enterpriseAccountId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return productRepository.findByEnterpriseId(enterprise.getId(), pageable).stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable).stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(UUID id) {
        Product product =
                productRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        return productMapper.toResponse(product);
    }
}
