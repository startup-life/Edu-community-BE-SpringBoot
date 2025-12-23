package kr.adapterz.edu_community.domain.comment.service;

import kr.adapterz.edu_community.domain.comment.dto.response.CommentInfo;
import kr.adapterz.edu_community.domain.comment.dto.response.CommentRelationData;
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
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentQueryRepository commentQueryRepository;
    private final FileRepository fileRepository;
    private final UserRepository userRepository;

    private final String DEFAULT_PROFILE_IMAGE_PATH = "/pubic/profile/default.jpg";

    // 특정 게시글의 댓글 조회
    public CommentsResponse getComments(Long postId) {

        List<Comment> comments = commentQueryRepository.findAllByPostIdWithAuthor(postId);
        if (comments.isEmpty()) {
            return CommentsResponse.of(List.of());
        }

        CommentRelationData relationData = loadCommentRelationData(comments);
        List<CommentInfo> commentResults = comments.stream()
                .map(comment -> toCommentInfo(comment, relationData))
                .toList();

        return CommentsResponse.of(commentResults);
    }

    // ========== Private Methods ==========

    // Comment 엔티티를 CommentInfo DTO로 변환하는 메서드
    private CommentInfo toCommentInfo(
            Comment comment,
            CommentRelationData data
    ) {
        User user = data.getUserMap().get(comment.getAuthor().getId());

        String profileImagePath = setProfileImagePath(user, data);

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

    // 댓글과 관련된 데이터를 로드하는 메서드
    private CommentRelationData loadCommentRelationData(List<Comment> comments) {

        List<Long> authorIds = comments.stream()
                .map(c -> c.getAuthor().getId())
                .distinct()
                .toList();

        Map<Long, User> userMap =
                userRepository.findAllActiveByIds(authorIds).stream()
                        .collect(Collectors.toMap(User::getId, Function.identity()));

        List<Long> fileIds = userMap.values().stream()
                .map(User::getProfileImageId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<Long, File> fileMap = fileIds.isEmpty()
                ? Map.of()
                : fileRepository.findAllById(fileIds).stream()
                .collect(Collectors.toMap(File::getId, Function.identity()));

        return new CommentRelationData(userMap, fileMap);
    }

    // 프로필 이미지 경로 설정 메서드
    private String setProfileImagePath(User user, CommentRelationData data) {
        String profileImagePath = DEFAULT_PROFILE_IMAGE_PATH;

        if (user.getProfileImageId() != null) {
            File file = data.getFileMap().get(user.getProfileImageId());
            if(file != null) {
                profileImagePath = file.getFilePath();
            }
        }

        return profileImagePath;
    }

}
