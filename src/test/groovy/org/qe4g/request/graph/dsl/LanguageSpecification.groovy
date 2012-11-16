package org.qe4g.request.graph.dsl

import org.qe4g.request.graph.EdgeTypes;

import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.Vertex;

import spock.lang.AutoCleanup;
import spock.lang.Specification;
import static org.qe4g.request.graph.MyGraph.*
import static org.qe4g.request.graph.dsl.Language.*
import static org.qe4g.request.graph.EdgeTypes.*

class LanguageSpecification extends Specification {

	Language l = new Language()

	@AutoCleanup("clear")
	Graph graph = graph()

	Vertex vertex = graph() << [id:"1"]
	Vertex otherVertex = graph() << [id:"2"]
	Vertex otherVertex2 = graph() << [id:"3"]


	def "create vertex"() {
		when :
		Vertex vertex = graph() << [:]
		then :
		null != vertex
	}

	def "create vertex with properties"() {
		when:
		Vertex vertex = graph() << [id:"toto",event:["event"]]
		then :
		"toto" == vertex.getId()
		1 == vertex.event.size()
		vertex.event.contains("event")
	}

	def "remove vertex"() {
		setup:
		Vertex vertex = graph() << [id:"ForTest",macopine:'Cathy']
		null != graph().getVertex("ForTest")
		when:
		vertex--
		then:
		null == graph().getVertex("ForTest")
	}

	def "remove vertex from graph instance"() {
		setup:
		Vertex vertex = graph() << [id:"ForTest"]
		null != graph().getVertex("ForTest")
		when:
		graph() >> vertex
		then:
		null == graph().getVertex("ForTest")
	}

	def "create new edge between two existent vertices"() {
		when:
		Edge edge = vertex >> [label:ATTACHED] >> otherVertex
		then:
		null != edge
		"ATTACHED" == edge.getLabel()
		vertex == edge.getOutVertex()
		otherVertex == edge.getInVertex()
	}

	def "create new edge between two existent vertices in other direction"() {
		when:
		Edge edge = vertex << [id:"testId",label:ATTACHED,type:"myType"] << otherVertex
		then:
		null != edge
		"ATTACHED" == edge.getLabel()
		"myType" == edge.type
		"testId" == edge.getId()
		vertex == edge.getInVertex()
		otherVertex == edge.getOutVertex()
	}

	def "find vertex linked by ATTACHED Edge, From Direction"() {
		setup:
		vertex << [id:"link1",label:ATTACHED] << otherVertex
		vertex >> [id:"link2",label:ATTACHED] >> otherVertex2
		when:
		def results =  1 % vertex >> ATTACHED
		then:
		1 == results.size()
		results.contains(otherVertex2)
	}

	def "find vertex linked by ATTACHED Edge, To Direction"() {
		setup:
		vertex << [id:"link1",label:ATTACHED] << otherVertex
		vertex >> [id:"link2",label:ATTACHED] >> otherVertex2
		when:
		def results = 1 % vertex << [label:ATTACHED]
		then:
		1 == results.size()
		results.contains(otherVertex)
	}

	def "remove vertex linked by Attached Edge, To Direction"() {
		setup:
		vertex << [id:"link1",label:ATTACHED] << otherVertex
		vertex >> [id:"link2",label:ATTACHED] >> otherVertex2
		when:
		(vertex << [label:ATTACHED]) --
		then:
		null == graph().getEdge("link1")
		null != graph().getEdge("link2")
	}

	def collectedVertex = []
	def test = { collectedVertex << it }

	def "apply closure on each Vertex found"() {
		setup :
		vertex << ATTACHED << otherVertex
		otherVertex << ATTACHED << otherVertex2
		when :
		def result = test % vertex << ATTACHED
		then:
		collectedVertex.size() == 2
		collectedVertex.contains(otherVertex)
		collectedVertex.contains(otherVertex2)
		result == collectedVertex
	}

	def removeVertex = { collectedVertex << it; it-- }

	def "Remove each Vertex found"() {
		setup :
		collectedVertex = []
		vertex << ATTACHED << otherVertex
		otherVertex << ATTACHED << otherVertex2
		when :
		def result = removeVertex % vertex << ATTACHED
		then:
		collectedVertex.size() == 2
		collectedVertex.contains(otherVertex)
		collectedVertex.contains(otherVertex2)
		result == collectedVertex
		null != graph().getVertex("1")
		null == graph().getVertex("2")
		null == graph().getVertex("3")
	}

	def removeVertexButStopAfterFirst = { collectedVertex << it; it--; return false }

	def "Remove just first level of result"() {
		setup :
		collectedVertex = []
		vertex << ATTACHED << otherVertex
		otherVertex << ATTACHED << otherVertex2
		when :
		def result = removeVertexButStopAfterFirst % vertex << ATTACHED
		then:
		collectedVertex.size() == 1
		collectedVertex.contains(otherVertex)
		result == collectedVertex
		null != graph().getVertex("1")
		null == graph().getVertex("2")
		null != graph().getVertex("3")
	}
	
	def "Remove just first level of result, two vertex found on first level"() {
		setup :
		collectedVertex = []
		vertex << ATTACHED << otherVertex
		vertex << ATTACHED << otherVertex2
		when :
		def result = removeVertexButStopAfterFirst % vertex << ATTACHED
		then:
		collectedVertex.size() == 2
		collectedVertex.contains(otherVertex)
		collectedVertex.contains(otherVertex2)
		result == collectedVertex
		null != graph().getVertex("1")
		null == graph().getVertex("2")
		null == graph().getVertex("3")
	}
}
