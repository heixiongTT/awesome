package tt.heixiong.awesome.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ObservabilityFilter implements Filter {

    public static final String REQUEST_ID_HEADER = "X-Request-Id";
    public static final String REQUEST_ID_MDC_KEY = "requestId";

    private static final Logger ACCESS_LOG = LoggerFactory.getLogger("ACCESS_LOG");

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        StatusCaptureResponseWrapper httpResponse = new StatusCaptureResponseWrapper((HttpServletResponse) response);

        String requestId = resolveRequestId(httpRequest);
        long start = System.currentTimeMillis();

        MDC.put(REQUEST_ID_MDC_KEY, requestId);
        httpResponse.setHeader(REQUEST_ID_HEADER, requestId);

        try {
            chain.doFilter(request, httpResponse);
        } finally {
            long durationMs = System.currentTimeMillis() - start;
            ACCESS_LOG.info("requestId={} method={} path={} query={} status={} durationMs={} clientIp={} userAgent={}",
                    requestId,
                    httpRequest.getMethod(),
                    httpRequest.getRequestURI(),
                    normalize(httpRequest.getQueryString()),
                    httpResponse.getStatus(),
                    durationMs,
                    resolveClientIp(httpRequest),
                    normalize(httpRequest.getHeader("User-Agent")));
            MDC.remove(REQUEST_ID_MDC_KEY);
        }
    }

    private String resolveRequestId(HttpServletRequest request) {
        String requestId = request.getHeader(REQUEST_ID_HEADER);
        if (!StringUtils.hasText(requestId)) {
            return UUID.randomUUID().toString().replace("-", "");
        }
        return requestId.trim();
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(forwardedFor)) {
            return forwardedFor.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (StringUtils.hasText(realIp)) {
            return realIp.trim();
        }
        return request.getRemoteAddr();
    }

    private String normalize(String value) {
        return StringUtils.hasText(value) ? value : "-";
    }

    private static class StatusCaptureResponseWrapper extends HttpServletResponseWrapper {

        private int httpStatus = SC_OK;

        StatusCaptureResponseWrapper(HttpServletResponse response) {
            super(response);
        }

        @Override
        public void setStatus(int sc) {
            super.setStatus(sc);
            this.httpStatus = sc;
        }

        @Override
        public void sendError(int sc) throws IOException {
            super.sendError(sc);
            this.httpStatus = sc;
        }

        @Override
        public void sendError(int sc, String msg) throws IOException {
            super.sendError(sc, msg);
            this.httpStatus = sc;
        }

        @Override
        public void sendRedirect(String location) throws IOException {
            super.sendRedirect(location);
            this.httpStatus = SC_FOUND;
        }

        @Override
        public int getStatus() {
            return this.httpStatus;
        }
    }
}
