package org.homework.mcep.request;

import java.util.List;

import org.homework.mcep.Event;

/**
 * <p>
 * Used to evaluate a list of {@link Event} order by arriving date inside the
 * {@link Request}. The last event is the most recent to evaluate.
 * 
 * @author Willow
 * 
 */
public interface Evaluator {
	
	/**
	 * @param events - order by arriving order inside the {@link Request}
	 * @return
	 */
	boolean evaluate(List<Event> events);
}
