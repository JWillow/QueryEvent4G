package org.homework.mcep.extractor

import java.util.Map;

abstract class AbstractExtractor implements Extractor {

	protected List<DependOnToken> dependOnTokens = []
	protected List<PostProcess> postProcesses = []


	/** 
	 * Applique les {@link PostProcess} après delegation à la méthode {@link #applyExtraction(String)}
	 * @see org.homework.mcep.extractor.Extractor#extract(java.lang.String)
	 */
	public Map<String,Object> extract(String line) {
		Map<String,Object> tokens = applyExtraction(line);
		if(tokens) {
			postProcesses.each {
				tokens = it.process(tokens)
			}
		}
		return tokens
	}

	protected abstract Map<String,String> applyExtraction(String line);

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
