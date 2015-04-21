package com.javaranch.forums.dibs.persistence.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.conversion.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.javaranch.forums.dibs.persistence.model.Dibs;
import com.javaranch.forums.dibs.persistence.model.Forum;
import com.javaranch.forums.dibs.persistence.model.Person;
import com.javaranch.forums.dibs.persistence.repository.DibsRepository;
import com.javaranch.forums.dibs.persistence.repository.ForumRepository;
import com.javaranch.forums.dibs.persistence.repository.PersonRepository;



/**
 * Business-level persistence for Forums.
 * 
 * @author timh
 * @since Jun 10, 2014
 */
@Service
@Transactional
public class ForumService {

	/* Logger */

	final private static Logger log = LogManager
		.getLogger(ForumService.class);

	// --
	@Autowired
	private PersonRepository personRepository;

	/**
	 * @param repository
	 *            the Repository to set
	 */
	public void setPersonRepository(PersonRepository repository) {
		this.personRepository = repository;
	}

	// --
	@Autowired
	private ForumRepository forumRepository;

	/**
	 * @param forumRepository
	 *            the forumRepository to set
	 */
	public void setForumRepository(
			ForumRepository forumRepository) {
		this.forumRepository = forumRepository;
	}

	// --
	@Autowired
	private DibsRepository dibsRepository;

	/**
	 * @param repository
	 *            the Repository to set
	 */
	public void setDibsRepository(DibsRepository repository) {
		this.dibsRepository = repository;
	}

	// ===
	
	/**
	 * Default Constructor.
	 * Constructor.
	 */
	public ForumService() {
		
	}
	
	// ===
	public List<Forum> findAllForums() {
		Result<Forum> forums = forumRepository.findAll();
		ArrayList<Forum> list = new ArrayList<Forum>();
		for (Forum forum : forums) {
			list.add(forum);
		}
		return list;
	}

	/**
	 * Save a list of Dibs. Assumes no existing list.
	 * @param personId Person placing Dibs
	 * @param idList the Forum IDs to place Dibs on.
	 */
	public void connectDibs(long personId, long[] idList) {
		Person person = this.personRepository.findOne(personId);
		if (person == null) {
			throw new RuntimeException("Person ID " + personId
					+ " not found.");
		}

		int priority = 1;
		for (long forumId : idList) {
			Forum f = this.forumRepository.findOne(forumId);
			Dibs d = new Dibs(person, f, priority);
			priority++;
			dibsRepository.save(d);
		}
	}

	/**
	 * Remove person from forum dibs relationships
	 * 
	 * @param personId
	 * @param idList
	 *            Forum Ids
	 */
	public void disconnectDibs(long personId, long[] idList) {
		Person person = this.personRepository.findOne(personId);
		if (person == null) {
			throw new RuntimeException("Person ID " + personId
					+ " not found.");
		}

		Set<Forum> dibsList = new HashSet<Forum>(idList.length);
		for (long forumId : idList) {
			Forum f = this.forumRepository.findOne(forumId);
			if (f != null) {
				f.getDibsBidders().remove(person);
				f = this.forumRepository.save(f);
				dibsList.add(f);
			} else {
				log.error("Forum ID " + forumId + " Not found");
			}
		}

		//=<<person.setDibsList(dibsList);
		this.personRepository.save(person);
	}

	/**
	 * Remove person from forum dibs relationships
	 * 
	 * @param personId
	 * @param idList
	 *            Forum Ids
	 */
	public void clearDibs(long personId) {
		com.javaranch.forums.dibs.persistence.model.Person person = this.personRepository.findOne(personId);
		if (person == null) {
			throw new RuntimeException("Person ID " + personId
					+ " not found.");
		}

		person.removeAllDibs();
//		
//		Set<Forum> dibsList = forumRepository.findDibsOn(person);
//		for (Forum f : dibsList) {
//			f.removeBidder(person);
//			f = this.forumRepository.save(f);
//		}
//
////		person.getDibsList().clear();
		//this.personRepository.save(person);
	}

	// ===
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

	public List<Person> findAllPersons() {
		ArrayList<Person> lst = new ArrayList<Person>();
//		Transaction tx = this.graphDatabaseService.beginTx();
		Result<Person> l = this.personRepository.findAll();
		for (Person person : l) {
			lst.add(person);
		}
//		tx.close();
		return lst;
	}
}
