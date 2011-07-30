package org.qe4g.request

import groovy.lang.Closure;

import java.util.List
import java.util.Map

import org.qe4g.Event
import org.qe4g.request.Request.Builder;

import static org.qe4g.request.Window.State.*

class Pattern {

	/**
	 * Evaluation state
	 * @author Willow
	 */
	public enum State {
		/** The {@link Event} hasn't been evaluate because the Pattern has rejected the {@link Event}. The {@link #accept}*/
		REJECTED,
		/** The {@link Event} has been integrated and the pattern has been detected */
		DETECTED,
		/** The {@link Event} has been integrated because the evaluation by {@link Evaluator} is positive*/
		INTEGRATED,
		/** The {@link Event} has broken the pattern detection, the evaluation by {@link Evaluator} is negative*/ 
		BROKEN,
		/** The {@link Event} has broken the pattern detection in progress, but it has been integrated in new pattern detection */ 
		REBUILD
	}

	List<Evaluator> evaluators

	/**
	 * <p>Closure that define the acceptation strategy of published {@link Event}. If an {@link Event} is not accepted then this {@link Event} will not influence the pattern detection algorithm.
	 * <p>By default all {@link Event} are accepted.
	 */
	def accept = {true}

	/**
	 * <p>Closure used by the pattern detection algorithm to regroup {@link Event} around business problematic.
	 * <p>By default, no grouping are performed.
	 */
	def groupBy = {"groupBy"}

	/**
	 * {@link Window} identified by groupBy id.
	 */
	Map<String,Window> windows = [:]

	public Evaluation evaluate(Event event) {
		if(!accept(event)) {
			return new Evaluation(state:State.REJECTED,processedEvents:[event])
		}

		Window window = findWindow(event)
		org.qe4g.request.Window.State windowState = window.processEvent(event)
		List<Event> events = window.events;
		switch(windowState) {
			case CLOSED :
				windows.remove(window.id);
				return new Evaluation(state:State.DETECTED,processedEvents:events)
			case BROKEN :
				windows.remove(window.id);
				if(events.size() >= 2) {
					Evaluation evaluation = evaluate(event)
					if(evaluation.state == State.INTEGRATED) {
						return new Evaluation(state:State.REBUILD, processedEvents:events)
					}
				}
				return new Evaluation(state:State.BROKEN,processedEvents:events)
			case OPEN :
				return new Evaluation(state:State.INTEGRATED,processedEvents:events)
			default :
				throw new IllegalStateException()
		}
	}

	/**
	 * Créer pour faciliter les tests unitaires, pour mocker un objet {@link Window}
	 * @param id
	 * @param requestEventDefinitions
	 * @return
	 */
	protected Window createWindow(def id, def evaluators) {
		return new Window(id:id, evaluators:evaluators)
	}

	protected Window findWindow(Event event) {
		def groupById = groupBy(event)
		Window window = windows[groupById]
		if(!window) {
			window = createWindow(groupById, evaluators)
			windows[groupById] = window
		}
		return window
	}

	public static class Evaluation {
		State state
		List<Event> processedEvents
	}

	// ------------
	// BUILDER PART
	// ------------
	protected Pattern() {
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private Closure groupBy = null;
		private Closure accept = null;
		private List<Evaluator> evaluators = [];

		public Builder withGroupBy(Closure closure) {
			groupBy = closure;
			return this;
		}

		public Builder addEvaluator(Evaluator evaluator) {
			evaluators << evaluator;
			return this;
		}

		public Builder withAccept(Closure accept) {
			this.accept = accept
			return this;
		}

		public Pattern build() {
			Pattern pattern = new Pattern();
			if(groupBy != null) {
				pattern.groupBy = this.groupBy;
			}
			if(accept != null) {
				pattern.accept = this.accept;
			}
			pattern.evaluators = evaluators;
			return pattern;
		}
	}

}
