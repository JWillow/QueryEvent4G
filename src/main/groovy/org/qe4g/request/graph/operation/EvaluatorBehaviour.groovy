package org.qe4g.request.graph.operation

import static org.qe4g.request.graph.EdgeTypes.*
import org.qe4g.request.graph.VertexTypes.Behaviour;
import com.tinkerpop.blueprints.pgm.Vertex;

class EvaluatorBehaviour implements Behaviour{

	def evaluate = { Vertex vEvent -> println "Evaluate with: ${delegate} on $vEvent";}

	def evaluateOnStaticCriteria = { Vertex vEvent -> 
		delegate.evaluator.evaluateOnStaticCriteria(vEvent);
	}
	
	def evaluateOnOccurrenceCriteria = {Vertex vEvaluationContext, Vertex vEvent ->
		delegate.evaluator.evaluateOnOccurrenceCriteria(vEvaluationContext, vEvent);	
	}

	def reset = {->
		(1 % delegate << DEPEND_ON).each {it.reset()}
	}

	void apply(Vertex vertex) {
		vertex.metaClass.evaluate = evaluate;
		vertex.metaClass.reset = reset;
		vertex.metaClass.evaluateOnStaticCriteria = evaluateOnStaticCriteria;
		vertex.metaClass.evaluateOnOccurrenceCriteria = evaluateOnOccurrenceCriteria;
	}
}
