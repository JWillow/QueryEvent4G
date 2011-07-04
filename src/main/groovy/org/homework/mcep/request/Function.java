package org.homework.mcep.request;

import java.util.List;

import org.homework.mcep.Event;

/**
 * Fonction déclenchée lors de la détection d'un pattern
 * 
 * @author Willow
 * 
 */
public interface Function {

	/**
	 * Déclenchée lors de la détection d'un pattern
	 * 
	 * @param requestDefinition
	 * @param events
	 *            Liste des événements correspondant au pattern lors de la
	 *            détection
	 */
	void onPatternDetection(RequestDefinition requestDefinition,
			List<Event> events);

	/**
	 * Demande à la fonction de produire son résultat explicitement
	 */
	void get();

	/**
	 * Réinitialise la fonction
	 */
	void reset();
}
