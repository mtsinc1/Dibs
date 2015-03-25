package com.javaranch.forums.dibs.backing;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import com.javaranch.forums.dibs.persistence.service.DBLoader;


/**
 * Database setup. Transforms flat files into Neo database
 * 
 * @author timh
 * @since Jun 4, 2014
 */
@ManagedBean
@ViewScoped
public class Optimus {

	@ManagedProperty("#{dbLoader}")
	DBLoader dBLoader;

	/**
	 * @param dBLoader
	 *            the dBLoader to set
	 */
	public void setdBLoader(DBLoader dBLoader) {
		this.dBLoader = dBLoader;
	}

	/**
	 * Initialize the database from flat-file definitions. And
	 * yes, it's a silly joke.
	 */
	public void prime() {
		loadPersons();
		loadForums();
	}

	private void loadPersons() {
		//dBLoader.loadPersons();
		JSFUtils.addInfoMessage("Persons have been loaded.");
	}

	public void loadForums() {
		//dBLoader.loadForumList();
		JSFUtils.addInfoMessage("Forums have been loaded.");
	}
}
