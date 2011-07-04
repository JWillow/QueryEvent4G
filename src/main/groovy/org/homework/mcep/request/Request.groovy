package org.homework.mcep.request

import static org.homework.mcep.request.Window.State.BROKEN
import static org.homework.mcep.request.Window.State.CLOSED
import static org.homework.mcep.request.Window.State.OPEN

import org.homework.mcep.Event
import org.homework.mcep.request.Window.State;

class Request {

	private RequestDefinition requestDefinition
	private Map<String,Window> windows = [:]


	private void notifyFunctions(Window window) {
		requestDefinition.functions.each {it.onPatternDetection requestDefinition, window.proceedEvents}
	}

	void get() {
		requestDefinition.functions*.get()
	}
	
	void onEvent(Event event) {
		requestDefinition.eventListeners*.beforeEventProcessing(requestDefinition, windows.values(),event)
		if(!requestDefinition.accept(event)) {
			return
		}

		Window window = findWindow(requestDefinition, event)
		State state = window.processEvent(event)

		switch(state) {
			case CLOSED :
				notifyFunctions(window)
			case BROKEN :
				windows.remove(window.id);
			case OPEN :
				break;
			default :
				throw new IllegalStateException()
		}
		
		requestDefinition.eventListeners*.afterEventProcessed(requestDefinition, windows.values(),event)
	}


	/**
	 * Créer pour faciliter les tests unitaires, pour mocker un objet {@link Window}
	 * @param id
	 * @param requestEventDefinitions
	 * @return
	 */
	protected Window createWindow(def id, def eventDefinitions) {
		return new Window(id:id, eventDefinitions:eventDefinitions)
	}

	protected Window findWindow(def request, Event event) {
		def groupById = request.groupBy(event)
		Window window = windows[groupById]
		if(!window) {
			window = createWindow(groupById, request.eventDefinitions)
			windows[groupById] = window
		}
		return window
	}

	// ------------
	// BUILDER PART
	// ------------
	private Request() {
	}

	public static Request newEngine(RequestDefinition requestDefinition) {
		Request request = new Request();
		request.requestDefinition = requestDefinition;
		return request;
	}
}
