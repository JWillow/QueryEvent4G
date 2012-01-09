package org.qe4g.request.pattern

import static org.qe4g.request.evaluation.OccurResponse.*
import static org.qe4g.request.graph.EdgeTypes.*
import static org.qe4g.request.graph.VertexTypes.*
import static org.qe4g.request.graph.MyGraph.*
import static org.qe4g.request.evaluation.Evaluator.Type.*
import org.qe4g.request.evaluation.Evaluator;
import groovy.lang.Closure

import java.util.List
import java.util.concurrent.CopyOnWriteArrayList

import org.qe4g.Event;
import org.qe4g.request.evaluation.Evaluator
import org.qe4g.request.evaluation.OccurResponse
import org.qe4g.request.graph.EdgeTypes;
import org.qe4g.request.graph.VertexTypes
import org.qe4g.request.graph.dsl.GraphCategory;
import org.qe4g.request.graph.dsl.Language;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import ch.qos.logback.core.boolex.EvaluationException;

import com.tinkerpop.blueprints.pgm.Edge
import com.tinkerpop.blueprints.pgm.Vertex

class Pattern {

	final static Logger logger = LoggerFactory.getLogger(Pattern.class);

	static {
		Language.load()
	}

	List<Vertex> vEvaluators = []

	/**
	 * <p>Closure that define the acceptation strategy of published {@link Event}. If an {@link Event} is not accepted then this {@link Event} will not influence the pattern detection algorithm.
	 * <p>By default all {@link Event} are accepted.
	 */
	def accept = { true }

	/**
	 * Select path from event passed in parameter
	 * @param vEvent
	 * @return Vertex List of Path Type
	 */
	List<Vertex> selectPaths(final Vertex vEvent, List<Vertex> vEvaluatorsSelected) {
		def selected = [];
		vEvaluatorsSelected.each { Vertex vEvaluator ->
			Evaluator evaluator = vEvaluator.evaluator;
			// Current level search
			selected = (1 % vEvaluator << ATTACHED).findAll { Vertex vEvaluationContext ->
				return ((vEvaluationContext.state == KO_BUT_KEEP_ME
				|| vEvaluationContext.state == OK_BUT_KEEP_ME)
				&& evaluator.evaluateRelationship(vEvaluationContext, vEvent));
			};
			if(selected.empty) {
				logger.infoG("No Evaluation Context found at selected evaluator level")
			} else {
				logger.infoG("Evaluation Context found at selected evaluator level : {}", selected);
			}

			List<Vertex> vEvaluationContextsSelected = [];
			Vertex vPreviousEvaluator = (1 % vEvaluator << NEXT).unique();
			while(vPreviousEvaluator != null) {
				logger.debugG("Search on Evaluator : {}",vPreviousEvaluator);
				selected + (1 % vPreviousEvaluator << DEPEND_ON).findAll{Vertex vEvaluationContext ->
					logger.debugG("Test EvaluationContext : {}", vEvaluationContext);
					return (vEvaluationContext.state != KO_BUT_KEEP_ME
					&& evaluator.evaluateRelationship(vEvaluationContext, vEvent));
				}
				if(!vPreviousEvaluator.evaluator.isOptional()) {
					break;
				}
				vPreviousEvaluator = (1 % vPreviousEvaluator << NEXT).unique();
			}

			selected = selected.collect { Vertex vEvaluationContext ->
				if( (1 % vEvaluationContext << DEPEND_ON).unique() != vEvaluator) {
					Vertex newEvaluationContext = graph() << [type:EVALUATION_CONTEXT,state:null,occur:0];
					Vertex vPath = (1 % vEvaluationContext << CURRENT_EVAL_CONTEXT).unique();
					(vEvaluationContext << CURRENT_EVAL_CONTEXT) --
					newEvaluationContext << CURRENT_EVAL_CONTEXT << vPath;
					vEvaluationContext << PREVIOUS << newEvaluationContext;
					vEvaluator << DEPEND_ON << newEvaluationContext;
					vEvaluationContext = newEvaluationContext;

				}
				vEvent << EVALUATED << vEvaluationContext;
				return vEvaluationContext;
			}
			if(evaluator.getType() != GLOBAL && selected.size() > 1) {
				logger.debugG("Evaluator of type SINGLE, we need to remove the one EvaluationContext the most old !");
				remove(selected[0]);
			}
		}

		// PERMET LE DEBUT DU PATTERN
		if(vEvaluatorsSelected.contains(vEvaluators[0]) && selected.empty) {
			Vertex vEvaluationContext = graph() << [type:EVALUATION_CONTEXT,state:null,occur:0];
			Vertex vPath = graph() << PATH;
			vPath >> CURRENT_EVAL_CONTEXT >> vEvaluationContext;
			vEvaluationContext >> DEPEND_ON >> vEvaluators[0];
			vEvaluationContext >> EVALUATED >> vEvent;
			logger.infoG("New EvaluationContext{}/Path{}/Evaluator{} created",vEvaluationContext,vPath,vEvaluators[0]);
			selected << vEvaluationContext;
		}

		
		if(selected.empty) {
			vEvaluatorsSelected.each { Vertex vEvaluator ->
				logger.infoG("No Evaluation Context found for event {} ! we attached it directly to evaluator {}", vEvent, vEvaluator);
				(vEvent >> ATTACHED >> vEvaluator)
			};
		} else {
			logger.infoG("Evaluation Context [{}]found for event [{}]",selected,vEvent);
		}
		return selected
	}

	/**
	 * @param vEvent 
	 * @return Collection of Vertex of type Path
	 */
	public Collection<Vertex> correlate(final Vertex vEvent) {
		logger.debugG("correlate {}", vEvent);
		List<Vertex> vEvaluatorSelected = evaluateOnStaticCriteria(vEvent)
		if(!checkEvaluatorAndResetIfNecessary(vEvent, vEvaluatorSelected)) {
			return []
		}

		List<Vertex> paths = selectPaths(vEvent,vEvaluatorSelected)

		def globalResult =
				paths.findAll {Vertex vEvaluationContext ->
					Vertex vEvaluator = (1 % vEvaluationContext >> DEPEND_ON).unique()
					OccurResponse response = vEvaluator.evaluator.evaluateOnOccurrenceCriteria(vEvaluationContext, vEvent)
					return (response == OK || response == OK_BUT_KEEP_ME) && (1 % vEvaluator >> NEXT).empty;
				}.collect {(1 % it << CURRENT_EVAL_CONTEXT).unique()}

		logger.debugG("correlate, found {}", globalResult);
		return globalResult
	}

	private void reset() {
		vEvaluators.each { evaluator ->
			(1 % evaluator << DEPEND_ON).each { Vertex vEvaluationContext ->
				Vertex vPath = (1 % vEvaluationContext << CURRENT_EVAL_CONTEXT).unique();
				(0 % vEvaluationContext << PREVIOUS).each {
					if(vPath == null) {
						vPath = (1 % vEvaluationContext << CURRENT_EVAL_CONTEXT).unique();
					}
					it--;
				}
				vEvaluationContext--
				vPath--
			}
		}
	}

	private void remove(Vertex vEvaluationContext) {
		logger.infoG("Remove EvaluationContext {} and all dependencies", vEvaluationContext);
		Vertex vPath = (1 % vEvaluationContext << CURRENT_EVAL_CONTEXT).unique()
		logger.debugG("Remove Path {}", vPath);
		vPath --;
		List<Vertex> vEvents = [];
		List<Vertex> vEvaluationContexts = [vEvaluationContext];
		vEvaluationContexts + (0 % vEvaluationContext >> PREVIOUS)
		vEvaluationContexts + (0 % vEvaluationContext << PREVIOUS)
		 
		vEvaluationContexts.each {
			logger.debugG("Remove EvaluationContext {}", it)
			vEvents + (1 % it >> EVALUATED)
			it--;
		}
		
		vEvents.findAll{(1 % it << EVALUATED).empty && (1 % it >> ATTACHED).empty}
		.each{logger.debugG("Delete Event {}",it); it--};

	}


	private boolean checkEvaluatorAndResetIfNecessary(final Vertex vEvent, final List<Vertex> vEvaluatorsSelected) {
		if(vEvaluatorsSelected.empty) {
			logger.infoG("No evaluator found for {}; reset all ! ", vEvent);
			vEvent--
			reset()
			return false
		}
		return true
	}

	private List<Vertex> evaluateOnStaticCriteria(Vertex vEvent) {
		List<Vertex> vEvaluatorSelected = []
		vEvaluators.each {
			if(it.evaluator.evaluateOnStaticCriteria(vEvent)) {
				it >> ATTACHED >> vEvent
				vEvaluatorSelected << it
			}
		}
		return vEvaluatorSelected
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
		private List<Vertex> vEvaluators = []

		public Builder addEvaluator(Evaluator evaluator) {
			vEvaluators << (graph() << [type:EVALUATOR,evaluator:evaluator])
			int size = vEvaluators.size()
			if(size > 1) {
				vEvaluators[size - 2] >> NEXT >> vEvaluators[size - 1]
			}
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
			pattern.vEvaluators = this.vEvaluators
			return pattern;
		}
	}
}


