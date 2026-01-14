package kr.adapterz.edu_community.domain.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostIdResponse {
    private Long postId;

    // 편의상 static factory method 추가 (선택사항)
    public static PostIdResponse from(Long postId) {
        return new PostIdResponse(postId);
    }
}