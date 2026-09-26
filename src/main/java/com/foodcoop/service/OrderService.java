package com.foodcoop.service;

import com.foodcoop.dto.OrderResponse;
import com.foodcoop.model.Order;
import com.foodcoop.model.OrderItem;
import com.foodcoop.model.PickupWeek;
import com.foodcoop.model.Product;
import com.foodcoop.model.User;
import com.foodcoop.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Core ordering business rules: orders may only be placed while the pickup
 * week is OPEN and before its order deadline; stock is reduced when an order
 * is confirmed by a volunteer.
 */
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final PickupWeekService pickupWeekService;
    private final ProductService productService;

    public OrderService(OrderRepository orderRepository,
                        UserService userService,
                        PickupWeekService pickupWeekService,
                        ProductService productService) {
        this.orderRepository = orderRepository;
        this.userService = userService;
        this.pickupWeekService = pickupWeekService;
        this.productService = productService;
    }

    public List<Order> findByMember(Long memberId) {
        return orderRepository.findByMemberIdOrderByIdDesc(memberId);
    }

    public List<Order> findByPickupWeek(Long pickupWeekId) {
        return orderRepository.findByPickupWeekIdOrderByIdAsc(pickupWeekId);
    }

    public OrderResponse getById(Long id) {
        Order order = orderRepository.findWithItemsById(id);
        if (order == null) {
            throw new IllegalArgumentException("Order not found: " + id);
        }
        return OrderResponse.from(order);
    }

    @Transactional
    public Order placeOrder(Long memberId, Long pickupWeekId, Map<Long, Integer> productQuantities) {
        User member = userService.findById(memberId);
        PickupWeek week = pickupWeekService.findById(pickupWeekId);

        if (!"OPEN".equals(week.getStatus())) {
            throw new IllegalStateException("This pickup week is not open for orders.");
        }
        if (week.getOrderDeadline().isBefore(LocalDate.now())) {
            throw new IllegalStateException("The order deadline for this pickup week has passed.");
        }
        if (productQuantities == null || productQuantities.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item.");
        }

        Order order = new Order();
        order.setMember(member);
        order.setPickupWeek(week);
        order.setStatus("PENDING");

        for (Map.Entry<Long, Integer> entry : productQuantities.entrySet()) {
            Integer quantity = entry.getValue();
            if (quantity == null || quantity <= 0) {
                continue;
            }
            Product product = productService.findById(entry.getKey());
            if (!Boolean.TRUE.equals(product.getActive())) {
                throw new IllegalStateException("Product is not available: " + product.getName());
            }
            order.addItem(new OrderItem(product, quantity));
        }

        if (order.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item.");
        }
        order.recalculateTotal();
        return orderRepository.save(order);
    }

    @Transactional
    public Order updateStatus(Long orderId, String newStatus) {
        Order order = orderRepository.findWithItemsById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order not found: " + orderId);
        }
        String previous = order.getStatus();
        order.setStatus(newStatus);

        // Reduce stock the first time an order is confirmed.
        if ("CONFIRMED".equals(newStatus) && !"CONFIRMED".equals(previous)) {
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                int remaining = product.getStockQuantity() - item.getQuantity();
                if (remaining < 0) {
                    throw new IllegalStateException("Not enough stock for product: " + product.getName());
                }
                product.setStockQuantity(remaining);
                productService.save(product);
            }
        }
        // Give stock back when a confirmed order is cancelled.
        if ("CANCELLED".equals(newStatus) && "CONFIRMED".equals(previous)) {
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
                productService.save(product);
            }
        }
        return orderRepository.save(order);
    }

    @Transactional
    public void cancelOrder(Long orderId, Long memberId) {
        Order order = orderRepository.findWithItemsById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order not found: " + orderId);
        }
        if (!order.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("Order does not belong to member: " + memberId);
        }
        if ("COMPLETED".equals(order.getStatus())) {
            throw new IllegalStateException("Completed orders cannot be cancelled.");
        }
        order.setStatus("CANCELLED");
        orderRepository.save(order);
    }

    public BigDecimal totalForWeek(Long pickupWeekId) {
        BigDecimal total = BigDecimal.ZERO;
        for (Order order : findByPickupWeek(pickupWeekId)) {
            if (!"CANCELLED".equals(order.getStatus())) {
                total = total.add(order.getTotalAmount());
            }
        }
        return total;
    }
}
