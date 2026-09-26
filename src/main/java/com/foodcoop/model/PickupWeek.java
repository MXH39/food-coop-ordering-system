package com.foodcoop.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * One weekly pickup cycle. Members order before the deadline;
 * volunteers close the week and prepare the pickup.
 */
@Entity
@Table(name = "pickup_weeks")
public class PickupWeek {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "week_start", nullable = false)
    private LocalDate weekStart;

    @NotNull
    @Column(name = "week_end", nullable = false)
    private LocalDate weekEnd;

    /** Last date/time members can place or modify orders. */
    @NotNull
    @Column(name = "order_deadline", nullable = false)
    private LocalDate orderDeadline;

    /** OPEN, CLOSED or COMPLETED */
    @Column(nullable = false, length = 20)
    private String status = "OPEN";

    @Column(length = 255)
    private String notes;

    public PickupWeek() {
    }

    public PickupWeek(LocalDate weekStart, LocalDate weekEnd, LocalDate orderDeadline, String status, String notes) {
        this.weekStart = weekStart;
        this.weekEnd = weekEnd;
        this.orderDeadline = orderDeadline;
        this.status = status;
        this.notes = notes;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getWeekStart() { return weekStart; }
    public void setWeekStart(LocalDate weekStart) { this.weekStart = weekStart; }

    public LocalDate getWeekEnd() { return weekEnd; }
    public void setWeekEnd(LocalDate weekEnd) { this.weekEnd = weekEnd; }

    public LocalDate getOrderDeadline() { return orderDeadline; }
    public void setOrderDeadline(LocalDate orderDeadline) { this.orderDeadline = orderDeadline; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
