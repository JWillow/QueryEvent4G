package org.homework.mcep.request

import static org.homework.mcep.request.Window.State.*

import org.homework.mcep.Event
import org.homework.mcep.request.Window.State

import spock.lang.Specification

class RequestSpecification extends Specification {

	def groupId = 'myId'
	def event = new Event(names:["MainEvent"],attributes:[test:"value"])
	Window window = Mock(Window)
	EventListener eventListener = Mock(EventListener)
	Function function = Mock(Function)
	Request request = new Request(eventListeners:[eventListener],groupBy:{groupId},accept:{true},functions:[function])

	def registerWindowInEngine() {
		window.id >> groupId
		request.windows[groupId] = window
	}

	def "When an Event is performed and, no Window are registred or no Window are linked with the grouping id extract from Event, then a new Window is created and used."(){
		setup:
		request.metaClass.createWindow = {a,b -> window}

		when: request.onEvent(event)

		then: "The new Window performed the Event and it state is OPEN"
		1 * window.processEvent(event) >> State.OPEN
	}

	def "When after an Event performed if the Window is in OPEN state then this Window is registred inside the Request"() {
		
	}
	
	def "When an Event is performed then a new Window is created. If the state's Window is OPEN then the Window is registred inside the Request"(){
		setup:
		request.metaClass.createWindow = {a,b -> window}

		when: request.onEvent(event)

		then: "The new Window performed the Event and it state is OPEN"
		1 * window.processEvent(event) >> State.OPEN
		and: "The Window has been registred inside the Request"
		request.windows.size() == 1
		and: "La fenêtre n'étant pas dans l'état CLOSED, la fonction test n'a pas été appelée"
		0 * function.onPatternDetection(_,_)
	}
	
	def "Un nouvel événement est reçu qui vient compléter une fenêtre déjà existante. La fenêtre reste dans l'état OPEN."() {
		setup: registerWindowInEngine()

		when: request.onEvent(event)

		then: "Sur le traitement de l'événement par la fenêtre, la fenêtre reste dans l'état OPEN"
		1 * window.processEvent(event) >> OPEN
		and: "La fenêtre n'étant pas dans l'état CLOSED, la fonction test n'a pas été appelée"
		0 * function.onPatternDetection(_,_)
		and : "Comme nous avons utilisé une fenêtre déjà existante, aucune nouvelle fenetre n'a été enregistré dans le registre du moteur"
		request.windows.size() == 1
	}

	def "Le moteur traite un événement qui n'est pas accepté par la requête"(){
		setup: "La requête n'accepte aucun événement"
		requestDefinition.accept = {false}

		when: request.onEvent(event)

		then: "Aucune fenêtre n'a été recherchée & créée"
		request.windows.size() == 0
		and: "La fenêtre n'étant pas dans l'état CLOSED, la fonction test n'a pas été appelée"
		0 * function.onPatternDetection(_,_)
	}

	def "Lorsqu'une fenêtre est dans l'état BROKEN, celle-ci est supprimée du registre, il n'y a pas d'appel aux Functions"() {
		setup: registerWindowInEngine()

		when: request.onEvent(event)

		then: "Sur le traitement de l'événement par la fenêtre, la fenêtre est passée dans l'état BROKEN"
		1 * window.processEvent(event) >> BROKEN
		and: "La fenêtre n'étant pas dans l'état CLOSED, la fonction test n'a pas été appelée"
		0 * function.onPatternDetection(_,_)
		and : "Etant dans l'état BROKEN la fenêtre a été supprimée du registre"
		request.windows.size() == 0
	}

	def "Lorqu'un événement clotûre (état CLOSED) une fenêtre celle-ci notifie les Functions enregistrée et la fenêtre est supprimée du registre"() {
		setup:registerWindowInEngine()

		when: request.onEvent(event)

		then: "Sur le traitement de l'événement par la fenêtre, la fenêtre est passée dans l'état CLOSED"
		1 * window.processEvent(event) >> CLOSED
		and: "La fenêtre n'étant pas dans l'état CLOSED, la fonction test n'a pas été appelée"
		1 * function.onPatternDetection(requestDefinition,_)
		and : "Etant dans l'état CLOSED la fenêtre a été supprimée du registre"
		request.windows.size() == 0
	}

	def "Lorsqu'un événement est reçu par la Request, les listeners Event sont appelés avant et après le traitement de l'Event"() {
		setup:
		request.metaClass.createWindow = {a,b -> window}
		when: request.onEvent(event)
		then :
		1* eventListener.beforeEventProcessing(requestDefinition, _, event)
		and: "Sur le traitement de l'événement par la fenêtre, la fenêtre reste dans l'état OPEN"
		1 * window.processEvent(event) >> OPEN
		1* eventListener.afterEventProcessed(requestDefinition, _, event)
	}
}
