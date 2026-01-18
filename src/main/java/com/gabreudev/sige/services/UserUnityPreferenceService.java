package com.gabreudev.sige.services;

import com.gabreudev.sige.entities.unity.Unity;
import com.gabreudev.sige.entities.user.User;
import com.gabreudev.sige.entities.user.UserRole;
import com.gabreudev.sige.entities.user.dto.UserPreferredUnitiesResponseDTO;
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

    private void checkPermission(UUID userId, User currentUser) {
        if (!currentUser.getId().equals(userId) && !currentUser.getUserRole().equals(UserRole.ADMIN)) {
            throw new RuntimeException("Você não tem permissão para modificar essas preferências");
        }
    }

    @Transactional
    public UserPreferredUnitiesResponseDTO setPreferredUnitiesWithResponse(UUID userId, List<UUID> unityIds, User currentUser) {
        checkPermission(userId, currentUser);
        User updatedUser = setPreferredUnities(userId, unityIds);
        List<Unity> unities = getPreferredUnities(userId);
        return UserPreferredUnitiesResponseDTO.fromUser(updatedUser, unities);
    }

    @Transactional
    public UserPreferredUnitiesResponseDTO addPreferredUnityWithResponse(UUID userId, UUID unityId, User currentUser) {
        checkPermission(userId, currentUser);
        User updatedUser = addPreferredUnity(userId, unityId);
        List<Unity> unities = getPreferredUnities(userId);
        return UserPreferredUnitiesResponseDTO.fromUser(updatedUser, unities);
    }

    @Transactional
    public UserPreferredUnitiesResponseDTO removePreferredUnityWithResponse(UUID userId, UUID unityId, User currentUser) {
        checkPermission(userId, currentUser);
        User updatedUser = removePreferredUnity(userId, unityId);
        List<Unity> unities = getPreferredUnities(userId);
        return UserPreferredUnitiesResponseDTO.fromUser(updatedUser, unities);
    }

    public UserPreferredUnitiesResponseDTO getPreferredUnitiesWithResponse(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Unity> unities = getPreferredUnities(userId);
        return UserPreferredUnitiesResponseDTO.fromUser(user, unities);
    }
}
