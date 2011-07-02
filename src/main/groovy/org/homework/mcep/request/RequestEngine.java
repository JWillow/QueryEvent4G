package org.homework.mcep.request;

import org.homework.mcep.Event;

public interface RequestEngine {
	void onEvent(Event event);
}
