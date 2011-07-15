package org.homework.mcep.extractor

import java.util.Map;

/**
 * @author Willow
 * @specification {@link AbstractExtractorSpecification}
 */
abstract class AbstractExtractor implements Extractor {

	protected List<DependOnToken> dependOnTokens = []
	protected List<PostProcess> postProcesses = []

	/** 
	 * Two steps :
	 * <ul>
	 * <li>Delegate the extraction to {@link #applyExtraction(String)}</li>
	 * <li>Apply the {@link PostProcess} registred</li>
	 * </ul>
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

	/**
	 * Method to override
	 * @param line
	 * @return
	 */
	protected abstract Map<String,String> applyExtraction(String line);

	/**
	 * Used to know if this extractor satisfy all {@link DependOnToken} registred
	 * @param collectedAttributes - Attributes already collected. These attributes are compared to {@link DependOnToken} list.
	 * @return
	 */
	public boolean satisfiedDependency(Map<String,String> collectedAttributes) {
		if(collectedAttributes.size() < dependOnTokens.size()) {
			return false
		}
		return dependOnTokens.every {it.value.equals(collectedAttributes[it.id])}
	}
}
