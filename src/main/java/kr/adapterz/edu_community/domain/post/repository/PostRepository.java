package kr.adapterz.edu_community.domain.post.repository;

import kr.adapterz.edu_community.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findAllByDeletedAtIsNull(Pageable pageable);
}
