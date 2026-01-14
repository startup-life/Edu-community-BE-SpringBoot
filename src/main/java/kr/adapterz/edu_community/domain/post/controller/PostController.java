package kr.adapterz.edu_community.domain.post.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import kr.adapterz.edu_community.domain.post.dto.request.CreatePostRequest;
import kr.adapterz.edu_community.domain.post.dto.request.UpdatePostRequest;
import kr.adapterz.edu_community.domain.post.dto.response.PostIdResponse;
import kr.adapterz.edu_community.domain.post.dto.response.PostLikeResponse;
import kr.adapterz.edu_community.domain.post.dto.response.PostResponse;
import kr.adapterz.edu_community.domain.post.dto.response.PostsResponse;
import kr.adapterz.edu_community.domain.post.service.PostService;
import kr.adapterz.edu_community.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/posts")
@RequiredArgsConstructor
@Validated
public class PostController {

    private final PostService postService;

    // 게시글 작성
    @PostMapping
    public ResponseEntity<ApiResponse<PostIdResponse>> createPost(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody CreatePostRequest createPostRequest
    ) {
        Long createdPostId = postService.createPost(userId, createPostRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.of("POST_CREATED", PostIdResponse.from(createdPostId)));
    }

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
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponse>> getPost(
            @PathVariable("postId")
            @Positive(message = "INVALID_FORMAT")
            Long postId
    ) {
        PostResponse response = postService.getPost(postId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("POST_RETRIEVED", response));
    }

    // 게시글 수정
    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> updatePost(
            @PathVariable("postId")
            @Positive(message = "INVALID_FORMAT")
            Long postId,
            @AuthenticationPrincipal Long userId,
            @RequestBody UpdatePostRequest updatePostRequest
    ) {
        postService.updatePost(postId, userId, updatePostRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("POST_UPDATED", null));
    }

    // 게시글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
        @PathVariable("postId")
        @AuthenticationPrincipal Long userId,
        @Positive(message = "INVALID_FORMAT")
        Long postId
    ) {
        postService.deletePost(postId, userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("POST_DELETED", null));
    }

    // 게시글 조회수 증가
    @PostMapping("/{postId}/views")
    public ResponseEntity<ApiResponse<Void>> increasePostViews(
            @PathVariable("postId")
            @Positive(message = "INVALID_FORMAT")
            Long postId
    ) {
        postService.increasePostViews(postId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("POST_VIEW_INCREASED", null));
    }

    // 게시글 좋아요 증가
    @PostMapping("/{postId}/likes")
    public ResponseEntity<ApiResponse<PostLikeResponse>> increasePostLikes(
            @PathVariable("postId")
            @Positive(message = "INVALID_FORMAT")
            Long postId,
            @AuthenticationPrincipal Long userId
    ) {
        PostLikeResponse updatedLikeCount = postService.increasePostLikes(postId, userId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.of("POST_LIKE_CREATED", updatedLikeCount));
    }

    // 게시글 좋아요 감소
    @DeleteMapping("/{postId}/likes")
    public ResponseEntity<ApiResponse<PostLikeResponse>> decreasePostLikes(
        @PathVariable("postId")
        @Positive(message = "INVALID_FORMAT")
        Long postId,
        @AuthenticationPrincipal Long userId
    ) {
        PostLikeResponse updatedLikeCount = postService.decreasePostLikes(postId, userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("POST_LIKE_DELETED", updatedLikeCount));
    }
}
