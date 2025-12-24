package kr.adapterz.edu_community.domain.comment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CommentsResponse {

    private List<CommentInfo> comments;

    public static CommentsResponse of(
            List<CommentInfo> comments
    ) {
        return new CommentsResponse(
                comments
        );
    }
}
