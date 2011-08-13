package integration

import org.qe4g.Event
import org.qe4g.request.RequestDispatcher
import org.qe4g.request.dsl.GRequestEngineBuilder
import spock.lang.Specification;
import groovy.lang.IntRange

class EventBasedOnNameAndAttributeWithOccurIntegration extends Specification {

	def eventA = new Event(names:["A"],attributes:[userId:"01", test:"A"])
	def eventB = new Event(names:["B"],attributes:[userId:"01", test:"B"])
	def eventBBis = new Event(names:["B"],attributes:[userId:"01",test:"BBis"])
	def eventC = new Event(names:["C"],attributes:[test:"C"])

	def gRequestEngineBuilder = new GRequestEngineBuilder();
	def requestEngine;

	def patternDetected = 0;
	def controlClosure = {context,event -> patternDetected++}


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

	def "Simple case, we define a pattern on two named event and attribute criteria with occurs criteria. Positive evaluation"() {
		setup:
		definedPatternBasedOnNamedEventAndAttributeValueWithOccurs({true},eventA, 2..3)
		when:
		requestEngine.onEvent eventA
		requestEngine.onEvent eventA
		requestEngine.onEvent eventB
		then :
		patternDetected == 1
	}

	def "Simple case, we define a pattern on two named event and attribute criteria with occurs criteria on first Event. Positive evaluation, test max limit"() {
		setup:
		definedPatternBasedOnNamedEventAndAttributeValueWithOccurs({true},eventA, 2..3)
		when:
		requestEngine.onEvent eventA
		requestEngine.onEvent eventA
		requestEngine.onEvent eventA
		requestEngine.onEvent eventB
		then :
		patternDetected == 1
	}

	def "Simple case, we define a pattern on two named event and attribute criteria with occurs criteria on second Event. Positive evaluation, test max limit"() {
		setup:
		definedPatternBasedOnNamedEventAndAttributeValueWithOccurs({true},eventB, 2..3)
		when:
		requestEngine.onEvent eventA
		requestEngine.onEvent eventB
		requestEngine.onEvent eventB
//		requestEngine.onEvent eventB
//		requestEngine.onEvent eventA
//		requestEngine.onEvent eventB
//		requestEngine.onEvent eventB
		then :
		patternDetected == 1
	}
			
	def "Simple case, we define a pattern on two named event and attribute criteria with occurs criteria. Negative evaluation"() {
		setup:
		definedPatternBasedOnNamedEventAndAttributeValueWithOccurs({true},eventA, 2..3)
		when:
		requestEngine.onEvent eventA
		requestEngine.onEvent eventB
		then :
		patternDetected == 0
	}
	
	def "Simple case, we define a pattern on two named event and attribute criteria with occurs criteria. Test optional event. Positive evaluation"() {
		setup:
		definedPatternBasedOnNamedEventAndAttributeValueWithOccurs({true},eventA, 0..3)
		when:
		requestEngine.onEvent eventB
		then :
		patternDetected == 1
	}
}
