package com.javaranch.forums.dibs.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

import com.javaranch.forums.dibs.jsf.util.SelectionModel;
import com.javaranch.forums.dibs.persistence.model.Dibs;
import com.javaranch.forums.dibs.persistence.model.Forum;
import com.javaranch.forums.dibs.persistence.model.Person;
import com.javaranch.forums.dibs.persistence.repository.PersonRepository;
import com.javaranch.forums.dibs.persistence.service.ConnectionService;
import com.javaranch.forums.dibs.persistence.service.ForumService;


/**
 * Backing bean for Dibs selections.
 * 
 * @author timh
 * @since Jun 28, 2012
 */

@ManagedBean
@SessionScoped
public class DibsSelectorBean implements Serializable {

	/* Logger */
	
	final private static Logger log =
			Logger.getLogger(DibsSelectorBean.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// ---
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
	
	//===
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

	
	//---
	private List<SelectionModel> selectedForumList
	= new ArrayList<SelectionModel>();
	private List<SelectionModel> unselectedForumList
	= new ArrayList<SelectionModel>();
	
	public List<SelectionModel> getSelectedForumList() {
		return selectedForumList;
	}

	/**
	 * @param selectedForumList the selectedForumList to set
	 */
	public void setSelectedForumList(
			List<SelectionModel> selectedForumList) {
		this.selectedForumList = selectedForumList;
	}

	/**
	 * @param unselectedForumList the unselectedForumList to set
	 */
	public void setUnselectedForumList(
			List<SelectionModel> unselectedForumList) {
		this.unselectedForumList = unselectedForumList;
	}

	public List<SelectionModel> getUnselectedForumList() {
		return unselectedForumList;
	}

	
	// --
	private List<SelectItem> forums;


	/**
	 * Default Constructor.
	 */
	public DibsSelectorBean() {

	}

	@PostConstruct
	public void postConfig() {
		// Deprecated. Functions are now in "begin" method
	}

	/**
	 * Build Forum selection list. Presently done for every
	 * beginEdit.
	 * 
	 * TODO: Move this to an APPLICATION scope backing bean
	 * and only update it where forums are added/removed from
	 * database! Note that this dibsCounts are approximate, due
	 * to potential multi-threading effects.
	 * 
	 * @return The list of Forum SelectItems
	 */
	private List<SelectionModel> buildForumList() {
		Transaction trans = this.graphDatabaseService.beginTx();
		List<Forum> forums = forumService.findAllForums();
		List<SelectionModel> forumList =
				new ArrayList<SelectionModel>(forums.size());
		for (Forum f : forums) {
			int fc = connectionService.getDibsCount(f);
			SelectionModel si = new SelectionModel(f.nodeId, f.name, fc);
			forumList.add(si);
		}
		trans.close();
		return forumList;
	}

	public String doSave() {
		this.saveChangedSelections();
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
	 * Commit the new Dibs connections. Break all existing
	 * connections of this type. Construct new connections.
	 * Commit changes.
	 */
	public void saveChangedSelections() {
		log.debug("Saving...");
		try (Transaction trans =
				this.graphDatabaseService.beginTx()) {
			log.debug("Saving...begin");
			Person person =
					this.personRepository.findOne(this.personId);
			log.debug("Saving...clear");

			this.connectionService.clearDibs(person);
			log.debug("Saving...update");

			this.connectionService.connect(this.personId,
				Dibs.CONNECT_DIBS, idList(this.getSelectedForumList()));
			log.debug("Saving...finishing");

			trans.success();

			JSFUtils.addInfoMessage("List has been updated.");
		}
		log.info("+++++ LIST CHANGED  +++++");
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
//		this.forums = buildForumList();
		//TODO this.choices = new Long[] {};

		this.personId = personId;
		this.connectionType = connectionType;
		
		this.selectedForumList = new ArrayList<SelectionModel>();
		this.unselectedForumList = new ArrayList<SelectionModel>();

		
//		this.unselectedForumList.add(new SelectionModel("101]fubar]4"));
		this.unselectedForumList.addAll(buildForumList());

		this.selectedForumList.clear();
		try (Transaction trans =
				this.graphDatabaseService.beginTx()) {
			Person p = this.personRepository.findOne(personId);
			this.person = p;
			List<Dibs> forumdibs =
					this.connectionService.findDibsOn(p);

			if (log.isTraceEnabled()) {
				log.trace("BEGIN EDIT FOR " + p.getName());
			}

			for (Dibs dibs : forumdibs) {
				if (log.isTraceEnabled()) {
					log.trace(dibs.toString());
				}
				Forum fm = dibs.getForum(); 
				SelectionModel sm = new SelectionModel(fm.nodeId, fm.getName(), 123);
				selectedForumList.add(sm);
			}
			trans.success();
		}
	}
	
	/**
	 * Convert List of IDs in string form to an array in native
	 * form.
	 * 
	 * @param list
	 *            IDs to convert
	 * @return converted IDs
	 */
	private long[] idList(List<SelectionModel> list) {
		long[] ids = new long[list.size()];
		int i = 0;
		for (SelectionModel s : list) {
			long id = s.getId();
			ids[i++] = id;
		}
		return ids;
	}
}
