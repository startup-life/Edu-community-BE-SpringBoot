package kr.adapterz.edu_community.domain.post.service;

import kr.adapterz.edu_community.domain.file.entity.File;
import kr.adapterz.edu_community.domain.file.repository.FileRepository;
import kr.adapterz.edu_community.domain.post.dto.internal.PostRelationData;
import kr.adapterz.edu_community.domain.post.dto.response.PageInfo;
import kr.adapterz.edu_community.domain.post.dto.response.PostInfo;
import kr.adapterz.edu_community.domain.post.dto.response.PostResponse;
import kr.adapterz.edu_community.domain.post.dto.response.PostsResponse;
import kr.adapterz.edu_community.domain.post.entity.Post;
import kr.adapterz.edu_community.domain.post.repository.PostQueryRepository;
import kr.adapterz.edu_community.domain.post.repository.PostRepository;
import kr.adapterz.edu_community.domain.user.entity.User;
import kr.adapterz.edu_community.domain.user.repository.UserRepository;
import kr.adapterz.edu_community.global.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostQueryRepository postQueryRepository;
    private final UserRepository userRepository;
    private final FileRepository fileRepository;

    private final String DEFAULT_PROFILE_IMAGE_PATH = "/public/images/profile/default.jpg";

    // 개사굴 목록 조회
    @Transactional(readOnly = true)
    public PostsResponse getPosts(int page, int size, String sortBy, String direction) {
        Pageable pageable = createPageable(page, size, sortBy, direction);

        Page<Post> postsPage = postRepository.findPage(pageable);
        List<Post> posts = postsPage.getContent();

        if (posts.isEmpty()) {
            return PostsResponse.of(List.of(), PageInfo.from(postsPage));
        }

        PostRelationData relationData = loadPostRelationData(posts);

        List<PostInfo> postResults = posts.stream()
                .map(post -> toPostInfo(post, relationData))
                .toList();

        return PostsResponse.of(postResults, PageInfo.from(postsPage));
    }

    // 게시글 상세 조회
    @Transactional(readOnly = true)
    public PostResponse getPost(Long postId) {
        // 게시글 조회
        Post post = postQueryRepository.findByIdWithAuthor(postId)
                .orElseThrow(() -> new NotFoundException("post_not_found"));

        // 작성자 조회
        User user = post.getAuthor();

        // 프로필 이미지 경로 조회
        String profileImagePath = DEFAULT_PROFILE_IMAGE_PATH;
        if (user.getProfileImageId() != null) {
            profileImagePath = fileRepository.findById(user.getProfileImageId())
                    .map(File::getFilePath)
                    .orElse(DEFAULT_PROFILE_IMAGE_PATH);
        }

        // 첨부 파일 조회
        File attachFile = null;
        if (post.getAttachFileId() != null) {
            attachFile = fileRepository.findById(post.getAttachFileId())
                    .orElse(null);
        }

        return PostResponse.from(post, user, profileImagePath, attachFile);
    }

    // ================================= 내부 메서드 =================================//

    // Pageable 생성 메서드
    private Pageable createPageable(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        return PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
    }

    // Post와 관련된 User, File 데이터를 한 번에 로드하는 메서드
    private PostRelationData loadPostRelationData(List<Post> posts) {
        List<Long> authorIds = posts.stream()
                .map(post -> post.getAuthor().getId())
                .distinct()
                .toList();

        Map<Long, User> userMap = userRepository.findAllActiveByIds(authorIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        List<Long> fileIds = userMap.values().stream()
                .map(User::getProfileImageId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<Long, File> fileMap = fileIds.isEmpty()
                ? Map.of()
                : fileRepository.findAllById(fileIds).stream()
                .collect(Collectors.toMap(File::getId, Function.identity()));

        return new PostRelationData(userMap, fileMap);
    }

    // Post와 관련된 데이터를 조합하여 PostInfo로 변환하는 메서드
    private PostInfo toPostInfo(Post post, PostRelationData data) {
        User user = data.getUserMap().get(post.getAuthor().getId());

        if (user == null) {
            throw new NotFoundException("user_not_found for id: " + post.getAuthor().getId());
        }

        String profileImagePath = setProfileImagePath(user, data);

        return PostInfo.from(post, user, profileImagePath);
    }

    // 프로필 이미지 경로 설정 메서드
    private String setProfileImagePath(User user, PostRelationData data) {
        String profileImagePath = DEFAULT_PROFILE_IMAGE_PATH;

        if (user.getProfileImageId() != null) {
            File file = data.getFileMap().get(user.getProfileImageId());
            if(file != null) {
                profileImagePath = file.getFilePath();
            }
        }

        return profileImagePath;
    }
}
