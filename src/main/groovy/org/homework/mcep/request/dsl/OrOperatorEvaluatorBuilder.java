package org.homework.mcep.request.dsl;

import java.util.Map;

import org.homework.mcep.dsl.builder.Builder;
import org.homework.mcep.dsl.builder.GroovySupportingBuilder;
import org.homework.mcep.request.Evaluator;
import org.homework.mcep.request.evaluator.OrOperatorEvaluator;

public class OrOperatorEvaluatorBuilder implements
		GroovySupportingBuilder<OrOperatorEvaluator> {

	private OrOperatorEvaluator.Builder internalBuilder = OrOperatorEvaluator
			.builder();

	public OrOperatorEvaluator build() {
		OrOperatorEvaluator ooe = internalBuilder.build();
		internalBuilder = OrOperatorEvaluator.builder();
		return ooe;
	}

	public GroovySupportingBuilder withData(Object value) {
		throw new IllegalArgumentException();
	}

	public GroovySupportingBuilder withAttributes(Map<String, Object> attributes) {
		throw new IllegalArgumentException();
	}

	public GroovySupportingBuilder withBuilder(Builder builder) {
		Object object = builder.build();
		if (object instanceof Evaluator) {
			internalBuilder.onEvaluator((Evaluator) object);
		} else {
			throw new IllegalArgumentException();
		}
		return this;
	}

}
