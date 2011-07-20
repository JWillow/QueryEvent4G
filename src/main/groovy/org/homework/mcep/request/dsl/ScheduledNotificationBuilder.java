package org.homework.mcep.request.dsl;

import java.util.List;
import java.util.Map;

import org.homework.mcep.dsl.builder.Builder;
import org.homework.mcep.dsl.builder.GroovySupportingBuilder;
import org.homework.mcep.request.Function;
import org.homework.mcep.request.eventlistener.ScheduledNotification;

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
				internalBuilder.withInterval(getInterval(attribut,
						(Integer) entry.getValue()));
			} else if (attribut.equals("reset")) {
				internalBuilder.reset((Boolean) entry.getValue());
			} else {
				throw new IllegalArgumentException();
			}
		}
		return this;
	}

	private int getInterval(String attribut, Integer value) {
		if (attribut.equals("intervalInSecond")) {
			return value * 1000;
		} else if (attribut.equals("intervalInHour")) {
			return value * 60 * 1000;
		} else if (attribut.equals("intervalInMillis")) {
			return value;
		}
		throw new IllegalArgumentException(
				"attribut "
						+ attribut
						+ " not expected ! Supported (intervalInMillis,intervalInSecond,intervalInHour)");
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
