package com.javaranch.forums.dibs.persistence.repository;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.graphdb.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.conversion.Result;
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

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private ForumRepository forumRepository;
	
	@Test
	public void testFindByForum() {
		
		Transaction tx = template.getGraphDatabase().beginTx();
		Person node1 = new Person("Test Person");
		node1 = personRepository.save(node1);
		Person node2 = new Person("Person 2");
		node2 = personRepository.save(node2);
		
		Forum f;
		f = new Forum("Rock Arranging");
//		f.getDibsBidders().add(node1);
//		f.getDibsBidders().add(node2);
		f = forumRepository.save( f);
//
//		node1.getDibsList().add(f);
//		template.save(node1);
//		node2.getDibsList().add(f);
//		template.save(node2);
		tx.success();
		
		
		tx = template.getGraphDatabase().beginTx();
		Dibs d1 = new Dibs(node1, f, 1);
		d1.person = node1;
		d1.forum = f;
		Dibs d2 = new Dibs(node2, f, 2);
		d1 = dibsRepository.save(d1);
		d2 = dibsRepository.save(d2);
		
		tx.success();
		
		List<Dibs> list = dibsRepository.findAllDibs(f);
		for (Dibs zz: list) {
			System.out
				.println(zz);
		}
		assertNotNull(list);
//		assertEquals(2, list.size());
	}
}
