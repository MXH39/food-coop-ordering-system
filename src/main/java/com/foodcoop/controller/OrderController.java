package com.foodcoop.controller;

import com.foodcoop.dto.OrderResponse;
import com.foodcoop.model.Order;
import com.foodcoop.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{id}")
    public OrderResponse get(@PathVariable Long id) {
        return orderService.getById(id);
    }

    @GetMapping
    public List<OrderResponse> list(@RequestParam(required = false) Long memberId,
                                    @RequestParam(required = false) Long pickupWeekId) {
        List<Order> orders;
        if (memberId != null) {
            orders = orderService.findByMember(memberId);
        } else if (pickupWeekId != null) {
            orders = orderService.findByPickupWeek(pickupWeekId);
        } else {
            throw new IllegalArgumentException("Specify memberId or pickupWeekId.");
        }
        return orders.stream()
                .map(o -> orderService.getById(o.getId()))
                .collect(Collectors.toList());
    }

    /**
     * Place an order. Body example:
     * { "memberId": 1, "pickupWeekId": 1, "items": { "1": 2, "3": 1 } }
     * where the items map is productId -> quantity.
     */
    @PostMapping
    public OrderResponse place(@RequestBody PlaceOrderRequest request) {
        Order order = orderService.placeOrder(request.getMemberId(), request.getPickupWeekId(),
                request.itemsAsMap());
        return orderService.getById(order.getId());
    }

    /** Volunteer action: confirm, mark ready for pickup, complete or cancel. */
    @PutMapping("/{id}/status")
    public OrderResponse updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        if (status == null || status.isEmpty()) {
            throw new IllegalArgumentException("Missing 'status' field.");
        }
        Order updated = orderService.updateStatus(id, status);
        return orderService.getById(updated.getId());
    }

    /** Member action: cancel own order. */
    @PutMapping("/{id}/cancel")
    public OrderResponse cancel(@PathVariable Long id, @RequestParam Long memberId) {
        orderService.cancelOrder(id, memberId);
        return orderService.getById(id);
    }

    public static class PlaceOrderRequest {
        private Long memberId;
        private Long pickupWeekId;
        private Map<String, Integer> items;

        public Long getMemberId() { return memberId; }
        public void setMemberId(Long memberId) { this.memberId = memberId; }

        public Long getPickupWeekId() { return pickupWeekId; }
        public void setPickupWeekId(Long pickupWeekId) { this.pickupWeekId = pickupWeekId; }

        public Map<String, Integer> getItems() { return items; }
        public void setItems(Map<String, Integer> items) { this.items = items; }

        public Map<Long, Integer> itemsAsMap() {
            return items == null ? null : items.entrySet().stream()
                    .collect(java.util.stream.Collectors.toMap(
                            e -> Long.valueOf(e.getKey()), Map.Entry::getValue));
        }
    }
}
