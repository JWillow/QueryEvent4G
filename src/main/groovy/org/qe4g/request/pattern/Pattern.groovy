package org.qe4g.request.pattern

import static org.qe4g.request.evaluation.OccurResponse.*
import static org.qe4g.request.graph.EdgeTypes.*
import static org.qe4g.request.graph.VertexTypes.*
import static org.qe4g.request.graph.MyGraph.*
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
	 * @param vEvent 
	 * @return Collection of Vertex of type Path
	 */
	public Collection<Vertex> correlate(final Vertex vEvent) {
		logger.debugG("*************************************************************");
		logger.debugG("correlate {}", vEvent);
		List<Vertex> vEvaluatorSelected = evaluateOnStaticCriteria(vEvent)
		if(!checkEvaluatorAndResetIfNecessary(vEvent, vEvaluatorSelected)) {
			return []
		}

		List<Vertex> paths = select(vEvent,vEvaluatorSelected)

		def globalResult =
				paths.findAll {Vertex vEvaluationContext ->
					Vertex vEvaluator = (1 % vEvaluationContext >> DEPEND_ON).unique()
					OccurResponse response = vEvaluator.evaluator.evaluateOnOccurrenceCriteria(vEvaluationContext, vEvent)
					return (response == OK || response == OK_BUT_KEEP_ME) && (1 % vEvaluator >> NEXT).empty;
				}.collect {(1 % it << CURRENT_EVAL_CONTEXT).unique()}

		return globalResult
	}

	private boolean areSameEventSet(Vertex vEvaluationContext, Vertex vEvent) {
		Event currentEvaluationContextEvent = null;
		List<Vertex> associatedVEvents = (1 % vEvaluationContext >> EVALUATED)
		if(associatedVEvents.isEmpty()) {
			return true;
		}
		return associatedVEvents[0].event.names == vEvent.event.names && associatedVEvents[0].event.attributes == vEvent.event.attributes;
	}

	private List<Vertex> primarySelectOfEvaluationContext(final Vertex vEvent, final Vertex vEvaluator) {
		Evaluator evaluator = vEvaluator.evaluator;
		List<Vertex> selected = (1 % vEvaluator << DEPEND_ON).findAll { Vertex vEvaluationContext ->
			return ((vEvaluationContext.state == KO_BUT_KEEP_ME
			|| vEvaluationContext.state == OK_BUT_KEEP_ME)
			&& evaluator.evaluateRelationship(vEvaluationContext, vEvent) 
			&& areSameEventSet(vEvaluationContext, vEvent));
		};

		if(selected.empty) {
			if(vEvaluator.canBeginPattern) {
				(1 % vEvaluator << DEPEND_ON).find {Vertex vEvaluationContext ->
					boolean result = (vEvaluationContext.state == OK
							&& (1 % vEvaluationContext << PREVIOUS).isEmpty()
							&& areSameEventSet(vEvaluationContext, vEvent));
					if(result == false) {
						return false;
					}
					selected << vEvaluationContext;
					// REPOSITIONNEMENT DU START_EVENT
					Vertex vPath = (1 % vEvaluationContext << CURRENT_EVAL_CONTEXT).unique()
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

		List<Vertex> vEvaluationContextsSelected = [];
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
			&& areSameEventSet(vEvaluationContext, vEvent));
		}.each {remove(it)}
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


	private boolean reevaluate(Vertex vPath, long timeToCompare, Vertex vEvent) {
		Set<Vertex> evaluationContextToReevaluate = [];

		while(vEvent != null && timeToCompare > vEvent.event.getTime()) {
			evaluationContextToReevaluate + (1 % vEvent << EVALUATED).unique();
			Vertex nextVEvent = (1 % vEvent << PREVIOUS).unique();
			vEvent --;
			vEvent = nextVEvent;
			if(vEvent != null) {
				vEvent << FIRST_EVENT << vPath
			}
		}

		boolean toRemove = evaluationContextToReevaluate.find {Vertex vEvaluationContext ->
			Vertex vEvaluator = (1 % vEvaluationContext >> DEPEND_ON).unique();
			vEvaluationContext.occur = 0
			vEvaluationContext.state = null
			Collection<Vertex> vEvents = (1 % vEvaluation >> EVALUATED);
			if(vEvents.isEmpty() && !vEvaluator.isOptional()) {
				return true;
			}
			vEvents.each {
				vEvaluator.evaluator.evaluateOnOccurrenceCriteria(vEvaluationContext, it);
			}
			return vEvaluationContext.state == OccurResponse.KO_BUT_KEEP_ME;
		}
		return toRemove;

		// Réévaluer les EVALUATION_CONTEXT en fonction du critère occurs
	}


	private boolean searchOldEventAndRemoveEvaluationContext(final Vertex vEvaluator, final Vertex vEvaluationContext) {
		logger.debugG("searchOldEventAndRemoveEvaluationContext")
		Evaluator evaluator = vEvaluator.evaluator;
		boolean toRemove = (1 % vEvaluator << ATTACHED).find {Vertex vOldEvent ->
			if(evaluator.evaluateRelationship(vEvaluationContext, vOldEvent)) {
				Vertex vLinkedEventPath = ( 1 % vEvaluationContext << CURRENT_EVAL_CONTEXT).unique()
				Vertex firstEvent = (1 % vLinkedEventPath >> FIRST_EVENT).unique();
				return reevaluate(vLinkedEventPath, vOldEvent.event.getTime(), firstEvent);
			}
			return false;
		};

		if(toRemove) {
			remove(vEvaluationContext);
			return true;
		}
		return false;
	}

	/**
	 * Select path from event passed in parameter
	 * @param vEvent
	 * @return Vertex List of Path Type
	 */
	List<Vertex> select(final Vertex vEvent, List<Vertex> vEvaluatorsSelected) {
		def selected = [];
		vEvaluatorsSelected.each { Vertex vEvaluator ->
			def localSelection = primarySelectOfEvaluationContext(vEvent,vEvaluator);
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
		if(selected.empty && vEvaluatorsSelected.find{it.canBeginPattern}) {
			Vertex vEvaluationContext = graph() << [type:EVALUATION_CONTEXT,state:null,occur:0];
			Vertex vPath = graph() << PATH;
			vPath >> CURRENT_EVAL_CONTEXT >> vEvaluationContext;
			vEvaluationContext >> DEPEND_ON >> vEvaluatorsSelected.last();
			vEvaluationContext >> EVALUATED >> vEvent;
			vPath >> FIRST_EVENT >> vEvent;
			vPath >> LAST_EVENT >> vEvent;
			logger.infoG("Created - EvaluationContext({})/Path({})/Evaluator({})",vEvaluationContext,vPath,vEvaluatorsSelected.last());
			selected << vEvaluationContext;
		}

		if(selected.empty) {
			vEvaluatorsSelected.each { Vertex vEvaluator ->
				logger.infoG("No Evaluation Context found for event {} ! we attached it directly to evaluator {}", vEvent, vEvaluator);
				(vEvent >> ATTACHED >> vEvaluator)
			};
		} else {
			logger.infoG("Evaluation Context [{}] found for event [{}]",selected,vEvent);
		}
		return selected
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
		logger.infoG("Removed - EvaluationContext {} and all dependencies", vEvaluationContext);

		List<Vertex> vEvents = [];
		List<Vertex> vEvaluationContexts = [vEvaluationContext];
		vEvaluationContexts + (0 % vEvaluationContext >> PREVIOUS)
		vEvaluationContexts + (0 % vEvaluationContext << PREVIOUS)

		Vertex vPath = null;
		vEvaluationContexts.each {
			logger.debugG("Removed - EvaluationContext {}", it)
			vEvents + (1 % it >> EVALUATED)
			if(vPath == null) {
				vPath = (1 % it << CURRENT_EVAL_CONTEXT).unique()
			}
			it--;
		}
		logger.debugG("Removed - Path {}", vPath);
		vPath --;

		vEvents.findAll{(1 % it << EVALUATED).empty && (1 % it >> ATTACHED).empty}
		.each{logger.debugG("Deleted - Event {}",it); it--};

	}


	private boolean checkEvaluatorAndResetIfNecessary(final Vertex vEvent, final List<Vertex> vEvaluatorsSelected) {
		if(vEvaluatorsSelected.empty) {
			logger.infoG("Not found - Evaluator {} - Reset all ! ", vEvent);
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


