package org.qe4g.request.evaluator

import org.qe4g.Event;
import org.qe4g.request.Evaluator.Response;

import spock.lang.Specification;

class SimpleEventEvaluatorSpecification extends Specification {
	SimpleEventEvaluator see = new SimpleEventEvaluator(id:"id",occurs:new IntRange(1,1))
	Event eventBC = new Event(names:["B","C"])
	Event eventCWithAttAAndC = new Event(names:["C"],attributes:[attA:"A",attC:"C"])
	Event eventCWithAttAAndB = new Event(names:["C"],attributes:[attA:"A",attB:"B"])
	Map<String,Object> context = [:]
	
	def "If the evaluator name not matche with the event name then the Response is KO"() {
		setup: see.name = "A"
		expect:	see.evaluate(context,[eventBC]) == Response.KO
	}
	
	def "If the event satisfy other constraint and the occurrence criteria is 1 then the Response is OK"() {
		setup: 
		see.name = "C"
		see.occurs = new IntRange(1,1)
		expect:	see.evaluate(context,[eventBC]) == Response.OK
	}

	def "If the event doesn't satisfy other constraints and the occurrence indicate that this event is optional the Response is CONTINUE_WITH_NEXT_EVALUATOR"() {
		setup:
		see.name = "A"
		see.occurs = new IntRange(0,2)
		expect:	see.evaluate(context,[eventBC]) == Response.CONTINUE_WITH_NEXT_EVALUATOR
	}
	
	def "If the event doesn't satisfy other constraints and we have not reach the min occurrence expected then Response is KO"() {
		setup:
		see.name = "A"
		see.occurs = new IntRange(1,2)
		expect:	see.evaluate(context,[eventBC]) == Response.KO
	}
	
	def "If the event satisfy other constraint and the occurrence criteria is 1 to 2 then the Response is OK_BUT_KEEP_ME"() {
		setup:
		see.name = "C"
		see.occurs = new IntRange(1,2)
		expect:	see.evaluate(context,[eventBC]) == Response.OK_BUT_KEEP_ME
	}
	
	def "If the event satisfy constraints but we exceed the occurence threshold then the Response is KO"() {
		setup:
		see.name = "C"
		see.occurs = new IntRange(1,2)
		expect:	see.evaluate(["id":2],[eventBC]) == Response.KO
	}
	
	def "If the event satisfy constraints and we reach the occurence threshold then the Response is OK"() {
		setup:
		see.name = "C"
		see.occurs = new IntRange(1,2)
		expect:	see.evaluate(["id":1],[eventBC]) == Response.OK
	}
	
	def "If the evaluator defined attributes constraint and the event doesn't match ALL attributes constraint then the Response is KO"() {
		setup:
		see.name = "C"
		see.attributes = [attA:"A",attB:"B"]
		expect:	see.evaluate(context,[eventCWithAttAAndC]) == Response.KO
	}
	
	def "If the evaluator defined attributes constraint and the event match ALL attributes constraint then the Response is OK"() {
		setup:
		see.name = "C"
		see.attributes = [attA:"A",attB:"B"]
		expect:	see.evaluate(context,[eventCWithAttAAndB]) == Response.OK
	}

	def "If the evaluator defined criterion closure and ALL execution return true then the Response is OK"() {
		setup:
		see.name = "C"
		see.criterions = [{true},{true}]
		expect:	see.evaluate(context,[eventBC]) == Response.OK
	}
		
	def "If the evaluator defined criterion closure and any execution return false then the Response is KO"() {
		setup:
		see.name = "C"
		see.criterions = [{true},{false}]
		expect:	see.evaluate(context,[eventBC]) == Response.KO
	}
	
}
