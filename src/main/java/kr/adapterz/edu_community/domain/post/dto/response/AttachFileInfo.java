package kr.adapterz.edu_community.domain.post.dto.response;

import kr.adapterz.edu_community.domain.file.entity.File;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AttachFileInfo {

    private Long fileId;
    private String path;

    public static AttachFileInfo from(File file) {
        return new AttachFileInfo(
                file.getId(),
                file.getFilePath()
        );
    }
}
