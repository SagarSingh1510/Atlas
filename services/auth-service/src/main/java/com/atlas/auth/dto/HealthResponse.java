package com.atlas.auth.dto;

import com.atlas.auth.model.HealthStatus;

public record HealthResponse (HealthStatus status,String service){
    
}
