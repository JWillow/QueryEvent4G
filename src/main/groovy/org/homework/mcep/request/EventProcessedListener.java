package org.homework.mcep.request;

import org.homework.mcep.Event;
import org.homework.mcep.request.Pattern.Evaluation;

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
	 *            - {@link Window} that handle the {@link Event}
	 * @param evaluation
	 */
	void afterEventProcessed(Request request, Evaluation evaluation);
}
