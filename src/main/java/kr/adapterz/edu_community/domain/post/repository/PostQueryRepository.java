package kr.adapterz.edu_community.domain.post.repository;

import kr.adapterz.edu_community.domain.post.entity.Post;

import java.util.Optional;

public interface PostQueryRepository {

    Optional<Post> findByIdWithAuthor(Long postId);
}
