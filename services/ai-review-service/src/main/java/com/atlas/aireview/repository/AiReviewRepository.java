package com.atlas.aireview.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.atlas.aireview.entity.AiReview;

public interface AiReviewRepository extends JpaRepository<AiReview, Long> {
    List<AiReview> findAllByDeploymentId(Long deploymentId);
    Optional<AiReview> findByDeploymentId(Long deploymentId);
}
