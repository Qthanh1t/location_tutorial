package com.ord.core.logging.audit_log.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic auditLogTopic() {
        return TopicBuilder.name("audit-logs")
                .partitions(3) // Tùy chỉnh số partition
                .replicas(1)  // Tùy chỉnh số replica
                .build();
    }
}
