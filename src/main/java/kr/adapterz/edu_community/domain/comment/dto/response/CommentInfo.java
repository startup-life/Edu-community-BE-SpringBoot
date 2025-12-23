package kr.adapterz.edu_community.domain.comment.dto.response;

import kr.adapterz.edu_community.domain.comment.entity.Comment;
import kr.adapterz.edu_community.domain.post.dto.response.AuthorInfo;
import kr.adapterz.edu_community.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CommentInfo {

    private Long commentId;
    private String content;
    private AuthorInfo author;
    private LocalDateTime createdAt;

    public static CommentInfo from(Comment comment, User user, String profileImagePath) {
        return new CommentInfo(
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
