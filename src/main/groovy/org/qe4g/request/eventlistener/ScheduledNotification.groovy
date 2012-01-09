package org.qe4g.request.eventlistener

import java.util.List

import org.qe4g.Event
import org.qe4g.request.Function
import org.qe4g.request.Request

class ScheduledNotification {//implements EventProcessedListener, Functions {

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

	public void beforeEventProcessing(Request request, Event event) {
		long dateInMillis = event.attributes.time
		initCurrentTimeIfNecessary(dateInMillis)
		if(currentTime + interval < dateInMillis) {
			functionsToNotified.each {
				it.get(currentTime)
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

	/**
	 * Do nothing
	 * @see org.qe4g.request.EventListener#afterEventProcessed(org.qe4g.request.Request, java.util.Collection<org.qe4g.request.Pattern.Evaluation>)
	 */
	public void afterEventProcessed(Request request, List events) {
		// DO NOTHING
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
