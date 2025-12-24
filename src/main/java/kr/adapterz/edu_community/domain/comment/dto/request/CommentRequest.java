package kr.adapterz.edu_community.domain.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentRequest {

    @NotBlank(message = "required")
    @Size(max = 1500, message = "invalid_length")
    private String content;
}
