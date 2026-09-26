package com.foodcoop.repository;

import com.foodcoop.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByMemberIdOrderByIdDesc(Long memberId);
    List<Order> findByPickupWeekIdOrderByIdAsc(Long pickupWeekId);

    @Query("select distinct o from Order o join fetch o.items join fetch o.member where o.id = :id")
    Order findWithItemsById(@Param("id") Long id);
}
