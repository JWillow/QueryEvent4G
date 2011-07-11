package org.homework.mcep.request

import org.homework.mcep.Event;
import org.homework.mcep.request.RequestDefinition;
import org.homework.mcep.request.Request;
import org.homework.mcep.request.Window;
import org.homework.mcep.request.Window.State;

import static org.homework.mcep.request.Window.State.*;

import spock.lang.Specification;

class RequestTest extends Specification {

	def groupId = 'myId'
	def event = new Event(attributes:[test:"value"])
	Window window = Mock(Window)
	EventListener eventListener = Mock(EventListener)
	Function function = Mock(Function)
	RequestDefinition requestDefinition = new RequestDefinition(eventListeners:[eventListener],groupBy:{groupId},accept:{true},functions:[function])
	Request request = new Request(requestDefinition:requestDefinition)

	def registerWindowInEngine() {
		window.id >> groupId
		request.windows[groupId] = window
	}

	def "Cr�ation d'une fen�tre sur la r�ception d'un �v�nement, pas d'appel aux Function"(){
		setup:
		request.metaClass.createWindow = {a,b -> window}

		when: request.onEvent(event)

		then: "La fen�tre nouvellement cr��e est appel�e pour traiter l'�v�nement, la fen�tre reste OPEN"
		1 * window.processEvent(event) >> State.OPEN
		and: "La fen�tre est enregistr�e aupr�s du moteur"
		request.windows.size() == 1
		and: "La fen�tre n'�tant pas dans l'�tat CLOSED, la fonction test n'a pas �t� appel�e"
		0 * function.onPatternDetection(_,_)
	}

	def "Un nouvel �v�nement est re�u qui vient compl�ter une fen�tre d�j� existante. La fen�tre reste dans l'�tat OPEN."() {
		setup: registerWindowInEngine()

		when: request.onEvent(event)

		then: "Sur le traitement de l'�v�nement par la fen�tre, la fen�tre reste dans l'�tat OPEN"
		1 * window.processEvent(event) >> OPEN
		and: "La fen�tre n'�tant pas dans l'�tat CLOSED, la fonction test n'a pas �t� appel�e"
		0 * function.onPatternDetection(_,_)
		and : "Comme nous avons utilis� une fen�tre d�j� existante, aucune nouvelle fenetre n'a �t� enregistr� dans le registre du moteur"
		request.windows.size() == 1
	}

	def "Le moteur traite un �v�nement qui n'est pas accept� par la requ�te"(){
		setup: "La requ�te n'accepte aucun �v�nement"
		requestDefinition.accept = {false}

		when: request.onEvent(event)

		then: "Aucune fen�tre n'a �t� recherch�e & cr��e"
		request.windows.size() == 0
		and: "La fen�tre n'�tant pas dans l'�tat CLOSED, la fonction test n'a pas �t� appel�e"
		0 * function.onPatternDetection(_,_)
	}

	def "Lorsqu'une fen�tre est dans l'�tat BROKEN, celle-ci est supprim�e du registre, il n'y a pas d'appel aux Functions"() {
		setup: registerWindowInEngine()

		when: request.onEvent(event)

		then: "Sur le traitement de l'�v�nement par la fen�tre, la fen�tre est pass�e dans l'�tat BROKEN"
		1 * window.processEvent(event) >> BROKEN
		and: "La fen�tre n'�tant pas dans l'�tat CLOSED, la fonction test n'a pas �t� appel�e"
		0 * function.onPatternDetection(_,_)
		and : "Etant dans l'�tat BROKEN la fen�tre a �t� supprim�e du registre"
		request.windows.size() == 0
	}

	def "Lorqu'un �v�nement clot�re (�tat CLOSED) une fen�tre celle-ci notifie les Functions enregistr�e et la fen�tre est supprim�e du registre"() {
		setup:registerWindowInEngine()

		when: request.onEvent(event)

		then: "Sur le traitement de l'�v�nement par la fen�tre, la fen�tre est pass�e dans l'�tat CLOSED"
		1 * window.processEvent(event) >> CLOSED
		and: "La fen�tre n'�tant pas dans l'�tat CLOSED, la fonction test n'a pas �t� appel�e"
		1 * function.onPatternDetection(requestDefinition,_)
		and : "Etant dans l'�tat CLOSED la fen�tre a �t� supprim�e du registre"
		request.windows.size() == 0
	}

	def "Lorsqu'un �v�nement est re�u par la Request, les listeners Event sont appel�s avant et apr�s le traitement de l'Event"() {
		setup:
		request.metaClass.createWindow = {a,b -> window}
		when: request.onEvent(event)
		then :
		1* eventListener.beforeEventProcessing(requestDefinition, _, event)
		and: "Sur le traitement de l'�v�nement par la fen�tre, la fen�tre reste dans l'�tat OPEN"
		1 * window.processEvent(event) >> OPEN
		1* eventListener.afterEventProcessed(requestDefinition, _, event)
	}
}
