package org.homework.mcep.request;

import java.util.List;

import org.homework.mcep.Event;

public interface Evaluator {
	boolean evaluate(List<Event> events);
}
