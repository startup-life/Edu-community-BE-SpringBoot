package kr.adapterz.edu_community.domain.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AttachFileInfo {

    private Long fileId;
    private String path;

    public static AttachFileInfo of(
            Long fileId,
            String path
    ) {
        return new AttachFileInfo(
                fileId,
                path
        );
    }
}
