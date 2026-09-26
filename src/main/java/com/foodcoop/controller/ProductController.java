package com.foodcoop.controller;

import com.foodcoop.model.Product;
import com.foodcoop.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /** Public catalog: active products for members. */
    @GetMapping
    public List<Product> list(@RequestParam(required = false) String category,
                              @RequestParam(required = false) Boolean all) {
        if (Boolean.TRUE.equals(all)) {
            return productService.findAll();
        }
        if (category != null && !category.isEmpty()) {
            return productService.findActive();
        }
        return productService.findActive();
    }

    @GetMapping("/{id}")
    public Product get(@PathVariable Long id) {
        return productService.findById(id);
    }

    @PostMapping
    public Product create(@RequestBody Product product) {
        return productService.save(product);
    }

    @PutMapping("/{id}")
    public Product update(@PathVariable Long id, @RequestBody Product product) {
        product.setId(id);
        return productService.save(product);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }
}
