package org.qe4g.request

import static org.qe4g.request.Window.State.*

import org.qe4g.Event
import org.qe4g.request.Pattern.Evaluation
import org.qe4g.request.Window.State

import spock.lang.Specification

class RequestSpecification extends Specification {

	def event = new Event(names:["MainEvent"],attributes:[test:"value"])
	EventProcessedListener eventListener = Mock(EventProcessedListener)
	Function function = Mock(Function)
	Pattern pattern = Mock(Pattern)
	Request request = new Request(eventListeners:[eventListener],functions:[function],pattern:pattern)

	def "When we process an Event, we notify all listeners even if the evaluation is BROKEN"() {
		setup:
		Evaluation evaluation = new Evaluation(state:org.qe4g.request.Pattern.State.BROKEN)
		when:request.onEvent(event)
		then:
		1 * eventListener.beforeEventProcessing(request,event)
		1 * pattern.evaluate(event) >> evaluation
		1 * eventListener.afterEventProcessed(request,evaluation)
		and:"As the pattern is not detected, we don't apply the functions"
		0 * function.onPatternDetection(_,_)
	}

	def "When we process an Event, we notify all listeners even if the evaluation is REBUILD"() {
		setup:
		Evaluation evaluation = new Evaluation(state:org.qe4g.request.Pattern.State.REBUILD)
		when:request.onEvent(event)
		then:
		1 * eventListener.beforeEventProcessing(request,event)
		1 * pattern.evaluate(event) >> evaluation
		1 * eventListener.afterEventProcessed(request,evaluation)
		and:"As the pattern is not detected, we don't apply the functions"
		0 * function.onPatternDetection(_,_)
	}

	def "When we process an Event, we notify all listeners even if the evaluation is REJECTED"() {
		setup:
		Evaluation evaluation = new Evaluation(state:org.qe4g.request.Pattern.State.REJECTED)
		when:request.onEvent(event)
		then:
		1 * eventListener.beforeEventProcessing(request,event)
		1 * pattern.evaluate(event) >> evaluation
		1 * eventListener.afterEventProcessed(request,evaluation)
		and:"As the pattern is not detected, we don't apply the functions"
		0 * function.onPatternDetection(_,_)
	}

	
	def "When we process an Event, we notify all listeners even if the evaluation is INTEGRATED"() {
		setup:
		Evaluation evaluation = new Evaluation(state:org.qe4g.request.Pattern.State.INTEGRATED)
		when:request.onEvent(event)
		then:
		1 * eventListener.beforeEventProcessing(request,event)
		1 * pattern.evaluate(event) >> evaluation
		1 * eventListener.afterEventProcessed(request,evaluation)
		and:"As the pattern is not detected, we don't apply the functions"
		0 * function.onPatternDetection(_,_)
	}

	def "When we process an Event, we notify all listeners if the evaluation is DETECTED and we apply the functions"() {
		setup:
		Evaluation evaluation = new Evaluation(state:org.qe4g.request.Pattern.State.DETECTED)
		when:request.onEvent(event)
		then:
		1 * eventListener.beforeEventProcessing(request,event)
		1 * pattern.evaluate(event) >> evaluation
		1 * eventListener.afterEventProcessed(request,evaluation)
		and:"As the pattern is detected, we apply all the functions"
		1 * function.onPatternDetection(request,_)
	}
	
	def "the call to get method, call get on all functions"() {
		when:request.get()
		then: 1 * function.get()
	}
		
	def "the call to reset method, call reset on all functions"() {
		when: request.reset()
		then: 1 * function.reset()
	}
}
