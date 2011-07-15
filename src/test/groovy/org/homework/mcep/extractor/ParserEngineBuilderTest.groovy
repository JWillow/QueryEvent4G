package org.homework.mcep.extractor

import org.homework.mcep.extractor.dsl.GParserEngineBuilder;

import spock.lang.Specification;

class ParserEngineBuilderTest extends Specification {

	def parserEngineBuilder = new GParserEngineBuilder();
	ParserEngine parserEngine = null;

	def "L'on crée un moteur avec 2 extractor de type regexp"() {

		when:
		parserEngine = parserEngineBuilder.engine() {
			regExpExtractor()
			regExpExtractor()
		}

		then:
		assert parserEngine.extractors.size() == 2
	}
}
