package com.erp.inventory.system.service.impl;

import com.erp.inventory.system.dto.ProductDto;
import com.erp.inventory.system.model.Category;
import com.erp.inventory.system.model.Product;
import com.erp.inventory.system.model.Supplier;
import com.erp.inventory.system.repository.CategoryRepository;
import com.erp.inventory.system.repository.ProductRepository;
import com.erp.inventory.system.repository.SupplierRepository;
import com.erp.inventory.system.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;

    @Override
    public ProductDto createProduct(ProductDto productDto) {
        productRepository.findBySku(productDto.getSku()).ifPresent(p -> {
            throw new IllegalStateException("Product with SKU '" + p.getSku() + "' already exists.");
        });

        Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + productDto.getCategoryId()));

        Supplier supplier = supplierRepository.findById(productDto.getSupplierId())
                .orElseThrow(() -> new EntityNotFoundException("Supplier not found with id: " + productDto.getSupplierId()));

        Product product = new Product();
        product.setSku(productDto.getSku());
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setCategory(category);
        product.setSupplier(supplier);
        product.setUnitPrice(productDto.getUnitPrice());

        Product savedProduct = productRepository.save(product);
        return mapToDto(savedProduct);
    }

    @Override
    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
        return mapToDto(product);
    }

    @Override
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public ProductDto updateProduct(Long id, ProductDto productDto) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));

        Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + productDto.getCategoryId()));

        Supplier supplier = supplierRepository.findById(productDto.getSupplierId())
                .orElseThrow(() -> new EntityNotFoundException("Supplier not found with id: " + productDto.getSupplierId()));

        existingProduct.setName(productDto.getName());
        existingProduct.setDescription(productDto.getDescription());
        existingProduct.setCategory(category);
        existingProduct.setSupplier(supplier);
        existingProduct.setUnitPrice(productDto.getUnitPrice());

        Product updatedProduct = productRepository.save(existingProduct);
        return mapToDto(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    private ProductDto mapToDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setSku(product.getSku());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setCategoryId(product.getCategory().getId());
        dto.setSupplierId(product.getSupplier().getId());
        dto.setUnitPrice(product.getUnitPrice());
        return dto;
    }
}

