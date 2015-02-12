package com.javaranch.forums.dibs.backing;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseListener;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;

public class Plisten  implements PhaseListener {

	       public PhaseId getPhaseId() { return PhaseId.RENDER_RESPONSE; }

	 

	       public void afterPhase(PhaseEvent event) { }

	 

	       public void beforePhase(PhaseEvent event) {

	          // we need to ask for the session early

	          FacesContext.getCurrentInstance().getExternalContext().getSession(true);

	       }

	}
