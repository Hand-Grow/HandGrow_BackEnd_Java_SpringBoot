package com.handgrow.demo.service.impl;

import com.handgrow.demo.dto.request.CreateProductRequest;
import com.handgrow.demo.dto.response.ProductResponse;
import com.handgrow.demo.entity.Enterprise;
import com.handgrow.demo.entity.Product;
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

    @Override
    @Transactional
    public ProductResponse createProduct(UUID enterpriseAccountId, CreateProductRequest request) {
        Enterprise enterprise = enterpriseRepository
                .findByAccountId(enterpriseAccountId)
                .orElseThrow(() -> new RuntimeException("Enterprise not found for account"));

        Product product = Product.builder()
                .enterprise(enterprise)
                .name(request.getName())
                .category(request.getCategory())
                .unit(request.getUnit())
                .basePrice(request.getBasePrice())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .attributes(request.getAttributes())
                .priceTiers(request.getPriceTiers().stream()
                        .map(t -> new Product.PriceTier(t.getMinQty(), t.getPrice()))
                        .collect(Collectors.toList()))
                .build();

        Product saved = productRepository.save(product);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByEnterprise(UUID enterpriseAccountId, Pageable pageable) {
        Enterprise enterprise = enterpriseRepository
                .findByAccountId(enterpriseAccountId)
                .orElseThrow(() -> new RuntimeException("Enterprise not found for account"));

        return productRepository.findByEnterpriseId(enterprise.getId(), pageable).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(UUID id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        return mapToResponse(product);
    }

    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .enterpriseId(product.getEnterprise().getId())
                .enterpriseName(product.getEnterprise().getCompanyName())
                .name(product.getName())
                .category(product.getCategory())
                .unit(product.getUnit())
                .basePrice(product.getBasePrice())
                .description(product.getDescription())
                .imageUrl(product.getImageUrl())
                .attributes(product.getAttributes())
                .priceTiers(product.getPriceTiers().stream()
                        .map(t -> new ProductResponse.PriceTierResponse(t.getMinQty(), t.getPrice()))
                        .collect(Collectors.toList()))
                .build();
    }
}
