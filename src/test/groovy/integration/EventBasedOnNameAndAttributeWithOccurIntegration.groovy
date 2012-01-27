package integration

import org.qe4g.Event
import org.qe4g.request.RequestDispatcher
import org.qe4g.request.dsl.GRequestEngineBuilder

import spock.lang.AutoCleanup;
import spock.lang.Specification;
import groovy.lang.IntRange

class EventBasedOnNameAndAttributeWithOccurIntegration extends Specification {


	def gRequestEngineBuilder = new GRequestEngineBuilder();

	@AutoCleanup("shutdown")
	def requestEngine;

	def patternDetected = 0;
	def controlClosure = {context,events ->  patternDetected++ }

	Event newEventA() {
		return new Event(names:["A"],attributes:[userId:"01", test:"A"])
	}
	Event newEventB() {
		return new Event(names:["B"],attributes:[userId:"01", test:"B"])
	}

	/**
	 * <pre>
	 * 	pattern(accept:accept) {
	 *		event(name:'A',attributes:[userId:'01'],occurs:range)
	 *		event(name:'B',attributes:[test:'B'])
	 *	}
	 * </pre>
	 * @param accept
	 */
	private void definedPatternBasedOnNamedEventAndAttributeValueWithOccurs(Closure accept,Event eventParam, IntRange range) {
		def rangeA = 1..1
		def rangeB = 1..1
		if(eventParam.names.contains("A")) {
			rangeA = range
		} else if (eventParam.names.contains('B')){
			rangeB = range
		}
		requestEngine = gRequestEngineBuilder.engine {
			request {
				pattern(accept:accept) {
					event(name:'A',attributes:[userId:'01'],occurs:rangeA)
					event(name:'B',attributes:[test:'B'],occurs:rangeB)
				}
				onPatternDetection { function(core:controlClosure) }
			}
		}
	}
	def "Simple case with 2..3 constraint for first event in pattern"() {
		setup:
		definedPatternBasedOnNamedEventAndAttributeValueWithOccurs({true},newEventA(), 2..3)
		when:
		// OK
		requestEngine.onEvent newEventA()
		requestEngine.onEvent newEventA()
		requestEngine.onEvent newEventA()
		requestEngine.onEvent newEventB()
		then :
		patternDetected == 1
	}

	def "Simple case with 2..3 constraint for second event in pattern"() {
		setup:
		definedPatternBasedOnNamedEventAndAttributeValueWithOccurs({true},newEventB(), 2..3)
		when:
		// OK
		requestEngine.onEvent newEventA()
		requestEngine.onEvent newEventB()
		requestEngine.onEvent newEventB()
		then :
		patternDetected == 1
	}



	def "Simple case, we define a pattern on two named event and attribute criteria with occurs criteria on second Event. Positive evaluation, test max limit"() {
		setup:
		definedPatternBasedOnNamedEventAndAttributeValueWithOccurs({true},newEventB(), 2..3)
		when:
		// FAILED
		requestEngine.onEvent newEventA()
		requestEngine.onEvent newEventB()
		// OK
		requestEngine.onEvent newEventA()
		requestEngine.onEvent newEventB()
		requestEngine.onEvent newEventB()
		// OK
		requestEngine.onEvent newEventA()
		requestEngine.onEvent newEventB()
		requestEngine.onEvent newEventB()
		requestEngine.onEvent newEventB()
		// FAILED
		requestEngine.onEvent newEventB()
		requestEngine.onEvent newEventA()
		requestEngine.onEvent newEventB()
		then :
		patternDetected == 2
	}

	def "Simple case, we define a pattern on two named event and attribute criteria with occurs criteria."() {
		setup:
		definedPatternBasedOnNamedEventAndAttributeValueWithOccurs({true},newEventA(), 2..3)
		when:
		requestEngine.onEvent newEventA()
		// OK
		requestEngine.onEvent newEventA()
		requestEngine.onEvent newEventA()
		requestEngine.onEvent newEventA()
		requestEngine.onEvent newEventB()

		then :
		patternDetected == 1
	}

	def "Simple case, we define a pattern on two named event and attribute criteria with occurs criteria. Negative evaluation"() {
		setup:
		definedPatternBasedOnNamedEventAndAttributeValueWithOccurs({true},newEventA(), 2..3)
		when:
		// FAILED
		requestEngine.onEvent newEventA()
		requestEngine.onEvent newEventB()
		// OK
		requestEngine.onEvent newEventA()
		requestEngine.onEvent newEventA()
		requestEngine.onEvent newEventB()
		// FAILED
		//requestEngine.onEvent newEventB()
		// OK
		//requestEngine.onEvent newEventA()
		//requestEngine.onEvent newEventA()
		//requestEngine.onEvent newEventA()
		//requestEngine.onEvent newEventB()
		// FAILED
		//requestEngine.onEvent newEventA()
		//requestEngine.onEvent newEventA()
		//requestEngine.onEvent newEventA()
		//requestEngine.onEvent newEventA()
		//requestEngine.onEvent newEventB()

		then :
		patternDetected == 1
	}

	def "Simple case, we define a pattern on two named event and attribute criteria with occurs criteria. Test optional event. Positive evaluation"() {
		setup:
		definedPatternBasedOnNamedEventAndAttributeValueWithOccurs({true},newEventA(), 0..3)
		when:
		// ---- OK
		requestEngine.onEvent newEventB()
		// ---- OK
		requestEngine.onEvent newEventA()
		requestEngine.onEvent newEventB()
		// ---- OK
		requestEngine.onEvent newEventA()
		requestEngine.onEvent newEventA()
		requestEngine.onEvent newEventB()
		// ---- OK
		requestEngine.onEvent newEventA()
		requestEngine.onEvent newEventA()
		requestEngine.onEvent newEventA()
		requestEngine.onEvent newEventB()
		// ---- OK because the first series of eventA is invalid, but the eventB alone is valid
		requestEngine.onEvent newEventA()
		requestEngine.onEvent newEventA()
		requestEngine.onEvent newEventA()
		requestEngine.onEvent newEventA()
		requestEngine.onEvent newEventB()
		then :
		patternDetected == 5
	}
}
