package integration

import groovy.lang.Closure;

import org.qe4g.Event
import org.qe4g.request.dsl.GRequestEngineBuilder

import spock.lang.AutoCleanup;
import spock.lang.Specification;

class EventBasedOnNameAndAttributeIntegration extends Specification {

	def eventA = new Event(names:["A"],attributes:[userId:"01", test:"A"],triggeredTime:10)
	def eventB = new Event(names:["B"],attributes:[userId:"01", test:"B"],triggeredTime:12)
	def eventBBis = new Event(names:["B"],attributes:[userId:"01",test:"BBis"])
	def eventC = new Event(names:["C"],attributes:[test:"C"])

	def gRequestEngineBuilder = new GRequestEngineBuilder();
	
	@AutoCleanup("shutdown")
	def requestEngine;

	def patternDetected = 0;
	def controlClosure = {context,path -> 
		patternDetected++
	}

	/**
	 * <pre>
	 * 	pattern(accept:accept) {
	 *		event(name:'A',attributes:[userId:'01'])
	 *		event(name:'B',attributes:[test:'B'])
	 *	}
	 * </pre>
	 * @param accept
	 */
	/**
	 * @param accept
	 */
	private void definedPatternBasedOnNamedEventAndAttributeValue(Closure accept) {
		requestEngine = gRequestEngineBuilder.engine {
			request {
				pattern(accept:accept) {
					event(name:'A',attributes:[userId:'01'])
					event(name:'B',attributes:[test:'B'])
				}
				onPatternDetection { function(core:controlClosure) }
			}
		}
	}

	def "Simple case, we define a pattern on two named event and attribute criteria. Positive evaluation"() {
		setup:
		definedPatternBasedOnNamedEventAndAttributeValue({true})
		when:
		requestEngine.onEvent eventA
		requestEngine.onEvent eventB
		then :
		patternDetected == 1
	}

	def "Simple case, we define a pattern on two named event and attribute criteria. Negative evaluation"() {
		setup:
		definedPatternBasedOnNamedEventAndAttributeValue({true})
		when:
		requestEngine.onEvent eventA
		requestEngine.onEvent eventBBis
		then :
		patternDetected == 0
	}

	def "Simple case, we define a pattern on two named event and attribute criteria. Negative evaluation, because an unexpected C event occur."() {
		setup:
		definedPatternBasedOnNamedEventAndAttributeValue({true})
		when:
		requestEngine.onEvent eventA
		requestEngine.onEvent eventC
		requestEngine.onEvent eventB
		then :
		patternDetected == 0
	}

	def "Simple case, we define a pattern on two named event and attribute criteria. Positive evaluation, because the request ignore the C."() {
		setup:
		definedPatternBasedOnNamedEventAndAttributeValue({event -> !Collections.disjoint(['A', 'B'],(event.names))})
		when:
		requestEngine.onEvent eventA
		requestEngine.onEvent eventC
		requestEngine.onEvent eventB
		then :
		patternDetected == 1
	}

	def "Simple case, we define a pattern on two named event and attribute criteria. Positive evaluation, because the request ignore the C. We processed multiple events"() {
		setup:
		definedPatternBasedOnNamedEventAndAttributeValue({event -> !Collections.disjoint(['A', 'B'],(event.names))})
		when:
		requestEngine.onEvent eventA
		requestEngine.onEvent eventC
		requestEngine.onEvent eventB
		requestEngine.onEvent eventB
		requestEngine.onEvent eventA
		requestEngine.onEvent eventB
		requestEngine.onEvent eventC
		then :
		patternDetected == 2
	}
}
