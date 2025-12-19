package kr.adapterz.edu_community.domain.post.dto.resposne;

import kr.adapterz.edu_community.domain.post.entity.Post;
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

    public static PostInfo from(Post post, String profileImagePath) {
        return new PostInfo(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getLikeCount(),
                post.getCommentCount(),
                post.getHits(),
                AuthorInfo.of(
                        post.getAuthor().getId(),
                        post.getAuthor().getNickname(),
                        profileImagePath
                ),
                post.getCreatedAt()
        );
    }
}