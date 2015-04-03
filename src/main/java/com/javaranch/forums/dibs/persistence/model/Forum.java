package com.javaranch.forums.dibs.persistence.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.OrderBy;

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
	List<Dibs> dibsBidders = new ArrayList<Dibs>(5);
	
	/**
	 * @return the dibsBidders
	 */
	@Query("START f=node({self}) MATCH (f)-[:dibs_on]-(n:Person) return n ORDER BY n.priority")
	public List<Dibs> getDibsBidders() {
		System.out.println("DIB BIDDER COUNT="+ dibsBidders.size()+", name="+ name);
		if ( !dibsBidders.isEmpty()) {
			Person p = dibsBidders.get(0).getPerson();
			System.out.println("P="+p);
		}
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

}
