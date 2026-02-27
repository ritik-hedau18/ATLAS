package com.atlas.searchservice.repository;

import com.atlas.searchservice.document.JobDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobDocRepository extends ElasticsearchRepository<JobDoc, String> {
    List<JobDoc> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);
}
