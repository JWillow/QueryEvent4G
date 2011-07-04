package org.homework.mcep.request.dsl;

import groovy.lang.Closure;

import java.util.List;
import java.util.Map;

import org.homework.mcep.dsl.builder.Builder;
import org.homework.mcep.dsl.builder.GroovySupportingBuilder;
import org.homework.mcep.request.EventDefinition;
import org.homework.mcep.request.EventListener;
import org.homework.mcep.request.Function;
import org.homework.mcep.request.RequestDefinition;
import org.homework.mcep.request.Request;

public class RequestDefinitionBuilder implements
		GroovySupportingBuilder<Request> {

	RequestDefinition.Builder internalBuilder = RequestDefinition.builder();

	public Request build() {
		RequestDefinition requestDefinition = internalBuilder.build();
		internalBuilder = RequestDefinition.builder();
		return Request.newEngine(requestDefinition);
	}

	public GroovySupportingBuilder<Request> withData(Object value) {
		throw new UnsupportedOperationException();
	}

	public GroovySupportingBuilder<Request> withAttributes(
			Map<String, Object> attributes) {
		for (Map.Entry<String, Object> entry : attributes.entrySet()) {
			String attribut = entry.getKey();
			if (attribut.equals("date")) {
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

	public GroovySupportingBuilder<Request> withBuilder(Builder builder) {
		Object object = builder.build();
		if (object instanceof EventDefinition) {
			internalBuilder.addEventDefinition((EventDefinition) object);
		} else if (object instanceof Function) {
			internalBuilder.addFunction((Function) object);
		} else if (object instanceof EventListener) {
			internalBuilder.addEventListener((EventListener) object);
		} else if (builder instanceof SimpleListBuilder) {
			SimpleListBuilder slb = (SimpleListBuilder) builder;
			if (slb.getType().equals("pattern")) {
				setEventDefinition((List<Object>) object);
			} else if (slb.getType().equals("functions")) {
				setFunctions((List<Object>) object);
			}
		}
		return this;
	}

	private void setFunctions(List<Object> list) {
		for (Object object : list) {
			internalBuilder.addFunction((Function) object);
		}
	}

	private void setEventDefinition(List<Object> list) {
		for (Object object : list) {
			internalBuilder.addEventDefinition((EventDefinition) object);
		}
	}

}
