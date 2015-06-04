package com.javaranch.forums.dibs.backing;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseListener;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;

/**
 * This may or may not serve a useful purpose. I need to refresh
 * my memory.
 * 
 * @author timh
 * @since Jun 4, 2015
 */
public class Plisten  implements PhaseListener {

	       public PhaseId getPhaseId() { return PhaseId.RENDER_RESPONSE; }

	 

	       public void afterPhase(PhaseEvent event) { }

	 

	       public void beforePhase(PhaseEvent event) {

	          // we need to ask for the session early

	          FacesContext.getCurrentInstance().getExternalContext().getSession(true);

	       }

	}
