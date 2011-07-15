package org.homework.mcep.request.dsl;

import java.util.Map;

import org.homework.mcep.dsl.builder.Builder;
import org.homework.mcep.dsl.builder.GroovySupportingBuilder;
import org.homework.mcep.request.Evaluator;
import org.homework.mcep.request.evaluator.NotOperatorEvaluator;

public class NotOperatorEvaluatorBuilder implements
		GroovySupportingBuilder<NotOperatorEvaluator> {

	private NotOperatorEvaluator.Builder internalBuilder = NotOperatorEvaluator
			.builder();

	public NotOperatorEvaluator build() {
		NotOperatorEvaluator noe = internalBuilder.build();
		internalBuilder = NotOperatorEvaluator.builder();
		return noe;
	}

	public GroovySupportingBuilder withData(Object value) {
		throw new IllegalArgumentException();
	}

	public GroovySupportingBuilder withAttributes(Map<String, Object> attributes) {
		throw new IllegalArgumentException();
	}

	public GroovySupportingBuilder withBuilder(Builder builder) {
		Object object = builder.build();
		if(object instanceof Evaluator) {
			internalBuilder.onEvaluator((Evaluator) object);
		} else {
			throw new IllegalArgumentException();
		}
		return this;
	}

}
