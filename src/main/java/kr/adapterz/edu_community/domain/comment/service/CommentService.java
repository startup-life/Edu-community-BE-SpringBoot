package kr.adapterz.edu_community.domain.comment.service;

import kr.adapterz.edu_community.domain.comment.dto.response.CommentInfo;
import kr.adapterz.edu_community.domain.comment.dto.response.CommentsResponse;
import kr.adapterz.edu_community.domain.comment.entity.Comment;
import kr.adapterz.edu_community.domain.comment.repository.CommentQueryRepository;
import kr.adapterz.edu_community.domain.comment.repository.CommentRepository;
import kr.adapterz.edu_community.domain.file.entity.File;
import kr.adapterz.edu_community.domain.file.repository.FileRepository;
import kr.adapterz.edu_community.domain.post.dto.response.AuthorInfo;
import kr.adapterz.edu_community.domain.post.entity.Post;
import kr.adapterz.edu_community.domain.post.repository.PostRepository;
import kr.adapterz.edu_community.domain.user.entity.User;
import kr.adapterz.edu_community.domain.user.repository.UserRepository;
import kr.adapterz.edu_community.global.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentQueryRepository commentQueryRepository;
    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    // 특정 게시글의 댓글 조회
    @Transactional(readOnly = true)
    public CommentsResponse getComments(Long postId) {

        List<Comment> comments =
                commentQueryRepository.findAllByPostIdWithAuthor(postId);

        return CommentsResponse.of(
                comments.stream()
                        .map(this::toCommentInfo)
                        .toList()
        );
    }

    // 댓글 작성
    public Long createComment(
            Long postId,
            Long userId,
            String content
    ) {
        User author = userRepository.findActiveById(userId)
                .orElseThrow(() -> new NotFoundException("user_not_found"));

        Post post = postRepository.findActiveById(postId)
                .orElseThrow(() -> new NotFoundException("post_not_found"));

        Comment comment = Comment.create(content, author, post);
        Comment savedComment = commentRepository.save(comment);
        return savedComment.getId();
    }

    // 댓글 수정
    public void updateComment(
            Long postId,
            Long commentId,
            Long userId,
            String content
    ) {
        Comment comment = commentQueryRepository.findByIdAndPostIdAndAuthorId(
                        commentId, postId, userId)
                .orElseThrow(() -> new NotFoundException("comment_not_found"));

        comment.update(content);
    }

    // 댓글 삭제
    public void deleteComment(
            Long postId,
            Long commentId
    ) {
        Comment comment = commentQueryRepository.findByIdAndPostId(
                        commentId, postId)
                .orElseThrow(() -> new NotFoundException("comment_not_found"));

        comment.delete();
    }

    // ========== Private Methods ==========

    // Comment 엔티티를 CommentInfo DTO로 변환하는 메서드
    private CommentInfo toCommentInfo(Comment comment) {
        User user = comment.getAuthor();

        // 프로필 이미지 (null이면 프론트엔드에서 기본 이미지 사용)
        String profileImagePath = Optional.ofNullable(user.getProfileImage())
                .map(File::getFilePath)
                .orElse(null);

        return CommentInfo.of(
                comment.getId(),
                comment.getContent(),
                AuthorInfo.of(
                        user.getId(),
                        user.getNickname(),
                        profileImagePath
                ),
                comment.getCreatedAt()
        );
    }
}
