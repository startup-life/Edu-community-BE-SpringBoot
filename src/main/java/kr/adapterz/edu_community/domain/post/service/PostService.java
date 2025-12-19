package kr.adapterz.edu_community.domain.post.service;

import kr.adapterz.edu_community.domain.post.dto.resposne.PostsResponse;
import kr.adapterz.edu_community.domain.post.dto.resposne.PageInfo;
import kr.adapterz.edu_community.domain.post.dto.resposne.PostInfo;
import kr.adapterz.edu_community.domain.post.entity.Post;
import kr.adapterz.edu_community.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public PostsResponse getPosts(int page, int size, String sortBy, String direction) {

        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<Post> postsPage = postRepository.findAllByDeletedAtIsNull(pageable);

        List<PostInfo> postResults = postsPage.getContent().stream()
                .map(post -> {
                    String profileImagePath = "/public/images/profile/default.jpg";
                    return PostInfo.from(post, profileImagePath);
                })
                .toList();

        return PostsResponse.of(
                postResults,
                PageInfo.from(postsPage)
        );

    }
}
