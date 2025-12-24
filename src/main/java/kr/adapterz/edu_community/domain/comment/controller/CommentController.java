package kr.adapterz.edu_community.domain.comment.controller;

import kr.adapterz.edu_community.domain.comment.dto.request.CommentRequest;
import kr.adapterz.edu_community.domain.comment.dto.response.CommentsResponse;
import kr.adapterz.edu_community.domain.comment.service.CommentService;
import kr.adapterz.edu_community.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts/{post_id}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 특정 게시글의 댓글 조회
    @GetMapping()
    public ApiResponse<CommentsResponse> getComments(
            @PathVariable("post_id") Long postId
    ) {
        CommentsResponse response = commentService.getComments(postId);

        return ApiResponse.ok(
                "get_comments_success",
                response
        );
    }

    // 댓글 작성
    @PostMapping()
    public ApiResponse<Long> createComment(
            @PathVariable("post_id") Long postId,
            @AuthenticationPrincipal Long userId,
            @RequestBody CommentRequest createCommentRequest
    ) {
        Long response = commentService.createComment(
                postId,
                userId,
                createCommentRequest.getContent()
        );

        return ApiResponse.of(
                HttpStatus.CREATED,
                "create_comment_success",
                response
        );
    }

    // 댓글 수정
    @PutMapping({"/{comment_id}"})
    public ApiResponse<Void> updateComment(
            @PathVariable("post_id") Long postId,
            @PathVariable("comment_id") Long commentId,
            @AuthenticationPrincipal Long userId,
            @RequestBody CommentRequest updateCommentRequest
    ) {
        commentService.updateComment(
                postId,
                commentId,
                userId,
                updateCommentRequest.getContent()
        );

        return ApiResponse.ok(
                "update_comment_success",
                null
        );
    }

    // 댓글 삭제
    @DeleteMapping({"/{comment_id}"})
    public ApiResponse<Void> deleteComment(
            @PathVariable("post_id") Long postId,
            @PathVariable("comment_id") Long commentId
    ) {
        commentService.deleteComment(
                postId,
                commentId
        );

        return ApiResponse.ok(
                "delete_comment_success",
                null
        );
    }
}
