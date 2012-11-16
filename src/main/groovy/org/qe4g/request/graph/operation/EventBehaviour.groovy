package org.qe4g.request.graph.operation;

import java.util.List;

import org.qe4g.request.graph.VertexTypes.Behaviour;

import com.tinkerpop.blueprints.pgm.Vertex;

public class EventBehaviour implements Behaviour {

	def select = {List<Vertex> vEvaluators ->
		List<Vertex> vEvaluatorSelected = vEvaluators.findAll {
			it.evaluateOnStaticCriteria(vEvent)
		}
	
	}
	
	void apply(Vertex vertex) {
	}
}
