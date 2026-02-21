package com.onlogn.onlogn.service;

import com.onlogn.onlogn.common.exception.ApiException;
import com.onlogn.onlogn.common.exception.ProblemType;
import com.onlogn.onlogn.entity.UserEntity;
import com.onlogn.onlogn.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserEntity getCurrentUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(
                        ProblemType.RESOURCE_NOT_FOUND,
                        "User not found",
                        "/api/v1/users/me"));
    }
}
