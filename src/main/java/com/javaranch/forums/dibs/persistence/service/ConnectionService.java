package com.javaranch.forums.dibs.persistence.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.bean.ManagedProperty;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.javaranch.forums.dibs.persistence.model.Dibs;
import com.javaranch.forums.dibs.persistence.model.Forum;
import com.javaranch.forums.dibs.persistence.model.Person;
import com.javaranch.forums.dibs.persistence.repository.DibsRepository;
import com.javaranch.forums.dibs.persistence.repository.ForumRepository;
import com.javaranch.forums.dibs.persistence.repository.PersonRepository;




@Repository
@Transactional
public class ConnectionService {

	// ---
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

	/**
	 * Connect a person with targetIDs.
	 * @param personId
	 * @param connectType Type of connection. DIBS or MODERATES.
	 * @param targetIds
	 */
	public void connect(long personId, String connectType,
			long[] targetIds) {
		try {
			Person p = this.personRepository.findOne(personId);
			final int targetCount = targetIds.length;
			for (int i = 0; i < targetCount; i++) {
				Forum f = this.forumRepository.findOne(targetIds[i]);
				Dibs d = new Dibs(p, f, i);
				this.dibsRepository.save(d);
				p.getDibsList().add(d);
			}
			this.personRepository.save(p);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//    beanFactory = WebApplicationContextUtils
//            .getRequiredWebApplicationContext(getServletContext());

	
	public void connect(long personId, String connectionType,
			List<Long> targetIds) {
		Person p = this.personRepository.findOne(personId);
		final int targetCount = targetIds.size();
		Set<Forum> forums = new HashSet<Forum>();
		for (int i = 0; i < targetCount; i++) {
			Forum f = this.forumRepository.findOne(targetIds.get(i));
			forums.add(f);
		}
		//=<<< p.setDibsList(forums);
		this.personRepository.save(p);
	}
	
	/**
	 * Return List of Dibs for selected Person ordered by
	 * Dibs Priority
	 * @param person
	 * @return qualifying Dibs
	 */
	public List<Dibs> findDibsOn(Person person) {
		List<Dibs> dlist = 
				personRepository.findDibs(person);
		return dlist;
	}

	public void clearDibs(Person person) {
		this.dibsRepository.clearDibs(person);
	}

	public void clearDatabase() {
		this.dibsRepository.clearDatabase();
	}

	public void clearAllDibs() {
		this.dibsRepository.clearAllDibs();
	}

	public void setDibsToModerators() {
		// TODO Auto-generated method stub
		clearAllDibs();
		// TODO Render all Moderates nodes as Dibs nodes
	}

	/**
	 * Compute number of Persons with Dibs on a Forum
	 * @param f Forum
	 * @return count
	 */
	public int getDibsCount(Forum f) {
		List<Dibs> list =this.dibsRepository.findDibsForForum(f);
		return list.size();
//		return this.dibsRepository.findDibsCountForForum(f);
	}
}
