package com.javaranch.forums.dibs.persistence.service;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Serializable;
import java.util.Iterator;
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
 * 
 *           TODO: Re-validate the database item delete
 *           functions. They were originally written for
 *           "brute-force" YAML parsing.
 */
@Repository(value = "dbLoader")
public class DBLoader implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
	 * @param personService
	 *            the personService to set
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
	public void load(final LineNumberReader rdr)
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

	/**
	 * Load Person entries. Entry may be simple name or
	 * "name: new", where "new" indicated that the Person
	 * should be tagged as a rookie.
	 * @param o List of Person entries digested from YAML.
	 * @throws IOException
	 */
	private void loadPersons(Object o) throws IOException {

		if (o == null) { // No data
			return;
		}

		@SuppressWarnings("unchecked")
		List<Object> olist = (List<Object>) o;

		int personExists = 0;
		int personNew = 0;

		for (Object item : olist) {
			NameValue nv = findNameValue(item);
			final String name = nv.getName();

			// TODO: add/update "rookie" status

			if (name.endsWith("-")) {
				personDelete(name);
			} else {
				Person person = this.personService.findByName(name);
				if (person == null) {
					if (log.isDebugEnabled()) {
						log.debug("Adding " + name);
					}
					person = new Person(name);
					if ( nv.hasValue() ) {
						if ( nv.getStringValue().equals("new")) {
							person.setRookie(true);
						}
					}
					personService.save(person);
					personNew++;
				} else {
					if (log.isDebugEnabled()) {
						log.debug("Exists " + name);
					}
					if (nv.getStringValue().equals("new")) {
						person.setRookie(true);
						personService.save(person);
					}
					personExists++;
				}
			}
		}
		// dumpLoad("Persons loaded.");
		log.info(personExists + " persons already existed.");
		log.info(personNew + " persons added.");
		log.info((personNew + personExists) + " persons total.");
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
	 * @param forumService
	 *            the forumService to set
	 */
	public void setForumService(ForumService forumService) {
		this.forumService = forumService;
	}

	/**
	 * Load forums. Forum entry may be simple name or
	 * "name: numModerators".
	 * 
	 * @param o
	 *            List of Forums loaded from YAML.
	 * @throws IOException
	 */
	private void loadForums(Object o) throws IOException {

		if (o == null) { // No data
			return;
		}

		int forumExists = 0;
		int forumNew = 0;

		@SuppressWarnings("unchecked")
		List<Object> olist = (List<Object>) o;
		for (Object item : olist) {
			NameValue nv = findNameValue(item);

			String name = nv.getName();
			if (name.endsWith("-")) {
				forumDelete(name);
			} else {
				Forum forum = forumService.findByName(name);
				if (forum == null) {
					if (log.isDebugEnabled()) {
						log.debug("Adding " + name);
					}
					forum = new Forum(name);
					if (nv.hasValue()) {
						forum.setNumModerators(nv.getIntValue());
					}
					forumService.save(forum);
					forumNew++;
				} else {
					if (log.isDebugEnabled()) {
						log.debug("Exists " + name);
					}
					if (nv.hasValue()) {
						// update
						forum.setNumModerators(nv.getIntValue());
					}
					forumService.save(forum);
					forumExists++;
				}
			}
		}
		log.info(forumExists + " forums already existed.");
		log.info(forumNew + " forums added.");
		log.info((forumNew + forumExists) + " forums total.");
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
	private void loadDibs(Object o) throws IOException {

		if (o == null) { // No data
			return;
		}

		@SuppressWarnings("unchecked")
		Map<String, List<Map<String, Integer>>> m =
				(Map<String, List<Map<String, Integer>>>) o;
		for (Object forumName : m.keySet()) {
			// System.out.println("DIBS KEY=" + forumName);
			List<Map<String, Integer>> v = m.get(forumName);
			// System.out.println("DIBS VALUES" + v);

			Forum forum = this.findForum((String) forumName);
			loadDibsForum(forum, v);
		}
	}

	/**
	 * Create Dibs records for a forum.
	 * 
	 * @param forum
	 * @param personPrtyList
	 *            list of Map(personName, int dibs priority)
	 */
	private void loadDibsForum(Forum forum,
			List<Map<String, Integer>> personPrtyList) {
		for (Map<String, Integer> map : personPrtyList) {
			String personNameString =
					map.keySet().iterator().next();
			Integer priority = map.get(personNameString);
			Person person = findPerson(personNameString);
			Dibs dibs = findDibs(person, forum);
			dibs.setPriority(priority);
			try (final Transaction trans =
					this.graphDatabaseService.beginTx()) {
				this.dibsRepository.save(dibs);
				// this.personService.save(person);
				trans.success();
			}
		}
	}

	// ===
	/**
	 * Load existing moderation information.
	 * 
	 * @param object
	 *            children of "moderates:" YAML
	 * @throws IOException
	 */
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

			// log.debug("KEY=" + key);
			@SuppressWarnings("unchecked")
			List<String> moderators =
					(List<String>) forumMap.get(key);
			for (String name : moderators) {
				// log.debug("MODERATOR=" + name);
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

	// ===

	@SuppressWarnings("rawtypes")
	private NameValue findNameValue(Object o) {
		if (o instanceof java.lang.String) {
			NameValue nv = new NameValue((String) o);
			return nv;
		}

		if (o instanceof java.util.Map) {
			Map m = (Map) o;
			Set keys = m.keySet();
			if (keys.isEmpty()) {
				throw new RuntimeException("No keys for object "
						+ o);
			}

			if (keys.size() != 1) {
				throw new RuntimeException(
						"Object has more than one key/value "
								+ o);
			}

			String name = (String) keys.iterator().next();
			Object value = m.get(name);
			NameValue nv = new NameValue(name, value);
			return nv;
		}

		throw new RuntimeException("Unrecognized object type "
				+ o);
	}

	// ===
	/**
	 * Locate a Person. Create Person record if it doesn't
	 * already exist.
	 * 
	 * @param name
	 * @category locator
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

	// ---

	/**
	 * Locate a Forum. Create Forum record if it doesn't already
	 * exist.
	 * 
	 * @param name
	 * @category locator
	 * @return
	 */
	private Forum findForum(String name) {
		Forum p = forumService.findByName(name);
		if (p == null) {
			p = new Forum(name);
			p = this.forumService.save(p);
		}
		return p;
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

	// ---
	/**
	 * Obtain a unique Dibs for a Person on a Forum. Because the
	 * database cannot constrain unique relationships, this
	 * method will return one entry and delete any others.
	 * 
	 * @param person
	 * @param forum
	 * @return Dibs. Will construct one if none found.
	 * @category locator
	 */
	private Dibs findDibs(Person person, Forum forum) {
		Dibs dibs = null;
		try (final Transaction trans =
				this.graphDatabaseService.beginTx()) {
			Set<Dibs> fd =
					this.dibsRepository.findRelation(person,
						forum);
			log.debug("OBK-" + fd);
			if (!fd.isEmpty()) {
				Iterator<Dibs> iter = fd.iterator();
				dibs = iter.next();
				while (iter.hasNext()) {
					this.dibsRepository.delete(iter.next());
				}
			}
			trans.success();
		}
		if (dibs == null) {
			dibs = new Dibs(person, forum);
		}
		return dibs;
	}
}
