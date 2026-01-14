package kr.adapterz.edu_community.domain.comment.repository;

import kr.adapterz.edu_community.domain.comment.entity.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentQueryRepository {
    List<Comment> findAllByPostIdWithAuthor(Long postId);
    Optional<Comment> findByIdAndPostId(Long commentId, Long postId);
}
