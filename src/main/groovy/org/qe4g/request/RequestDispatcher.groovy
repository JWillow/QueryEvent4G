package org.qe4g.request

import org.neo4j.graphdb.GraphDatabaseService
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Transaction
import org.neo4j.kernel.EmbeddedGraphDatabase
import org.qe4g.Event
import org.neo4j.graphdb.Node
import org.neo4j.graphdb.Relationship
import static org.qe4g.request.graph.RelTypes.*;
import static org.qe4g.request.graph.EventNodes.*;

class RequestDispatcher {

	private List<Request> requests;
	private GraphDatabaseService graphDb
	private Collection<Node> nodes = [];

	private Node predecessor = null;

	public void onEvent(Event event) {
		if(!event.isInconsistent()) {
			def selectedRequests = requests.findAll {it.accept(event)}
			// Register the Node ?
			if(selectedRequests.isEmpty()) {
				return
			}
			Transaction tx = graphDb.beginTx();
			Node currentNode = null;
			try {
				currentNode = createNodeFrom(graphDb, event)
				handleAncestrorByTimeRelationship(currentNode)
				handleAncestrorByTypeRelationship(currentNode)
				handleLinkedByPropertiesRelationship(currentNode)
				tx.success();
			} finally {
				tx.finish();
			}
			if(currentNode) {
				selectedRequests.each { Request request -> request.onNodeEvent(currentNode) }
			}
		}
	}

	private void handleLinkedByPropertiesRelationship(Node node) {
		nodes.each { Node tmpNode ->
			def sharedProperties = []
			tmpNode.getPropertyKeys().each { String key ->
				if(!isPublicProperty(key)) {
					return
				}
				if(node.getProperty(key,null) == tmpNode.getProperty(key)) {
					sharedProperties << key
				}
			}
			if(sharedProperties.isEmpty()) {
				return
			}
			Relationship relationship = node.createRelationshipTo(tmpNode, LINKED_BY_PROPERTIES);
			relationship.setProperty("attached", sharedProperties.toArray(new String[sharedProperties.size()]))
		}
	}

	private void handleAncestrorByTypeRelationship(Node node) {
		Node ancestror = nodes.find { Node tmpNode -> areEquals(tmpNode,node) }
		if(ancestror) {
			node.createRelationshipTo(ancestror, ANCESTROR_BY_TYPE)
			nodes.remove(ancestror)
		}
		nodes.add(node);
	}

	private void handleAncestrorByTimeRelationship(Node node) {
		if(predecessor != null) {
			node.createRelationshipTo(predecessor, ANCESTROR_BY_TIME)
		}
		predecessor = node;
	}


	public void shutdown() {
		requests*.get()
		graphDb.shutdown();
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
			engine.graphDb = new EmbeddedGraphDatabase("/c:/dev/datas/graphdata");
			registerShutdownHook(engine)
			return engine
		}
	}

}
