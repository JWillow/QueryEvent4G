package org.qe4g.request;

import java.util.Collection;
import java.util.List;

import org.qe4g.Event;

/**
 * <p>
 * Triggerred for each {@link Event} received by the {@link Request}
 * 
 * @author Willow
 */
public interface EventProcessedListener {

	/**
	 * Call before the {@link Request#accept} method on each {@link Event}
	 * before any treatment
	 * 
	 * @param request
	 *            - Request that will handle the event
	 * @param event
	 *            - Event to proceed
	 */
	void beforeEventProcessing(Request request, Event event);

	/**
	 * Called after all the treatments were performed.
	 * 
	 * @param request
	 *            - Request that handle the {@link Event}
	 * @param evaluations
	 */
	void afterEventProcessed(Request request, List<Event> events);
}
