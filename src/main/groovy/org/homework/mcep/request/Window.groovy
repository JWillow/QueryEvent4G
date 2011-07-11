package org.homework.mcep.request

import static org.homework.mcep.request.Window.State.*

import org.homework.mcep.Event;

class Window {

	public enum State {
		CLOSED,OPEN,BROKEN
	}
	/** Identifiant de la fenêtre */
	def id

	/** Initialisé à l'état {@link State#TO_CONTINUE} */
	def state = OPEN

	/** Evénements matchant les requestEventDefinitions */
	List<Event> proceedEvents = []

	/** Définition des événements à matcher */
	List<EventDefinition> eventDefinitions = []

	/**
	 * Traitement d'un événement, si la fenêtre est dans un autre état que {@link State#OPEN} alors l'événement n'est pas traité, l'état de la fenêtre n'est pas changé
	 * @param event
	 * @return Retourne l'état courant de la fenêtre après traitement
	 */
	public State processEvent(Event event) {
		if(state != OPEN) {
			return state
		}
		proceedEvents << event
		if(!eventDefinitions[proceedEvents.size() - 1].evaluate(proceedEvents)) {
			state = BROKEN
			return state
		}
		if(proceedEvents.size() == eventDefinitions.size()) {
			state = CLOSED
		} else {
			state = OPEN
		}
		return state
	}
}
