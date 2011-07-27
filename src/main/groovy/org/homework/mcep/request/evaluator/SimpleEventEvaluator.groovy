package org.homework.mcep.request.evaluator

import org.homework.mcep.Event;
import org.homework.mcep.request.Evaluator;
import org.homework.mcep.request.Evaluator.Response;

class SimpleEventEvaluator implements Evaluator {
	def name
	Map<String,Object> attributes = [:]
	String id;
	List<Closure> criterions
	Range<Integer> occurs;

	private boolean evaluateOnNameAndAttributesAndCriterion(Map<String,Object> context, List<Event> events) {
		Event event = events[events.size() -1]
		if(!event.names.contains(name)) {
			return false
		}
		if(attributes.size() != 0) {
			boolean result = attributes.every { key, value ->
				value == event.attributes[key]
			}
			if(!result) {
				return false;
			}
		}
		return criterions.every {it(events)}
	}
	public Response evaluate(Map<String,Object> context, List<Event> events) {
		boolean result = evaluateOnNameAndAttributesAndCriterion(context, events)

		if(!result) {
			if(occurs.contains(getCounter(context))) {
				return Response.CONTINUE_WITH_NEXT_EVALUATOR
			} else {
				return Response.KO
			}
		}

		int checkCounter = getCounterAndIncrement(context)
		if (checkCounter > occurs[occurs.size() - 1] ) {
			return Response.KO;
		} else if (checkCounter == occurs[occurs.size() - 1]) {
			return Response.OK
		} else {
			return Response.OK_BUT_KEEP_ME
		}
	}

	private int getCounter(Map<String,Object> context) {
		def counter = context[id]
		if(counter == null) {
			counter = 0
		}
		return counter
	}

	private int getCounterAndIncrement(Map<String,Object> context) {
		int counter = getCounter(context)
		context[id] = ++counter
		return counter
	}

	// ------------
	// BUILDER PART
	// ------------
	public static Builder builder() {
		return new Builder();
	}
	public static class Builder {
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

		public SimpleEventEvaluator build() {
			SimpleEventEvaluator evaluator = new SimpleEventEvaluator()
			evaluator.id = this.id
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
