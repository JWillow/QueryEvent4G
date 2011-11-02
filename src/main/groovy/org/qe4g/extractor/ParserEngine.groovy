package org.qe4g.extractor

import org.qe4g.Event;

/**
 * Core of the parser mechanism. Apply all {@link Extractor} registred to {@link String} data source to create an {@link Event}.
 * @author Willow
 * @specification ParserEngineSpecification
 */
class ParserEngine {

	List<Extractor> extractors = []

	/**
	 * Extract <b>token</b> by using all {@link Extractor} registred. {@link Extractor} are applied with the respect of dependencies.
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
					event.triggeredTime = datas.time
					datas.remove('time')
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
