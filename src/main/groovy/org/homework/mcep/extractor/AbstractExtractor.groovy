package org.homework.mcep.extractor

import java.util.List
import java.util.Map

abstract class AbstractExtractor implements Extractor {

	protected List<DependOnToken> dependOnTokens = []
	
   /**
   * Permet de savoir si ce parser peut être utilisé, la réponse sera en fonction du respect des dépendances. En effet, il est courant qu'en vu de l'application d'un Parser celui dépende du fait de l'existance d'autres attributs
   * @param collectedAttributes
   * @return
   */
  public boolean satisfiedDependency(Map<String,String> collectedAttributes) {
	  if(collectedAttributes.size() < dependOnTokens.size()) {
		  return false
	  }
	  return dependOnTokens.every {it.value.equals(collectedAttributes[it.id])}
  }
}
