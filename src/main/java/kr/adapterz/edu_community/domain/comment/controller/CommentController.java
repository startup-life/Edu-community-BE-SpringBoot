package kr.adapterz.edu_community.domain.comment.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import kr.adapterz.edu_community.domain.comment.dto.request.CommentRequest;
import kr.adapterz.edu_community.domain.comment.dto.response.CommentsResponse;
import kr.adapterz.edu_community.domain.comment.service.CommentService;
import kr.adapterz.edu_community.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/posts/{postId}/comments")
@RequiredArgsConstructor
@Validated
public class CommentController {

    private final CommentService commentService;

    // 댓글 작성
    @PostMapping()
    public ResponseEntity<ApiResponse<Void>> createComment(
            @PathVariable("postId")
            @Positive(message = "INVALID_FORMAT")
            Long postId,
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody CommentRequest createCommentRequest
    ) {
        commentService.createComment(
                postId,
                userId,
                createCommentRequest.getContent()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.of("COMMENT_CREATED", null));
    }

    // 특정 게시글의 댓글 조회
    @GetMapping()
    public ResponseEntity<ApiResponse<CommentsResponse>> getComments(
            @PathVariable("postId") Long postId
    ) {
        CommentsResponse response = commentService.getComments(postId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("COMMENTS_RETRIEVED", response));
    }

    // 댓글 수정
    @PutMapping({"/{commentId}"})
    public ResponseEntity<ApiResponse<Void>> updateComment(
            @PathVariable("postId")
            @Positive(message = "INVALID_FORMAT")
            Long postId,
            @PathVariable("commentId")
            @Positive(message = "INVALID_FORMAT")
            Long commentId,
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody CommentRequest updateCommentRequest
    ) {
        commentService.updateComment(
                postId,
                commentId,
                userId,
                updateCommentRequest.getContent()
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("COMMENT_UPDATED", null));
    }

    // 댓글 삭제
    @DeleteMapping({"/{commentId}"})
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable("postId")
            @Positive(message = "INVALID_FORMAT")
            Long postId,
            @PathVariable("commentId")
            @Positive(message = "INVALID_FORMAT")
            Long commentId,
            @AuthenticationPrincipal Long userId
    ) {
        commentService.deleteComment(
                postId,
                commentId,
                userId
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("COMMENT_DELETED", null));
    }
}
