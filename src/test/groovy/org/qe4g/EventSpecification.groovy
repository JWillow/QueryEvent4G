package org.qe4g

import spock.lang.Specification;

class EventSpecification extends Specification {

	def "If event has no names then the event is inconsistent"() {
		setup :
		Event event = new Event(names:[])
		when:	
		boolean result = event.isInconsistent()
		then:
		assert true == result
	}
	
	def "If event has no names and attributes available then the event is inconsistent"() {
		setup :
		Event event = new Event(names:[],attributes:[att:"value"])
		when:
		boolean result = event.isInconsistent()
		then:
		assert true == result
	}
	
	def "If event has names and no attributes available then the event is consistent"() {
		setup :
		Event event = new Event(names:["MainEvent"],attributes:[:])
		when:
		boolean result = event.isInconsistent()
		then:
		assert false == result
	}
		
	def "If event has names and attributes available then the event is consistent"() {
		setup :
		Event event = new Event(names:["MainEvent"],attributes:[att:"value"])
		when:
		boolean result = event.isInconsistent()
		then:
		assert false == result
	}
}
