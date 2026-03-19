package tt.heixiong.awesome.common;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ApiResponse<T> {

    private final String code;
    private final String message;
    private final T data;
    private final String traceId;
    private final LocalDateTime timestamp;

    private ApiResponse(String code, String message, T data, String traceId, LocalDateTime timestamp) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.traceId = traceId;
        this.timestamp = timestamp;
    }

    public static <T> ApiResponse<T> success(T data, String traceId) {
        return new ApiResponse<T>("SUCCESS", "OK", data, traceId, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> failure(String code, String message, T data, String traceId) {
        return new ApiResponse<T>(code, message, data, traceId, LocalDateTime.now());
    }
}
