package com.atlas.feedservice.kafka.streams;

import com.atlas.feedservice.feign.ConnectionServiceClient;
import com.atlas.feedservice.feign.ConnectionUserDto;
import com.atlas.feedservice.kafka.FeedEntryEvent;
import com.atlas.feedservice.kafka.PostEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class FeedStreamsTopology {

    private final ConnectionServiceClient connectionServiceClient;

    @Bean
    public KStream<String, PostEvent> kStream(StreamsBuilder kStreamBuilder) {
        JsonSerde<PostEvent> postEventSerde = new JsonSerde<>(PostEvent.class);
        JsonSerde<FeedEntryEvent> feedEntrySerde = new JsonSerde<>(FeedEntryEvent.class);

        KStream<String, PostEvent> sourceStream = kStreamBuilder.stream(
                "post-events",
                Consumed.with(Serdes.String(), postEventSerde)
        );

        sourceStream
                .filter((key, value) -> value != null && "POST_CREATED".equals(value.getEventType()))
                .filter((key, value) -> "PUBLIC".equals(value.getVisibility()) || "CONNECTIONS".equals(value.getVisibility()))
                .flatMap((key, value) -> {
                    log.info("Processing post fan-out in stream topology. Post ID: {}, Author: {}", value.getPostId(), value.getAuthorId());
                    List<ConnectionUserDto> connections;
                    try {
                        connections = connectionServiceClient.getConnections(value.getAuthorId().toString());
                    } catch (Exception e) {
                        log.error("Failed to fetch connections for author {}: {}", value.getAuthorId(), e.getMessage());
                        connections = Collections.emptyList();
                    }

                    List<org.apache.kafka.streams.KeyValue<String, FeedEntryEvent>> result = new ArrayList<>();
                    
                    // Self-feed: Author gets their own post in feed
                    double selfScore = calculateFeedScore(value, 0.0);
                    result.add(new org.apache.kafka.streams.KeyValue<>(
                            value.getAuthorId().toString(),
                            FeedEntryEvent.builder()
                                    .userId(value.getAuthorId().toString())
                                    .postId(value.getPostId().toString())
                                    .score(selfScore)
                                    .build()
                    ));

                    // Fan-out: Deliver to all 1st-degree connections
                    for (ConnectionUserDto conn : connections) {
                        double score = calculateFeedScore(value, 1.0);
                        result.add(new org.apache.kafka.streams.KeyValue<>(
                                conn.getUserId(),
                                FeedEntryEvent.builder()
                                        .userId(conn.getUserId())
                                        .postId(value.getPostId().toString())
                                        .score(score)
                                        .build()
                        ));
                    }
                    return result;
                })
                .to("feed-fanout", Produced.with(Serdes.String(), feedEntrySerde));

        return sourceStream;
    }

    private double calculateFeedScore(PostEvent post, double connectionStrength) {
        double recencyWeight = 0.5;
        double connectionWeight = 0.5;
        double recencyScore = 1.0; 
        return (recencyWeight * recencyScore) + (connectionWeight * connectionStrength);
    }
}
