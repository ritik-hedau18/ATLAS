package com.atlas.searchservice.controller;

import com.atlas.searchservice.document.JobDoc;
import com.atlas.searchservice.document.PostDoc;
import com.atlas.searchservice.document.UserDoc;
import com.atlas.searchservice.repository.JobDocRepository;
import com.atlas.searchservice.repository.PostDocRepository;
import com.atlas.searchservice.repository.UserDocRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@Slf4j
public class SearchController {

    private final UserDocRepository userDocRepository;
    private final PostDocRepository postDocRepository;
    private final JobDocRepository jobDocRepository;

    @GetMapping
    public ResponseEntity<?> search(
            @RequestParam String q,
            @RequestParam String type) {
        log.info("Full-text search query: '{}', type: '{}'", q, type);
        if ("PEOPLE".equalsIgnoreCase(type)) {
            return ResponseEntity.ok(userDocRepository.findByFullNameContainingIgnoreCaseOrHeadlineContainingIgnoreCase(q, q));
        } else if ("POSTS".equalsIgnoreCase(type)) {
            return ResponseEntity.ok(postDocRepository.findByContentContainingIgnoreCase(q));
        } else if ("JOBS".equalsIgnoreCase(type)) {
            return ResponseEntity.ok(jobDocRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(q, q));
        }
        return ResponseEntity.badRequest().body("Invalid search type. Use: PEOPLE | POSTS | JOBS");
    }

    @GetMapping("/suggest")
    public ResponseEntity<List<String>> suggest(@RequestParam String q) {
        log.info("Prefix suggestion query: '{}'", q);
        List<UserDoc> users = userDocRepository.findByFullNameContainingIgnoreCaseOrHeadlineContainingIgnoreCase(q, q);
        List<String> suggestions = users.stream()
                .map(UserDoc::getFullName)
                .distinct()
                .limit(5)
                .collect(Collectors.toList());

        return ResponseEntity.ok(suggestions);
    }
}
