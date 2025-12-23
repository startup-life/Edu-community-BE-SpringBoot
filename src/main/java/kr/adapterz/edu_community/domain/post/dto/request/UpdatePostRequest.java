package kr.adapterz.edu_community.domain.post.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdatePostRequest {

    private String title;
    private String content;
    private String attachFilePath;
}
