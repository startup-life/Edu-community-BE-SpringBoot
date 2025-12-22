package kr.adapterz.edu_community.domain.post.controller;

import kr.adapterz.edu_community.domain.post.dto.response.PostResponse;
import kr.adapterz.edu_community.domain.post.dto.response.PostsResponse;
import kr.adapterz.edu_community.domain.post.service.PostService;
import kr.adapterz.edu_community.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping()
    public ApiResponse<PostsResponse> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        PostsResponse response = postService.getPosts(page, size, sortBy, direction);

        return ApiResponse.of(
                HttpStatus.OK,
                "get_posts_success",
                response
        );
    }

    @GetMapping("/{post_id}")
    public ApiResponse<PostResponse> getPost(
            @PathVariable("post_id") Long postId
    ) {
        PostResponse response = postService.getPost(postId);

        return ApiResponse.of(
                HttpStatus.OK,
                "get_post_success",
                response
        );
    }
}
