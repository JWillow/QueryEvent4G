package org.qe4g.request.pattern

import static org.qe4g.request.evaluation.OccurResponse.*
import static org.qe4g.request.graph.EdgeTypes.*
import static org.qe4g.request.graph.VertexTypes.*
import static org.qe4g.request.graph.MyGraph.*
import org.qe4g.request.evaluation.Evaluator;
import groovy.lang.Closure

import java.util.Collections;
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
	 * Entry point for a Pattern to perform the event correlation with others events 
	 * @param vEvent - Vertex of type Event
	 * @return {@link Collections} of Verteces of type {@link VertexTypes#PATH}, if paths are completed, else return an empty {@link Collections}
	 */
	public Collection<Vertex> correlate(final Vertex vEvent) {
		logger.debugG("*************************************************************");
		logger.debugG("correlate {}", vEvent);
		List<Vertex> vEvaluatorsSelected = vEvaluators.findAll {	it.evaluateOnStaticCriteria(vEvent)	}
		if(vEvaluatorsSelected.empty) {
			logger.infoG("Not found - Evaluator {} - Reset all ! ", vEvent);
			vEvent--
			vEvaluators.each { it.reset()}
			return []
		}

		List<Vertex> paths = select(vEvent,vEvaluatorsSelected)

		def globalResult =
				paths.findAll {Vertex vEvaluationContext ->
					Vertex vEvaluator = (1 % vEvaluationContext >> DEPEND_ON).unique()
					OccurResponse response = vEvaluator.evaluateOnOccurrenceCriteria(vEvaluationContext, vEvent)
					return (response == OK || response == OK_BUT_KEEP_ME) && (1 % vEvaluator >> NEXT).empty;
				}.collect {(1 % it << CURRENT_EVAL_CONTEXT).unique()}

		return globalResult
	}

	/**
	 * <p>Select all {@link VertexTypes#EVALUATION_CONTEXT} concern by {@link VertexTypes#EVENT}
	 * <p>Create new {@link VertexTypes#EVALUATION_CONTEXT} if event can be used to begin a 
	 * new Path
	 * <p>Remove Path if the event break it.
	 * 
	 * @param vEvent - {@link VertexTypes#EVENT}
	 * @param vEvaluatorsSelected of {@link VertexTypes#EVALUATOR}. They have been selected after a positive static evaluation on {@link Evaluator} associated.
	 * @return Verteces List of {@link VertexTypes#EVALUATION_CONTEXT}
	 */
	List<Vertex> select(final Vertex vEvent, List<Vertex> vEvaluatorsSelectedOnStaticEvaluation) {
		def selected = [];
		vEvaluatorsSelectedOnStaticEvaluation.each { Vertex vEvaluator ->
			List<Vertex> localSelection = primarySelectOfEvaluationContext(vEvent,vEvaluator);
			if(localSelection.empty) {
				searchAndRemoveInProgressEvaluationContext(vEvent,vEvaluator);
			} else {
				selected + localSelection.findAll {!searchOldEventAndRemoveEvaluationContext(vEvaluator, it)}
				.collect { Vertex vEvaluationContext ->
					attachOrCreateNewEvaluationContext(vEvent,vEvaluator,vEvaluationContext);
				}
			}
		}

		// PERMET LE DEBUT DU PATTERN
		if(selected.empty && vEvaluatorsSelectedOnStaticEvaluation.find{it.canBeginPattern}) {
			Vertex vEvaluationContext = graph() << [type:EVALUATION_CONTEXT,state:null,occur:0];
			Vertex vPath = graph() << PATH;
			vPath >> CURRENT_EVAL_CONTEXT >> vEvaluationContext;
			vEvaluationContext >> DEPEND_ON >> vEvaluatorsSelectedOnStaticEvaluation.last();
			vEvaluationContext >> EVALUATED >> vEvent;
			vPath >> FIRST_EVENT >> vEvent;
			vPath >> LAST_EVENT >> vEvent;
			logger.infoG("Created - EvaluationContext({})/Path({})/Evaluator({})",vEvaluationContext,vPath,vEvaluatorsSelectedOnStaticEvaluation.last());
			selected << vEvaluationContext;
		}

		if(selected.empty) {
			vEvaluatorsSelectedOnStaticEvaluation.each { Vertex vEvaluator ->
				logger.infoG("No Evaluation Context found for event {} ! we attached it directly to evaluator {}", vEvent, vEvaluator);
				(vEvent >> ATTACHED >> vEvaluator)
			};
		} else {
			logger.infoG("Evaluation Context [{}] found for event [{}]",selected,vEvent);
		}
		return selected
	}


	/**
	 * <p>Select  {@link VertexTypes#EVALUATION_CONTEXT} that are in {@link OccurResponse#KO_BUT_KEEP_ME} 
	 * or {@link OccurResponse#OK_BUT_KEEP_ME} state.
	 * <p>If no Verteces are found in the first step, we lookup if an evaluator can start a new pattern 
	 * @param vEvent - {@link VertexTypes#EVENT}
	 * @param vEvaluator - {@link VertexTypes#EVALUATOR}
	 * @return
	 */
	private List<Vertex> primarySelectOfEvaluationContext(final Vertex vEvent, final Vertex vEvaluator) {
		Evaluator evaluator = vEvaluator.evaluator;
		List<Vertex> selected = (1 % vEvaluator << DEPEND_ON).findAll { Vertex vEvaluationContext ->
			return ((vEvaluationContext.state == KO_BUT_KEEP_ME
			|| vEvaluationContext.state == OK_BUT_KEEP_ME)
			&& evaluator.evaluateRelationship(vEvaluationContext, vEvent)
			&& vEvaluationContext.areSameEventSet(vEvent));
		};

		if(selected.empty) {
			if(vEvaluator.canBeginPattern) {
				(1 % vEvaluator << DEPEND_ON).find {Vertex vEvaluationContext ->
					boolean result = (vEvaluationContext.state == OK
							&& (1 % vEvaluationContext << PREVIOUS).isEmpty()
							&& vEvaluationContext.areSameEventSet(vEvent));
					if(result == false) {
						return false;
					}
					selected << vEvaluationContext;
					// REPOSITIONNEMENT DU START_EVENT
					Vertex vPath = (1 % vEvaluationContext << CURRENT_EVAL_CONTEXT).unique();
					Vertex vFirstEvent = (1 % vPath >> FIRST_EVENT).unique();
					Vertex nextEvent = (1 % vFirstEvent << PREVIOUS).unique();
					vFirstEvent --;
					if(nextEvent != null) {
						vPath >> FIRST_EVENT >> nextEvent
					} else {
						vPath >> FIRST_EVENT >> vEvent
					}
					if((1 % vPath >> LAST_EVENT).isEmpty()) {
						vPath >> LAST_EVENT >> vEvent
					}
					return true;
				}
			}
			if(selected.empty) {
				logger.infoG("No Evaluation Context found at selected evaluator level")
			} else {
				logger.infoG("Recycle First Evaluation Context {}", selected[0]);
			}
		} else {
			logger.infoG("Evaluation Context found at selected evaluator level : {}", selected);
		}

		Vertex vPreviousEvaluator = (1 % vEvaluator << NEXT).unique();
		while(vPreviousEvaluator != null) {
			logger.debugG("Search on Evaluator : {}",vPreviousEvaluator);
			selected + (1 % vPreviousEvaluator << DEPEND_ON).findAll{Vertex vEvaluationContext ->
				logger.debugG("Test EvaluationContext : {}", vEvaluationContext);
				boolean result = ((1 % vEvaluationContext << PREVIOUS).empty
						&& vEvaluationContext.state != KO_BUT_KEEP_ME
						&& evaluator.evaluateRelationship(vEvaluationContext, vEvent));
				logger.debugG("Result : {}", result);
				return result;
			}
			if(!vPreviousEvaluator.evaluator.isOptional()) {
				break;
			}
			vPreviousEvaluator = (1 % vPreviousEvaluator << NEXT).unique();
		}

		return selected;
	}


	private void searchAndRemoveInProgressEvaluationContext(final Vertex vEvent, final Vertex vEvaluator) {
		Evaluator evaluator = vEvaluator.evaluator;
		(1 % vEvaluator << DEPEND_ON).findAll { Vertex vEvaluationContext ->
			return ((vEvaluationContext.state == OK)
			&& vEvaluationContext.areSameEventSet(vEvent));
		}.each {
			//it.removePath()
			it.reset()
		}
	}

	private Vertex attachOrCreateNewEvaluationContext(Vertex vEvent, Vertex vEvaluator, Vertex vEvaluationContext) {
		logger.debugG("attachOrCreateNewEvaluationContext")
		Vertex dependOnVEvaluator = (1 % vEvaluationContext >> DEPEND_ON).unique();
		Vertex vPath = (1 % vEvaluationContext << CURRENT_EVAL_CONTEXT).unique();
		if(!dependOnVEvaluator.getId().equals(vEvaluator.getId())) {
			Vertex newEvaluationContext = graph() << [type:EVALUATION_CONTEXT,state:null,occur:0];
			(vEvaluationContext << CURRENT_EVAL_CONTEXT) --
			newEvaluationContext << CURRENT_EVAL_CONTEXT << vPath;
			vEvaluationContext << PREVIOUS << newEvaluationContext;
			vEvaluator << DEPEND_ON << newEvaluationContext;
			logger.debugG("Created - EvaluationContext {}", newEvaluationContext);
			vEvaluationContext = newEvaluationContext;
		}
		vEvent << EVALUATED << vEvaluationContext;
		Vertex vLastEvent = (1 % vPath >> LAST_EVENT).unique();
		(vPath >> LAST_EVENT) --
		vPath >> LAST_EVENT >> vEvent;
		vEvent >> PREVIOUS >> vLastEvent;

		return vEvaluationContext
	}


	private boolean searchOldEventAndRemoveEvaluationContext(final Vertex vEvaluator, final Vertex vEvaluationContext) {
		logger.debugG("searchOldEventAndRemoveEvaluationContext")
		Evaluator evaluator = vEvaluator.evaluator;
		boolean toRemove = (1 % vEvaluator << ATTACHED).find {Vertex vOldEvent ->
			if(evaluator.evaluateRelationship(vEvaluationContext, vOldEvent)) {
				Vertex vLinkedEventPath = ( 1 % vEvaluationContext << CURRENT_EVAL_CONTEXT).unique()
				Vertex firstEvent = (1 % vLinkedEventPath >> FIRST_EVENT).unique();
				return vLinkedEventPath.reevaluate(vOldEvent.event.getTime(), firstEvent);
			}
			return false;
		};

		if(toRemove) {
			remove(vEvaluationContext);
			return true;
		}
		return false;
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
			vEvaluators << (graph() << [type:EVALUATOR,evaluator:evaluator,canBeginPattern:false])
			int size = vEvaluators.size()
			if(size > 1) {
				Vertex previousVEvaluator = vEvaluators[size - 2]
				previousVEvaluator >> NEXT >> vEvaluators[size - 1]
				if(previousVEvaluator.canBeginPattern == true && previousVEvaluator.evaluator.isOptional()) {
					vEvaluators[size - 1].canBeginPattern = true;
				}
			} else {
				vEvaluators[0].canBeginPattern = true;
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


