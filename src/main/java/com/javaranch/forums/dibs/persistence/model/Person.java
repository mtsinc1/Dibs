package com.javaranch.forums.dibs.persistence.model;

import java.util.Set;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedToVia;

@NodeEntity
public class Person implements java.io.Serializable {
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Person [nodeId=" + nodeId + ", name=" + name
				+ "]";
	}

	@GraphId
	public Long nodeId;

	@Indexed(unique=true)
	String name = "Person Name Not Set.";

	public Person(String name) {
		this.name = name;
	}

	public Person() {
	}

//	@RelatedTo(direction = Direction.BOTH, type = Dibs.CONNECT_MODERATES, elementClass = Forum.class)
//	Set<Forum> moderatesList;

	@RelatedToVia
	Set<Dibs> dibsList;// = new HashSet<Dibs>();

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

//	/**
//	 * @return the moderatesList
//	 */
//	public Set<Forum> getModeratesList() {
//		return moderatesList;
//	}
//
//	/**
//	 * @param moderatesList
//	 *            the moderatesList to set
//	 */
//	public void setModeratesList(Set<Forum> moderatesList) {
//		this.moderatesList = moderatesList;
//	}

	/**
	 * @return the dibsList
	 */
	public Iterable<Dibs> getDibsList() {
		return dibsList;
	}

//	/**
//	 * @param dibsList
//	 *            the dibsList to set
//	 */
//	public void setDibsList(Set<Dibs> dibsList) {
//		this.dibsList = dibsList;
//	}

	/*
	 * (non-Javadoc)
	 * 
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Person other = (Person) obj;
		if (nodeId == null) {
			if (other.nodeId != null) return false;
		} else if (!nodeId.equals(other.nodeId)) return false;
		return true;
	}

}
