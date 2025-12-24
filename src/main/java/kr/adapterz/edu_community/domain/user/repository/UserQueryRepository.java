package kr.adapterz.edu_community.domain.user.repository;

import kr.adapterz.edu_community.domain.user.entity.User;

import java.util.Optional;

public interface UserQueryRepository {
    Optional<User> findActiveByEmailWithProfileImage(String email);
    Optional<User> findActiveByIdWithProfileImage(Long id);
}
