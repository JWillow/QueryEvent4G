package org.homework.mcep.request

import org.homework.mcep.request.dsl.GRequestEngineBuilder;

import spock.lang.Specification

class EngineBuilderTest extends Specification {

	def engineBuilder = new GRequestEngineBuilder()
	RequestDispatcher engine = null
	def "Création d'un moteur traitant 2 requêtes de type count"() {
		setup:
			def acceptClosure = {}
			def acceptClosure1 = {}
		
		when: 
		RequestDispatcher engine = engineBuilder.engine() {
			count(description:"req")
			count(description:"req1")
		}
		
		then:
		assert engine.requestEngines.size() == 2
		assert engine.requestEngines[0].request.description == "req"
		assert engine.requestEngines[1].request.description == "req1"
	}
	
	
}
