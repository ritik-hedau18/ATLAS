package com.atlas.searchservice.repository;

import com.atlas.searchservice.document.PostDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostDocRepository extends ElasticsearchRepository<PostDoc, String> {
    List<PostDoc> findByContentContainingIgnoreCase(String content);
}
