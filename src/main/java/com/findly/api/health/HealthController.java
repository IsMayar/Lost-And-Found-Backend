package com.findly.api.health;

import com.findly.api.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/api/v1/health")
    public ApiResponse<Map<String, Object>> health(HttpServletRequest request) {
        return ApiResponse.success(
                "Findly API is running",
                Map.of(
                        "service", "findly-api",
                        "status", "UP"
                ),
                request.getRequestURI()
        );
    }
}
