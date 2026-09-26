package com.foodcoop.dto;

import com.foodcoop.model.Order;
import com.foodcoop.model.OrderItem;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data transfer object returned by the REST API.
 */
public class OrderResponse {

    private Long id;
    private String memberName;
    private String memberEmail;
    private Long pickupWeekId;
    private String pickupWeekLabel;
    private String status;
    private BigDecimal totalAmount;
    private String createdAt;
    private List<ItemDto> items;

    public static OrderResponse from(Order order) {
        OrderResponse r = new OrderResponse();
        r.id = order.getId();
        if (order.getMember() != null) {
            r.memberName = order.getMember().getFullName();
            r.memberEmail = order.getMember().getEmail();
        }
        if (order.getPickupWeek() != null) {
            r.pickupWeekId = order.getPickupWeek().getId();
            r.pickupWeekLabel = order.getPickupWeek().getWeekStart() + " to " + order.getPickupWeek().getWeekEnd();
        }
        r.status = order.getStatus();
        r.totalAmount = order.getTotalAmount();
        if (order.getCreatedAt() != null) {
            r.createdAt = order.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
        r.items = order.getItems() == null ? null : order.getItems().stream()
                .map(OrderResponse::toItem)
                .collect(Collectors.toList());
        return r;
    }

    private static ItemDto toItem(OrderItem item) {
        ItemDto dto = new ItemDto();
        dto.productId = item.getProduct() != null ? item.getProduct().getId() : null;
        dto.productName = item.getProduct() != null ? item.getProduct().getName() : null;
        dto.quantity = item.getQuantity();
        dto.unitPrice = item.getUnitPrice();
        dto.subtotal = item.getSubtotal();
        return dto;
    }

    public static class ItemDto {
        public Long productId;
        public String productName;
        public Integer quantity;
        public BigDecimal unitPrice;
        public BigDecimal subtotal;

        public Long getProductId() { return productId; }
        public String getProductName() { return productName; }
        public Integer getQuantity() { return quantity; }
        public BigDecimal getUnitPrice() { return unitPrice; }
        public BigDecimal getSubtotal() { return subtotal; }
    }

    public Long getId() { return id; }
    public String getMemberName() { return memberName; }
    public String getMemberEmail() { return memberEmail; }
    public Long getPickupWeekId() { return pickupWeekId; }
    public String getPickupWeekLabel() { return pickupWeekLabel; }
    public String getStatus() { return status; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getCreatedAt() { return createdAt; }
    public List<ItemDto> getItems() { return items; }
}
