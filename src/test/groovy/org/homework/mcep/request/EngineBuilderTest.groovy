package org.homework.mcep.request

import spock.lang.Specification

class EngineBuilderTest extends Specification {

	def engineBuilder = new GRequestEngineBuilder()
	Engine engine = null
	def "Cr�ation d'un moteur traitant 2 requ�tes de type count"() {
		setup:
			def acceptClosure = {}
			def acceptClosure1 = {}
		
		when: 
		Engine engine = engineBuilder.engine() {
			count(description:"req")
			count(description:"req1")
		}
		
		then:
		assert engine.requestEngines.size() == 2
		assert engine.requestEngines[0].request.description == "req"
		assert engine.requestEngines[1].request.description == "req1"
	}
	
	
}