package com.javaranch.forums.dibs.jsf.util;

/**
 * Line-item submodel for Selection Shuttle control.
 * @author timh
 * @since Jul 8, 2015
 * Interacts with DibsSelectionConverter.
 */
public class SelectionModel implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long id;
	private String label;
	private int dibsCount;

	public SelectionModel(String value) {
		String[] vals = value.split("]");
		this.id = Long.parseLong(vals[0]);
		this.label = vals[1];
		this.dibsCount = Integer.parseInt(vals[2]);
	}

	/**
	 * Constructor from parameters.
	 * @param nodeId
	 * @param label
	 * @param dibsCount
	 */
	public SelectionModel(Long nodeId, String label, int dibsCount) {
		this.id = nodeId;
		this.label = label;
		this.dibsCount = dibsCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return id + "]" + label + "]" + dibsCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		SelectionModel other = (SelectionModel) obj;
		if (id != other.id) return false;
		return true;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @return the dibsCount
	 */
	public int getDibsCount() {
		return dibsCount;
	}

}
