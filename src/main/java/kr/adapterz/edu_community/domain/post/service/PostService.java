package kr.adapterz.edu_community.domain.post.service;

import kr.adapterz.edu_community.domain.post.entity.Post;
import kr.adapterz.edu_community.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public Page<Post> getPosts(int page, int size, String sortBy, String direction) {

        Sort sort = Sort.by(sortBy, direction);
        Pageable pageable = PageRequest.of(page, size, sort);
        return postRepository.findAllByDeletedAtIsNull(pageable);

    }
}
