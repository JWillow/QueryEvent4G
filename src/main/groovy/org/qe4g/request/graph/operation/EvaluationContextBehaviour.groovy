package org.qe4g.request.graph.operation

import static org.qe4g.request.graph.EdgeTypes.*
import org.qe4g.request.graph.VertexTypes.Behaviour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.pgm.Vertex;

class EvaluationContextBehaviour implements Behaviour{

	final static Logger logger = LoggerFactory.getLogger(EvaluationContextBehaviour.class);

	/**
	 * Remove all datas related to EvaluationContext than Path, linked EvaluationContext and Event if it is not referenced by other EvaluationContext
	 * @param vEvaluationContext
	 */
	def reset = {
		List<Vertex> vEvents = [];
		List<Vertex> vEvaluationContexts = [delegate];
		vEvaluationContexts + (0 % delegate >> PREVIOUS)
		vEvaluationContexts + (0 % delegate << PREVIOUS)

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


	/**
	 * Search if another event exist with same names and same attributes than the event attached to
	 * the <code>vEvent</code> parameter inside the same Vertex EvaluationContext.
	 * @param vEvaluationContext
	 * @param vEvent
	 * @return
	 */
	def areSameEventSet =  {Vertex vEvent ->
		List<Vertex> associatedVEvents = (1 % delegate >> EVALUATED)
		if(associatedVEvents.isEmpty()) {
			return true;
		}
		return (associatedVEvents[0].event.names == vEvent.event.names
		&& associatedVEvents[0].event.attributes == vEvent.event.attributes);
	}



	void apply(Vertex vertex) {
		vertex.metaClass.reset = reset;
		vertex.metaClass.areSameEventSet = areSameEventSet;
	}
}
