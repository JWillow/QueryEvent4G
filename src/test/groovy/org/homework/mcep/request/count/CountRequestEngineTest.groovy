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


	def "Création d'une fenêtre sur la réception d'un événement, pas de notification attendue de la part du moteur"(){
		setup:
		countRequestEngine.metaClass.createWindow = {def a,def b -> window}

		when: countRequestEngine.onEvent(event)

		then: "La fenêtre nouvellement créée est appelée pour traiter l'événement, la fenêtre reste OPEN"
		1 * window.processEvent(event) >> State.OPEN
		and: "La fenêtre est enregistrée auprès du moteur"
		countRequestEngine.windows.size() == 1
		and: "La fenêtre n'étant pas dans l'état CLOSED le compteur n'a pas été incrémenté"
		countRequestEngine.count == 0
	}

	def "Un nouvel événement est reçu qui vient compléter une fenêtre déjà existante. La fenêtre reste dans l'état OPEN."() {
		setup: registerWindowInEngine()

		when: countRequestEngine.onEvent(event)

		then: "Sur le traitement de l'événement par la fenêtre, la fenêtre reste dans l'état OPEN"
		1 * window.processEvent(event) >> OPEN
		and: "Etant dans l'état OPEN, le compteur du moteur n'est toujours pas incrémenté"
		countRequestEngine.count == 0
		and : "Comme nous avons utilisé une fenêtre déjà existante, aucune nouvelle fenetre n'a été enregistré dans le registre du moteur"
		countRequestEngine.windows.size() == 1
	}

	def "Le moteur traite un événement qui n'est pas accepté par la requête"(){
		setup: "La requête n'accepte aucun événement"
		request.accept = {false}

		when: countRequestEngine.onEvent(event)

		then: "Aucune fenêtre n'a été recherchée & créée"
		countRequestEngine.windows.size() == 0
		and: "Comme aucune fenêtre n'a été créé la fenêtre n'a pu être dans l'état CLOSED et incrémentée le compteur"
		countRequestEngine.count == 0
	}

	def "Lorsqu'une fenêtre est dans l'état BROKEN, celle-ci est supprimée du registre, il n'y a pas d'incrémentation du compteur général"() {
		setup: registerWindowInEngine()

		when: countRequestEngine.onEvent(event)

		then: "Sur le traitement de l'événement par la fenêtre, la fenêtre est passée dans l'état BROKEN"
		1 * window.processEvent(event) >> BROKEN
		and: "Etant dans l'état BROKEN, la fenêtre n'a pas été comptabilisée"
		countRequestEngine.count == 0
		and : "Etant dans l'état BROKEN la fenêtre a été supprimée du registre"
		countRequestEngine.windows.size() == 0
	}

	def "Lorqu'un événement clotûre (état CLOSED) une fenêtre celle-ci incrémente le compteur du moteur et la fenêtre est supprimée du registre"() {
		setup:registerWindowInEngine()

		when: countRequestEngine.onEvent(event)

		then: "Sur le traitement de l'événement par la fenêtre, la fenêtre est passée dans l'état CLOSED"
		1 * window.processEvent(event) >> CLOSED
		and: "Etant dans l'état CLOSED, le compteur du moteur a été incrémenté"
		countRequestEngine.count == 1
		and : "Etant dans l'état CLOSED la fenêtre a été supprimée du registre"
		countRequestEngine.windows.size() == 0
	}

	// TEST DE LA NOTIFICATION
	def "Lorsque la date liée à un événement dépasse le seuil d'intervalle il déclenche la notification, le compteur est ensuite remis à 0. L'événement traité ici donne lieu à la cloture d'une fenêtre/"() {
		setup:
		registerWindowInEngine()
		and:"Mise en place des conditions pour le déclenchement d'une notification"
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
