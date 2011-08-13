package integration

import org.qe4g.Event
import org.qe4g.request.dsl.GRequestEngineBuilder
import spock.lang.Specification;

class EventBasedOnNameIntegration extends Specification {

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
	   requestEngine.onEvent eventA
	   requestEngine.onEvent eventB
	   then :
	   patternDetected == 1
   }

   def "Simple case, we define a pattern on two named event as only criteria. Negative evaluation"() {
	   setup:
	   definedPatternBasedOnNamedEvent()
	   when:
	   requestEngine.onEvent eventA
	   requestEngine.onEvent eventA
	   then :
	   patternDetected == 0
   }

   def "Simple case, we define a pattern on two named event as only criteria. After a negative evaluation, the pattern is detected"() {
	   setup:
	   definedPatternBasedOnNamedEvent()
	   when:
	   requestEngine.onEvent eventA
	   requestEngine.onEvent eventA
	   requestEngine.onEvent eventB
	   then :
	   patternDetected == 1
   }

   def "Simple case, we define a pattern on two named event as only criteria. Two detection"() {
	   setup:
	   definedPatternBasedOnNamedEvent()
	   when:
	   requestEngine.onEvent eventA
	   requestEngine.onEvent eventB
	   requestEngine.onEvent eventA
	   requestEngine.onEvent eventB
	   then :
	   patternDetected == 2
   }
}
