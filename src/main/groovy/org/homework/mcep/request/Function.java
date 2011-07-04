package org.homework.mcep.request;

import java.util.List;

import org.homework.mcep.Event;

/**
 * Fonction d�clench�e lors de la d�tection d'un pattern
 * 
 * @author Willow
 * 
 */
public interface Function {

	/**
	 * D�clench�e lors de la d�tection d'un pattern
	 * 
	 * @param requestDefinition
	 * @param events
	 *            Liste des �v�nements correspondant au pattern lors de la
	 *            d�tection
	 */
	void onPatternDetection(RequestDefinition requestDefinition,
			List<Event> events);

	/**
	 * Demande � la fonction de produire son r�sultat explicitement
	 */
	void get();

	/**
	 * R�initialise la fonction
	 */
	void reset();
}
