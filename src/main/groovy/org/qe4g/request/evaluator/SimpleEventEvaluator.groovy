package org.qe4g.request.evaluator
import java.util.List
import java.util.Map

import org.qe4g.Event
import org.qe4g.request.evaluation.Evaluator
import org.qe4g.request.evaluation.Response;
import org.neo4j.graphdb.Node
import org.qe4g.request.evaluation.EvaluatorDefinition
import static org.qe4g.request.evaluation.Response.*

class SimpleEventEvaluator implements Evaluator {
	def name
	Map<Integer,Closure> linkOn = [:]
	Map<String,Object> attributes = [:]
	String id;
	Range<Integer> occurs;
	EvaluatorDefinition definition = null;
	List<Closure> criterions = []
	
	public Response on(Node node, Map context) {
		if(!evaluateOnStaticCriteria(node)) {
			if(occurs.contains(context.get("cpt_${id}",{0}))) {
				return Response.CONTINUE_WITH_NEXT_EVALUATOR
			} else {
				return Response.KO
			}
		}

		int checkCounter = context.get("cpt_${id}",{0},{ return ++it})
		if (checkCounter > occurs[occurs.size() - 1] ) {
			return Response.KO;
		} else if (checkCounter == occurs[occurs.size() - 1]) {
			return Response.OK
		} else if (checkCounter < occurs[0]){
			return Response.KO_BUT_KEEP_ME
		} else {
			return Response.OK_BUT_KEEP_ME
		}
	}

	public boolean evaluateOnStaticCriteria(Event event) {
		return _evaluateOnStaticCriteria(event.names, event.attributes)
	}
	
	private boolean evaluateOnStaticCriteria(Node node) {
		return _evaluateOnStaticCriteria(node.names, node.getPublicProperties())
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

		private EvaluatorDefinition getDefinition() {
			EvaluatorDefinition definition = new EvaluatorDefinition()
			definition.eventName = this.event
			List currentAttributes = []
			currentAttributes.addAll(this.attributes.keySet())
			return definition
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
			evaluator.definition = getDefinition()
			return evaluator
		}
	}
}
