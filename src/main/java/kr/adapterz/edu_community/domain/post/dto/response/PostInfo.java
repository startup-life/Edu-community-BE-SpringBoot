package kr.adapterz.edu_community.domain.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PostInfo {

    private Long postId;
    private String title;
    private String content;
    private int likeCount;
    private int commentCount;
    private int hits;
    private AuthorInfo author;
    private LocalDateTime createdAt;

    public static PostInfo of(
            Long postId,
            String title,
            String content,
            int likeCount,
            int commentCount,
            int hits,
            AuthorInfo author,
            LocalDateTime createdAt
    ) {
        return new PostInfo(
                postId, title, content,
                likeCount, commentCount, hits,
                author, createdAt
        );
    }
}