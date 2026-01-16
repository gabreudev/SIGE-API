package com.gabreudev.sige.services;

import com.gabreudev.sige.entities.unity.dto.Unity;
import com.gabreudev.sige.entities.unity.dto.UnityCreateDTO;
import com.gabreudev.sige.entities.unity.dto.UnityUpdateDTO;
import com.gabreudev.sige.entities.user.User;
import com.gabreudev.sige.repositories.UnityRepository;
import com.gabreudev.sige.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UnityService {

    @Autowired
    private UnityRepository unityRepository;

    @Autowired
    private UserRepository userRepository;

    public Unity createUnity(UnityCreateDTO dto) {
        User preceptor = null;
        if (dto.preceptorId() != null) {
            preceptor = userRepository.findById(dto.preceptorId())
                    .orElseThrow(() -> new RuntimeException("Preceptor not found"));
        }

        Unity unity = new Unity(
                dto.name(),
                dto.address(),
                preceptor,
                dto.availability()
        );

        return unityRepository.save(unity);
    }

    public Unity updateUnity(UUID id, UnityUpdateDTO dto) {
        Unity existing = unityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Unity not found"));

        User preceptor = null;
        if (dto.preceptorId() != null) {
            preceptor = userRepository.findById(dto.preceptorId())
                    .orElseThrow(() -> new RuntimeException("Preceptor not found"));
        }

        Unity updated = new Unity(
                existing,
                dto.name(),
                dto.address(),
                preceptor,
                dto.availability()
        );

        return unityRepository.save(updated);
    }

    public void deleteUnity(UUID id) {
        if (!unityRepository.existsById(id)) {
            throw new RuntimeException("Unity not found");
        }
        unityRepository.deleteById(id);
    }

    public Unity findById(UUID id) {
        return unityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Unity not found"));
    }

    public List<Unity> findAll() {
        return unityRepository.findAll();
    }
}

