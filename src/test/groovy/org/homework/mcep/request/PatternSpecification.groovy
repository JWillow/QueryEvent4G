package org.homework.mcep.request

import org.homework.mcep.Event;
import org.homework.mcep.request.Pattern.Evaluation;
import org.homework.mcep.request.Pattern.State;

import spock.lang.Specification;

class PatternSpecification extends Specification {

	def groupId = 'myId'
	def eventA = new Event(names:["A"],attributes:[test:"A"])
	def eventB = new Event(names:["B"],attributes:[test:"B"])
	def eventC = new Event(names:["C"],attributes:[test:"C"])
	def olderEvent  = new Event(names:["Z"],attributes:[test:"Z"])
	Window window = Mock(Window)
	Pattern pattern = new Pattern(groupBy:{groupId},accept:{true})

	def registerWindowInEngine() {
		window.id >> groupId
		pattern.windows[groupId] = window
	}

	def "When an Event is performed and, if no Window are registred or no Window are linked with the grouping id extract from Event, then a new Window is created and used."(){
		setup:pattern.metaClass.createWindow = {a,b -> window}
		when:Evaluation evaluation = pattern.evaluate(eventA)
		then: "The new Window has processed the Event"
		1 * window.processEvent(eventA) >> org.homework.mcep.request.Window.State.OPEN
		and:"The pattern has registred the new Window"
		pattern.windows.size() == 1
	}

	def "If for a groupId extract from Event exist a Window we use it. If the state's Window is OPEN then the Window is registred inside the Request"(){
		setup:registerWindowInEngine()
		when: Evaluation evaluation = pattern.evaluate(eventA)
		then: "The new Window performed the Event and it state is OPEN"
		1 * window.processEvent(eventA) >> org.homework.mcep.request.Window.State.OPEN
		and: "The Window has been registred inside the Request"
		pattern.windows.size() == 1
	}

	def "If Window processed an Event and return OPEN then the Evaluation is INTEGRATED"() {
		setup: registerWindowInEngine()
		when: Evaluation evaluation = pattern.evaluate(eventA)
		then: "The  Window performed the Event and it state is OPEN"
		1 * window.processEvent(eventA) >> org.homework.mcep.request.Window.State.OPEN
		1 * window.events >> [eventA]
		and: "The Window has been registred inside the Request"
		evaluation.state == State.INTEGRATED
		evaluation.processedEvents.size() == 1
	}

	def "If Window processed an Event and return CLOSED then the Evaluation is DETECTED"() {
		setup: registerWindowInEngine()
		when: Evaluation evaluation = pattern.evaluate(eventA)
		then: "The  Window performed the Event and it state is CLOSED"
		1 * window.processEvent(eventA) >> org.homework.mcep.request.Window.State.CLOSED
		1 * window.events >> [eventA]
		and: "The Window has been registred inside the Request"
		evaluation.state == State.DETECTED
		evaluation.processedEvents.size() == 1
	}

	def "If Window processed an Event contains already one Event and return BROKEN, the Event has the rigth to a second evaluation because the current event can be the begin of a new Pattern detection. If the second evaluation is positive the evaluation state is REBUILD"() {
		setup:"We register the existing window"
		registerWindowInEngine()
		and:"We prepare the new Window creation"
		Window otherWindow = Mock(Window)
		pattern.metaClass.createWindow = {a,b -> otherWindow}

		when: Evaluation evaluation = pattern.evaluate(eventA)

		then: "The first Event evaluation by existing Window, broke the existing Window"
		1 * window.processEvent(eventA) >> org.homework.mcep.request.Window.State.BROKEN
		and : "The existing window had already one event registred, with the current event we have two Event in total. The current event has the rigth to a second evaluation"
		(1.._) * window.events >> [olderEvent, eventA]
		and :"The second Event evaluation by the new window is OPEN"
		1 * otherWindow.processEvent(eventA) >> org.homework.mcep.request.Window.State.OPEN
		and: "The evaluation reflect the process, we have broken a window and create new a window, we have 'rebuild' the window"
		evaluation.state == State.REBUILD
		and:"The 2 events contains by the first window are given"
		evaluation.processedEvents.size() ==  2
	}
	
	def "If Window processed an Event contains already one Event and return BROKEN, the Event has the rigth to a second evaluation because the current event can be the begin of a new Pattern detection. If the second evaluation is negative the evaluation state is BROKEN"() {
		setup:"We register the existing window"
		registerWindowInEngine()
		and:"We prepare the new Window creation"
		Window otherWindow = Mock(Window)
		pattern.metaClass.createWindow = {a,b -> otherWindow}

		when: Evaluation evaluation = pattern.evaluate(eventA)

		then: "The first Event evaluation by existing Window, broke the existing Window"
		1 * window.processEvent(eventA) >> org.homework.mcep.request.Window.State.BROKEN
		and : "The existing window had already one event registred, with the current event we have two Event in total. The current event has the rigth to a second evaluation"
		(1.._) * window.events >> [olderEvent, eventA]
		and :"The second Event evaluation by the new window is BROKEN"
		1 * otherWindow.processEvent(eventA) >> org.homework.mcep.request.Window.State.BROKEN
		1 * otherWindow.events >> [eventA]
		and: "The evaluation reflect the process, we have broken a window and create new a window, we have 'rebuild' the window"
		evaluation.state == State.BROKEN
		and:"The 2 events contains by the first window are given"
		evaluation.processedEvents.size() == 2
	}
	
	def "If Window processed an Event and contains no Event, the Event breaks the new Window, then the evaluation state is BROKEN"() {
		setup:registerWindowInEngine()
		when: Evaluation evaluation = pattern.evaluate(eventA)
		then: 
		1 * window.processEvent(eventA) >> org.homework.mcep.request.Window.State.BROKEN
		(1.._) * window.events >> [eventA]
		evaluation.state == State.BROKEN
		evaluation.processedEvents.size() == 1
	}
	
	def "If pattern doesn't accept an Event then no Window are created and the evaluation state is REJECTED"() {
		setup:
		pattern.accept = {false}
		when: Evaluation evaluation = pattern.evaluate(eventA)
		then:
		evaluation.state == State.REJECTED
		evaluation.processedEvents.size() == 1
	}
}
