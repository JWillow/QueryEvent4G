package integration;

import static org.qe4g.request.graph.MyGraph.*
import static org.qe4g.request.graph.VertexTypes.*
import static org.junit.Assert.*;

import org.qe4g.request.graph.dsl.Language;

import com.tinkerpop.blueprints.pgm.Vertex;

import spock.lang.Specification;

class LanguageDelegationSpecification extends Specification {
	static {
		Language.load();
	}
	def "test nom" () {
		setup:
		Vertex vertex = graph() << [type:PATH];
		Vertex vertex2 = graph() << [type:PATH];
		
		Vertex vEvaluator = graph() << [type:EVALUATOR];
		//vertex.metaClass.reevaluate = {String param -> println "Mon $param"}
		when:
		vertex.reevaluate()
		vertex2.reevaluate()
		
		vEvaluator.evaluate(vertex)
		vEvaluator.reevaluate()
		then: true
	}
}
