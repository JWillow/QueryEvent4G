package org.qe4g.request.graph
import org.qe4g.Event;

import com.tinkerpop.blueprints.pgm.Element;
import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.Vertex;
import static org.qe4g.request.graph.MyGraph.*

class EventNodes {
		static {
		ExpandoMetaClass.enableGlobally()

		Element.metaClass.getProperty = {name ->
			return delegate.getProperty(name)
		}

		Element.metaClass.setProperty = {name, val ->
			delegate.setProperty(name, val)
		}
		
		Vertex.metaClass.hasInEdges = {String label -> 
			return delegate.getInEdges(label).iterator().hasNext()
		}

		Map.metaClass.get = {key,Closure cValue ->
			def value;
			if((value = delegate.get(key)) == null) {
				value = cValue.call();
				delegate.put(key, value);
			}
			return value;
		}

		Map.metaClass.get = {key,Closure cValue, Closure cApply ->
			def value = delegate.get(key,cValue);
			value = cApply.call(value)
			delegate.put(key, value)
			return value;
		}
	}

	public static final String TRIGGERED_TIME = '_triggeredTime'

	/**
	 * Don't care about private node properties to determine equality
	 * @param node1
	 * @param node2
	 * @return
	 */
	public static boolean areEquals(Vertex vertex1, Vertex vertex2) {
		Event event1 = vertex1.event;
		Event event2 = vertex2.event;
		if(event1.names.size() != event2.names.size() && event1.names.every{event2.names.contains(it)}) {
			return false;
		}
		
		if(event1.attributes.size() != event2.attributes.size()) {
			return false
		}

		return event1.attributes.every { key,value ->
			return event2.attributes[key] == value
		}
	}


	public static Vertex createFrom(Event event) {
		Vertex vertex = graph().addVertex(null);
		vertex.type = VertexTypes.EVENT
		vertex.event = event;
		return vertex
	}

	public static boolean isPublicProperty(String keyProperty) {
		return keyProperty[0] != '_';
	}
}
