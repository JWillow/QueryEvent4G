package org.qe4g.request.graph.operation

import static org.qe4g.request.graph.EdgeTypes.*;
import org.qe4g.request.graph.VertexTypes.Behaviour;

import com.tinkerpop.blueprints.pgm.Vertex;

class PathBehaviour implements Behaviour{

	/**
	* 
	* @param vPath
	* @param timeToCompare
	* @param vEvent
	* @return
			*/
	def reevaluate =  {long timeToCompare, Vertex vEvent ->
		Set<Vertex> evaluationContextToReevaluate = [];

		while(vEvent != null && timeToCompare > vEvent.event.getTime()) {
			def result = (1 % vEvent << EVALUATED)
			println result
			evaluationContextToReevaluate +  result.unique()
			Vertex nextVEvent = (1 % vEvent << PREVIOUS).unique();
			vEvent --;
			vEvent = nextVEvent;
			if(vEvent != null) {
				vEvent << FIRST_EVENT << delegate
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
	}

	
	
	void apply(Vertex vertex) {
		vertex.metaClass.reevaluate = reevaluate;
	}
	
}
