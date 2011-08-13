package org.qe4g.request.dsl.keyword

import org.qe4g.Event;

/**
 * Handle the <code>by</code> used by the <code>count</code> function. More detail on supported syntax on {@link CountBySpecification}
 * @author Willow
 */
class CountBy {

	private static Closure countCore = {Map<Integer,List<Object>> criterion, Map context, List events ->
		def key = "cpt"
		def keys = []
		criterion.each {index,atts ->
			if(index == 0) {
				index = events.size() - 1
			} else {
				index -= 1
			}
			if(atts) {
				Event event = events[index]
				atts.each {att -> 
					if(att instanceof String) {
						keys << "${att}:${event.attributes[att]}"
					} else if (att instanceof Closure){
						keys << att(event)
					} 
				}
			}
		}
		if(keys.size() > 0) {
			key = keys.toString();
		}
		(context.containsKey(key))?context[key]++:(context[key] = 1)
	}

	public static Closure get(String name, Object value) {
		if(value == null) {
			return countCore.curry([0:null])
		}
		if(value instanceof String) {
			return countCore.curry([0:[value]]);
		}
		if(value instanceof Collection) {
			return countCore.curry([0:value]);
		}
		if(value instanceof Closure) {
			return countCore.curry([0:value])
		}
		if(value instanceof Map) {
			Map params = [:]
			value.each {key,associatedValue ->
				if(associatedValue instanceof String) {
					params.put(key, [associatedValue])
				} else {
					params.put(key, associatedValue)
				}
			}
			return countCore.curry(params)
		}
	}
}
