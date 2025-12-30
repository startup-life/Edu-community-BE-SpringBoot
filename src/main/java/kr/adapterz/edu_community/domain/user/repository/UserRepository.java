package kr.adapterz.edu_community.domain.user.repository;

import kr.adapterz.edu_community.domain.user.entity.User;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("""
        select case when count(u) > 0 then true else false end
        from User u
        where u.email = :email
    """)
    boolean existsActiveByEmail(@Param("email") String email);

    @Query("""
        select case when count(u) > 0 then true else false end
        from User u
        where u.nickname = :nickname
    """)
    boolean existsActiveByNickname(@Param("nickname") String nickname);

    @Query("""
        select u
        from User u
        where u.id = :id
    """)
    @NonNull
    Optional<User> findActiveById(@Param("id") Long id);

    @Query("""
        select u
        from User u
        where u.id in :ids
    """)
    List<User> findAllActiveByIds(@Param("ids") List<Long> ids);
}
