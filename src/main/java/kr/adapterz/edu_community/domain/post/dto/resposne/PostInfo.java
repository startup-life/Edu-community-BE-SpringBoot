package kr.adapterz.edu_community.domain.post.dto.internal;

import kr.adapterz.edu_community.domain.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PostDto {

    private Long postId;
    private String title;
    private String content;
    private int likeCount;
    private int commentCount;
    private int hits;
    private AuthorDto author;
    private LocalDateTime createdAt;

    public static PostDto from(Post post, String profileImagePath) {
        return new PostDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getLikeCount(),
                post.getCommentCount(),
                post.getHits(),
                AuthorDto.of(
                        post.getAuthor().getId(),
                        post.getAuthor().getNickname(),
                        profileImagePath
                ),
                post.getCreatedAt()
        );
    }
}