package kr.adapterz.edu_community.domain.post.dto.resposne;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostsResult {

    private List<PostResult> posts;

    public static PostsResult of(List<PostResult> posts) {
        return new PostsResult(posts);
    }
}
