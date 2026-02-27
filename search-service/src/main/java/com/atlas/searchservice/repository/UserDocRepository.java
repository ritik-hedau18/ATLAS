package com.atlas.searchservice.repository;

import com.atlas.searchservice.document.UserDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDocRepository extends ElasticsearchRepository<UserDoc, String> {
    List<UserDoc> findByFullNameContainingIgnoreCaseOrHeadlineContainingIgnoreCase(String fullName, String headline);
}
