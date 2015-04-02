package com.javaranch.forums.dibs.persistence.model;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;
import org.springframework.data.neo4j.annotation.RelatedToVia;

@NodeEntity
public class Forum implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Forum [nodeId=" + nodeId + ", name=" + name
				+ "]";
	}

	@GraphId
	public Long nodeId;

	@Indexed(unique=true)
	public String name;
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	public Forum() {
		this.name = "ACKForum";
	}
	
	public Forum(String name) {
		this.name = name;
	}
	
	//--
	@RelatedToVia(type="DIBS_ON")
	@Fetch Set<Dibs> dibsBidders;// = new HashSet<Person>(5);
	
	/**
	 * @return the dibsBidders
	 */
	public Set<Dibs> getDibsBidders() {
		return dibsBidders;
	}

//	/**
//	 * @param dibsBidders the dibsBidders to set
//	 */
//	public void setDibsBidders(Set<Person> dibsBidders) {
//		this.dibsBidders = dibsBidders;
//	}

	//--
	@RelatedTo(type="MODERATES")
	Set<Person> moderators;

	/**
	 * @return the moderators
	 */
	public Set<Person> getModerators() {
		return moderators;
	}

	/**
	 * @param moderators the moderators to set
	 */
	public void setModerators(Set<Person> moderators) {
		this.moderators = moderators;
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
		Forum other = (Forum) obj;
		if (nodeId == null) {
			if (other.nodeId != null) return false;
		} else if (!nodeId.equals(other.nodeId)) return false;
		return true;
	}


}
