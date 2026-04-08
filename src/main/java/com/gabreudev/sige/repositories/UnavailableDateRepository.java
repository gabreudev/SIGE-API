package com.gabreudev.sige.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gabreudev.sige.entities.calendar.UnavailableDate;

public interface UnavailableDateRepository extends JpaRepository<UnavailableDate, UUID> {
    boolean existsByDate(LocalDate date);
    Optional<UnavailableDate> findByDate(LocalDate date);
    List<UnavailableDate> findByDateBetween(LocalDate start, LocalDate end);
}
