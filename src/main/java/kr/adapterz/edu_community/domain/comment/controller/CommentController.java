package kr.adapterz.edu_community.domain.comment.controller;

import kr.adapterz.edu_community.domain.comment.dto.response.CommentsResponse;
import kr.adapterz.edu_community.domain.comment.service.CommentService;
import kr.adapterz.edu_community.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
