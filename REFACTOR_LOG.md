# 게시글 조회 시 N+1 문제 해결

### N+1 문제 발생 코드

```java
public PostsResponse getPosts(int page, int size, String sortBy, String direction) {

    Sort.Direction sortDirection = Sort.Direction.fromString(direction);
    Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

    Page<Post> postsPage = postRepository.findAllByDeletedAtIsNull(pageable);

    List<PostInfo> postResults = postsPage.getContent().stream()
            .map(post -> {
                return PostInfo.from(post, getAuthorProfileImagePath(post.getAuthor().getId()));
            })
            .toList();

    return PostsResponse.of(
            postResults,
            PageInfo.from(postsPage)
    );

}

private String getAuthorProfileImagePath(Long authorId) {
    User user = userRepository.findById(authorId)
            .orElseThrow(() -> new NotFoundException("user not found"));

    String profileImagePath = "/public/images/profile/default.jpg";
    if (user.getProfileImageId() != null) {
        profileImagePath = fileRepository.findById(user.getProfileImageId())
                .map(File::getFilePath)
                .orElse(profileImagePath);
    }

    return profileImagePath;
}
```

### N+1 문제 해결 코드
```java
@Transactional(readOnly = true)
public PostsResponse getPosts(int page, int size, String sortBy, String direction) {
    Pageable pageable = createPageable(page, size, sortBy, direction);

    Page<Post> postsPage = postRepository.findAllByDeletedAtIsNull(pageable);
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

    Map<Long, User> userMap = userRepository.findAllByIdInAndDeletedAtIsNull(authorIds).stream()
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
    String profileImagePath = "/public/images/profile/default.jpg";

    if (user.getProfileImageId() != null) {
        File file = data.getFileMap().get(user.getProfileImageId());
        if(file != null) {
            profileImagePath = file.getFilePath();
        }
    }

    return profileImagePath;
}
```

### 해결 방법
- `Post`, `User`, `File` 엔티티 간의 연관된 데이터를 한 번에 로드하여 N+1 문제를 해결했습니다.
- `PostRelationData` 클래스를 사용하여 관련 데이터를 효율적으로 관리하고, 필요한 정보를 조합하여 `PostInfo` 객체를 생성합니다.
- 이로 인해 데이터베이스 쿼리 수가 크게 감소하여 성능이 향상되었습니다.