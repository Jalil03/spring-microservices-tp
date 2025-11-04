package com.example.productcomposite.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Objects;

@Component
public class RequestMetrics implements HandlerInterceptor {

    private final Counter getRequests;
    private final Counter postPutRequests;

    public RequestMetrics(MeterRegistry registry) {
        this.getRequests = Counter.builder("composite.requests.get.count")
                .description("Nombre de requêtes GET reçues par le service composite")
                .register(registry);

        this.postPutRequests = Counter.builder("composite.requests.postput.count")
                .description("Nombre de requêtes POST ou PUT reçues par le service composite")
                .register(registry);
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             jakarta.servlet.http.HttpServletResponse response,
                             Object handler) throws Exception {
        String method = request.getMethod();

        if (Objects.equals(method, "GET")) {
            getRequests.increment();
        } else if (Objects.equals(method, "POST") || Objects.equals(method, "PUT")) {
            postPutRequests.increment();
        }

        return true;
    }
}
