package org.qe4g.request.evaluation

import java.util.List;
import java.util.Map;

class EvaluatorDefinition {
	String eventName
	
	/**
	 * The key Map is the event order inside the pattern. The attached
	 * value is a list of attributes. The event index 0 represents the
	 *  current event processed by the evaluator.
	 */
	Map<Integer, List<String>> attributesUsed
}
