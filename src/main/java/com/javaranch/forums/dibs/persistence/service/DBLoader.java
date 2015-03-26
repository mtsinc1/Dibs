package com.javaranch.forums.dibs.persistence.service;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.javaranch.forums.dibs.persistence.model.Forum;
import com.javaranch.forums.dibs.persistence.model.Person;
import com.javaranch.forums.dibs.persistence.repository.ForumRepository;
import com.javaranch.forums.dibs.persistence.repository.PersonRepository;

/**
 * Database loading from flat storage.
 * 
 * @author timh
 * @since Jun 4, 2014
 * @TestWith TestDBLoader
 */
@Repository(value = "dbLoader")
public class DBLoader {
/**
	 * Data format: <pre><code>
	 * ---
	 * persons:
	 *  - John Doe
	 *  - Joe Schmoe
	 *  - Harriet Jones
	 * forums:
	 *  - butterfly collecting
	 *  - cookie baking
	 *  - butterfly arranging
	 * moderates:
	 *  forum: name:
	 *     cookie baking persons:
	 *      - John Doe
	 *      - Harriet Jones
	 *  forum:
	 *     name: etc.
	 *  </code></pre>
	 * <p/>
	 * Moderations of undeclared persons/forums will
	 * automatically add them with a warning.
	 */
	/* Logger */

	final private static Logger log = 
		LogManager.getLogger(DBLoader.class);
	// --
	@Autowired
	private transient PersonRepository personRepository;

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
			} else {
				log.warn("Invalid line in input: line "
						+ rdr.getLineNumber());
			}
		}
	}

	private void loadPersons(RescanningLineNumberReader rdr)
			throws IOException {

		int personExists = 0;
		int personNew = 0;

		String line;
		while (((line = rdr.readLine()) != null) &&
			 CrudeYAMLParser.isItemLine(line)) {
				// line value is person name ("- name"). Add if
				// not present.
				String[] v = CrudeYAMLParser.extractItem(line);
				String name = v[0];
				int k = personRepository.hasName(name);
				if (k == 0) {
					Person person = new Person(name);
					personRepository.save(person);
					personNew++;
				} else {
					personExists++;
				}
		}
		log.info(personExists + " persons already existed.");
		log.info(personNew + " persons added.");
		log.info((personNew + personExists) + " persons total.");
		rdr.rescan();
	}

	// ===

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
				int k = forumRepository.hasName(name);
				if (k == 0) {
					Forum person = new Forum(name);
					forumRepository.save(person);
					forumNew++;
				} else {
					forumExists++;
				}
			}
		}
		log.info(forumExists + " forums already existed.");
		log.info(forumNew + " forums added.");
		log.info((forumNew + forumExists) + " forums total.");
		rdr.rescan();
		rdr.rescan();
		;
	}

	// ===

	private void loadModerates(RescanningLineNumberReader rdr)
			throws IOException {
		// TODO
	}
}
