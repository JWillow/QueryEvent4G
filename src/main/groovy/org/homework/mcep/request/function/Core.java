package org.homework.mcep.request.function;

import java.util.List;
import java.util.Map;

import org.homework.mcep.Event;

public interface Core {
	void onPatternDetection(Map<Object,Object> context, List<Event> events);
}
