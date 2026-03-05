package com.angela.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Component
public class TraceFilter implements Filter {

    private static final Logger log =
            LoggerFactory.getLogger(TraceFilter.class);

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // 如果 header 里已有 traceId，则沿用（分布式场景）
        String traceId = req.getHeader("traceId");
        if (traceId == null) {
            traceId = UUID.randomUUID().toString().replace("-", "");
        }

        MDC.put("traceId", traceId);
        MDC.put("uri", req.getRequestURI());
        MDC.put("method", req.getMethod());

        String userId = req.getParameter("userId");
        if (userId != null) {
            MDC.put("userId", userId);
        }

        long start = System.currentTimeMillis();

        try {
            chain.doFilter(request, response);
        } finally {
            long cost = System.currentTimeMillis() - start;

            MDC.put("status", String.valueOf(resp.getStatus()));
            MDC.put("cost", String.valueOf(cost));

            // 统一记录一条请求日志
            log.info("request finished");

            // 必须清理，线程池会复用线程
            MDC.clear();
        }
    }
}