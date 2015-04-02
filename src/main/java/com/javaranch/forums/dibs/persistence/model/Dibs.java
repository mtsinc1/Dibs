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

	public Dibs(Person p, Forum f, int priority) {
		this.person = p;
		this.forum = f;
		this.priority = priority;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result =
				prime
						* result
						+ ((nodeId == null) ? 0 : nodeId
							.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Dibs other = (Dibs) obj;
		if (nodeId == null) {
			if (other.nodeId != null) return false;
		} else if (!nodeId.equals(other.nodeId)) return false;
		return true;
	}
}
