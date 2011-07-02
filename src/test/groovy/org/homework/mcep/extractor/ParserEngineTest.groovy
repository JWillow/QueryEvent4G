package org.homework.mcep.extractor

import org.homework.mcep.Event;

import spock.lang.Specification;

class ParserEngineTest extends Specification {

	ParserEngine parserEngine;
	def line = "une ligne pour le test"

	def "2 extracteurs sont utilis�s d�s leur premi�re sollicitation, le troisi�me n'est jamais sollicit�"() {
		setup:"Configuration des 3 extracteurs"
		Extractor extractor = Mock()
		Extractor extractor1 = Mock()
		Extractor extractor2 = Mock()
		parserEngine = ParserEngine.builder().withExtractor(extractor2).withExtractor(extractor1).withExtractor(extractor).build()
		and : "Cet extractor n'est pas utilis�"
		2 * extractor.satisfiedDependency(_) >> false
		and : "Cet extractor est utilis� d�s la premi�re sollicitation"
		1 * extractor1.satisfiedDependency(_) >> true
		and : "Cet extracteur est utilis� d�s la premi�re sollicitation"
		1 * extractor2.satisfiedDependency(_) >> true

		and:"Fin du param�trage des extracteurs"
		1 * extractor1.extract(line) >> [et1Name:'name']
		1 * extractor2.extract(line) >> [et2Name:'name2']

		1 * extractor1.getEventName() >> 'et1'
		1 * extractor2.getEventName() >> 'et2'

		when:
		Event event = parserEngine.process(line)

		then:
		assert event
		assert event.attributes.size() == 2
		assert event.names.size() == 2
		assert event.names.contains('et1')
		assert event.names.contains('et2')
		assert event.attributes.et2Name == 'name2'
		assert event.attributes.et1Name == 'name'
	}

	def "3 extractors sont param�tr�s pour permettre d'extraire un �v�nement, seulement 2 sont utilis�es"() {
		setup:"Configuration des 3 extracteurs"
		Extractor extractor = Mock()
		Extractor extractor1 = Mock()
		Extractor extractor2 = Mock()
		parserEngine = ParserEngine.builder().withExtractor(extractor2).withExtractor(extractor1).withExtractor(extractor).build()
		and : "Cette extractor simule une d�pendance, car lors de la premi�re solicitation il r�pond false"
		2 * extractor.satisfiedDependency(_) >>> [false, true]
		and : "Cette extractor n'est jamais utilis�"
		3 * extractor1.satisfiedDependency(_) >> false
		and : "Cette extracteur est utilis� d�s la premi�re sollicitation puis ne sera plus appel�"
		1 * extractor2.satisfiedDependency(_) >> true

		and:"Fin du param�trage des extracteurs"
		1 * extractor.extract(line) >> [et1Name:'name']
		1 * extractor2.extract(line) >> [et2Name:'name2']

		1 * extractor.getEventName() >> 'et1'
		1 * extractor2.getEventName() >> 'et2'

		when:
		Event event = parserEngine.process(line)

		then:
		assert event
		assert event.attributes.size() == 2
		assert event.names.size() == 2
		assert event.names.contains('et1')
		assert event.names.contains('et2')
		assert event.attributes.et2Name == 'name2'
		assert event.attributes.et1Name == 'name'
	}

	def "Aucun extracteur n'a pu extraire d'information"() {
		setup:
		Extractor extractor = Mock()
		parserEngine = ParserEngine.builder().withExtractor(extractor).build()
		1 * extractor.satisfiedDependency(_) >> true
		1 * extractor.extract(line) >> [:]

		when:
		Event event = parserEngine.process(line)

		then:
		assert event
		assert event.isInconsistent()
	}
}
