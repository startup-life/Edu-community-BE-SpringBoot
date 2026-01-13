package kr.adapterz.edu_community.domain.post.controller;

import kr.adapterz.edu_community.domain.post.dto.request.CreatePostRequest;
import kr.adapterz.edu_community.domain.post.dto.request.UpdatePostRequest;
import kr.adapterz.edu_community.domain.post.dto.response.PostResponse;
import kr.adapterz.edu_community.domain.post.dto.response.PostsResponse;
import kr.adapterz.edu_community.domain.post.service.PostService;
import kr.adapterz.edu_community.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 개사굴 목록 조회
    @GetMapping()
    public ResponseEntity<ApiResponse<PostsResponse>> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        PostsResponse response = postService.getPosts(page, size, sortBy, direction);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("POSTS_RETRIEVED", response));
    }

    // 개사굴 단일 조회
    @GetMapping("/{post_id}")
    public ResponseEntity<ApiResponse<PostResponse>> getPost(
            @PathVariable("post_id") Long postId
    ) {
        PostResponse response = postService.getPost(postId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("POST_RETRIEVED", response));
    }

    // 게시글 작성
    @PostMapping()
    public ResponseEntity<ApiResponse<Long>> createPost(
            @AuthenticationPrincipal Long userId,
            @RequestBody CreatePostRequest createPostRequest
    ) {
        Long response = postService.createPost(userId, createPostRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.of("POST_CREATED", response));
    }

    // 게시글 수정
    @PutMapping("/{post_id}")
    public ResponseEntity<ApiResponse<Long>> updatePost(
            @PathVariable("post_id") Long postId,
            @RequestBody UpdatePostRequest updatePostRequest
    ) {
        Long response = postService.updatePost(postId, updatePostRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("POST_UPDATED", response));
    }

    // 게시글 삭제
    @DeleteMapping("/{post_id}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
        @PathVariable("post_id")  Long postId
    ) {
        postService.deletePost(postId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("POST_DELETED", null));
    }

    // 게시글 조회수 증가
    @PostMapping("/{post_id}/views")
    public ResponseEntity<ApiResponse<Void>> increasePostViews(
            @PathVariable("post_id") Long postId
    ) {
        postService.increasePostViews(postId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("POST_VIEW_INCREASED", null));
    }
}
