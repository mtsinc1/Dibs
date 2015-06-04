package com.javaranch.forums.dibs.backing;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.model.ListDataModel;

import com.javaranch.forums.dibs.persistence.model.Dibs;
import com.javaranch.forums.dibs.persistence.model.Forum;
import com.javaranch.forums.dibs.persistence.model.Person;
import com.javaranch.forums.dibs.persistence.service.ForumService;

/**
 * Backing bean for report.
 * 
 * @author timh
 * @since Jun 5, 2014
 */
@ManagedBean
@SessionScoped
public class ReportBean {

	// ===
	private static final int EXPECTED_FORUM_COUNT = 45;
	// ===
	/**
	 * Persistency service for Forum
	 */

	@ManagedProperty(value = "#{forumService}")
	private transient ForumService forumService;

	public void setForumService(ForumService service) {
		this.forumService = service;
	}

	// ===
	/**
	 * Default Constructor.
	 */
	public ReportBean() {
		setName("XYZZY");
	}

	/**
	 * Setup for new display. Reset models.
	 */
	public void init() {
		this.claimedListModel = null;
		this.dibsMakerList = null;
		this.unclaimed = null;
	}

	private String name;

	/**
	 * @return the name
	 * @category uimodel
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
	 * @category uimodel
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
		List<Forum> flist = forumService.findAllUnclaimed();
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
	private ListDataModel<ClaimedForum> claimedListModel;

	/**
	 * @return the claimedList
	 */
	public List<ClaimedForum> getClaimedList() {
		ListDataModel<ClaimedForum> model =
				getClaimedListModel();
		@SuppressWarnings("unchecked")
		List<ClaimedForum> list =
				(List<ClaimedForum>) model.getWrappedData();
		return list;
	}

	/**
	 * @return the claimedList DataModel
	 * @category uimodel
	 */
	public ListDataModel<ClaimedForum> getClaimedListModel() {
		if (claimedListModel == null) {
			claimedListModel = buildClaimedForumList();
		}
		return claimedListModel;
	}

	private ListDataModel<ClaimedForum> buildClaimedForumList() {
		List<ClaimedForum> list = loadClaimedForumList();
		ListDataModel<ClaimedForum> model =
				new ListDataModel<ClaimedForum>(list);
		return model;
	}

	private List<ClaimedForum> loadClaimedForumList() {
		List<Forum> flist = this.forumService.findAllClaimed();
		ArrayList<ClaimedForum> olist =
				new ArrayList<ClaimedForum>();
		for (Forum forum : flist) {
			ClaimedForum cf = new ClaimedForum(forum.name);
			cf.setNumModerators(forum.getNumModerators());
			List<Dibs> dibs = forum.getDibsBidders();
			for (Dibs d : dibs) {
				cf.dibsList.add(d.getPerson());
			}
			olist.add(cf);
		}
		return olist;
	}

	public class ClaimedForum {
		private String name;
		private List<Person> dibsList = null;
		private int numModerators = 1;

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
		 * @return the numModerators
		 */
		public int getNumModerators() {
			return numModerators;
		}

		/**
		 * @param numModerators the numModerators to set
		 */
		public void setNumModerators(int numModerators) {
			this.numModerators = numModerators;
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

	private ListDataModel<ModerationMaker> dibsMakerList;

	/**
	 * @return the dibsMakerList
	 * @category uimodel
	 */
	public ListDataModel<ModerationMaker> getDibsMakerModel() {
		if (dibsMakerList == null) {
			dibsMakerList = buildModeratorMakerList();
		}
		return dibsMakerList;
	}

	private ListDataModel<ModerationMaker> buildModeratorMakerList() {
		List<ModerationMaker> list = loadDibsMakerList();
		ListDataModel<ModerationMaker> model =
				new ListDataModel<ModerationMaker>(list);
		return model;
	}

	private List<ModerationMaker> loadDibsMakerList() {
		List<Person> flist = this.forumService.findAllPersons();
		ArrayList<ModerationMaker> olist =
				new ArrayList<ModerationMaker>();
		for (Person person : flist) {
			ModerationMaker dm =
					new ModerationMaker(person.getName(), "");
			olist.add(dm);

			StringBuilder sb = new StringBuilder(256);
			String dlm = "";
			for (Dibs dibOn : person.getDibsList()) {
				sb.append(dlm);
				sb.append(dibOn.getForum().getName());
				dlm = ", ";
			}
			if (sb.length() == 0) {
				sb.append("No Forums");
			}
			dm.setForumNames(sb.toString());
		}
		return olist;
	}

	public class ModerationMaker {
		private String name;
		private String forumNames;

		/**
		 * Constructor.
		 * 
		 * @param name
		 * @param forumNames
		 */
		public ModerationMaker(String name, String forumNames) {
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

	// $SECT Model
	public List<DibsMaker> dibsModel;

	/**
	 * @return the dibsModel
	 */
	public List<DibsMaker> getDibsModel() {
		if (this.dibsModel == null) {
			this.dibsModel = buildDibsModel();
		}
		return this.dibsModel;
	}

	/**
	 * Construct the output model for Dibs. Used by export.
	 * @return List of Dibs as a model.
	 */
	private List<DibsMaker> buildDibsModel() {
		try {
			List<Forum> forums =
					this.forumService.findAllForums();
			ArrayList<DibsMaker> list =
					new ArrayList<DibsMaker>(
							EXPECTED_FORUM_COUNT);
			for (Forum forum : forums) {
				DibsMaker maker = new DibsMaker();
				maker.setForumName(forum.getName());
				List<Dibs> dlist = forum.getDibsBidders();
				for (Dibs dibs : dlist) {
					maker.getDibs().add(dibs);
				}
				list.add(maker);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<DibsMaker>(0);
		}
	}

	// ===
	public class DibsMaker {
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "DibsMaker [forumName=" + forumName
					+ ", dibs=" + dibs + "]";
		}

		private String forumName;

		/**
		 * @return the forumName
		 */
		public String getForumName() {
			return forumName;
		}

		/**
		 * @param forumName
		 *            the forumName to set
		 */
		public void setForumName(String forumName) {
			this.forumName = forumName;
		}

		// --
		private List<Dibs> dibs = new ArrayList<Dibs>(3);

		/**
		 * @return the dibs
		 */
		public List<Dibs> getDibs() {
			return dibs;
		}

		/**
		 * @param dibs
		 *            the dibs to set
		 */
		public void setDibs(List<Dibs> dibs) {
			this.dibs = dibs;
		}
	}
	
	//$SECT Export
	/**
	 * Non-cached retrieval of current Person inventory
	 * via the ForumService (there is no PersonService right now).
	 * @return all Person's
	 */
	public List<Person> getPeople() {
		return this.forumService.findAllPersons();
	}
	
	/**
	 * Non-cached retrieval of current Forum inventory
	 * @return all Forum's
	 */
	public List<Forum> getForums() {
		return this.forumService.findAllForums();
	}
	
	/**
	 * Non-cached retrieval of current Moderates relations.
	 * @return all Moderations.
	 */
	public List<ClaimedForum> getModeratesList() {
		List<Forum> flist = this.forumService.findAllClaimed();
		ArrayList<ClaimedForum> olist =
				new ArrayList<ClaimedForum>();
		for (Forum forum : flist) {
			List<Person> persons =
					this.forumService.findModerators(forum);
			if (persons.isEmpty()) {
				continue;
			}
			ClaimedForum cf = new ClaimedForum(forum.name);
			for (Person p : persons) {
				cf.dibsList.add(p);
			}
			olist.add(cf);
		}
		return olist;
	}
}
