package org.qe4g.request.pattern

import groovy.lang.Closure

import java.util.List
import java.util.concurrent.CopyOnWriteArrayList

import org.qe4g.Event
import org.qe4g.request.evaluation.Evaluator
import org.qe4g.request.evaluation.EventIdentity;
import org.qe4g.request.evaluation.Response;
import org.qe4g.request.graph.Path;
import org.qe4g.request.graph.RelTypes;
import org.qe4g.request.graph.Traverser;
import org.qe4g.request.pattern.ContextualPathEvaluator.State
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.TraversalDescription;

import static org.neo4j.graphdb.Direction.*;
import static org.qe4g.request.pattern.ContextualPathEvaluator.State.*

class Pattern {

	List<Evaluator> evaluators

	/**
	 * <p>Closure that define the acceptation strategy of published {@link Event}. If an {@link Event} is not accepted then this {@link Event} will not influence the pattern detection algorithm.
	 * <p>By default all {@link Event} are accepted.
	 */
	def accept = { true }

	public Collection<Path> correlate(Node nodeEvent) {
		Traverser traverser = new Traverser(evaluators:evaluators)
		traverser.traverse(nodeEvent)
		return traverser.getSelectedPath()
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
		private Collection eventIdentities = [];
		//private EventIdentitiesBuilder eib = EventIdentitiesBuilder.builder();

		public Builder addEvaluator(Evaluator evaluator) {
			evaluators << evaluator;
			//eib.extractAttributeUsed(evaluator);
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
			pattern.evaluators = new CopyOnWriteArrayList(evaluators.reverse());
			//pattern.eventIdentities = eib.giveMeEventIdentities()
			return pattern;
		}
	}

}
