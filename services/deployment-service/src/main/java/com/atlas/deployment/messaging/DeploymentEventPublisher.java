package com.atlas.deployment.messaging;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.atlas.events.AtlasTopics;
import com.atlas.events.DeploymentRequestedEvent;

@Component
public class DeploymentEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public DeploymentEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publish(DeploymentRequestedEvent event) {
        kafkaTemplate.send(AtlasTopics.DEPLOYMENT_REQUESTED, event.deploymentId().toString(), event);
    }
}
