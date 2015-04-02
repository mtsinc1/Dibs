package com.javaranch.forums.dibs.persistence.repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;

import com.javaranch.forums.dibs.persistence.model.Dibs;
import com.javaranch.forums.dibs.persistence.model.Forum;

/**
 * 
 * @author timh
 * @since Apr 2, 2015
 * 
 * @TestedBy DibsRepositoryTest
 */
public interface DibsRepository extends GraphRepository<Dibs> {

	@Query(value="MATCH (n:Dibs {forum: {forum}}) return n ORDER BY n.priority ")
	List<Dibs> findAllDibs(@Param("forum") Forum f);
}
