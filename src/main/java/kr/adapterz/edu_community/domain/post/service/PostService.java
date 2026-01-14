package kr.adapterz.edu_community.domain.post.service;

import kr.adapterz.edu_community.domain.file.entity.File;
import kr.adapterz.edu_community.domain.file.repository.FileRepository;
import kr.adapterz.edu_community.domain.post.dto.internal.PostRelationData;
import kr.adapterz.edu_community.domain.post.dto.request.CreatePostRequest;
import kr.adapterz.edu_community.domain.post.dto.request.UpdatePostRequest;
import kr.adapterz.edu_community.domain.post.dto.response.*;
import kr.adapterz.edu_community.domain.post.entity.Post;
import kr.adapterz.edu_community.domain.post.repository.PostQueryRepository;
import kr.adapterz.edu_community.domain.post.repository.PostRepository;
import kr.adapterz.edu_community.domain.user.entity.User;
import kr.adapterz.edu_community.domain.user.repository.UserRepository;
import kr.adapterz.edu_community.global.exception.AccessDeniedException;
import kr.adapterz.edu_community.global.exception.NotFoundException;
import kr.adapterz.edu_community.global.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    // 게시글 작성
    public Long createPost(Long authorId, CreatePostRequest createPostRequest) {
        User author = userRepository.findActiveById(authorId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        File attachFile = resolveAttachFile(createPostRequest.getAttachFileUrl());
        System.out.println("================= attachFile: " + attachFile);
        Post post = Post.create(
                createPostRequest.getTitle(),
                createPostRequest.getContent(),
                author,
                attachFile
        );

        postRepository.save(post);

        // 무거운 변환 로직 제거하고 ID만 반환
        return post.getId();
    }

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
    public PostResponse getPost(Long postId) {
        // 게시글 조회
        Post post = postQueryRepository.findByIdWithAuthor(postId)
                .orElseThrow(() -> new NotFoundException("POST_NOT_FOUND"));

        // 작성자 조회
        User user = post.getAuthor();

        // 작성자 프로필 이미지 조회 (null이면 프론트엔드에서 기본 이미지 사용)
        String profileImageUrl = Optional.ofNullable(user.getProfileImage())
                .map(File::getFilePath)
                .orElse(null);

        // 첨부 파일 조회
        File attachFile = post.getAttachFile();

        return PostResponse.of(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getLikeCount(),
                post.getCommentCount(),
                post.getHits(),
                AuthorInfo.of(
                        user.getId(),
                        user.getNickname(),
                        profileImageUrl
                ),
                attachFile != null ? AttachFileInfo.of(attachFile.getId(), attachFile.getFilePath()) : null,
                post.getCreatedAt()
        );
    }

    // 게시글 수정
    public void updatePost(Long postId, Long userId, UpdatePostRequest updatePostRequest) {
        Post post = postRepository.findActiveById(postId)
                .orElseThrow(() -> new NotFoundException("POST_NOT_FOUND"));

        if (!post.getAuthor().getId().equals(userId)) {
            throw new AccessDeniedException("FORBIDDEN");
        }

        File attachFile = resolveAttachFile(updatePostRequest.getAttachFileUrl());

        post.update(
                updatePostRequest.getTitle(),
                updatePostRequest.getContent(),
                attachFile
        );
    }

    // 게시글 삭제
    public void deletePost(Long postId) {
        Post post = postRepository.findActiveById(postId)
                .orElseThrow(() -> new NotFoundException("post_not_found"));
        post.delete();
    }

    // 게시글 조회수 증가
    public void increasePostViews(Long postId) {
        Post post = postRepository.findActiveById(postId)
                .orElseThrow(() -> new NotFoundException("post_not_found"));
        post.increaseHits();
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

        return new PostRelationData(userMap);
    }

    // Post와 관련된 데이터를 조합하여 PostInfo로 변환하는 메서드
    private PostInfo toPostInfo(Post post, PostRelationData data) {
        User user = data.getUserMap().get(post.getAuthor().getId());

        if (user == null) {
            throw new NotFoundException("user_not_found for id: " + post.getAuthor().getId());
        }

        // 프로필 이미지 (null이면 프론트엔드에서 기본 이미지 사용)
        String profileImageUrl = Optional.ofNullable(user.getProfileImage())
                .map(File::getFilePath)
                .orElse(null);

        return PostInfo.of(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getLikeCount(),
                post.getCommentCount(),
                post.getHits(),
                AuthorInfo.of(
                        user.getId(),
                        user.getNickname(),
                        profileImageUrl
                ),
                post.getCreatedAt()
        );
    }

    private File resolveAttachFile(String attachFilePath) {
        // FileUtil 적용: URL이 들어와도 Path만 추출
        String path = FileUtil.extractPathFromUrl(attachFilePath);

        if (path == null || path.isBlank()) {
            return null;
        }

        return fileRepository.findByFilePath(path)
                .orElseThrow(() -> new NotFoundException("ATTACH_FILE_NOT_FOUND")); // 오타 수정 및 명칭 변경
    }

}
