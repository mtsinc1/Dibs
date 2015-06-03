package com.javaranch.forums.dibs.persistence.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.javaranch.forums.dibs.persistence.model.Dibs;
import com.javaranch.forums.dibs.persistence.model.Forum;
import com.javaranch.forums.dibs.persistence.model.Person;

/**
 * 
 * @author timh
 * @since Apr 2, 2015
 * 
 * @TestedBy DibsRepositoryTest
 */
@Repository
@Transactional
public interface DibsRepository extends GraphRepository<Dibs> {

	/*
	 * @return Dibs for person in ascending priority sequence.
	 */
	@Query(value="START p=node({p}) MATCH (p)-[n:dibs_on]->(:Forum) return n ORDER BY n.priority")
	List<Dibs> findDibsForPerson(@Param("p") Person p);

	
	// This doesn't work:
	@Query(value="MATCH (f:Forum {name: {fname}}) return f.dibsBidders")
	List<Dibs> findAllDibs(@Param("fname") String f);

	@Query("START p=node({p}) MATCH (p)-[n:dibs_on]->(:Forum) DELETE n")
	void clearDibs(@Param("p") Person person);

	/**
	 * Remove ALL records from database.
	 */
	@Query("MATCH (n) OPTIONAL MATCH (n)-[r]-() DELETE n, r")
	void clearDatabase();


	/**
	 * Remove Dibs records from database.
	 */
	@Query("MATCH (n)-[r:dibs_on]-() DELETE r")
	void clearAllDibs();


	/**
	 * Check for Dibs already existing
	 * @param person
	 * @param forum
	 * @return
	 */
	@Query("MATCH (p:Person)-[d:dibs_on]->(f:Forum) WHERE id(p)={p} AND id(f)={f} RETURN d")
	Set<Dibs> findRelation(@Param("p") Person person,
			@Param("f") Forum forum);
}
