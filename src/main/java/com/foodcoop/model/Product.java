package com.foodcoop.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * A food item offered by the cooperative for a weekly pickup.
 */
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 500)
    private String description;

    /** e.g. VEGETABLES, DAIRY, BAKERY, PANTRY */
    @Column(length = 50)
    private String category;

    /** e.g. kg, bunch, loaf, dozen */
    @Column(length = 20)
    private String unit;

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stockQuantity = 0;

    @Column(nullable = false)
    private Boolean active = true;

    public Product() {
    }

    public Product(String name, String description, String category, String unit,
                   BigDecimal price, Integer stockQuantity) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.unit = unit;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
