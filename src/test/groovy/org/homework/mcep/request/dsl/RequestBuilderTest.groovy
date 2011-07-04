package org.homework.mcep.request.dsl


import spock.lang.Specification;

class RequestBuilderTest extends Specification {

	def requestBuilder = new GRequestEngineBuilder();

	def "Contruction d'un request de type count"() {
		setup:
		def dateExtractor = {}
		def selectClosure = {}
		def selectClosure2 = {}
		def acceptClosure = {}
		def notificationClosure = {}
		def groupByClosure = {}
		
		when :
		def requestEngine = requestBuilder.count(accept:acceptClosure,notification:notificationClosure,groupBy:groupByClosure,timeNotificationInterval:34,date:dateExtractor) {
			event(name:'RessourceEvent',select:selectClosure)
			event(name:'OtherRessourceEvent',select:selectClosure2)
		}
		
		then :
		assert requestEngine
		assert requestEngine.request.eventDefinitions.size() == 2
		assert requestEngine.request.eventDefinitions[0].name == 'RessourceEvent'
		assert requestEngine.request.eventDefinitions[0].select == selectClosure
		assert requestEngine.request.eventDefinitions[1].name == 'OtherRessourceEvent'
		assert requestEngine.request.eventDefinitions[1].select == selectClosure2
		assert requestEngine.request.date == dateExtractor
		assert requestEngine.request.timeNotificationInterval == 34
		assert requestEngine.request.groupBy == groupByClosure
		assert requestEngine.request.notification == notificationClosure
		assert requestEngine.request.accept == acceptClosure
	}
}
