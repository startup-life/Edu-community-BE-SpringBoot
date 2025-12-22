package kr.adapterz.edu_community.domain.user.repository;

import kr.adapterz.edu_community.domain.user.entity.User;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select case when count(u) > 0 then true else false end " +
            "from User u where u.email = :email and u.deletedAt is null")
    boolean existsActiveByEmail(@Param("email") String email);

    @Query("select case when count(u) > 0 then true else false end " +
            "from User u where u.nickname = :nickname and u.deletedAt is null")
    boolean existsActiveByNickname(@Param("nickname") String nickname);

    @Query("select u from User u where u.email = :email and u.deletedAt is null")
    Optional<User> findActiveByEmail(@Param("email") String email);

    @Query("select u from User u where u.id = :id and u.deletedAt is null")
    @NonNull
    Optional<User> findActiveById(@Param("id") Long id);

    @Query("select u from User u where u.id in :ids and u.deletedAt is null")
    List<User> findAllActiveByIds(@Param("ids") List<Long> ids);
}
