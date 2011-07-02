package org.homework.mcep.request.count

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.homework.mcep.Event;
import org.homework.mcep.request.RequestEngine;
import org.homework.mcep.request.Window;
import org.homework.mcep.request.Window.State;

import static org.homework.mcep.request.Window.State.BROKEN
import static org.homework.mcep.request.Window.State.CLOSED
import static org.homework.mcep.request.Window.State.OPEN

class CountRequestEngine implements RequestEngine {
	
	private CountRequestEngine() {
		
	}
	
	public static CountRequestEngine newEngine(CountRequest countRequest) {
		CountRequestEngine engine = new CountRequestEngine();
		engine.request = countRequest;
		return engine;
	}
	 
	CountRequest request
	def windows = [:]
	int count = 0
	long currentTime

	void initCurrentTimeIfNecessary(long date) {
		if(currentTime != 0) {
			return
		}
		currentTime = ((long) (date/ 1000)) * 1000
	}

	void onEvent(Event event) {
		long date = request.date(event)
		if(date != -1) {
			initCurrentTimeIfNecessary(date)
			if(currentTime + request.timeNotificationInterval < date) {
				request.notification(currentTime, count)
				count = 0
				currentTime += request.timeNotificationInterval
			}
		}
		processEvent(event)
	}

	protected void processEvent(Event event) {
		if(!request.accept(event)) {
			return
		}
		
		Window window = findWindow(request, event)
		State state = window.processEvent(event)
		
		switch(state) {
			case CLOSED :
				count ++
			case BROKEN :
				windows.remove(window.id);
			case OPEN :
				break;
			default :
				throw new IllegalStateException()
		}
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
}
