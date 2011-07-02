package org.homework.mcep.request.dsl.count;

import groovy.lang.Closure;

import java.util.Map;

import org.homework.mcep.dsl.builder.Builder;
import org.homework.mcep.dsl.builder.GroovySupportingBuilder;
import org.homework.mcep.request.EventDefinition;
import org.homework.mcep.request.count.CountRequest;
import org.homework.mcep.request.count.CountRequestEngine;

public class CountRequestBuilder implements
		GroovySupportingBuilder<CountRequestEngine> {

	CountRequest.Builder internalBuilder = CountRequest.builder();

	public CountRequestEngine build() {
		CountRequest countRequest = internalBuilder.build();
		internalBuilder = CountRequest.builder();
		return CountRequestEngine.newEngine(countRequest);
	}

	public GroovySupportingBuilder<CountRequestEngine> withData(Object value) {
		throw new UnsupportedOperationException();
	}

	public GroovySupportingBuilder<CountRequestEngine> withAttributes(
			Map<String, Object> attributes) {
		for (Map.Entry<String, Object> entry : attributes.entrySet()) {
			String attribut = entry.getKey();
			if (attribut.equals("timeNotificationInterval")) {
				internalBuilder.withTimeNotificationInterval((Integer) entry
						.getValue());
			} else if (attribut.equals("notification")) {
				internalBuilder.withNotification((Closure<?>) entry.getValue());
			} else if (attribut.equals("date")) {
				internalBuilder.withDate((Closure<?>) entry.getValue());
			} else if (attribut.equals("accept")) {
				internalBuilder.withAccept((Closure<?>) entry.getValue());
			} else if (attribut.equals("notification")) {
				internalBuilder.withNotification((Closure<?>) entry.getValue());
			} else if (attribut.equals("groupBy")) {
				internalBuilder.withGroupBy((Closure<?>) entry.getValue());
			} else if (attribut.equals("description")) {
				internalBuilder.withDescription((String) entry.getValue());
			}
		}
		return this;
	}

	public GroovySupportingBuilder<CountRequestEngine> withBuilder(
			Builder builder) {
		Object object = builder.build();
		if (object instanceof EventDefinition) {
			internalBuilder.addEventDefinition((EventDefinition) object);
		}
		return this;
	}

}
