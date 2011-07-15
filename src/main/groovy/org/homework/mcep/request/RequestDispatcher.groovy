package org.homework.mcep.request

import org.homework.mcep.Event

class RequestDispatcher {
	
	private List<Request> requests;

	public void onEvent(Event event) {
		if(!event.isInconsistent()) {
			requests.each { it.onEvent(event)}
		}
	}

	public void get() {
		requests.each { it.get()}
	}
	
	// -------------
	// BUILDER PART
	// ------------
	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private List<Request> requestEngines = []

		public Builder withRequestEngine(Request requestEngine) {
			requestEngines << requestEngine
			return this
		}

		public RequestDispatcher build() {
			RequestDispatcher engine = new RequestDispatcher()
			engine.requests = this.requestEngines;
			return engine
		}
	}

}
