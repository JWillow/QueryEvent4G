package org.qe4g.request.evaluation;


import java.util.Map;

import org.neo4j.graphdb.Node;
import org.qe4g.Event;
import org.qe4g.request.Request;

/**
 * <p>
 * Used to evaluate a list of {@link Event} order by arriving date inside the
 * {@link Request}. The last event is the most recent to evaluate.
 * 
 * @author Willow
 * 
 */
public interface Evaluator {

	/**
	 * @param events
	 *            - order by arriving order inside the {@link Request}
	 * @return
	 */
	boolean evaluateOnStaticCriteria(Event event);
	
	Response on(Node event, Map context);
	
}
