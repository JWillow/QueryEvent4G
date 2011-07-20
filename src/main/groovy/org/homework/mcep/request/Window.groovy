package org.homework.mcep.request

import static org.homework.mcep.request.Window.State.*

import org.homework.mcep.Event;

class Window {

	public enum State {
		CLOSED,OPEN,BROKEN
	}
	/** Identifiant de la fen�tre */
	def id

	/** Initialis� � l'�tat {@link State#TO_CONTINUE} */
	def state = OPEN

	/** Ev�nements matchant les requestEventDefinitions */
	List<Event> events = []

	/** D�finition des �v�nements � matcher */
	List<Evaluator> evaluators = []

	/**
	 * Traitement d'un �v�nement, si la fen�tre est dans un autre �tat que {@link State#OPEN} alors l'�v�nement n'est pas trait�, l'�tat de la fen�tre n'est pas chang�
	 * @param event
	 * @return Retourne l'�tat courant de la fen�tre apr�s traitement
	 */
	public State processEvent(Event event) {
		if(state != OPEN) {
			return state
		}
		events << event
		if(!evaluators[events.size() - 1].evaluate(events)) {
			state = BROKEN
			return state
		}
		if(events.size() == evaluators.size()) {
			state = CLOSED
		} else {
			state = OPEN
		}
		return state
	}
}
