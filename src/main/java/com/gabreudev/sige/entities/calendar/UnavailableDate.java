package com.gabreudev.sige.entities.calendar;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "unavailable_dates")
@Data
@NoArgsConstructor
public class UnavailableDate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private LocalDate date;

    @Column(length = 255)
    private String reason;

    public UnavailableDate(LocalDate date, String reason) {
        this.date = date;
        this.reason = reason;
    }
}
