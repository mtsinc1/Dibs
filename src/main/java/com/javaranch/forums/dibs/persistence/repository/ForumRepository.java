package com.javaranch.forums.dibs.persistence.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.javaranch.forums.dibs.persistence.model.Forum;
import com.javaranch.forums.dibs.persistence.model.Person;



@Repository
public interface ForumRepository extends GraphRepository<Forum> {

	//@Query(value="MATCH (p:Person)-[:DIBS_ON]->(n:Forum) return n")
	@Query(value="MATCH (n:Forum)-[:DIBS_ON]->() return DISTINCT n")
	public List<Forum> findAllClaimed();

	@Query(value="MATCH (n:Forum) WHERE NOT (n)-[:DIBS_ON]->() return n")
	public List<Forum> findAllUnclaimed();

	@Query(value="MATCH (n:Forum) WHERE (n)-[:DIBS_ON]->({0}) return n")
	public Set<Forum> findDibsOn(Person person);

	/**
	 * Test to see if a Forum with this name already exists.
	 * @param name Name of Forum to check. 
	 * @return 0 unless the Forum already exists. Otherwise
	 * number of Forum nodes matching name.
	 */
	@Query(value="MATCH (n:Forum {name: {name}}) return COUNT(*)")
	public int hasName(@Param("name") String name);
}
