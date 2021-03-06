package com.javaranch.forums.dibs.persistence.repository;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.javaranch.forums.dibs.persistence.model.Person;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/testApplicationContext.xml" })
public class PersonRepositoryTest {

	@Autowired
	private PersonRepository personRepository;

	// ---
	@Autowired
	private GraphDatabaseService graphDatabaseService;

	@Test
	public void testFindByName() {
		try (final Transaction trans =
				this.graphDatabaseService.beginTx()) {
			Person node = new Person("Test Person");
			personRepository.save(node); // << TRANSACTION

			Person n1 =
					personRepository.findByName("Test Person");
			assertNotNull(n1);

			// Note: "save" doesn't create new node
			// if unique index constraint exists.
			node = new Person("Test Person");
			personRepository.save(node);

			// Uniqueness test. Throws NoSuchElement if not
			// unique.
			n1 = personRepository.findByName("Test Person");
			assertNotNull(n1);

			n1 = personRepository.findByName("Any Person");
			assertNull(n1);
		}
	}
}
