package org.qe4g.request

import static org.qe4g.request.graph.EventNodes.*

import org.qe4g.Event
import org.qe4g.request.graph.EventNodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.blueprints.pgm.Graph
import com.tinkerpop.blueprints.pgm.Vertex
import com.tinkerpop.blueprints.pgm.impls.tg.TinkerGraph
import static org.qe4g.request.graph.VertexTypes.*;
import static org.qe4g.request.graph.EdgeTypes.*;
import static org.qe4g.request.graph.MyGraph.*;

class RequestDispatcher {

	final static Logger logger = LoggerFactory.getLogger(RequestDispatcher.class);
	
	private List<Request> requests;

	private Vertex eventTypeRegistry;

	public void onEvent(Event event) {
		if(event.isInconsistent()) {
			return;
		}
		def selectedRequests = requests.findAll {it.accept(event)}
		if(selectedRequests.isEmpty()) {
			logger.info("Ignored - Event {}", event);
			return
		}
		Vertex currentVertex = graph() << [event:event,type:EVENT]
		currentVertex << REFER << eventTypeRegistry
		selectedRequests.each { Request request -> request.onNodeEvent(currentVertex) }
	}

	public void shutdown() {
		requests*.get()
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
			engine.eventTypeRegistry =  graph() << [type:REQUEST]
			return engine
		}
	}

}
