package com.foodcoop.repository;

import com.foodcoop.model.PickupWeek;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PickupWeekRepository extends JpaRepository<PickupWeek, Long> {
    List<PickupWeek> findAllByOrderByWeekStartDesc();
    Optional<PickupWeek> findByStatus(String status);
}
