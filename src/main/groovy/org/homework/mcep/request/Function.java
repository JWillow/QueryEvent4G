package org.homework.mcep.request;

import java.util.List;

import org.homework.mcep.Event;

/**
 * <p>
 * Represent a chunk of code that will be executed when the pattern is detected.
 * 
 * @author Willow
 */
public interface Function {

	/**
	 * Déclenchée lors de la détection d'un pattern
	 * 
	 * @param requestDefinition
	 * @param events
	 *            Liste des événements correspondant au pattern lors de la
	 *            détection
	 */
	void onPatternDetection(Request request, List<Event> events);

	/**
	 * Explicit demand to {@link Function} to produce a result
	 */
	void get();

	/**
	 * Demand to {@link Function} to reset all context elements that can be used
	 * to produce a result
	 */
	void reset();
}
