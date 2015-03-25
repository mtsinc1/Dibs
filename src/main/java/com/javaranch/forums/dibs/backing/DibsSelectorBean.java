package com.javaranch.forums.dibs.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import com.javaranch.forums.dibs.persistence.model.Forum;
import com.javaranch.forums.dibs.persistence.model.Person;
import com.javaranch.forums.dibs.persistence.repository.PersonRepository;
import com.javaranch.forums.dibs.persistence.service.ConnectionService;
import com.javaranch.forums.dibs.persistence.service.ForumService;


/**
 * Main control panel backing bean.
 * 
 * @author timh
 * @since Jun 28, 2012
 */

@ManagedBean
@SessionScoped
public class DibsSelectorBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * ID of the person being connected.
	 */
	private long personId;
	
	/**
	 * Details of person (name)
	 */
	private Person person;

	/**
	 * @return the person
	 */
	public Person getPerson() {
		return person;
	}

	/**
	 * Connection type. Dibs or Moderates.
	 */
	private String connectionType;
	// --
	@ManagedProperty("#{connectionService}")
	private transient ConnectionService connectionService;

	/**
	 * @param service
	 *            the service to setr
	 */
	public void setConnectionService(ConnectionService service) {
		this.connectionService = service;
	}

	// --
	@ManagedProperty("#{forumService}")
	private transient ForumService forumService;

	/**
	 * @param forumRepository
	 *            the forumRepository to set
	 * @category repository
	 */
	public void setForumService(ForumService service) {
		this.forumService = service;
	}

	// --
	@ManagedProperty("#{personRepository}")
	private transient PersonRepository personRepository;

	/**
	 * @return the forums
	 */
	public List<SelectItem> getForums() {
		return forums;
	}

	/**
	 * @param forums
	 *            the forums to set
	 */
	public void setForums(List<SelectItem> forums) {
		this.forums = forums;
	}

	/**
	 * @param forumRepository
	 *            the forumRepository to set
	 */
	public void setPersonRepository(
			PersonRepository personRepository) {
		this.personRepository = personRepository;
	}

	// --
	private List<SelectItem> forums;
	private Long[] choices = {};

	/**
	 * @param choices
	 *            the choices to set
	 */
	public void setChoices(Long[] choices) {
		this.choices = choices;
	}

	/**
	 * Default Constructor.
	 */
	public DibsSelectorBean() {

	}

	@PostConstruct
	public void postConfig() {
		// foo();

		this.forums = buildForumList();
		this.choices = new Long[] {};
	}

	public Long[] getChoices() {
		return this.choices;
	}

	private List<SelectItem> buildForumList() {
		List<SelectItem> forumList = new ArrayList<SelectItem>();
		List<Forum> forums = forumService.findAllForums();
		for (Forum f : forums) {
			SelectItem si = new SelectItem(f.nodeId, f.name);
			forumList.add(si);
		}
		return forumList;
	}

	public String doSave() {
		this.listChanged();
		return "selector";
	}

	/**
	 * Abort updates and return to primary screen.
	 * 
	 * @return
	 */
	public String doCancel() {
		return "main";
	}

	/**
	 * Commit the new Dibs connections
	 */
	public void listChanged() {
		// . Break all existing connections of this type.
		// . Construct new connections.
		// . Commit changes.
		this.forumService.clearDibs(this.personId);
		
		this.forumService.connectDibs(this.personId,
			idList(this.choices));

		JSFUtils.addInfoMessage("List has been updated.");
		System.out.println("+++++ LIST CHANGED  +++++");
	}

	// ====
	/**
	 * Setup for editing dibs connections
	 * 
	 * @param personId
	 *            ID of the Person Node to connect
	 * @param connectionType
	 *            "dibs" or "moderates"
	 * @see {@link #CONNECT_DIBS}, {@link #CONNECT_MODERATES}
	 */
	public void beginEdit(long personId, String connectionType) {
		this.personId = personId;
		this.connectionType = connectionType;

		Person p = this.personRepository.findOne(personId);
		this.person = p;
		Set<Forum> forums = p.getDibsList();
		final int fsize = forums.size();
		List<Long> fpicked = new ArrayList<Long>(fsize);
		for (Forum f : forums) {
			System.out.println(f.toString());
			fpicked.add(f.nodeId);
		}
		int ssz = fpicked.size();
		this.setChoices(fpicked.toArray(new Long[ssz]));
	}

	/**
	 * Convert List of IDs in string form to an array in native
	 * form.
	 * 
	 * @param stringIds
	 *            IDs to convert
	 * @return converted IDs
	 */
	private long[] idList(Long[] stringIds) {
		long[] ids = new long[stringIds.length];
		int i = 0;
		for (Long s : stringIds) {
			long id = s;
			ids[i++] = id;
		}
		return ids;
	}
}
