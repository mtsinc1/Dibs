package com.javaranch.forums.dibs.persistence.service;

/**
 * A Pair class for holding a name and (optional) associated
 * value.
 * 
 * @author timh
 * @since Jun 4, 2015
 */
public class NameValue {
	final private String name;
	final private Object value;

	/**
	 * No-value Constructor.
	 * 
	 * @param name
	 *            Name of this object.
	 */
	public NameValue(String name) {
		this.name = name;
		this.value = null;
	}

	public NameValue(String name, Object value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Test to see if we have a value
	 * 
	 * @return {@link } if there's a value paired with this name.
	 */
	public boolean hasValue() {
		return value != null;
	}

	/**
	 * @return Integer value.
	 * @throws ClassCastException
	 *             if the value isn't an Integer.
	 */
	public int getIntValue() {
		return (Integer) value;
	}

	/**
	 * @return String value.
	 * @throws ClassCastException
	 *             if the value isn't a String.
	 */
	public String getStringValue() {
		return (String) value;
	}
}
