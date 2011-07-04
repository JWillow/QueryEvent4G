package org.homework.mcep.request.function

import java.text.SimpleDateFormat;

import org.homework.mcep.Event
import org.homework.mcep.request.EventListener;
import org.homework.mcep.request.RequestDefinition;
import org.homework.mcep.request.Window;

class ScheduledNotification implements EventListener {

	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
	
	Closure dateExtractor;
	List<Integer> functionsIndexHandled;
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
		long dateInMillis = dateExtractor(event)
		initCurrentTimeIfNecessary(dateInMillis)
		if(currentTime + interval < dateInMillis) {
			println "-- Notification at ${sdf.format(new Date(currentTime + interval))}"
			if(functionsIndexHandled.isEmpty()) {
				requestDefinition.functions*.get()
				if(reset) {
					requestDefinition.functions*.reset()
				}
			} else {
				functionsIndexHandled.each { index ->
					requestDefinition.functions[index].get()
					if(reset) {
						requestDefinition.functions[index].reset()
					}
				}
			}
			currentTime += interval
		}
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

		private Closure dateExtractor = {System.currentTimeMillis()}
		List<Integer> functionsIndexHandled = []
		int interval = 1000
		boolean reset = true

		public Builder withDateExtractor(Closure dateExtractor) {
			this.dateExtractor = dateExtractor
			return this
		}

		public Builder withInterval(int interval) {
			this.interval = interval
			return this
		}

		public Builder reset(boolean reset) {
			this.reset = reset
			return this
		}

		public Builder onFunction(List<Integer> functionIndexes) {
			functionsIndexHandled = functionsIndexHandled
			return this;
		}

		public ScheduledNotification build() {
			ScheduledNotification sn = new ScheduledNotification();
			sn.dateExtractor = this.dateExtractor
			sn.functionsIndexHandled = this.functionsIndexHandled
			sn.interval = this.interval
			sn.reset = this.reset
			return sn;
		}
	}
}
