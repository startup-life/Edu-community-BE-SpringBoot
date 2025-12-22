package kr.adapterz.edu_community.domain.post.repository;

import kr.adapterz.edu_community.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findByIdAndDeletedAtIsNull(Long postId);
    Page<Post> findAllByDeletedAtIsNull(Pageable pageable);
}
