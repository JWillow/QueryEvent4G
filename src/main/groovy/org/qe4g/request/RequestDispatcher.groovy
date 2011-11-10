package org.qe4g.request

import static org.qe4g.request.graph.EventNodes.*

import org.qe4g.Event

import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.blueprints.pgm.Graph
import com.tinkerpop.blueprints.pgm.Vertex
import com.tinkerpop.blueprints.pgm.impls.tg.TinkerGraph

import static org.qe4g.request.graph.EdgeTypes.*;
import static org.qe4g.request.graph.MyGraph.*;

class RequestDispatcher {

	private List<Request> requests;
	private Collection<Vertex> vertices = [];

	private Vertex predecessor = null;

	public void onEvent(Event event) {
		if(!event.isInconsistent()) {
			def selectedRequests = requests.findAll {it.accept(event)}
			// Register the Node ?
			if(selectedRequests.isEmpty()) {
				return
			}
			Vertex currentVertex = createFrom(event)
			handleAncestrorByTimeRelationship(currentVertex)
			//handleAncestrorByTypeRelationship(currentVertex)
			//handleLinkedByPropertiesRelationship(currentVertex)
			selectedRequests.each { Request request -> request.onNodeEvent(currentVertex) }
			//currentVertex.getInEdges(EVALUATION.name()).
		}
	}

	private void handleLinkedByPropertiesRelationship(Vertex vertex) {
		Event event = vertex.event
		vertices.each { Vertex tmpVertex ->
			if(tmpVertex.getId().equals(vertex.getId())) {
				return
			}
			def sharedProperties = []
			tmpVertex.event.attributes.each { String key, value ->
				if(event.attributes[key] == value) {
					sharedProperties << key
				}
			}
			if(sharedProperties.isEmpty()) {
				return
			}
			graph().addEdge(null, vertex, tmpVertex, LINKED_BY_PROPERTIES.name());
		}
	}

	private void handleAncestrorByTypeRelationship(Vertex vertex) {
		Vertex ancestror = vertices.find { Vertex tmpVertex -> areEquals(vertex,tmpVertex) }
		if(ancestror) {
			graph().addEdge(null, vertex, ancestror, ANCESTROR_BY_TIME.name());
			vertices.remove(ancestror)
		}
		vertices.add(vertex);
	}

	private void handleAncestrorByTimeRelationship(Vertex vertex) {
		if(predecessor != null) {
			Edge edge = graph().addEdge(null, vertex, predecessor, ANCESTROR_BY_TIME.name());
		}
		predecessor = vertex;
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

		private void registerShutdownHook(final RequestDispatcher engine ) {
			// Registers a shutdown hook for the Neo4j and index service instances
			// so that it shuts down nicely when the VM exits (even if you
			// "Ctrl-C" the running example before it's completed)
			Runtime.getRuntime().addShutdownHook( new Thread()	{
						@Override
						public void run(){
							engine.shutdown();
						}
					} );
		}
		public RequestDispatcher build() {
			RequestDispatcher engine = new RequestDispatcher()
			engine.requests = this.requestEngines;
			registerShutdownHook(engine)
			return engine
		}
	}

}
