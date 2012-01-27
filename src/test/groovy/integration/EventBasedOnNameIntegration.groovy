package integration

import org.qe4g.Event
import org.qe4g.request.dsl.GRequestEngineBuilder

import spock.lang.AutoCleanup;
import spock.lang.Specification;

class EventBasedOnNameIntegration extends Specification {

	def eventA = new Event(names:["A"])
	def eventB = new Event(names:["B"])
	def eventC = new Event(names:["C"])
	def eventD = new Event(names:["D"])

	Event newEvent(String name) {
		return new Event(names:[name]);
	}
	
	def gRequestEngineBuilder = new GRequestEngineBuilder();

	@AutoCleanup("shutdown")
	def requestEngine;

	def patternDetected = 0;
	def controlClosure = {context,event -> patternDetected++}


	/**
	 * <pre>
	 * 	pattern() {
	 *		event(name:'A')
	 *		event(name:'B')
	 *	}
	 * </pre>
	 */
	private void definedPatternBasedOnNamedEvent() {
		requestEngine = gRequestEngineBuilder.engine {
			request {
				pattern() {
					event(name:'A')
					event(name:'B')
				}
				onPatternDetection { function(core:controlClosure) }
			}
		}
	}

	def "Simple case, we define a pattern on two named event as only criteria. Positive evaluation"() {
		setup:
		definedPatternBasedOnNamedEvent()
		when:
		requestEngine.onEvent newEvent("A")
		requestEngine.onEvent eventB
		then :
		patternDetected == 1
	}

	def "Simple case, we define a pattern on two named event as only criteria. Negative evaluation"() {
		setup:
		definedPatternBasedOnNamedEvent()
		when:
		requestEngine.onEvent newEvent("A")
		requestEngine.onEvent newEvent("A")
		then :
		patternDetected == 0
	}

	def "Simple case, we define a pattern on two named event as only criteria. After a negative evaluation, the pattern is detected"() {
		setup:
		definedPatternBasedOnNamedEvent()
		when:
		requestEngine.onEvent newEvent("A")
		requestEngine.onEvent newEvent("A")
		requestEngine.onEvent newEvent("B")
		then :
		patternDetected == 1
	}

	def "Simple case, we define a pattern on two named event as only criteria. Two detection"() {
		setup:
		definedPatternBasedOnNamedEvent()
		when:
		requestEngine.onEvent newEvent("A")
		requestEngine.onEvent newEvent("B")
		requestEngine.onEvent newEvent("A")
		requestEngine.onEvent newEvent("B")
		then :
		patternDetected == 2
	}

	/**
	 * <pre>
	 * 	pattern() {
	 *		event(name:'A')
	 *		event(name:'B')
	 *      event(name:'C')
	 *      event(name:'D')
	 *	}
	 * </pre>
	 */
	private void definedLargePatternBasedOnNamedEvent() {
		requestEngine = gRequestEngineBuilder.engine {
			request {
				pattern() {
					event(name:'A')
					event(name:'B')
					event(name:'C')
					event(name:'D')
				}
				onPatternDetection { function(core:controlClosure) }
			}
		}
	}
	
	def "Simple case, we define a pattern on 4 named event as only criteria. One detection"() {
		setup:
		definedLargePatternBasedOnNamedEvent()
		when:
		requestEngine.onEvent newEvent("A")
		requestEngine.onEvent newEvent("B")
		requestEngine.onEvent newEvent("C")
		requestEngine.onEvent newEvent("D")
		then :
		patternDetected == 1
	}
	
	def "Simple case, we define a pattern on 4 named event as only criteria. One detection, with parasyte event"() {
		setup:
		definedLargePatternBasedOnNamedEvent()
		
		when:
		requestEngine.onEvent newEvent("A")
		requestEngine.onEvent newEvent("B")
		requestEngine.onEvent newEvent("D")
		requestEngine.onEvent newEvent("C")
		
		requestEngine.onEvent newEvent("A")
		requestEngine.onEvent newEvent("B")
		requestEngine.onEvent newEvent("C")
		requestEngine.onEvent newEvent("D")

		then :
		patternDetected == 1
	}
}
