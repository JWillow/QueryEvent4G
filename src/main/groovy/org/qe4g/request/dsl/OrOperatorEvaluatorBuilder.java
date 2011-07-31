package org.qe4g.request.dsl;

import java.util.Map;

import org.qe4g.dsl.builder.Builder;
import org.qe4g.dsl.builder.GroovySupportingBuilder;
import org.qe4g.request.Counter;
import org.qe4g.request.Evaluator;
import org.qe4g.request.evaluator.OrOperatorEvaluator;

public class OrOperatorEvaluatorBuilder implements
		GroovySupportingBuilder<OrOperatorEvaluator> {

	private OrOperatorEvaluator.Builder internalBuilder = OrOperatorEvaluator
			.builder();

	public OrOperatorEvaluator build() {
		Counter.stop();
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

	public GroovySupportingBuilder withBuilder(String childName, Builder builder) {
		Object object = builder.build();
		internalBuilder.affectId(Counter.getId());
		if (object instanceof Evaluator) {
			internalBuilder.onEvaluator((Evaluator) object);
		} else {
			throw new IllegalArgumentException();
		}
		return this;
	}

}
