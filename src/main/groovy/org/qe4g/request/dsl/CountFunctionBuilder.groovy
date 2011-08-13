package org.qe4g.request.dsl;

import groovy.lang.Closure;

import java.util.Map;

import org.qe4g.dsl.builder.Builder;
import org.qe4g.dsl.builder.GroovySupportingBuilder;
import org.qe4g.request.dsl.keyword.CountBy;
import org.qe4g.request.function.BasicFunction;

public class CountFunctionBuilder implements
GroovySupportingBuilder<BasicFunction> {

	String by = null;
	BasicFunction.Builder internalBuilder = BasicFunction.builder();

	public BasicFunction build() {
		internalBuilder.withCore(CountBy.get("by",by))
		BasicFunction cf = internalBuilder.build();
		internalBuilder = BasicFunction.builder();
		by = null;
		return cf;
	}

	public GroovySupportingBuilder withData(Object value) {
		throw new UnsupportedOperationException();
	}

	public GroovySupportingBuilder withAttributes(Map<String, Object> attributes) {
		for (Map.Entry<String, Object> entry : attributes.entrySet()) {
			String attribut = entry.getKey();
			if (attribut.equals("by")) {
				by = (String) entry.getValue();
			} else if (attribut.equals("notification")) {
				internalBuilder.withNotification((Closure<?>) entry.getValue());
			} else {
				throw new IllegalArgumentException(String.format(
				"Field [%s] unknown for count tag !",
				attribut));
			}
		}
		return this;
	}

	public GroovySupportingBuilder withBuilder(String childName, Builder builder) {
		throw new IllegalArgumentException("No child node accepted !");
	}
}
