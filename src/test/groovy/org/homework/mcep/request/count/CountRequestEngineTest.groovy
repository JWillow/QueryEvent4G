package org.homework.mcep.request.count

import org.homework.mcep.Event;
import org.homework.mcep.request.RequestDefinition;
import org.homework.mcep.request.Request;
import org.homework.mcep.request.Window;
import org.homework.mcep.request.Window.State;

import static org.homework.mcep.request.Window.State.*;

import spock.lang.Specification;

class CountRequestEngineTest extends Specification {


	def groupId = 'myId'
	def event = new Event(attributes:[test:"value"])
	Window window = Mock(Window)
	RequestDefinition request = new RequestDefinition(date:{13445l},timeNotificationInterval:500,groupBy:{groupId},accept:{true},notification:{assert false})
	Request countRequestEngine = new Request(request:request)

	def registerWindowInEngine() {
		window.id >> groupId
		countRequestEngine.windows[groupId] = window
	}


	def "Cr�ation d'une fen�tre sur la r�ception d'un �v�nement, pas de notification attendue de la part du moteur"(){
		setup:
		countRequestEngine.metaClass.createWindow = {def a,def b -> window}

		when: countRequestEngine.onEvent(event)

		then: "La fen�tre nouvellement cr��e est appel�e pour traiter l'�v�nement, la fen�tre reste OPEN"
		1 * window.processEvent(event) >> State.OPEN
		and: "La fen�tre est enregistr�e aupr�s du moteur"
		countRequestEngine.windows.size() == 1
		and: "La fen�tre n'�tant pas dans l'�tat CLOSED le compteur n'a pas �t� incr�ment�"
		countRequestEngine.count == 0
	}

	def "Un nouvel �v�nement est re�u qui vient compl�ter une fen�tre d�j� existante. La fen�tre reste dans l'�tat OPEN."() {
		setup: registerWindowInEngine()

		when: countRequestEngine.onEvent(event)

		then: "Sur le traitement de l'�v�nement par la fen�tre, la fen�tre reste dans l'�tat OPEN"
		1 * window.processEvent(event) >> OPEN
		and: "Etant dans l'�tat OPEN, le compteur du moteur n'est toujours pas incr�ment�"
		countRequestEngine.count == 0
		and : "Comme nous avons utilis� une fen�tre d�j� existante, aucune nouvelle fenetre n'a �t� enregistr� dans le registre du moteur"
		countRequestEngine.windows.size() == 1
	}

	def "Le moteur traite un �v�nement qui n'est pas accept� par la requ�te"(){
		setup: "La requ�te n'accepte aucun �v�nement"
		request.accept = {false}

		when: countRequestEngine.onEvent(event)

		then: "Aucune fen�tre n'a �t� recherch�e & cr��e"
		countRequestEngine.windows.size() == 0
		and: "Comme aucune fen�tre n'a �t� cr�� la fen�tre n'a pu �tre dans l'�tat CLOSED et incr�ment�e le compteur"
		countRequestEngine.count == 0
	}

	def "Lorsqu'une fen�tre est dans l'�tat BROKEN, celle-ci est supprim�e du registre, il n'y a pas d'incr�mentation du compteur g�n�ral"() {
		setup: registerWindowInEngine()

		when: countRequestEngine.onEvent(event)

		then: "Sur le traitement de l'�v�nement par la fen�tre, la fen�tre est pass�e dans l'�tat BROKEN"
		1 * window.processEvent(event) >> BROKEN
		and: "Etant dans l'�tat BROKEN, la fen�tre n'a pas �t� comptabilis�e"
		countRequestEngine.count == 0
		and : "Etant dans l'�tat BROKEN la fen�tre a �t� supprim�e du registre"
		countRequestEngine.windows.size() == 0
	}

	def "Lorqu'un �v�nement clot�re (�tat CLOSED) une fen�tre celle-ci incr�mente le compteur du moteur et la fen�tre est supprim�e du registre"() {
		setup:registerWindowInEngine()

		when: countRequestEngine.onEvent(event)

		then: "Sur le traitement de l'�v�nement par la fen�tre, la fen�tre est pass�e dans l'�tat CLOSED"
		1 * window.processEvent(event) >> CLOSED
		and: "Etant dans l'�tat CLOSED, le compteur du moteur a �t� incr�ment�"
		countRequestEngine.count == 1
		and : "Etant dans l'�tat CLOSED la fen�tre a �t� supprim�e du registre"
		countRequestEngine.windows.size() == 0
	}

	// TEST DE LA NOTIFICATION
	def "Lorsque la date li�e � un �v�nement d�passe le seuil d'intervalle il d�clenche la notification, le compteur est ensuite remis � 0. L'�v�nement trait� ici donne lieu � la cloture d'une fen�tre/"() {
		setup:
		registerWindowInEngine()
		and:"Mise en place des conditions pour le d�clenchement d'une notification"
		def countNotified = 0
		request.timeNotificationInterval = 1000
		request.date = {12345l}
		request.notification = {countNotified=it}
		countRequestEngine.count = 11
		countRequestEngine.currentTime = 11000

		when: countRequestEngine.onEvent(event)

		then:
		1 * window.processEvent(event) >> CLOSED
		countNotified == 11
		countRequestEngine.count == 1
	}
}
