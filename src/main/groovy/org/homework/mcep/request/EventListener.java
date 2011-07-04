package org.homework.mcep.request;

import java.util.Collection;

import org.homework.mcep.Event;

/**
 * <p>
 * Listener déclenché à chaque événement traité que cela donne lieu à la
 * détection d'un pattern ou non.
 * 
 * @author Willow
 * 
 */
public interface EventListener {
	
	void beforeEventProcessing(RequestDefinition requestDefinition, Collection<Window> windows,
			Event event);
	
	void afterEventProcessed(RequestDefinition requestDefinition, Collection<Window> windows,
			Event event);
}
