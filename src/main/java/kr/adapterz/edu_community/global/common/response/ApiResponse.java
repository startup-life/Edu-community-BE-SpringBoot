package kr.adapterz.edu_community.global.common.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonPropertyOrder({"status", "message", "data"})
public class ApiResponse<T> {

    private final String code;
    private final T data;

    // 범용 생성 메서드
    public static <T> ApiResponse<T> of(String code, T data) {
        return new ApiResponse<>(code, data);
    }

    // 편의 메서드들
    public static <T> ApiResponse<T> ok(String code, T data) {
        return of(code, data);
    }
}