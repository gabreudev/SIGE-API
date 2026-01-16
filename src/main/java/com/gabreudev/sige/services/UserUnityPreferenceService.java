package com.gabreudev.sige.services;

import com.gabreudev.sige.entities.unity.Unity;
import com.gabreudev.sige.entities.user.User;
import com.gabreudev.sige.repositories.UnityRepository;
import com.gabreudev.sige.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserUnityPreferenceService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UnityRepository unityRepository;

    @Transactional
    public User setPreferredUnities(UUID userId, List<UUID> unityIds) {
        if (unityIds.size() > 3) {
            throw new RuntimeException("User can have maximum 3 preferred unities");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validar se todas as unidades existem
        for (UUID unityId : unityIds) {
            if (!unityRepository.existsById(unityId)) {
                throw new RuntimeException("Unity not found: " + unityId);
            }
        }

        user.getPreferredUnityIds().clear();
        user.getPreferredUnityIds().addAll(unityIds);

        return userRepository.save(user);
    }

    @Transactional
    public User addPreferredUnity(UUID userId, UUID unityId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getPreferredUnityIds().size() >= 3) {
            throw new RuntimeException("User already has 3 preferred unities");
        }

        if (!unityRepository.existsById(unityId)) {
            throw new RuntimeException("Unity not found");
        }

        if (user.getPreferredUnityIds().contains(unityId)) {
            throw new RuntimeException("Unity already in preferred list");
        }

        user.getPreferredUnityIds().add(unityId);

        return userRepository.save(user);
    }

    @Transactional
    public User removePreferredUnity(UUID userId, UUID unityId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.getPreferredUnityIds().remove(unityId);

        return userRepository.save(user);
    }

    public List<Unity> getPreferredUnities(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return unityRepository.findAllById(user.getPreferredUnityIds());
    }
}
