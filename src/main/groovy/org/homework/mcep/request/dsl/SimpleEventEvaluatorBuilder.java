package org.homework.mcep.request.dsl;

import groovy.lang.Closure;
import groovy.lang.IntRange;

import java.util.Map;

import org.homework.mcep.dsl.builder.Builder;
import org.homework.mcep.dsl.builder.GroovySupportingBuilder;
import org.homework.mcep.request.Counter;
import org.homework.mcep.request.evaluator.SimpleEventEvaluator;

/**
 * Different examples :
 * 
 * <p>
 * We want between 0 and 3 events with name <code>B</code> and with attributes
 * values equals to <code>Ready</code> for <code>state</code> and
 * <code>finance</code> for <code>skill</code>.
 * <br/><code>event(name:'B',attributes:[state:'Ready',skill:'finance'],occurs:0..3)</code>
 * <p>
 * Here we specify our personal closure to select the events with <code>B</code> name.
 * <br/><code>event(name:'RessourceEvent',select:{List<Event> events -> lastEvents(events).state = 'ACDAddrBusyEv'})</code>
 * <p>
 * In this example we describe we want the event <code>B</code> with a mininum
 * of 10 seconds on the last event processed. <br/>
 * <code>event(name:'B',minIntervalWithLastEventInSecond:10)</code> <br/>
 * Support
 * <code>{min or max}IntervalWith{index(First,Second,Third or Fourth) event or last event}In{time unit (Millis,Second,Minute or Hour)</code>
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
