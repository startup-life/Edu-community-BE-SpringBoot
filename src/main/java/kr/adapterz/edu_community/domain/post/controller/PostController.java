package kr.adapterz.edu_community.domain.post.controller;

import kr.adapterz.edu_community.domain.post.dto.request.CreatePostRequest;
import kr.adapterz.edu_community.domain.post.dto.request.UpdatePostRequest;
import kr.adapterz.edu_community.domain.post.dto.response.PostResponse;
import kr.adapterz.edu_community.domain.post.dto.response.PostsResponse;
import kr.adapterz.edu_community.domain.post.service.PostService;
import kr.adapterz.edu_community.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 개사굴 목록 조회
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

    // 개사굴 단일 조회
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

    // 게시글 작성
    @PostMapping()
    public ApiResponse<Long> createPost(
            @AuthenticationPrincipal Long userId,
            @RequestBody CreatePostRequest createPostRequest
    ) {
        Long response = postService.createPost(userId, createPostRequest);

        return ApiResponse.of(
                HttpStatus.CREATED,
                "create_post_success",
                response
        );
    }

    // 게시글 수정
    @PutMapping("/{post_id}")
    public ApiResponse<Long> updatePost(
            @PathVariable("post_id") Long postId,
            @RequestBody UpdatePostRequest updatePostRequest
    ) {
        Long response = postService.updatePost(postId, updatePostRequest);

        return ApiResponse.of(
                HttpStatus.OK,
                "update_post_success",
                response
        );
    }
}
