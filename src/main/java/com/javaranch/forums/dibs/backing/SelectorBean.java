package com.javaranch.forums.dibs.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.springframework.data.neo4j.conversion.EndResult;

import com.javaranch.forums.dibs.persistence.model.Dibs;
import com.javaranch.forums.dibs.persistence.model.Person;
import com.javaranch.forums.dibs.persistence.repository.ForumRepository;
import com.javaranch.forums.dibs.persistence.repository.PersonRepository;
import com.javaranch.forums.dibs.persistence.service.DBLoader;


/**
 * Main control panel backing bean.
 * 
 * @author timh
 * @since Jun 28, 2012
 */

@ManagedBean
@SessionScoped
public class SelectorBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// --
	@ManagedProperty("#{graphDatabaseService}")
	private transient GraphDatabaseService graphDatabaseService;

	/**
	 * @param forumRepository
	 *            the forumRepository to set
	 */
	public void setGraphDatabaseService(
			GraphDatabaseService service) {
		this.graphDatabaseService = service;
	}

	// --
	@ManagedProperty("#{dibsSelectorBean}")
	private DibsSelectorBean dibsSelectorBean;

	/**
	 * @param dibsSelectorBean
	 *            the dibsSelectorBean to set
	 */
	public void setDibsSelectorBean(
			DibsSelectorBean dibsSelectorBean) {
		this.dibsSelectorBean = dibsSelectorBean;
	}

	// --
	@ManagedProperty("#{forumRepository}")
	private transient ForumRepository forumRepository;

	/**
	 * @param forumRepository
	 *            the forumRepository to set
	 */
	public void setForumRepository(
			ForumRepository forumRepository) {
		this.forumRepository = forumRepository;
	}

	// --
	@ManagedProperty(value = "#{personRepository}")
	private transient PersonRepository personRepository;

	/**
	 * @param forumRepository
	 *            the forumRepository to set
	 */
	public void setPersonRepository(
			PersonRepository personRepository) {
		this.personRepository = personRepository;
	}


	
	// --
	private List<SelectItem> personList;

	// --
	private Long selectPerson;

	/**
	 * @return the selectPerson
	 */
	public Long getSelectPerson() {
		return selectPerson;
	}

	/**
	 * @param selectPerson
	 *            the selectPerson to set
	 */
	public void setSelectPerson(Long selectPerson) {
		this.selectPerson = selectPerson;
	}

	/**
	 * Default Constructor.
	 */
	public SelectorBean() {
		// this.personList = buildPersonList();

	}

	@PostConstruct
	public void postConstruct() {
		this.personList = getPersonList();
	}

	public List<SelectItem> getPersonList() {
		if (this.personList == null) {
			this.personList = buildPersonList();
		}
		return this.personList;
	}

	private List<SelectItem> buildPersonList() {
		List<SelectItem> plist = new ArrayList<SelectItem>();

		Transaction trans = graphDatabaseService.beginTx();
		EndResult<Person> persons = personRepository.findAll();
		for (Person person : persons) {
			plist.add(new SelectItem(person.nodeId, person
				.getName()));
		}
		trans.success();
		return plist;
	}

	// --
	/**
	 * Action for staff selection
	 */
	public String doDibs() {
		this.dibsSelectorBean.beginEdit(this.getSelectPerson(),
			Dibs.CONNECT_DIBS);
		return "dibselect";
	}

	public String goPeople() {
		return "personList";
	}

	public String goForums() {
		return "forumList";
	}

	public String goReport() {
		return "forumList";
	}

	@ManagedProperty("#{dbLoader}")
	private DBLoader dbLoader;
	
	/**
	 * @param dbLoader the dbLoader to set
	 */
	public void setDbLoader(DBLoader dbLoader) {
		this.dbLoader = dbLoader;
	}

	public String goLoad() {
		//JSFUtils.
//		dbLoader.loadPersons();
//		dbLoader.loadForumList();
		this.personList = null;
		return null;
	}
}
