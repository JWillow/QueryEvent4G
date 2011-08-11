package org.qe4g.request;

import java.util.Collection;

import org.qe4g.Event;
import org.qe4g.request.Pattern.Evaluation;

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
	void afterEventProcessed(Request request, Collection<Evaluation> evaluations);
}
