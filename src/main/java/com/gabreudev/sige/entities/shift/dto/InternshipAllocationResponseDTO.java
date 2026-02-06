package com.gabreudev.sige.entities.shift.dto;

public record InternshipAllocationResponseDTO(
        String message,
        Integer totalStudentsAllocated,
        Integer totalShiftsCreated
) {}
