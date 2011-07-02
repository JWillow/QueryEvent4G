package org.homework.mcep.request

import org.homework.mcep.Event;

class EventDefinition {
	def name
	def select = {true}
	
	boolean evaluate(Event event) {
		if(!event.names.contains(name)) {
			return false
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
			eventDefinition.select = this.select
			return eventDefinition
		}
	}
}
