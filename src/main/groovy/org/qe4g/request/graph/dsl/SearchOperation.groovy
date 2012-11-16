package org.qe4g.request.graph.dsl

import com.tinkerpop.blueprints.pgm.Vertex;

class SearchOperation {
	public enum Element {VERTEX,EDGE}
	Vertex vertex
	int depth;
	Element element;
	Closure closureToApply = {}
}
