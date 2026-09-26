package com.foodcoop.service;

import com.foodcoop.model.PickupWeek;
import com.foodcoop.repository.PickupWeekRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PickupWeekService {

    private final PickupWeekRepository pickupWeekRepository;

    public PickupWeekService(PickupWeekRepository pickupWeekRepository) {
        this.pickupWeekRepository = pickupWeekRepository;
    }

    public List<PickupWeek> findAll() {
        return pickupWeekRepository.findAllByOrderByWeekStartDesc();
    }

    public PickupWeek findById(Long id) {
        return pickupWeekRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pickup week not found: " + id));
    }

    public PickupWeek save(PickupWeek week) {
        return pickupWeekRepository.save(week);
    }
}
