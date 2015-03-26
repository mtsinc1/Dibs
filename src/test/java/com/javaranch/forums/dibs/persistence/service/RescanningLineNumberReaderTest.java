package com.javaranch.forums.dibs.persistence.service;

import static org.junit.Assert.assertEquals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/testApplicationContext.xml"})
public class RescanningLineNumberReaderTest {

//	ClassRule
//	public static ManagedNeoServer managedNeoServer =
//			newManagedNeo4jServerRule()
//				.neo4jPath(
//					"/Users/alexsotobueno/Applications/neo4j-community-1.7.2")
//				.build();

//	@Rule
//	public Neo4jRule neo4jRule = newNeo4jRule()
//		.defaultSpringGraphDatabaseServiceNeo4j();

	final private Pattern COMMENT_PAT = Pattern
		.compile("([^#]*)\\#?.*");

	@Test
//	@UsingDataSet(locations = "star-trek-TNG-dataset.xml", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
	public void test1() {
		String s;

		s = commentTest1("---");
		assertEquals("---", s);
		s = commentTest1(" ABCDE #fgh");
		assertEquals(" ABCDE ", s);
		s = commentTest1(" ABCDE #fgh");
		assertEquals(" ABCDE ", s);
		s = commentTest1(" JABBERWOCKY ");
		assertEquals(" JABBERWOCKY ", s);
		s = commentTest1("#A comment");
		assertEquals("", s);
	}

	private String commentTest1(String string) {
		String s = RescanningLineNumberReader.trimComent(string);
		return s;
	}

	// ---

	@Test
	public void test2() {
		String[] s;
		//
		// s = extractItem(" ABCDE");
		// assertEquals("ABCDE", s[0]);
		// s = extractItem("- ABCDE");
		// assertEquals("ABCDE", s[0]);
		// s = extractItem(" JABBERWOCKY: ");
		// assertEquals("JABBERWOCKY", s[0]);
		// s = extractItem(" WAYNE: BATMAN");
		// assertEquals("WAYNE", s[0]);
		// assertEquals("BATMAN", s[1]);
		//
		// s = extractItem("- FETT: BOBBA");
		// assertEquals("FETT", s[0]);
		// assertEquals("BOBBA", s[1]);
	}
}
