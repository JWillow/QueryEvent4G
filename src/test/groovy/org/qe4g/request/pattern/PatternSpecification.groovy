package org.qe4g.request.pattern;

import static org.junit.Assert.*;
import static org.qe4g.request.evaluation.OccurResponse.*
import static org.qe4g.request.graph.EdgeTypes.*
import static org.qe4g.request.graph.VertexTypes.*
import static org.qe4g.request.graph.MyGraph.*

import org.qe4g.request.evaluation.Evaluator;

import com.tinkerpop.blueprints.pgm.Vertex;

import spock.lang.AutoCleanup;
import spock.lang.Specification;

class PatternSpecification extends Specification {

	Evaluator evaluator1 = Mock();
	Evaluator evaluator2 = Mock();
	Evaluator evaluator3 = Mock();
	Evaluator evaluator4 = Mock();

	@AutoCleanup("clear")
	com.tinkerpop.blueprints.pgm.Graph graph = graph();

	/**
	 * [Evaluator(1)]--Next-->[Evaluator(2)]--Next-->[Evaluator(3)]--Next-->[Evaluator(4)]
	 */
	Pattern pattern = Pattern.builder().addEvaluator(evaluator1).addEvaluator(evaluator2).addEvaluator(evaluator3).addEvaluator(evaluator4).build()

	/**
	 * <p>Initial situation :
	 * <pre>   
	 * [Path]--Evaluator_Used(state:OK,occur:1)-->[Evaluator(1)]--Next-->[Evaluator(2)]
	 *      \--Last_Event_Evaluated-->[Event(ev1)]
	 * </pre>
	 * <p>And a new [Event] come :
	 * <pre>
	 * [Event(ev2)]<--Attached--[Evaluator(2)]
	 * </pre>  
	 * <p>Result : [Path] found
	 *   
	 */
	def "case 1"() {
		setup : "Initial situation :"
		Vertex vLastEvent = graph() << [type:EVENT,id:'ev1']
		Vertex vPath = graph() << [type:PATH,id:'path1']
		pattern.vEvaluators[0] << [label:EVALUATOR_ATTACHED,occur:1,state:OK] << vPath
		vLastEvent << LAST_EVENT_EVALUATED << vPath
		and: "New Event come"
		Vertex vNewEvent = graph() << [type:EVENT,id:'ev2'];
		vNewEvent << ATTACHED << pattern.vEvaluators[1];

		when:
		Vertex vPathSelected = pattern.select(vNewEvent)[0];

		then: vPathSelected == vPath
	}

	/**
	 * <p>Initial situation :
	 * <pre>
	 * [Path]--Evaluator_Used(state:OK,occur:1)-->[Evaluator(1)]--Next-->[Evaluator(2,optional)]--Next-->[Evaluator(3,optional)]--Next-->[Evaluator(4)]
	 *      \--Last_Event_Evaluated-->[Event(ev1)]
	 * </pre>
	 * <p>And a new [Event] come :
	 * <pre>
	 * [Event(ev2)]<--Attached--[Evaluator(4)]
	 * </pre>
	 * <p>Result : [Path] found
	 *
	 */
	def "case 2.1 - with optional evaluator"() {
		setup : "Initial situation :"
		evaluator2.isOptional() >> true
		evaluator3.isOptional() >> true
		Vertex vLastEvent = graph() << [type:EVENT,id:'ev1']
		Vertex vPath = graph() << [type:PATH,id:'path1']
		pattern.vEvaluators[0] << [label:EVALUATOR_ATTACHED,occur:1,state:OK] << vPath
		vLastEvent << LAST_EVENT_EVALUATED << vPath
		and: "New Event come"
		Vertex vNewEvent = graph() << [type:EVENT,id:'ev2'];
		vNewEvent << ATTACHED << pattern.vEvaluators[3];

		when:
		Vertex vPathSelected = pattern.select(vNewEvent)[0];

		then: vPathSelected == vPath
	}

	/**
	 * <p>Initial situation :
	 * <pre>
	 * [Path]--Evaluator_Used(state:OK_BUT_KEEP_ME,occur:1)-->[Evaluator(2,optional(0..3))]--Next-->[Evaluator(3,optional)]--Next-->[Evaluator(4)]
	 *      \--Last_Event_Evaluated-->[Event(ev1)]
	 * </pre>
	 * <p>And a new [Event] come :
	 * <pre>
	 * [Event(ev2)]<--Attached--[Evaluator(4)]
	 * </pre>
	 * <p>Result : [Path] found
	 *
	 */
	def "case 2.2 - with optional evaluator"() {
		setup : "Initial situation :"
		evaluator2.isOptional() >> true
		evaluator3.isOptional() >> true
		Vertex vLastEvent = graph() << [type:EVENT,id:'ev1']
		Vertex vPath = graph() << [type:PATH,id:'path1']
		pattern.vEvaluators[1] << [label:EVALUATOR_ATTACHED,occur:1,state:OK_BUT_KEEP_ME] << vPath
		vLastEvent << LAST_EVENT_EVALUATED << vPath
		and: "New Event come"
		Vertex vNewEvent = graph() << [type:EVENT,id:'ev2'];
		vNewEvent << ATTACHED << pattern.vEvaluators[3];

		when:
		Vertex vPathSelected = pattern.select(vNewEvent)[0];

		then: vPathSelected == vPath
	}

	/**
	 * <p>Initial situation :
	 * <pre>   
	 * [Path]--Evaluator_Used(state:OK_BUT_KEEP_ME,occur:1)-->[Evaluator(1)]--Next-->[Evaluator(2)]
	 *      \--Last_Event_Evaluated-->[Event(ev1)]
	 * </pre>
	 * <p>And a new [Event] come :
	 * <pre>
	 * [Event(ev2)]<--Attached--[Evaluator(2)]
	 * </pre>  
	 * <p>Result : No [Path] found
	 *   
	 */
	def "case 3"() {
		setup : "Initial situation :"
		Vertex vLastEvent = graph() << [type:EVENT,id:'ev1']
		Vertex vPath = graph() << [type:PATH,id:'path1']
		pattern.vEvaluators[1] << [label:EVALUATOR_ATTACHED,occur:1,state:OK_BUT_KEEP_ME] << vPath
		vLastEvent << LAST_EVENT_EVALUATED << vPath
		and: "New Event come"
		Vertex vNewEvent = graph() << [type:EVENT,id:'ev2'];
		vNewEvent << ATTACHED << pattern.vEvaluators[2];

		when:
		Vertex vPathSelected = pattern.select(vNewEvent)[0];

		then: vPathSelected == vPath
	}

	/**
	 * <p>Initial situation :
	 * <pre>
	 * [Path]--Evaluator_Used(state:KO_BUT_KEEP_ME,occur:1)-->[Evaluator(1)]--Next-->[Evaluator(2)]
	 *      \--Last_Event_Evaluated-->[Event(ev1)]
	 * </pre>
	 * <p>And a new [Event] come :
	 * <pre>
	 * [Event(ev2)]<--Attached--[Evaluator(2)]
	 * </pre>
	 * <p>Result : No [Path] found
	 *
	 */
	def "case 4"() {
		setup : "Initial situation :"
		Vertex vLastEvent = graph() << [type:EVENT,id:'ev1']
		Vertex vPath = graph() << [type:PATH,id:'path1']
		pattern.vEvaluators[1] << [label:EVALUATOR_ATTACHED,occur:1,state:KO_BUT_KEEP_ME] << vPath
		vLastEvent << LAST_EVENT_EVALUATED << vPath
		and: "New Event come"
		Vertex vNewEvent = graph() << [type:EVENT,id:'ev2'];
		vNewEvent << ATTACHED << pattern.vEvaluators[2];

		when:
		def selection = pattern.select(vNewEvent);

		then: true == selection.empty
	}

	/**
	 * <p>Initial situation :
	 * <pre>
	 * [Path]--Evaluator_Used(state:OK_BUT_KEEP_ME,occur:1)-->[Evaluator(1)]--Next-->[Evaluator(2)]
	 *      \--Last_Event_Evaluated-->[Event(ev1)]
	 * </pre>
	 * <p>And a new [Event] come :
	 * <pre>
	 * [Event(ev2)]<--Attached--[Evaluator(1)]
	 * </pre>
	 * <p>Result : [Path] found, EVALUATOR_TO_USE Ev1 
	 *
	 */
	def "case 5"() {
		setup : "Initial situation :"
		Vertex vLastEvent = graph() << [type:EVENT,id:'ev1']
		Vertex vPath = graph() << [type:PATH,id:'path1']
		pattern.vEvaluators[1] << [label:EVALUATOR_ATTACHED,occur:1,state:OK_BUT_KEEP_ME] << vPath
		vLastEvent << LAST_EVENT_EVALUATED << vPath
		and: "New Event come"
		Vertex vNewEvent = graph() << [type:EVENT,id:'ev2'];
		vNewEvent << ATTACHED << pattern.vEvaluators[1];

		when:
		Vertex vPathSelected = pattern.select(vNewEvent)[0];

		then: vPathSelected == vPath
	}

	/**
	 * <p>Initial situation :
	 * <pre>
	 * [Path]--Evaluator_Used(state:OK,occur:1)-->[Evaluator(1)]--Next-->[Evaluator(2)]
	 *      \--Last_Event_Evaluated-->[Event(ev1)]
	 * </pre>
	 * <p>And a new [Event] come :
	 * <pre>
	 * [Event(ev2)]<--Attached--[Evaluator(1)]
	 * </pre>
	 * <p>Result : No [Path] found
	 *
	 */
	def "case 6"() {
		setup : "Initial situation :"
		Vertex vLastEvent = graph() << [type:EVENT,id:'ev1']
		Vertex vPath = graph() << [type:PATH,id:'path1']
		pattern.vEvaluators[1] << [label:EVALUATOR_ATTACHED,occur:1,state:OK] << vPath
		vLastEvent << LAST_EVENT_EVALUATED << vPath
		and: "New Event come"
		Vertex vNewEvent = graph() << [type:EVENT,id:'ev2'];
		vNewEvent << ATTACHED << pattern.vEvaluators[1];

		when:
		def selection = pattern.select(vNewEvent);

		then: true == selection.empty
	}
}
