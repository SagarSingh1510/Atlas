package com.atlas.gateway;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class GatewayProxyController {
    private static final List<String> HOP_BY_HOP_HEADERS = List.of(
            "host",
            "connection",
            "content-length",
            "transfer-encoding"
    );

    private final RestTemplate restTemplate;
    private final GatewayProperties properties;

    public GatewayProxyController(RestTemplate restTemplate, GatewayProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    @RequestMapping("/api/v1/**")
    public ResponseEntity<byte[]> proxy(HttpServletRequest request, HttpEntity<byte[]> entity) {
        return forward(request, entity, targetBaseUrl(request.getRequestURI()));
    }

    private String targetBaseUrl(String path) {
        if (path.startsWith("/api/v1/auth/") || path.startsWith("/api/v1/users/")) {
            return properties.authServiceUrl();
        }
        if (path.matches("/api/v1/workspaces/\\d+/tasks.*") || path.startsWith("/api/v1/tasks/")) {
            return properties.taskServiceUrl();
        }
        if (path.startsWith("/api/v1/simulations/")
                || path.matches("/api/v1/deployments/\\d+/simulations.*")) {
            return properties.simulationServiceUrl();
        }
        if (path.startsWith("/api/v1/ai-reviews/")
                || path.matches("/api/v1/deployments/\\d+/ai-reviews.*")) {
            return properties.aiReviewServiceUrl();
        }
        if (path.startsWith("/api/v1/deployments/")
                || path.matches("/api/v1/diagrams/\\d+/deployments.*")) {
            return properties.deploymentServiceUrl();
        }
        if (path.matches("/api/v1/workspaces/\\d+/diagrams.*")
                || path.matches("/api/v1/diagrams/\\d+/deploy$")
                || path.startsWith("/api/v1/diagrams/")) {
            return properties.diagramServiceUrl();
        }
        return properties.workspaceServiceUrl();
    }

    private ResponseEntity<byte[]> forward(HttpServletRequest request, HttpEntity<byte[]> entity, String targetBaseUrl) {
        URI targetUri = UriComponentsBuilder.fromUriString(targetBaseUrl)
                .path(request.getRequestURI())
                .query(request.getQueryString())
                .build(true)
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        entity.getHeaders().forEach((name, values) -> {
            if (!HOP_BY_HOP_HEADERS.contains(name.toLowerCase())) {
                headers.put(name, values);
            }
        });

        try {
            return restTemplate.exchange(
                    targetUri,
                    HttpMethod.valueOf(request.getMethod()),
                    new HttpEntity<>(entity.getBody(), headers),
                    byte[].class
            );
        } catch (HttpStatusCodeException exception) {
            HttpHeaders responseHeaders = new HttpHeaders();
            exception.getResponseHeaders().forEach((name, values) -> {
                if (!HOP_BY_HOP_HEADERS.contains(name.toLowerCase())) {
                    responseHeaders.put(name, values);
                }
            });
            return new ResponseEntity<>(
                    exception.getResponseBodyAsByteArray(),
                    responseHeaders,
                    exception.getStatusCode()
            );
        }
    }
}
