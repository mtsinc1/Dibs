package com.javaranch.forums.dibs.persistence.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.javaranch.forums.dibs.backing.JSFUtils;
import com.javaranch.forums.dibs.persistence.model.Forum;
import com.javaranch.forums.dibs.persistence.model.Person;


import com.javaranch.forums.dibs.persistence.repository.ForumRepository;
import com.javaranch.forums.dibs.persistence.repository.PersonRepository;


/**
 * Database loading from flat storage.
 * 
 * @author timh
 * @since Jun 4, 2014
 */
@Repository(value="dbLoader")
public class DBLoader {

	// --
	@Autowired
	private transient PersonRepository personRepository;

	/**
	 * Load Persons
	 */
	public void loadPersons() {
		// final String DBFILE = "/home/timh/neo4j/dibsdb";
		// GraphDatabaseService graphDb =
		// new EmbeddedGraphDatabase(DBFILE);
		// registerShutdownHook(graphDb);

		// Node tom=gds.createNode();
		// tom.setProperty("name","Tom Hanks");
		// tom.setProperty("staff", "person");

		LineNumberReader ireader =
				new LineNumberReader(
						new InputStreamReader(
								JSFUtils
									.getResourceStream("/WEB-INF/classes/staff.txt")));
		String line;
		try {
			while ((line = ireader.readLine()) != null) {
				Person person = new Person(line);
				personRepository.save(person);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			ireader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// --
	
	/**
	 * Load forums
	 */
	@Autowired
	private ForumRepository forumRepository;

	public void loadForumList() {

		LineNumberReader ireader =
				new LineNumberReader(
						new InputStreamReader(
								JSFUtils
									.getResourceStream("/WEB-INF/classes/forums.txt")));
		String line;
		try {
			while ((line = ireader.readLine()) != null) {
				Forum forum = new Forum(line);
				forumRepository.save(forum);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			ireader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
