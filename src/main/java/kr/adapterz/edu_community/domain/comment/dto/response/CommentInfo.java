package kr.adapterz.edu_community.domain.comment.dto.response;

import kr.adapterz.edu_community.domain.post.dto.response.AuthorInfo;
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

    public static CommentInfo of(
            Long commentId,
            String content,
            AuthorInfo author,
            LocalDateTime createdAt
    ) {
        return new CommentInfo(
                commentId,
                content,
                author,
                createdAt
        );
    }
}
