package org.qe4g.request;

import java.util.List;

import org.qe4g.Event;

/**
 * <p>
 * Represent a chunk of code that will be executed when the pattern is detected.
 * 
 * @author Willow
 */
public interface Function {

	/**
	 * D�clench�e lors de la d�tection d'un pattern
	 * 
	 * @param requestDefinition
	 * @param events
	 *            Liste des �v�nements correspondant au pattern lors de la
	 *            d�tection
	 */
	void onPatternDetection(Request request, List<Event> events);

	/**
	 * Demand to {@link Function} to produce a result if {@link Function}
	 * implementation aggregate result for example
	 * 
	 * @param at - demand date
	 */
	void get(long at);

	/**
	 * Demand to {@link Function} to reset all context elements that can be used
	 * to produce a result
	 */
	void reset();
}
