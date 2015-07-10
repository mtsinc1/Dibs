/**
 * Copyright (C) 2015, Tim Holloway
 *
 * Date written: Jul 8, 2015
 * Author: Tim Holloway <timh@mousetech.com>
 */
package com.javaranch.forums.dibs.jsf.util;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
/**
 * @author timh
 * @since Jul 8, 2015
 * Used with DibsSelectorBean.
 */
@FacesConverter("dibsSelectionConverter")
public class DibsSelectionConverter implements Converter {

	/* (non-Javadoc)
	 * @see javax.faces.convert.Converter#getAsObject(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.String)
	 */
	@Override
	public Object getAsObject(FacesContext context,
			UIComponent component, String value) {
		return new SelectionModel(value);
	}

	/* (non-Javadoc)
	 * @see javax.faces.convert.Converter#getAsString(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.Object)
	 */
	@Override
	public String getAsString(FacesContext context,
			UIComponent component, Object value) {
		SelectionModel sm = (SelectionModel) value;
		return sm.toString();
	}
}
