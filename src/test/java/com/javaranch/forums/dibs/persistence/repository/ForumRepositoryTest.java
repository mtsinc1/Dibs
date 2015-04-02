package com.javaranch.forums.dibs.persistence.repository;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.javaranch.forums.dibs.persistence.model.Forum;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/testApplicationContext.xml"})
public class ForumRepositoryTest {

	@Autowired
	private ForumRepository forumRepository;
	
	@Test
	public void testFindByName() {
		Forum node = new Forum("Test forum");
		forumRepository.save(node);
		
		Forum n1 = forumRepository.findByName("Test forum");
		assertNotNull(n1);

		// Note: "save" doesn't create new node
		// if unique index constraint exists.
		node = new Forum("Test forum");
		forumRepository.save(node);
		
		// Uniqueness test. Throws NoSuchElement if not unique.
		n1 = forumRepository.findByName("Test forum");
		assertNotNull(n1);
		
		n1 = forumRepository.findByName("Any Forum");
		assertNull(n1);

	}
}
