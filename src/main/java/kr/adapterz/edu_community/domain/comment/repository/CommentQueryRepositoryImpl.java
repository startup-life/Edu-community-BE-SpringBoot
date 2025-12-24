package kr.adapterz.edu_community.domain.comment.repository;

import jakarta.persistence.EntityManager;
import kr.adapterz.edu_community.domain.comment.entity.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CommentQueryRepositoryImpl implements CommentQueryRepository {

    private final EntityManager entityManager;

    @Override
    public List<Comment> findAllByPostIdWithAuthor(Long postId) {
        return entityManager.createQuery("""
        select c
        from Comment c
        join fetch c.author u
        left join fetch u.profileImage
        where c.post.id = :postId
        and c.deletedAt is null
        order by c.createdAt asc
    """, Comment.class)
                .setParameter("postId", postId)
                .getResultList();
    }

    @Override
    public Optional<Comment> findByIdAndPostIdAndAuthorId(
            Long commentId,
            Long postId,
            Long authorId
    ) {
        return entityManager.createQuery("""
        select c
        from Comment c
        where c.id = :commentId
        and c.post.id = :postId
        and c.author.id = :authorId
        and c.deletedAt is null
    """, Comment.class)
                .setParameter("commentId", commentId)
                .setParameter("postId", postId)
                .setParameter("authorId", authorId)
                .getResultStream()
                .findFirst();
    }
}
