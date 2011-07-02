package org.homework.mcep.request.dsl;

import java.util.Map;

import org.homework.mcep.dsl.builder.Builder;
import org.homework.mcep.dsl.builder.GroovySupportingBuilder;
import org.homework.mcep.request.Engine;
import org.homework.mcep.request.RequestEngine;

public class EngineBuilder implements GroovySupportingBuilder<Engine> {

	private Engine.Builder internalBuilder = Engine.builder();

	public Engine build() {
		Engine engine = internalBuilder.build();
		internalBuilder = Engine.builder();
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
		if (object instanceof RequestEngine) {
			internalBuilder.withRequestEngine((RequestEngine) object);
		} else {
			
		}
		return this;
	}

}
