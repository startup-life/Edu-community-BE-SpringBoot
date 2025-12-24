package kr.adapterz.edu_community.domain.user.repository;

import jakarta.persistence.EntityManager;
import kr.adapterz.edu_community.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserQueryRepositoryImpl implements UserQueryRepository {

    private final EntityManager entityManager;

    @Override
    public Optional<User> findActiveByEmailWithProfileImage(String email) {
        return entityManager.createQuery("""
            select u
            from User u
            left join fetch u.profileImage
            where u.email = :email
            and u.deletedAt is null
        """, User.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst();
    }

    @Override
    public Optional<User> findActiveByIdWithProfileImage(Long id) {
        return entityManager.createQuery("""
            select u
            from User u
            left join fetch u.profileImage
            where u.id = :id
            and u.deletedAt is null
        """, User.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst();
    }
}
