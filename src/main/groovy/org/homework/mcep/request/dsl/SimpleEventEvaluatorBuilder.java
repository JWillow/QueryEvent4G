package org.homework.mcep.request.dsl;

import groovy.lang.Closure;
import groovy.lang.Range;

import java.util.Map;

import org.homework.mcep.dsl.builder.Builder;
import org.homework.mcep.dsl.builder.GroovySupportingBuilder;
import org.homework.mcep.request.Counter;
import org.homework.mcep.request.evaluator.SimpleEventEvaluator;

public class SimpleEventEvaluatorBuilder implements
		GroovySupportingBuilder<SimpleEventEvaluator> {

	SimpleEventEvaluator.Builder internalBuilder = SimpleEventEvaluator
			.builder();

	public SimpleEventEvaluator build() {
		SimpleEventEvaluator eventDefinition = internalBuilder.build();
		internalBuilder = SimpleEventEvaluator.builder();
		return eventDefinition;
	}

	public GroovySupportingBuilder withData(Object value) {
		throw new UnsupportedOperationException();
	}

	private int getIndex(String attribut) {
		if (attribut.endsWith("First")) {
			return 0;
		}
		if (attribut.endsWith("Second")) {
			return 1;
		}
		if (attribut.endsWith("Third")) {
			return 2;
		}
		if (attribut.endsWith("Fourth")) {
			return 3;
		}
		return -1;
	}

	public GroovySupportingBuilder withAttributes(Map<String, Object> attributes) {
		internalBuilder.affectId(Counter.getId());
		for (Map.Entry<String, Object> entry : attributes.entrySet()) {
			String attribut = entry.getKey();
			if (attribut.equals("select")) {
				internalBuilder.withSelect((Closure<?>) entry.getValue());
			} else if (attribut.equals("name")) {
				internalBuilder.withEventName((String) entry.getValue());
			} else if (attribut.equals("attributes")) {
				internalBuilder.selectOnAttributes((Map) entry.getValue());
			} else if (attribut.startsWith("occurs")) {
				internalBuilder.occurs((Range<Comparable>) entry.getValue());
			} else if (attribut.startsWith("maxInterval")) {
				internalBuilder.withMaxIntervalCriteria(
						(Integer) entry.getValue(), getIndex(attribut));
			} else if (attribut.startsWith("minInterval")) {
				internalBuilder.withMinIntervalCriteria(
						(Integer) entry.getValue(), getIndex(attribut));
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
