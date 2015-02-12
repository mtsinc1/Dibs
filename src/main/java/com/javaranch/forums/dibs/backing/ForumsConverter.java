package com.javaranch.forums.dibs.backing;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import com.javaranch.forums.dibs.persistence.model.Forum;


/**
 * @author timh
 */
@FacesConverter("ForumsConverter")
public class ForumsConverter implements Converter {
	//private ForumsParser forumsParser;

	public Object getAsObject(FacesContext facesContext,
			UIComponent component, String s) {
//		for (Forum Forum : getForumsParser(facesContext)
//			.getForumsList()) {
//			if (Forum.getName().equals(s)) {
//				return Forum;
//			}
//		}
		return null;
	}

	public String getAsString(FacesContext facesContext,
			UIComponent component, Object o) {
		if (o == null) return null;
		return ((Forum) o).name;
	}

//	public ForumsParser getForumsParser(
//			FacesContext facesContext) {
//		if (ForumsParser == null) {
//			ELContext elContext = facesContext.getELContext();
//			forumsParser =
//					(ForumsParser) elContext.getELResolver()
//						.getValue(elContext, null,
//							"ForumsParser");
//		}
//		return forumsParser;
//	}
}
