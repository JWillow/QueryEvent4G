package org.homework.mcep.request.eventlistener

import java.text.SimpleDateFormat;
import java.util.List;

import org.homework.mcep.Event
import org.homework.mcep.request.EventListener;
import org.homework.mcep.request.Function;
import org.homework.mcep.request.Functions;
import org.homework.mcep.request.RequestDefinition;
import org.homework.mcep.request.Window;

class ScheduledNotification implements EventListener, Functions {

	List<Function> functionsToNotified;
	int interval
	boolean reset
	long currentTime = -1

	private void initCurrentTimeIfNecessary(long date) {
		if(currentTime != -1) {
			return
		}
		currentTime = ((long) date / 1000) * 1000
	}

	public void beforeEventProcessing(RequestDefinition requestDefinition, Collection<Window> windows, Event event) {
		long dateInMillis = event.attributes.time
		initCurrentTimeIfNecessary(dateInMillis)
		if(currentTime + interval < dateInMillis) {
			functionsToNotified.each {
				it.get()
				if(reset) {
					it.reset()
				}
			}
			currentTime += interval
		}
	}

	public List<Function> getFunctions() {
		return functionsToNotified;
	}

	public void afterEventProcessed(RequestDefinition requestDefinition, Collection<Window> windows, Event event) {
	}


	// ------------
	// BUILDER PART
	// ------------
	private ScheduledNotification() {}

	public static Builder builder() {
		return new Builder()
	}

	public static class Builder {

		List<Function> functionsToNotified = []
		int interval = 1000
		boolean reset = true

		public Builder withInterval(int interval) {
			this.interval = interval
			return this
		}

		public Builder reset(boolean reset) {
			this.reset = reset
			return this
		}

		public Builder onFunction(Function functionToNotified) {
			this.functionsToNotified << functionToNotified
			return this;
		}

		public ScheduledNotification build() {
			ScheduledNotification sn = new ScheduledNotification();
			sn.functionsToNotified = this.functionsToNotified
			sn.interval = this.interval
			sn.reset = this.reset
			return sn;
		}
	}
}
