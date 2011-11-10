package org.qe4g.request.pattern

import static org.qe4g.request.evaluation.Response.*
import static org.qe4g.request.graph.EdgeTypes.*
import static org.qe4g.request.graph.MyGraph.*
import groovy.lang.Closure

import java.util.List
import java.util.concurrent.CopyOnWriteArrayList

import org.qe4g.Event;
import org.qe4g.request.evaluation.Evaluator
import org.qe4g.request.evaluation.Response
import org.qe4g.request.graph.Path
import org.qe4g.request.graph.VertexTypes

import com.tinkerpop.blueprints.pgm.Edge
import com.tinkerpop.blueprints.pgm.Vertex

class Pattern {

	private Vertex vContextualEvaluators;

	List<Evaluator> evaluators

	/**
	 * <p>Closure that define the acceptation strategy of published {@link Event}. If an {@link Event} is not accepted then this {@link Event} will not influence the pattern detection algorithm.
	 * <p>By default all {@link Event} are accepted.
	 */
	def accept = { true }

	public Collection<List<Event>> correlate(Vertex eventVertex) {
		def results = []
		for(Edge edgeEvaluation:vContextualEvaluators.getInEdges(REGISTER_TO.name())) {
			def cVertex = edgeEvaluation.getOutVertex()
			on(cVertex,eventVertex)
			if(cVertex.completed == true) {
				// CREATE PATH
				results << cVertex.getInEdges().collect{it}.sort {it.order}.collect { Edge edge ->
					edge.getOutVertex().event }
				graph().removeVertex(cVertex)
			}
		}
		if(results.isEmpty() && !eventVertex.hasInEdges(EVALUATION.name())) {
			Vertex cVertex = graph().addVertex(null)
			cVertex.context = [:]
			cVertex.completed = false
			cVertex.indexNextEvaluatorToUse = 0
			on(cVertex,eventVertex)
			if(eventVertex.hasInEdges(EVALUATION.name())) {
				graph().addEdge(null, cVertex, vContextualEvaluators, REGISTER_TO.name())
			} else {
				graph().removeVertex(cVertex)
			}
		}
		return results
	}

	public void on(Vertex cVertex, Vertex eventVertex) {
		int index = cVertex['indexNextEvaluatorToUse']
		Response response = evaluators[index].on(eventVertex, cVertex)
		response.digest(evaluators, cVertex,eventVertex)
		if(response.equals(Response.CONTINUE_WITH_NEXT_EVALUATOR) && cVertex.completed == false) {
			on(cVertex, eventVertex)
		}
	}

	// ------------
	// BUILDER PART
	// ------------
	protected Pattern() {
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private Closure accept = null;
		private List<Evaluator> evaluators = [];

		public Builder addEvaluator(Evaluator evaluator) {
			evaluators << evaluator;
			return this;
		}

		public Builder withAccept(Closure accept) {
			this.accept = accept
			return this;
		}

		public Pattern build() {
			Pattern pattern = new Pattern();
			if(accept != null) {
				pattern.accept = this.accept;
			}
			pattern.evaluators = new CopyOnWriteArrayList(evaluators);
			pattern.vContextualEvaluators = graph().addVertex(VertexTypes.INDEX_CONTEXTUAL_EVALUATOR.name() + "@" + this)
			pattern.vContextualEvaluators.setProperty('type',VertexTypes.INDEX_CONTEXTUAL_EVALUATOR)
			return pattern;
		}
	}

}
