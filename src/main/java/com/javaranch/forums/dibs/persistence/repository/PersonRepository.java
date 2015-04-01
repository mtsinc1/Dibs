package com.javaranch.forums.dibs.persistence.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.javaranch.forums.dibs.persistence.model.Person;

/**
 * Repository object for Person nodes
 * @author timh
 * @since Apr 1, 2015
 * @TestedBy PersonRepositoryTest
 */

@Repository
@Transactional
public interface PersonRepository extends GraphRepository<Person> {
	
	/**
	 * Test to see if a Person with this name already exists.
	 * @param name Name of Person to check. 
	 * @return 0 unless the Person already exists. Otherwise
	 * number of Person nodes matching name.
	 */
	@Query(value="MATCH (n:Person {name: {name}}) return COUNT(*)")
	public int hasName(@Param("name") String name);

	@Query(value="MATCH (n:Person {name: {name}}) return n")
	public Person findByName(@Param("name") String personName);	
}
