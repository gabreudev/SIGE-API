package com.gabreudev.sige.repositories;

import com.gabreudev.sige.entities.unity.Unity;
import com.gabreudev.sige.entities.unity.UnityRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UnityRepository extends JpaRepository<Unity, UUID> {
    List<Unity> findByUnityRole(UnityRole unityRole);
}

