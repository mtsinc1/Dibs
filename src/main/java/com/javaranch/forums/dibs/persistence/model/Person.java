package com.javaranch.forums.dibs.persistence.model;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.annotation.RelatedToVia;

@NodeEntity
public class Person implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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

	@RelatedToVia(type=Dibs.CONNECT_DIBS)
	Set<Dibs> dibsList = new HashSet<Dibs>();

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

	//---
	/**
	 * A rookie is a Person who has never previously moderated
	 * a Forum. Such persons get first crack at forums when
	 * Dibs are processed.
	 */
	public boolean rookie;
	
	/**
	 * @return the rookie status
	 */
	public boolean isRookie() {
		return rookie;
	}

	/**
	 * @param rookie status 
	 */
	public void setRookie(boolean rookie) {
		this.rookie = rookie;
	}
	
	// ===
	/**
	 * Place Dibs on a forum.
	 * @param f Forum to claim.
	 * @param priority
	 * @return Constructed Dibs entry
	 */
	public Dibs dibsOn(Forum f, int priority) {
		Dibs d = new Dibs(this, f, priority);
		this.dibsList.add(d);
		return d;
	}

	/**
	 * @return the dibsList
	 */
	public Set<Dibs> getDibsList() {
		return dibsList;
	}

	public void removeDibs(Forum forum) {
		this.dibsList.remove(forum);
	}

	@Query("START f=node({self}) MATCH (f)-[n:dibs_on]->(:Forum) DELETE n")
	public void clearDibs() {
		this.dibsList.clear();
	}

	/**
	 * Add, but do not persist a Dibs entry.
	 * @param dibs Entry to add. Does NOT adjust priority!
	 */
	public void addDibs(Dibs dibs) {
		this.dibsList.add(dibs);
	}
}
