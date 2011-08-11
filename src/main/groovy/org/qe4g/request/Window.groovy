package org.qe4g.request

import static org.qe4g.request.Window.State.*

import org.qe4g.Event;
import org.qe4g.request.Evaluator.Response;

class Window {

	public enum State {
		CLOSED,OPEN,BROKEN
	}

	/** Initialisé à l'état {@link State#TO_CONTINUE} */
	def state = OPEN

	/** Evénements matchant les requestEventDefinitions */
	List<Event> events = []

	/** Définition des événements à matcher */
	List<Evaluator> evaluators = []

	int indexNextEvaluatorToUse = 0;

	Map<String,Object> contextForEvaluator = [:]

	/**
	 * Traitement d'un événement, si la fenêtre est dans un autre état que {@link State#OPEN} alors l'événement n'est pas traité, l'état de la fenêtre n'est pas changé
	 * @param event
	 * @return Retourne l'état courant de la fenêtre après traitement
	 */
	public State processEvent(Event event) {
		if(state != OPEN) {
			return state
		}
		events << event
		switch(applyEvaluator(event,indexNextEvaluatorToUse)) {
			case Response.CONTINUE_WITH_NEXT_EVALUATOR:
			case Response.OK :
				if(indexNextEvaluatorToUse == evaluators.size()) {
					state = CLOSED
					break
				}
			case Response.OK_BUT_KEEP_ME:
				state = OPEN
				break
			case Response.KO :
				state = BROKEN
				break
			default:
				throw new IllegalStateException("Not supported !")
		}
		return state
	}

	private Response applyEvaluator(Event event, int indexEvaluatorToUse) {
		if(indexEvaluatorToUse == evaluators.size()) {
			indexNextEvaluatorToUse = indexEvaluatorToUse
			return Response.OK
		}
		Response response = evaluators[indexEvaluatorToUse].evaluate(contextForEvaluator, events)
		switch(response) {
			case Response.CONTINUE_WITH_NEXT_EVALUATOR:
				return applyEvaluator(event, indexEvaluatorToUse + 1)
				break;

			case Response.OK_BUT_KEEP_ME:
				indexNextEvaluatorToUse = indexEvaluatorToUse
				break;

			case Response.OK :
				indexNextEvaluatorToUse = indexEvaluatorToUse + 1
			case Response.KO :
				break;
		}				
		return response;
	}
}
