package org.qe4g.request.graph

import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.impls.tg.TinkerGraph
import org.qe4g.request.graph.dsl.Language
class MyGraph {
	public static Graph INSTANCE = new TinkerGraph();
	
	public static Graph graph() {
		return INSTANCE;
	}	
}
