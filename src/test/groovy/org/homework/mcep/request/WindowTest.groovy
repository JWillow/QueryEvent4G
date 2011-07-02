package org.homework.mcep.request;

import org.homework.mcep.Event
import org.homework.mcep.request.Window.State;

import spock.lang.Specification;

class WindowTest extends Specification {

	def eventDefinition = Mock(EventDefinition)
	def event = new Event(attributes:[test:"value"],names:[])
	def event2 = new Event(attributes:[test:"value2"],names:[])

	def "Traitement d'un evenement matchant avec l'unique requestEventDefinition"() {
		setup: "D�finition d'une fen�tre avec une seule d�finition qui �valuera positivement l'�v�nement trait�"
		eventDefinition.evaluate(event) >> true
		def window = new Window(id:'toto',eventDefinitions:[eventDefinition])

		when:
		window.processEvent(event)

		then: "L'�v�nement a bien �t� �valu� positivement, comme il n'y a qu'une seule d�finition d'�v�nement pour la fen�tre celle-ci est consid�r�e comme COMPLETED"
		window.proceedEvents.size() == 1
		window.getState() == State.CLOSED
	}

	def "2 d�finitions ont �t� d�finies ; 2 �v�nements sont re�us ; l'un matche, l'autre pas ; la fen�tre est donc invalide"() {
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

		then:"Les 2 �v�nements ont �t� trait�s, mais le second �v�nement ayant �t� n�gativement �valu� la fen�tre est dans l'�tat BROKEN"
		window.proceedEvents.size() == 2
		window.getState() == State.BROKEN
	}

	def "2 d�finitions ont �t� d�finies ; 1 �v�nement est re�u ; il matche ; la fen�tre est donc continuer"() {
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
		setup:"On d�finit une fen�tre avec une seule d�finition qui �valuera n�gativement l'�v�nement"
		eventDefinition.evaluate(event) >> false
		def window = new Window(id:'toto',eventDefinitions:[eventDefinition])

		when:
		window.processEvent(event)

		then:"L'�v�nement a �t� enregistr� mais l'�tat de la fen�tre est pass� � BROKEN"
		window.proceedEvents.size() == 1
		window.getState() == State.BROKEN
	}

	def "Traitement de 2 evenements matchant avec les eventDefinitions"() {
		setup:"On d�finit une fen�tre avec 2 d�finitions. Les 2 �v�nements existant seront �valu� positivement"
		eventDefinition.evaluate(event) >> true
		eventDefinition.evaluate(event2) >> true
		def window = new Window(id:'toto',eventDefinitions:[
			eventDefinition,
			eventDefinition
		])

		when:"On traite les 2 �v�nements"
		window.processEvent(event)
		window.processEvent(event2)

		then:"Les 2 �v�nements ont �t� �valu�s positivement, comme la fen�tre n'est d�finie qu'avec 2 d�finitions la fen�tre est d�fini comme COMPLETED"
		window.proceedEvents.size() == 2
		window.getState() == State.CLOSED
	}
}