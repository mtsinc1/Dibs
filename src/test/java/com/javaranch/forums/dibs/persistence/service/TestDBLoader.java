package com.javaranch.forums.dibs.persistence.service;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.test.TestGraphDatabaseFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.javaranch.forums.dibs.persistence.repository.PersonRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/testApplicationContext.xml"})
public class TestDBLoader {

	@Autowired
	private DBLoader dbLoader;
	
	@Autowired
	private PersonRepository personRepository;
	
	@Test
	public void test1() throws IOException {
		System.out.println("LOADER="+dbLoader);
		
		long npersons = personRepository.count();
		assertEquals(0, npersons);
		
		File f = new File("src/test/resources/test.yml");
		System.out.println("FILE='"+f.getAbsolutePath()+"'");
		FileReader frdr = new FileReader(f);
		LineNumberReader lnr = new LineNumberReader(frdr);
		RescanningLineNumberReader rdr = new RescanningLineNumberReader(lnr);
		
		dbLoader.load(rdr);
			
		npersons = personRepository.count();
		assertEquals(3, npersons);
		
		
		int i23 = 23;
		i23++;
	}
}
