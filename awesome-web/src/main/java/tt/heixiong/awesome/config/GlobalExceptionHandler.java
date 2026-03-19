package tt.heixiong.awesome.config;

import feign.FeignException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import tt.heixiong.awesome.common.ApiResponse;
import tt.heixiong.awesome.exception.BusinessException;
import org.springframework.web.server.ResponseStatusException;
import tt.heixiong.awesome.dto.ApiErrorResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String TRACE_ID_HEADER = "X-Trace-Id";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex,
                                                                                                  HttpServletRequest request) {
        Map<String, Object> errors = new LinkedHashMap<String, Object>();
        errors.put("errors", ex.getBindingResult()
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        ApiErrorResponse response = new ApiErrorResponse();
        response.setMessage("Validation failed");
        response.setErrors(ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList()));
        return buildResponse(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", "Validation failed", errors, request);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleBusinessException(BusinessException ex,
                                                                                     HttpServletRequest request) {
        return buildResponse(ex.getHttpStatus(), ex.getCode(), ex.getMessage(), null, request);
    }

    @ExceptionHandler({MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleBadRequest(Exception ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage(), null, request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleDataIntegrityViolationException(DataIntegrityViolationException ex,
                                                                                                   HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, "DATA_INTEGRITY_VIOLATION", "Data integrity violation", null, request);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleFeignException(FeignException ex,
                                                                                  HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_GATEWAY, "REMOTE_CALL_FAILED", "Remote call failed", null, request);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleResponseStatusException(ResponseStatusException ex,
                                                                                           HttpServletRequest request) {
        return buildResponse(ex.getStatus(), "HTTP_STATUS_ERROR", ex.getReason(), null, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleException(Exception ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "Internal server error", null, request);
    }

    private ResponseEntity<ApiResponse<Map<String, Object>>> buildResponse(HttpStatus status,
                                                                           String code,
                                                                           String message,
                                                                           Map<String, Object> data,
                                                                           HttpServletRequest request) {
        return ResponseEntity.status(status)
                .body(ApiResponse.failure(code, message, appendRequestPath(data, request), getTraceId(request)));
    }

    private Map<String, Object> appendRequestPath(Map<String, Object> data, HttpServletRequest request) {
        Map<String, Object> body = data == null ? new LinkedHashMap<String, Object>() : new LinkedHashMap<String, Object>(data);
        body.put("path", request.getRequestURI());
        return body;
    }

    private String getTraceId(HttpServletRequest request) {
        String traceId = request.getHeader(TRACE_ID_HEADER);
        return traceId != null && traceId.trim().length() > 0 ? traceId : UUID.randomUUID().toString();
    }

    @ExceptionHandler(ResponseStatusException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse handleResponseStatusException(ResponseStatusException ex) {
        ApiErrorResponse response = new ApiErrorResponse();
        response.setMessage(ex.getReason());
        return response;
    }
}
