package com.ord.core.logging.audit_log.service;


import com.ord.core.logging.audit_log.AuditLogDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class AuditLogService {

    // Tên topic Kafka để gửi audit log
    private static final String KAFKA_TOPIC = "audit-logs";

    private final KafkaTemplate<String, AuditLogDto> kafkaTemplate;

    /**
     * Gửi một đối tượng AuditLogDto đến Kafka.
     * KafkaTemplate mặc định là bất đồng bộ (async).
     *
     * @param log Đối tượng AuditLogDto
     */
    public void send(AuditLogDto log) {
        try {
            // Gửi đi và không chờ kết quả
            kafkaTemplate.send(KAFKA_TOPIC, log);
        } catch (Exception e) {
            // Log lỗi nếu không gửi được, nhưng không ném exception
            // để tránh làm hỏng luồng request chính
            log.error("Lỗi khi gửi audit log đến Kafka: {}", e.getMessage());
        }
    }
}