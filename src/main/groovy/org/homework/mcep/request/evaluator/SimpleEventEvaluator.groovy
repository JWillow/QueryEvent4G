package org.homework.mcep.request.evaluator

import org.homework.mcep.Event;
import org.homework.mcep.request.Evaluator;

class SimpleEventEvaluator implements Evaluator {
	def name
	def attributes
	List<Closure> criterion

	boolean evaluate(List<Event> events) {
		Event event = events.get(events.size() - 1)
		if(!event.names.contains(name)) {
			return false
		}
		if(attributes.size() != 0) {
			boolean result = attributes.every {key, value ->
				value == event.attributes[key]
			}
			if(!result) {
				return false;
			}
		}
		return criterion.every() { it(events); }
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
		private Map<String,Object> attributes

		private def interval = {int timeInterval, int index, boolean min, List<Event> events ->
			Event lastEvent = events[events.size() - 1]
			Event eventForIntervalTest = events[index]
			long time = lastEvent.attributes['time'] - eventForIntervalTest.attributes['time']
			if(min) {
				return time > timeInterval
			} else {
				return time < timeInterval
			}
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
			evaluator.name = this.event
			evaluator.attributes = this.attributes
			evaluator.criterion = this.criterion
			return evaluator
		}
	}
}
