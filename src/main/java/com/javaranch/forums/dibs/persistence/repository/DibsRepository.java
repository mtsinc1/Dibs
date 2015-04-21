package com.javaranch.forums.dibs.persistence.repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;
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
}
