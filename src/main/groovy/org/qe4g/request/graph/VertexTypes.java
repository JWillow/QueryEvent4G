package org.qe4g.request.graph;

import org.qe4g.Event;
import org.qe4g.request.evaluation.Evaluator;
import org.qe4g.request.graph.operation.EvaluatorBehaviour;
import org.qe4g.request.graph.operation.PathBehaviour;

import com.tinkerpop.blueprints.pgm.Vertex;

/**
 * Regroup all verteces types used inside the program to correlate event
 */
public enum VertexTypes {

	/**
	 * Identify a {@link Vertex} with an {@link Event} associated. In other
	 * words, represent the event inside the graph model
	 */
	EVENT(null),

	/**
	 * Identify a {@link Vertex} with an {@link Evaluator} associated. In other
	 * words, represent the evaluator inside the graph model
	 */
	EVALUATOR(new EvaluatorBehaviour()),

	/**
	 * For a given evaluator, represent the events set evaluated they are
	 * correlate between them. An evaluation context is in relation with events
	 * and one evaluator
	 */
	EVALUATION_CONTEXT(new org.qe4g.request.graph.operation.EvaluationContextBehaviour()),

	/**
	 * Represent the relation between the all set of evaluation context. If all
	 * evaluator are linked with evaluation context, then we have complete an
	 * path.
	 */
	PATH(new PathBehaviour()), REQUEST(null);

	Behaviour behaviour;

	private VertexTypes(Behaviour behaviour) {
		this.behaviour = behaviour;
	}
	
	void applyBehaviour(Vertex vertex) {
		if(behaviour != null) {
			behaviour.apply(vertex);
		}
	}

	public interface Behaviour {
		void apply(Vertex vertex);
	}
}
