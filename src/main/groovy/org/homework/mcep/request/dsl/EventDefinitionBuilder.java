package org.homework.mcep.request.dsl;

import groovy.lang.Closure;

import java.util.Map;

import org.homework.mcep.dsl.builder.Builder;
import org.homework.mcep.dsl.builder.GroovySupportingBuilder;
import org.homework.mcep.request.EventDefinition;

public class EventDefinitionBuilder implements
		GroovySupportingBuilder<EventDefinition> {

	EventDefinition.Builder internalBuilder = EventDefinition.builder();
	
	public EventDefinition build() {
		EventDefinition eventDefinition = internalBuilder.build();
		internalBuilder = EventDefinition.builder();
		return eventDefinition;
	}

	public GroovySupportingBuilder withData(Object value) {
		throw new UnsupportedOperationException();
	}

	public GroovySupportingBuilder withAttributes(Map<String, Object> attributes) {
		for(Map.Entry<String, Object> entry:attributes.entrySet()) {
			String attribut = entry.getKey();
			if(attribut.equals("select")) {
				internalBuilder.withSelect((Closure<?>) entry.getValue());
			} else if (attribut.equals("name")) {
				internalBuilder.withEventName((String) entry.getValue());
			} else {
				throw new IllegalArgumentException(String.format(
						"Field [%s] unknown for EventDefinitionBuilder !",
						attribut));
			}
		}
		return this;
	}

	public GroovySupportingBuilder withBuilder(Builder builder) {
		throw new UnsupportedOperationException();
	}

}
