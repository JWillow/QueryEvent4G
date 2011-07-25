package org.homework.mcep.request

import spock.lang.Specification;

class CounterSpecification extends Specification {
	def "Must represent a tree structure"() {
		setup:
		Counter.reset()
		Counter.start()
		Counter.start()
		Counter.stop()
		Counter.start()
		when:def id = Counter.getId()
		then:id == "1.2"
	}
	
	def "Must represent a tree structure 2.1"() {
		setup:
		Counter.reset()
		Counter.start()
			Counter.start()
			Counter.stop()
			Counter.start()
			Counter.stop()
		Counter.stop()
		Counter.start()
		Counter.start()
		when:def id = Counter.getId()
		then:id == "2.1"
	}
	
	def startAndTest(def valueToTest) {
		Counter.start()
		assert Counter.getId() == valueToTest
	}
	
	def startStopAndTest(def valueToTest) {
		Counter.start()
		assert Counter.getId() == valueToTest
		Counter.stop()
	}
	def "Must represent a tree structure 3.2.1"() {
		when:
		Counter.reset()
		startAndTest("1")
			startStopAndTest("1.1")
			startStopAndTest("1.2")
		Counter.stop()
		startAndTest("2")
			startStopAndTest("2.1")
		Counter.stop()
		startAndTest("3")
			startAndTest("3.1")
				startStopAndTest("3.1.1")
				startStopAndTest("3.1.2")
				startStopAndTest("3.1.3")
			Counter.stop()
			startAndTest("3.2")
				startStopAndTest("3.2.1")
				startStopAndTest("3.2.2")
			Counter.stop()
		then:assert true
	}
}
