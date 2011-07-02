package org.homework.mcep.request

import org.homework.mcep.Event

class Engine {
	private List<RequestEngine> requestEngines;

	public void process(Event event) {
		requestEngines.each { it.onEvent(event)}
	}

	// -------------
	// BUILDER PART
	// ------------
	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private List<RequestEngine> requestEngines = []

		public Builder withRequestEngine(RequestEngine requestEngine) {
			requestEngines << requestEngine
			return this
		}

		public Engine build() {
			Engine engine = new Engine()
			engine.requestEngines = this.requestEngines;
			return engine
		}
	}

}
