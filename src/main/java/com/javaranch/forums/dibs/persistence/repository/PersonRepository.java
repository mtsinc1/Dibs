package com.javaranch.forums.dibs.persistence.repository;

import org.springframework.data.neo4j.repository.GraphRepository;
import com.javaranch.forums.dibs.persistence.model.Person;

public interface PersonRepository extends GraphRepository<Person> {}
