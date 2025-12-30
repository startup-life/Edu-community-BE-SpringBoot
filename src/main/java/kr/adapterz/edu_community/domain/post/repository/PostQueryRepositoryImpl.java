package kr.adapterz.edu_community.domain.post.repository;

import jakarta.persistence.EntityManager;
import kr.adapterz.edu_community.domain.post.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostQueryRepositoryImpl implements PostQueryRepository {

    private final EntityManager entityManager;

    @Override
    public Optional<Post> findByIdWithAuthor(Long postId) {
        return entityManager.createQuery("""
            select p
            from Post p
            join fetch p.author u
            left join fetch u.profileImage
            left join fetch p.attachFile
            where p.id = :postId
        """, Post.class)
                .setParameter("postId", postId)
                .getResultStream()
                .findFirst();
    }
}
