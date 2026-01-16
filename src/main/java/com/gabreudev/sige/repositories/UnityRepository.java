package com.gabreudev.sige.repositories;

import com.gabreudev.sige.entities.unity.dto.Unity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UnityRepository extends JpaRepository<Unity, UUID> {
}

