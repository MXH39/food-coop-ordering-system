package com.foodcoop.controller;

import com.foodcoop.model.PickupWeek;
import com.foodcoop.service.PickupWeekService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pickup-weeks")
public class PickupWeekController {

    private final PickupWeekService pickupWeekService;

    public PickupWeekController(PickupWeekService pickupWeekService) {
        this.pickupWeekService = pickupWeekService;
    }

    @GetMapping
    public List<PickupWeek> list() {
        return pickupWeekService.findAll();
    }

    @GetMapping("/open")
    public PickupWeek openWeek() {
        return pickupWeekService.findAll().stream()
                .filter(w -> "OPEN".equals(w.getStatus()))
                .findFirst()
                .orElse(null);
    }

    @GetMapping("/{id}")
    public PickupWeek get(@PathVariable Long id) {
        return pickupWeekService.findById(id);
    }

    @PostMapping
    public PickupWeek create(@RequestBody PickupWeek week) {
        return pickupWeekService.save(week);
    }

    @PutMapping("/{id}")
    public PickupWeek update(@PathVariable Long id, @RequestBody PickupWeek week) {
        week.setId(id);
        return pickupWeekService.save(week);
    }
}
