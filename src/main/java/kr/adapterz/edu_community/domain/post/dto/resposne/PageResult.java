package kr.adapterz.edu_community.domain.post.dto.resposne;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@AllArgsConstructor
public class PageResult {

    private int page;
    private int size;
    private long totalElements;
    private long totalPages;
    private boolean hasNext;

    public static PageResult from(Page<?> page) {
        return new PageResult(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext()
        );
    }
}
