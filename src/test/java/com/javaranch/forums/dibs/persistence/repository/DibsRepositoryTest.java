package com.javaranch.forums.dibs.persistence.repository;

import static org.junit.Assert.*;

import java.util.List;

import org.apache.log4j.Logger;
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

	/* Logger */
	
	final private static Logger log =
			Logger.getLogger(DibsRepositoryTest.class);
	
	private static final String ROCK_ARRANGING = "Rock Arranging";

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
		f = new Forum(ROCK_ARRANGING);
//		f.getDibsBidders().add(node1);
//		f.getDibsBidders().add(node2);
		f = forumRepository.save( f);
		tx = template.getGraphDatabase().beginTx();
//		Dibs d1 = new Dibs(node1, f, 5);
//		d1.person = node1;
//		d1.forum = f;
//		Dibs d2 = new Dibs(node2, f, 2);
//		d1 = dibsRepository.save(d1);
//		d2 = dibsRepository.save(d2);
		
		node1.dibsOn(f, 3);
		node2.dibsOn(f, 2);

		node1 = personRepository.save(node1);
		node2 = personRepository.save(node2);

		
//		f.getDibsBidders().add(d1);
		tx.success();

		//===
		tx = template.getGraphDatabase().beginTx();
		f = new Forum("Axe Chipping");
		f = forumRepository.save( f);
		Dibs d3 = new Dibs(node2, f, 5);
		d3 = dibsRepository.save(d3);
		tx.success();
		
		//===

		
		Result<Dibs> list = dibsRepository.findAll();
		int listsize = 0;
		for (Dibs zz: list) {
			log.debug("DIBSLIST: " + zz);
			listsize++;
		}
		assertNotNull(list);
		assertEquals(8, listsize);
		
		Forum arrangers = forumRepository.findByName(ROCK_ARRANGING);
		assertNotNull(arrangers);
		
		List<Dibs> alist = arrangers.getDibsBidders();
		for ( Dibs d: alist ) {
			log.debug("ARRANGE: "+d);
			
			Dibs u = dibsRepository.findOne(d.getNodeId());
			log.debug("LOOKUP: "+u);
		}
	}
}
