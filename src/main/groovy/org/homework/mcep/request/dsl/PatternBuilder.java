package org.homework.mcep.request.dsl;

import groovy.lang.Closure;

import java.util.Map;

import org.homework.mcep.dsl.builder.Builder;
import org.homework.mcep.dsl.builder.GroovySupportingBuilder;
import org.homework.mcep.request.Evaluator;
import org.homework.mcep.request.Pattern;

public class PatternBuilder implements GroovySupportingBuilder<Pattern> {

	Pattern.Builder internalBuilder = Pattern.builder();

	public Pattern build() {
		Pattern pattern = internalBuilder.build();
		internalBuilder = Pattern.builder();
		return pattern;
	}

	public GroovySupportingBuilder withData(Object value) {
		throw new UnsupportedOperationException();
	}

	public GroovySupportingBuilder withAttributes(Map<String, Object> attributes) {
		for (Map.Entry<String, Object> entry : attributes.entrySet()) {
			String attribut = entry.getKey();
			if (attribut.equals("accept")) {
				internalBuilder.withAccept((Closure<?>) entry.getValue());
			} else if (attribut.equals("groupBy")) {
				internalBuilder.withGroupBy((Closure<?>) entry.getValue());
			} else {
				throw new IllegalArgumentException("Attribut " + attribut
						+ " is not expected !");
			}
		}
		return this;
	}

	public GroovySupportingBuilder withBuilder(Builder builder) {
		Object object = builder.build();
		if (object instanceof Evaluator) {
			internalBuilder.addEvaluator((Evaluator) object);
		} else {
			throw new IllegalArgumentException("Object " + object
					+ " isn't expected !");
		}
		return this;
	}

}
