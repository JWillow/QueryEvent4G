package org.qe4g.request

import static org.qe4g.request.Window.State.BROKEN
import static org.qe4g.request.Window.State.CLOSED
import static org.qe4g.request.Window.State.OPEN

import groovy.lang.Closure;

import java.util.List;

import org.qe4g.Event
import org.qe4g.request.Pattern.Evaluation;
import org.qe4g.request.Pattern.State;
import org.qe4g.request.eventlistener.ScheduledNotification;

/**
 * <p>Represent a Request based on pattern detection ({@link Evaluator}) and apply {@link Function} when the detection occurs.
 * <p>Request example :
 * <pre>
 * def notifCount = { println it}
 * def countCore = {context,events ->
 *	(context.containsKey("cpt"))?context.cpt++:(context.cpt = 1)
 * }
 * def display = {context,events ->
 * 	def id = events[0].attributes.id
 *	def begin = events[0].attributes.date
 *	def end = events[1].attributes.date
 *	println "$id -> [$begin, $end]"
 * }
 * request(groupBy:{it.attributes.id }) {
 *	pattern {
 *		event(name:'RessourceEvent',attributes:[state:'Busy',skill:'6192000'])
 *		event(name:'RessourceEvent',attributes:[state:'Ready',skill:'6192000'])
 *	}
 *	scheduledNotification(interval:600000*6, reset:true) {
 *		function(core:countCore,notification:notifCount)
 *	}
 *	onPatternDetection {
 *		function(core:display,notification:{})
 *	}
 *} 
 * </pre>
 * In this example : 
 * <ul>
 * <li>We declare a <code>groupBy</code> closure on {@link Event#getAttributes()} <code>id</code>.</li>
 * <li>We define a pattern detection based on two {@link Event}</li>
 * <li>We declare a {@link ScheduledNotification} to display the <code>countCore</code> closure result each hour</li>
 * <li>We declare a function call each detection to display the pattern found</li>
 * </ul> 
 * @author Willow
 */
class Request {

	List<EventProcessedListener> eventListeners;

	String description

	Pattern pattern

	List<Function> functions

	/**
	 * Call {@link Function#get()} on each {@link Function} registred
	 */
	void get() {
		functions*.get()
	}
	
	/**
	* Call {@link Function#reset()} on each {@link Function} registred
	*/
	void reset() {
		functions*.reset()
	}

	/**
	 * Perform one {@link Event}.
	 * <ul>
	 * <li>Call {@link EventListener#beforeEventProcessing(Request, Event)} on each {@link EventListener} registred</li>
	 * <li>Call the {@link #accept} closure</li>
	 * <li>Call call {@link #notify()} if the {@link Event} is accepted, and every {@link Evaluator} are positive</li>
	 * <li>Call {@link EventListener#afterEventProcessed(Request, Window, Event)} on each {@link EventListener}</li>
	 * </ul>
	 * @param event
	 */
	void onEvent(Event event) {
		eventListeners*.beforeEventProcessing(this ,event)
		Collection<Evaluation> evaluations = pattern.evaluate(event)
		evaluations.each { evaluation -> 
			if(evaluation.state == State.DETECTED) {
				notifyFunctions(evaluation.processedEvents)
			}
		}
		eventListeners*.afterEventProcessed(this, evaluations)
	}

	private void notifyFunctions(List<Event> events) {
		functions.each {
			it.onPatternDetection(this, events)
		}
	}

	@Override
	public String toString() {
		return description
	}

	// ------------
	// BUILDER PART
	// ------------
	private Request() {
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private Pattern pattern = null;
		private List<EventProcessedListener> eventListeners = []
		private List<Function> functions = [];
		private String description

		public Builder withDescription(String description) {
			this.description = description
			return this
		}

		public Builder addEventListener(EventProcessedListener eventListener) {
			eventListeners << eventListener
			return this
		}

		public Builder addFunction(Function function) {
			functions << function
			return this
		}

		public Builder workAroundPattern(Pattern pattern) {
			this.pattern = pattern
			return this
		}

		public Request build() {
			Request request = new Request();
			request.eventListeners = this.eventListeners
			request.description = this.description
			request.functions = this.functions
			request.pattern = this.pattern
			return request;
		}
	}

}
