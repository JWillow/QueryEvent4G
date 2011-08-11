package org.qe4g.request

import org.qe4g.Event;
import org.qe4g.request.Pattern.Evaluation;
import org.qe4g.request.Pattern.State;

import spock.lang.Specification;

class PatternSpecification extends Specification {

	def eventA = new Event(names:["A"],attributes:[test:"A"])
	def eventB = new Event(names:["B"],attributes:[test:"B"])
	def eventC = new Event(names:["C"],attributes:[test:"C"])
	def olderEvent  = new Event(names:["Z"],attributes:[test:"Z"])
	Pattern pattern = new Pattern(accept:{true})
	Window window = Mock(Window)
	Window existingWindow = Mock(Window)
	List<Window> existingWindows = [existingWindow]
	
	def "For each event accepted, a new Window is created to evaluate it"() {
		setup: 
		pattern.createWindow = {window}
		when: List<Evaluation> evaluations = pattern.evaluate(eventA)
		then:
		evaluations.size() == 1
		evaluations[0].state == State.INTEGRATED
		1 * window.processEvent(eventA) >> org.qe4g.request.Window.State.OPEN
	}

	def "If window evaluation equals OPEN then the pattern evaluation is INTEGRATED"() {	
		setup:
		pattern.createWindow = {window}
		when: List<Evaluation> evaluations = pattern.evaluate(eventA)
		then:
		evaluations.size() == 1
		evaluations[0].state == State.INTEGRATED
		1 * window.processEvent(eventA) >> org.qe4g.request.Window.State.OPEN
	}

	def "If window evaluation equals BROKEN then the pattern evaluation is BROKEN"() {
		setup:
		pattern.createWindow = {window}
		when: List<Evaluation> evaluations = pattern.evaluate(eventA)
		then:
		evaluations.size() == 1
		evaluations[0].state == State.BROKEN
		1 * window.processEvent(eventA) >> org.qe4g.request.Window.State.BROKEN
	}
	
	def "If window evaluation equals CLOSED then the pattern evaluation is DETECTED"() {
		setup:
		pattern.createWindow = {window}
		when: List<Evaluation> evaluations = pattern.evaluate(eventA)
		then:
		evaluations.size() == 1
		evaluations[0].state == State.DETECTED
		1 * window.processEvent(eventA) >> org.qe4g.request.Window.State.CLOSED
	}
		
	def "If pattern doesn't accept an Event then no Window is created and the evaluation state is REJECTED"() {
		setup:pattern.accept = {false}
		when: List<Evaluation> evaluations = pattern.evaluate(eventA)
		then:
		evaluations.size() == 1
		evaluations[0].state == State.REJECTED
		evaluations[0].processedEvents.size() == 1
		and: "there are no Window"
		pattern.windows.size() == 0
	}
}
