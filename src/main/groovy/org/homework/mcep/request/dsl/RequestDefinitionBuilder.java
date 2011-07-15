package org.homework.mcep.request.dsl;

import groovy.lang.Closure;

import java.util.List;
import java.util.Map;

import org.homework.mcep.dsl.builder.Builder;
import org.homework.mcep.dsl.builder.GroovySupportingBuilder;
import org.homework.mcep.request.Evaluator;
import org.homework.mcep.request.EventListener;
import org.homework.mcep.request.Function;
import org.homework.mcep.request.Functions;
import org.homework.mcep.request.Request;
import org.homework.mcep.request.RequestDefinition;
import org.homework.mcep.request.evaluator.SimpleEventEvaluator;

public class RequestDefinitionBuilder implements
		GroovySupportingBuilder<Request> {

	RequestDefinition.Builder internalBuilder = RequestDefinition.builder();

	public Request build() {
		RequestDefinition requestDefinition = internalBuilder.build();
		internalBuilder = RequestDefinition.builder();
		return Request.newEngine(requestDefinition);
	}

	public GroovySupportingBuilder<Request> withData(Object value) {
		throw new UnsupportedOperationException();
	}

	public GroovySupportingBuilder<Request> withAttributes(
			Map<String, Object> attributes) {
		for (Map.Entry<String, Object> entry : attributes.entrySet()) {
			String attribut = entry.getKey();
			if (attribut.equals("date")) {
				internalBuilder.withDate((Closure<?>) entry.getValue());
			} else if (attribut.equals("accept")) {
				internalBuilder.withAccept((Closure<?>) entry.getValue());
			} else if (attribut.equals("notification")) {
				internalBuilder.withNotification((Closure<?>) entry.getValue());
			} else if (attribut.equals("groupBy")) {
				internalBuilder.withGroupBy((Closure<?>) entry.getValue());
			} else if (attribut.equals("description")) {
				internalBuilder.withDescription((String) entry.getValue());
			}
		}
		return this;
	}

	public GroovySupportingBuilder<Request> withBuilder(Builder builder) {
		Object object = builder.build();
		if (object instanceof Evaluator) {
			internalBuilder.addEvaluator((Evaluator) object);
		}
		if (object instanceof Function) {
			internalBuilder.addFunction((Function) object);
		} 
		if (object instanceof Functions) {
			setFunctions(((Functions) object).getFunctions());
		}
		if (object instanceof EventListener) {
			internalBuilder.addEventListener((EventListener) object);
		} 
		if (builder instanceof SimpleListBuilder) {
			SimpleListBuilder slb = (SimpleListBuilder) builder;
			if (slb.getType().equals("pattern")) {
				setEvaluators((List<Object>) object);
			} else if (slb.getType().equals("functions")) {
				setFunctions((List<Function>) object);
			}
		}
		return this;
	}

	private void setFunctions(List<Function> list) {
		for (Object object : list) {
			internalBuilder.addFunction((Function) object);
		}
	}

	private void setEvaluators(List<Object> list) {
		for (Object object : list) {
			internalBuilder.addEvaluator((Evaluator) object);
		}
	}

}
