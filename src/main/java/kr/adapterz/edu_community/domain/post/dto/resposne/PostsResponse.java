package kr.adapterz.edu_community.domain.post.dto.resposne;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetPostsResponse {

    private PostsResult posts;
    private PageInfo page;

    public static GetPostsResponse of(PostsResult posts, PageInfo page) {
        return new GetPostsResponse(posts, page);
    }
}
