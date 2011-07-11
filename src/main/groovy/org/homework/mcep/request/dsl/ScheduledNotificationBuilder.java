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
			} else if (attribut.equals("reset")) {
				internalBuilder.reset((Boolean) entry.getValue());
			} else {
				throw new IllegalArgumentException();
			}
		}
		return this;
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
