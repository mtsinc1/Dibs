package com.javaranch.forums.dibs.persistence.model;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

@RelationshipEntity(type = Dibs.CONNECT_DIBS)
public class Dibs implements java.io.Serializable {
	
	// Relationship types
	public final static String CONNECT_DIBS = "dibs_on";
	public final static String CONNECT_MODERATES = "moderates";
	
	@GraphId
	Long nodeId;
	
	/**
	 * @return the nodeId
	 */
	public Long getNodeId() {
		return nodeId;
	}

	@Fetch @StartNode
	public Person person;
	@Fetch @EndNode
	public Forum forum;
	public int priority;
	
	/**
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * @param priority the priority to set
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}

	public Dibs() {
		
	}
	
	public Dibs(Person p, Forum f) {
		this.person = p;
		this.forum = f;
	}

	public Dibs(Person p, Forum f, int priority) {
		this.person = p;
		this.forum = f;
		this.priority = priority;
	}
	
	/**
	 * @return the person
	 */
	public Person getPerson() {
		return person;
	}

	/**
	 * @param person the person to set
	 */
	public void setPerson(Person person) {
		this.person = person;
	}

	/**
	 * @return the forum
	 */
	public Forum getForum() {
		return forum;
	}

	/**
	 * @param forum the forum to set
	 */
	public void setForum(Forum forum) {
		this.forum = forum;
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
