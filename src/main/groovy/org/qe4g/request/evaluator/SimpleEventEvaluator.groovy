package org.qe4g.request.evaluator
import java.util.List
import java.util.Map

import org.qe4g.Event
import org.qe4g.request.evaluation.Evaluator
import org.qe4g.request.evaluation.OccurResponse;
import org.qe4g.request.evaluation.EvaluatorDefinition
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.blueprints.pgm.Vertex;

import static org.qe4g.request.graph.EdgeTypes.*
import static org.qe4g.request.evaluation.OccurResponse.*

class SimpleEventEvaluator implements Evaluator {
	final static Logger logger = LoggerFactory.getLogger(SimpleEventEvaluator.class);

	def name
	Map<Integer,Closure> linkOn = [:]
	Map<String,Object> attributes = [:]
	String id;
	Range<Integer> occurs;
	EvaluatorDefinition definition = null;
	List<Closure> criterions = []

	public boolean isOptional() {
		return occurs[0] == 0
	}

	public boolean evaluateRelationship(Vertex vEvaluationContext, Vertex vEvent) {

		List<Event> events =  ((0 % vEvaluationContext >> PREVIOUS)).collect {Vertex vEvalContext ->
			(1 % vEvalContext >> EVALUATED)[0].event
		}

		if(events.size() < linkOn.size()) {
			return false;
		}

		return  linkOn.every {index,control ->
			control(vEvent.event, events[index])
		}
	}

	public OccurResponse evaluateOnOccurrenceCriteria(Vertex vEvaluationContext, Vertex vEvent) {
		OccurResponse response = null
		vEvaluationContext.occur ++;
		if (vEvaluationContext.occur == occurs[occurs.size() - 1]) {
			response = OccurResponse.OK
		} else if (vEvaluationContext.occur < occurs[0]){
			response = OccurResponse.KO_BUT_KEEP_ME
		} else {
			response = OccurResponse.OK_BUT_KEEP_ME
		}
		vEvaluationContext.state = response
		return response
	}

	public boolean evaluateOnStaticCriteria(Vertex vertextEvent) {
		return _evaluateOnStaticCriteria(vertextEvent.event.names, vertextEvent.event.attributes)
	}

	private boolean _evaluateOnStaticCriteria(List<String> names, Map properties) {
		if(name != null && !names.any {it.equals(name)}) {
			return false
		}
		if(attributes.size() != 0) {
			return attributes.every { key, value ->	value.equals(properties[key])}
		}
		return true
	}
	// ------------
	// BUILDER PART
	// ------------
	public static Builder builder() {
		return new Builder();
	}
	public static class Builder {
		private Map<Integer,Closure> linkOn = [:];
		private String event
		private List<Closure> criterion = []
		private Map<String,Object> attributes = [:]
		private String id = null;
		private IntRange range = null;

		private def interval = {int timeInterval, int index, boolean min, List<Event> events ->
			Event currentEvent = events[events.size() - 1]
			Event eventForIntervalTest = events[index]
			if(index == -1) {
				eventForIntervalTest = events[events.size() - 1]
			} else {
				eventForIntervalTest = events[index]
			}
			long time = currentEvent.attributes['time'] - eventForIntervalTest.attributes['time']
			if(min) {
				return time > timeInterval
			} else {
				return time < timeInterval
			}
		}

		public Builder occurs(IntRange occurs) {
			this.range = occurs
			return this
		}
		public Builder affectId(String id) {
			this.id = id;
			return this;
		}
		public Builder selectOnAttributes(Map<String,Object> attributes) {
			this.attributes = attributes
			return this
		}
		public Builder withSelect(Closure select) {
			this.criterion << select
			return this;
		}

		public Builder withEventName(String event) {
			this.event = event;
			return this;
		}

		public Builder withMinIntervalCriteria(int time, int eventIndex) {
			criterion << interval.curry(time,eventIndex,true)
			return this
		}

		public Builder withMaxIntervalCriteria(int time, int eventIndex) {
			criterion << interval.curry(time,eventIndex,false)
			return this
		}

		public Builder linkOn(Map<Integer,Closure> linkOn) {
			this.linkOn = linkOn;
			return this
		}

		public SimpleEventEvaluator build() {
			SimpleEventEvaluator evaluator = new SimpleEventEvaluator()
			evaluator.id = this.id
			evaluator.linkOn = this.linkOn
			evaluator.name = this.event
			evaluator.attributes = this.attributes
			evaluator.criterions = this.criterion
			if(this.range == null) {
				evaluator.occurs = new IntRange(1,1)
			} else {
				evaluator.occurs = this.range
			}
			return evaluator
		}
	}
}
