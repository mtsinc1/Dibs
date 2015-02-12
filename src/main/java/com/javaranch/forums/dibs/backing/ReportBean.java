package com.javaranch.forums.dibs.backing;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.ListDataModel;

import com.javaranch.forums.dibs.persistence.model.Forum;
import com.javaranch.forums.dibs.persistence.model.Person;
import com.javaranch.forums.dibs.persistence.repository.ForumRepository;
import com.javaranch.forums.dibs.persistence.service.ForumService;

/**
 * Backing bean for report
 * 
 * @author timh
 * @since Jun 5, 2014
 */
@ManagedBean
@ViewScoped
public class ReportBean {

	// ===
	/**
	 * Persistency service for Forum
	 */

	@ManagedProperty(value = "#{forumService}")
	private transient ForumService forumService;

	public void setForumService(ForumService service) {
		this.forumService = service;
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

	// ===
	/**
	 * Default Constructor.
	 */
	public ReportBean() {
		setName("XYZZY");
	}

	private String name;

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

	// --
	private String[] unclaimed;

	/**
	 * @return collection of unclaimed forum names.
	 */
	public String[] getUnclaimed() {
		if (unclaimed == null) {
			unclaimed = loadUnclaimed();
		}
		return unclaimed;
	}

	/**
	 * Find all unclaimed forums and return an array of their
	 * names.
	 * 
	 * @return Array for unclaimed forum names.
	 */
	private String[] loadUnclaimed() {
		List<Forum> flist = forumRepository.findAllUnclaimed();
		int fsize = flist.size();
		// System.out.println(fsize);
		String[] u = new String[fsize];
		int i = 0;
		for (Forum f : flist) {
			u[i++] = f.name;
		}
		return u;
	}

	// ---
	private ListDataModel<ClaimedForum> claimedList;

	/**
	 * @return the claimedList
	 */
	public ListDataModel<ClaimedForum> getClaimedList() {
		if (claimedList == null) {
			claimedList = buildClaimedForumList();
		}
		return claimedList;
	}

	private ListDataModel<ClaimedForum> buildClaimedForumList() {
		List<ClaimedForum> list = loadClaimedForumList();
		ListDataModel<ClaimedForum> model =
				new ListDataModel<ClaimedForum>(list);
		return model;
	}

	private List<ClaimedForum> loadClaimedForumList() {
		List<Forum> flist =
				this.forumRepository.findAllClaimed();
		ArrayList<ClaimedForum> olist =
				new ArrayList<ClaimedForum>();
		for (Forum forum : flist) {
			ClaimedForum cf = new ClaimedForum(forum.name);
			Set<Person> dl = forum.getDibsBidders();
			for (Person person : dl) {
				cf.dibsList.add(person);
			}
			olist.add(cf);
		}
		return olist;
	}

	public class ClaimedForum {
		private String name;
		private List<Person> dibsList = null;

		/**
		 * Constructor.
		 * 
		 * @param name
		 */
		public ClaimedForum(String name) {
			this.name = name;
			this.dibsList = new ArrayList<Person>();
		}

		/**
		 * Constructor.
		 * 
		 * @param name
		 * @param dibsList
		 */
		public ClaimedForum(String name, List<Person> dibsList) {
			this.name = name;
			this.dibsList = dibsList;
		}

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

		/**
		 * @return the dibsList
		 */
		public List<Person> getDibsList() {
			return dibsList;
		}

		/**
		 * @param dibsList
		 *            the dibsList to set
		 */
		public void setDibsList(List<Person> dibsList) {
			this.dibsList = dibsList;
		}
	}

	// $SECT Sub-report by person

	private ListDataModel<DibsMaker> dibsMakerList;

	/**
	 * @return the dibsMakerList
	 */
	public ListDataModel<DibsMaker> getDibsMakerList() {
		if (dibsMakerList == null) {
			dibsMakerList = buildDibsMakerList();
		}
		return dibsMakerList;
	}

	private ListDataModel<DibsMaker> buildDibsMakerList() {
		List<DibsMaker> list = loadDibsMakerList();
		ListDataModel<DibsMaker> model =
				new ListDataModel<DibsMaker>(list);
		return model;
	}

	private List<DibsMaker> loadDibsMakerList() {
		List<Person> flist =
				this.forumService.findAllPersons();
		ArrayList<DibsMaker> olist = new ArrayList<DibsMaker>();
		for (Person person : flist) {
			DibsMaker dm = new DibsMaker(person.getName(), "");
			olist.add(dm);

			Set<Forum> dibOn = person.getDibsList();
			String d = dm.getForumNames();
			if (dibOn.isEmpty()) {
				d = "No Forums";
			} else {
				for (Forum forum : dibOn) {
					if (d.length() != 0) {
						d += ", ";
					}
					d += forum.getName();
				}
				dm.setForumNames(d);
			}
		}
		return olist;
	}

	public class DibsMaker {
		private String name;
		private String forumNames;

		/**
		 * Constructor.
		 * 
		 * @param name
		 * @param forumNames
		 */
		public DibsMaker(String name, String forumNames) {
			this.name = name;
			this.forumNames = forumNames;
		}

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

		/**
		 * @return the forumNames
		 */
		public String getForumNames() {
			return forumNames;
		}

		/**
		 * @param forumNames
		 *            the forumNames to set
		 */
		public void setForumNames(String forumNames) {
			this.forumNames = forumNames;
		}

	}
}
