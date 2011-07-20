package org.homework.mcep.request.function

import java.util.List

import org.homework.mcep.Event
import org.homework.mcep.request.Function
import org.homework.mcep.request.Request

class ClosureFunction implements Function {
	private final static String KEYWORD = "get"

	Closure notification;
	Closure core;
	Map<Object,Object> context = [KEYWORD :null];

	void onPatternDetection(Request request, List<Event> events) {
		core(context,events)
	}

	void get() {
		notification(context);
	}

	void reset() {
		context.clear();
		context.put KEYWORD, null
	}

	// ------------
	// BUILDER PART
	// ------------
	private ClosureFunction() {}

	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		private Closure core;
		private Closure notification= {println "Notification $it"};

		public Builder withNotification(Closure notification) {
			this.notification = notification
			return this
		}

		public Builder withCore(Closure core) {
			this.core = core
			return this
		}

		public ClosureFunction build() {
			ClosureFunction cf = new ClosureFunction()
			cf.core = this.core
			cf.notification = this.notification
			return cf
		}
	}
}
