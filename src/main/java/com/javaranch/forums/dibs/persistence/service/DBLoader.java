package com.javaranch.forums.dibs.persistence.service;

import java.io.IOException;
import java.io.Serializable;

import javax.faces.model.SelectItem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.conversion.Result;
import org.springframework.stereotype.Repository;

import com.javaranch.forums.dibs.persistence.model.Dibs;
import com.javaranch.forums.dibs.persistence.model.Forum;
import com.javaranch.forums.dibs.persistence.model.Person;
import com.javaranch.forums.dibs.persistence.repository.ForumRepository;
import com.javaranch.forums.dibs.persistence.repository.PersonRepository;

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
	private transient PersonRepository personRepository;

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
		String line;
		
		Transaction trans = graphDatabaseService.beginTx();
		while ((line = rdr.readLine()) != null) {
			if (line.length() == 0) {
				continue;
			}
			if (line.startsWith("---")) {
				continue;
			}
			if (line.startsWith("persons:")) {
				loadPersons(rdr);
			} else if (line.startsWith("forums:")) {
				loadForums(rdr);
			} else if (line.startsWith("moderates:")) {
				loadModerates(rdr);
			} else if (line.startsWith("dibs:")) {
				loadDibs(rdr);
			} else {
				log.warn("Invalid line in input: line "
						+ rdr.getLineNumber());
			}
		}
		dumpLoad("Last in-trans.");
		trans.success();
		trans.close();
		//dumpLoad("Trans closed.");
	}

	private void loadPersons(RescanningLineNumberReader rdr)
			throws IOException {

		int personExists = 0;
		int personNew = 0;

		String line;
		while (((line = rdr.readLine()) != null)
				&& CrudeYAMLParser.isItemLine(line)) {
			// line value is person name ("- name"). Add if
			// not present.
			String[] v = CrudeYAMLParser.extractItem(line);
			String name = v[0];
			if (name.endsWith("-")) {
				personDelete(name);
			} else {
				int k = personRepository.hasName(name);
				if (k == 0) {
					if (log.isDebugEnabled()) {
						log.debug("Adding " + name);
					}
					Person person = new Person(name);
					personRepository.save(person);
					personNew++;
				} else {
					if (log.isDebugEnabled()) {
						log.debug("Exists " + name);
					}
					personExists++;
				}
			}
		}
		dumpLoad("Persons loaded.");
		log.info(personExists + " persons already existed.");
		log.info(personNew + " persons added.");
		log.info((personNew + personExists) + " persons total.");
		rdr.rescan();
	}

	public void dumpLoad(String string) {
		// TODO Auto-generated method stub
		log.warn(">>>DUMPLOAD: " +string);
		Result<Person> persons = personRepository.findAll();
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
		Person p = personRepository.findByName(name);
		if (p != null) {
			personRepository.delete(p);
		}
	}

	/**
	 * Load forums
	 */
	@Autowired
	private ForumRepository forumRepository;

	private void loadForums(RescanningLineNumberReader rdr)
			throws IOException {
		int forumExists = 0;
		int forumNew = 0;

		String line;
		while ((line = rdr.readLine()) != null) {
			if (CrudeYAMLParser.isItemLine(line)) {
				// line value is forum name ("- name"). Add if
				// not present.
				String[] v = CrudeYAMLParser.extractItem(line);
				String name = v[0];
				if (name.endsWith("-")) {
					forumDelete(name);
				} else {
					int k = forumRepository.hasName(name);
					if (k == 0) {
						Forum node = new Forum(name);
						forumRepository.save(node);
						forumNew++;
					} else {
						forumExists++;
					}
				}
			} else {
				rdr.rescan();
				break;
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
		Forum f = forumRepository.findByName(name);
		if (f != null) {
			forumRepository.delete(f);
		}

	}

	//===
	/**
	 * Load person's Dibs requests, in priority order.
	 * 
	 * Form is:
	 * <code>
	 *   person:
	 *     - forum
	 *     - forum
	 *     - forum
	 * </code>
	 * @param rdr
	 * @throws IOException
	 */
	private void loadDibs(RescanningLineNumberReader rdr)
			throws IOException {
		String line;
		while ((line = rdr.readLine()) != null) {
			if (CrudeYAMLParser.isItemLine(line)) {
				// line value is forum name ("- name"). Add if
				// not present.
				String[] v = CrudeYAMLParser.extractItem(line);
				String name = v[0];
				// Name MUST be "person"
				if (v[1] != null) {
					// Person name
					String personName = name;
					int k = personRepository.hasName(personName);
					Person node = null;
					if (k == 0) {
						node = new Person(personName);
						personRepository.save(node);
					} else {
						node =
								personRepository
									.findByName(personName);
					}
					loadPersonDibs( node, rdr );
				}
			}
		}
		rdr.rescan();		
	}
	
	/**
	 * Load the forums that a person has placed Dibs on.
	 * Does not return updated personNode.
	 * 
	 * @param personNode person for whom Dibs bids are being
	 * collected.
	 * @param rdr Input data source
	 * @throws IOException 
	 */
	private void loadPersonDibs(Person personNode,
			RescanningLineNumberReader rdr) throws IOException {
		// Clear old dibs, if any!
		
		personNode.clearDibs();
		String line;
		int priority = 0;
		while ((line = rdr.readLine()) != null) {
			if (CrudeYAMLParser.isItemLine(line)) {
				// line value is forum name ("- name"). Add if
				// not present.
				String[] v = CrudeYAMLParser.extractItem(line);
				String name = v[0];
				if (v[1] != null) {
					// Forum name
					String forumName = name;
					int k = forumRepository.hasName(forumName);
					Forum node = null;
					if (k == 0) {
						node = new Forum(forumName);
						forumRepository.save(node);
					} else {
						node =
								forumRepository
									.findByName(forumName);
					}
					Dibs dibs = new Dibs ( personNode, node, priority++);
					personNode.addDibs(dibs);
				}
			}
		}
		personRepository.save(personNode);
		rdr.rescan();		
	}

	//===
	private void loadModerates(RescanningLineNumberReader rdr)
			throws IOException {
		String line;
		while ((line = rdr.readLine()) != null) {
			if (CrudeYAMLParser.isItemLine(line)) {
				// line value is forum name ("- name"). Add if
				// not present.
				String[] v = CrudeYAMLParser.extractItem(line);
				String forumName = v[1];
				if (forumName != null) {
					Forum node = null;
					node =
							forumRepository
								.findByName(forumName);
					if (node == null) {
						node = new Forum(forumName);
						node = forumRepository.save(node);
					}
					loadModerateUsers(node, rdr);
				}
			}
		}
		rdr.rescan();
	}

	/**
	 * Load list of currnt moderators for forum
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
								personRepository
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
		forumRepository.save(forum);
	}

	/**
	 * Remove person from forum if person exists and is in forum.
	 * 
	 * @param forum
	 * @param name
	 */
	private void forumRemove(Forum forum, String name) {
		name = name.substring(0, name.length() - 1).trim();

		Person p = personRepository.findByName(name);
		if (p != null) {
			forum.getModerators().remove(p);
		}
	}
}
