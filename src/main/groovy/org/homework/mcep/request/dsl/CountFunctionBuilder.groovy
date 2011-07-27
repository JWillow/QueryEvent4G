package org.homework.mcep.request.dsl;

import groovy.lang.Closure;

import java.util.Map;

import org.homework.mcep.dsl.builder.Builder;
import org.homework.mcep.dsl.builder.GroovySupportingBuilder;
import org.homework.mcep.request.function.ClosureFunction;

public class CountFunctionBuilder implements
GroovySupportingBuilder<ClosureFunction> {

	String by = null;
	ClosureFunction.Builder internalBuilder = ClosureFunction.builder();

	def countCore = {attr, context, events->
		def key = "cpt"
		if(attr != '') {
			def event = events[events.size() - 1]
			key = event.attributes[attr]
		}
		(context.containsKey(key))?context[key]++:(context[key] = 1)
	}
	
	public ClosureFunction build() {
		if(!by) {
			by=''
		}
		def newClos = countCore.curry(by)
		internalBuilder.withCore(newClos)
		ClosureFunction cf = internalBuilder.build();
		internalBuilder = ClosureFunction.builder();
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

	public GroovySupportingBuilder withBuilder(Builder builder) {
		throw new IllegalArgumentException("No child node accepted !");
	}
}
