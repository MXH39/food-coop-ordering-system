package com.foodcoop.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * A member's order for one pickup week.
 */
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private User member;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pickup_week_id", nullable = false)
    private PickupWeek pickupWeek;

    /** PENDING, CONFIRMED, READY_FOR_PICKUP, COMPLETED, CANCELLED */
    @Column(nullable = false, length = 30)
    private String status = "PENDING";

    @NotNull
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    public void recalculateTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : items) {
            total = total.add(item.getSubtotal());
        }
        this.totalAmount = total;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getMember() { return member; }
    public void setMember(User member) { this.member = member; }

    public PickupWeek getPickupWeek() { return pickupWeek; }
    public void setPickupWeek(PickupWeek pickupWeek) { this.pickupWeek = pickupWeek; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
}
