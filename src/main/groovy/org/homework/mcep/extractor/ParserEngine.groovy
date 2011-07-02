package org.homework.mcep.extractor

import org.homework.mcep.Event;

/**
 * @author Willow
 *
 */
class ParserEngine {

	List<Extractor> extractors = []

	/**
	 * Traitement d'une ligne afin d'en extraire une liste de propriétées
	 * @param line
	 * @return
	 */
	Event process(String line) {
		def extractorUsed = []
		Event event = new Event()
		while(true) {
			boolean found = false
			extractors.each { extractor ->
				if(extractorUsed.contains(extractor)) {
					return
				}
				if(!extractor.satisfiedDependency(event.attributes)) {
					return
				}
				found = true
				extractorUsed << extractor
				Map<String,String> datas = extractor.extract(line);
				if(!datas.isEmpty()) {
					event.attributes += datas
					event.names << extractor.getEventName()
				}
			}
			if(!found) {
				break
			}
		}
		return event
	}

	// ------------
	// BUILDER PART
	// ------------
	private ParserEngine() {
	}

	public static Builder builder() {
		return new Builder()
	}

	public static class Builder {
		List<Extractor> extractors = []

		public Builder withExtractor(Extractor extractor) {
			extractors << extractor
			return this
		}

		public ParserEngine build() {
			ParserEngine parserEngine = new ParserEngine()
			parserEngine.extractors = this.extractors
			return parserEngine
		}
	}
}
