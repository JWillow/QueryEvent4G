package org.qe4g.request.dsl;

import groovy.lang.Closure;

import java.util.Map;

import org.qe4g.dsl.builder.Builder;
import org.qe4g.dsl.builder.GroovySupportingBuilder;
import org.qe4g.request.function.BasicFunction;

public class FunctionBuilder implements
		GroovySupportingBuilder<BasicFunction> {

	BasicFunction.Builder internalBuilder = BasicFunction.builder();

	public BasicFunction build() {
		BasicFunction cf = internalBuilder.build();
		internalBuilder = BasicFunction.builder();
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

	public GroovySupportingBuilder withBuilder(String childName, Builder builder) {
		throw new IllegalArgumentException("No child node accepted !");
	}

}
