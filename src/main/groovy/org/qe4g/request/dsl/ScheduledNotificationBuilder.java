package org.qe4g.request.dsl;

import java.util.List;
import java.util.Map;

import org.qe4g.dsl.builder.Builder;
import org.qe4g.dsl.builder.GroovySupportingBuilder;
import org.qe4g.request.Function;
import org.qe4g.request.eventlistener.ScheduledNotification;

public class ScheduledNotificationBuilder implements
		GroovySupportingBuilder<ScheduledNotification> {

	private ScheduledNotification.Builder internalBuilder = ScheduledNotification
			.builder();

	public ScheduledNotification build() {
		ScheduledNotification sn = internalBuilder.build();
		internalBuilder = ScheduledNotification.builder();
		return sn;
	}

	public GroovySupportingBuilder withData(Object value) {
		throw new UnsupportedOperationException();
	}

	public GroovySupportingBuilder withAttributes(Map<String, Object> attributes) {
		for (Map.Entry<String, Object> entry : attributes.entrySet()) {
			String attribut = entry.getKey();
			if (attribut.equals("interval")) {
				internalBuilder.withInterval((Integer) entry.getValue());
			} else if (attribut.startsWith("interval")) {
				internalBuilder.withInterval(getTime(attribut,
						(Integer) entry.getValue()));
			} else if (attribut.equals("reset")) {
				internalBuilder.reset((Boolean) entry.getValue());
			} else {
				throw new IllegalArgumentException();
			}
		}
		return this;
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

	public GroovySupportingBuilder withBuilder(Builder builder) {
		Object object = builder.build();
		if (object instanceof Function) {
			internalBuilder.onFunction((Function) object);
		} else {
			throw new UnsupportedOperationException();
		}
		return this;
	}
}
