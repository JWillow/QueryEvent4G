package org.homework.mcep.request

import org.homework.mcep.Event;

class EventDefinition {
	def name
	def attributes
	Closure select = {true}
	
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
		return select(event)
	}
	
	
	// BUILDER PART
	public static Builder builder() {
		return new Builder();
	}
	public static class Builder {
		private String event
		private Closure select
		private Map<String,Object> attributes
		
		public Builder selectOnAttributes(Map<String,Object> attributes) {
			this.attributes = attributes
			return this
		}
		public Builder withSelect(Closure select) {
			this.select = select
			return this;
		}
		
		public Builder withEventName(String event) {
			this.event = event;
			return this;
		}
		
		public EventDefinition build() {
			EventDefinition eventDefinition = new EventDefinition()
			eventDefinition.name = this.event
			eventDefinition.attributes = this.attributes
			if(this.select != null) {
				eventDefinition.select = this.select
			}
			return eventDefinition
		}
	}
}
