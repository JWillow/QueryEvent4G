package integration

import org.qe4g.Event
import org.qe4g.request.RequestDispatcher
import org.qe4g.request.dsl.GRequestEngineBuilder

import spock.lang.AutoCleanup;
import spock.lang.Specification;
import groovy.lang.IntRange

class EventBasedOnNameAndAttributeWithOccurIntegration extends Specification {

	def eventA = new Event(names:["A"],attributes:[userId:"01", test:"A"])
	def eventB = new Event(names:["B"],attributes:[userId:"01", test:"B"])
	def eventBBis = new Event(names:["B"],attributes:[userId:"01",test:"BBis"])
	def eventC = new Event(names:["C"],attributes:[test:"C"])

	def gRequestEngineBuilder = new GRequestEngineBuilder();

	@AutoCleanup("shutdown")
	def requestEngine;

	def patternDetected = 0;
	def controlClosure = {context,events ->  patternDetected++ }


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
		if(eventParam == eventA) {
			rangeA = range
		} else if (eventParam == eventB){
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
		definedPatternBasedOnNamedEventAndAttributeValueWithOccurs({true},eventA, 2..3)
		when:
		// OK
		requestEngine.onEvent eventA
		requestEngine.onEvent eventA
		requestEngine.onEvent eventB
		then :
		patternDetected == 1
	}
	
	def "Simple case with 2..3 constraint for second event in pattern"() {
		setup:
		definedPatternBasedOnNamedEventAndAttributeValueWithOccurs({true},eventB, 2..3)
		when:
		// OK
		requestEngine.onEvent eventA
		requestEngine.onEvent eventB
		requestEngine.onEvent eventB
		then :
		patternDetected == 1
	}



	def "Simple case, we define a pattern on two named event and attribute criteria with occurs criteria on second Event. Positive evaluation, test max limit"() {
		setup:
		definedPatternBasedOnNamedEventAndAttributeValueWithOccurs({true},eventB, 2..3)
		when:
		// FAILED
		requestEngine.onEvent eventA
		requestEngine.onEvent eventB
		// OK
		requestEngine.onEvent eventA
		requestEngine.onEvent eventB
		requestEngine.onEvent eventB
		// OK
		requestEngine.onEvent eventA
		requestEngine.onEvent eventB
		requestEngine.onEvent eventB
		requestEngine.onEvent eventB
		// FAILED
		requestEngine.onEvent eventB
		requestEngine.onEvent eventA
		requestEngine.onEvent eventB
		then :
		patternDetected == 2
	}

	def "Simple case, we define a pattern on two named event and attribute criteria with occurs criteria. Negative evaluation"() {
		setup:
		definedPatternBasedOnNamedEventAndAttributeValueWithOccurs({true},eventA, 2..3)
		when:
		// FAILED
		requestEngine.onEvent eventA
		requestEngine.onEvent eventB
		// OK
		requestEngine.onEvent eventA
		requestEngine.onEvent eventA
		requestEngine.onEvent eventB
		// FAILED
		requestEngine.onEvent eventB
		// OK
		requestEngine.onEvent eventA
		requestEngine.onEvent eventA
		requestEngine.onEvent eventA
		requestEngine.onEvent eventB
		// FAILED
		requestEngine.onEvent eventA
		requestEngine.onEvent eventA
		requestEngine.onEvent eventA
		requestEngine.onEvent eventA
		requestEngine.onEvent eventB

		then :
		patternDetected == 2
	}

	def "Simple case, we define a pattern on two named event and attribute criteria with occurs criteria. Test optional event. Positive evaluation"() {
		setup:
		definedPatternBasedOnNamedEventAndAttributeValueWithOccurs({true},eventA, 0..3)
		when:
		// ---- OK
		requestEngine.onEvent eventB
		// ---- OK
		requestEngine.onEvent eventA
		requestEngine.onEvent eventB
		// ---- OK
		requestEngine.onEvent eventA
		requestEngine.onEvent eventA
		requestEngine.onEvent eventB
		// ---- OK
		requestEngine.onEvent eventA
		requestEngine.onEvent eventA
		requestEngine.onEvent eventA
		requestEngine.onEvent eventB
		// ---- OK because the first series of eventA is invalid, but the eventB alone is valid
		requestEngine.onEvent eventA
		requestEngine.onEvent eventA
		requestEngine.onEvent eventA
		requestEngine.onEvent eventA
		requestEngine.onEvent eventB
		then :
		patternDetected == 5
	}
}
