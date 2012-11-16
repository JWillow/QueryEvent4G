package org.qe4g.request.graph.dsl

import java.util.concurrent.atomic.AtomicInteger;

import org.qe4g.Event;
import org.qe4g.request.graph.dsl.VertexOperation.Direction;
import org.slf4j.Logger;

import com.sun.org.apache.xpath.internal.operations.Plus;
import com.tinkerpop.blueprints.pgm.Element;
import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.blueprints.pgm.impls.tg.TinkerGraph;
import com.tinkerpop.blueprints.pgm.impls.tg.TinkerVertex;
import org.qe4g.request.graph.VertexDecoratorForLog;
import org.qe4g.request.graph.VertexTypes;

class Language {
	static load() {
	}

	private static AtomicInteger ai = new AtomicInteger();


	static {
		Closure addVertexCmd = {Graph graph, Map<String,Object> properties ->
			String id = properties['id']
			if(id == null & properties.containsKey('type')) {
				id = "${properties['type'].toString()}_${ai.incrementAndGet()}";
			}
			Vertex vertex = graph.addVertex(id)
			vertex.graph = graph
			properties.remove('id')
			properties.each{key,value ->
				vertex.setProperty(key, value)
			}
			// Add behaviour
			VertexTypes type = properties.type;
			type.applyBehaviour(vertex);
			return vertex
		}

		Closure decorateVertexForLog = {Object[] argArray ->
			List<Object> newArray = [];
			if(argArray != null) {
				for(int i = 0; i < argArray.length;i++) {
					if(argArray[i] instanceof Vertex) {
						newArray << new VertexDecoratorForLog(vertex:argArray[i]);
					} else {
						newArray << argArray[i]
					}
				}
			}
			return newArray.toArray();
		}


		ExpandoMetaClass.enableGlobally()
		Collection.metaClass {
			unique << {
				if(delegate.size() > 1) {
					throw new IllegalStateException()
				}
				if(delegate.isEmpty()) {
					return null
				}
				return delegate.iterator().next()
			}
			plus << {Collection collection ->
				delegate.addAll(collection)
			}
		}
		Map.metaClass {
			get = {key,Closure cValue ->
				def value;
				if((value = delegate.get(key)) == null) {
					value = cValue.call();
					delegate.put(key, value);
				}
				return value;
			}

			get = {key,Closure cValue, Closure cApply ->
				def value = delegate.get(key,cValue);
				value = cApply.call(value)
				delegate.put(key, value)
				return value;
			}
		}
		Element.metaClass {
			getProperty = {name ->
				return delegate.getProperty(name)
			}

			setProperty = {name, val ->
				delegate.setProperty(name, val)
			}
		}

		Graph.metaClass {
			// <<
			leftShift << {  Map<String,Object> properties ->
				return addVertexCmd(delegate,properties)
			}

			// <<
			leftShift << {  Enum enumArg ->
				return addVertexCmd(delegate,[type:enumArg.toString()])
			}

			// >>
			rightShift << { Vertex vertex ->
				delegate.removeVertex(vertex)
			}
		}

		Logger.metaClass {
			infoG << {String format, Object[] argArray ->
				if(delegate.isDebugEnabled()) {
					delegate.info(format,decorateVertexForLog(argArray));
				} else {
					delegate.info(format,argArray);
				}
			};
			debugG << {String format, Object[] argArray ->
				delegate.debug(format,decorateVertexForLog(argArray));
			};
		}

		Vertex.metaClass {
			// --
			previous << {
				Graph graph = delegate.graph
				if(graph == null) {
					throw new IllegalStateException("")
				}
				graph.removeVertex(delegate)
			}

			// <<
			leftShift << {  Enum  enumArg->
				VertexOperation vo = new VertexOperation(direction:Direction.TO,vertex:delegate,edgeProperties:[label:enumArg.name()])
				return vo
			}
			// <<
			leftShift << {  Map<String, Object>  edgeProperties->
				VertexOperation vo = new VertexOperation(direction:Direction.TO,vertex:delegate,edgeProperties:edgeProperties)
				return vo
			}
			// >>
			rightShift << {  Enum  enumArg->
				VertexOperation vo = new VertexOperation(direction:Direction.FROM,vertex:delegate,edgeProperties:[label:enumArg.name()])
				return vo
			}
			// >>
			rightShift << { Map<String, Object>  edgeProperties->
				VertexOperation vo = new VertexOperation(direction:Direction.FROM,vertex:delegate,edgeProperties:edgeProperties)
				return vo
			}
			
			methodMissing <<  {String name, args ->
				def method = dynamicMethods.find { it.match(name) }
				if(method) {
				   GORM.metaClass."$name" = { Object[] varArgs ->
					  method.invoke(delegate, name, varArgs)
				   }
				   return method.invoke(delegate,name, args)
				}
				else throw new MissingMethodException(name, delegate, args)
			}
			
		}

		VertexOperation.metaClass {
			// <<
			leftShift << {  Vertex vertex ->
				return delegate.createEdgeWithOtherVertex(vertex, Direction.FROM)
			}

			// >>
			rightShift << {  Vertex vertex ->
				return delegate.createEdgeWithOtherVertex(vertex, Direction.TO)
			}

			// --
			previous << { delegate.removeEdges() }
		}

		SearchOperation.metaClass {
			// <<
			leftShift << {  Enum  enumArg->
				VertexOperation vo = new VertexOperation(direction:Direction.TO,vertex:delegate.vertex,edgeProperties:[label:enumArg.name()])
				return vo.searchVerteces(delegate.depth,delegate.closureToApply)
			}
			// <<
			leftShift << {  Map<String, Object>  edgeProperties->
				VertexOperation vo = new VertexOperation(direction:Direction.TO,vertex:delegate.vertex,edgeProperties:edgeProperties)
				return vo.searchVerteces(delegate.depth,delegate.closureToApply)
			}
			// >>
			rightShift << {  Enum  enumArg->
				VertexOperation vo = new VertexOperation(direction:Direction.FROM,vertex:delegate.vertex,edgeProperties:[label:enumArg.name()])
				return vo.searchVerteces(delegate.depth,delegate.closureToApply)
			}
			// >>
			rightShift << { Map<String, Object>  edgeProperties->
				VertexOperation vo = new VertexOperation(direction:Direction.FROM,vertex:delegate.vertex,edgeProperties:edgeProperties)
				return vo.searchVerteces(delegate.depth,delegate.closureToApply)
			}
		}

		Integer.metaClass {
			mod << { Vertex vertex ->
				if(delegate == 0) {
					delegate = Integer.MAX_VALUE
				}
				return new SearchOperation(vertex:vertex,depth:delegate,element:org.qe4g.request.graph.dsl.SearchOperation.Element.VERTEX)
			}
		}

		Closure.metaClass {
			mod << { Vertex vertex ->
				return new SearchOperation(vertex:vertex,depth:Integer.MAX_VALUE,element:org.qe4g.request.graph.dsl.SearchOperation.Element.VERTEX,closureToApply:delegate)
			}
		}

	}
}
