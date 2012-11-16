package integration

import org.qe4g.Event
import org.qe4g.request.RequestDispatcher
import org.qe4g.request.dsl.GRequestEngineBuilder
import spock.lang.Specification;
import groovy.lang.IntRange

class EventBasedOnNameAndAttributeWithLinkIntegration extends Specification {


	def gRequestEngineBuilder = new GRequestEngineBuilder();
	def requestEngine;

	def patternDetected = 0;
	def controlClosure = {context,event -> patternDetected++}

	private Event newBEventB() {
		return new Event(names:["B"],attributes:[userId:"B"]);
	}
		
	private Event newBEventA() {
		return new Event(names:["A"],attributes:[userId:"B"]);
	}

	private Event newAEventC() {
		return new Event(names:["C"],attributes:[userId:"A"]);
	}
		
	private Event newAEventA() {
		return new Event(names:["A"],attributes:[userId:"A"]);
	}
	
	private Event newAEventB() {
		return new Event(names:["B"],attributes:[userId:"A"]);
	}
	
	/**
	 * <pre>
	 *	pattern() {
	 *		event(name:'A')
	 *		event(name:'B',linkOn:"userId")
	 * 	}	 
	 * </pre>
	 */
	private void definedPatternWithLink() {
		requestEngine = gRequestEngineBuilder.engine {
			request {
				pattern() {
					event(name:'A')
					event(name:'B',linkOn:"userId")
				}
				onPatternDetection { function(core:controlClosure) }
			}
		}
	}

	def "Insert user B event inside the sequence"() {
		setup:
		definedPatternWithLink()
		when:
		requestEngine.onEvent newAEventA()
		requestEngine.onEvent newBEventA()
		requestEngine.onEvent newAEventB()
		then : "We detect the pattern for user A only"
		patternDetected == 1
	}

	def "Insert mixed event between user A and user B"() {
		setup:
		definedPatternWithLink()
		when:
		requestEngine.onEvent newAEventA()
		requestEngine.onEvent newBEventA()
		requestEngine.onEvent newAEventB()
		requestEngine.onEvent newBEventB()
		then : "We detect the pattern for user A and user B"
		patternDetected == 2
	}

	def "Insert mixed event between user A and user B. Invalid User A event sequence by using an event of type C"() {
		setup:
		definedPatternWithLink()
		when:
		requestEngine.onEvent newAEventA()
		requestEngine.onEvent newBEventA()
		// INVALID - Reset all
		requestEngine.onEvent newAEventC()
		requestEngine.onEvent newBEventA()
		requestEngine.onEvent newAEventB()
		requestEngine.onEvent newBEventB()
		then : "We detect the pattern for user B (bEvent)"
		patternDetected == 1
	}

	/**
	 * <pre>
	 *	pattern() {
	 *		event(name:'A')
	 *		event(name:'B',linkOn:"userId")
	 *		event(name:'C')
	 * 	}
	 * </pre>
	 */
	private void definedPatternWithLinkAndAnotherCEventWithoutLink() {
		requestEngine = gRequestEngineBuilder.engine {
			request {
				pattern() {
					event(name:'A')
					event(name:'B',linkOn:"userId")
					event(name:'C')
				}
				onPatternDetection { function(core:controlClosure) }
			}
		}
	}

	def "With pattern 'definedPatternWithLinkAndAnotherCEventWithoutLink'. Insert mixed event between user A and user B. Invalid User A event sequence by using an event of type C"() {
		setup:
		definedPatternWithLinkAndAnotherCEventWithoutLink()
		when:
		requestEngine.onEvent newAEventA()
		requestEngine.onEvent newAEventB()
		requestEngine.onEvent newBEventA()
		requestEngine.onEvent newBEventB()
		and:"This event trigger the detection of the two other pattern. The event of type C has no link defined inside the pattern"
		requestEngine.onEvent newAEventC()
		then : "We detect the pattern for user B (bEvent) and A"
		patternDetected == 2
	}

	def "With pattern 'definedPatternWithLinkAndAnotherCEventWithoutLink'. Insert mixed event between user A and user B. Invalid User B event sequence by using two event of type B"() {
		setup:
		definedPatternWithLinkAndAnotherCEventWithoutLink()
		when:
		requestEngine.onEvent newAEventA()
		requestEngine.onEvent newAEventB()
		requestEngine.onEvent newBEventA()
		requestEngine.onEvent newBEventB()
		and:"Invalid the event sequence for event B"
		requestEngine.onEvent newBEventB()
		and:"This event trigger the detection of the pattern for user A "
		requestEngine.onEvent newAEventC()
		then : "We detect the pattern for user B (bEvent) and A"
		patternDetected == 1
	}
	
	/**
	* <pre>
	*	pattern() {
	*		event(name:'A')
	*		event(name:'B',linkOn:"userId")
	*		event(name:'C',linkOn:"userId")
	* 	}
	* </pre>
	*/
   private void definedPatternWithLinkAndAnotherCEvent() {
	   requestEngine = gRequestEngineBuilder.engine {
		   request {
			   pattern() {
				   event(name:'A')
				   event(name:'B',linkOn:"userId")
				   event(name:'C',linkOn:"userId")
			   }
			   onPatternDetection { function(core:controlClosure) }
		   }
	   }
   }

   def "With pattern 'definedPatternWithLinkAndAnotherCEvent'. Insert mixed event between user A and user B. Invalid User A event sequence by using an event of type C"() {
	   setup:
	   definedPatternWithLinkAndAnotherCEvent()
	   when:
	   requestEngine.onEvent newAEventA()
	   requestEngine.onEvent newBEventA()
	   requestEngine.onEvent newAEventB()
	   requestEngine.onEvent newBEventB()
	   requestEngine.onEvent newAEventC()
	   then : "We detect the pattern for user A"
	   patternDetected == 1
   }
}