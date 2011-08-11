package org.qe4g.request

import static org.qe4g.request.Window.State.*

import org.qe4g.Event;
import org.qe4g.request.Evaluator.Response;

class Window {

	public enum State {
		CLOSED,OPEN,BROKEN
	}

	/** Initialis� � l'�tat {@link State#TO_CONTINUE} */
	def state = OPEN

	/** Ev�nements matchant les requestEventDefinitions */
	List<Event> events = []

	/** D�finition des �v�nements � matcher */
	List<Evaluator> evaluators = []

	int indexNextEvaluatorToUse = 0;

	Map<String,Object> contextForEvaluator = [:]

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
