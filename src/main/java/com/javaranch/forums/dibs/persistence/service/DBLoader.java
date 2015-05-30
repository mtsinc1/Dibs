package com.javaranch.forums.dibs.persistence.service;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.conversion.Result;
import org.springframework.stereotype.Repository;
import org.yaml.snakeyaml.Yaml;

import com.javaranch.forums.dibs.persistence.model.Dibs;
import com.javaranch.forums.dibs.persistence.model.Forum;
import com.javaranch.forums.dibs.persistence.model.Person;
import com.javaranch.forums.dibs.persistence.repository.DibsRepository;

/**
 * Database loading from flat storage.
 * 
 * @author timh
 * @since Jun 4, 2014
 * @TestedBy DBLoaderTest
 */
@Repository(value = "dbLoader")
public class DBLoader implements Serializable {
	/**
	 * Data format:
	 * 
	 * <pre>
	 * <code>
	 * ---
	 * persons:
	 *  - John Doe
	 *  - Joe Schmoe
	 *  - Harriet Jones
	 * forums:
	 *  - butterfly collecting
	 *  - cookie baking
	 *  - butterfly arranging
	 * dibs:
	 *   - person: John Doe
	 *     - cookie baking
	 *     - butterfly arranging
	 *   - person: Harriet Jones
	 *     - butterfly collecting
	 * moderates:
	 *  forum: name
	 *    - John Doe
	 *    - Harriet Jones
	 *  forum: name
	 *    - person
	 *      
	 *  </code>
	 * </pre>
	 * <p/>
	 * Moderations of undeclared persons/forums will
	 * automatically add them with a warning.
	 * 
	 * Appending a "-" to the person or forum name deletes that
	 * node (if present).
	 * 
	 * TODO: under moderates, the construct "- forum: -" should
	 * remove all moderators from that forum.
	 * 
	 */
	/* Logger */

	final private static Logger log = LogManager
		.getLogger(DBLoader.class);
	// --
	@Autowired
	private transient PersonService personService;

	/**
	 * @param personService the personService to set
	 */
	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	// --
	@Autowired
	private transient DibsRepository dibsRepository;

	/**
	 * @param dibsRepository
	 *            the dibsRepository to set
	 */
	public void setDibsRepository(DibsRepository dibsRepository) {
		this.dibsRepository = dibsRepository;
	}

	// ---
	@Autowired
	private transient GraphDatabaseService graphDatabaseService;


	public void setGraphDatabaseService(
			GraphDatabaseService service) {
		this.graphDatabaseService = service;
	}

	// ===
	/**
	 * Default Constructor
	 */

	public DBLoader() {

	}

	/**
	 * Load YAML-format data
	 * 
	 * @param rdr
	 *            - Input line-oriented datastream.
	 * @throws IOException
	 */
	public void load(final RescanningLineNumberReader rdr)
			throws IOException {
		Yaml yaml = new Yaml();
		@SuppressWarnings("rawtypes")
		Map map = (Map) yaml.load(rdr);
		System.out.println(map);
		loadPersons(map.get("persons"));
		loadForums(map.get("forums"));
		loadModerates(map.get("moderates"));
		loadDibs(map.get("dibs"));
	}

	private void loadPersons(Object o) throws IOException {

		if ( o == null ) { // No data
			return;
		}
		
		@SuppressWarnings("unchecked")
		List<String> r = (List<String>) o;

		int personExists = 0;
		int personNew = 0;

		for (String name : r) {
			if (name.endsWith("-")) {
				personDelete(name);
			} else {
				int k = personService.hasName(name);
				if (k == 0) {
					if (log.isDebugEnabled()) {
						log.debug("Adding " + name);
					}
					Person person = new Person(name);
					personService.save(person);
					personNew++;
				} else {
					if (log.isDebugEnabled()) {
						log.debug("Exists " + name);
					}
					personExists++;
				}
			}
		}
		//dumpLoad("Persons loaded.");
		log.info(personExists + " persons already existed.");
		log.info(personNew + " persons added.");
		log.info((personNew + personExists) + " persons total.");
		// rdr.rescan();
	}

	void dumpLoad(String string) {
		// TODO Auto-generated method stub
		log.warn(">>>DUMPLOAD: " + string);
		Result<Person> persons = personService.findAll();
		for (Person person : persons) {
			log.info("Person: " + person.getName());
		}
	}

	// ===

	/**
	 * Delete a person
	 * 
	 * @param name
	 */
	private void personDelete(String name) {
		name = name.substring(0, name.length() - 1).trim();
		log.info("Deleting person " + name);
		Person p = personService.findByName(name);
		if (p != null) {
			personService.delete(p);
		}
	}

	/**
	 * Load forums
	 */
	@Autowired
	private ForumService forumService;

	/**
	 * @param forumService the forumService to set
	 */
	public void setForumService(ForumService forumService) {
		this.forumService = forumService;
	}

	private void loadForums(Object o) throws IOException {

		if ( o == null ) { // No data
			return;
		}

		@SuppressWarnings("unchecked")
		List<String> r = (List<String>) o;

		int forumExists = 0;
		int forumNew = 0;

		for (String name : r) {
			if (name.endsWith("-")) {
				forumDelete(name);
			} else {
				int k = forumService.hasName(name);
				if (k == 0) {
					if (log.isDebugEnabled()) {
						log.debug("Adding " + name);
					}
					Forum forum = new Forum(name);
					forumService.save(forum);
					forumNew++;
				} else {
					if (log.isDebugEnabled()) {
						log.debug("Exists " + name);
					}
					forumExists++;
				}
			}
		}
		//dumpLoad("forums loaded.");
		log.info(forumExists + " forums already existed.");
		log.info(forumNew + " forums added.");
		log.info((forumNew + forumExists) + " forums total.");
		// rdr.rescan();
	}

	// ===

	/**
	 * Delete a forum.
	 * 
	 * @param name
	 */
	private void forumDelete(String name) {
		name = name.substring(0, name.length() - 1).trim();
		log.info("Deleting forum " + name);
		Forum f = forumService.findByName(name);
		if (f != null) {
			forumService.delete(f);
		}

	}

	// ===
	/**
	 * Load person's Dibs requests, in priority order.
	 * 
	 * Form is: <code>
	 * dibs:
	 *    - forum1:
	 *     - person1: priority
	 *     - person2: priority
	 *   - forum2:
	 * </code>
	 * 
	 * Priority is the priority that the person has assigned to
	 * this forum bid.
	 * 
	 * @param object
	 * @throws IOException
	 */
	private void loadDibs(Object object)
			throws IOException {
		String line;
//		while ((line = object.readLine()) != null) {
//			if (CrudeYAMLParser.isItemLine(line)) {
//				// line value is forum name ("- name"). Add if
//				// not present.
//				String[] v = CrudeYAMLParser.extractItem(line);
//				String forumName = v[0];
//				if (forumName != null) {
//					Forum f =
//							this.forumService
//								.findByName(forumName);
//					if (f == null) {
//						log.error("Forum " + forumName
//								+ " does not exist");
//						// todo: create forum
//					}
//					loadPersonDibs(f, object);
//					this.forumService.save(f);
//				}
//			}
//		}
//		object.rescan();
	}

	/**
	 * Add person Dibs for Forum, with priority
	 * 
	 * @param forum
	 * @param rdr
	 *            Input data source
	 * @throws IOException
	 */
	private void loadPersonDibs(final Forum forum,
			RescanningLineNumberReader rdr) throws IOException {
		String line;
		int priority = 0;
		while ((line = rdr.readLine()) != null) {
			if (CrudeYAMLParser.isItemLine(line)) {
				// line value is forum name ("- name"). Add if
				// not present.
				String[] v = CrudeYAMLParser.extractItem(line);
				String personName = v[0];
				int npri = Integer.parseInt(v[1]); // todo:
													// valudate
				if (personName != null) {
					Person personNode =
							this.personService
								.findByName(personName);
					if (personNode == null) {
						log.error("Person " + personName
								+ " does not exist.");
						// todo: add person
					}
					Dibs dibs =
							new Dibs(personNode, forum, npri);
					personNode.addDibs(dibs);
					this.dibsRepository.save(dibs);
					this.personService.save(personNode);
				}
			}
		}
		rdr.rescan();
	}

	// ===
	private void loadModerates(Object object) throws IOException {


			@SuppressWarnings("rawtypes")
			Map forumMap = (Map) object;
			for (Object key : forumMap.keySet()) {
				String forumName = (String) key;
				Forum forum = null;
				forum = forumService.findByName(forumName);
				if (forum == null) {
					forum = new Forum(forumName);
					forum = forumService.save(forum);
				}

				System.out.println("KEY=" + key);
				@SuppressWarnings("unchecked")
				List<String> moderators =
						(List<String>) forumMap.get(key);
				for (String name : moderators) {
					System.out.println("MODERATOR=" + name);
					Person p = findPerson(name);
					try (final Transaction trans =
							this.graphDatabaseService.beginTx()) {					
						forum.addModerator(p);
						trans.success();
					}
				}
				this.forumService.save(forum);
			}
	}

	/**
	 * Locate a Person. Create Person record if it doesn't already
	 * exist.
	 * @param name
	 * @return
	 */
	private Person findPerson(String name) {
		Person p = personService.findByName(name);
		if (p == null) {
			p = new Person(name);
			p = this.personService.save(p);
		}
		return p;
	}

	/**
	 * Load list of current moderators for forum
	 * 
	 * @param forum
	 *            Forum
	 * @param rdr
	 *            input data reader
	 * @throws IOException
	 */
	private void loadModerateUsers(final Forum forum,
			RescanningLineNumberReader rdr) throws IOException {
		log.debug("Forum: " + forum.name);
		String line;

		while ((line = rdr.readLine()) != null) {
			if (CrudeYAMLParser.isItemLine(line)) {
				String[] v = CrudeYAMLParser.extractItem(line);
				String name = v[0];
				log.debug("  Name " + name);
				if (v[1] == null) {
					// User name. Add to group. (- remove
					// from
					// group )
					if (name.endsWith("-")) {
						forumRemove(forum, name);
					} else {
						Person p =
								personService
									.findByName(name);
						if (p == null) {
							p = new Person(name);
							forum.getModerators().add(p);
						}
						forum.getModerators().add(p);
					}
				} else {
					// New group
					rdr.rescan();
					break;
				}
			}
		}
		// Update persistent
		forumService.save(forum);
	}

	/**
	 * Remove person from forum if person exists and is in forum.
	 * 
	 * @param forum
	 * @param name
	 */
	private void forumRemove(Forum forum, String name) {
		name = name.substring(0, name.length() - 1).trim();

		Person p = personService.findByName(name);
		if (p != null) {
			forum.getModerators().remove(p);
		}
	}
}
