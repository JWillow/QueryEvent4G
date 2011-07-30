package org.qe4g.request;

import java.util.List;
import java.util.Map;

import org.qe4g.Event;

/**
 * <p>
 * Used to evaluate a list of {@link Event} order by arriving date inside the
 * {@link Request}. The last event is the most recent to evaluate.
 * 
 * @author Willow
 * 
 */
public interface Evaluator {
	
	public enum Response{OK,KO,CONTINUE_WITH_NEXT_EVALUATOR,OK_BUT_KEEP_ME}
	
	/**
	 * @param events - order by arriving order inside the {@link Request}
	 * @return
	 */
	Response evaluate(Map<String,Object> context, List<Event> events);
}
