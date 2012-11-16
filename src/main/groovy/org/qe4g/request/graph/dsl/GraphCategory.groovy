package org.qe4g.request.graph.dsl;

import java.awt.image.renderable.ContextualRenderedImageFactory;

import org.qe4g.request.evaluation.OccurResponse;
import org.qe4g.request.graph.EdgeTypes;
import org.qe4g.request.graph.VertexTypes;

import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.blueprints.pgm.Vertex;
import static org.qe4g.request.graph.VertexTypes.*
import static org.qe4g.request.graph.EdgeTypes.*
import static org.qe4g.request.graph.MyGraph.*
public class GraphCategory {

	static Vertex createVertex(VertexTypes type, Closure closure) {
		Vertex vertex = graph().addVertex("${type.name()}@" + (++vertexIndex))
		vertex.type = type
		if(closure != null) {
			closure.call(vertex)
		}
		return vertex
	}

	static Vertex createVertex(VertexTypes type) {
		return createVertex(type,null)
	}

	static Edge createLink(Vertex from, Vertex to, EdgeTypes type) {
		Edge edge = graph().addEgde("${type.name()}@" + (++vertexIndex), from, to, type.name())
		return edge
	}

	static List<Vertex> getFromMe(Vertex vertex, EdgeTypes type) {
		List<Vertex> result = vertex.getOutEdges(type.name()).collect { it.getInVertex() }
		return result
	}

	static List<Vertex> getToMe(Vertex vertex, EdgeTypes type) {
		List<Vertex> result = vertex.getInEdges(type.name()).collect { it.getOutVertex() }
		return result
	}

	static boolean evaluateOnStaticCriteria(Vertex evVertex, Vertex eventVertex) {
		return evVertex.evaluator.evaluateOnStaticCriteria(eventVertex)
	}

	static void applyFromHereToTheEnd(Vertex vertex, Closure c) {
		use(GraphCategory) {
			c.call(vertex)
			Vertex otherVertex = vertex.next()
			if(otherVertex != null) {
				otherVertex.applyFromHereToTheEnd(c)
			}
		}
	}

	static OccurResponse evaluate(Vertex vertex, Vertex eventVertex) {
		use(GraphCategory) {
			vertex.getFromMe(EVALUATOR_ATTACHED).collect { Vertex evalVertex ->
				def evaluator = evalVertex.evaluator
				OccurResponse response = evaluator.on(eventVertex,vertex);
				response.digest (edge.getInVertex(), vertex,eventVertex)
				return response
			}

			if(responses.size() > 1) {
				throw new IllegalStateException("This vertex [${vertex}] cannot be linked to more than one evaluator !")
			}
			return responses[0];
		}
	}

	private static int vertexIndex = 0
	
	static Iterable getContextualEvaluators(Vertex eventVertex, Vertex firstEvaluator) {
		use(GraphCategory) {
			Collection<Vertex> contextualEvaluators = eventVertex.getToMe(ATTACHED)*.getToMe(EVALUATOR_ATTACHED)
			if(contextualEvaluators.isEmpty()) {
				contextualEvaluators << PATH.createVertex({
					it.context = [:]
					it.createLink(firstEvaluator, EVALUATOR_ATTACHED)
				})
			}
			return contextualEvaluators.flatten()
		}
	}

	static boolean isAttached(Vertex vertex) {
		return vertex.getInEdges(ATTACHED.name()).find { true }
	}

	static Vertex previous(Vertex vertex) {
		use(GraphCategory) {
			def collect = vertex.getFromMe(PREVIOUS_EVENT)
			if(collect.size() > 1) {
				throw new IllegalStateException("This vertex [${vertex}] has multiple PREVIOUS edges !")
			}
			return collect[0]
		}
	}

	static Vertex next(Vertex vertex) {
		use(GraphCategory) {
			def collect = vertex.getFromMe(NEXT)
			if(collect.size() > 1) {
				throw new IllegalStateException("This vertex [${vertex}] has multiple NEXT edges !")
			}
			return collect[0]
		}
	}
}
