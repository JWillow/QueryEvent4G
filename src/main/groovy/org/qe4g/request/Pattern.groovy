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
		/** The {@link Event} hasn't been evaluate because the Pattern has rejected the {@link Event}. Two cause : <ul><li>{@link #accept}</li><li>or the event is not linked</li></ul>*/
		REJECTED,
		/** The {@link Event} has been integrated and the pattern has been detected */
		DETECTED,
		/** The {@link Event} has been integrated because the evaluation by {@link Evaluator} is positive*/
		INTEGRATED,
		/** The {@link Event} has broken the pattern detection, the evaluation by {@link Evaluator} is negative*/ 
		BROKEN,
	}

	List<Evaluator> evaluators

	protected def createWindow = { return new Window(evaluators:evaluators)}

	/**
	 * <p>Closure that define the acceptation strategy of published {@link Event}. If an {@link Event} is not accepted then this {@link Event} will not influence the pattern detection algorithm.
	 * <p>By default all {@link Event} are accepted.
	 */
	def accept = {true}

	Collection<Window> windows = [];

	private Evaluation evaluate(Event event, Window window) {
		org.qe4g.request.Window.State windowState = window.processEvent(event)
		List<Event> events = window.events;
		switch(windowState) {
			case NOT_LINKED :
				return new Evaluation(state:State.REJECTED,processedEvents:events)
			case CLOSED :
				windows.remove(window);
				return new Evaluation(state:State.DETECTED,processedEvents:events)
			case BROKEN :
				windows.remove(window);
				return new Evaluation(state:State.BROKEN,processedEvents:events)
			case OPEN :
				return new Evaluation(state:State.INTEGRATED,processedEvents:events)
			default :
				throw new IllegalStateException()
		}
	}


	public List<Evaluation> evaluate(Event event) {
		if(!accept(event)) {
			return [
				new Evaluation(state:State.REJECTED,processedEvents:[event])
			]
		}
		Collection<Evaluation> result = []

		new ArrayList(windows).each { window ->
			result << evaluate(event,window)
		}
		if(!result.any {Evaluation evaluation -> evaluation.state == State.INTEGRATED }) {
			def window = createWindow()
			windows << window
			result << evaluate(event,window)
		}

		return result
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

		private Closure accept = null;
		private List<Evaluator> evaluators = [];

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
			if(accept != null) {
				pattern.accept = this.accept;
			}
			pattern.evaluators = evaluators;
			return pattern;
		}
	}

}
