package com.javaranch.forums.dibs.persistence.service;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.javaranch.forums.dibs.persistence.model.Person;
import com.javaranch.forums.dibs.persistence.repository.ForumRepository;
import com.javaranch.forums.dibs.persistence.repository.PersonRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/testApplicationContext.xml" })
public class DBLoaderTest {

	/* Logger */
	
	final private static Logger log =
			Logger.getLogger(DBLoaderTest.class);
	
	static final String DATA_DIR = "src/test/resources";

	@Autowired
	private DBLoader dbLoader;

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private ForumRepository forumRepository;

	@Test
	public void test1() throws IOException {

		long npersons = personRepository.count();
		assertEquals(0, npersons);

		long nforums = forumRepository.count();
		assertEquals(0, nforums);

		RescanningLineNumberReader rdr = buildReader("test.yml");
		dbLoader.load(rdr);
		rdr.close();

		npersons = personRepository.count();
		assertEquals(3, npersons);

		nforums = forumRepository.count();
		assertEquals(4, nforums);

		{
			Person p1 =
					personRepository
						.findByName("Fred Feebleberger");
			assertNotNull(p1);
		}

		// Apply maintenance to loaded data. Removes 1, adds 1.
		rdr = buildReader("test2.yml");
		dbLoader.load(rdr);
		rdr.close();

		{
			Person p2 =
					personRepository
						.findByName("Fred Feebleberger");
			assertNull(p2);
			
			npersons = personRepository.count();
			assertEquals(3, npersons);			
		}
	}

	/**
	 * @param yamlfile
	 * @return
	 * @throws FileNotFoundException
	 */
	private RescanningLineNumberReader buildReader(
			String yamlfile) throws FileNotFoundException {
		File f = new File(DATA_DIR, yamlfile);
		log.info("FILE='" + f.getAbsolutePath() + "'");
		FileReader frdr = new FileReader(f);
		LineNumberReader lnr = new LineNumberReader(frdr);
		RescanningLineNumberReader rdr =
				new RescanningLineNumberReader(lnr);
		return rdr;
	}
}
