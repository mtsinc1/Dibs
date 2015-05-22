package com.javaranch.forums.dibs.backing;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.richfaces.event.FileUploadEvent;
import org.richfaces.model.UploadedFile;
import org.springframework.data.neo4j.conversion.Result;

import com.javaranch.forums.dibs.persistence.model.Dibs;
import com.javaranch.forums.dibs.persistence.model.Forum;
import com.javaranch.forums.dibs.persistence.model.Person;
import com.javaranch.forums.dibs.persistence.repository.ForumRepository;
import com.javaranch.forums.dibs.persistence.repository.PersonRepository;
import com.javaranch.forums.dibs.persistence.service.ConnectionService;
import com.javaranch.forums.dibs.persistence.service.DBLoader;
import com.javaranch.forums.dibs.persistence.service.RescanningLineNumberReader;

/**
 * Main control panel backing bean.
 * 
 * @author timh
 * @since Jun 28, 2012
 */

@ManagedBean
@SessionScoped
public class AdminMainBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/* Logger */

	final private static Logger log = Logger
		.getLogger(AdminMainBean.class);

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
	@ManagedProperty("#{connectionService}")
	private transient ConnectionService connectionService;

	/**
	 * @param service
	 *            the ConnectionService to set
	 */
	public void setConnectionService(ConnectionService service) {
		this.connectionService = service;
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
	public AdminMainBean() {
		// this.personList = buildPersonList();

	}

	@PostConstruct
	public void postConstruct() {
		this.personList = getPersonList();
	}

	
	//===
	/**
	 * Reset Personlist, causing next inquiry to construct
	 * a fresh one.
	 */
	private void clearPersonList() {
		this.personList = null;
	}
	
	public List<SelectItem> getPersonList() {
		if (this.personList == null) {
			this.personList = buildPersonList();
		}
		return this.personList;
	}

	private List<SelectItem> buildPersonList() {
		List<SelectItem> plist = new ArrayList<SelectItem>();

		// dbLoader.dumpLoad("Build new list");

		Transaction trans = graphDatabaseService.beginTx();
		Result<Person> persons = personRepository.findAll();
		for (Person person : persons) {
			plist.add(new SelectItem(person.nodeId, person
				.getName()));
		}
		trans.close();
		log.debug(plist.size() + " entries loaded.");

		// dbLoader.dumpLoad("Build completed.");

		return plist;
	}

	// --
	/**
	 * Action for staff selection
	 */
	public String doDibs() {
		this.dibsSelectorBean.beginEdit(this.getSelectPerson(),
			Dibs.CONNECT_DIBS);
		return "/dibselect";
	}

	/**
	 * Action for "Clear Database" button. Removes ALL records
	 * from the database.
	 * 
	 * @return <code>null</code> to re-display the admin page.
	 * @category action
	 */
	public String doClearDatabase() {
		try {
			this.connectionService.clearDatabase();
			this.clearPersonList();
			JSFUtils
				.addInfoMessage("Database has been cleared.");
		} catch (Exception e) {
			JSFUtils.addErrorMessage("Operation Failed.", e);
		}
		return null;
	}

	/**
	 * Action for "Reset Dibs" button. Removes ALL records Dibs
	 * from the database.
	 * 
	 * @return <code>null</code> to re-display the admin page.
	 * @category action
	 */
	public String doClearDibs() {
		try {
			this.connectionService.clearAllDibs();
			JSFUtils.addInfoMessage("Dibs have been cleared.");
		} catch (Exception e) {
			JSFUtils.addErrorMessage("Operation Failed.", e);
		}
		return null;
	}

	/**
	 * Action for "Restore Moderations" button. Removes ALL
	 * current Dibs and sets new Dibs based on current
	 * Moderators.
	 * 
	 * @return <code>null</code> to re-display the admin page.
	 * @category action
	 */
	public String doRestoreModerations() {
		try {
			this.connectionService.setDibsToModerators();
			JSFUtils
				.addInfoMessage("Dibs are now same as current Moderators.");
		} catch (Exception e) {
			JSFUtils.addErrorMessage("Operation Failed.", e);
		}
		return null;
	}

	// === Navigation ===

	public String goPeople() {
		return "personList";
	}

	public String goForums() {
		return "forumList";
	}

	@ManagedProperty("#{reportBean}")
	private ReportBean reportBean;

	/**
	 * @param reportBean
	 *            the reportBean to set
	 */
	public void setReportBean(ReportBean bean) {
		this.reportBean = bean;
	}

	public String goReport() {
		this.reportBean.init();
		return "/admin/report";
	}

	// $ SECT Export to YAML (results.jsp)

	/**
	 * Return people names as elements in a String array.
	 * 
	 * @return
	 */
	public String[] getPeople() {
		// https://jira.spring.io/browse/DATAGRAPH-531
		Transaction trans = graphDatabaseService.beginTx();
		Result<Person> people = this.personRepository.findAll();
		ArrayList<String> l = new ArrayList<String>(100);
		for (Person p : people) {
			l.add(p.getName());
		}
		trans.success();
		return l.toArray(new String[l.size()]);
	}

	/**
	 * Return Forum objects as elements in a String array.
	 * 
	 * @return
	 */
	public Forum[] getForums() {
		Transaction trans = graphDatabaseService.beginTx();
		Result<Forum> forums = this.forumRepository.findAll();
		ArrayList<Forum> l = new ArrayList<Forum>(100);
		for (Forum p : forums) {
			Set<Person> s = p.getModerators();
			for (Person z : s) {
				; // Force-fetch.
			}
			l.add(p);
		}
		trans.success();
		return l.toArray(new Forum[l.size()]);
	}

	// $ SECT Summary Report page
	@ManagedProperty("#{dbLoader}")
	private DBLoader dbLoader;

	/**
	 * @param dbLoader
	 *            the dbLoader to set
	 */
	public void setDbLoader(DBLoader dbLoader) {
		this.dbLoader = dbLoader;
	}

	public void listener(FileUploadEvent event) throws Exception {
		UploadedFile item = event.getUploadedFile();
		InputStream istr = item.getInputStream();
		InputStreamReader r0 = new InputStreamReader(istr);
		LineNumberReader lnr = new LineNumberReader(r0);
		RescanningLineNumberReader rdr =
				new RescanningLineNumberReader(lnr);

		dbLoader.load(rdr);

		// dbLoader.dumpLoad("Back in admin");

		this.personList = null;
		// dbLoader.dumpLoad("Model reset");

	}
}
