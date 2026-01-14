package kr.adapterz.edu_community.domain.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PostResponse {

    private Long postId;
    private String title;
    private String content;
    private int likeCount;
    private int commentCount;
    private int hits;
    private boolean isLiked;
    private AuthorInfo author;
    private AttachFileInfo file;
    private LocalDateTime createdAt;

    public static PostResponse of(
            Long postId,
            String title,
            String content,
            int likeCount,
            int commentCount,
            int hits,
            boolean isLiked,
            AuthorInfo author,
            AttachFileInfo file,
            LocalDateTime createdAt
    ) {
        return new PostResponse(
                postId,
                title,
                content,
                likeCount,
                commentCount,
                hits,
                isLiked,
                author,
                file,
                createdAt
        );
    }
}
