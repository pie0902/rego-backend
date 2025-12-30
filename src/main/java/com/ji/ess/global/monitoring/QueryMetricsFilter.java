package com.ji.ess.global.monitoring;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.ttddyy.dsproxy.QueryCountHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.security.web.util.OnCommittedResponseWrapper;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class QueryMetricsFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(QueryMetricsFilter.class);

    private final Map<String, Integer> baseline;

    public QueryMetricsFilter(QueryMetricsProperties props) {
        this.baseline = props.baseline();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // reset counter for this request
        QueryCountHolder.clear();

        String path = request.getRequestURI();
        Integer base = (baseline != null) ? baseline.get(path) : null;

        HttpServletResponse wrappedResponse = new OnCommittedResponseWrapper(response) {
            @Override
            protected void onResponseCommitted() {
                var grandTotal = QueryCountHolder.getGrandTotal();
                long total = (grandTotal != null) ? grandTotal.getTotal() : 0L;
                long timeMs = (grandTotal != null) ? grandTotal.getTime() : 0L;
                double avgTimeMs = total > 0 ? (double) timeMs / total : 0.0;

                super.setHeader("X-Query-Count", String.valueOf(total));
                super.setHeader("X-Query-Time-Ms", String.valueOf(timeMs));
                super.setHeader("X-Query-Avg-Time-Ms", String.format("%.3f", avgTimeMs));
                if (base != null && base > 0) {
                    super.setHeader("X-Query-Baseline", String.valueOf(base));
                    double improvement = (1.0 - (double) total / base) * 100.0;
                    super.setHeader("X-Query-Improvement", String.format("%.1f%%", improvement));
                }
            }
        };

        try {
            filterChain.doFilter(request, wrappedResponse);
        } finally {
            var grandTotal = QueryCountHolder.getGrandTotal();
            long total = (grandTotal != null) ? grandTotal.getTotal() : 0L;

            if (base != null && base > 0) {
                //몇퍼센트 개선 됐는지
                double improvement = (1.0 - (double) total / base) * 100.0;
                log.info("Queries {} ({}% vs {}) | {} {}",
                        total,
                        String.format("%.1f", improvement),
                        base,
                        request.getMethod(),
                        path);
            } else {
                log.info("Queries {} | {} {}", total, request.getMethod(), path);
            }
        }
    }
}
