package kr.adapterz.edu_community.domain.user.repository;

import kr.adapterz.edu_community.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    Optional<User> findByEmail(String email);
    Optional<User> findByIdAndDeletedAtIsNull(Long id);
    List<User> findAllByIdInAndDeletedAtIsNull(List<Long> ids);
}
