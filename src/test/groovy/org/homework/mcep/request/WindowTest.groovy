package org.homework.mcep.request;

import org.homework.mcep.Event
import org.homework.mcep.request.Window.State;

import spock.lang.Specification;

class WindowTest extends Specification {

	def eventDefinition = Mock(EventDefinition)
	def event = new Event(attributes:[test:"value"],names:[])
	def event2 = new Event(attributes:[test:"value2"],names:[])

	def "Traitement d'un evenement matchant avec l'unique requestEventDefinition"() {
		setup: "Définition d'une fenêtre avec une seule définition qui évaluera positivement l'événement traité"
		eventDefinition.evaluate(event) >> true
		def window = new Window(id:'toto',eventDefinitions:[eventDefinition])

		when:
		window.processEvent(event)

		then: "L'événement a bien été évalué positivement, comme il n'y a qu'une seule définition d'événement pour la fenêtre celle-ci est considérée comme COMPLETED"
		window.proceedEvents.size() == 1
		window.getState() == State.CLOSED
	}

	def "2 définitions ont été définies ; 2 événements sont reçus ; l'un matche, l'autre pas ; la fenêtre est donc invalide"() {
		setup:
		eventDefinition.evaluate(event) >> true
		eventDefinition.evaluate(event2) >> false
		def window = new Window(id:'toto',eventDefinitions:[
			eventDefinition,
			eventDefinition
		])

		when:
		window.processEvent(event)
		window.processEvent(event2)

		then:"Les 2 événements ont été traités, mais le second événement ayant été négativement évalué la fenêtre est dans l'état BROKEN"
		window.proceedEvents.size() == 2
		window.getState() == State.BROKEN
	}

	def "2 définitions ont été définies ; 1 événement est reçu ; il matche ; la fenêtre est donc continuer"() {
		setup:
		eventDefinition.evaluate(event) >> true
		def window = new Window(id:'toto',eventDefinitions:[
			eventDefinition,
			eventDefinition
		])

		when:
		window.processEvent(event)

		then:
		window.proceedEvents.size() == 1
		window.getState() == State.OPEN
	}

	def "Traitement d'un evenement ne matchant pas avec l'unique requestEventDefinition"() {
		setup:"On définit une fenêtre avec une seule définition qui évaluera négativement l'événement"
		eventDefinition.evaluate(event) >> false
		def window = new Window(id:'toto',eventDefinitions:[eventDefinition])

		when:
		window.processEvent(event)

		then:"L'événement a été enregistré mais l'état de la fenêtre est passé à BROKEN"
		window.proceedEvents.size() == 1
		window.getState() == State.BROKEN
	}

	def "Traitement de 2 evenements matchant avec les eventDefinitions"() {
		setup:"On définit une fenêtre avec 2 définitions. Les 2 événements existant seront évalué positivement"
		eventDefinition.evaluate(event) >> true
		eventDefinition.evaluate(event2) >> true
		def window = new Window(id:'toto',eventDefinitions:[
			eventDefinition,
			eventDefinition
		])

		when:"On traite les 2 événements"
		window.processEvent(event)
		window.processEvent(event2)

		then:"Les 2 événements ont été évalués positivement, comme la fenêtre n'est définie qu'avec 2 définitions la fenêtre est défini comme COMPLETED"
		window.proceedEvents.size() == 2
		window.getState() == State.CLOSED
	}
}