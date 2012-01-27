package org.qe4g.request

import java.util.List

import org.qe4g.Event
import org.qe4g.request.pattern.Pattern
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.pgm.Vertex
import static org.qe4g.request.graph.EdgeTypes.*
import static org.qe4g.request.graph.VertexTypes.*

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

	final static Logger logger = LoggerFactory.getLogger(Request.class);
	
	List<EventProcessedListener> eventListeners;

	String description

	Pattern pattern

	List<Function> functions

	/**
	 * Call {@link Function#get()} on each {@link Function} registred
	 */
	void get() {
		//functions*.get(-1)
	}

	/**
	 * Call {@link Function#reset()} on each {@link Function} registred
	 */
	void reset() {
		functions*.reset()
	}


	boolean accept(final Event event) {
		return pattern.accept(event)
	}

	private Comparator<Event> timeBasedComparator = new Comparator<Event>() {
		public int compare(Event ev1, Event ev2) {
			return ev1.getTime() - ev2.getTime();
		}
	};

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
	void onNodeEvent(Vertex vEvent) {
		Collection<Vertex> vPaths = pattern.correlate(vEvent)
		vPaths.each { Vertex vPath ->
			// Evaluation Context Collect
			List<Vertex> vEvaluationContexts = [];
			vEvaluationContexts << (1 % vPath >> CURRENT_EVAL_CONTEXT).unique();
			vEvaluationContexts + (0 % vEvaluationContexts[0] >> PREVIOUS);
			vEvaluationContexts + (0 % vEvaluationContexts[0] << PREVIOUS);
			// Event Vertex Collect
			List<Vertex> vEvents = [];
			vEvaluationContexts.each {Vertex vEvaluationContext ->
				vEvents + (1 % vEvaluationContext >> EVALUATED);
				logger.debugG("Delete Evaluation Context : {}", vEvaluationContext);
				vEvaluationContext--;
			}
			logger.debugG("Delete Path : {}", vPath);
			vPath --
			logger.debugG("Events collected : {}", vEvents);
			vEvents.findAll{(1 % it << EVALUATED).empty && (1 % it >> ATTACHED).empty}
			.each{logger.debugG("Delete Event {}",it); it--};

			// Event Collect
			List<Event> events = vEvents.collect {it.event}
			Collections.sort(events, timeBasedComparator);
			logger.debugG("Events collected : {}", events);
			functions.each {it.onPatternDetection(this, events)}
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
