package com.javaranch.forums.dibs.persistence.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.Query;
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
	@RelatedToVia
	@Fetch
	List<Dibs> dibsBidders = new ArrayList<Dibs>(5);
	
	/**
	 * @return the dibsBidders. Note that presently only their nodeIds are valid!
	 */
	@Query("START f=node({self}) MATCH (f)-[n:dibs_on]-(p:Person) return n ORDER BY n.priority")
	public List<Dibs> getDibsBidders() {
		return dibsBidders;
	}


	@Query("START f=node({self}) MATCH (f)-[n:dibs_on]-(p:Person) DELETE n")
	public void removeBidder(Person person) {
		person.removeDibs(this);
	}
	
	//--
	@RelatedTo(type=Dibs.CONNECT_MODERATES,direction = Direction.INCOMING)
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

	public void addModerator(Person p) {
		this.moderators.add(p);
	}

}
