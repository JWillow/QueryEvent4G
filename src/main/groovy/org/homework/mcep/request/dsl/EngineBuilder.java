package org.homework.mcep.request.dsl;

import java.util.Map;

import org.homework.mcep.dsl.builder.Builder;
import org.homework.mcep.dsl.builder.GroovySupportingBuilder;
import org.homework.mcep.request.RequestDispatcher;
import org.homework.mcep.request.Request;

public class EngineBuilder implements GroovySupportingBuilder<RequestDispatcher> {

	private RequestDispatcher.Builder internalBuilder = RequestDispatcher.builder();

	public RequestDispatcher build() {
		RequestDispatcher engine = internalBuilder.build();
		internalBuilder = RequestDispatcher.builder();
		return engine;
	}

	public GroovySupportingBuilder withData(Object value) {
		throw new UnsupportedOperationException();
	}

	public GroovySupportingBuilder withAttributes(Map<String, Object> attributes) {
		throw new UnsupportedOperationException();
	}

	public GroovySupportingBuilder withBuilder(Builder builder) {
		Object object = builder.build();
		if (object instanceof Request) {
			internalBuilder.withRequestEngine((Request) object);
		} else {
			
		}
		return this;
	}

}
