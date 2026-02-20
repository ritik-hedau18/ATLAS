package com.atlas.connectionservice.entity;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Node("User")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserNode {
    @Id
    private String userId; // Store UUID as string
    private String username;
    private String headline;

    @Relationship(type = "CONNECTED_TO", direction = Relationship.Direction.OUTGOING)
    @Builder.Default
    private Set<UserNode> connections = new HashSet<>();

    @Relationship(type = "FOLLOWS", direction = Relationship.Direction.OUTGOING)
    @Builder.Default
    private Set<UserNode> follows = new HashSet<>();
}
