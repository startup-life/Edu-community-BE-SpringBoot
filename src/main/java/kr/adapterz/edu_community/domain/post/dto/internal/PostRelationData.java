package kr.adapterz.edu_community.domain.post.dto.internal;

import kr.adapterz.edu_community.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class PostRelationData {
    private final Map<Long, User> userMap;
}
