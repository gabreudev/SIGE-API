package com.gabreudev.sige.repositories;

import com.gabreudev.sige.entities.shift.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, UUID> {
    List<Shift> findByUserId(UUID userId);
    List<Shift> findByUnity_Id(UUID unityId);
}
