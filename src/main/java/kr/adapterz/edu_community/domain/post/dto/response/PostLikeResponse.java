package kr.adapterz.edu_community.domain.post.dto.response;

import kr.adapterz.edu_community.domain.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostLikeResponse {
    private int likeCount;

    public static PostLikeResponse from(Post post) {
        return new PostLikeResponse(post.getLikeCount());
    }

    public static PostLikeResponse of(int likeCount) {
        return new PostLikeResponse(likeCount);
    }
}
