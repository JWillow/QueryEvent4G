package org.homework.mcep.request.dsl;

import groovy.lang.Closure;

import java.util.Map;

import org.homework.mcep.dsl.builder.Builder;
import org.homework.mcep.dsl.builder.GroovySupportingBuilder;
import org.homework.mcep.request.function.ClosureFunction;

public class FunctionBuilder implements
		GroovySupportingBuilder<ClosureFunction> {

	ClosureFunction.Builder internalBuilder = ClosureFunction.builder();

	public ClosureFunction build() {
		ClosureFunction cf = internalBuilder.build();
		internalBuilder = ClosureFunction.builder();
		return cf;
	}

	public GroovySupportingBuilder withData(Object value) {
		throw new UnsupportedOperationException();
	}

	public GroovySupportingBuilder withAttributes(Map<String, Object> attributes) {
		for (Map.Entry<String, Object> entry : attributes.entrySet()) {
			String attribut = entry.getKey();
			if (attribut.equals("core")) {
				internalBuilder.withCore((Closure<?>) entry.getValue());
			} else if (attribut.equals("notification")) {
				internalBuilder.withNotification((Closure<?>) entry.getValue());
			} else {
				throw new IllegalArgumentException(String.format(
						"Field [%s] unknown for EventDefinitionBuilder !",
						attribut));
			}
		}
		return this;
	}

	public GroovySupportingBuilder withBuilder(Builder builder) {
		// TODO Auto-generated method stub
		return null;
	}

}
