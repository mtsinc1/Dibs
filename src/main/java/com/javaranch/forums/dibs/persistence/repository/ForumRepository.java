package com.javaranch.forums.dibs.persistence.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.javaranch.forums.dibs.persistence.model.Forum;
import com.javaranch.forums.dibs.persistence.model.Person;

/**
 * Repository interface for Forum nodes.
 * @author timh
 * @since Apr 1, 2015
 * @TestedBy ForumRepositoryTest
 */

@Repository
@Transactional
public interface ForumRepository extends GraphRepository<Forum> {

	@Query(value="MATCH (n:Forum) WHERE (:Person)-[:dibs_on]->(n:Forum) return n")
	public List<Forum> findAllClaimed();

	@Query(value="MATCH (n:Forum) WHERE NOT (:Person)-[:dibs_on]->(n) return n ORDER BY n.name")
	public List<Forum> findAllUnclaimed();

	@Query(value="MATCH (n:Forum) WHERE ({0})-[:dibs_on]->(n) return n")
	public Set<Forum> findDibsOn(Person person);


	@Query(value="START f=node({f}) MATCH (p:Person)-[:moderates]->(f) return p")
	public List<Person> findModerators(@Param("f") Forum f);
	
	
	/**
	 * Test to see if a Forum with this name already exists.
	 * @param name Name of Forum to check. 
	 * @return 0 unless the Forum already exists. Otherwise
	 * number of Forum nodes matching name.
	 */
	@Query(value="MATCH (n:Forum {name: {name}}) return COUNT(*)")
	public int hasName(@Param("name") String name);

	@Query(value="MATCH (n:Forum {name: {name}}) return n")
	public Forum findByName(@Param("name") String forumName);
}
