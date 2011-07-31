package org.qe4g.request.function

import java.util.List

import org.qe4g.Event
import org.qe4g.request.Function
import org.qe4g.request.Request

class BasicFunction implements Function {
	Notifier notification;
	Core core;
	Map<Object,Object> context = [:];

	void onPatternDetection(Request request, List<Event> events) {
		core.onPatternDetection context, events
	}

	void get(long at) {
		notification.get context;
	}

	void reset() {
		context.clear();
	}

	// ------------
	// BUILDER PART
	// ------------
	private BasicFunction() {}

	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		private Core core;
		private Notifier notification= {println "Notification $it"} as Notifier;

		public Builder withNotification(Closure notification) {
			this.notification = notification as Notifier
			return this
		}

		public Builder withNotification(Notifier notification) {
			this.notification = notification
			return this
		}
		
		public Builder withCore(Closure core) {
			this.core = core as Core
			return this
		}
		
		public Builder withCore(Core core) {
			this.core = core
			return this
		}
		public BasicFunction build() {
			BasicFunction cf = new BasicFunction()
			cf.core = this.core
			cf.notification = this.notification
			return cf
		}
	}
}
