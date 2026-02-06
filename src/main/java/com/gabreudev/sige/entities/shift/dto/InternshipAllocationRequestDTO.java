package com.gabreudev.sige.entities.shift.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record InternshipAllocationRequestDTO(
        List<UUID> studentIds,
        List<UUID> unityIds,
        LocalDate startDate,
        LocalDate endDate
) {}
