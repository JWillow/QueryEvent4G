package org.qe4g.request.dsl.keyword

import org.qe4g.Event

/**
 * Handle the keyword <code>linkOn</code> used by the <code>event</code> expression to build a {@link SimpleEventEvaluator}.
 * @author Willow
 * @specification {@link LinkOnSpecification}
 */
class LinkOn {

	/**
	 * Expression (<code><-></code) to indicate an explicit relation between two attributes
	 */
	private static final String EXPLICITE_RELATION_SYMBOL = "<->"

	private static def linkEvaluation = { List<Closure> conditions, Event current, Event other ->
		return conditions.every { it(current,other) }
	}

	private static def orEvaluation = { List<Closure> conditions, Event current, Event other ->
		return conditions.any { it(current,other) }
	}

	private static def attributEvaluation = { String currentAttr, String otherAttr, Event current, Event other ->
		def val1 = current.attributes[currentAttr];
		def val2 = other.attributes[otherAttr];
		return val1 == val2
	}

	private static Closure onStringExpression(String value) {
		if(value.contains(EXPLICITE_RELATION_SYMBOL)) {
			String[] token = value.split(EXPLICITE_RELATION_SYMBOL)
			return attributEvaluation.curry(token[0].trim(),token[1].trim());
		} else {
			return attributEvaluation.curry(value.trim(),value.trim());
		}
	}

	private static List<Closure> onListExpression(Collection collections, boolean orCondition) {
		def result = []
		collections.each {
			if(it instanceof String) {
				result << onStringExpression(it);
			} else if (it instanceof Closure) {
				result << it
			}
		}
		if(orCondition) {
			def closure = orEvaluation.curry(new ArrayList(result))
			result.clear()
			result << closure
		}
		return result;
	}

	private static List<Closure> onMapExpression(Map map) {
		def result = []
		map.each{ key,value ->
			if(key == "or") {
				result.addAll(onListExpression(value,true))
			} else if(key == "and") {
				result.addAll(onListExpression(value,true))
			} else {
				if(value instanceof Collection) {
					result.addAll(onListExpression(value,true))
				} else if(value instanceof Map) {
					result.addAll(onMapExpression(map))
				}
			}
		}
		return result;
	}

	private static Closure get(Object value) {
		List conditions = [];
		if(value instanceof String) {
			conditions << onStringExpression(value)
		} else if(value instanceof Collection) {
			conditions.addAll(onListExpression(value, false))
		} else if (value instanceof Map) {
			conditions.addAll(onMapExpression(value))
		} else if (value instanceof Closure) {
			conditions  << value
		}
		return linkEvaluation.curry(conditions);
	}

	/**
	 * Entry point to evaluation <code>linkOn</code> expression.
	 * @param attribut
	 * @param value
	 * @return
	 */
	public static Map<Integer,Closure> get(String attribut, Object value) {
		Map results = [:];
		if (value instanceof Map) {
			for(Map.Entry entry:value.entrySet()) {
				if(entry.key instanceof Integer) {
					results.put(entry.key, get(entry.value))
				} else {
					results.put(0, get(value))
					break;
				}
			}
		} else {
			return [0:get(value)];
		}
		return results
	}
}

