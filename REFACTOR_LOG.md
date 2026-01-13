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

    String profileImageUrl = "/public/images/profile/default.jpg";
    if (user.getProfileImageId() != null) {
        profileImageUrl = fileRepository.findById(user.getProfileImageId())
                .map(File::getFilePath)
                .orElse(profileImageUrl);
    }

    return profileImageUrl;
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

    String profileImageUrl = setProfileImagePath(user, data);

    return PostInfo.from(post, user, profileImageUrl);
}

// 프로필 이미지 경로 설정 메서드
private String setProfileImagePath(User user, PostRelationData data) {
    String profileImageUrl = "/public/images/profile/default.jpg";

    if (user.getProfileImageId() != null) {
        File file = data.getFileMap().get(user.getProfileImageId());
        if(file != null) {
            profileImageUrl = file.getFilePath();
        }
    }

    return profileImageUrl;
}
```

### 해결 방법
- `Post`, `User`, `File` 엔티티 간의 연관된 데이터를 한 번에 로드하여 N+1 문제를 해결했습니다.
- `PostRelationData` 클래스를 사용하여 관련 데이터를 효율적으로 관리하고, 필요한 정보를 조합하여 `PostInfo` 객체를 생성합니다.
- 이로 인해 데이터베이스 쿼리 수가 크게 감소하여 성능이 향상되었습니다.

---

# DTO `from` / `of` 사용

## 1. 핵심 원칙 한 줄

**DTO는 엔티티를 모르게 하고, 값으로만 생성한다.**


## 2. `from` 과 `of`의 의미 정의

### `from`

* **의미**: 변환
* **정체성**: 이미 완성된 하나의 개념 → 다른 표현
* **전제**: 입력 자체가 하나의 의미 단위여야 함

### `of`

* **의미**: 조립
* **정체성**: 여러 값을 모아 새로운 객체 생성
* **전제**: 값들의 조합으로 의미가 만들어짐

## 3. DTO에서의 사용 규칙 (중요)

### ❌ DTO에서 금지

```java
PostInfo.from(Post post)
PostInfo.from(Post post, User user)
```

**이유**

* DTO가 엔티티에 의존
* N+1 구조적 허용
* 조회 전략이 DTO에 숨어버림
* QueryDSL / Projection 전환 시 발목 잡힘

### ⭕ DTO에서 권장

```java
PostInfo.of(
    postId,
    title,
    content,
    authorInfo,
    createdAt
);
```

**이유**

* DTO는 값만 알면 됨
* 생성 책임이 서비스에 명확
* 조회 방식 변경에 영향 없음

## 4. `from`을 써도 되는 예외 조건

### 조건

* 입력이 **엔티티가 아님**
* 이미 가공된 **하나의 개념**

### 허용 예시

```java
AuthorInfo.from(
    userId,
    nickname,
    profileImageUrl
);
```

또는

```java
AuthorInfo.from(AuthorProjection projection);
```

## 5. 서비스 계층과 DTO의 역할 분리

### Service

* 조회 전략 결정
* 쿼리 횟수 관리
* 값 추출
* DTO 조립 오케스트레이션

### DTO

* 응답 구조 정의
* 값 보관
* 표현 책임만 가짐

## 6. 실무 판단 체크리스트

아래 중 하나라도 해당하면 `from(Entity)` 사용 금지다.

* DTO가 엔티티를 import 하고 있다
* DTO 생성 시 DB 조회 위치가 애매해진다
* 팀원이 실수로 루프 안에서 조회할 수 있다
* QueryDSL DTO 프로젝션을 고려 중이다

---

## 7. 네이밍 결정 공식 (외워도 됨)

```
입력이 하나의 “개념”이다 → from
입력이 여러 “값”이다     → of
애매하다                 → of
```

---

## 8. 최종 가이드 문장

**DTO에서는 `of`를 기본값으로 사용하고,
`from`은 “엔티티가 아닌 완성된 개념 변환”에만 제한적으로 허용한다.**
