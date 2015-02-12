package com.javaranch.forums.dibs.backing;

import java.io.InputStream;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

public class JSFUtils {

	private static ExternalContext getExternalContext() {
		FacesContext facesContext =
				FacesContext.getCurrentInstance();
		ExternalContext externalContext =
				facesContext.getExternalContext();
		return externalContext;
	}

	/**
	 * Obtain resource as Stream. The caller must close this
	 * stream after use.
	 * 
	 * @param pathName
	 *            Path of the resource. For example,
	 *            "/WEB-INF/classes/xys.properties".
	 * @return InputStream or <code>null</code>, if no such
	 *         resource exists.
	 */
	public static InputStream getResourceStream(String pathName) {
		InputStream response =
				getExternalContext().getResourceAsStream(
					pathName);
		return response;
	}

	// ===
	/**
	 * Post an info-level message to the FacesContext where the
	 * &lt;h:messages&gt; tag can display it.
	 * 
	 * @param string
	 *            Message text
	 */
	public static void addInfoMessage(String string) {
		FacesMessage message =
				new FacesMessage(FacesMessage.SEVERITY_INFO,
						string, string);
		FacesContext.getCurrentInstance().addMessage(null,
			message);
	}

	/**
	 * Post an error-level message to the FacesContext where the
	 * &lt;h:messages&gt; tag can display it.
	 * 
	 * @param string
	 *            Message text
	 */
	public static void addErrorMessage(String string) {
		FacesMessage message =
				new FacesMessage(FacesMessage.SEVERITY_ERROR,
						string, string);
		FacesContext.getCurrentInstance().addMessage(null,
			message);
	}

}
