package com.javaranch.forums.dibs.persistence.repository;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.graphdb.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.javaranch.forums.dibs.persistence.model.Dibs;
import com.javaranch.forums.dibs.persistence.model.Forum;
import com.javaranch.forums.dibs.persistence.model.Person;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/testApplicationContext.xml"})
public class DibsRepositoryTest {

	@Autowired
	Neo4jTemplate template;
	
	@Autowired
	private DibsRepository dibsRepository;
	
	@Test
	public void testFindByForum() {
		
		Transaction tx = template.getGraphDatabase().beginTx();
		Person node1 = new Person("Test Person");
		node1 = template.save(node1);
		Person node2 = new Person("Person 2");
		node2 = template.save(node2);
		
		Forum f;
		f = new Forum("Rock Arranging");
//		f.getDibsBidders().add(node1);
//		f.getDibsBidders().add(node2);
		f = template.save( f);
//
//		node1.getDibsList().add(f);
//		template.save(node1);
//		node2.getDibsList().add(f);
//		template.save(node2);
		
		
		Dibs d1 = new Dibs(node1, f, 1);
		Dibs d2 = new Dibs(node2, f, 2);
		d1 = dibsRepository.save(d1);
		d2 = dibsRepository.save(d2);
		
		tx.success();
		
		List<Dibs> list = dibsRepository.findAllDibs(f);
		assertNotNull(list);
		assertEquals(2, list.size());
	}
}