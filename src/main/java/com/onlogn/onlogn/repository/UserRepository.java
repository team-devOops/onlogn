package com.onlogn.onlogn.repository;

import com.onlogn.onlogn.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByProfileSlug(String profileSlug);
    Optional<UserEntity> findByGithubId(String githubId);
}
