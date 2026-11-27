package com.spd.framework.web.filter;

import java.io.IOException;
import java.util.UUID;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * 请求耗时日志：生产备份可按 requestId / SLOW_REQUEST / TIMEOUT_RISK 检索超时。
 * WARN ≥ warnMs（默认 3s），ERROR ≥ errorMs（默认 5s）。
 */
@Component
public class RequestCostFilter implements Filter, Ordered
{
    private static final Logger log = LoggerFactory.getLogger(RequestCostFilter.class);
    public static final String MDC_REQUEST_ID = "requestId";

    @Value("${spd.request.slow-warn-ms:3000}")
    private long warnMs;

    @Value("${spd.request.slow-error-ms:5000}")
    private long errorMs;

    @Override
    public void init(FilterConfig filterConfig)
    {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException
    {
        HttpServletRequest http = request instanceof HttpServletRequest ? (HttpServletRequest) request : null;
        String requestId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        MDC.put(MDC_REQUEST_ID, requestId);
        long t0 = System.currentTimeMillis();
        try
        {
            chain.doFilter(request, response);
        }
        finally
        {
            long cost = System.currentTimeMillis() - t0;
            if (http != null)
            {
                String uri = http.getRequestURI();
                String method = http.getMethod();
                String qs = http.getQueryString();
                if (cost >= errorMs)
                {
                    log.error("SLOW_REQUEST TIMEOUT_RISK requestId={} costMs={} {} {}{}{}",
                        requestId, cost, method, uri,
                        qs == null || qs.isEmpty() ? "" : "?",
                        qs == null ? "" : qs);
                }
                else if (cost >= warnMs)
                {
                    log.warn("SLOW_REQUEST requestId={} costMs={} {} {}{}{}",
                        requestId, cost, method, uri,
                        qs == null || qs.isEmpty() ? "" : "?",
                        qs == null ? "" : qs);
                }
            }
            MDC.remove(MDC_REQUEST_ID);
        }
    }

    @Override
    public void destroy()
    {
    }

    @Override
    public int getOrder()
    {
        return Ordered.HIGHEST_PRECEDENCE + 20;
    }
}
