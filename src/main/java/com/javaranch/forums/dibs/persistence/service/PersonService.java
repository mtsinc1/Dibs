package com.javaranch.forums.dibs.persistence.service;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.conversion.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.javaranch.forums.dibs.persistence.model.Person;
import com.javaranch.forums.dibs.persistence.repository.PersonRepository;

@Service
@Transactional
public class PersonService {

	@Autowired
	private PersonRepository personRepository;
	
	/**
	 * @param personRepository the personRepository to set
	 */
	public void setPersonRepository(PersonRepository personRepository) {
		this.personRepository = personRepository;
	}

	//===
	// --
	@Autowired
	private transient GraphDatabaseService graphDatabaseService;

	/**
	 * @param forumRepository
	 *            the forumRepository to set
	 */
	public void setGraphDatabaseService(
			GraphDatabaseService service) {
		this.graphDatabaseService = service;
	}
	
	//===
	
	public int hasName(String name) {
		try (final Transaction trans =
				this.graphDatabaseService.beginTx()) {
			return this.personRepository.hasName(name);
		}
	}

	public Person save(Person person) {
		try (Transaction trans =
				this.graphDatabaseService.beginTx()) {
			Person p = this.personRepository.save(person);
			trans.success();
			return p;
		}
	}

	public Result<Person> findAll() {
		try (final Transaction trans =
				this.graphDatabaseService.beginTx()) {
			return this.personRepository.findAll();
		}
	}

	public Person findByName(String name) {
		try (final Transaction trans =
				this.graphDatabaseService.beginTx()) {
			return this.personRepository.findByName(name);
		}
	}

	public void delete(Person p) {
		try (final Transaction trans =
				this.graphDatabaseService.beginTx()) {
			this.personRepository.delete(p);
		}
	}
}
