package kr.adapterz.edu_community.domain.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostsResponse {

    private List<PostInfo> posts;  // 직접 리스트
    private PageInfo page;

    public static PostsResponse of(List<PostInfo> posts, PageInfo page) {
        return new PostsResponse(posts, page);
    }
}
