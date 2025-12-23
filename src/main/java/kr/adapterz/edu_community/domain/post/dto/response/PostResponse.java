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
    private AuthorInfo author;
    private AttachFileInfo file;
    private LocalDateTime createdAt;

    /*public static PostResponse from(
            Post post,
            User user,
            String profileImagePath,
            File file
    ) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getLikeCount(),
                post.getCommentCount(),
                post.getHits(),
                AuthorInfo.of(
                        user.getId(),
                        user.getNickname(),
                        profileImagePath
                ),
                file != null ? AttachFileInfo.from(file) : null,
                post.getCreatedAt()
        );
    }*/
    public static PostResponse of(
            Long postId,
            String title,
            String content,
            int likeCount,
            int commentCount,
            int hits,
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
                author,
                file,
                createdAt
        );
    }
}
