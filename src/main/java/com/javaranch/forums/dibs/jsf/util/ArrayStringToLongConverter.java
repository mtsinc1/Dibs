package com.javaranch.forums.dibs.jsf.util;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * This converter provides the HTML (text)-to-long array
 * conversion needed to support a SelectItemList where the values
 * are Long.
 * 
 * @author timh
 * @since Oct 19, 2012
 */
@FacesConverter("arrayStringToLongConverter")
public class ArrayStringToLongConverter implements Converter {
	/**
	 * converts the String representation of the key back to the
	 * Object
	 */
	public Object getAsObject(FacesContext context,
			UIComponent component, String value) {
		// will throw new IllegalArgumentException if it can't
		// parse.
		Long l = new Long(value); 
		return l; // KeyFactory.stringToKey( value );
	}

	/**
	 * converts the Key object into its String representation.
	 */
	public String getAsString(FacesContext context,
			UIComponent component, Object value) {
		Long v = (Long)value;
		return String.valueOf(v);
	}
}
