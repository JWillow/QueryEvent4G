package org.qe4g.request.function;

import java.util.List;
import java.util.Map;

import org.qe4g.Event;

public interface Core {
	void onPatternDetection(Map<Object,Object> context, List<Event> events);
}
