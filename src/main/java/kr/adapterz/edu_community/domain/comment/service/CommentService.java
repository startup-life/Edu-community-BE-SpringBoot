package kr.adapterz.edu_community.domain.comment.service;

import kr.adapterz.edu_community.domain.comment.dto.response.CommentInfo;
import kr.adapterz.edu_community.domain.comment.dto.response.CommentsResponse;
import kr.adapterz.edu_community.domain.comment.entity.Comment;
import kr.adapterz.edu_community.domain.comment.repository.CommentQueryRepository;
import kr.adapterz.edu_community.domain.comment.repository.CommentRepository;
import kr.adapterz.edu_community.domain.file.entity.File;
import kr.adapterz.edu_community.domain.file.repository.FileRepository;
import kr.adapterz.edu_community.domain.post.dto.response.AuthorInfo;
import kr.adapterz.edu_community.domain.user.entity.User;
import kr.adapterz.edu_community.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentQueryRepository commentQueryRepository;
    private final FileRepository fileRepository;
    private final UserRepository userRepository;

    // 특정 게시글의 댓글 조회
    public CommentsResponse getComments(Long postId) {

        List<Comment> comments =
                commentQueryRepository.findAllByPostIdWithAuthor(postId);

        return CommentsResponse.of(
                comments.stream()
                        .map(this::toCommentInfo)
                        .toList()
        );
    }

    // ========== Private Methods ==========

    // Comment 엔티티를 CommentInfo DTO로 변환하는 메서드
    private CommentInfo toCommentInfo(Comment comment) {
        User user = comment.getAuthor();

        String DEFAULT_PROFILE_IMAGE_PATH = "/pubic/profile/default.jpg";
        String profileImagePath = Optional.ofNullable(user.getProfileImage())
                .map(File::getFilePath)
                .orElse(DEFAULT_PROFILE_IMAGE_PATH);

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
