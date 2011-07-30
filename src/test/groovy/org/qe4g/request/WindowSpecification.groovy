package org.qe4g.request;

import org.qe4g.Event
import org.qe4g.request.Evaluator.Response;
import org.qe4g.request.Window.State;
import org.qe4g.request.evaluator.SimpleEventEvaluator;

import spock.lang.Specification;

class WindowSpecification extends Specification {

	Evaluator evaluator = Mock(Evaluator)
	Evaluator secondEvaluator = Mock(Evaluator)
	Evaluator thirdEvaluator = Mock(Evaluator)
	def event = new Event(attributes:[test:"value"],names:[])
	def event2 = new Event(attributes:[test:"value2"],names:[])

	def "If the unique Evaluator is OK then the Window is CLOSED"() {
		setup:
		def window = new Window(evaluators:[evaluator])
		1 * evaluator.evaluate(_,_) >> Response.OK

		when: State state = window.processEvent(event)

		then: 
		window.state == State.CLOSED
		state == State.CLOSED
	}
	
	def "If the unique Evaluator is KO then the window is BROKEN"() {
		setup:
		def window = new Window(evaluators:[evaluator])
		1 * evaluator.evaluate(_,_) >> Response.KO

		when: State state = window.processEvent(event)

		then: 
		window.state == State.BROKEN
		state == State.BROKEN
	}

	def "If the unique Evaluator is CONTINUE_TO_NEXT_EVALUATOR then the window is CLOSED because there are no other evaluator"() {
		setup:
		def window = new Window(evaluators:[evaluator])
		1 * evaluator.evaluate(_,_) >> Response.CONTINUE_WITH_NEXT_EVALUATOR

		when: State state = window.processEvent(event)

		then:
		window.state == State.CLOSED
		state == State.CLOSED
	}

	def "If the unique Evaluator is OK_BUT_KEEP_ME then the window is OPEN because the evaluator wait other event to be completed"() {
		setup:
		def window = new Window(evaluators:[evaluator])
		1 * evaluator.evaluate(_,_) >> Response.OK_BUT_KEEP_ME

		when: State state = window.processEvent(event)

		then:
		window.state == State.OPEN
		state == State.OPEN
		window.indexNextEvaluatorToUse == 0
	}
	
	def "If the first Evaluator response CONTINUE_WITH_NEXT_EVALUATOR and the second Evaluator response OK then the window is CLOSED"() {
		setup:
		def window = new Window(evaluators:[evaluator,secondEvaluator])
		1 * evaluator.evaluate(_,_) >> Response.CONTINUE_WITH_NEXT_EVALUATOR
		1 * secondEvaluator.evaluate(_,_) >> Response.OK
		
		when: State state = window.processEvent(event)

		then:
		window.state == State.CLOSED
		state == State.CLOSED
	}

	def "If the first Evaluator response CONTINUE_WITH_NEXT_EVALUATOR and the second Evaluator response KO then the window is BROKEN"() {
		setup:
		def window = new Window(evaluators:[evaluator,secondEvaluator])
		1 * evaluator.evaluate(_,_) >> Response.CONTINUE_WITH_NEXT_EVALUATOR
		1 * secondEvaluator.evaluate(_,_) >> Response.KO
		
		when: State state = window.processEvent(event)

		then:
		window.state == State.BROKEN
		state == State.BROKEN
	}
	
	def "If the first Evaluator response CONTINUE_WITH_NEXT_EVALUATOR and the second Evaluator response OK_BUT_KEEP_ME then the window is OPEN"() {
		setup:
		def window = new Window(evaluators:[evaluator,secondEvaluator])
		1 * evaluator.evaluate(_,_) >> Response.CONTINUE_WITH_NEXT_EVALUATOR
		1 * secondEvaluator.evaluate(_,_) >> Response.OK_BUT_KEEP_ME
		
		when: State state = window.processEvent(event)

		then:
		window.state == State.OPEN
		state == State.OPEN
		and:"we will used the second evaluator again"
		window.indexNextEvaluatorToUse == 1
	}

	
	def "If the first Evaluator response OK_BUT_KEEP_ME then the second Evaluator is not used and the window is OPEN"() {
		setup:
		def window = new Window(evaluators:[evaluator,secondEvaluator])
		1 * evaluator.evaluate(_,_) >> Response.OK_BUT_KEEP_ME
		0 * secondEvaluator.evaluate(_,_)
		
		when: State state = window.processEvent(event)

		then:
		window.state == State.OPEN
		state == State.OPEN
		window.indexNextEvaluatorToUse == 0
	}
	
	def "If the first Evaluator response OK then the second Evaluator is not used and the window is OPEN"() {
		setup:
		def window = new Window(evaluators:[evaluator,secondEvaluator])
		1 * evaluator.evaluate(_,_) >> Response.OK
		0 * secondEvaluator.evaluate(_,_)
		
		when: State state = window.processEvent(event)

		then:
		window.state == State.OPEN
		state == State.OPEN
		and:"next time we will used the second evaluator"
		window.indexNextEvaluatorToUse == 1
	}
		
}