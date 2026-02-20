package com.atlas.connectionservice.repository;

import com.atlas.connectionservice.entity.UserNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserNodeRepository extends Neo4jRepository<UserNode, String> {

    Optional<UserNode> findByUserId(String userId);

    @Query("MATCH (u:User {userId: $userId})-[:CONNECTED_TO]-(c:User) RETURN c")
    List<UserNode> findFirstDegreeConnections(String userId);

    @Query("MATCH (u:User {userId: $userId})-[:CONNECTED_TO*2]-(suggestion:User) " +
           "WHERE NOT (u)-[:CONNECTED_TO]-(suggestion) AND suggestion.userId <> $userId " +
           "RETURN DISTINCT suggestion LIMIT 10")
    List<UserNode> findPeopleYouMayKnow(String userId);

    @Query("MATCH (u1:User {userId: $userId1})-[:CONNECTED_TO]-(mutual:User)-[:CONNECTED_TO]-(u2:User {userId: $userId2}) " +
           "RETURN mutual")
    List<UserNode> findMutualConnections(String userId1, String userId2);

    @Query("MATCH (u1:User {userId: $userId1}), (u2:User {userId: $userId2}) " +
           "MATCH p = shortestPath((u1)-[:CONNECTED_TO*]-(u2)) " +
           "RETURN [n in nodes(p) | n.userId]")
    List<String> findShortestPath(String userId1, String userId2);

    @Query("MATCH (u1:User {userId: $userId1}), (u2:User {userId: $userId2}) " +
           "CREATE (u1)-[:CONNECTED_TO]->(u2)")
    void createConnection(String userId1, String userId2);

    @Query("MATCH (u1:User {userId: $userId1})-[r:CONNECTED_TO]-(u2:User {userId: $userId2}) " +
           "DELETE r")
    void deleteConnection(String userId1, String userId2);

    @Query("MATCH (u1:User {userId: $userId1}), (u2:User {userId: $userId2}) " +
           "CREATE (u1)-[:FOLLOWS]->(u2)")
    void createFollow(String userId1, String userId2);

    @Query("MATCH (u1:User {userId: $userId1})-[r:FOLLOWS]->(u2:User {userId: $userId2}) " +
           "DELETE r")
    void deleteFollow(String userId1, String userId2);
}
