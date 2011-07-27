package org.homework.mcep.request.dsl;

import groovy.lang.Closure;
import groovy.lang.IntRange;
import groovy.lang.Range;

import java.util.Map;

import org.homework.mcep.dsl.builder.Builder;
import org.homework.mcep.dsl.builder.GroovySupportingBuilder;
import org.homework.mcep.request.Counter;
import org.homework.mcep.request.evaluator.SimpleEventEvaluator;

/**
 * Different examples :
 * 
 * <pre>
 * event(name:'RessourceEvent',attributes:[state:'ACDAddrBusyEv',skill:'6192000'],occurs:0..3)
 * </pre>
 * 
 * <pre>
 * event(name:'RessourceEvent',select:{List<Event> events -> lastEvents(events).state = 'ACDAddrBusyEv'})
 * </pre>
 * 
 * <pre>
 * event(name:'RessourceEvent',minIntervalWithLastEventInSecond:10)
 * </pre>
 * 
 * @author Willow
 * 
 */
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
		if (attribut.contains("First")) {
			return 0;
		}
		if (attribut.contains("Second")) {
			return 1;
		}
		if (attribut.contains("Third")) {
			return 2;
		}
		if (attribut.contains("Fourth")) {
			return 3;
		}
		return -1;
	}

	private int getTime(String attribut, int value) {
		if (attribut.endsWith("InMillis")) {
			return value;
		}
		value = value * 1000;
		if (attribut.endsWith("InSecond")) {
			return value;
		}
		value = value * 60;
		if (attribut.endsWith("InMinute")) {
			return value;
		}
		value = value * 60;
		if (attribut.endsWith("InHour")) {
			return value;
		}
		throw new IllegalArgumentException(String.format(
				"attribut[%s] not supported !", attribut));
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
				internalBuilder.occurs((IntRange) entry.getValue());
			} else if (attribut.startsWith("maxInterval")) {
				internalBuilder.withMaxIntervalCriteria(
						getTime(attribut, (Integer) entry.getValue()),
						getIndex(attribut));
			} else if (attribut.startsWith("minInterval")) {
				internalBuilder.withMinIntervalCriteria(
						getTime(attribut, (Integer) entry.getValue()),
						getIndex(attribut));
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
