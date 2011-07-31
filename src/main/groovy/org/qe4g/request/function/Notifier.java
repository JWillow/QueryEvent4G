package org.qe4g.request.function;

import java.util.Map;

public interface Notifier {

	/**
	 * @param context
	 * @param at - time in millisecond on the notification
	 */
	void get(Map<Object,Object> context,long at);
	
}
