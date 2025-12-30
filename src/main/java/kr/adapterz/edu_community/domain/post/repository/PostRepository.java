package kr.adapterz.edu_community.domain.post.repository;

import kr.adapterz.edu_community.domain.post.entity.Post;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("""
        select p
        from Post p
        where p.id = :postId
    """)
    @NonNull
    Optional<Post> findActiveById(@Param("postId") @NonNull Long postId);

    @Query("""
        select p
        from Post p
    """)
    Page<Post> findPage(Pageable pageable);
}
