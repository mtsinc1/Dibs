package com.javaranch.forums.dibs.persistence.model;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

@RelationshipEntity(type = "DIBS_ON")
public class Dibs {
	
	// Relationship types
	public final static String CONNECT_DIBS = "dibs_on";
	public final static String CONNECT_MODERATES = "moderates";
	
	@GraphId
	Long nodeId;
	
	@StartNode
	Person person;
	@EndNode
	Forum forum;
	int priority;
	
	public Dibs() {
		
	}
	
	public Dibs(Person p, Forum f) {
		this.person = p;
		this.forum = f;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Dibs [nodeId=" + nodeId + ", person=" + person
				+ ", forum=" + forum + ", priority=" + priority
				+ "]";
	}
}
