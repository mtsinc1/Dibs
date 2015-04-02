package com.javaranch.forums.dibs.persistence.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.javaranch.forums.dibs.persistence.model.Forum;
import com.javaranch.forums.dibs.persistence.model.Person;
import com.javaranch.forums.dibs.persistence.repository.ForumRepository;
import com.javaranch.forums.dibs.persistence.repository.PersonRepository;




@Repository
@Transactional
public class ConnectionService {

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

	/**
	 * Connect a person with targetIDs.
	 * @param personId
	 * @param connectType Type of connection. DIBS or MODERATES.
	 * @param targetIds
	 */
	public void connect(long personId, String connectType,
			long[] targetIds) {
		Person p = this.personRepository.findOne(personId);
		final int targetCount = targetIds.length;
		Set<Forum> forums = new HashSet<Forum>();
		for (int i = 0; i < targetCount; i++) {
			Forum f = this.forumRepository.findOne(targetIds[i]);
			forums.add(f);
		}
		//=<<< p.setDibsList(forums);
		this.personRepository.save(p);
	}

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
}
