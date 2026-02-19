package com.atlas.auditlogservice.repository;

import com.atlas.auditlogservice.document.AuditLog;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends ElasticsearchRepository<AuditLog, String> {
}
