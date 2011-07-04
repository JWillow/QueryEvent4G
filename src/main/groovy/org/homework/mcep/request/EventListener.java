package org.homework.mcep.request;

import java.util.Collection;

import org.homework.mcep.Event;

/**
 * <p>
 * Listener d�clench� � chaque �v�nement trait� que cela donne lieu � la
 * d�tection d'un pattern ou non.
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
