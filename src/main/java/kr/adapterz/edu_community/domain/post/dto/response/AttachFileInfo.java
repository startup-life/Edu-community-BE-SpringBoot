package kr.adapterz.edu_community.domain.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AttachFileInfo {

    private Long fileId;
    private String fileUrl;

    public static AttachFileInfo of(
            Long fileId,
            String fileUrl
    ) {
        return new AttachFileInfo(
                fileId,
                fileUrl
        );
    }
}
